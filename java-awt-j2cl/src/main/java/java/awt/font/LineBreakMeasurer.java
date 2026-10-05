package java.awt.font;

import java.text.AttributedCharacterIterator;
import java.text.BreakIterator;

public final class LineBreakMeasurer {
    private final FontRenderContext context;
    private final BreakIterator breakIterator;
    private TextMeasurer measurer;
    private int position;

    public LineBreakMeasurer(AttributedCharacterIterator text, FontRenderContext context) {
        this(text, BreakIterator.getLineInstance(), context);
    }

    public LineBreakMeasurer(AttributedCharacterIterator text, BreakIterator breakIterator, FontRenderContext context) {
        if (text == null || breakIterator == null || context == null) {
            throw new IllegalArgumentException("text, breakIterator, and context must not be null");
        }
        this.context = context;
        this.breakIterator = breakIterator;
        breakIterator.setText((AttributedCharacterIterator) text.clone());
        measurer = new TextMeasurer(text, context);
        position = text.getBeginIndex();
    }

    public int nextOffset(float wrappingWidth) {
        return nextOffset(wrappingWidth, measurer.getEndIndex(), false);
    }

    public int nextOffset(float wrappingWidth, int offsetLimit, boolean requireNextWord) {
        if (offsetLimit <= position || offsetLimit > measurer.getEndIndex()) {
            throw new IllegalArgumentException("offsetLimit must follow the current position and remain within the text");
        }
        int measured = Math.min(offsetLimit, measurer.getLineBreakIndex(position, wrappingWidth));
        if (measured == offsetLimit) {
            return offsetLimit;
        }
        String text = measurer.getText();
        int begin = measurer.getBeginIndex();
        int candidate = breakIterator.isBoundary(measured) ? measured : breakIterator.preceding(measured);
        if (candidate > position) {
            return candidate;
        }
        if (requireNextWord) {
            return position;
        }
        if (measured > position) {
            return measured;
        }
        int next = position + Character.charCount(text.codePointAt(position - begin));
        while (next < offsetLimit && TextMeasurer.isCombining(text.codePointAt(next - begin))) {
            next += Character.charCount(text.codePointAt(next - begin));
        }
        return Math.min(next, offsetLimit);
    }

    public TextLayout nextLayout(float wrappingWidth) {
        return nextLayout(wrappingWidth, measurer.getEndIndex(), false);
    }

    public TextLayout nextLayout(float wrappingWidth, int offsetLimit, boolean requireNextWord) {
        if (position == measurer.getEndIndex()) {
            return null;
        }
        int next = nextOffset(wrappingWidth, offsetLimit, requireNextWord);
        if (next == position) {
            return null;
        }
        TextLayout layout = measurer.getLayout(position, next);
        position = next;
        return layout;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int newPosition) {
        if (newPosition < measurer.getBeginIndex() || newPosition > measurer.getEndIndex()) {
            throw new IllegalArgumentException("Invalid position: " + newPosition);
        }
        position = newPosition;
    }

    public void insertChar(AttributedCharacterIterator newParagraph, int insertPos) {
        measurer.insertChar(newParagraph, insertPos);
        breakIterator.setText((AttributedCharacterIterator) newParagraph.clone());
        if (position >= insertPos) {
            position++;
        }
    }

    public void deleteChar(AttributedCharacterIterator newParagraph, int deletePos) {
        measurer.deleteChar(newParagraph, deletePos);
        breakIterator.setText((AttributedCharacterIterator) newParagraph.clone());
        if (position > deletePos) {
            position--;
        }
        position = Math.min(position, measurer.getEndIndex());
    }
}
