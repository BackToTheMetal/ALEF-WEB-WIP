#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."

# Shared with the IO/NIO/Trove migration workers. Never kill another workspace's
# Bazel server or change WSL configuration to make room for this build.
if [[ "${J2CL_BUILD_LOCK_HELD:-0}" != "1" ]]; then
  echo "Waiting for shared WSL build lock /tmp/j2cl-build.lock"
  exec flock /tmp/j2cl-build.lock env J2CL_BUILD_LOCK_HELD=1 bash "$0" "$@"
fi

python3 tools/check_sources.py
python3 tools/make_patch.py --check
bazelisk build @j2cl//jre/java:jre_bootclasspath
binary_root="$(bazelisk info bazel-bin)"
bazelisk build //tests/java/org/javaconcurrentj2cl:application
python3 tools/make_patch.py --check
python3 tools/check_coverage.py "$binary_root/external/j2cl+/jre/java/libjre_bootclasspath-hjar.jar"
browser_args=()
if [[ -n "${1:-}" ]]; then browser_args=(--browser "$1"); fi
python3 tools/browser_smoke.py "$binary_root/tests/java/org/javaconcurrentj2cl/application.js" "${browser_args[@]}"
# Release only this workspace's workers before the second Bazel output base.
bazelisk shutdown
python3 tools/check_consumer.py "${browser_args[@]}"
echo "java-concurrent-j2cl validation passed."
