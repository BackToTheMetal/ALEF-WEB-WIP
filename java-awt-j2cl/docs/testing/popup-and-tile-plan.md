# Popup and tile parity fixes

Implemented and validated on 2026-09-06. The public AWT API and shared gallery tile bounds are unchanged.

## Findings

- `PopupMenu.show(origin, x, y)` forwards origin-relative coordinates to a peer attached to the menu's parent. A button nested inside that parent therefore opens the popup in the wrong place.
- The document's bubbling click listener dismisses the popup during the same button click that opens it. Existing gallery tests invoke an exported JavaScript method and miss this failure.
- Popup submenus are positioned like top-level menu-bar dropdowns and can extend beyond the viewport.
- The shared gallery sets every tile to 160 by 170 AWT units, with 185-unit column spacing and 220-unit row spacing. Live Chrome has the same CSS bounds and centered captions. Its device pixel ratio is 1.25. An exact-color scan across row 240 of the original 2969 by 950 screenshot measured three browser tile widths of 200 pixels and three desktop tile widths of 160 pixels. This is exactly a 1.25 scale difference. Changing Java bounds to compensate would break logical layout parity.

## Implementation plan

1. Pass the origin peer through the internal menu port. Position popup menus from the origin's rendered rectangle, so nested components and scrolling are accounted for. Keep the public AWT API unchanged and reject a hidden origin.
2. Handle outside clicks during capture, before the activating button handles the click. Preserve dismissal, checkbox state, keyboard operation, and listener disposal.
3. Fit popup and submenu rectangles to the viewport and open popup submenus beside their parent item.
4. Replace programmatic-only popup acceptance with real pointer activation; cover keyboard opening, nested actions, option persistence, dismissal, viewport edges, and remounting.
5. Verify all 18 tiles retain their shared AWT dimensions, spacing, and caption alignment at device scale factors 1 and 1.25. Add a JDK-side gallery geometry check. Keep physical display scaling separate from Java component bounds.
6. Run formatting, JVM/J2CL suites, all three modern browser engines, the complete Bazel build, bundle budgets, and API validation. Save and inspect the menus-page screenshot.

## Contracts

- [AWT PopupMenu.show](https://docs.oracle.com/en/java/javase/21/docs/api/java.desktop/java/awt/PopupMenu.html#show(java.awt.Component,int,int)) defines coordinates relative to the origin component.
- [DOM event capture](https://developer.mozilla.org/en-US/docs/Web/API/EventTarget/addEventListener) runs before target and bubbling handlers.

## Validation results

All stages of `tools/quality/Validate-WebAwt.ps1` passed after correcting the new test's selector, accounting for native border metrics, providing the older WebKit transform spelling, and declaring the test's direct foundation dependency. Bazel used `--jobs=1 --worker_max_instances=1`; the repository defaults were not changed. The final build and report stages resumed after the test-only dependency correction; the already passing browser matrix was not repeated for that dependency declaration.

| Check | Result |
| --- | --- |
| Java formatting | 448 source files passed |
| Quality tooling and dependency policy | 5 tests passed; production dependency policy passed |
| JVM/J2CL test suite | 46 targets passed |
| Desktop suite under Xvfb | 3 targets passed, including all 18 gallery tile bounds and alignment |
| Modern browser matrix | All 6 suites passed: both showcases in Chromium, Firefox, and WebKit |
| Complete Bazel build | 303 targets built |
| Compatibility tooling | 14 tests passed |
| Emitted API | 299 types compared with 325 OpenJDK types; zero false implementation claims |
| Completeness invariants | 299/299 target types and 4714/4714 target members callable; zero violations |
| Optimized bundles | Point 7,086 raw / 3,025 gzip bytes; showcase 170,297 / 55,186; dual showcase 241,277 / 79,644; all within unchanged budgets |

The live Chrome gallery was reloaded and the actual popup button and nested action were exercised successfully. The [final Chromium menus screenshot](../screenshots/menus-chromium.png) was inspected visually. Cross-browser tests check every tile's 160 by 170 CSS-unit bounds and centered caption at device scales 1 and 1.25. Browser zoom was left unchanged; physical pixel equality requires matching the display scales as described in the [comparison guide](../../samples/dual_showcase/README.md).
