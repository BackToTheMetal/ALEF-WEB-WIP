package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_GridLayout {
    static void call0()  { java.awt.GridLayout value = new java.awt.GridLayout(); }
    static void call1()  { java.awt.GridLayout value = new java.awt.GridLayout((int) 0, (int) 0); }
    static void call2()  { java.awt.GridLayout value = new java.awt.GridLayout((int) 0, (int) 0, (int) 0, (int) 0); }
    static void call3(java.awt.GridLayout receiver)  { int value = receiver.getRows(); }
    static void call4(java.awt.GridLayout receiver)  { receiver.setRows((int) 0); }
    static void call5(java.awt.GridLayout receiver)  { int value = receiver.getColumns(); }
    static void call6(java.awt.GridLayout receiver)  { receiver.setColumns((int) 0); }
    static void call7(java.awt.GridLayout receiver)  { int value = receiver.getHgap(); }
    static void call8(java.awt.GridLayout receiver)  { receiver.setHgap((int) 0); }
    static void call9(java.awt.GridLayout receiver)  { int value = receiver.getVgap(); }
    static void call10(java.awt.GridLayout receiver)  { receiver.setVgap((int) 0); }
    static void call11(java.awt.GridLayout receiver)  { receiver.addLayoutComponent((java.lang.String) null, (java.awt.Component) null); }
    static void call12(java.awt.GridLayout receiver)  { receiver.removeLayoutComponent((java.awt.Component) null); }
    static void call13(java.awt.GridLayout receiver)  { java.awt.Dimension value = receiver.preferredLayoutSize((java.awt.Container) null); }
    static void call14(java.awt.GridLayout receiver)  { java.awt.Dimension value = receiver.minimumLayoutSize((java.awt.Container) null); }
    static void call15(java.awt.GridLayout receiver)  { receiver.layoutContainer((java.awt.Container) null); }
    static void call16(java.awt.GridLayout receiver)  { java.lang.String value = receiver.toString(); }
}

abstract class Override_java_awt_GridLayout extends java.awt.GridLayout {
    Override_java_awt_GridLayout()  { super(); }
    @Override
    public int getRows()  { throw new UnsupportedOperationException(); }
    @Override
    public void setRows(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int getColumns()  { throw new UnsupportedOperationException(); }
    @Override
    public void setColumns(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int getHgap()  { throw new UnsupportedOperationException(); }
    @Override
    public void setHgap(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int getVgap()  { throw new UnsupportedOperationException(); }
    @Override
    public void setVgap(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void addLayoutComponent(java.lang.String arg0, java.awt.Component arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void removeLayoutComponent(java.awt.Component arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Dimension preferredLayoutSize(java.awt.Container arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Dimension minimumLayoutSize(java.awt.Container arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void layoutContainer(java.awt.Container arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String toString()  { throw new UnsupportedOperationException(); }
}
