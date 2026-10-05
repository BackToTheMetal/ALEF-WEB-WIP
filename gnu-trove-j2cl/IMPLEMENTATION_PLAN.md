# GNU Trove compatibility for J2CL

Status: the JetBrains 1.0.20200330 library profile is implemented and tested.
The plan below is the original design, not a claim that every broader MPS
release gate is complete. See [VALIDATION.md](VALIDATION.md) for completed
checks and remaining acceptance work, [README.md](README.md) for usage, and
[compatibility/exclusions.md](compatibility/exclusions.md) for the exact boundary.

## Objective and compatibility contract

Provide source-compatible `gnu.trove` collections that MPS and its generated Java can compile with J2CL and execute in a browser. Existing supported imports, constructors, method calls, callbacks, and collection behavior should remain usable when the original Trove dependency is replaced.

The runtime implementation must use Java language features and JRE APIs provided by the pinned J2CL distribution. Build tools, API inspection, and the original JVM reference implementation may run outside J2CL, but must never enter the browser dependency graph.

Compatibility means matching the selected Trove version's observable behavior for the published supported API. It does not mean JVM binary compatibility, identical hash-table layout, or a complete MPS web port. MPS's other platform dependencies remain separate migration work.

The working directory was empty when this plan was prepared. During implementation,
MPS revision `fb6524e4726c73081d1e0934b54da5a0f78e7d05` was pinned for the selected
node-map and identity-strategy fixtures. Its full workbench and separately bundled
Trove 2.1.0 are outside this validated scope. Candidate class examples below retain
their original planning context; the implementation and evidence are recorded in
the compatibility reports and validation record.

## Decisions

