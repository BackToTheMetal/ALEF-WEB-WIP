package com.intellij.codeInsight.hint;
import com.intellij.openapi.editor.Editor;
import com.intellij.ui.HintHint;
import com.intellij.ui.LightweightHint;
import com.intellij.ui.awt.RelativePoint;
import java.awt.Point;
import java.awt.event.MouseEvent;
public final class TooltipController {
  private static final TooltipController INSTANCE = new TooltipController();
  private LightweightHint current;
  private TooltipGroup group;
  public static TooltipController getInstance() { return INSTANCE; }
  public void cancelTooltips() { if (current != null) current.hide(); current = null; group = null; }
  public void cancelTooltip(TooltipGroup target, MouseEvent event, boolean forced) { if (forced || target.equals(group)) cancelTooltips(); }
  public LightweightHint showTooltipByMouseMove(Editor editor, RelativePoint point, TooltipRenderer renderer, boolean align, TooltipGroup group, HintHint hint) {
    showTooltip(editor, point.getPoint(editor.getContentComponent()), renderer, align, group, hint); return current;
  }
  public void showTooltip(Editor editor, Point point, TooltipRenderer renderer, boolean align, TooltipGroup group) { showTooltip(editor, point, renderer, align, group, new HintHint(editor, point)); }
  public void showTooltip(Editor editor, Point point, TooltipRenderer renderer, boolean align, TooltipGroup group, HintHint hint) {
    cancelTooltips(); this.group = group; current = renderer.show(editor, point, align, group, hint);
  }
  public void hide(LightweightHint hint) { hint.hide(); if (hint == current) { current = null; group = null; } }
  public void resetCurrent() { cancelTooltips(); }
}
