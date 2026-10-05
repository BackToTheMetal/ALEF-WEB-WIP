package alefj2cl.extensions;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/** Scoped Object.clone equivalent for the pinned J2CL JavaScript object layout. */
public final class ObjectCopy {
  @SuppressWarnings("unchecked")
  public static <T> T copy(T source) throws CloneNotSupportedException {
    if (!(source instanceof Cloneable)) throw new CloneNotSupportedException();
    // Java instance fields are enumerable; J2CL's identity hash is non-enumerable.
    // Preserve the concrete prototype and shallow field references, without copying identity.
    return (T) NativeObject.assign(NativeObject.create(NativeObject.getPrototypeOf(source)), source);
  }

  @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
  private static class NativeObject {
    public static native Object assign(Object target, Object source);
    public static native Object create(Object prototype);
    public static native Object getPrototypeOf(Object object);
  }
}
