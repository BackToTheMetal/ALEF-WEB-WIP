# Local WebAWT overrides

`org/webawt/internal/browser/Elemental2FontMeasurementProvider.java` is based on
the sibling WebAWT source with SHA-256
`31f722d9de9eb25b373ee2d771fc0cf0843976fc5ec1240222a716c14608e385`.
`tools/prepare.py` places this replacement in the local dependency view without
editing the sibling repository. Review it against upstream when updating WebAWT.

MPS editor rebuilds repeatedly measure the same glyphs and line metrics. The
provider caches up to 2,048 `(font, text)` measurements and clears them with a
zero-delay timer. Returned arrays are independent, including on cache hits.
Any custom document FontFace entries bypass and clear the cache, preserving
font loading and synchronous font replacement behavior. Misses keep the native
canvas measurement and invalid-font fallback behavior.

`tools/test_font_measurements.mjs` checks native metric agreement, cache hits,
defensive results, invalid fonts, Unicode, custom FontFace sets, expiration, and
the size limit in Chromium, Firefox, and WebKit. The application benchmark is
`tools/benchmark_name_edit.mjs`; set `ALEF_PROFILE=1` for a Chrome CPU profile and
keyboard listener timings.

`org/webawt/internal/browser/Elemental2CanvasPeer.java` is based on the composed
WebAWT source with SHA-256
`e1b76e729c273dd40d267b7e26bac2f817bf76da50e723d1b2506027c3858ac4`.
It changes canvas width/height attributes only when their device-pixel dimensions
change. Assigning even an unchanged canvas dimension erases all pixels, causing
flicker during repeated layout/getGraphics calls. Device-pixel scaling is still
updated. `tests/ScrollBarPaintContracts.java` verifies pixel retention across an
unchanged layout; `tools/test_scroll_interactions.mjs` exercises overflowing text
edits in the full editor.
