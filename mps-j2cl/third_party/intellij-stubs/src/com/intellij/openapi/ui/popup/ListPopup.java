package com.intellij.openapi.ui.popup;
public interface ListPopup extends JBPopup {
  ListPopupStep<?> getListStep();
  default void setHandleAutoSelectionBeforeShow(boolean value) {}
}
