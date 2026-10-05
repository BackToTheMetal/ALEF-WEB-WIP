package java.awt.event;

public abstract class ContainerAdapter implements ContainerListener {
    protected ContainerAdapter() {
    }

    @Override
    public void componentAdded(ContainerEvent event) {
        // Default adapter implementation intentionally ignores the event.
    }

    @Override
    public void componentRemoved(ContainerEvent event) {
        // Default adapter implementation intentionally ignores the event.
    }
}
