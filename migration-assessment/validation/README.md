# Validation and limits

## Recorded local migration experiment

The separate migration implementation replays the nine-file patch previously tested from baseline `818c4136`. The local source commit used in that experiment was `e53c0d37fc85658b0eb0dadcd539fcc73d719faf`; it was delivered through a disposable local Git server rather than GitHub.

Jenkins build **#9** packaged the source, built and pushed an OCI image, and promoted its digest into the local GitOps repository. Argo CD reported **Synced / Healthy**; the PreSync SQL job completed. `/owners/1` and `/readyz` returned 200; `/actuator/env` returned 404. The [live contract snapshot](live-contract.json) records all nine DP checks as **pass** after remediation. The cluster was **K3s/k3d**, using CloudNativePG, Jenkins, and Argo CD.

The digest was `sha256:c766739095e5b0d5ed0b2012b6bec9e53ed74d8f8404941076f8fcb36e5ea956`; the recorded GitOps revision was `694ca2ae71b1f36bb1535ffc02847ab1c3e7b10f`. The verifier's `git_revision` is the GitOps revision, not the application source commit. A pre-migration fail followed by a post-migration pass is recorded as **resolved**, not as a prediction mismatch that disproves the original assessment.

## Targeted tests

- [PostgreSQL integration test](PostgresIntegrationTests.txt): **3 passed**, zero skipped, against isolated PostgreSQL 17.6. Test setup explicitly provides schema and seed data after disabling application startup SQL.
- [Health integration test](PlatformHealthIntegrationTests.txt): **1 passed**, zero skipped. Using the default test datasource, it closes the connection pool and verifies readiness becomes 503 while liveness remains 200. The live healthy-probe check alone does not demonstrate this failure behavior.
- Maven formatting validation passed in the recorded experiment.

These are preserved historical results for the implementation patch. They are not a new GitHub Actions run or a full production acceptance suite.

## Self-consistency

The [comparison record](consistency.json) checks two independent assessments against the same pinned inputs. Both report effort **18** and the same nine failing rule verdicts. Their file plans differ in placement of the health test. Run C's nine application paths match the implementation paths exactly; Run D places the health test under `system/`.

The implementation followed run C's plan. This shows that the plan was executable and sufficient for these local checks; it does not independently establish the minimum required migration effort or prove an unattended prompt optimizer.

## Remaining work and review gates

- **Platform-owned test gate:** Jenkins packaged with `-DskipTests`. The targeted tests ran separately. The platform lead must make failing tests block image promotion before treating the pipeline as an enforced quality gate.
- **Production data and rollback:** The live run reused the existing local database. It did not transfer production data or exercise rollback after an incompatible schema change.
- **Production platform and value:** Local checks do not establish EKS behavior, high availability, production security, portfolio-scale accuracy, or customer value. The proposed pilot must measure those outcomes separately.

The original Jenkins job and platform settings were restored after the experiment. A restoring GitOps commit `10d10f84ecbdd87e964e8469242723479566189a` returned the original demo image to Synced / Healthy; a fresh baseline verification passed.
