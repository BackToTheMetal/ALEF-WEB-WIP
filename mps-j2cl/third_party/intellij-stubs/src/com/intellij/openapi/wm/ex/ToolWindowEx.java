package com.intellij.openapi.wm.ex;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.wm.ToolWindow;
public interface ToolWindowEx extends ToolWindow {
  void setTitleActions(java.util.List<? extends AnAction> actions);
  void setAdditionalGearActions(ActionGroup actions);
}
