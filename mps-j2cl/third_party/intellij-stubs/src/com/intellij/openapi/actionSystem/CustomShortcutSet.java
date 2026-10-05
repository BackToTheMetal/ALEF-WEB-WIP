package com.intellij.openapi.actionSystem;
import javax.swing.KeyStroke;
public class CustomShortcutSet implements ShortcutSet {
  public static final CustomShortcutSet EMPTY = new CustomShortcutSet(new Shortcut[0]);
  private final Shortcut[] shortcuts;
  public CustomShortcutSet(Shortcut... shortcuts) { this.shortcuts = java.util.Arrays.copyOf(shortcuts, shortcuts.length); }
  public CustomShortcutSet(KeyStroke... strokes) {
    shortcuts = new Shortcut[strokes.length];
    for (int i = 0; i < strokes.length; i++) shortcuts[i] = new KeyboardShortcut(strokes[i], null);
  }
  public CustomShortcutSet(int keyCode, int modifiers) { this(KeyStroke.getKeyStroke(keyCode, modifiers)); }
  public static CustomShortcutSet fromString(String... strokes) {
    KeyStroke[] result = new KeyStroke[strokes.length];
    for (int i = 0; i < strokes.length; i++) result[i] = KeyStroke.getKeyStroke(strokes[i]);
    return new CustomShortcutSet(result);
  }
  @Override public Shortcut[] getShortcuts() { return java.util.Arrays.copyOf(shortcuts, shortcuts.length); }
}
