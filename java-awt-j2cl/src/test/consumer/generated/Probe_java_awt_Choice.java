package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_Choice {
    static void call0() throws java.awt.HeadlessException { try { java.awt.Choice value = new java.awt.Choice(); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call1(java.awt.Choice receiver)  { receiver.addNotify(); }
    static void call2(java.awt.Choice receiver)  { int value = receiver.getItemCount(); }
    static void call3(java.awt.Choice receiver)  { int value = receiver.countItems(); }
    static void call4(java.awt.Choice receiver)  { java.lang.String value = receiver.getItem((int) 0); }
    static void call5(java.awt.Choice receiver)  { receiver.add((java.lang.String) null); }
    static void call6(java.awt.Choice receiver)  { receiver.addItem((java.lang.String) null); }
    static void call7(java.awt.Choice receiver)  { receiver.insert((java.lang.String) null, (int) 0); }
    static void call8(java.awt.Choice receiver)  { receiver.remove((java.lang.String) null); }
    static void call9(java.awt.Choice receiver)  { receiver.remove((int) 0); }
    static void call10(java.awt.Choice receiver)  { receiver.removeAll(); }
    static void call11(java.awt.Choice receiver)  { java.lang.String value = receiver.getSelectedItem(); }
    static void call12(java.awt.Choice receiver)  { java.lang.Object[] value = receiver.getSelectedObjects(); }
    static void call13(java.awt.Choice receiver)  { int value = receiver.getSelectedIndex(); }
    static void call14(java.awt.Choice receiver)  { receiver.select((int) 0); }
    static void call15(java.awt.Choice receiver)  { receiver.select((java.lang.String) null); }
    static void call16(java.awt.Choice receiver)  { receiver.addItemListener((java.awt.event.ItemListener) null); }
    static void call17(java.awt.Choice receiver)  { receiver.removeItemListener((java.awt.event.ItemListener) null); }
    static void call18(java.awt.Choice receiver)  { java.awt.event.ItemListener[] value = receiver.getItemListeners(); }
    static void call19(java.awt.Choice receiver)  { java.util.EventListener[] value = receiver.getListeners((java.lang.Class) null); }
    static void call20(java.awt.Choice receiver)  { receiver.processEvent((java.awt.AWTEvent) null); }
    static void call21(java.awt.Choice receiver)  { receiver.processItemEvent((java.awt.event.ItemEvent) null); }
    static void call22(java.awt.Choice receiver)  { java.lang.String value = receiver.paramString(); }
    static void call23(java.awt.Choice receiver)  { javax.accessibility.AccessibleContext value = receiver.getAccessibleContext(); }
}

abstract class Override_java_awt_Choice extends java.awt.Choice {
    Override_java_awt_Choice() throws java.awt.HeadlessException { super(); }
    @Override
    public void addNotify()  { throw new UnsupportedOperationException(); }
    @Override
    public int getItemCount()  { throw new UnsupportedOperationException(); }
    @Override
    public int countItems()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String getItem(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void add(java.lang.String arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void addItem(java.lang.String arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void insert(java.lang.String arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void remove(java.lang.String arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void remove(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void removeAll()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String getSelectedItem()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Object[] getSelectedObjects()  { throw new UnsupportedOperationException(); }
    @Override
    public int getSelectedIndex()  { throw new UnsupportedOperationException(); }
    @Override
    public void select(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void select(java.lang.String arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void addItemListener(java.awt.event.ItemListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void removeItemListener(java.awt.event.ItemListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.event.ItemListener[] getItemListeners()  { throw new UnsupportedOperationException(); }
    @Override
    public <T extends java.util.EventListener> T[] getListeners(java.lang.Class<T> arg0)  { throw new UnsupportedOperationException(); }
    @Override
    protected void processEvent(java.awt.AWTEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    protected void processItemEvent(java.awt.event.ItemEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    protected java.lang.String paramString()  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.AccessibleContext getAccessibleContext()  { throw new UnsupportedOperationException(); }
}
