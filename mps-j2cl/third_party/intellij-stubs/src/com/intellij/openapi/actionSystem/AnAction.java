package com.intellij.openapi.actionSystem;
import com.intellij.ide.DataManager;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.util.Disposer;
import java.awt.event.ActionEvent;
import java.util.function.Supplier;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JComponent;
public abstract class AnAction {
  public static final AnAction[] EMPTY_ARRAY = new AnAction[0];
  private final Presentation template = new Presentation();
  private ShortcutSet shortcuts = CustomShortcutSet.EMPTY;
  private boolean enabledInModalContext;
  public AnAction() {}
  public AnAction(String text) { template.setText(text); }
  public AnAction(Icon icon) { template.setIcon(icon); }
  public AnAction(Supplier<String> text) { this(text.get()); }
  public AnAction(String text, String description, Icon icon) { this(text); template.setDescription(description); template.setIcon(icon); }
  public AnAction(Supplier<String> text, Supplier<String> description, Icon icon) { this(text.get(), description.get(), icon); }
  public AnAction(Supplier<String> text, Supplier<String> description, Supplier<? extends Icon> icon) { this(text.get(), description.get(), icon.get()); }
  public AnAction(Supplier<String> text, Supplier<String> description) { this(text.get(), description.get(), null); }
  public abstract void actionPerformed(AnActionEvent event);
  public static com.intellij.openapi.project.Project getEventProject(AnActionEvent event) { return event.getProject(); }
  public void update(AnActionEvent event) {}
  public void beforeActionPerformedUpdate(AnActionEvent event) { update(event); }
  public Presentation getTemplatePresentation() { return template; }
  public String getTemplateText() { return template.getText(); }
  public ActionUpdateThread getActionUpdateThread() { return ActionUpdateThread.EDT; }
  public boolean isDumbAware() { return this instanceof DumbAware; }
  public ShortcutSet getShortcutSet() { return shortcuts; }
  public void setShortcutSet(ShortcutSet value) { shortcuts = value; }
  public void copyFrom(AnAction other) { template.copyFrom(other.template); shortcuts = other.shortcuts; }
  public void copyShortcutFrom(AnAction other) { shortcuts = other.shortcuts; }
  public boolean isEnabledInModalContext() { return enabledInModalContext; }
  protected void setEnabledInModalContext(boolean value) { enabledInModalContext = value; }
  public void registerCustomShortcutSet(ShortcutSet set, JComponent component) {
    shortcuts = set;
    for (Shortcut shortcut : set.getShortcuts()) {
      if (!(shortcut instanceof KeyboardShortcut)) throw new UnsupportedOperationException("Only keyboard shortcuts are implemented");
      KeyboardShortcut keyboard = (KeyboardShortcut) shortcut;
      if (keyboard.getSecondKeyStroke() != null) throw new UnsupportedOperationException("Two-stroke action shortcuts need host keymap dispatch");
      component.registerKeyboardAction(new AbstractAction() {
        @Override public void actionPerformed(ActionEvent ignored) {
          AnActionEvent event = AnActionEvent.createFromAnAction(AnAction.this, null, ActionPlaces.UNKNOWN, DataManager.getInstance().getDataContext(component));
          beforeActionPerformedUpdate(event);
          if (event.getPresentation().isEnabledAndVisible()) AnAction.this.actionPerformed(event);
        }
      }, keyboard.getFirstKeyStroke(), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }
  }
  public void registerCustomShortcutSet(ShortcutSet set, JComponent component, Disposable parent) {
    registerCustomShortcutSet(set, component); Disposer.register(parent, () -> unregisterCustomShortcutSet(component));
  }
  public void registerCustomShortcutSet(int code, int modifiers, JComponent component) { registerCustomShortcutSet(new CustomShortcutSet(code, modifiers), component); }
  public void registerCustomShortcutSet(JComponent component, Disposable parent) { registerCustomShortcutSet(shortcuts, component, parent); }
  public void unregisterCustomShortcutSet(JComponent component) {
    for (Shortcut shortcut : shortcuts.getShortcuts()) if (shortcut instanceof KeyboardShortcut) component.unregisterKeyboardAction(((KeyboardShortcut) shortcut).getFirstKeyStroke());
  }
}
