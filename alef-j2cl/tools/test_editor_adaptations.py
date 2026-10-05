#!/usr/bin/env python3
"""Upgrade guards for editor bindings and compiled grammar menu adaptations."""
from pathlib import Path
import importlib.util
import json
import unittest
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]

def load(name, path):
    spec = importlib.util.spec_from_file_location(name, path)
    module = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(module)
    return module

shortcuts = load('shortcuts', ROOT.parent / 'mps-j2cl/tools/prepare_editor_shortcuts.py')
grammar = load('grammar', ROOT / 'tools/adapt_grammar_menus.py')

class EditorAdaptations(unittest.TestCase):
    def registration(self):
        return ET.parse(shortcuts.ACTIONS / 'MPSEditorActions.xml').getroot()

    def test_reject_duplicate_shortcut(self):
        root = self.registration()
        action = next(a for a in root.findall('./actions/action') if a.attrib['id'].endswith('CopyThisDown_Action'))
        next(k for k in action.findall('keyboard-shortcut') if k.attrib['keymap'] == '$default').set('first-keystroke', 'ctrl Y')
        with self.assertRaisesRegex(RuntimeError, 'Conflicting shortcut'):
            shortcuts.inventory(root)

    def test_reject_wrong_action_class(self):
        root = self.registration()
        action = next(a for a in root.findall('./actions/action') if a.attrib['id'].endswith('CopyThisDown_Action'))
        action.set('class', 'jetbrains.mps.ide.editor.actions.DeleteLine_Action')
        with self.assertRaisesRegex(RuntimeError, 'Unexpected action implementation'):
            shortcuts.inventory(root)

    def test_changed_binding_reaches_generated_dispatch(self):
        root = self.registration()
        action = next(a for a in root.findall('./actions/action') if a.attrib['id'].endswith('CopyThisDown_Action'))
        next(k for k in action.findall('keyboard-shortcut') if k.attrib['keymap'] == '$default').set('first-keystroke', 'ctrl shift D')
        generated = shortcuts.host_bindings(shortcuts.inventory(root))
        self.assertIn('"ctrl shift D", true, false, new CopyThisDown_Action()', generated)
        self.assertNotIn('"ctrl D"', generated)

    def test_upstream_handler_preserved(self):
        original = (shortcuts.ROOT / 'mps' / shortcuts.BASE / 'EditorComponent.java').read_text()
        adapted = shortcuts.adapt_editor(original)
        self.assertEqual(adapted.replace(shortcuts.DISPATCH, '', 1).replace(shortcuts.mapping(shortcuts.inventory()) + '\n', '', 1), original)
        self.assertEqual(shortcuts.adapt_editor(adapted), adapted)

    def test_reject_unexpected_handler_edit(self):
        original = (shortcuts.ROOT / 'mps' / shortcuts.BASE / 'EditorComponent.java').read_text()
        adapted = shortcuts.adapt_editor(original)
        changed = adapted.replace('    // Generated language keymaps', '    unexpectedCall();\n    // Generated language keymaps', 1)
        with self.assertRaisesRegex(RuntimeError, 'differs from upstream'):
            shortcuts.adapt_editor(changed)

    def test_reject_unrecognized_grammar_hook(self):
        with self.assertRaisesRegex(RuntimeError, 'Unrecognized generated grammar'):
            grammar.adapt('class Changed extends GrammarCellsTransformationMenuPart { public SModule getModule(SRepository repo) { return null; } }')

    def test_compiled_reference_retains_original_identity(self):
        inventory = json.loads((ROOT / 'out/source-inventory.json').read_text())
        menus = [ROOT / p for p in sorted(set(inventory['sources']) | set(inventory['overrides']))
                 if p.startswith('upstream-src/') and p.endswith('GrammarCellsSideTransformationsMenu.java')]
        self.assertGreater(len(menus), 0)
        for path in menus:
            original = path.read_text()
            with self.subTest(menu=path.name):
                result = grammar.adapt(original)
                for match in grammar.MODULE_GETTER.finditer(original):
                    self.assertIn('return ' + match[1] + ';', result)

if __name__ == '__main__':
    unittest.main()
