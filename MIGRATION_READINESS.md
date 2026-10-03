# Petclinic: selected local platform delivery

## Active path

The selected target is demo-platform-2.3, platform tree
`8108b78d2c5145d4fb925a5861b70bc3d4c21f47`. The existing Jenkins `petclinic`
job checks out this application's exact `TARGET_SHA`, packages the application,
builds the non-root Dockerfile once, pushes to the platform registry, and promotes
that image digest through internal Git and Argo CD. GitHub Actions is an
informational, manually dispatched handoff only; it does not build or deploy.
The former `deploy/nordhelm-values.yaml` and `deploy/migrations/V001__petclinic_schema.sql`
are inactive historical artifacts, not inputs to this selected pipeline.

Use the existing platform-owned `migrations/petclinic` Deployment, Service,
Ingress, CNPG cluster, SQL ConfigMap and PreSync Job. They already provide
ClusterIP/Traefik routing, non-root and read-only-root-filesystem security,
writable `/tmp`, CPU/memory limits, CNPG-generated credential Secret references,
structured stdout logs and independent `/livez` and database-aware `/readyz`
probes on the application listener. Connection credentials and runtime settings
are injected by the platform; none are baked into the image. Startup SQL
initialization and Hibernate DDL stay disabled.

The PreSync Job applies **both** `migrations/petclinic/sql/schema.sql` and
`data.sql` before application startup. Do not substitute the former incomplete
V001 script. `PlatformMigrationIntegrationTests` consumes these exact external
platform SQL files, checks all mapped tables and fixtures, repeat bootstrap,
read/write access, dependency-aware readiness/recovery, and real English/German
rendering with the replica-portable locale cookie. The resource basename is
`messages/messages`, with optional `SPRING_MESSAGES_BASENAME` override.

## Verification and boundaries

Run the targeted tests in a separate writable migration workspace, supplying
`TEST_DATABASE_URL`, `TEST_DATABASE_USER`, `TEST_DATABASE_PASSWORD` for an
isolated disposable PostgreSQL database and `PLATFORM_SQL_DIR` for the pinned
platform SQL. Test bootstrap is limited to that isolated database. Existing
cluster data is preserved; this change does not authorize disposal of retained
production data. Production retention, cutover, downtime and rollback approval
remain application-owner prerequisites (DEC-03), not missing platform features.

The selected platform supports stdout logging, but equivalence to Nordhelm's
injected OpenTelemetry agent/ADOT collector has not been established. This
migration does not claim preserved production tracing; the application owner
must confirm required telemetry and arrange an equivalent collector/agent if
needed. AWS-backed external chart and secret provenance are historical, not
selected runtime dependencies. No DynamoDB, SNS or S3 feature is removed here.
Jenkins currently packages with tests skipped: the targeted integration tests
must pass separately, and enforcing them in the delivery gate remains follow-up
work. Live deployment verification is separate evidence from a source scan.
