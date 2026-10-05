# Shared editor utilities

`StringTokenizer` is a real shared JRE implementation, used unchanged by the
MPS editor-language `SelectionUtil`. It supports all three constructors,
default/custom/null delimiters, returned delimiters, delimiter changes, token
counting and the `Enumeration` API. Delimiters use Unicode code points, including
supplementary characters and unmatched surrogate code units. It does not use a
regular expression or produce empty tokens between adjacent delimiters.

`EditorUtilitiesTest` runs the same source on the JVM and in the browser. The
committed Java 21 oracle contains 520 exact observation rows. It checks repeated
lookahead/counting, cursor state after exhaustion, replacement delimiters and
recovery from null delimiters, as well as ordinary and Unicode tokenization.

The differential runner also patches the JVM's `java.base` with the actual
browser tokenizer source and compares every output row with the standard JVM.
An empty tokenizer's `countTokens()` returns zero even when its delimiter is
null; other scanning methods throw. That edge case was caught by the oracle.

```sh
python3 tools/run_editor_utilities_oracle.py --check
bash tools/bazel.sh build //:editor_utilities_browser //:editor_utilities_browser_dev
node tools/test_browser.mjs editor_utilities
```

The root `editor-jre-tests.bzl` declares the targets through `editor_jre_tests()`.
No BUILD files are added inside the upstream checkout or its sparse replacements.
The browser runner checks both development and optimized outputs in Chromium,
Firefox and WebKit. Oracle validation alone does not establish browser success;
the browser result report records that separately.
