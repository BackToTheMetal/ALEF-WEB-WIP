package alefj2cl.example;

import jetbrains.mps.nodeEditor.NodeEditorComponent;
import jetbrains.mps.openapi.editor.*;
import jetbrains.mps.openapi.navigation.NavigationSupport;
import jetbrains.mps.project.Project;
import org.jetbrains.mps.openapi.model.*;
import org.jetbrains.mps.openapi.module.SModule;

/** Connect generated MPS debug actions to the browser's existing editor. */
final class BrowserNavigationSupport extends NavigationSupport {
  interface OpenNode {
    NodeEditorComponent open(SNode node, boolean focus, boolean select);
  }
  private final OpenNode open;
  BrowserNavigationSupport(OpenNode open) { this.open = open; }

  @Override public Editor openNode(Project project, SNode node, boolean focus, boolean select) {
    NodeEditorComponent component = open.open(node, focus, select);
    return component == null ? null : new Handle(component);
  }
  @Override public void selectInTree(Project project, SNode node, boolean focus) {
    open.open(node, false, true);
  }
  @Override public void selectInTree(Project project, SModel model, boolean focus) {
    throw new UnsupportedOperationException("Browser navigation requires a root or child node");
  }
  @Override public void selectInTree(Project project, SModule module, boolean focus) {
    throw new UnsupportedOperationException("Browser navigation requires a root or child node");
  }

  // A borrowed handle: the application owns and caches the actual component.
  private final class Handle implements Editor {
    private NodeEditorComponent component;
    Handle(NodeEditorComponent component) { this.component = component; }
    @Override public EditorComponent getCurrentEditorComponent() { return component; }
    @Override public SNodeReference getCurrentlyEditedNode() { return component.getEditedNode().getReference(); }
    @Override public EditorContext getEditorContext() { return component.getEditorContext(); }
    @Override public boolean isTabbed() { return false; }
    @Override public void showNode(SNode node, boolean select) {
      NodeEditorComponent next = open.open(node, true, select);
      if (next != null) component = next;
    }
    @Override public void dispose() { /* Component lifetime belongs to the workspace. */ }
    @Override public EditorState saveState() { return new State(component.captureState()); }
    @Override public void loadState(EditorState state) {
      if (!(state instanceof State)) throw new IllegalArgumentException("Not a browser editor state");
      component.restoreState(((State) state).value);
    }
    @Override public void selectNotify() { component.relayout(); }
    @Override public void deselectNotify() { /* No tab or platform file-editor lifecycle. */ }
  }
  private static final class State implements EditorState {
    final EditorComponentState value;
    State(EditorComponentState value) { this.value = value; }
    @Override public void clearSessionState() { value.clearSessionState(); }
    @Override public void save(org.jdom.Element element) {
      throw new UnsupportedOperationException("Browser editor state is session-only");
    }
    @Override public void load(org.jdom.Element element) {
      throw new UnsupportedOperationException("Browser editor state is session-only");
    }
  }
}
