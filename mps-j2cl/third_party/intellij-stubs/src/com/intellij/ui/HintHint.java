package com.intellij.ui;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.popup.Balloon.Position;
import java.awt.Component;
import java.awt.Point;
public final class HintHint {
  private final Component owner;
  private final Point point;
  private Position position = Position.below;
  private boolean focus;
  public HintHint() { this((Component) null, new Point()); }
  public HintHint(Component owner, Point point) { this.owner = owner; this.point = point; }
  public HintHint(Editor editor, Point point) { this(editor.getContentComponent(), point); }
  public HintHint(java.awt.event.MouseEvent event) { this((Component) event.getSource(), event.getPoint()); }
  public HintHint setAwtTooltip(boolean value) { return this; }
  public HintHint setPreferredPosition(Position value) { position = value; return this; }
  public Position getPreferredPosition() { return position; }
  public HintHint setRequestFocus(boolean value) { focus = value; return this; }
  public boolean isRequestFocus() { return focus; }
  public Component getOriginalComponent() { return owner; }
  public Point getOriginalPoint() { return point; }
  public HintHint setContentActive(boolean value) { return this; }
  public HintHint setForcePopup(boolean value) { return this; }
  public HintHint setShowImmediately(boolean value) { return this; }
}
