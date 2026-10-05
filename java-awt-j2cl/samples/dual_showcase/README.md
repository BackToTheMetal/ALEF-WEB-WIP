# WebAWT dual-runtime showcase

Current status: the owner accepted this showcase on 2026-09-08. Graphics2D coverage now includes 37 shared scenarios; use Rendering lab > Graphics state and the scenario dropdown at the bottom. See [the finalization record](../../FINALIZATION.md) for current gates and [the retained text profile](../../docs/text-rendering-profile.md) for limits. Dated validation figures below are historical.

The gallery now has twelve pages. The [input and capability plan](../../docs/testing/showcase-input-plan.md) records the implementation and validation contract.

**Rendering lab > Graphics state** shows the same shared scenarios on direct Canvas and BufferedImage. Select clip-before-translation, transformed intersections, background clearing, child isolation, scaled/clipped and overlapping copies, six image overloads, or affine/filtered images; toggle the translation and cyan/transparent background. The page reports clip bounds and state checks alongside the pixels. Transparent BufferedImage pixels reveal the white page; direct browser Canvas preserves zero alpha. Native desktop Canvas transparency depends on the platform. Copying supports translation and positive axis-aligned scaling, including high-density browser canvases. Rotation, reflection and shear are rejected: WebAWT uses `UnsupportedOperationException` because stock J2CL has no JDK `InternalError`. Native window exposure handling for unavailable source pixels is platform-dependent; source-boundary assertions run separately on BufferedImage and browser Canvas.

The **Image overloads** panel reads left to right: natural size, scaled size, natural size with cyan background; then scaled with background, reversed destination, and reversed source with background. **Affine and filtered images** shows rotated/scaled placement, a null operation, and a white-producing rescale operation. Interior pixel assertions avoid interpolation-edge differences.

The stroke scenarios cover nonuniform scaling and clipping, continuous dash phase, cap/join/miter choices, and custom strokes. The two primitive panels read left to right: line, rectangle, rounded rectangle, oval; then arc, polyline, polygon arrays, and the Polygon overload. The custom stroke fills each shape's expanded bounds and checks that each complete primitive reaches it exactly once. Desktop/J2CL and native-resolution browser pixels are compared for both backends; 2x density checks stroke geometry directly on Canvas. Enlarged BufferedImage edge colors are subject to interpolation. Zero-width hairlines, degenerate paths and full antialiasing parity remain follow-up work.

The six paint/composite scenarios cover transformed and clipped textures, custom-paint lifecycle and recovery, reflected/repeated gradients, linear-RGB interpolation, transformed radial gradients, all twelve AlphaComposite rules, and image/copy SRC and CLEAR. The composite grid uses triangles in numeric rule order, with checks outside each triangle as well as inside it. Custom Composite implementations are explicitly rejected by both WebAWT backends without changing state; the JDK accepts them. The custom-paint panel reports context counts and text-paint handling. WebAWT disposes a failing PaintContext immediately; JDK 17 retains it at this point. The blue marker verifies recovery after rejection/failure.

Gradient vectors allow 3 channel levels, or 6 for the radial samples rescaled at 2x device density; the global-alpha vector allows 1 level. JDK native-window samples separately permit 4 levels on the reflected ramp and 8 at the radial center because of pixel-center sampling and native channel rounding. Other vectors remain exact interior comparisons. Tests needing retained alpha (the composite grid and image/copy composite panel) run on BufferedImage and browser Canvas; native desktop window surfaces cannot be assumed to retain alpha. Paint-context tiles use logical surface resolution; interpolation, antialiasing, very large fills and curved software-fill performance remain follow-up work.

The **Font state and metrics** panel compares four rows (iiii, WWWW, WebAWT 123 and Cafe with a combining acute accent). Green bars use 24px font metrics; blue bars use explicitly requested 12px metrics. **Font render context** checks scale/shear, translation exclusion, explicit antialiasing/fractional hints, defensive copies and snapshot isolation. Both Canvas and BufferedImage use the browser measurement provider installed by WebAwt.install; non-browser model tests retain the previous estimates.

