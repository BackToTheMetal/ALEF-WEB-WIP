package mpsj2cl.completion;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.codeStyle.MinusculeMatcher;
import com.intellij.psi.codeStyle.NameUtil;
import com.intellij.util.containers.FList;

public final class CompletionMatchingTest {
  private static void check(boolean value, String message) { if (!value) throw new AssertionError(message); }
  public static String run() {
    check(NameUtil.buildMatcher("print").build().matches("println"), "literal prefix");
    check(!NameUtil.buildMatcher("print").build().matches("sprint"), "unstarred prefix");
    MinusculeMatcher substring = NameUtil.buildMatcher("*print").build();
    check(substring.matches("sprint"), "star substring");
    check(!substring.isStartMatch("sprint"), "substring does not auto-substitute as prefix");
    check(substring.isStartMatch("println"), "prefix start");
    check(NameUtil.buildMatcher("NP").build().matches("NullPointer"), "camel initials");
    check(NameUtil.buildMatcher("np").build().matches("nullPointer"), "case insensitive initials");
    check(NameUtil.buildMatcher("NPE").build().matches("NullPointerException"), "three camel humps");
    check(NameUtil.buildMatcher("n_p").build().matches("node_property"), "literal underscore and run");
    check(!NameUtil.buildMatcher("nx").build().matches("node_property"), "absent character");
    check(NameUtil.buildMatcher("*").build().matches(""), "empty wildcard");
    check(NameUtil.buildMatcher("").build().matches("name"), "empty pattern");
    check(!NameUtil.buildMatcher("P", NameUtil.MatchingCaseSensitivity.ALL).matches("print"), "sensitive match");
    check(NameUtil.buildMatcher("p", NameUtil.MatchingCaseSensitivity.NONE).matches("Print"), "insensitive match");
    check(!NameUtil.buildMatcher("p", NameUtil.MatchingCaseSensitivity.FIRST_LETTER).matches("Print"), "first letter case");
    check(NameUtil.buildMatcher("*a*b").build().matches("zzacccb"), "multiple gaps");
    check(NameUtil.buildMatcher("*a*a*b").build().matches("aaab"), "backtracking with repeated letters");
    FList<TextRange> ranges = NameUtil.buildMatcher("NPE").build().matchingFragments("NullPointerException");
    check(ranges.size() == 3, "three fragments");
    check(ranges.get(0).equals(new TextRange(0, 1)) && ranges.get(1).equals(new TextRange(4, 5))
        && ranges.get(2).equals(new TextRange(11, 12)), "highlight positions");
    ranges = NameUtil.buildMatcher("*print").build().matchingFragments("sprint");
    check(ranges.size() == 1 && ranges.getHead().equals(new TextRange(1, 6)), "contiguous fragment");
    MinusculeMatcher rank = NameUtil.buildMatcher("*np").build();
    check(rank.matchingDegree("np") > rank.matchingDegree("npExtra"), "exact match rank");
    check(rank.matchingDegree("npExtra") > rank.matchingDegree("NullPointer"), "contiguous prefix rank");
    check(rank.matchingDegree("NullPointer") > rank.matchingDegree("anp"), "prefix hump rank over substring");
    check(rank.matchingDegree("none") == Integer.MIN_VALUE, "nonmatch rank");
    FList<String> original = FList.<String>emptyList().prepend("b").prepend("a");
    check(original.without("a").getHead().equals("b") && original.size() == 2, "persistent removal");
    try { original.add("c"); throw new AssertionError("immutable list"); } catch (UnsupportedOperationException expected) {}
    return "PASS";
  }
  public static void main(String[] args) { System.out.println(run()); }
}
