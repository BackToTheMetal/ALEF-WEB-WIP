#!/usr/bin/env python3
"""Verify direct UI libraries and materialize only Bazel root-local prerequisite patches.

The historical entry point is retained for bootstrap compatibility. No dependency
views or Java replacements are created.
"""
import argparse
import hashlib
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
def digest(data):
    return hashlib.sha256(data).hexdigest()

def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--check', action='store_true')
    args = parser.parse_args()
    config = json.loads((ROOT / 'dependency-views.json').read_text())
    if config['schema_version'] != 2:
        raise SystemExit('Expected direct-library dependency configuration')
    for library in config['libraries']:
        source = (ROOT / library['source']).resolve(strict=True)
        if source.parent != ROOT.parent or digest((source / 'MODULE.bazel').read_bytes()) != library['module_sha256']:
            raise SystemExit('UI dependency changed; review dependency-views.json: ' + str(source))
    ownership = {entry['path']: entry for entry in json.loads((ROOT / 'third_party/j2cl/composition.json').read_text())['ownership']}
    awt = ROOT.parent / 'java-awt-j2cl'
    for name in ('URI', 'URL', 'URISyntaxException', 'MalformedURLException'):
        path = 'jre/java/java/net/' + name + '.java'
        if ownership[path]['emitted_sha256'] != digest((awt / ('src/main/java/java/net/' + name + '.java')).read_bytes()):
            raise SystemExit('AWT/composed-JRE provider mismatch: ' + name)
    for copy in config['patch_copies']:
        content = (ROOT / copy['source']).read_bytes()
        if digest(content) != copy['sha256']:
            raise SystemExit('UI prerequisite changed: ' + copy['source'])
        output = ROOT / copy['destination']
        if not output.resolve().is_relative_to(ROOT.resolve()) or output.is_symlink():
            raise SystemExit('Unsafe root patch destination: ' + str(output))
        if output.exists() and output.read_bytes() == content:
            continue
        if args.check:
            raise SystemExit('Stale root-local UI prerequisite: ' + str(output))
        output.parent.mkdir(parents=True, exist_ok=True)
        output.write_bytes(content)
    print('Direct UI dependencies verified; no Java overrides; four net classes owned by the composed JRE')

if __name__ == '__main__':
    main()
