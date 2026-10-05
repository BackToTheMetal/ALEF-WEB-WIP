package com.intellij.ui.scale;
public final class JBUIScale {
  private static float userScale = 1;
  private JBUIScale() {}
  public static int scale(int value) { return Math.round(value * userScale); }
  public static float scale(float value) { return value * userScale; }
  public static float getUserScaleFactor() { return userScale; }
  public static void setUserScaleFactor(float value) { if (!(value > 0) || Float.isInfinite(value)) throw new IllegalArgumentException(); userScale = value; }
}
