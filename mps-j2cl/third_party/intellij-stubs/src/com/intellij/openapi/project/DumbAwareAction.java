package com.intellij.openapi.project;
import com.intellij.openapi.actionSystem.AnAction;
import javax.swing.Icon;
public abstract class DumbAwareAction extends AnAction implements DumbAware {
  public DumbAwareAction() {}
  public DumbAwareAction(String text) { super(text); }
  public DumbAwareAction(String text, String description, Icon icon) { super(text, description, icon); }
}
