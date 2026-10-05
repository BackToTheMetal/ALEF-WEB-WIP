# Deployment refusal after local package cleanup

On 2026-09-29, deployments of finalized package v1.8.1 to the simulated test
environment failed after `DISPATCH_AUTHORIZED`. The approval, permissions,
package bytes and environment configuration were valid.

The earlier requested package cleanup cleared `governance.platform_head` and
removed the old package catalog/deployment entries, but left
`execution_stub.platform_state.installed_digest` pointing to the old package.
Both test and production therefore appeared empty in the application while the
durable simulator retained the deleted installation. Its expected-installed
digest check correctly refused the inconsistent request.

## Repair

- Saved simulator state and all six retained operations before changing data.
- Cleared only the two stale installed pointers for the known deleted digest,
  with repository/target locks and checks that both targets were idle,
  simulated, empty in governance, and had no pending deployment jobs or
  retained catalog entry for the old package.
- Kept operation history and recorded two audit events explaining the repair.
- Retried the same approved v1.8.1 candidate/digest to test using the original
  administrator account. New deployment
  `4a18bae4-7c28-4b9c-bcd8-761382ade209` completed successfully. No promotion was
  requested. Package revision, check job and test report remained unchanged.

Repair scripts, state backup, API results and the served dashboard screenshot
are retained in `out/deployment-dispatch-fix`.

Future local simulator cleanup must update the governance and adapter-owned
installed pointers together, after checking that no operations are active.
Do not bypass the adapter's expected-digest check or automatically overwrite
its state on dispatch: legitimate unexpected target changes must still fail.

## Error reporting and verification

`DeploymentService` now preserves the specific public rejection reason in the
job result and records its reason/code in the refusal event. `DurableStubAdapter`
distinguishes an installed-package mismatch from an occupied target slot.

Eight deployment recovery tests and 24 package workflow tests passed. The new
coverage reproduces the stale simulator installation, checks that the refusal
does not bypass target validation, verifies success after explicit repair, and
checks specific revocation and injected-failure messages. Live verification
confirmed the test environment's installed digest and dashboard package label.
