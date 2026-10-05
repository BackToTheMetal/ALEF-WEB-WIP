#!/usr/bin/env python3
"""Audit the pristine checkout, generated input hashes, and sparse overrides."""
from pathlib import Path
import hashlib
import json
import os
import re
import subprocess

ROOT = Path(__file__).resolve().parents[1]


def main():
    lock = json.loads((ROOT / 'source-lock.json').read_text())
    checkout = ROOT / 'alef'
    head = subprocess.check_output(['git', '-C', str(checkout), 'rev-parse', 'HEAD'], text=True).strip()
    status = subprocess.check_output(['git', '-C', str(checkout), 'status', '--porcelain', '--untracked-files=all'], text=True)
    if head != lock['alef']['commit'] or status:
        raise RuntimeError('ALEF checkout is modified or differs from the pinned revision: ' + status)
    bazel_names = {'BUILD', 'BUILD.bazel', 'MODULE.bazel', 'WORKSPACE', 'WORKSPACE.bazel'}
    for directory, directories, files in os.walk(checkout):
        if '.git' in directories:
            directories.remove('.git')
        found = bazel_names.intersection(files)
        if found:
            raise RuntimeError('Bazel metadata found inside ALEF: ' + str(Path(directory) / next(iter(found))))
    generated = json.loads((ROOT / 'out/generated-sources.json').read_text())
    for entry in generated['sources']:
        if hashlib.sha256((ROOT / entry['path']).read_bytes()).hexdigest() != entry['sha256']:
            raise RuntimeError('Generated original changed: ' + entry['path'])
    inventory = json.loads((ROOT / 'out/source-inventory.json').read_text())
    selected = inventory['sources']
    types = {}
    for path in selected:
        text = (ROOT / path).read_text()
        match = re.search(r'^package\s+([\w.]+);', text, re.M)
        if match:
            name = match[1] + '.' + Path(path).stem
            if name in types:
                raise RuntimeError('Duplicate type: ' + name)
            types[name] = path
    for original, replacement in inventory['overrides'].items():
        if original in selected or replacement not in selected or not replacement.startswith('j2cl-src/'):
            raise RuntimeError('Invalid override selection: ' + original)
        package = lambda p: re.search(r'^package\s+([\w.]+);', (ROOT / p).read_text(), re.M)[1]
        if package(original) != package(replacement) or Path(original).name != Path(replacement).name:
            raise RuntimeError('Override changes type identity: ' + original)
    replacements = set(inventory['overrides'].values())
    for path in (ROOT / 'j2cl-src/alef').rglob('*.java'):
        if path.relative_to(ROOT).as_posix() not in replacements:
            raise RuntimeError('Unselected ALEF override: ' + str(path))
    extensions = json.loads((ROOT / 'out/extension-inventory.json').read_text())
    for original, replacement in extensions['overrides'].items():
        linked_original = '.work/extensions-src/' + original.removeprefix('upstream-src/')
        if replacement not in extensions['sources'] or linked_original in extensions['sources']:
            raise RuntimeError('Invalid extension override: ' + original)
    mps = json.loads((ROOT / 'out/mps-inventory.json').read_text())
    for original, replacement in mps['overrides'].items():
        if replacement not in mps['sources'] or '.work/mps-src/' + original in mps['sources']:
            raise RuntimeError('Invalid MPS source override: ' + original)
    access = json.loads((ROOT / 'mps-access.json').read_text())
    for entry in access:
        if hashlib.sha256((ROOT / entry['base']).read_bytes()).hexdigest() != entry['base_sha256']:
            raise RuntimeError('MPS replacement baseline changed: ' + entry['base'])
        if not (ROOT / entry['replacement']).is_file():
            raise RuntimeError('Missing MPS replacement: ' + entry['replacement'])
    report = {'passed': True, 'alef_commit': head, 'generated_files': len(generated['sources']),
              'selected_java_sources': len(selected), 'replacements': len(replacements), 'duplicate_types': 0,
              'supplemental_extension_sources': len(extensions['sources']),
              'extension_replacements': len(extensions['overrides']), 'mps_access_replacements': len(access),
              'supplemental_mps_sources': len(mps['sources']), 'mps_source_replacements': len(mps['overrides'])}
    (ROOT / 'out/source-audit.json').write_text(json.dumps(report, indent=2) + '\n')
    print(json.dumps(report))


if __name__ == '__main__':
    main()
