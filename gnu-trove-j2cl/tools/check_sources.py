#!/usr/bin/env python3
"""Reject unpinned references and forbidden runtime dependencies."""
import hashlib
import json
from pathlib import Path
import re
import sys
from port_sources import mask
from mps_requirements import check_profile

ROOT = Path(__file__).resolve().parents[1]


def main():
    check_profile()
    baseline = json.loads((ROOT / 'compatibility/baseline.json').read_text())
    errors = []
    for item in baseline['artifacts'] + baseline['fixtureFiles']:
        p = ROOT / item['file']
        if not p.is_file() or hashlib.sha256(p.read_bytes()).hexdigest() != item['sha256']:
            errors.append('Checksum mismatch: ' + str(p))
    for source in (ROOT / 'src/main/java').rglob('*.java'):
        text = mask(source.read_text())
        for forbidden in [r'import java\.lang\.reflect\.', r'\bObject(?:Input|Output)Stream\b',
                          r'\bExternalizable\b', r'\bsuper\.clone\(', r'\b(?:native|synchronized)\s+(?:\w|\()',
                          r'import (?:com\.intellij|org\.jetbrains|jsinterop|elemental2)\.', r'System\.getProperty\(']:
            if re.search(forbidden, text): errors.append(str(source) + ': forbidden ' + forbidden)
    print('Source integrity and J2CL runtime dependency checks:', 'PASS' if not errors else 'FAIL')
    if errors: print('\n'.join(errors)); sys.exit(1)


if __name__ == '__main__': main()
