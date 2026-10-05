# ALEF design revision 08

**Design only.** The active prototype is http://127.0.0.1:18821/#model.
Only the isolated design files changed. The application and backend are unchanged;
all edits, leases and release actions in this prototype remain illustrative.

## Lean file header

The file tabs are followed by one compact toolbar instead of four stacked rows.
The file name remains in its tab rather than repeating underneath it.

- Root version and state stay visible at the left.
- Saved/Unsaved status, lock ownership and Save remain directly accessible.
- The Release dropdown contains inclusion, Choose changes, View package and
  Compare saved draft. Its header shows the release state and revision.
- More file actions contains Version history, Undo, Redo and Root actions.
- Review feedback stays inside the editor, collapsed initially. Expanding it
  reveals the message and Open discussion action.

Release selection is still applied by Save. An unsaved inclusion change displays
Inclusion pending. The menu remains open after changing the checkbox, with focus
returned to that checkbox. Actions that require the root lock remain disabled
without it. These are the existing prototype rules, with a more compact layout.

Menus overlay the canvas, close when clicking outside or opening another menu,
and close on Escape with focus returned to their trigger. Keyboard users can
expand the native disclosure and tab to its controls.

The generated editor and generated-inspector-only bottom panel remain unchanged.
Project-tree typography, hidden tree/tab versions, release semantics and ordered
environment promotion from revisions 06 and 07 remain in effect.

## Space and responsive behavior

At the user's 878 × 791 viewport, the rows between file tabs and editor formerly
occupied approximately 198 px. The replacement toolbar occupies 48 px, returning
approximately 150 px to the editor. The document width remains 878 px with no
horizontal page overflow. The editor itself retains its existing internal scroll.

At widths up to 600 px, the controls can wrap and the release version moves into
its dropdown. Version state, save status, lock and Save stay available. The project
panel retains its existing responsive behavior. This revision was visually checked
at 878 px and the hidden test tab's 1280 px width; phone layout was not re-tested.

## Verification performed

Checked the served prototype in the in-app browser:

| Check | Observed result |
| --- | --- |
| Initial state without lock | Saved visible; Save and inclusion changes disabled |
| Lock → Preview edit → Save | Lock held, Unsaved then Saved; root remains v13 and review revision advances |
| Release inclusion checkbox | Inclusion pending appears; Save becomes available; dropdown stays open |
| Choose changes | Existing root/resource inclusion dialog opens |
| More → Version history | Existing version lifecycle dialog opens |
| Switch menus and Escape | Other menu closes; Escape closes current menu and restores trigger focus |
| Review feedback | Initially collapsed; expanding reveals message and discussion action |
| Current preview | Revision 08 loaded; 48 px toolbar and no document horizontal overflow at 878 × 791 |
| JavaScript syntax | node --check passed for editor-header-08.js and design-08.js |

These are prototype checks, not application persistence, backend or cross-browser
acceptance tests.

## Artifacts

- [Compact file header](revision-08-editor.jpg)
- [Release options dropdown](revision-08-release-menu.jpg)

iteration-08.html loads design-08.js, editor-header-08.js and
revision-08.css alongside the retained environment module, styles and assets.
index.html now shows revision 09, which adds the orange theme. iteration-07.html
preserves the preceding layout. No application build or server restart is required.