Use **Load reference font** to load the bundled DejaVu Sans face and refresh the bars, or **Fail font load** to inspect rejection and retry. WebAwt.loadFont(family, url) loads a normal face and resolves after registering it; separate weight/style descriptors remain follow-up work. It deduplicates the same family/resolved URL and evicts failed requests so they can be retried. Font faces are retained for the document lifetime. The showcase ignores completion after disposal; loading does not cancel independent image decoding. Await loading before layout, then revalidate/repaint application components that previously used fallback metrics. The desktop launcher loads the same font from its classpath resource.

The reference font is unmodified, with its [license and source notice](fonts/LICENSE.txt). Its SHA-256 is b4c632e3cdf9acc7f28758fb5a323c8524d7fc6660d46904d9b6cbe2809c419c. Browser advances match a separate native canvas measurement within 0.05px. The four JDK width samples permit 2px to account for engine hinting/rounding; this is a sample-specific bound, not a guarantee for arbitrary text. Canvas font bounding-box ascent/descent are used where available, with Hg ink bounds as a fallback and zero browser leading. Max-glyph bounds, font-transformed bounds, attributed-run shaping, fallback run metrics and rasterization hint fidelity remain limited. FRC state is expressed in logical surface coordinates; retaining a hint does not make the browser implement its rasterization policy. Plain-string painting and graphics/font transforms are covered by the text checkpoint below; attributed Canvas runs, BufferedImage attributed painting and true glyph outlines remain follow-up work.

The four plain-text panels demonstrate both `drawString` overloads, `drawChars` and unsigned Latin-1 `drawBytes`, including nonzero array ranges. **Transformed text** rotates and scales actual glyphs, applies shear and a transformed font, and checks child isolation. **Clipped translucent text** keeps the clip fixed while drawing rotated text at 50% alpha. **Text clearing and limits** erases glyph coverage and draws again after a composite capability probe. The first string spans multiple raster tiles. Use the reference-font button before comparing desktop and browser results.

BufferedImage text painting uses a browser font rasterizer installed by `WebAwt.install`. It renders whole strings into bounded 128x128 coverage tiles, samples the current Paint, composites, then applies glyph coverage in premultiplied color space. This preserves untouched pixels and antialiased edges for SRC and CLEAR; paint contexts are disposed even when rasterization fails. Text is stored at image pixel resolution. A 2x display enlarges those pixels, whereas direct Canvas rasterizes at display density. Model-only execution without a rasterizer fails explicitly for nonempty BufferedImage text; the legacy PhantomJS runner checks the portable coverage/composite contract with controlled masks, while actual glyph checks run on the JDK and modern browsers.

Direct Canvas plain text retains native colors/gradients and supports SRC_OVER, CLEAR, DST and the other destination-preserving AlphaComposite rules. SRC, SRC_IN, SRC_OUT, DST_IN and DST_ATOP text fail explicitly because native Canvas can otherwise clear pixels outside the glyphs. Arbitrary Paint for direct Canvas text remains unsupported; BufferedImage text samples PaintContext. Fractional clip-edge antialiasing, text rasterization hints, font attributes beyond CSS family/style/size and an affine font transform, complex attributed layout, and true glyph outlines remain limited.

Text acceptance compares every pixel against an independently drawn native reference at 1x and 2x density, for both backends and both translation settings, using the bundled font. The allowed total RGBA difference is at most 8 levels per channel per reference-ink pixel (the whole surface contributes to the difference). Shared JDK tests require nonempty ink in each individual string region and no escaped clip pixels; this does not assert identical JDK/browser glyph outlines. The existing width tests retain their separate 0.05px native / 2px JDK limits.

