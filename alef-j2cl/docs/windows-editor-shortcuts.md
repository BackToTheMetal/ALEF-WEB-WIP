# Windows MPS editor shortcuts

The shared MPS browser bridge wires 50 of the 56 editor commands with Windows bindings in the pinned MPS action registration. The remaining 6 require desktop workbench services. A bound action still depends on the selected cell, language, and edit permissions.

## Source locations

- Original registrations: `mps-j2cl/mps/workbench/mps-editor/source_gen/jetbrains/mps/ide/editor/actions/MPSEditorActions.xml`.
- Original command implementations: Java files beside that registration; cell implementations are in `mps-j2cl/mps/editor/editor-runtime/source/jetbrains/mps/nodeEditor/EditorComponentActions.java`, `NodeEditorActions.java`, `cellActions/`, and `folding/`.
- Shared browser dispatch: `mps-j2cl/mps-web/editor/editor-runtime/source/jetbrains/mps/nodeEditor/EditorComponent.java`; workbench actions use the original MPS implementations selected by `mps-j2cl/editor-action-sources.bzl`.
- `BrowserEditorShortcuts.java` only supplies browser focus, read-only guards and action context. `BrowserEditorActionBindings.java` is generated from the original registration, including the action class for each shortcut. Original MPS actions own applicability, editing, selection, popup ordering and undo commands.
- Reproducible adaptations and complete classification: `mps-j2cl/tools/prepare_editor_shortcuts.py` and `mps-j2cl/compatibility/editor-shortcuts.json`.
- ALEF consumes the shared mappings through `tools/prepare.py`. Inspectors use the same installer. Backend history bindings preserve Ctrl+Y for Delete Line.

## Important Windows behavior

- Ctrl+Up / Ctrl+Down expand and shrink structural selection. Shift+Up / Shift+Down extend selection to sibling nodes. Chrome reserves Ctrl+W and Ctrl+Shift+W for closing tabs/windows; use the arrow bindings.
- Ctrl+Y is MPS Delete Line. Redo is Ctrl+Shift+Z. Undo remains Ctrl+Z.
- Browser history restores model edits. A recreated node may need to be selected again after undo/redo; full desktop caret and selection restoration is outside the current history implementation.
- Ctrl+Left/Right and Ctrl+Shift+Left/Right move/select by word or adjacent cell.
- Folding accepts the main keyboard Minus/Equals keys and the registered numeric-keypad variants. Recursive folding uses Ctrl+Alt+Minus/Equals. Browser zoom shortcuts are consumed while the editor handles folding.
- Alt+Enter opens intentions. Tab / Shift+Tab traverse cells; Page Up/Down navigate pages; Shift+Home/End select to the line boundary; Ctrl+F1 shows the selected diagnostic; F2 / Shift+F2 navigate errors.
- F3 / Shift+F3 navigate search matches when search is active, otherwise highlighted cells. Escape closes search/completion or clears selection.
- Ctrl+C/X/V and Ctrl+Insert / Shift+Delete / Shift+Insert invoke MPS clipboard actions. Structured node transfer uses the application clipboard; OS clipboard import/export is a separate existing browser-host limitation.
- Generated language keymaps and completion keep priority. Model-changing actions use MPS commands and respect read-only cells and backend write leases. AltGr is not treated as a Ctrl+Alt shortcut.

## Complete registration audit

This table verifies wiring against the original registration, not applicability to every possible language concept.

