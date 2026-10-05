package java.awt.dnd;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.io.Serializable;
import java.util.TooManyListenersException;

public class DragSourceContext implements DragSourceListener, DragSourceMotionListener, Serializable {
    protected static final int DEFAULT = 0;
    protected static final int ENTER = 1;
    protected static final int OVER = 2;
    protected static final int CHANGED = 3;
    private final DragGestureEvent trigger;
    private final Transferable transferable;
    private DragSourceListener listener;
    private Cursor cursor;

    public DragSourceContext(DragGestureEvent trigger, Cursor cursor, Image image, Point offset, Transferable transferable, DragSourceListener listener) {
        if (trigger == null || transferable == null) {
            throw new NullPointerException("trigger and transferable are required");
        }
        if ((image == null) != (offset == null)) {
            throw new NullPointerException("drag image and offset must be supplied together");
        }
        this.trigger = trigger;
        this.cursor = cursor;
        this.transferable = transferable;
        this.listener = listener;
    }

    public DragSource getDragSource() {
        return trigger.getDragSource();
    }

    public Component getComponent() {
        return trigger.getComponent();
    }

    public DragGestureEvent getTrigger() {
        return trigger;
    }

    public int getSourceActions() {
        return trigger.getSourceAsDragGestureRecognizer().getSourceActions();
    }

    public synchronized void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }

    public Cursor getCursor() {
        return cursor;
    }

    public synchronized void addDragSourceListener(DragSourceListener listener) throws TooManyListenersException {
        if (listener == null) {
            return;
        }
        if (this.listener != null) {
            throw new TooManyListenersException();
        }
        this.listener = listener;
    }

    public synchronized void removeDragSourceListener(DragSourceListener listener) {
        if (this.listener != listener) {
            throw new IllegalArgumentException("listener mismatch");
        }
        this.listener = null;
    }

    public void transferablesFlavorsChanged() {
    }

    @Override
    public void dragEnter(DragSourceDragEvent event) {
        if (listener != null) {
            listener.dragEnter(event);
        }
        getDragSource().fireDragEnter(event);
    }

    @Override
    public void dragOver(DragSourceDragEvent event) {
        if (listener != null) {
            listener.dragOver(event);
        }
        getDragSource().fireDragOver(event);
    }

    @Override
    public void dragExit(DragSourceEvent event) {
        if (listener != null) {
            listener.dragExit(event);
        }
        getDragSource().fireDragExit(event);
    }

    @Override
    public void dropActionChanged(DragSourceDragEvent event) {
        if (listener != null) {
            listener.dropActionChanged(event);
        }
        getDragSource().fireDropActionChanged(event);
    }

    @Override
    public void dragDropEnd(DragSourceDropEvent event) {
        if (listener != null) {
            listener.dragDropEnd(event);
        }
        getDragSource().fireDragDropEnd(event);
    }

    @Override
    public void dragMouseMoved(DragSourceDragEvent event) {
        getDragSource().fireDragMouseMoved(event);
    }

    public Transferable getTransferable() {
        return transferable;
    }
}
