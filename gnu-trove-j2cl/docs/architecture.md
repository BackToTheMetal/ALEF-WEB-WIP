# Architecture

The upstream implementation is the compatibility facade and backend. Inspection
showed that its protected arrays, hashing strategies, capacity controls, and
iterator behavior are observable. Preserving that Java implementation provides
better compatibility than translating everything into `java.util.HashMap`.

`THash` owns size/capacity/compaction state. `TPrimitiveHash` and primitive hash
bases own occupancy and key arrays; primitive-to-object maps have their own
layout. `TObjectHash` supplies strategy-aware object probing. Concrete maps,
sets, lists, views, decorators, and callbacks preserve the pinned source API.

`ArraySupport` performs explicit null/bounds checks before J2CL array copies.
`FloatSupport` normalizes float32 using emulated bit conversion. Concrete clone
methods explicitly copy storage; the abstract-base cloning boundary is described
in the exclusions report. Production code uses only J2CL's emulated JRE.

`tools/port_sources.py` reproduces the modifications from a checksummed upstream
source JAR; helpers are maintained separately. Primitive family tests are
generated from independent array-model scenarios, not from implementation code.
The original binary executes in a separate JVM classpath and cannot enter the
browser build. Build-time reflection compares actual compiled signatures.

`compatibility/mps-required.json` selects the production source units. Its roots
come from pinned MPS Java/Kotlin, serialized model references, and reviewed
generator name construction; complete implementation and signature dependencies
are retained. `tools/mps_requirements.py` recomputes this closure offline and
rejects missing or extra sources. API comparison uses the same retained scope.

The public Bazel targets are `//:trove-j2cl` for J2CL consumers and `//:trove` for
JVM tests/consumers. Test fixtures, JavaScript entry points, and browser tooling
are separate. The browser harness executes development and optimized artifacts
with normal JRE checks; it compares deterministic traces to the original JVM
and separately executes intentional-correction regressions.

`tools/validate.py` owns the shared build lock for the complete validation run.
It calls the consumer in-process to avoid acquiring the same lock recursively.
The standalone consumer acquires the lock itself. Bazel lifetime is scoped with
`try/finally` cleanup, including failure and interruption paths. API exceptions
use an exact checked-in signature allowlist, not blanket method-name exemptions.
