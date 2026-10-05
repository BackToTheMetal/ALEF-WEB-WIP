package com.intellij.ide;
import com.intellij.ui.HintHint;
import javax.swing.JEditorPane;
import javax.swing.JPopupMenu;
public final class IdeTooltipManager {
  private static final IdeTooltipManager INSTANCE = new IdeTooltipManager();
  private JPopupMenu current;
  public static IdeTooltipManager getInstance() { return INSTANCE; }
  public static JEditorPane initPane(String text, HintHint hint, Object renderer) {
    JEditorPane pane = new JEditorPane("text/html", text); pane.setEditable(false); return pane;
  }
  public void show(IdeTooltip tooltip, boolean now) { hideCurrentNow(); current = new JPopupMenu(); current.setRequestFocusEnabled(false); current.add(tooltip.getTipComponent()); java.awt.Point point = tooltip.getPoint(); current.show(tooltip.getComponent(), point.x, point.y); }
  public void hideCurrentNow() { if (current != null) { current.setVisible(false); current = null; } }
}
