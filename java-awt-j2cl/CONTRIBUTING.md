# Contributing to java-awt-j2cl

Read `PLAN.md` and the architecture decisions before making implementation changes.

Consumer applications use the stable root `//:webawt` target. Release packaging is documented in `docs/releasing.md`; SonarQube is deferred by the owner, while executable behavior and compatibility gates remain mandatory.

Every public API change must update the compatibility manifest and include tests for its declared support status. Production code must use only APIs present in the pinned J2CL JRE or dependencies approved by an architecture decision.

Follow `FINALIZATION.md` for the current completion plan. The dual-runtime showcase is the consuming application. Every Graphics2D implementation change must include its showcase demonstration and regression assertions, including affected overloads and both Canvas/BufferedImage backends where applicable.

The supported API profile is locked. Review intentional manifest, extended-disposition, or prerequisite-root changes together with the explicitly regenerated `compatibility/stock-profile-lock.json`; CI does not regenerate that lock. New protected/nested contracts are tracked in `compatibility/extended-api-worklist.md`. The validator always enforces complete extended API and Graphics2D showcase coverage; `-RequireCompleteApi` remains accepted for invocation compatibility.

Closure compilation is limited to one persistent worker to bound memory use during a full test rebuild. Other Bazel actions retain the configured four-action concurrency.

Java source is not word-wrapped. Blank lines are not used inside method bodies. Wildcard imports, tabs, trailing whitespace and multiple statements on one line are prohibited. Methods should remain small enough to read clearly without internal blank-line grouping.

Elemental2 types must stay behind internal browser ports and must not appear in public `java.awt` signatures. Every browser listener or resource needs a disposal path.

Do not copy OpenJDK implementation bodies or tests. Record provenance before reusing any third-party source or algorithm.

Run the formatting check from the repository root:

```powershell
./tools/quality/Check-JavaFormatting.ps1
```

Run the complete validation gate from Windows PowerShell with:

```powershell
./tools/quality/Validate-WebAwt.ps1
```

The gate runs validation-tool unit tests, production dependency policy, JVM and J2CL tests, Chromium/Firefox/WebKit tests, the full Bazel build, optimized bundle budgets, formatting rules, and emitted-API validation. Use `-SkipModernBrowsers` only for a fast local iteration after the modern matrix has already passed for the same change. Browser setup is documented in `docs/testing/modern-browser-tests.md`, and the non-percentage coverage contract is documented in `docs/testing/coverage-policy.md`.
