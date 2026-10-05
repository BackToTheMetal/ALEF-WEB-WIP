# Showcase input and capability coverage

## Objective

Make keyboard mapping, character input, focus and editing behavior directly comparable in the shared JDK/WebAWT gallery. Expose representative missing graphics, text, image, pointer, lifecycle and accessibility capabilities without implying desktop parity for browser-limited behavior.

## Implementation sequence

1. Repair the browser boundary: named/function/modifier/numpad and punctuation key codes; composition-aware character delivery; consumed-event handling; TextField Enter actions; bidirectional text selection/caret synchronization; menu accelerator dispatch. Preserve native editing and remove listeners on disposal.
2. Add a shared Keyboard & input page with a bounded persistent event trace (pressed/typed/released, code/name, character, modifiers, location, target, consumed), editable Unicode text, password input, caret/selection controls, IME commit reporting, traversal controls, disabled/hidden targets, an application binding and menu shortcut, and a focusable pointer canvas.
3. Expand capability coverage: text layout/glyphs/wrapping/highlights; clipping, radial/texture paints, compositing and Area/hit tests; asynchronous encoded-image loading with success/failure reporting, multi-resolution selection and buffered rendering; dynamic layout changes and lifecycle/accessibility inspection. Keep file dialogs/printing excluded and document remaining browser limitations.
4. Add behavioral regression tests at the browser boundary and shared-gallery level, including real browser keyboard sequences and model/DOM selection agreement, consumption, IME suppression, shortcuts, focus changes and disposal. Keep desktop coverage for the same shared UI and keyboard contract. Run formatting, targeted tests, all JVM/J2CL and desktop tests, all three browser engines, full build, dependency/bundle/API gates, and inspect screenshots.

## Acceptance criteria

- Letter, shifted punctuation, navigation, function, modifier and numpad keys expose meaningful AWT codes; pressed/released keys remain distinct from character input.
- Consumed keys do not edit the DOM or trigger actions; composition does not invoke shortcuts or premature TextField actions; ordinary Enter actions work repeatedly and blur does not submit.
- Programmatic and native selections agree with AWT getters, including backward-selection caret position, and survive peer reattachment.
- Menu shortcuts activate the correct enabled action for the owning frame; custom traversal and application binding behavior are observable and cleaned up on disposal.
- The gallery exposes persistent input/focus/IME diagnostics and representative demonstrations for every coverage area above, with explicit limits where appropriate.
- Tests exercise actual resulting events, state and pixels rather than merely visiting pages. Existing clipboard, menu, control and rendering tests remain intact.

## Validation record

The first Chromium run passed the keyboard/input checks and exposed missing pending-image observer registration and a missing layout validation in the lifecycle dialog. Both were repaired; the rendering/lifecycle/pointer rerun passed. Screenshot inspection then exposed rectangular glyph-outline painting; WebAWT Unicode glyph vectors now use native browser text painting, with custom vectors retaining their outline path. Tests reject solid metric rectangles and verify positioned/transformed supplementary glyph commands.

Validation covers 46 JVM/J2CL targets, 3 desktop targets (including a real JDK Robot key sequence), and all 6 modern-browser suites in Chromium, Firefox and WebKit. The 303-target build passes after declaring direct dependencies for the new tests. Formatting, production dependency policy, 5 quality-tool tests, 14 compatibility-tool tests, optimized bundle budgets and API comparison passed; the comparison reports zero false implementation claims and zero invariant violations. Final keyboard/rendering regressions additionally check the repeating texture and initial input-canvas paint. Unchanged tests used Bazel's cache where available.

Reviewed Chromium captures: [keyboard/input](../screenshots/keyboard-input-chromium.png), [paint/geometry](../screenshots/paint-geometry-chromium.png), [text layout](../screenshots/text-layout-chromium.png), [images](../screenshots/rendering-images-chromium.png), and [lifecycle/accessibility](../screenshots/lifecycle-chromium.png).

Browser-managed IME candidate UI, non-US punctuation layouts, exact glyph outlines/contextual shaping and hardware page flipping remain explicit manual checks or browser-profile limitations; they are not claimed as fully reproduced desktop behavior. The original geometry panel retains its cached rectangular BufferedImage texture sample. The Graphics state panels now compare TexturePaint directly on Canvas and BufferedImage through paint contexts; see FINALIZATION.md for the paint/composite checkpoint and remaining text-paint limits. Software curved fills repeatedly flatten geometry during containment tests and need separate performance work; the showcase avoids doing that work during repaint.

The new three-page capability slice and shared input fixes increase the optimized dual gallery to 296,883 raw / 98,578 gzip bytes and the smaller browser showcase to 175,455 raw / 57,173 gzip bytes. Feature budgets are rebased to 304/100 KiB and 176/58 KiB respectively (roughly 3-5% headroom); the point example's 8/3.5 KiB budget is unchanged. This is an explicit feature-growth baseline, not a disabled size check.
