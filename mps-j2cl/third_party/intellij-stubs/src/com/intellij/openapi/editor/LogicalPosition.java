package com.intellij.openapi.editor;
public class LogicalPosition implements Comparable<LogicalPosition> {
  public final int line, column;
  public LogicalPosition(int line, int column) { this.line = line; this.column = column; }
  @Override public int compareTo(LogicalPosition other) { int result = Integer.compare(line, other.line); return result == 0 ? Integer.compare(column, other.column) : result; }
  @Override public boolean equals(Object other) { return other instanceof LogicalPosition && line == ((LogicalPosition) other).line && column == ((LogicalPosition) other).column; }
  @Override public int hashCode() { return 31 * line + column; }
}
