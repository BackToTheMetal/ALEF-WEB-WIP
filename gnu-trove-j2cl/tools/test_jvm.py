#!/usr/bin/env python3
"""Compile and execute the same contracts on isolated original/replacement JVMs."""
from pathlib import Path
import subprocess
import sys
import json
import os
import shutil

ROOT = Path(__file__).resolve().parents[1]


def run(*args):
    subprocess.run(args, cwd=ROOT, check=True)


def main():
    report = ROOT / 'out/jvm-results.json'
    report.unlink(missing_ok=True)
    version = subprocess.run(['java', '-version'], capture_output=True, text=True, check=True).stderr.strip()
    seeds = json.loads((ROOT / os.environ.get('TROVE_SEEDS_FILE', 'tests/seeds.json')).read_text())
    common = sorted((ROOT / 'tests/contract').rglob('*.java')) + sorted((ROOT / 'tests/jvm').rglob('*.java')) + sorted((ROOT / 'tests/mps').rglob('*.java'))
    reference = ROOT / 'third_party/trove4j-1.0.20200330.jar'
    for mode in ('reference', 'replacement'):
        out = ROOT / 'out' / mode
        # Resolve and constrain deletion before removing stale compiler output.
        if out.is_symlink() or out.resolve() != ROOT.resolve() / 'out' / mode:
            raise ValueError('Unsafe compiler output directory: ' + str(out))
        if out.exists():
            shutil.rmtree(out)
        out.mkdir(parents=True, exist_ok=True)
        sources = common + (sorted((ROOT / 'src/main/java').rglob('*.java')) + sorted((ROOT / 'tests/regression').rglob('*.java')) if mode == 'replacement' else [])
        argfile = out / 'sources.args'
        argfile.write_text('\n'.join('"' + str(p).replace('\\', '/') + '"' for p in sources))
        args = ['javac', '-J-Xmx512m', '-nowarn', '-d', str(out)]
        if mode == 'reference': args += ['-cp', str(reference)]
        run(*args, '@' + str(argfile))
        for seed in seeds:
            cp = str(out) + (os.pathsep + str(reference) if mode == 'reference' else '')
            result = subprocess.run(['java', '-Xmx256m', '-cp', cp, 'org.trovej2cl.Main', str(seed)], cwd=ROOT, capture_output=True, text=True, timeout=60)
            if result.returncode:
                print(result.stderr); sys.exit(result.returncode)
            (ROOT / 'out' / f'{mode}-{seed}.txt').write_text(result.stdout, newline='\n')
            print(mode, seed, result.stdout.splitlines()[0])
        if mode == 'replacement': run('java', '-cp', str(out), 'org.trovej2cl.RegressionMain')
    for seed in seeds:
        a = (ROOT / 'out' / f'reference-{seed}.txt').read_text()
        b = (ROOT / 'out' / f'replacement-{seed}.txt').read_text()
        if a != b:
            import difflib
            print(''.join(difflib.unified_diff(a.splitlines(True), b.splitlines(True), 'reference', 'replacement')))
            sys.exit(1)
    print('All JVM differential traces match.')
    report.write_text(json.dumps({'status': 'passed', 'javaVersion': version, 'seeds': seeds,
        'tracesMatched': True, 'regressions': 'passed'}, indent=2) + '\n')


if __name__ == '__main__': main()
