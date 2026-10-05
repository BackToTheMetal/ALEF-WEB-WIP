package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_MenuComponent {
    static void call0(java.awt.MenuComponent receiver)  { java.lang.String value = receiver.getName(); }
    static void call1(java.awt.MenuComponent receiver)  { receiver.setName((java.lang.String) null); }
    static void call2(java.awt.MenuComponent receiver)  { java.awt.MenuContainer value = receiver.getParent(); }
    static void call3(java.awt.MenuComponent receiver)  { java.awt.Font value = receiver.getFont(); }
    static void call4(java.awt.MenuComponent receiver)  { receiver.setFont((java.awt.Font) null); }
    static void call5(java.awt.MenuComponent receiver)  { receiver.removeNotify(); }
    static void call6(java.awt.MenuComponent receiver)  { boolean value = receiver.postEvent((java.awt.Event) null); }
    static void call7(java.awt.MenuComponent receiver)  { receiver.dispatchEvent((java.awt.AWTEvent) null); }
    static void call8(java.awt.MenuComponent receiver)  { receiver.processEvent((java.awt.AWTEvent) null); }
    static void call9(java.awt.MenuComponent receiver)  { java.lang.String value = receiver.paramString(); }
    static void call10(java.awt.MenuComponent receiver)  { java.lang.String value = receiver.toString(); }
    static void call11(java.awt.MenuComponent receiver)  { java.lang.Object value = receiver.getTreeLock(); }
    static void call12(java.awt.MenuComponent receiver)  { javax.accessibility.AccessibleContext value = receiver.getAccessibleContext(); }
}

abstract class Override_java_awt_MenuComponent extends java.awt.MenuComponent {
    Override_java_awt_MenuComponent() throws java.awt.HeadlessException { super(); }
    @Override
    public java.lang.String getName()  { throw new UnsupportedOperationException(); }
    @Override
    public void setName(java.lang.String arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.MenuContainer getParent()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Font getFont()  { throw new UnsupportedOperationException(); }
    @Override
    public void setFont(java.awt.Font arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void removeNotify()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean postEvent(java.awt.Event arg0)  { throw new UnsupportedOperationException(); }
    @Override
    protected void processEvent(java.awt.AWTEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    protected java.lang.String paramString()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String toString()  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.AccessibleContext getAccessibleContext()  { throw new UnsupportedOperationException(); }
}
