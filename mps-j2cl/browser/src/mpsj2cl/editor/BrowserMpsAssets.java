package mpsj2cl.editor;

import com.intellij.compat.BrowserIcons;
import com.intellij.openapi.Disposable;
import java.util.Map;
import javax.swing.Icon;

/** Scoped installation of the original resources used by MPSIcons. */
public final class BrowserMpsAssets implements Disposable {
  private final Map<String, Icon> assets = BrowserMpsAssetData.create();
  public BrowserMpsAssets() { assets.forEach(BrowserIcons::register); }
  public int size() { return assets.size(); }
  @Override public void dispose() { assets.forEach(BrowserIcons::unregister); assets.clear(); }
}
