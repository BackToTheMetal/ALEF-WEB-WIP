#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
if [[ "${J2CL_BUILD_LOCK_HELD:-0}" != 1 ]]; then
  exec flock /tmp/j2cl-build.lock env J2CL_BUILD_LOCK_HELD=1 bash "$0" "$@"
fi
mkdir -p out
log="out/bazel-$(date +%Y%m%d-%H%M%S)-$$.log"
bazelisk --host_jvm_args=-Xmx1024m "$@" 2>&1 | tee "$log" out/latest-bazel.log
