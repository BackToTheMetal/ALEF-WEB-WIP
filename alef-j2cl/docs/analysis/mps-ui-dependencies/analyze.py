"""Reproducible direct-reference inventory; does not build or modify MPS."""
import ast
import bisect
import collections
import csv
import hashlib
import json
from pathlib import Path
import re

HERE = Path(__file__).resolve().parent
ROOT = HERE.parents[2]
MPS = ROOT.parent / 'mps-j2cl'
UPSTREAM = MPS / 'mps'
EXT = ROOT.parent / 'mps-extensions-j2cl'


def assignments(path):
    result = {}
    for node in ast.parse(path.read_text(encoding='utf-8')).body:
        if isinstance(node, ast.Assign) and isinstance(node.targets[0], ast.Name):
            try:
                result[node.targets[0].id] = ast.literal_eval(node.value)
            except (ValueError, TypeError):
                pass
    return result


# Preserve offsets/newlines while removing comments and literal contents.
LEX = re.compile(r'"""[\s\S]*?"""|"(?:\\.|[^"\\])*"|\'(?:\\.|[^\'\\])*\'|//[^\n]*|/\*[\s\S]*?\*/')
IMPORT = re.compile(r'^\s*import\s+(?:(static)\s+)?([\w.$*]+)(?:\s+as\s+(\w+))?\s*;?', re.M)
TOKEN = re.compile(r'\b[A-Za-z_$][\w$]*\b')
FQN = re.compile(r'\b(?:javax\.swing|java\.awt)(?:\.[A-Za-z_$][\w$]*)+')


def blank(match):
    return re.sub(r'[^\r\n]', ' ', match.group())


def auxiliary(path):
    parts = path.lower().split('/')
    return (parts[0] in {'testbench', 'tests', 'samples'} or
            any(p in {'test', 'tests', 'testdata', 'test_data', 'testsource', 'test_source', 'test_gen', 'test_source_gen', 'sandbox', 'sandboxes'} for p in parts) or
            any(p.endswith('.tests') or p.endswith('-tests') for p in parts))


