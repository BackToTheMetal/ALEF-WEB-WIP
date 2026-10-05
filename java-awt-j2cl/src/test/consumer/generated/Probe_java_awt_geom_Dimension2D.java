package java.awt.geom;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_geom_Dimension2D {
    static void call0(java.awt.geom.Dimension2D receiver)  { double value = receiver.getWidth(); }
    static void call1(java.awt.geom.Dimension2D receiver)  { double value = receiver.getHeight(); }
    static void call2(java.awt.geom.Dimension2D receiver)  { receiver.setSize((double) 0, (double) 0); }
    static void call3(java.awt.geom.Dimension2D receiver)  { receiver.setSize((java.awt.geom.Dimension2D) null); }
    static void call4(java.awt.geom.Dimension2D receiver)  { java.lang.Object value = receiver.clone(); }
}

abstract class Override_java_awt_geom_Dimension2D extends java.awt.geom.Dimension2D {
    Override_java_awt_geom_Dimension2D()  { super(); }
    @Override
    public double getWidth()  { throw new UnsupportedOperationException(); }
    @Override
    public double getHeight()  { throw new UnsupportedOperationException(); }
    @Override
    public void setSize(double arg0, double arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void setSize(java.awt.geom.Dimension2D arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Object clone()  { throw new UnsupportedOperationException(); }
}
