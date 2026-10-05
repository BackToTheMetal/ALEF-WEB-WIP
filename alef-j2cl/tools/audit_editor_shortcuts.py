#!/usr/bin/env python3
"""Audit the selected browser bridge against the original MPS Windows registrations."""
from pathlib import Path
import importlib.util
import json

ROOT = Path(__file__).resolve().parents[1]
MPS = ROOT.parent / 'mps-j2cl'
spec = importlib.util.spec_from_file_location('shortcuts', MPS / 'tools/prepare_editor_shortcuts.py')
shortcuts = importlib.util.module_from_spec(spec)
spec.loader.exec_module(shortcuts)
rows = shortcuts.inventory()
for path, expected in shortcuts.outputs().items():
    if path.read_text() != expected:
        raise SystemExit('Stale shared shortcut adaptation: ' + str(path))
component = ROOT / 'j2cl-src/mps' / shortcuts.BASE / 'EditorComponent.java'
if shortcuts.mapping(rows) not in component.read_text():
    raise SystemExit('ALEF is not using the current shared shortcut mappings')
host = (MPS / 'browser/src/mpsj2cl/editor/BrowserEditorShortcuts.java').read_text()
if 'BrowserEditorActionBindings.install(editor, project, input, actions)' not in host:
    raise SystemExit('Generated original-action bindings not installed')
# outputs() checks the exact shortcut -> action class mapping, not just key presence.
for build in (MPS / 'BUILD.bazel', ROOT / '.work/mps-j2cl/BUILD.bazel'):
    text = build.read_text()
    if 'srcs = EDITOR_ACTION_SOURCES + [' not in text or 'BrowserEditorActionBindings.java' not in text:
        raise SystemExit('Original action source set not selected: ' + str(build))
for name in ('BrowserEditorHost.java',):
    text = (MPS / 'browser/src/mpsj2cl/editor' / name).read_text()
    if 'BrowserEditorShortcuts.install(editor, project, input, actions)' not in text:
        raise SystemExit('Shared shortcut installer not connected')
inspector = (ROOT / 'example/BrowserInspector.java').read_text()
if 'host.installEditorShortcuts(editor)' not in inspector:
    raise SystemExit('Inspector does not use the shared keymap')

report = ROOT / 'out/editor-shortcuts/audit.json'
report.parent.mkdir(parents=True, exist_ok=True)
report.write_text(json.dumps({'status': 'passed', 'actions': len(rows),
    'wired': sum(row['kind'] != 'desktop' for row in rows),
    'desktop_only': sum(row['kind'] == 'desktop' for row in rows),
    'commands': rows}, indent=2) + '\n')
