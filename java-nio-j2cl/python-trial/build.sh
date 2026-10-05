#!/usr/bin/env bash
set -euo pipefail
trial_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$trial_dir"
if [[ "${J2CL_BUILD_LOCK_HELD:-0}" != "1" ]]; then
  echo "Waiting for shared WSL build lock /tmp/j2cl-build.lock"
  exec flock /tmp/j2cl-build.lock env J2CL_BUILD_LOCK_HELD=1 bash "$trial_dir/build.sh" "$@"
fi
export PATH="/usr/lib/jvm/java-21-openjdk-amd64/bin:$PATH"
mkdir -p reports
if [[ ! -f .cache/jdk-reference.jar ]]; then
  echo "Run generate.py first to create the NIO sources and JDK compile-time references." >&2
  exit 1
fi
bazelisk build //:nio-stubs "$@" 2>&1 | tee reports/bazel-build.log
install -m 644 bazel-bin/nio-stubs.js.zip reports/nio-stubs.js.zip
python3 check_output.py
