package com.intellij.psi.codeStyle;

import com.intellij.openapi.util.TextRange;
import com.intellij.util.containers.FList;
import com.intellij.util.text.Matcher;
import java.util.Objects;

/** Completion matching contract. Scoring is deterministic browser ranking, not IDE score parity. */
public abstract class MinusculeMatcher implements Matcher {
  public abstract String getPattern();
  public abstract FList<TextRange> matchingFragments(String name);
  @Override public boolean matches(String name) { return matchingFragments(name) != null; }
  public boolean isStartMatch(String name) { return isStartMatch(matchingFragments(name)); }
  public static boolean isStartMatch(Iterable<? extends TextRange> ranges) {
    if (ranges == null) return false;
    java.util.Iterator<? extends TextRange> iterator = ranges.iterator();
    return !iterator.hasNext() || iterator.next().getStartOffset() == 0;
  }
  public int matchingDegree(String name) { return matchingDegree(name, false); }
  public int matchingDegree(String name, boolean valueStartCaseMatch) {
    return matchingDegree(name, valueStartCaseMatch, matchingFragments(name));
  }
  public int matchingDegree(String name, boolean valueStartCaseMatch, FList<? extends TextRange> ranges) {
    Objects.requireNonNull(name);
    if (ranges == null) return Integer.MIN_VALUE;
    if (ranges.isEmpty()) return 0;
    int score = isStartMatch(ranges) ? 1000 : 0;
    int matched = 0;
    for (TextRange range : ranges) matched += range.getEndOffset() - range.getStartOffset();
    score += matched * 100 - ranges.size() * 15 - ranges.getHead().getStartOffset();
    if (ranges.size() == 1 && ranges.getHead().getStartOffset() == 0) score += 300;
    if (matched == name.length()) score += 500;
    String plain = getPattern().replace("*", "");
    if (!plain.isEmpty() && name.startsWith(plain)) score += valueStartCaseMatch ? 100 : 50;
    return score;
  }
}
