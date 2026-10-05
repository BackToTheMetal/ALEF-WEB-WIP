package java.lang.ref;

/**
 * Browser soft-cache policy: retain until explicit clear/enqueue or owner disposal.
 * Browsers expose no memory-pressure notification equivalent to the JVM soft policy.
 */
public class SoftReference<T> extends Reference<T> {
  public SoftReference(T referent) { super(referent, null, true); }
  public SoftReference(T referent, ReferenceQueue<? super T> queue) { super(referent, queue, true); }
}
