package alefj2cl.example;

import elemental2.core.Global;
import elemental2.core.JsArray;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jetbrains.mps.nodeEditor.AbstractAdditionalPainter;
import jetbrains.mps.nodeEditor.EditorComponent;
import jetbrains.mps.nodeEditor.NodeEditorComponent;
import jetbrains.mps.nodeEditor.cells.EditorCell_Collection;
import jetbrains.mps.nodeEditor.cells.EditorCell_Label;
import jetbrains.mps.nodeEditor.configuration.EditorConfigurationBuilder;
import jetbrains.mps.openapi.editor.cells.EditorCell;
import jetbrains.mps.smodel.adapter.ids.MetaIdHelper;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import mpsj2cl.editor.BrowserEditorHost;
import org.jetbrains.mps.openapi.language.SReferenceLink;
import org.jetbrains.mps.openapi.model.SNode;

/** Read-only projectional view of a retained model in the shared MPS host. */
final class BrowserComparisonEditor implements AutoCloseable {
  private static final Color TEXT = new Color(48, 48, 48);
  private static final Color ADDED = new Color(28, 100, 49), REMOVED = new Color(156, 39, 39);
  private static final Color ADDED_BACKGROUND = new Color(213, 242, 221), REMOVED_BACKGROUND = new Color(253, 221, 216);
  final NodeEditorComponent editor;
  private final BrowserEditorHost host;
  private final Map<String, JsPropertyMap<Object>> changes = new HashMap<>();
  private final Map<String, List<EditorCell_Label>> cells = new LinkedHashMap<>();
  private final Map<EditorCell_Label, Highlight> highlights = new LinkedHashMap<>();

  private static final class Highlight {
    final int[] ranges;
    final boolean whole;
    Highlight(int[] ranges, boolean whole) { this.ranges = ranges; this.whole = whole; }
  }

  BrowserComparisonEditor(BrowserEditorHost host, SNode root, String json) {
    this.host = host;
    JsArray<Object> entries = Js.cast(Global.JSON.parse(json));
    for (int i = 0; i < entries.length; i++) {
      JsPropertyMap<Object> entry = Js.asPropertyMap(entries.getAt(i));
      changes.put(Js.asString(entry.get("id")), entry);
    }
    editor = new NodeEditorComponent(host.getProject().getRepository(),
        new EditorConfigurationBuilder().readOnly(true).hasContextMenu(false)
            .showLightBulb(false).showLeftHighlighter(false).showSelectionLine(false));
    ExampleTheme.style(editor);
    // Suppress language/error colours only in comparisons. The regular editor
    // retains its syntax colours and diagnostics.
    editor.addAdditionalPainter(new AbstractAdditionalPainter<Object>() {
      public boolean paintsAbove() { return false; }
      public boolean paintsBackground() { return false; }
      public void paint(Graphics g, EditorComponent component) {}
      public void paintBackground(Graphics g, EditorComponent component) {}
      public Object getItem() { return this; }
      public Rectangle getCoverageArea(EditorComponent component) {
        var cell = component.getRootCell();
        return cell == null ? null : new Rectangle(cell.getX(), cell.getY(), cell.getWidth(), cell.getHeight());
      }
      public Color getCellsFontColor(EditorCell_Label cell) { return TEXT; }
    });
    editor.setFocusable(true);
    editor.editNode(root);
    collect(editor.getRootCell());
    editor.relayout();
  }

  private void collect(EditorCell cell) {
    if (cell instanceof EditorCell_Label) {
      var label = (EditorCell_Label) cell;
      var line = label.getRenderedTextLine();
      line.setCaretEnabled(false);
      line.setTextSelectionColors(TEXT, new Color(230, 230, 230));
      String node = cell.getSNode() == null ? "" : cell.getSNode().getNodeId().toString();
      String role = cell.getSRole() == null ? "" : cell.getSRole().getName();
      // Pair the actual generated presentations, including derived/reference text.
      String key = node + "\0" + cell.getCellId() + "\0" + role;
      cells.computeIfAbsent(key, ignored -> new ArrayList<>()).add(label);
    }
    if (cell instanceof EditorCell_Collection) for (EditorCell child : (EditorCell_Collection) cell) collect(child);
  }

