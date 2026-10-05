package java.awt.event;

import java.util.EventListener;

public interface WindowFocusListener extends EventListener {
    void windowGainedFocus(WindowEvent event);
    void windowLostFocus(WindowEvent event);
}
