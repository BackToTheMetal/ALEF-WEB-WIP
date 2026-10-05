package java.text;

public final class StringCharacterIterator implements CharacterIterator {
    private String text;
    private int begin;
    private int end;
    private int position;

    public StringCharacterIterator(String text) {
        this(text, 0);
    }

    public StringCharacterIterator(String text, int position) {
        this(text, 0, requireText(text).length(), position);
    }

    public StringCharacterIterator(String text, int begin, int end, int position) {
        validate(text, begin, end, position);
        this.text = text;
        this.begin = begin;
        this.end = end;
        this.position = position;
    }

    public void setText(String text) {
        this.text = requireText(text);
        begin = 0;
        end = text.length();
        position = 0;
    }

    @Override
    public char first() {
        position = begin;
        return current();
    }

    @Override
    public char last() {
        position = end == begin ? end : end - 1;
        return current();
    }

    @Override
    public char setIndex(int position) {
        if (position < begin || position > end) {
            throw new IllegalArgumentException("Invalid index");
        }
        this.position = position;
        return current();
    }

    @Override
    public char current() {
        return position >= begin && position < end ? text.charAt(position) : DONE;
    }

    @Override
    public char next() {
        if (position < end - 1) {
            position++;
            return text.charAt(position);
        }
        position = end;
        return DONE;
    }

    @Override
    public char previous() {
        if (position > begin) {
            position--;
            return text.charAt(position);
        }
        return DONE;
    }

    @Override
    public int getBeginIndex() {
        return begin;
    }

    @Override
    public int getEndIndex() {
        return end;
    }

    @Override
    public int getIndex() {
        return position;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof StringCharacterIterator)) {
            return false;
        }
        StringCharacterIterator iterator = (StringCharacterIterator) object;
        return text.equals(iterator.text) && begin == iterator.begin && end == iterator.end && position == iterator.position;
    }

    @Override
    public int hashCode() {
        return text.hashCode() ^ begin ^ end ^ position;
    }

    public Object clone() {
        return new StringCharacterIterator(text, begin, end, position);
    }

    private static String requireText(String text) {
        if (text == null) {
            throw new NullPointerException("text");
        }
        return text;
    }

    private static void validate(String text, int begin, int end, int position) {
        int length = requireText(text).length();
        if (begin < 0 || begin > end || end > length || position < begin || position > end) {
            throw new IllegalArgumentException("Invalid substring range");
        }
    }
}
