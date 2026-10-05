#!/usr/bin/env python3
"""Verify source ownership, dependency snapshots, and the declared NIO type profile."""
import hashlib
import json
import re
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]

def main():
    owners={}; errors=[]
    upstream=json.loads((ROOT/'compatibility/j2cl-types.json').read_text())['types']
    for name in upstream:
        if name != 'module-info': owners[name]={"owner":"J2CL 20260402","status":"upstream"}
    overrides={"java.nio.charset.Charset","java.nio.charset.StandardCharsets","javaemul.internal.EmulatedCharset"}
    support=set(json.loads((ROOT/'compatibility/external-support.json').read_text())['types'])
    for owner,folder in [('java-nio-j2cl',ROOT/'src/main/super'),('java-io-j2cl',ROOT/'third_party/java-io-j2cl/overlay'),('WebAWT',ROOT/'third_party/WebAWT/overlay')]:
        for source in sorted(folder.rglob('*.java')):
            name=source.relative_to(folder).as_posix()[:-5].replace('/','.')
            if name=='module-info': continue
            if name in owners and not (owner=='java-nio-j2cl' and name in overrides): errors.append('Duplicate provider: '+name)
            owners[name]={"owner":owner,"source":source.relative_to(ROOT).as_posix(),"status":"augmented upstream" if name in overrides else "external signature subset" if name in support else "source"}
    for entry in json.loads((ROOT/'compatibility/dependency-snapshots.json').read_text()):
        path=ROOT/'third_party'/entry['owner']/'overlay'/entry['path']
        if hashlib.sha256(path.read_bytes()).hexdigest()!=entry['sha256']: errors.append('Modified dependency snapshot: '+str(path))
    for entry in json.loads((ROOT/'compatibility/j2cl-baseline.json').read_text())['modified_upstream_sources']:
        path=ROOT/'third_party/j2cl-base/jre/java'/entry['path']
        if hashlib.sha256(path.read_bytes()).hexdigest()!=entry['sha256']: errors.append('Modified upstream baseline: '+str(path))
    for entry in json.loads((ROOT/'compatibility/mps-fixtures.json').read_text())['files']:
        path=ROOT/'src/test/mps'/entry['fixture']
        if hashlib.sha256(path.read_bytes()).hexdigest()!=entry['sha256']: errors.append('Modified MPS fixture: '+str(path))
    api=json.loads((ROOT/'compatibility/jdk-nio-api.json').read_text())
    nio=[]
    for c in api['classes']:
        outer=c['binaryName'].split('$')[0]
        if outer not in owners: errors.append('Missing NIO source provider: '+c['name'])
        nio.append({"type":c['name'],"provider":owners.get(outer)})
    for source in (ROOT/'src/main/super/java/nio').rglob('*.java'):
        text=source.read_text()
        if re.search(r'\b(localStorage|indexedDB|ServiceLoader|sun\.nio|Unsafe)\b',text): errors.append('Forbidden runtime facility: '+str(source))
    report={"status":"FAIL" if errors else "PASS","nio_types":nio,"providers":owners,"integration_extensions":{"java.io.File.toPath":"integrations/File.nio-members.java.fragment"},"errors":errors}
    (ROOT/'compatibility/type-ownership.json').write_text(json.dumps(report,indent=2)+'\n')
    print(f"{report['status']}: {len(nio)} NIO types; one source provider per type; pinned snapshots verified")
    if errors: raise SystemExit('\n'.join(errors))
if __name__=='__main__': main()
