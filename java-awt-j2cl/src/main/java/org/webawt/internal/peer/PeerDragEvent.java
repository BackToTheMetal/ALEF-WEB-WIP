package org.webawt.internal.peer;

public final class PeerDragEvent {
    public static final int DRAG_START = 1;
    public static final int DRAG_ENTER = 2;
    public static final int DRAG_OVER = 3;
    public static final int DROP_ACTION_CHANGED = 4;
    public static final int DRAG_EXIT = 5;
    public static final int DROP = 6;
    public static final int DRAG_END = 7;
    private final int type;
    private final int x;
    private final int y;
    private final int modifiers;
    private int sourceActions;
    private int dropAction;
    private int acceptedAction;
    private String[] mimeTypes;
    private String[] data;
    private boolean localTransfer;

    public PeerDragEvent(int type, int x, int y, int modifiers, int sourceActions, int dropAction, String[] mimeTypes, String[] data) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.modifiers = modifiers;
        this.sourceActions = sourceActions;
        this.dropAction = dropAction;
        this.mimeTypes = copy(mimeTypes);
        this.data = copy(data);
    }

    public int getType() {
        return type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getModifiers() {
        return modifiers;
    }

    public int getSourceActions() {
        return sourceActions;
    }

    public int getDropAction() {
        return dropAction;
    }

    public int getAcceptedAction() {
        return acceptedAction;
    }

    public String[] getMimeTypes() {
        return copy(mimeTypes);
    }

    public String[] getData() {
        return copy(data);
    }

    public boolean isLocalTransfer() {
        return localTransfer;
    }

    public void setSource(int sourceActions, String[] mimeTypes, String[] data) {
        this.sourceActions = sourceActions;
        this.mimeTypes = copy(mimeTypes);
        this.data = copy(data);
        localTransfer = true;
    }

    public void accept(int action) {
        acceptedAction = action;
        dropAction = action;
    }

    private static String[] copy(String[] values) {
        if (values == null) {
            return new String[0];
        }
        String[] result = new String[values.length];
        System.arraycopy(values, 0, result, 0, values.length);
        return result;
    }
}
