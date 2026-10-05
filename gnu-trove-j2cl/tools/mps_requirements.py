#!/usr/bin/env python3
"""Inventory pinned MPS Java/Kotlin and serialized models; close over port sources.

Refresh: python tools/mps_requirements.py --archive out/research/mps-pinned.tar.gz
Ordinary validation checks the recorded roots/closure without downloading MPS.
"""
import argparse
from collections import Counter, defaultdict
import hashlib
import json
from pathlib import Path
import re
import tarfile
import zipfile

from port_sources import ROOT, SOURCE, mask, port

PROFILE = ROOT / 'compatibility/mps-required.json'
FQN = re.compile(r'\bgnu\.trove(?:\.[A-Za-z_$][\w$]*)+')
IMPORT = re.compile(r'\bimport\s+(?:static\s+)?(\w+(?:\.\w+)*)(\.\*)?(?:\s+as\s+(\w+))?\s*;?')


def source_catalog():
    """Reconstruct the full candidate catalog even after unused files are removed."""
    with zipfile.ZipFile(SOURCE) as jar:
        sources = {p[:-5].replace('/', '.'): port(Path(p).stem, jar.read(p).decode())
                   for p in sorted(jar.namelist()) if p.endswith('.java')
                   and not p.endswith('/SerializationProcedure.java')}
    for name in ('ArraySupport', 'FloatSupport'):
        sources['gnu.trove.' + name] = (ROOT / 'src/main/java/gnu/trove' / (name + '.java')).read_text()
    return sources


def named_references(text, names):
    """Return owners of qualified names, imports and same-package identifiers."""
    code = mask(text)
    tokens = set(re.findall(r'\b\w+\b', code))
    refs = set()
    package = re.search(r'\bpackage\s+([\w.]+)', code)
    packages = {package[1]} if package else set()
    for match in IMPORT.finditer(code):
        name = match[1].rstrip('.')
        if match[2]:
            packages.add(name)
        else:
            owner = name
            while '.' in owner and owner not in names:
                owner = owner.rsplit('.', 1)[0]
            if owner in names:
                refs.add(owner)  # Even unused explicit imports must still compile.
    for name in names:
        package, short = name.rsplit('.', 1)
        if package in packages and short in tokens:
            refs.add(name)
    for match in FQN.finditer(code):
        name = match[0]
        while '.' in name and name not in names:
            name = name.rsplit('.', 1)[0]
        if name in names:
            refs.add(name)
    return refs


def closure(sources, roots):
    dependencies = {n: sorted(named_references(t, sources) - {n}) for n, t in sources.items()}
    kept, parents, pending = set(), {}, sorted(set(roots) & sources.keys())
    while pending:
        name = pending.pop(0)
        if name in kept:
            continue
        kept.add(name)
        for dep in dependencies[name]:
            if dep not in kept:
                parents.setdefault(dep, name)
                pending.append(dep)
    return sorted(kept), {n: dependencies[n] for n in sorted(kept)}, parents


def auxiliary(path):
    return any(p.lower() in {'test', 'tests', 'testbench', 'testsolutions', 'samples'}
               for p in Path(path).parts)


