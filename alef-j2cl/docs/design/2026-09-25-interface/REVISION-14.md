# ALEF design revision 14 — release-scoped editor

Design-only refinement, 27 September 2026. Active preview:
http://127.0.0.1:18821/#release

## Two editing contexts

The main **Editor** always shows the latest workspace. Saving there changes only
the workspace; it cannot replace an open release's graph or invalidate its approval.
Workspace options link to the open release editor and a workspace/release comparison.

Each release now has its own **Editor** tab in place of **Included roots**.
The old **Edit in Editor** heading action is removed. The local editor includes:

- A project tree limited to that revision's included roots and managed resources.
- All, Changed and Unchanged filters, with search across included names.
- File tabs, root versions, a release-specific lock and Save controls.
- Editable changed files and previously unchanged files in an open release.
- Existing feedback and comments beside the file contents; line comments share
  their revision/root/line anchors with the diff.

Deleted roots remain visible in Changes but are absent from the included files.
An unchanged root becomes changed when edited, and advances its finalized version
when saved. Drafts survive file/tab navigation within the page. Save retains a new
release revision, preserves earlier snapshots, and returns Approved to In review.
Pending release edits block review and packaging. Discard edits restores the saved
revision. Packaged, rejected and earlier revisions expose read-only contents.

## Verification

Verified in the served in-app browser at its current 2104 × 1272 viewport:

- The release Editor replaces Included roots; the heading no longer links editing
  to the main Editor.
- Modified the changed name model and previously unchanged ibm root in one draft;
  switching file tabs preserved both edits. Saving added ibm to the release diff.
- Approved the release, edited it again, and confirmed packaging was disabled
  while unsaved. Save produced revision 4 with In review status.
- Revision 2 retained its original contents and exposed no editing lock.
- Saved a separate workspace edit, then compared workspace w44 with release
  revision 4: each retained its own distinct edit; the release revision stayed 4.
- Packaged v1.3.1 showed read-only content without edit/save controls.
- JavaScript syntax checks passed for the three revision-14 scripts. No browser
  console errors were observed during these interactions.

## Boundary

The release editor uses an interactive text representation of the prototype's
root fixtures. It is not a replacement or integration of the real generated DSL
editor. The original generated editor and inspector captures in the main Editor
are unchanged. Locks, comments, drafts and saves are page-memory simulations and
reset on reload. No backend API, real models, permissions or release data changed.

index.html and iteration-14.html load this revision. Earlier iterations remain
available. The orange styling and compact file controls are retained.

[Release editor](revision-14-release-editor.jpg)
