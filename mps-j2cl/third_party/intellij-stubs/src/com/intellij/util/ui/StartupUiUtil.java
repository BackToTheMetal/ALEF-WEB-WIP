package com.intellij.util.ui;
import java.awt.Font;
import javax.swing.UIManager;
public final class StartupUiUtil {
  private StartupUiUtil() {}
  public static Font getLabelFont() { Font font = UIManager.getFont("Label.font"); return font == null ? new Font("Dialog", Font.PLAIN, 12) : font; }
  public static boolean isUnderDarcula() { return UIUtil.isUnderDarcula(); }
}
