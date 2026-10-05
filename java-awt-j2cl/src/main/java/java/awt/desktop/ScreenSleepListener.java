package java.awt.desktop;

public interface ScreenSleepListener extends SystemEventListener {
    void screenAboutToSleep(ScreenSleepEvent event);
    void screenAwoke(ScreenSleepEvent event);
}
