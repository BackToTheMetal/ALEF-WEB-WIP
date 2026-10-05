#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
# Shared with java-io-j2cl. The lock is held for the complete child command.
if [[ "${J2CL_BUILD_LOCK_HELD:-0}" == "1" ]]; then exec "$@"; fi
exec flock /tmp/j2cl-build.lock env J2CL_BUILD_LOCK_HELD=1 "$@"
