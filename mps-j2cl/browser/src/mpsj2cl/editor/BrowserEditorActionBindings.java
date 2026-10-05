// Generated from MPSEditorActions.xml by tools/prepare_editor_shortcuts.py.
package mpsj2cl.editor;

import jetbrains.mps.ide.editor.actions.*;

final class BrowserEditorActionBindings {
  static void install(jetbrains.mps.nodeEditor.EditorComponent editor,
      jetbrains.mps.project.MPSProject project, javax.swing.InputMap input, javax.swing.ActionMap actions) {
    BrowserEditorShortcuts.bind(editor, project, input, actions, "ENTER", true, false, new Insert_Action());
    BrowserEditorShortcuts.bind(editor, project, input, actions, "ESCAPE", false, false, new Escape_Action());
    BrowserEditorShortcuts.bind(editor, project, input, actions, "INSERT", true, false, new InsertBefore_Action());
    BrowserEditorShortcuts.bind(editor, project, input, actions, "ctrl B", false, true, new GoToDeclarationInMenu_Action(), new GoToDeclaration_Action());
    BrowserEditorShortcuts.bind(editor, project, input, actions, "ctrl CLOSE_BRACKET", false, false, new ShowRegularEditor_Action());
    BrowserEditorShortcuts.bind(editor, project, input, actions, "ctrl D", true, false, new CopyThisDown_Action());
    BrowserEditorShortcuts.bind(editor, project, input, actions, "ctrl OPEN_BRACKET", false, false, new ShowReflectiveEditor_Action());
    BrowserEditorShortcuts.bind(editor, project, input, actions, "ctrl Y", true, false, new DeleteLine_Action());
    BrowserEditorShortcuts.bind(editor, project, input, actions, "ctrl alt ENTER", false, false, new JumpToContextAssistant_Action());
    BrowserEditorShortcuts.bind(editor, project, input, actions, "ctrl alt T", true, false, new ShowSurroundWithIntentions_Action());
    BrowserEditorShortcuts.bind(editor, project, input, actions, "ctrl shift CLOSE_BRACKET", false, false, new ShowRegularEditor_Action());
    BrowserEditorShortcuts.bind(editor, project, input, actions, "ctrl shift DOWN", true, false, new MoveElementsDown_Action());
    BrowserEditorShortcuts.bind(editor, project, input, actions, "ctrl shift ENTER", true, false, new InsertPlaceholder_Action());
    BrowserEditorShortcuts.bind(editor, project, input, actions, "ctrl shift OPEN_BRACKET", false, false, new ShowReflectiveEditorsForSubtree_Action());
    BrowserEditorShortcuts.bind(editor, project, input, actions, "ctrl shift UP", true, false, new MoveElementsUp_Action());
    BrowserEditorShortcuts.bind(editor, project, input, actions, "shift ENTER", true, false, new InsertBefore_Action());
  }
}
