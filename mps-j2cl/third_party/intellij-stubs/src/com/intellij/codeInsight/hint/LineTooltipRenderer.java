package com.intellij.codeInsight.hint;
import com.intellij.openapi.editor.Editor;
import com.intellij.ui.HintHint;
import com.intellij.ui.LightweightHint;
import java.awt.Point;
import javax.swing.JLabel;
import javax.swing.JPanel;
public class LineTooltipRenderer implements TooltipRenderer {
  protected String myText;
  protected final int myCurrentWidth;
  public LineTooltipRenderer(String text, Object[] identity) { this(text, 0, identity); }
  public LineTooltipRenderer(String text, int width, Object[] identity) { myText = text; myCurrentWidth = width; }
  public interface TooltipReloader { void reload(boolean expand); }
  @Override public LightweightHint show(Editor editor, Point point, boolean align, TooltipGroup group, HintHint hint) {
    LightweightHint result = createHint(editor, point, align, group, hint, true, false, null);
    result.show(editor.getContentComponent(), point.x, point.y, null, hint); return result;
  }
  public LightweightHint createHint(Editor editor, Point point, boolean align, TooltipGroup group, HintHint hint, boolean actions, boolean expanded, TooltipReloader reloader) {
    JPanel panel = new JPanel(new java.awt.BorderLayout());
    panel.add(new JLabel(dressDescription(editor, myText, expanded)), java.awt.BorderLayout.CENTER);
    return new LightweightHint(panel);
  }
  protected String dressDescription(Editor editor, String text, boolean expanded) { return text; }
  public void addBelow(String text) { myText += "<br>" + text; }
  public String getText() { return myText; }
  public LineTooltipRenderer createRenderer(String text, int width) { return new LineTooltipRenderer(text, width, new Object[]{text}); }
}
