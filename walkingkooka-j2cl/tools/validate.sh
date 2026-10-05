#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
if [[ "${J2CL_BUILD_LOCK_HELD:-0}" != 1 ]]; then
  exec flock /tmp/j2cl-build.lock env J2CL_BUILD_LOCK_HELD=1 bash "$0" "$@"
fi
mkdir -p out/jdk
rm -f out/browser-results.json out/consumer-results.json
python3 tools/check_metadata.py
python3 tools/overlay.py --check
javac -d out/jdk tests/TimeContracts.java
java -cp out/jdk walkingkooka.integration.tests.TimeContracts > out/jdk-reference.txt
bazelisk build //tests:browser_dev //tests:browser
node tools/test_browser.cjs
python3 tools/package_release.py
python3 tools/test_consumer.py
python3 tools/record_validation.py
