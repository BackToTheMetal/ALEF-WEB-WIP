package java.util.concurrent.atomic;

/** A reference and boolean mark stored on the single executing thread. */
public class AtomicMarkableReference<V> {
  private V reference;
  private boolean mark;

  public AtomicMarkableReference(V initialRef, boolean initialMark) {
    reference = initialRef;
    mark = initialMark;
  }

  public V getReference() {
    return reference;
  }

  public boolean isMarked() {
    return mark;
  }

  public V get(boolean[] markHolder) {
    markHolder[0] = mark;
    return reference;
  }

  public boolean compareAndSet(
      V expectedReference, V newReference, boolean expectedMark, boolean newMark) {
    if (reference != expectedReference || mark != expectedMark) {
      return false;
    }
    reference = newReference;
    mark = newMark;
    return true;
  }

  public boolean weakCompareAndSet(
      V expectedReference, V newReference, boolean expectedMark, boolean newMark) {
    return compareAndSet(expectedReference, newReference, expectedMark, newMark);
  }

  public void set(V newReference, boolean newMark) {
    reference = newReference;
    mark = newMark;
  }

  public boolean attemptMark(V expectedReference, boolean newMark) {
    if (reference != expectedReference) {
      return false;
    }
    mark = newMark;
    return true;
  }
}
