"""Inventory direct MPS references to the actual Trove variants in this workspace."""
import bisect
import collections
import csv
import hashlib
import json
from pathlib import Path
import re
import struct
import sys
import zipfile

HERE = Path(__file__).resolve().parent
ROOT = HERE.parents[2]
sys.path.insert(0, str(HERE.parent / 'mps-ui-dependencies'))
from analyze import assignments, auxiliary, blank, IMPORT, LEX, TOKEN

MPS = ROOT.parent / 'mps-j2cl'
UPSTREAM = MPS / 'mps'
PORT = ROOT.parent / 'gnu-trove-j2cl'
EXT = ROOT.parent / 'mps-extensions-j2cl'
FQN = re.compile(r'\bgnu\.trove(?:\.[A-Za-z_$][\w$]*)+')


def class_flags(data):
    """Read class access flags without executing or loading the library."""
    assert data[:4] == b'\xca\xfe\xba\xbe'
    count = struct.unpack_from('>H', data, 8)[0]
    offset, entry = 10, 1
    sizes = {3: 4, 4: 4, 5: 8, 6: 8, 7: 2, 8: 2, 9: 4, 10: 4, 11: 4,
             12: 4, 15: 3, 16: 2, 17: 4, 18: 4, 19: 2, 20: 2}
    while entry < count:
        tag = data[offset]
        offset += 1
        if tag == 1:
            size = struct.unpack_from('>H', data, offset)[0]
            offset += 2 + size
        else:
            offset += sizes[tag]
            if tag in (5, 6):
                entry += 1
        entry += 1
    return struct.unpack_from('>H', data, offset)[0]


def jar_catalog(path):
    result = {}
    with zipfile.ZipFile(path) as jar:
        for name in jar.namelist():
            if name.startswith('gnu/trove/') and name.endswith('.class') and '$' not in name:
                flags = class_flags(jar.read(name))
                if flags & 1:
                    result[name[:-6].replace('/', '.')] = 'interface' if flags & 0x200 else 'class'
    return result


def selected_sources():
    profile = assignments(MPS / 'mps-browser-projects.bzl')
    overrides = assignments(MPS / 'mps-overrides.bzl')['MPS_OVERRIDES']
    rules = assignments(ROOT / '.work/mps-j2cl/mps-rules.bzl')
    selected = {}
    for project in profile['MPS_BROWSER_NAMES']:
        for relative in profile['MPS_BROWSER_PROJECTS'][project]['srcs']:
            if relative in rules['ALEF_OVERRIDES']:
                path = ROOT / rules['ALEF_OVERRIDES'][relative]
            elif relative in rules['EXTENSION_OVERRIDES']:
                path = EXT / 'j2cl-src' / rules['EXTENSION_OVERRIDES'][relative]
            elif relative in overrides:
                path = MPS / 'mps-web' / overrides[relative]
            else:
                path = UPSTREAM / relative
            selected[relative] = path
    for relative in assignments(MPS / 'financial-sources.bzl')['FINANCIAL_SOURCES']:
        if '/samples/' not in relative:
            selected.setdefault(relative.split('/', 1)[1], MPS / relative)
    for relative in assignments(ROOT / 'mps-sources.bzl')['MPS_SOURCES']:
        logical = relative.removeprefix('.work/mps-src/').removeprefix('j2cl-src/mps/')
        selected.setdefault(logical, UPSTREAM / logical if relative.startswith('.work/mps-src/') else ROOT / relative)
    return selected


