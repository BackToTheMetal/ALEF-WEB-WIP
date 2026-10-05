#!/usr/bin/env python3
"""Install a root-local patch copy; Bazel rejects single_version_override patches in dependencies."""
import argparse
import hashlib
import json
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
def main():
    p=argparse.ArgumentParser(); p.add_argument('consumer',type=Path); args=p.parse_args()
    destination=args.consumer.resolve()/'third_party/java-nio'
    content=(ROOT/'patches/j2cl-nio.patch').read_bytes()
    files={'j2cl-nio.patch':content,'BUILD.bazel':b'exports_files(["j2cl-nio.patch"])\n'}
    # Check both files before writing either; never overwrite a different integration.
    for name,data in files.items():
        path=destination/name
        if path.exists() and path.read_bytes()!=data: raise SystemExit('Existing file differs; review it before refreshing: '+str(path))
    destination.mkdir(parents=True,exist_ok=True)
    for name,data in files.items(): (destination/name).write_bytes(data)
    (destination/'profile.json').write_text(json.dumps({'profile':'java-nio-j2cl 0.1.0','patch_sha256':hashlib.sha256(content).hexdigest()},indent=2)+'\n')
    print('Installed root patch //third_party/java-nio:j2cl-nio.patch; add the MODULE.bazel override shown in README.md')
if __name__=='__main__': main()
