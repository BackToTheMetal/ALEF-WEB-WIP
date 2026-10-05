package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_Button {
    static void call0() throws java.awt.HeadlessException { try { java.awt.Button value = new java.awt.Button(); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call1() throws java.awt.HeadlessException { try { java.awt.Button value = new java.awt.Button((java.lang.String) null); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call2(java.awt.Button receiver)  { receiver.addNotify(); }
    static void call3(java.awt.Button receiver)  { java.lang.String value = receiver.getLabel(); }
    static void call4(java.awt.Button receiver)  { receiver.setLabel((java.lang.String) null); }
    static void call5(java.awt.Button receiver)  { receiver.setActionCommand((java.lang.String) null); }
    static void call6(java.awt.Button receiver)  { java.lang.String value = receiver.getActionCommand(); }
    static void call7(java.awt.Button receiver)  { receiver.addActionListener((java.awt.event.ActionListener) null); }
    static void call8(java.awt.Button receiver)  { receiver.removeActionListener((java.awt.event.ActionListener) null); }
    static void call9(java.awt.Button receiver)  { java.awt.event.ActionListener[] value = receiver.getActionListeners(); }
    static void call10(java.awt.Button receiver)  { java.util.EventListener[] value = receiver.getListeners((java.lang.Class) null); }
    static void call11(java.awt.Button receiver)  { receiver.processEvent((java.awt.AWTEvent) null); }
    static void call12(java.awt.Button receiver)  { receiver.processActionEvent((java.awt.event.ActionEvent) null); }
    static void call13(java.awt.Button receiver)  { java.lang.String value = receiver.paramString(); }
    static void call14(java.awt.Button receiver)  { javax.accessibility.AccessibleContext value = receiver.getAccessibleContext(); }
}

abstract class Override_java_awt_Button extends java.awt.Button {
    Override_java_awt_Button() throws java.awt.HeadlessException { super(); }
    @Override
    public void addNotify()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String getLabel()  { throw new UnsupportedOperationException(); }
    @Override
    public void setLabel(java.lang.String arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setActionCommand(java.lang.String arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String getActionCommand()  { throw new UnsupportedOperationException(); }
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
    protected void processActionEvent(java.awt.event.ActionEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    protected java.lang.String paramString()  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.AccessibleContext getAccessibleContext()  { throw new UnsupportedOperationException(); }
}
