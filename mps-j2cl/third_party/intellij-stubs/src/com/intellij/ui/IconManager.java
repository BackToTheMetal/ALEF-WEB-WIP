package com.intellij.ui;
import com.intellij.compat.BrowserIcons;
import javax.swing.Icon;
public class IconManager {
  private static final IconManager INSTANCE = new IconManager();
  public static IconManager getInstance() { return INSTANCE; }
  public Icon getIcon(String path, ClassLoader loader) { return BrowserIcons.named(path); }
}
