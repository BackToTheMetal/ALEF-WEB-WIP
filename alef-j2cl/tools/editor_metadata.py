#!/usr/bin/env python3
"""Materialize PuntConcept's editor-model query for the compiled language profile."""
import json
from pathlib import Path
import re
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
PUNT_MODEL = 'r:60e358c3-3293-420e-bd4f-559af2524137'
PUNT_NODE = '7AED00rb4DZ'


def terminal_punctuation():
    languages = json.loads((ROOT / 'out/source-inventory.json').read_text())['languages']
    result = set()
    for path in sorted((ROOT / 'alef').glob('languages/**/editor.mps')):
        model = ET.parse(path).getroot()
        namespace = model.get('ref').split('(')[-1].removesuffix(')')
        if namespace.removesuffix('.editor') not in languages:
            continue
        concepts = {c.get('index'): c.get('name') for c in model.findall('registry/language/concept')}
        roles = {r.get('index'): r.get('name') for c in model.findall('registry/language/concept') for r in c}
        imports = {i.get('index'): i.get('ref') for i in model.findall('imports/import')}
        parents = {child: parent for parent in model.iter('node') for child in parent.findall('node')}
        for editor in model.findall('node'):
            if concepts[editor.get('concept')] != 'jetbrains.mps.lang.editor.structure.ConceptEditorDeclaration':
                continue
            declaration = next((r for r in editor.findall('ref') if roles[r.get('role')] == 'conceptDeclaration'), None)
            if declaration is None:
                continue
            for cell in editor.iter('node'):
                if concepts[cell.get('concept')] != 'jetbrains.mps.lang.editor.structure.CellModel_Component':
                    continue
                component = next((r for r in cell.findall('ref') if roles[r.get('role')] == 'editorComponent'), None)
                if component is None:
                    continue
                if component.get('node'):
                    target_model, target_node = model.get('ref'), component.get('node')
                else:
                    imported, target_node = component.get('to').split(':', 1)
                    target_model = imports[imported]
                if not target_model.startswith(PUNT_MODEL + '(') or target_node != PUNT_NODE:
                    continue
                # Match upstream isLast: no following sibling at any cell-model level.
                current = cell
                while current in parents and parents[current].findall('node')[-1] is current:
                    current = parents[current]
                    if current is editor:
                        imported = declaration.get('to').split(':', 1)[0]
                        structure = re.search(r'\(([^)]+)\)', imports[imported])[1]
                        result.add(structure + '.' + declaration.get('resolve'))
                        break
    if not result:
        raise RuntimeError('No terminal Punt editor components found')
    return sorted(result)


def main():
    from adapt_translators import save
    names = terminal_punctuation()
    cases = '\n'.join('      case "' + name + '":' for name in names)
    save('languages/regelspraak/source_gen/regelspraak/editor/PuntConcept.java', '''package regelspraak.editor;

/* Browser copy: generated from the original editor models by tools/editor_metadata.py. */
import org.jetbrains.mps.openapi.language.SAbstractConcept;

public class PuntConcept {
  public static boolean is(SAbstractConcept concept) {
    switch (concept.getQualifiedName()) {
''' + cases + '''
        return true;
      default:
        return false;
    }
  }
}
''')
    (ROOT / 'out/editor-metadata.json').write_text(json.dumps({'terminal_punctuation': names}, indent=2) + '\n')
    print('Terminal punctuation metadata:', len(names), 'concepts')


if __name__ == '__main__':
    main()
