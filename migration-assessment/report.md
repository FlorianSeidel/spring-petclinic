# Migration assessment: spring-petclinic

Application commit: `818c4136ea971c21674525f9053de0d9c7ad8cfe`

Platform tree: `d68124ca315d1c15b874bf03c1f0d88c09cd5493`

Migration effort: **18**

Category: **migration_needs_work**

## Rule coverage

- DP-01: fail — Applicable: Petclinic is delivered as an OCI image. The pinned source lacks the build definition required by Jenkins and its own manifest selects an unrelated image, meeting two assessment fail conditions.
- DP-02: fail — Applicable: Petclinic runs in an application pod. The pinned workload has no required security settings and the source has no container build establishing a non-root user. The later platform Deployment is a reference, not proof for this commit.
- DP-03: fail — Applicable: the application serves HTTP. Its pinned manifest uses NodePort, an explicit assessment failure, although the later platform reference supplies a Traefik route.
- DP-04: fail — Applicable: the application depends on PostgreSQL. The legacy manifest has distinct paths but does not include db in readiness; official Spring Boot 4.1 documentation confirms it is not included by default. The selected platform reference does include it.
- DP-05: fail — Applicable: Petclinic has a PostgreSQL driver and relational data. Its pinned source supplies an app-owned database, a legacy binding to that database, and a localhost profile fallback; these meet DP-05 assessment fail conditions despite the later CloudNativePG reference.
- DP-06: fail — Applicable: the application has PostgreSQL schema and seed SQL. The pinned PostgreSQL profile performs startup SQL, an assessment failure. The existing platform PreSync Job and deployment override do not erase this independent source setting.
- DP-07: fail — Applicable: the application needs database credentials. The pinned legacy Kubernetes Secret and active legacy PostgreSQL profile include literal credentials. The later GitOps reference correctly uses an operator-generated Secret but does not make the pinned source pass.
- DP-08: fail — Applicable: this application is delivered through GitOps on the selected platform. The pinned application retains direct Kind deployment of incompatible manifests, while the later platform pipeline supplies the intended digest promotion.
- DP-09: fail — Applicable: the pinned source defines an application container. Its legacy container specification has no resources.requests; the later platform reference declares both requests but does not make the earlier manifest compliant.

## Findings

### MF-ffc2397626a1: Supply the application OCI build

Owner: application

The pinned application has no Dockerfile compatible with the existing Jenkins docker build step. Its container instructions also describe a mutable latest tag.

Change: Add the non-root Java 17 OCI build definition and update its build documentation.

Success: Jenkins can build the packaged application jar from a pinned commit, push it, obtain a sha256 digest, and run the resulting image as the non-root user required by the existing platform Deployment.

- File action: `application:Dockerfile` — Add a Dockerfile that copies the Maven-packaged jar from target/, uses a pinned Java 17 runtime image, creates the non-root runtime user, and starts the jar with /tmp as its writable temporary directory. This new file supplies the build input used by the existing Jenkins docker build step.
- File action: `application:README.md` — Replace the statements that there is no Dockerfile and that the runtime image is a latest tag with instructions for the new Dockerfile and Jenkins digest promotion.

### MF-7e8414a37253: Retire the incompatible workload manifest

Owner: application

The pinned application retains a separate deployment path with an unrelated image, NodePort, no required runtime security context or resource requests, and readiness that does not add the database indicator. The later platform reference already contains the target settings but does not migrate this earlier application commit.

Change: Retire the incompatible application workload manifest and use the existing app-specific platform GitOps Deployment, Service, and Ingress as the migration path.

Success: No application delivery path selects the legacy workload; the selected GitOps workload uses the Jenkins digest, non-root read-only runtime, ClusterIP and Traefik route, distinct probes with database readiness, operator Secret, and CPU and memory requests.

- File action: `application:k8s/petclinic.yml` — Remove the legacy workload manifest so the incompatible third-party image, NodePort route, incomplete readiness configuration, missing security context, and missing requests cannot be selected as the application deployment path. The existing platform Petclinic Deployment, Service, and Ingress supply the target path.

### MF-10db5a1e7948: Retire the application-owned database and plaintext Secret

Owner: application

The application repository contains an app-owned PostgreSQL Deployment and a plaintext Kubernetes Secret. The selected platform already provisions CloudNativePG and refers to its generated application Secret.

Change: Remove the legacy database manifest from the application delivery path.

Success: GitOps contains no app-owned PostgreSQL workload or plaintext database Secret, and the application and migration Job use the CloudNativePG service and generated Secret.

