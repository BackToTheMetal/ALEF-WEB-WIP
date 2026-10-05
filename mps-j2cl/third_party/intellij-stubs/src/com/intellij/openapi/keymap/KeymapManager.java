package com.intellij.openapi.keymap;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.actionSystem.Shortcut;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.KeyStroke;
public class KeymapManager {
  public static final String DEFAULT_IDEA_KEYMAP = "$default";
  private static final KeymapManager INSTANCE = new KeymapManager();
  private final Map<String, Keymap> keymaps = new LinkedHashMap<>();
  private Keymap active = getKeymap(DEFAULT_IDEA_KEYMAP);
  public KeymapManager() {
    active.addShortcut(com.intellij.openapi.actionSystem.IdeActions.ACTION_EDITOR_ESCAPE, KeyboardShortcut.fromString("ESCAPE"));
    active.addShortcut(com.intellij.openapi.actionSystem.IdeActions.ACTION_EDITOR_MOVE_CARET_UP, KeyboardShortcut.fromString("UP"));
    active.addShortcut(com.intellij.openapi.actionSystem.IdeActions.ACTION_EDITOR_MOVE_CARET_DOWN, KeyboardShortcut.fromString("DOWN"));
    active.addShortcut(com.intellij.openapi.actionSystem.IdeActions.ACTION_QUICK_JAVADOC, KeyboardShortcut.fromString("ctrl Q"));
  }
  public static KeymapManager getInstance() { return INSTANCE; }
  public Keymap getActiveKeymap() { return active; }
  public void setActiveKeymap(Keymap keymap) { active = java.util.Objects.requireNonNull(keymap); }
  public Keymap getKeymap(String name) { return keymaps.computeIfAbsent(name, BrowserKeymap::new); }
  public Keymap[] getAllKeymaps() { return keymaps.values().toArray(new Keymap[0]); }
  private static final class BrowserKeymap implements Keymap {
    final String name; final Map<String, List<Shortcut>> shortcuts = new LinkedHashMap<>();
    BrowserKeymap(String name) { this.name = name; }
    @Override public String getName() { return name; }
    @Override public Shortcut[] getShortcuts(String id) { List<Shortcut> values = shortcuts.get(id); return values == null ? new Shortcut[0] : values.toArray(new Shortcut[0]); }
    @Override public void addShortcut(String id, Shortcut shortcut) { List<Shortcut> values = shortcuts.computeIfAbsent(id, ignored -> new ArrayList<>()); if (!values.contains(shortcut)) values.add(shortcut); }
    @Override public void removeShortcut(String id, Shortcut shortcut) { List<Shortcut> values = shortcuts.get(id); if (values != null) values.remove(shortcut); }
    @Override public void removeAllActionShortcuts(String id) { shortcuts.remove(id); }
    @Override public String[] getActionIds(Shortcut shortcut) { List<String> result = new ArrayList<>(); for (Map.Entry<String, List<Shortcut>> entry : shortcuts.entrySet()) if (entry.getValue().contains(shortcut)) result.add(entry.getKey()); return result.toArray(new String[0]); }
    @Override public String[] getActionIds(KeyStroke stroke) { return getActionIds(new KeyboardShortcut(stroke, null)); }
  }
}
