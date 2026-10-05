# Implementation status

## Implemented

- Bazel/J2CL baseline pinned to Bazel `8.3.1`, J2CL `20260402`,
  rules_closure `0.16.0`, and Java 21.
- Canonical JRE overlay containing 30 new `java.io` types and two prerequisite
  `java.lang` exception types.
- Deterministic patch generation and a source-policy check that rejects stock
  J2CL duplicates and browser/native storage dependencies.
- MPS source inventory at revision
  `1ae803e4f8c52e470c94563059eca4b4b8397c8c`: 29,907 tracked Java/Kotlin
  files and 45 distinct import/qualified-name tokens. Two qualified tokens are
  comments/Javadoc (`Inputstream` and `WinNTFileSystem`), not API requirements.
- J2CL consumer probes for lexical paths, data streams and modified UTF,
  charset decoding, character arrays, and explicit unsupported boundaries.
- Shared WSL locking and source ownership contract with `java-nio-j2cl`.

## Verification

- Source policy: passed for all 32 added JRE types; every source is free of the
  forbidden storage/native dependencies.
- MPS type coverage: passed for all 42 named API references after excluding the
  two recorded comment-only tokens. Wildcard imports were inspected separately.
- Generated J2CL patch drift: passed. Patch SHA-256 is
  `c0b1b579b6c5fd654ca338940ae64f3ce910f4a1c874693cb5d3488997927d9b`.
- J2CL compile/tests: passed. The patched JRE, consumer library, Closure output,
  and five browser-targeted test scenarios compiled; the test target passed
  1/1. The cold serial build completed 1,261 actions in 570 seconds.
- Reproducible source archive generation and byte-for-byte recheck: passed.
- Cross-project composition: `java-nio-j2cl` consumed the final IO patch SHA in
  its integration-only JRE patch. Its emitted API check covered 163 NIO types
  and 1,174 non-preview members, 56 optimized browser checks passed, and its
  separate archive consumer built and ran successfully. It did not modify the
  canonical IO or WebAWT sources. Both Bazel servers were shut down and the
  shared WSL lock was released after validation.

## Deliberately unsupported

- Persistent or virtual browser filesystem semantics.
- `localStorage`, IndexedDB, File System Access API, and native calls.
- JVM object serialization and thread-blocking pipes.
- Cross-owned `File.toPath`, `Path.toFile`, and `File.toURI` methods until a
  combined IO/NIO/WebAWT consumer patch is validated.
