# ALEF design revision 06

**Historical environment layout.** [Revision 07](REVISION-07.md) replaces the
independent target picker with a fixed promotion path and moves environment
management into Settings. The release and root-version semantics below remain.

**Design only.** Active preview: http://127.0.0.1:18821/. The ALEF application,
backend and native models are unchanged. The generated model editor and inspector
are existing captures. Preview edits, approvals and packages are illustrative
in-memory state that resets on reload; no real packaging or deployment occurs.

## Revised lifecycle

The latest comments supersede revision 05: approval no longer freezes a release.
The final, immutable state is **Packaged**, replacing **Released**. “When the
package is created it can be changed anymore” is interpreted as **cannot be
changed anymore**, consistent with this finalization step and earlier feedback.

| State | Open? | Available behavior |
| --- | --- | --- |
| In review | Yes | Edit in Model; selected saved changes create another review revision |
| Changes requested | Yes | Correct in Model; Save returns selected changes to In review |
| Approved | Yes | Edit in Model; a changed inclusion or included content invalidates approval and returns to In review |
| Packaged | No | Complete immutable artifact from the exact approved revision; deployment optional |
| Rejected | No | Retained closed proposal and comments; no deployable artifact |

Exactly one release may be open across In review, Changes requested and Approved.
The list offers Open current release while it exists. Start release is available
only after packaging or rejection closes it. A historical rejected fixture replaces
the former Outdated terminology; there is no competing open release.

The UI distinguishes **Start release** (open a review proposal) from **Create
package** (freeze its approved contents). This resolves the overloaded word
“package” without suggesting that an immutable artifact can be edited.
Approval remains tied to an exact revision. Create package verifies that approval
still matches the current revision. Historical revisions cannot be approved or
packaged through the current-review controls.

## Include changes from Model

Model has an **Include this root’s changes in release** checkbox. **Choose changes…**
lists changed roots and managed resources, including additions, deletions, moves
and renames. Inclusion is at the complete-root/resource level in this proposal,
not individual properties or lines. This is a proposed granularity, open to refinement.
The prototype demonstrates the current captured root plus the workspace checklist;
it does not implement a new editable generated runtime.

Changing choices is staged. Explicit Save stores the workspace draft and applies
inclusion choices. Until Save, pending choices and unsaved content are visibly
separate from the review revision. Existing included changes remain selected by
default. Editing and inclusion controls require the prototype root lock.

- Selected changes go to the one open release.
- Excluded changes remain in the saved workspace and remain visible in workspace
  comparisons. They do not alter an approved release when its selected graph is unchanged.
- Excluding a previously included change alters the review graph and therefore
  invalidates approval too.
- A complete package contains selected changes plus the baseline versions of
  excluded roots/resources. An excluded new root is absent; an excluded deletion
  retains its baseline root. This is a complete snapshot, not a partial delta.
- Inclusion choices persist for the workspace across release cycles in this preview.

Cross-root dependencies must be validated against this composed graph in the real
application. A selected service change may depend on an excluded root change;
the production UI must require the necessary dependency changes or block packaging
with an actionable diagnostic. No real dependency checking runs in this prototype.
The pre-existing Checks tab remains explicitly illustrative.

## Root versions and retained review history

Root v13 remains v13 through edits, saves, review, approval and re-review.
Create package finalizes its exact contents. The first later edit creates v14.
Deployment is never required to start the next root version. An excluded draft
does not become finalized merely because another root was packaged.

Every saved review revision is retained as a fixed snapshot with its original
comments. Updating an open release selects a new current revision; it does not
rewrite earlier snapshots. Existing comparisons stay pinned. Approval applies to
the latest snapshot; packaging retains its identity and illustrative digest.

Packaged releases can remain undeployed indefinitely. Multiple test environments
remain supported. The deployment picker contains packaged, unrevoked releases only,
and all outcomes are labelled SIMULATED. Revocation keeps a packaged artifact
immutable and blocks new deployment; revoking an un-packaged approval returns the
open release to review.

## Presentation and responsive behavior

Existing typography, restrained colors, project trees, version visibility only in
the model/detail areas, generated-inspector-only dock, inline review comments and
comparison controls are preserved. The new inclusion row wraps on narrow screens;
its secondary explanatory text is hidden below 900px while the checkbox and
Choose changes action remain. The release filters may wrap. The editor retains
the existing scroll/collapse behavior and images.

## Browser verification performed

Verified the served revision 06 prototype in the in-app browser, plus JavaScript
syntax with node --check design-06.js:

| Journey | Observed result |
| --- | --- |
| Initial Releases list | One In review release; historical Packaged and Rejected entries; no Released or Outdated status |
| Uncheck current root, Save | Revision 3 excludes that root's changes; changed tree drops from seven entries to six |
| Approve revision 3 | Approved / not packaged; Edit in Model and Create package available |
| Edit excluded root, Save | Workspace changed; same approval and release revision retained |
| Include root, Save | Same release v1.4.0 returns to In review as revision 4; root remains v13 |
| Exclude execution.properties in Choose changes | Saved revision 5 omits that resource difference |
| Approve revision 5, Create package | Packaged; editor action absent; deployment separate |
| Edit and save after packaging | Workspace root v14; packaged revision 5, digest and root v13 unchanged |
| Start v1.5.0 | Exactly one open row; Start release action absent while it is open |
| Reject v1.5.0 | Rejected and closed; Start release available again |

These checks demonstrate prototype state/navigation only, not native model
compatibility, authentication, cryptographic digests or concurrent backend safety.
The real backend must atomically enforce the open-release uniqueness rule and
serialize Save, approval and packaging against expected revision identities.
Packaging must validate dependency completeness, authorizations and current
approval. These changes to workflow semantics still need application/backend
implementation in a separately authorized task.

## Files and screenshots

The main entry index.html loads design-06.js and revision-06.css alongside existing
styles/assets. iteration-06.html is the explicit revision entry; iteration-05.html
preserves the preceding workflow. README.md identifies the current design.

- [Releases](revision-06-releases.jpg)
- [Model inclusion](revision-06-model-inclusion.jpg)
- [Approved, still editable](revision-06-approved.jpg)
- [Packaged, immutable](revision-06-packaged.jpg)

Run node preview-server.cjs from this directory if the existing local preview
server is not already running. No application build is needed.