| MPS action | Windows shortcuts | Browser connection |
| --- | --- | --- |
| AddLanguageImport | `ctrl L` | Desktop language-import dialog; compiled languages are installed by the browser host. |
| AddModelImportByRoot | `ctrl R` | Desktop root/import chooser; browser model loading is handled by the application. |
| AddModelImport | `ctrl M` | Desktop model-import dialog. |
| Backspace | `BACK_SPACE` | MPS cell action `BACKSPACE` |
| CollapseAll | `ctrl shift SUBTRACT`, `ctrl shift MINUS` | MPS cell action `FOLD_ALL` |
| CollapseRecursively | `ctrl alt MINUS` | MPS cell action `FOLD_RECURSIVELY` |
| Collapse | `ctrl SUBTRACT`, `ctrl MINUS` | MPS cell action `FOLD` |
| Comment | `ctrl SLASH` | MPS cell action `COMMENT` |
| CompleteSmart | `ctrl shift SPACE` | MPS cell action `COMPLETE_SMART` |
| Complete | `ctrl SPACE` | MPS cell action `COMPLETE` |
| CopyThisDown | `ctrl D` | Original MPS action `CopyThisDown_Action` |
| DeleteLine | `ctrl Y` | Original MPS action `DeleteLine_Action` |
| DeleteToWordEnd | `ctrl DELETE` | MPS cell action `DELETE_TO_WORD_END` |
| Delete | `DELETE` | MPS cell action `DELETE` |
| End | `END` | MPS cell action `END` |
| Escape | `ESCAPE` | Original MPS action `Escape_Action` |
| ExpandAll | `ctrl shift ADD`, `ctrl shift EQUALS` | MPS cell action `UNFOLD_ALL` |
| ExpandRecursively | `ctrl alt EQUALS` | MPS cell action `UNFOLD_RECURSIVELY` |
| Expand | `ctrl ADD`, `ctrl EQUALS` | MPS cell action `UNFOLD` |
| ExtractComponent | `ctrl alt C` | Desktop refactoring infrastructure. |
| FindNext | `F3` | MPS cell action `FIND_NEXT` |
| FindPrevious | `shift F3` | MPS cell action `FIND_PREV` |
| Find | `ctrl F` | MPS cell action `FIND` |
| FoldSelection | `ctrl PERIOD` | MPS cell action `TOGGLE_FOLDING` |
| GoToDeclarationInMenu | `ctrl B` | Original MPS action `GoToDeclarationInMenu_Action` |
| GoToDeclaration | `ctrl B` | Original MPS action `GoToDeclaration_Action` |
| Home | `HOME` | MPS cell action `HOME` |
| InsertBefore | `INSERT`, `shift ENTER` | Original MPS action `InsertBefore_Action` |
| InsertPlaceholder | `ctrl shift ENTER` | Original MPS action `InsertPlaceholder_Action` |
| Insert | `ENTER` | Original MPS action `Insert_Action` |
| JumpToContextAssistant | `ctrl alt ENTER` | Original MPS action `JumpToContextAssistant_Action` |
| LocalEnd | `ctrl RIGHT` | MPS cell action `LOCAL_END` |
| LocalHome | `ctrl LEFT` | MPS cell action `LOCAL_HOME` |
| MoveDown | `DOWN` | MPS cell action `DOWN` |
| MoveElementsDown | `ctrl shift DOWN` | Original MPS action `MoveElementsDown_Action` |
| MoveElementsUp | `ctrl shift UP` | Original MPS action `MoveElementsUp_Action` |
| MoveLeft | `LEFT` | MPS cell action `LEFT` |
| MoveRight | `RIGHT` | MPS cell action `RIGHT` |
| MoveUp | `UP` | MPS cell action `UP` |
| RootEnd | `ctrl END` | MPS cell action `ROOT_END` |
| RootHome | `ctrl HOME` | MPS cell action `ROOT_HOME` |
| SelectAll | `ctrl A` | MPS cell action `SELECT_ALL` |
| SelectDown | `ctrl DOWN`, `ctrl shift W` | MPS cell action `SELECT_DOWN` |
| SelectLeft | `shift LEFT` | MPS cell action `SELECT_LEFT` |
| SelectLocalEnd | `ctrl shift RIGHT` | MPS cell action `SELECT_LOCAL_END` |
| SelectLocalHome | `ctrl shift LEFT` | MPS cell action `SELECT_LOCAL_HOME` |
| SelectNext | `shift DOWN` | MPS cell action `SELECT_NEXT` |
| SelectPrevious | `shift UP` | MPS cell action `SELECT_PREVIOUS` |
| SelectRight | `shift RIGHT` | MPS cell action `SELECT_RIGHT` |
| SelectUp | `ctrl UP`, `ctrl W` | MPS cell action `SELECT_UP` |
| ShowGenerationActions | `alt INSERT` | Desktop generation action group. |
| core.ShowNodeMessages | `alt shift M` | Desktop node-message tool window; Ctrl+F1 shows the selected diagnostic in the browser. |
| ShowReflectiveEditor | `ctrl OPEN_BRACKET` | Original MPS action `ShowReflectiveEditor_Action` |
| ShowReflectiveEditorsForSubtree | `ctrl shift OPEN_BRACKET` | Original MPS action `ShowReflectiveEditorsForSubtree_Action` |
| ShowRegularEditor | `ctrl CLOSE_BRACKET`, `ctrl shift CLOSE_BRACKET` | Original MPS action `ShowRegularEditor_Action` |
| ShowSurroundWithIntentions | `ctrl alt T` | Original MPS action `ShowSurroundWithIntentions_Action` |

## Validation

`python3 tools/audit_editor_shortcuts.py` checks the complete registration, exact shortcut-to-action mappings, original action source selection, selected ALEF overlay and inspector connection. `python3 tools/test_editor_adaptations.py` exercises rejection of registration conflicts and unexpected source changes. `tests/LanguageContracts.java` executes real generated editors with key events, including structural/word selection, single-node and range duplicate/move/delete, insertion, undo/redo, folding, search, focus isolation, inspector action context and read-only guards. `node tools/test_editor_shortcuts.mjs --optimized` checks native keyboard input against a disposable example server. Check counts are assertions across the language suite, not one test for every registered shortcut.

The browser does not collect IntelliJ feature-usage telemetry. This optional service is the only new compatibility stub needed to compile the original actions. Six desktop workbench commands remain outside this profile.
