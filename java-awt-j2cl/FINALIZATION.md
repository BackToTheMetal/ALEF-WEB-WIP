# WebAWT finalization plan

Date: 2026-09-07. Scope: the existing stock-J2CL browser profile. This plan incorporates the source audit, agreed showcase acceptance requirements, and implementation progress recorded below.

The authorized steps 1–4 are complete for the retained browser profile, with implementation, consumer evidence and passing validation recorded below. On 2026-09-08 the owner accepted the existing showcase, deferred additional step-5 acceptance and SonarQube, and authorized step 6 release engineering. This is a bounded browser compatibility profile, not full Java SE or native font-engine parity.

## Agreed consuming application and delivery rule

The existing dual-runtime showcase in `samples/dual_showcase` is the consuming application for finalization. Its desktop variant runs against the JDK and its browser variant runs against WebAWT. Consumer selection is complete; acceptance work extends this application.

Every implemented Graphics2D method must also be exercised and demonstrated in the showcase sample application. This includes existing implementations, every overload, inherited Graphics methods, state/query methods, and lifecycle methods. Each Graphics2D implementation change must include the corresponding showcase scenario and regression assertions in the same change. A library method is not complete until its behavior can be inspected in the showcase and verified through its tests.

## Authorized continuation through step 4

The requested continuation covers: (1) finish the retained text-rendering profile, (2) close Graphics2D showcase coverage, (3) repair rendering performance and lifecycle, and (4) close the extended API review and consumer compilation probes. All four items have passed their implementation, consumer-evidence and applicable validation gates. The subsequent owner decision for steps 5 and 6 is recorded below.

- [x] Step 1: attributed drawing, shared layout/wrapping, glyph positioning, measured caret/highlight behavior and explicit shaping/font limits.
- [x] Step 2: every retained Graphics2D signature demonstrated and asserted on both backends; unavailable cases explicitly reviewed.
- [x] Step 3: prepared software geometry, image upload reuse with invalidation, measured performance budgets and runtime/resource teardown.
- [x] Step 4: extension/prerequisite dispositions individually reviewed, retained gaps repaired, generated consumer probes and package-ownership enforcement passing.

## Step 6 execution (2026-09-08)

Owner decision: use the existing showcase acceptance; defer step 5 and SonarQube/source-attributed percentage coverage. Existing behavior/API/browser checks remain required. The deferral does not assert manual assistive-technology or native IME acceptance that was never run.

- [x] Stable `@webawt//:webawt` consumer target and documented installation/mounting/disposal/asynchronous behavior.
- [x] Apache-2.0 LICENSE, NOTICE, third-party notices and explicit provenance records under the existing license decision.
- [x] Deterministic source-candidate packaging, SHA-256 manifest/checksums, resolved-module inventory and scoped CycloneDX SBOM.
- [x] Candidate-only release workflow, immutable artifact naming/version checks and documentation reconciliation.
- [x] Archive consumer smoke passes in Chromium, Firefox and WebKit, including real blue-ellipse pixels on Direct Canvas and BufferedImage, self-check and zero retained resources after unmount. All 59 main test targets, the 321-target build, 48 compatibility-tool tests, five quality-tool tests and five release-tool tests pass, along with formatting, dependency policy, bundle budgets, profile lock, strict API/Graphics2D completion and 6101 reference consumer probes. The 48-component SBOM passes the official CycloneDX 1.6 JSON schema. Local logs: `out/release-full-validation.log`, `out/release-final-gates.log` and `out/release-consumer-second.log`. Candidate location: `out/release-candidate-path.txt`.
- [x] The packaging test caught and repaired two distribution issues: root-local patch labels are required, and generated consumer workspaces must be excluded from `//...` through `.bazelignore`. The source candidate includes that ignore file and the patch.
- [ ] Prepublication provenance/legal review from ADR 0003: not recorded; candidate preparation does not satisfy this independent review.
- [ ] Public publication: no release destination or publication requested; workflow prepares reviewable artifacts only.

The release tooling and archive contract are documented in `docs/releasing.md`. Source candidates preserve the Elemental2 patch and repeat the override in the consuming root. They include the existing showcase and font, not downloaded dependency/browser binaries. The SBOM states its source/build scope and leaves unknown dependency licenses unasserted.

## Execution checkpoint: retained text, complete graphics coverage, performance and extended APIs

This checkpoint supersedes the remaining-work statements in the historical checkpoints below.

