# ALEF design revision 13 — collapsed project panel

Design-only refinement, 27 September 2026. Active preview:
http://127.0.0.1:18821/#model

On desktop, collapsing Project leaves a 44 px rail with an orange panel icon
and vertical Project label. The reopen control has its own space beside the
file tabs. Expanding restores the project tree and moves focus to its collapse
button. The collapsed state survives editor updates within the page.

The controls expose Show/Hide project panel labels and expanded state. Narrow
screens retain the drawer, with a full-height opener aligned to the file tabs.
Unlocked roots have no visible lock icon; existing locks remain aligned.

Verified in the served browser preview at 2042 × 1272:

- Collapse and reopen work, with focus on the newly visible control.
- Acquiring the illustrative editor lock preserves the collapsed rail.
- The collapsed view has one project opener and neatly aligned file tabs.
- Unlocked roots hide their icons; locked roots retain their indicators.
- JavaScript syntax validation passed for design-13.js.

Mobile drawer styling was not separately browser-tested in this revision.
Only the isolated prototype changed; no application or backend changes.

[Collapsed panel](revision-13-collapsed.jpg) · [Expanded panel](revision-13-expanded.jpg)

index.html and iteration-13.html load this revision. The orange palette, compact
toolbar, generated editor/inspector captures and workflow semantics are retained.
