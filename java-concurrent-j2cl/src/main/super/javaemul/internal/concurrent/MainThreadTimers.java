package javaemul.internal.concurrent;

import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;

/** J2CL's standard JsInterop bridge to main-thread browser timers. */
public final class MainThreadTimers {
  @JsFunction public interface Callback { void run(); }
  @JsMethod(namespace = JsPackage.GLOBAL, name = "setTimeout")
  public static native double setTimeout(Callback callback, double milliseconds);
  @JsMethod(namespace = JsPackage.GLOBAL, name = "clearTimeout")
  public static native void clearTimeout(double id);
  private MainThreadTimers() {}
}
