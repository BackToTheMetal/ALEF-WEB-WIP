#!/usr/bin/env python3
"""Reject profile/data mismatches before reporting validation success."""
import hashlib, json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
metadata = json.loads((ROOT / 'release/metadata.json').read_text())
if metadata['profile'] != json.loads((ROOT / 'profile.json').read_text()):
    raise SystemExit('Profile changed: run tools/prepare.py and tools/overlay.py')
actual = {p.relative_to(ROOT / 'generated').as_posix(): hashlib.sha256(p.read_bytes()).hexdigest()
          for p in sorted((ROOT / 'generated').rglob('*.java'))}
if actual != metadata['providers']:
    raise SystemExit('Generated provider hashes differ from release metadata')
print('Metadata verified, TZDB', metadata['tzdbVersion'])
