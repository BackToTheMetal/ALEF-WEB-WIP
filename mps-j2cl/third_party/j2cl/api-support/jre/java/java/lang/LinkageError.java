package java.lang;

/** Base class for errors linking Java class definitions. */
public class LinkageError extends Error {
  public LinkageError() {}
  public LinkageError(String message) { super(message); }
  public LinkageError(String message, Throwable cause) { super(message, cause); }
}
