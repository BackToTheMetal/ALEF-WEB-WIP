package java.text;

import java.util.Locale;

public abstract class BreakIterator implements Cloneable {
    public static final int DONE = -1;

    protected BreakIterator() {
    }

    public Object clone() {
        if (this instanceof SimpleBreakIterator) {
            return ((SimpleBreakIterator) this).copy();
        }
        throw new UnsupportedOperationException("Custom BreakIterator subclasses must provide clone support");
    }

    public abstract int first();
    public abstract int last();
    public abstract int next(int count);
    public abstract int next();
    public abstract int previous();
    public abstract int following(int offset);
    public abstract int current();

    public int preceding(int offset) {
        int boundary = following(offset);
        while (boundary >= offset && boundary != DONE) {
            boundary = previous();
        }
        return boundary;
    }

    public boolean isBoundary(int offset) {
        if (offset == getText().getBeginIndex()) {
            return true;
        }
        return following(offset - 1) == offset;
    }

    public abstract CharacterIterator getText();

    public void setText(String newText) {
        setText(new StringCharacterIterator(newText));
    }

    public abstract void setText(CharacterIterator newText);

    public static BreakIterator getWordInstance() {
        return getWordInstance(Locale.getDefault());
    }

    public static BreakIterator getWordInstance(Locale locale) {
        return new SimpleBreakIterator(Mode.WORD);
    }

    public static BreakIterator getLineInstance() {
        return getLineInstance(Locale.getDefault());
    }

    public static BreakIterator getLineInstance(Locale locale) {
        return new SimpleBreakIterator(Mode.LINE);
    }

    public static BreakIterator getCharacterInstance() {
        return getCharacterInstance(Locale.getDefault());
    }

    public static BreakIterator getCharacterInstance(Locale locale) {
        return new SimpleBreakIterator(Mode.CHARACTER);
    }

    public static BreakIterator getSentenceInstance() {
        return getSentenceInstance(Locale.getDefault());
    }

    public static BreakIterator getSentenceInstance(Locale locale) {
        return new SimpleBreakIterator(Mode.SENTENCE);
    }

    public static Locale[] getAvailableLocales() {
        return new Locale[] {Locale.getDefault()};
    }

    private enum Mode {
        CHARACTER,
        LINE,
        SENTENCE,
        WORD
    }

    private static final class SimpleBreakIterator extends BreakIterator {
        private final Mode mode;
        private CharacterIterator text = new StringCharacterIterator("");
        private int position;

        private SimpleBreakIterator(Mode mode) {
            this.mode = mode;
        }

        @Override
        public int first() {
            position = text.getBeginIndex();
            return position;
        }

        @Override
        public int last() {
            position = text.getEndIndex();
            return position;
        }

        @Override
        public int next(int count) {
            int result = position;
            if (count > 0) {
                for (int index = 0; index < count && result != DONE; index++) {
                    result = next();
                }
            } else {
                for (int index = 0; index > count && result != DONE; index--) {
                    result = previous();
                }
            }
            return result;
        }

        @Override
        public int next() {
            if (position >= text.getEndIndex()) {
                return DONE;
            }
            position++;
            while (position < text.getEndIndex() && !isBreak(position)) {
                position++;
            }
            return position;
        }

        @Override
        public int previous() {
            if (position <= text.getBeginIndex()) {
                return DONE;
            }
            position--;
            while (position > text.getBeginIndex() && !isBreak(position)) {
                position--;
            }
            return position;
        }

        @Override
        public int following(int offset) {
            if (offset < text.getBeginIndex() || offset > text.getEndIndex()) {
                throw new IllegalArgumentException("Offset is outside the text");
            }
            position = offset;
            return next();
        }

        @Override
        public int current() {
            return position;
        }

        @Override
        public CharacterIterator getText() {
            return (CharacterIterator) text.clone();
        }

        @Override
        public void setText(CharacterIterator newText) {
            if (newText == null) {
                throw new NullPointerException("newText");
            }
            text = (CharacterIterator) newText.clone();
            position = text.getBeginIndex();
        }

        private boolean isBreak(int offset) {
            if (mode == Mode.CHARACTER) {
                return true;
            }
            char before = text.setIndex(offset - 1);
            char after = text.setIndex(offset);
            if (mode == Mode.SENTENCE) {
                return before == '.' || before == '!' || before == '?';
            }
            return Character.isWhitespace(before) != Character.isWhitespace(after);
        }

        private SimpleBreakIterator copy() {
            SimpleBreakIterator copy = new SimpleBreakIterator(mode);
            copy.text = (CharacterIterator) text.clone();
            copy.position = position;
            return copy;
        }
    }
}
