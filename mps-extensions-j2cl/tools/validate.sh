#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
python3 tools/check_sources.py
bash tools/bazel.sh build //:reflection //:hacks //:querylist //:boolean_editor //:math_editor //:math_symbols //:grammarcells //:grammarcells_language //:contracts_browser_dev //:contracts_browser //:contracts_stripped
node tools/test_browser.mjs
