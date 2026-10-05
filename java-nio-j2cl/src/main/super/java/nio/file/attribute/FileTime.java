package java.nio.file.attribute;

/** Signature-only time value; conversions and factories require a future implementation. */
public final class FileTime implements Comparable<FileTime> {
  private FileTime() {}
  public static FileTime from(long value, java.util.concurrent.TimeUnit unit) { throw unsupported("from(long,TimeUnit)"); }
  public static FileTime from(java.time.Instant instant) { throw unsupported("from(Instant)"); }
  public static FileTime fromMillis(long value) { throw unsupported("fromMillis(long)"); }
  public long to(java.util.concurrent.TimeUnit unit) { throw unsupported("to(TimeUnit)"); }
  public long toMillis() { throw unsupported("toMillis()"); }
  public java.time.Instant toInstant() { throw unsupported("toInstant()"); }
  @Override public int compareTo(FileTime other) { throw unsupported("compareTo(FileTime)"); }
  @Override public boolean equals(Object other) { throw unsupported("equals(Object)"); }
  @Override public int hashCode() { throw unsupported("hashCode()"); }
  @Override public String toString() { throw unsupported("toString()"); }
  private static UnsupportedOperationException unsupported(String method) { return javaemul.internal.nio.UnsupportedNio.operation("java.nio.file.attribute.FileTime." + method); }
}
