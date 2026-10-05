package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_PopupMenu {
    static void call0() throws java.awt.HeadlessException { try { java.awt.PopupMenu value = new java.awt.PopupMenu(); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call1() throws java.awt.HeadlessException { try { java.awt.PopupMenu value = new java.awt.PopupMenu((java.lang.String) null); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call2(java.awt.PopupMenu receiver)  { java.awt.MenuContainer value = receiver.getParent(); }
    static void call3(java.awt.PopupMenu receiver)  { receiver.addNotify(); }
    static void call4(java.awt.PopupMenu receiver)  { receiver.show((java.awt.Component) null, (int) 0, (int) 0); }
    static void call5(java.awt.PopupMenu receiver)  { javax.accessibility.AccessibleContext value = receiver.getAccessibleContext(); }
}

abstract class Override_java_awt_PopupMenu extends java.awt.PopupMenu {
    Override_java_awt_PopupMenu() throws java.awt.HeadlessException { super(); }
    @Override
    public java.awt.MenuContainer getParent()  { throw new UnsupportedOperationException(); }
    @Override
    public void addNotify()  { throw new UnsupportedOperationException(); }
    @Override
    public void show(java.awt.Component arg0, int arg1, int arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.AccessibleContext getAccessibleContext()  { throw new UnsupportedOperationException(); }
}
