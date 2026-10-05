#!/usr/bin/env python3
"""Inventory Java dependencies of the generated financial language; never reads MPS models."""
import ast
import base64
import collections
import hashlib
import json
import re
import subprocess
from pathlib import Path
from check_sources import java_code

ROOT = Path(__file__).resolve().parents[1]
ASPECTS = {'structure', 'behavior', 'constraints', 'typesystem', 'intentions', 'editor', 'actions'}
LANGUAGES = {
    'jetbrains.mps.samples.fincalculator': 'samples/financialCalculations/languages/calculator/source_gen',
    'jetbrains.mps.baseLanguage': 'languages/baseLanguage/baseLanguage/source_gen',
    'jetbrains.mps.lang.core': 'languages/core/core/source_gen',
    'jetbrains.mps.lang.traceable': 'languages/languageDesign/traceable/source_gen',
    'jetbrains.mps.execution.util': 'languages/util/execution/util/source_gen',
    'jetbrains.mps.baseLanguage.blTypes': 'languages/baseLanguage/blTypes/source_gen',
}

def main():
    paths = subprocess.check_output(['git', '-c', 'safe.directory=' + (ROOT / 'mps').as_posix(), '-C', str(ROOT / 'mps'), 'ls-files', '*.java'], text=True).splitlines()
    index = {}
    texts = {}
    for rel in paths:
        path = 'mps/' + rel
        source = (ROOT / path).read_text(encoding='utf-8-sig')
        match = re.search(r'^package\s+([\w.]+)\s*;', source, re.M)
        if not match:
            continue
        name = match[1] + '.' + Path(rel).stem
        # Record production provider; duplicate test fixtures are irrelevant.
        if name not in index or '/test' in index[name]:
            index[name] = path
            texts[name] = source
    existing = set()
    tree = ast.parse((ROOT / 'mps-browser-projects.bzl').read_text())
    projects = next(ast.literal_eval(n.value) for n in tree.body if isinstance(n, ast.Assign) and n.targets[0].id == 'MPS_BROWSER_PROJECTS')
    existing_paths = {'mps/' + p for project in projects.values() for p in project['srcs']}
    for name, path in index.items():
        if path in existing_paths:
            existing.add(name)
    roots = set()
    overrides = {}
    excluded_aspects = ['findUsages', 'migration', 'refactorings', 'scripts', 'textGen', 'plugin']
    for namespace, directory in LANGUAGES.items():
        name = namespace + '.Language'
        source = texts[name]
        removed_types = []
        for imported in re.findall(r'^import ([\w.]+);', source, re.M):
            if any('.' + aspect + '.' in imported for aspect in excluded_aspects) or imported.endswith(('.MigrationAspectDescriptor', '.RefactoringAspect', '.ScriptAspectDescriptor', '.TextGenAspectDescriptor', '.FindUsageAspectDescriptor', '.MakeAspectDescriptor')):
                removed_types.append(imported.rsplit('.', 1)[1])
                source = source.replace('import ' + imported + ';\n', '')
        for aspect in removed_types:
            source = re.sub(r'    if \(aspectClass == ' + aspect + r'\.class\) \{\n.*?\n    \}\n', '', source, flags=re.S)
        if source != texts[name]:
            path = 'financial-web/' + index[name][4:]
            target = ROOT / path
            target.parent.mkdir(parents=True, exist_ok=True)
            target.write_text(source, encoding='utf-8', newline='\n')
            overrides[index[name]] = path
            texts[name] = source
    for namespace, directory in LANGUAGES.items():
        for name, path in index.items():
            if path.startswith('mps/' + directory + '/'):
                suffix = name[len(namespace) + 1:]
                if name == namespace + '.Language' or suffix.split('.')[0] in ASPECTS:
                    roots.add(name)
    roots.add('jetbrains.mps.lang.typesystem.structure.StructureAspectDescriptor')
    roots.add('jetbrains.mps.java.stub.JavaPackageModelId')
    roots.add('jetbrains.mps.smodel.persistence.def.v9.ModelReader9Handler')
    selected = set()
    pending = collections.deque(sorted(roots))
    scheduled = set(roots)
    missing = collections.defaultdict(set)
    parents = {}
    def resolve(name):
        while '.' in name:
            if name in index:
                return name
            name = name.rsplit('.', 1)[0]
        return None
    while pending:
        name = pending.popleft()
        if name in existing or name in selected:
            continue
        selected.add(name)
        replacement = ROOT / ('financial-web/' + index[name][4:])
        if replacement.exists():
            overrides[index[name]] = replacement.relative_to(ROOT).as_posix()
            source = replacement.read_text(encoding='utf-8')
        else:
            source = texts[name]
        source = java_code(source)
        imports = re.findall(r'^import\s+(?:static\s+)?([\w.$]+);', source, re.M)
        references = imports + re.findall(r'\b(?:jetbrains|org\.jetbrains)\.[\w.$]+', source)
        package = name.rsplit('.', 1)[0]
        for token in set(re.findall(r'\b[A-Za-z_$][\w$]*\b', source)):
            if package + '.' + token in index:
                references.append(package + '.' + token)
        for reference in sorted(set(references)):
            target = resolve(reference)
            if target:
                if target not in scheduled and target not in existing:
                    pending.append(target)
                    scheduled.add(target)
                    parents[target] = name
            elif reference in imports and not reference.startswith(('java.', 'javax.', 'org.jetbrains.annotations.')):
                missing[reference].add(name)
    report = {'languages': LANGUAGES, 'root_count': len(roots), 'selected_count': len(selected),
              'sources': sorted(overrides.get(index[n], index[n]) for n in selected),
              'overrides': overrides, 'parents': parents,
              'packages': dict(collections.Counter(n.rsplit('.', 1)[0] for n in selected).most_common()),
              'missing_imports': {k: sorted(v) for k, v in sorted(missing.items())}}
    (ROOT / 'out').mkdir(exist_ok=True)
    (ROOT / 'out/financial-inventory.json').write_text(json.dumps(report, indent=2) + '\n')
    (ROOT / 'financial-sources.bzl').write_text('# Generated by tools/inventory_financial.py; separate generated-language deployment.\nFINANCIAL_SOURCES = ' + json.dumps(report['sources'], indent=4) + '\n')
    registration = ['package mpsj2cl.financial;', '', '// Generated from the selected, pinned language classes by tools/inventory_financial.py.',
                    'import java.util.ArrayList;', 'import java.util.Collections;', 'import java.util.List;',
                    'import java.util.HashSet;', 'import java.util.Set;',
                    'import jetbrains.mps.classloading.ModuleClassLoader;', 'import jetbrains.mps.classloading.FinancialClassLoader;',
                    'import jetbrains.mps.core.platform.BrowserPlatform;', 'import jetbrains.mps.smodel.language.LanguageRuntime;',
                    'public final class FinancialLanguages {', '  private final List<ModuleClassLoader> loaders = new ArrayList<>();',
                    '  private final Set<Class<?>> aspectRuleClasses = new HashSet<>();',
                    '  public FinancialLanguages(BrowserPlatform platform) {']
    order = ['jetbrains.mps.lang.core', 'jetbrains.mps.lang.traceable', 'jetbrains.mps.baseLanguage.blTypes', 'jetbrains.mps.baseLanguage', 'jetbrains.mps.execution.util', 'jetbrains.mps.samples.fincalculator']
    for i, namespace in enumerate(order):
        registration.append(f'    register{i}(platform);')
    registration += ['    platform.registerLanguage(new RuntimeTypesLanguage());']
    registration += ['  }', '  public void verifyTypesystems(BrowserPlatform platform) {']
    for namespace in order:
        if 'aspectClass == IHelginsDescriptor.class' in texts[namespace + '.Language']:
            variable = 'aspect' + str(order.index(namespace))
            registration += [f'    jetbrains.mps.lang.typesystem.runtime.IHelginsDescriptor {variable} = platform.findComponent(jetbrains.mps.smodel.language.LanguageRegistry.class).getLanguage("{namespace}").getAspect(jetbrains.mps.lang.typesystem.runtime.IHelginsDescriptor.class);',
                             f'    if ({variable} == null)',
                             f'      throw new IllegalStateException("Typesystem aspect failed to initialize: {namespace}");']
            for getter in ['getInferenceRules', 'getNonTypesystemRules', 'getSubtypingRules', 'getSubstituteTypeRules', 'getComparisonRules', 'getEliminationRules', 'getVariableConverters', 'getOverloadedOperationsTypesProviders']:
                registration.append(f'    bindRules({variable}.{getter}(), "{namespace}");')
    registration += ['  }']
    for i, namespace in enumerate(order):
        registration += [f'  private void register{i}(BrowserPlatform platform) {{',
                         f'    LanguageRuntime language = new {namespace}.Language();',
                         '    ModuleClassLoader loader = FinancialClassLoader.create(language.getIdentity().getSourceModuleReference());',
                         '    loaders.add(loader);']
        rules = []
        for name in sorted(selected):
            if not name.startswith(namespace + '.typesystem.'):
                continue
            source = texts[name]
            if not re.search(r'public (?:final )?(?:abstract )?class ' + re.escape(name.rsplit('.', 1)[1]) + r'\b', source):
                continue
            rules.append(name)
            rules.extend(name + '.' + nested for nested in re.findall(r'public static (?:final )?(?:abstract )?class (\w+)', source))
            if re.search(r'extends QuickFix_Runtime\b', source):
                registration.append(f'    loader.registerFactory("{name}", {name}.class, new Class<?>[0], args -> new {name}());')
        registration += ['    platform.registerLanguage(language, loader, Collections.emptyList(), Collections.emptyList(), new Class<?>[] {',
                         ',\n'.join('      ' + name + '.class' for name in rules), '    });', '  }']
    registration += ['  private void bindRules(Iterable<?> rules, String namespace) {',
                     '    for (Object rule : rules) {',
                     '      jetbrains.mps.lang.typesystem.runtime.RuleSet.registerRuleNamespace(rule.getClass(), namespace);',
                     '      aspectRuleClasses.add(rule.getClass());',
                     '    }', '  }',
                     '  public void close() {',
                     '    aspectRuleClasses.forEach(jetbrains.mps.lang.typesystem.runtime.RuleSet::unregisterRuleNamespace);',
                     '    aspectRuleClasses.clear();',
                     '    for (ModuleClassLoader loader : loaders) loader.dispose(); loaders.clear();', '  }', '}']
    (ROOT / 'browser/financial/FinancialLanguages.java').write_text('\n'.join(registration) + '\n')
    # Treat the accessory model as an opaque asset. The original MPS XML9 reader,
    # not this inventory script, interprets its serialized model data at runtime.
    accessory_path = 'mps/languages/baseLanguage/blTypes/languageAccessories/primitiveDescriptors.mps'
    accessory_bytes = (ROOT / accessory_path).read_bytes()
    encoded = base64.b64encode(accessory_bytes).decode('ascii')
    (ROOT / 'browser/financial/PrimitiveModelResource.java').write_text(
        'package mpsj2cl.financial;\n// Generated opaque accessory bytes; decoded by the original MPS XML9 reader.\n'
        'final class PrimitiveModelResource {\n  static byte[] bytes() { return java.util.Base64.getDecoder().decode("' + encoded + '"); }\n}\n')
    profile = {
        'schema_version': 1, 'mps_commit': json.loads((ROOT / 'source-lock.json').read_text())['mps']['commit'],
        'scope': 'Generated financialCalculations editor and semantic aspects; in-memory browser application',
        'languages': LANGUAGES, 'source_count': len(selected), 'source_manifest': 'financial-sources.bzl',
        'overrides': overrides,
        'accessory_model': {'source': accessory_path, 'sha256': hashlib.sha256(accessory_bytes).hexdigest(), 'reader': 'MPS ModelReader9Handler'},
        'support': ['BaseLanguage dataflow used by typechecking', 'Generated typesystem structure metadata for runtime variables', 'Compiled quick-fix factories'],
        'excluded': ['Generation and text generation', 'Migrations and make facets', 'Desktop execution', 'Saving models and user filesystem persistence', 'Reflection over JVM stub constants'],
    }
    (ROOT / 'compatibility/financial-language-profile.json').write_text(json.dumps(profile, indent=2) + '\n')
    print(json.dumps({k: report[k] for k in ['root_count', 'selected_count', 'packages']}, indent=2))

if __name__ == '__main__':
    main()
