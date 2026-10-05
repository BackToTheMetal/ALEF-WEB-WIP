package java.awt;

import java.io.Serializable;

public class MenuShortcut implements Serializable {
    private static final long serialVersionUID = 143448358473180225L;
    private final int key;
    private final boolean usesShift;

    public MenuShortcut(int key) {
        this(key, false);
    }

    public MenuShortcut(int key, boolean useShiftModifier) {
        this.key = key;
        usesShift = useShiftModifier;
    }

    public int getKey() {
        return key;
    }

    public boolean usesShiftModifier() {
        return usesShift;
    }

    public boolean equals(MenuShortcut shortcut) {
        return shortcut != null && key == shortcut.key && usesShift == shortcut.usesShift;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof MenuShortcut && equals((MenuShortcut) object);
    }

    @Override
    public int hashCode() {
        return usesShift ? ~key : key;
    }

    @Override
    public String toString() {
        return "Ctrl+" + (usesShift ? "Shift+" : "") + keyText(key);
    }

    private static String keyText(int key) {
        if (key >= Event.F1 && key <= Event.F12) {
            return "F" + (key - Event.F1 + 1);
        }
        if (key >= 32 && key <= 126) {
            return Character.toString((char) key);
        }
        if (key == Event.ENTER) {
            return "Enter";
        }
        if (key == Event.BACK_SPACE) {
            return "Backspace";
        }
        if (key == Event.TAB) {
            return "Tab";
        }
        if (key == Event.ESCAPE) {
            return "Escape";
        }
        if (key == Event.DELETE) {
            return "Delete";
        }
        return keyTextForNavigation(key);
    }

    private static String keyTextForNavigation(int key) {
        switch (key) {
            case Event.HOME:
                return "Home";
            case Event.END:
                return "End";
            case Event.PGUP:
                return "Page Up";
            case Event.PGDN:
                return "Page Down";
            case Event.UP:
                return "Up";
            case Event.DOWN:
                return "Down";
            case Event.LEFT:
                return "Left";
            case Event.RIGHT:
                return "Right";
            default:
                return "Unknown keyCode: 0x" + Integer.toHexString(key);
        }
    }
    protected String paramString() { return "key=" + key + ",usesShift=" + usesShiftModifier(); }
}
