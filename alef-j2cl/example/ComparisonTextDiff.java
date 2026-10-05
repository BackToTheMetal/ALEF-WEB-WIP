package alefj2cl.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** UTF-16 ranges from one shared alignment of the two rendered MPS labels. */
final class ComparisonTextDiff {
  static int[] ranges(String before, String after) { return diff(before, after)[0]; }
  static int[] addedRanges(String before, String after) { return diff(before, after)[1]; }

  /** Colour changed text, leaving generated spacing and empty cells unpainted. */
  static int[] withoutWhitespace(String text, int[] ranges) {
    List<Integer> result = new ArrayList<>();
    for (int r = 0; r < ranges.length; r += 2) {
      int start = -1;
      for (int i = ranges[r]; i < ranges[r + 1]; i++) {
        char character = text.charAt(i);
        boolean space = Character.isWhitespace(character) || Character.isSpaceChar(character);
        if (!space && start < 0) start = i;
        if (space && start >= 0) { result.add(start); result.add(i); start = -1; }
      }
      if (start >= 0) { result.add(start); result.add(ranges[r + 1]); }
    }
    return result.stream().mapToInt(Integer::intValue).toArray();
  }

  private static int[][] diff(String before, String after) {
    int[] a = codePoints(before), b = codePoints(after);
    int prefix = 0;
    while (prefix < a.length && prefix < b.length && a[prefix] == b[prefix]) prefix++;
    int n = a.length, m = b.length;
    while (n > prefix && m > prefix && a[n - 1] == b[m - 1]) { n--; m--; }
    int[] aOffsets = offsets(a), bOffsets = offsets(b);
    if ((long) (n - prefix) * (m - prefix) > 1000000) return new int[][]{
        n == prefix ? new int[0] : new int[]{aOffsets[prefix], aOffsets[n]},
        m == prefix ? new int[0] : new int[]{bOffsets[prefix], bOffsets[m]}};
    int[][] lengths = new int[n - prefix + 1][m - prefix + 1];
    for (int i = n - 1; i >= prefix; i--) for (int j = m - 1; j >= prefix; j--)
      lengths[i - prefix][j - prefix] = a[i] == b[j]
          ? 1 + lengths[i - prefix + 1][j - prefix + 1]
          : Math.max(lengths[i - prefix + 1][j - prefix], lengths[i - prefix][j - prefix + 1]);
    boolean[] removed = new boolean[a.length], added = new boolean[b.length];
    int i = prefix, j = prefix;
    while (i < n || j < m) {
      if (i < n && j < m && a[i] == b[j]) { i++; j++; }
      else if (i < n && (j == m || lengths[i - prefix + 1][j - prefix] >= lengths[i - prefix][j - prefix + 1])) removed[i++] = true;
      else added[j++] = true;
    }
    return new int[][]{ranges(removed, aOffsets), ranges(added, bOffsets)};
  }

  private static int[] offsets(int[] points) {
    int[] result = new int[points.length + 1];
    for (int i = 0; i < points.length; i++) result[i + 1] = result[i] + Character.charCount(points[i]);
    return result;
  }

  private static int[] codePoints(String text) {
    int[] result = new int[text.length()];
    int count = 0;
    for (int i = 0; i < text.length(); i++) {
      char value = text.charAt(i);
      if (Character.isHighSurrogate(value) && i + 1 < text.length() && Character.isLowSurrogate(text.charAt(i + 1)))
        result[count++] = 0x10000 + ((value - 0xd800) << 10) + text.charAt(++i) - 0xdc00;
      else result[count++] = value;
    }
    return Arrays.copyOf(result, count);
  }

  private static int[] ranges(boolean[] changed, int[] offsets) {
    List<Integer> result = new ArrayList<>();
    for (int i = 0; i < changed.length; i++) {
      if (!changed[i]) continue;
      result.add(offsets[i]);
      while (i + 1 < changed.length && changed[i + 1]) i++;
      result.add(offsets[i + 1]);
    }
    return result.stream().mapToInt(Integer::intValue).toArray();
  }
}
