package java.awt.dnd;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

public class DragGestureEvent extends EventObject {
    private final int dragAction;
    private final Point dragOrigin;
    private final List<InputEvent> events;

    public DragGestureEvent(DragGestureRecognizer recognizer, int dragAction, Point dragOrigin, List<? extends InputEvent> events) {
        super(recognizer);
        if (dragOrigin == null || events == null || events.isEmpty()) {
            throw new IllegalArgumentException("origin and events are required");
        }
        this.dragAction = dragAction;
        this.dragOrigin = new Point(dragOrigin);
        this.events = new ArrayList<>(events);
    }

    public DragGestureRecognizer getSourceAsDragGestureRecognizer() {
        return (DragGestureRecognizer) getSource();
    }

    public Component getComponent() {
        return getSourceAsDragGestureRecognizer().getComponent();
    }

    public DragSource getDragSource() {
        return getSourceAsDragGestureRecognizer().getDragSource();
    }

    public Point getDragOrigin() {
        return new Point(dragOrigin);
    }

    public Iterator<InputEvent> iterator() {
        return events.iterator();
    }

    public Object[] toArray() {
        return events.toArray();
    }

    public Object[] toArray(Object[] array) {
        return events.toArray(array);
    }

    public int getDragAction() {
        return dragAction;
    }

    public InputEvent getTriggerEvent() {
        return events.get(0);
    }

    public void startDrag(Cursor cursor, Transferable transferable) throws InvalidDnDOperationException {
        getDragSource().startDrag(this, cursor, transferable, null);
    }

    public void startDrag(Cursor cursor, Transferable transferable, DragSourceListener listener) throws InvalidDnDOperationException {
        getDragSource().startDrag(this, cursor, transferable, listener);
    }

    public void startDrag(Cursor cursor, Image image, Point offset, Transferable transferable, DragSourceListener listener) throws InvalidDnDOperationException {
        getDragSource().startDrag(this, cursor, image, offset, transferable, listener);
    }
}
