#!/usr/bin/env python3
"""Keep compiled grammar menu identities without requiring desktop language modules."""
from pathlib import Path
import json
import re

ROOT = Path(__file__).resolve().parents[1]
MODULE_GETTER = re.compile(
    r'    public SModule getModule\(SRepository repository\) \{\n'
    r'      return (PersistenceFacade\.getInstance\(\)\.createModuleReference\("[^"\n]+"\))\.resolve\(repository\);\n'
    r'    \}'
)


def adapt(text):
    if 'extends GrammarCellsTransformationMenuPart' not in text:
        return text

    def replacement(match):
        return match[0] + '''

    // The browser deploys grammar descriptors, not desktop language modules.
    @Override
    public org.jetbrains.mps.openapi.module.SModuleReference getModuleReference(SRepository repository) {
      return ''' + match[1] + ''';
    }'''

    result, count = MODULE_GETTER.subn(replacement, text)
    if count != text.count('extends GrammarCellsTransformationMenuPart'):
        raise RuntimeError('Unrecognized generated grammar transformation menu')
    return result


def main():
    count = 0
    inventory = json.loads((ROOT / 'out/source-inventory.json').read_text())
    originals = set(inventory['sources']) | set(inventory['overrides'])
    for relative in sorted(originals):
        if not relative.startswith('upstream-src/') or not relative.endswith('GrammarCellsSideTransformationsMenu.java'):
            continue
        original = ROOT / relative
        text = original.read_text()
        result = adapt(text)
        if result == text:
            continue
        target = ROOT / 'j2cl-src/alef' / original.relative_to(ROOT / 'upstream-src')
        target.parent.mkdir(parents=True, exist_ok=True)
        target.write_text(result)
        count += 1
    if not count:
        raise RuntimeError('No generated grammar transformation menus found')
    print(f'Adapted {count} grammar transformation menus to compiled module references')


if __name__ == '__main__':
    main()
