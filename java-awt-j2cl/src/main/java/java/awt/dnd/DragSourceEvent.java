package java.awt.dnd;

import java.awt.Point;
import java.util.EventObject;

public class DragSourceEvent extends EventObject {
    private final int x;
    private final int y;
    private final boolean locationSpecified;

    public DragSourceEvent(DragSourceContext context) {
        super(context);
        x = 0;
        y = 0;
        locationSpecified = false;
    }

    public DragSourceEvent(DragSourceContext context, int x, int y) {
        super(context);
        this.x = x;
        this.y = y;
        locationSpecified = true;
    }

    public DragSourceContext getDragSourceContext() {
        return (DragSourceContext) getSource();
    }

    public Point getLocation() {
        return locationSpecified ? new Point(x, y) : null;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
