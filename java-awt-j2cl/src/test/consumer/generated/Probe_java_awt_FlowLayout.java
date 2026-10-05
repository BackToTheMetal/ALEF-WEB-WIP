package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_FlowLayout {
    static void call0() { int value = java.awt.FlowLayout.LEFT; }
    static void call1() { int value = java.awt.FlowLayout.CENTER; }
    static void call2() { int value = java.awt.FlowLayout.RIGHT; }
    static void call3() { int value = java.awt.FlowLayout.LEADING; }
    static void call4() { int value = java.awt.FlowLayout.TRAILING; }
    static void call5()  { java.awt.FlowLayout value = new java.awt.FlowLayout(); }
    static void call6()  { java.awt.FlowLayout value = new java.awt.FlowLayout((int) 0); }
    static void call7()  { java.awt.FlowLayout value = new java.awt.FlowLayout((int) 0, (int) 0, (int) 0); }
    static void call8(java.awt.FlowLayout receiver)  { int value = receiver.getAlignment(); }
    static void call9(java.awt.FlowLayout receiver)  { receiver.setAlignment((int) 0); }
    static void call10(java.awt.FlowLayout receiver)  { int value = receiver.getHgap(); }
    static void call11(java.awt.FlowLayout receiver)  { receiver.setHgap((int) 0); }
    static void call12(java.awt.FlowLayout receiver)  { int value = receiver.getVgap(); }
    static void call13(java.awt.FlowLayout receiver)  { receiver.setVgap((int) 0); }
    static void call14(java.awt.FlowLayout receiver)  { receiver.setAlignOnBaseline(false); }
    static void call15(java.awt.FlowLayout receiver)  { boolean value = receiver.getAlignOnBaseline(); }
    static void call16(java.awt.FlowLayout receiver)  { receiver.addLayoutComponent((java.lang.String) null, (java.awt.Component) null); }
    static void call17(java.awt.FlowLayout receiver)  { receiver.removeLayoutComponent((java.awt.Component) null); }
    static void call18(java.awt.FlowLayout receiver)  { java.awt.Dimension value = receiver.preferredLayoutSize((java.awt.Container) null); }
    static void call19(java.awt.FlowLayout receiver)  { java.awt.Dimension value = receiver.minimumLayoutSize((java.awt.Container) null); }
    static void call20(java.awt.FlowLayout receiver)  { receiver.layoutContainer((java.awt.Container) null); }
    static void call21(java.awt.FlowLayout receiver)  { java.lang.String value = receiver.toString(); }
}

abstract class Override_java_awt_FlowLayout extends java.awt.FlowLayout {
    Override_java_awt_FlowLayout()  { super(); }
    @Override
    public int getAlignment()  { throw new UnsupportedOperationException(); }
    @Override
    public void setAlignment(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int getHgap()  { throw new UnsupportedOperationException(); }
    @Override
    public void setHgap(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int getVgap()  { throw new UnsupportedOperationException(); }
    @Override
    public void setVgap(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setAlignOnBaseline(boolean arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean getAlignOnBaseline()  { throw new UnsupportedOperationException(); }
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
