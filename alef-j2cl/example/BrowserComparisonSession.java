package alefj2cl.example;

import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.WebSwingContainer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import mpsj2cl.editor.BrowserEditorHost;

/** Temporary versioned models and read-only views inside the existing MPS host. */
final class BrowserComparisonSession implements AutoCloseable {
  private final BrowserEditorHost host;
  private final Map<String, BackendModelBridge> versions = new HashMap<>();
  private final Map<String, BrowserComparisonEditor> editors = new HashMap<>();
  private final Map<String, JFrame> windows = new HashMap<>();
  private String status = "LOADING";
  private int remaining = 2;
  private boolean closed;

  BrowserComparisonSession(BrowserEditorHost host) { this.host = host; }

  void load(String before, String after) {
    loadSide("before", before); loadSide("after", after);
  }
  private void loadSide(String side, String json) {
    if (closed || status.startsWith("FAIL:")) return;
    var models = new BackendModelBridge(host, UUID.randomUUID().toString());
    versions.put(side, models);
    models.loadAsync(json, () -> {
      if (!closed && --remaining == 0 && !status.startsWith("FAIL:")) status = "READY";
    }, failure -> { status = "FAIL: " + failure; });
  }
  String status() { return closed ? "CLOSED" : status; }

  void open(String side, String reference, String changes, String targetId) {
    if (!"READY".equals(status())) throw new IllegalStateException("Comparison models are not ready");
    Element target = DomGlobal.document.getElementById(targetId);
    if (target == null) throw new IllegalArgumentException("Comparison surface is unavailable");
    host.getProject().getModelAccess().runReadAction(() -> {
      var root = versions.get(side).comparisonRoot(reference);
      if (root == null || root.getParent() != null) throw new IllegalArgumentException("Comparison root is unavailable");
      closeEditor(side);
      var view = new BrowserComparisonEditor(host, root, changes);
      editors.put(side, view);
      // Compare generated cell presentations once both panes are available.
      for (var entry : editors.entrySet()) entry.getValue().compare(
          editors.get("before".equals(entry.getKey()) ? "after" : "before"), "before".equals(entry.getKey()));
      var pane = new JPanel(new BorderLayout());
      pane.setBackground(Color.WHITE); pane.setBorder(BorderFactory.createEmptyBorder(16, 14, 16, 10));
      pane.add(view.editor.getExternalComponent(), BorderLayout.CENTER);
      var window = new JFrame("Read-only comparison " + side);
      windows.put(side, window);
      window.setUndecorated(true); window.setLocation(0, 0);
      window.getContentPane().add(pane); window.setSize(Math.max(240, target.clientWidth), Math.max(120, target.clientHeight));
      window.setVisible(true);
      target.appendChild(WebSwingContainer.componentElement(window));
      window.validate(); view.editor.relayout(); view.editor.repaint();
    });
  }
  void layout(String side, int width, int height) { var window = windows.get(side); if (window != null) window.setSize(Math.max(240, width), Math.max(120, height)); }
  String state(String side) { var editor = editors.get(side); return editor == null ? "null" : editor.state(); }
  void focus(String side, String node) { var editor = editors.get(side); if (editor != null) editor.focus(node); }
  void closeEditor(String side) {
    var editor = editors.remove(side); if (editor != null) editor.close();
    var window = windows.remove(side); if (window != null) window.dispose();
  }
  public void close() {
    if (closed) return;
    closed = true;
    closeEditor("before"); closeEditor("after");
    host.getProject().getModelAccess().runWriteAction(() -> { for (var version : versions.values()) version.closeRepository(); versions.clear(); });
  }
}