- Attributed drawing on Canvas and BufferedImage snapshots iterator runs and preserves position, fonts, foreground/background, swap colors, underline and strikethrough. TextLayout, TextMeasurer and LineBreakMeasurer share run data and respect supplied break iterators. Positioned/transformed glyph vectors paint on both backends. The shared reference font provides 2-pixel advance/caret/highlight tolerances; unsupported mixed-direction caret geometry, replacement attributes and true glyph-outline fidelity are explicit in `docs/text-rendering-profile.md`.
- The consuming application now has **37 shared graphics scenarios**, including eleven new attributed/layout/glyph, primitive/query, rendered/renderable-image and performance panels. All **84 signatures** are reviewed: **83 verified on both backends**, plus `setXORMode` verified on BufferedImage and explicitly rejected on Canvas. No implemented signature remains pending. Native JDK 17 passes 148 shared scenario/offset/background combinations and the scaled-surface check. Renderable-image transforms, 3D borders and transformed solid hairlines were repaired with paired assertions.
- Software fills prepare flattened geometry once and use scanline spans with bounded paint tiles. Canvas uploads use a content-verified four-entry / 262144-pixel LRU; direct raster-array and shared-subimage mutations invalidate reuse. Larger uploads remain uncached and temporary native canvases are released. Browser tests assert one upload plus nineteen hits, eight changed-image uploads, bounded eviction, and zero retained resources after teardown.
- Optimized workload gates require each of these to complete within 2 seconds: 50 curved software fills, two 512x513 uncached uploads, ten 512x256-to-256x128 software image draws, and twenty layouts of 100 components. Measured Chromium/Firefox/WebKit times (milliseconds) were 745/1272/1020 for curves, 13/12/29 for large uploads, 466/833/673 for scaling, and 4/10/4 for layout. Repeated mount/dispose checks cover document listeners, scheduled callbacks, focus, windows, DOM children and image resources. Uninstallation cancels image decoding, clears runtime event/drag state and releases singleton application listeners. Successfully loaded fonts and pending font registration remain document-scoped.
- Extended API review now covers **103 type dispositions and 1528 individual member dispositions**, with zero unreviewed or missing retained candidates and zero incompatible retained declarations. Repairs include accessible component/container/list/menu contexts, protected component/Toolkit/GridBag hooks, mutable event fields, geometry factories and browser-local drag/drop extension contracts. The precise retained and unavailable surfaces are recorded in `docs/extended-api-profile.md` and the locked dispositions.
- **6101 generated consumer probes in 396 types**, including fields, constructors, generics, checked exceptions and subclass overrides, compile against both WebAWT and the exact JDK 17 reference. The live J2CL package-ownership audit rejects namespace collisions and validates the single unavailable DataFlavor/Externalizable parent exception. Compilation coverage does not imply complete behavioral equivalence. Both API-profile completion and Graphics2D showcase completion are mandatory in validation and CI.
- Reviewed bundle growth: point remains 7086 raw / 3025 gzip bytes. The small showcase is 187250 / 60998 (+5623 / +1929), and the dual showcase is 369652 / 124267 (+33881 / +12307). Retained text/layout, lifecycle and accessibility implementations account for shared growth; eleven new demonstration panels and diagnostics increase the dual consumer further. Generated compile probes do not ship in these bundles. Limits are now 188/61 KiB for the small showcase and 368/124 KiB for the dual showcase (raw/gzip); point limits are unchanged.
- Final validation passes: **48 main JVM/J2CL targets, five desktop targets, six modern-browser suites and the complete 320-target build**. All three dual-showcase browser suites pass 32 tests each, including 1x/2x graphics assertions and repeated teardown. All 48 compatibility-tool tests, five quality-tool tests, formatting (860 source/test and 21 showcase files), dependency policy, bundle budgets, profile lock, strict public/extended API gates, graphics completion and generated/reference consumer probes pass. Exact OpenJDK 17 additionally passes the 148 shared graphics combinations and five new extension-behavior tests. The integrated build caught missing direct dependencies in two new test targets; both declarations were repaired before completing the build and remaining gates. Local evidence is in `out/finalization-validation.log` and `out/finalization-completion.log`. Final attributed, curved-fill/hairline and rendered-image screenshots were visually reviewed.

The API report's `releaseReady` field refers only to the reviewed API profile. The September 8 checkpoint records the accepted showcase scope and subsequent release engineering.

## Historical execution checkpoint: compatibility foundation and EventQueue

- [x] Fail the public API gate and worklist for missing feasible members on partial types, missing supported types, incompatible supported type declarations, and unresolved or inconsistent classifications. Regression tests cover the original missing-member escape and both CLI option names.
- [x] Freeze public member dispositions, reviewed extensions, and prerequisite reference roots in `compatibility/stock-profile-lock.json`. Disposition swaps with unchanged totals also fail. CI checks the lock without rewriting it.
- [x] Export from Bazel-declared production jars, excluding stale outputs. Add a class-file visibility audit and batch javap export for protected members, accessible nested types, and the 38 shipped prerequisite type families.
- [x] Generate `compatibility/extended-api-worklist.md` and JSON. New contracts require explicit review; disappearance or incompatibility of reviewed supported extensions fails the development gate. `-RequireCompleteApi` enforces closure of the entire extended backlog.
- [x] Implement EventQueue's protected `dispatchEvent` and `pop` hooks, pending-event transfer, nested current-event restoration, and continued scheduling after dispatch failure. The seven EventQueue J2CL test methods pass.
- [x] Integrate the new gates and extended reports into Windows validation and Linux CI, pin the reference JDK, and retain test diagnostics and browser outputs as CI artifacts.
- [x] Validate this integrated checkpoint: all 46 JVM/J2CL targets, three desktop targets, and six Chromium/Firefox/WebKit suites pass, along with the complete 303-target build, formatting, dependency policy, bundle budgets, profile lock, and compatibility gates. Five quality-tool tests and 40 compatibility-tool tests pass. The first full run encountered an out-of-memory interruption; limiting Closure to one persistent worker allowed the complete retry to pass. The final additional method-modifier regression test and extended gate were checked after that run.

The refreshed extended export identifies 440 reference types versus 389 emitted types. Beyond the old public-only denominator, it exposes 12 missing candidate types, 367 missing candidate members, 102 type dispositions and 1,554 member dispositions requiring review, and declaration differences on 14 types and 12 members. Both newly implemented EventQueue hooks are present in the rebuilt class jar, and there are zero missing or incompatible explicitly supported extension contracts. Candidate gaps include browser/J2CL-infeasible contracts and require review rather than automatic implementation promises. The extended API release-completion gate remains intentionally unsatisfied.

Section 1 remains open for disposition of the newly discovered contracts, generated consumer compilation probes, J2CL package-ownership enforcement, and fuller behavior evidence. Graphics2D implementation and showcase changes have not started in this checkpoint; the mandatory paired delivery rule above remains in force.

## Execution checkpoint: component and control event extensions

