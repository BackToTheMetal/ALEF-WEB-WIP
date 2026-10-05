package java.awt.event;

import java.util.EventListener;

public interface HierarchyListener extends EventListener {
    void hierarchyChanged(HierarchyEvent event);
}
