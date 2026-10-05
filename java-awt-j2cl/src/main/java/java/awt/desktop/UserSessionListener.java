package java.awt.desktop;

public interface UserSessionListener extends SystemEventListener {
    void userSessionDeactivated(UserSessionEvent event);
    void userSessionActivated(UserSessionEvent event);
}
