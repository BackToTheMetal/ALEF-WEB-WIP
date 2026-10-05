#!/usr/bin/env python3
"""Fetch the hash-pinned pristine J2CL baseline used by source-patch generators."""
import hashlib
import json
from pathlib import Path
import tarfile
import tempfile
import urllib.request

ROOT = Path(__file__).resolve().parents[1]
LOCK = json.loads((ROOT / 'source-lock.json').read_text())['j2cl']
WORK = ROOT / '.work'
VERSION = LOCK['version']
ARCHIVE = WORK / ('j2cl-v' + VERSION + '.tar.gz')
BASELINE = WORK / ('j2cl-' + VERSION)


def main():
    WORK.mkdir(exist_ok=True)
    if not ARCHIVE.exists():
        url = 'https://github.com/google/j2cl/releases/download/v' + VERSION + '/j2cl-v' + VERSION + '.tar.gz'
        with tempfile.NamedTemporaryFile(dir=WORK, suffix='.download', delete=False) as output:
            temporary = Path(output.name)
            try:
                with urllib.request.urlopen(url) as response:
                    while block := response.read(1024 * 1024):
                        output.write(block)
            except BaseException:
                output.close()
                temporary.unlink(missing_ok=True)
                raise
        if hashlib.sha256(temporary.read_bytes()).hexdigest() != LOCK['archive_sha256']:
            temporary.unlink()
            raise SystemExit('Downloaded J2CL archive hash differs from source-lock.json')
        temporary.rename(ARCHIVE)
    if hashlib.sha256(ARCHIVE.read_bytes()).hexdigest() != LOCK['archive_sha256']:
        raise SystemExit('Existing J2CL archive hash differs from source-lock.json; file kept for inspection')
    if BASELINE.exists():
        if not (BASELINE / 'jre/java/java/lang/Class.java').is_file():
            raise SystemExit('Existing J2CL baseline is incomplete; no files were reset')
        print('Verified J2CL release archive; existing generator baseline kept unchanged')
        return
    with tempfile.TemporaryDirectory(dir=WORK, prefix='j2cl-extract-') as temporary:
        stage = Path(temporary)
        with tarfile.open(ARCHIVE) as archive:
            for member in archive.getmembers():
                target = (stage / member.name).resolve()
                if not target.is_relative_to(stage.resolve()):
                    raise SystemExit('J2CL archive contains a path outside its extraction directory')
            archive.extractall(stage, filter='data')
        extracted = stage / BASELINE.name
        if not (extracted / 'jre/java/java/lang/Class.java').is_file():
            raise SystemExit('Unexpected J2CL release archive layout')
        extracted.rename(BASELINE)
    print('Extracted hash-pinned pristine J2CL generator baseline at ' + str(BASELINE))


if __name__ == '__main__':
    main()
