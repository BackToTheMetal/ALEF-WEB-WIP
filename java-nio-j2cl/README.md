# Java NIO source stubs for J2CL

Java 21 `java.nio.*` declarations for the JetBrains MPS web migration, targeting J2CL `20260402` and Bazel `8.3.1`.

This is a source compatibility profile. NIO operations fail with `UnsupportedOperationException` and the affected member name. It does not implement a filesystem, buffer storage, charset conversion, networking, threads, selectors, file watching, localStorage, IndexedDB, or other browser persistence.

## API profile

The reference contains **163 public/protected types** across all eight NIO packages. The emitted JRE API gate checks **1,174 non-preview declared fields, constructors, and methods**, including nested types and generic signatures. Java 21's preview `FileChannel.map(..., Arena)` foreign-memory overload is excluded and recorded in the API report. External signature-support types are deliberately limited; the gate does not establish their complete inherited APIs.

Enums, byte-order constants, watch event kinds, charset result/error metadata, and exception diagnostic fields are usable. Other newly supplied concrete operations fail immediately. Abstract declarations remain abstract so consumers can implement their own subclasses.

J2CL's existing UTF-8 and ISO-8859-1 functionality remains in place. Missing charset API members are added around it. The additional standard charset constants provide names and identities; attempting their encoding/decoding fails explicitly. `Files.exists`, `Files.notExists`, listing, traversal, reads, and writes all fail rather than returning fabricated filesystem state.

## Build and validate

From PowerShell:

```powershell
.\tools\validate.ps1
```

Or inside WSL Ubuntu with Java 21, Python 3, Bazelisk, and Chromium installed:

```bash
bash tools/validate.sh /path/to/chromium
```

Without the argument, validation looks for an existing Playwright-managed Chromium installation and prefers its dedicated headless shell. That runner passes the WSL checks; the full cached Chromium executable timed out on the same file harness. Windows Chrome also passed the optimized runtime checks. The Python tools use only the standard library; Playwright itself is not required.

Every heavy WSL build must hold the shared lock agreed with `java-io-j2cl`, Trove, JDOM, and WebSwing:

```bash
bash tools/with_wsl_build_lock.sh bazelisk build //src/test/consumer:application
```

The repository fixes one job, one test job, and one worker per worker type. The lock spans the complete child command and releases automatically. Do not shut down WSL or terminate another project's build to acquire it.

Validation checks source ownership and snapshot hashes, verifies patch reproducibility, compiles the JRE and independent consumer probes, compares the actual emitted class headers against the JDK inventory, runs 56 checks in optimized JavaScript in headless Chromium, records an MPS boundary diagnostic, and creates a verified source archive in `out/release`. Negative controls verify that the API gate rejects missing types and members.

## Use from another Bazel module

The consumer root must select the JRE patch: dependency modules' `single_version_override` declarations do not propagate to the root, and Bazel only accepts patch labels in the main repository. Install a root-local copy first:

```bash
python3 /path/to/java-nio-j2cl/tools/install_profile.py /path/to/consumer
```

Then configure the consumer's `MODULE.bazel`:

```starlark
bazel_dep(name = "java_nio_j2cl", version = "0.1.0")
bazel_dep(name = "j2cl", version = "20260402")

# Select an actual published source archive or a checkout for development.
local_path_override(module_name = "java_nio_j2cl", path = "/path/to/java-nio-j2cl")

single_version_override(
    module_name = "j2cl",
    version = "20260402",
    patches = ["//third_party/java-nio:j2cl-nio.patch"],
    patch_strip = 1,
)
```

Add `@java_nio_j2cl//:java-nio` to the consumer's `j2cl_library` dependencies. The label is a stable profile anchor; the declarations themselves are compiled into J2CL's JRE. A normal JVM JAR or an unpatched J2CL dependency does not install this profile.

The supplied patch is **composed**: it includes NIO, limited external signature dependencies, pinned missing-IO sources owned by `java-io-j2cl`, and the four URI/URL classes owned by WebAWT. Do not also apply the standalone IO patch or another definition of those classes. The integration-only `File.toPath()` declaration throws the same descriptive unsupported-operation exception; it does not change the canonical IO repository.

## MPS evidence and limits

`compatibility/mps-nio-usage.json` records the exact source revision and reference scan. The unchanged `InternalAnonymousClassUsage.java` fixture exercises the `Readable`/`CharBuffer` relationship. Its target disables only Error Prone's unused-anonymous-class check, because those expressions are intentional in the original fixture. The separate unchanged production `FileWithPosition.java` fixture is a diagnostic target: its NIO signatures resolve, while `String.format` and the `since`/`forRemoval` attributes of `Deprecated` remain outside stock J2CL support. Check `compatibility/mps-boundary.json` for the observed result.

A source/import/JAR reference inventory is not a complete resolved MPS dependency graph. No complete browser module set or full IDE build has been established. Reflection, classloading, IntelliJ platform services, threading, and operational persistence remain separate migration work. The public API report describes compilation compatibility, not Java SE behavioral equivalence.

## Shared type ownership

`compatibility/type-ownership.json` assigns one source provider to each emitted type. `compatibility/dependency-snapshots.json` records the owner and SHA-256 of every imported IO/WebAWT source. `compatibility/external-support.json` explicitly lists the small non-NIO type subsets used to close signatures; it does not claim full support for `java.net`, `java.time`, security, classloading, or threading.

When combining this profile with WebAWT, its `uri-j2cl` source target must yield ownership of all four URI/URL classes to the patched JRE. Use the separate integration guidance in `integrations/README.md`. Canonical WebAWT remains on stock J2CL; this project does not alter its sources, profile locks, or validators. The reused URI implementation is narrow. All NIO URI conversion operations fail, so no URI round-trip or escaping semantics are claimed.

## Regeneration and release

Ordinary builds use the checked-in patch and snapshots; sibling projects are not needed. To refresh sources intentionally:

```bash
mkdir -p out/tools
javac -d out/tools tools/ExtractApi.java
java -cp out/tools ExtractApi > compatibility/jdk-nio-api.json
python3 tools/generate_stubs.py
python3 tools/generate_support.py
python3 tools/prepare_overlay.py --upstream /path/to/j2cl --io /path/to/java-io-j2cl --webawt /path/to/WebAWT
python3 tools/make_patch.py
```

Use the pinned J2CL release for `--upstream`. Refresh externally owned snapshots only after coordinating with their project. `prepare_overlay.py` preserves upstream charset code, adds required metadata and declarations, and records hashes. `make_patch.py --check` compares the committed patch against sources and the small pinned upstream baseline without accessing sibling checkouts.

The source archive is deterministic and its manifest contains a SHA-256. Releases are source overlays, not a published Bazel registry entry. See `NOTICE` for source provenance.
