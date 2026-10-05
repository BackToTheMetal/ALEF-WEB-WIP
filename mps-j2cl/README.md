# MPS editor and typesystem runtime on J2CL

This workspace builds MPS OpenAPI, Kernel, the editor runtime and typechecking
for browsers with Bazel 8.3.1 and J2CL 20260402. MPS is pinned to
`d2589ed55bbc9445b660769fcc17da97e8e395a6` in `source-lock.json`.
The catalog contains all 107 MPS Java projects. The selected browser profile
contains 36 projects and 2,052 Java sources, retaining the initial 20-project
OpenAPI/Kernel closure. Desktop-heavy projects contribute only their recorded
Java subsets; Kotlin compiler/import support, ASM tooling and Gson bug reporting
are excluded from the browser dependency graph.

The browser host runs the real MPS editor, model commands, incremental type
engine and highlighter. Its handwritten expression language exercises structure,
behavior, property constraints, completion, intentions, selection, clipboard,
undo/redo and basic usage search. Mouse and keyboard input reach the actual cells;
model edits update inferred types and visible diagnostics. See
[validation](docs/validation.md) and [the editor plan](MPS-EDITOR-J2CL-PLAN.md)
for tested scope and remaining language-deployment work.

The completed validation passed all 54 steps: 353 Bazel targets and 199
browser/build runs, including all nine live-editor combinations. Recorded output
hashes match the final bundles; no validation steps were skipped.
These figures describe the original runtime/editor validation. The generated
financialCalculations application has a separate short smoke test, described below.

## Layout

```text
Codex/
├── java-io-j2cl/             existing portable JRE sources
├── java-nio-j2cl/
├── java-concurrent-j2cl/
├── java-awt-j2cl/            existing browser graphics implementation
├── javax-swing-j2cl/
├── gnu-trove-j2cl/
├── org-jdom-j2cl/
└── mps-j2cl/
    ├── BUILD.bazel          all MPS targets and browser verification targets
    ├── MODULE.bazel         toolchain and external dependency configuration
    ├── mps-projects.bzl     generated project roots, dependencies and exports
    ├── mps-rules.bzl        project-sized compilation rules
    ├── mps-overrides.bzl    explicit original exclusions and replacement paths
    ├── mps/                clean, pinned upstream checkout
    ├── mps-web/             sparse replacements with the same relative paths
    ├── third_party/         coordinated JRE patches and provenance
    ├── compatibility/       source inventory and compatibility data
    ├── tests/               shared JVM/browser fixtures and browser probes
    ├── tools/               setup, generation, audits and validation
    ├── docs/                runtime contracts and implementation details
    ├── .work/               ignored generator inputs and scratch
    └── out/                 ignored validation reports
```

No Bazel files are inserted into `mps/` or `mps-web/`. Each retained project has
one public `//:mps_<project>` target. The Kernel/Project/Constraints Runtime and
Actions/Editor/Intentions cycles each compile once through a private target,
with a public alias for each project. The build honors exported `.iml` module
and library dependencies; explicit browser source selections and revised
dependencies are recorded in `compatibility/browser-profile.json`.
The profile contains 99 explicit MPS replacements. The source audit checks every
replacement against its original and reports no duplicate or orphan providers.

## Build on Windows with WSL

Prerequisites: WSL Ubuntu, Git, Python 3, Java 21 and Bazelisk. Keep the seven
sibling repositories shown above beside this workspace. Their existing local
source trees are inputs; JRE composition and direct-library manifests check
the reviewed source identities. Bazel downloads its pinned remote tools.

From PowerShell in this workspace:

```powershell
.\tools\bootstrap.ps1
wsl -d Ubuntu -- bash -lc 'cd /mnt/c/Codex/mps-j2cl && bash tools/bazel.sh build //:mps_openapi //:mps_kernel'
```

Bootstrap clones the pinned checkout if absent, refuses to reset an existing
different or modified checkout, and verifies the direct AWT/Swing libraries,
shared runtime patches and hash-pinned J2CL baseline for generator checks. Builds use one job
and a shared J2CL build lock to limit memory use.
`out/latest-bazel.log` contains the latest build output.

The patches stored under `third_party/j2cl/` are applied by Bazel to its
external J2CL repository. They are composed in this order: sibling JRE
implementations, compiler/Class type support, Swing callback exceptions, registered ClassLoader support,
then the additional APIs needed by MPS. Every `java.*` JRE type is compiled
once. The compiler itself is built from the patched J2CL source.

## Run the editor demo

The demo uses a handwritten expression test language with the real MPS model,
editor cells, selection, undo and typechecking runtime. It is an in-memory
runtime demonstration, not the full MPS IDE or a generated-language build.
It runs its startup contracts before enabling the demo controls.

Build and serve the development bundle from PowerShell in this workspace:

```powershell
wsl -d Ubuntu -- bash -lc 'cd /mnt/c/Codex/mps-j2cl && bash tools/bazel.sh build //:editor_live_browser_dev'
wsl -d Ubuntu -- bash -lc 'cd /mnt/c/Codex/mps-j2cl && python3 tools/serve_editor_demo.py --development'
```

