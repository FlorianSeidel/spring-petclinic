# Platform migration readiness

This repository is deployed through the shared Nordhelm delivery model:

- `Dockerfile` packages one non-root foreground Java process.
- `deploy/nordhelm-values.yaml` declares values against `nordhelm-base`; it
  uses a ClusterIP service, platform ingress TLS termination, independent
  liveness/readiness probes, Secrets Store CSI delivery, and the platform's
  injected OpenTelemetry Java agent.
- `.github/workflows/platform-delivery.yml` builds and scans one image per
  commit, executes `deploy/migrations/V001__petclinic_schema.sql` before
  rollout, and promotes the same immutable digest through environments.
- `application.properties` requires database credentials and the HTTP port
  from the environment, emits structured JSON to stdout, and disables all
  boot-time SQL initialization and in-process TLS.

No raw Kubernetes manifest, committed Secret, NodePort, environment-specific
profile, or per-instance deployment hook is part of the production path.
