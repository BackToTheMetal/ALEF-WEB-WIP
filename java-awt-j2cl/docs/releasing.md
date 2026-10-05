# Release engineering

The current version is **0.2.0, unreleased**. Use `@webawt//:webawt` as the stable consumer label. No public registry entry, download location or published release is assumed.

## Prepare a candidate

Use the pinned Linux/WSL toolchain, JDK 17.0.16+8 for reference inspection, and the Playwright 1.62.0 browser installation from `docs/testing/modern-browser-tests.md`.

1. Run the full validation workflow or `tools/quality/Validate-WebAwt.ps1` from Windows. It checks behavior, three browser engines, the complete build, API/probe completion, formatting, dependencies and bundle budgets. SonarQube and percentage coverage are deferred by the owner; see `docs/testing/coverage-policy.md`.
2. Run `python3 -m unittest discover -s tools/release -p 'test_*.py'`.
3. Run `python3 tools/release/package_source.py --expected-version 0.2.0`. The printed directory under `out/releases/` contains the archive, checksum file, source manifest, resolved module graph and CycloneDX 1.6 SBOM. The source manifest records each included file's SHA-256 and the exact module/patch/toolchain inputs.
4. Run `python3 tools/release/consumer_smoke.py --candidate PRINTED_DIRECTORY --workdir out/consumer-UNIQUE_NAME`. Use an empty work directory. This validates the archive checksum and file manifest, creates a separate root module, resolves java-awt-j2cl through a checksummed `archive_override`, copies the archived Elemental2 patch into that root (Bazel rejects patch labels from dependency repositories), applies its override, and builds the existing showcase with only the stable java-awt-j2cl target. Its JUnit smoke test runs mount, render, self-check and teardown in Chromium, Firefox and WebKit.
5. Run `python3 tools/release/capture_evidence.py --candidate PRINTED_DIRECTORY --consumer out/consumer-UNIQUE_NAME/consumer`. Preserve the indexed test logs, XML, browser outputs and environment report along with the candidate. `validation-environment.json` is an execution record written after packaging, separate from the deterministic source artifacts in SHA256SUMS. CI retains both.

The GitHub Actions **java-awt-j2cl release candidate** workflow runs the full validation first, then prepares and tests the archive. It uploads candidate/evidence artifacts with read-only repository permissions and does not publish a GitHub release or registry entry. The input version must match MODULE.bazel. Release validation and candidate creation execute in the same checkout so the archived reports correspond to that validation run.

## Archive contract and reproducibility

Included: production and test sources, shared showcase/font, Bazel configuration and locks, build patch, tools, compatibility reports, architecture/testing documentation, license/notices, changelog and provenance record. Excluded: workspace caches, `out`, Bazel outputs, IDE state, Python bytecode and compiled class files. Source symlinks are rejected.

Members are sorted; archive paths, permissions, ownership and timestamps are normalized. Gzip timestamps are fixed. Identical input bytes produce identical archive bytes. Generated reports may contain timestamps, so a fresh validation can produce a different source candidate even at the same Git revision; the file manifest makes those differences auditable. The candidate directory includes the archive hash, and existing artifacts are never replaced with different bytes.

The SBOM covers resolved Bzlmod module identities, root locked Maven test artifacts and the included font. It is a source/build inventory, not a complete inventory of embedded components inside downloaded JDK, compiler or browser binaries. Unknown licenses are unasserted. The Maven lock contains artifact hashes; the module lock contains registry metadata hashes. They are distinct from binary checksums and must not be relabeled as such.

## Version and publication contract

Keep MODULE.bazel and CHANGELOG.md synchronized. Use `0.x` versions while the browser profile is stabilizing; incompatible retained source/behavior changes require a new minor version, compatible fixes a patch version. For an eventual 1.x line, incompatible retained contracts require a major version. Any profile-disposition change requires review and explicit lock regeneration regardless of version.

Before public distribution, complete and record the provenance/legal review required by ADR 0003 in `release/provenance.json`, including reviewer/evidence and third-party material checks. This review is currently **pending**; creating the license, notices and SBOM does not assert it has occurred. No additional manual showcase expansion or SonarQube run is required for this candidate under the owner's September 8 decision.

After release approval, identify the exact tested source archive by SHA-256, set the corresponding `vVERSION` tag on its source revision, retain its generated-file manifest, and publish that archive with checksums, SBOM, changelog and validation evidence. Never replace a published version or move its tag; corrections require a new version. This workflow deliberately prepares artifacts only. Publication requires a separately authorized destination and completed provenance review.

Supported baseline: Bazel 8.3.1, J2CL 20260402, Elemental2 1.3.2 with the included patch, rules_closure 0.16.0, Bazel remote JDK 21, JDK 17.0.16+8 reference, and Playwright 1.62.0's Chromium/Firefox/WebKit revisions on Linux. This does not certify arbitrary vendor-browser versions, Safari on macOS, mobile browsers or untested operating systems.