- [x] Implement Component's final protected `enableEvents(long)` and `disableEvents(long)`. Event families are selected independently; disabling a mask does not remove listener interest. Application-defined event IDs remain dispatchable.
- [x] Add protected action/item/text processing hooks and event routing for Button, Checkbox, Choice, List, TextComponent, and TextField. Browser peer callbacks now pass through `dispatchEvent`, Toolkit observers, subclass hooks, and listener snapshots.
- [x] Apply mask/listener filtering to Container, Window, and Scrollbar without conflating component, container, window focus/state, or mouse motion/wheel events.
- [x] Add 14 shared consumer regression methods, compiled and run against WebAWT, the configured Bazel desktop JDK 21, and the exact OpenJDK 17.0.16 reference. Cover listener-free masks, selective disabling, listener removal, subclass suppression, listener snapshots, and inherited TextField text/action routing.
- [x] Add DOM regression assertions for eight control event paths and Toolkit/subclass/listener delivery. TextField Enter translation remains covered by the existing modern-browser workflows: the legacy J2CL runner cannot synthesize a usable KeyboardEvent. Its action subclass hook is covered by the shared consumer tests.
- [x] Review and lock 23 additional protected extension contracts with test evidence. These consumer tests are concrete subclass compilation probes; comprehensive generated signature probes remain open.
- [x] Validate all 47 main targets, four desktop targets, and six modern-browser suites, including the existing dual-runtime showcase in Chromium, Firefox, and WebKit. The complete 311-target build, 452-file formatting check, dependency policy, bundle budgets, five quality-tool tests, and 40 compatibility-tool tests pass. The DOM test was rerun successfully after removing unsupported legacy keyboard synthesis; the other 46 main targets passed their fresh full-suite run.
- [x] Regenerate the public and extended inventories from rebuilt production jars and pass the profile lock, strict public API gate, public completeness worklist, and extended development gate.

This checkpoint closes nine missing protected members and reviews 23 contracts. The extended backlog now contains 358 missing candidate members, 12 missing candidate types, 1,531 member dispositions and 102 type dispositions requiring review, plus declaration reviews on 14 types and 12 members. All 25 explicitly reviewed extension contracts (including the earlier EventQueue pair) are present with compatible declarations. Release completion remains open; these figures include unreviewed candidates rather than a promise to implement browser-infeasible APIs.

Graphics2D implementation changes remain paired with showcase scenarios and assertions. The next rendering step is the signature-level showcase coverage inventory and the shared transform/clip/state scenarios from section 2; extended API disposition and generated consumer probes remain tracked in section 1.

## Execution checkpoint: Graphics2D state and showcase coverage

- [x] Inventory all 84 public Graphics2D signatures, including inherited Graphics methods and every overload, in `compatibility/graphics2d-showcase-coverage.json` and `.md`. Resolve each backend's actual declaring source, including inherited default implementations. Keep constructors and internal WebAWT-only helpers outside the JDK consumer-method denominator.
- [x] Add explicit reviewed scenario/evidence mappings for 19 signatures on both Canvas and BufferedImage. Remaining signatures stay visibly pending; Canvas XOR is explicitly unavailable. The generator checks signature resolution and evidence references, while behavioral coverage requires the linked tests. Four tooling tests cover overloads, inherited resolution, invalid reviews, and missing backend evidence.
- [x] Store the Canvas user clip in surface coordinates, transform incoming clips once, and invert the current transform for clip queries. Changing the transform no longer moves an established clip; created contexts copy clip state independently.
- [x] Make Canvas `clearRect` respect the transform and clip, replace pixels with the configured background regardless of the active composite, preserve state, and ignore nonpositive dimensions. Clearing and background filling are constrained to the transformed clear path and enclosed in peer save/restore.
- [x] Add **Rendering lab > Graphics state** to the shared consuming application: direct Canvas and BufferedImage comparisons for clip-before-translation, transformed intersections, background clearing, and child isolation. Controls select the scenario, switch translation between 20 and 40, and choose cyan or transparent clearing. Query results and state assertions are visible alongside the output.
- [x] Add shared BufferedImage pixel/state vectors, real JDK Canvas pixel checks, recorded Canvas commands, and optimized browser pixel assertions for both displayed surfaces. Native desktop Canvas assertions cover opaque backgrounds; transparent desktop surfaces remain platform-dependent. Browser Canvas alpha and BufferedImage alpha are checked explicitly, including transparent-image composition onto the white showcase page.
- [x] Add the matrix generator to development validation and CI report artifacts. `--require-complete` rejects the remaining method-demonstration backlog; the regular development gate does not claim full Graphics2D coverage.
- [x] Validate all 47 main targets, four desktop targets, and six Chromium/Firefox/WebKit suites. Complete the 311-target build, formatting for 453 source/test files and nine showcase files, dependency policy, all 44 compatibility-tool and five quality-tool tests, refreshed API inventories, profile lock, public/extended development gates, and showcase coverage validation. Run the 16 shared pixel/query combinations separately on OpenJDK 17.0.16. Review the Chromium screenshot; record the Canvas XOR limitation in Runtime & limits. Final optimized dual-showcase output is 306,606 raw / 101,362 gzip bytes, within the existing budgets. The completion gate correctly remains unsatisfied at 19/84 verified signatures.

At this checkpoint, 65 signatures were still awaiting complete demonstrations and assertions on both backends. The following checkpoint addresses transformed/clipped `copyArea` and image placement; stroke/text/paint behavior remains open. The matrix is an incremental reviewed coverage record, not proof of all rendering combinations or pixel-perfect antialiasing parity.

## Execution checkpoint: copying and image placement

