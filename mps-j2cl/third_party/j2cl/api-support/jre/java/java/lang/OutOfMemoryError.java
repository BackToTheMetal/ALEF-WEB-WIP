package java.lang;

/** Explicit Java capacity failures; browser allocation failures remain host errors. */
public class OutOfMemoryError extends VirtualMachineError {
  public OutOfMemoryError() {}
  public OutOfMemoryError(String message) { super(message); }
}
