package java.awt.event;

import java.util.EventListener;

public interface MouseMotionListener extends EventListener {
    void mouseDragged(MouseEvent event);
    void mouseMoved(MouseEvent event);
}