The [Graphics2D method matrix](../../compatibility/graphics2d-showcase-coverage.md) inventories all 84 public signatures and inherited overloads. All 84 signatures are reviewed: 83 have demonstrations and assertions on both backends; Canvas XOR is explicitly unavailable and BufferedImage XOR is tested. There are 37 shared graphics scenarios. Every subsequent Graphics2D implementation change must update its sample scenario and assertions together. Existing gallery calls alone do not count as overload coverage.

The [dialog and rendering regression notes](../../docs/testing/dialog-and-rendering-validation.md) describe the decorated browser dialog and the fix for the Rendering lab stall. Dialogs now show their title and a close control, reserve decoration space in AWT layouts, and use the requested outer bounds without the browser's default dialog margin or padding. Native font and widget styling can still differ between platforms.

Input/capability validation (2026-09-07): 46 JVM/J2CL targets, 3 desktop targets and 6 modern-browser suites passed, with targeted final regressions in all three engines. The full 303-target build and formatting/dependency/bundle/API gates passed. The plan links the reviewed screenshots and records the remaining limitations.

- **Keyboard & input** records up to 200 key/focus/text/IME/pointer events, with key names/codes, UTF-16 character values, modifiers, locations, targets and consumption. Try the sample selection/caret controls, password field, disabled/hidden targets, optional F6/Shift+F6 traversal, and focusable pointer canvas. The application binding is Ctrl+Shift+K (Meta+Shift+K on macOS); the Actions menu exposes Ctrl/Meta+Shift+M. The consume option blocks X and Enter. No binding is installed after gallery disposal.
- **Rendering lab** compares radial/texture paints, clipping, Area subtraction and hit testing, alpha compositing, TextLayout highlights/carets, glyph vectors, attributed line wrapping, Unicode text, multi-resolution images and BufferStrategy presentation. PNG decoding uses shared encoded bytes and ImageObserver callbacks; the invalid-image action demonstrates failure and retry without a network dependency.
- **Lifecycle & accessibility** demonstrates dynamic component addition/removal, resizing/revalidation, a nested reversible focus cycle, accessible names/roles/states/actions, and an owned modeless dialog's lifecycle.

Browser keyboard tests use native Playwright input for ordinary keys, shortcuts, selection, focus and pointer interactions. Synthetic DOM events cover explicit numpad/AltGraph metadata and IME composition boundaries; opening an operating-system IME candidate window still requires manual comparison. Key codes for letters follow the browser's logical key; ambiguous ASCII punctuation falls back to its standard `code` position. Non-US punctuation layouts and browser-reserved shortcuts need manual verification. Supplementary characters remain visible through text/IME events; a single AWT `keyChar` cannot contain an entire supplementary Unicode character.

WebAWT's Unicode glyph vectors now paint native browser glyphs. Per-glyph positions and transforms are honored; metrics and true outline geometry retain the explicit limits in docs/text-rendering-profile.md, and per-glyph painting cannot provide full contextual shaping. The multilingual `drawString` sample alongside them uses native browser string shaping. TexturePaint and custom paints now render filled and stroked shapes through paint contexts on both backends. Canvas native text/glyph drawing still requires a color or native gradient; other paints throw UnsupportedOperationException. BufferedImage plain and attributed text now paint through the installed browser rasterizer. Native text gradient cycles and color-space fidelity remain open. Browser buffer presentation does not promise hardware page flipping. Declaration completeness is deliberately not presented as behavioral parity in the Runtime & limits page.

This gallery compiles the same `DualShowcase.java` source against two AWT implementations. The desktop target uses the JDK's `java.awt` classes and has no WebAWT dependency. The browser target uses WebAWT, J2CL and Elemental2.

## Desktop

Run the desktop gallery on a machine with a graphical display:

```shell
bazelisk run //samples/dual_showcase:desktop
```

