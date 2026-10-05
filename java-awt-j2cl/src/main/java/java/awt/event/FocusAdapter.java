package java.awt.event;

public abstract class FocusAdapter implements FocusListener {
    protected FocusAdapter() {
    }

    @Override
    public void focusGained(FocusEvent event) {
        // Default adapter implementation intentionally ignores the event.
    }

    @Override
    public void focusLost(FocusEvent event) {
        // Default adapter implementation intentionally ignores the event.
    }
}
