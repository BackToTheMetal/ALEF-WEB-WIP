# Compatibility data

`openjdk-awt-api.json` is generated from the active JDK's `java.desktop.jmod` and contains the exported public top-level AWT types and their public members.

`compatibility-manifest.json` records the planned browser support status independently from implementation progress. Supported status values are `exact`, `web-mapped`, `limited`, `deferred`, `unavailable`, and `excluded`.

`j2cl-jre-api.json` is the authoritative inventory generated from the Bazel-resolved source tree for J2CL 20260402. `j2cl-jre-assessment.json` summarizes the resulting constraints, and `j2cl-dependency-report.json` classifies the non-excluded AWT signature dependencies.

`webawt-awt-api.json` is exported from the current J2CL library jars declared by Bazel. The exporter queries target outputs rather than scanning stale jars left by earlier builds. Run the complete build before exporting: a test-only build can refresh header jars and JavaScript without refreshing every full class jar. `webawt-api-validation.json` and `webawt-api-validation.md` compare the locked public top-level AWT inventory with OpenJDK, including inherited members and type declarations. The strict gate fails on every missing in-profile type or member, including partial types, incompatible supported type declarations, and unclassified contracts.

`stock-profile-lock.json` freezes the public reference member inventories and support dispositions, explicitly reviewed extension dispositions, and prerequisite reference roots. Counts alone are insufficient: exchanging one supported member for another also fails this check. Implementation progress can change without changing the profile. Intentional profile changes must include a rationale in the manifest or extension dispositions; explicitly regenerate the lock with `python3 tools/compatibility/check_profile_lock.py --write-lock` and review those changes together. CI only checks the lock and never rewrites it.

`openjdk-extended-api.json` and `webawt-extended-api.json` include protected members and accessible nested types. They also include the shipped prerequisite type families in `prerequisite-types.json`, using JDK 17's `java.desktop`, `java.base`, and `java.datatransfer` modules. Those fixed reference roots remain in the denominator if an implementation is removed. The class-file visibility audit excludes private/local classes and public classes nested inside inaccessible enclosing classes.

`extended-api-worklist.json` and `extended-api-worklist.md` expose the newly measured finalization backlog. New contracts remain `review-required` until they receive an explicit disposition in `extended-api-dispositions.json`, with reasons and behavior-test evidence for supported entries. The audit preserves final/abstract/visibility and throws differences for source-compatibility review; the old callability normalization does not prove extension compatibility. A missing or incompatible explicitly supported extension fails development validation immediately. `python3 tools/compatibility/audit_extended_api.py --require-complete` additionally fails while any new contract is unreviewed, a candidate is missing, or declaration review remains open. Development validation can pass with an explicitly reported finalization backlog; it must not be presented as release readiness.

`implementation-status.json` is the explicit evidence baseline for implemented and partial types. Source-file presence does not promote implementation state. `api-completeness-worklist.json` combines that evidence with the manifest, emitted API, and J2CL dependency report. Its gate rejects missing target types/members, unresolved dispositions, incompatible supported types, J2CL signature contradictions, and disagreements between the reference, manifest, and validation inventories.

The reviewed component/control event extensions are exercised by the same `PortableEventDispatchTest.java` consumer source in the main J2CL suite and display-dependent desktop suite. Its subclass overrides verify event masks, listener interest, delivery suppression, and listener snapshots. DOM-generated event routing is checked in `Elemental2BrowserDocumentTest.java`; modern-browser suites cover keyboard translation. These targeted consumer probes complement the binary inventory, but do not close the general generated-compilation-probe backlog.

`graphics2d-showcase-coverage.json` and `.md` inventory all 84 Graphics2D/Graphics consumer method signatures, with per-backend implementation resolution and explicit pending/verified/unavailable coverage. `graphics2d-showcase-reviews.json` contains manually reviewed mappings to observable sample scenarios and regression evidence. Run `python3 tools/compatibility/graphics_showcase_coverage.py` to regenerate and validate references, or add `--require-complete` to enforce full demonstration coverage. Reference checks cannot establish assertion quality; reviewed mappings must accompany actual shared consumer and backend pixel/state tests. The Windows completion validator enforces both the extended API and showcase completion gates.

`bundle-size-report.json` records deterministic raw and gzip measurements for the optimized sample applications. Its limits are defined in `config/bundle-budgets.json` and enforced by `tools/quality/check_bundle_budgets.py` after a complete build.

Production dependency policy is enforced separately by `tools/quality/check_production_dependencies.py`. It verifies exact module and test-tool pins, lock files and Maven checksums, the main-source import allowlist, and external repositories referenced by production and sample Bazel targets.

Run the OpenJDK inventory and initial classification from the repository root:

```powershell
./tools/compatibility/Export-OpenJdkAwtApi.ps1
./tools/compatibility/New-CompatibilityManifest.ps1
./tools/compatibility/New-CompatibilityReport.ps1
```

Regenerate the J2CL inventory after obtaining the pinned source tree:

```powershell
./tools/compatibility/Export-J2clJreInventory.ps1 -J2clJavaSourceDirectory C:/path/to/j2cl/jre/java/java -SourceIdentity "Bazel-resolved j2cl@20260402 source tree"
./tools/compatibility/Compare-AwtDependenciesToJ2cl.ps1
```

Build and validate the emitted WebAWT API with:

```powershell
wsl -d Ubuntu --exec bash -lc "cd /mnt/c/Codex/WebAWT && bazelisk build //... && python3 tools/compatibility/export_webawt_api.py"
./tools/compatibility/New-CompatibilityManifest.ps1
./tools/compatibility/New-CompatibilityReport.ps1
wsl -d Ubuntu --exec bash -lc "cd /mnt/c/Codex/WebAWT && python3 tools/compatibility/check_profile_lock.py && python3 tools/compatibility/compare_webawt_api.py --fail-on-incomplete-api"
wsl -d Ubuntu --exec bash -lc "cd /mnt/c/Codex/WebAWT && python3 tools/compatibility/build_completeness_worklist.py"
```

The manifest separates planned support from implementation state. Every entry must receive a method-level dependency and behavior review before implementation starts, and implementation state changes only after the relevant build and tests pass.

The previous `--fail-on-false-claims` option remains an alias for the stricter gate. The full Windows validator now also exports and audits the extended API. Use `-ReferenceJavaHome` to select a JDK 17 installation; it otherwise uses the JDK containing Windows `javap`. WSL's local `javap` may come from a newer JDK and reads the reference class files without executing Windows tools. Linux CI selects JDK 17 explicitly.

```powershell
./tools/quality/Validate-WebAwt.ps1
./tools/quality/Validate-WebAwt.ps1 -RequireCompleteApi
```

The second command enforces the extended API completion gate in addition to development checks. Coverage, manual browser acceptance, licensing, and packaging remain separately tracked release requirements in `FINALIZATION.md`.
