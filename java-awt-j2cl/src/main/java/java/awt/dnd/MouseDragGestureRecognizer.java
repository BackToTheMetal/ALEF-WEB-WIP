package java.awt.dnd;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import org.webawt.internal.peer.PeerDragGestureStarter;

public abstract class MouseDragGestureRecognizer extends DragGestureRecognizer implements MouseListener, MouseMotionListener, PeerDragGestureStarter {
    private Point origin;

    protected MouseDragGestureRecognizer(DragSource dragSource, Component component, int sourceActions, DragGestureListener listener) {
        super(dragSource, component, sourceActions, listener);
    }

    protected MouseDragGestureRecognizer(DragSource dragSource, Component component, int sourceActions) {
        super(dragSource, component, sourceActions);
    }

    protected MouseDragGestureRecognizer(DragSource dragSource, Component component) {
        super(dragSource, component);
    }

    protected MouseDragGestureRecognizer(DragSource dragSource) {
        super(dragSource);
    }

    @Override
    protected void registerListeners() {
        component.addMouseListener(this);
        component.addMouseMotionListener(this);
    }

    @Override
    protected void unregisterListeners() {
        component.removeMouseListener(this);
        component.removeMouseMotionListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent event) {
    }

    @Override
    public void mousePressed(MouseEvent event) {
        resetRecognizer();
        origin = event.getPoint();
        appendEvent(event);
    }

    @Override
    public void mouseReleased(MouseEvent event) {
        resetRecognizer();
        origin = null;
    }

    @Override
    public void mouseEntered(MouseEvent event) {
    }

    @Override
    public void mouseExited(MouseEvent event) {
    }

    @Override
    public void mouseDragged(MouseEvent event) {
        if (origin == null) {
            return;
        }
        appendEvent(event);
        int deltaX = Math.abs(event.getX() - origin.x);
        int deltaY = Math.abs(event.getY() - origin.y);
        if (Math.max(deltaX, deltaY) >= DragSource.getDragThreshold()) {
            fireDragGestureRecognized(preferredAction(event), origin);
            origin = null;
        }
    }

    @Override
    public void mouseMoved(MouseEvent event) {
    }

    @Override
    public void startPeerDrag(int x, int y, int modifiers) {
        if (origin == null) {
            origin = new Point(x, y);
            appendEvent(new MouseEvent(component, MouseEvent.MOUSE_PRESSED, 0L, modifiers, x, y, 1, false, MouseEvent.BUTTON1));
        }
        MouseEvent event = new MouseEvent(component, MouseEvent.MOUSE_DRAGGED, 0L, modifiers, x, y, 0, false, MouseEvent.NOBUTTON);
        appendEvent(event);
        fireDragGestureRecognized(preferredAction(event), origin);
        origin = null;
    }

    private int preferredAction(MouseEvent event) {
        int actions = getSourceActions();
        if (event.isControlDown() && (actions & DnDConstants.ACTION_COPY) != 0) {
            return DnDConstants.ACTION_COPY;
        }
        if ((actions & DnDConstants.ACTION_MOVE) != 0) {
            return DnDConstants.ACTION_MOVE;
        }
        if ((actions & DnDConstants.ACTION_COPY) != 0) {
            return DnDConstants.ACTION_COPY;
        }
        return (actions & DnDConstants.ACTION_LINK) != 0 ? DnDConstants.ACTION_LINK : DnDConstants.ACTION_NONE;
    }
}
