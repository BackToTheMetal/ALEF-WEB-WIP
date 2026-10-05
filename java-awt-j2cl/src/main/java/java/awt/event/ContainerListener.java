package java.awt.event;

import java.util.EventListener;

public interface ContainerListener extends EventListener {
    void componentAdded(ContainerEvent event);

    void componentRemoved(ContainerEvent event);
}
