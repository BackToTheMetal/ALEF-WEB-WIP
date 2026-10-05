package java.awt.dnd;

public class DragSourceDropEvent extends DragSourceEvent {
    private final int dropAction;
    private final boolean success;

    public DragSourceDropEvent(DragSourceContext context, int dropAction, boolean success) {
        super(context);
        this.dropAction = dropAction;
        this.success = success;
    }

    public DragSourceDropEvent(DragSourceContext context, int dropAction, boolean success, int x, int y) {
        super(context, x, y);
        this.dropAction = dropAction;
        this.success = success;
    }

    public DragSourceDropEvent(DragSourceContext context) {
        this(context, DnDConstants.ACTION_NONE, false);
    }

    public boolean getDropSuccess() {
        return success;
    }

    public int getDropAction() {
        return dropAction;
    }
}
