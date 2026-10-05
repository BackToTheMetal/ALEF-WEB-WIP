package java.awt.event;

public abstract class WindowAdapter implements WindowListener, WindowStateListener, WindowFocusListener {
    protected WindowAdapter() {
    }

    @Override
    public void windowOpened(WindowEvent event) {
        // Default adapter implementation intentionally ignores the event.
    }

    @Override
    public void windowClosing(WindowEvent event) {
        // Default adapter implementation intentionally ignores the event.
    }

    @Override
    public void windowClosed(WindowEvent event) {
        // Default adapter implementation intentionally ignores the event.
    }

    @Override
    public void windowIconified(WindowEvent event) {
        // Default adapter implementation intentionally ignores the event.
    }

    @Override
    public void windowDeiconified(WindowEvent event) {
        // Default adapter implementation intentionally ignores the event.
    }

    @Override
    public void windowActivated(WindowEvent event) {
        // Default adapter implementation intentionally ignores the event.
    }

    @Override
    public void windowDeactivated(WindowEvent event) {
        // Default adapter implementation intentionally ignores the event.
    }

    @Override
    public void windowStateChanged(WindowEvent event) {
        // Default adapter implementation intentionally ignores the event.
    }

    @Override
    public void windowGainedFocus(WindowEvent event) {
        // Default adapter implementation intentionally ignores the event.
    }

    @Override
    public void windowLostFocus(WindowEvent event) {
        // Default adapter implementation intentionally ignores the event.
    }
}
