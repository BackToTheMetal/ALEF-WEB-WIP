# ALEF design revision 09 — orange visual refresh

**Design only.** The active preview is http://127.0.0.1:18821/#model.
Application, backend and generated editor code are unchanged.

The visual shell now uses orange rather than green as its primary accent:

- Vivid orange logo, selected-tab lines and promotion steps.
- Deep orange primary buttons, with readable white labels and subtle shadows.
- Peach selection fills in Model and Changes, with a brighter selection edge.
- Warm white and pale peach page backgrounds, table headers and supporting panels.
- Orange version badges, focus indicators, menu hover states and active filters.
- Stronger page headings and a little more contrast between white content and
  warm supporting surfaces, without adding header rows or extra decoration.

Status meaning remains separate from branding: approved/packaged states retain
green, review states use soft violet, warnings retain amber, and rejected states
remain red. Additions/deletions in comparisons and the captured generated editor's
syntax colors are unchanged. SIMULATED deployment labels stay visible.

## Scope and implementation

revision-09.css is a presentation-only override, loaded last by iteration-09.html.
Revision 10 retains it and adds aligned tree icons in the active index.html.
The revision 09 entry reuses the existing design-08.js, editor-header-08.js and
environment-design-07.js. Workflow logic, layout, the 48 px file toolbar, menus,
generated inspector, release semantics and promotion path are retained.

Earlier iteration HTML files preserve their original palettes. The historical
base token named --green maps to --accent in this theme; semantic success has its
own --success token so brand and workflow colors can be adjusted independently.

| Token | Value |
| --- | --- |
| Primary action/text accent | #bb430d |
| Bright decorative accent | #f97316 |
| Selection surface | #fff0e3 |
| Page surface | #fff8f2 |
| Main text | #302821 |
| Secondary text | #75685f |
| Border | #e9dfd7 |

## Verification

Visually inspected the actually served Model, Changes, Releases list, release
review, Environments and Settings screens in the in-app browser at 878 × 791.
Navigation worked across these views, existing diff colors remained distinct,
and the generated editor/inspector captures retained their appearance. Changes
and Settings had document scroll widths of 878 px; wide tables retain their own
existing scroll containers. Model's toolbar remains 48 px tall.

Calculated contrast ratios for the chosen token pairs exceed 4.5:1 for white
primary-button labels, selected-navigation text, main text and secondary text.
This is a focused visual/token check, not a full accessibility or cross-browser
audit. Existing responsive layout rules are retained; no new phone layout is
introduced. Reduced-motion preferences disable the new short hover transitions.

Screenshots:

- [Model](revision-09-model.jpg)
- [Changes](revision-09-changes.jpg)
- [Releases](revision-09-releases.jpg)
- [Review](revision-09-review.jpg)
- [Environments](revision-09-environments.jpg)
- [Settings](revision-09-settings.jpg)
