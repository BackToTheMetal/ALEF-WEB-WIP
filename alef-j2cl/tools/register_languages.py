#!/usr/bin/env python3
"""Generate explicit browser language and quick-fix registration."""
import json
from pathlib import Path
import re
from adapt_helpers import remove_method

ROOT = Path(__file__).resolve().parents[1]
MPS = ROOT.parent / 'mps-j2cl'


def main():
    dest = ROOT / 'j2cl-src/mps/browser/financial'
    dest.mkdir(parents=True, exist_ok=True)
    for name in ('FinancialClassLoader.java', 'RuntimeTypesLanguage.java', 'FinancialLanguages.java'):
        source = (MPS / 'browser/financial' / name).read_text()
        if name == 'FinancialLanguages.java':
            source = source.replace('    register5(platform);\n', '')
            source = remove_method(source, 'private void register5(')
            start = source.index('    jetbrains.mps.lang.typesystem.runtime.IHelginsDescriptor aspect5')
            end = source.index('\n  }', start)
            source = source[:start] + source[end:]
        (dest / name).write_text(source)
    report = json.loads((ROOT / 'out/source-inventory.json').read_text())
    mps = json.loads((ROOT / 'out/mps-inventory.json').read_text())
    profiles = json.loads((ROOT / 'out/mps-language-profiles.json').read_text())
    language_names = [name.removesuffix('.Language') for name in profiles['roots']] + report['languages']
    by_name, texts = {}, {}
    for path in report['sources'] + mps['sources']:
        text = (ROOT / path).read_text()
        match = re.search(r'^package ([\w.]+);', text, re.M)
        if match:
            name = match[1] + '.' + Path(path).stem
            by_name[name], texts[name] = path, text
    languages = []
    def visit(lang):
        if lang in languages: return
        block = texts[lang + '.Language'].split('protected void fillExtendedLanguages', 1)[-1].split('\n  }', 1)[0]
        for parent in re.findall(r'MetaAdapterFactory.getLanguage\([^\n]*"([\w.]+)"', block):
            if parent in language_names: visit(parent)
        languages.append(lang)
    for lang in language_names: visit(lang)
    source = '''package alefj2cl;
import java.util.*;
import jetbrains.mps.classloading.*;
import jetbrains.mps.core.platform.BrowserPlatform;
import jetbrains.mps.smodel.language.LanguageRuntime;
import jetbrains.mps.lang.typesystem.runtime.IHelginsDescriptor;
import jetbrains.mps.lang.typesystem.runtime.RuleSet;
import jetbrains.mps.smodel.runtime.IconResource;
/** Explicit registration of the compiled ALEF language closure. */
public final class AlefLanguages implements AutoCloseable {
  private final mpsj2cl.financial.FinancialLanguages base;
  private final List<ModuleClassLoader> loaders = new ArrayList<>();
  private final Set<Class<?>> rules = new HashSet<>();
  private final Set<Class<?>> iconProviders = new HashSet<>();
  public AlefLanguages(BrowserPlatform platform) {
    base = new mpsj2cl.financial.FinancialLanguages(platform);
    platform.registerLanguage(new com.mbeddr.mpsutil.grammarcells.runtimelang.Language());
'''
    for name, text in sorted(texts.items()):
        origin = re.search(r'@ResourceModule\("([^"\n]+)"\)', text)
        if origin and 'new IconResource(' in text:
            source += ('    IconResource.registerProvider(' + name + '.class, "' + origin[1] + '", "' + name.rsplit('.', 1)[0] + '");\n'
                       '    iconProviders.add(' + name + '.class);\n')
    for i, lang in enumerate(languages): source += '    register' + str(i) + '(platform);\n'
    source += '    base.verifyTypesystems(platform);\n  }\n'
    for i, lang in enumerate(languages):
        source += '  private void register' + str(i) + '(BrowserPlatform platform) {\n    LanguageRuntime language = new ' + lang + '.Language();\n'
        source += '    ModuleClassLoader loader = FinancialClassLoader.create(language.getIdentity().getSourceModuleReference());\n    loaders.add(loader);\n'
        rule_classes = []
        for name in sorted(by_name):
            if not name.startswith(lang + '.typesystem.'): continue
            simple = name.rsplit('.', 1)[1]
            text = texts[name]
            if re.search(r'public (?:final )?class ' + simple + r'\b', text):
                rule_classes.append(name)
            if name.endswith('_QuickFix') and ('public ' + simple + '()') in text:
                source += '    loader.registerFactory("' + name + '", ' + name + '.class, new Class<?>[0], args -> new ' + name + '());\n'
        source += '    platform.registerLanguage(language, loader, Collections.emptyList(), Collections.emptyList(), new Class<?>[]{\n'
        source += ',\n'.join('      ' + name + '.class' for name in rule_classes) + '\n    });\n'
        source += '''    IHelginsDescriptor aspect = language.getAspect(IHelginsDescriptor.class);
    if (aspect != null) {
      bind(aspect.getInferenceRules(), language.getNamespace());
      bind(aspect.getNonTypesystemRules(), language.getNamespace());
      bind(aspect.getSubtypingRules(), language.getNamespace());
      bind(aspect.getSubstituteTypeRules(), language.getNamespace());
      bind(aspect.getComparisonRules(), language.getNamespace());
      bind(aspect.getEliminationRules(), language.getNamespace());
      bind(aspect.getVariableConverters(), language.getNamespace());
      bind(aspect.getOverloadedOperationsTypesProviders(), language.getNamespace());
    }
'''
        descriptor = lang + '.editor.GrammarActionsDescriptor'
        for name in by_name:
            if name.startswith(lang + '.editor.') and 'extends AbstractGrammarActionDescriptor' in texts[name]:
                source += '    com.mbeddr.mpsutil.grammarcells.runtime.DescriptorCache.getInstance().register(language.getIdentity().getSourceModuleReference(), new ' + name + '());\n'
        source += '  }\n'
    source += '''  private void bind(Iterable<?> values, String namespace) {
    for (Object rule : values) { RuleSet.registerRuleNamespace(rule.getClass(), namespace); rules.add(rule.getClass()); }
  }
  public void close() {
    iconProviders.forEach(IconResource::unregisterProvider); iconProviders.clear();
    rules.forEach(RuleSet::unregisterRuleNamespace); rules.clear();
    for (ModuleClassLoader loader : loaders) loader.dispose(); loaders.clear();
    base.close();
  }
}
'''
    (ROOT / 'browser-src/alefj2cl/AlefLanguages.java').write_text(source)
    print('Registered ALEF languages:', ', '.join(languages))


if __name__ == '__main__': main()
