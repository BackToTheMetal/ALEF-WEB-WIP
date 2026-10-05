# Metalava → Java NIO stubs → J2CL trial

This trial generates Java stubs with **Metalava 1.0.0-alpha15** from the sources of **OpenJDK 21.0.12.1+1-1-26.04.4-Ubuntu**, using the same JDK's complete `java.base` declarations to resolve dependencies. It supplies those stubs to **J2CL 20260402** through a separate patched JRE build.

Everything produced by this trial lives in this folder. The parent project's sources, patch, and validation reports are not modified.

## Run

From PowerShell in the parent directory:

```powershell
.\metalava-trial\run.ps1
```

Generation and Java/API validation only:

```powershell
.\metalava-trial\run.ps1 -Phase generate
```

The entry point uses WSL Ubuntu. It needs a full JDK 21, Python 3, and network access for the first tool setup. The full trial also needs the existing Bazelisk and Chromium setup. The Gradle wrapper and dependency lock/checksum files pin Metalava and its dependencies. The matching source package is downloaded and unpacked locally if the selected JDK has no `src.zip`; no OS package is installed.

Select another JDK 21 and its matching source archive using **Linux paths**:

```powershell
.\metalava-trial\run.ps1 -JdkHome '/path/to/jdk-21' -SourceZip '/path/to/matching/src.zip'
```

Or directly in WSL:

```bash
bash run.sh --jdk-home /path/to/jdk-21 --source-zip /path/to/matching/src.zip
```

The default source download is accepted only for the pinned JDK class-image hash. An explicitly selected source archive is the caller's responsibility to match to its JDK; both hashes and the actual Java version are recorded. This trial's policy supports Java 21, not arbitrary newer Java releases. A JRE alone lacks the compiler, JMODs, and sources required for this source-based trial.

## Files to inspect

| File/directory | Purpose |
| --- | --- |
| `stubs/raw/` | Unmodified Metalava output: 156 top-level Java type files across all eight NIO packages. |
| `stubs/j2cl/` | 152 Metalava type files selected for the J2CL overlay, with the single explicit preview-method exclusion. |
| `reports/nio-api.txt` | Metalava's API signature snapshot. |
| `reports/input.json` | Actual JDK version, input hashes, source archive, and class counts. |
| `reference.json` | Pinned default JDK and matching source-package hashes and URL. |
| `policy.json` | Exact package scope, preserved charset types, and excluded J2CL member. |
| `reports/raw-api-check.json` | Independent comparison of the compiled Java stubs with the selected JDK. |
| `reports/reproducibility.json` | Byte-identical output confirmed across two independent generation runs. |
| `reports/metadata-differences.json` | Sealed declarations and enum ordering compared separately using Java syntax trees. |
| `reports/j2cl-adaptation.json` | Every generated type, preserved provider, imported dependency hash, and patch hash. |
| `reports/j2cl-api-check.json` | Comparison against the actual J2CL JRE header JAR. |
| `reports/browser-check.json` | Result of executing the optimized JavaScript in Chromium. |
| `reports/run.json` | Status and timestamps of the most recent pipeline invocation. |
| `reports/full-run.json` | Successful end-to-end verification through the documented PowerShell entry point. |
| `j2cl-workspace/` | Separate Bazel module, composed JRE patch, and independent consumer probes. |

## How it works

1. `scripts/prepare_jdk.py` extracts the matching NIO sources and builds a deterministic declaration JAR from `java.base.jmod`. It never loads or executes the input classes.
2. Metalava reads those sources with the complete JDK declaration classpath. It writes ordinary Java source and a signature file. `reports/metalava-arguments.json` contains the exact arguments.
3. `javac --patch-module java.base=...` compiles the raw stubs. Java 21 preview is enabled for this check because the raw API contains the foreign-memory `FileChannel.map` overload.
4. The parent project's independent reflection inventory and class-file API gate compare 163 public/protected types and 1,174 non-preview declared members. The trial's small checker normalizes only interface-list ordering. It does not suppress missing methods or changed generic signatures.
5. `scripts/AdaptStubs.java` uses javac syntax trees and source positions to remove exactly the member listed in `policy.json`. The policy must match once; a missing or duplicate match fails generation. Every remaining method body is still Metalava's original body.
6. `scripts/prepare_j2cl.py` composes a standalone J2CL patch. The required IO, networking, and other signature dependencies are reused from the parent's existing providers and recorded with hashes.
7. The separate Bazel module transpiles the patched JRE and builds an optimized browser application. Builds hold `/tmp/j2cl-build.lock` and use the parent's single-worker limits. The browser checks seven throwing NIO operations and preserved UTF-8 behavior.

