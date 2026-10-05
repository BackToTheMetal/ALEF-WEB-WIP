# ALEF design revision 19 — extend packages and review before finalization

Open a package as its author. In **… → Add changes…**, select additional saved
versions in the popup. It reuses tag filters and version selection from New
package, with no inline diff preview. The current package is the starting point;
unselected contents and the original baseline remain intact. An explicitly
selected newer file version can replace the version already included.

Adding or removing changes creates a new review revision and returns the package
to In review. Earlier revisions, decisions and diff discussions stay in history.
Every assigned reviewer must approve the new revision. Successful additions clear
tags on exactly the selected saved versions and synchronize the Editor's tag
state, just like package creation. Cancellation, empty selections and stale
dialogs preserve contents and tags. Other open packages remain unchanged.

## Reviewers and authors

- **Add reviewers** is available in the reviewer summary and the author's menu.
  New packages start without reviewers. Each assigned reviewer is required;
  the author cannot be assigned to review their own package. Existing reviewer
  assignments are retained. Adding a reviewer preserves existing decisions but
  requires the new person's approval before finalization.
- The author's primary action is **Finalize**, disabled until all reviewers
  approve the current revision and it has valid, nonempty changes.
- Assigned reviewers see **Review…**, with **Approve revision** and **Request
  changes**. Each decision is tied to that person, package revision and digest.
  One reviewer's approval cannot override another person's request for changes.
  A reviewer can revise their own decision before finalization.
- In **Contents**, use **+** beside a diff line to comment. Reviewers and authors
  can reply and resolve discussions. Comments retain their exact revision, file,
  comparison side and line. Resolving a discussion does not approve the package.
- Finalize is an author-only action. It creates the immutable Packaged artifact;
  deployment remains a separate action. Finalized or historical contents cannot
  be modified or approved through these controls.

The study toolbar includes **View as** with four fixture identities. Use
`author Local` to add reviewers/changes and finalize, then `reviewer Local` or
another assigned identity to review. Switching identity closes pending dialogs.
This is a preview switch, not authentication or an invitation mechanism.

## Verification and scope

`node verify-19.cjs` exercises tag lifecycle, exact content preservation,
additional versions and deletions, stale/cancelled dialogs, author/reviewer
separation, multiple required approvals, change requests, review invalidation,
historical immutability and diff comment authorship/anchors.

These checks run the active prototype scripts in a Node VM with a small document
fixture. They do not validate browser layout, authentication or backend behavior.
The existing browser security restriction prevents automated visual inspection.
The design is in memory and resets on reload; no real model, approval, invitation
or deployment is changed. Previous iterations are preserved. Both index.html and
iteration-19.html load revision 19.
