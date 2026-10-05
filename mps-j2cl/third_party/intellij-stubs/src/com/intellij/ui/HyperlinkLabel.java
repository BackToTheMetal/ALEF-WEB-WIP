package com.intellij.ui;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
public class HyperlinkLabel extends JButton {
  private final List<HyperlinkListener> listeners = new ArrayList<>();
  public HyperlinkLabel(String text) {
    super(text); setBorder(javax.swing.BorderFactory.createEmptyBorder()); setOpaque(false); setForeground(JBColor.BLUE);
    addActionListener(ignored -> { HyperlinkEvent event = new HyperlinkEvent(this, HyperlinkEvent.EventType.ACTIVATED, null, getText()); for (HyperlinkListener listener : new ArrayList<>(listeners)) listener.hyperlinkUpdate(event); });
  }
  public void addHyperlinkListener(HyperlinkListener listener) { listeners.add(listener); }
  public void removeHyperlinkListener(HyperlinkListener listener) { listeners.remove(listener); }
}
