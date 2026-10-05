package com.intellij.openapi.actionSystem;
import javax.swing.JComponent;
public interface ActionToolbar {
  int NOWRAP_LAYOUT_POLICY = 0, WRAP_LAYOUT_POLICY = 1, AUTO_LAYOUT_POLICY = 2;
  JComponent getComponent();
  void setTargetComponent(JComponent component);
  void setLayoutPolicy(int policy);
  void updateActionsImmediately();
  default void setReservePlaceAutoPopupIcon(boolean reserve) {}
  default void setSecondaryActionsIcon(javax.swing.Icon icon, boolean hideDropdown) {}
}
