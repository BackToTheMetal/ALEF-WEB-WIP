# ALEF design revision 02

This is a revision of the standalone design prototype, **not an application
implementation**. Open http://127.0.0.1:18821/ or `index.html`. It makes no API
requests and all review, release and deployment data is illustrative. Reloading
resets its in-memory demonstration. The actual ALEF application remains unchanged.

## Editor: only the generated inspector

The right inspector sidebar has been removed. **Inspector and Diagnostics are
adjacent tabs in the same bottom panel.** Inspector contains only the captured
generated MPS inspector, including its metatags controls. It does not repeat the
selected-node name, model, language, access details or editor shortcut list.

The central editor now spans the width to the right of the project tree. The
bottom panel can collapse. On narrow screens it stays a bottom panel; only the
project tree becomes a drawer. The language-specific editor and inspector
rendering remain unchanged. Their images are static captures in this design;
the eventual application must mount the existing generated controls there.

## Changes → pull request → release

The navigation now separates **Model, Changes, Pull requests, Releases and
Environments**.

1. **Changes:** compare a selected saved workspace revision with the last released
   baseline. Browse changed models and inspect before/after differences before
   selecting Create pull request. Unsaved local edits are excluded.
2. **Create pull request:** enter a title, description and independent reviewer.
   The submitted complete workspace snapshot becomes an immutable PR revision.
3. **Review:** use Overview, Diff, Revisions and Checks. A changed-model list,
   side-by-side differences, contextual discussions, reviewer decisions and an
   explicit revision selector follow familiar pull-request conventions.
4. **Create release:** only an approved, validated, retained latest PR revision
   qualifies. Creating a release names and retains the **exact already-approved
   package**, without rebuilding it or including later workspace changes.
5. **Deploy:** select the released package and one or more authorized environments.
   Each target has an independent job and its own gates.