- [x] Map `copyArea` through the current transform on both backends, using the JDK pixel-center convention. Clip only the destination; retain an overlap-safe source snapshot and use the active composite. Bound browser SRC operations to accessible destination pixels, preserve unavailable source pixels, and convert source coordinates correctly at 2x device density.
- [x] Support translation and positive axis-aligned scaling. Empty requests do nothing. Rotated, reflected and sheared copies fail explicitly. Stock J2CL lacks `InternalError`, so WebAWT uses `UnsupportedOperationException`; the shared consumer accepts this documented profile difference.
- [x] Correct both `drawImage(BufferedImage, BufferedImageOp, int, int)` implementations so a null operation draws the image unchanged. Demonstrate a non-null rescale operation as well.
- [x] Add five selectable Graphics state scenarios: scaled/clipped copies, overlapping copies, composite/transform limits, all six inherited image overloads, and affine/filtered images. Assert each image overload's quadrant pixels and applicable boolean result, transparency/background handling, reversed endpoints, affine placement, fixed clipping, and state preservation.
- [x] Extend the reviewed method matrix to 28/84 signatures on both backends: `copyArea` and all eight `drawImage` overloads join the previous 19. Keep the remaining 56 signatures pending.
- [x] Review the consuming-application size allowance for the five new scenarios and nine additional signatures: dual-showcase limits increase from 304 to 308 KiB raw and 100 to 102 KiB gzip. Other bundle budgets stay unchanged. The initial measured increase is 4,981 raw / 1,714 gzip bytes (about 1.7% compressed).
- [x] Validate all 47 main targets, four desktop targets, and six Chromium/Firefox/WebKit suites, including copy/image pixel comparisons at 1x and 2x browser density. Complete the 311-target build, formatting for 454 source/test and nine showcase files, dependency policy, 44 compatibility-tool and five quality-tool tests, API exports, profile lock, and public/extended development gates. The full build exposed a missing direct painting dependency in the new test; adding it completed validation. OpenJDK 17.0.16 passes all 36 shared scenario combinations and the separate BufferedImage source-boundary vectors. Review the copy/image screenshots. Final dual-showcase output is 311,587 raw / 103,076 gzip bytes; other bundles also pass their budgets. Public inventory remains 299/299 types and 4,714/4,714 callable members; supported extension failures remain zero. Release completion remains open.

Unavailable source pixels are checked separately on BufferedImage and browser Canvas: native desktop windows may expose/repaint missing source regions according to the window system. Shared direct-desktop comparisons use available source pixels. This checkpoint does not claim interpolation, antialiasing, all composite combinations, asynchronous observers, or rendered/renderable-image coverage. The following checkpoint addresses stroke widths, dash arrays/phases, and custom strokes with paired showcase scenarios and assertions.

## Execution checkpoint: stroke geometry and primitive paths

- [x] Apply the Canvas transform to both path and positive-width BasicStroke geometry. Forward dash arrays and phases, resetting the native dash when switching to a solid stroke. Keep the established device clip fixed and restore peer state after drawing. Avoid configuring stroke styles for fill-only operations.
- [x] Evaluate custom Stroke objects in user coordinates and fill the resulting shape on Canvas. Draw complete rectangle/polyline/polygon paths so custom strokes receive each primitive once and BasicStroke dashes/joins remain continuous.
- [x] Restrict BufferedImage's default-width pixel-line fast path to untranslated/translated unit-scale geometry. Transformed default strokes use the stroked shape; non-default polylines, rectangles and polygons likewise use a single complete path.
- [x] Fill the complete BasicStroke miter join wedge, including its center vertex. Screenshot review exposed triangular holes in software joins; shared showcase pixels and JVM/J2CL outline assertions now cover both halves of left- and right-turn joins.
- [x] Add five shared showcase scenarios: transformed strokes, dash phase/continuity, caps/joins/miter limits, custom stroke primitives and dashed primitives. Exercise eight primitive overloads individually plus draw(Shape), setStroke and getStroke, including child-state isolation.
- [x] Extend reviewed coverage to 39/84 Graphics2D signatures on both backends. Every new row links the primitive's own observable pixels or state assertion. The remaining 45 signatures stay pending.
- [x] Review bundle allowance for the five stroke scenarios and eleven additional method demonstrations: dual-showcase limits increase from 308 to 312 KiB raw and 102 to 104 KiB gzip. Other bundle budgets are unchanged. Final dual-showcase output is 317,058 raw / 104,920 gzip bytes, an increase of 5,471 raw / 1,844 gzip bytes (about 1.8% compressed).
- [x] Complete the full development validator: all 47 main targets, four desktop targets and six Chromium/Firefox/WebKit suites pass, followed by the 311-target build, bundle budgets, dependency policy, 44 compatibility-tool tests, five quality-tool tests, API exports, profile lock, strict public API gate and extended development gate. Formatting passes for 454 source/test and ten showcase files. OpenJDK 17.0.16 passes all 56 shared showcase combinations. Review the final stroke and dashed-primitive screenshots and confirm the miter holes are gone. Public coverage remains 299/299 types and 4,714/4,714 callable members; supported extension failures remain zero. Full release completion and the 45 remaining Graphics2D demonstrations stay open.

Stroke pixel comparisons use positive widths and interior points. Both backends are checked at native image resolution; 2x browser-density stroke checks use direct Canvas because enlarged BufferedImage edge colors are filtered. The existing Canvas hairline path is retained; zero-width parity, degenerate paths, extreme transforms, paint/composite combinations and antialiasing parity remain open. The next core rendering step is paint/composite behavior and explicit handling of unsupported paint/composite implementations, with paired showcase cases.

## Execution checkpoint: shape paints and composite boundaries