lines = [
    '# Windows MPS editor shortcuts', '',
    f"The shared MPS browser bridge wires {sum(row['kind'] != 'desktop' for row in rows)} of the {len(rows)} editor commands with Windows bindings in the pinned MPS action registration. The remaining {sum(row['kind'] == 'desktop' for row in rows)} require desktop workbench services. A bound action still depends on the selected cell, language, and edit permissions.", '',
    '## Source locations', '',
    '- Original registrations: `mps-j2cl/mps/workbench/mps-editor/source_gen/jetbrains/mps/ide/editor/actions/MPSEditorActions.xml`.',
    '- Original command implementations: Java files beside that registration; cell implementations are in `mps-j2cl/mps/editor/editor-runtime/source/jetbrains/mps/nodeEditor/EditorComponentActions.java`, `NodeEditorActions.java`, `cellActions/`, and `folding/`.',
    '- Shared browser dispatch: `mps-j2cl/mps-web/editor/editor-runtime/source/jetbrains/mps/nodeEditor/EditorComponent.java`; workbench actions use the original MPS implementations selected by `mps-j2cl/editor-action-sources.bzl`.',
    '- `BrowserEditorShortcuts.java` only supplies browser focus, read-only guards and action context. `BrowserEditorActionBindings.java` is generated from the original registration, including the action class for each shortcut. Original MPS actions own applicability, editing, selection, popup ordering and undo commands.',
    '- Reproducible adaptations and complete classification: `mps-j2cl/tools/prepare_editor_shortcuts.py` and `mps-j2cl/compatibility/editor-shortcuts.json`.',
    '- ALEF consumes the shared mappings through `tools/prepare.py`. Inspectors use the same installer. Backend history bindings preserve Ctrl+Y for Delete Line.', '',
    '## Important Windows behavior', '',
    '- Ctrl+Up / Ctrl+Down expand and shrink structural selection. Shift+Up / Shift+Down extend selection to sibling nodes. Chrome reserves Ctrl+W and Ctrl+Shift+W for closing tabs/windows; use the arrow bindings.',
    '- Ctrl+Y is MPS Delete Line. Redo is Ctrl+Shift+Z. Undo remains Ctrl+Z.',
    '- Browser history restores model edits. A recreated node may need to be selected again after undo/redo; full desktop caret and selection restoration is outside the current history implementation.',
    '- Ctrl+Left/Right and Ctrl+Shift+Left/Right move/select by word or adjacent cell.',
    '- Folding accepts the main keyboard Minus/Equals keys and the registered numeric-keypad variants. Recursive folding uses Ctrl+Alt+Minus/Equals. Browser zoom shortcuts are consumed while the editor handles folding.',
    '- Alt+Enter opens intentions. Tab / Shift+Tab traverse cells; Page Up/Down navigate pages; Shift+Home/End select to the line boundary; Ctrl+F1 shows the selected diagnostic; F2 / Shift+F2 navigate errors.',
    '- F3 / Shift+F3 navigate search matches when search is active, otherwise highlighted cells. Escape closes search/completion or clears selection.',
    '- Ctrl+C/X/V and Ctrl+Insert / Shift+Delete / Shift+Insert invoke MPS clipboard actions. Structured node transfer uses the application clipboard; OS clipboard import/export is a separate existing browser-host limitation.',
    '- Generated language keymaps and completion keep priority. Model-changing actions use MPS commands and respect read-only cells and backend write leases. AltGr is not treated as a Ctrl+Alt shortcut.', '',
    '## Complete registration audit', '',
    'This table verifies wiring against the original registration, not applicability to every possible language concept.', '',
    '| MPS action | Windows shortcuts | Browser connection |',
    '| --- | --- | --- |',
]
for row in rows:
    destination = row['command'] if row['kind'] == 'desktop' else ('MPS cell action `' + row['command'] + '`' if row['kind'] == 'cell' else 'Original MPS action `' + row['action'] + '_Action`')
    lines.append('| ' + row['action'] + ' | ' + ', '.join('`' + key + '`' for key in row['keys']) + ' | ' + destination + ' |')
lines += ['', '## Validation', '',
          '`python3 tools/audit_editor_shortcuts.py` checks the complete registration, exact shortcut-to-action mappings, original action source selection, selected ALEF overlay and inspector connection. `python3 tools/test_editor_adaptations.py` exercises rejection of registration conflicts and unexpected source changes. `tests/LanguageContracts.java` executes real generated editors with key events, including structural/word selection, single-node and range duplicate/move/delete, insertion, undo/redo, folding, search, focus isolation, inspector action context and read-only guards. `node tools/test_editor_shortcuts.mjs --optimized` checks native keyboard input against a disposable example server. Check counts are assertions across the language suite, not one test for every registered shortcut.', '',
          'The browser does not collect IntelliJ feature-usage telemetry. This optional service is the only new compatibility stub needed to compile the original actions. Six desktop workbench commands remain outside this profile.', '']
(ROOT / 'docs/windows-editor-shortcuts.md').write_text('\n'.join(lines))
print('Windows shortcut audit passed:', len(rows), 'actions;', sum(row['kind'] != 'desktop' for row in rows), 'wired')
