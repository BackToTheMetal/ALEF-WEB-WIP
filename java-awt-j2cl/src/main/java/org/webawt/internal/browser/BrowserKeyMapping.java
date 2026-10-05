package org.webawt.internal.browser;

import java.util.HashMap;
import java.util.Map;

/** Browser key names to AWT virtual keys. Kept below the public AWT dependency boundary. */
final class BrowserKeyMapping {
    private static final Map<String, Integer> NAMED = namedKeys();
    private static final Map<String, Integer> PUNCTUATION = punctuationKeys();

    private BrowserKeyMapping() {
    }

    static int keyCode(String key, String code, int location) {
        if (key == null || "Unidentified".equals(key) || "Process".equals(key) || "Dead".equals(key)) {
            return 0;
        }
        Integer named = NAMED.get(key);
        if (named != null) {
            return named;
        }
        if (location == 3 && code != null && code.startsWith("Numpad")) {
            int numpad = numpadCode(code);
            if (numpad != 0) {
                return numpad;
            }
        }
        if (key.length() != 1) {
            return 0;
        }
        char character = Character.toUpperCase(key.charAt(0));
        if (character >= 'A' && character <= 'Z' || character >= '0' && character <= '9' || character == ' ') {
            return character;
        }
        if (code != null && code.startsWith("Digit") && code.length() == 6) {
            return code.charAt(5);
        }
        Integer punctuation = PUNCTUATION.get(code);
        if (punctuation != null) {
            return punctuation;
        }
        if (character < 128 && code != null && code.startsWith("Key") && code.length() == 4) {
            return code.charAt(3);
        }
        return character >= 128 ? 0x01000000 + character : punctuationCode(character);
    }

    private static int punctuationCode(char key) {
        switch (key) {
            case ';': return 59;
            case '=': return 61;
            case ',': return 44;
            case '-': return 45;
            case '.': return 46;
            case '/': return 47;
            case '[': return 91;
            case '\\': return 92;
            case ']': return 93;
            case '`': return 192;
            case '\'': return 222;
            default: return 0;
        }
    }

    private static int numpadCode(String code) {
        if (code.length() == 7 && code.charAt(6) >= '0' && code.charAt(6) <= '9') {
            return 96 + code.charAt(6) - '0';
        }
        switch (code) {
            case "NumpadMultiply": return 106;
            case "NumpadAdd": return 107;
            case "NumpadComma": return 108;
            case "NumpadSubtract": return 109;
            case "NumpadDecimal": return 110;
            case "NumpadDivide": return 111;
            case "NumpadEnter": return 10;
            case "NumpadEqual": return 61;
            default: return 0;
        }
    }

    private static Map<String, Integer> namedKeys() {
        Map<String, Integer> keys = new HashMap<>();
        String[] names = {"Enter", "Tab", "Backspace", "Escape", "Shift", "Control", "Alt", "Meta", "AltGraph", "CapsLock", "NumLock", "ScrollLock", "Pause", "PrintScreen", "Insert", "Delete", "Home", "End", "PageUp", "PageDown", "ArrowLeft", "ArrowUp", "ArrowRight", "ArrowDown", "ContextMenu", "Clear", "Help", "Cancel", "Convert", "NonConvert", "Accept", "ModeChange"};
        int[] codes = {10, 9, 8, 27, 16, 17, 18, 157, 65406, 20, 144, 145, 19, 154, 155, 127, 36, 35, 33, 34, 37, 38, 39, 40, 525, 12, 156, 3, 28, 29, 30, 31};
        for (int index = 0; index < names.length; index++) {
            keys.put(names[index], codes[index]);
        }
        for (int number = 1; number <= 24; number++) {
            keys.put("F" + number, number <= 12 ? 111 + number : 61440 + number - 13);
        }
        return keys;
    }

    private static Map<String, Integer> punctuationKeys() {
        Map<String, Integer> keys = new HashMap<>();
        String[] names = {"Semicolon", "Equal", "Comma", "Minus", "Period", "Slash", "BracketLeft", "Backslash", "BracketRight", "Backquote", "Quote", "IntlBackslash"};
        int[] codes = {59, 61, 44, 45, 46, 47, 91, 92, 93, 192, 222, 92};
        for (int index = 0; index < names.length; index++) {
            keys.put(names[index], codes[index]);
        }
        return keys;
    }
}
