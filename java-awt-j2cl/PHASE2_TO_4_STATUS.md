# Phases 2 through 4 execution status

## Outcome

WebAWT now has an end-to-end stock-J2CL vertical slice from public `java.awt` APIs through model peers to Elemental2 DOM and Canvas APIs. This is the approved baseline for the Phase 5 validation gate, not a claim that every OpenJDK AWT member is implemented. The compatibility manifest distinguishes implemented, partial, not-started, unavailable, and excluded behavior.

## Phase 2: DOM component system

Implemented:

- Component and container state, property changes, hierarchy, validation, sizing, painting hooks, and inherited foreground, background, font, locale, cursor, and orientation.
- Flow, border, grid, card, and null layouts.
- Semantic DOM peers for panels, buttons, labels, text fields, text areas, checkboxes, choices, lists, canvases, frames, windows, and dialogs.
- Action, item, text, component, container, focus, keyboard, pointer, motion, and wheel event models with listener and adapter dispatch.
- Browser translation for focus, keyboard, mouse, drag-motion, and wheel events. Consumed input events call `preventDefault()`.
- Explicit listener disposal when a DOM peer is detached.
- Basic accessibility roles, states, names, and component hierarchy exposure.

Deferred or limited:

- Menus, scroll panes, scrollbars, focus traversal policies, IME, input methods, desktop toolkit services, and native desktop peers are not implemented.
- Browser keyboard names and pointer buttons are normalized deterministically; they do not use desktop Toolkit localization or hardware queries.
- DOM focus opposites and AWT `AppContext` filtering are unavailable.
- Preferred control sizes are deterministic estimates rather than live browser layout measurements.

## Phase 3: graphics, images, and fonts

Implemented:

- Canvas-backed `Graphics2D` paths, shapes, transforms, clipping, basic strokes, colors, alpha compositing, gradients, text commands, copying, and high-DPI canvas backing stores.
- Correct `CLEAR` erasure and no-op `DST` compositing behavior on Canvas.
- Data buffers, sample models, writable rasters, color models, image producers, filters, and memory image sources.
- All 13 standard `BufferedImage` layouts, RGB access, shared subimages, alpha coercion, scaling, pixel production, and raster-backed `createGraphics()`.
- Logical fonts, derivation, decoding, Unicode display checks, deterministic font metrics, and `FontRenderContext` state.
- DOM and Canvas CSS font serialization.

Deferred or limited:

- Canvas image drawing/loading and `ImageBitmap` lifecycle APIs are not part of the current `Graphics` surface.
- Raster text drawing is a no-op; browser Canvas text uses CSS fonts but metrics remain deterministic estimates.
- Raster antialiasing, dithering, fractional metrics, and stroke-control hints are stored but do not change rasterization.
- Browser gradient cycle methods are reduced to native Canvas gradients; pure Java paint contexts retain repeat and reflect behavior.
- Dynamic `System.getProperty` font/color lookup is unavailable because stock J2CL only permits literal property keys.
- Font file loading, glyph vectors, shaping, `TextLayout`, and asynchronous `FontFace` loading are deferred.

## Phase 4: constrained browser integrations

Implemented:

- Async text clipboard extensions backed by the browser Clipboard API and Elemental2 Promise.
- Browser navigation through an explicit new-window extension with popup-blocker feedback.
- Managed in-page windows, frames, and dialogs with attach, pack, visibility, ownership, and disposal behavior.

Deferred or limited:

- Clipboard calls require a secure context, browser permission, and usually user activation.
- Dialog modality is modeled without a blocking nested event loop.
- Drag-and-drop remains deferred until its `DataTransfer` event-lifetime, async conversion, and security contract is approved.
- File dialogs and printing remain excluded by scope.

## Consumer proof

`//samples/showcase:showcase_application` is an optimized Closure application that installs WebAWT, lays out semantic controls, dispatches an action event, and paints to an AWT canvas. It complements the small point sample and the browser/J2CL test suites.
