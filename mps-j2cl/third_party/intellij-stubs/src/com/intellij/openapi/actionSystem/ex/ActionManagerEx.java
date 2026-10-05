package com.intellij.openapi.actionSystem.ex;
import com.intellij.openapi.actionSystem.ActionManager;
public final class ActionManagerEx {
  private ActionManagerEx() {}
  public static ActionManager getInstance() { return ActionManager.getInstance(); }
  public static ActionManager getInstanceEx() { return ActionManager.getInstance(); }
}
