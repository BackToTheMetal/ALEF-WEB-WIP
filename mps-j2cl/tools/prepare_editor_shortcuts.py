#!/usr/bin/env python3
"""Bridge the pinned MPS Windows editor keymap into the browser runtime.

Only reads generated Java/action registration XML, never serialized MPS models.
"""
from pathlib import Path
import argparse
import json
import re
import xml.etree.ElementTree as ET
from collections import defaultdict

ROOT = Path(__file__).resolve().parents[1]
BASE = 'editor/editor-runtime/source/jetbrains/mps/nodeEditor/'
ACTIONS = ROOT / 'mps/workbench/mps-editor/source_gen/jetbrains/mps/ide/editor/actions'
CUSTOM = {
    'Insert': 'insert', 'InsertBefore': 'insertBefore', 'InsertPlaceholder': 'insertPlaceholder',
    'CopyThisDown': 'duplicate', 'DeleteLine': 'deleteLine',
    'Escape': 'escape', 'MoveElementsUp': 'moveUp', 'MoveElementsDown': 'moveDown',
    'JumpToContextAssistant': 'contextAssistant', 'GoToDeclaration': 'declaration',
    'GoToDeclarationInMenu': 'declaration',
    'ShowReflectiveEditor': 'reflective', 'ShowReflectiveEditorsForSubtree': 'reflectiveSubtree',
    'ShowRegularEditor': 'regular', 'ShowSurroundWithIntentions': 'surround',
}
DESKTOP = {
    'AddLanguageImport': 'Desktop language-import dialog; compiled languages are installed by the browser host.',
    'AddModelImportByRoot': 'Desktop root/import chooser; browser model loading is handled by the application.',
    'AddModelImport': 'Desktop model-import dialog.',
    'ExtractComponent': 'Desktop refactoring infrastructure.',
    'ShowGenerationActions': 'Desktop generation action group.',
    'core.ShowNodeMessages': 'Desktop node-message tool window; Ctrl+F1 shows the selected diagnostic in the browser.',
}

WRITE_ACTIONS = {'Insert', 'InsertBefore', 'InsertPlaceholder', 'CopyThisDown', 'DeleteLine',
                 'MoveElementsUp', 'MoveElementsDown', 'ShowSurroundWithIntentions'}
HELPERS = ['DeleteLineAction', 'ReflectiveEditorAction', 'ReflectiveEditorActionUtil',
           'EditorActionUtils', 'InsertUtil', 'FindDeclarationUtils', 'GoToDeclarationHandlerRegistry']


def inventory(registration=None):
    rows = []
    root = ET.parse(ACTIONS / 'MPSEditorActions.xml').getroot() if registration is None else registration
    for action in root.findall('./actions/action'):
        name = action.attrib['id'].removeprefix('jetbrains.mps.ide.editor.actions.').removesuffix('_Action')
        keys = [k.attrib['first-keystroke'].strip() for k in action.findall('keyboard-shortcut') if k.attrib['keymap'] == '$default']
        if not keys:
            continue
        if name in CUSTOM:
            kind, command = 'host', CUSTOM[name]
        elif name in DESKTOP:
            kind, command = 'desktop', DESKTOP[name]
        else:
            source = (ACTIONS / (name + '_Action.java')).read_text()
            types = set(re.findall(r'CellActionType\.(\w+)', source))
            if len(types) != 1:
                raise RuntimeError('Unclassified MPS action: ' + name)
            kind, command = 'cell', types.pop()
        row = dict(action=name, keys=keys, kind=kind, command=command)
        if kind == 'host':
            expected_class = 'jetbrains.mps.ide.editor.actions.' + name + '_Action'
            if action.attrib['class'] != expected_class:
                raise RuntimeError('Unexpected action implementation: ' + name)
            row.update(implementation=expected_class, writes=name in WRITE_ACTIONS)
        rows.append(row)
    if len({r['action'] for r in rows}) != len(rows):
        raise RuntimeError('Duplicate action registration')
    owners = defaultdict(list)
    for row in rows:
        for key in row['keys']:
            owners[key].append(row['action'])
    for key, names in owners.items():
        if len(names) > 1 and sorted(names) != ['GoToDeclaration', 'GoToDeclarationInMenu']:
            raise RuntimeError('Conflicting shortcut: ' + key + ': ' + ', '.join(names))
    return rows

