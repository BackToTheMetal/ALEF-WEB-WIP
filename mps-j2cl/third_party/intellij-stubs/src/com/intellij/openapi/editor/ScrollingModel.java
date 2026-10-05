package com.intellij.openapi.editor;
import com.intellij.openapi.editor.event.VisibleAreaListener;
import java.awt.Rectangle;
public interface ScrollingModel {
  Rectangle getVisibleArea(); Rectangle getVisibleAreaOnScrollingFinished();
  void scrollToCaret(ScrollType type); void scrollTo(LogicalPosition position, ScrollType type);
  void runActionOnScrollingFinished(Runnable action); void disableAnimation(); void enableAnimation();
  int getVerticalScrollOffset(); int getHorizontalScrollOffset();
  void scrollVertically(int offset); void scrollHorizontally(int offset); void scroll(int horizontal, int vertical);
  void addVisibleAreaListener(VisibleAreaListener listener); void removeVisibleAreaListener(VisibleAreaListener listener);
}
