FROM eclipse-temurin:17-jre

WORKDIR /app
COPY target/spring-petclinic-*.jar /app/petclinic.jar

# The image contains one foreground application process and runs without root.
USER 10001:10001
ENTRYPOINT ["java", "-jar", "/app/petclinic.jar"]
