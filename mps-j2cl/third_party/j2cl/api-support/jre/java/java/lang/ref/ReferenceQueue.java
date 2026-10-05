package java.lang.ref;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

/**
 * A real pollable queue. Native weak references are checked when poll is called;
 * collection is not simulated and never depends on a finalizer running.
 * Blocking remove is unavailable on the browser event thread.
 */
public class ReferenceQueue<T> {
  private final ArrayDeque<Reference<? extends T>> pending = new ArrayDeque<>();
  private final List<Reference.NativeWeakRef<Reference<? extends T>>> watched = new ArrayList<>();

  public ReferenceQueue() {}
  @SuppressWarnings("unchecked")
  void register(Reference<?> reference) {
    watched.add(new Reference.NativeWeakRef<>((Reference<? extends T>) reference));
  }
  @SuppressWarnings("unchecked")
  void enqueueReference(Reference<?> reference) { pending.add((Reference<? extends T>) reference); }
  public Reference<? extends T> poll() {
    for (int i = watched.size() - 1; i >= 0; i--) {
      Reference<?> reference = watched.get(i).deref();
      if (reference == null || reference.checkCollected()) watched.remove(i);
    }
    Reference<? extends T> reference = pending.poll();
    if (reference != null) reference.dequeued();
    return reference;
  }
  public Reference<? extends T> remove() throws InterruptedException { return remove(0); }
  public Reference<? extends T> remove(long timeout) throws InterruptedException {
    if (timeout < 0) throw new IllegalArgumentException("Negative timeout");
    Reference<? extends T> ready = poll();
    if (ready != null) return ready;
    throw new UnsupportedOperationException("ReferenceQueue.remove cannot block the browser event thread; use poll");
  }
}