def inspect_archive(archive):
    revision = (ROOT / 'third_party/mps-revision.txt').read_text().strip()
    sources = source_catalog()
    names = set(sources)
    for jar_path in (ROOT / 'third_party').glob('*.jar'):
        with zipfile.ZipFile(jar_path) as jar:
            names.update(p[:-6].replace('/', '.') for p in jar.namelist()
                         if p.startswith('gnu/trove/') and p.endswith('.class') and '$' not in p)
    evidence, counts, files, mentions = defaultdict(list), Counter(), {}, []
    with tarfile.open(archive, 'r|gz') as tar:
        for member in tar:
            prefix, _, path = member.name.partition('/')
            if prefix != 'MPS-' + revision:
                raise ValueError('Archive is not rooted at the pinned MPS revision')
            if not member.isfile() or Path(path).suffix not in {'.java', '.kt', '.mps', '.xml', '.msd', '.mpl', '.devkit'}:
                continue
            counts[Path(path).suffix] += 1
            raw = tar.extractfile(member).read()
            if b'gnu.trove' not in raw:
                continue
            text = raw.decode('utf-8-sig')
            lines = text.splitlines()
            files[path] = hashlib.sha256(raw).hexdigest()
            scope = 'auxiliary' if auxiliary(path) else 'main'
            seen = set()

            def add(name, line, kind):
                if (name, kind) not in seen:
                    evidence[name].append({'path': path, 'line': line, 'kind': kind, 'scope': scope,
                                           'text': lines[line - 1].strip()})
                    seen.add((name, kind))

            # Java imports and wildcard resolution; literals separately below.
            if Path(path).suffix in {'.java', '.kt'}:
                for name in named_references(text, names):
                    short = name.rsplit('.', 1)[-1]
                    line = next(i for i, t in enumerate(lines, 1) if re.search(r'\b' + short + r'\b', t))
                    add(name, line, 'source')
            # MPS model references use import indices, not Java import syntax.
            aliases = {m[1]: m[2] for m in re.finditer(
                r'<import\s+index="([^"]+)"\s+ref="[^"]*java:(gnu\.trove(?:\.decorator)?)\(', text)}
            for i, line in enumerate(lines, 1):
                for match in re.finditer(r'\bto="([^:"]+):~([\w$]+)', line):
                    if match[1] in aliases:
                        add(aliases[match[1]] + '.' + match[2], i, 'model')
                for match in FQN.finditer(line):
                    owner = match[0]
                    while '.' in owner and owner not in names:
                        owner = owner.rsplit('.', 1)[0]
                    if owner in names:
                        add(owner, i, 'qualified-mention')
                if 'gnu.trove' in line:
                    mentions.append({'path': path, 'line': i, 'text': line.strip()})
                # Reviewed generator family: six numeric primitive list wrappers.
                if '"gnu.trove.T"' in line and '"ArrayList"' in line:
                    for primitive in ('Byte', 'Short', 'Int', 'Long', 'Float', 'Double'):
                        add('gnu.trove.T' + primitive + 'ArrayList', i, 'generated-family')
    roots = sorted(n for n, refs in evidence.items() if any(r['scope'] == 'main' for r in refs))
    kept, dependencies, parents = closure(sources, roots)
    with archive.open('rb') as stream:
        archive_hash = hashlib.sha256()
        for chunk in iter(lambda: stream.read(1024 * 1024), b''):
            archive_hash.update(chunk)
    data = {'schemaVersion': 1, 'revision': revision,
            'archiveUrl': 'https://codeload.github.com/JetBrains/MPS/tar.gz/' + revision,
            'archiveSha256': archive_hash.hexdigest(),
            'scope': 'Main pinned MPS sources, serialized models and generated list family; auxiliary references recorded separately',
            'scannedFilesByExtension': dict(sorted(counts.items())), 'evidenceFileHashes': dict(sorted(files.items())),
            'roots': roots, 'unsupportedRoots': sorted(set(roots) - sources.keys()),
            'retained': kept, 'removed': sorted(sources.keys() - set(kept)),
            'dependencies': dependencies, 'dependencyParents': dict(sorted(parents.items())),
            'evidence': dict(sorted(evidence.items())), 'qualifiedMentions': mentions}
    PROFILE.write_text(json.dumps(data, indent=2) + '\n')
    print(f'MPS roots: {len(roots)}; retained: {len(kept)}/{len(sources)}; absent from baseline: {len(data["unsupportedRoots"])}')
    print('Remove:', ', '.join(data['removed']))


def check_profile(check_files=True):
    data = json.loads(PROFILE.read_text())
    if data['revision'] != (ROOT / 'third_party/mps-revision.txt').read_text().strip():
        raise ValueError('MPS profile revision drift')
    roots = sorted(n for n, refs in data['evidence'].items() if any(r['scope'] == 'main' for r in refs))
    sources = source_catalog()
    kept, dependencies, parents = closure(sources, roots)
    expected = {'roots': roots, 'retained': kept, 'dependencies': dependencies, 'dependencyParents': parents,
                'removed': sorted(sources.keys() - set(kept)), 'unsupportedRoots': sorted(set(roots) - sources.keys())}
    for key, value in expected.items():
        if data[key] != value:
            raise ValueError('MPS profile drift: ' + key)
    if check_files:
        actual = sorted(p.relative_to(ROOT / 'src/main/java').as_posix()[:-5].replace('/', '.')
                        for p in (ROOT / 'src/main/java').rglob('*.java'))
        if actual != kept:
            raise ValueError('Production source set differs from MPS dependency closure')
    return data


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--archive', type=Path)
    args = parser.parse_args()
    if args.archive:
        inspect_archive(args.archive)
    else:
        data = check_profile()
        print(f'MPS source closure: PASS ({len(data["retained"])} files)')
