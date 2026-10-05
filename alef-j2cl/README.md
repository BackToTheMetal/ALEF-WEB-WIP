# ALEF languages on J2CL

This workspace integrates the generated Regelspraak, Gegevensspraak, Servicespraak,
and Testspraak language aspects with the sibling MPS, MPS extensions, and
WalkingKooka J2CL projects.
The requested profile consists of behavior, intentions, typesystem, constraints,
and editors, together with structure and their necessary Java dependencies.

The browser profile exposes `//:regelspraak`, `//:gegevensspraak`,
`//:servicespraak`, and `//:testspraak`. Chromium passes 67 ALEF language checks
in development and optimized builds as of 2026-09-17, including the four language
profiles and their standard models. The executable service/test example passes
in Chromium, Firefox, and WebKit in both build variants. The original editor,
completion, and error-gutter regressions also pass.
The earlier dependency baseline passed 59 sibling extension checks and the
composed JRE date/time, formatting, and IO contracts on 2026-09-16.

The ALEF checks execute both generated editors and their layout, leap-date
behavior, a derived constraint, linguistic rendering, translator dispatch,
MPS inference rules, parameterized intentions, and table/box clone contracts.
These are representative runtime checks, not exhaustive validation of all
generated rules. Results and bundle hashes are in `out/language-browser.json`
and `out/dependency-browser.json`; source ownership is audited separately in
`out/source-audit.json`.

## Editor example

The example displays the actual generated Gegevensspraak ObjectModel editor and
Regelspraak Regelgroep editor in a full-screen IDE workspace. `Huishouden` contains `Persoon` and
`Woning`, six typed attributes, the `Bewoning` relationship with two object
references, and a numeric parameter. `Bereken toeslag` contains the `Basistoeslag`
rule with a rule version, validity period, assignment, `(100 plus 25)` expression,
and a condition requiring the person's age to be at least 18. Its selections
reference the objects and attributes in the Gegevensspraak model.

Open roots from the project tree to use the Servicespraak and Testspraak editors in
the same workspace. `Toeslagservice` contains `BerekenToeslag`, an `Aanvraag` input
mapping from the person's age, a `Besluit` output mapping to the allowance, and a
reference to `Bereken toeslag`. The service test supplies age 21 and expects 125.
The browser API `alefExample.runTest()` executes the generated interpreters against the current
model. Changing the age to 17 or changing the rule's 100 to 200 makes that
expectation fail. All editors share the same model and undo history.

The workspace uses the whole viewport, with a project sidebar, root editor tabs,
package breadcrumbs, a generated MPS inspector, and a compact status bar.
Drag the project or inspector divider to resize it; focused dividers also support
arrow keys. Tabs support Left/Right, Home/End, and closing/reopening roots.
Demo buttons, paired-view switches, model readouts, and introduction cards have
been removed. Edit cells directly, use Alt+Enter for intentions, and use the
history toolbar or keyboard shortcuts for undo/redo (Ctrl+Z / Ctrl+Shift+Z).
The [Windows editor shortcut audit](docs/windows-editor-shortcuts.md) lists all
MPS editor bindings, their implementation locations, and browser limitations.
Ctrl+Y follows MPS and deletes the current line. Reloading starts a fresh
in-memory session; this example does not save models.

After preparing this workspace, run from its directory in Ubuntu/WSL:

```bash
npm ci --ignore-scripts
bash tools/bazel.sh build //:editor_example_browser_dev
python3 tools/serve_example.py --port 8810
```

Open <http://localhost:8810/>. The server serves the current development bundle,
so rebuilding only requires a page reload. The development bundle is large;
this target is intended for local development.

For the optimized Closure build, use:

```bash
bash tools/bazel.sh build //:editor_example_browser //:language_contracts_browser
python3 tools/serve_example.py --optimized --port 8811
```

The optimized JavaScript is `bazel-bin/editor_example_browser.js`. Verify it from
another WSL terminal with:

```bash
ALEF_EXAMPLE_VARIANT=optimized ALEF_EXAMPLE_URL=http://127.0.0.1:8811/ node tools/test_example.mjs
node tools/test_dependencies.mjs --languages --optimized
python3 tools/test_editor_adaptations.py
python3 tools/audit_editor_shortcuts.py
node tools/test_editor_shortcuts.mjs --optimized
```

