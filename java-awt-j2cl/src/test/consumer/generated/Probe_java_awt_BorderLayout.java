package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_BorderLayout {
    static void call0() { java.lang.String value = java.awt.BorderLayout.NORTH; }
    static void call1() { java.lang.String value = java.awt.BorderLayout.SOUTH; }
    static void call2() { java.lang.String value = java.awt.BorderLayout.EAST; }
    static void call3() { java.lang.String value = java.awt.BorderLayout.WEST; }
    static void call4() { java.lang.String value = java.awt.BorderLayout.CENTER; }
    static void call5() { java.lang.String value = java.awt.BorderLayout.BEFORE_FIRST_LINE; }
    static void call6() { java.lang.String value = java.awt.BorderLayout.AFTER_LAST_LINE; }
    static void call7() { java.lang.String value = java.awt.BorderLayout.BEFORE_LINE_BEGINS; }
    static void call8() { java.lang.String value = java.awt.BorderLayout.AFTER_LINE_ENDS; }
    static void call9() { java.lang.String value = java.awt.BorderLayout.PAGE_START; }
    static void call10() { java.lang.String value = java.awt.BorderLayout.PAGE_END; }
    static void call11() { java.lang.String value = java.awt.BorderLayout.LINE_START; }
    static void call12() { java.lang.String value = java.awt.BorderLayout.LINE_END; }
    static void call13()  { java.awt.BorderLayout value = new java.awt.BorderLayout(); }
    static void call14()  { java.awt.BorderLayout value = new java.awt.BorderLayout((int) 0, (int) 0); }
    static void call15(java.awt.BorderLayout receiver)  { int value = receiver.getHgap(); }
    static void call16(java.awt.BorderLayout receiver)  { receiver.setHgap((int) 0); }
    static void call17(java.awt.BorderLayout receiver)  { int value = receiver.getVgap(); }
    static void call18(java.awt.BorderLayout receiver)  { receiver.setVgap((int) 0); }
    static void call19(java.awt.BorderLayout receiver)  { receiver.addLayoutComponent((java.awt.Component) null, (java.lang.Object) null); }
    static void call20(java.awt.BorderLayout receiver)  { receiver.addLayoutComponent((java.lang.String) null, (java.awt.Component) null); }
    static void call21(java.awt.BorderLayout receiver)  { receiver.removeLayoutComponent((java.awt.Component) null); }
    static void call22(java.awt.BorderLayout receiver)  { java.awt.Component value = receiver.getLayoutComponent((java.lang.Object) null); }
    static void call23(java.awt.BorderLayout receiver)  { java.awt.Component value = receiver.getLayoutComponent((java.awt.Container) null, (java.lang.Object) null); }
    static void call24(java.awt.BorderLayout receiver)  { java.lang.Object value = receiver.getConstraints((java.awt.Component) null); }
    static void call25(java.awt.BorderLayout receiver)  { java.awt.Dimension value = receiver.minimumLayoutSize((java.awt.Container) null); }
    static void call26(java.awt.BorderLayout receiver)  { java.awt.Dimension value = receiver.preferredLayoutSize((java.awt.Container) null); }
    static void call27(java.awt.BorderLayout receiver)  { java.awt.Dimension value = receiver.maximumLayoutSize((java.awt.Container) null); }
    static void call28(java.awt.BorderLayout receiver)  { float value = receiver.getLayoutAlignmentX((java.awt.Container) null); }
    static void call29(java.awt.BorderLayout receiver)  { float value = receiver.getLayoutAlignmentY((java.awt.Container) null); }
    static void call30(java.awt.BorderLayout receiver)  { receiver.invalidateLayout((java.awt.Container) null); }
    static void call31(java.awt.BorderLayout receiver)  { receiver.layoutContainer((java.awt.Container) null); }
    static void call32(java.awt.BorderLayout receiver)  { java.lang.String value = receiver.toString(); }
}

abstract class Override_java_awt_BorderLayout extends java.awt.BorderLayout {
    Override_java_awt_BorderLayout()  { super(); }
    @Override
    public int getHgap()  { throw new UnsupportedOperationException(); }
    @Override
    public void setHgap(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int getVgap()  { throw new UnsupportedOperationException(); }
    @Override
    public void setVgap(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void addLayoutComponent(java.awt.Component arg0, java.lang.Object arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void addLayoutComponent(java.lang.String arg0, java.awt.Component arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void removeLayoutComponent(java.awt.Component arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Component getLayoutComponent(java.lang.Object arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Component getLayoutComponent(java.awt.Container arg0, java.lang.Object arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Object getConstraints(java.awt.Component arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Dimension minimumLayoutSize(java.awt.Container arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Dimension preferredLayoutSize(java.awt.Container arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Dimension maximumLayoutSize(java.awt.Container arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public float getLayoutAlignmentX(java.awt.Container arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public float getLayoutAlignmentY(java.awt.Container arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void invalidateLayout(java.awt.Container arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void layoutContainer(java.awt.Container arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String toString()  { throw new UnsupportedOperationException(); }
}
