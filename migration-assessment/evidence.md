# Evidence index

Application citations point to the immutable assessed commit. Platform citations point to the pinned platform snapshot included here; its original tree was local to the assessment repository. Credential values in published excerpts are redacted. Review the full finding and success criteria in [report.md](report.md).

### application: [.github/workflows/deploy-and-test-cluster.yml:25–25](https://github.com/FlorianSeidel/spring-petclinic/blob/818c4136ea971c21674525f9053de0d9c7ad8cfe/.github/workflows/deploy-and-test-cluster.yml#L25-L25)

```text
          kubectl apply -f k8s/
```

### application: [README.md:45–45](https://github.com/FlorianSeidel/spring-petclinic/blob/818c4136ea971c21674525f9053de0d9c7ad8cfe/README.md#L45-L45)

```text
There is no `Dockerfile` in this project. You can build a container image (if you have a docker daemon) using the Spring Boot build plugin:
```

### application: [README.md:55–55](https://github.com/FlorianSeidel/spring-petclinic/blob/818c4136ea971c21674525f9053de0d9c7ad8cfe/README.md#L55-L55)

```text
docker run -p 8080:8080 docker.io/library/spring-petclinic:latest
```

### application: [README.md:68–68](https://github.com/FlorianSeidel/spring-petclinic/blob/818c4136ea971c21674525f9053de0d9c7ad8cfe/README.md#L68-L68)

```text
A similar setup is provided for MySQL and PostgreSQL if a persistent database configuration is needed. Note that whenever the database type changes, the app needs to run with a different profile: `spring.profiles.active=mysql` for MySQL or `spring.profiles.active=postgres` for PostgreSQL. See the [Spring Boot documentation](https://docs.spring.io/spring-boot/how-to/properties-and-configuration.html#howto.properties-and-configuration.set-active-spring-profiles) for more detail on how to set the active profile.
```

### application: [docker-compose.yml:19–19](https://github.com/FlorianSeidel/spring-petclinic/blob/818c4136ea971c21674525f9053de0d9c7ad8cfe/docker-compose.yml#L19-L19)

```text
      - [REDACTED sha256:9c3f8e6fa470]
```

### application: [k8s/db.yml:14–14](https://github.com/FlorianSeidel/spring-petclinic/blob/818c4136ea971c21674525f9053de0d9c7ad8cfe/k8s/db.yml#L14-L14)

```text
  password: [REDACTED demo credential]"
```

### application: [k8s/db.yml:31–31](https://github.com/FlorianSeidel/spring-petclinic/blob/818c4136ea971c21674525f9053de0d9c7ad8cfe/k8s/db.yml#L31-L31)

```text
  name: demo-db
```

### application: [k8s/db.yml:44–44](https://github.com/FlorianSeidel/spring-petclinic/blob/818c4136ea971c21674525f9053de0d9c7ad8cfe/k8s/db.yml#L44-L44)

```text
        - image: postgres:18.4
```

### application: [k8s/petclinic.yml:7–7](https://github.com/FlorianSeidel/spring-petclinic/blob/818c4136ea971c21674525f9053de0d9c7ad8cfe/k8s/petclinic.yml#L7-L7)

```text
  type: NodePort
```

### application: [k8s/petclinic.yml:30–33](https://github.com/FlorianSeidel/spring-petclinic/blob/818c4136ea971c21674525f9053de0d9c7ad8cfe/k8s/petclinic.yml#L30-L33)

```text
    spec:
      containers:
        - name: workload
          image: dsyer/petclinic
```

### application: [k8s/petclinic.yml:33–33](https://github.com/FlorianSeidel/spring-petclinic/blob/818c4136ea971c21674525f9053de0d9c7ad8cfe/k8s/petclinic.yml#L33-L33)

```text
          image: dsyer/petclinic
```

### application: [k8s/petclinic.yml:42–42](https://github.com/FlorianSeidel/spring-petclinic/blob/818c4136ea971c21674525f9053de0d9c7ad8cfe/k8s/petclinic.yml#L42-L42)

```text
                  "management.endpoint.health.probes.add-additional-paths": true
```

