package java.awt.geom;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_geom_Line2D {
    static void call0(java.awt.geom.Line2D receiver)  { double value = receiver.getX1(); }
    static void call1(java.awt.geom.Line2D receiver)  { double value = receiver.getY1(); }
    static void call2(java.awt.geom.Line2D receiver)  { java.awt.geom.Point2D value = receiver.getP1(); }
    static void call3(java.awt.geom.Line2D receiver)  { double value = receiver.getX2(); }
    static void call4(java.awt.geom.Line2D receiver)  { double value = receiver.getY2(); }
    static void call5(java.awt.geom.Line2D receiver)  { java.awt.geom.Point2D value = receiver.getP2(); }
    static void call6(java.awt.geom.Line2D receiver)  { receiver.setLine((double) 0, (double) 0, (double) 0, (double) 0); }
    static void call7(java.awt.geom.Line2D receiver)  { receiver.setLine((java.awt.geom.Point2D) null, (java.awt.geom.Point2D) null); }
    static void call8(java.awt.geom.Line2D receiver)  { receiver.setLine((java.awt.geom.Line2D) null); }
    static void call9()  { int value = java.awt.geom.Line2D.relativeCCW((double) 0, (double) 0, (double) 0, (double) 0, (double) 0, (double) 0); }
    static void call10(java.awt.geom.Line2D receiver)  { int value = receiver.relativeCCW((double) 0, (double) 0); }
    static void call11(java.awt.geom.Line2D receiver)  { int value = receiver.relativeCCW((java.awt.geom.Point2D) null); }
    static void call12()  { boolean value = java.awt.geom.Line2D.linesIntersect((double) 0, (double) 0, (double) 0, (double) 0, (double) 0, (double) 0, (double) 0, (double) 0); }
    static void call13(java.awt.geom.Line2D receiver)  { boolean value = receiver.intersectsLine((double) 0, (double) 0, (double) 0, (double) 0); }
    static void call14(java.awt.geom.Line2D receiver)  { boolean value = receiver.intersectsLine((java.awt.geom.Line2D) null); }
    static void call15()  { double value = java.awt.geom.Line2D.ptSegDistSq((double) 0, (double) 0, (double) 0, (double) 0, (double) 0, (double) 0); }
    static void call16()  { double value = java.awt.geom.Line2D.ptSegDist((double) 0, (double) 0, (double) 0, (double) 0, (double) 0, (double) 0); }
    static void call17(java.awt.geom.Line2D receiver)  { double value = receiver.ptSegDistSq((double) 0, (double) 0); }
    static void call18(java.awt.geom.Line2D receiver)  { double value = receiver.ptSegDistSq((java.awt.geom.Point2D) null); }
    static void call19(java.awt.geom.Line2D receiver)  { double value = receiver.ptSegDist((double) 0, (double) 0); }
    static void call20(java.awt.geom.Line2D receiver)  { double value = receiver.ptSegDist((java.awt.geom.Point2D) null); }
    static void call21()  { double value = java.awt.geom.Line2D.ptLineDistSq((double) 0, (double) 0, (double) 0, (double) 0, (double) 0, (double) 0); }
    static void call22()  { double value = java.awt.geom.Line2D.ptLineDist((double) 0, (double) 0, (double) 0, (double) 0, (double) 0, (double) 0); }
    static void call23(java.awt.geom.Line2D receiver)  { double value = receiver.ptLineDistSq((double) 0, (double) 0); }
    static void call24(java.awt.geom.Line2D receiver)  { double value = receiver.ptLineDistSq((java.awt.geom.Point2D) null); }
    static void call25(java.awt.geom.Line2D receiver)  { double value = receiver.ptLineDist((double) 0, (double) 0); }
    static void call26(java.awt.geom.Line2D receiver)  { double value = receiver.ptLineDist((java.awt.geom.Point2D) null); }
    static void call27(java.awt.geom.Line2D receiver)  { boolean value = receiver.contains((double) 0, (double) 0); }
    static void call28(java.awt.geom.Line2D receiver)  { boolean value = receiver.contains((java.awt.geom.Point2D) null); }
    static void call29(java.awt.geom.Line2D receiver)  { boolean value = receiver.intersects((double) 0, (double) 0, (double) 0, (double) 0); }
    static void call30(java.awt.geom.Line2D receiver)  { boolean value = receiver.intersects((java.awt.geom.Rectangle2D) null); }
    static void call31(java.awt.geom.Line2D receiver)  { boolean value = receiver.contains((double) 0, (double) 0, (double) 0, (double) 0); }
    static void call32(java.awt.geom.Line2D receiver)  { boolean value = receiver.contains((java.awt.geom.Rectangle2D) null); }
    static void call33(java.awt.geom.Line2D receiver)  { java.awt.Rectangle value = receiver.getBounds(); }
    static void call34(java.awt.geom.Line2D receiver)  { java.awt.geom.PathIterator value = receiver.getPathIterator((java.awt.geom.AffineTransform) null); }
    static void call35(java.awt.geom.Line2D receiver)  { java.awt.geom.PathIterator value = receiver.getPathIterator((java.awt.geom.AffineTransform) null, (double) 0); }
    static void call36(java.awt.geom.Line2D receiver)  { java.lang.Object value = receiver.clone(); }
}

abstract class Override_java_awt_geom_Line2D extends java.awt.geom.Line2D {
    Override_java_awt_geom_Line2D()  { super(); }
    @Override
    public double getX1()  { throw new UnsupportedOperationException(); }
    @Override
    public double getY1()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.Point2D getP1()  { throw new UnsupportedOperationException(); }
    @Override
    public double getX2()  { throw new UnsupportedOperationException(); }
    @Override
    public double getY2()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.Point2D getP2()  { throw new UnsupportedOperationException(); }
    @Override
    public void setLine(double arg0, double arg1, double arg2, double arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public void setLine(java.awt.geom.Point2D arg0, java.awt.geom.Point2D arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void setLine(java.awt.geom.Line2D arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int relativeCCW(double arg0, double arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public int relativeCCW(java.awt.geom.Point2D arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean intersectsLine(double arg0, double arg1, double arg2, double arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean intersectsLine(java.awt.geom.Line2D arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public double ptSegDistSq(double arg0, double arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public double ptSegDistSq(java.awt.geom.Point2D arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public double ptSegDist(double arg0, double arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public double ptSegDist(java.awt.geom.Point2D arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public double ptLineDistSq(double arg0, double arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public double ptLineDistSq(java.awt.geom.Point2D arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public double ptLineDist(double arg0, double arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public double ptLineDist(java.awt.geom.Point2D arg0)  { throw new UnsupportedOperationException(); }
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
