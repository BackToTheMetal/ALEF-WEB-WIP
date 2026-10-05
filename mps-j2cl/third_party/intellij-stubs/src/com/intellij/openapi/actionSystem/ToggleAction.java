package com.intellij.openapi.actionSystem;
import javax.swing.Icon;
public abstract class ToggleAction extends AnAction {
  public ToggleAction() {}
  public ToggleAction(String text) { super(text); }
  public ToggleAction(String text, String description, Icon icon) { super(text, description, icon); }
  public abstract boolean isSelected(AnActionEvent event);
  public abstract void setSelected(AnActionEvent event, boolean selected);
  @Override public void actionPerformed(AnActionEvent event) { setSelected(event, !isSelected(event)); }
}
