#!/usr/bin/env python3
"""Include the parent MPS language aspects used by ALEF's runtime profile."""
from pathlib import Path
import json
import re

ROOT = Path(__file__).resolve().parents[1]
MPS = ROOT.parent / 'mps-j2cl/mps'
PROFILES = {
    'plugins/mps-testing/languages/baseLanguage/unitTest/source_gen/jetbrains/mps/baseLanguage/unitTest/Language.java': {
        'StructureAspectDescriptor', 'BehaviorAspectDescriptor',
    },
    'plugins/mps-core/languages/core.xml/source_gen/jetbrains/mps/core/xml/Language.java': {
        'StructureAspectDescriptor', 'BehaviorAspectDescriptor', 'ConstraintsAspectDescriptor',
    },
    # Core checks ask about these meta-concepts even when editing ordinary models.
    'languages/baseLanguage/closures/source_gen/jetbrains/mps/baseLanguage/closures/Language.java': {
        'StructureAspectDescriptor',
    },
    'languages/baseLanguage/collections/source_gen/jetbrains/mps/baseLanguage/collections/Language.java': {
        'StructureAspectDescriptor',
    },
    'languages/languageDesign/modelapi/source_gen/jetbrains/mps/lang/modelapi/Language.java': {
        'StructureAspectDescriptor',
    },
    'languages/baseLanguage/classifiers/source_gen/jetbrains/mps/baseLanguage/classifiers/Language.java': {
        'StructureAspectDescriptor',
    },
    'languages/languageDesign/smodel/source_gen/jetbrains/mps/lang/smodel/Language.java': {
        'StructureAspectDescriptor',
    },
    'languages/languageDesign/generator/languages/descriptor/source_gen/jetbrains/mps/lang/descriptor/Language.java': {
        'StructureAspectDescriptor',
    },
    'languages/languageDesign/sharedConcepts/source_gen/jetbrains/mps/lang/sharedConcepts/Language.java': {
        'StructureAspectDescriptor',
    },
    'languages/languageDesign/structure/source_gen/jetbrains/mps/lang/structure/Language.java': {
        'StructureAspectDescriptor', 'ConceptPresentationAspect', 'BehaviorAspectDescriptor',
    },
    'languages/util/core.text/source_gen/jetbrains/mps/lang/text/Language.java': {
        'StructureAspectDescriptor', 'ConceptPresentationAspect', 'BehaviorAspectDescriptor',
        'ConstraintsAspectDescriptor', 'EditorAspectDescriptor', 'IntentionAspectDescriptor',
        'IHelginsDescriptor', 'ActionAspectDescriptor',
    },
}


def main():
    roots = []
    for relative, aspects in PROFILES.items():
        source = (MPS / relative).read_text()
        removed = set()
        def aspect(match):
            if match[1] in aspects: return match[0]
            removed.add(match[1])
            removed.update(re.findall(r'new (\w+)\(', match[0]))
            return ''
        source = re.sub(r'    if \(aspectClass == (\w+)\.class\) \{\n.*?\n    \}\n', aspect, source, flags=re.S)
        for name in removed:
            source = re.sub(r'^import [\w.]+\.' + re.escape(name) + r';\n', '', source, flags=re.M)
            source = re.sub(r'^    extensions\.recordContribution\([^\n]*\b' + re.escape(name) + r'\.class\);\n', '', source, flags=re.M)
        target = ROOT / 'j2cl-src/mps' / relative
        target.parent.mkdir(parents=True, exist_ok=True)
        target.write_text(source)
        roots.append(re.search(r'^package ([\w.]+);', source, re.M)[1] + '.Language')
    (ROOT / 'out/mps-language-profiles.json').write_text(json.dumps({'roots': roots, 'profiles': {p: sorted(a) for p, a in PROFILES.items()}}, indent=2) + '\n')
    print('Prepared', len(PROFILES), 'MPS language profiles')


if __name__ == '__main__': main()
