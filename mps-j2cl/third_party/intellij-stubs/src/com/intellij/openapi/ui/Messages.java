package com.intellij.openapi.ui;
import com.intellij.openapi.project.Project;
import java.awt.Component;
import javax.swing.Icon;
public final class Messages {
  public static final int YES = 0, NO = 1, CANCEL = 2;
  private Messages() {}
  public static Icon getQuestionIcon() { return com.intellij.compat.BrowserIcons.named("General.QuestionDialog"); }
  public static Icon getWarningIcon() { return com.intellij.compat.BrowserIcons.named("General.WarningDialog"); }
  public static Icon getErrorIcon() { return com.intellij.compat.BrowserIcons.named("General.ErrorDialog"); }
  public static void showWarningDialog(Project project, String message, String title) { showWarningDialog((Component) null, message, title); }
  public static void showWarningDialog(Component parent, String message, String title) {
    com.intellij.notification.Notifications.Bus.notify(new com.intellij.notification.Notification("Messages", title, message, com.intellij.notification.NotificationType.WARNING));
  }
  public static int showYesNoDialog(Component parent, String message, String title, Icon icon) {
    throw new UnsupportedOperationException("Synchronous confirmation dialogs require a browser host workflow; no response is fabricated");
  }
}
