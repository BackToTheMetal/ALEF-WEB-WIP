#!/usr/bin/env python3
"""Generate additional JRE API adaptations against the pinned J2CL release."""
import argparse
import difflib
import hashlib
import json
from pathlib import Path
import subprocess
import tempfile

ROOT = Path(__file__).resolve().parents[1]
DIRECTORY = ROOT / 'third_party/j2cl/api-support'

def preimage(upstream, relative):
    original = upstream / relative
    before = original.read_text(encoding='utf-8') if original.exists() else ''
    exists = original.exists()
    for prior in ['composed.patch', 'type-support/class-type-support.patch', 'swing-support/j2cl-invocation-support.patch', 'mps-support/classloader.patch']:
        content = (ROOT / 'third_party/j2cl' / prior).read_text(encoding='utf-8')
        marker = 'diff --git a/' + relative + ' b/' + relative + '\n'
        if marker not in content:
            continue
        section = marker + content.split(marker, 1)[1].split('diff --git ', 1)[0]
        with tempfile.TemporaryDirectory(prefix='mps-api-preimage-') as temporary:
            file = Path(temporary) / relative
            if exists:
                file.parent.mkdir(parents=True, exist_ok=True)
                file.write_text(before, encoding='utf-8', newline='\n')
            subprocess.run(['git', 'apply', '--whitespace=nowarn', '-'], input=section, text=True, cwd=temporary, check=True)
            exists = file.exists()
            before = file.read_text(encoding='utf-8') if exists else ''
    return before, exists

def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--check', action='store_true')
    parser.add_argument('--upstream', type=Path, default=ROOT / '.work/j2cl-20260402')
    args = parser.parse_args()
    sections, ownership = [], []
    for source in sorted((DIRECTORY / 'jre').rglob('*')):
        if not source.is_file():
            continue
        relative = source.relative_to(DIRECTORY).as_posix()
        if relative == 'jre/java/java/lang/reflect/InvocationTargetException.java':
            raise SystemExit('InvocationTargetException is owned by the shared AWT prerequisite; remove the duplicate source')
        before, exists = preimage(args.upstream, relative)
        after = source.read_text(encoding='utf-8')
        if before == after:
            raise SystemExit('Unnecessary JRE replacement: ' + relative)
        header = 'diff --git a/' + relative + ' b/' + relative + '\n'
        if not exists:
            header += 'new file mode 100644\n'
        sections.append(header + ''.join(difflib.unified_diff(
            before.splitlines(keepends=True), after.splitlines(keepends=True),
            fromfile='a/' + relative if exists else '/dev/null', tofile='b/' + relative)))
        ownership.append({'path': relative, 'upstream_sha256': hashlib.sha256(before.encode()).hexdigest(),
                          'replacement_sha256': hashlib.sha256(after.encode()).hexdigest()})
    patch = ''.join(sections)
    outputs = {DIRECTORY / 'api-support.patch': patch,
               DIRECTORY / 'provenance.json': json.dumps({'j2cl_version': '20260402', 'ownership': ownership,
                   'patch_sha256': hashlib.sha256(patch.encode()).hexdigest()}, indent=2) + '\n'}
    for path, content in outputs.items():
        if args.check:
            if not path.exists() or path.read_bytes() != content.encode():
                raise SystemExit('Stale JRE API patch: ' + str(path))
        else:
            path.write_text(content, encoding='utf-8', newline='\n')
    print('JRE API patch: ' + str(len(ownership)) + ' reviewed source replacements')

if __name__ == '__main__':
    main()