- [x] Render Canvas non-color shape paints through tiled PaintContexts, respecting the graphics transform, fixed clip, paint transform and surface bounds. TexturePaint, custom Paint, cyclic gradients and linear-RGB/transformed gradients now have observable shape output. Custom paint also colors stroked outlines.
- [x] Dispose paint contexts after success and failure on both backends, restore Canvas peer state after paint exceptions, and read raster data relative to its actual origin. Image and copy operations ignore the current Paint.
- [x] Bound Canvas destructive composite operations to the actual shape or accessible image/copy destination. CLEAR removes pixels independently of source/paint alpha; transparent SRC pixels replace the destination. Explicitly reject custom Composite on both backends before changing state.
- [x] Add six shared paint/composite panels, increasing reviewed coverage to 44/84 signatures: fill(Shape), setPaint/getPaint and setComposite/getComposite. Existing draw, image and copy mappings also link the new behavior. The other 40 signatures remain pending.
- [x] Record deliberate differences: the JDK accepts custom Composite; WebAWT rejects it. WebAWT disposes failed paint contexts immediately whereas JDK 17 does not. The showcase reports native text-paint rejection and verifies subsequent drawing. Native desktop alpha-dependent comparisons use BufferedImage; browser Canvas retains alpha and is checked directly.
- [x] Review the consuming-application budget for six new panels and five additional method demonstrations: dual-showcase limits increase from 312 to 320 KiB raw and 104 to 106 KiB gzip. Final dual-showcase output is 324,861 raw / 107,562 gzip bytes, an increase of 7,803 raw / 2,642 gzip bytes (about 2.5% compressed); other bundle limits stay unchanged.
- [x] Complete every development-validation stage: all 47 main targets, four desktop targets and six Chromium/Firefox/WebKit suites pass, along with the 311-target build, bundle budgets, dependency policy, 44 compatibility-tool tests and five quality-tool tests. Formatting passes for 454 source/test and eleven showcase files. The affected J2CL consumers pass a final rerun after adding native-window tolerance metadata. OpenJDK 17.0.16 passes all 80 shared scenario combinations. Review the texture/composite screenshots and confirm all 57,600 texture-surface pixels agree between Canvas and BufferedImage. Regenerate API inventories and pass the profile lock, strict public API gate, extended development gate and showcase matrix. Public inventory remains 299/299 types and 4,714/4,714 members; supported extension failures remain zero. Release completion and the remaining 40 method demonstrations stay open.

The new composite grid initially reproduced the known curved software-fill stall. It uses triangles to test exact shape boundaries without relying on that unresolved hotspot; section 4 still requires prepared geometry and measured performance budgets. Gradient vectors permit 3 channel levels, or 6 for radial resampling at 2x density, and the global-alpha vector permits 1. JDK native-window vectors separately permit 4 levels on the reflected ramp and 8 at the radial center because of pixel-center sampling and native channel rounding. Other comparisons remain exact. Paint contexts currently sample at logical surface resolution. This checkpoint does not establish antialiasing/interpolation parity, all fractional-alpha combinations, extreme transforms, or native text gradient fidelity. The next rendering work is the text/font contract and its remaining showcase demonstrations.

## Execution checkpoint: browser font measurement and font-context state

- [x] Install a browser font-measurement provider and use complete-string measurements in Font bounds and FontMetrics string/character-array/byte-array widths. CharacterIterator bounds preserve iterator position and surrogate pairs. Browser ascent/descent use native font bounds (or Hg ink bounds when unavailable), with zero leading; portable/model use retains the existing estimates. Measurements are not cached across font loading.
- [x] Use consistent CSS logical-family mapping and quote custom families for Canvas text and measurement. Capture Graphics2D FontRenderContext state on both backends: retain the linear transform and explicit text hints, remove translation, and return defensive snapshots through both the graphics and its FontMetrics.
- [x] Add asynchronous WebAwt.loadFont(family, url), resolved-URL deduplication and failed-request eviction. Add visible loading/failure/retry controls to the consuming application. A late completion cannot repaint a disposed showcase; independent image loading remains unaffected. Loaded font faces live for the document lifetime.
- [x] Bundle an unmodified DejaVu Sans reference font with its license/source notice; the desktop launcher and browser tests load the same bytes. Add font-state/measurement bars and font-context scenarios to the shared showcase. Reviewed Graphics2D coverage increases to 49/84 signatures with setFont, getFont, both getFontMetrics overloads and getFontRenderContext; 35 signatures remain pending.
- [x] Verify complete-run measurement, array/iterator handling, refreshed measurements and transformed font advances through provider-contract tests. Browser checks compare loaded widths against an independent native canvas (0.05px tolerance) and four JDK reference samples (2px tolerance), verify visible bar endpoints on both backends, retry the same failed URL, reuse loaded faces and ignore completion after disposal.
- [x] Review measured bundle growth: the smaller showcase grows by 2,207 raw / 797 gzip bytes; its raw budget increases from 176 to 180 KiB and its gzip budget stays at 58 KiB. The dual showcase grows by 6,276 raw / 2,482 gzip bytes (about 2.3% compressed); its budgets increase from 320 to 328 KiB raw and 106 to 108 KiB gzip. The reference TTF is a separate 759,720-byte asset loaded on request in the browser.
- [x] Complete all development-validation stages: 47 main targets, four desktop targets and six Chromium/Firefox/WebKit suites pass, along with the 311-target build, bundle budgets, dependency policy, 44 compatibility-tool tests and five quality-tool tests. Formatting passes for 457 source/test and fourteen showcase files. The final affected JVM/J2CL targets pass after the direct iterator dependency and initially scaled graphics-context regression are added. OpenJDK 17.0.16 passes all 88 shared scenario combinations and the initial-scale probe. Review the loaded-font screenshot and its matching Canvas/BufferedImage bars. Regenerate compatibility reports and pass the profile lock, strict public API gate, extended development gate and showcase matrix. Public inventory remains 299/299 types and 4,714/4,714 members; supported extension failures remain zero. Final bundles are 7,086 / 3,025 bytes for point, 181,437 / 59,019 for showcase and 331,137 / 110,044 for dual-showcase (raw / gzip). Release readiness and the remaining 35 Graphics2D demonstrations stay open.

