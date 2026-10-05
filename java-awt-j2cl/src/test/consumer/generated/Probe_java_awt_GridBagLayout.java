package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_GridBagLayout {
    static void call0() { int value = java.awt.GridBagLayout.MAXGRIDSIZE; }
    static void call1() { int value = java.awt.GridBagLayout.MINSIZE; }
    static void call2() { int value = java.awt.GridBagLayout.PREFERREDSIZE; }
    static void call3(java.awt.GridBagLayout receiver) { java.awt.GridBagConstraints value = receiver.defaultConstraints; receiver.defaultConstraints = value; }
    static void call4(java.awt.GridBagLayout receiver) { java.awt.GridBagLayoutInfo value = receiver.layoutInfo; receiver.layoutInfo = value; }
    static void call5(java.awt.GridBagLayout receiver) { int[] value = receiver.columnWidths; receiver.columnWidths = value; }
    static void call6(java.awt.GridBagLayout receiver) { int[] value = receiver.rowHeights; receiver.rowHeights = value; }
    static void call7(java.awt.GridBagLayout receiver) { double[] value = receiver.columnWeights; receiver.columnWeights = value; }
    static void call8(java.awt.GridBagLayout receiver) { double[] value = receiver.rowWeights; receiver.rowWeights = value; }
    static void call9()  { java.awt.GridBagLayout value = new java.awt.GridBagLayout(); }
    static void call10(java.awt.GridBagLayout receiver)  { receiver.setConstraints((java.awt.Component) null, (java.awt.GridBagConstraints) null); }
    static void call11(java.awt.GridBagLayout receiver)  { java.awt.GridBagConstraints value = receiver.getConstraints((java.awt.Component) null); }
    static void call12(java.awt.GridBagLayout receiver)  { java.awt.GridBagConstraints value = receiver.lookupConstraints((java.awt.Component) null); }
    static void call13(java.awt.GridBagLayout receiver)  { java.awt.Point value = receiver.getLayoutOrigin(); }
    static void call14(java.awt.GridBagLayout receiver)  { int[][] value = receiver.getLayoutDimensions(); }
    static void call15(java.awt.GridBagLayout receiver)  { double[][] value = receiver.getLayoutWeights(); }
    static void call16(java.awt.GridBagLayout receiver)  { java.awt.Point value = receiver.location((int) 0, (int) 0); }
    static void call17(java.awt.GridBagLayout receiver)  { receiver.addLayoutComponent((java.lang.String) null, (java.awt.Component) null); }
    static void call18(java.awt.GridBagLayout receiver)  { receiver.addLayoutComponent((java.awt.Component) null, (java.lang.Object) null); }
    static void call19(java.awt.GridBagLayout receiver)  { receiver.removeLayoutComponent((java.awt.Component) null); }
    static void call20(java.awt.GridBagLayout receiver)  { java.awt.Dimension value = receiver.preferredLayoutSize((java.awt.Container) null); }
    static void call21(java.awt.GridBagLayout receiver)  { java.awt.Dimension value = receiver.minimumLayoutSize((java.awt.Container) null); }
    static void call22(java.awt.GridBagLayout receiver)  { java.awt.Dimension value = receiver.maximumLayoutSize((java.awt.Container) null); }
    static void call23(java.awt.GridBagLayout receiver)  { float value = receiver.getLayoutAlignmentX((java.awt.Container) null); }
    static void call24(java.awt.GridBagLayout receiver)  { float value = receiver.getLayoutAlignmentY((java.awt.Container) null); }
    static void call25(java.awt.GridBagLayout receiver)  { receiver.invalidateLayout((java.awt.Container) null); }
    static void call26(java.awt.GridBagLayout receiver)  { receiver.layoutContainer((java.awt.Container) null); }
    static void call27(java.awt.GridBagLayout receiver)  { java.lang.String value = receiver.toString(); }
    static void call28(java.awt.GridBagLayout receiver)  { java.awt.GridBagLayoutInfo value = receiver.getLayoutInfo((java.awt.Container) null, (int) 0); }
    static void call29(java.awt.GridBagLayout receiver)  { java.awt.GridBagLayoutInfo value = receiver.GetLayoutInfo((java.awt.Container) null, (int) 0); }
    static void call30(java.awt.GridBagLayout receiver)  { receiver.adjustForGravity((java.awt.GridBagConstraints) null, (java.awt.Rectangle) null); }
    static void call31(java.awt.GridBagLayout receiver)  { receiver.AdjustForGravity((java.awt.GridBagConstraints) null, (java.awt.Rectangle) null); }
    static void call32(java.awt.GridBagLayout receiver)  { java.awt.Dimension value = receiver.getMinSize((java.awt.Container) null, (java.awt.GridBagLayoutInfo) null); }
    static void call33(java.awt.GridBagLayout receiver)  { java.awt.Dimension value = receiver.GetMinSize((java.awt.Container) null, (java.awt.GridBagLayoutInfo) null); }
    static void call34(java.awt.GridBagLayout receiver)  { receiver.arrangeGrid((java.awt.Container) null); }
    static void call35(java.awt.GridBagLayout receiver)  { receiver.ArrangeGrid((java.awt.Container) null); }
}

abstract class Override_java_awt_GridBagLayout extends java.awt.GridBagLayout {
    Override_java_awt_GridBagLayout()  { super(); }
    @Override
    public void setConstraints(java.awt.Component arg0, java.awt.GridBagConstraints arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.GridBagConstraints getConstraints(java.awt.Component arg0)  { throw new UnsupportedOperationException(); }
    @Override
    protected java.awt.GridBagConstraints lookupConstraints(java.awt.Component arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Point getLayoutOrigin()  { throw new UnsupportedOperationException(); }
    @Override
    public int[][] getLayoutDimensions()  { throw new UnsupportedOperationException(); }
    @Override
    public double[][] getLayoutWeights()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Point location(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void addLayoutComponent(java.lang.String arg0, java.awt.Component arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void addLayoutComponent(java.awt.Component arg0, java.lang.Object arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void removeLayoutComponent(java.awt.Component arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Dimension preferredLayoutSize(java.awt.Container arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Dimension minimumLayoutSize(java.awt.Container arg0)  { throw new UnsupportedOperationException(); }
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
    @Override
    protected java.awt.GridBagLayoutInfo getLayoutInfo(java.awt.Container arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    protected java.awt.GridBagLayoutInfo GetLayoutInfo(java.awt.Container arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    protected void adjustForGravity(java.awt.GridBagConstraints arg0, java.awt.Rectangle arg1)  { throw new UnsupportedOperationException(); }
    @Override
    protected void AdjustForGravity(java.awt.GridBagConstraints arg0, java.awt.Rectangle arg1)  { throw new UnsupportedOperationException(); }
    @Override
    protected java.awt.Dimension getMinSize(java.awt.Container arg0, java.awt.GridBagLayoutInfo arg1)  { throw new UnsupportedOperationException(); }
    @Override
    protected java.awt.Dimension GetMinSize(java.awt.Container arg0, java.awt.GridBagLayoutInfo arg1)  { throw new UnsupportedOperationException(); }
    @Override
    protected void arrangeGrid(java.awt.Container arg0)  { throw new UnsupportedOperationException(); }
    @Override
    protected void ArrangeGrid(java.awt.Container arg0)  { throw new UnsupportedOperationException(); }
}
