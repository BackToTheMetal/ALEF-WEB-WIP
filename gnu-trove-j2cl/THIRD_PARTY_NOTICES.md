# Third-party source and artifacts

The `gnu.trove` implementation is adapted from JetBrains Trove
`org.jetbrains.intellij.deps:trove4j:1.0.20200330`. The original source and binary
JARs are retained under `third_party/` with checksums in
`compatibility/baseline.json`. Upstream copyright headers are retained.

Trove is distributed under LGPL 2.1 or later; see `LICENSE` and
`third_party/TROVE-README-license.txt`. `HashFunctions` and `PrimeFinder` also
carry the CERN permission notice reproduced in that file and their source.
This port's modifications and helper code are distributed under LGPL 2.1 or
later. Source regeneration is provided in `tools/port_sources.py`.

Modifications: remove JVM object-stream hooks and their helper; replace
reflective typed-array allocation; implement explicit collection cloning;
preserve Java array-copy errors and float32 boundaries; fix list extrema,
clone value-array aliasing, and compaction during
retain operations; correct primitive bulk assignment and shuffle choices; and
correct null-valued key removal and aliased map-view bulk removal.
Detailed compatibility differences are recorded in
`compatibility/exclusions.md`.
Only the pinned MPS source/model dependency closure is emitted; unused stack
and intrusive-list sources are excluded from the production tree.

The unchanged MPS test fixtures under `tests/mps/source/` are from JetBrains/MPS
commit `fb6524e4726c73081d1e0934b54da5a0f78e7d05`, under Apache License 2.0.
See `third_party/MPS-LICENSE.txt` and per-file URLs/checksums in the baseline.
Source/model excerpts in `compatibility/mps-required.json` are from that same
revision and license; the inventory records their paths, lines and file hashes.
Test-only model boundary values under `tests/mps/support/` are project code;
they are not MPS runtime implementations or production library dependencies.

J2CL, Bazel rules, and Playwright are external build/test dependencies, pinned
in `MODULE.bazel`, `MODULE.bazel.lock`, and `package-lock.json`. Neither the
reference Trove JAR nor the MPS fixtures are dependencies of the library.
