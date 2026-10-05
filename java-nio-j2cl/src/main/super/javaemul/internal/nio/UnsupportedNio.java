package javaemul.internal.nio;

/** Shared failure contract for operations outside this source compatibility profile. */
public final class UnsupportedNio {
  private UnsupportedNio() {}

  public static UnsupportedOperationException operation(String member) {
    return new UnsupportedOperationException("J2CL NIO stub: " + member);
  }
}
