package com.intellij.codeInsight.documentation;
import com.intellij.openapi.options.FontSize;
import java.util.function.Consumer;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
public final class DocFontSizePopup {
  private DocFontSizePopup() {}
  public static void show(JComponent owner, Consumer<FontSize> selection) {
    JPopupMenu popup = new JPopupMenu();
    for (FontSize size : FontSize.values()) {
      JMenuItem item = new JMenuItem(Integer.toString(size.getSize()));
      item.addActionListener(event -> { DocumentationFontSize.setDocumentationFontSize(size); selection.accept(size); }); popup.add(item);
    }
    popup.show(owner, 0, owner.getHeight());
  }
}
