# Validation results

Completed: 2026-09-09T18:38:57+00:00. All 54 validation steps passed; no steps were skipped.

- Bazel `build //:all`: **353 targets passed**.
- MPS closure: **36 projects**, **2052 Java files**.
- Sparse MPS replacements: **99**, with **zero source-audit errors**.
- Browser verification: **199 passing browser/build runs**; recorded hashes match the current bundles.
- Source pin: `d2589ed55bbc9445b660769fcc17da97e8e395a6`; Bazel `8.3.1`, J2CL `20260402`.

The validation command checks source ownership, reproducible generated metadata and patches,
stock-JVM golden results, browser behavior and optimized output. Ordinary browser suites run
on Chromium, Firefox and WebKit with development and ADVANCED JavaScript. Class, runtime,
typechecking, platform and live editor fixtures also run with STRIPPED class metadata. Explicit garbage collection uses Chromium test
instrumentation; dynamic-component lifecycle behavior runs on all three engines.

| Suite | Browser/build runs | Result |
| --- | ---: | --- |
| keystroke | 6 | PASS |
| swing_editor_controls | 6 | PASS |
| intellij_contracts | 6 | PASS |
| swing_editor_native | 6 | PASS |
| swing_editor | 6 | PASS |
| completion_matching | 6 | PASS |
| swing_utilities | 6 | PASS |
| editor_runtime_contracts | 6 | PASS |
| foundation_jre | 6 | PASS |
| typechecking_foundation | 9 | PASS |
| browser_platform | 9 | PASS |
| editor_utilities | 6 | PASS |
| kernel_utilities | 6 | PASS |
| modifiers | 6 | PASS |
| regex | 6 | PASS |
| class | 9 | PASS |
| core | 6 | PASS |
| jdom | 6 | PASS |
| openapi | 6 | PASS |
| classloader | 6 | PASS |
| smodel | 6 | PASS |
| util | 6 | PASS |
| api_basics | 6 | PASS |
| vfs | 6 | PASS |
| runtime | 6 | PASS |
| runtime_support | 9 | PASS |
| formatting | 6 | PASS |
| WeakSet GC | 2 | PASS |
| PNG decoding | 6 | PASS |
| Foundation GC | 2 | PASS |
| Dynamic components | 6 | PASS |
| Live MPS editor | 9 | PASS |

The core smoke test exercises actual MPS nodes and models, containment, properties, references,
indexing, events, imports, detach/reattach, collections and tuples. The live editor fixture uses
a handwritten test language with real MPS model repositories, cell construction, incremental
typechecking, commands, intentions, completion and browser input. These tests establish the
documented browser profile, not a complete desktop MPS IDE port. Host files/JAR extraction,
dynamic JVM bytecode loading, unrestricted reflection, the full Java regex language and desktop
threading are outside that profile. See [the README](../README.md) for exact contracts.

Reproduce with `python3 tools/validate.py` in WSL (or `tools/validate.ps1` from PowerShell), then
`python3 tools/summarize_validation.py`. Detailed machine-readable results and per-step logs are
under `out/validation-summary.json` and `out/validation-logs/`. The summary refuses skipped,
failed or stale browser results.
