package mpsj2cl.editor;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import jetbrains.mps.editor.runtime.cells.ReadOnlyUtil;
import jetbrains.mps.ide.actions.MPSCommonDataKeys;
import jetbrains.mps.nodeEditor.EditorComponent;
import jetbrains.mps.project.MPSProject;

/** Focus-scoped browser transport for the original MPS workbench actions.
 * Language keymaps and completion process events first. Each original action
 * supplies its own applicability checks and model-access/undo policy.
 */
final class BrowserEditorShortcuts {
  static void install(EditorComponent editor, MPSProject project, InputMap input, ActionMap actions) {
    BrowserEditorActionBindings.install(editor, project, input, actions);
  }

  static void bind(EditorComponent editor, MPSProject project, InputMap input, ActionMap actions,
      String shortcut, boolean writes, boolean duringCompletion, AnAction... candidates) {
    Object key = new Object();
    input.put(KeyStroke.getKeyStroke(shortcut), key);
    actions.put(key, new AbstractAction(shortcut) {
      @Override public boolean isEnabled() {
        return !editor.isDisposed() && editor.isFocusOwner()
            && (duringCompletion || !editor.getNodeSubstituteChooser().isVisible());
      }

      @Override public void actionPerformed(ActionEvent ignored) {
        if (!isEnabled()) return;
        var access = editor.getEditorContext().getRepository().getModelAccess();
        if (writes && access.computeReadAction(() -> ReadOnlyUtil.isSelectionReadOnlyInEditor(editor))) return;
        DataContext context = access.computeReadAction(() -> {
          DataContext editorContext = DataManager.getInstance().getDataContext(editor);
          // Inspectors need the same project context as normal host-owned editors.
          return id -> CommonDataKeys.PROJECT.is(id) ? project.getProject()
              : MPSCommonDataKeys.MPS_PROJECT.is(id) ? project : editorContext.getData(id);
        });
        for (AnAction candidate : candidates) {
          AnActionEvent event = AnActionEvent.createFromAnAction(candidate, null, "BrowserEditor", context);
          candidate.update(event);
          if (!event.getPresentation().isEnabledAndVisible()) continue;
          // Do not hold a read lock or add an outer command: the original action
          // owns its access policy (including IntelligentNodeMover's command).
          candidate.actionPerformed(event);
          editor.repaintExternalComponent();
          return;
        }
        // Swing still consumes an inapplicable editor shortcut, keeping it away
        // from the browser's text input proxy and default page shortcuts.
      }
    });
  }
}
