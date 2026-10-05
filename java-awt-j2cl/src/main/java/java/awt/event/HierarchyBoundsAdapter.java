package java.awt.event;

public abstract class HierarchyBoundsAdapter implements HierarchyBoundsListener {
    protected HierarchyBoundsAdapter() {
    }

    @Override
    public void ancestorMoved(HierarchyEvent event) {
        // Default adapter implementation intentionally ignores the event.
    }

    @Override
    public void ancestorResized(HierarchyEvent event) {
        // Default adapter implementation intentionally ignores the event.
    }
}
