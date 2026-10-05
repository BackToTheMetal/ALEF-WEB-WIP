package java.lang.ref;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/** Browser reference storage. Native object referents require JavaScript WeakRef. */
public abstract class Reference<T> {
  private NativeWeakRef<T> weak;
  private T strong;
  private boolean active;
  private final ReferenceQueue<? super T> queue;
  private boolean enqueued;
  private boolean dequeued;

  Reference(T referent, ReferenceQueue<? super T> queue, boolean retain) {
    this.queue = queue;
    if (retain || referent == null || referent instanceof String || referent instanceof Boolean || referent instanceof Double) {
      strong = referent;
    } else {
      weak = new NativeWeakRef<>(referent);
      active = true;
    }
    if (queue != null && active) queue.register(this);
  }

  public T get() { return peek(); }
  final T peek() { return weak != null ? weak.deref() : strong; }
  public final boolean refersTo(T object) { return peek() == object; }
  public void clear() { weak = null; strong = null; active = false; }
  public boolean isEnqueued() { return enqueued; }
  public boolean enqueue() {
    if (queue == null || enqueued || dequeued) return false;
    clear();
    enqueued = true;
    queue.enqueueReference(this);
    return true;
  }
  final boolean checkCollected() {
    if (active && peek() == null) enqueue();
    return !active;
  }
  final void dequeued() { enqueued = false; dequeued = true; }

  /** JavaScript objects returned by WeakRef.deref remain alive through the current job. */
  public static void reachabilityFence(Object ref) {}

  @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "WeakRef")
  static final class NativeWeakRef<T> {
    NativeWeakRef(T value) {}
    native T deref();
  }
}