Optimized editor reports and screenshots go to `out/example-optimized/`; language
contracts go to `out/language-browser-optimized.json`.
The shortcut test starts and stops its own isolated example server. Its native
keyboard report goes to `out/editor-shortcuts/native-optimized.json`. Workbench
shortcuts execute original MPS action classes; the browser bindings and source
selection are generated from the upstream action registration.

The four-language optimized bundle measured on 2026-09-17 is 16,211,987 bytes
(16.21 MB), or 3,278,375 bytes (3.28 MB) with gzip, compared with approximately
218 MB for the development bundle. Copies are in `out/example-optimized/app.js` and
`out/example-optimized/app.js.gz`; exact measurements and the SHA-256 hash are
in `out/example-optimized/size.json`.

The Java application is in `example/AlefEditorApp.java`, with its font scheme in
`example/ExampleTheme.java`. `example/entry.js` exposes the Java controls to the
HTML toolbar, while the generated editor cells run through `BrowserEditorHost`
and the sibling Swing/WebAWT libraries. All example source and build metadata
remain outside `alef/`.

The project sidebar is the Vue 3 `<mps-project-view>` custom element. It lists
the example solution's actual MPS models and root nodes, grouping each model's
roots by `BaseConcept.virtualPackage`. Selecting a root opens its generated
editor in a tab; previously opened roots remain available in the tab strip. The tree
updates after editing and undo/redo, supports filtering and keyboard navigation,
and uses stable serialized MPS node references rather than names as identity.
Vue is pinned in `package-lock.json` and served locally, with no runtime CDN.
See `example/PROJECT_VIEW.md` for the reusable component and bridge contract.

```bash
ALEF_EXAMPLE_URL=http://127.0.0.1:8810/ ALEF_BROWSERS=chromium,firefox,webkit npm run test:project-view
```

Project-view reports and screenshots are written to `out/project-view/`.

Use **+ New root** in the project sidebar to select an MPS root type, name, and
optional virtual package. The form prefills the focused package. MPS node
factories initialize the new root, which opens in its generated editor and shares
the existing undo/redo history. Root creation is in memory, like other edits.
Verify it with `ALEF_BROWSERS=chromium,firefox,webkit node tools/test_create_root.mjs`
(set `ALEF_EXAMPLE_URL` to the server URL; this test defaults to port 8812).

Right-click a model, package, or root for **New root…** (also available through
Shift+F10); a root's context menu also offers **Open**. The **Inspector** below the
editors follows the selected MPS node and displays the generated language
inspector. It supports hide/show and shares the editor's undo history.
`tools/test_project_inspector.mjs` covers context-menu navigation, creation in the
clicked package, inspector selection, visibility, and cleanup.

Ctrl-click a reference to open its containing root and reveal the referenced node.
Existing tabs are reused and the inspector follows the target. Holding Ctrl while
hovering highlights the reference with a blue underline and hand cursor. Plain
clicks select for editing; Ctrl/Cmd+B navigates from the keyboard.
`tools/test_reference_navigation.mjs` checks navigation and ordinary editing.

Typing `plus` at the end of a numeric cell creates an addition and selects its
new operand. The compiled grammar-menu lookup and its regression checks are
documented in [Grammar-cell side transformations](docs/grammar-cells-side-transformations.md).

Verify the IDE layout and native interactions with `ALEF_BROWSERS=chromium,firefox,webkit node tools/test_ide.mjs` (defaults to port 8812, configurable with `ALEF_EXAMPLE_URL`). Screenshots are in `out/ide/`.

With the server running, verify the editor runtime from another WSL terminal:

```bash
node tools/test_example.mjs
```

The smoke test uses the sibling `org-jdom-j2cl` Playwright installation. Chromium
checks the current bundle, generated editor initialization, model descendants,
resolved references, inferred types, native cell editing, keyboard and toolbar
undo/redo, the generated sorting intention, focus isolation, resize/reset, and
runtime disposal. The report
and screenshots are in `out/example/`; these exercise the two example models,
not every editor or rule in the languages.

The service/test execution and editor checks are run with:

```bash
python3 tools/test_translator_bindings.py
ALEF_EXAMPLE_URL=http://127.0.0.1:8810/ node tools/test_service_test.mjs
```

