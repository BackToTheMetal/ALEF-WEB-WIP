package java.awt.event;

import java.awt.AWTEvent;

public class ActionEvent extends AWTEvent {
    private static final long serialVersionUID = -7671078796273832149L;
    public static final int SHIFT_MASK = 1;
    public static final int CTRL_MASK = 2;
    public static final int META_MASK = 4;
    public static final int ALT_MASK = 8;
    public static final int ACTION_FIRST = 1001;
    public static final int ACTION_LAST = 1001;
    public static final int ACTION_PERFORMED = 1001;
    private final String actionCommand;
    private final long when;
    private final int modifiers;

    public ActionEvent(Object source, int id, String command) {
        this(source, id, command, 0L, 0);
    }

    public ActionEvent(Object source, int id, String command, int modifiers) {
        this(source, id, command, 0L, modifiers);
    }

    public ActionEvent(Object source, int id, String command, long when, int modifiers) {
        super(source, id);
        actionCommand = command;
        this.when = when;
        this.modifiers = modifiers;
    }

    public String getActionCommand() {
        return actionCommand;
    }

    public long getWhen() {
        return when;
    }

    public int getModifiers() {
        return modifiers;
    }

    @Override
    public String paramString() {
        return (id == ACTION_PERFORMED ? "ACTION_PERFORMED" : "unknown type") + ",cmd=" + actionCommand + ",when=" + when + ",modifiers=" + modifierText();
    }

    private String modifierText() {
        String result = "";
        if ((modifiers & META_MASK) != 0) {
            result = "Meta";
        }
        if ((modifiers & CTRL_MASK) != 0) {
            result = appendModifier(result, "Ctrl");
        }
        if ((modifiers & ALT_MASK) != 0) {
            result = appendModifier(result, "Alt");
        }
        if ((modifiers & SHIFT_MASK) != 0) {
            result = appendModifier(result, "Shift");
        }
        return result;
    }

    private static String appendModifier(String current, String modifier) {
        return current.length() == 0 ? modifier : current + "+" + modifier;
    }
}
