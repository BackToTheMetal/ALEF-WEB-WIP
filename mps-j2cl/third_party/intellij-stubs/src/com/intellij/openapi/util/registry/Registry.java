package com.intellij.openapi.util.registry;
public final class Registry {
  private static final java.util.Map<String, Boolean> flags = new java.util.HashMap<>();
  private Registry() {}
  public static boolean is(String key) { return is(key, false); }
  public static boolean is(String key, boolean fallback) { return flags.getOrDefault(key, fallback); }
  public static void setBrowserFlag(String key, boolean value) { flags.put(key, value); }
}
