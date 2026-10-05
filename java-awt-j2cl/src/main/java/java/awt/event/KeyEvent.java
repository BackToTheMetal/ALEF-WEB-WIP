package java.awt.event;

import java.awt.Component;

public class KeyEvent extends InputEvent {
    public static final int KEY_FIRST = 400;
    public static final int KEY_LAST = 402;
    public static final int KEY_TYPED = 400;
    public static final int KEY_PRESSED = 401;
    public static final int KEY_RELEASED = 402;
    public static final int VK_ENTER = 10;
    public static final int VK_BACK_SPACE = 8;
    public static final int VK_TAB = 9;
    public static final int VK_CANCEL = 3;
    public static final int VK_CLEAR = 12;
    public static final int VK_SHIFT = 16;
    public static final int VK_CONTROL = 17;
    public static final int VK_ALT = 18;
    public static final int VK_PAUSE = 19;
    public static final int VK_CAPS_LOCK = 20;
    public static final int VK_ESCAPE = 27;
    public static final int VK_SPACE = 32;
    public static final int VK_PAGE_UP = 33;
    public static final int VK_PAGE_DOWN = 34;
    public static final int VK_END = 35;
    public static final int VK_HOME = 36;
    public static final int VK_LEFT = 37;
    public static final int VK_UP = 38;
    public static final int VK_RIGHT = 39;
    public static final int VK_DOWN = 40;
    public static final int VK_COMMA = 44;
    public static final int VK_MINUS = 45;
    public static final int VK_PERIOD = 46;
    public static final int VK_SLASH = 47;
    public static final int VK_0 = 48;
    public static final int VK_1 = 49;
    public static final int VK_2 = 50;
    public static final int VK_3 = 51;
    public static final int VK_4 = 52;
    public static final int VK_5 = 53;
    public static final int VK_6 = 54;
    public static final int VK_7 = 55;
    public static final int VK_8 = 56;
    public static final int VK_9 = 57;
    public static final int VK_SEMICOLON = 59;
    public static final int VK_EQUALS = 61;
    public static final int VK_A = 65;
    public static final int VK_B = 66;
    public static final int VK_C = 67;
    public static final int VK_D = 68;
    public static final int VK_E = 69;
    public static final int VK_F = 70;
    public static final int VK_G = 71;
    public static final int VK_H = 72;
    public static final int VK_I = 73;
    public static final int VK_J = 74;
    public static final int VK_K = 75;
    public static final int VK_L = 76;
    public static final int VK_M = 77;
    public static final int VK_N = 78;
    public static final int VK_O = 79;
    public static final int VK_P = 80;
    public static final int VK_Q = 81;
    public static final int VK_R = 82;
    public static final int VK_S = 83;
    public static final int VK_T = 84;
    public static final int VK_U = 85;
    public static final int VK_V = 86;
    public static final int VK_W = 87;
    public static final int VK_X = 88;
    public static final int VK_Y = 89;
    public static final int VK_Z = 90;
    public static final int VK_OPEN_BRACKET = 91;
    public static final int VK_BACK_SLASH = 92;
    public static final int VK_CLOSE_BRACKET = 93;
    public static final int VK_NUMPAD0 = 96;
    public static final int VK_NUMPAD1 = 97;
    public static final int VK_NUMPAD2 = 98;
    public static final int VK_NUMPAD3 = 99;
    public static final int VK_NUMPAD4 = 100;
    public static final int VK_NUMPAD5 = 101;
    public static final int VK_NUMPAD6 = 102;
    public static final int VK_NUMPAD7 = 103;
    public static final int VK_NUMPAD8 = 104;
    public static final int VK_NUMPAD9 = 105;
    public static final int VK_MULTIPLY = 106;
    public static final int VK_ADD = 107;
    public static final int VK_SEPARATER = 108;
    public static final int VK_SEPARATOR = 108;
    public static final int VK_SUBTRACT = 109;
    public static final int VK_DECIMAL = 110;
    public static final int VK_DIVIDE = 111;
    public static final int VK_DELETE = 127;
    public static final int VK_NUM_LOCK = 144;
    public static final int VK_SCROLL_LOCK = 145;
    public static final int VK_F1 = 112;
    public static final int VK_F2 = 113;
    public static final int VK_F3 = 114;
    public static final int VK_F4 = 115;
    public static final int VK_F5 = 116;
    public static final int VK_F6 = 117;
    public static final int VK_F7 = 118;
    public static final int VK_F8 = 119;
    public static final int VK_F9 = 120;
    public static final int VK_F10 = 121;
    public static final int VK_F11 = 122;
    public static final int VK_F12 = 123;
    public static final int VK_F13 = 61440;
    public static final int VK_F14 = 61441;
    public static final int VK_F15 = 61442;
    public static final int VK_F16 = 61443;
    public static final int VK_F17 = 61444;
    public static final int VK_F18 = 61445;
    public static final int VK_F19 = 61446;
    public static final int VK_F20 = 61447;
    public static final int VK_F21 = 61448;
    public static final int VK_F22 = 61449;
    public static final int VK_F23 = 61450;
    public static final int VK_F24 = 61451;
    public static final int VK_PRINTSCREEN = 154;
    public static final int VK_INSERT = 155;
    public static final int VK_HELP = 156;
    public static final int VK_META = 157;
    public static final int VK_BACK_QUOTE = 192;
    public static final int VK_QUOTE = 222;
    public static final int VK_KP_UP = 224;
    public static final int VK_KP_DOWN = 225;
    public static final int VK_KP_LEFT = 226;
    public static final int VK_KP_RIGHT = 227;
    public static final int VK_DEAD_GRAVE = 128;
    public static final int VK_DEAD_ACUTE = 129;
    public static final int VK_DEAD_CIRCUMFLEX = 130;
    public static final int VK_DEAD_TILDE = 131;
    public static final int VK_DEAD_MACRON = 132;
    public static final int VK_DEAD_BREVE = 133;
    public static final int VK_DEAD_ABOVEDOT = 134;
    public static final int VK_DEAD_DIAERESIS = 135;
    public static final int VK_DEAD_ABOVERING = 136;
    public static final int VK_DEAD_DOUBLEACUTE = 137;
    public static final int VK_DEAD_CARON = 138;
    public static final int VK_DEAD_CEDILLA = 139;
    public static final int VK_DEAD_OGONEK = 140;
    public static final int VK_DEAD_IOTA = 141;
    public static final int VK_DEAD_VOICED_SOUND = 142;
    public static final int VK_DEAD_SEMIVOICED_SOUND = 143;
    public static final int VK_AMPERSAND = 150;
    public static final int VK_ASTERISK = 151;
    public static final int VK_QUOTEDBL = 152;
    public static final int VK_LESS = 153;
    public static final int VK_GREATER = 160;
    public static final int VK_BRACELEFT = 161;
    public static final int VK_BRACERIGHT = 162;
    public static final int VK_AT = 512;
    public static final int VK_COLON = 513;
    public static final int VK_CIRCUMFLEX = 514;
    public static final int VK_DOLLAR = 515;
    public static final int VK_EURO_SIGN = 516;
    public static final int VK_EXCLAMATION_MARK = 517;
    public static final int VK_INVERTED_EXCLAMATION_MARK = 518;
    public static final int VK_LEFT_PARENTHESIS = 519;
    public static final int VK_NUMBER_SIGN = 520;
    public static final int VK_PLUS = 521;
    public static final int VK_RIGHT_PARENTHESIS = 522;
    public static final int VK_UNDERSCORE = 523;
    public static final int VK_WINDOWS = 524;
    public static final int VK_CONTEXT_MENU = 525;
    public static final int VK_FINAL = 24;
    public static final int VK_CONVERT = 28;
    public static final int VK_NONCONVERT = 29;
    public static final int VK_ACCEPT = 30;
    public static final int VK_MODECHANGE = 31;
    public static final int VK_KANA = 21;
    public static final int VK_KANJI = 25;
    public static final int VK_ALPHANUMERIC = 240;
    public static final int VK_KATAKANA = 241;
    public static final int VK_HIRAGANA = 242;
    public static final int VK_FULL_WIDTH = 243;
    public static final int VK_HALF_WIDTH = 244;
    public static final int VK_ROMAN_CHARACTERS = 245;
    public static final int VK_ALL_CANDIDATES = 256;
    public static final int VK_PREVIOUS_CANDIDATE = 257;
    public static final int VK_CODE_INPUT = 258;
    public static final int VK_JAPANESE_KATAKANA = 259;
    public static final int VK_JAPANESE_HIRAGANA = 260;
    public static final int VK_JAPANESE_ROMAN = 261;
    public static final int VK_KANA_LOCK = 262;
    public static final int VK_INPUT_METHOD_ON_OFF = 263;
    public static final int VK_CUT = 65489;
    public static final int VK_COPY = 65485;
    public static final int VK_PASTE = 65487;
    public static final int VK_UNDO = 65483;
    public static final int VK_AGAIN = 65481;
    public static final int VK_FIND = 65488;
    public static final int VK_PROPS = 65482;
    public static final int VK_STOP = 65480;
    public static final int VK_COMPOSE = 65312;
    public static final int VK_ALT_GRAPH = 65406;
    public static final int VK_BEGIN = 65368;
    public static final int VK_UNDEFINED = 0;
    public static final char CHAR_UNDEFINED = '\uffff';
    public static final int KEY_LOCATION_UNKNOWN = 0;
    public static final int KEY_LOCATION_STANDARD = 1;
    public static final int KEY_LOCATION_LEFT = 2;
    public static final int KEY_LOCATION_RIGHT = 3;
    public static final int KEY_LOCATION_NUMPAD = 4;
    private static final long serialVersionUID = 1L;
    private int keyCode;
    private char keyChar;
    private final int keyLocation;
    private int extendedKeyCode;

