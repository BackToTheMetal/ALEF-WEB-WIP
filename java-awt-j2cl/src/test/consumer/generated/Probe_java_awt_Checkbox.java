package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_Checkbox {
    static void call0() throws java.awt.HeadlessException { try { java.awt.Checkbox value = new java.awt.Checkbox(); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call1() throws java.awt.HeadlessException { try { java.awt.Checkbox value = new java.awt.Checkbox((java.lang.String) null); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call2() throws java.awt.HeadlessException { try { java.awt.Checkbox value = new java.awt.Checkbox((java.lang.String) null, false); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call3() throws java.awt.HeadlessException { try { java.awt.Checkbox value = new java.awt.Checkbox((java.lang.String) null, false, (java.awt.CheckboxGroup) null); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call4() throws java.awt.HeadlessException { try { java.awt.Checkbox value = new java.awt.Checkbox((java.lang.String) null, (java.awt.CheckboxGroup) null, false); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call5(java.awt.Checkbox receiver)  { receiver.addNotify(); }
    static void call6(java.awt.Checkbox receiver)  { java.lang.String value = receiver.getLabel(); }
    static void call7(java.awt.Checkbox receiver)  { receiver.setLabel((java.lang.String) null); }
    static void call8(java.awt.Checkbox receiver)  { boolean value = receiver.getState(); }
    static void call9(java.awt.Checkbox receiver)  { receiver.setState(false); }
    static void call10(java.awt.Checkbox receiver)  { java.lang.Object[] value = receiver.getSelectedObjects(); }
    static void call11(java.awt.Checkbox receiver)  { java.awt.CheckboxGroup value = receiver.getCheckboxGroup(); }
    static void call12(java.awt.Checkbox receiver)  { receiver.setCheckboxGroup((java.awt.CheckboxGroup) null); }
    static void call13(java.awt.Checkbox receiver)  { receiver.addItemListener((java.awt.event.ItemListener) null); }
    static void call14(java.awt.Checkbox receiver)  { receiver.removeItemListener((java.awt.event.ItemListener) null); }
    static void call15(java.awt.Checkbox receiver)  { java.awt.event.ItemListener[] value = receiver.getItemListeners(); }
    static void call16(java.awt.Checkbox receiver)  { java.util.EventListener[] value = receiver.getListeners((java.lang.Class) null); }
    static void call17(java.awt.Checkbox receiver)  { receiver.processEvent((java.awt.AWTEvent) null); }
    static void call18(java.awt.Checkbox receiver)  { receiver.processItemEvent((java.awt.event.ItemEvent) null); }
    static void call19(java.awt.Checkbox receiver)  { java.lang.String value = receiver.paramString(); }
    static void call20(java.awt.Checkbox receiver)  { javax.accessibility.AccessibleContext value = receiver.getAccessibleContext(); }
}

abstract class Override_java_awt_Checkbox extends java.awt.Checkbox {
    Override_java_awt_Checkbox() throws java.awt.HeadlessException { super(); }
    @Override
    public void addNotify()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String getLabel()  { throw new UnsupportedOperationException(); }
    @Override
    public void setLabel(java.lang.String arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean getState()  { throw new UnsupportedOperationException(); }
    @Override
    public void setState(boolean arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Object[] getSelectedObjects()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.CheckboxGroup getCheckboxGroup()  { throw new UnsupportedOperationException(); }
    @Override
    public void setCheckboxGroup(java.awt.CheckboxGroup arg0)  { throw new UnsupportedOperationException(); }
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
