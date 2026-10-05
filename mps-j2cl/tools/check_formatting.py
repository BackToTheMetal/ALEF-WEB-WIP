#!/usr/bin/env python3
"""Compare the MPS formatter with a JVM oracle and verify committed browser goldens."""
import argparse
from pathlib import Path
import subprocess
import tempfile

ROOT = Path(__file__).resolve().parents[1]
TESTS = ROOT / 'tests/formatting'


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--update-golden', action='store_true')
    parser.add_argument('--javac', default='javac')
    parser.add_argument('--java', default='java')
    args = parser.parse_args()
    with tempfile.TemporaryDirectory(prefix='mps-formatting-jvm-') as temporary:
        classes = Path(temporary) / 'classes'
        classes.mkdir()
        subprocess.run([args.javac, '-encoding', 'UTF-8', '-d', str(classes),
                        str(TESTS / 'FormattingCases.java'), str(TESTS / 'GenerateFormattingGolden.java')], check=True)
        java = [args.java, '-Duser.language=en', '-Duser.country=US', '-Duser.timezone=UTC', '-cp', str(classes)]
        expected = subprocess.run(java + ['mpsj2cl.formatting.GenerateFormattingGolden'],
                                  text=True, encoding='utf-8', stdout=subprocess.PIPE, check=True).stdout
        golden = TESTS / 'FormattingGolden.java'
        if args.update_golden:
            golden.write_text(expected, encoding='utf-8', newline='\n')
        elif golden.read_bytes() != expected.encode('utf-8'):
            raise SystemExit('Formatting goldens differ from the JVM oracle; inspect before --update-golden')
        subprocess.run([args.javac, '-encoding', 'UTF-8', '-cp', str(classes), '-d', str(classes),
                        str(ROOT / 'third_party/j2cl/api-support/jre/java/javaemul/internal/MpsFormatter.java'),
                        str(golden), str(TESTS / 'FormattingTest.java')], check=True)
        subprocess.run(java + ['mpsj2cl.formatting.FormattingTest'], check=True)
    print('Committed browser goldens match the JVM oracle; the shared helper passed its differential checks')


if __name__ == '__main__':
    main()