  void compare(BrowserComparisonEditor other, boolean before) {
    highlights.clear();
    for (var entry : cells.entrySet()) {
      var counterparts = other == null ? null : other.cells.get(entry.getKey());
      for (int i = 0; i < entry.getValue().size(); i++) {
        var cell = entry.getValue().get(i);
        var line = cell.getRenderedTextLine();
        String text = line.getText();
        var counterpart = counterparts == null || i >= counterparts.size() ? null : counterparts.get(i);
        boolean whole = structural(cell.getSNode()) || referenceChanged(cell) || counterpart == null;
        int[] ranges = whole ? new int[]{0, text.length()}
            : before ? ComparisonTextDiff.ranges(text, counterpart.getRenderedTextLine().getText())
            : ComparisonTextDiff.addedRanges(counterpart.getRenderedTextLine().getText(), text);
        ranges = ComparisonTextDiff.withoutWhitespace(text, ranges);
        line.setTextHighlights(ranges, before ? REMOVED : ADDED, before ? REMOVED_BACKGROUND : ADDED_BACKGROUND);
        if (ranges.length > 0) highlights.put(cell, new Highlight(ranges, whole));
      }
    }
    editor.repaint();
  }

  private boolean structural(SNode node) {
    for (SNode current = node; current != null; current = current.getParent()) {
      var change = changes.get(current.getNodeId().toString());
      if (change != null && (!"modified".equals(Js.asString(change.get("kind"))) || Boolean.TRUE.equals(change.get("whole")))) return true;
    }
    return false;
  }

  private boolean referenceChanged(EditorCell cell) {
    // A reference's presentation may be nested below its role-bearing cell.
    for (EditorCell current = cell; current != null; current = current.getParent()) {
      if (!(current.getSRole() instanceof SReferenceLink) || current.getSNode() == null) continue;
      var change = changes.get(current.getSNode().getNodeId().toString());
      if (change == null || !change.has("references")) continue;
      String role = MetaIdHelper.getAssociation((SReferenceLink) current.getSRole()).serialize();
      JsArray<String> references = Js.cast(change.get("references"));
      for (int i = 0; i < references.length; i++) if (role.equals(references.getAt(i))) return true;
    }
    return false;
  }

  void focus(String nodeId) {
    host.getProject().getModelAccess().runReadAction(() -> {
      SNode node = find(editor.getEditedNode(), nodeId);
      if (node == null) return;
      EditorCell cell = editor.findNodeCell(node);
      if (cell != null) { editor.changeSelection(cell); editor.scrollToCell(cell); editor.repaint(); }
    });
  }

  private SNode find(SNode root, String id) {
    if (id.equals(root.getNodeId().toString())) return root;
    for (SNode child : root.getChildren()) { SNode found = find(child, id); if (found != null) return found; }
    return null;
  }

  String state() {
    return host.getProject().getModelAccess().computeReadAction(() -> {
      var result = JsPropertyMap.<Object>of();
      result.set("readOnly", editor.isReadOnly());
      result.set("modelReference", editor.getEditedNode().getModel().getReference().toString());
      EditorCell selected = editor.getSelectedCell();
      result.set("selectedNodeId", selected == null || selected.getSNode() == null ? null : selected.getSNode().getNodeId().toString());
      result.set("selectedText", selected instanceof EditorCell_Label ? ((EditorCell_Label) selected).getText() : "");
      StringBuilder text = new StringBuilder(); text(editor.getRootCell(), text); result.set("text", text.toString());
      Set<String> nodes = new HashSet<>();
      var spans = new JsArray<Object>();
      for (var entry : highlights.entrySet()) {
        var cell = entry.getKey(); var highlight = entry.getValue();
        String id = cell.getSNode() == null ? "" : cell.getSNode().getNodeId().toString();
        nodes.add(id);
        var span = JsPropertyMap.<Object>of();
        span.set("id", id); span.set("cellId", cell.getCellId());
        span.set("text", cell.getRenderedTextLine().getText()); span.set("whole", highlight.whole);
        var ranges = new JsArray<Object>();
        for (int i = 0; i < highlight.ranges.length; i += 2) ranges.push(new JsArray<>((double) highlight.ranges[i], (double) highlight.ranges[i + 1]));
        span.set("ranges", ranges); spans.push(span);
      }
      result.set("highlightedNodes", nodes.size()); result.set("highlights", spans);
      return Global.JSON.stringify(result);
    });
  }

  private void text(EditorCell cell, StringBuilder text) {
    if (cell instanceof EditorCell_Label) text.append(((EditorCell_Label) cell).getText()).append(' ');
    if (cell instanceof EditorCell_Collection) for (EditorCell child : (EditorCell_Collection) cell) text(child, text);
  }

  public void close() { editor.dispose(); }
}
