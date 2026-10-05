package com.intellij.notification;
public class Notification {
  private final String group, title, content;
  private final NotificationType type;
  private boolean expired;
  public Notification(String group, String title, String content, NotificationType type) { this.group = group; this.title = title; this.content = content; this.type = type; }
  public String getGroupId() { return group; }
  public String getTitle() { return title; }
  public String getContent() { return content; }
  public NotificationType getType() { return type; }
  public void expire() { expired = true; }
  public boolean isExpired() { return expired; }
  public void notify(com.intellij.openapi.project.Project project) { Notifications.Bus.notify(this, project); }
}
