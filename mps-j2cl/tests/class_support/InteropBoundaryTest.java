package mpsj2cl.classsupport;

import java.lang.reflect.Array;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/** Browser-only checks for native type identities that upstream J2CL deliberately erases. */
public final class InteropBoundaryTest {
  @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
  public static class NativeObject {
    public NativeObject() {}
  }

  @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
  public interface NativeView {}

  @JsFunction
  public interface NativeFunction { void run(); }

  public static void run() {
    NativeObject object = new NativeObject();
    NativeFunction function = () -> {};
    Class<?>[] erasedTypes = {NativeObject.class, NativeView.class, NativeFunction.class};
    for (Class<?> erased : erasedTypes) {
      require(Object.class.isAssignableFrom(erased), "native classes are references");
      require(!isNullInstance(erased), "null is never a native instance");
      require(erased.cast(null) == null, "null native cast");
      unavailable(() -> erased.isAssignableFrom(erased));
      unavailable(() -> erased.isAssignableFrom(String.class));
      unavailable(() -> Array.newInstance(erased, 1));
    }
    require(Object.class.isInstance(object) && Object.class.isInstance(function), "native Object instance");
    unavailable(() -> NativeObject.class.isInstance(object));
    unavailable(() -> NativeFunction.class.isInstance(function));
  }

  // Exercise Class.isInstance itself; replacing the call with a constant defeats this boundary test.
  @SuppressWarnings("IsInstanceOfClass")
  private static boolean isNullInstance(Class<?> type) { return type.isInstance(null); }

  private static void unavailable(Runnable operation) {
    try { operation.run(); }
    catch (UnsupportedOperationException expected) { return; }
    throw new AssertionError("Erased native Class relation was guessed");
  }

  private static void require(boolean condition, String message) {
    if (!condition) { throw new AssertionError(message); }
  }
}
