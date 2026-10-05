package com.intellij.ui.components;
import javax.swing.JList;
import javax.swing.ListModel;
public class JBList<T> extends JList<T> {
  public JBList() {}
  public JBList(ListModel<T> model) { super(model); }
  public JBList(T[] values) { super(values); }
  public JBList(java.util.List<T> values) { super(new com.intellij.ui.CollectionListModel<>(values)); }
}
