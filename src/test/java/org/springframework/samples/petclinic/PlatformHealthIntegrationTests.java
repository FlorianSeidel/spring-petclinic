/*
 * Copyright 2012-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.samples.petclinic;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT,
		properties = { "management.endpoint.health.probes.enabled=true",
				"management.endpoint.health.probes.add-additional-paths=true",
				"management.endpoint.health.group.readiness.include=readinessState,db" })
class PlatformHealthIntegrationTests {

	@LocalServerPort
	int port;

	@Autowired
	DataSource dataSource;

	@Test
	void readinessTracksDatabaseWhileLivenessRemainsHealthy() throws Exception {
		HttpClient client = HttpClient.newHttpClient();
		assertThat(status(client, "/livez")).isEqualTo(200);
		assertThat(status(client, "/readyz")).isEqualTo(200);

		assertThat(dataSource).isInstanceOf(HikariDataSource.class);
		((HikariDataSource) dataSource).close();
		assertThat(status(client, "/readyz")).isEqualTo(503);
		assertThat(status(client, "/livez")).isEqualTo(200);
	}

	private int status(HttpClient client, String path) throws Exception {
		HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path)).GET().build();
		return client.send(request, HttpResponse.BodyHandlers.discarding()).statusCode();
	}

}
