package java.awt.geom;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_geom_CubicCurve2D {
    static void call0(java.awt.geom.CubicCurve2D receiver)  { double value = receiver.getX1(); }
    static void call1(java.awt.geom.CubicCurve2D receiver)  { double value = receiver.getY1(); }
    static void call2(java.awt.geom.CubicCurve2D receiver)  { java.awt.geom.Point2D value = receiver.getP1(); }
    static void call3(java.awt.geom.CubicCurve2D receiver)  { double value = receiver.getCtrlX1(); }
    static void call4(java.awt.geom.CubicCurve2D receiver)  { double value = receiver.getCtrlY1(); }
    static void call5(java.awt.geom.CubicCurve2D receiver)  { java.awt.geom.Point2D value = receiver.getCtrlP1(); }
    static void call6(java.awt.geom.CubicCurve2D receiver)  { double value = receiver.getCtrlX2(); }
    static void call7(java.awt.geom.CubicCurve2D receiver)  { double value = receiver.getCtrlY2(); }
    static void call8(java.awt.geom.CubicCurve2D receiver)  { java.awt.geom.Point2D value = receiver.getCtrlP2(); }
    static void call9(java.awt.geom.CubicCurve2D receiver)  { double value = receiver.getX2(); }
    static void call10(java.awt.geom.CubicCurve2D receiver)  { double value = receiver.getY2(); }
    static void call11(java.awt.geom.CubicCurve2D receiver)  { java.awt.geom.Point2D value = receiver.getP2(); }
    static void call12(java.awt.geom.CubicCurve2D receiver)  { receiver.setCurve((double) 0, (double) 0, (double) 0, (double) 0, (double) 0, (double) 0, (double) 0, (double) 0); }
    static void call13(java.awt.geom.CubicCurve2D receiver)  { receiver.setCurve((double[]) null, (int) 0); }
    static void call14(java.awt.geom.CubicCurve2D receiver)  { receiver.setCurve((java.awt.geom.Point2D) null, (java.awt.geom.Point2D) null, (java.awt.geom.Point2D) null, (java.awt.geom.Point2D) null); }
    static void call15(java.awt.geom.CubicCurve2D receiver)  { receiver.setCurve((java.awt.geom.Point2D[]) null, (int) 0); }
    static void call16(java.awt.geom.CubicCurve2D receiver)  { receiver.setCurve((java.awt.geom.CubicCurve2D) null); }
    static void call17()  { double value = java.awt.geom.CubicCurve2D.getFlatnessSq((double) 0, (double) 0, (double) 0, (double) 0, (double) 0, (double) 0, (double) 0, (double) 0); }
    static void call18()  { double value = java.awt.geom.CubicCurve2D.getFlatness((double) 0, (double) 0, (double) 0, (double) 0, (double) 0, (double) 0, (double) 0, (double) 0); }
    static void call19()  { double value = java.awt.geom.CubicCurve2D.getFlatnessSq((double[]) null, (int) 0); }
    static void call20()  { double value = java.awt.geom.CubicCurve2D.getFlatness((double[]) null, (int) 0); }
    static void call21(java.awt.geom.CubicCurve2D receiver)  { double value = receiver.getFlatnessSq(); }
    static void call22(java.awt.geom.CubicCurve2D receiver)  { double value = receiver.getFlatness(); }
    static void call23(java.awt.geom.CubicCurve2D receiver)  { receiver.subdivide((java.awt.geom.CubicCurve2D) null, (java.awt.geom.CubicCurve2D) null); }
    static void call24()  { java.awt.geom.CubicCurve2D.subdivide((java.awt.geom.CubicCurve2D) null, (java.awt.geom.CubicCurve2D) null, (java.awt.geom.CubicCurve2D) null); }
    static void call25()  { java.awt.geom.CubicCurve2D.subdivide((double[]) null, (int) 0, (double[]) null, (int) 0, (double[]) null, (int) 0); }
    static void call26()  { int value = java.awt.geom.CubicCurve2D.solveCubic((double[]) null); }
    static void call27()  { int value = java.awt.geom.CubicCurve2D.solveCubic((double[]) null, (double[]) null); }
    static void call28(java.awt.geom.CubicCurve2D receiver)  { boolean value = receiver.contains((double) 0, (double) 0); }
    static void call29(java.awt.geom.CubicCurve2D receiver)  { boolean value = receiver.contains((java.awt.geom.Point2D) null); }
    static void call30(java.awt.geom.CubicCurve2D receiver)  { boolean value = receiver.intersects((double) 0, (double) 0, (double) 0, (double) 0); }
    static void call31(java.awt.geom.CubicCurve2D receiver)  { boolean value = receiver.intersects((java.awt.geom.Rectangle2D) null); }
    static void call32(java.awt.geom.CubicCurve2D receiver)  { boolean value = receiver.contains((double) 0, (double) 0, (double) 0, (double) 0); }
    static void call33(java.awt.geom.CubicCurve2D receiver)  { boolean value = receiver.contains((java.awt.geom.Rectangle2D) null); }
    static void call34(java.awt.geom.CubicCurve2D receiver)  { java.awt.Rectangle value = receiver.getBounds(); }
    static void call35(java.awt.geom.CubicCurve2D receiver)  { java.awt.geom.PathIterator value = receiver.getPathIterator((java.awt.geom.AffineTransform) null); }
    static void call36(java.awt.geom.CubicCurve2D receiver)  { java.awt.geom.PathIterator value = receiver.getPathIterator((java.awt.geom.AffineTransform) null, (double) 0); }
    static void call37(java.awt.geom.CubicCurve2D receiver)  { java.lang.Object value = receiver.clone(); }
}

abstract class Override_java_awt_geom_CubicCurve2D extends java.awt.geom.CubicCurve2D {
    Override_java_awt_geom_CubicCurve2D()  { super(); }
    @Override
    public double getX1()  { throw new UnsupportedOperationException(); }
    @Override
    public double getY1()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.Point2D getP1()  { throw new UnsupportedOperationException(); }
    @Override
    public double getCtrlX1()  { throw new UnsupportedOperationException(); }
    @Override
    public double getCtrlY1()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.Point2D getCtrlP1()  { throw new UnsupportedOperationException(); }
    @Override
    public double getCtrlX2()  { throw new UnsupportedOperationException(); }
    @Override
    public double getCtrlY2()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.Point2D getCtrlP2()  { throw new UnsupportedOperationException(); }
    @Override
    public double getX2()  { throw new UnsupportedOperationException(); }
    @Override
    public double getY2()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.Point2D getP2()  { throw new UnsupportedOperationException(); }
    @Override
    public void setCurve(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7)  { throw new UnsupportedOperationException(); }
    @Override
    public void setCurve(double[] arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void setCurve(java.awt.geom.Point2D arg0, java.awt.geom.Point2D arg1, java.awt.geom.Point2D arg2, java.awt.geom.Point2D arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public void setCurve(java.awt.geom.Point2D[] arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void setCurve(java.awt.geom.CubicCurve2D arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public double getFlatnessSq()  { throw new UnsupportedOperationException(); }
    @Override
    public double getFlatness()  { throw new UnsupportedOperationException(); }
    @Override
    public void subdivide(java.awt.geom.CubicCurve2D arg0, java.awt.geom.CubicCurve2D arg1)  { throw new UnsupportedOperationException(); }
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
    public java.awt.Rectangle getBounds()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.PathIterator getPathIterator(java.awt.geom.AffineTransform arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.PathIterator getPathIterator(java.awt.geom.AffineTransform arg0, double arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Object clone()  { throw new UnsupportedOperationException(); }
}
