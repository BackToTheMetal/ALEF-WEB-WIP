# Validation record — 2026-10-01

The MPS-scoped reduction passed the complete `bash tools/validate.sh` entry
point under the shared `/tmp/j2cl-build.lock`. This validates the reduced
JetBrains Trove compatibility library and selected MPS collection consumers,
not a complete MPS browser port.

## Source scope

Pinned MPS revision: `fb6524e4726c73081d1e0934b54da5a0f78e7d05`.
The inventory includes Java/Kotlin sources, serialized models and generated
primitive-list names. Of 207 production source files, 203 remain: 89 supported
root types and 114 dependencies. `TIntStack`, `TLinkedList`, `TLinkable`, and
`TLinkableAdaptor` were removed. All retained production files matched the
previous release manifest byte-for-byte before the new archive was created.

The new inventory tests cover model import aliases, wildcard imports, generated
families, auxiliary scope, transitive dependencies, missing dependencies and
accidental reintroduction of a removed source. Regeneration enforces the source
set. JVM compiler output is cleared before compilation to prevent stale classes
from hiding removal errors. Tests for the removed stack/list APIs were removed;
all applicable family, defect, portability and MPS fixture tests remain.

## Observed results

| Check | Result |
| --- | --- |
| Artifact/fixture integrity, source closure and regeneration | Passed |
| Validation-tool tests | 11 passed on Linux |
| Retained compiled public/protected API | 4,579 entries; zero mismatches outside exact documented adaptations |
| Original and replacement JVM | Four seeded traces identical; 573,204 assertions per implementation |
| Replacement JVM defect/portability regressions | Passed |
| JVM library, J2CL library, development and optimized application builds | Passed; 203 production Java sources |
| Browser differential tests | 24 passed: four seeds × two build modes × three engines; 3,439,224 assertions |
| Browser defect/portability regressions | All six engine/build combinations passed |
| Independent archive consumer | Build and optimized execution passed in Chromium, Firefox and WebKit |
| Archive audit | All 266 input hashes match the workspace; exactly 203 production sources; removed types absent |

Environment: OpenJDK 21.0.12.1 under Ubuntu/WSL, Bazel 8.3.1, J2CL 20260402,
Playwright 1.62.0; Chromium 151.0.7922.34, Firefox 153.0 and WebKit 26.5.
Upstream boxed-constructor/decorator and Bazel dependency warnings remain;
compilation and Java execution pass. The archive consumer reuses the action
cache sequentially but resolves its source dependency from the checksummed
archive, without a local source override.

Archive SHA-256:
`3fef154bb2821a00bd908dba8dfeb0d3cbf8f52fed5d710e19a51ee226654979`.

## Boundaries and evidence

The inventory identifies 29 short-related Trove 2.1.0 requirements absent from
the existing JetBrains baseline. This change does not implement them or establish
compatibility with the separately bundled Trove 2.1.0 artifact. The identity of
MPS's assembled platform `lib/trove.jar` remains unverified. External plugins,
IntelliJ binaries, arbitrary reflective names and future language output require
separate integration checks. See [MPS requirements](docs/mps-requirements.md)
and [compatibility boundaries](compatibility/exclusions.md).

Four unchanged MPS node-map/strategy fixtures execute with test-only model
boundary values, including a 20,000-node workload. A previous 32-seed campaign
predates this reduction; the current completed run uses the four default seeds.
No hosted CI run, publication or full MPS application validation is claimed.

Evidence: [complete log](out/mps-pruning-validation.log),
[validation result](out/validation-results.json),
[JVM results](out/jvm-results.json), [API inventory](compatibility/api.json),
[browser results](out/browser-results.json),
[browser regressions](out/browser-regressions.json),
[consumer result](out/consumer-result.json), and
[archive manifest](out/source-release.json).
This validation record is a workspace report, not an input to the tested archive.
