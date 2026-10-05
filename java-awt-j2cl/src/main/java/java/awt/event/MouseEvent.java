package java.awt.event;

import java.awt.Component;
import java.awt.IllegalComponentStateException;
import java.awt.Point;

public class MouseEvent extends InputEvent {
    public static final int MOUSE_FIRST = 500;
    public static final int MOUSE_LAST = 507;
    public static final int MOUSE_CLICKED = 500;
    public static final int MOUSE_PRESSED = 501;
    public static final int MOUSE_RELEASED = 502;
    public static final int MOUSE_MOVED = 503;
    public static final int MOUSE_ENTERED = 504;
    public static final int MOUSE_EXITED = 505;
    public static final int MOUSE_DRAGGED = 506;
    public static final int MOUSE_WHEEL = 507;
    public static final int NOBUTTON = 0;
    public static final int BUTTON1 = 1;
    public static final int BUTTON2 = 2;
    public static final int BUTTON3 = 3;
    private static final int MAXIMUM_BUTTON = 20;
    private static final long serialVersionUID = 1L;
    int x;
    int y;
    private int xAbs;
    private int yAbs;
    private int clickCount;
    private int button;
    private boolean popupTrigger;
    private boolean excludeButtonFromExtModifiers;

    public Point getLocationOnScreen() {
        return new Point(xAbs, yAbs);
    }

    public int getXOnScreen() {
        return xAbs;
    }

    public int getYOnScreen() {
        return yAbs;
    }

    public MouseEvent(Component source, int id, long when, int modifiers, int x, int y, int clickCount, boolean popupTrigger, int button) {
        this(source, id, when, modifiers, x, y, 0, 0, clickCount, popupTrigger, button);
        try {
            Point location = source.getLocationOnScreen();
            xAbs = location.x + x;
            yAbs = location.y + y;
        } catch (IllegalComponentStateException exception) {
            xAbs = 0;
            yAbs = 0;
        }
    }

    public MouseEvent(Component source, int id, long when, int modifiers, int x, int y, int clickCount, boolean popupTrigger) {
        this(source, id, when, modifiers, x, y, clickCount, popupTrigger, NOBUTTON);
    }

    public MouseEvent(Component source, int id, long when, int modifiers, int x, int y, int xAbs, int yAbs, int clickCount, boolean popupTrigger, int button) {
        super(source, id, when, modifiers);
        if (button < NOBUTTON || button > MAXIMUM_BUTTON) {
            throw new IllegalArgumentException("Invalid button value " + button);
        }
        this.x = x;
        this.y = y;
        this.xAbs = xAbs;
        this.yAbs = yAbs;
        this.clickCount = clickCount;
        this.popupTrigger = popupTrigger;
        this.button = button;
        if (button > BUTTON3 && (id == MOUSE_RELEASED || id == MOUSE_CLICKED) && (modifiers & getMaskForButton(button)) != 0) {
            excludeButtonFromExtModifiers = true;
        }
        normalizeModifiers();
    }

