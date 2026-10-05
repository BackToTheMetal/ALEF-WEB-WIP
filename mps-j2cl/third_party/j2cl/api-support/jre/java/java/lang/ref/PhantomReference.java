package java.lang.ref;

/** Native weak liveness with an inaccessible referent and pollable collection notification. */
public class PhantomReference<T> extends Reference<T> {
  public PhantomReference(T referent, ReferenceQueue<? super T> queue) { super(referent, queue, false); }
  @Override public T get() { return null; }
}
