package java.awt.event;

import java.util.EventListener;

public interface FocusListener extends EventListener {
    void focusGained(FocusEvent event);

    void focusLost(FocusEvent event);
}
