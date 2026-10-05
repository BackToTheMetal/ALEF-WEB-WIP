# Package deletion

The Packaging list and package page have a **Delete package** button with a
confirmation dialog. The package author needs Submit access; a workspace
administrator with Manage access can also delete. Review access is still
required. Users without these permissions do not see the button.

Only packages that have never been deployed can be deleted. Review approval
or finalization alone does not prevent deletion. Current installations, past
successful deployments, and active or unreconciled deployments prevent it.
The disabled button explains the reason. A request refused before target
acceptance does not count as a successful deployment.

`DELETE /api/v1/packages/{id}` takes `expectedRevision` and an `Idempotency-Key`.
The backend verifies permissions, the current revision and deployment history
under the same repository lock used to request deployments. Deleted candidates
cannot be submitted for deployment, including through the API.

Migration V20 records an immutable catalog deletion. The package disappears
from Packaging, comparison choices, new baseline choices and project links.
Saved workspace content, package revisions, reviews, build artifacts and
deployment records remain intact. A package already based on the removed
package can still read its retained baseline. The removed version number stays
reserved. Queued checks are cancelled; running checks receive a cancellation
request. Further workspace saves no longer update the removed package.

## Verification

Backend regression cases cover permissions, stale revisions, idempotency,
workspace preservation, check cancellation, reviewed packages, retained
baselines, new deployment rejection, current and historical deployments, and
requests refused before acceptance. Browser coverage checks both button
locations, permission visibility, disabled reasons, confirmation cancellation,
error recovery, request revision/idempotency and project-link refresh.

On 2026-09-29, all 30 PackageWorkflowTest cases and eight DeploymentRecoveryTest
cases passed, along with the interface browser suite. Migration V20 and the
browser/backend update were installed locally. Live author and administrator
checks confirmed that v1.0.0 is deletable and the deployed v1.8.1 is protected;
the reviewer has no deletion control. Both confirmation dialogs were cancelled
in the live check. Package revisions, workspace versions and installed packages
were unchanged. Served JavaScript and CSS matched the source hashes.

Implementation backups and local verification artifacts are retained under
`out/package-deletion`. The backend sources are in the sibling ALEFBackend
checkout; browser sources are `example/interface.js` and `example/interface.css`.
