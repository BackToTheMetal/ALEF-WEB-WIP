package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_MenuBar {
    static void call0() throws java.awt.HeadlessException { try { java.awt.MenuBar value = new java.awt.MenuBar(); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call1(java.awt.MenuBar receiver)  { receiver.addNotify(); }
    static void call2(java.awt.MenuBar receiver)  { receiver.removeNotify(); }
    static void call3(java.awt.MenuBar receiver)  { java.awt.Menu value = receiver.getHelpMenu(); }
    static void call4(java.awt.MenuBar receiver)  { receiver.setHelpMenu((java.awt.Menu) null); }
    static void call5(java.awt.MenuBar receiver)  { java.awt.Menu value = receiver.add((java.awt.Menu) null); }
    static void call6(java.awt.MenuBar receiver)  { receiver.remove((int) 0); }
    static void call7(java.awt.MenuBar receiver)  { receiver.remove((java.awt.MenuComponent) null); }
    static void call8(java.awt.MenuBar receiver)  { int value = receiver.getMenuCount(); }
    static void call9(java.awt.MenuBar receiver)  { int value = receiver.countMenus(); }
    static void call10(java.awt.MenuBar receiver)  { java.awt.Menu value = receiver.getMenu((int) 0); }
    static void call11(java.awt.MenuBar receiver)  { java.util.Enumeration value = receiver.shortcuts(); }
    static void call12(java.awt.MenuBar receiver)  { java.awt.MenuItem value = receiver.getShortcutMenuItem((java.awt.MenuShortcut) null); }
    static void call13(java.awt.MenuBar receiver)  { receiver.deleteShortcut((java.awt.MenuShortcut) null); }
    static void call14(java.awt.MenuBar receiver)  { javax.accessibility.AccessibleContext value = receiver.getAccessibleContext(); }
}

abstract class Override_java_awt_MenuBar extends java.awt.MenuBar {
    Override_java_awt_MenuBar() throws java.awt.HeadlessException { super(); }
    @Override
    public void addNotify()  { throw new UnsupportedOperationException(); }
    @Override
    public void removeNotify()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Menu getHelpMenu()  { throw new UnsupportedOperationException(); }
    @Override
    public void setHelpMenu(java.awt.Menu arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Menu add(java.awt.Menu arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void remove(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void remove(java.awt.MenuComponent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int getMenuCount()  { throw new UnsupportedOperationException(); }
    @Override
    public int countMenus()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Menu getMenu(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.util.Enumeration<java.awt.MenuShortcut> shortcuts()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.MenuItem getShortcutMenuItem(java.awt.MenuShortcut arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void deleteShortcut(java.awt.MenuShortcut arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.AccessibleContext getAccessibleContext()  { throw new UnsupportedOperationException(); }
}