def mapping(rows):
    keys = {key: row['command'] for row in rows if row['kind'] == 'cell' for key in row['keys']}
    # These are registered directly by upstream EditorComponent or the IDE clipboard providers.
    keys.update({'shift HOME': 'SELECT_HOME', 'shift END': 'SELECT_END',
                 'PAGE_UP': 'PAGE_UP', 'PAGE_DOWN': 'PAGE_DOWN', 'TAB': 'NEXT', 'shift TAB': 'PREV',
                 'ctrl F1': 'SHOW_MESSAGE', 'ctrl C': 'COPY', 'ctrl X': 'CUT', 'ctrl V': 'PASTE',
                 'ctrl INSERT': 'COPY', 'shift DELETE': 'CUT', 'shift INSERT': 'PASTE'})
    lines = ['  private static CellActionType browserKeyAction(KeyEvent event) {',
             '    if (event.getID() != KeyEvent.KEY_PRESSED || event.isAltGraphDown()) return null;',
             '    int modifiers = event.getModifiersEx() & (KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK',
             '        | KeyEvent.ALT_DOWN_MASK | KeyEvent.META_DOWN_MASK);',
             '    // Keep the existing Command aliases without accepting Ctrl+AltGr as a shortcut.',
             '    if ((modifiers & KeyEvent.META_DOWN_MASK) != 0) {',
             '      if ((modifiers & KeyEvent.CTRL_DOWN_MASK) != 0) return null;',
             '      modifiers = (modifiers & ~KeyEvent.META_DOWN_MASK) | KeyEvent.CTRL_DOWN_MASK;',
             '    }']
    for key, command in sorted(keys.items()):
        parts = key.split()
        mask = ' | '.join('KeyEvent.' + m.upper() + '_DOWN_MASK' for m in parts[:-1]) or '0'
        lines.append(f'    if (event.getKeyCode() == KeyEvent.VK_{parts[-1]} && modifiers == ({mask})) return CellActionType.{command};')
    lines += ['    return null;', '  }', '', '  private static boolean browserActionWrites(CellActionType action) {', '    switch (action) {',
              '      case BACKSPACE: case DELETE: case DELETE_TO_WORD_END:',
              '      case CUT: case PASTE: case PASTE_BEFORE: case PASTE_AFTER:',
              '      case INSERT: case INSERT_BEFORE: case INSERT_PLACEHOLDER: case INSERT_PLACEHOLDER_BEFORE:',
              '      case COMMENT: case RIGHT_TRANSFORM: case LEFT_TRANSFORM:',
              '      case COMPLETE: case COMPLETE_SMART:',
              '        return true;', '      default: return false;', '    }', '  }', '']
    return '\n'.join(lines)

DISPATCH = '''    // Generated language keymaps and active completion handlers retain priority.
    if (!keyEvent.isConsumed() && !myNodeSubstituteChooser.isVisible()) {
      CellActionType action = browserKeyAction(keyEvent);
      if (action != null) {
        Selection selection = getModelAccess().computeReadAction(() -> {
          Selection current = getSelectionManager().getSelection();
          return current != null && (!browserActionWrites(action) || !ReadOnlyUtil.isSelectionReadOnlyInEditor(this))
              && current.canExecuteAction(action) ? current : null;
        });
        // Release read access before an action starts an undoable command.
        if (selection != null) selection.executeAction(action);
        else if (action == CellActionType.FIND_NEXT) goToNextHighlightedCell(false);
        else if (action == CellActionType.FIND_PREV) goToNextHighlightedCell(true);
        // Even unavailable commands must not edit the DOM input proxy or scroll the page.
        keyEvent.consume();
      }
    }

'''

