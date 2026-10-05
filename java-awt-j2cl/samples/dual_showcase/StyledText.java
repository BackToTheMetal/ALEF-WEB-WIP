package org.webawt.samples.dualshowcase;

import java.text.AttributedCharacterIterator;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Shared sample iterator with real attribute runs and an optional nonzero begin index. */
public final class StyledText implements AttributedCharacterIterator {
    private final String text;
    private final int begin;
    private final int[] limits;
    private final List<Map<Attribute, Object>> runs;
    private int index;

    public StyledText(String text, int begin, int[] limits, List<Map<Attribute, Object>> runs) {
        this.text = text;
        this.begin = begin;
        this.limits = java.util.Arrays.copyOf(limits, limits.length);
        this.runs = new java.util.ArrayList<>();
        for (Map<Attribute, Object> run : runs) {
            this.runs.add(Collections.unmodifiableMap(new HashMap<>(run)));
        }
        index = begin;
    }
    private int run() {
        int position = Math.min(index - begin, Math.max(0, text.length() - 1));
        int result = 0;
        while (result < limits.length - 1 && position >= limits[result]) { result++; }
        return result;
    }
    public char first() { return setIndex(begin); }
    public char last() { return setIndex(begin + Math.max(0, text.length() - 1)); }
    public char current() { return index == getEndIndex() ? DONE : text.charAt(index - begin); }
    public char next() { return setIndex(Math.min(getEndIndex(), index + 1)); }
    public char previous() { return index == begin ? DONE : setIndex(index - 1); }
    public char setIndex(int position) {
        if (position < begin || position > getEndIndex()) { throw new IllegalArgumentException("position"); }
        index = position;
        return current();
    }
    public int getBeginIndex() { return begin; }
    public int getEndIndex() { return begin + text.length(); }
    public int getIndex() { return index; }
    public int getRunStart() { return begin + (run() == 0 ? 0 : limits[run() - 1]); }
    public int getRunStart(Attribute attribute) { return getRunStart(); }
    public int getRunStart(Set<? extends Attribute> attributes) { return getRunStart(); }
    public int getRunLimit() { return begin + limits[run()]; }
    public int getRunLimit(Attribute attribute) { return getRunLimit(); }
    public int getRunLimit(Set<? extends Attribute> attributes) { return getRunLimit(); }
    public Map<Attribute, Object> getAttributes() { return runs.get(run()); }
    public Object getAttribute(Attribute attribute) { return getAttributes().get(attribute); }
    public Set<Attribute> getAllAttributeKeys() {
        Set<Attribute> result = new HashSet<>();
        for (Map<Attribute, Object> attributes : runs) { result.addAll(attributes.keySet()); }
        return result;
    }
    public Object clone() { StyledText copy = new StyledText(text, begin, limits, runs); copy.index = index; return copy; }
}
