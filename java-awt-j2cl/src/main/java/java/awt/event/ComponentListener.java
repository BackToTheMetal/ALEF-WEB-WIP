package java.awt.event;

import java.util.EventListener;

public interface ComponentListener extends EventListener {
    void componentResized(ComponentEvent event);

    void componentMoved(ComponentEvent event);

    void componentShown(ComponentEvent event);

    void componentHidden(ComponentEvent event);
}
