package com.intellij.openapi.keymap;
import com.intellij.openapi.actionSystem.Shortcut;
import javax.swing.KeyStroke;
public interface Keymap {
  String getName();
  Shortcut[] getShortcuts(String actionId);
  void addShortcut(String actionId, Shortcut shortcut);
  void removeShortcut(String actionId, Shortcut shortcut);
  void removeAllActionShortcuts(String actionId);
  String[] getActionIds(Shortcut shortcut);
  String[] getActionIds(KeyStroke stroke);
}
