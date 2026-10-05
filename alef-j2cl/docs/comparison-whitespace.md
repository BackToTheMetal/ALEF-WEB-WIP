# Diff whitespace highlighting

On 2026-09-29, package v1.8.1's Contributieregels comparison showed green
backgrounds on 16 blank generated MPS cells. Structural changes marked the
entire rendered label as changed, including empty and whitespace-only labels.

`BrowserComparisonEditor` now filters the presentation highlight ranges through
`ComparisonTextDiff.withoutWhitespace`. Spaces, tabs, newlines and Unicode space
characters stay uncoloured, including gaps inside changed labels. Empty ranges
are omitted. Node identity and text comparison results remain intact, as do
native model content, cell geometry, and the regular editor's styling.

Validation: the Java text-diff checks passed, the optimized J2CL/Closure browser
build succeeded, and `tools/test_mps_diff_whitespace.mjs` verified the native MPS
views using the rebuilt bundle. All four real package roots had no whitespace
highlight ranges. Response-only fixtures verified red/green word replacements,
added/removed suffixes, and whitespace-only changes without changing live models
or packages. The affected rules view now has 42 highlighted text cells and zero
blank highlighted cells. A rendered screenshot is retained in
`out/diff-whitespace/rules-rendered.png`.

The rebuilt bundle is installed in `out/backend-web/app.js`; its served SHA-256
matches `out/backend-browser-artifacts.json`. The previous bundle is retained
under `out/diff-whitespace/app-before.js`.
