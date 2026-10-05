package java.awt.event;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.font.TextHitInfo;
import java.text.AttributedCharacterIterator;
import java.text.CharacterIterator;

public class InputMethodEvent extends AWTEvent {
    public static final int INPUT_METHOD_FIRST = 1100;
    public static final int INPUT_METHOD_TEXT_CHANGED = INPUT_METHOD_FIRST;
    public static final int CARET_POSITION_CHANGED = INPUT_METHOD_FIRST + 1;
    public static final int INPUT_METHOD_LAST = CARET_POSITION_CHANGED;
    private static final long serialVersionUID = 1L;
    private final long when;
    private final AttributedCharacterIterator text;
    private final int committedCharacterCount;
    private final TextHitInfo caret;
    private final TextHitInfo visiblePosition;

    public InputMethodEvent(Component source, int id, long when, AttributedCharacterIterator text, int committedCharacterCount, TextHitInfo caret, TextHitInfo visiblePosition) {
        super(source, id);
        validate(id, text, committedCharacterCount);
        this.when = when;
        this.text = text;
        this.committedCharacterCount = committedCharacterCount;
        this.caret = caret;
        this.visiblePosition = visiblePosition;
    }

    public InputMethodEvent(Component source, int id, AttributedCharacterIterator text, int committedCharacterCount, TextHitInfo caret, TextHitInfo visiblePosition) {
        this(source, id, EventQueue.getMostRecentEventTime(), text, committedCharacterCount, caret, visiblePosition);
    }

    public InputMethodEvent(Component source, int id, TextHitInfo caret, TextHitInfo visiblePosition) {
        this(source, id, EventQueue.getMostRecentEventTime(), null, 0, caret, visiblePosition);
    }

    public AttributedCharacterIterator getText() {
        return text;
    }

    public int getCommittedCharacterCount() {
        return committedCharacterCount;
    }

    public TextHitInfo getCaret() {
        return caret;
    }

    public TextHitInfo getVisiblePosition() {
        return visiblePosition;
    }

    @Override
    public void consume() {
        super.consume();
    }

    @Override
    public boolean isConsumed() {
        return super.isConsumed();
    }

    public long getWhen() {
        return when;
    }

    @Override
    public String paramString() {
        String type = id == INPUT_METHOD_TEXT_CHANGED ? "INPUT_METHOD_TEXT_CHANGED" : id == CARET_POSITION_CHANGED ? "CARET_POSITION_CHANGED" : "unknown type";
        String textDescription = text == null ? "no text" : describeText();
        String caretDescription = caret == null ? "no caret" : "caret: " + caret;
        String visibleDescription = visiblePosition == null ? "no visible position" : "visible position: " + visiblePosition;
        return type + ", " + textDescription + ", " + committedCharacterCount + " characters committed, " + caretDescription + ", " + visibleDescription;
    }

    private String describeText() {
        AttributedCharacterIterator iterator = (AttributedCharacterIterator) text.clone();
        StringBuilder committed = new StringBuilder();
        StringBuilder composed = new StringBuilder();
        int characterIndex = 0;
        for (char character = iterator.first(); character != CharacterIterator.DONE; character = iterator.next()) {
            if (characterIndex < committedCharacterCount) {
                committed.append(character);
            } else {
                composed.append(character);
            }
            characterIndex++;
        }
        return "\"" + committed + "\" + \"" + composed + "\"";
    }

    private static void validate(int id, AttributedCharacterIterator text, int committedCharacterCount) {
        if (id < INPUT_METHOD_FIRST || id > INPUT_METHOD_LAST) {
            throw new IllegalArgumentException("invalid input method event id");
        }
        int textLength = text == null ? 0 : text.getEndIndex() - text.getBeginIndex();
        if (committedCharacterCount < 0 || committedCharacterCount > textLength) {
            throw new IllegalArgumentException("invalid committed character count");
        }
        if (id == CARET_POSITION_CHANGED && text != null) {
            throw new IllegalArgumentException("caret position event must not contain text");
        }
    }
}
