package java.awt.desktop;

public interface AppForegroundListener extends SystemEventListener {
    void appRaisedToForeground(AppForegroundEvent event);
    void appMovedToBackground(AppForegroundEvent event);
}
