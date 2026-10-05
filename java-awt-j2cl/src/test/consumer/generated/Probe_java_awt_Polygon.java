package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_Polygon {
    static void call0(java.awt.Polygon receiver) { int value = receiver.npoints; receiver.npoints = value; }
    static void call1(java.awt.Polygon receiver) { int[] value = receiver.xpoints; receiver.xpoints = value; }
    static void call2(java.awt.Polygon receiver) { int[] value = receiver.ypoints; receiver.ypoints = value; }
    static void call3(java.awt.Polygon receiver) { java.awt.Rectangle value = receiver.bounds; receiver.bounds = value; }
    static void call4()  { java.awt.Polygon value = new java.awt.Polygon(); }
    static void call5()  { java.awt.Polygon value = new java.awt.Polygon((int[]) null, (int[]) null, (int) 0); }
    static void call6(java.awt.Polygon receiver)  { receiver.reset(); }
    static void call7(java.awt.Polygon receiver)  { receiver.invalidate(); }
    static void call8(java.awt.Polygon receiver)  { receiver.translate((int) 0, (int) 0); }
    static void call9(java.awt.Polygon receiver)  { receiver.addPoint((int) 0, (int) 0); }
    static void call10(java.awt.Polygon receiver)  { java.awt.Rectangle value = receiver.getBounds(); }
    static void call11(java.awt.Polygon receiver)  { java.awt.Rectangle value = receiver.getBoundingBox(); }
    static void call12(java.awt.Polygon receiver)  { boolean value = receiver.contains((java.awt.Point) null); }
    static void call13(java.awt.Polygon receiver)  { boolean value = receiver.contains((int) 0, (int) 0); }
    static void call14(java.awt.Polygon receiver)  { boolean value = receiver.inside((int) 0, (int) 0); }
    static void call15(java.awt.Polygon receiver)  { java.awt.geom.Rectangle2D value = receiver.getBounds2D(); }
    static void call16(java.awt.Polygon receiver)  { boolean value = receiver.contains((double) 0, (double) 0); }
    static void call17(java.awt.Polygon receiver)  { boolean value = receiver.contains((java.awt.geom.Point2D) null); }
    static void call18(java.awt.Polygon receiver)  { boolean value = receiver.intersects((double) 0, (double) 0, (double) 0, (double) 0); }
    static void call19(java.awt.Polygon receiver)  { boolean value = receiver.intersects((java.awt.geom.Rectangle2D) null); }
    static void call20(java.awt.Polygon receiver)  { boolean value = receiver.contains((double) 0, (double) 0, (double) 0, (double) 0); }
    static void call21(java.awt.Polygon receiver)  { boolean value = receiver.contains((java.awt.geom.Rectangle2D) null); }
    static void call22(java.awt.Polygon receiver)  { java.awt.geom.PathIterator value = receiver.getPathIterator((java.awt.geom.AffineTransform) null); }
    static void call23(java.awt.Polygon receiver)  { java.awt.geom.PathIterator value = receiver.getPathIterator((java.awt.geom.AffineTransform) null, (double) 0); }
}

abstract class Override_java_awt_Polygon extends java.awt.Polygon {
    Override_java_awt_Polygon()  { super(); }
    @Override
    public void reset()  { throw new UnsupportedOperationException(); }
    @Override
    public void invalidate()  { throw new UnsupportedOperationException(); }
    @Override
    public void translate(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void addPoint(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Rectangle getBounds()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Rectangle getBoundingBox()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean contains(java.awt.Point arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean contains(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean inside(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.Rectangle2D getBounds2D()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean contains(double arg0, double arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean contains(java.awt.geom.Point2D arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean intersects(double arg0, double arg1, double arg2, double arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean intersects(java.awt.geom.Rectangle2D arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean contains(double arg0, double arg1, double arg2, double arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean contains(java.awt.geom.Rectangle2D arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.PathIterator getPathIterator(java.awt.geom.AffineTransform arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.PathIterator getPathIterator(java.awt.geom.AffineTransform arg0, double arg1)  { throw new UnsupportedOperationException(); }
}