### application: [k8s/petclinic.yml:43–43](https://github.com/FlorianSeidel/spring-petclinic/blob/818c4136ea971c21674525f9053de0d9c7ad8cfe/k8s/petclinic.yml#L43-L43)

```text
                }
```

### application: [k8s/petclinic.yml:53–53](https://github.com/FlorianSeidel/spring-petclinic/blob/818c4136ea971c21674525f9053de0d9c7ad8cfe/k8s/petclinic.yml#L53-L53)

```text
              path: /readyz
```

### application: [k8s/petclinic.yml:59–59](https://github.com/FlorianSeidel/spring-petclinic/blob/818c4136ea971c21674525f9053de0d9c7ad8cfe/k8s/petclinic.yml#L59-L59)

```text
      volumes:
```

### application: [k8s/petclinic.yml:64–64](https://github.com/FlorianSeidel/spring-petclinic/blob/818c4136ea971c21674525f9053de0d9c7ad8cfe/k8s/petclinic.yml#L64-L64)

```text
                  name: demo-db
```

### application: [src/main/resources/application-mysql.properties:7–7](https://github.com/FlorianSeidel/spring-petclinic/blob/818c4136ea971c21674525f9053de0d9c7ad8cfe/src/main/resources/application-mysql.properties#L7-L7)

```text
spring.sql.init.mode=always
```

### application: [src/main/resources/application-postgres.properties:3–3](https://github.com/FlorianSeidel/spring-petclinic/blob/818c4136ea971c21674525f9053de0d9c7ad8cfe/src/main/resources/application-postgres.properties#L3-L3)

```text
spring.datasource.url=${POSTGRES_URL:jdbc:postgresql://localhost/petclinic}
```

### application: [src/main/resources/application-postgres.properties:5–5](https://github.com/FlorianSeidel/spring-petclinic/blob/818c4136ea971c21674525f9053de0d9c7ad8cfe/src/main/resources/application-postgres.properties#L5-L5)

```text
spring.datasource.password=[REDACTED demo credential]
```

### application: [src/main/resources/application-postgres.properties:7–7](https://github.com/FlorianSeidel/spring-petclinic/blob/818c4136ea971c21674525f9053de0d9c7ad8cfe/src/main/resources/application-postgres.properties#L7-L7)

```text
spring.sql.init.mode=always
```

### application: [src/main/resources/application.properties:4–4](https://github.com/FlorianSeidel/spring-petclinic/blob/818c4136ea971c21674525f9053de0d9c7ad8cfe/src/main/resources/application.properties#L4-L4)

```text
spring.sql.init.data-locations=classpath*:db/${database}/data.sql
```

### application: [src/main/resources/application.properties:21–21](https://github.com/FlorianSeidel/spring-petclinic/blob/818c4136ea971c21674525f9053de0d9c7ad8cfe/src/main/resources/application.properties#L21-L21)

```text
management.endpoints.web.exposure.include=*
```

### application: [src/main/resources/db/postgres/schema.sql:1–1](https://github.com/FlorianSeidel/spring-petclinic/blob/818c4136ea971c21674525f9053de0d9c7ad8cfe/src/main/resources/db/postgres/schema.sql#L1-L1)

```text
CREATE TABLE IF NOT EXISTS vets (
```

### application: [src/test/java/org/springframework/samples/petclinic/PostgresIntegrationTests.java:53–53](https://github.com/FlorianSeidel/spring-petclinic/blob/818c4136ea971c21674525f9053de0d9c7ad8cfe/src/test/java/org/springframework/samples/petclinic/PostgresIntegrationTests.java#L53-L53)

```text
@ActiveProfiles("postgres")
```

### application: [src/test/java/org/springframework/samples/petclinic/PostgresIntegrationTests.java:90–90](https://github.com/FlorianSeidel/spring-petclinic/blob/818c4136ea971c21674525f9053de0d9c7ad8cfe/src/test/java/org/springframework/samples/petclinic/PostgresIntegrationTests.java#L90-L90)

```text
		ResponseEntity<String> result = template.exchange(RequestEntity.get("/owners/1").build(), String.class);
```

### platform: [README.md:15–16](evidence/platform/README.md#L15-L16)

