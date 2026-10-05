#!/usr/bin/env python3
"""Analyze both java.net providers and verify the selected source ownership."""
from pathlib import Path
import subprocess

ROOT = Path(__file__).resolve().parents[2]
QUERY = 'filter("src/main/java/java/net:.*[.]java$", deps(//src/main/java/java/net:uri-j2cl))'
for external in (False, True):
    result = subprocess.run([
        'bazelisk', 'cquery', QUERY, '--noshow_progress', '--output=starlark',
        '--starlark:expr=str(target.label)',
        '--//src/main/java/java/net:external_jre=' + str(external).lower(),
    ], cwd=ROOT, check=True, text=True, capture_output=True)
    actual = {line.rsplit(':', 1)[-1] for line in result.stdout.splitlines() if line.strip()}
    expected = set() if external else {'URI.java', 'URL.java', 'URISyntaxException.java', 'MalformedURLException.java'}
    if actual != expected:
        raise SystemExit('java.net ownership mismatch: external_jre=' + str(external) + ' ' + str(actual))
    print('java.net external_jre=' + str(external).lower() + ': ' + str(len(actual)) + ' AWT source providers')