    public int getModifiersEx() {
        int modifiersEx = super.getModifiersEx();
        return excludeButtonFromExtModifiers ? modifiersEx & ~getMaskForButton(button) : modifiersEx;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Point getPoint() {
        return new Point(x, y);
    }

    public synchronized void translatePoint(int x, int y) {
        this.x += x;
        this.y += y;
    }

    public int getClickCount() {
        return clickCount;
    }

    public int getButton() {
        return button;
    }

    public boolean isPopupTrigger() {
        return popupTrigger;
    }

    public static String getMouseModifiersText(int modifiers) {
        StringBuilder text = new StringBuilder();
        appendModifier(text, modifiers, ALT_MASK, "Alt");
        appendModifier(text, modifiers, META_MASK, "Meta");
        appendModifier(text, modifiers, CTRL_MASK, "Ctrl");
        appendModifier(text, modifiers, SHIFT_MASK, "Shift");
        appendModifier(text, modifiers, ALT_GRAPH_MASK, "Alt Graph");
        appendModifier(text, modifiers, BUTTON1_MASK, "Button1");
        for (int button = 1; button <= MAXIMUM_BUTTON; button++) {
            appendModifier(text, modifiers, getMaskForButton(button), "Button" + button);
        }
        return text.toString();
    }

    public String paramString() {
        String type;
        switch (id) {
            case MOUSE_PRESSED: type = "MOUSE_PRESSED"; break;
            case MOUSE_RELEASED: type = "MOUSE_RELEASED"; break;
            case MOUSE_CLICKED: type = "MOUSE_CLICKED"; break;
            case MOUSE_ENTERED: type = "MOUSE_ENTERED"; break;
            case MOUSE_EXITED: type = "MOUSE_EXITED"; break;
            case MOUSE_MOVED: type = "MOUSE_MOVED"; break;
            case MOUSE_DRAGGED: type = "MOUSE_DRAGGED"; break;
            case MOUSE_WHEEL: type = "MOUSE_WHEEL"; break;
            default: type = "unknown type";
        }
        StringBuilder text = new StringBuilder(type).append(",(").append(x).append(',').append(y).append("),absolute(").append(xAbs).append(',').append(yAbs).append(')');
        if (id != MOUSE_DRAGGED && id != MOUSE_MOVED) {
            text.append(",button=").append(getButton());
        }
        if (getModifiers() != 0) {
            text.append(",modifiers=").append(getMouseModifiersText(modifiers));
        }
        if (getModifiersEx() != 0) {
            text.append(",extModifiers=").append(getModifiersExText(modifiers));
        }
        text.append(",clickCount=").append(clickCount);
        return text.toString();
    }

    private void normalizeModifiers() {
        if ((modifiers & JDK_1_3_MODIFIERS) != 0 && getModifiersEx() == 0) {
            setNewModifiers();
        } else if ((modifiers & JDK_1_3_MODIFIERS) == 0 && getModifiersEx() != 0) {
            setOldModifiers();
        }
    }

    private void setNewModifiers() {
        if (id == MOUSE_PRESSED || id == MOUSE_RELEASED || id == MOUSE_CLICKED) {
            if ((modifiers & BUTTON1_MASK) != 0) {
                button = BUTTON1;
                modifiers |= BUTTON1_DOWN_MASK;
            } else if ((modifiers & BUTTON2_MASK) != 0) {
                button = BUTTON2;
                modifiers |= BUTTON2_DOWN_MASK;
            } else if ((modifiers & BUTTON3_MASK) != 0) {
                button = BUTTON3;
                modifiers |= BUTTON3_DOWN_MASK;
            }
            if (id == MOUSE_RELEASED || id == MOUSE_CLICKED) {
                modifiers &= ~getChangedButtonMask();
            }
        } else {
            if ((modifiers & BUTTON1_MASK) != 0) {
                modifiers |= BUTTON1_DOWN_MASK;
            }
            if ((modifiers & BUTTON2_MASK) != 0) {
                modifiers |= BUTTON2_DOWN_MASK;
            }
            if ((modifiers & BUTTON3_MASK) != 0) {
                modifiers |= BUTTON3_DOWN_MASK;
            }
        }
        if ((modifiers & ALT_MASK) != 0) {
            modifiers |= ALT_DOWN_MASK;
        }
        if ((modifiers & META_MASK) != 0) {
            modifiers |= META_DOWN_MASK;
        }
        if ((modifiers & SHIFT_MASK) != 0) {
            modifiers |= SHIFT_DOWN_MASK;
        }
        if ((modifiers & CTRL_MASK) != 0) {
            modifiers |= CTRL_DOWN_MASK;
        }
        if ((modifiers & ALT_GRAPH_MASK) != 0) {
            modifiers |= ALT_GRAPH_DOWN_MASK;
        }
    }

    private void setOldModifiers() {
        if (id == MOUSE_PRESSED || id == MOUSE_RELEASED || id == MOUSE_CLICKED) {
            if (button == BUTTON1) {
                modifiers |= BUTTON1_MASK;
            } else if (button == BUTTON2) {
                modifiers |= BUTTON2_MASK;
            } else if (button == BUTTON3) {
                modifiers |= BUTTON3_MASK;
            }
        } else {
            if ((modifiers & BUTTON1_DOWN_MASK) != 0) {
                modifiers |= BUTTON1_MASK;
            }
            if ((modifiers & BUTTON2_DOWN_MASK) != 0) {
                modifiers |= BUTTON2_MASK;
            }
            if ((modifiers & BUTTON3_DOWN_MASK) != 0) {
                modifiers |= BUTTON3_MASK;
            }
        }
        if ((modifiers & ALT_DOWN_MASK) != 0) {
            modifiers |= ALT_MASK;
        }
        if ((modifiers & META_DOWN_MASK) != 0) {
            modifiers |= META_MASK;
        }
        if ((modifiers & SHIFT_DOWN_MASK) != 0) {
            modifiers |= SHIFT_MASK;
        }
        if ((modifiers & CTRL_DOWN_MASK) != 0) {
            modifiers |= CTRL_MASK;
        }
        if ((modifiers & ALT_GRAPH_DOWN_MASK) != 0) {
            modifiers |= ALT_GRAPH_MASK;
        }
    }

    private int getChangedButtonMask() {
        return button > NOBUTTON ? getMaskForButton(button) : 0;
    }

    private static void appendModifier(StringBuilder text, int modifiers, int mask, String label) {
        if ((modifiers & mask) != 0) {
            if (text.length() != 0) {
                text.append('+');
            }
            text.append(label);
        }
    }
}
