package java.awt.font;

import java.awt.Font;
import java.text.AttributedCharacterIterator;
import java.util.Map;

public final class TextMeasurer implements Cloneable {
    private int beginIndex;
    private TextRunLayout runs;
    private final FontRenderContext context;
    private String text;

    public TextMeasurer(AttributedCharacterIterator iterator, FontRenderContext context) {
        if (iterator == null || context == null) {
            throw new IllegalArgumentException("iterator and context must not be null");
        }
        this.context = context;
        update(iterator);
    }

    public int getLineBreakIndex(int start, float maxAdvance) {
        checkPosition(start, true);
        int relativeStart = start - beginIndex;
        for (int index = relativeStart; index < text.length();) {
            int next = index + Character.charCount(text.codePointAt(index));
            while (next < text.length() && isCombining(text.codePointAt(next))) {
                next += Character.charCount(text.codePointAt(next));
            }
            if (runs.advance(relativeStart, next, context) > maxAdvance) {
                return beginIndex + index;
            }
            index = next;
        }
        return beginIndex + text.length();
    }

    public float getAdvanceBetween(int start, int limit) {
        checkRange(start, limit);
        return runs.advance(start - beginIndex, limit - beginIndex, context);
    }

    public TextLayout getLayout(int start, int limit) {
        checkRange(start, limit);
        if (start == limit) {
            throw new IllegalArgumentException("TextLayout requires at least one character");
        }
        return new TextLayout(runs.slice(start - beginIndex, limit - beginIndex), context);
    }

    public void insertChar(AttributedCharacterIterator newParagraph, int insertPos) {
        if (newParagraph == null) {
            throw new IllegalArgumentException("newParagraph must not be null");
        }
        if (insertPos < newParagraph.getBeginIndex() || insertPos >= newParagraph.getEndIndex()) {
            throw new IndexOutOfBoundsException("Invalid insertion position: " + insertPos);
        }
        update(newParagraph);
    }

    public void deleteChar(AttributedCharacterIterator newParagraph, int deletePos) {
        if (newParagraph == null) {
            throw new IllegalArgumentException("newParagraph must not be null");
        }
        if (deletePos < newParagraph.getBeginIndex() || deletePos > newParagraph.getEndIndex()) {
            throw new IndexOutOfBoundsException("Invalid deletion position: " + deletePos);
        }
        update(newParagraph);
    }

    int getBeginIndex() {
        return beginIndex;
    }

    int getEndIndex() {
        return beginIndex + text.length();
    }

    String getText() {
        return text;
    }

    protected Object clone() { return new TextMeasurer(this); }

    private TextMeasurer(TextMeasurer source) {
        context = source.context;
        runs = source.runs;
        text = source.text;
        beginIndex = source.beginIndex;
    }

    private void update(AttributedCharacterIterator iterator) {
        runs = new TextRunLayout(iterator);
        beginIndex = runs.begin;
        text = runs.text;
    }

    static boolean isCombining(int codePoint) {
        return codePoint >= 0x0300 && codePoint <= 0x036f || codePoint >= 0x1ab0 && codePoint <= 0x1aff || codePoint >= 0xfe00 && codePoint <= 0xfe0f || codePoint >= 0x1f3fb && codePoint <= 0x1f3ff;
    }

    private void checkRange(int start, int limit) {
        if (start < beginIndex || limit < start || limit > getEndIndex()) {
            throw new IndexOutOfBoundsException("Invalid text range: " + start + ".." + limit);
        }
    }

    private void checkPosition(int position, boolean allowEnd) {
        int maximum = allowEnd ? getEndIndex() : getEndIndex() - 1;
        if (position < beginIndex || position > maximum) {
            throw new IndexOutOfBoundsException("Invalid text position: " + position);
        }
    }
}
