package java.awt.event;

public abstract class KeyAdapter implements KeyListener {
    protected KeyAdapter() {
    }

    public void keyTyped(KeyEvent event) {
        // Default adapter implementation.
    }

    public void keyPressed(KeyEvent event) {
        // Default adapter implementation.
    }

    public void keyReleased(KeyEvent event) {
        // Default adapter implementation.
    }
}