def main():
    catalog_path = HERE / 'api-results/jdk-21.tsv'
    catalog = {}
    for row in csv.DictReader(catalog_path.open(encoding='utf-8'), delimiter='\t'):
        if row['kind'] == 'type':
            catalog[row['owner']] = {'package': row['package'], 'signature': row['signature']}
    by_package = collections.defaultdict(dict)
    for name, row in catalog.items():
        short = name[len(row['package']) + 1:]
        if '.' not in short:
            by_package[row['package']][short] = name

    def owner(name):
        while '.' in name:
            if name in catalog:
                return name
            name = name.rsplit('.', 1)[0]
        return None

    def references(text):
        code = LEX.sub(blank, text)
        imports = list(IMPORT.finditer(code))
        body = IMPORT.sub(blank, code)
        tokens = collections.defaultdict(list)
        for match in TOKEN.finditer(body):
            tokens[match[0]].append(match.start())
        newlines = [i for i, c in enumerate(code) if c == '\n']
        found = {}
        explicit = {}
        wildcards = []
        for match in imports:
            static, name, alias = match.groups()
            if not name.endswith('.*') and not static:
                explicit[alias or name.rsplit('.', 1)[-1]] = name
        def add(name, pos, method):
            if name:
                evidence = {'line': bisect.bisect_left(newlines, pos) + 1, 'method': method}
                # Retain actual code evidence in preference to an import-only reference.
                if name not in found or found[name]['method'] == 'import-only':
                    found[name] = evidence
        for match in imports:
            static, name, alias = match.groups()
            if not name.startswith(('javax.swing.', 'java.awt.')):
                continue
            if name.endswith('.*') and not static:
                if name[:-2] in by_package:
                    wildcards.append(name[:-2])
                else:
                    outer = owner(name[:-2])
                    if outer:
                        add(outer, match.start(), 'type-wildcard-import')
                        for nested in catalog:
                            if nested.rsplit('.', 1)[0] == outer and nested.rsplit('.', 1)[1] in tokens:
                                add(nested, tokens[nested.rsplit('.', 1)[1]][0], 'nested-wildcard')
                continue
            target = owner(name[:-2] if name.endswith('.*') else name)
            simple = alias or name.rsplit('.', 1)[-1]
            if target:
                add(target, tokens[simple][0] if simple in tokens else match.start(),
                    'static-import' if static else ('explicit-import-and-token' if simple in tokens else 'import-only'))
        for package in wildcards:
            for simple, target in by_package[package].items():
                if simple in tokens and (simple not in explicit or explicit[simple] == target):
                    add(target, tokens[simple][0], 'wildcard-and-token')
        for match in FQN.finditer(body):
            add(owner(match[0]), match.start(), 'fully-qualified')
        # Outer.Inner references where Outer has an explicit or wildcard import.
        for simple, positions in tokens.items():
            base = explicit.get(simple)
            if not base:
                bases = [by_package[p][simple] for p in wildcards if simple in by_package[p]]
                base = bases[0] if len(bases) == 1 else None
            if base in catalog:
                for pos in positions:
                    match = re.match(r'[\w$]+(?:\.[\w$]+)+', body[pos:])
                    if match:
                        nested = owner(base + match[0][len(simple):])
                        if nested and nested != base:
                            add(nested, pos, 'nested-qualified')
        return found

    # Regression checks for meaningful scanner boundaries.
    probe = references('import java.awt.*;\nimport javax.swing.Timer;\n// java.awt.Robot\nclass P { Color c; Timer t; String s = "java.awt.Desktop"; }')
    assert set(probe) == {'java.awt.Color', 'javax.swing.Timer'}, probe
    probe = references('import java.awt.geom.Rectangle2D;\nclass P { Rectangle2D.Double r; }')
    assert set(probe) == {'java.awt.geom.Rectangle2D', 'java.awt.geom.Rectangle2D.Double'}, probe

    tracked = (HERE / 'tracked-sources.txt').read_text(encoding='utf-8-sig').splitlines()
    candidates = (HERE / 'candidate-sources.txt').read_text(encoding='utf-8-sig').splitlines()
    evidence = collections.defaultdict(list)
    totals = collections.Counter()
    for relative in tracked:
        totals['auxiliary' if auxiliary(relative) else 'upstream'] += 1
    def scan(path, scope, logical):
        text = path.read_text(encoding='utf-8', errors='replace')
        if 'javax.swing' not in text and 'java.awt' not in text:
            return
        for name, ref in references(text).items():
            evidence[name].append(dict(ref, scope=scope, path=path.as_posix(), logical=logical))
    for relative in candidates:
        scan(UPSTREAM / relative, 'auxiliary' if auxiliary(relative) else 'upstream', relative)

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
    base_count = len(selected)
    for relative in assignments(MPS / 'financial-sources.bzl')['FINANCIAL_SOURCES']:
        if '/samples/' not in relative:
            logical = relative.split('/', 1)[1]
            selected.setdefault(logical, MPS / relative)
    financial_count = len(selected) - base_count
    for relative in assignments(ROOT / 'mps-sources.bzl')['MPS_SOURCES']:
        logical = relative.removeprefix('.work/mps-src/').removeprefix('j2cl-src/mps/')
        path = UPSTREAM / logical if relative.startswith('.work/mps-src/') else ROOT / relative
        selected.setdefault(logical, path)
    for logical, path in sorted(selected.items()):
        assert path.is_file(), path
        scan(path, 'browser', logical)
    totals['browser'] = len(selected)

    classes = []
    for name in sorted(evidence):
        refs = evidence[name]
        counts = {scope: len({r['path'] for r in refs if r['scope'] == scope and r['method'] != 'import-only'}) for scope in ('upstream', 'browser', 'auxiliary')}
        import_counts = {scope: sum(r['scope'] == scope and r['method'] == 'import-only' for r in refs) for scope in counts}
        classes.append({'name': name, 'package': catalog[name]['package'], 'counts': counts,
                        'import_only': sum(import_counts.values()), 'import_counts': import_counts, 'evidence': refs})
    packages = []
    for package in sorted(by_package):
        members = [c for c in classes if c['package'] == package]
        packages.append({'name': package, 'available_top_level_types': len(by_package[package]),
                         'counts': {scope: sum(c['counts'][scope] > 0 for c in members) for scope in ('upstream', 'browser', 'auxiliary')},
                         'classes': [c['name'] for c in members]})
    data = {'commit': (HERE / 'commit.txt').read_text(encoding='utf-8-sig').strip(),
            'source_file_counts': dict(totals), 'candidate_file_count': len(candidates),
            'browser_selection': {'base': base_count, 'financial_support': financial_count, 'supplemental': len(selected)-base_count-financial_count},
            'catalog_sha256': hashlib.sha256(catalog_path.read_bytes()).hexdigest(),
            'packages': packages, 'classes': classes,
            'browser_sources': [{'logical': k, 'path': p.as_posix(), 'sha256': hashlib.sha256(p.read_bytes()).hexdigest()} for k, p in sorted(selected.items())]}
    (HERE / 'inventory.json').write_text(json.dumps(data, indent=2) + '\n', encoding='utf-8')
    with (HERE / 'classes.csv').open('w', encoding='utf-8', newline='') as stream:
        writer = csv.writer(stream)
        writer.writerow(['Package', 'Class or interface', 'Upstream files', 'Browser files', 'Auxiliary files', 'Import-only evidence rows', 'Example source', 'Line'])
        for c in classes:
            ref = next((r for r in c['evidence'] if r['scope'] == 'browser'), c['evidence'][0])
            writer.writerow([c['package'], c['name'], *c['counts'].values(), c['import_only'], ref['path'], ref['line']])
    print(json.dumps({k: data[k] for k in ('commit', 'source_file_counts', 'browser_selection')}, indent=2))
    for prefix in ('javax.swing', 'java.awt'):
        rows = [c for c in classes if c['name'].startswith(prefix + '.')]
        print(prefix, {scope: sum(c['counts'][scope] > 0 for c in rows) for scope in ('upstream', 'browser', 'auxiliary')})
        for p in packages:
            if p['name'].startswith(prefix):
                print(p['name'], p['counts'])
    print('Evidence methods:', dict(collections.Counter(r['method'] for c in classes for r in c['evidence'])))


if __name__ == '__main__':
    main()
