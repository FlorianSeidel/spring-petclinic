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
- `deploy/nordhelm-values.yaml` injects every Spring runtime setting as an
  environment variable. No application profile or runtime configuration file
  is baked into the image. The injected settings emit structured JSON to
  stdout and disable boot-time SQL initialization and in-process TLS.
- `WebConfiguration` keeps locale preference in a client-side cookie rather
  than an application HTTP session, so replicas retain no request state.
- Local Docker Compose configuration also requires credentials from the caller;
  it contains no password value or empty-password fallback.

No raw Kubernetes manifest, committed Secret, NodePort, environment-specific
profile, or per-instance deployment hook is part of the production path.