def scan_references(text, catalog):
    code = LEX.sub(blank, text)
    imports = list(IMPORT.finditer(code))
    body = IMPORT.sub(blank, code)
    newlines = [i for i, c in enumerate(code) if c == '\n']
    tokens = {}
    for match in TOKEN.finditer(body):
        tokens.setdefault(match[0], match.start())
    found, explicit, wildcards, unknown = {}, {}, [], set()
    def owner(name):
        while '.' in name:
            if name in catalog:
                return name
            name = name.rsplit('.', 1)[0]
        return None
    def add(name, pos, method):
        if name and (name not in found or found[name]['method'] == 'import-only'):
            found[name] = {'line': bisect.bisect_left(newlines, pos) + 1, 'method': method}
    for match in imports:
        static, name, alias = match.groups()
        if not static and not name.endswith('.*'):
            explicit[alias or name.rsplit('.', 1)[-1]] = name
        if not name.startswith('gnu.trove.'):
            continue
        if name.endswith('.*') and not static:
            wildcards.append(name[:-2])
            continue
        target = owner(name[:-2] if name.endswith('.*') else name)
        if target is None:
            unknown.add(name)
            continue
        token = alias or name.rsplit('.', 1)[-1]
        add(target, tokens.get(token, match.start(2)),
            'explicit-import-and-token' if token in tokens else 'import-only')
    for name in catalog:
        package, short = name.rsplit('.', 1)
        if package in wildcards and short in tokens and explicit.get(short, name) == name:
            add(name, tokens[short], 'wildcard-and-token')
    for match in FQN.finditer(body):
        target = owner(match[0])
        if target:
            add(target, match.start(), 'fully-qualified')
        else:
            unknown.add(match[0])
    return found, sorted(unknown)


