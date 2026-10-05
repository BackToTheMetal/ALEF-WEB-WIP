package com.intellij.openapi.actionSystem;
public final class ActionInGroup {
  private final DefaultActionGroup group; private final AnAction action;
  ActionInGroup(DefaultActionGroup group, AnAction action) { this.group = group; this.action = action; }
  public ActionInGroup setAsSecondary(boolean secondary) { group.setAsPrimary(action, !secondary); return this; }
}
