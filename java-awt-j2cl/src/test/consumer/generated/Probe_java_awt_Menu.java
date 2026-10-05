package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_Menu {
    static void call0() throws java.awt.HeadlessException { try { java.awt.Menu value = new java.awt.Menu(); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call1() throws java.awt.HeadlessException { try { java.awt.Menu value = new java.awt.Menu((java.lang.String) null); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call2() throws java.awt.HeadlessException { try { java.awt.Menu value = new java.awt.Menu((java.lang.String) null, false); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call3(java.awt.Menu receiver)  { receiver.addNotify(); }
    static void call4(java.awt.Menu receiver)  { receiver.removeNotify(); }
    static void call5(java.awt.Menu receiver)  { boolean value = receiver.isTearOff(); }
    static void call6(java.awt.Menu receiver)  { int value = receiver.getItemCount(); }
    static void call7(java.awt.Menu receiver)  { int value = receiver.countItems(); }
    static void call8(java.awt.Menu receiver)  { java.awt.MenuItem value = receiver.getItem((int) 0); }
    static void call9(java.awt.Menu receiver)  { java.awt.MenuItem value = receiver.add((java.awt.MenuItem) null); }
    static void call10(java.awt.Menu receiver)  { receiver.add((java.lang.String) null); }
    static void call11(java.awt.Menu receiver)  { receiver.insert((java.awt.MenuItem) null, (int) 0); }
    static void call12(java.awt.Menu receiver)  { receiver.insert((java.lang.String) null, (int) 0); }
    static void call13(java.awt.Menu receiver)  { receiver.addSeparator(); }
    static void call14(java.awt.Menu receiver)  { receiver.insertSeparator((int) 0); }
    static void call15(java.awt.Menu receiver)  { receiver.remove((int) 0); }
    static void call16(java.awt.Menu receiver)  { receiver.remove((java.awt.MenuComponent) null); }
    static void call17(java.awt.Menu receiver)  { receiver.removeAll(); }
    static void call18(java.awt.Menu receiver)  { java.lang.String value = receiver.paramString(); }
    static void call19(java.awt.Menu receiver)  { javax.accessibility.AccessibleContext value = receiver.getAccessibleContext(); }
}

abstract class Override_java_awt_Menu extends java.awt.Menu {
    Override_java_awt_Menu() throws java.awt.HeadlessException { super(); }
    @Override
    public void addNotify()  { throw new UnsupportedOperationException(); }
    @Override
    public void removeNotify()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isTearOff()  { throw new UnsupportedOperationException(); }
    @Override
    public int getItemCount()  { throw new UnsupportedOperationException(); }
    @Override
    public int countItems()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.MenuItem getItem(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.MenuItem add(java.awt.MenuItem arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void add(java.lang.String arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void insert(java.awt.MenuItem arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void insert(java.lang.String arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void addSeparator()  { throw new UnsupportedOperationException(); }
    @Override
    public void insertSeparator(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void remove(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void remove(java.awt.MenuComponent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void removeAll()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String paramString()  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.AccessibleContext getAccessibleContext()  { throw new UnsupportedOperationException(); }
}
