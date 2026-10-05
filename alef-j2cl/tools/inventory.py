#!/usr/bin/env python3
"""Select the requested ALEF aspects and their Java source dependencies."""
import collections
import hashlib
import json
from pathlib import Path
import re
import subprocess

ROOT = Path(__file__).resolve().parents[1]
ASPECTS = {'structure', 'behavior', 'intentions', 'typesystem', 'constraints', 'editor'}
LANGUAGES = {'regelspraak', 'gegevensspraak', 'servicespraak', 'testspraak'}
OMITTED = {'findUsages', 'migration', 'refactorings', 'scripts', 'textGen', 'plugin', 'projecthygiene'}


def code_only(text):
    return re.sub(r'//[^\n]*|/\*[\s\S]*?\*/|"(?:\\.|[^"\\])*"|\'(?:\\.|[^\'\\])*\'',
                  lambda m: ' ' * len(m[0]), text)


def main():
    report = json.loads((ROOT / 'out/generated-sources.json').read_text())
    paths = [entry['path'] for entry in report['sources'] if entry['path'].endswith('.java')]
    paths += ['alef/' + path for path in subprocess.check_output(
        ['git', '-C', str(ROOT / 'alef'), 'ls-files', 'AlefJava/*/src/main/java/*.java'], text=True).splitlines()]
    index, texts = {}, {}
    for relative in paths:
        path = ROOT / relative
        # The generation cache is on native WSL storage and much faster to read.
        native = ROOT / '.work/generation/alef' / relative.removeprefix('upstream-src/')
        text = (native if relative.startswith('upstream-src/') and native.exists() else path).read_text()
        match = re.search(r'^package\s+([\w.]+);', text, re.M)
        if not match:
            continue
        name = match[1] + '.' + path.stem
        if name in index:
            raise RuntimeError(f'Duplicate source provider: {name}: {index[name]}, {relative}')
        index[name], texts[name] = relative, text
    languages = set(LANGUAGES) | {'xml.schema'}
    pending_languages = list(languages)
    while pending_languages:
        lang = pending_languages.pop()
        descriptor = texts[lang + '.Language']
        block = descriptor.split('protected void fillExtendedLanguages', 1)[-1].split('\n  }', 1)[0]
        for parent in re.findall(r'MetaAdapterFactory.getLanguage\([^\n]*"([\w.]+)"', block):
            if parent + '.Language' in index and parent not in languages:
                languages.add(parent)
                pending_languages.append(parent)
    roots = {name for name in index if any(name == lang + '.Language' or
        (lang in LANGUAGES and name.startswith(lang + '.') and name[len(lang) + 1:].split('.')[0] in ASPECTS) for lang in languages)}
    # Service and test execution are part of the browser profile, including
    # translators which were previously reached only from desktop actions.
    roots.update(name for name in index if name.startswith(('servicespraak.translator.', 'testspraak.translator.')))
    # Quick-fix providers name their classes as strings, including inherited languages.
    roots.update(name for name in index if name.endswith('_QuickFix') and
                 any(name.startswith(lang + '.typesystem.') for lang in languages if lang != 'rapporten'))
    # Typed browser bindings can make previously reflective dependencies explicit.
    for binding in (ROOT / 'browser-src').rglob('*.java'):
        code = code_only(binding.read_text())
        roots.update(name for name in index if name in code)
    for lang in LANGUAGES:
        for aspect in ASPECTS:
            if not any(name.startswith(lang + '.' + aspect + '.') for name in roots):
                raise RuntimeError(f'Missing generated aspect {lang}.{aspect}; generation is incomplete')
    overrides = {}
    # Limit language aspect registration. Other referenced helper classes remain
    # eligible for the closure, including helpers in otherwise omitted aspects.
    for lang in sorted(languages):
        name = lang + '.Language'
        text = texts[name]
        original = text
        for imported in re.findall(r'^import ([\w.]+);', original, re.M):
            # Reporting UI/export actions are outside the requested languages.
            reporting_ui = lang == 'rapporten' and imported.endswith(('.EditorAspectDescriptor', '.EditorAspectDescriptorImpl', '.IntentionAspectDescriptor', '.IntentionsDescriptor', '.BehaviorAspectDescriptor', '.ConstraintsAspectDescriptor', '.IHelginsDescriptor', '.TypesystemDescriptor'))
            if lang == 'xml.schema':
                reporting_ui = imported.endswith(('.EditorAspectDescriptor', '.EditorAspectDescriptorImpl', '.IntentionAspectDescriptor', '.IntentionsDescriptor', '.IHelginsDescriptor', '.TypesystemDescriptor'))
            if reporting_ui or any('.' + aspect + '.' in imported for aspect in OMITTED) or imported.endswith((
                '.MigrationAspectDescriptor', '.RefactoringAspect', '.ScriptAspectDescriptor',
                '.TextGenAspectDescriptor', '.FindUsageAspectDescriptor', '.MakeAspectDescriptor', '.IProjectHygieneAspectDescriptor')):
                simple = imported.rsplit('.', 1)[1]
                text = text.replace('import ' + imported + ';\n', '')
                text = re.sub(r'    if \(aspectClass == ' + simple + r'\.class\) \{\n.*?\n    \}\n', '', text, flags=re.S)
                text = re.sub(r'^    extensions\.recordContribution\([^\n]*\b' + re.escape(simple) + r'\.class\);\n', '', text, flags=re.M)
        replacement = ROOT / 'j2cl-src/alef' / index[name].removeprefix('upstream-src/')
        if text != original and (not replacement.exists() or lang in {'rapporten', 'servicespraak', 'testspraak', 'xml.schema'}):
            replacement.parent.mkdir(parents=True, exist_ok=True)
            replacement.write_text(text)
    def resolve(name):
        while '.' in name:
            if name in index:
                return name
            name = name.rsplit('.', 1)[0]
        return None
    extensions = collections.defaultdict(list)
    for name, text in texts.items():
        if not any(name.startswith(lang + '.translator.') for lang in languages):
            continue
        match = re.search(r'public class \w+ extends ([\w.]+)', code_only(text))
        if not match:
            continue
        superclass = match[1]
        imports = {i.rsplit('.', 1)[-1]: i for i in re.findall(r'^import ([\w.]+);', text, re.M)}
        head, *tail = superclass.split('.')
        parent = imports.get(head, name.rsplit('.', 1)[0] + '.' + head)
        if tail:
            parent += '.' + '.'.join(tail)
        parent = resolve(parent)
        if parent:
            extensions[parent].append(name)
    pending = collections.deque(sorted(roots))
    selected, parents, unresolved = set(), {}, collections.defaultdict(set)
    while pending:
        name = pending.popleft()
        if name in selected:
            continue
        selected.add(name)
        for extension in extensions[name]:
            pending.append(extension)
            parents.setdefault(extension, name)
        relative = index[name]
        replacement = ROOT / 'j2cl-src/alef' / relative.removeprefix('upstream-src/').removeprefix('alef/')
        if replacement.exists():
            overrides[relative] = replacement.relative_to(ROOT).as_posix()
            text = replacement.read_text()
        else:
            text = texts[name]
        code = code_only(text)
        imports = re.findall(r'^import\s+(?:static\s+)?([\w.$*]+);', code, re.M)
        tokens = set(re.findall(r'\b[A-Za-z_$][\w$]*\b', code))
        package = name.rsplit('.', 1)[0]
        references = set(imports) | {package + '.' + token for token in tokens}
        references.update(re.findall(r'\b[a-z][\w$]*(?:\.[\w$]+)+', code))
        for imported in imports:
            if imported.endswith('.*'):
                references.update(imported[:-1] + token for token in tokens)
        for reference in sorted(references):
            target = resolve(reference)
            if target and target not in selected:
                pending.append(target)
                parents.setdefault(target, name)
            elif not target and reference in imports and not reference.startswith(('java.', 'javax.', 'org.jetbrains.annotations.')):
                unresolved[reference].add(name)
    sources = sorted(overrides.get(index[name], index[name]) for name in selected)
    report = {'languages': sorted(languages), 'roots': sorted(roots), 'selected_count': len(selected), 'sources': sources,
              'overrides': overrides, 'parents': parents,
              'packages': dict(collections.Counter(name.rsplit('.', 1)[0] for name in selected).most_common()),
              'external_imports': {key: sorted(value) for key, value in sorted(unresolved.items())},
              'selected_hashes': {p: hashlib.sha256((ROOT / p).read_bytes()).hexdigest() for p in sources}}
    (ROOT / 'out/source-inventory.json').write_text(json.dumps(report, indent=2) + '\n')
    (ROOT / 'alef-sources.bzl').write_text('# Generated by tools/inventory.py; originals replaced explicitly.\nALEF_SOURCES = ' + json.dumps(sources, indent=4) + '\n')
    print(f'Selected {len(roots)} aspect roots, {len(selected)} Java sources, {len(overrides)} replacements')


if __name__ == '__main__':
    main()
