# Phase 1 execution status

## Current slice

The curved-path foundation slice is complete. It adds mutable paths, connected path composition, quadratic and cubic curves, recursive flattening, transformed path creation, curve utilities, and browser-compatible hit testing without external runtime dependencies.

## Completed

- Added the complete public member surface of `Dimension`, `Insets`, and `Dimension2D` from the generated OpenJDK inventory.
- Implemented constructors, mutable fields, copying, floating-point rounding, equality, hash codes, string representations, cloning and size mutation.
- Added dependency-free scenarios for `Dimension`, `Insets`, and `Point`, compiled unchanged against stock OpenJDK AWT and WebAWT under J2CL.
- Verified special numeric conversion behavior for `NaN` and positive infinity.
- Added a root `//:all-tests` suite covering portable JVM tests, J2CL tests, the existing point tests and Elemental2 DOM integration.
- Updated the compatibility manifest for the initial five point, dimension, and insets types.
- Consolidated mutually recursive `java.awt` and `java.awt.geom` sources into one J2CL compilation target, documented by ADR 0005.
- Implemented the complete public surfaces of `Shape`, `PathIterator`, `Rectangle`, `RectangularShape`, `Rectangle2D`, `Line2D`, `AffineTransform`, `IllegalPathStateException`, and `NoninvertibleTransformException`.
- Added concrete float and double point, line, and rectangle variants with path iteration and type-preserving cloning for the built-in variants.
- Added differential scenarios for rectangle boundaries, negative dimensions, integer overflow, intersection, union, growth, outcodes, line relationships, distance calculations, path iteration, affine composition, inversion, array overlap, transform classification, transformed shapes, and numeric string formatting.
- Implemented the complete 117-member top-level API inventory for `Path2D`, `GeneralPath`, `FlatteningPathIterator`, `QuadCurve2D`, and `CubicCurve2D`, including their public float and double variants.
- Added path storage with float-specific rounding, winding rules, append/connect behavior, transformed iteration, bounds, trimming, reset, current-point tracking, and type-preserving clones for all built-in path variants.
- Added adaptive de Casteljau flattening with configurable flatness and recursion limits, plus curve flatness, subdivision, real-root solving, iterators, bounds, and shape queries.
- Replaced the affine transformed-shape wrapper with `Path2D.Double`, so transformed curved paths use the shared path and flattening machinery.
- Added differential coverage for segment order, winding containment, append behavior, path transforms and clones, recursive flattening, curve subdivision, polynomial roots, float precision, and exceptional states.

## Known limitation

Stock J2CL omits `Object.clone`. Abstract geometry bases therefore preserve values but cannot generically preserve the runtime class of an unknown user subclass. Built-in concrete variants override cloning and preserve their type. The affected base-class members are classified as limited.

Curved-path point and rectangular hit testing uses adaptive flattening with a `0.0000001` geometry-unit tolerance and a recursion limit of 16. Results can differ from OpenJDK for points within that tolerance of a curve or for pathological coordinate ranges. The affected members are classified as limited.

`CubicCurve2D.solveCubic` implements analytic real-root solving. Extreme ill-conditioned coefficients can differ from OpenJDK's root-refinement behavior, so the two solving members are classified as limited.

`AffineTransform.createTransformedShape` now returns a transformed `Path2D.Double`; its hit testing inherits the curved-boundary tolerance.

## Verification

- `bazelisk test //:all-tests --nocache_test_results`: the root suite includes six JVM and J2CL test targets.
- The shared portable scenarios passed against both OpenJDK AWT on the JVM and WebAWT under J2CL.
- The geometry adapter exposes 22 independently reported J2CL test methods, backed by the same scenarios used by the JVM target.
- `bazelisk build //...`: all 41 targets built, including the optimized Closure application.
- The Java source-structure check passed all 29 current Java files, and generated JSON plus Checkstyle XML parsed successfully.
- The compatibility report records 19 implemented public types and 410 implemented public members.

## Next slice

Implement the remaining primitive shapes: `Ellipse2D`, `Arc2D`, and `RoundRectangle2D`. Reuse the curved-path foundation for their iterators, transforms, flattening, bounds, and hit testing, then add differential vectors around arc closure modes and boundary behavior.
