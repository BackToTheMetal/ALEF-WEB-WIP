package com.intellij.openapi.actionSystem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import javax.swing.Icon;
public class DefaultActionGroup extends ActionGroup {
  private final List<AnAction> children = new ArrayList<>();
  private final java.util.Set<AnAction> secondary = java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>());
  public DefaultActionGroup() {}
  public DefaultActionGroup(AnAction... actions) { addAll(actions); }
  public DefaultActionGroup(List<? extends AnAction> actions) { children.addAll(actions); }
  public DefaultActionGroup(String text, boolean popup) { super(text, popup); }
  public DefaultActionGroup(Supplier<String> text, boolean popup) { super(text, popup); }
  public DefaultActionGroup(String text, String description, Icon icon) { super(text, description, icon); }
  public DefaultActionGroup(String text, List<? extends AnAction> actions) { super(text, false); children.addAll(actions); }
  public void add(AnAction action) { children.add(java.util.Objects.requireNonNull(action)); }
  public ActionInGroup addAction(AnAction action) { add(action); return new ActionInGroup(this, action); }
  public void setAsPrimary(AnAction action, boolean primary) { if (primary) secondary.remove(action); else secondary.add(action); }
  public boolean isPrimary(AnAction action) { return !secondary.contains(action); }
  public void add(AnAction action, ActionManager manager) { add(action); }
  public ActionInGroup add(AnAction action, Constraints constraints) {
    int index = children.size();
    if (constraints.getAnchor() == Anchor.FIRST) index = 0;
    else if (constraints.getAnchor() == Anchor.BEFORE || constraints.getAnchor() == Anchor.AFTER) {
      AnAction relative = ActionManager.getInstance().getAction(constraints.getRelativeToActionId()); int found = children.indexOf(relative);
      if (found < 0) throw new IllegalArgumentException("Unknown relative action: " + constraints.getRelativeToActionId());
      index = found + (constraints.getAnchor() == Anchor.AFTER ? 1 : 0);
    }
    children.add(index, java.util.Objects.requireNonNull(action)); return new ActionInGroup(this, action);
  }
  public void addAll(AnAction... actions) { children.addAll(Arrays.asList(actions)); }
  public void addAll(Collection<? extends AnAction> actions) { children.addAll(actions); }
  public void addAll(ActionGroup group) { addAll(group.getChildren((AnActionEvent) null)); }
  public void addSeparator() { add(new Separator()); }
  public void addSeparator(String text) { add(new Separator(text)); }
  public void remove(AnAction action) { children.remove(action); secondary.remove(action); }
  public void removeAll() { children.clear(); secondary.clear(); }
  public boolean containsAction(AnAction action) { return children.contains(action); }
  public int getChildrenCount() { return children.size(); }
  @Override public AnAction[] getChildren(AnActionEvent event) { return children.toArray(new AnAction[0]); }
  public AnAction[] getChildren(ActionManager manager) { return getChildren((AnActionEvent) null); }
  public AnAction[] getChildActionsOrStubs() { return getChildren((AnActionEvent) null); }
}
