package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_List {
    static void call0() throws java.awt.HeadlessException { try { java.awt.List value = new java.awt.List(); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call1() throws java.awt.HeadlessException { try { java.awt.List value = new java.awt.List((int) 0); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call2() throws java.awt.HeadlessException { try { java.awt.List value = new java.awt.List((int) 0, false); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call3(java.awt.List receiver)  { receiver.addNotify(); }
    static void call4(java.awt.List receiver)  { receiver.removeNotify(); }
    static void call5(java.awt.List receiver)  { int value = receiver.getItemCount(); }
    static void call6(java.awt.List receiver)  { int value = receiver.countItems(); }
    static void call7(java.awt.List receiver)  { java.lang.String value = receiver.getItem((int) 0); }
    static void call8(java.awt.List receiver)  { java.lang.String[] value = receiver.getItems(); }
    static void call9(java.awt.List receiver)  { receiver.add((java.lang.String) null); }
    static void call10(java.awt.List receiver)  { receiver.addItem((java.lang.String) null); }
    static void call11(java.awt.List receiver)  { receiver.add((java.lang.String) null, (int) 0); }
    static void call12(java.awt.List receiver)  { receiver.addItem((java.lang.String) null, (int) 0); }
    static void call13(java.awt.List receiver)  { receiver.replaceItem((java.lang.String) null, (int) 0); }
    static void call14(java.awt.List receiver)  { receiver.removeAll(); }
    static void call15(java.awt.List receiver)  { receiver.clear(); }
    static void call16(java.awt.List receiver)  { receiver.remove((java.lang.String) null); }
    static void call17(java.awt.List receiver)  { receiver.remove((int) 0); }
    static void call18(java.awt.List receiver)  { receiver.delItem((int) 0); }
    static void call19(java.awt.List receiver)  { int value = receiver.getSelectedIndex(); }
    static void call20(java.awt.List receiver)  { int[] value = receiver.getSelectedIndexes(); }
    static void call21(java.awt.List receiver)  { java.lang.String value = receiver.getSelectedItem(); }
    static void call22(java.awt.List receiver)  { java.lang.String[] value = receiver.getSelectedItems(); }
    static void call23(java.awt.List receiver)  { java.lang.Object[] value = receiver.getSelectedObjects(); }
    static void call24(java.awt.List receiver)  { receiver.select((int) 0); }
    static void call25(java.awt.List receiver)  { receiver.deselect((int) 0); }
    static void call26(java.awt.List receiver)  { boolean value = receiver.isIndexSelected((int) 0); }
    static void call27(java.awt.List receiver)  { boolean value = receiver.isSelected((int) 0); }
    static void call28(java.awt.List receiver)  { int value = receiver.getRows(); }
    static void call29(java.awt.List receiver)  { boolean value = receiver.isMultipleMode(); }
    static void call30(java.awt.List receiver)  { boolean value = receiver.allowsMultipleSelections(); }
    static void call31(java.awt.List receiver)  { receiver.setMultipleMode(false); }
    static void call32(java.awt.List receiver)  { receiver.setMultipleSelections(false); }
    static void call33(java.awt.List receiver)  { int value = receiver.getVisibleIndex(); }
    static void call34(java.awt.List receiver)  { receiver.makeVisible((int) 0); }
    static void call35(java.awt.List receiver)  { java.awt.Dimension value = receiver.getPreferredSize((int) 0); }
    static void call36(java.awt.List receiver)  { java.awt.Dimension value = receiver.preferredSize((int) 0); }
    static void call37(java.awt.List receiver)  { java.awt.Dimension value = receiver.getPreferredSize(); }
    static void call38(java.awt.List receiver)  { java.awt.Dimension value = receiver.preferredSize(); }
    static void call39(java.awt.List receiver)  { java.awt.Dimension value = receiver.getMinimumSize((int) 0); }
    static void call40(java.awt.List receiver)  { java.awt.Dimension value = receiver.minimumSize((int) 0); }
    static void call41(java.awt.List receiver)  { java.awt.Dimension value = receiver.getMinimumSize(); }
    static void call42(java.awt.List receiver)  { java.awt.Dimension value = receiver.minimumSize(); }
    static void call43(java.awt.List receiver)  { receiver.addItemListener((java.awt.event.ItemListener) null); }
    static void call44(java.awt.List receiver)  { receiver.removeItemListener((java.awt.event.ItemListener) null); }
    static void call45(java.awt.List receiver)  { java.awt.event.ItemListener[] value = receiver.getItemListeners(); }
    static void call46(java.awt.List receiver)  { receiver.addActionListener((java.awt.event.ActionListener) null); }
    static void call47(java.awt.List receiver)  { receiver.removeActionListener((java.awt.event.ActionListener) null); }
    static void call48(java.awt.List receiver)  { java.awt.event.ActionListener[] value = receiver.getActionListeners(); }
    static void call49(java.awt.List receiver)  { java.util.EventListener[] value = receiver.getListeners((java.lang.Class) null); }
    static void call50(java.awt.List receiver)  { receiver.processEvent((java.awt.AWTEvent) null); }
    static void call51(java.awt.List receiver)  { receiver.processItemEvent((java.awt.event.ItemEvent) null); }
    static void call52(java.awt.List receiver)  { receiver.processActionEvent((java.awt.event.ActionEvent) null); }
    static void call53(java.awt.List receiver)  { java.lang.String value = receiver.paramString(); }
    static void call54(java.awt.List receiver)  { receiver.delItems((int) 0, (int) 0); }
    static void call55(java.awt.List receiver)  { javax.accessibility.AccessibleContext value = receiver.getAccessibleContext(); }
}

abstract class Override_java_awt_List extends java.awt.List {
    Override_java_awt_List() throws java.awt.HeadlessException { super(); }
    @Override
    public void addNotify()  { throw new UnsupportedOperationException(); }
    @Override
    public void removeNotify()  { throw new UnsupportedOperationException(); }
    @Override
    public int getItemCount()  { throw new UnsupportedOperationException(); }
    @Override
    public int countItems()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String getItem(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String[] getItems()  { throw new UnsupportedOperationException(); }
    @Override
    public void add(java.lang.String arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void addItem(java.lang.String arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void add(java.lang.String arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void addItem(java.lang.String arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void replaceItem(java.lang.String arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void removeAll()  { throw new UnsupportedOperationException(); }
    @Override
    public void clear()  { throw new UnsupportedOperationException(); }
    @Override
    public void remove(java.lang.String arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void remove(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void delItem(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int getSelectedIndex()  { throw new UnsupportedOperationException(); }
    @Override
    public int[] getSelectedIndexes()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String getSelectedItem()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String[] getSelectedItems()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Object[] getSelectedObjects()  { throw new UnsupportedOperationException(); }
    @Override
    public void select(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void deselect(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isIndexSelected(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isSelected(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int getRows()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isMultipleMode()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean allowsMultipleSelections()  { throw new UnsupportedOperationException(); }
    @Override
    public void setMultipleMode(boolean arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setMultipleSelections(boolean arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int getVisibleIndex()  { throw new UnsupportedOperationException(); }
    @Override
    public void makeVisible(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Dimension getPreferredSize(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Dimension preferredSize(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Dimension getPreferredSize()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Dimension preferredSize()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Dimension getMinimumSize(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Dimension minimumSize(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Dimension getMinimumSize()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Dimension minimumSize()  { throw new UnsupportedOperationException(); }
    @Override
    public void addItemListener(java.awt.event.ItemListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void removeItemListener(java.awt.event.ItemListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.event.ItemListener[] getItemListeners()  { throw new UnsupportedOperationException(); }
    @Override
    public void addActionListener(java.awt.event.ActionListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void removeActionListener(java.awt.event.ActionListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.event.ActionListener[] getActionListeners()  { throw new UnsupportedOperationException(); }
    @Override
    public <T extends java.util.EventListener> T[] getListeners(java.lang.Class<T> arg0)  { throw new UnsupportedOperationException(); }
    @Override
    protected void processEvent(java.awt.AWTEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    protected void processItemEvent(java.awt.event.ItemEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    protected void processActionEvent(java.awt.event.ActionEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    protected java.lang.String paramString()  { throw new UnsupportedOperationException(); }
    @Override
    public void delItems(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.AccessibleContext getAccessibleContext()  { throw new UnsupportedOperationException(); }
}
