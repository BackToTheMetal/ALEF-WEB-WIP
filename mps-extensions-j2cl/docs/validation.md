# Runtime validation

Verified on 2026-09-16 using WSL Ubuntu, Bazel 8.3.1 and J2CL 20260402.

The eight runtime libraries compile and transpile against the sibling MPS browser
profile. The selected runtime inventory contains 149 Java classes. There are 20
package-correct Java files in `j2cl-src`, including eight MPS access replacements.

| Browser | Development | Optimized | Stripped metadata |
| --- | --- | --- | --- |
| Chromium | 59 checks passed | 59 checks passed | 59 checks passed |
| Firefox | 59 checks passed | 59 checks passed | 59 checks passed |
| WebKit | 59 checks passed | 59 checks passed | 59 checks passed |

All nine runs completed without page errors or console errors. Exact bundle hashes
and per-run results are in `out/browser-results.json`.

The contracts exercise:

- Every supported static MPS field/method bridge, including actual object identity,
  substitution output concepts and label selection replacement. Unknown reflection
  operations fail explicitly.
- Query-list context nesting and exception restoration, cell construction and
  insertion-role metadata.
- Grammar tokenization, parser checkpoints, successful parsing and token rejection,
  compiled descriptor discovery and registration, and specialized containment lookup.
- Conditional-editor selection with explicit class registration and cache invalidation.
- The generated arbitrary-text annotation editor, deletion and reparsing of attached
  model nodes, last-annotation deletion on a model root, and handler disposal/reinitialization.
- Math cell style initialization, layout and geometry, drawing to browser-backed
  images, and built-in sum, integral and parenthesis symbols.
- Default checkbox drawing and command-based toggling of an attached MPS model.

`tools/bootstrap.py` was exercised with the pinned inputs already downloaded. It
re-extracted and verified 4,997 generated Java sources, regenerated the compatibility
overlays, and recreated the consumer-owned MPS build view. Source hashes, package
paths, unique selected type ownership, upstream Git cleanliness and absence of
Bazel metadata inside `mps-extensions/` are checked by `tools/check_sources.py`.
The final audit is recorded in `out/source-audit.json`.

The tests use a handwritten language fixture and the real generated grammar runtime
language. They do not deploy the extensions' full language-design editors,
generators, sandboxes or desktop IntelliJ plugins. Custom checkbox resource loading,
all individual math-symbol shapes and complete end-user language applications have
not been exercised by this suite. Browser applications must register their compiled
languages, grammar descriptors and conditional-editor classes as described in
`README.md`.

Reproduce with `bash tools/validate.sh` after bootstrap. The Bazel wrapper serializes
builds through the shared `/tmp/j2cl-build.lock`.
