#!/usr/bin/env python3
"""Compose the sibling MPS and WalkingKooka patches with explicit ownership."""
from pathlib import Path
import difflib
import hashlib
import json
import re
import tarfile

ROOT = Path(__file__).resolve().parents[1]
MPS = ROOT.parent / 'mps-j2cl'
TIME = ROOT.parent / 'walkingkooka-j2cl'
PATCHES = ['third_party/j2cl/composed.patch',
           'third_party/j2cl/type-support/class-type-support.patch',
           'third_party/j2cl/mps-support/classloader.patch',
           'third_party/j2cl/api-support/api-support.patch']


def apply(files, patch):
    """Apply exact unified-diff contexts in memory; reject mismatched inputs."""
    result = dict(files)
    for chunk in re.split(r'(?=^diff --git )', patch, flags=re.M):
        if not chunk.startswith('diff --git '):
            continue
        lines = chunk.splitlines(True)
        path = lines[0].strip().split(' b/', 1)[1]
        original = files.get(path, '').splitlines(True)
        output, cursor = [], 0
        i = 1
        while i < len(lines):
            match = re.match(r'@@ -(\d+)(?:,\d+)? \+\d+(?:,\d+)? @@', lines[i])
            if not match:
                i += 1
                continue
            start = max(0, int(match[1]) - 1)
            output.extend(original[cursor:start])
            cursor = start
            i += 1
            while i < len(lines) and not lines[i].startswith('@@ '):
                line = lines[i]
                if line.startswith((' ', '-')):
                    if cursor >= len(original) or original[cursor].rstrip('\n') != line[1:].rstrip('\n'):
                        raise ValueError(f'Patch context mismatch: {path}:{cursor + 1}')
                    cursor += 1
                if line.startswith((' ', '+')):
                    output.append(line[1:])
                i += 1
        output.extend(original[cursor:])
        result[path] = ''.join(output)
    return result


def main():
    archive = TIME / 'out/j2cl-v20260402.tar.gz'
    expected = json.loads((MPS / 'source-lock.json').read_text())['j2cl']['archive_sha256']
    if hashlib.sha256(archive.read_bytes()).hexdigest() != expected:
        raise ValueError('J2CL baseline checksum mismatch')
    inputs = PATCHES + ['patches/j2cl-java-time.patch']
    texts = [(MPS / p if i < 4 else TIME / p).read_text() for i, p in enumerate(inputs)]
    paths = {m[1] for text in texts for m in re.finditer(r'^diff --git a/(\S+) b/', text, re.M)}
    paths.add('jre/java/module-info.java')
    overlay = ROOT / 'j2cl-src/jre'
    paths.update('jre/java/' + p.relative_to(overlay).as_posix() for p in overlay.rglob('*.java'))
    with tarfile.open(archive) as tf:
        baseline = {m.name.removeprefix('j2cl-20260402/'): tf.extractfile(m).read().decode()
                    for m in tf.getmembers() if m.isfile() and m.name.removeprefix('j2cl-20260402/') in paths}
    mps = baseline
    for text, relative in zip(texts[:4], PATCHES):
        mps = apply(mps, text)
        target = ROOT / relative
        target.parent.mkdir(parents=True, exist_ok=True)
        target.write_text(text)
    walking = apply(baseline, texts[-1])
    combined = dict(mps)
    providers = {}
    for path in sorted(paths):
        if walking.get(path) == baseline.get(path):
            continue
        if path.startswith('jre/java/java/io/') and path in mps:
            providers[path] = 'mps-j2cl: preserve the existing IO implementation'
            continue
        if path == 'jre/java/BUILD':
            build = mps[path]
            assert '["java*/**/*.java"]' in build
            build = build.replace('["java*/**/*.java"]', '["java*/**/*.java", "walkingkooka/**/*.java"]')
            assert 'srcs = [":jre_java_files"],' in build
            build = build.replace('srcs = [":jre_java_files"],', 'srcs = [":walkingkooka_jre_stripped"],')
            build += '\n# The bootclasspath java_library' + walking[path].split('# The bootclasspath java_library', 1)[1]
            combined[path] = build
            providers[path] = 'merged build metadata'
        else:
            combined[path] = walking[path]
            providers[path] = 'walkingkooka-j2cl'
    module_path = 'jre/java/module-info.java'
    module = combined[module_path]
    for package in ('java.time.chrono', 'java.time.format', 'java.time.temporal', 'java.time.zone', 'java.text'):
        if 'exports ' + package + ';' not in module:
            module = module.replace('\n}', '\n  exports ' + package + ';\n}')
    combined[module_path] = module
    providers[module_path] = 'export composed java.time and text APIs'
    module_copy = ROOT / 'j2cl-src/jre/module-info.java'
    module_copy.parent.mkdir(parents=True, exist_ok=True)
    module_copy.write_text(module)
    # User adaptations always live outside the original source trees.
    overlay = ROOT / 'j2cl-src/jre'
    if overlay.exists():
        for source in sorted(overlay.rglob('*.java')):
            path = 'jre/java/' + source.relative_to(overlay).as_posix()
            combined[path] = source.read_text()
            providers[path] = source.relative_to(ROOT).as_posix()
    chunks = []
    for path, content in sorted(combined.items()):
        before = mps.get(path, '')
        if before == content:
            continue
        chunks.append(f'diff --git a/{path} b/{path}\n')
        if path not in mps:
            chunks.append('new file mode 100644\n')
        chunks.extend(difflib.unified_diff(before.splitlines(True), content.splitlines(True),
                      fromfile='a/' + path if before else '/dev/null', tofile='b/' + path))
    target = ROOT / 'third_party/j2cl/java-time.patch'
    target.write_text(''.join(chunks))
    report = {'baseline_sha256': expected, 'inputs': [
        {'path': ('../mps-j2cl/' if i < 4 else '../walkingkooka-j2cl/') + p,
         'sha256': hashlib.sha256(texts[i].encode()).hexdigest()} for i, p in enumerate(inputs)],
        'providers': providers, 'patch_sha256': hashlib.sha256(target.read_bytes()).hexdigest()}
    (ROOT / 'third_party/j2cl/composition.json').write_text(json.dumps(report, indent=2) + '\n')
    print('Composed java.time with MPS JRE:', len(providers), 'ownership decisions')


if __name__ == '__main__':
    main()