This closed the first measurement/readiness slice of section 3. At that checkpoint text painting remained open; the following checkpoint adds plain-string BufferedImage painting and Canvas transforms. Attributed runs, glyph outlines, contextual shaping and layout/caret/highlight error budgets still require work. FRC reports logical-surface transform/hint state; native browser rasterization need not implement every retained hint. Font-transformed bounds, fallback-run line metrics, maximum-glyph bounds and fractional/hinted parity remain follow-up work. The following checkpoint pairs plain-text rendering changes with visible glyph scenarios on both backends.

## Execution checkpoint: plain text painting and transforms

- [x] Implement both BufferedImage drawString(String, ...) overloads with native browser whole-run glyph coverage, transformed ink bounds and bounded 128x128 tiles. Apply graphics and font transforms, surface clips, PaintContext and AlphaComposite; interpolate antialiased coverage after compositing so SRC/CLEAR leave uncovered pixels intact. Dispose paint contexts on failure. Model-only execution without a rasterizer and browsers without native ink bounds fail explicitly.
- [x] Apply the full graphics/baseline/font transform to Canvas glyphs instead of only moving their origin. Preserve native state with try/finally. Explicitly reject SRC, SRC_IN, SRC_OUT, DST_IN and DST_ATOP for Canvas plain text, since native Canvas otherwise modifies pixels outside the glyphs. Retain native color/gradient text; arbitrary Paint remains unavailable on direct Canvas text.
- [x] Demonstrate both String overloads plus inherited drawChars and unsigned drawBytes in four paired showcase panels: overloads, transforms, clipping/translucency, and clearing/recovery. Check byte-array bounds even for empty ranges. Coverage increases from 49 to 53 of 84 signatures; 31 remain pending.
- [x] Verify transformed command mapping, controlled coverage/composite results, untouched pixels, bounded raster tiles and cleanup after rasterization failure. Compare reference-font glyph pixels against independent native rendering in Chromium, Firefox and WebKit at 1x/2x density and both offsets. Require ink in every text region in shared JDK BufferedImage/direct Canvas checks; alpha-dependent desktop checks use BufferedImage. The legacy PhantomJS suite retains the first 22 model scenarios; actual glyph acceptance runs in modern browsers and the JDK.
- [x] Review all four paired text screenshots, including transforms and clearing. Measured dual-showcase growth is 4,634 raw / 1,916 gzip bytes (about 1.7% compressed), including the four acceptance panels and rasterization path. Increase its limits from 328 to 332 KiB raw and 108 to 110 KiB gzip; final output is 335,771 raw / 111,960 gzip bytes. The smaller showcase grows by 190 raw / 50 gzip bytes to 181,627 / 59,069 and stays within its existing limits; point remains 7,086 / 3,025. The font asset is unchanged.
- [x] Complete every development-validation stage: all 47 main JVM/J2CL targets, four desktop targets and six modern-browser suites pass. The corrected direct image-core test dependency builds and the affected test remains passing. The 311-target build, bundle budgets, dependency policy, 44 compatibility-tool tests and five quality-tool tests pass; formatting passes for 459 source/test and fifteen showcase files. OpenJDK 17.0.16 passes 104 shared scenario combinations plus the initial-scale probe. Regenerate public/extended API inventories and pass the profile lock, strict public API gate, extended development gate and 53/84 showcase matrix. Public coverage remains 299/299 types and 4,714/4,714 callable members; supported extension failures remain zero. Release readiness and the remaining 31 Graphics2D demonstrations stay open.

This completes the plain-string painting slice, not the entire font engine. Native-oracle acceptance permits a total RGBA difference of 8 channel levels per reference-ink pixel (normalized by four channels), with differences counted across the whole surface. Measured maximum normalized native-oracle errors are 1.361 in Chromium, 0.181 in Firefox and 0.187 in WebKit, below the limit of 8. JDK checks establish visible glyphs, transforms and clipping without claiming identical outlines across engines. BufferedImage stores logical-resolution text and is resampled when displayed at higher density. Attributed drawString, true glyph outlines, contextual layout/caret/highlight behavior, fractional clip edges and rasterization-hint parity remain open. The next text step is attributed-run painting with matching showcase acceptance.

## Current baseline

The checked-in generated reports record 299 of 299 target top-level AWT types and 4,714 of 4,714 target members callable. They classify 265 types as implemented and 34 as partial. The 26 not-started types correspond to the 11 unavailable and 15 excluded types; they are not a backlog of 26 feasible classes.

These figures describe the current inventory, not the complete Java extension contract or behavioral parity. The reference inventory identifies OpenJDK 17.0.16. The plan describes Java 8-era foundations plus a Java 17 inventory; the release should state its baseline unambiguously.

Evidence: `compatibility/webawt-api-validation.md`, `compatibility/api-completeness-worklist.json`, `compatibility/openjdk-awt-api.json`.

The root build now lists 48 main JVM/J2CL test targets, five display-dependent targets, and six modern-browser targets. The browser suites cover substantially more than the old Phase 5 document suggests: keyboard input, composition translation, menus, scrolling, images, dialogs, clipboard, drag/drop, and repeated mounting are already exercised.

## 1. Compatibility gates - completed for the retained profile

The current checkpoint closes the audit, disposition, probe and package-ownership work. Original findings and acceptance criteria remain below for traceability.

### Confirmed gate escape

