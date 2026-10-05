package java.awt.im;

import java.awt.font.TextAttribute;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class InputMethodHighlight {
    public static final int RAW_TEXT = 0;
    public static final int CONVERTED_TEXT = 1;
    public static final InputMethodHighlight UNSELECTED_RAW_TEXT_HIGHLIGHT = new InputMethodHighlight(false, RAW_TEXT);
    public static final InputMethodHighlight SELECTED_RAW_TEXT_HIGHLIGHT = new InputMethodHighlight(true, RAW_TEXT);
    public static final InputMethodHighlight UNSELECTED_CONVERTED_TEXT_HIGHLIGHT = new InputMethodHighlight(false, CONVERTED_TEXT);
    public static final InputMethodHighlight SELECTED_CONVERTED_TEXT_HIGHLIGHT = new InputMethodHighlight(true, CONVERTED_TEXT);
    private final boolean selected;
    private final int state;
    private final Map<TextAttribute, ?> style;
    private final int variation;

    public InputMethodHighlight(boolean selected, int state) {
        this(selected, state, 0, null);
    }

    public InputMethodHighlight(boolean selected, int state, int variation) {
        this(selected, state, variation, null);
    }

    public InputMethodHighlight(boolean selected, int state, int variation, Map<TextAttribute, ?> style) {
        if (state != RAW_TEXT && state != CONVERTED_TEXT) {
            throw new IllegalArgumentException("state must be RAW_TEXT or CONVERTED_TEXT");
        }
        if (variation < 0) {
            throw new IllegalArgumentException("variation must be non-negative");
        }
        this.selected = selected;
        this.state = state;
        this.variation = variation;
        this.style = style == null ? null : Collections.unmodifiableMap(new HashMap<TextAttribute, Object>(style));
    }

    public boolean isSelected() {
        return selected;
    }

    public int getState() {
        return state;
    }

    public int getVariation() {
        return variation;
    }

    public Map<TextAttribute, ?> getStyle() {
        return style;
    }
}
