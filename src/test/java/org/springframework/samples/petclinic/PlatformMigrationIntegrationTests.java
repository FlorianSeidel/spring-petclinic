package org.springframework.samples.petclinic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doThrow;

import java.net.CookieManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Locale;
import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.MessageSource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		properties = { "spring.docker.compose.enabled=false", "spring.sql.init.mode=never",
				"spring.jpa.hibernate.ddl-auto=none", "management.endpoint.health.probes.enabled=true",
				"management.endpoint.health.probes.add-additional-paths=true",
				"management.endpoint.health.group.readiness.include=readinessState,db",
				"management.endpoints.web.exposure.include=health,info" })
class PlatformMigrationIntegrationTests {

	@LocalServerPort
	int port;

	@MockitoSpyBean
	DataSource dataSource;

	@Autowired
	MessageSource messages;

	static String required(String name) {
		String value = System.getenv(name);
		if (value == null || value.isBlank()) {
			throw new IllegalStateException("Supply " + name + " for the isolated migration test database");
		}
		return value;
	}

	static Connection database() throws SQLException {
		return DriverManager.getConnection(required("TEST_DATABASE_URL"), required("TEST_DATABASE_USER"),
				required("TEST_DATABASE_PASSWORD"));
	}

	static void bootstrap() throws Exception {
		Path sql = Path.of(required("PLATFORM_SQL_DIR"));
		try (Connection connection = database()) {
			ScriptUtils.executeSqlScript(connection, new FileSystemResource(sql.resolve("schema.sql")));
			ScriptUtils.executeSqlScript(connection, new FileSystemResource(sql.resolve("data.sql")));
		}
	}

	@DynamicPropertySource
	static void targetDatabase(DynamicPropertyRegistry registry) throws Exception {
		// Complete platform SQL is applied before the Spring application starts.
		bootstrap();
		registry.add("spring.datasource.url", () -> required("TEST_DATABASE_URL"));
		registry.add("spring.datasource.username", () -> required("TEST_DATABASE_USER"));
		registry.add("spring.datasource.password", () -> required("TEST_DATABASE_PASSWORD"));
		registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
	}

	@Test
	void platformBootstrapIsCompleteRepeatableAndSupportsWrites() throws Exception {
		bootstrap();
		try (Connection connection = database(); var statement = connection.createStatement()) {
			for (String table : new String[] { "vets", "specialties", "vet_specialties", "types", "owners", "pets",
					"visits" }) {
				try (var rows = statement.executeQuery("SELECT COUNT(*) FROM " + table)) {
					assertThat(rows.next()).isTrue();
					assertThat(rows.getInt(1)).isGreaterThan(0);
				}
			}
			try (var rows = statement.executeQuery("SELECT COUNT(*) FROM vets")) {
				rows.next();
				assertThat(rows.getInt(1)).isEqualTo(6);
			}
			connection.setAutoCommit(false);
			statement.executeUpdate(
					"INSERT INTO owners(first_name,last_name,address,city,telephone) VALUES ('Migration','Test','Test','Test','1234567890')");
			try (var rows = statement.executeQuery("SELECT COUNT(*) FROM owners WHERE last_name='Test'")) {
				rows.next();
				assertThat(rows.getInt(1)).isEqualTo(1);
			}
			connection.rollback();
		}
		assertThat(get(HttpClient.newHttpClient(), "/owners/1").statusCode()).isEqualTo(200);
		assertThat(get(HttpClient.newHttpClient(), "/vets.html").statusCode()).isEqualTo(200);
	}

	@Test
	void readinessFailsAndRecoversWithoutFailingLiveness() throws Exception {
		HttpClient client = HttpClient.newHttpClient();
		assertThat(get(client, "/readyz").statusCode()).isEqualTo(200);
		assertThat(get(client, "/livez").statusCode()).isEqualTo(200);
		try {
			doThrow(new SQLException("Simulated database outage")).when(dataSource).getConnection();
			assertThat(get(client, "/readyz").statusCode()).isEqualTo(503);
			assertThat(get(client, "/livez").statusCode()).isEqualTo(200);
		}
		finally {
			doCallRealMethod().when(dataSource).getConnection();
		}
		assertThat(get(client, "/readyz").statusCode()).isEqualTo(200);
		assertThat(get(client, "/actuator/env").statusCode()).isEqualTo(404);
	}

	@Test
	void languageBundlesRenderAndLocaleCookieSurvivesNextRequest() throws Exception {
		HttpClient client = HttpClient.newBuilder().cookieHandler(new CookieManager()).build();
		String english = messages.getMessage("welcome", null, Locale.ENGLISH);
		String german = messages.getMessage("welcome", null, Locale.GERMAN);
		assertThat(german).isNotEqualTo(english);
		assertThat(get(client, "/").body()).contains(english).doesNotContain("??welcome");
		var response = get(client, "/?lang=de");
		assertThat(response.statusCode()).isEqualTo(200);
		assertThat(response.body()).contains(german).doesNotContain("??welcome");
		assertThat(response.headers().allValues("set-cookie")).anyMatch(value -> value.startsWith("petclinic-locale="));
		assertThat(get(client, "/").body()).contains(german);
	}

	HttpResponse<String> get(HttpClient client, String path) throws Exception {
		return client.send(HttpRequest.newBuilder(URI.create("http://localhost:" + port + path)).GET().build(),
				HttpResponse.BodyHandlers.ofString());
	}

}
