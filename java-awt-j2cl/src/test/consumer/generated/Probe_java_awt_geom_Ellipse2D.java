package java.awt.geom;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_geom_Ellipse2D {
    static void call0(java.awt.geom.Ellipse2D receiver)  { boolean value = receiver.contains((double) 0, (double) 0); }
    static void call1(java.awt.geom.Ellipse2D receiver)  { boolean value = receiver.intersects((double) 0, (double) 0, (double) 0, (double) 0); }
    static void call2(java.awt.geom.Ellipse2D receiver)  { boolean value = receiver.contains((double) 0, (double) 0, (double) 0, (double) 0); }
    static void call3(java.awt.geom.Ellipse2D receiver)  { java.awt.geom.PathIterator value = receiver.getPathIterator((java.awt.geom.AffineTransform) null); }
    static void call4(java.awt.geom.Ellipse2D receiver)  { int value = receiver.hashCode(); }
    static void call5(java.awt.geom.Ellipse2D receiver)  { boolean value = receiver.equals((java.lang.Object) null); }
}

abstract class Override_java_awt_geom_Ellipse2D extends java.awt.geom.Ellipse2D {
    Override_java_awt_geom_Ellipse2D()  { super(); }
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
