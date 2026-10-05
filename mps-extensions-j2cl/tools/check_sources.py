"""Audit pristine inputs, package layout, replacement ownership and selected reflection calls."""
from pathlib import Path
import ast
import hashlib
import json
import re
import subprocess

ROOT = Path(__file__).resolve().parents[1]
def sha(path): return hashlib.sha256(path.read_bytes()).hexdigest()
lock = json.loads((ROOT / 'source-lock.json').read_text())
assert subprocess.check_output(['git', '-C', str(ROOT.parent / 'mps-j2cl/mps'), 'rev-parse', 'HEAD'], text=True).strip() == lock['mps']['commit']
assert subprocess.check_output(['git', '-C', str(ROOT / 'mps-extensions'), 'rev-parse', 'HEAD'], text=True).strip() == lock['extensions']['commit']
assert not subprocess.check_output(['git', '-C', str(ROOT / 'mps-extensions'), 'status', '--porcelain', '--untracked-files=all'], text=True)
for record in json.loads((ROOT / 'upstream-sources.json').read_text()):
    assert sha(ROOT / record['path']) == record['sha256'], record['path']
mps_access = json.loads((ROOT / 'mps-access.json').read_text())
for record in mps_access:
    assert sha(ROOT.parent / 'mps-j2cl' / record['base']) == record['base_sha256'], record['base']
sources = ast.literal_eval((ROOT / 'runtime-sources.bzl').read_text().split('RUNTIME_SOURCES = ', 1)[1])
seen = set()
reflection = []
used_overlays = {'j2cl-src/' + record['replacement'] for record in mps_access}
for group, paths in sources.items():
    for path in paths:
        source = ROOT / path
        if path.startswith('j2cl-src/'):
            used_overlays.add(path)
        text = source.read_text()
        package = re.search(r'^package ([\w.]+);', text, re.M).group(1)
        name = package + '.' + source.stem
        assert name not in seen, name
        seen.add(name)
        assert path.endswith(package.replace('.', '/') + '/' + source.name), path
        assert not re.search(r'java\.lang\.(reflect|invoke)|Class\.forName|\.getDeclared(Field|Method)s?\(|\.getOwnClass\(', text), path
        for line in text.splitlines():
            if 'ReflectionUtil.' in line:
                reflection.append({'source': path, 'call': line.strip()})
for path in (ROOT / 'j2cl-src').rglob('*.java'):
    assert path.relative_to(ROOT).as_posix() in used_overlays, 'Orphan replacement: ' + str(path)
    text = path.read_text()
    package = re.search(r'^package ([\w.]+);', text, re.M).group(1)
    assert path.relative_to(ROOT / 'j2cl-src').as_posix() == package.replace('.', '/') + '/' + path.name
# The clean-status check above includes untracked files. Inspect Git's file index
# once instead of walking the large Windows-mounted checkout five times.
upstream_files = subprocess.check_output(['git', '-C', str(ROOT / 'mps-extensions'), 'ls-files', '-z'], text=True).split('\0')
bazel_names = {'BUILD', 'BUILD.bazel', 'MODULE.bazel', 'WORKSPACE', 'WORKSPACE.bazel'}
assert not any(Path(path).name in bazel_names for path in upstream_files), 'Bazel metadata in upstream checkout'
report = {'selected_classes': len(seen), 'reflection_calls': reflection, 'upstream_clean': True, 'mps_bases_unchanged': True}
(ROOT / 'out').mkdir(exist_ok=True)
(ROOT / 'out/source-audit.json').write_text(json.dumps(report, indent=2) + '\n')
print(f'Source audit passed: {len(seen)} selected runtime classes; pristine upstream and MPS bases')
