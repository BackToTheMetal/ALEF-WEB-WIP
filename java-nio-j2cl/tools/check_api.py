#!/usr/bin/env python3
"""Compare the actual emitted JRE class files with the independent JDK API inventory."""
import argparse
import json
import re
import subprocess
import tempfile
import zipfile
from pathlib import Path
ROOT = Path(__file__).resolve().parents[1]

def normalized(s):
    return re.sub(r"\s+", "", s.replace('$', '.').rstrip(';').replace('synchronized ', '').replace('native ', ''))

def main():
    p = argparse.ArgumentParser()
    p.add_argument("jar", type=Path)
    p.add_argument("--javap", default="javap")
    p.add_argument("--reference", type=Path, default=ROOT / "compatibility/jdk-nio-api.json")
    p.add_argument("--report", type=Path, default=ROOT / "compatibility/api-check.json")
    args = p.parse_args()
    api = json.loads(args.reference.read_text())
    missing, excluded, checked = [], [], 0
    declarations = {}
    with zipfile.ZipFile(args.jar) as archive, tempfile.TemporaryDirectory(prefix="nio-api-") as directory:
        paths = []
        for c in api["classes"]:
            entry = c["binaryName"].replace('.', '/') + '.class'
            if entry not in archive.namelist(): missing.append({"type":c["name"],"missing":"class"}); continue
            path = Path(directory) / entry
            path.parent.mkdir(parents=True, exist_ok=True)
            path.write_bytes(archive.read(entry)); paths.append(str(path))
        for start in range(0, len(paths), 30):
            output = subprocess.check_output([args.javap, '-protected', *paths[start:start+30]], text=True)
            current = None
            for line in output.splitlines():
                match = re.match(r"(?:public|protected).*?\b(?:class|interface)\s+([^\s<{]+)", line)
                if match:
                    current = match[1].replace('$','.')
                    declarations[current] = {"header":line.rstrip(' {'),"members":[]}
                elif current and line.startswith(('  public ', '  protected ')):
                    declarations[current]["members"].append(line.strip())
        for c in api["classes"]:
            emitted = declarations.get(c["name"])
            if emitted is None: continue
            actual = {normalized(m.replace(c["binaryName"]+'(',c["simpleName"]+'(')) for m in emitted["members"]}
            expected_members = c["fields"] + c["constructors"] + c["methods"]
            for member in expected_members:
                if 'java.lang.foreign.' in member['declaration']:
                    excluded.append({"type":c['name'],"member":member['declaration'],"reason":"Java 21 preview foreign memory API"}); continue
                checked += 1
                if normalized(member['declaration']) not in actual:
                    missing.append({"type":c['name'],"missing":member['declaration']})
            # Enums are emitted as final classes extending Enum; methods and constants still checked.
            if not c['enumConstants']:
                expected = c['declaration'].replace('static ', '')
                header = emitted['header'].replace(c['binaryName'], c['simpleName'], 1).replace('$','.')
                if normalized(expected) != normalized(header): missing.append({"type":c['name'],"missing":"declaration mismatch","expected":expected,"actual":header})
    result = {"artifact":str(args.jar),"reference_jdk":api['jdk'],"types":len(api['classes']),"members_checked":checked,"missing":missing,"excluded":excluded,"status":"PASS" if not missing else "FAIL"}
    args.report.write_text(json.dumps(result, indent=2)+'\n')
    print(json.dumps({k:v for k,v in result.items() if k not in {'missing','excluded'}}, indent=2))
    if missing:
        print(json.dumps(missing[:25],indent=2))
        raise SystemExit(1)

if __name__ == '__main__': main()
