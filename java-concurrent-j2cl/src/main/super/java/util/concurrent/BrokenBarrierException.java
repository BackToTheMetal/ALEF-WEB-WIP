package java.util.concurrent;

/** Indicates that a barrier generation failed or was interrupted. */
public class BrokenBarrierException extends Exception {
  public BrokenBarrierException() {}

  public BrokenBarrierException(String message) {
    super(message);
  }
}
