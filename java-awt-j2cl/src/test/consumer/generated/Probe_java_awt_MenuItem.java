package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_MenuItem {
    static void call0() throws java.awt.HeadlessException { try { java.awt.MenuItem value = new java.awt.MenuItem(); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call1() throws java.awt.HeadlessException { try { java.awt.MenuItem value = new java.awt.MenuItem((java.lang.String) null); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call2() throws java.awt.HeadlessException { try { java.awt.MenuItem value = new java.awt.MenuItem((java.lang.String) null, (java.awt.MenuShortcut) null); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call3(java.awt.MenuItem receiver)  { receiver.addNotify(); }
    static void call4(java.awt.MenuItem receiver)  { java.lang.String value = receiver.getLabel(); }
    static void call5(java.awt.MenuItem receiver)  { receiver.setLabel((java.lang.String) null); }
    static void call6(java.awt.MenuItem receiver)  { boolean value = receiver.isEnabled(); }
    static void call7(java.awt.MenuItem receiver)  { receiver.setEnabled(false); }
    static void call8(java.awt.MenuItem receiver)  { receiver.enable(); }
    static void call9(java.awt.MenuItem receiver)  { receiver.enable(false); }
    static void call10(java.awt.MenuItem receiver)  { receiver.disable(); }
    static void call11(java.awt.MenuItem receiver)  { java.awt.MenuShortcut value = receiver.getShortcut(); }
    static void call12(java.awt.MenuItem receiver)  { receiver.setShortcut((java.awt.MenuShortcut) null); }
    static void call13(java.awt.MenuItem receiver)  { receiver.deleteShortcut(); }
    static void call14(java.awt.MenuItem receiver)  { receiver.enableEvents((long) 0); }
    static void call15(java.awt.MenuItem receiver)  { receiver.disableEvents((long) 0); }
    static void call16(java.awt.MenuItem receiver)  { receiver.setActionCommand((java.lang.String) null); }
    static void call17(java.awt.MenuItem receiver)  { java.lang.String value = receiver.getActionCommand(); }
    static void call18(java.awt.MenuItem receiver)  { receiver.addActionListener((java.awt.event.ActionListener) null); }
    static void call19(java.awt.MenuItem receiver)  { receiver.removeActionListener((java.awt.event.ActionListener) null); }
    static void call20(java.awt.MenuItem receiver)  { java.awt.event.ActionListener[] value = receiver.getActionListeners(); }
    static void call21(java.awt.MenuItem receiver)  { java.util.EventListener[] value = receiver.getListeners((java.lang.Class) null); }
    static void call22(java.awt.MenuItem receiver)  { receiver.processEvent((java.awt.AWTEvent) null); }
    static void call23(java.awt.MenuItem receiver)  { receiver.processActionEvent((java.awt.event.ActionEvent) null); }
    static void call24(java.awt.MenuItem receiver)  { java.lang.String value = receiver.paramString(); }
    static void call25(java.awt.MenuItem receiver)  { javax.accessibility.AccessibleContext value = receiver.getAccessibleContext(); }
}

abstract class Override_java_awt_MenuItem extends java.awt.MenuItem {
    Override_java_awt_MenuItem() throws java.awt.HeadlessException { super(); }
    @Override
    public void addNotify()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String getLabel()  { throw new UnsupportedOperationException(); }
    @Override
    public void setLabel(java.lang.String arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isEnabled()  { throw new UnsupportedOperationException(); }
    @Override
    public void setEnabled(boolean arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void enable()  { throw new UnsupportedOperationException(); }
    @Override
    public void enable(boolean arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void disable()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.MenuShortcut getShortcut()  { throw new UnsupportedOperationException(); }
    @Override
    public void setShortcut(java.awt.MenuShortcut arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void deleteShortcut()  { throw new UnsupportedOperationException(); }
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
    public java.lang.String paramString()  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.AccessibleContext getAccessibleContext()  { throw new UnsupportedOperationException(); }
}