def main():
    jars = {
        'jetbrains': PORT / 'third_party/trove4j-1.0.20200330.jar',
        'language_2_1': UPSTREAM / 'languages/baseLanguage/collections/languages/trove/runtime/lib/trove-2.1.0.jar',
    }
    catalogs = {key: jar_catalog(path) for key, path in jars.items()}
    port_sources = {p.relative_to(PORT / 'src/main/java').as_posix()[:-5].replace('/', '.'): p
                    for p in (PORT / 'src/main/java').rglob('*.java')}
    catalog = dict(catalogs['jetbrains'], **catalogs['language_2_1'])
    for name, path in port_sources.items():
        code = LEX.sub(blank, path.read_text(encoding='utf-8'))
        if re.search(r'\bpublic\s+(?:(?:abstract|final)\s+)*(?:class|interface)\s+' + re.escape(path.stem) + r'\b', code):
            catalog.setdefault(name, 'interface' if re.search(r'\binterface\s+' + re.escape(path.stem) + r'\b', code) else 'class')

    probe, unknown = scan_references('import gnu.trove.*;\n// gnu.trove.TIntHashSet\nclass P { THashMap m; String s="gnu.trove.TLongArrayList"; }', catalog)
    assert set(probe) == {'gnu.trove.THashMap'} and not unknown
    probe, _ = scan_references('import gnu.trove.THashSet; class P {}', catalog)
    assert probe['gnu.trove.THashSet']['method'] == 'import-only'
    evidence = collections.defaultdict(list)
    unknown_refs, string_mentions = [], []
    candidates = (HERE / 'candidate-sources.txt').read_text(encoding='utf-8-sig').splitlines()
    tracked = (HERE / 'tracked-sources.txt').read_text(encoding='utf-8-sig').splitlines()
    totals = collections.Counter('auxiliary' if auxiliary(p) else 'upstream' for p in tracked)
    def scan(path, scope, logical):
        text = path.read_text(encoding='utf-8')
        if 'gnu.trove' not in text:
            return
        refs, unknown = scan_references(text, catalog)
        for name, ref in refs.items():
            evidence[name].append(dict(ref, scope=scope, path=path.as_posix(), logical=logical))
        if unknown:
            unknown_refs.append({'path': path.as_posix(), 'scope': scope, 'names': unknown})
        for i, line in enumerate(text.splitlines(), 1):
            if '"gnu.trove.' in line:
                string_mentions.append({'path': path.as_posix(), 'scope': scope, 'line': i, 'text': line.strip()})
    for relative in candidates:
        scan(UPSTREAM / relative, 'auxiliary' if auxiliary(relative) else 'upstream', relative)
    selected = selected_sources()
    for logical, path in sorted(selected.items()):
        scan(path, 'browser', logical)
    totals['browser'] = len(selected)
    classes = []
    for name, refs in sorted(evidence.items()):
        counts = {scope: sum(r['scope'] == scope and r['method'] != 'import-only' for r in refs)
                  for scope in ('upstream', 'browser', 'auxiliary')}
        import_counts = {scope: sum(r['scope'] == scope and r['method'] == 'import-only' for r in refs) for scope in counts}
        classes.append({'name': name, 'package': name.rsplit('.', 1)[0], 'kind': catalog[name],
                        'counts': counts, 'import_counts': import_counts,
                        'providers': {key: name in names for key, names in catalogs.items()} | {'port_source': name in port_sources},
                        'evidence': refs})
    packages = []
    for package in sorted({name.rsplit('.', 1)[0] for name in catalog}):
        packages.append({'name': package,
                         'available': {key: sum(n.rsplit('.', 1)[0] == package for n in names) for key, names in catalogs.items()},
                         'counts': {scope: sum(c['package'] == package and c['counts'][scope] > 0 for c in classes) for scope in totals}})

    # Conservative named-source closure inside the current J2CL Trove provider.
    # This includes all method bodies/signatures in a source unit, not runtime reachability.
    dependencies = {}
    for name, path in port_sources.items():
        code = LEX.sub(blank, path.read_text(encoding='utf-8'))
        refs, _ = scan_references(code, port_sources)
        tokens = set(TOKEN.findall(IMPORT.sub(blank, code)))
        package = name.rsplit('.', 1)[0]
        same_package = {n for n in port_sources if n.rsplit('.', 1)[0] == package and n.rsplit('.', 1)[1] in tokens}
        dependencies[name] = sorted((set(refs) | same_package) - {name})
    roots = {c['name'] for c in classes if c['counts']['browser']}
    closure, parent, pending = set(), {}, sorted(roots)
    while pending:
        name = pending.pop(0)
        if name in closure:
            continue
        assert name in port_sources, name
        closure.add(name)
        for dep in dependencies[name]:
            if dep not in closure:
                parent.setdefault(dep, name)
                pending.append(dep)
    data = {'commit': (HERE / 'commit.txt').read_text(encoding='utf-8-sig').strip(),
            'source_file_counts': dict(totals), 'candidate_file_count': len(candidates),
            'artifacts': {key: {'path': path.as_posix(), 'sha256': hashlib.sha256(path.read_bytes()).hexdigest(),
                                'public_top_level_types': len(catalogs[key])} for key, path in jars.items()},
            'packages': packages, 'classes': classes, 'unknown_references': unknown_refs, 'string_mentions': string_mentions,
            'port_source_count': len(port_sources), 'port_source_closure': sorted(closure), 'port_closure_parent': parent,
            'port_source_hashes': {n: hashlib.sha256(port_sources[n].read_bytes()).hexdigest() for n in sorted(closure)},
            'port_dependencies': {n: dependencies[n] for n in sorted(closure)},
            'browser_sources': [{'logical': k, 'path': p.as_posix(), 'sha256': hashlib.sha256(p.read_bytes()).hexdigest()}
                                for k, p in sorted(selected.items())]}
    (HERE / 'inventory.json').write_text(json.dumps(data, indent=2) + '\n', encoding='utf-8')
    with (HERE / 'classes.csv').open('w', newline='', encoding='utf-8') as stream:
        writer = csv.writer(stream)
        writer.writerow(['Package', 'Type', 'Main MPS files', 'Browser files', 'Auxiliary files', 'JetBrains baseline', 'Trove 2.1', 'Port source', 'Evidence', 'Line'])
        for c in classes:
            ref = next((r for r in c['evidence'] if r['scope'] == 'browser' and r['method'] != 'import-only'), c['evidence'][0])
            writer.writerow([c['package'], c['name'], *c['counts'].values(), *c['providers'].values(), ref['path'], ref['line']])
    print('Source files:', dict(totals), 'Candidate files:', len(candidates))
    print('Packages:', json.dumps(packages))
    for c in classes:
        print(c['name'], c['counts'], c['providers'])
    print('Unknown:', unknown_refs)
    print('String mentions:', string_mentions)
    print('Conservative Trove source closure:', len(closure), '/', len(port_sources))
    print('Support sources:', sorted(closure - roots))


if __name__ == '__main__':
    main()
