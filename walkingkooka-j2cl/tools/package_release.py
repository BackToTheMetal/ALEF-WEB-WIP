#!/usr/bin/env python3
"""Package the buildable JRE patch, capability target, sources, and provenance."""
from pathlib import Path
import gzip, hashlib, io, json, tarfile

ROOT = Path(__file__).resolve().parents[1]

def main():
    release = ROOT / 'release'
    release.mkdir(exist_ok=True)
    files = {}
    for name in ['MODULE.bazel', 'MODULE.bazel.lock', '.bazelversion', '.bazelrc', 'BUILD.bazel', 'README.md', 'upstream-lock.json', 'profile.json', 'package.json', 'package-lock.json']:
        files[name] = (ROOT / name).read_bytes()
    for directory in ['patches', 'overlay', 'generated', 'src', 'tests', 'tools']:
        for p in sorted((ROOT / directory).rglob('*')):
            if p.is_file() and '__pycache__' not in p.parts:
                files[p.relative_to(ROOT).as_posix()] = p.read_bytes()
    for name in ['metadata.json', 'source-manifest.json']:
        files['release/' + name] = (release / name).read_bytes()
    for repo in sorted((ROOT / 'upstream').iterdir()):
        for p in repo.glob('LICENSE*'):
            files['licenses/' + repo.name + '/' + p.name] = p.read_bytes()
    dest = release / 'walkingkooka-java-time-0.1.0.tar.gz'
    with dest.open('wb') as raw, gzip.GzipFile(filename='', fileobj=raw, mode='wb', mtime=0) as gz, tarfile.open(fileobj=gz, mode='w') as tf:
        for name, data in sorted(files.items()):
            info = tarfile.TarInfo('walkingkooka-java-time-0.1.0/' + name)
            info.size = len(data)
            info.mode = 0o644
            tf.addfile(info, io.BytesIO(data))
    digest = hashlib.sha256(dest.read_bytes()).hexdigest()
    dest.with_suffix(dest.suffix + '.sha256').write_text(digest + '  ' + dest.name + '\n')
    print(dest, digest)

if __name__ == '__main__':
    main()
