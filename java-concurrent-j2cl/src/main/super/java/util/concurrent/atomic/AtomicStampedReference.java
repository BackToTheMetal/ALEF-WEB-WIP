package java.util.concurrent.atomic;

/** A reference and integer stamp stored on the single executing thread. */
public class AtomicStampedReference<V> {
  private V reference;
  private int stamp;

  public AtomicStampedReference(V initialRef, int initialStamp) {
    reference = initialRef;
    stamp = initialStamp;
  }

  public V getReference() {
    return reference;
  }

  public int getStamp() {
    return stamp;
  }

  public V get(int[] stampHolder) {
    stampHolder[0] = stamp;
    return reference;
  }

  public boolean compareAndSet(
      V expectedReference, V newReference, int expectedStamp, int newStamp) {
    if (reference != expectedReference || stamp != expectedStamp) {
      return false;
    }
    reference = newReference;
    stamp = newStamp;
    return true;
  }

  public boolean weakCompareAndSet(
      V expectedReference, V newReference, int expectedStamp, int newStamp) {
    return compareAndSet(expectedReference, newReference, expectedStamp, newStamp);
  }

  public void set(V newReference, int newStamp) {
    reference = newReference;
    stamp = newStamp;
  }

  public boolean attemptStamp(V expectedReference, int newStamp) {
    if (reference != expectedReference) {
      return false;
    }
    stamp = newStamp;
    return true;
  }
}
