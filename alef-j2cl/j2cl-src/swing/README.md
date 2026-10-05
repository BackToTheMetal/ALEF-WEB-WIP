# Local Swing renderer overrides

These replacements are based on the composed Swing view in
`../mps-j2cl/.work/dependency-views/webswing/src/main/java/`.
`tools/prepare.py` excludes the matching original Java files and includes these
copies in the same Swing library. It does not edit the sibling checkout.

Original SHA-256 hashes at the time of this adaptation:

| Source | SHA-256 |
| --- | --- |
| `javax/swing/JList.java` | `d711b573bbd2a826797d463fb79c69e85ec25e364f211177f0f22d84341df178` |
| `javax/swing/BrowserRendererSnapshot.java` | `6458204dafb63d18928137567b532ed6271803bbb2474d3f004faf06ed952503` |
| `javax/swing/plaf/basic/BasicScrollBarUI.java` | `2d8f525c008126ba159b1bb4a0e825f8ab16d1bbdbfdc8c11a2e9e15c1036c58` |
| `javax/swing/IconSupport.java` | `c06ae634c953558c3b49cb6c105d75ad9106e339a79ebbd2bf752f08a90a5100` |
| `javax/swing/JScrollBar.java` | `6db9cae66e238731ce1bd26a67fac270bd25210409c988b963dbac68b9b5f351` |
| `javax/swing/JViewport.java` | `a8b8ad48ecacf8a9e97090ec29483afc3264cd12707e74845dd28d8e91d8ba9d` |
| `javax/swing/JScrollPane.java` | `9acbfeb6470467ec7d0989a46a07e1f76c68546de983c6c18520d25d09c2f597` |
| `javax/swing/BrowserPaintSurface.java` | `3f81b3c4dd45ee18450d73cfb8d77feadefab55433bf5b13e9f62549015de153` |

`JList` updates only rows whose selected state changed, preserving the remaining
DOM and renderer snapshots. Selection events still reach listeners synchronously
and update `aria-activedescendant`. Model, renderer, size, and style changes keep
the full refresh path.

`BrowserRendererSnapshot` paints custom Swing components using WebAWT's canvas
graphics, avoiding the software raster and per-pixel copy through `BufferedImage`.
Rows must be attached before painting so their canvas peers can find the parent.
The peer owns device-pixel scaling; dimensions and clipping remain in CSS pixels.

`BasicScrollBarUI` removes ordinary button margins from its controls. Their DOM
boxes must fit the UI-assigned bounds, including zero-sized buttonless controls.
Its arrow buttons use rounded vector chevrons with accessible direction labels.
`IconSupport` omits the text gap for icon-only buttons and aligns their canvas
vertically; text-bearing buttons use their configured gap. Scrollbar geometry,
status icons, focus stops, and viewport synchronization are checked at two sizes
in Chromium, Firefox, and WebKit by `tools/test_scrollbars.mjs`.

`JScrollBar` clears damaged pixels on its private canvas before painting. MPS's
transparent messages gutter draws current markers without filling the track, so
removed diagnostics otherwise leave old pixels behind. Both scrollbar and canvas
repaints follow the same path; the damage clip preserves untouched pixels, and
ordinary `paint(Graphics)` into another surface does not clear that surface.
`tests/ScrollBarPaintContracts.java` checks these pixel contracts in all three
browsers at device scale 2 (`//:scrollbar_paint_browser_dev`, then
`node tools/test_scrollbar_paint.mjs`). `node tools/test_gutter_errors.mjs` checks
a real invalid assignment followed by undo/redo and verifies the red stripe is
removed without resizing or reloading the editor.

`JScrollBar` captures the pointer on track presses so thumb drags continue outside
the track until release. Capture loss also releases the Swing drag state; child
arrow and inspection buttons keep their own input handling.

`JScrollPane` starts layout from its current scrollbar visibility and stops once
the geometry settles. It no longer temporarily removes optional scrollbars on
each keystroke, expanding the viewport and disturbing the scroll offset. When
content fits without optional bars, both can disappear together. The paint
contracts also cover this interaction between the two axes.
`tools/test_scroll_interactions.mjs` checks off-track dragging, release and capture
loss, viewport stability, and nonblank rendered frames while typing into overflow.

`BrowserPaintSurface` preserves the previous canvas image across a real resize
until its scheduled repaint. This avoids a blank browser frame when a growing
line enlarges the backing surface. A lazily allocated scratch canvas grows in
256-pixel chunks during typing and is released when the surface is unmounted.
The interaction regression samples frames while the horizontal bar appears,
while text keeps growing, and when shortening the text removes the bar again.

`JViewport` keeps its view's DOM peer at the origin even while Java bounds change
to negative scroll coordinates. Otherwise the transient DOM displacement shrinks
the overflow and clamps native wheel scrolling before reaching the bottom. A
scoped CSS rule prevents that intermediate displacement; native scroll events do
not write the offset back, preserving WebKit's smooth wheel movement. The marker
is removed when a view is replaced. `tools/test_service_scrolling.mjs` exercises
vertical and horizontal wheel scrolling, stable content extents, and Home/End
against the full Servicespraak example in Chromium, Firefox, and WebKit at scale 2.

`JViewport` also repaints the visible part of its view after native or
programmatic scrolling. MPS clips updates to the visible editor region; merely
moving the DOM canvas can expose stale offscreen pixels, such as an absent red
cross beside a failed test value. The debug regression starts with a short
viewport, runs an individual failing test, and checks the cross's red pixels
after wheel and scrollbar movement without clicking its result cells.

When updating the sibling Swing view, review these copies against the new inputs.
Browser regression checks are in `tests/ListSelectionContracts.java` and
`tools/benchmark_completion.mjs`.
