#!/usr/bin/env python3
"""Validate library-owned JVM contracts and actual development/optimized browser bundles."""
import argparse
from pathlib import Path
import subprocess
import sys
import tempfile

ROOT = Path(__file__).resolve().parents[1]
def run(*args):
    subprocess.run([str(arg) for arg in args], cwd=ROOT, check=True)

def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--skip-build', action='store_true')
    args = parser.parse_args()
    (ROOT / 'out').mkdir(exist_ok=True)
    with tempfile.TemporaryDirectory(prefix='awt-class-oracle-') as temp:
        fixtures = ROOT / 'src/test/runtime/class_support'
        run('javac', '-d', temp, fixtures / 'FixtureTypes.java', fixtures / 'ClassSupportTest.java')
        actual = subprocess.check_output(['java', '-cp', temp, 'org.webawt.classsupport.ClassSupportTest'], text=True)
        if actual != (fixtures / 'jvm-expected.txt').read_text():
            raise SystemExit('Class support JVM oracle changed')
    if not args.skip_build:
        run('bazelisk', 'build', '//:class_support_browser', '//:class_support_browser_dev', '//:class_support_stripped', '--jobs=1', '--noshow_progress')
    run('node', 'tools/test_runtime.mjs', 'class')

if __name__ == '__main__':
    main()
