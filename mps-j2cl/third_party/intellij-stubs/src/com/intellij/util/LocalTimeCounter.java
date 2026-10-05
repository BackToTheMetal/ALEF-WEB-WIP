package com.intellij.util;
public final class LocalTimeCounter {
  private static long last;
  private LocalTimeCounter() {}
  public static long currentTime() { last = Math.max(last + 1, System.currentTimeMillis()); return last; }
}
