package java.awt.dnd;

public final class DnDConstants {
    public static final int ACTION_NONE = 0;
    public static final int ACTION_COPY = 1;
    public static final int ACTION_MOVE = 2;
    public static final int ACTION_COPY_OR_MOVE = ACTION_COPY | ACTION_MOVE;
    public static final int ACTION_LINK = 0x40000000;
    public static final int ACTION_REFERENCE = ACTION_LINK;

    private DnDConstants() {
    }
}
