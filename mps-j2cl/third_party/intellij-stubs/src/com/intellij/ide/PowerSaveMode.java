package com.intellij.ide;
public final class PowerSaveMode {
  private static boolean enabled;
  private PowerSaveMode() {}
  public static boolean isEnabled() { return enabled; }
  public static void setEnabled(boolean value) { enabled = value; }
}
