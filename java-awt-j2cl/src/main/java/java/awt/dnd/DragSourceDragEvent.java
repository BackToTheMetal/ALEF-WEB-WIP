package java.awt.dnd;

public class DragSourceDragEvent extends DragSourceEvent {
    private final int targetActions;
    private final int userAction;
    private final int gestureModifiers;

    public DragSourceDragEvent(DragSourceContext context, int userAction, int targetActions, int gestureModifiers) {
        super(context);
        this.userAction = userAction;
        this.targetActions = targetActions;
        this.gestureModifiers = gestureModifiers;
    }

    public DragSourceDragEvent(DragSourceContext context, int userAction, int targetActions, int gestureModifiers, int x, int y) {
        super(context, x, y);
        this.userAction = userAction;
        this.targetActions = targetActions;
        this.gestureModifiers = gestureModifiers;
    }

    public int getTargetActions() {
        return targetActions;
    }

    @Deprecated
    public int getGestureModifiers() {
        return gestureModifiers & 63;
    }

    public int getGestureModifiersEx() {
        return gestureModifiers & ~63;
    }

    public int getUserAction() {
        return userAction;
    }

    public int getDropAction() {
        return userAction & targetActions & getDragSourceContext().getSourceActions();
    }
}
