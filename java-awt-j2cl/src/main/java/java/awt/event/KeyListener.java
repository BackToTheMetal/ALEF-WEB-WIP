package java.awt.event;

import java.util.EventListener;

public interface KeyListener extends EventListener {
    void keyTyped(KeyEvent event);
    void keyPressed(KeyEvent event);
    void keyReleased(KeyEvent event);
}
