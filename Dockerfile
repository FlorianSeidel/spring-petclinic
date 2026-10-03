FROM eclipse-temurin:17-jre@sha256:bd25c61779663bc2df8ced059f1cc114fafc073afe16125c13fcc3a37fecd845

WORKDIR /app
COPY --chown=10001:10001 target/spring-petclinic-*.jar /app/petclinic.jar
USER 10001:10001

ENTRYPOINT ["java", "-Djava.io.tmpdir=/tmp", "-jar", "/app/petclinic.jar"]
