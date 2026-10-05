package com.intellij.notification;
import com.intellij.openapi.project.Project;
public interface Notifications {
  final class Bus {
    private static java.util.function.BiConsumer<Notification, Project> sink = (notification, project) -> System.err.println(notification.getTitle() + ": " + notification.getContent());
    private Bus() {}
    public static void setBrowserSink(java.util.function.BiConsumer<Notification, Project> value) { sink = java.util.Objects.requireNonNull(value); }
    public static void notify(Notification notification) { notify(notification, null); }
    public static void notify(Notification notification, Project project) { if (!notification.isExpired()) sink.accept(notification, project); }
  }
}