    public KeyEvent(Component source, int id, long when, int modifiers, int keyCode, char keyChar, int keyLocation) {
        super(source, id, when, modifiers);
        if (id == KEY_TYPED && keyChar == CHAR_UNDEFINED) {
            throw new IllegalArgumentException("invalid keyChar");
        }
        if (id == KEY_TYPED && keyCode != VK_UNDEFINED) {
            throw new IllegalArgumentException("invalid keyCode");
        }
        if (id == KEY_TYPED && keyLocation != KEY_LOCATION_UNKNOWN) {
            throw new IllegalArgumentException("invalid keyLocation");
        }
        if (keyLocation < KEY_LOCATION_UNKNOWN || keyLocation > KEY_LOCATION_NUMPAD) {
            throw new IllegalArgumentException("invalid keyLocation");
        }
        this.keyCode = keyCode;
        this.keyChar = keyChar;
        this.keyLocation = keyLocation;
        normalizeModifiers();
    }

    public KeyEvent(Component source, int id, long when, int modifiers, int keyCode, char keyChar) {
        this(source, id, when, modifiers, keyCode, keyChar, KEY_LOCATION_UNKNOWN);
    }

    @Deprecated
    public KeyEvent(Component source, int id, long when, int modifiers, int keyCode) {
        this(source, id, when, modifiers, keyCode, (char) keyCode);
    }

