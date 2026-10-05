package com.intellij.util;
import java.util.Arrays;
public final class ArrayUtil {
  public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
  public static final String[] EMPTY_STRING_ARRAY = new String[0];
  private ArrayUtil() {}
  public static <T> T[] remove(T[] values, T value) {
    for (int i = 0; i < values.length; i++) if (java.util.Objects.equals(values[i], value)) return remove(values, i);
    return values;
  }
  public static <T> T[] remove(T[] values, int index) {
    if (index < 0 || index >= values.length) throw new IndexOutOfBoundsException();
    T[] result = Arrays.copyOf(values, values.length - 1);
    System.arraycopy(values, index + 1, result, index, values.length - index - 1);
    return result;
  }
}
