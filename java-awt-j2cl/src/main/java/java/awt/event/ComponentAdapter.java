package java.awt.event;

public abstract class ComponentAdapter implements ComponentListener {
    protected ComponentAdapter() {
    }

    @Override
    public void componentResized(ComponentEvent event) {
        // Default adapter implementation intentionally ignores the event.
    }

    @Override
    public void componentMoved(ComponentEvent event) {
        // Default adapter implementation intentionally ignores the event.
    }

    @Override
    public void componentShown(ComponentEvent event) {
        // Default adapter implementation intentionally ignores the event.
    }

    @Override
    public void componentHidden(ComponentEvent event) {
        // Default adapter implementation intentionally ignores the event.
    }
}
