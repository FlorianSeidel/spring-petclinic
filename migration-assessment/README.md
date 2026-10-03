# Petclinic migration assessment

This assessment supplies a reviewable migration plan for the historical Petclinic baseline [`818c4136`](https://github.com/FlorianSeidel/spring-petclinic/commit/818c4136ea971c21674525f9053de0d9c7ad8cfe), evaluated against the pinned local demo platform. This PR contains assessment and validation artifacts only. The implementation is delivered in a separate migration PR against the same demo baseline.

## Review in this order

1. [Report: findings, ownership, changes, and success criteria](report.md)
2. [Evidence: immutable application citations and pinned platform snapshots](evidence.md)
3. [Summary: deterministic classification, effort, file actions, and evidence](summary.json)
4. [Validation: live checks, tests, self-consistency, and remaining gaps](validation/README.md)
5. [Manifest: pinned inputs and original model profile](manifest.json)

## Assessment result

The baseline is classified **migration_needs_work**, with deterministic application effort **18**. The plan identifies nine distinct application files. This effort is a relative policy measure, not a person-day estimate or a financial value calculation. All nine DP rules fail for the pinned baseline; the report identifies source violations and the remediation for each.

Application owners review the source changes. The platform lead owns the shared Jenkins test-gate gap. Senior leadership supplies business priority, delivery capacity, and acceptance criteria before allocating work across a portfolio.

## Provenance

The recorded assessments ran on 23 September 2026 using **gpt-6-sol / high**, with independent runs C and D. Both used the same pinned source, platform tree, prompt, schema, contract, register, and budget. The pipeline was subsequently configured to use **gpt-6.1-sol** for future runs; the evidence here retains its actual historical model.

The local platform tree is `d68124ca315d1c15b874bf03c1f0d88c09cd5493`. The cited platform files are included under `evidence/platform/` for review. Credential literals in published excerpts are redacted; the original summary hashes are retained in the consistency record. The manifest's original workstation path is omitted.

The fork's `main` already contains a later migration. The dedicated `codex/petclinic-demo-baseline` branch keeps this assessment and its implementation reproducible against the source that was actually assessed.