The review interaction takes inspiration from Bitbucket's diff, contextual
comments and review workflow. See Atlassian's
[review documentation](https://support.atlassian.com/bitbucket-cloud/docs/review-a-pull-request/)
and [create-pull-request workflow](https://support.atlassian.com/bitbucket-cloud/docs/create-a-pull-request/).
ALEF's explicitly frozen review revisions are the user's requirement, rather than
a claim that Bitbucket locks a source branch during review. This is not a Bitbucket
integration and does not create Git-host pull requests.

## Requested changes without modifying an immutable version

“Change the version in question” is represented as a **new revision of that same
pull request**, based on its exact reviewed snapshot:

`Open r1 → Changes requested → Correction draft based on r1 → Submit r2 → Fresh review → Approved r2 → Release`

The PR is the continuing conversation. Each submitted revision is a frozen
snapshot. When changes are requested, **Edit requested version** opens an editable
correction draft copied from the submitted revision, not from whatever happens to
be in the latest shared workspace. Users edit with the normal generated editor,
root leases and explicit Save, then submit the corrected draft to the same PR.

| Object | Can change? |
| --- | --- |
| Local or saved correction draft | Yes, with the normal permissions and locks |
| Submitted PR revision and its package bytes | No |
| PR's selected/latest revision pointer | Advances when a new correction is submitted |
| Review decision | New append-only decision, bound to one exact revision and digest |
| Released package | No; changes require another reviewed revision and release |

Earlier revisions remain inspectable. Discussions retain their original revision
and node/change anchors; they may be resolved or marked outdated, but are not
silently moved onto changed content. A newer revision gets fresh native validation
and a new independent approval. Approvals never transfer. Historical revisions
have no current approval action in the design.

Changes requested makes the **correction draft** editable, never the archived
snapshot. Opening a PR does not freeze the whole shared workspace or prevent other
work. Unrelated later edits are not automatically included. A released version is
not reopened in place.

The original implementation has one shared current workspace and immutable
release candidates; it does **not** already provide these PR revision groups,
contextual discussions or isolated correction drafts. They are proposed backend
extensions, not a relabeling of existing functionality. A correction workspace
must be isolated by identity and revision, retain opaque protected references,
and use the governed publication rules. If corrections also need to update the
current shared workspace, that is a separate revision-checked apply/merge with
conflict handling; never reset the workspace to an old PR snapshot.

Because ordinary edits are already saved to the shared workspace, the UI uses
Create release rather than implying a Git merge that the backend does not have.
The storage provider can remain Git, filesystem or database. The PR concept is
application review metadata, not a requirement for one Git branch per PR.

## Multiple test environments

The design uses a scalable environment list, grouped and filterable by type,
rather than a fixed Test/Production pair. The default illustration has:

| Environment | Type | Illustrated role |
| --- | --- | --- |
| Integration | Test | Required before Production EU |
| Regression | Test | Independent test target; optional for Production EU |
| Acceptance | Test | Required before Production EU |
| Production EU | Production | Requires Integration and Acceptance plus environment approval |

**Add environment** takes a name, type and adapter. Each environment has its own
configuration version, permissions, credentials, last confirmed installed package,
delivery progress, reconciliation and history. More test or production environments
fit the same list. The prototype allows adding another named test target and
filtering it; it does not save that configuration to PostgreSQL.

Promotion gates reference **specific environment IDs**, not a generic “test
passed” flag. For a production target, every selected prerequisite must have a
confirmed successful delivery of the exact same package digest. Other tests can
remain optional. Environment approval is an additional independent gate. Cycles
in prerequisite relationships must be rejected. Reconfiguration invalidates old
queued-job/gate assumptions and is checked again at dispatch.

Running or uncertain jobs are not shown as installed. The list separates the last
confirmed release from the delivery status for the selected release. An uncertain
acceptance delivery offers reconciliation of the original operation before retry.
Release approval remains separate from all target statuses. Rollback uses a
retained, approved prior package and obeys each selected target's gates.

All illustrated targets are **SIMULATED**. No actual ALEF execution is suggested.
The existing fixed `requireTestSuccess` configuration would need to become a list
of prerequisites for this design; that backend change has not been made.

## What is new versus preserved

- **Preserved:** existing projectional editor, generated inspector, project tree,
  root locks, explicit Save, diagnostics, whole-workspace packages, independent
  approval, immutable hashes and durable stub delivery concepts.
- **New layout:** generated-only bottom Inspector tab; Changes and Pull requests
  areas; diff browser; reviewer rail; separate release catalog; many-environment list.
- **New workflow to implement later:** stable PR identities with multiple immutable
  review revisions, isolated correction drafts, anchored discussions, named release
  creation from an approved revision, and per-target environment prerequisite lists.

The prototype changes roles between screens only to illustrate each permission
context. A real application must retain the logged-in identity, enforce backend
permissions and hide unavailable actions. Its placeholder data, shortened hashes,
diagrammatic model differences and instant validation/approval states are not
backend test evidence.

## Prototype verification

The following interactions were exercised in the in-app browser:

- Inspector/Diagnostics switching; only generated inspector content is present.
- Changes → create PR #19, revision 1 → request changes → edit correction draft →
  Save → submit revision 2 in the same PR.
- Revision 1 keeps its original displayed source hash/package digest, remains
  inspectable, and exposes no current approval button.
- Revision 2 starts with zero approvals, is independently approved in the preview,
  and its exact displayed package digest carries into the release.
- Adding “Performance test” produces four test targets; the Test filter shows all
  four without including Production EU.
- JavaScript syntax check passes; no browser console errors were observed during
  that desktop walkthrough. These checks validate design interactions only.
- The revised editor, PR diff and environment list were visually checked at
  390 × 844. Each had a document width of 390 px; wide environment tables scroll
  within their own container. The inspector stays in the bottom tab panel, and
  the removed metadata/shortcut headings are absent from the rendered editor.

Screenshots are `revision-02-editor.jpg`, `revision-02-changes.jpg`,
`revision-02-pull-request.jpg`, `revision-02-changes-requested.jpg`,
`revision-02-release.jpg`, and `revision-02-environments.jpg`. Earlier screenshots
and `iteration-01.html` preserve the previous proposal.

Run `node preview-server.cjs` in this directory to serve the artifact on
`127.0.0.1:18821`. The current prototype uses `styles.css`, `revision-02.css`,
`icons.js` and `prototype.js`; these are design artifacts, not application bundles.
