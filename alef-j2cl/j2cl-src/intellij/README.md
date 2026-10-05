# Consumer-owned IntelliJ compatibility overrides

`com/intellij/openapi/command/undo/UndoManager.java` preserves MPS undo-transparent
commands: automatic quick fixes join the preceding edit instead of becoming
separate undo steps or clearing redo. Corrections before any user edit form part
of the baseline. The original SHA-256 is
`e0b01c97e6cfa432eda1818c3615cc3be8325a0c7c97dbfd96a434f158eecace`.
`tools/test_create_root.mjs` checks creation, edits, undo and redo with the real
MPS background checkers running. `tools/test_undo_transparent.mjs` verifies
baseline corrections, grouped undo/redo ordering, and preservation of redo
against the actual history adapter in the development browser bundle.

`com/intellij/ui/popup/AbstractPopup.java` is copied from the composed IntelliJ
compatibility sources. Its original SHA-256 is
`b382e2ae13d0b3ab9fbff9734215afbc371200493c3036891a567dc942010247`.
`tools/prepare.py` overlays this file into the local dependency view without
changing sibling repositories.

`pack(width, height)` resizes the actual lightweight Swing popup. The inherited
interface default only revalidated its content, leaving completion menus stuck
at their initial size after filtering changed the result count. Unrequested
dimensions retain their previous size. The existing Swing popup still owns
placement, browser bounds, focus, and cancellation.

Browser coverage is in `tools/test_completion_style.mjs`,
`tools/benchmark_completion.mjs`, and `tools/test_example.mjs`.

`com/intellij/openapi/ui/popup/JBPopupFactory.java` is copied from the same
compatibility sources (original SHA-256
`3442fd6ec5f852bc440c0713b32f487bed8b19c465a38959419d31bb9e97adba`).
Its action-list popups use `BrowserActionPopupStyle` for regular 14px text,
aligned 16px icons, padded rows, a flat frame, and blue selection. Preferred
widths use the actual font metrics so long intention labels remain visible.
The first enabled action receives focus on opening; Swing retains ownership
of keyboard navigation, cancellation, and action execution. These styles are
scoped to action popups and do not change the completion renderer.

The example theme supplies intention bulbs using vector geometry adapted from
MPS's Apache-2.0 `languages/languageDesign/intentions/icons/intentionBulb_newui.svg`.
Existing registered icons take precedence, and theme disposal unregisters only
the example's own icons. `tools/test_intention_style.mjs` checks actual node
intentions, visible bulb pixels, label fit, keyboard selection, cancel, execution,
and undo in Chromium, Firefox, and WebKit.
