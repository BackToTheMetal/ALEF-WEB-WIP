package com.intellij.openapi.editor.event;
import com.intellij.openapi.editor.Editor;
import java.awt.Rectangle;
public class VisibleAreaEvent extends java.util.EventObject {
  private final Rectangle oldRectangle, newRectangle;
  public VisibleAreaEvent(Editor editor, Rectangle oldRectangle, Rectangle newRectangle) { super(editor); this.oldRectangle = oldRectangle; this.newRectangle = newRectangle; }
  public Editor getEditor() { return (Editor) getSource(); }
  public Rectangle getOldRectangle() { return oldRectangle; }
  public Rectangle getNewRectangle() { return newRectangle; }
}
