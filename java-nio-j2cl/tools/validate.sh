#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
if [[ "${J2CL_BUILD_LOCK_HELD:-0}" != "1" ]]; then
  echo "Waiting for shared WSL build lock /tmp/j2cl-build.lock"
  exec flock /tmp/j2cl-build.lock env J2CL_BUILD_LOCK_HELD=1 bash "$0" "$@"
fi
python3 tools/check_sources.py
python3 tools/make_patch.py --check
bazelisk build //:java-nio //src/test/consumer:application //src/test/mps/supported:mps-readable
binary_root="$(bazelisk info bazel-bin)"
python3 tools/check_api.py "$binary_root/external/j2cl+/jre/java/libjre_bootclasspath-hjar.jar"
python3 tools/test_api_gate.py "$binary_root/external/j2cl+/jre/java/libjre_bootclasspath-hjar.jar"
python3 tools/check_mps_boundary.py
browser="${1:-}"
if [[ -z "$browser" ]]; then
  for candidate in "$HOME"/.cache/ms-playwright/chromium-*/chrome-linux*/chrome; do
    if [[ -x "$candidate" ]]; then browser="$candidate"; fi
  done
  # Prefer the dedicated headless runner when available.
  for candidate in "$HOME"/.cache/ms-playwright/chromium_headless_shell-*/chrome-headless-shell-linux*/chrome-headless-shell; do
    if [[ -x "$candidate" ]]; then browser="$candidate"; fi
  done
fi
if [[ -z "$browser" ]]; then echo "Pass a Chromium executable path as the first argument." >&2; exit 1; fi
python3 tools/browser_smoke.py "$binary_root/src/test/consumer/application.js" --browser "$browser"
python3 tools/package_release.py
echo "NIO profile validation passed; see compatibility/mps-boundary.json for MPS limits."
