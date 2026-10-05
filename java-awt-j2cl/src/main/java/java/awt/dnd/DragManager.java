package java.awt.dnd;

import org.webawt.internal.peer.PeerDragEvent;

final class DragManager {
    private static DragSourceContext activeContext;
    static { org.webawt.internal.peer.WebAwtRuntime.addShutdownHook(() -> activeContext = null); }

    private DragManager() {
    }

    static synchronized void begin(DragSourceContext context) {
        if (activeContext != null) {
            throw new InvalidDnDOperationException("A drag operation is already active");
        }
        activeContext = context;
    }

    static synchronized DragSourceContext getActiveContext() {
        return activeContext;
    }

    static synchronized void complete(int action, boolean success, int x, int y) {
        DragSourceContext context = activeContext;
        activeContext = null;
        if (context != null) {
            context.dragDropEnd(new DragSourceDropEvent(context, action, success, x, y));
        }
    }

    static synchronized void handlePeerEvent(int type, int userAction, int targetActions, int modifiers, int x, int y, boolean success) {
        DragSourceContext context = activeContext;
        if (context == null) {
            return;
        }
        if (type == PeerDragEvent.DRAG_ENTER) {
            context.dragEnter(new DragSourceDragEvent(context, userAction, targetActions, modifiers, x, y));
        } else if (type == PeerDragEvent.DRAG_OVER || type == PeerDragEvent.DROP_ACTION_CHANGED) {
            DragSourceDragEvent event = new DragSourceDragEvent(context, userAction, targetActions, modifiers, x, y);
            if (type == PeerDragEvent.DROP_ACTION_CHANGED) {
                context.dropActionChanged(event);
            } else {
                context.dragOver(event);
                context.dragMouseMoved(event);
            }
        } else if (type == PeerDragEvent.DRAG_EXIT) {
            context.dragExit(new DragSourceEvent(context, x, y));
        } else if (type == PeerDragEvent.DRAG_END) {
            complete(userAction, success, x, y);
        }
    }
}
