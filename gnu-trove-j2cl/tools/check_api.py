#!/usr/bin/env python3
"""Compare compiled public/protected signatures, including inherited members."""
from pathlib import Path
import json
import re
import subprocess
import zipfile
from mps_requirements import check_profile

ROOT = Path(__file__).resolve().parents[1]


def load_adaptations():
    entries = json.loads((ROOT / 'compatibility/api-adaptations.json').read_text())['entries']
    result = {(e['class'], e['kind'], e['member']): e for e in entries}
    if len(result) != len(entries):
        raise ValueError('Duplicate API adaptation')
    return result


def classify(key, reference, replacement, adaptations):
    if key[0] == 'gnu.trove.SerializationProcedure' and replacement is None:
        return 'excluded', 'JVM object serialization'
    if reference == replacement:
        return 'compatible', None
    approved = adaptations.get(key)
    if approved and (reference, replacement) == (approved['reference'], approved['replacement']):
        return 'adapted', approved['reason']
    return 'mismatch', None


def main():
    adaptations = load_adaptations()
    profile = check_profile()
    retained = set(profile['retained'])
    out = ROOT / 'out/api'; out.mkdir(parents=True, exist_ok=True)
    jar = ROOT / 'third_party/trove4j-1.0.20200330.jar'
    with zipfile.ZipFile(jar) as z:
        # Include accessible nested classes by checking their visibility below.
        names = sorted(n[:-6].replace('/', '.') for n in z.namelist() if n.startswith('gnu/') and n.endswith('.class')
                       and n[:-6].replace('/', '.').split('$')[0] in retained)
    classes = out / 'classes.txt'; classes.write_text('\n'.join(names))
    subprocess.run(['javac', '-d', str(out), str(ROOT / 'tools/ApiDump.java')], check=True)
    dumps = []
    for label, cp in [('reference', jar), ('replacement', ROOT / 'out/replacement')]:
        dump = subprocess.check_output(['java', '-cp', str(out), 'ApiDump', str(cp), str(classes)], text=True)
        (out / (label + '.tsv')).write_text(dump)
        dumps.append({tuple(row.split('\t')[:3]): row.split('\t', 3)[3] for row in dump.splitlines()})
    original, replacement = dumps
    records, failures = [], []
    for key, signature in original.items():
        name, kind, member = key
        # Compiler-generated implementation classes are not exported APIs.
        cls = original.get((name, 'CLASS', 'class'), '')
        if '$' in name and not re.match(r'(public|protected)\b', cls): continue
        actual = replacement.get(key)
        status, reason = classify(key, signature, actual, adaptations)
        if status == 'mismatch':
            failures.append({'key': key, 'expected': signature, 'actual': actual})
        records.append({'class': name, 'kind': kind, 'member': member, 'reference': signature,
                        'replacement': actual, 'status': status, 'reason': reason})
    report = {'baseline': 'org.jetbrains.intellij.deps:trove4j:1.0.20200330',
              'mpsRevision': profile['revision'], 'excludedSourceTypes': profile['removed'] + ['gnu.trove.SerializationProcedure'],
              'entries': records, 'mismatches': failures}
    (ROOT / 'compatibility/api.json').write_text(json.dumps(report, indent=2) + '\n')
    print(f'API: {len(records)} entries; {len(failures)} mismatches')
    if failures:
        print(json.dumps(failures[:12], indent=2)); raise SystemExit(1)


if __name__ == '__main__': main()
