package java.awt.geom;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_geom_Arc2D {
    static void call0() { int value = java.awt.geom.Arc2D.OPEN; }
    static void call1() { int value = java.awt.geom.Arc2D.CHORD; }
    static void call2() { int value = java.awt.geom.Arc2D.PIE; }
    static void call3(java.awt.geom.Arc2D receiver)  { double value = receiver.getAngleStart(); }
    static void call4(java.awt.geom.Arc2D receiver)  { double value = receiver.getAngleExtent(); }
    static void call5(java.awt.geom.Arc2D receiver)  { int value = receiver.getArcType(); }
    static void call6(java.awt.geom.Arc2D receiver)  { java.awt.geom.Point2D value = receiver.getStartPoint(); }
    static void call7(java.awt.geom.Arc2D receiver)  { java.awt.geom.Point2D value = receiver.getEndPoint(); }
    static void call8(java.awt.geom.Arc2D receiver)  { receiver.setArc((double) 0, (double) 0, (double) 0, (double) 0, (double) 0, (double) 0, (int) 0); }
    static void call9(java.awt.geom.Arc2D receiver)  { receiver.setArc((java.awt.geom.Point2D) null, (java.awt.geom.Dimension2D) null, (double) 0, (double) 0, (int) 0); }
    static void call10(java.awt.geom.Arc2D receiver)  { receiver.setArc((java.awt.geom.Rectangle2D) null, (double) 0, (double) 0, (int) 0); }
    static void call11(java.awt.geom.Arc2D receiver)  { receiver.setArc((java.awt.geom.Arc2D) null); }
    static void call12(java.awt.geom.Arc2D receiver)  { receiver.setArcByCenter((double) 0, (double) 0, (double) 0, (double) 0, (double) 0, (int) 0); }
    static void call13(java.awt.geom.Arc2D receiver)  { receiver.setArcByTangent((java.awt.geom.Point2D) null, (java.awt.geom.Point2D) null, (java.awt.geom.Point2D) null, (double) 0); }
    static void call14(java.awt.geom.Arc2D receiver)  { receiver.setAngleStart((double) 0); }
    static void call15(java.awt.geom.Arc2D receiver)  { receiver.setAngleExtent((double) 0); }
    static void call16(java.awt.geom.Arc2D receiver)  { receiver.setAngleStart((java.awt.geom.Point2D) null); }
    static void call17(java.awt.geom.Arc2D receiver)  { receiver.setAngles((double) 0, (double) 0, (double) 0, (double) 0); }
    static void call18(java.awt.geom.Arc2D receiver)  { receiver.setAngles((java.awt.geom.Point2D) null, (java.awt.geom.Point2D) null); }
    static void call19(java.awt.geom.Arc2D receiver)  { receiver.setArcType((int) 0); }
    static void call20(java.awt.geom.Arc2D receiver)  { receiver.setFrame((double) 0, (double) 0, (double) 0, (double) 0); }
    static void call21(java.awt.geom.Arc2D receiver)  { java.awt.geom.Rectangle2D value = receiver.getBounds2D(); }
    static void call22(java.awt.geom.Arc2D receiver)  { java.awt.geom.Rectangle2D value = receiver.makeBounds((double) 0, (double) 0, (double) 0, (double) 0); }
    static void call23(java.awt.geom.Arc2D receiver)  { boolean value = receiver.containsAngle((double) 0); }
    static void call24(java.awt.geom.Arc2D receiver)  { boolean value = receiver.contains((double) 0, (double) 0); }
    static void call25(java.awt.geom.Arc2D receiver)  { boolean value = receiver.intersects((double) 0, (double) 0, (double) 0, (double) 0); }
    static void call26(java.awt.geom.Arc2D receiver)  { boolean value = receiver.contains((double) 0, (double) 0, (double) 0, (double) 0); }
    static void call27(java.awt.geom.Arc2D receiver)  { boolean value = receiver.contains((java.awt.geom.Rectangle2D) null); }
    static void call28(java.awt.geom.Arc2D receiver)  { java.awt.geom.PathIterator value = receiver.getPathIterator((java.awt.geom.AffineTransform) null); }
    static void call29(java.awt.geom.Arc2D receiver)  { int value = receiver.hashCode(); }
    static void call30(java.awt.geom.Arc2D receiver)  { boolean value = receiver.equals((java.lang.Object) null); }
}

abstract class Override_java_awt_geom_Arc2D extends java.awt.geom.Arc2D {
    Override_java_awt_geom_Arc2D()  { super(); }
    @Override
    public double getAngleStart()  { throw new UnsupportedOperationException(); }
    @Override
    public double getAngleExtent()  { throw new UnsupportedOperationException(); }
    @Override
    public int getArcType()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.Point2D getStartPoint()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.Point2D getEndPoint()  { throw new UnsupportedOperationException(); }
    @Override
    public void setArc(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, int arg6)  { throw new UnsupportedOperationException(); }
    @Override
    public void setArc(java.awt.geom.Point2D arg0, java.awt.geom.Dimension2D arg1, double arg2, double arg3, int arg4)  { throw new UnsupportedOperationException(); }
    @Override
    public void setArc(java.awt.geom.Rectangle2D arg0, double arg1, double arg2, int arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public void setArc(java.awt.geom.Arc2D arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setArcByCenter(double arg0, double arg1, double arg2, double arg3, double arg4, int arg5)  { throw new UnsupportedOperationException(); }
    @Override
    public void setArcByTangent(java.awt.geom.Point2D arg0, java.awt.geom.Point2D arg1, java.awt.geom.Point2D arg2, double arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public void setAngleStart(double arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setAngleExtent(double arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setAngleStart(java.awt.geom.Point2D arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setAngles(double arg0, double arg1, double arg2, double arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public void setAngles(java.awt.geom.Point2D arg0, java.awt.geom.Point2D arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void setArcType(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setFrame(double arg0, double arg1, double arg2, double arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.Rectangle2D getBounds2D()  { throw new UnsupportedOperationException(); }
    @Override
    protected java.awt.geom.Rectangle2D makeBounds(double arg0, double arg1, double arg2, double arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean containsAngle(double arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean contains(double arg0, double arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean intersects(double arg0, double arg1, double arg2, double arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean contains(double arg0, double arg1, double arg2, double arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean contains(java.awt.geom.Rectangle2D arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.PathIterator getPathIterator(java.awt.geom.AffineTransform arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int hashCode()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean equals(java.lang.Object arg0)  { throw new UnsupportedOperationException(); }
}