Set `ALEF_BROWSERS=chromium,firefox,webkit` to exercise all three browser engines.
Reports and screenshots go to `out/service-test/`. For an optimized server, also
set `ALEF_EXAMPLE_VARIANT=optimized`; its reports go to `out/service-test-optimized/`.
The checks cover native entry-point/input editing, resolved references, mapped
types, passing and failing service execution, correction, and rule edits/undo.

Servicespraak and Testspraak use the same browser aspect profile as the original
languages: structure, behavior, constraints, typesystem, intentions, and editors.
The service/test interpreter and translator dependencies use generated typed
bindings instead of Java reflection, including nested generic mapping arguments,
overloaded signatures, and imported translators declared in other source files.
XML import uses the existing JetBrains JDOM browser provider (`org.jdom`).
Browser consumers install `AlefStandardModels` in their MPS project inside a
write action. These read-only models are generated from ALEF's `gegevensspraak.base`
and `standaard.funcs`, plus the Regelspraak, Servicespraak, and Testspraak runtime
slot models, retaining the original persistent IDs used by the interpreter.
The example and language contracts install them during model initialization.
Compiled model-import metadata preserves Testspraak's completion filters without
requiring desktop language source modules.

Desktop MPS generation, XSD registration/comparison, and schema validation remain
outside this browser profile. Schema commands fail explicitly; XSD nodes carry a
warning that validation requires desktop MPS. The generator-context-only helper
overloads are excluded from the browser source copies. Desktop plugin menus,
migrations, project hygiene, and refactorings are not registered. This does not
add a deployed SOAP/REST endpoint, filesystem persistence, or the separate
`servicespraak.tijd` and `testspraak.tijd` language variants.

Completion selection uses local Swing overrides in `j2cl-src/swing/`: arrow keys
repaint only the changed selections, and custom renderers paint directly into
browser canvases. To check reference-picker navigation and selection performance:

```bash
ALEF_ASSERT_INCREMENTAL=1 node tools/benchmark_completion.mjs after
bash tools/bazel.sh build //:list_selection_browser_dev
node tools/test_list_selection.mjs
```

The benchmark measures synchronous key handling on the six-item attribute
reference picker. Reports and screenshots go to `out/completion-performance/`.
The list contracts check selection events, accessibility, model changes, renderer
pixels, and resizing in Chromium, Firefox, and WebKit at device scale 2.

Measured on 2026-09-17 in headless Chromium with the development bundle at a
1500-by-1150 viewport: median synchronous keydown handling fell from 122 ms to
3.7 ms (32 alternating arrow-key samples, first four excluded from timing
statistics). All six row elements survive selection changes; only the old and
new selections are repainted. These timings describe this local example, not a
latency guarantee for arbitrary models or devices.

Name editing synchronously refreshes dependent editor cells, including references
in the other model. The local WebAWT override in `j2cl-src/awt/` reuses repeated
font measurements during this work. Its bounded cache expires on a timer and
bypasses custom FontFace sets, so font changes retain native measurement behavior.

```bash
bash tools/bazel.sh build //:font_measurement_browser_dev
node tools/test_font_measurements.mjs
node tools/benchmark_name_edit.mjs current
ALEF_PROFILE=1 node tools/benchmark_name_edit.mjs profile
ALEF_DISABLE_MEASUREMENT_CACHE=1 node tools/benchmark_name_edit.mjs uncached
```

The name benchmark alternates 24 character insertions and deletions, checking
that each key edits once and linked references update immediately. It excludes
the first four samples from timing statistics. Reports and optional Chrome CPU
profiles go to `out/name-edit-performance/`. Set `ALEF_EXAMPLE_URL` to benchmark
the optimized server. `ALEF_DISABLE_MEASUREMENT_CACHE=1` adds an unused custom
font to exercise the uncached path in the same build without changing editor
fonts. Timings measure synchronous key handling, excluding subsequent painting.

Measured on 2026-09-17 in headless Chromium, with cache-off and cache-on runs of
the same build and no CPU profiler:

| Build | Cache off, median keydown | Cache on, median keydown | Cache on, p95 |
| --- | ---: | ---: | ---: |
| Development | 50.7 ms | 41.6 ms | 47.2 ms |
| Optimized | 28.1 ms | 24.1 ms | 28.5 ms |

