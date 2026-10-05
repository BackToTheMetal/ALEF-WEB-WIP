package alefj2cl;

/** Use the browser's Unicode normalization and mark categories for identifiers. */
public final class BrowserUnicode {
  private BrowserUnicode() {}
  public static String stripMarks(String text) {
    java.util.Objects.requireNonNull(text);
    return new NativeString(new NativeString(text).normalize("NFD"))
        .replace(new NativeRegExp("\\p{M}", "gu"), "");
  }
  @jsinterop.annotations.JsType(isNative = true, namespace = jsinterop.annotations.JsPackage.GLOBAL, name = "String")
  private static class NativeString {
    public NativeString(String text) {}
    public native String normalize(String form);
    public native String replace(NativeRegExp pattern, String replacement);
  }
  @jsinterop.annotations.JsType(isNative = true, namespace = jsinterop.annotations.JsPackage.GLOBAL, name = "RegExp")
  private static class NativeRegExp {
    public NativeRegExp(String pattern, String flags) {}
  }
}
