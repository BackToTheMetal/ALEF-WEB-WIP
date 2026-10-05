package java.awt.event;

import java.awt.Window;

public class WindowEvent extends ComponentEvent {
    public static final int WINDOW_FIRST = 200;
    public static final int WINDOW_OPENED = WINDOW_FIRST;
    public static final int WINDOW_CLOSING = WINDOW_FIRST + 1;
    public static final int WINDOW_CLOSED = WINDOW_FIRST + 2;
    public static final int WINDOW_ICONIFIED = WINDOW_FIRST + 3;
    public static final int WINDOW_DEICONIFIED = WINDOW_FIRST + 4;
    public static final int WINDOW_ACTIVATED = WINDOW_FIRST + 5;
    public static final int WINDOW_DEACTIVATED = WINDOW_FIRST + 6;
    public static final int WINDOW_GAINED_FOCUS = WINDOW_FIRST + 7;
    public static final int WINDOW_LOST_FOCUS = WINDOW_FIRST + 8;
    public static final int WINDOW_STATE_CHANGED = WINDOW_FIRST + 9;
    public static final int WINDOW_LAST = WINDOW_STATE_CHANGED;
    private static final long serialVersionUID = 1L;
    private final Window opposite;
    private final int oldState;
    private final int newState;

    public WindowEvent(Window source, int id, Window opposite, int oldState, int newState) {
        super(source, id);
        this.opposite = opposite;
        this.oldState = oldState;
        this.newState = newState;
    }

    public WindowEvent(Window source, int id, Window opposite) {
        this(source, id, opposite, 0, 0);
    }

    public WindowEvent(Window source, int id, int oldState, int newState) {
        this(source, id, null, oldState, newState);
    }

    public WindowEvent(Window source, int id) {
        this(source, id, null, 0, 0);
    }

    public Window getWindow() {
        return source instanceof Window ? (Window) source : null;
    }

    public Window getOppositeWindow() {
        return opposite;
    }

    public int getOldState() {
        return oldState;
    }

    public int getNewState() {
        return newState;
    }

    @Override
    public String paramString() {
        String type = eventName();
        return type + ",opposite=" + opposite + ",oldState=" + oldState + ",newState=" + newState;
    }

    private String eventName() {
        switch (id) {
            case WINDOW_OPENED: return "WINDOW_OPENED";
            case WINDOW_CLOSING: return "WINDOW_CLOSING";
            case WINDOW_CLOSED: return "WINDOW_CLOSED";
            case WINDOW_ICONIFIED: return "WINDOW_ICONIFIED";
            case WINDOW_DEICONIFIED: return "WINDOW_DEICONIFIED";
            case WINDOW_ACTIVATED: return "WINDOW_ACTIVATED";
            case WINDOW_DEACTIVATED: return "WINDOW_DEACTIVATED";
            case WINDOW_GAINED_FOCUS: return "WINDOW_GAINED_FOCUS";
            case WINDOW_LOST_FOCUS: return "WINDOW_LOST_FOCUS";
            case WINDOW_STATE_CHANGED: return "WINDOW_STATE_CHANGED";
            default: return "unknown type";
        }
    }
}