In IntelliJ, run `org.webawt.samples.dualshowcase.DesktopLauncher` with the repository JDK. The shared source and launcher need only the JDK on the module path or classpath. Include samples/dual_showcase as a resource root so /fonts/DejaVuSans.ttf is available; Bazel packages that resource automatically.

## Browser

Build the optimized J2CL application:

```shell
bazelisk build //samples/dual_showcase:browser_application
```

The repository limits Bazel to four concurrent actions because a cold J2CL toolchain build otherwise starts enough native Protobuf compiler jobs to exhaust WSL memory on common development machines.

Serve the repository root so the HTML page can load the Bazel output:

```shell
python3 -m http.server 8000
```

Open `http://localhost:8000/samples/dual_showcase/`.

## Comparison workflow

Open both variants, choose the same page from the top-right selector and perform the same action. Controls use the host platform's native widgets, so their styling and precise font metrics are expected to differ. Layout, state changes, events, geometry, image results and custom canvas content should remain functionally equivalent.

File dialogs and printing are intentionally outside the WebAWT scope. The Runtime & limits page documents browser capabilities that cannot exactly reproduce unrestricted desktop behavior.

The DnD & clipboard page's **Write system clipboard** and **Read system clipboard** buttons transfer text to and from the operating-system clipboard. The shared gallery uses a small `ClipboardAccess` boundary: the desktop implementation uses the JDK toolkit, and `BrowserClipboardAccess` calls `WebAwt.writeClipboardText` / `readClipboardText` directly from the button action. The browser waits for completion, reports failures alongside the controls, and preserves the text after a failed read. HTTPS or localhost and browser clipboard permission are required; a browser may present a permission or Paste prompt. Keyboard copy/paste in the text field remains available if a button is blocked. These requirements follow the [Clipboard API contract](https://www.w3.org/TR/clipboard-apis/).

WebAWT's synchronous `Toolkit.getSystemClipboard()` remains an application-local clipboard; browser applications needing the system clipboard must use the asynchronous WebAWT extension, as this gallery now does. Playwright covers pending operations, external text, empty text, denied/unavailable access, retries, and disposal in Chromium, Firefox, and WebKit. It also verifies real system writes through native keyboard paste in all three engines and real system reads with an explicit permission grant in Chromium. Firefox/WebKit's native read-permission prompts require manual acceptance and are not automated.

Clipboard validation (2026-09-06): all 46 JVM/J2CL targets, 3 desktop targets, and 6 modern browser suites passed; unchanged tests used Bazel's cache. The complete 303-target build, formatting, dependency policy, and bundle budgets passed. The optimized dual showcase is 242,894 raw / 79,971 gzip bytes. The [Chromium clipboard screenshot](../../docs/screenshots/clipboard-chromium.png) shows text pasted through the native clipboard after the Write action.

The controls comparison also checks menu/header separation, right-aligned Help, vertically centered labels, visible checkbox captions, compact indicators, and mutually exclusive radio inputs. The gallery's Playwright tests run in Chromium, Firefox, and WebKit and save `controls.png` in each engine's Bazel test outputs. Native widget colors and exact font metrics remain browser dependent.

The menus comparison opens the popup through its actual button and checks action delivery, persistent options, nested items, keyboard operation, outside-click dismissal, viewport edges, and disposal. Each engine saves `menus.png`. The [popup and tile fix plan](../../docs/testing/popup-and-tile-plan.md) records the defects and validation results.

Tiles are 160 by 170 logical AWT units on both runtimes. Compare physical sizes at matching desktop display scale and browser zoom: a browser device scale of 1.25 renders a 160-unit tile as 200 screen pixels. If the desktop comparison uses 160 screen pixels, reducing the browser's current zoom by a factor of 0.8 matches that scale. Do not resize Java tiles to compensate for a display-scale difference. Caption centering and tile bounds are checked at browser device scales 1 and 1.25, and the JDK test checks the same gallery bounds.
