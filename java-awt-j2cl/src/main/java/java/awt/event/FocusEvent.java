package java.awt.event;

import java.awt.Component;

public class FocusEvent extends ComponentEvent {
    public enum Cause {
        UNKNOWN,
        MOUSE_EVENT,
        TRAVERSAL,
        TRAVERSAL_UP,
        TRAVERSAL_DOWN,
        TRAVERSAL_FORWARD,
        TRAVERSAL_BACKWARD,
        ROLLBACK,
        UNEXPECTED,
        ACTIVATION,
        CLEAR_GLOBAL_FOCUS_OWNER
    }

    public static final int FOCUS_FIRST = 1004;
    public static final int FOCUS_LAST = 1005;
    public static final int FOCUS_GAINED = 1004;
    public static final int FOCUS_LOST = 1005;
    private static final long serialVersionUID = 1L;
    private final boolean temporary;
    private final Component opposite;
    private final Cause cause;

    public FocusEvent(Component source, int id, boolean temporary, Component opposite) {
        this(source, id, temporary, opposite, Cause.UNKNOWN);
    }

    public FocusEvent(Component source, int id, boolean temporary, Component opposite, Cause cause) {
        super(source, id);
        if (cause == null) {
            throw new IllegalArgumentException("null cause");
        }
        this.temporary = temporary;
        this.opposite = opposite;
        this.cause = cause;
    }

    public FocusEvent(Component source, int id, boolean temporary) {
        this(source, id, temporary, null);
    }

    public FocusEvent(Component source, int id) {
        this(source, id, false);
    }

    public boolean isTemporary() {
        return temporary;
    }

    public Component getOppositeComponent() {
        return opposite;
    }

    @Override
    public String paramString() {
        String type = "unknown type";
        if (id == FOCUS_GAINED) {
            type = "FOCUS_GAINED";
        } else if (id == FOCUS_LOST) {
            type = "FOCUS_LOST";
        }
        return type + (temporary ? ",temporary" : ",permanent") + ",opposite=" + getOppositeComponent() + ",cause=" + cause;
    }

    public final Cause getCause() {
        return cause;
    }
}
