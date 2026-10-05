# ALEF design revision 05

**Historical.** [Revision 06](REVISION-06.md) supersedes approval as the freeze boundary and automatic inclusion of all changes. View this earlier workflow through iteration-05.html.

**Design only.** The active preview is http://127.0.0.1:18821/. The application,
backend, native models and upstream checkout are unchanged. All workflow state
is local demonstration data and resets on reload. The generated editor and
inspector remain existing captures, not a newly implemented editor/runtime.

## One active package, editable during review

The latest feedback replaces two revision 04 rules: there are no parallel active
packages, and being In review does not prevent model editing.

- One active package contains all saved changes in the shared workspace, across
  every root, model and managed resource. No root selection or competing package
  creation is offered.
- The Releases list shows that active package alongside finalized historical
  releases. Earlier approved or released packages remain available for comparison
  and deployment; they are not parallel work in progress. The v1.4.1 parallel fixture
  has been removed.
- While a package is active, the list offers **Open active package**. Its detail
  offers **Edit in Model**, including while In review. Creation also checks for an
  existing active package and opens it instead of creating another.
- Editing still requires the root lock. **Preview edit** simulates a change in
  this prototype; **Save** captures all saved workspace content into the same
  package. Unsaved local edits remain separate until Save.
- Save retains the package identity and release version, adds a numbered revision,
  and sets its status to In review. There is no replacement-package action.
- Requested changes or rejection can be corrected in Model. The next Save updates
  that same package and makes the new revision available for review.

This interprets “cannot have multiple packages” as **one active package at a time**.
Retaining finalized releases is necessary for the requested release comparisons,
promotion, rollback and approved-but-undeployed states.

## Package identity, revisions and root versions

These are separate concepts:

| Concept | Example | Changes when |
| --- | --- | --- |
| Package identity | REL-18 | A new package is opened after the previous one is finalized |
| Release version | v1.4.0 | A later release package is created with its own version |
| Saved revision | Revision 2 → 3 → 4 | Workspace edits are explicitly saved into the active package |
| Root version | v13 | Remains v13 during edits, saves and review; the first edit after approval starts v14 |

The active package is an evolving review object. Each saved revision is an
immutable whole-workspace snapshot in its history. Updating the package changes
which revision is current; it does not overwrite previous snapshots or introduce
another active package. This is the explicit revised interpretation of immutability.

Approval binds the exact latest revision and its digest, freezes the package
contents and finalizes the included root versions. Review decisions in the
prototype carry the revision identity; historical or stale revisions cannot be
approved. Review is unavailable while this tab has pending unsaved changes.
Approved contents cannot be edited through Edit in Model.

The next edit starts the next root number, such as v14, and belongs to a future
package. Unchanged roots keep their finalized numbers. No deployment is required
to begin the next version cycle; approved packages can remain undeployed.

## Review and comparisons

The existing clean release layout, Version column, Compare / Against selectors,
generated-inspector-only bottom panel and multiple test environments are retained.
All stub deployments remain labelled **SIMULATED**.

Compare selects a saved revision of the open package. Against can select an earlier
revision of the same package or a retained release. The selectors pin snapshots,
so an already-selected comparison does not silently change after a Save.
The Activity / history tab lists revisions of one package, not separate candidates.

Discussions remain attached to their original immutable revision, root and side's
source snapshot. Requested-change messages stay inside the diff/editor context.
Earlier feedback remains accessible in revision history and from Model's feedback
links. It is not silently reattached to changed content in a new revision.

## Eventual application implementation

The real backend must enforce the one-active-package invariant and serialize Save
against approval using the expected workspace and package revisions. If Save wins,
the previous revision's pending decision must not approve the new content. If
approval wins, Save cannot overwrite it; preserve the local draft for reconciliation
into the next root version. Validation and review evidence must identify the exact
saved revision. Earlier decisions and comments remain audit history.

Root authorization, server-enforced leases, independent approver checks and package
retention remain required production behavior. The prototype demonstrates their
presentation; it does not implement identity, native persistence, real validation,
concurrent review or deployment. The previous technical backend design has not
been modified by this visual-design task.

## Verification

Checked against the served prototype in the in-app browser:

| Interaction | Observed result |
| --- | --- |
| Initial Releases list | One active v1.4.0 package, three finalized historical releases; no parallel v1.4.1 row |
| Open active package → Edit in Model | Root v13 remains In review and can be locked |
| Edit before acquiring lock | Preview edit disabled |
| Lock → edit → Save | Same REL-18 / v1.4.0; revision 2 becomes 3; root stays v13 |
| Request changes → edit in Model → Save | Same REL-18 / v1.4.0; revision 4 becomes current and In review |
| Select revision 3 | Its feedback remains; no Review action for this historical revision |
| Approve latest revision | Revision 4 finalized; Approved / not deployed; Edit in Model absent |
| First edit after approval | Root v14 Draft, with no active package attached yet |
| Save v14 and revisit approved v1.4.0 | Revision 4, digest and included root v13 unchanged |
| Create v1.5.0 | Exactly one active package; creation action replaced with Open active package |
| JavaScript syntax | `node --check design-05.js` passed |

These checks establish prototype navigation and state transitions, not backend
transaction safety or native editor compatibility.

Screenshots: [one active package](revision-05-single-package.jpg),
[editing during review](revision-05-edit-during-review.jpg), and
[the package review](revision-05-package-review.jpg).

## Files and preview

`index.html` loads `design-05.js` and the existing styles/editor assets. The previous
workflow is preserved in `iteration-04.html` with `design-04.js`; `iteration-05.html`
is an explicit entry to this new design. `README.md` points to these current notes.
The existing local preview server serves the files without an application build.
If needed, run `node preview-server.cjs` from this design directory.
