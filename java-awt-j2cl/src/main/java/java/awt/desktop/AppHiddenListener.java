package java.awt.desktop;

public interface AppHiddenListener extends SystemEventListener {
    void appHidden(AppHiddenEvent event);
    void appUnhidden(AppHiddenEvent event);
}
