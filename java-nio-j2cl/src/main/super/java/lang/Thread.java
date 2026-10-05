// Signature dependency only; see compatibility/external-support.json.
package java.lang;

public class Thread implements Runnable {
  public Thread() { throw unsupported(); }
  public Thread(Runnable runnable) { throw unsupported(); }
  public void start() { throw unsupported(); }
  public void run() { throw unsupported(); }
  private static UnsupportedOperationException unsupported() { return javaemul.internal.nio.UnsupportedNio.operation("java.lang.Thread"); }
}
