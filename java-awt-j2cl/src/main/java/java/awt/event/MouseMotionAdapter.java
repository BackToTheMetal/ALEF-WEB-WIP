package java.awt.event;

public abstract class MouseMotionAdapter implements MouseMotionListener {
    protected MouseMotionAdapter() {
    }

    public void mouseDragged(MouseEvent event) {
        // Default adapter implementation.
    }

    public void mouseMoved(MouseEvent event) {
        // Default adapter implementation.
    }
}