An in-memory audit removed `public int getX();` from the emitted inventory for `java.awt.Component`. The comparator reported one missing feasible member, but its original CLI failure predicate remained false. The worklist also produced zero violations. No inventory files were modified by this experiment.

Before finalization, the comparator failed only for false implemented claims and missing supported types, while the worklist counted missing members without rejecting them. This escape is now fixed: the stricter comparator and worklist both reject it, with executable regression tests.

Acceptance criteria:

- Make missing feasible members, missing feasible types, and supported declaration incompatibilities explicit failures.
- Add mutation tests that remove a partial member, change extension modifiers, break inheritance, or alter the disposition denominator and verify failure.
- Require reviewed changes to the supported denominator, so reclassification cannot silently disguise a regression.

### Confirmed inventory omissions

The original public-only reference and emitted inventories omit nested classes and protected members. The extended exporter now covers these contracts and the shipped prerequisite type families; all retained contracts and unavailable dispositions are reviewed.

The audit found a concrete gap: OpenJDK exposes protected `EventQueue.dispatchEvent(AWTEvent)` and `pop()`, while WebAWT originally used a private static dispatch helper and had no pop operation. These hooks are now implemented and tested in the checkpoint above. The generated worklist now has no unresolved retained extension gaps.

Acceptance criteria:

- Inventory accessible nested types and protected extension points, then audit the newly discovered gaps.
- Add generated compilation probes for constructors, overloads, fields, generics, checked-exception contracts, inheritance, and subclass overrides.
- Preserve extension compatibility: the original normalizer stripped `abstract`, `final`, and checked-exception declarations. The extended gate and generated probes now check these declarations.
- Inventory and test the shipped prerequisite subsets in `java.net`, `java.text`, `java.beans`, `java.awt.datatransfer`, and `javax.accessibility` as appropriate. The older AWT-only export did not establish this; the extended export and generated probes now cover those retained prerequisite subsets.
- Enforce the project-owned/J2CL package-ownership audit on toolchain upgrades, as required by ADR 0006.
- Replace the coarse type-list evidence assertion with traceable behavior-test evidence for supported contracts.

Exit: every retained API and extension point compiles in consumer probes and the applicable showcase scenarios, and deliberately injected compatibility regressions fail CI.

## 2. Core rendering and showcase methods - retained profile completed

All 84 signatures have reviewed evidence and no implemented method remains unmapped. Explicit profile limits remain; the following records the source audit and acceptance criteria.

Current retained behavior following the `CanvasGraphics2D.java` source audit:

- `clearRect` now applies the transform, clip, and configured background, with paired showcase assertions. `copyArea` now maps translated/scaled source bounds and copy offsets, applies the destination clip and composite, snapshots overlapping copies, and accounts for browser device pixel density. Unsupported rotation/reflection/shear is explicit.
- Plain-string `drawString` applies both graphics and font transforms to the glyphs. BufferedImage rasterizes whole runs through the browser provider. Attributed drawing now preserves styled runs on both backends.
- Positive-width BasicStroke paths and stroke geometry now receive the Canvas transform together; dash arrays/phases, caps, joins and miter limits are forwarded. Custom strokes are evaluated before filling their returned shapes. Polyline, polygon and rectangle paths preserve joins and dash continuity on both backends. Transformed solid hairlines are implemented; dashed and singular-transform hairlines are explicitly unavailable. Extreme/degenerate geometry and full antialiasing fidelity are outside the retained claim.
- TexturePaint and custom Paint now render shapes through paint contexts on both backends. Custom Composite is explicitly rejected without changing state. Canvas native text/glyph paths reject arbitrary paint; BufferedImage plain text now samples PaintContext. Canvas plain text explicitly rejects composites that native Canvas would apply outside glyph coverage.
- Shape gradients now use their paint contexts, including cycle/color-space settings and paint transforms. Native text gradients and most rendering hints still have fidelity gaps.
- Canvas clips now remain fixed in surface coordinates across transform changes. Shared state/rectangle scenarios are covered; shared image, text, stroke and paint scenarios now exercise the retained combinations.

The shared gallery now compares TexturePaint directly on Canvas and BufferedImage. Arbitrary paint works for shapes and BufferedImage plain text; direct Canvas arbitrary text paint and broader font fidelity have explicit profile limits.

Acceptance criteria:

- Establish one consistent transform/clip/state model across paths, text, images, clearing, copying, and child graphics contexts.
- Implement the retained paint, stroke, composite, and hint behavior; give other cases explicit documented failure or limitation behavior.
- Add shared JVM pixel vectors where deterministic, recorded-command tests for state mapping, and browser pixel checks for actual Canvas output.
- Exercise combinations: translated clear/copy, rotated text, scaled dashed paths, clip-before-transform, gradients under nonuniform scale, and images with transparency.
- Update the dual-runtime showcase alongside every Graphics2D implementation change, following the method coverage contract below.

### Mandatory Graphics2D showcase coverage

