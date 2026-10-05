package java.awt.geom;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_geom_RoundRectangle2D {
    static void call0(java.awt.geom.RoundRectangle2D receiver)  { double value = receiver.getArcWidth(); }
    static void call1(java.awt.geom.RoundRectangle2D receiver)  { double value = receiver.getArcHeight(); }
    static void call2(java.awt.geom.RoundRectangle2D receiver)  { receiver.setRoundRect((double) 0, (double) 0, (double) 0, (double) 0, (double) 0, (double) 0); }
    static void call3(java.awt.geom.RoundRectangle2D receiver)  { receiver.setRoundRect((java.awt.geom.RoundRectangle2D) null); }
    static void call4(java.awt.geom.RoundRectangle2D receiver)  { receiver.setFrame((double) 0, (double) 0, (double) 0, (double) 0); }
    static void call5(java.awt.geom.RoundRectangle2D receiver)  { boolean value = receiver.contains((double) 0, (double) 0); }
    static void call6(java.awt.geom.RoundRectangle2D receiver)  { boolean value = receiver.intersects((double) 0, (double) 0, (double) 0, (double) 0); }
    static void call7(java.awt.geom.RoundRectangle2D receiver)  { boolean value = receiver.contains((double) 0, (double) 0, (double) 0, (double) 0); }
    static void call8(java.awt.geom.RoundRectangle2D receiver)  { java.awt.geom.PathIterator value = receiver.getPathIterator((java.awt.geom.AffineTransform) null); }
    static void call9(java.awt.geom.RoundRectangle2D receiver)  { int value = receiver.hashCode(); }
    static void call10(java.awt.geom.RoundRectangle2D receiver)  { boolean value = receiver.equals((java.lang.Object) null); }
}

abstract class Override_java_awt_geom_RoundRectangle2D extends java.awt.geom.RoundRectangle2D {
    Override_java_awt_geom_RoundRectangle2D()  { super(); }
    @Override
    public double getArcWidth()  { throw new UnsupportedOperationException(); }
    @Override
    public double getArcHeight()  { throw new UnsupportedOperationException(); }
    @Override
    public void setRoundRect(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5)  { throw new UnsupportedOperationException(); }
    @Override
    public void setRoundRect(java.awt.geom.RoundRectangle2D arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setFrame(double arg0, double arg1, double arg2, double arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean contains(double arg0, double arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean intersects(double arg0, double arg1, double arg2, double arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean contains(double arg0, double arg1, double arg2, double arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.PathIterator getPathIterator(java.awt.geom.AffineTransform arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int hashCode()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean equals(java.lang.Object arg0)  { throw new UnsupportedOperationException(); }
}
