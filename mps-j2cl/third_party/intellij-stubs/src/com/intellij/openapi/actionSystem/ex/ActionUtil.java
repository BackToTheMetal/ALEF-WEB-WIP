package com.intellij.openapi.actionSystem.ex;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
public final class ActionUtil {
  private ActionUtil() {}
  public static void updateAction(AnAction action, AnActionEvent event) { action.update(event); }
  public static void performAction(AnAction action, AnActionEvent event) { performActionDumbAwareWithCallbacks(action, event); }
  public static boolean lastUpdateAndCheckDumb(AnAction action, AnActionEvent event, boolean visibility) { action.beforeActionPerformedUpdate(event); return event.getPresentation().isEnabled() && (!visibility || event.getPresentation().isVisible()); }
  public static void performActionDumbAwareWithCallbacks(AnAction action, AnActionEvent event) { if (lastUpdateAndCheckDumb(action, event, true)) action.actionPerformed(event); }
}