- Maintain a signature-level coverage matrix for all implemented Graphics2D methods, including every overload and inherited Graphics method. Map each signature to the implementation/backend, showcase page and scenario, observable result, and automated assertion. Audit existing methods as well as newly completed ones; a single example for a method family does not establish overload coverage.
- Expand the existing Graphics2D and Rendering lab pages into navigable scenarios covering shapes and primitives, transforms, clipping, paints, strokes, composites, rendering hints, text and glyphs, images, clearing/copying, hit testing, state inspection, and graphics creation/disposal.
- Make each implemented drawing operation visibly inspectable. Show input controls and rendered results so the same scenario can be compared between the desktop and browser variants.
- Demonstrate getters and queries through visible reported values and assertions. Demonstrate setters through both their reported state and their drawing effect. Demonstrate create/dispose and child contexts through state isolation and resource-lifecycle scenarios. Exercise applicable protected extension points through scenario subclasses.
- Exercise Canvas and BufferedImage directly wherever a method is implemented on both backends. A BufferedImage workaround does not count as a demonstration of the Canvas implementation.
- Use shared scenario code against the JDK and WebAWT, with browser-specific integration kept at the existing launcher/peer boundary. Record deliberate profile differences and engine tolerances alongside the affected scenario.
- Add automated assertions for each scenario's meaningful result: pixels, geometry, state, return values, observer callbacks, or lifecycle behavior as appropriate. Drive the optimized browser variant in Chromium, Firefox, and WebKit and run the desktop variant's corresponding tests.
- Keep unavailable methods explicitly classified in the matrix and visible in the showcase's limitations information. Do not count unavailable methods, placeholder output, screenshots without assertions, or calls with no observable outcome as implemented-method coverage.

Exit: the same retained drawing contracts hold on both BufferedImage and Canvas, with explicit engine tolerances where needed and no silent substitution of unsupported operations. Every implemented Graphics2D signature has a working showcase demonstration and a passing regression assertion; no implemented method or overload remains unmapped.

## 3. Text and color fidelity - retained text profile completed

Font and FontMetrics now use native browser measurements after WebAwt.install, and a bundled reference font plus asynchronous loading are available. Portable/model estimates remain the fallback. Attributed runs and positioned/transformed glyphs now paint on both backends, with shared layout/wrapping and measured caret/highlight assertions. True font outlines, complete bidi/grapheme behavior and complex shaping controls remain explicit limitations in `docs/text-rendering-profile.md`.

Acceptance criteria:

- Browser measurement and asynchronous loading are implemented in the checkpoint above. Complete application layout/repaint integration and broader fallback/loading acceptance.
- Use fixed licensed test fonts; measure width, wrapping, caret, and highlight errors with stated tolerances.
- Test mixed-direction text, combining marks, supplementary characters, fallback fonts, transformed text, and attributed runs.
- Decide which complex shaping and true glyph-outline contracts remain limited. Full OpenJDK font-engine parity need not be a first-release requirement.

`ICC_ColorSpace.selectDelegate` maps custom RGB and gray profiles to predefined spaces rather than applying their actual profile transforms. The manifest and showcase Runtime & limits page expose this approximation. Standard-space conversion remains tested; arbitrary device-profile conversion is not a retained fidelity claim.

Exit: measured layout agrees sufficiently with painted text in the dual-runtime showcase, and color/font fidelity limits are specific and testable. Graphics2D text and glyph changes include the corresponding showcase scenarios and assertions.

## 4. Rendering performance and lifecycle - retained workloads validated

The original curved-fill stall came from rebuilding flattened edges for every containment query. Prepared geometry and scanline spans now remove that repeated work; the consuming application exercises actual curved fills without the previous triangle workaround.

`Elemental2CanvasPeer.drawImage` now reuses unchanged uploads under a bounded cache, detects direct array/subimage changes, and releases image surfaces during disposal. The current checkpoint records exact reuse and workload assertions.

Acceptance criteria:

- Prepare flattened geometry once per operation or use scanline rasterization; avoid per-pixel reconstruction and unnecessary allocation.
- Benchmark curved fills, large images, image scaling, repeated repaint, and component-heavy layout in optimized output.
- Cache browser image representations with explicit mutation invalidation and disposal.
- Define budgets for interaction latency, repaint time, and retained resources on an agreed reference environment.
- Extend lifecycle tests beyond DOM-child counts to listeners, pending callbacks, global focus/drag state, and graphics/image resources.
- Document the single global runtime/root, or provide teardown/reinstallation semantics suitable for embedding in another application's lifecycle. DesktopPeer now has idempotent attach/detach with document-listener removal.

Exit: representative workloads do not stall interaction, and resources return to an agreed baseline after repeated mounting and disposal.

## 5. Additional manual acceptance - deferred by owner

The current consuming showcase is accepted as good enough for this release preparation (2026-09-08). Additional OS IME/non-US keyboard/screen-reader acceptance, browser permission dialogs and remaining drag/drop edge workflows are deferred. Existing automated showcase assertions remain mandatory. This is a scope decision, not evidence that deferred workflows passed.

## 6. Release engineering

Implementation and evidence are recorded in the September 8 checkpoint above and `docs/releasing.md`. SonarQube and source-map-attributed percentage coverage are deferred by the owner; `docs/testing/coverage-policy.md` retains the executable behavior/API/browser gates.

The prepublication provenance/legal review required by PLAN.md and ADR 0003 remains distinct from creating notices and an SBOM. The candidate-only workflow does not publish a release, create a registry entry or mark that review complete.

Keep file dialogs, printing, Robot, global desktop integration, blocking browser event loops and a patched-J2CL profile outside this release profile. Bazel package consolidation is not required.

## Original audit verification (before the implementation checkpoint)

- Source, build, CI, generated-report, test, and documentation review.
- Formatting check: passed for 451 Java files.
- Quality-tool tests: 5 passed.
- Compatibility-tool tests: 14 passed.
- Production dependency policy: passed.
- In-memory missing-member mutation: confirmed the gate escape described above.
- Local JDK protected-API inspection: confirmed the missing EventQueue extension methods.
- Fresh main JVM/J2CL and modern-browser run: intentionally interrupted during a substantial cold J2CL/Protobuf toolchain rebuild. Bazel had completed 15 of 52 tests without reporting failures. This is not a passing full-suite result; the browser matrix, display suite, and complete build were not freshly certified during this audit.

The checked-in API counts and earlier full-build results are existing evidence, not a newly regenerated complete release certification. This audit is targeted source analysis, not an exhaustive proof of all 4,714 member behaviors.
