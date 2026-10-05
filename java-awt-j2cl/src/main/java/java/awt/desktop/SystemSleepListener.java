package java.awt.desktop;

public interface SystemSleepListener extends SystemEventListener {
    void systemAboutToSleep(SystemSleepEvent event);
    void systemAwoke(SystemSleepEvent event);
}
