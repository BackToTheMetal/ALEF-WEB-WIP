# Dialog appearance and Rendering lab responsiveness

## Defects and fixes

The browser mapped `Dialog` to an unstyled HTML dialog. The browser added default border, padding and automatic horizontal margins outside AWT's layout assumptions. The title was absent and the content had no decoration insets. `Elemental2DialogPeer` now resets that browser styling, provides a caption and close button, and paints a raised frame and shadow. `Dialog` reserves 4 logical units around the content and 26 for the title bar; its requested size remains the outer size. Undecorated dialogs retain zero decoration insets. Titles update the caption and accessible name without changing the component name.

The title-bar button dispatches `WINDOW_CLOSING`, allowing the application to decide whether to close, just as on the JDK. The gallery handles that event by disposing its dialog. Disposal removes the button listener. These decorations are an in-page browser mapping; native theme details and OS window management remain platform dependent.

The Rendering lab's Area subtraction repeatedly called `Path2D.contains` while classifying intervals. Each call rebuilt a curve approximation with hit-test flatness of 0.0000001. This allocated and subdivided the same paths hundreds of times during a single operation, blocking the browser without a console error. Area now builds each hit-test edge set once per operation and reuses it, preserving the existing flatness and winding rules. The gallery also retains its unchanged sample Area for painting and mouse hit tests.

## Regression coverage

- Browser integration tests cover packed decoration insets, dynamic titles, independent component names, close-event delivery, listener disposal and undecorated dialogs.
- The gallery's Playwright test checks its dialog's 460 by 170 outer bounds, owner-relative position, visible title, separation of title and content, and both close controls.
- The Rendering lab test requires its first painted frame within two seconds and checks the Area fill and hole, mouse hit testing, texture repeat, clipping, text layout, image decode success/failure/retry, buffer presentation and navigation back to geometry.
- Existing portable geometry tests compare the geometry contracts with the JDK.

The original rendering-only Chromium test took 56.2 seconds. After the fix, the targeted rendering and dialog tests together took 3.8 seconds, with the first Rendering lab frame measured at 452 ms. The rebuilt page was also opened successfully in the user's Chrome tab after reproducing a greater-than-30-second stall there before the fix.

Reviewed screenshot: [decorated Chromium dialog](../screenshots/dialog-chromium.png).

## Final validation (2026-09-07)

All 46 JVM/J2CL targets, three desktop targets, six modern-browser suites and the complete 303-target build passed. The final gallery suites measured the first Rendering lab frame at 428 ms in Chromium, 483 ms in Firefox and 607 ms in WebKit. Optimized bundle budgets, Java formatting, dependency policy and validation-tool unit tests passed. Emitted-API validation reported zero false implemented claims and zero invariant violations, with 299/299 target types and 4714/4714 members callable.

The initial concurrent browser matrix exhausted WSL's 16 GB RAM and 4 GB swap after the compiler workers accumulated. That run was cancelled, the workspace's Bazel server was shut down to release its workers, and the entire modern matrix was rerun successfully with `--local_test_jobs=1 --jobs=1 --worker_max_instances=1 --worker_max_multiplex_instances=1`. The build used the same worker limits. Validation stages were invoked directly in WSL because the PowerShell gate wrapper failed to resolve this workspace's WSL path.
