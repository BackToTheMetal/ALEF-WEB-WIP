package java.awt;

import java.util.EventObject;

public abstract class AWTEvent extends EventObject {
    private static final long serialVersionUID = -1825314779160409405L;
    public static final long COMPONENT_EVENT_MASK = 1L;
    public static final long CONTAINER_EVENT_MASK = 2L;
    public static final long FOCUS_EVENT_MASK = 4L;
    public static final long KEY_EVENT_MASK = 8L;
    public static final long MOUSE_EVENT_MASK = 16L;
    public static final long MOUSE_MOTION_EVENT_MASK = 32L;
    public static final long WINDOW_EVENT_MASK = 64L;
    public static final long ACTION_EVENT_MASK = 128L;
    public static final long ADJUSTMENT_EVENT_MASK = 256L;
    public static final long ITEM_EVENT_MASK = 512L;
    public static final long TEXT_EVENT_MASK = 1024L;
    public static final long INPUT_METHOD_EVENT_MASK = 2048L;
    public static final long PAINT_EVENT_MASK = 8192L;
    public static final long INVOCATION_EVENT_MASK = 16384L;
    public static final long HIERARCHY_EVENT_MASK = 32768L;
    public static final long HIERARCHY_BOUNDS_EVENT_MASK = 65536L;
    public static final long MOUSE_WHEEL_EVENT_MASK = 131072L;
    public static final long WINDOW_STATE_EVENT_MASK = 262144L;
    public static final long WINDOW_FOCUS_EVENT_MASK = 524288L;
    public static final int RESERVED_ID_MAX = 1999;
    protected int id;
    protected boolean consumed;

    public AWTEvent(Event event) {
        this(event.target, event.id);
    }

    public AWTEvent(Object source, int id) {
        super(source);
        this.id = id;
    }

    public void setSource(Object source) {
        if (source == null) {
            throw new IllegalArgumentException("null source");
        }
        this.source = source;
    }

    public int getID() {
        return id;
    }

    @Override
    public String toString() {
        return getClass().getName() + "[" + paramString() + "] on " + source;
    }

    public String paramString() {
        return "";
    }

    protected void consume() {
        consumed = true;
    }

    protected boolean isConsumed() {
        return consumed;
    }
}