Ordinary `java.nio` classes belong to the JRE. Adding them as a normal application dependency would conflict with runtime ownership; this is why the trial patches J2CL's JRE.

## Explicit J2CL adaptations

- Preserve the existing `Charset` and `StandardCharsets` implementations from the parent profile, plus upstream J2CL's `IllegalCharsetNameException` and `UnsupportedCharsetException`. `java.lang.String` needs the existing charset implementation.
- Exclude only `FileChannel.map(MapMode, long, long, Arena)`, the Java 21 preview foreign-memory overload. Its external types are outside this J2CL profile.
- Retain Metalava's standard `RuntimeException("Stub!")` method bodies. This trial does not convert them to the parent's descriptive `UnsupportedOperationException` contract.

The raw output still includes all four charset types and the preview overload, so those choices remain reviewable.

## What this trial establishes, and its limits

The raw Java compilation, independent declaration comparison, J2CL transpilation/optimized build, and Chromium checks all pass. Both compiled-API comparisons cover all 1,174 non-preview members; five class headers differ only in interface-list ordering. The browser verifies seven throwing operations and existing UTF-8 support. Two independent generations produce byte-identical raw sources. The API gate's negative control rejects a deliberately removed class.

J2CL emits one `unusable-by-js` warning for the `IOException` return type of `DirectoryIteratorException.getCause()`. The warning is retained in `reports/first-j2cl-build.log`; no extra suppression or source rewrite was added for it.

Metalava produces **SDK-style compilation stubs**, with runtime consequences that require an explicit policy before using them as a replacement runtime:

- Concrete methods and constructors throw `RuntimeException("Stub!")`. Abstract methods remain abstract. Exception construction and user subclass construction can therefore fail too.
- Object-valued fields such as `ByteOrder.BIG_ENDIAN` are initialized to `null`. Reading such a field does not throw.
- Eight enums have reordered constants. For example, `AccessMode` is emitted as `EXECUTE, READ, WRITE`, changing ordinals from the JDK declaration order.
- Nine sealed restrictions on the NIO buffer hierarchy are dropped. `AddedSealed` is deliberately downgraded to a visible warning; the separate metadata report records the actual differences.
- The declaration gate does not claim annotation equivalence, constant-value equivalence, sealed equivalence, full inherited external APIs, or Java SE runtime behavior. It excludes the one preview overload even when checking raw output.
- Only the selected browser operations are executed. Compiling/transpiling the whole API does not establish working filesystem, buffer, channel, charset, or networking implementations.

An initial experiment using only a NIO class JAR lost dependencies such as `IOException` and exception superclasses. Supplying all of `java.base` restored those declarations, but that JAR-input invocation emitted 2,650 top-level files despite the requested package filter. The maintained pipeline therefore uses matching NIO sources plus a complete declaration classpath. The final stubs are generated by Metalava, not reconstructed from the reflection inventory.

## Regeneration and ownership

Edit `policy.json` or the scripts, then rerun the trial. Do not edit generated stubs. Regeneration replaces only the trial's managed output directories; it leaves the original JDK and parent project intact. `build/`, `.cache/`, and `.gradle/` are disposable tooling/output directories. The source and JAR hashes identify the actual input, and `generation.json` contains a hash of the generated raw tree for reproducibility checks.

The generation stage currently reads the parent project's reference-inventory/checker tools and dependency providers. The generated `j2cl-workspace/patches/metalava-nio.patch` is self-contained for its Bazel build. This is an evaluation folder, not a published replacement for the existing NIO profile.

Metalava preserves OpenJDK copyright headers in source-derived stubs. The selected OpenJDK distribution's notices are retained under `licenses/`. Metalava's upstream project is https://android.googlesource.com/platform/tools/metalava/.