def adapt_editor(text):
    """Insert a checked fallback without reconstructing/discarding upstream code."""
    original = (ROOT / 'mps' / BASE / 'EditorComponent.java').read_text()
    start_marker = '  public void processKeyPressed(final KeyEvent keyEvent)'
    end_marker = '  public void processKeyReleased(final KeyEvent keyEvent)'
    original_method = original[original.index(start_marker):original.index(end_marker)]
    tail = '    repaintExternalComponent();\n  }\n\n'
    if not original_method.endswith(tail) or original_method.count(tail) != 1:
        raise RuntimeError('Upstream key handler changed; review fallback insertion')
    adapted = original_method[:-len(tail)] + DISPATCH + tail + mapping(inventory()) + '\n'
    start = text.index('  public void processKeyPressed(final KeyEvent keyEvent)')
    end = text.index('  public void processKeyReleased(final KeyEvent keyEvent)', start)
    if text[start:end] not in (original_method, adapted):
        raise RuntimeError('Editor key handler differs from upstream and the generated fallback')
    return text[:start] + adapted + text[end:]


def host_bindings(rows):
    groups = defaultdict(list)
    for row in rows:
        if row['kind'] == 'host':
            for key in row['keys']:
                groups[key].append(row)
    lines = ['// Generated from MPSEditorActions.xml by tools/prepare_editor_shortcuts.py.',
             'package mpsj2cl.editor;', '',
             'import jetbrains.mps.ide.editor.actions.*;', '',
             'final class BrowserEditorActionBindings {',
             '  static void install(jetbrains.mps.nodeEditor.EditorComponent editor,',
             '      jetbrains.mps.project.MPSProject project, javax.swing.InputMap input, javax.swing.ActionMap actions) {']
    for key, candidates in sorted(groups.items()):
        # Ctrl+B first tries the completion menu target, then the selected cell.
        candidates.sort(key=lambda r: (r['action'] != 'GoToDeclarationInMenu', r['action']))
        writes = str(any(r['writes'] for r in candidates)).lower()
        completion = str(any(r['action'] == 'GoToDeclarationInMenu' for r in candidates)).lower()
        constructors = ', '.join('new ' + r['action'] + '_Action()' for r in candidates)
        lines.append(f'    BrowserEditorShortcuts.bind(editor, project, input, actions, "{key}", {writes}, {completion}, {constructors});')
    return '\n'.join(lines + ['  }', '}', ''])


def original_sources(rows):
    names = HELPERS + [row['action'] + '_Action' for row in rows if row['kind'] == 'host']
    sources = [(ACTIONS / (name + '.java')).relative_to(ROOT).as_posix() for name in names]
    sources.append('mps/workbench/mps-editor/source/jetbrains/mps/ide/editor/EditorActionAccess.java')
    for path in sources:
        if not (ROOT / path).is_file():
            raise RuntimeError('Missing original action source: ' + path)
    return sorted(sources)

def outputs():
    rows = inventory()
    editor = ROOT / 'mps-web' / BASE / 'EditorComponent.java'
    result = {editor: adapt_editor(editor.read_text())}
    relative = BASE + 'EditorComponentKeyboardHandler.java'
    text = (ROOT / 'mps' / relative).read_text()
    old = '''    // special case - don't allow kbd focus to leave editor area
    if (keyEvent.getKeyCode() == KeyEvent.VK_UP && keyEvent.isControlDown() && !keyEvent.isAltDown() && !keyEvent.isShiftDown()) {
      keyEvent.consume();
    }
'''
    if text.count(old) != 1:
        raise RuntimeError('Upstream Ctrl+Up workaround changed')
    text = text.replace(old, '    // The browser fallback receives Ctrl+Up for structural selection.\n')
    result[ROOT / 'mps-web' / relative] = text
    result[ROOT / 'browser/src/mpsj2cl/editor/BrowserEditorActionBindings.java'] = host_bindings(rows)
    result[ROOT / 'editor-action-sources.bzl'] = ('# Generated by tools/prepare_editor_shortcuts.py; original MPS implementations.\n'
        + 'EDITOR_ACTION_SOURCES = ' + json.dumps(original_sources(rows), indent=4) + '\n')
    result[ROOT / 'compatibility/editor-shortcuts.json'] = json.dumps(rows, indent=2) + '\n'
    return result

def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--check', action='store_true')
    args = parser.parse_args()
    for path, text in outputs().items():
        if args.check:
            if path.read_text() != text:
                raise SystemExit('Stale shortcut adaptation: ' + str(path))
        else:
            path.parent.mkdir(parents=True, exist_ok=True)
            path.write_text(text, newline='\n')
    print('Classified every Windows shortcut in MPSEditorActions.xml:', len(inventory()), 'actions')

if __name__ == '__main__': main()
