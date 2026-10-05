package java.awt.event;

import java.awt.Component;

public abstract class InputEvent extends ComponentEvent {
    @Deprecated
    public static final int SHIFT_MASK = 1;
    @Deprecated
    public static final int CTRL_MASK = 2;
    @Deprecated
    public static final int META_MASK = 4;
    @Deprecated
    public static final int ALT_MASK = 8;
    @Deprecated
    public static final int ALT_GRAPH_MASK = 32;
    @Deprecated
    public static final int BUTTON1_MASK = 16;
    @Deprecated
    public static final int BUTTON2_MASK = 8;
    @Deprecated
    public static final int BUTTON3_MASK = 4;
    public static final int SHIFT_DOWN_MASK = 64;
    public static final int CTRL_DOWN_MASK = 128;
    public static final int META_DOWN_MASK = 256;
    public static final int ALT_DOWN_MASK = 512;
    public static final int BUTTON1_DOWN_MASK = 1024;
    public static final int BUTTON2_DOWN_MASK = 2048;
    public static final int BUTTON3_DOWN_MASK = 4096;
    public static final int ALT_GRAPH_DOWN_MASK = 8192;
    static final int JDK_1_3_MODIFIERS = 63;
    private static final int MAXIMUM_BUTTON = 20;
    private static final long serialVersionUID = 1L;
    long when;
    int modifiers;

    InputEvent(Component source, int id, long when, int modifiers) {
        super(source, id);
        this.when = when;
        this.modifiers = modifiers;
    }

    public static int getMaskForButton(int button) {
        if (button <= 0 || button > MAXIMUM_BUTTON) {
            throw new IllegalArgumentException("button doesn't exist " + button);
        }
        return button <= 3 ? 1 << (button + 9) : 1 << (button + 10);
    }

    public boolean isShiftDown() {
        return (modifiers & SHIFT_DOWN_MASK) != 0;
    }

    public boolean isControlDown() {
        return (modifiers & CTRL_DOWN_MASK) != 0;
    }

    public boolean isMetaDown() {
        return (modifiers & META_DOWN_MASK) != 0;
    }

    public boolean isAltDown() {
        return (modifiers & ALT_DOWN_MASK) != 0;
    }

    public boolean isAltGraphDown() {
        return (modifiers & ALT_GRAPH_DOWN_MASK) != 0;
    }

    public long getWhen() {
        return when;
    }

    @Deprecated
    public int getModifiers() {
        return modifiers & (JDK_1_3_MODIFIERS | Integer.MIN_VALUE);
    }

    public int getModifiersEx() {
        return modifiers & ~JDK_1_3_MODIFIERS;
    }

    @Override
    public void consume() {
        consumed = true;
    }

    @Override
    public boolean isConsumed() {
        return consumed;
    }

    public static String getModifiersExText(int modifiers) {
        StringBuilder result = new StringBuilder();
        appendModifier(result, modifiers, META_DOWN_MASK, "Meta");
        appendModifier(result, modifiers, CTRL_DOWN_MASK, "Ctrl");
        appendModifier(result, modifiers, ALT_DOWN_MASK, "Alt");
        appendModifier(result, modifiers, SHIFT_DOWN_MASK, "Shift");
        appendModifier(result, modifiers, ALT_GRAPH_DOWN_MASK, "Alt Graph");
        for (int button = 1; button <= MAXIMUM_BUTTON; button++) {
            appendModifier(result, modifiers, getMaskForButton(button), "Button" + button);
        }
        return result.toString();
    }

    private static void appendModifier(StringBuilder result, int modifiers, int mask, String text) {
        if ((modifiers & mask) == 0) {
            return;
        }
        if (result.length() > 0) {
            result.append('+');
        }
        result.append(text);
    }
}
