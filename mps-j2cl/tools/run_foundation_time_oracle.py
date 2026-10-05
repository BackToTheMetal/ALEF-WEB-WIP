#!/usr/bin/env python3
"""Differential validation of the browser Instant/Duration profile against Java 21."""
import argparse
import difflib
import json
from pathlib import Path
import shutil
import subprocess
import tempfile

ROOT = Path(__file__).resolve().parents[1]

def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--java', default='java')
    parser.add_argument('--javac', default='javac')
    parser.add_argument('--check', action='store_true')
    args = parser.parse_args()
    base = ROOT / '.work/foundation-time-oracle'
    base.mkdir(parents=True, exist_ok=True)
    with tempfile.TemporaryDirectory(dir=base) as temporary:
        temporary = Path(temporary)
        classes, sources, patch = (temporary / name for name in ['classes', 'sources', 'patch'])
        classes.mkdir(); patch.mkdir()
        source_root = ROOT / 'third_party/j2cl/api-support/jre/java/java/time'
        shutil.copytree(source_root, sources / 'java/time')
        source = ROOT / 'tests/typechecking/FoundationTimeOracle.java'
        subprocess.run([args.javac, '-encoding', 'UTF-8', '-d', str(classes), str(source)], check=True)
        java = [args.java, '-ea', '-cp', str(classes), 'mpsj2cl.typechecking.FoundationTimeOracle']
        expected = subprocess.check_output(java, text=True)
        subprocess.run([args.javac, '--patch-module', 'java.base=' + str(sources), '-d', str(patch)]
                       + [str(p) for p in sorted(sources.rglob('*.java'))], check=True)
        # Compile the observation source against the profile too: between accepts
        # Instant directly because this browser profile does not implement Temporal.
        subprocess.run([args.javac, '--patch-module', 'java.base=' + str(patch), '-d', str(classes), str(source)], check=True)
        actual = subprocess.check_output([args.java, '--patch-module', 'java.base=' + str(patch)] + java[1:], text=True)
        if actual != expected:
            raise SystemExit(''.join(difflib.unified_diff(expected.splitlines(True), actual.splitlines(True), fromfile='Java21', tofile='browser overlay')))
    baseline = ROOT / 'tests/typechecking/time-jvm-expected.txt'
    if args.check:
        if baseline.read_text() != expected: raise SystemExit('Stale time oracle baseline')
    else:
        baseline.write_text(expected, newline='\n')
    version = subprocess.run([args.java, '-version'], capture_output=True, text=True, check=True).stderr.splitlines()[0]
    report = {'passed': True, 'rows': len(expected.splitlines()), 'jvm': version,
              'scope': 'Instant full-range ISO, epochs, nanosecond normalization, arithmetic, ordering, hashes; Duration arithmetic and units'}
    (ROOT / 'out').mkdir(exist_ok=True)
    (ROOT / 'out/foundation-time-oracle.json').write_text(json.dumps(report, indent=2) + '\n')
    print(json.dumps(report))

if __name__ == '__main__': main()
