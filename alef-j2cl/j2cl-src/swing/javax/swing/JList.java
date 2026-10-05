package javax.swing;

import java.util.ArrayList;
import java.util.List;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Color;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import elemental2.dom.KeyboardEvent;
import elemental2.dom.MouseEvent;
import javax.swing.plaf.basic.BasicDomStyles;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionListener;

public class JList<E> extends JComponent implements SwingConstants {
  private int fixedCellWidth = -1, fixedCellHeight = -1;
  private Color selectionBackground = new Color(184, 207, 229), selectionForeground = new Color(51, 51, 51);
  private ListModel<E> model;
  private ListSelectionModel selectionModel = new DefaultListSelectionModel();
  private ListCellRenderer<? super E> renderer = new DefaultListCellRenderer();
  private int visibleRowCount = 8;
  private int rendererWidth;
  private final javax.swing.event.EventListenerList selectionListeners = new javax.swing.event.EventListenerList();
  private final ListSelectionListener selectionListener = event -> {
    syncSelection(event.getFirstIndex(), event.getLastIndex());
    javax.swing.event.ListSelectionEvent forwarded = new javax.swing.event.ListSelectionEvent(this, event.getFirstIndex(), event.getLastIndex(), event.getValueIsAdjusting());
    for (ListSelectionListener listener : selectionListeners.getListeners(ListSelectionListener.class)) listener.valueChanged(forwarded);
  };
  private final ListDataListener modelListener = new ListDataListener() {
    @Override public void intervalAdded(ListDataEvent e) { int first = Math.min(e.getIndex0(), e.getIndex1()); selectionModel.insertIndexInterval(first, Math.abs(e.getIndex1() - e.getIndex0()) + 1, true); syncPeer(); revalidate(); }
    @Override public void intervalRemoved(ListDataEvent e) { selectionModel.removeIndexInterval(e.getIndex0(), e.getIndex1()); syncPeer(); revalidate(); }
    @Override public void contentsChanged(ListDataEvent e) { syncPeer(); revalidate(); }
  };
  public JList() { this(new DefaultListModel<E>()); }
  public JList(E[] values) { this(arrayModel(values)); }
  public JList(ListModel<E> model) { super("div"); setBackground(Color.WHITE); setForeground(new Color(51, 51, 51)); setOpaque(true); setModel(model); selectionModel.addListSelectionListener(selectionListener); }
  private static <T> ListModel<T> arrayModel(T[] values) { DefaultListModel<T> model = new DefaultListModel<>(); for (T value : values) model.addElement(value); return model; }
  public ListModel<E> getModel() { return model; }
  public void setModel(ListModel<E> value) { if (value == null) throw new NullPointerException("model"); if (model == value) return; if (model != null) model.removeListDataListener(modelListener); ListModel<E> old = model; model = value; model.addListDataListener(modelListener); clearSelection(); firePropertyChange("model", old, model); syncPeer(); revalidate(); }
  public ListSelectionModel getSelectionModel() { return selectionModel; }
  public void setSelectionModel(ListSelectionModel value) { if (value == null) throw new NullPointerException("selectionModel"); selectionModel.removeListSelectionListener(selectionListener); selectionModel = value; value.addListSelectionListener(selectionListener); syncPeer(); }
  public int getSelectedIndex() { return selectionModel.getMinSelectionIndex(); }
  public void setSelectedIndex(int index) { if (index < 0) clearSelection(); else selectionModel.setSelectionInterval(index, index); }
  public int[] getSelectedIndices() { List<Integer> indexes = new ArrayList<>(); for (int i = 0; i < model.getSize(); i++) if (selectionModel.isSelectedIndex(i)) indexes.add(i); int[] result = new int[indexes.size()]; for (int i = 0; i < result.length; i++) result[i] = indexes.get(i); return result; }
  public E getSelectedValue() { int index = getSelectedIndex(); return index < 0 ? null : model.getElementAt(index); }
  public List<E> getSelectedValuesList() { List<E> values = new ArrayList<>(); for (int i : getSelectedIndices()) values.add(model.getElementAt(i)); return values; }
  public void clearSelection() { selectionModel.clearSelection(); }
  public void setSelectionMode(int mode) { selectionModel.setSelectionMode(mode); syncPeer(); }
  public void addListSelectionListener(ListSelectionListener listener) { selectionListeners.add(ListSelectionListener.class, listener); }
  public void removeListSelectionListener(ListSelectionListener listener) { selectionListeners.remove(ListSelectionListener.class, listener); }
  public void setCellRenderer(ListCellRenderer<? super E> renderer) { this.renderer = renderer; syncPeer(); }
  public ListCellRenderer<? super E> getCellRenderer() { return renderer; }
  public void setVisibleRowCount(int count) { visibleRowCount = Math.max(0, count); syncPeer(); revalidate(); }
  public int getVisibleRowCount() { return visibleRowCount; }
  public int getFixedCellWidth() { return fixedCellWidth; }
  public void setFixedCellWidth(int value) { int previous = fixedCellWidth; fixedCellWidth = value; firePropertyChange("fixedCellWidth", previous, value); syncPeer(); revalidate(); }
  public int getFixedCellHeight() { return fixedCellHeight; }
  public void setFixedCellHeight(int value) { int previous = fixedCellHeight; fixedCellHeight = value; firePropertyChange("fixedCellHeight", previous, value); syncPeer(); revalidate(); }
  private int rowHeight() { return fixedCellHeight > 0 ? fixedCellHeight : getFont().getSize() + 6; }
  public Color getSelectionBackground() { return selectionBackground; }
  public void setSelectionBackground(Color value) { Color previous = selectionBackground; selectionBackground = value; firePropertyChange("selectionBackground", previous, value); syncPeer(); }
  public Color getSelectionForeground() { return selectionForeground; }
  public void setSelectionForeground(Color value) { Color previous = selectionForeground; selectionForeground = value; firePropertyChange("selectionForeground", previous, value); syncPeer(); }
  @Override public void setBounds(int x, int y, int width, int height) {
    boolean widthChanged = width != getWidth();
    super.setBounds(x, y, width, height);
    if (widthChanged && isDisplayable()) syncPeer();
  }
  public int getFirstVisibleIndex() {
    java.awt.Rectangle visible = getVisibleRect();
    if (model.getSize() == 0 || visible.width <= 0 || visible.height <= 0) return -1;
    int index = Math.max(0, visible.y / rowHeight());
    return index < model.getSize() ? index : -1;
  }
  public int getLastVisibleIndex() {
    java.awt.Rectangle visible = getVisibleRect();
    if (getFirstVisibleIndex() < 0) return -1;
    return Math.min(model.getSize() - 1, (visible.y + visible.height - 1) / rowHeight());
  }
  public void ensureIndexIsVisible(int index) {
    if (index < 0 || index >= model.getSize()) return;
    scrollRectToVisible(new java.awt.Rectangle(0, index * rowHeight(), getWidth(), rowHeight()));
  }
  @Override public Dimension getPreferredSize() {
    if (isPreferredSizeSet()) return super.getPreferredSize();
    int width = 0;
    for (int index = 0; index < model.getSize(); index++) {
      E value = model.getElementAt(index);
      Component cell = renderer == null ? null : renderer.getListCellRendererComponent(this, value, index, false, false);
      width = Math.max(width, cell == null ? peerTextWidth(value == null ? "" : value.toString()) + 4 : cell.getPreferredSize().width);
    }
    return new Dimension(fixedCellWidth > 0 ? fixedCellWidth : width, model.getSize() * rowHeight());
  }
  public Dimension getPreferredScrollableViewportSize() {
    Dimension size = getPreferredSize();
    return new Dimension(size.width, visibleRowCount * rowHeight());
  }
  @Override public void addNotify() {
    super.addNotify(); syncPeer();
    if (peerElement() != null) {
      peerElement().setAttribute("tabindex", "0");
      peerElement().addEventListener("keydown", event -> {
        KeyboardEvent key = (KeyboardEvent) event;
        int index = selectionModel.getLeadSelectionIndex();
        if ("ArrowDown".equals(key.key)) index = Math.min(model.getSize() - 1, index + 1);
        else if ("ArrowUp".equals(key.key)) index = Math.max(0, index - 1);
        else if ("Home".equals(key.key)) index = 0;
        else if ("End".equals(key.key)) index = model.getSize() - 1;
        else return;
        event.preventDefault(); selectFromPeer(index, key.shiftKey, false); revealSelection(index);
      });
    }
  }
  private void selectFromPeer(int index, boolean extend, boolean toggle) {
    if (!isEnabled() || index < 0 || index >= model.getSize()) return;
    if (extend && selectionModel.getSelectionMode() != ListSelectionModel.SINGLE_SELECTION && selectionModel.getAnchorSelectionIndex() >= 0)
      selectionModel.setSelectionInterval(selectionModel.getAnchorSelectionIndex(), index);
    else if (toggle && selectionModel.getSelectionMode() == ListSelectionModel.MULTIPLE_INTERVAL_SELECTION) {
      if (selectionModel.isSelectedIndex(index)) selectionModel.removeSelectionInterval(index, index);
      else selectionModel.addSelectionInterval(index, index);
    } else setSelectedIndex(index);
  }
  private void revealSelection(int index) {
    if (getParent() instanceof JViewport && index >= 0) {
      JViewport viewport = (JViewport) getParent(); int y = index * rowHeight();
      java.awt.Point position = viewport.getViewPosition();
      if (y < position.y) viewport.setViewPosition(new java.awt.Point(position.x, y));
      else if (y + rowHeight() > position.y + viewport.getHeight()) viewport.setViewPosition(new java.awt.Point(position.x, y + rowHeight() - viewport.getHeight()));
    }
  }
  private void syncPeer() {
    if (model == null) return;
    HTMLElement root = (HTMLElement) peerElement();
    if (root != null) {
      root.textContent = ""; root.setAttribute("role", "listbox");
      root.setAttribute("aria-multiselectable", Boolean.toString(selectionModel.getSelectionMode() != ListSelectionModel.SINGLE_SELECTION));
    }
    rendererWidth = root == null ? 0 : Math.max(getWidth(), getPreferredSize().width);
    for (int index = 0; index < model.getSize(); index++) {
      if (root == null) continue;
      HTMLElement item = (HTMLElement) DomGlobal.document.createElement("div");
      item.id = root.id + "-option-" + index; item.className = "ws-list-cell";
      item.setAttribute("role", "option");
      item.style.setProperty("height", rowHeight() + "px");
      root.appendChild(item);
      paintRow(item, index, rendererWidth);
      final int selectedIndex = index;
      item.addEventListener("click", event -> { MouseEvent mouse = (MouseEvent) event; selectFromPeer(selectedIndex, mouse.shiftKey, mouse.ctrlKey || mouse.metaKey); requestFocusInWindow(); });
    }
    if (root != null) syncActiveDescendant(root);
    revalidate();
  }

