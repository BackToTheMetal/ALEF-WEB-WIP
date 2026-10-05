package com.intellij.codeInsight.hint;
public final class TooltipGroup implements Comparable<TooltipGroup> {
  private final String name; private final int priority;
  public TooltipGroup(String name, int priority) { this.name = name; this.priority = priority; }
  @Override public int compareTo(TooltipGroup other) { return Integer.compare(priority, other.priority); }
  @Override public boolean equals(Object other) { return other instanceof TooltipGroup && name.equals(((TooltipGroup) other).name); }
  @Override public int hashCode() { return name.hashCode(); }
}
