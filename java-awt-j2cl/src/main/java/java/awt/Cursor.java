package java.awt;

import java.io.Serializable;

public class Cursor implements Serializable {
    private static final long serialVersionUID = 8028237497568985504L;
    public static final int DEFAULT_CURSOR = 0;
    public static final int CROSSHAIR_CURSOR = 1;
    public static final int TEXT_CURSOR = 2;
    public static final int WAIT_CURSOR = 3;
    public static final int SW_RESIZE_CURSOR = 4;
    public static final int SE_RESIZE_CURSOR = 5;
    public static final int NW_RESIZE_CURSOR = 6;
    public static final int NE_RESIZE_CURSOR = 7;
    public static final int N_RESIZE_CURSOR = 8;
    public static final int S_RESIZE_CURSOR = 9;
    public static final int W_RESIZE_CURSOR = 10;
    public static final int E_RESIZE_CURSOR = 11;
    public static final int HAND_CURSOR = 12;
    public static final int MOVE_CURSOR = 13;
    public static final int CUSTOM_CURSOR = -1;
    private static final String[] PREDEFINED_NAMES = {"Default Cursor", "Crosshair Cursor", "Text Cursor", "Wait Cursor", "Southwest Resize Cursor", "Southeast Resize Cursor", "Northwest Resize Cursor", "Northeast Resize Cursor", "North Resize Cursor", "South Resize Cursor", "West Resize Cursor", "East Resize Cursor", "Hand Cursor", "Move Cursor"};
    protected static Cursor[] predefined = new Cursor[PREDEFINED_NAMES.length];
    protected String name;
    private final int type;

    public static Cursor getPredefinedCursor(int type) {
        if (type < DEFAULT_CURSOR || type > MOVE_CURSOR) {
            throw new IllegalArgumentException("illegal cursor type");
        }
        if (predefined[type] == null) {
            predefined[type] = new Cursor(type);
        }
        return predefined[type];
    }

    public static Cursor getSystemCustomCursor(String name) throws AWTException, HeadlessException {
        if (name == null) {
            throw new NullPointerException("name");
        }
        return null;
    }

    public static Cursor getDefaultCursor() {
        return getPredefinedCursor(DEFAULT_CURSOR);
    }

    public Cursor(int type) {
        if (type < DEFAULT_CURSOR || type > MOVE_CURSOR) {
            throw new IllegalArgumentException("illegal cursor type");
        }
        this.type = type;
        name = PREDEFINED_NAMES[type];
    }

    protected Cursor(String name) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        type = CUSTOM_CURSOR;
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getClass().getName() + "[" + name + "]";
    }
}
