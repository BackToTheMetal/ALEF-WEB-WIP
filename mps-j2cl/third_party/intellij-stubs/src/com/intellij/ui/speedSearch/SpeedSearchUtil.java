package com.intellij.ui.speedSearch;
import com.intellij.openapi.util.TextRange;
import com.intellij.ui.SimpleColoredComponent;
import com.intellij.ui.SimpleTextAttributes;
public final class SpeedSearchUtil {
  private SpeedSearchUtil() {}
  public static void appendColoredFragments(SimpleColoredComponent component, String text, Iterable<TextRange> ranges, SimpleTextAttributes base, SimpleTextAttributes match) {
    int end = 0;
    if (ranges != null) for (TextRange range : ranges) {
      int start = range.getStartOffset(), next = range.getEndOffset();
      if (start < end || next > text.length()) throw new IllegalArgumentException("Matching fragments must be ordered, nonoverlapping and inside the text");
      if (start > end) component.append(text.substring(end, start), base);
      if (next > start) component.append(text.substring(start, next), match);
      end = next;
    }
    if (end < text.length()) component.append(text.substring(end), base);
  }
}
