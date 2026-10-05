# Implementation plan

This plan implements an intentionally narrow compile-and-test profile for the
MPS web migration. It is not a browser storage project.

1. **Freeze the baseline.** Pin Bazel, J2CL, Closure rules, Java, and the list of
   `java.io` types already supplied by J2CL.
2. **Inventory MPS.** Scan tracked Java and Kotlin sources at a recorded Git
   revision and retain per-type counts and file paths.
3. **Add only missing types.** Patch declarations into J2CL's own `java.base`
   source tree, preserving ownership of stock J2CL classes.
4. **Separate behavior tiers.** Implement useful in-memory protocols, keep
   `File` lexical, and make filesystem, object serialization, and blocking pipe
   boundaries fail explicitly.
5. **Verify under J2CL.** Compile and run browser-targeted tests for the useful
   tier and negative tests for unsupported facilities.
6. **Coordinate cross-project ownership.** Serialize cold WSL builds and defer
   `File.toPath`, `Path.toFile`, and `File.toURI` to a consumer-owned composed
   patch.
7. **Publish reproducibly.** Generate the J2CL patch and source archive from the
   canonical overlay, with drift checks in validation.

Progress and evidence are maintained in `IMPLEMENTATION_STATUS.md`.
