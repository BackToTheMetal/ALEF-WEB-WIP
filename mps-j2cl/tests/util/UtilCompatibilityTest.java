package mpsj2cl.utilcompat;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import jetbrains.mps.util.ArrayWrapper;
import jetbrains.mps.util.NameUtil;
import jetbrains.mps.util.StringUtil;
import jetbrains.mps.util.WeakSet;
import jetbrains.mps.util.performance.PerformanceTracer;

public final class UtilCompatibilityTest {
  private static WeakSet<Object> garbageCollectionProbe;

  public static String run() {
    require(NameUtil.toValidIdentifier("A_name42").equals("A_name42"), "ASCII identifier");
    require(NameUtil.toValidIdentifier("42 a-b").equals("_42_a_b"), "identifier sanitation");
    require(NameUtil.toValidIdentifier("å-label").equals("å_label"), "Unicode sanitation");
    require(NameUtil.toValidIdentifier("\u0661\u03b1").equals("_\u0661\u03b1"), "Unicode digit and letter");
    require(NameUtil.toValidCamelIdentifier("foo-bar_baz").equals("fooBar_baz"), "camel sanitation");
    require(NameUtil.toValidCamelIdentifier("valid_name").equals("valid_name"), "valid underscore");
    require(NameUtil.toValidCamelIdentifier("foo-ß").equals("fooß"), "simple char casing cannot expand");
    require(NameUtil.captionWithNamingPolicy(" foo and 'bar BAZ' ").equals("Foo and 'bar BAZ'"), "quoted caption");
    require(NameUtil.captionWithNamingPolicy("foo\tbar and baz").equals("Foo Bar and Baz"), "token whitespace");
    require(NameUtil.multiWordDecapitalize("HTTPServer  MyName\tOther").equals("httpServer myName other"), "multiple words");
    require(StringUtil.checkShortTitleCapitalization("Start service"), "short title");
    require(!StringUtil.checkShortTitleCapitalization("start service"), "lowercase title");
    require(!StringUtil.checkShortTitleCapitalization("Start New Service"), "excess capitals");
    require(StringUtil.checkShortTitleCapitalization("Use XML and JSON"), "abbreviations");
    require(StringUtil.checkShortTitleCapitalization("Éditer modèle"), "Unicode title");

    ArrayWrapper<String> wrapper = new ArrayWrapper<String>() {
      @Override protected String[] getArray() { return new String[] {"one", "two"}; }
      @Override protected void setArray(String[] array) {}
      @Override protected String[] newArray(int size) { return new String[size]; }
    };
    CharSequence[] widened = wrapper.toArray(new CharSequence[0]);
    require(widened.getClass() == CharSequence[].class, "destination array runtime type");
    require(Arrays.equals(widened, new String[] {"one", "two"}), "destination contents");
    Object[] large = {null, null, "discard", "keep"};
    require(wrapper.toArray(large) == large && large[2] == null && large[3].equals("keep"), "large destination");
    boolean incompatible = false;
    try { incompatibleArray(wrapper); } catch (ArrayStoreException expected) { incompatible = true; }
    require(incompatible, "incompatible destination");

    WeakSet<Object> set = new WeakSet<>();
    Key first = new Key(1);
    Key equal = new Key(1);
    Key second = new Key(2);
    require(set.add(first) && !set.add(equal) && set.add(second), "weak equality uniqueness");
    require(set.contains(equal) && set.size() == 2, "weak lookup");
    require(set.add(null) && !set.add(null) && set.contains(null), "null weak key");
    require(set.add("primitive") && set.add(Boolean.TRUE) && set.add(Double.valueOf(3)), "primitive-backed values");
    Iterator<Object> iterator = set.iterator();
    int removed = 0;
    while (iterator.hasNext()) { iterator.next(); iterator.remove(); removed++; }
    require(removed == 6 && set.isEmpty(), "iterator removal");
    boolean repeatedRemove = false;
    try { iterator.remove(); } catch (IllegalStateException expected) { repeatedRemove = true; }
    require(repeatedRemove, "iterator remove state");
    set.add(first);
    Iterator<Object> failFast = set.iterator();
    set.add(second);
    boolean modified = false;
    try { failFast.next(); } catch (ConcurrentModificationException expected) { modified = true; }
    require(modified, "iterator concurrent modification");
    require(set.remove(equal) && !set.remove(equal), "equal-key removal");
    set.clear();
    require(set.isEmpty(), "clear");

    PerformanceTracer tracer = new PerformanceTracer("test");
    tracer.push("task");
    tracer.pop();
    require(tracer.report().startsWith("[test]\n") && tracer.report().contains("task:"), "performance report");
    return "PASS";
  }

  /** Invoke in a separate browser job before an explicit test-only GC request. */
  public static void prepareWeakProbe() {
    garbageCollectionProbe = new WeakSet<>();
    garbageCollectionProbe.add(new Key(999));
  }

  public static int weakProbeSize() {
    return garbageCollectionProbe.size();
  }

  private static void require(boolean condition, String message) {
    if (!condition) throw new AssertionError(message);
  }

  // An incompatible destination is intentional: ArrayStoreException is the behavior under test.
  @SuppressWarnings("CollectionToArraySafeParameter")
  private static void incompatibleArray(ArrayWrapper<String> wrapper) { wrapper.toArray(new Integer[0]); }

  private static final class Key {
    private final int value;
    Key(int value) { this.value = value; }
    @Override public int hashCode() { return value % 2; }
    @Override public boolean equals(Object object) {
      return object instanceof Key && ((Key) object).value == value;
    }
  }
}
