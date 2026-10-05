package org.webawt.internal.text;

import java.text.AttributedCharacterIterator;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public final class PlainAttributedCharacterIterator implements AttributedCharacterIterator {
    private final String text;
    private int index;

    public PlainAttributedCharacterIterator(String text) {
        if (text == null) {
            throw new NullPointerException("text");
        }
        this.text = text;
    }

    @Override
    public char first() {
        index = 0;
        return current();
    }

    @Override
    public char last() {
        index = text.isEmpty() ? 0 : text.length() - 1;
        return current();
    }

    @Override
    public char current() {
        return index < text.length() ? text.charAt(index) : DONE;
    }

    @Override
    public char next() {
        if (index < text.length()) {
            index++;
        }
        return current();
    }

    @Override
    public char previous() {
        if (index == 0) {
            return DONE;
        }
        index--;
        return current();
    }

    @Override
    public char setIndex(int position) {
        if (position < 0 || position > text.length()) {
            throw new IllegalArgumentException("Invalid iterator position: " + position);
        }
        index = position;
        return current();
    }

    @Override
    public int getBeginIndex() {
        return 0;
    }

    @Override
    public int getEndIndex() {
        return text.length();
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public Object clone() {
        PlainAttributedCharacterIterator copy = new PlainAttributedCharacterIterator(text);
        copy.index = index;
        return copy;
    }

    @Override
    public int getRunStart() {
        return 0;
    }

    @Override
    public int getRunStart(Attribute attribute) {
        return 0;
    }

    @Override
    public int getRunStart(Set<? extends Attribute> attributes) {
        return 0;
    }

    @Override
    public int getRunLimit() {
        return text.length();
    }

    @Override
    public int getRunLimit(Attribute attribute) {
        return text.length();
    }

    @Override
    public int getRunLimit(Set<? extends Attribute> attributes) {
        return text.length();
    }

    @Override
    public Map<Attribute, Object> getAttributes() {
        return Collections.emptyMap();
    }

    @Override
    public Object getAttribute(Attribute attribute) {
        return null;
    }

    @Override
    public Set<Attribute> getAllAttributeKeys() {
        return Collections.emptySet();
    }
}
