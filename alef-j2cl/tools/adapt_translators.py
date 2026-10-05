#!/usr/bin/env python3
"""Generate checked, typed equivalents of ALEF translator reflection sites."""
import json
from pathlib import Path
import re
from inventory import code_only

ROOT = Path(__file__).resolve().parents[1]
RUNTIME = 'plugins/translator/solutions/translator.runtime/source_gen/translator/runtime/runtime/'


def end_at(code, start, opening='{', closing='}'):
    depth = 0
    for i in range(start, len(code)):
        if code[i] == opening:
            depth += 1
        elif code[i] == closing:
            depth -= 1
            if not depth:
                return i
    raise ValueError('Unbalanced generated Java')


def params(text):
    result, depth, start = [], 0, 0
    for i, char in enumerate(text):
        if char == '<': depth += 1
        elif char == '>': depth -= 1
        elif char == ',' and depth == 0:
            result.append(text[start:i].strip())
            start = i + 1
    if text[start:].strip(): result.append(text[start:].strip())
    return result


def boxed(name):
    return {'boolean': 'Boolean', 'int': 'Integer', 'long': 'Long', 'double': 'Double',
            'float': 'Float', 'byte': 'Byte', 'short': 'Short', 'char': 'Character'}.get(name, name)


def body(text, signature, replacement):
    code = code_only(text)
    start = code.index('{', code.index(signature))
    end = end_at(code, start)
    return text[:start] + '{\n' + replacement + '\n  }' + text[end + 1:]


def save(relative, text):
    path = ROOT / 'j2cl-src/alef' / relative
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(text)


