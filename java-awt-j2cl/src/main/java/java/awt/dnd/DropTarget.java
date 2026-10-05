package java.awt.dnd;

import java.awt.Component;
import java.awt.datatransfer.FlavorMap;
import java.io.Serializable;
import java.util.TooManyListenersException;
import org.webawt.internal.peer.PeerDragEvent;
import org.webawt.internal.peer.WebAwtRuntime;

public class DropTarget implements DropTargetListener, Serializable {
    private Component component;
    private int defaultActions;
    private DropTargetListener listener;
    private boolean active;
    private FlavorMap flavorMap;
    private final DropTargetContext context;

    public DropTarget(Component component, int actions, DropTargetListener listener, boolean active, FlavorMap flavorMap) throws java.awt.HeadlessException {
        defaultActions = validActions(actions);
        this.listener = listener;
        this.active = active;
        this.flavorMap = flavorMap == null ? new DefaultFlavorMap() : flavorMap;
        context = createDropTargetContext();
        setComponent(component);
    }

    public DropTarget(Component component, int actions, DropTargetListener listener, boolean active) throws java.awt.HeadlessException {
        this(component, actions, listener, active, null);
    }

    public DropTarget() throws java.awt.HeadlessException {
        this(null, DnDConstants.ACTION_COPY_OR_MOVE, null, true, null);
    }

    public DropTarget(Component component, DropTargetListener listener) throws java.awt.HeadlessException {
        this(component, DnDConstants.ACTION_COPY_OR_MOVE, listener, true, null);
    }

    public DropTarget(Component component, int actions, DropTargetListener listener) throws java.awt.HeadlessException {
        this(component, actions, listener, true, null);
    }

    public synchronized void setComponent(Component component) {
        if (this.component == component) {
            return;
        }
        Component previous = this.component;
        this.component = component;
        if (previous != null && previous.getDropTarget() == this) {
            previous.setDropTarget(null);
        }
        if (component != null && component.getDropTarget() != this) {
            component.setDropTarget(this);
        }
    }

    public synchronized Component getComponent() {
        return component;
    }

    public void setDefaultActions(int actions) {
        defaultActions = validActions(actions);
    }

    public int getDefaultActions() {
        return defaultActions;
    }

    public synchronized void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public synchronized void addDropTargetListener(DropTargetListener listener) throws TooManyListenersException {
        if (listener == null) {
            return;
        }
        if (this.listener != null) {
            throw new TooManyListenersException();
        }
        this.listener = listener;
    }

    public synchronized void removeDropTargetListener(DropTargetListener listener) {
        if (this.listener != listener) {
            throw new IllegalArgumentException("listener mismatch");
        }
        this.listener = null;
    }

    @Override
    public synchronized void dragEnter(DropTargetDragEvent event) {
        if (active && listener != null) {
            listener.dragEnter(event);
        }
    }

    @Override
    public synchronized void dragOver(DropTargetDragEvent event) {
        if (active && listener != null) {
            listener.dragOver(event);
        }
    }

    @Override
    public synchronized void dropActionChanged(DropTargetDragEvent event) {
        if (active && listener != null) {
            listener.dropActionChanged(event);
        }
    }

    @Override
    public synchronized void dragExit(DropTargetEvent event) {
        if (active && listener != null) {
            listener.dragExit(event);
        }
    }

    @Override
    public synchronized void drop(DropTargetDropEvent event) {
        if (active && listener != null) {
            listener.drop(event);
        } else {
            event.rejectDrop();
            event.dropComplete(false);
        }
    }

    public FlavorMap getFlavorMap() {
        return flavorMap;
    }

    public void setFlavorMap(FlavorMap flavorMap) {
        this.flavorMap = flavorMap == null ? new DefaultFlavorMap() : flavorMap;
    }

    public void addNotify() {
    }

    public void removeNotify() {
    }

    public DropTargetContext getDropTargetContext() {
        return context;
    }

    public void handlePeerDragEvent(PeerDragEvent event) {
        if (!active) {
            return;
        }
        int type = event.getType();
        if (type == PeerDragEvent.DRAG_EXIT) {
            dragExit(new DropTargetEvent(context));
            WebAwtRuntime.notifyDragSource(type, event.getDropAction(), defaultActions, event.getModifiers(), event.getX(), event.getY(), false);
            return;
        }
        prepareContext(event);
        if (type == PeerDragEvent.DROP) {
            drop(new DropTargetDropEvent(context, new java.awt.Point(event.getX(), event.getY()), event.getDropAction(), event.getSourceActions(), WebAwtRuntime.isLocalDragActive()));
        } else {
            DropTargetDragEvent dragEvent = new DropTargetDragEvent(context, new java.awt.Point(event.getX(), event.getY()), event.getDropAction(), event.getSourceActions());
            if (type == PeerDragEvent.DRAG_ENTER) {
                dragEnter(dragEvent);
            } else if (type == PeerDragEvent.DROP_ACTION_CHANGED) {
                dropActionChanged(dragEvent);
            } else {
                dragOver(dragEvent);
            }
        }
        event.accept(context.getDropAction());
        WebAwtRuntime.notifyDragSource(type, context.getDropAction(), defaultActions, event.getModifiers(), event.getX(), event.getY(), false);
    }

    private void prepareContext(PeerDragEvent event) {
        if (!WebAwtRuntime.isLocalDragActive()) {
            context.begin(new SnapshotTransferable(event.getMimeTypes(), event.getData()), event.getSourceActions(), event.getDropAction(), false);
        }
    }

    private static int validActions(int actions) {
        return actions & (DnDConstants.ACTION_COPY_OR_MOVE | DnDConstants.ACTION_LINK);
    }
    protected DropTargetContext createDropTargetContext() { return new DropTargetContext(this); }
}
