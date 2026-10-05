package com.intellij.ide;
import com.intellij.openapi.actionSystem.DataContext;
public interface CutProvider {
  default com.intellij.openapi.actionSystem.ActionUpdateThread getActionUpdateThread() { return com.intellij.openapi.actionSystem.ActionUpdateThread.EDT; }
  void performCut(DataContext context); boolean isCutEnabled(DataContext context); boolean isCutVisible(DataContext context);
}
