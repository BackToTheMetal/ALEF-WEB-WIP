#!/usr/bin/env python3
"""Create and verify a deterministic source release, excluding workspaces and build outputs."""
import gzip
import hashlib
import io
import json
import tarfile
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
NAME='java-nio-j2cl-0.1.0'
def main():
    entries=[]
    for base in ['src','tools','compatibility','patches','integrations','third_party']:
        entries += [p for p in (ROOT/base).rglob('*') if p.is_file() and '__pycache__' not in p.parts]
    entries += [ROOT/p for p in ['MODULE.bazel','MODULE.bazel.lock','BUILD.bazel','.bazelversion','.bazelrc','README.md','LICENSE','NOTICE','IMPLEMENTATION_PLAN.md'] if (ROOT/p).exists()]
    buffer=io.BytesIO()
    with tarfile.open(fileobj=buffer,mode='w') as archive:
        for path in sorted(set(entries)):
            content=path.read_bytes(); info=tarfile.TarInfo(NAME+'/'+path.relative_to(ROOT).as_posix())
            info.size=len(content); info.mode=0o644; archive.addfile(info,io.BytesIO(content))
    data=gzip.compress(buffer.getvalue(),mtime=0)
    output=ROOT/'out/release'; output.mkdir(parents=True,exist_ok=True)
    archive=output/(NAME+'.tar.gz'); archive.write_bytes(data)
    with tarfile.open(archive,'r:gz') as check:
        assert len(check.getmembers())==len(set(entries))
        for item in check.getmembers():
            relative=Path(item.name).relative_to(NAME)
            assert check.extractfile(item).read()==(ROOT/relative).read_bytes()
    manifest={"archive":archive.name,"sha256":hashlib.sha256(data).hexdigest(),"files":len(set(entries)),"version":"0.1.0"}
    (output/'manifest.json').write_text(json.dumps(manifest,indent=2)+'\n')
    print(json.dumps(manifest,indent=2))
if __name__=='__main__': main()
