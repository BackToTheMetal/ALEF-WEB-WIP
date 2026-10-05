package mpsj2cl.intellij;
import javax.swing.SwingUtilities;
import org.webawt.WebAwt;
public final class IntelliJBrowserTest {
  private static String result = "RUNNING";
  public static String getResult() { return result; }
  public static void start() {
    WebAwt.install("intellij-contracts-root");
    SwingUtilities.invokeLater(() -> {
      try {
        String synchronous = IntelliJContractsTest.run();
        popupFocusContracts();
        IntelliJContractsTest.runAsync(() -> { result = "PASS: " + synchronous + "; popup focus contracts passed; asynchronous scheduler contracts passed"; WebAwt.uninstall(); }, IntelliJBrowserTest::fail);
      } catch (Throwable error) { fail(error); }
    });
  }
  /** Uses browser-backed Swing peers, including synchronous native focus/loss events. */
  private static void popupFocusContracts() {
    javax.swing.JWindow window = new javax.swing.JWindow();
    javax.swing.JPanel panel = new javax.swing.JPanel(null);
    javax.swing.JTextField editor = new javax.swing.JTextField("editor");
    javax.swing.JTextField other = new javax.swing.JTextField("other");
    editor.setBounds(10, 10, 160, 24); other.setBounds(10, 50, 160, 24);
    panel.add(editor); panel.add(other); window.add(panel);
    window.setBounds(10, 10, 300, 180); window.setVisible(true); window.validate();
    final int[] losses = {0};
    editor.addFocusListener(new java.awt.event.FocusAdapter() {
      @Override public void focusLost(java.awt.event.FocusEvent event) { losses[0]++; }
    });
    try {
      editor.requestFocusInWindow(); requireFocus(editor, "initial editor focus");
      javax.swing.JTextField content = new javax.swing.JTextField("documentation");
      com.intellij.openapi.ui.popup.JBPopup passive = new com.intellij.openapi.ui.popup.ComponentPopupBuilder(content, content)
          .setFocusable(true).setRequestFocus(false).createPopup();
      passive.showUnderneathOf(editor);
      requireFocus(editor, "passive documentation opening");
      if (losses[0] != 0) throw new AssertionError("passive popup transiently stole editor focus");
      content.requestFocusInWindow(); requireFocus(content, "passive popup remains explicitly focusable");
      passive.cancel(); requireFocus(editor, "focused documentation closing");

      passive = new com.intellij.openapi.ui.popup.ComponentPopupBuilder(new javax.swing.JLabel("passive"), null)
          .setRequestFocus(false).createPopup();
      passive.showUnderneathOf(editor); other.requestFocusInWindow();
      passive.cancel(); requireFocus(other, "passive close must preserve unrelated focus");

      editor.requestFocusInWindow();
      content = new javax.swing.JTextField("active");
      com.intellij.openapi.ui.popup.JBPopup active = new com.intellij.openapi.ui.popup.ComponentPopupBuilder(content, content)
          .setRequestFocus(true).createPopup();
      active.showUnderneathOf(editor); requireFocus(content, "active popup preferred focus");
      active.cancel(); requireFocus(editor, "active popup closing");

      active = new com.intellij.openapi.ui.popup.ComponentPopupBuilder(new javax.swing.JTextField("nonfocusable"), null)
          .setFocusable(false).setRequestFocus(true).createPopup();
      active.showUnderneathOf(editor); requireFocus(editor, "nonfocusable popup opening");
      active.cancel(); requireFocus(editor, "nonfocusable popup closing");

      int before = losses[0];
      content = new javax.swing.JTextField("hint");
      com.intellij.ui.LightweightHint hint = new com.intellij.ui.LightweightHint(content);
      hint.show(editor, 0, editor.getHeight(), content, new com.intellij.ui.HintHint().setRequestFocus(false));
      requireFocus(editor, "passive lightweight hint"); hint.hide(); requireFocus(editor, "passive lightweight hint close");
      com.intellij.ide.IdeTooltipManager manager = com.intellij.ide.IdeTooltipManager.getInstance();
      manager.show(new com.intellij.ide.IdeTooltip(editor, new java.awt.Point(0, 24), new javax.swing.JLabel("tooltip")), true);
      requireFocus(editor, "passive IDE tooltip"); manager.hideCurrentNow(); requireFocus(editor, "passive IDE tooltip close");
      if (losses[0] != before) throw new AssertionError("passive hint transiently stole editor focus");
      hint.show(editor, 0, editor.getHeight(), content, new com.intellij.ui.HintHint().setRequestFocus(true));
      requireFocus(content, "active lightweight hint"); hint.hide(); requireFocus(editor, "active lightweight hint close");

      javax.swing.JPopupMenu menu = new javax.swing.JPopupMenu(); menu.add("menu item");
      menu.show(editor, 0, 24); requireFocus(menu, "ordinary menu opening");
      menu.setVisible(false); requireFocus(editor, "ordinary menu closing");
      com.intellij.openapi.ui.popup.ListPopup list = com.intellij.openapi.ui.popup.JBPopupFactory.getInstance().createListPopup(
          new com.intellij.openapi.ui.popup.util.BaseListPopupStep<String>("list", java.util.Arrays.asList("one", "two")));
      list.showUnderneathOf(editor);
      if (!(java.awt.KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner() instanceof javax.swing.JPopupMenu)) {
        throw new AssertionError("ordinary IntelliJ list popup did not request focus");
      }
      list.cancel(); requireFocus(editor, "ordinary IntelliJ list popup closing");
    } finally { window.dispose(); }
  }
  private static void requireFocus(java.awt.Component expected, String operation) {
    if (java.awt.KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner() != expected) {
      throw new AssertionError(operation + " changed the native-backed focus owner");
    }
  }
  private static void fail(Throwable error) { error.printStackTrace(); result = "FAIL: " + error; WebAwt.uninstall(); }
}
