package java.lang.ref;

/** Native weak object reference; primitive-backed Java values require strong retention. */
public class WeakReference<T> extends Reference<T> {
  public WeakReference(T referent) { super(referent, null, false); }
  public WeakReference(T referent, ReferenceQueue<? super T> queue) { super(referent, queue, false); }
}
