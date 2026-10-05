package com.intellij.ide;
public final class IdeBundle {
  private IdeBundle() {}
  public static String message(String key, Object... values) { return "title.popup.new.element".equals(key) ? "New Element" : key; }
}