- File action: `application:k8s/db.yml` — Remove the app-owned PostgreSQL Deployment, Service, and plaintext Secret manifest. The existing CloudNativePG Cluster and operator-generated application Secret provide the selected platform database path.

### MF-edd4d6f0ca35: Remove PostgreSQL startup initialization and credential fallbacks

Owner: application

The PostgreSQL profile still enables startup SQL and supplies local connection and literal credential fallbacks. The platform's PreSync Job and Deployment overrides are already present, but the profile is an independent source violation.

Change: Require supplied PostgreSQL connection values in that profile, disable its startup SQL, adapt the profile integration test to provision data as test setup, and document the new behavior.

Success: The PostgreSQL profile cannot silently use localhost or the committed password; application startup performs no schema or seed SQL; the PostgreSQL integration test provisions its own data and verifies that behavior.

- File action: `application:src/main/resources/application-postgres.properties` — Remove the PostgreSQL profile's localhost URL and literal username/password fallbacks, requiring supplied connection values when that profile is used; change its SQL initialization mode from always to never.
- File action: `application:src/test/java/org/springframework/samples/petclinic/PostgresIntegrationTests.java` — Adapt the PostgreSQL integration test to provision schema and seed data explicitly as test setup and assert that the profile does not initialize SQL during application startup.
- File action: `application:README.md` — Update the PostgreSQL setup section to document externally supplied connection values and pre-rollout SQL provisioning for the migrated profile.

### MF-6246f3bf8944: Retire direct Kind delivery

Owner: application

An application workflow still applies the legacy k8s directory directly to Kind, bypassing the selected Jenkins-to-internal-Git-to-Argo-CD delivery path.

Change: Retire that legacy deployment workflow after removing its manifests.

Success: No application CI workflow deploys the retired k8s directory; migration delivery is through the existing digest-promoting Jenkins pipeline and Argo CD Application.

- File action: `application:.github/workflows/deploy-and-test-cluster.yml` — Retire the Kind workflow that applies k8s/ directly, since those legacy manifests are removed and the migration uses Jenkins digest promotion and Argo CD.

### MF-98ce6f62476b: Narrow default Actuator exposure

Owner: application

The application's default configuration exposes every Actuator web endpoint. The selected platform manifest narrows exposure, but the broad source default remains an independent runtime risk.

Change: Set the source default to health and info.

Success: The application's default web Actuator exposure is limited to health and info, matching the selected platform runtime.

- File action: `application:src/main/resources/application.properties` — Replace wildcard Actuator web exposure with the health and info endpoints needed for the migrated runtime.

### MF-29b0d4221feb: Test database-dependent readiness failure

Owner: application

Existing tests and the platform's healthy-probe check do not verify that database loss makes readiness fail while liveness remains independent.

Change: Add a targeted integration test for the selected health configuration and PostgreSQL outage behavior.

Success: The targeted test observes distinct healthy probes, then observes readiness fail on database loss without making liveness depend on the database.

- File action: `application:src/test/java/org/springframework/samples/petclinic/PlatformHealthIntegrationTests.java` — Add an integration test using the platform-equivalent health settings and PostgreSQL dependency that checks /livez and /readyz separately and verifies readiness becomes unavailable when the database connection fails.

### MF-baab26851f0b: Run application tests before Jenkins promotion

Owner: platform_shared

The shared Jenkins packaging step skips application tests before building and promoting the image, including the PostgreSQL boundary test.

Change: Make the Maven container able to use the existing Docker sidecar and run tests before image build and promotion.

Success: A failing application integration test prevents Jenkins from building and promoting a digest; successful tests allow promotion.

- File action: `platform:jenkins/Jenkinsfile` — Configure the Maven Jenkins container to reach the existing Docker sidecar for integration tests and remove -DskipTests from the package command, so tests gate image build and promotion.

## File effort trace

- `application:.github/workflows/deploy-and-test-cluster.yml`: 2
- `application:Dockerfile`: 2
- `application:README.md`: 2
- `application:k8s/db.yml`: 2
- `application:k8s/petclinic.yml`: 2
- `application:src/main/resources/application-postgres.properties`: 2
- `application:src/main/resources/application.properties`: 2
- `application:src/test/java/org/springframework/samples/petclinic/PlatformHealthIntegrationTests.java`: 2
- `application:src/test/java/org/springframework/samples/petclinic/PostgresIntegrationTests.java`: 2

## Platform signals

- backlog PB-02: nonblocking — Petclinic changes from startup schema and seed initialization to a PreSync SQL Job.

## Evidence gaps

No mechanical evidence gaps detected. Architecture judgments remain reviewable.
