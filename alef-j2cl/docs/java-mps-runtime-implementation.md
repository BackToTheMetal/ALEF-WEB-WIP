# Java MPS runtime implementation

Implementation of [the 29 September plan](java-mps-runtime-plan.md), 1 October 2026.
Status: implemented, verified and selected in the local app, using the plan's
explicit Java-process fallback for hard deadlines.

## Architecture delivered

The related `ALEFBackend` now contains `alef-runtime-api` and `alef-runtime-java`.
Package composition and checks share a disposable model session over the original
JVM MPS and ALEF libraries. `ReleaseBuilder`, `PackageComposer` and the pinned
`ReleaseToolchain` use this boundary. Historical package contents continue to use
their dedicated contents worker. Workspace saving remains on its existing path.

The native implementation boots explicit MPS components without an IntelliJ
application, automatic plugin discovery, make service or generator engine. Original
facet registration and dynamic components are necessary for language loading.
Servicespraak XSD checks also require the original text-generation registry; broad
parity exposed this requirement, so it is included rather than skipping that checker.

Two hosts use the same bridge: an embedded executor/classloader and a warm Java
process owned by Spring. **The process host is selected for deployment**, as allowed
by the plan's hard-timeout fallback. Complete cooperative interpreter cancellation
has not been demonstrated. Embedded timeouts quarantine the host; process timeouts
retire the owned worker. This is an explicit deviation from the initially proposed
embedded default, not a claim that interrupting Java guarantees termination.

Explicit package scope replaces desktop service, definition and metatag lookups.
Adapters are compiled into copies of original module jars; pinned generated source
and original jar hashes plus strict source anchors guard regeneration drift. Other
reachable desktop lookups fail visibly. Original checkers and Testspraak semantics
remain intact. Interpreter state and translator caches/listeners are reset between
sessions; package and standard-model snapshots guard against mutation.

## Pinned distribution

* ALEF: `96dd99ff70cb88112e578088e2d4b70d6efc4327`.
* MPS markers: root `MPS-251.23774.423`, lib `MPS-251.23774.580`, preserving the
  actual original distribution. Every shipped file is hash-pinned.
* Final runtime fingerprint: `51b2c06392bbd66b210b78a3846b0798db71b5f0a93b2c33da955ee692b72beb`.
* Dependency archive: 787,454,543 bytes; SHA-256
  `14552648924ef50a9bd1779dba8441ded63bf48c1afc8e59fcb08d755fec9c22`.
* Explicit inventory: 765 module identities and 1,882 files. This is a validated
  complete JVM closure, not a claim of minimal bytecode. No browser compatibility
  classes or J2CL runtime are included.

`prepare-java-runtime.py` verifies the retained original dependency archive, pinned
adapter inputs, manifest paths, unique module identities and bridge-source stability
during assembly. Runtime verification rejects missing, changed or undeclared files.
Windows plugin macro paths and the UTF-8 protocol stream are explicitly handled.

## Verified native parity

The retained corpus contains 111 models / 31,154 nodes from the pinned ALEF tests.
It covers cross-model services, rule and input/output tests, expected-red behavior,
skipped/incomplete cases, exception recovery, translator extensions and standard
functions/models. The original `IdeaEnvironment` worker remains the parity oracle.

The final packaged runtime ran three disposable sessions on each platform:

| Result | Native baseline | Windows Java | Linux Java |
|---|---:|---:|---:|
| Cases | 431 | 431 | 431 |
| Passed | 417 | 417 | 417 |
| Skipped | 12 | 12 | 12 |
| Expected fixture exception outcomes | 2 | 2 | 2 |
| Model diagnostics | 156 | 156 | 156 |
| Checker engine failures | 0 | 0 | 0 |

All test identities, statuses, messages and diagnostics match exactly after removing
case durations. Package and five standard models remain unchanged in every run.
These deliberately invalid language fixtures produce a failing aggregate report;
acceptance is exact parity, not suppressing those failures.

| Packaged measurement | Windows JBR 21.0.8 | Linux JBR 21.0.6 |
|---|---:|---:|
| Process/bootstrap startup | 15.109 s | 19.086 s |
| First full session | 20.094 s | 27.018 s |
| Second full session | 8.234 s | 8.241 s |
| Third full session | 7.766 s | 7.867 s |

Session timing includes opening, loading, checking and closing. Linux read the
portable distribution from the Windows-mounted filesystem; these cold timings are
not directly comparable to the original Linux-cache baseline. The warm host avoids
paying bootstrap per package. Earlier Sportclub proof sessions measured 6.835 s
startup, 7.913 s first session and 0.154/0.118 s warm sessions before the final
standard-model guard; they are historical proof measurements, not final benchmarks.

Evidence is retained under `out/java-mps-runtime-implementation/`: original native
reports, `windows-language-parity-v3.json`, `linux-language-parity-v3.json`,
`compare-parity.py`, packaging locks, build tests, original backend file backups and
the checked installation receipt. Earlier candidate packages are superseded.

## Integration and local activation

The full Gradle build and `:alef-application:installDist` passed: **188 tests passed,
four optional original-worker tests skipped, zero failures/errors**. The module
counts, skips and native integration XML reports are retained in `backend-tests.json`.
An initial broad-suite failure was PostgreSQL startup on the Windows-mounted
filesystem; the successful rerun uses the existing `ALEF_TEST_TMP` setting on Linux.

`JavaPackageRuntimeTest` passed for both embedded and process hosts, covering native
parity, wrong expectations, incomplete/invalid cases, correction, malformed imports,
same-ID reuse, concurrent requests and process deadline/replacement. Embedded retained
heap grew by 2,380,384 bytes over the soak. A separate diagnostic measurement of the
actual process worker grew from 265,884,672 to 268,188,672 retained heap bytes across
20 additional sessions; all 21 sessions passed five Sportclub cases. This is bounded
soak evidence, not proof that no workload could ever leak.

`JavaReleaseBuilderTest` passed with real isolated PostgreSQL and model storage,
including selective composition, retained baseline roots, native checks, runtime
archive retention, source preservation, archive/report hashes and stale-toolchain
rejection. The original toolchain and artifacts remain available for rollback.

The local backend was restarted with the versioned distribution selected. Its
package worker was Windows `java.exe` PID 31844, parent backend PID 49148; the same
child served both browser verification runs. Package execution used neither Python
nor WSL. Those process IDs describe this verification, not permanent configuration.

The browser verification created only the labelled temporary package
`0.0.0-runtime-verification`, then reran its checks. Both immutable reports carry the
new fingerprint, zero checker engine failures, unchanged package/standard models,
and the existing five failing cases from the saved workspace. The full jobs took
**37.994 s cold and 0.381 s warm**, including composition, checks and report storage;
the cold job also retained the new runtime archive. This comparison demonstrates
reuse, not a universal performance guarantee or an old-versus-new runtime benchmark.

The Checks tab rendered the completed reports, both comparison editor DOM identities
stayed mounted, and no browser console errors were observed. The temporary package
was removed through the package UI after verification. Its audit evidence and reserved
test version remain, as required by that workflow. Existing packages retained their
revisions/approval status, and the workspace head remained
`0279bc3885ea4bf96e6030c2794c84a59a86504d` before and after verification. The temporary
browser tab was closed; the original editor tab was left open.

Live evidence: `live-ui-runtime.json`, `live-ui-report.png` and
`live-ui-cleanup.png` in the implementation evidence directory.

Operational details and rollback commands are in the backend's
`docs/java-package-runtime.md`. In-process hard cancellation and further dependency
trimming remain deferred. Deployment execution adapters are outside this migration.
