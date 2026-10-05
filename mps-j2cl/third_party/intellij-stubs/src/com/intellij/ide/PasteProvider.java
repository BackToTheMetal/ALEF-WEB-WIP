package com.intellij.ide;
import com.intellij.openapi.actionSystem.DataContext;
public interface PasteProvider {
  default com.intellij.openapi.actionSystem.ActionUpdateThread getActionUpdateThread() { return com.intellij.openapi.actionSystem.ActionUpdateThread.EDT; }
  void performPaste(DataContext context); boolean isPastePossible(DataContext context); boolean isPasteEnabled(DataContext context);
}
