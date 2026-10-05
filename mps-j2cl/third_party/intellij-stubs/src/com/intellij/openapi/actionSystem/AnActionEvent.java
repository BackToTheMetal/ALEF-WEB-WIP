package com.intellij.openapi.actionSystem;
import com.intellij.openapi.project.Project;
import java.awt.event.InputEvent;
public class AnActionEvent {
  private final InputEvent input;
  private final DataContext context;
  private final String place;
  private final Presentation presentation;
  private final ActionManager manager;
  private final int modifiers;
  private final ActionUiKind kind;
  public AnActionEvent(InputEvent input, DataContext context, String place, Presentation presentation, ActionManager manager, int modifiers) {
    this(context, presentation, place, ActionUiKind.NONE, input, modifiers, manager);
  }
  public AnActionEvent(InputEvent input, DataContext context, String place, Presentation presentation, ActionManager manager, int modifiers, boolean contextMenu, boolean toolbar) {
    this(context, presentation, place, toolbar ? ActionUiKind.TOOLBAR : contextMenu ? ActionUiKind.POPUP : ActionUiKind.NONE, input, modifiers, manager);
  }
  public AnActionEvent(DataContext context, Presentation presentation, String place, ActionUiKind kind, InputEvent input, int modifiers, ActionManager manager) {
    this.input = input; this.context = context; this.place = place; this.presentation = presentation; this.manager = manager; this.modifiers = modifiers; this.kind = kind;
  }
  public static AnActionEvent createFromDataContext(String place, Presentation presentation, DataContext context) {
    return new AnActionEvent(null, context, place, presentation, ActionManager.getInstance(), 0);
  }
  public static AnActionEvent createFromAnAction(AnAction action, InputEvent input, String place, DataContext context) {
    return new AnActionEvent(input, context, place, action.getTemplatePresentation().clone(), ActionManager.getInstance(), input == null ? 0 : input.getModifiersEx());
  }
  public static AnActionEvent createEvent(DataContext context, Presentation presentation, String place, ActionUiKind kind, InputEvent input) {
    return new AnActionEvent(context, presentation, place, kind, input, input == null ? 0 : input.getModifiersEx(), ActionManager.getInstance());
  }
  public static AnActionEvent createEvent(AnAction action, DataContext context, Presentation presentation, String place, ActionUiKind kind, InputEvent input) {
    return createEvent(context, presentation, place, kind, input);
  }
  public InputEvent getInputEvent() { return input; }
  public Project getProject() { return getData(CommonDataKeys.PROJECT); }
  public DataContext getDataContext() { return context; }
  public <T> T getData(DataKey<T> key) { return key.getData(context); }
  public <T> T getRequiredData(DataKey<T> key) { return java.util.Objects.requireNonNull(getData(key), key.getName()); }
  public String getPlace() { return place; }
  public Presentation getPresentation() { return presentation; }
  public ActionManager getActionManager() { return manager; }
  public int getModifiers() { return modifiers; }
  public ActionUiKind getUiKind() { return kind; }
  public boolean isFromContextMenu() { return kind == ActionUiKind.POPUP; }
  public boolean isFromActionToolbar() { return kind == ActionUiKind.TOOLBAR; }
  public boolean isFromMainMenu() { return kind == ActionUiKind.MAIN_MENU; }
  public AnActionEvent withDataContext(DataContext value) { return new AnActionEvent(value, presentation, place, kind, input, modifiers, manager); }
}
