package com.intellij.openapi.ui.popup;
import com.intellij.openapi.ui.MessageType;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.popup.AbstractPopup;
import java.awt.Dimension;
import java.awt.Point;
import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkListener;
/** Lightweight help popup. Native windows and blocked click-through modality are unavailable. */
public final class BalloonBuilder {
  private final JEditorPane content;
  public BalloonBuilder(String text, MessageType type, HyperlinkListener links) {
    content = new JEditorPane("text/html", text); content.setEditable(false); content.setBackground(type.getPopupBackground());
    if (links != null) content.addHyperlinkListener(links);
  }
  public BalloonBuilder setBlockClicksThroughBalloon(boolean value) { if (value) throw new UnsupportedOperationException("A browser help popup cannot block the host page"); return this; }
  public BalloonBuilder setHideOnAction(boolean value) { return this; }
  public BalloonBuilder setHideOnClickOutside(boolean value) { if (!value) throw new UnsupportedOperationException("Persistent popups require a host panel"); return this; }
  public BalloonBuilder setHideOnFrameResize(boolean value) { return this; }
  public BalloonBuilder setHideOnKeyOutside(boolean value) { return this; }
  public BalloonBuilder setHideOnLinkClick(boolean value) { return this; }
  public BalloonBuilder setFadeoutTime(long milliseconds) { if (milliseconds != 0) throw new UnsupportedOperationException("Timed notification presentation belongs to the browser host"); return this; }
  public Balloon createBalloon() {
    AbstractPopup popup = new AbstractPopup(content, null);
    return new Balloon() {
      public void show(RelativePoint anchor, Position position) {
        Point point = anchor.getPoint(); Dimension size = content.getPreferredSize();
        if (position == Position.above) point.translate(-size.width / 2, -size.height);
        else if (position == Position.below) point.translate(-size.width / 2, 0);
        else if (position == Position.atLeft) point.translate(-size.width, -size.height / 2);
        else point.translate(0, -size.height / 2);
        popup.show(new RelativePoint(anchor.getComponent(), point));
      }
      public void hide() { popup.cancel(); }
      public boolean isDisposed() { return popup.isDisposed(); }
    };
  }
}
