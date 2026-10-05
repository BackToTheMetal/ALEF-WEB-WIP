package java.awt.event;

import java.util.EventListener;

public interface HierarchyBoundsListener extends EventListener {
    void ancestorMoved(HierarchyEvent event);
    void ancestorResized(HierarchyEvent event);
}