```text
The checked-in Petclinic GitOps manifests represent the later migrated commit; they
are a reference example, not evidence that an assessed earlier commit has migrated.
```

### platform: [README.md:95–95](evidence/platform/README.md#L95-L95)

```text
to test readiness failure. A single local PostgreSQL instance cannot prove
```

### platform: [jenkins/Jenkinsfile:23–23](evidence/platform/jenkins/Jenkinsfile#L23-L23)

```text
          value: tcp://127.0.0.1:2375
```

### platform: [jenkins/Jenkinsfile:61–61](evidence/platform/jenkins/Jenkinsfile#L61-L61)

```text
          sh 'cd source && sh ./mvnw -B -DskipTests -Dcheckstyle.skip package'
```

### platform: [jenkins/Jenkinsfile:71–71](evidence/platform/jenkins/Jenkinsfile#L71-L71)

```text
docker build -t "$REGISTRY/petclinic:$TARGET_SHA" source
```

### platform: [jenkins/Jenkinsfile:86–86](evidence/platform/jenkins/Jenkinsfile#L86-L86)

```text
sed -i "s|image: .*petclinic@sha256:.*|image: $IMAGE|" gitops/petclinic/deployment.yaml
```

### platform: [jenkins/Jenkinsfile:92–92](evidence/platform/jenkins/Jenkinsfile#L92-L92)

```text
  git -C gitops push origin main
```

### platform: [k8s/postgres-cluster.yaml:12–12](evidence/platform/k8s/postgres-cluster.yaml#L12-L12)

```text
      owner: petclinic
```

### platform: [migrations/petclinic/deployment.yaml:36–36](evidence/platform/migrations/petclinic/deployment.yaml#L36-L36)

```text
              value: jdbc:postgresql://petclinic-db-rw.demo-app.svc.cluster.local:5432/petclinic
```

### platform: [migrations/petclinic/deployment.yaml:40–40](evidence/platform/migrations/petclinic/deployment.yaml#L40-L40)

```text
                  name: petclinic-db-app
```

### platform: [migrations/petclinic/deployment.yaml:45–45](evidence/platform/migrations/petclinic/deployment.yaml#L45-L45)

```text
                  name: petclinic-db-app
```

### platform: [migrations/petclinic/deployment.yaml:50–50](evidence/platform/migrations/petclinic/deployment.yaml#L50-L50)

```text
              value: never
```

### platform: [migrations/petclinic/deployment.yaml:56–56](evidence/platform/migrations/petclinic/deployment.yaml#L56-L56)

```text
              value: health,info
```

### platform: [migrations/petclinic/deployment.yaml:62–62](evidence/platform/migrations/petclinic/deployment.yaml#L62-L62)

```text
              value: readinessState,db
```

### platform: [migrations/petclinic/deployment.yaml:84–84](evidence/platform/migrations/petclinic/deployment.yaml#L84-L84)

```text
              memory: 512Mi
```

### platform: [migrations/petclinic/deployment.yaml:91–91](evidence/platform/migrations/petclinic/deployment.yaml#L91-L91)

```text
            readOnlyRootFilesystem: true
```

### platform: [migrations/petclinic/ingress.yaml:6–6](evidence/platform/migrations/petclinic/ingress.yaml#L6-L6)

```text
  ingressClassName: traefik
```

### platform: [migrations/petclinic/migration-job.yaml:6–6](evidence/platform/migrations/petclinic/migration-job.yaml#L6-L6)

```text
    argocd.argoproj.io/hook: PreSync
```

### platform: [migrations/petclinic/migration-job.yaml:29–29](evidence/platform/migrations/petclinic/migration-job.yaml#L29-L29)

```text
              psql -v ON_ERROR_STOP=1 -f /sql/schema.sql
```

### platform: [migrations/petclinic/service.yaml:8–8](evidence/platform/migrations/petclinic/service.yaml#L8-L8)

```text
  type: ClusterIP
```

### platform: [migrations/petclinic/sql/schema.sql:1–1](evidence/platform/migrations/petclinic/sql/schema.sql#L1-L1)

```text
CREATE TABLE IF NOT EXISTS vets (
```
