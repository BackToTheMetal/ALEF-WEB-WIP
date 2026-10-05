package alefj2cl.example;

import java.util.Arrays;

public class ComparisonTextDiffTest {
  private static void check(String text, String other, int... ranges) {
    int[] actual = ComparisonTextDiff.ranges(text, other);
    if (!Arrays.equals(actual, ranges)) throw new AssertionError(text + ": " + Arrays.toString(actual));
  }
  private static void visible(String text, int[] changed, int... expected) {
    int[] actual = ComparisonTextDiff.withoutWhitespace(text, changed);
    if (!Arrays.equals(actual, expected)) throw new AssertionError("Visible text: " + text + ": " + Arrays.toString(actual));
  }
  public static void main(String[] args) {
    check("same", "same");
    check("Model", "Model fix");
    check("Model fix", "Model", 5, 9);
    check("old name", "new name", 0, 3);
    check("alpha beta gamma", "alpha BETA gamma", 6, 10);
    check("aXbYc", "a1b2c", 1, 2, 3, 4);
    check("", "new");
    check("removed", "", 0, 7);
    check("a😀b", "a😎b", 1, 3);
    check("école", "ecole", 0, 1);
    check("ab", "ba", 0, 1);
    if (!Arrays.equals(ComparisonTextDiff.addedRanges("ab", "ba"), new int[]{1, 2})) throw new AssertionError("Both sides must retain the same b");
    if (!Arrays.equals(ComparisonTextDiff.addedRanges("Model", "Model fix"), new int[]{5, 9})) throw new AssertionError("Only the inserted suffix is green");
    check("same " + "x".repeat(1500) + " tail", "same " + "y".repeat(1500) + " tail", 5, 1505);
    visible("", new int[]{0, 0});
    visible(" \t\n\r\u00a0\u2003\u202f ", new int[]{0, 8});
    visible("  New rule  ", new int[]{0, 12}, 2, 5, 6, 10);
    visible("a\tb\u00a0c", new int[]{0, 5}, 0, 1, 2, 3, 4, 5);
    visible(" 😀 name ", new int[]{0, 9}, 1, 3, 4, 8);
    visible("Model fix", ComparisonTextDiff.addedRanges("Model", "Model fix"), 6, 9);
    visible("Model fix", ComparisonTextDiff.ranges("Model fix", "Model"), 6, 9);
    visible("a b", ComparisonTextDiff.addedRanges("ab", "a b"));
    visible("unchanged", new int[0]);
    visible("aX bY", new int[]{1, 3, 4, 5}, 1, 2, 4, 5);
    System.out.println("PASS: text-range alignment, insertions, deletions, multiple hunks, Unicode, bounded large cells and uncoloured whitespace");
  }
}
