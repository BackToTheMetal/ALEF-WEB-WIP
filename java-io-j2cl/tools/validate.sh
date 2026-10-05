#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")/.."

if [[ "${J2CL_BUILD_LOCK_HELD:-0}" != "1" ]]; then
  exec flock /tmp/j2cl-build.lock env J2CL_BUILD_LOCK_HELD=1 bash "$0" "$@"
fi

python3 tools/check_sources.py
python3 tools/generate_patch.py --check
bazelisk test //:all-tests
bazelisk build //tests/java/com/javaioj2cl:consumer-j2cl
python3 tools/package_release.py
python3 tools/package_release.py --check

echo "java-io-j2cl validation passed."
