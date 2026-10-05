package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_DefaultKeyboardFocusManager {
    static void call0()  { java.awt.DefaultKeyboardFocusManager value = new java.awt.DefaultKeyboardFocusManager(); }
    static void call1(java.awt.DefaultKeyboardFocusManager receiver)  { boolean value = receiver.dispatchEvent((java.awt.AWTEvent) null); }
    static void call2(java.awt.DefaultKeyboardFocusManager receiver)  { boolean value = receiver.dispatchKeyEvent((java.awt.event.KeyEvent) null); }
    static void call3(java.awt.DefaultKeyboardFocusManager receiver)  { boolean value = receiver.postProcessKeyEvent((java.awt.event.KeyEvent) null); }
    static void call4(java.awt.DefaultKeyboardFocusManager receiver)  { receiver.processKeyEvent((java.awt.Component) null, (java.awt.event.KeyEvent) null); }
    static void call5(java.awt.DefaultKeyboardFocusManager receiver)  { receiver.enqueueKeyEvents((long) 0, (java.awt.Component) null); }
    static void call6(java.awt.DefaultKeyboardFocusManager receiver)  { receiver.dequeueKeyEvents((long) 0, (java.awt.Component) null); }
    static void call7(java.awt.DefaultKeyboardFocusManager receiver)  { receiver.discardKeyEvents((java.awt.Component) null); }
    static void call8(java.awt.DefaultKeyboardFocusManager receiver)  { receiver.focusPreviousComponent((java.awt.Component) null); }
    static void call9(java.awt.DefaultKeyboardFocusManager receiver)  { receiver.focusNextComponent((java.awt.Component) null); }
    static void call10(java.awt.DefaultKeyboardFocusManager receiver)  { receiver.upFocusCycle((java.awt.Component) null); }
    static void call11(java.awt.DefaultKeyboardFocusManager receiver)  { receiver.downFocusCycle((java.awt.Container) null); }
}

abstract class Override_java_awt_DefaultKeyboardFocusManager extends java.awt.DefaultKeyboardFocusManager {
    Override_java_awt_DefaultKeyboardFocusManager()  { super(); }
    @Override
    public boolean dispatchEvent(java.awt.AWTEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean dispatchKeyEvent(java.awt.event.KeyEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean postProcessKeyEvent(java.awt.event.KeyEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void processKeyEvent(java.awt.Component arg0, java.awt.event.KeyEvent arg1)  { throw new UnsupportedOperationException(); }
    @Override
    protected void enqueueKeyEvents(long arg0, java.awt.Component arg1)  { throw new UnsupportedOperationException(); }
    @Override
    protected void dequeueKeyEvents(long arg0, java.awt.Component arg1)  { throw new UnsupportedOperationException(); }
    @Override
    protected void discardKeyEvents(java.awt.Component arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void focusPreviousComponent(java.awt.Component arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void focusNextComponent(java.awt.Component arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void upFocusCycle(java.awt.Component arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void downFocusCycle(java.awt.Container arg0)  { throw new UnsupportedOperationException(); }
}
