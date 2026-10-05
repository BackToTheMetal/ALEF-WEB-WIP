package com.intellij.psi.codeStyle;

import com.intellij.openapi.util.TextRange;
import com.intellij.util.containers.FList;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Browser completion subset: literal runs, '*' gaps, and identifier word/camel boundaries. */
public final class NameUtil {
  private NameUtil() {}
  public enum MatchingCaseSensitivity { NONE, FIRST_LETTER, ALL }
  public static MatcherBuilder buildMatcher(String pattern) { return new MatcherBuilder(pattern); }
  public static MinusculeMatcher buildMatcher(String pattern, MatchingCaseSensitivity sensitivity) {
    return buildMatcher(pattern).withCaseSensitivity(sensitivity).build();
  }
  public static final class MatcherBuilder {
    private final String pattern;
    private MatchingCaseSensitivity sensitivity = MatchingCaseSensitivity.NONE;
    private MatcherBuilder(String pattern) { this.pattern = Objects.requireNonNull(pattern); }
    public MatcherBuilder withCaseSensitivity(MatchingCaseSensitivity value) { sensitivity = Objects.requireNonNull(value); return this; }
    public MinusculeMatcher build() { return new BrowserMatcher(pattern, sensitivity); }
  }

  private static final class BrowserMatcher extends MinusculeMatcher {
    private final String pattern;
    private final MatchingCaseSensitivity sensitivity;
    BrowserMatcher(String pattern, MatchingCaseSensitivity sensitivity) { this.pattern = pattern; this.sensitivity = sensitivity; }
    @Override public String getPattern() { return pattern; }
    @Override public FList<TextRange> matchingFragments(String name) {
      Objects.requireNonNull(name);
      int[][] positions = new int[pattern.length() + 1][name.length() + 1];
      boolean[][] visited = new boolean[pattern.length() + 1][name.length() + 1];
      if (!match(name, 0, 0, positions, visited)) return null;
      List<TextRange> ranges = new ArrayList<>();
      int i = 0, cursor = 0, start = -1, end = -1;
      while (i < pattern.length()) {
        if (pattern.charAt(i) == '*') { i++; continue; }
        int at = positions[i][cursor];
        if (start < 0) { start = at; end = at + 1; }
        else if (at == end) end++;
        else { ranges.add(new TextRange(start, end)); start = at; end = at + 1; }
        cursor = at + 1; i++;
      }
      if (start >= 0) ranges.add(new TextRange(start, end));
      FList<TextRange> result = FList.emptyList();
      for (int j = ranges.size() - 1; j >= 0; j--) result = result.prepend(ranges.get(j));
      return result;
    }
    private boolean match(String name, int i, int cursor, int[][] positions, boolean[][] visited) {
      while (i < pattern.length() && pattern.charAt(i) == '*') i++;
      if (i == pattern.length()) return true;
      if (visited[i][cursor]) return positions[i][cursor] >= 0;
      visited[i][cursor] = true;
      positions[i][cursor] = -1;
      boolean gap = i > 0 && pattern.charAt(i - 1) == '*';
      for (int at = cursor; at < name.length(); at++) {
        if (at != cursor && !gap && (i == 0 || !boundary(name, at))) continue;
        if (!equal(pattern.charAt(i), name.charAt(at), i)) continue;
        if (match(name, i + 1, at + 1, positions, visited)) { positions[i][cursor] = at; return true; }
      }
      return false;
    }
    private boolean equal(char expected, char actual, int index) {
      if (expected == actual) return true;
      boolean firstLiteral = true;
      for (int i = 0; i < index; i++) if (pattern.charAt(i) != '*') firstLiteral = false;
      if (sensitivity == MatchingCaseSensitivity.ALL || sensitivity == MatchingCaseSensitivity.FIRST_LETTER && firstLiteral) return false;
      return Character.toLowerCase(expected) == Character.toLowerCase(actual) || Character.toUpperCase(expected) == Character.toUpperCase(actual);
    }
    private boolean boundary(String name, int at) {
      if (at == 0) return true;
      char previous = name.charAt(at - 1), current = name.charAt(at);
      return !Character.isLetterOrDigit(previous) || !Character.isLetterOrDigit(current)
          || Character.isLowerCase(previous) && Character.isUpperCase(current)
          || Character.isDigit(previous) != Character.isDigit(current)
          || Character.isUpperCase(current) && Character.isUpperCase(previous)
             && at + 1 < name.length() && Character.isLowerCase(name.charAt(at + 1));
    }
  }
}
