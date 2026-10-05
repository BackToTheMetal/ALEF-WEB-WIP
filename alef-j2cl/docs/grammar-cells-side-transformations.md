# Grammar-cell side transformations

Typing `plus` after a numeric literal used to leave invalid text such as `25plus`
in the numeric cell. The generated grammar already described the addition and
the editor dispatched normal keyboard events correctly.

The missing link was
`com.mbeddr.mpsutil.grammarcells.runtime.menu.GrammarCellsTransformationMenuPart`
in the sibling `mps-extensions-j2cl` runtime. Its descriptor lookup first called
the generated `getModule(repository)` hook. That hook resolved the language's
source module, which is absent when the browser deploys compiled language
descriptors. Resolving it returned `null`, so the menu silently returned no
transformation actions, although `DescriptorCache` held the correct descriptor.

The runtime now provides `getModuleReference(repository)` and looks up the
descriptor directly by that reference. Its default implementation preserves the
existing module-based hook for other consumers. The runtime overlay and its
`tools/make_overlays.py` generator live in `../mps-extensions-j2cl`.

`tools/adapt_grammar_menus.py` adds the reference hook to the selected generated
Gegevensspraak, Regelspraak, and Linguistics menus. It runs during
`tools/configure.py`; the copies live under `j2cl-src/alef`. Both upstream
checkouts and generated originals remain unchanged. There are no special cases
for a particular operator or expression in the fix.

`tests/LanguageContracts.java` checks the actual generated numeric cell without
a desktop language module, a unique `plus` action, keyboard transformation,
operand preservation, caret selection, undo/redo, and subsequent right-operand
entry. The original code failed at the assertion that the `plus` action exists.

Build and open the regression in a browser from Ubuntu/WSL:

```sh
bash tools/bazel.sh build //:language_contracts_browser_dev //:language_contracts_browser
python3 tools/serve_language_contracts.py
# Use --optimized --port 8814 to check the optimized artifact separately.
```

The existing automated entry point is `node tools/test_dependencies.mjs --languages`
(add `--optimized` for the optimized artifact). The local manual test page prints
the same language-contract result. The standard editor example also allows the
operation to be checked with native browser keyboard input.

Verified on 2026-09-30: both development and optimized builds pass 78 language
checks. Chrome native keyboard entry also creates the expression and accepts the
new right operand in both builds. The ALEF and MPS-extensions source audits pass.
Evidence is in `out/grammar-cells/validation.json` and its adjacent screenshots.
The optimized application served at `http://localhost:8080/app.js` matches the
built and packaged bundle (SHA-256
`5c1662ebb36de13d4f4359a80f300e8c01901b7d72d0fc843df9cb922815a55f`).
