package com.intellij.ui;
import java.awt.Color;
public final class Gray extends Color {
  public static final Gray _0 = get(0), _64 = get(64), _128 = get(128), _192 = get(192), _255 = get(255);
  private Gray(int level, int alpha) { super(level, level, level, alpha); }
  public static Gray get(int level) { return new Gray(level, 255); }
  public static Gray get(int level, int alpha) { return new Gray(level, alpha); }
}