  // Selection does not change the model or row geometry. Retain the DOM and
  // snapshots outside the changed interval, and avoid measuring every renderer.
  private void syncSelection(int first, int last) {
    HTMLElement root = (HTMLElement) peerElement();
    if (root == null || model == null) return;
    if (root.childNodes.length != model.getSize()) { syncPeer(); return; }
    for (int index = Math.max(0, first); index <= Math.min(last, model.getSize() - 1); index++) {
      HTMLElement item = (HTMLElement) root.childNodes.item(index);
      boolean selected = selectionModel.isSelectedIndex(index);
      if (!Boolean.toString(selected).equals(item.getAttribute("aria-selected"))) {
        paintRow(item, index, rendererWidth);
      }
    }
    syncActiveDescendant(root);
  }

  private void paintRow(HTMLElement item, int index, int width) {
    E value = model.getElementAt(index);
    boolean selected = selectionModel.isSelectedIndex(index);
    Component rendered = renderer == null ? null : renderer.getListCellRendererComponent(this, value, index, selected, false);
    item.setAttribute("aria-selected", Boolean.toString(selected));
    BasicDomStyles.paintRenderer(item, rendered, value == null ? "" : value.toString());
    if (rendered != null && !(rendered instanceof JLabel)) {
      BrowserRendererSnapshot.paint(item, rendered, value == null ? "" : value.toString(), width, rowHeight());
    }
  }

  private void syncActiveDescendant(HTMLElement root) {
    int lead = selectionModel.getLeadSelectionIndex();
    if (lead >= 0 && lead < model.getSize()) root.setAttribute("aria-activedescendant", root.id + "-option-" + lead); else root.removeAttribute("aria-activedescendant");
  }
  @Override public AccessibleContext getAccessibleContext() { return new AccessibleJList(); }
  protected class AccessibleJList extends AccessibleJComponent { @Override public AccessibleRole getAccessibleRole() { return AccessibleRole.LIST; } }
}
