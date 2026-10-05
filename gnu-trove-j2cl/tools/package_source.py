#!/usr/bin/env python3
"""Produce a reproducible source archive with notices and regeneration inputs."""
from pathlib import Path
import gzip
import hashlib
import io
import json
import tarfile

ROOT = Path(__file__).resolve().parents[1]


def main():
    paths = [ROOT / x for x in ['MODULE.bazel', 'MODULE.bazel.lock', 'BUILD.bazel', '.bazelrc', '.bazelversion',
             'README.md', 'LICENSE', 'THIRD_PARTY_NOTICES.md', 'package.json', 'package-lock.json',
             'third_party/trove4j-1.0.20200330.jar', 'third_party/trove4j-1.0.20200330-sources.jar',
             'third_party/TROVE-README-license.txt', 'third_party/MPS-LICENSE.txt']]
    paths += [ROOT / x for x in ['third_party/mps-revision.txt', 'third_party/mps-language-trove-2.1.0.jar',
                               'third_party/mps-language-trove-src-2.1.0.jar']]
    for directory in ['src', 'tools', 'compatibility', 'docs', 'tests']:
        paths += [p for p in (ROOT / directory).rglob('*') if p.is_file() and '__pycache__' not in p.parts]
    out = ROOT / 'out'; out.mkdir(exist_ok=True)
    archive = out / 'gnu-trove-j2cl-0.1.0.tar.gz'
    manifest = {}
    with archive.open('wb') as raw:
        with gzip.GzipFile(fileobj=raw, mode='wb', filename='', mtime=0) as compressed:
            with tarfile.open(fileobj=compressed, mode='w') as tar:
                # pathlib sorts Windows paths case-insensitively, unlike Linux.
                # Sort normalized archive names so both hosts emit identical bytes.
                for path in sorted(set(paths), key=lambda p: p.relative_to(ROOT).as_posix()):
                    data = path.read_bytes(); rel = path.relative_to(ROOT).as_posix()
                    info = tarfile.TarInfo('gnu-trove-j2cl-0.1.0/' + rel)
                    info.size = len(data); info.mode = 0o644
                    tar.addfile(info, io.BytesIO(data)); manifest[rel] = hashlib.sha256(data).hexdigest()
    digest = hashlib.sha256(archive.read_bytes()).hexdigest()
    (out / 'source-release.json').write_text(json.dumps({'archive': archive.name, 'sha256': digest, 'files': manifest}, indent=2) + '\n')
    print(archive.name, digest)


if __name__ == '__main__': main()
