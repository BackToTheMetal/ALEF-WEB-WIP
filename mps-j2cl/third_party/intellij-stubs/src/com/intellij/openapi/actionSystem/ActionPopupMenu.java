package com.intellij.openapi.actionSystem;
import javax.swing.JPopupMenu;
public interface ActionPopupMenu {
  JPopupMenu getComponent();
  void setTargetComponent(javax.swing.JComponent component);
}
