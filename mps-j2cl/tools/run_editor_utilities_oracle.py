#!/usr/bin/env python3
"""Compare editor utility overlays to the JVM and generate exact browser observations."""
import argparse
import difflib
import json
from pathlib import Path
import shutil
import subprocess
import tempfile

ROOT = Path(__file__).resolve().parents[1]
TESTS = ROOT / 'tests/editor_utilities'


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--check', action='store_true')
    parser.add_argument('--javac', default='javac')
    parser.add_argument('--java', default='java')
    args = parser.parse_args()
    workspace = ROOT / '.work/editor-utilities-oracle'
    workspace.mkdir(parents=True, exist_ok=True)
    with tempfile.TemporaryDirectory(dir=workspace) as temporary:
        temporary = Path(temporary)
        classes, sources, patch = (temporary / name for name in ['classes', 'sources', 'patch'])
        classes.mkdir()
        patch.mkdir()
        source = sources / 'java/util/StringTokenizer.java'
        source.parent.mkdir(parents=True)
        shutil.copyfile(ROOT / 'third_party/j2cl/api-support/jre/java/java/util/StringTokenizer.java', source)
        subprocess.run([args.javac, '-encoding', 'UTF-8', '-d', str(classes),
                        str(TESTS / 'EditorUtilitiesTest.java')], check=True)
        java = [args.java, '-ea', '-cp', str(classes), 'mpsj2cl.editorutilities.EditorUtilitiesTest']
        expected = subprocess.check_output(java, text=True, encoding='utf-8')
        subprocess.run([args.javac, '-encoding', 'UTF-8', '--patch-module', 'java.base=' + str(sources),
                        '-d', str(patch), str(source)], check=True)
        actual = subprocess.check_output([args.java, '--patch-module', 'java.base=' + str(patch)] + java[1:],
                                         text=True, encoding='utf-8')
        if actual != expected:
            raise SystemExit(''.join(difflib.unified_diff(expected.splitlines(True), actual.splitlines(True),
                                                       fromfile='JVM', tofile='overlay')))
    version = subprocess.run([args.java, '-version'], capture_output=True, text=True, check=True).stderr.splitlines()[0]
    baseline = {'scope': 'StringTokenizer Unicode delimiters, lookahead, mutation, exhaustion and null behavior',
                'jvm': version, 'rows': len(expected.splitlines()), 'test_source': 'tests/editor_utilities/EditorUtilitiesTest.java'}
    for path, content in [(TESTS / 'jvm-expected.txt', expected),
                          (TESTS / 'jvm-baseline.json', json.dumps(baseline, indent=2) + '\n')]:
        if args.check:
            if path.name == 'jvm-baseline.json':
                stored = json.loads(path.read_text(encoding='utf-8'))
                if any(stored[key] != baseline[key] for key in ['scope', 'rows', 'test_source']):
                    raise SystemExit('Stale editor utilities baseline')
            elif not path.exists() or path.read_text(encoding='utf-8') != content:
                raise SystemExit('Stale editor utilities golden output: ' + str(path))
        else:
            path.write_text(content, encoding='utf-8', newline='\n')
    print(f"Editor utilities JVM differential: {baseline['rows']} matching rows; {version}")


if __name__ == '__main__':
    main()
