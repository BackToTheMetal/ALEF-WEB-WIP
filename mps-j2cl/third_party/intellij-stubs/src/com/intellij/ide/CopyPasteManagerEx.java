package com.intellij.ide;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.List;
/** WebAWT clipboard with bounded identity-preserving history for MPS node transfer flavors. */
public final class CopyPasteManagerEx {
  private static final CopyPasteManagerEx INSTANCE = new CopyPasteManagerEx();
  private final List<Transferable> history = new ArrayList<>();
  private Clipboard installedClipboard;
  private int limit = 50;
  public static CopyPasteManagerEx getInstanceEx() { return INSTANCE; }
  public static CopyPasteManagerEx getInstance() { return INSTANCE; }
  private Clipboard clipboard() { return installedClipboard == null ? Toolkit.getDefaultToolkit().getSystemClipboard() : installedClipboard; }
  public void setBrowserClipboard(Clipboard clipboard) { installedClipboard = clipboard; history.clear(); }
  public void setBrowserHistoryLimit(int maximum) { if (maximum < 1) throw new IllegalArgumentException("Clipboard history must retain at least one entry"); limit = maximum; trim(); }
  public Transferable getContents() { Transferable current = clipboard().getContents(null); if (current != null) remember(current); return current; }
  @SuppressWarnings("unchecked") public <T> T getContents(DataFlavor flavor) {
    Transferable current = getContents();
    if (current == null || !current.isDataFlavorSupported(flavor)) return null;
    try { return (T) current.getTransferData(flavor); }
    catch (java.awt.datatransfer.UnsupportedFlavorException | java.io.IOException error) { return null; }
  }
  public Transferable[] getAllContents() { getContents(); return history.toArray(new Transferable[0]); }
  public void setContents(Transferable content) { clipboard().setContents(java.util.Objects.requireNonNull(content), null); remember(content); }
  public void removeContent(Transferable content) {
    removeIdentity(content);
    if (clipboard().getContents(null) == content) clipboard().setContents(history.isEmpty() ? null : history.get(0), null);
  }
  public void clearBrowserHistory() { history.clear(); }
  private void remember(Transferable content) { removeIdentity(content); history.add(0, content); trim(); }
  private void removeIdentity(Transferable content) { for (int i = history.size() - 1; i >= 0; i--) if (history.get(i) == content) history.remove(i); }
  private void trim() { while (history.size() > limit) history.remove(history.size() - 1); }
}
