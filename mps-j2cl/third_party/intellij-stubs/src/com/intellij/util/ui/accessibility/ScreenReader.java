package com.intellij.util.ui.accessibility;
public final class ScreenReader {
  private static boolean active;
  private ScreenReader() {}
  public static boolean isActive() { return active; }
  public static void setActive(boolean value) { active = value; }
}
