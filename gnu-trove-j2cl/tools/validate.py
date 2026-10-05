#!/usr/bin/env python3
"""Validate all stages under one shared lock, with guaranteed Bazel cleanup."""
from datetime import datetime, timezone
import json
from pathlib import Path
import subprocess
import sys
from build_support import build_lock, bazel_session, invalidate_results
from test_consumer import run_consumer

ROOT = Path(__file__).resolve().parents[1]


def run(*command):
    subprocess.run(command, cwd=ROOT, check=True)


def main():
    with build_lock():
        (ROOT / 'out').mkdir(exist_ok=True)
        invalidate_results(ROOT, ['validation-results.json', 'browser-results.json',
                                  'browser-regressions.json', 'consumer-result.json', 'jvm-results.json'])
        for command in [('check_sources.py',), ('port_sources.py', '--check'),
                        ('generate_family_tests.py', '--check')]:
            run(sys.executable, 'tools/' + command[0], *command[1:])
        run(sys.executable, '-m', 'unittest', 'discover', '-s', 'tools/tests', '-v')
        run(sys.executable, 'tools/test_jvm.py')
        run(sys.executable, 'tools/check_api.py')
        with bazel_session(ROOT):
            run('bazelisk', 'build', '//:trove', '//:trove-j2cl',
                '//tests:browser_application', '//tests:browser_application_dev')
            run('node', 'tools/test_browser.cjs')
        run(sys.executable, 'tools/package_source.py')
        # Direct call reuses our lock. The standalone consumer acquires its own.
        run_consumer()
        consumer = json.loads((ROOT / 'out/consumer-result.json').read_text())
        results = {'status': 'passed', 'completedAt': datetime.now(timezone.utc).isoformat(),
                   'entryPoint': 'tools/validate.sh', 'sharedBuildLock': '/tmp/j2cl-build.lock',
                   'archiveSha256': consumer['archiveSha256']}
        (ROOT / 'out/validation-results.json').write_text(json.dumps(results, indent=2) + '\n')
        print('Validation passed. Results and source archive are in out/.', flush=True)


if __name__ == '__main__':
    main()
