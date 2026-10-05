package java.awt.event;

import java.util.EventListener;

public interface WindowListener extends EventListener {
    void windowOpened(WindowEvent event);
    void windowClosing(WindowEvent event);
    void windowClosed(WindowEvent event);
    void windowIconified(WindowEvent event);
    void windowDeiconified(WindowEvent event);
    void windowActivated(WindowEvent event);
    void windowDeactivated(WindowEvent event);
}
