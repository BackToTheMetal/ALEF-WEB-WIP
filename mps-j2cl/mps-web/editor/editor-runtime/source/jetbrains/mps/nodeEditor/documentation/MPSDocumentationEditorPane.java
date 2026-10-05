/* Copyright 2000-2023 JetBrains s.r.o. Licensed under the Apache License, Version 2.0. */
package jetbrains.mps.nodeEditor.documentation;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.FontSize;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.util.Disposer;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.UIManager;

/** Readable documentation using WebSwing's plain-text HTML profile and real font metrics. */
public final class MPSDocumentationEditorPane extends JEditorPane implements Disposable {
  private JBPopup hint;
  public MPSDocumentationEditorPane() {
    super("text/html", "");
    setEditable(false);
    setFont(UIManager.getFont("Label.font"));
    setBackground(UIManager.getColor("Panel.background"));
    setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
  }
  int getPreferredWidth() {
    int length = getDocument().getLength();
    return 300 + Math.min(800, Math.max(0, length - 200)) / 4;
  }
  int getPreferredHeight() {
    FontMetrics metrics = getFontMetrics(getFont());
    int available = getPreferredWidth() - getInsets().left - getInsets().right;
    int lines = 0;
    for (String line : getText().split("\n", -1)) {
      lines += Math.max(1, (metrics.stringWidth(line) + available - 1) / available);
    }
    return lines * metrics.getHeight() + getInsets().top + getInsets().bottom;
  }
  @Override public Dimension getPreferredSize() { return new Dimension(getPreferredWidth(), getPreferredHeight()); }
  public void setHint(JBPopup value) {
    hint = value;
    Disposer.register(value, () -> { if (hint == value) hint = null; });
  }
  public void applyFontProps(FontSize size) {
    setFont(new Font(getFont().getName(), Font.PLAIN, size.getSize()));
    revalidate(); repaint();
  }
  @Override public void dispose() {
    hint = null;
    getCaret().setVisible(false);
  }
}
