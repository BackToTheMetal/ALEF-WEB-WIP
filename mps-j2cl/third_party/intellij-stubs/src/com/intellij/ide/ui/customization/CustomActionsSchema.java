package com.intellij.ide.ui.customization;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.ActionManager;
public final class CustomActionsSchema {
  private static final CustomActionsSchema INSTANCE = new CustomActionsSchema();
  private final java.util.Map<String, AnAction> overrides = new java.util.HashMap<>();
  public static CustomActionsSchema getInstance() { return INSTANCE; }
  public AnAction getCorrectedAction(String id) { return overrides.containsKey(id) ? overrides.get(id) : ActionManager.getInstance().getAction(id); }
  public void setBrowserOverride(String id, AnAction action) { if (action == null) overrides.remove(id); else overrides.put(id, action); }
}
