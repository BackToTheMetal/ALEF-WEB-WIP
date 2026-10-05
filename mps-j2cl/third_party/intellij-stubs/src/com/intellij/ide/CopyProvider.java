package com.intellij.ide;
import com.intellij.openapi.actionSystem.DataContext;
public interface CopyProvider {
  default com.intellij.openapi.actionSystem.ActionUpdateThread getActionUpdateThread() { return com.intellij.openapi.actionSystem.ActionUpdateThread.EDT; }
  void performCopy(DataContext context); boolean isCopyEnabled(DataContext context); boolean isCopyVisible(DataContext context);
}
