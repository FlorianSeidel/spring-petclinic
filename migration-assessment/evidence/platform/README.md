# Local migration demo platform

## Assessment contract and platform backlog

The selected platform policy is [contract/contract.json](contract/contract.json).
Its nine DP rules describe deployable behavior. The
[decision and backlog register](contract/decisions.json) records open platform
choices and capabilities already decided for later implementation. A migration
assessment reads this repository at one pinned revision, including this
documentation, the Jenkinsfile, Kubernetes manifests, and Petclinic GitOps
files. The contract adds migration practices for test coverage, rollback, and
application-specific GitOps ownership. Planned backlog work is not treated as
implemented platform behavior.

The checked-in Petclinic GitOps manifests represent the later migrated commit; they
are a reference example, not evidence that an assessed earlier commit has migrated.

Application-side findings can reference a decision or backlog item without
becoming blocked. They set an explicit blocker only when migration cannot
finish before that item is resolved.

This folder builds a real, disposable Kubernetes target for migration
experiments. The platform uses k3d/K3s, a local OCI registry, Jenkins CI,
Argo CD delivery, CloudNativePG PostgreSQL, Sealed Secrets, Traefik ingress,
and a native admission policy. Its [contract](contract/PLATFORM-CONTRACT.md)
describes what an application must provide and what the live verifier checks.

The first migrated application is the inventory-pinned Spring Petclinic commit
in `versions.env`. Its source checkout under `runs/` is never modified or
executed. Jenkins clones the public fork into an isolated CI pod after the
assessment stage, builds its jar and image, and promotes the resulting digest
to an internal Git repository. Argo CD runs the versioned SQL as a PreSync Job
and then deploys the image. `bin/verify.py` records live outcomes by rule.
The pinned source currently fails its NoHttp Checkstyle rule on an existing
collector URL in `deploy/nordhelm-values.yaml`. The demo packaging command
skips Checkstyle and tests; it does not edit the source or treat that lint
failure as a platform-contract pass.

## Requirements

- macOS or Linux with Docker, k3d, kubectl, Helm, Git, and Python 3.9+.
- A Docker backend with approximately 8 GiB RAM and 6 CPUs available. On the
  development Mac, `colima start --cpu 6 --memory 8 --disk 60` provides it.
- Internet access on first install for pinned Helm charts, container images,
  Maven dependencies, and the public Petclinic source repository.

No GitHub token or cloud account is needed. The image registry and Git server
are local to the k3d cluster. The Git daemon deliberately permits writes on
the cluster-internal network for this disposable lab; it has no Ingress or
NodePort. Jenkins uses a privileged Docker-in-Docker sidecar to build images.
Do not expose this lab cluster or reuse its CI setup as a production platform.

## Run

From the repository root:

```bash
bash demo-platform/bin/up.sh
python3 demo-platform/bin/run-migration.py
python3 demo-platform/bin/verify.py
```

The first command is idempotent. It installs pinned chart versions, creates
PostgreSQL, seeds the private local GitOps repository, and configures the
Jenkins pipeline. The second command triggers the Jenkins job, waits for Argo
CD to report `Synced` and `Healthy`, and executes all contract checks. The
Petclinic page is at `http://petclinic.localhost:8088/` after deployment.
Verification JSON is written to `.local/verification.json` (ignored by Git).

To view Jenkins or Argo CD locally:

```bash
kubectl --context k3d-demo-platform -n ci port-forward service/jenkins 18080:8080
kubectl --context k3d-demo-platform -n argocd port-forward service/argocd-server 18081:80
```

Retrieve initial UI passwords from their Kubernetes Secrets using the
official chart instructions; passwords are never stored in this repository.
`bash demo-platform/bin/down.sh` deletes the disposable cluster and its local
data. The `up.sh` script preserves an existing GitOps repository when rerun.

## Assessment comparison

A pre-migration assessment uses the nine rules in `contract/contract.json`
and the source-backed schema at `../schemas/assessment.schema.json`. Supply
its frozen summary JSON to `python3 demo-platform/bin/run-migration.py
--assessment PATH`, or to `verify.py --assessment PATH` after deployment.
The verifier records whether a failing rule was resolved, a passing rule
remained satisfied, or an uncertainty was clarified. The assessment project
also compares proposed files with the actual application diff through
`portfolio-discovery compare migration`.

The live checks are deliberately narrower than a production certification.
They confirm healthy probe responses, but do not yet inject a database outage
to test readiness failure. A single local PostgreSQL instance cannot prove
production backup or high availability. Both limitations are explicit in the
contract so an assessment cannot quietly infer them.

## Layout

- `bin/`: bootstrap, Jenkins configuration, migration, and verification.
- `contract/`: reviewable and machine-readable platform rules.
- `git-server/`, `helm/`, `k8s/`: local platform definitions.
- `jenkins/Jenkinsfile`: pinned-source CI and digest promotion.
- `migrations/petclinic/`: Argo CD manifests plus SQL copied from the pinned
  target commit. The copy keeps provenance while leaving `runs/**/source/`
  read-only.
