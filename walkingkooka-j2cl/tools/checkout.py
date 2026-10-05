#!/usr/bin/env python3
"""Checkout the compile-time WalkingKooka dependency closure at locked revisions."""
import json
from pathlib import Path
import subprocess
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
LOCK = ROOT / 'upstream-lock.json'
ns = {'m': 'http://maven.apache.org/POM/4.0.0'}

def main():
    locked = json.loads(LOCK.read_text()) if LOCK.exists() else {}
    found = {}
    pending = ['j2cl-java-time']
    while pending:
        name = pending.pop(0)
        if name in found:
            continue
        path = ROOT / 'upstream' / name
        url = 'https://github.com/mP1/' + name + '.git'
        if not (path / '.git').exists():
            subprocess.run(['git', 'clone', '--quiet', url, str(path)], check=True)
        if name in locked:
            subprocess.run(['git', '-C', str(path), 'checkout', '--quiet', locked[name]['commit']], check=True)
        commit = subprocess.check_output(['git', '-C', str(path), 'rev-parse', 'HEAD'], text=True).strip()
        pom = ET.parse(path / 'pom.xml')
        deps = []
        for dep in pom.findall('./m:dependencies/m:dependency', ns):
            if dep.findtext('m:groupId', namespaces=ns) == 'walkingkooka' and dep.findtext('m:scope', namespaces=ns) not in ('test', 'provided'):
                deps.append(dep.findtext('m:artifactId', namespaces=ns))
        found[name] = {'url': url, 'commit': commit, 'dependencies': deps}
        print(name, commit[:12], deps, flush=True)
        pending.extend(deps)
    LOCK.write_text(json.dumps(found, indent=2) + '\n')

if __name__ == '__main__':
    main()
