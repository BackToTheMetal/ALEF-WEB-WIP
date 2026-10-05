package org.webawt.internal.peer;

public final class PeerInputEvent {
    public static final int FOCUS_GAINED = 1;
    public static final int FOCUS_LOST = 2;
    public static final int KEY_TYPED = 3;
    public static final int KEY_PRESSED = 4;
    public static final int KEY_RELEASED = 5;
    public static final int MOUSE_CLICKED = 6;
    public static final int MOUSE_PRESSED = 7;
    public static final int MOUSE_RELEASED = 8;
    public static final int MOUSE_MOVED = 9;
    public static final int MOUSE_ENTERED = 10;
    public static final int MOUSE_EXITED = 11;
    public static final int MOUSE_DRAGGED = 12;
    public static final int MOUSE_WHEEL = 13;
    public final int type;
    public final long when;
    public final int modifiers;
    public final int x;
    public final int y;
    public final int screenX;
    public final int screenY;
    public final int clickCount;
    public final int button;
    public final int keyCode;
    public final char keyChar;
    public final int keyLocation;
    public final double wheelRotation;

    public PeerInputEvent(int type, long when, int modifiers, int x, int y, int screenX, int screenY, int clickCount, int button, int keyCode, char keyChar, int keyLocation, double wheelRotation) {
        this.type = type;
        this.when = when;
        this.modifiers = modifiers;
        this.x = x;
        this.y = y;
        this.screenX = screenX;
        this.screenY = screenY;
        this.clickCount = clickCount;
        this.button = button;
        this.keyCode = keyCode;
        this.keyChar = keyChar;
        this.keyLocation = keyLocation;
        this.wheelRotation = wheelRotation;
    }
}
