package java.awt.dnd;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.TooManyListenersException;

public abstract class DragGestureRecognizer implements Serializable {
    protected DragSource dragSource;
    protected Component component;
    protected transient DragGestureListener dragGestureListener;
    protected int sourceActions;
    protected ArrayList<InputEvent> events = new ArrayList<>();

    protected DragGestureRecognizer(DragSource dragSource, Component component, int sourceActions, DragGestureListener listener) {
        if (dragSource == null) {
            throw new IllegalArgumentException("dragSource");
        }
        this.dragSource = dragSource;
        this.sourceActions = validActions(sourceActions);
        this.dragGestureListener = listener;
        setComponent(component);
    }

    protected DragGestureRecognizer(DragSource dragSource, Component component, int sourceActions) {
        this(dragSource, component, sourceActions, null);
    }

    protected DragGestureRecognizer(DragSource dragSource, Component component) {
        this(dragSource, component, DnDConstants.ACTION_NONE, null);
    }

    protected DragGestureRecognizer(DragSource dragSource) {
        this(dragSource, null, DnDConstants.ACTION_NONE, null);
    }

    public DragSource getDragSource() {
        return dragSource;
    }

    public synchronized Component getComponent() {
        return component;
    }

    public synchronized void setComponent(Component component) {
        if (this.component == component) {
            return;
        }
        if (this.component != null) {
            unregisterListeners();
        }
        this.component = component;
        if (component != null) {
            registerListeners();
        }
    }

    public synchronized int getSourceActions() {
        return sourceActions;
    }

    public synchronized void setSourceActions(int actions) {
        sourceActions = validActions(actions);
    }

    public InputEvent getTriggerEvent() {
        return events.isEmpty() ? null : events.get(0);
    }

    public void resetRecognizer() {
        events.clear();
    }

    public synchronized void addDragGestureListener(DragGestureListener listener) throws TooManyListenersException {
        if (listener == null) {
            return;
        }
        if (dragGestureListener != null) {
            throw new TooManyListenersException();
        }
        dragGestureListener = listener;
    }

    public synchronized void removeDragGestureListener(DragGestureListener listener) {
        if (dragGestureListener != listener) {
            throw new IllegalArgumentException("listener mismatch");
        }
        dragGestureListener = null;
    }

    protected abstract void registerListeners();

    protected abstract void unregisterListeners();

    protected synchronized void fireDragGestureRecognized(int dragAction, Point origin) {
        if (dragGestureListener != null) {
            dragGestureListener.dragGestureRecognized(new DragGestureEvent(this, dragAction, origin, new ArrayList<>(events)));
        }
        resetRecognizer();
    }

    protected synchronized void appendEvent(InputEvent event) {
        if (event != null) {
            events.add(event);
        }
    }

    private static int validActions(int actions) {
        return actions & (DnDConstants.ACTION_COPY_OR_MOVE | DnDConstants.ACTION_LINK);
    }
}
