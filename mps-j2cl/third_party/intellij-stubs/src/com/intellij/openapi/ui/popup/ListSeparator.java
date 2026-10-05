package com.intellij.openapi.ui.popup;
public class ListSeparator {
  private final String text;
  public ListSeparator() { this(null); }
  public ListSeparator(String text) { this.text = text; }
  public String getText() { return text; }
}
