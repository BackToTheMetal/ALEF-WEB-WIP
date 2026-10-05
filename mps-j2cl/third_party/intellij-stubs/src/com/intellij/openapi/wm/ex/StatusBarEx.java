package com.intellij.openapi.wm.ex;
import com.intellij.openapi.wm.StatusBar;
public interface StatusBarEx extends StatusBar {
  String getInfo();
  default void notifyProgressByBalloon(com.intellij.openapi.ui.MessageType type, String text) {
    setInfo(text);
    com.intellij.notification.NotificationType severity = type == com.intellij.openapi.ui.MessageType.ERROR ? com.intellij.notification.NotificationType.ERROR : type == com.intellij.openapi.ui.MessageType.WARNING ? com.intellij.notification.NotificationType.WARNING : com.intellij.notification.NotificationType.INFORMATION;
    com.intellij.notification.Notifications.Bus.notify(new com.intellij.notification.Notification("Progress", "Progress", text, severity));
  }
}
