package java.io;

/** Error wrapper retained for source compatibility. */
public class IOError extends Error {
  private static final long serialVersionUID = 67100927991680413L;

  public IOError(Throwable cause) {
    super(cause);
  }
}
