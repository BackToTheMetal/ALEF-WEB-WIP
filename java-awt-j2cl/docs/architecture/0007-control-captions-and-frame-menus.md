# ADR 0007: Control captions and frame menu layout

## Status

Accepted for the highlighted dual-runtime gallery corrections.

## Decision

AWT component bounds describe the complete checkbox or label, including its text. A checkbox browser peer owns a semantic HTML label containing a native input and a visible caption. Only the wrapper receives layout bounds; the input retains a compact size and receives native focus and disabled state. Grouped checkboxes use radio inputs with an internal group name. Java remains authoritative for group selection, including group reassignment and programmatic updates. Caption activation delivers one item event per state change, and disposal removes the registered listeners.

Labels center text vertically and honor LEFT, CENTER, and RIGHT alignment within their allocated bounds. Label and checkbox preferred sizes use the effective AWT font and the existing approximate FontMetrics implementation. DOM font conversion maps Java logical fonts to CSS generic families, and parent font changes refresh inheriting children. This does not claim exact desktop text measurement.

A frame reserves its menu bar's measured height through getInsets. Before a DOM measurement is available, font metrics provide a deterministic estimate. The bar spans the frame and may wrap at narrow widths; font changes, menu replacement, and frame resizing invalidate and lay out the content. The designated Help menu occupies the right edge, with its popup opening toward the interior. Menu rendering does not automatically take focus when a frame is mounted.

All browser-specific details remain behind internal peer interfaces. No Elemental2 types or new browser-specific public methods are added to java.awt. Existing partial support classifications remain in place.

## Validation

Shared JVM and J2CL checkbox tests check group reassignment and programmatic event behavior. DOM integration tests cover captions, focus, runtime font and alignment changes, menu insets, resizing, replacement, and disposal. The optimized gallery runs pointer and keyboard checks in Chromium, Firefox, and WebKit, with a controls screenshot saved as a Bazel test artifact.

## Bundle cost

The small showcase grows from 163,233 to 169,193 raw bytes and from 53,268 to 54,959 deterministic gzip bytes. The 1,691-byte compressed increase (3.2 percent) covers the new caption peers, font conversion, and menu layout behavior; no production dependency was added. Its previous compressed budget had only 44 bytes of headroom. The revised limits are 168 KiB raw and 55 KiB gzip. The dual showcase remains within its existing limits at 240,174 raw bytes and 79,440 gzip bytes.