Open [the local demo](http://localhost:8799/). Use **Edit value** to select the
integer, then type; **Introduce type error** sets invalid text so the real
highlighter can report a diagnostic. Ctrl/Cmd+Z undoes an edit and
Ctrl/Cmd+Shift+Z redoes it while the editor has focus. **Close editor** disposes the runtime;
reload to create a fresh model. Stop the server with Ctrl+C. Use `--port 8800`
if the default port is occupied. To serve the optimized bundle, build
`//:editor_live_browser` and omit `--development`. The server snapshots its
bundle at startup, so restart it after rebuilding.

With the optimized demo server running, `node tools/test_editor_demo.mjs` checks
the served bundle identity, demo controls, native editing, diagnostics,
undo/redo and disposal. The broader live-editor matrix uses isolated pages.

### Generated financialCalculations editor

The financial sample uses its checked-in generated language and editor classes,
with BaseLanguage, core, traceable, execution-util and primitive-type support.
It opens an in-memory `Savings` calculator with inputs, arithmetic and an output
field. No MPS installation or regeneration is needed.

```powershell
wsl -d Ubuntu -- bash -lc 'cd /mnt/c/Codex/mps-j2cl && bash tools/bazel.sh build //:financial_browser_dev'
wsl -d Ubuntu -- bash -lc 'cd /mnt/c/Codex/mps-j2cl && python3 tools/serve_financial_demo.py'
```

Open [the financial editor](http://localhost:8800/). Click cells to edit;
Ctrl+Space opens completion and Alt+Enter opens intentions. Reload resets the
session. Restart the server after rebuilding. See
[deployment details](docs/financial-calculations.md) for the source profile,
original accessory-model loading and short Chromium smoke command.

## Runtime contracts

`Class.isInstance` is the dynamic operation corresponding to the requested
“Class.instanceof”; Java's `instanceof` operator remains available. The type
patch also implements `Class.isAssignableFrom`, `cast`, `asSubclass` and
reflective array allocation. Compiler-emitted superclass/interface relations
remain available with class-name metadata stripped. Native JavaScript types
without Java nominal identity have explicit limitations documented in
`third_party/j2cl/type-support/README.md`.

This is an ahead-of-time browser build. Register compiled module classes,
typed factories and resource bytes before loading them. JVM bytecode loading,
host JDK discovery, host files and archive extraction require browser backends
and are explicitly unavailable where no implementation exists. IO memory
streams and lexical paths are available; the NIO sibling has substantial
signature-only API coverage. Concurrency uses the sibling browser event loop,
inline execution and timers; it does not create JVM threads.

The browser regex profile supports the verified MPS constructs and rejects
unsupported constructs explicitly. Formatting covers
the verified MPS conversions and uses a deterministic English/ROOT profile.
Pinned English validation messages are embedded in affected MPS replacements.
`StringTokenizer`, required time APIs, browser event scheduling and selected
Swing/IntelliJ contracts have executable implementations and JVM/browser tests.

See [runtime factories](docs/runtime-factories.md),
[formatting](docs/formatting.md), [regex](tests/regex/README.md),
[UI dependencies and PNG output](docs/ui-dependencies.md), and
[the approved architecture and diagrams](MPS-J2CL-PLAN.md).

## Verification and regeneration

Run the complete build, source audits, JVM oracles and browser suites with:

```powershell
.\tools\validate.ps1
```

The equivalent WSL command is `python3 tools/validate.py`. Each step keeps its
exit status and log; `out/validation-summary.json` records the overall result.

Browser probes are built as root Bazel targets, then executed by
`node tools/test_browser.mjs <mode>` in WSL. Modes include `class`, `openapi`,
`core`, `runtime_support`, `api_basics`, `regex`, `classloader`, `runtime`,
`smodel`, `util`, `vfs`, `jdom` and `formatting`, plus the editor utility,
IntelliJ, Swing, typechecking and platform suites. The runner uses the existing
Playwright installation in `../org-jdom-j2cl` and runs Chromium, Firefox and
WebKit against development and optimized JavaScript. Metadata-sensitive suites
also have stripped-metadata builds. `tools/test_editor_live.mjs` exercises the
mounted MPS editor in all nine engine/build combinations. Chromium IME tests
use the browser input protocol; Firefox/WebKit verify the DOM composition
contract. Physical OS input-method candidate UI is outside those tests.
Results, output hashes and failures are written under `out/`.

`python3 tools/check_sources.py --report out/source-audit.json` checks the
complete selected source set, duplicate types, missing/orphan replacements and
Bazel package boundaries. `python3 tools/check_generated.py` additionally
checks regeneration and provenance. The latter requires the pristine J2CL
20260402 release extracted at `.work/j2cl-20260402`; `tools/fetch_j2cl.py`
prepares it using the archive SHA256 in `source-lock.json`. Existing baseline
files are never reset. The checks never edit upstream MPS or sibling sources.

Shared JVM oracle tools under `tools/run_*_oracle.py` verify committed golden
results against Java 21. Run these when changing the corresponding adapter;
browser compilation alone does not establish equivalent runtime behavior.
