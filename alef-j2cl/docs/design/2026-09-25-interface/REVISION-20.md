# ALEF design revision 20 — linked package edits in Editor

Project rows have a 14 px outline package icon immediately after the lock column.
Orange indicates a linked change (or a selected update pending save). Muted
icons indicate inherited contents or a finalized saved version. Hover details
name the packages and explain which ones will follow edits. Unlocked icons
remain hidden, preserving both columns' alignment.

## Try the unchanged-root choice

1. Use **View as → author Local**, open **Editor**, and select **ibm**.
2. Choose **Lock to edit**, then **Preview edit** in the study toolbar.
3. The popup asks where the change should go. Select an open package to update
   it, or leave all packages unselected to keep the change in the workspace.
4. Choose **Continue editing**, optionally add a tag, then **Save**.

The toolbar shows the save destination. **Choose packages…** in that menu can
adjust optional destinations before saving. Mandatory existing links are listed
and remain automatic. Cancel leaves the root unchanged. Other roots can be
selected and have independent local drafts, locks and tags; Save affects only
the active root. Non-name roots use a simple textual fixture view, while the
original generated-editor capture remains for the name root.

## Membership and save behavior

- A changed root included at its current saved workspace version follows future
  Editor saves while that package is open. The same version can be linked to
  several open packages; they update together. The toolbar lists the destinations.
- Roots merely inherited from the baseline do not follow saves automatically.
  Selecting a package in the popup makes the saved edit part of its changes and
  establishes the link for later saves. Workspace-only edits remain available
  for New package or Add changes.
- Older retained versions selected explicitly are not overwritten by newer
  workspace saves. They can be replaced explicitly using the destination popup
  or Add changes. Packages belonging to another fixture author cannot be changed
  through the current viewer's Editor save.
- Each content save creates a new package revision, resets approvals and retains
  the previous revision, reviewer decisions and anchored comments. Other roots
  and the original package baseline are preserved. Removing a selected change
  removes its automatic link.
- Tags on versions successfully included by a linked save are consumed and the
  Editor chips reflect that. Workspace-only tags remain available for selection.
  Saving only tags does not create a package revision or reset approvals.
- Finalized packages remain immutable. If a package is finalized during a local
  draft, Save allocates a new root version rather than changing the finalized
  root/version. Stale optional destinations require a fresh choice before saving.

This extends revision 19's independent reviewer and author-finalization workflow.
It supersedes earlier descriptions of package contents never following Editor
saves. The prototype still changes only in-memory fixtures, with no actual ALEF
editor, backend, authentication or deployment integration. Reload resets it.

## Verification

`node verify-20.cjs` checks icon order/state, root selection, cancellation,
workspace-only saves, explicit and automatic links, parallel packages, retained
versions, isolated root drafts, tag behavior, lock/identity guards, stale choices,
approval invalidation, finalization during unsaved editing and comment history.
It executes revision-20 scripts in a Node VM with a minimal document fixture.
Browser layout is not verified because of the existing browser security block.
Index and iteration-20.html load revision 20; prior revisions remain preserved.
