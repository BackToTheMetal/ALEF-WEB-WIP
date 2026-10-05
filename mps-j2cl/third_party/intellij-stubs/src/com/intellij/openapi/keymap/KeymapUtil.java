package com.intellij.openapi.keymap;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.actionSystem.Shortcut;
import javax.swing.KeyStroke;
public final class KeymapUtil {
  private KeymapUtil() {}
  public static com.intellij.openapi.actionSystem.ShortcutSet getActiveKeymapShortcuts(String id) { return new com.intellij.openapi.actionSystem.CustomShortcutSet(KeymapManager.getInstance().getActiveKeymap().getShortcuts(id)); }
  public static String getKeystrokeText(KeyStroke stroke) { return stroke == null ? "" : stroke.toString(); }
  public static String getShortcutText(Shortcut shortcut) {
    if (!(shortcut instanceof KeyboardShortcut)) return shortcut.toString();
    KeyboardShortcut key = (KeyboardShortcut) shortcut;
    return getKeystrokeText(key.getFirstKeyStroke()) + (key.getSecondKeyStroke() == null ? "" : ", " + getKeystrokeText(key.getSecondKeyStroke()));
  }
  public static String getFirstKeyboardShortcutText(String id) { for (Shortcut shortcut : KeymapManager.getInstance().getActiveKeymap().getShortcuts(id)) if (shortcut.isKeyboard()) return getShortcutText(shortcut); return ""; }
  public static String getFirstKeyboardShortcutText(AnAction action) { for (Shortcut shortcut : action.getShortcutSet().getShortcuts()) if (shortcut.isKeyboard()) return getShortcutText(shortcut); return ""; }
}
