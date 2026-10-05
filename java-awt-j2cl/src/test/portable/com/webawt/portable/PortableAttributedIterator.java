package com.webawt.portable;

import java.awt.Font;
import java.awt.font.TextAttribute;
import java.text.AttributedCharacterIterator;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public final class PortableAttributedIterator implements AttributedCharacterIterator {
    private final Font font;
    private final String text;
    private int index;

    public PortableAttributedIterator(String text, Font font) {
        this.text = text;
        this.font = font;
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
        PortableAttributedIterator copy = new PortableAttributedIterator(text, font);
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
        return Collections.<Attribute, Object>singletonMap(TextAttribute.FONT, font);
    }

    @Override
    public Object getAttribute(Attribute attribute) {
        return attribute == TextAttribute.FONT ? font : null;
    }

    @Override
    public Set<Attribute> getAllAttributeKeys() {
        return Collections.<Attribute>singleton(TextAttribute.FONT);
    }
}
