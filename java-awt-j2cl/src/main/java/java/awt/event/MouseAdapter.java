package java.awt.event;

public abstract class MouseAdapter implements MouseListener, MouseWheelListener, MouseMotionListener {
    protected MouseAdapter() {
    }

    public void mouseClicked(MouseEvent event) {
        // Default adapter implementation.
    }

    public void mousePressed(MouseEvent event) {
        // Default adapter implementation.
    }

    public void mouseReleased(MouseEvent event) {
        // Default adapter implementation.
    }

    public void mouseEntered(MouseEvent event) {
        // Default adapter implementation.
    }

    public void mouseExited(MouseEvent event) {
        // Default adapter implementation.
    }

    public void mouseWheelMoved(MouseWheelEvent event) {
        // Default adapter implementation.
    }

    public void mouseDragged(MouseEvent event) {
        // Default adapter implementation.
    }

    public void mouseMoved(MouseEvent event) {
        // Default adapter implementation.
    }
}
