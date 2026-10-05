# Java NIO stubs for J2CL and the MPS web migration

Status: implemented source profile; validation evidence and integration limits are maintained in README.md and compatibility/*.json. The original plan below remains as design context.

## Implemented decisions

- The reference is OpenJDK 21.0.12: 163 public/protected NIO types and 1,174 non-preview members. The single foreign-memory preview mapping overload is explicitly excluded.
- J2CL 20260402 already supplies four charset classes. Its existing functionality is preserved; missing members and standard charset metadata are added.
- The build uses a root-owned, composed JRE overlay. Sources live under `src/main/super`; pinned IO and WebAWT prerequisites live under `third_party` with ownership and checksum manifests.
- `File.toPath()` is an integration-only fail-fast declaration. No concrete path or filesystem implementation was added.
- The standalone profile has a stable `//:java-nio` consumer anchor. Consumers must apply its JRE patch at their own module root.
- MPS was inventoried at revision `1ae803e4f8c52e470c94563059eca4b4b8397c8c`. An unchanged sandbox source fixture covers Readable/CharBuffer, and an unchanged production fixture records remaining non-NIO compilation blockers. This is not a full MPS module build.
- Heavy WSL work uses the shared `/tmp/j2cl-build.lock` and serial worker limits coordinated with the other projects.

## Objective and boundaries

Create a Java source compatibility library that lets the selected JetBrains MPS code and its dependencies compile through J2CL while preserving their `java.nio.*` imports and API signatures.

The runtime library will use only Java language features and APIs supported by the pinned J2CL runtime, plus explicitly owned compatibility declarations needed to close signature dependencies. Build-time tooling may use a normal JDK and scripts; those tools are not shipped to the browser.

This is a stub library. There will be no localStorage, IndexedDB, browser filesystem integration, in-memory filesystem, network backend, native code, threads, reflection, dynamic class loading, or service-provider discovery. Buffer storage, charset conversion, path resolution, and actual I/O are also outside the initial implementation. Minimal constants, exception data, and metadata objects are allowed where needed for useful declarations and safe class initialization.

The success criterion is source compatibility and predictable failure when unsupported behavior is reached. Running the complete MPS IDE is a separate migration outcome.

## Planning evidence and initial assumptions

- This repository was empty when inspected on 2026-09-08. The sibling `C:/Codex/java-io-j2cl` directory also had no implementation files.
- `C:/Codex/WebAWT/MODULE.bazel` pins J2CL `20260402`; its `.bazelversion` specifies Bazel `8.3.1`. Use these as the initial integration baseline, then verify them in phase 1.
- WebAWT already owns `java.net.URI`, `URISyntaxException`, `URL`, and `MalformedURLException` in its `uri-j2cl` target. Do not introduce duplicate definitions.
- The exact MPS revision and selected browser modules have not been supplied or audited. Use Java 21 as a provisional API reference and build JDK; confirm the required API release against MPS before generating the definitive inventory. The API reference version and transpiler language level are separate settings.
- J2CL identifies its `jre/java` sources as the authority for available runtime APIs. Inspect the pinned release, not just current upstream documentation. Its handling of replacement `java.*` classes must be proven with the actual consumer build.

## Stub behavior contract

| Declaration or operation | Initial behavior |
| --- | --- |
| Interfaces, abstract methods, nested types, generic bounds | Preserve the selected JDK public/protected API shape. |
| Enums, primitive constants, symbolic option objects | Supply actual named values and stable identities. |
| Exceptions | Preserve inheritance, constructors, causes, and relevant stored diagnostic fields/accessors. |
| Required metadata singletons such as byte-order or charset constants | Construct without I/O or provider discovery; support only the explicitly documented metadata methods. |
| Concrete computational methods and factories | Throw `UnsupportedOperationException` identifying the API member unless specifically listed as metadata support. |
| Filesystem queries, reads, writes, traversal, channel opening, watching, mapping | Throw immediately; never return fabricated success, empty results, or guessed existence. |
| Abstract base constructors needed for subclass compatibility | Permit minimal initialization without performing operations. |
| Static initialization | Remain side-effect-free and avoid calling throwing operation factories. |

Use one internal failure helper, for example `UnsupportedNio.operation("java.nio.file.Files.readAllBytes(Path)")`. Keep declared checked exceptions in signatures even though the stub throws an unchecked exception. This is an explicit stub contract, not a claim of JDK behavioral equivalence.

Do not use `null`, zero, `false`, empty streams, or no-op writes as universal method bodies. In particular, returning `false` from `Files.exists` could cause MPS to take an incorrect project-creation or recovery path.

## Phase 0: Establish the API and dependency inventory

1. Record the MPS commit, intended modules, generated sources, and dependency versions. Initially scan available source imports and fully qualified references; include bytecode references from third-party JARs and compiler diagnostics rather than treating imports as complete evidence.
2. Export the selected JDK's public/protected API for all eight packages: `java.nio`, `java.nio.file`, `java.nio.file.attribute`, `java.nio.file.spi`, `java.nio.channels`, `java.nio.channels.spi`, `java.nio.charset`, and `java.nio.charset.spi`.
3. Include constructors, overloads, generic bounds, inheritance, nested classes, constants, default methods, covariant return types, and declared exceptions. Do not include `sun.nio.*`, `jdk.internal.*`, or private JDK implementation details.
4. Compare against the pinned J2CL JRE and the compatibility libraries already present. Record one owner per fully qualified type.
5. Trace transitive signature dependencies such as `java.io.File`, streams/readers/writers, `java.net.URI`, `java.lang.Readable`, `java.time` types, and concurrency/network types exposed by channel APIs. Verify presence at member level, not merely class level.

Deliverables: `compatibility/jdk-nio-api.json`, `compatibility/mps-nio-usage.json`, and `compatibility/type-ownership.json`. Every reference is classified as upstream-provided, planned stub, deliberately excluded, or blocked by another compatibility type. Record unknown MPS coverage explicitly if its sources are still unavailable.

Exit criterion: a reproducible target API inventory with a known dependency closure. Broad public NIO coverage is the target; MPS usage determines implementation order. Any signature that cannot yet be supplied remains a visible gap rather than being weakened to `Object` or silently omitted.

## Phase 1: Prove J2CL integration before generating the library

1. Add a pinned Bazel module and a minimal `j2cl_library`, following the existing WebAWT build pattern. Expose one stable consumer label, proposed as `@java_nio_j2cl//:java-nio`.
2. Compile a representative NIO declaration and a separate consumer that imports and calls it. Produce optimized Closure JavaScript and execute the consumer's caught failure in a browser. This verifies that the stub implementation survives linking and is the implementation actually used.
3. Extend the proof with a signature requiring an external type, and one involving a type in a package already owned by J2CL, such as the `Readable`/`CharBuffer` relationship if missing in the pinned JRE.
4. Resolve Java module/bootclasspath restrictions using the supported build pattern for the pinned toolchain. If a narrowly scoped JRE overlay or toolchain adjustment is required, document and pin it before proceeding. Do not assume ordinary JVM compilation with `--patch-module` establishes J2CL compatibility.
5. Resolve NIO/IO dependency cycles explicitly. For example, `File.toPath()` and `Path.toFile()` can require joint compilation. Keep source ownership separate while using an acyclic foundational target or a combined compatibility compilation target where necessary. Do not create circular Bazel dependencies.
6. Reuse the existing URI target where practical, with an explicit owner. Any later extraction into a shared compatibility module should be a coordinated change, not a second copy in this repository.

Exit criterion: a real Java-to-JavaScript-to-browser consumer proof, with the dependency and duplicate-type checks passing. This phase is the main feasibility gate.

## Phase 2: Implement the declarations in dependency order

| Order | Package group | Coverage |
| --- | --- | --- |
| 1 | Shared declarations | Exception hierarchies, option enums, marker interfaces, metadata constants, and the failure helper. |
| 2 | `java.nio` and `java.nio.charset[.spi]` | Buffer hierarchy and byte order; charset, encoder/decoder/result declarations; charset provider API. Handle their signature dependencies together where needed. No buffer or encoding engine. |
| 3 | `java.nio.file.attribute` | Attribute interfaces/views, permission enums, principals, `FileTime`, and exceptions, including external signature types. |
| 4 | `java.nio.file[.spi]` | `Path`, `Paths`, `Files`, filesystem/provider/store APIs, directory streams, visitors, matchers, watch APIs, options, and exceptions. All actual operations fail explicitly. |
| 5 | `java.nio.channels[.spi]` | Channel interfaces, file/seekable channels, locks, adapters, selectors, pipes, socket and asynchronous channel declarations and providers, subject to explicit signature dependency closure. No resources, polling, callbacks, or execution engine. |

Generate repetitive signatures and failure bodies from the pinned inventory where this is reliable. Review inheritance, constructor chains, checked exceptions, and initialization manually. Preserve factory constants such as standard charset objects without accidentally invoking unsupported conversion or lookup code during class initialization.

Some NIO groups have mutual type references. Use a single compilation target initially if needed; split targets only along proven acyclic boundaries. Package layout does not require one Bazel target per Java package.

Exit criterion: every targeted API member is represented by an upstream implementation, a compilable stub, or a precise documented blocker. A milestone with blockers is partial coverage, not API completion.

## Phase 3: Verify the artifact and the MPS boundary

- **API checks:** Compare generated declarations with the reference inventory at member level. Compile consumer probes covering overload resolution, generic assignments, subclass overrides, nested types, checked-exception catches, and try-with-resources.
- **J2CL linkage:** Transpile the complete stub source set and build an optimized consumer. Exercise representative exported APIs so Closure dead-code removal cannot conceal all runtime linkage problems.
- **Browser behavior:** Verify constants and exception metadata initialize correctly, and representative file queries, reads/writes, buffer/charset factories, watch services, and channel operations fail with the documented exception and member identifier. Check that no storage or network effects occur.
- **Ownership and dependency checks:** Reject duplicate `java.*` definitions and undeclared dependencies. Audit source and build dependencies for JVM-only facilities; compiler success is necessary but not sufficient evidence.
- **MPS compilation:** Compile the selected MPS modules and their real dependencies using the public artifact. Separate remaining NIO signature gaps from unrelated IntelliJ platform, Swing, reflection, classloading, threading, and persistence issues.
- **Reproducibility:** Build a separate consumer from a clean checkout using pinned dependencies. Keep local sibling overrides in a documented development setup; the published artifact must have a usable dependency configuration.

Use J2CL/browser execution as the authority for stub runtime behavior. Ordinary JVM tests may accidentally exercise the JDK's own `java.nio` implementation and must not count as stub validation. JVM tools are useful for extracting signatures and compiling reference probes.

Exit criterion: the declared API profile passes its compatibility checks and browser failure tests. Claim MPS compilation support only for the exact revision/modules actually tested. No claim of functional MPS startup or persistence follows from these results.

## Phase 4: Document and package

Provide a README with the dependency label, supported API/J2CL versions, build commands, a minimal consumer example, the failure contract, the compatibility matrix, and explicit external blockers. Include source/license provenance; prefer signature-driven stub bodies rather than copying OpenJDK implementations.

Suggested repository layout:

```text
MODULE.bazel
BUILD.bazel
.bazelversion
src/main/java/java/nio/                 # Includes the seven NIO subpackages
src/main/java/org/javanioj2cl/internal/ # Shared failure helper
src/test/java/                         # J2CL/browser behavior tests
src/test/consumer/                     # Signature and integration probes
compatibility/                         # Reference, usage, ownership, coverage
tools/                                 # Inventory generation and validation
README.md
```

The first implementation milestone is the minimal browser consumer proof. The next is the MPS-prioritized declaration set; the final milestone is the declared NIO profile with reproducible validation. Lexical path behavior, real buffers, charset conversion, or any storage implementation can be separate future work if MPS execution requires them.

## Reference sources

- [J2CL emulation limitations and runtime source of truth](https://github.com/google/j2cl/blob/master/docs/limitations.md).
- [J2CL JRE build configuration](https://github.com/google/j2cl/blob/master/jre/java/BUILD), illustrating module and bootclasspath concerns; implementation must inspect the pinned release.
- [J2CL discussion on external JRE/NIO support](https://github.com/google/j2cl/discussions/239), including the `Readable`/`CharBuffer` integration concern. Treat this as a risk to verify, not a guarantee about the selected release.
- [Java 21 NIO API overview](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/nio/package-summary.html).
- [Java 21 filesystem API](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/nio/file/package-summary.html).
