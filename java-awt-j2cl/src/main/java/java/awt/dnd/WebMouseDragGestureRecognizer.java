package java.awt.dnd;

import java.awt.Component;

final class WebMouseDragGestureRecognizer extends MouseDragGestureRecognizer {
    WebMouseDragGestureRecognizer(DragSource dragSource, Component component, int sourceActions, DragGestureListener listener) {
        super(dragSource, component, sourceActions, listener);
    }
}