def main():
    report = json.loads((ROOT / 'out/source-inventory.json').read_text())
    originals = set(report['overrides']) | {p for p in report['sources'] if p.startswith('upstream-src/')}
    sources = {p: (ROOT / p).read_text() for p in sorted(originals) if p.startswith('upstream-src/')}
    translator_names = set()
    for source in sources.values():
        if 'List<Mapping> mappings' in source:
            translator_names.update(re.findall(r'public (\w+)\(TranslatorScope\s', code_only(source)))
    files, classes = [], []
    for relative in sorted(originals):
        if not relative.startswith('upstream-src/'):
            continue
        text = sources[relative]
        if relative.endswith('/xmlUtils/XMLImporter.java'):
            text = text.replace('org.jdom2.', 'org.jdom.')
        if relative.endswith('/testspraak/translator/ServiceTestNaarTestSet.java'):
            text = text.replace('import alef.menus.plugin.FilterIdeMenus;\n', '')
            text = text.replace('FilterIdeMenus.isEnabled()', 'true')
        if relative.endswith('/testspraak/translator/TestspraakInterpreter.java'):
            # Closure cannot type the wildcard Iterable returned by this generated
            # flat-map lambda. Keep the same order and duplicates with a typed loop.
            text, count = re.subn(
                r'    final List<RtObject> regels = ListSequence.fromList\((.*?)\)\.translate\(\(regelVersie\) -> (.*?)\)\.toList\(\);',
                r'    final List<RtObject> regels = new ArrayList<>();\n    for (SNode regelVersie : \1) {\n      regels.addAll(\2);\n    }', text)
            if count != 1:
                raise RuntimeError('Expected the consistency prediction flat-map')
        if relative.endswith('/interpreter/debug/translator/ToDebugNode.java'):
            text = text.replace('RtObjectSet set, final Map<RtObject, SNode> objects)',
                                'RtObjectSet<?> set, final Map<RtObject, SNode> objects)')
        if 'List<Mapping> mappings' not in text:
            continue
        code = code_only(text)
        package = re.search(r'^package ([\w.]+);', text, re.M)[1]
        declarations = []
        for match in re.finditer(r'public\s+(?:static\s+)?(?:abstract\s+)?class\s+(\w+)(?:\s+extends\s+([\w.]+))?[^\{]*\{', code):
            start = match.end() - 1
            end = end_at(code, start)
            parents = [c for c in declarations if c['start'] < start < c['end']]
            cls = {'name': match[1], 'parent': match[2], 'start': start, 'end': end,
                   'path': '.'.join([c['name'] for c in parents] + [match[1]])}
            cls['fqn'] = package + '.' + cls['path']
            declarations.append(cls)
        concrete = []
        for cls in declarations:
            part = code[cls['start']:cls['end']]
            constructor = re.search(r'public ' + cls['name'] + r'\((TranslatorScope\s+[^)]*)\)', part)
            if constructor:
                cls['constructor'] = params(constructor[1])
                concrete.append(cls)
        edits = []
        for match in re.finditer(r'Mapping\.of\(', code):
            end = end_at(code, match.end() - 1, '(', ')')
            call = text[match.end():end]
            cls = max((c for c in concrete if c['start'] < match.start() < c['end']), key=lambda c: c['start'])
            name = re.search(r'^\w+\.class, "([\w$]+)"', call)[1]
            part = code[cls['start']:cls['end']]
            sig_start = call.index('new Signature(') + len('new Signature(')
            signature = params(call[sig_start:end_at(call, sig_start - 1, '(', ')')])[2:]
            expected = ['SNode' if s.startswith('CONCEPTS.') else 'SConcept' if s.startswith('CONCEPT_TYPE.') else s.removesuffix('.class').rsplit('.', 1)[-1] for s in signature]
            candidates = []
            for candidate in re.finditer(r'public static ([\w.<>, ?\[\]]+) ' + re.escape(name) + r'\(([^)]*)\)', part):
                position = cls['start'] + candidate.start()
                if any(c['start'] > cls['start'] and c['start'] < position < c['end'] for c in declarations):
                    continue
                formal = [re.sub(r'<.*>', '', p.rsplit(' ', 1)[0].removeprefix('final ')).rsplit('.', 1)[-1] for p in params(candidate[2])[1:]]
                if formal and formal[0] == 'Guard':
                    formal = formal[1:]
                if list(map(boxed, formal)) == list(map(boxed, expected)):
                    candidates.append(candidate)
            if len(candidates) != 1:
                raise RuntimeError(f'Expected one mapping implementation: {cls["fqn"]}.{name} {expected}; found {len(candidates)}')
            method = candidates[0]
            types = [p.rsplit(' ', 1)[0].removeprefix('final ') for p in params(method[2])]
            # A raw RtObjectSet also implements raw Iterable, making the generated
            # Iterable<RtObject>/RtObjectSet overload pair ambiguous to javac.
            types = ['RtObjectSet<?>' if typ == 'RtObjectSet' else typ for typ in types]
            args = ', '.join('(' + typ + ') values[' + str(i) + ']' for i, typ in enumerate(types))
            invoke = cls['name'] + '.' + name + '(' + args + ')'
            expression = '{ ' + invoke + '; return null; }' if method[1].strip() == 'void' else invoke
            edits.append((end, end, ', values -> ' + expression))
        for cls in concrete:
            children = [c for c in concrete if c['path'].rsplit('.', 1)[0] == cls['path']]
            own_code = code[cls['start']:cls['end']]
            # Mask nested declarations before looking for fields.
            for child in declarations:
                if cls['start'] < child['start'] < child['end'] < cls['end']:
                    a, b = child['start'] - cls['start'], child['end'] - cls['start']
                    own_code = own_code[:a] + ' ' * (b-a) + own_code[b:]
            args = ['scope'] + ['(' + p.rsplit(' ', 1)[0] + ') args[' + str(i) + ']'
                                for i, p in enumerate(cls['constructor'][1:])]
            methods = '\n  public static translator.runtime.runtime.TranslatorBase __browserNew(translator.runtime.runtime.TranslatorScope scope, Object[] args) { return new ' + cls['name'] + '(' + ', '.join(args) + '); }\n'
            methods += '  public static java.util.List<translator.runtime.runtime.Mapping> __browserMappings() { return mappings; }\n'
            methods += '  public static java.util.List<Class<?>> __browserExtends() { return ' + ('xtraExtends' if ' xtraExtends ' in own_code else 'java.util.Collections.emptyList()') + '; }\n'
            methods += '  public static Class<?>[] __browserNested() { return new Class<?>[]{' + ', '.join(c['path'] + '.class' for c in children) + '}; }\n'
            # Imported translator fields are initialized by the original constructor.
            fields = []
            for field in re.finditer(r'^\s*(?:public|protected|private)\s+(?:final\s+)?([\w.]+)\s+(\w+)\s*(?:=|;)', own_code, re.M):
                if field[1].rsplit('.', 1)[-1] in translator_names:
                    fields.append(field[2])
            methods += '  public translator.runtime.runtime.TranslatorBase[] __browserUsed() { return new translator.runtime.runtime.TranslatorBase[]{' + ', '.join(fields) + '}; }\n'
            cls['has_used'] = bool(fields)
            edits.append((cls['end'], cls['end'], methods))
        for start, end, replacement in sorted(edits, reverse=True):
            text = text[:start] + replacement + text[end:]
        save(relative.removeprefix('upstream-src/'), text)
        classes.extend(concrete)
        files.append(relative)
    registry = '''package alefj2cl;
import translator.runtime.runtime.*;
import java.util.*;
/** Generated typed bindings; unknown translators fail explicitly. */
public final class TranslatorRegistry {
'''
    for result, name, arguments, expression in [
        ('TranslatorBase', 'create', ', TranslatorScope scope, Object[] args', '__browserNew(scope, args)'),
        ('List<Mapping>', 'mappings', '', '__browserMappings()'),
        ('List<Class<?>>', 'extendsOf', '', '__browserExtends()'),
        ('Class<?>[]', 'nested', '', '__browserNested()'),
    ]:
        registry += '  public static ' + result + ' ' + name + '(Class<?> type' + arguments + ') {\n'
        for cls in classes:
            registry += '    if (type == ' + cls['fqn'] + '.class) return ' + cls['fqn'] + '.' + expression + ';\n'
        registry += '    throw new IllegalArgumentException("Unregistered translator: " + type.getName());\n  }\n'
    registry += '  public static TranslatorBase[] used(TranslatorBase instance) {\n'
    for cls in classes:
        registry += '    if (instance.getClass() == ' + cls['fqn'] + '.class) return ((' + cls['fqn'] + ') instance).__browserUsed();\n'
    registry += '    throw new IllegalArgumentException("Unregistered translator: " + instance.getClass().getName());\n  }\n}\n'
    registry = registry[:-2] + '  public static boolean isConcrete(Class<?> type) {\n    return ' + ' || '.join('type == ' + c['fqn'] + '.class' for c in classes) + ';\n  }\n}\n'
    registry = registry[:-2] + '  public static List<Class<TranslatorBase>> extensions(Set<String> languages) {\n    List<Class<TranslatorBase>> result = new ArrayList<>();\n'
    for cls in classes:
        if '.' not in cls['path']:
            language = cls['fqn'].split('.translator.')[0]
            registry += '    if (languages.contains("' + language + '")) result.add((Class) ' + cls['fqn'] + '.class);\n'
    registry += '    return result;\n  }\n}\n'
    registry = registry[:-2] + '  public static boolean hasUsed(Class<?> type) { return ' + (' || '.join('type == ' + c['fqn'] + '.class' for c in classes if c['has_used']) or 'false') + '; }\n}\n'
    path = ROOT / 'browser-src/alefj2cl/TranslatorRegistry.java'
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(registry)
    text = (ROOT / 'upstream-src' / RUNTIME / 'Mapping.java').read_text()
    start = text.index('  public static Mapping of(')
    end = text.index('  public Signature signature()')
    text = text[:start] + '''  @FunctionalInterface
  public interface Invoker { Object invoke(Object[] arguments); }
  public static Mapping of(Class trClass, String name, Signature sgn, boolean withGuard, SNodeReference ptr, Invoker invoker) {
    return new Mapping(name, sgn, withGuard, ptr, invoker);
  }
  private final Signature signature;
  private final Invoker method;
  private final String name;
  private final boolean guarded;
  protected final SNodeReference srcPtr;
  private Mapping(String name, Signature signature, boolean guarded, SNodeReference ptr, Invoker method) {
    this.name = name; this.signature = signature; this.guarded = guarded; this.srcPtr = ptr; this.method = method;
  }
  public static <T extends TranslatorBase> Class<T> baseOf(Class<? extends T> c) {
    while (!Modifier.isAbstract(c.getSuperclass().getModifiers())) c = (Class<? extends T>) c.getSuperclass();
    return (Class<T>) c;
  }
''' + text[end:]
    text = body(text, 'public boolean hasGuard()', '    return guarded;')
    text = text.replace('method.invokeWithArguments(allArgs)', 'method.invoke(allArgs)')
    text = re.sub(r'^import java.lang.(?:invoke\.[\w]+|reflect.Method);\n', '', text, flags=re.M)
    save(RUNTIME + 'Mapping.java', text)
    text = (ROOT / 'upstream-src' / RUNTIME / 'MappingSet.java').read_text().replace('import java.lang.invoke.MethodHandles;\n', '')
    text = text.replace('        MethodHandles.Lookup lookup = MethodHandles.lookup();\n', '')
    text = text.replace('(List<Mapping>) lookup.findStaticGetter(extClass, TranslatorInstantiator.MAPPING_FIELD_NAME, List.class).invoke()', 'alefj2cl.TranslatorRegistry.mappings(extClass)')
    save(RUNTIME + 'MappingSet.java', text)
    text = (ROOT / 'upstream-src' / RUNTIME / 'TranslatorBase.java').read_text()
    text = re.sub(r'^import java.lang.invoke.[\w]+;\n', '', text, flags=re.M)
    text = text.replace('import jetbrains.mps.ide.httpsupport.runtime.base.HttpSupportUtil;\n', '')
    text = text.replace('HttpSupportUtil.getURL(mappingAsNode)', 'String.valueOf(mappingAsNode.getReference())')
    text = re.sub(r'      MethodHandle getter = .*?;\n      List<Class<TranslatorBase>> xtraExtends = .*?;',
                  '      List<Class<TranslatorBase>> xtraExtends = (List) alefj2cl.TranslatorRegistry.extendsOf(c);', text)
    save(RUNTIME + 'TranslatorBase.java', text)
    text = (ROOT / 'upstream-src' / RUNTIME / 'TranslatorInstantiator.java').read_text()
    text = re.sub(r'^import java.lang.reflect.(?:Constructor|Field);\n', '', text, flags=re.M)
    text = body(text, 'private <T extends TranslatorBase> T instantiate(Class<T>', '''    T instance = (T) translatorInstances.get(trClass);
    if (instance == null) {
      instance = (T) alefj2cl.TranslatorRegistry.create(trClass, scope, arguments);
      translatorInstances.put(trClass, instance);
    }
    return instance;''')
    start = text.index('    for (Field field :')
    end = end_at(code_only(text), text.index('{', start))
    text = text[:start] + '''    for (Class<TranslatorBase> dc : allTranslators(extensions)) {
      if (!alefj2cl.TranslatorRegistry.hasUsed(dc)) continue;
      TranslatorBase inst = instantiateWithExtensions(dc, base.extensionScope.getExtensionsFor(dc), arguments);
      for (TranslatorBase usedTr : alefj2cl.TranslatorRegistry.used(inst)) {
        translatorInstances.put(usedTr.getClass(), usedTr);
        base.addUsed(usedTr);
      }
    }''' + text[end + 1:]
    start = text.index('  private static List<Field> importedTranslators(')
    end = end_at(code_only(text), text.index('{', start))
    text = text[:start] + text[end + 1:]
    text = body(text, 'private static Iterable<Class<?>> getDeclaredTranslators(', '    return java.util.Arrays.asList(alefj2cl.TranslatorRegistry.nested(cls));')
    save(RUNTIME + 'TranslatorInstantiator.java', text)
    for filename in ('Mapping.java', 'TranslatorBase.java', 'TranslatorInstantiator.java', 'SimpleTranslatorScope.java', 'TranslatorExtensionsScope.java'):
        destination = ROOT / 'j2cl-src/alef' / RUNTIME / filename
        source = destination if filename not in ('SimpleTranslatorScope.java', 'TranslatorExtensionsScope.java') else ROOT / 'upstream-src' / RUNTIME / filename
        text = source.read_text()
        text = re.sub(r'Modifier\.isAbstract\(([^\n;]*?)\.getModifiers\(\)\)', r'!alefj2cl.TranslatorRegistry.isConcrete(\1)', text)
        text = text.replace('getClass().getPackageName()', 'getClass().getName().substring(0, getClass().getName().lastIndexOf(\'.\'))')
        text = text.replace('b.isEmpty()', '(b.length() == 0)')
        save(RUNTIME + filename, text)
    text = (ROOT / 'upstream-src' / RUNTIME / 'TranslatorException.java').read_text()
    text = text.replace('import java.lang.invoke.MethodHandle;\n', '').replace('MethodHandle.class.getName()', '"java.lang.invoke.MethodHandle"')
    save(RUNTIME + 'TranslatorException.java', text)
    text = (ROOT / 'upstream-src' / RUNTIME / 'MappingTypeImpl.java').read_text()
    text = text.replace('Modifier.isAbstract(clazz.getModifiers())', 'alefj2cl.ClassMetadata.isAbstract(clazz)')
    save(RUNTIME + 'MappingTypeImpl.java', text)
    path = ROOT / 'j2cl-src/alef' / RUNTIME / 'TranslatorExtensionsScope.java'
    text = path.read_text()
    text = text.replace('  private final Set<ReloadableModule> modules;', '  private final Set<ReloadableModule> modules;\n  private SModel browserModel;')
    text = body(text, 'public synchronized static TranslatorExtensionsScope forModel(', '''    TranslatorExtensionsScope scope = new TranslatorExtensionsScope(java.util.Collections.emptySet());
    scope.browserModel = m;
    return scope;''')
    text = body(text, 'public List<Class<TranslatorBase>> getExtensionsFor(', '''    Class<TranslatorBase> base = (Class) Mapping.baseOf(genericTrCls);
    List<Class<TranslatorBase>> result = new SimpleTranslatorScope().getExtensionsFor(genericTrCls);
    SRepository repository = browserModel == null ? MPSModuleRepository.getInstance() : browserModel.getRepository();
    LanguageRegistry registry = LanguageRegistry.getInstance(repository);
    Collection<SLanguage> used = browserModel == null ? registry.getAllLanguages() : new ModelDependencyResolver(registry, repository).usedLanguages(browserModel);
    Set<String> languages = new HashSet<>();
    for (SLanguage language : used) languages.add(language.getQualifiedName());
    for (Class<TranslatorBase> candidate : alefj2cl.TranslatorRegistry.extensions(languages)) {
      if (base.isAssignableFrom(candidate) && !result.contains(candidate)) result.add(candidate);
    }
    for (int i = 0; i < result.size(); i++) {
      Class parent = result.get(i).getSuperclass();
      if (parent != null && genericTrCls.isAssignableFrom(parent) && !result.contains(parent)) result.add(parent);
    }
    return TopologicalSort.sortDesc(result, c -> TranslatorBase.extendsOf(c));''')
    path.write_text(text)
    (ROOT / 'out/translator-bindings.json').write_text(json.dumps({'files': files, 'classes': [c['fqn'] for c in classes]}, indent=2) + '\n')
    print('Typed translator bindings:', len(classes), 'classes in', len(files), 'source files')


if __name__ == '__main__':
    main()
