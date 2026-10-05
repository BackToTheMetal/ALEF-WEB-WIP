package java.awt.geom;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_geom_Area {
    static void call0()  { java.awt.geom.Area value = new java.awt.geom.Area(); }
    static void call1()  { java.awt.geom.Area value = new java.awt.geom.Area((java.awt.Shape) null); }
    static void call2(java.awt.geom.Area receiver)  { receiver.add((java.awt.geom.Area) null); }
    static void call3(java.awt.geom.Area receiver)  { receiver.subtract((java.awt.geom.Area) null); }
    static void call4(java.awt.geom.Area receiver)  { receiver.intersect((java.awt.geom.Area) null); }
    static void call5(java.awt.geom.Area receiver)  { receiver.exclusiveOr((java.awt.geom.Area) null); }
    static void call6(java.awt.geom.Area receiver)  { receiver.reset(); }
    static void call7(java.awt.geom.Area receiver)  { boolean value = receiver.isEmpty(); }
    static void call8(java.awt.geom.Area receiver)  { boolean value = receiver.isPolygonal(); }
    static void call9(java.awt.geom.Area receiver)  { boolean value = receiver.isRectangular(); }
    static void call10(java.awt.geom.Area receiver)  { boolean value = receiver.isSingular(); }
    static void call11(java.awt.geom.Area receiver)  { java.awt.geom.Rectangle2D value = receiver.getBounds2D(); }
    static void call12(java.awt.geom.Area receiver)  { java.awt.Rectangle value = receiver.getBounds(); }
    static void call13(java.awt.geom.Area receiver)  { java.lang.Object value = receiver.clone(); }
    static void call14(java.awt.geom.Area receiver)  { boolean value = receiver.equals((java.awt.geom.Area) null); }
    static void call15(java.awt.geom.Area receiver)  { receiver.transform((java.awt.geom.AffineTransform) null); }
    static void call16(java.awt.geom.Area receiver)  { java.awt.geom.Area value = receiver.createTransformedArea((java.awt.geom.AffineTransform) null); }
    static void call17(java.awt.geom.Area receiver)  { boolean value = receiver.contains((double) 0, (double) 0); }
    static void call18(java.awt.geom.Area receiver)  { boolean value = receiver.contains((java.awt.geom.Point2D) null); }
    static void call19(java.awt.geom.Area receiver)  { boolean value = receiver.contains((double) 0, (double) 0, (double) 0, (double) 0); }
    static void call20(java.awt.geom.Area receiver)  { boolean value = receiver.contains((java.awt.geom.Rectangle2D) null); }
    static void call21(java.awt.geom.Area receiver)  { boolean value = receiver.intersects((double) 0, (double) 0, (double) 0, (double) 0); }
    static void call22(java.awt.geom.Area receiver)  { boolean value = receiver.intersects((java.awt.geom.Rectangle2D) null); }
    static void call23(java.awt.geom.Area receiver)  { java.awt.geom.PathIterator value = receiver.getPathIterator((java.awt.geom.AffineTransform) null); }
    static void call24(java.awt.geom.Area receiver)  { java.awt.geom.PathIterator value = receiver.getPathIterator((java.awt.geom.AffineTransform) null, (double) 0); }
}

abstract class Override_java_awt_geom_Area extends java.awt.geom.Area {
    Override_java_awt_geom_Area()  { super(); }
    @Override
    public void add(java.awt.geom.Area arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void subtract(java.awt.geom.Area arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void intersect(java.awt.geom.Area arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void exclusiveOr(java.awt.geom.Area arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void reset()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isEmpty()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isPolygonal()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isRectangular()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isSingular()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.Rectangle2D getBounds2D()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Rectangle getBounds()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Object clone()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean equals(java.awt.geom.Area arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void transform(java.awt.geom.AffineTransform arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.Area createTransformedArea(java.awt.geom.AffineTransform arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean contains(double arg0, double arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean contains(java.awt.geom.Point2D arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean contains(double arg0, double arg1, double arg2, double arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean contains(java.awt.geom.Rectangle2D arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean intersects(double arg0, double arg1, double arg2, double arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean intersects(java.awt.geom.Rectangle2D arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.PathIterator getPathIterator(java.awt.geom.AffineTransform arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.PathIterator getPathIterator(java.awt.geom.AffineTransform arg0, double arg1)  { throw new UnsupportedOperationException(); }
}
