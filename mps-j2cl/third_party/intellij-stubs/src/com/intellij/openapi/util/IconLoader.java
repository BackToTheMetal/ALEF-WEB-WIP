package com.intellij.openapi.util;
import com.intellij.compat.BrowserIcons;
import javax.swing.Icon;
public final class IconLoader {
  private IconLoader() {}
  public static Icon getIcon(String path, Class<?> owner) { return BrowserIcons.named(path); }
  public static Icon getIcon(String path, ClassLoader loader) { return BrowserIcons.named(path); }
  public static Icon findIcon(String path, Class<?> owner) { return BrowserIcons.named(path); }
  public static Icon findIcon(String path, ClassLoader loader) { return BrowserIcons.named(path); }
  public static Icon findIcon(java.net.URL url, boolean useCache) { return BrowserIcons.findRegistered(url.toExternalForm()); }
}