This removes repeated measurement work, while MPS dependency updates and
generated linguistic rendering still account for much of the remaining time.
The optimized build is preferable for interactive use. These local measurements
are specific to the example models and device, not a general latency guarantee.

## Source ownership

* `alef/`: clean checkout of `belastingdienst/ALEF`, pinned in `source-lock.json`.
* `upstream-src/`: unmodified generated Java, preserving its ALEF-relative paths.
* `j2cl-src/alef/`: adapted copies at the same relative paths. The generated
  `alef-sources.bzl` selects each replacement instead of its original.
* `j2cl-src/jre/`: any additional Java platform adaptations.
* `j2cl-src/mps/`, `j2cl-src/extensions/`: sparse sibling source adaptations.
* Root `BUILD.bazel`, `MODULE.bazel`, `*.bzl`, `tools/`, `third_party/`: build metadata.
* `.work/`: dependency views and a link to the disposable headless generation tree.
* `out/`: logs, generated source hashes, source selection and audit reports.

No Bazel metadata or adaptations are placed inside `alef/`. Sibling repositories
are read as inputs and are not modified. Desktop-only aspects are not initial
source-selection roots; a helper is retained if the requested aspects reference it.

## Generate Java with MPS

Use Ubuntu/WSL, Python 3, Git, Java 21, and `xvfb-run`. The preparation script
downloads Maven 3.9.11, verifies its published SHA512, and uses ALEF's pinned
MPS 2025.1 and extensions 2025.1.3226.9d3d593 distributions. The task-owned
generation cache is on native WSL storage at
`~/.cache/alef-j2cl/96dd99ff70cb`; `.work/generation` points to it.

From this directory in WSL:

```bash
python3 tools/generate_sources.py --stage dependencies
bash tools/generate.sh
python3 tools/configure.py
```

The original Git checkout is archived into a disposable generation copy.
Only generation metadata in that copy is adjusted to avoid downloading JVMs
for operating systems that are not used. Upstream Ant generators build the
helper plugins, then generate ALEF's language code. Tests and standalone IDE
packaging are outside this generation operation. Generated originals and their
SHA256 values are copied out for the J2CL build.

Alternatives to headless generation are a matching existing generated tree, or
MPS 2025.1 from JetBrains' previous-releases page. Interactive generation follows
ALEF's README: prepare dependencies, open the project, set the `alef.home` path
variable, then rebuild. Do that in a separate copy to preserve `alef/` here.

## Prepare and build

```bash
python3 tools/configure.py
bash tools/bazel.sh build //:regelspraak //:gegevensspraak
bash tools/bazel.sh build //:dependency_contracts_browser_dev
node tools/test_dependencies.mjs
bash tools/bazel.sh build //:language_contracts_browser_dev
node tools/test_dependencies.mjs --languages
```

The current profile selects 2,476 ALEF Java sources, 130 additional extension
sources, and 246 additional MPS sources. The source audit checks the clean pinned
checkout, all 5,435 generated-file hashes, duplicate ALEF types, and the exclusion
of originals for selected overrides. The two public targets compile the same
shared unit; use either label as a dependency in this workspace.

The preparation uses the existing MPS extensions dependency view for the MPS
access adaptations. It creates local Swing and WebAWT views: ParseException
comes from WalkingKooka, and WebAWT's five text classes move into the composed
JRE so all `java.text` types have one module owner. `tools/compose_jre.py` applies
the MPS patches, preserves their IO providers, then incorporates WalkingKooka's
time, locale, and formatting sources, including the required module exports.
The ownership decisions are recorded in
`third_party/j2cl/composition.json`.

The two public language targets share a compilation unit because the languages
and their runtime helpers depend on each other. The additional table/cell-layout
sources come from the sibling extensions archive. Their reflective access to
MPS cells is replaced by typed access to six copied MPS classes, with base hashes
recorded in `mps-access.json`. Layout override metadata is collected from the
compiler inputs. Table cloning preserves concrete prototypes and shallow fields,
then retains the upstream deep-copy steps; J2CL identity hashes are not copied.
The copied `CalcOnWriteFrame` also fixes an upstream discarded clone result,
so updating a cloned frame does not update the original.

