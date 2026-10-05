package mpsj2cl.editorcontracts;

import java.awt.Canvas;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import jetbrains.mps.classloading.ModuleClassLoader;
import jetbrains.mps.classloading.RuntimeTestLoaders;
import jetbrains.mps.nodeEditor.keymaps.AWTKeymapHandler;
import jetbrains.mps.nodeEditor.selection.AbstractSelection;
import jetbrains.mps.nodeEditor.selection.SelectionInfoImpl;
import jetbrains.mps.nodeEditor.selection.SelectionRestoreException;
import jetbrains.mps.openapi.editor.EditorComponent;
import jetbrains.mps.openapi.editor.cells.CellActionType;
import jetbrains.mps.openapi.editor.cells.EditorCell;
import jetbrains.mps.openapi.editor.cells.KeyMap;
import jetbrains.mps.openapi.editor.cells.KeyMap.ActionKey;
import jetbrains.mps.openapi.editor.selection.Selection;
import jetbrains.mps.openapi.editor.selection.SelectionInfo;
import jetbrains.mps.openapi.editor.selection.SelectionStoreException;
import org.jdom.Element;
import org.jetbrains.mps.openapi.model.SNode;
import mpsj2cl.editor.BrowserSelectionFactories;

public final class EditorRuntimeContractsTest {
  public static String run() {
    keymaps();
    selectionDirections();
    selections();
    return "PASS";
  }

  private static void keymaps() {
    List<String> valid = AWTKeymapHandler.getValidKeyCodes();
    require(valid.size() == 191, "189 key names minus 3 modifiers plus 5 categories");
    require(valid.contains("VK_SEPARATOR") && valid.contains("VK_SEPARATER"), "both separator aliases");
    require(!valid.contains("VK_SHIFT") && !valid.contains("VK_ALT") && !valid.contains("VK_CONTROL"), "modifiers excluded");
    AWTKeymapHandler handler = new AWTKeymapHandler();
    Canvas source = new Canvas();
    Collection<ActionKey> keys = handler.getActionKeys(new KeyEvent(source, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_SEPARATOR, ','));
    require(keys.contains(new ActionKey(KeyMap.KEY_MODIFIERS_NONE, "VK_SEPARATOR", false)), "canonical separator event");
    require(keys.contains(new ActionKey(KeyMap.KEY_MODIFIERS_NONE, "VK_SEPARATER", false)), "legacy separator event");
    keys = handler.getActionKeys(new KeyEvent(source, KeyEvent.KEY_TYPED, 0, 0, KeyEvent.VK_UNDEFINED, '\u00e9'));
    require(keys.contains(new ActionKey(KeyMap.KEY_MODIFIERS_NONE, KeyMap.KEY_CODE_LETTER, true)), "Unicode typed letters");
    require(keys.contains(new ActionKey(KeyMap.KEY_MODIFIERS_ANY, KeyMap.KEY_CODE_CHAR, true)), "typed category wildcard");
    keys = handler.getActionKeys(new KeyEvent(source, KeyEvent.KEY_PRESSED, 0, InputEvent.CTRL_DOWN_MASK, KeyEvent.VK_A, 'a'));
    require(keys.contains(new ActionKey(KeyMap.KEY_MODIFIERS_CTRL, "VK_A", false)), "control key binding");
    require(!keys.contains(new ActionKey(KeyMap.KEY_MODIFIERS_CTRL, "a", false)), "control excludes key character");
  }

  private static void selectionDirections() {
    try {
      for (Selection.SelectionDirection direction : Selection.SelectionDirection.values()) {
        require(SelectionInfoImpl.Util.getEnumProperty(Collections.singletonMap("direction", direction.name()),
            "direction", Selection.SelectionDirection.class, Selection.SelectionDirection.NONE) == direction,
            "persisted direction restores by declared enum name");
      }
      require(SelectionInfoImpl.Util.getEnumProperty(Collections.emptyMap(), "direction",
          Selection.SelectionDirection.class, Selection.SelectionDirection.RIGHT) == Selection.SelectionDirection.RIGHT,
          "missing direction retains supplied default");
      try {
        SelectionInfoImpl.Util.getEnumProperty(Collections.singletonMap("direction", "right"), "direction",
            Selection.SelectionDirection.class, Selection.SelectionDirection.NONE);
        throw new AssertionError("invalid direction must fail restoration");
      } catch (SelectionStoreException expected) {
        require(expected.getMessage().contains("Invalid enum literal name specified: right"), "invalid enum diagnostic");
      }
    } catch (SelectionStoreException failure) {
      throw new AssertionError("valid selection direction", failure);
    }
  }

  private static void selections() {
    ModuleClassLoader loader = RuntimeTestLoaders.create("editor-contracts", Collections.emptyList());
    String name = "mpsj2cl.editorcontracts.RegisteredSelection";
    BrowserSelectionFactories.registerSelection(loader, name, StoredSelection.class,
        (editor, properties, cell) -> new StoredSelection(editor, properties), true);
    SelectionInfoImpl source = new SelectionInfoImpl(name);
    source.getPropertiesMap().put("value", "kept");
    Element xml = new Element("selection");
    source.persistToXML(xml);
    SelectionInfoImpl restored = new SelectionInfoImpl(xml);
    require(source.equals(restored), "selection XML round trip");
    // This constructor-only fixture does not dereference an editor. Full cell/model restoration is tested by the editor fixture.
    StoredSelection selection = (StoredSelection) restored.createSelection(null);
    require(selection != null && selection.properties.get("value").equals("kept"), "typed constructor receives persisted properties");
    require(selection.properties == restored.getPropertiesMap(), "constructor properties identity");
    String stale = "mpsj2cl.editorcontracts.StaleSelection";
    BrowserSelectionFactories.registerSelection(loader, stale, StaleSelection.class,
        (editor, properties, cell) -> { throw new SelectionRestoreException(); }, true);
    require(new SelectionInfoImpl(stale).createSelection(null) == null, "stale selection does not escape");
    loader.dispose();
    require(!ModuleClassLoader.hasFactory(StoredSelection.class, EditorComponent.class, Map.class,
        jetbrains.mps.openapi.editor.cells.CellInfo.class), "factory released with module loader");
  }

  public static class StoredSelection extends AbstractSelection {
    final Map<String, String> properties;
    StoredSelection(EditorComponent editor, Map<String, String> properties) { super(editor); this.properties = properties; }
    public void activate() {}
    public void deactivate() {}
    public SelectionInfo getSelectionInfo() { throw new UnsupportedOperationException("Constructor fixture only"); }
    public boolean isSame(Selection another) { return this == another; }
    public boolean canExecuteAction(CellActionType type) { return false; }
    public void executeAction(CellActionType type) { throw new UnsupportedOperationException("Constructor fixture only"); }
    public List<EditorCell> getSelectedCells() { return Collections.emptyList(); }
    public List<SNode> getSelectedNodes() { return Collections.emptyList(); }
    public void ensureVisible() {}
  }

  public static final class StaleSelection extends StoredSelection {
    StaleSelection(EditorComponent editor, Map<String, String> properties) { super(editor, properties); }
  }

  private static void require(boolean condition, String message) {
    if (!condition) throw new AssertionError(message);
  }
}
