# Package checks and test reports

The Checks panel runs native MPS model validation and Testspraak against the
package's selected saved root versions. It displays each test case's name,
suite, status and failure message, with totals for passed, failed, errors and
not-run cases. Model diagnostics appear separately. A runtime/preparation
failure is explicitly shown as tests not having run.

The release worker discovers the common `testspraak.structure.ITestGeval`
interface, including service, input/output and rule tests. Each case gets a
fresh evaluation context. Ignored/incomplete cases are reported as `SKIPPED`;
they are never counted as passed. Exceptions are recorded per case so remaining
cases still run. Validation errors, failed/error/skipped cases or mutations to
the persisted input prevent successful package checks.

`POST /api/v1/packages/{id}/checks` creates a new immutable candidate for the
same selected root versions, baseline and source revision. It preserves
reviewer assignments and requires fresh approvals. The command requires the
package author, Submit permission, an open package, its current revision and
an idempotency key. Concurrent runs and stale revisions are rejected. The UI
updates metadata/report progress without remounting the comparison editors.

Reviewer assignment is separate from package preparation. Adding or removing
reviewers retains the candidate, source versions, package digest, existing test
report and any queued/running check job. It advances the optimistic package
revision and starts a new approval round, preserving the existing requirement
for fresh approvals after changing reviewers. Previous votes remain auditable
but cannot count toward the new assignment, including after removing and
re-adding a reviewer. Saving the same reviewer list is a no-op.

Migration `V19__package_review_rounds.sql` adds the approval round to package
heads and votes; existing rows default to round zero without rewriting their
evidence. Comments use the candidate ID to keep their model anchors available
across reviewer metadata changes. Content changes and explicit **Run checks
again** still create new candidates and checks.

Reviewer fix verification (2026-09-29): 24 package workflow tests, two release
builder tests and one toolchain test passed against isolated PostgreSQL.
`test_interface.mjs` and `test_package_checks.mjs` passed, including retaining
the report, comparison editor and current comment anchors after reviewer edits.
After applying V19 and restarting the local application, both existing
packages retained their candidates, revisions, reviewers, jobs and five-test
reports. The served interface matched the updated source; live verification
did not modify package data. Evidence is in `out/reviewer-checks`.

## Runtime configuration

The reported preparation failure occurred at `RETAINING_DEPENDENCIES`, before
test execution. Four compiled RuntimeWorker class files no longer matched the
release toolchain's pinned hashes. The pin checks remain enforced. Changes to
the native worker require compilation followed by
`tools/prepare-release-toolchain.py` in the backend checkout. Existing candidates
retain their original evidence; use Run checks again for a new candidate.
Configuration mismatches now produce actionable messages, and other background
failures log their stack trace with the job ID.

## Implementation and verification

- Frontend: `example/package-checks.js`, `example/interface.js` and
  `example/interface.css`; the browser packaging and example server include
  the report module.
- Backend: `RuntimeWorker`, `ReleaseToolchain`, `BackgroundJobs`,
  `PackageController` and `PackageService` in the ALEFBackend checkout.
- Browser tests: `node tools/test_interface.mjs` and
  `node tools/test_package_checks.mjs` cover report states, escaping, responsive
  layout, permissions and retained comparison editors.
- Backend `PackageWorkflowTest` covers retry idempotency, permissions, exact
  contents, stale revisions and approval invalidation. `ReleaseToolchainTest`
  verifies that changed worker bytes cannot run under an old pin.
- `NativeReleaseEvidenceTest` uses the retained Sportclub fixture in an isolated
  worker. It checks five passing cases, two deliberately failing cases and one
  incomplete case, and five passing cases after correction. Every run checks
  that persisted source data is unchanged. Enable with `ALEF_WORKER_COMMAND`.

Local verification artifacts are in `out/package-checks`. Live verification
uses `tools/verify_package_checks_live.mjs`; `--run` explicitly starts a new
check run for the development package and therefore creates a package revision.

The installed application was verified on 2026-09-29 with package v1.0.0
“First version”, revision 8: five passed, zero failed/error/not-run tests,
`sourceUnchanged: true`. Root selections and reviewer assignments were preserved,
the comparison editor remained mounted, and the report appeared through polling
without reloading the page. Native, browser and 25 backend workflow/toolchain
regression tests passed. The native browser editor bundle was unchanged.