1. **Pin the actual MPS dependency first.** JetBrains maintains a distinct, now obsolete Trove fork; do not substitute the Trove 3 API based on the library name. Identify packages, artifact coordinates, version, source revision, and checksum from the selected MPS distribution/build. JetBrains also documents changes to MPS's exposure of Trove stubs, so include model descriptors and generated source in the inventory. Sources: [JetBrains Trove fork](https://github.com/JetBrains/intellij-deps-trove4j), [MPS migration guide](https://www.jetbrains.com/help/mps/migration-guide.html).
2. **Retain original package and class names.** Exclude the original Trove JAR from replacement compilation and browser dependencies. Do not load two definitions of the same class together.
3. **Use a hybrid implementation.** Delegate ordinary operations to J2CL's emulated Java collections where their behavior matches. Use strategy-aware storage and primitive arrays where delegation would change equality, sentinel behavior, capacity contracts, or required performance. Decide the backend per family after reference tests exist.
4. **Deliver MPS requirements first, with explicit coverage.** Inventory the complete pinned Trove API; implement the dependency closure needed by the selected MPS browser modules first, then the remaining portable families. Every published class gets its complete declared portable API, including inherited members. Do not claim all of Trove is supported while unimplemented portable families remain.
5. **Exclude unavailable capabilities at build time.** Unsupported serialization, reflection, or thread-related signatures must be listed as exclusions and absent from the browser profile. If selected MPS code needs one, record and resolve the consumer migration before accepting that MPS scope. Do not hide the gap with empty methods or runtime failure stubs. This follows [J2CL's guidance for compatible replacements](https://github.com/google/j2cl/blob/master/docs/best-practices.md).

## Phase 0 — Freeze scope and extract contracts

Tasks:

- Select the MPS commit/release and browser module set. Resolve its Trove artifacts, including dependencies of retained IntelliJ utilities and generated language runtimes. Inspect any existing local or upstream J2CL-compatible Trove implementation before creating a new backend.
- Record exact MPS, Trove, J2CL, Bazel, JDK, and browser toolchain versions. The adjacent WebAWT project currently declares J2CL `20260402`; use it as an integration candidate and verify compatibility before adopting it here.
- Extract the pinned Trove API from source and/or bytecode using build-time tools. Record package names, visibility, class hierarchy, interfaces, generic bounds, constructors, overloads, checked exceptions, constants, and public/protected members.
- Inventory MPS references in Java, generated Java, `.mps` models, `.msd`/`.mpl` descriptors, and retained third-party consumers. Text search seeds the inventory; compilation/symbol resolution establishes actual constructor, method, and inherited-member use.
- Detect subclassing, overridden protected methods, and direct backing-array access. These may require preserving upstream hierarchy and storage contracts rather than simple composition.
- Inspect upstream source and tests for each family, especially behavior that differs from `java.util`.
- Assign every API member a status: required by MPS, portable later, or excluded with a reason and consumer impact. Keep unknowns visible until resolved.
- Preserve source provenance and applicable notices for reused code, templates, and tests in `THIRD_PARTY_NOTICES.md` and dependency metadata.

Deliverables:

- `compatibility/baseline.json`: exact dependency identities and checksums.
- `compatibility/api.json`: complete upstream signature inventory and implementation status.
- `compatibility/mps-usage.json`: consumer locations and required API closure.
- `compatibility/exclusions.md`: unavailable capabilities and concrete migration actions.
- `docs/architecture.md`: chosen backend per collection family and hierarchy requirements.

Exit gate: no unidentified Trove artifact or unclassified Trove use in the selected MPS scope. Scope is fixed before generating the library surface.

## Phase 1 — Establish the J2CL build and reference harness

Create a Bazel module with separate targets for the replacement JVM library, replacement J2CL library, JVM reference runner, and browser tests. Share portable library and contract-test source wherever possible.

Proposed layout:

```text
MODULE.bazel
BUILD.bazel
src/main/java/gnu/trove/             # Exact pinned public package layout
src/main/java/.../internal/         # Internal implementations as appropriate
src/test/java/.../contract/         # Shared behavior suites
tests/reference/                   # Original Trove, isolated JVM execution
tests/differential/                # Trace generation and comparison
tests/browser/                     # Development and optimized browser runners
tests/consumer/                    # Separate dependency consumer
tests/mps/                         # Pinned real MPS source fixtures
tools/                             # API checks, generation, validation
compatibility/
templates/                         # Primitive families, if justified
```

Build a small portability probe for the exact APIs needed: Java collections, iterators, identity hashing, primitive arrays, array copying, typed `toArray`, numeric hashing, and callback interfaces. Verify these against the pinned J2CL JRE source and an executed JavaScript test. Do not assume JVM availability implies J2CL availability.

Reference comparison uses three isolated executions:

1. The original pinned Trove artifact on the JVM.
2. The replacement implementation on the JVM.
3. The same replacement compiled with J2CL and executed in browsers.

Compile common scenarios separately against each implementation; the two `gnu.trove` implementations never share a classpath. A neutral trace format records operations, return values, exceptions, and final contents. Encode longs as decimal strings, floating-point special values explicitly, and test objects with stable fixture IDs. Do not compare process-specific identity hash numbers.

Exit gate: one end-to-end collection scenario produces equivalent results in all three executions, including an optimized JavaScript build. A dependency check confirms no original Trove runtime dependency reaches J2CL.

## Phase 2 — Object collections and equality strategies

Candidate starting surface, subject to Phase 0: `THashMap`, `THashSet`, `TObjectHashingStrategy`, and their procedures, functions, iterators, and required base classes.

Implementation:

- Use composition over `java.util.HashMap`/`HashSet` for ordinary equality only when the upstream contract permits it. Preserve any superclass/interface relationships required by source compatibility.
- For custom strategies, use explicit strategy-aware hashing and equality. Prefer a small shared table core if wrapping keys would leak wrappers through views or mis-handle strategy semantics.
- Preserve the reference implementation's null policy; do not inherit a delegate's null behavior by accident.
- Implement backed key/value/entry views, mutation through views, iterator removal, and entry value replacement wherever the original API supports them.
- Match procedure short-circuiting and supported transform/retain operations. Preserve custom strategies through copies and clones.
- Implement observable capacity/load-factor/compaction methods and their argument validation. A delegate that cannot honor those contracts is not suitable for that API.
- If MPS subclasses depend on protected storage or hooks, adapt the corresponding upstream algorithms into J2CL-compatible Java and cover those subclass contracts explicitly.

Exit gate: all portable members of each published object collection pass signature, contract, and differential tests, including deliberate hash collisions and nonstandard equality strategies.

## Phase 3 — Primitive collections

Order the work by the measured MPS dependency closure. Likely candidates include `TObjectIntHashMap`, `TIntObjectHashMap`, `TIntIntHashMap`, `TIntHashSet`, and `TIntArrayList`; their presence and exact signatures must be established in Phase 0. Add long and other families when required, then complete the remaining portable API inventory.

Implementation:

- Implement primitive lists with growable primitive arrays and explicit public index/range checks where the reference API promises them.
- Implement maps/sets with primitive storage where useful, using separate occupancy state so zero, negative values, and any configured no-entry value remain valid data when the reference permits it.
- A boxed Java collection backend is acceptable only after contract tests show equivalent behavior and browser measurements meet the agreed workload limits.
- Keep `long` keys/values as Java `long`; never convert keys to JavaScript numbers or `double` for lookup or comparison.
- Preserve the selected version's absent-value results and supported adjustment operations; do not import newer Trove methods or configurable sentinels into an older API without evidence.
- If implementing open addressing, cover collision probing, deleted slots, growth, compaction, iterator state, reference clearing on removal, and bounded termination under repeated insertion/deletion.
- Generate repetitive primitive specializations from reviewed templates when the measured scope warrants it. Check generated output for reproducibility and compile every generated family. Keep semantic edge-case tests independently authored.

J2CL reports differences in array bounds behavior and `float` precision. Add explicit checks where required, and characterize float hashing, equality, arithmetic, and stored-value rounding before publishing float families. If exact behavior needs an unavailable operation, record a compatibility blocker and a consumer migration; do not silently claim JVM equivalence. Sources: [J2CL limitations](https://github.com/google/j2cl/blob/master/docs/limitations.md), [J2CL data semantics](https://github.com/google/j2cl/blob/master/docs/semantics.md).

Exit gate: every released primitive specialization passes the same contract matrix plus type-specific boundaries in JVM and browser execution.

## Phase 4 — Complete collection behavior and the portable surface

Finish any remaining portable methods and classes identified in the baseline, including decorators, wrappers, utility methods, and base classes where present. For each class, verify:

| Area | Required cases |
| --- | --- |
| Construction | Default/copy/array constructors, capacities, load factors, invalid arguments, strategy retention |
| Lookup and mutation | Empty state, insertion, replacement, old-value returns, removal, clear, duplicate keys, absent versus stored sentinel/null |
| Equality and hashing | Equal distinct keys, collisions, custom and identity strategies, wrong-type arguments, collection equality/hash rules |
| Iteration | Empty/exhausted iterators, cursor/advance contracts, removal before/after advance, repeated removal, replacement versus structural modification |
| Views | Backing behavior, duplicate values, mutation from both sides, entries after changes, supported and unsupported view operations |
| Arrays and lists | Empty/exact/oversized destination arrays, runtime component type, ordering, return identity, aliasing, insertion/removal ranges, bounds, overlap |
| Bulk operations | Empty inputs, duplicate inputs, self-input, aliased views, retain/remove, callback short-circuiting, callback exceptions and partial effects |
| Storage lifecycle | Resize, compact, trim, repeated deletion/reinsertion, full collision chains, cleared references, observable capacity contracts |
| Numeric behavior | Integer overflow, min/max values, distinct longs above 2^53, infinities, NaN, signed zero, float rounding where relevant |
| Copy/clone | Independent storage and views, shallow element copying, equality strategy and configuration preservation |

Match the reference contract for concurrent modification and fail-fast behavior; do not simply assume a Java delegate's policy matches. Browser execution is single-threaded, but reentrant callbacks and mutation during iteration still require correct behavior.

Do not compare unspecified hash traversal order between implementations. Normalize unordered contents while retaining multiplicities. For order-sensitive callbacks, verify valid traversal properties and side effects; compare exact order only where the API guarantees it. If MPS relies on unspecified order, expose that dependency and make the consumer deterministic or explicitly support the required behavior.

Serialization and synchronized wrappers need individual classification. A serializable marker, if available, does not provide object-stream functionality; single-thread execution does not establish a JVM locking contract. Required unavailable functionality blocks acceptance of that consumer scope until migrated.

Exit gate: the published API contains no placeholders, unexplained differences, or missing portable methods. Deferred families remain visibly outside the release claim.

## Phase 5 — Differential and browser verification

Use four complementary layers:

1. **Contract tests:** independently specified examples and invariants for every supported overload and semantic category above. Adapt relevant upstream tests with provenance.
2. **Differential tests:** deterministic operation sequences compared across original JVM, replacement JVM, and replacement JavaScript. Check intermediate results as well as final state. Persist and minimize failing seeds into focused regressions. Include several key distributions, custom strategies, and mutation-heavy workloads.
3. **API/consumer compilation:** compare actual compiled replacement signatures with the baseline and compile MPS-shaped usages of generics, overloads, inheritance, callbacks, and arrays. Check negative fixtures for intentionally excluded APIs. File presence is insufficient.
4. **Browser execution:** run Chromium, Firefox, and WebKit against development and production-optimized artifacts. Execute correctness tests against the exact Closure/JRE-check configuration intended for release. Use explicit completion/failure signals, timeouts, and captured errors.

Add bounded workload tests for expected MPS model sizes: node-ID lookup, identity-based caches, graph traversal/visited sets, and repeated incremental updates. Measure browser latency, throughput, bundle size, and memory where reliably available. Establish numerical acceptance limits using representative models before release; avoid arbitrary JVM-versus-JavaScript performance promises.

Run the full deterministic release corpus across all browser engines; use a larger seeded campaign in scheduled validation with recorded seeds. Do not accept persistent test skips as coverage.

Exit gate: zero unexplained supported-contract mismatches, all API gates pass, and both JavaScript build modes pass in all three engines.

## Phase 6 — Validate real MPS consumers and package the library

- Compile selected real MPS sources and generated Java against the wrappers, replacing the original dependency. Pin fixture provenance and source revisions.
- Exercise collection-dependent behavior from those sources: for example, model/node indexing, reference lookup, identity caches, graph traversal, or generated-language collection operations, selected from actual usages.
- Compare the JVM reference consumer with the J2CL consumer's observable results. Synthetic collection benchmarks supplement this gate; they do not replace real MPS fixtures.
- Track non-Trove blockers independently and state the precise validated MPS module boundary. Do not claim the whole MPS workbench runs from these tests.
- Package source inputs and Bazel targets suitable for J2CL; a JVM JAR alone is insufficient. Document dependency replacement and duplicate-class prevention.
- Validate a clean, separate consumer using an immutable release archive or revision and checksummed dependencies, without a local checkout override. Include a small reproducible J2CL example.
- Provide one validation entry point with JVM, API, J2CL, differential, and browser stages, plus usage documentation and the generated compatibility report.
- Use Linux CI and a Windows-to-WSL validation script consistent with the adjacent projects. Keep cold Bazel validation resource-bounded and sequential when necessary.

Exit gate: real selected MPS consumers compile and execute correctly in JavaScript, and an independent clean consumer can reproduce the result.

## Release acceptance

A release may claim compatibility only when:

- The exact Trove baseline and MPS scope are recorded and reproducible.
- Every Trove reference in that MPS scope is supported or has a completed, tested consumer migration.
- Every published portable API member has signature coverage and behavioral tests; the complete upstream inventory clearly identifies anything outside the release.
- Original JVM, replacement JVM, and J2CL behavior agree for supported contracts across deterministic and randomized tests.
- Development and production artifacts pass Chromium, Firefox, and WebKit execution.
- The runtime dependency graph uses only the pinned J2CL functionality and project collection code.
- Workload acceptance limits and independent consumer validation pass.
- Sources, notices, exclusions, compatibility evidence, and reproduction commands accompany the release.

Implement Phases 0 and 1 first, then take one object map and one primitive map through the full test path before expanding families. This validates both the wrapper architecture and the correctness harness before repetitive implementation work begins.