`tools/adapt_editor_updates.py` completes cell rebuilds with queued layout and
repaint, and observes temporary model changes outside commands. It preserves the
extension's existing updater access override. See
[the editor refresh investigation](docs/editor-refresh-investigation.md) for the
test-result paths, debug navigation fix and browser regression coverage.

Two additional MPS completion overrides in `mps-access.json` preserve the native
name/icon and right-aligned description layout while using a smaller, regular,
muted font for reference provenance. Selected descriptions retain contrast, and
MPS matching highlights and custom name styles remain intact. The popup follows
the filtered result count and reserves space for a horizontal scrollbar when
needed. `tools/test_completion_style.mjs` checks real reference completions in
Chromium, Firefox, and WebKit; `tools/benchmark_completion.mjs` checks selection,
accept/cancel, undo, and incremental row repainting.

Intention action popups use regular 14px text, aligned lightbulb icons, and blue
selection with the first enabled action selected on opening. Labels are measured
with their actual font so long actions fit. Consumer-owned IntelliJ overrides
and icon provenance are documented in `j2cl-src/intellij/README.md`;
`tools/test_intention_style.mjs` checks styling and real intention execution,
cancellation, keyboard navigation, and undo across the three browser engines.

Translator methods, constructors, extension metadata, and linguistics renderer
constructors have generated typed bindings. Java reflection and method handles
are replaced at their actual call sites. Translator subclasses in the selected
languages are included even when loaded dynamically by the original code.
Abstract-class metadata is generated from compiled dependency headers and the
selected sources. Quick-fix constructors and typesystem rule namespaces are
registered explicitly by `AlefLanguages`.

Icon provider packages and module origins are registered from generated metadata.
`tools/editor_metadata.py` evaluates ALEF's terminal punctuation query against
the original editor models and writes the `PuntConcept` replacement in
`j2cl-src/alef/`; the browser does not need desktop language source modules for
that query. Quick-fix factories include inherited languages such as `contexts`.

Regelspraak and Gegevensspraak retain all five requested aspects and structure.
Their inherited languages are included through descriptor dependencies.
`rapporten` contributes structure and presentation only: its separate report
editor, behavior, and ZIP export actions are outside this profile and are not
referenced by the requested aspects. Generation, migration, find usages, scripts,
refactorings, text generation, and project plugin registration are omitted unless
a selected Java helper needs them.

The inherited MPS text language includes its editor, behavior, constraints,
intentions, typesystem, actions, and structure. The MPS structure language supplies
its structure, presentation, and behavior descriptors; editing language definitions
is outside this ALEF profile. Classifiers, smodel, descriptor, sharedConcepts,
modelapi, collections, and closures contribute structure descriptors for core runtime checks.
These nine profiles
use copied `Language.java` files and are recorded in `out/mps-language-profiles.json`.

Browser host integration points:

* `BrowserEnvironment.set` supplies values for dynamic property/environment
  lookups; an unset value keeps ALEF's original `[name]` placeholder.
* `BrowserMessages.setListener` receives ALEF/ALEF-DEV messages with severity;
  `history()` retains them for a host message view. Existing console output remains.
* The time slider uses the browser range control, a live year label, and hierarchy
  events. Desktop slider tick painting is replaced by that label.
* Editor fonts use `SansSerif` when `os.name` is absent; theme colors use the
  current editor background rather than a desktop scheme name.
* Git subprocess helpers, desktop path-macro setters, and diff-editor special
  casing are outside this profile. Ordinary model/editor helpers remain.

The generated-source target disables Error Prone lint checks, while javac and
J2CL type checking remain enabled. Upstream lint findings such as integer
overflow in `Tijdseenheid__BehaviorDescriptor` are not silently rewritten as part
of this compatibility build. Browser smoke checks do not establish correctness
of every generated rule or replace a full ALEF acceptance suite.

Both headless generation and Bazel take `/tmp/j2cl-build.lock`. Bazel runs one
job with bounded resources. Neither wrapper shuts down another workspace's server.

MPS source/runtime and extension revisions in the siblings are newer than ALEF's
generation distribution. Compatibility must be established by compilation and
browser execution, rather than assumed from matching class names.

