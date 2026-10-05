# ALEF design revision 12 — navigation and project locks

Design-only refinement, 27 September 2026. Retained iteration:
http://127.0.0.1:18821/iteration-12.html#model

## Navigation labels

The four primary destinations are **Editor**, **Compare**, **Packaging** and
**Deployment**. Packaging/Deployment headings, the packaging breadcrumb and
links back to Editor use the new names. Release objects and environment settings
retain their domain names. Existing hash routes are preserved for old links.

## Project tree lock status

Every root has a dedicated right-aligned status indicator:

| Indicator | Meaning |
| --- | --- |
| No icon | Unlocked; acquire a lease before editing |
| Green closed padlock | Locked by you in this tab |
| Orange closed padlock | Locked by another user |
| Violet closed padlock | Locked by you in another tab |
| Amber attention icon | Ownership unknown or your lease expired |

Hovering a row or its lock icon uses the browser's native tooltip to show the
full name/type, owner, editing-tab scope and what is needed before editing.
Accessible row labels also expose the root name and lock state. Unlocked roots
have no visible icon; the reserved column keeps the remaining locks aligned.
Closed/attention shapes distinguish locked and uncertain states. Root versions remain
out of the tree; unsaved edits have a separate small dot beside the lock.

The selected root follows the existing Lock root / Release lock controls. Lease
expiry preserves the draft and shows an attention state. Connection interruption
makes all 15 ownership indicators unknown until the saved/connected preview is
restored. Other-user locks on MoveTarget1790287585892Z and ServiceTest, plus the
other-tab lock on ubc, are explicit illustrative fixtures, not real sessions.
There are no live lock requests or new backend integrations.

## Verification

Verified in the served in-app preview at 2042 × 1272:

- All four renamed navigation controls reach their existing routes; Packaging
  and Deployment show matching page headings.
- All 15 roots expose descriptive tooltip/accessibility text, including another
  user's name and the other-tab case. Unlocked indicators are hidden.
- Lock root changes the active row to Locked by you; Release lock returns it
  to Unlocked. Expiry and interrupted-connection previews show attention states.
- The editor toolbar remains 48 px tall. Document width remains 2042 px with
  no new horizontal page overflow.
- JavaScript syntax checks passed for design-12.js, project-tree-12.js and
  environment-design-12.js.

Tooltip content was verified through rendered title/accessibility attributes;
native tooltip timing/placement is browser-controlled. These are prototype checks,
not production lease or authorization tests.

iteration-12.html retains this revision; index.html follows the latest revision.
Only prototype/design files changed. The generated editor and inspector
captures, compact header, orange palette and workflow semantics are retained.

[Editor with project locks](revision-12-editor-locks.jpg)
