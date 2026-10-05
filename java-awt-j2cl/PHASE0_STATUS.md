# Phase 0 execution status

## Outcome

The central feasibility question is resolved positively: a project-owned `java.awt` package can be transpiled by the pinned J2CL release, linked into an optimized Closure application, and used alongside Elemental2 DOM bindings.

Phase 0 is functionally complete except for execution of the configured SonarQube analysis against a SonarQube server or hosted service.

## Verified evidence

| Gate | Result | Evidence |
| --- | --- | --- |
| Pinned toolchain | Passed | Bazel 8.3.1, J2CL 20260402, Java 21 and Elemental2 1.3.2 are pinned in repository configuration and the Bzlmod lock is committed-ready. |
| `java.awt` package | Passed | `java.awt.Point` and `java.awt.geom.Point2D` transpile as `j2cl_library` targets. |
| Consumer application | Passed | The point sample links to optimized JavaScript and exports translation and DOM-mount entry points. |
| Elemental2 integration | Passed | A browser port backed by `DomGlobal.document` compiles, writes to a real test DOM and reports missing elements explicitly. |
| J2CL tests | Passed | Six tests pass: four portable point tests and two Elemental2 DOM integration tests. |
| Optimized build | Passed | `//samples/point:point_application` produces optimized JavaScript and a source map. |
| API inventory | Passed | The local OpenJDK 17 `java.desktop` module exposes 325 public top-level AWT types in the generated inventory. |
| J2CL JRE inventory | Passed | The pinned J2CL source contains 250 public top-level emulated JRE types. |
| Dependency audit | Passed | Non-excluded AWT signatures currently reference 335 AWT-contract types, 39 stock-J2CL types, 8 types in unclaimed packages, 9 missing types in J2CL-owned packages and 4 missing `javax` types. |
| Source rules | Passed | The structural formatting check passes all current Java sources. |
| SonarQube execution | Pending | Configuration and quality thresholds exist, but no SonarQube endpoint or token is configured in this workspace. |

## Reproducible commands

Run these commands from WSL in the repository root:

```shell
bazelisk build //samples/point:point_application
bazelisk test //src/test/point/com/webawt/point:PointTest //src/test/browser/com/webawt/browser:Elemental2BrowserDocumentTest
```

Run the local source rules from PowerShell:

```powershell
./tools/quality/Check-JavaFormatting.ps1
```

## Important findings

- J2CL 20260402 compiles its emulated JRE with Java 21 source and target levels, so Bazel must use a Java 21 runtime. A Java 11 runtime fails before WebAWT sources are evaluated.
- Elemental2 1.3.2 needs a narrow BUILD-file backport with the current Closure Compiler extern set. The pinned patch bypasses an obsolete CSS extern patch and adds the required Web Animations extern. The change matches current upstream Elemental2 build behavior and does not modify generated Java APIs.
- Stock J2CL omits `Object.clone`. The current `Point2D.clone` implementation preserves WebAWT's built-in `Point2D.Float`, `Point2D.Double` and `Point` types, but an unknown user subclass is copied as `Point2D.Double`. This member is classified as limited.
- Direct local J2CL builds remain a WSL/Linux workflow. The project should use Linux CI.
- File dialogs and printing remain excluded from all implementation phases.

## Next implementation slice

Complete differential test infrastructure, then implement the foundational value types `Dimension`, `Insets` and `Rectangle` with OpenJDK comparison vectors. In parallel with later slices, audit referenced signature types against `j2cl-dependency-report.json` before adding each public API.