    public int getKeyCode() {
        return keyCode;
    }

    public void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
    }

    public char getKeyChar() {
        return keyChar;
    }

    public void setKeyChar(char keyChar) {
        this.keyChar = keyChar;
    }

    @Deprecated
    public void setModifiers(int modifiers) {
        this.modifiers = modifiers;
        normalizeModifiers();
    }

    public int getKeyLocation() {
        return keyLocation;
    }

    public int getExtendedKeyCode() {
        return extendedKeyCode;
    }

    public static int getExtendedKeyCodeForChar(int character) {
        if (character >= 'a' && character <= 'z') {
            return character - 'a' + VK_A;
        }
        if (character >= 'A' && character <= 'Z' || character >= '0' && character <= '9') {
            return character;
        }
        int knownCode = getPunctuationKeyCode(character);
        if (knownCode != VK_UNDEFINED) {
            return knownCode;
        }
        return character > 0 && character <= 0xffff ? 0x01000000 | character : VK_UNDEFINED;
    }

    public static String getKeyText(int keyCode) {
        if (keyCode >= VK_0 && keyCode <= VK_9 || keyCode >= VK_A && keyCode <= VK_Z) {
            return String.valueOf((char) keyCode);
        }
        if (keyCode >= VK_NUMPAD0 && keyCode <= VK_NUMPAD9) {
            return "NumPad-" + (keyCode - VK_NUMPAD0);
        }
        if (keyCode >= VK_F1 && keyCode <= VK_F12) {
            return "F" + (keyCode - VK_F1 + 1);
        }
        if (keyCode >= VK_F13 && keyCode <= VK_F24) {
            return "F" + (keyCode - VK_F13 + 13);
        }
        String keyText = getNamedKeyText(keyCode);
        if (keyText != null) {
            return keyText;
        }
        if ((keyCode & 0x01000000) != 0) {
            return String.valueOf((char) (keyCode ^ 0x01000000));
        }
        return "Unknown keyCode: 0x" + Integer.toString(keyCode, 16);
    }

    @Deprecated
    public static String getKeyModifiersText(int modifiers) {
        StringBuilder text = new StringBuilder();
        appendModifier(text, modifiers, META_MASK, "Meta");
        appendModifier(text, modifiers, CTRL_MASK, "Ctrl");
        appendModifier(text, modifiers, ALT_MASK, "Alt");
        appendModifier(text, modifiers, SHIFT_MASK, "Shift");
        appendModifier(text, modifiers, ALT_GRAPH_MASK, "Alt Graph");
        appendModifier(text, modifiers, BUTTON1_MASK, "Button1");
        return text.toString();
    }

    public boolean isActionKey() {
        if (keyCode >= VK_F1 && keyCode <= VK_F12 || keyCode >= VK_F13 && keyCode <= VK_F24) {
            return true;
        }
        switch (keyCode) {
            case VK_HOME:
            case VK_END:
            case VK_PAGE_UP:
            case VK_PAGE_DOWN:
            case VK_UP:
            case VK_DOWN:
            case VK_LEFT:
            case VK_RIGHT:
            case VK_BEGIN:
            case VK_KP_UP:
            case VK_KP_DOWN:
            case VK_KP_LEFT:
            case VK_KP_RIGHT:
            case VK_PRINTSCREEN:
            case VK_SCROLL_LOCK:
            case VK_CAPS_LOCK:
            case VK_NUM_LOCK:
            case VK_PAUSE:
            case VK_INSERT:
            case VK_FINAL:
            case VK_CONVERT:
            case VK_NONCONVERT:
            case VK_ACCEPT:
            case VK_MODECHANGE:
            case VK_KANA:
            case VK_KANJI:
            case VK_ALPHANUMERIC:
            case VK_KATAKANA:
            case VK_HIRAGANA:
            case VK_FULL_WIDTH:
            case VK_HALF_WIDTH:
            case VK_ROMAN_CHARACTERS:
            case VK_ALL_CANDIDATES:
            case VK_PREVIOUS_CANDIDATE:
            case VK_CODE_INPUT:
            case VK_JAPANESE_KATAKANA:
            case VK_JAPANESE_HIRAGANA:
            case VK_JAPANESE_ROMAN:
            case VK_KANA_LOCK:
            case VK_INPUT_METHOD_ON_OFF:
            case VK_AGAIN:
            case VK_UNDO:
            case VK_COPY:
            case VK_PASTE:
            case VK_CUT:
            case VK_FIND:
            case VK_PROPS:
            case VK_STOP:
            case VK_HELP:
            case VK_WINDOWS:
            case VK_CONTEXT_MENU:
                return true;
            default:
                return false;
        }
    }

    public String paramString() {
        String type;
        switch (id) {
            case KEY_PRESSED:
                type = "KEY_PRESSED";
                break;
            case KEY_RELEASED:
                type = "KEY_RELEASED";
                break;
            case KEY_TYPED:
                type = "KEY_TYPED";
                break;
            default:
                type = "unknown type";
        }
        StringBuilder text = new StringBuilder(type).append(",keyCode=").append(keyCode).append(",keyText=").append(getKeyText(keyCode)).append(",keyChar=").append(getKeyCharText());
        if (modifiers != 0) {
            text.append(",modifiers=").append(getKeyModifiersText(modifiers));
        }
        if (getModifiersEx() != 0) {
            text.append(",extModifiers=").append(getModifiersExText(getModifiersEx()));
        }
        text.append(",keyLocation=").append(getKeyLocationText()).append(",rawCode=0,primaryLevelUnicode=0,scancode=0,extendedKeyCode=0x").append(Integer.toHexString(extendedKeyCode));
        return text.toString();
    }

    private String getKeyCharText() {
        switch (keyChar) {
            case '\b':
                return "Backspace";
            case '\t':
                return "Tab";
            case '\n':
                return "Enter";
            case 0x18:
                return "Cancel";
            case 0x1b:
                return "Escape";
            case 0x7f:
                return "Delete";
            case CHAR_UNDEFINED:
                return "Undefined keyChar";
            default:
                return "'" + keyChar + "'";
        }
    }

    private String getKeyLocationText() {
        switch (keyLocation) {
            case KEY_LOCATION_STANDARD:
                return "KEY_LOCATION_STANDARD";
            case KEY_LOCATION_LEFT:
                return "KEY_LOCATION_LEFT";
            case KEY_LOCATION_RIGHT:
                return "KEY_LOCATION_RIGHT";
            case KEY_LOCATION_NUMPAD:
                return "KEY_LOCATION_NUMPAD";
            default:
                return "KEY_LOCATION_UNKNOWN";
        }
    }

    private void normalizeModifiers() {
        if ((modifiers & (SHIFT_MASK | CTRL_MASK | META_MASK | ALT_MASK | ALT_GRAPH_MASK | BUTTON1_MASK)) != 0 && getModifiersEx() == 0) {
            setNewModifiers();
        } else if ((modifiers & (SHIFT_MASK | CTRL_MASK | META_MASK | ALT_MASK | ALT_GRAPH_MASK | BUTTON1_MASK)) == 0 && getModifiersEx() != 0) {
            setOldModifiers();
        }
    }

    private void setNewModifiers() {
        if ((modifiers & SHIFT_MASK) != 0) {
            modifiers |= SHIFT_DOWN_MASK;
        }
        if ((modifiers & ALT_MASK) != 0) {
            modifiers |= ALT_DOWN_MASK;
        }
        if ((modifiers & CTRL_MASK) != 0) {
            modifiers |= CTRL_DOWN_MASK;
        }
        if ((modifiers & META_MASK) != 0) {
            modifiers |= META_DOWN_MASK;
        }
        if ((modifiers & ALT_GRAPH_MASK) != 0) {
            modifiers |= ALT_GRAPH_DOWN_MASK;
        }
        if ((modifiers & BUTTON1_MASK) != 0) {
            modifiers |= BUTTON1_DOWN_MASK;
        }
    }

    private void setOldModifiers() {
        if ((modifiers & SHIFT_DOWN_MASK) != 0) {
            modifiers |= SHIFT_MASK;
        }
        if ((modifiers & ALT_DOWN_MASK) != 0) {
            modifiers |= ALT_MASK;
        }
        if ((modifiers & CTRL_DOWN_MASK) != 0) {
            modifiers |= CTRL_MASK;
        }
        if ((modifiers & META_DOWN_MASK) != 0) {
            modifiers |= META_MASK;
        }
        if ((modifiers & ALT_GRAPH_DOWN_MASK) != 0) {
            modifiers |= ALT_GRAPH_MASK;
        }
        if ((modifiers & BUTTON1_DOWN_MASK) != 0) {
            modifiers |= BUTTON1_MASK;
        }
    }

    private static void appendModifier(StringBuilder text, int modifiers, int mask, String label) {
        if ((modifiers & mask) != 0) {
            if (text.length() != 0) {
                text.append('+');
            }
            text.append(label);
        }
    }

    private static int getPunctuationKeyCode(int character) {
        switch (character) {
            case '\n': return VK_ENTER;
            case '\b': return VK_BACK_SPACE;
            case '\t': return VK_TAB;
            case 0x1b: return VK_ESCAPE;
            case ' ': return VK_SPACE;
            case ',': return VK_COMMA;
            case '-': return VK_MINUS;
            case '.': return VK_PERIOD;
            case '/': return VK_SLASH;
            case ';': return VK_SEMICOLON;
            case '=': return VK_EQUALS;
            case '[': return VK_OPEN_BRACKET;
            case '\\': return VK_BACK_SLASH;
            case ']': return VK_CLOSE_BRACKET;
            case '`': return VK_BACK_QUOTE;
            case '\'': return VK_QUOTE;
            case '&': return VK_AMPERSAND;
            case '*': return VK_ASTERISK;
            case '"': return VK_QUOTEDBL;
            case '<': return VK_LESS;
            case '>': return VK_GREATER;
            case '{': return VK_BRACELEFT;
            case '}': return VK_BRACERIGHT;
            case '@': return VK_AT;
            case ':': return VK_COLON;
            case '^': return VK_CIRCUMFLEX;
            case '$': return VK_DOLLAR;
            case 0x20ac: return VK_EURO_SIGN;
            case '!': return VK_EXCLAMATION_MARK;
            case 0xa1: return VK_INVERTED_EXCLAMATION_MARK;
            case '(': return VK_LEFT_PARENTHESIS;
            case '#': return VK_NUMBER_SIGN;
            case '+': return VK_PLUS;
            case ')': return VK_RIGHT_PARENTHESIS;
            case '_': return VK_UNDERSCORE;
            case 0x7f: return VK_DELETE;
            default: return VK_UNDEFINED;
        }
    }

    private static String getNamedKeyText(int keyCode) {
        switch (keyCode) {
            case VK_ENTER: return "Enter";
            case VK_BACK_SPACE: return "Backspace";
            case VK_TAB: return "Tab";
            case VK_CANCEL: return "Cancel";
            case VK_CLEAR: return "Clear";
            case VK_COMPOSE: return "Compose";
            case VK_PAUSE: return "Pause";
            case VK_CAPS_LOCK: return "Caps Lock";
            case VK_ESCAPE: return "Escape";
            case VK_SPACE: return "Space";
            case VK_PAGE_UP: return "Page Up";
            case VK_PAGE_DOWN: return "Page Down";
            case VK_END: return "End";
            case VK_HOME: return "Home";
            case VK_LEFT: return "Left";
            case VK_UP: return "Up";
            case VK_RIGHT: return "Right";
            case VK_DOWN: return "Down";
            case VK_BEGIN: return "Begin";
            case VK_SHIFT: return "Shift";
            case VK_CONTROL: return "Ctrl";
            case VK_ALT: return "Alt";
            case VK_META: return "Meta";
            case VK_ALT_GRAPH: return "Alt Graph";
            case VK_COMMA: return "Comma";
            case VK_MINUS: return "Minus";
            case VK_PERIOD: return "Period";
            case VK_SLASH: return "Slash";
            case VK_SEMICOLON: return "Semicolon";
            case VK_EQUALS: return "Equals";
            case VK_OPEN_BRACKET: return "Open Bracket";
            case VK_BACK_SLASH: return "Back Slash";
            case VK_CLOSE_BRACKET: return "Close Bracket";
            case VK_MULTIPLY: return "NumPad *";
            case VK_ADD: return "NumPad +";
            case VK_SEPARATOR: return "NumPad ,";
            case VK_SUBTRACT: return "NumPad -";
            case VK_DECIMAL: return "NumPad .";
            case VK_DIVIDE: return "NumPad /";
            case VK_DELETE: return "Delete";
            case VK_NUM_LOCK: return "Num Lock";
            case VK_SCROLL_LOCK: return "Scroll Lock";
            case VK_WINDOWS: return "Windows";
            case VK_CONTEXT_MENU: return "Context Menu";
            case VK_PRINTSCREEN: return "Print Screen";
            case VK_INSERT: return "Insert";
            case VK_HELP: return "Help";
            case VK_BACK_QUOTE: return "Back Quote";
            case VK_QUOTE: return "Quote";
            case VK_KP_UP: return "Up";
            case VK_KP_DOWN: return "Down";
            case VK_KP_LEFT: return "Left";
            case VK_KP_RIGHT: return "Right";
            case VK_DEAD_GRAVE: return "Dead Grave";
            case VK_DEAD_ACUTE: return "Dead Acute";
            case VK_DEAD_CIRCUMFLEX: return "Dead Circumflex";
            case VK_DEAD_TILDE: return "Dead Tilde";
            case VK_DEAD_MACRON: return "Dead Macron";
            case VK_DEAD_BREVE: return "Dead Breve";
            case VK_DEAD_ABOVEDOT: return "Dead Above Dot";
            case VK_DEAD_DIAERESIS: return "Dead Diaeresis";
            case VK_DEAD_ABOVERING: return "Dead Above Ring";
            case VK_DEAD_DOUBLEACUTE: return "Dead Double Acute";
            case VK_DEAD_CARON: return "Dead Caron";
            case VK_DEAD_CEDILLA: return "Dead Cedilla";
            case VK_DEAD_OGONEK: return "Dead Ogonek";
            case VK_DEAD_IOTA: return "Dead Iota";
            case VK_DEAD_VOICED_SOUND: return "Dead Voiced Sound";
            case VK_DEAD_SEMIVOICED_SOUND: return "Dead Semivoiced Sound";
            case VK_AMPERSAND: return "Ampersand";
            case VK_ASTERISK: return "Asterisk";
            case VK_QUOTEDBL: return "Double Quote";
            case VK_LESS: return "Less";
            case VK_GREATER: return "Greater";
            case VK_BRACELEFT: return "Left Brace";
            case VK_BRACERIGHT: return "Right Brace";
            case VK_AT: return "At";
            case VK_COLON: return "Colon";
            case VK_CIRCUMFLEX: return "Circumflex";
            case VK_DOLLAR: return "Dollar";
            case VK_EURO_SIGN: return "Euro";
            case VK_EXCLAMATION_MARK: return "Exclamation Mark";
            case VK_INVERTED_EXCLAMATION_MARK: return "Inverted Exclamation Mark";
            case VK_LEFT_PARENTHESIS: return "Left Parenthesis";
            case VK_NUMBER_SIGN: return "Number Sign";
            case VK_PLUS: return "Plus";
            case VK_RIGHT_PARENTHESIS: return "Right Parenthesis";
            case VK_UNDERSCORE: return "Underscore";
            case VK_FINAL: return "Final";
            case VK_CONVERT: return "Convert";
            case VK_NONCONVERT: return "No Convert";
            case VK_ACCEPT: return "Accept";
            case VK_MODECHANGE: return "Mode Change";
            case VK_KANA: return "Kana";
            case VK_KANJI: return "Kanji";
            case VK_ALPHANUMERIC: return "Alphanumeric";
            case VK_KATAKANA: return "Katakana";
            case VK_HIRAGANA: return "Hiragana";
            case VK_FULL_WIDTH: return "Full-Width";
            case VK_HALF_WIDTH: return "Half-Width";
            case VK_ROMAN_CHARACTERS: return "Roman Characters";
            case VK_ALL_CANDIDATES: return "All Candidates";
            case VK_PREVIOUS_CANDIDATE: return "Previous Candidate";
            case VK_CODE_INPUT: return "Code Input";
            case VK_JAPANESE_KATAKANA: return "Japanese Katakana";
            case VK_JAPANESE_HIRAGANA: return "Japanese Hiragana";
            case VK_JAPANESE_ROMAN: return "Japanese Roman";
            case VK_KANA_LOCK: return "Kana Lock";
            case VK_INPUT_METHOD_ON_OFF: return "Input Method On/Off";
            case VK_CUT: return "Cut";
            case VK_COPY: return "Copy";
            case VK_PASTE: return "Paste";
            case VK_UNDO: return "Undo";
            case VK_AGAIN: return "Again";
            case VK_FIND: return "Find";
            case VK_PROPS: return "Properties";
            case VK_STOP: return "Stop";
            default: return null;
        }
    }
}
