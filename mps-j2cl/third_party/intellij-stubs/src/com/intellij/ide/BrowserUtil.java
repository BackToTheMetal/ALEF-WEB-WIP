package com.intellij.ide;
import java.util.function.Consumer;
public final class BrowserUtil {
  private static Consumer<String> opener = value -> { throw new UnsupportedOperationException("Browser host must install an external-link opener"); };
  private BrowserUtil() {}
  public static void setBrowserOpener(Consumer<String> value) { opener = java.util.Objects.requireNonNull(value); }
  public static void browse(String url) { opener.accept(url); }
  public static void launchBrowser(String url) { browse(url); }
  public static void browse(java.net.URI uri) { browse(uri.toString()); }
  public static void browse(java.net.URL url) { browse(url.toString()); }
  public static boolean isAbsoluteURL(String url) { return url.indexOf(':') > 0; }
}
