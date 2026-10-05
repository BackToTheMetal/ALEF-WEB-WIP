package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_CheckboxMenuItem {
    static void call0() throws java.awt.HeadlessException { try { java.awt.CheckboxMenuItem value = new java.awt.CheckboxMenuItem(); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call1() throws java.awt.HeadlessException { try { java.awt.CheckboxMenuItem value = new java.awt.CheckboxMenuItem((java.lang.String) null); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call2() throws java.awt.HeadlessException { try { java.awt.CheckboxMenuItem value = new java.awt.CheckboxMenuItem((java.lang.String) null, false); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call3(java.awt.CheckboxMenuItem receiver)  { receiver.addNotify(); }
    static void call4(java.awt.CheckboxMenuItem receiver)  { boolean value = receiver.getState(); }
    static void call5(java.awt.CheckboxMenuItem receiver)  { receiver.setState(false); }
    static void call6(java.awt.CheckboxMenuItem receiver)  { java.lang.Object[] value = receiver.getSelectedObjects(); }
    static void call7(java.awt.CheckboxMenuItem receiver)  { receiver.addItemListener((java.awt.event.ItemListener) null); }
    static void call8(java.awt.CheckboxMenuItem receiver)  { receiver.removeItemListener((java.awt.event.ItemListener) null); }
    static void call9(java.awt.CheckboxMenuItem receiver)  { java.awt.event.ItemListener[] value = receiver.getItemListeners(); }
    static void call10(java.awt.CheckboxMenuItem receiver)  { java.util.EventListener[] value = receiver.getListeners((java.lang.Class) null); }
    static void call11(java.awt.CheckboxMenuItem receiver)  { receiver.processEvent((java.awt.AWTEvent) null); }
    static void call12(java.awt.CheckboxMenuItem receiver)  { receiver.processItemEvent((java.awt.event.ItemEvent) null); }
    static void call13(java.awt.CheckboxMenuItem receiver)  { java.lang.String value = receiver.paramString(); }
    static void call14(java.awt.CheckboxMenuItem receiver)  { javax.accessibility.AccessibleContext value = receiver.getAccessibleContext(); }
}

abstract class Override_java_awt_CheckboxMenuItem extends java.awt.CheckboxMenuItem {
    Override_java_awt_CheckboxMenuItem() throws java.awt.HeadlessException { super(); }
    @Override
    public void addNotify()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean getState()  { throw new UnsupportedOperationException(); }
    @Override
    public void setState(boolean arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Object[] getSelectedObjects()  { throw new UnsupportedOperationException(); }
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
    public java.lang.String paramString()  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.AccessibleContext getAccessibleContext()  { throw new UnsupportedOperationException(); }
}
