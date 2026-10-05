# ALEF design revision 07

**Design only.** The active prototype remains http://127.0.0.1:18821/.
The ALEF application, backend, model runtime and real execution platforms are
unchanged. All deployment outcomes here are SIMULATED, held only in page memory
and reset by a reload. No execution jobs or external configuration are created.

## Focused Environments changes

Removed the environment-count summary, explanatory “independent” text, the
release-status selector, the recent-deployments table and section prerequisite
explanations. The page now contains a compact promotion path and one ordered
environment table: environment, installed package, status and next action.
The SIMULATED label remains visible.

Adding and editing environments moved to **Settings**, available through the gear
in the application header. Settings also controls promotion order and the approval
requirement for an environment. No Add environment or configuration buttons remain
in the operational Environments table.

The visual style, generated editor/inspector captures, release review and root
version lifecycle from revision 06 are preserved.

## One fixed promotion path

The proposed initial path uses the existing four environments in this order:

**Integration → Regression → Acceptance → Production EU**

This is an explicit design assumption based on their existing names. It can be
configured in Settings. Test environments precede production; more test or
production environments can be added. All steps belong to the path—Regression is
no longer an optional branch.

1. **Deploy package** chooses a packaged, approved, unrevoked release and deploys
   it to the first environment. The destination is fixed, not a target picker.
2. While a deployment runs, later steps remain unavailable.
3. Success enables **Promote to [next environment]** on that next row only.
4. Each promotion retains the same package revision and digest. No content is
   rebuilt or substituted, and no stage can be skipped.
5. Environment approval, when configured, is a prerequisite for that step.
   Production requires it in the initial configuration.
6. Once the entire path completes, another package can start at the first step.
   Re-deploying an older retained package also starts at the first step.

There is one active promotion run in this initial design. Starting another is
blocked until the current run completes or is cancelled. Confirmed installations
remain after cancellation. An uncertain outcome must be reconciled before the run
can be cancelled or continued.

The path and environment settings are captured when the deployment starts.
Settings edits apply to the next deployment, so an administrator cannot silently
skip a required step by reordering an active run. The package's approval and exact
revision/digest are checked again by the prototype before each simulated step.

## Simulated progress and failure behavior

The progress dialog explicitly provides Complete simulation, Simulate failure
and Simulate uncertain outcome controls. These are design demonstration controls.
A failed step offers Retry this step; an uncertain one offers Reconcile. Both
remain tied to the original operation identity. Later steps stay blocked until
success is confirmed. The installed-package column changes only after success.

The broad recent-deployments table is removed. Operation details remain available
from an active row's progress/reconciliation action, and simulated successes are
recorded in the release activity. Removal from this screen does not imply deleting
operational history or audit requirements in the eventual application.

The production approval checkbox is labelled as a preview. Real independent
approval, authorization, durable operations, reconciliation and target execution
remain backend responsibilities; this artifact does not implement them.

## Settings and responsive behavior

Settings offers Add environment, Edit and earlier/later order controls. Names
must be unique. Test environments are inserted before production; the ordering
controls keep test environments before production. At least one test environment
remains at the beginning. Production approval cannot be disabled in this proposal.

The path wraps across lines on narrower viewports. Environment tables retain
horizontal scrolling so names, package identity and actions remain legible.
Settings is a small header control rather than another primary workflow tab.

## Verification performed

Checked the served interactive prototype in the in-app browser:

| Journey | Observed result |
| --- | --- |
| Initial Environments | Removed summaries, selector, recent history and configuration controls absent |
| Deploy package v1.3.1 | Only Integration offered as first destination; review-only v1.4.0 absent from package choices |
| Simulate failure | Later promotions unavailable; retry retained operation sim-1 |
| Confirm Integration success | Only Promote to Regression became available |
| Add Performance in Settings during run | Settings gained a fifth step; active path retained its original four steps |
| Regression uncertain outcome | Only reconciliation offered; no downstream promotion |
| Reconcile, promote through Acceptance | Same package digest retained; Production became the next step |
| Configure approval on Integration | Initial deployment also blocked until its preview approval was granted |
| Production without environment approval | Promotion blocked with an explicit approval message |
| Complete production simulation | All four installations showed v1.3.1 with digest ad89076b…229c |
| Start another deployment | New path included Performance before Production EU |
| JavaScript syntax | node --check passed for design-07.js and environment-design-07.js |

These checks establish the prototype's behavior, not real execution, identity or
durable deployment guarantees. The requested application/backend remains untouched.

## Artifacts

- [Simplified Environments](revision-07-environments.jpg)
- [Next available promotion](revision-07-promotion.jpg)
- [Settings](revision-07-settings.jpg)
- [Completed simulated path](revision-07-completed.jpg)

iteration-07.html loads design-07.js plus environment-design-07.js and revision-07.css,
alongside the existing styles and assets. It preserves this revision; index.html
now shows revision 08. The existing preview server serves these design files
without an application build.
