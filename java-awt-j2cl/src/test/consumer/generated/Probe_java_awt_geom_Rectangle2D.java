package java.awt.geom;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_geom_Rectangle2D {
    static void call0() { int value = java.awt.geom.Rectangle2D.OUT_LEFT; }
    static void call1() { int value = java.awt.geom.Rectangle2D.OUT_TOP; }
    static void call2() { int value = java.awt.geom.Rectangle2D.OUT_RIGHT; }
    static void call3() { int value = java.awt.geom.Rectangle2D.OUT_BOTTOM; }
    static void call4(java.awt.geom.Rectangle2D receiver)  { receiver.setRect((double) 0, (double) 0, (double) 0, (double) 0); }
    static void call5(java.awt.geom.Rectangle2D receiver)  { receiver.setRect((java.awt.geom.Rectangle2D) null); }
    static void call6(java.awt.geom.Rectangle2D receiver)  { boolean value = receiver.intersectsLine((double) 0, (double) 0, (double) 0, (double) 0); }
    static void call7(java.awt.geom.Rectangle2D receiver)  { boolean value = receiver.intersectsLine((java.awt.geom.Line2D) null); }
    static void call8(java.awt.geom.Rectangle2D receiver)  { int value = receiver.outcode((double) 0, (double) 0); }
    static void call9(java.awt.geom.Rectangle2D receiver)  { int value = receiver.outcode((java.awt.geom.Point2D) null); }
    static void call10(java.awt.geom.Rectangle2D receiver)  { receiver.setFrame((double) 0, (double) 0, (double) 0, (double) 0); }
    static void call11(java.awt.geom.Rectangle2D receiver)  { java.awt.geom.Rectangle2D value = receiver.getBounds2D(); }
    static void call12(java.awt.geom.Rectangle2D receiver)  { boolean value = receiver.contains((double) 0, (double) 0); }
    static void call13(java.awt.geom.Rectangle2D receiver)  { boolean value = receiver.intersects((double) 0, (double) 0, (double) 0, (double) 0); }
    static void call14(java.awt.geom.Rectangle2D receiver)  { boolean value = receiver.contains((double) 0, (double) 0, (double) 0, (double) 0); }
    static void call15(java.awt.geom.Rectangle2D receiver)  { java.awt.geom.Rectangle2D value = receiver.createIntersection((java.awt.geom.Rectangle2D) null); }
    static void call16()  { java.awt.geom.Rectangle2D.intersect((java.awt.geom.Rectangle2D) null, (java.awt.geom.Rectangle2D) null, (java.awt.geom.Rectangle2D) null); }
    static void call17(java.awt.geom.Rectangle2D receiver)  { java.awt.geom.Rectangle2D value = receiver.createUnion((java.awt.geom.Rectangle2D) null); }
    static void call18()  { java.awt.geom.Rectangle2D.union((java.awt.geom.Rectangle2D) null, (java.awt.geom.Rectangle2D) null, (java.awt.geom.Rectangle2D) null); }
    static void call19(java.awt.geom.Rectangle2D receiver)  { receiver.add((double) 0, (double) 0); }
    static void call20(java.awt.geom.Rectangle2D receiver)  { receiver.add((java.awt.geom.Point2D) null); }
    static void call21(java.awt.geom.Rectangle2D receiver)  { receiver.add((java.awt.geom.Rectangle2D) null); }
    static void call22(java.awt.geom.Rectangle2D receiver)  { java.awt.geom.PathIterator value = receiver.getPathIterator((java.awt.geom.AffineTransform) null); }
    static void call23(java.awt.geom.Rectangle2D receiver)  { java.awt.geom.PathIterator value = receiver.getPathIterator((java.awt.geom.AffineTransform) null, (double) 0); }
    static void call24(java.awt.geom.Rectangle2D receiver)  { int value = receiver.hashCode(); }
    static void call25(java.awt.geom.Rectangle2D receiver)  { boolean value = receiver.equals((java.lang.Object) null); }
}

abstract class Override_java_awt_geom_Rectangle2D extends java.awt.geom.Rectangle2D {
    Override_java_awt_geom_Rectangle2D()  { super(); }
    @Override
    public void setRect(double arg0, double arg1, double arg2, double arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public void setRect(java.awt.geom.Rectangle2D arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean intersectsLine(double arg0, double arg1, double arg2, double arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean intersectsLine(java.awt.geom.Line2D arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int outcode(double arg0, double arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public int outcode(java.awt.geom.Point2D arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setFrame(double arg0, double arg1, double arg2, double arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.Rectangle2D getBounds2D()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean contains(double arg0, double arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean intersects(double arg0, double arg1, double arg2, double arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean contains(double arg0, double arg1, double arg2, double arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.Rectangle2D createIntersection(java.awt.geom.Rectangle2D arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.Rectangle2D createUnion(java.awt.geom.Rectangle2D arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void add(double arg0, double arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void add(java.awt.geom.Point2D arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void add(java.awt.geom.Rectangle2D arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.PathIterator getPathIterator(java.awt.geom.AffineTransform arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.PathIterator getPathIterator(java.awt.geom.AffineTransform arg0, double arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public int hashCode()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean equals(java.lang.Object arg0)  { throw new UnsupportedOperationException(); }
}
