# Financial calculations browser deployment

The checked-in generated Java for the upstream `samples/financialCalculations`
language builds with J2CL and displays its real MPS editor in the browser.
The development bundle passed the short Chromium smoke test.

This is a separate generated-language deployment profile, not an expansion of
the project-sized runtime profile or a claim to compile the complete `gensources`
project. Preserve upstream files. Record selected generated sources and browser
adaptations explicitly. Required aspects are structure, behavior, constraints,
typesystem, intentions, and the editor (including its supporting action menus).
Generation, text generation, migrations, desktop execution and saving models are
outside this application. BaseLanguage and core dependencies are recorded.

The calculator model is visible and editable in the browser; broader manual
acceptance of the language features belongs to the user.

## Build and run

From PowerShell in this workspace:

```powershell
wsl -d Ubuntu -- bash -lc 'cd /mnt/c/Codex/mps-j2cl && bash tools/bazel.sh build //:financial_browser_dev'
wsl -d Ubuntu -- bash -lc 'cd /mnt/c/Codex/mps-j2cl && python3 tools/serve_financial_demo.py'
```

Open http://localhost:8800/. The server snapshots the bundle at startup; restart
it after rebuilding. The development bundle is the first acceptance target.

The `Savings` model is created in Java using the real generated concept metadata:
two input fields, an interest calculation and an output field. It is a new
in-memory instance of the financial language, not an export of a bundled `.mps`
model. Reload resets the session. The bundled BaseLanguage primitive descriptor
accessory is included as opaque bytes and loaded by the original generated MPS
XML9 reader through the existing portable SAX implementation. Its node identities
and type relationships are preserved; upstream files are not modified.

`tools/inventory_financial.py` emits `financial-sources.bzl`, the language/factory
registration catalog, and `compatibility/financial-language-profile.json`.
`tools/prepare_financial_adapters.py` emits reviewed same-path adaptations under
`financial-web/`. These files preserve the upstream checkout. The language
bootstrap retains the requested aspects and supporting editor actions; the
BaseLanguage dataflow aspect is retained because typechecking uses it.
Type-system rule classes are registered from the actual aspect instances,
including anonymous generated operation-type providers, so each rule has its
language namespace in the browser.

The browser adaptations select the supported bootstrap aspects, report no
generated executable unit for the in-memory model, apply the dynamic-reference
check to editable memory models, replace two fixed diagnostic MessageFormat
templates with equivalent String.format templates, and remove defining-JVM-loader
diagnostics. Reflection over compiled JVM stub constants fails explicitly; model
expression evaluation remains in the original BaseLanguage behavior.
Two additional reader adaptations remove a dependency used only for a logger
category and inline the existing `ref` attribute constant. The XML9 parsing and
model construction code remains upstream code.

The shared browser editor handles Backspace/Delete, arrow keys (including Shift
selection), Home/End, Ctrl/Cmd+Home/End, Ctrl/Cmd+A, Enter/Shift+Enter, and
Ctrl/Cmd+Space directly through the current MPS selection's cell action. The
existing generated keymaps and completion popup handlers run first. Each action
keeps its own command and undo handling, and mutations check read-only state.
Eligibility reads end before execution so editing actions can open a command.
Disposal diagnostics are built only on failure, avoiding eager browser assertion
message evaluation against a live model's absent disposal trace.
This avoids loading desktop action registrations for basic editing. Tab and
Page Up/Down continue through the existing MPS keyboard handler; undo/redo remain
focused Swing bindings.

The short Chromium smoke runner is `node tools/test_financial_demo.mjs` in WSL.
It records the bundle hash, startup, inference of `DoubleType` for the interest
calculation, a native keyboard rename and undo, and reload in
`out/financial/smoke.json`, and saves a screenshot beside it. The test also checks
Backspace/Delete with undo, arrow movement, Shift selection, Home/End navigation,
focus isolation, and browser errors. This is not the
full runtime validation matrix or manual acceptance of all language features.
