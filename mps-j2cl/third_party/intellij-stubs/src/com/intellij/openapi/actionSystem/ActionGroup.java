package com.intellij.openapi.actionSystem;
import java.util.function.Supplier;
import javax.swing.Icon;
public abstract class ActionGroup extends AnAction {
  public static final ActionGroup EMPTY_GROUP = new ActionGroup() { @Override public AnAction[] getChildren(AnActionEvent event) { return EMPTY_ARRAY; } };
  private boolean popup;
  public ActionGroup() {}
  public ActionGroup(String text, boolean popup) { super(text); this.popup = popup; }
  public ActionGroup(Supplier<String> text, boolean popup) { this(text.get(), popup); }
  public ActionGroup(String text, String description, Icon icon) { super(text, description, icon); }
  public abstract AnAction[] getChildren(AnActionEvent event);
  @Override public void actionPerformed(AnActionEvent event) { throw new UnsupportedOperationException("Choose an action from this group"); }
  public boolean isPopup() { return popup; }
  public void setPopup(boolean value) { popup = value; }
}
