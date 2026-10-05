package alefj2cl.example;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.UiDataProvider;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.*;
import jetbrains.mps.ide.actions.MPSCommonDataKeys;
import jetbrains.mps.nodeEditor.InspectorTool;
import jetbrains.mps.nodeEditor.inspector.InspectorEditorComponent;
import mpsj2cl.editor.BrowserEditorHost;
import org.jetbrains.mps.openapi.model.SNode;

/** A real MPS inspector sharing the example's repository, commands and language editors. */
final class BrowserInspector implements AutoCloseable {
  private final BrowserEditorHost host;
  private final InspectorEditorComponent editor;
  private final JFrame window;
  private boolean visible = true;
  private final InspectorTool.InspectorAccessor accessor;
  private final Runnable releaseShortcuts;

  BrowserInspector(BrowserEditorHost host, int width) {
    this.host = host;
    editor = new InspectorEditorComponent(host.getProject().getRepository());
    ExampleTheme.style(editor);
    editor.setFocusable(true);
    DataManager.registerDataProvider(editor.getExternalComponent(), (UiDataProvider) sink -> {
      sink.set(CommonDataKeys.PROJECT, host.getIdeaProject());
      sink.set(MPSCommonDataKeys.MPS_PROJECT, host.getProject());
    });
    accessor = new InspectorTool.InspectorAccessor() {
      public void openToolLater(boolean activate) { setVisible(true); if (activate) editor.requestFocusInWindow(); }
      public jetbrains.mps.nodeEditor.EditorComponent getInspector() { return editor; }
      public void inspect(SNode node, com.intellij.openapi.fileEditor.FileEditor source, String[] hints, boolean readOnly) {
        boolean changed = editor.getUpdater().setInitialEditorHints(hints);
        changed |= editor.isReadOnlyInspector() != readOnly;
        editor.setReadOnlyInspector(readOnly);
        if (changed || editor.getEditedNode() != node) { editor.editNode(node); editor.relayout(); }
      }
      public boolean isAvailable() { return visible; }
    };
    InspectorTool.registerInspectorInstance(host.getProject(), accessor);
    releaseShortcuts = host.installEditorShortcuts(editor);
    window = new JFrame("Inspector");
    window.setUndecorated(true);
    window.setLocation(0, 664);
    JPanel pane = new JPanel(new BorderLayout());
    pane.setBackground(Color.WHITE);
    pane.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));
    pane.add(editor.getExternalComponent(), BorderLayout.CENTER);
    window.getContentPane().add(pane);
    resize(width);
    window.setVisible(true);
  }
  void resize(int width) { window.setSize(width, 260); }
  void layout(int width, int top, int height) {
    window.setLocation(0, top);
    window.setSize(width, Math.max(1, height));
  }
  void setVisible(boolean value) { visible = value; window.setVisible(value); }
  void inspect(SNode root) { accessor.inspect(root, null, null, false); }
  InspectorEditorComponent editor() { return editor; }
  String state() {
    return host.getProject().getModelAccess().computeReadAction(() -> {
      SNode node = editor.getEditedNode();
      if (node != null && node.getModel() == null) { inspect(null); node = null; }
      jsinterop.base.JsPropertyMap<Object> state = jsinterop.base.JsPropertyMap.of();
      state.set("visible", visible);
      state.set("id", node == null ? "" : node.getReference().toString());
      state.set("name", node == null ? "" : node.getPresentation());
      state.set("concept", node == null ? "" : node.getConcept().getName());
      return elemental2.core.Global.JSON.stringify(state);
    });
  }
  public void close() {
    releaseShortcuts.run();
    InspectorTool.unregisterInspectorInstance(host.getProject(), accessor);
    DataManager.removeDataProvider(editor.getExternalComponent());
    editor.dispose();
    window.dispose();
  }
}
