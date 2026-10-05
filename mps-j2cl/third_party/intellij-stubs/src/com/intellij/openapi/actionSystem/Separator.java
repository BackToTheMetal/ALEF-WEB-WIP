package com.intellij.openapi.actionSystem;
public final class Separator extends AnAction {
  private static final Separator INSTANCE = new Separator();
  public Separator() {}
  public Separator(String text) { super(text); }
  public static Separator getInstance() { return INSTANCE; }
  public static Separator create() { return new Separator(); }
  public static Separator create(String text) { return new Separator(text); }
  @Override public void actionPerformed(AnActionEvent event) { throw new UnsupportedOperationException("Separator is not executable"); }
}
