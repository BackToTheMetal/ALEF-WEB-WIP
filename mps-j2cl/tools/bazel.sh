#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
if [[ "${J2CL_BUILD_LOCK_HELD:-0}" != "1" ]]; then
  echo "Waiting for shared J2CL build lock"
  exec flock /tmp/j2cl-build.lock env J2CL_BUILD_LOCK_HELD=1 bash "$0" "$@"
fi
trap 'bazelisk shutdown >/dev/null 2>&1 || true' EXIT
mkdir -p out
bazelisk "$@" 2>&1 | tee out/latest-bazel.log
