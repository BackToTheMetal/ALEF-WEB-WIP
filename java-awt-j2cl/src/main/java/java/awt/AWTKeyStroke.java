package java.awt;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AWTKeyStroke implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Map<AWTKeyStroke, AWTKeyStroke> CACHE = new HashMap<>();
    private static Map<String, Integer> keyCodes;
    private static Map<Integer, String> keyNames;
    private final char keyChar;
    private final int keyCode;
    private final int modifiers;
    private final boolean onKeyRelease;

    protected AWTKeyStroke() {
        this(KeyEvent.CHAR_UNDEFINED, KeyEvent.VK_UNDEFINED, 0, false);
    }

    protected AWTKeyStroke(char keyChar, int keyCode, int modifiers, boolean onKeyRelease) {
        this.keyChar = keyChar;
        this.keyCode = keyCode;
        this.modifiers = normalizeModifiers(modifiers);
        this.onKeyRelease = onKeyRelease;
    }

    public static AWTKeyStroke getAWTKeyStroke(char keyChar) {
        return cached(keyChar, KeyEvent.VK_UNDEFINED, 0, false);
    }

    public static AWTKeyStroke getAWTKeyStroke(Character keyChar, int modifiers) {
        if (keyChar == null) {
            throw new IllegalArgumentException("keyChar cannot be null");
        }
        return cached(keyChar.charValue(), KeyEvent.VK_UNDEFINED, modifiers, false);
    }

    public static AWTKeyStroke getAWTKeyStroke(int keyCode, int modifiers, boolean onKeyRelease) {
        return cached(KeyEvent.CHAR_UNDEFINED, keyCode, modifiers, onKeyRelease);
    }

    public static AWTKeyStroke getAWTKeyStroke(int keyCode, int modifiers) {
        return getAWTKeyStroke(keyCode, modifiers, false);
    }

    public static AWTKeyStroke getAWTKeyStrokeForEvent(KeyEvent event) {
        int eventModifiers = event.getModifiers() | event.getModifiersEx();
        if (event.getID() == KeyEvent.KEY_TYPED) {
            return getAWTKeyStroke(Character.valueOf(event.getKeyChar()), eventModifiers);
        }
        return getAWTKeyStroke(event.getKeyCode(), eventModifiers, event.getID() == KeyEvent.KEY_RELEASED);
    }

    public static AWTKeyStroke getAWTKeyStroke(String specification) {
        if (specification == null) {
            throw new IllegalArgumentException("String cannot be null");
        }
        List<String> tokens = tokenize(specification);
        if (tokens.isEmpty()) {
            throw invalidSpecification();
        }
        int modifiers = 0;
        int index = 0;
        while (index < tokens.size()) {
            int modifier = modifierForName(tokens.get(index));
            if (modifier == 0) {
                break;
            }
            modifiers |= modifier;
            index++;
        }
        if (index == tokens.size()) throw invalidSpecification();
        String eventType = "pressed";
        String key = tokens.get(index++);
        if ("typed".equals(key) || "pressed".equals(key) || "released".equals(key)) {
            eventType = key;
            if (index == tokens.size()) throw invalidSpecification();
            key = tokens.get(index++);
        }
        if (index != tokens.size()) throw invalidSpecification();
        if ("typed".equals(eventType)) {
            if (key.length() != 1) {
                throw invalidSpecification();
            }
            return getAWTKeyStroke(Character.valueOf(key.charAt(0)), modifiers);
        }
        if (!"pressed".equals(eventType) && !"released".equals(eventType)) {
            throw invalidSpecification();
        }
        Integer keyCode = getKeyCodes().get(key);
        if (keyCode == null) {
            throw invalidSpecification();
        }
        return getAWTKeyStroke(keyCode.intValue(), modifiers, "released".equals(eventType));
    }

    public final char getKeyChar() {
        return keyChar;
    }

    public final int getKeyCode() {
        return keyCode;
    }

    public final int getModifiers() {
        return modifiers;
    }

    public final boolean isOnKeyRelease() {
        return onKeyRelease;
    }

    public final int getKeyEventType() {
        if (keyCode == KeyEvent.VK_UNDEFINED) {
            return KeyEvent.KEY_TYPED;
        }
        return onKeyRelease ? KeyEvent.KEY_RELEASED : KeyEvent.KEY_PRESSED;
    }

    @Override
    public int hashCode() {
        return (keyChar + 1) * (2 * (keyCode + 1)) * (modifiers + 1) + (onKeyRelease ? 1 : 2);
    }

    @Override
    public final boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof AWTKeyStroke)) {
            return false;
        }
        AWTKeyStroke other = (AWTKeyStroke) object;
        return keyChar == other.keyChar && keyCode == other.keyCode && modifiers == other.modifiers && onKeyRelease == other.onKeyRelease;
    }

    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();
        appendModifier(text, InputEvent.SHIFT_DOWN_MASK, "shift");
        appendModifier(text, InputEvent.CTRL_DOWN_MASK, "ctrl");
        appendModifier(text, InputEvent.META_DOWN_MASK, "meta");
        appendModifier(text, InputEvent.ALT_DOWN_MASK, "alt");
        appendModifier(text, InputEvent.ALT_GRAPH_DOWN_MASK, "altGraph");
        appendModifier(text, InputEvent.BUTTON1_DOWN_MASK, "button1");
        appendModifier(text, InputEvent.BUTTON2_DOWN_MASK, "button2");
        appendModifier(text, InputEvent.BUTTON3_DOWN_MASK, "button3");
        if (keyCode == KeyEvent.VK_UNDEFINED) {
            return text.append("typed ").append(keyChar).toString();
        }
        String keyName = getKeyNames().get(Integer.valueOf(keyCode));
        return text.append(onKeyRelease ? "released " : "pressed ").append(keyName == null ? "UNKNOWN" : keyName).toString();
    }

    private static AWTKeyStroke cached(char keyChar, int keyCode, int modifiers, boolean onKeyRelease) {
        AWTKeyStroke candidate = new AWTKeyStroke(keyChar, keyCode, modifiers, onKeyRelease);
        AWTKeyStroke existing = CACHE.get(candidate);
        if (existing != null) {
            return existing;
        }
        CACHE.put(candidate, candidate);
        return candidate;
    }

    private static int normalizeModifiers(int modifiers) {
        if ((modifiers & (InputEvent.SHIFT_MASK | InputEvent.SHIFT_DOWN_MASK)) != 0) {
            modifiers |= InputEvent.SHIFT_MASK | InputEvent.SHIFT_DOWN_MASK;
        }
        if ((modifiers & (InputEvent.CTRL_MASK | InputEvent.CTRL_DOWN_MASK)) != 0) {
            modifiers |= InputEvent.CTRL_MASK | InputEvent.CTRL_DOWN_MASK;
        }
        if ((modifiers & (InputEvent.META_MASK | InputEvent.META_DOWN_MASK)) != 0) {
            modifiers |= InputEvent.META_MASK | InputEvent.META_DOWN_MASK;
        }
        if ((modifiers & (InputEvent.ALT_MASK | InputEvent.ALT_DOWN_MASK)) != 0) {
            modifiers |= InputEvent.ALT_MASK | InputEvent.ALT_DOWN_MASK;
        }
        if ((modifiers & (InputEvent.ALT_GRAPH_MASK | InputEvent.ALT_GRAPH_DOWN_MASK)) != 0) {
            modifiers |= InputEvent.ALT_GRAPH_MASK | InputEvent.ALT_GRAPH_DOWN_MASK;
        }
        int legacyMasks = InputEvent.SHIFT_MASK | InputEvent.CTRL_MASK | InputEvent.META_MASK | InputEvent.ALT_MASK | InputEvent.ALT_GRAPH_MASK;
        int extendedMasks = InputEvent.SHIFT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK | InputEvent.META_DOWN_MASK | InputEvent.ALT_DOWN_MASK | InputEvent.ALT_GRAPH_DOWN_MASK | InputEvent.BUTTON1_DOWN_MASK | InputEvent.BUTTON2_DOWN_MASK | InputEvent.BUTTON3_DOWN_MASK;
        return modifiers & (legacyMasks | extendedMasks);
    }

    private static List<String> tokenize(String specification) {
        List<String> tokens = new ArrayList<>();
        int index = 0;
        while (index < specification.length()) {
            while (index < specification.length() && specification.charAt(index) == ' ') {
                index++;
            }
            int start = index;
            while (index < specification.length() && specification.charAt(index) != ' ') {
                index++;
            }
            if (start < index) {
                tokens.add(specification.substring(start, index));
            }
        }
        return tokens;
    }

    private static int modifierForName(String name) {
        if ("shift".equals(name)) {
            return InputEvent.SHIFT_DOWN_MASK;
        }
        if ("control".equals(name) || "ctrl".equals(name)) {
            return InputEvent.CTRL_DOWN_MASK;
        }
        if ("meta".equals(name)) {
            return InputEvent.META_DOWN_MASK;
        }
        if ("alt".equals(name)) {
            return InputEvent.ALT_DOWN_MASK;
        }
        if ("altGraph".equals(name)) {
            return InputEvent.ALT_GRAPH_DOWN_MASK;
        }
        if ("button1".equals(name)) {
            return InputEvent.BUTTON1_DOWN_MASK;
        }
        if ("button2".equals(name)) {
            return InputEvent.BUTTON2_DOWN_MASK;
        }
        if ("button3".equals(name)) {
            return InputEvent.BUTTON3_DOWN_MASK;
        }
        return 0;
    }

    private void appendModifier(StringBuilder text, int mask, String name) {
        if ((modifiers & mask) != 0) {
            text.append(name).append(' ');
        }
    }

    private static IllegalArgumentException invalidSpecification() {
        return new IllegalArgumentException("String formatted incorrectly");
    }

    private static void registerKeyNames() {
        registerKeyName("ENTER", KeyEvent.VK_ENTER);
        registerKeyName("BACK_SPACE", KeyEvent.VK_BACK_SPACE);
        registerKeyName("TAB", KeyEvent.VK_TAB);
        registerKeyName("CANCEL", KeyEvent.VK_CANCEL);
        registerKeyName("CLEAR", KeyEvent.VK_CLEAR);
        registerKeyName("SHIFT", KeyEvent.VK_SHIFT);
        registerKeyName("CONTROL", KeyEvent.VK_CONTROL);
        registerKeyName("ALT", KeyEvent.VK_ALT);
        registerKeyName("PAUSE", KeyEvent.VK_PAUSE);
        registerKeyName("CAPS_LOCK", KeyEvent.VK_CAPS_LOCK);
        registerKeyName("ESCAPE", KeyEvent.VK_ESCAPE);
        registerKeyName("SPACE", KeyEvent.VK_SPACE);
        registerKeyName("PAGE_UP", KeyEvent.VK_PAGE_UP);
        registerKeyName("PAGE_DOWN", KeyEvent.VK_PAGE_DOWN);
        registerKeyName("END", KeyEvent.VK_END);
        registerKeyName("HOME", KeyEvent.VK_HOME);
        registerKeyName("LEFT", KeyEvent.VK_LEFT);
        registerKeyName("UP", KeyEvent.VK_UP);
        registerKeyName("RIGHT", KeyEvent.VK_RIGHT);
        registerKeyName("DOWN", KeyEvent.VK_DOWN);
        registerKeyName("COMMA", KeyEvent.VK_COMMA);
        registerKeyName("MINUS", KeyEvent.VK_MINUS);
        registerKeyName("PERIOD", KeyEvent.VK_PERIOD);
        registerKeyName("SLASH", KeyEvent.VK_SLASH);
        registerKeyName("0", KeyEvent.VK_0);
        registerKeyName("1", KeyEvent.VK_1);
        registerKeyName("2", KeyEvent.VK_2);
        registerKeyName("3", KeyEvent.VK_3);
        registerKeyName("4", KeyEvent.VK_4);
        registerKeyName("5", KeyEvent.VK_5);
        registerKeyName("6", KeyEvent.VK_6);
        registerKeyName("7", KeyEvent.VK_7);
        registerKeyName("8", KeyEvent.VK_8);
        registerKeyName("9", KeyEvent.VK_9);
        registerKeyName("SEMICOLON", KeyEvent.VK_SEMICOLON);
        registerKeyName("EQUALS", KeyEvent.VK_EQUALS);
        registerKeyName("A", KeyEvent.VK_A);
        registerKeyName("B", KeyEvent.VK_B);
        registerKeyName("C", KeyEvent.VK_C);
        registerKeyName("D", KeyEvent.VK_D);
        registerKeyName("E", KeyEvent.VK_E);
        registerKeyName("F", KeyEvent.VK_F);
        registerKeyName("G", KeyEvent.VK_G);
        registerKeyName("H", KeyEvent.VK_H);
        registerKeyName("I", KeyEvent.VK_I);
        registerKeyName("J", KeyEvent.VK_J);
        registerKeyName("K", KeyEvent.VK_K);
        registerKeyName("L", KeyEvent.VK_L);
        registerKeyName("M", KeyEvent.VK_M);
        registerKeyName("N", KeyEvent.VK_N);
        registerKeyName("O", KeyEvent.VK_O);
        registerKeyName("P", KeyEvent.VK_P);
        registerKeyName("Q", KeyEvent.VK_Q);
        registerKeyName("R", KeyEvent.VK_R);
        registerKeyName("S", KeyEvent.VK_S);
        registerKeyName("T", KeyEvent.VK_T);
        registerKeyName("U", KeyEvent.VK_U);
        registerKeyName("V", KeyEvent.VK_V);
        registerKeyName("W", KeyEvent.VK_W);
        registerKeyName("X", KeyEvent.VK_X);
        registerKeyName("Y", KeyEvent.VK_Y);
        registerKeyName("Z", KeyEvent.VK_Z);
        registerKeyName("OPEN_BRACKET", KeyEvent.VK_OPEN_BRACKET);
        registerKeyName("BACK_SLASH", KeyEvent.VK_BACK_SLASH);
        registerKeyName("CLOSE_BRACKET", KeyEvent.VK_CLOSE_BRACKET);
        registerKeyName("NUMPAD0", KeyEvent.VK_NUMPAD0);
        registerKeyName("NUMPAD1", KeyEvent.VK_NUMPAD1);
        registerKeyName("NUMPAD2", KeyEvent.VK_NUMPAD2);
        registerKeyName("NUMPAD3", KeyEvent.VK_NUMPAD3);
        registerKeyName("NUMPAD4", KeyEvent.VK_NUMPAD4);
        registerKeyName("NUMPAD5", KeyEvent.VK_NUMPAD5);
        registerKeyName("NUMPAD6", KeyEvent.VK_NUMPAD6);
        registerKeyName("NUMPAD7", KeyEvent.VK_NUMPAD7);
        registerKeyName("NUMPAD8", KeyEvent.VK_NUMPAD8);
        registerKeyName("NUMPAD9", KeyEvent.VK_NUMPAD9);
        registerKeyName("MULTIPLY", KeyEvent.VK_MULTIPLY);
        registerKeyName("ADD", KeyEvent.VK_ADD);
        registerKeyName("SEPARATER", KeyEvent.VK_SEPARATER);
        registerKeyName("SEPARATOR", KeyEvent.VK_SEPARATOR);
        registerKeyName("SUBTRACT", KeyEvent.VK_SUBTRACT);
        registerKeyName("DECIMAL", KeyEvent.VK_DECIMAL);
        registerKeyName("DIVIDE", KeyEvent.VK_DIVIDE);
        registerKeyName("DELETE", KeyEvent.VK_DELETE);
        registerKeyName("NUM_LOCK", KeyEvent.VK_NUM_LOCK);
        registerKeyName("SCROLL_LOCK", KeyEvent.VK_SCROLL_LOCK);
        registerKeyName("F1", KeyEvent.VK_F1);
        registerKeyName("F2", KeyEvent.VK_F2);
        registerKeyName("F3", KeyEvent.VK_F3);
        registerKeyName("F4", KeyEvent.VK_F4);
        registerKeyName("F5", KeyEvent.VK_F5);
        registerKeyName("F6", KeyEvent.VK_F6);
        registerKeyName("F7", KeyEvent.VK_F7);
        registerKeyName("F8", KeyEvent.VK_F8);
        registerKeyName("F9", KeyEvent.VK_F9);
        registerKeyName("F10", KeyEvent.VK_F10);
        registerKeyName("F11", KeyEvent.VK_F11);
        registerKeyName("F12", KeyEvent.VK_F12);
        registerKeyName("F13", KeyEvent.VK_F13);
        registerKeyName("F14", KeyEvent.VK_F14);
        registerKeyName("F15", KeyEvent.VK_F15);
        registerKeyName("F16", KeyEvent.VK_F16);
        registerKeyName("F17", KeyEvent.VK_F17);
        registerKeyName("F18", KeyEvent.VK_F18);
        registerKeyName("F19", KeyEvent.VK_F19);
        registerKeyName("F20", KeyEvent.VK_F20);
        registerKeyName("F21", KeyEvent.VK_F21);
        registerKeyName("F22", KeyEvent.VK_F22);
        registerKeyName("F23", KeyEvent.VK_F23);
        registerKeyName("F24", KeyEvent.VK_F24);
        registerKeyName("PRINTSCREEN", KeyEvent.VK_PRINTSCREEN);
        registerKeyName("INSERT", KeyEvent.VK_INSERT);
        registerKeyName("HELP", KeyEvent.VK_HELP);
        registerKeyName("META", KeyEvent.VK_META);
        registerKeyName("BACK_QUOTE", KeyEvent.VK_BACK_QUOTE);
        registerKeyName("QUOTE", KeyEvent.VK_QUOTE);
        registerKeyName("KP_UP", KeyEvent.VK_KP_UP);
        registerKeyName("KP_DOWN", KeyEvent.VK_KP_DOWN);
        registerKeyName("KP_LEFT", KeyEvent.VK_KP_LEFT);
        registerKeyName("KP_RIGHT", KeyEvent.VK_KP_RIGHT);
        registerKeyName("DEAD_GRAVE", KeyEvent.VK_DEAD_GRAVE);
        registerKeyName("DEAD_ACUTE", KeyEvent.VK_DEAD_ACUTE);
        registerKeyName("DEAD_CIRCUMFLEX", KeyEvent.VK_DEAD_CIRCUMFLEX);
        registerKeyName("DEAD_TILDE", KeyEvent.VK_DEAD_TILDE);
        registerKeyName("DEAD_MACRON", KeyEvent.VK_DEAD_MACRON);
        registerKeyName("DEAD_BREVE", KeyEvent.VK_DEAD_BREVE);
        registerKeyName("DEAD_ABOVEDOT", KeyEvent.VK_DEAD_ABOVEDOT);
        registerKeyName("DEAD_DIAERESIS", KeyEvent.VK_DEAD_DIAERESIS);
        registerKeyName("DEAD_ABOVERING", KeyEvent.VK_DEAD_ABOVERING);
        registerKeyName("DEAD_DOUBLEACUTE", KeyEvent.VK_DEAD_DOUBLEACUTE);
        registerKeyName("DEAD_CARON", KeyEvent.VK_DEAD_CARON);
        registerKeyName("DEAD_CEDILLA", KeyEvent.VK_DEAD_CEDILLA);
        registerKeyName("DEAD_OGONEK", KeyEvent.VK_DEAD_OGONEK);
        registerKeyName("DEAD_IOTA", KeyEvent.VK_DEAD_IOTA);
        registerKeyName("DEAD_VOICED_SOUND", KeyEvent.VK_DEAD_VOICED_SOUND);
        registerKeyName("DEAD_SEMIVOICED_SOUND", KeyEvent.VK_DEAD_SEMIVOICED_SOUND);
        registerKeyName("AMPERSAND", KeyEvent.VK_AMPERSAND);
        registerKeyName("ASTERISK", KeyEvent.VK_ASTERISK);
        registerKeyName("QUOTEDBL", KeyEvent.VK_QUOTEDBL);
        registerKeyName("LESS", KeyEvent.VK_LESS);
        registerKeyName("GREATER", KeyEvent.VK_GREATER);
        registerKeyName("BRACELEFT", KeyEvent.VK_BRACELEFT);
        registerKeyName("BRACERIGHT", KeyEvent.VK_BRACERIGHT);
        registerKeyName("AT", KeyEvent.VK_AT);
        registerKeyName("COLON", KeyEvent.VK_COLON);
        registerKeyName("CIRCUMFLEX", KeyEvent.VK_CIRCUMFLEX);
        registerKeyName("DOLLAR", KeyEvent.VK_DOLLAR);
        registerKeyName("EURO_SIGN", KeyEvent.VK_EURO_SIGN);
        registerKeyName("EXCLAMATION_MARK", KeyEvent.VK_EXCLAMATION_MARK);
        registerKeyName("INVERTED_EXCLAMATION_MARK", KeyEvent.VK_INVERTED_EXCLAMATION_MARK);
        registerKeyName("LEFT_PARENTHESIS", KeyEvent.VK_LEFT_PARENTHESIS);
        registerKeyName("NUMBER_SIGN", KeyEvent.VK_NUMBER_SIGN);
        registerKeyName("PLUS", KeyEvent.VK_PLUS);
        registerKeyName("RIGHT_PARENTHESIS", KeyEvent.VK_RIGHT_PARENTHESIS);
        registerKeyName("UNDERSCORE", KeyEvent.VK_UNDERSCORE);
        registerKeyName("WINDOWS", KeyEvent.VK_WINDOWS);
        registerKeyName("CONTEXT_MENU", KeyEvent.VK_CONTEXT_MENU);
        registerKeyName("FINAL", KeyEvent.VK_FINAL);
        registerKeyName("CONVERT", KeyEvent.VK_CONVERT);
        registerKeyName("NONCONVERT", KeyEvent.VK_NONCONVERT);
        registerKeyName("ACCEPT", KeyEvent.VK_ACCEPT);
        registerKeyName("MODECHANGE", KeyEvent.VK_MODECHANGE);
        registerKeyName("KANA", KeyEvent.VK_KANA);
        registerKeyName("KANJI", KeyEvent.VK_KANJI);
        registerKeyName("ALPHANUMERIC", KeyEvent.VK_ALPHANUMERIC);
        registerKeyName("KATAKANA", KeyEvent.VK_KATAKANA);
        registerKeyName("HIRAGANA", KeyEvent.VK_HIRAGANA);
        registerKeyName("FULL_WIDTH", KeyEvent.VK_FULL_WIDTH);
        registerKeyName("HALF_WIDTH", KeyEvent.VK_HALF_WIDTH);
        registerKeyName("ROMAN_CHARACTERS", KeyEvent.VK_ROMAN_CHARACTERS);
        registerKeyName("ALL_CANDIDATES", KeyEvent.VK_ALL_CANDIDATES);
        registerKeyName("PREVIOUS_CANDIDATE", KeyEvent.VK_PREVIOUS_CANDIDATE);
        registerKeyName("CODE_INPUT", KeyEvent.VK_CODE_INPUT);
        registerKeyName("JAPANESE_KATAKANA", KeyEvent.VK_JAPANESE_KATAKANA);
        registerKeyName("JAPANESE_HIRAGANA", KeyEvent.VK_JAPANESE_HIRAGANA);
        registerKeyName("JAPANESE_ROMAN", KeyEvent.VK_JAPANESE_ROMAN);
        registerKeyName("KANA_LOCK", KeyEvent.VK_KANA_LOCK);
        registerKeyName("INPUT_METHOD_ON_OFF", KeyEvent.VK_INPUT_METHOD_ON_OFF);
        registerKeyName("CUT", KeyEvent.VK_CUT);
        registerKeyName("COPY", KeyEvent.VK_COPY);
        registerKeyName("PASTE", KeyEvent.VK_PASTE);
        registerKeyName("UNDO", KeyEvent.VK_UNDO);
        registerKeyName("AGAIN", KeyEvent.VK_AGAIN);
        registerKeyName("FIND", KeyEvent.VK_FIND);
        registerKeyName("PROPS", KeyEvent.VK_PROPS);
        registerKeyName("STOP", KeyEvent.VK_STOP);
        registerKeyName("COMPOSE", KeyEvent.VK_COMPOSE);
        registerKeyName("ALT_GRAPH", KeyEvent.VK_ALT_GRAPH);
        registerKeyName("BEGIN", KeyEvent.VK_BEGIN);
        registerKeyName("UNDEFINED", KeyEvent.VK_UNDEFINED);
    }

    private static void registerKeyName(String name, int code) {
        keyCodes.put(name, Integer.valueOf(code));
        keyNames.put(Integer.valueOf(code), name);
    }

    private static Map<String, Integer> getKeyCodes() {
        initializeKeyNames();
        return keyCodes;
    }

    private static Map<Integer, String> getKeyNames() {
        initializeKeyNames();
        return keyNames;
    }

    private static void initializeKeyNames() {
        if (keyCodes != null) {
            return;
        }
        keyCodes = new HashMap<>();
        keyNames = new HashMap<>();
        registerKeyNames();
    }
    protected static void registerSubclass(Class<?> subclass) {}
}
