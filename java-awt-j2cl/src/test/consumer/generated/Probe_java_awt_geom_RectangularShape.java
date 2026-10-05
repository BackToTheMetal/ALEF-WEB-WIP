package java.awt.geom;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_geom_RectangularShape {
    static void call0(java.awt.geom.RectangularShape receiver)  { double value = receiver.getX(); }
    static void call1(java.awt.geom.RectangularShape receiver)  { double value = receiver.getY(); }
    static void call2(java.awt.geom.RectangularShape receiver)  { double value = receiver.getWidth(); }
    static void call3(java.awt.geom.RectangularShape receiver)  { double value = receiver.getHeight(); }
    static void call4(java.awt.geom.RectangularShape receiver)  { double value = receiver.getMinX(); }
    static void call5(java.awt.geom.RectangularShape receiver)  { double value = receiver.getMinY(); }
    static void call6(java.awt.geom.RectangularShape receiver)  { double value = receiver.getMaxX(); }
    static void call7(java.awt.geom.RectangularShape receiver)  { double value = receiver.getMaxY(); }
    static void call8(java.awt.geom.RectangularShape receiver)  { double value = receiver.getCenterX(); }
    static void call9(java.awt.geom.RectangularShape receiver)  { double value = receiver.getCenterY(); }
    static void call10(java.awt.geom.RectangularShape receiver)  { java.awt.geom.Rectangle2D value = receiver.getFrame(); }
    static void call11(java.awt.geom.RectangularShape receiver)  { boolean value = receiver.isEmpty(); }
    static void call12(java.awt.geom.RectangularShape receiver)  { receiver.setFrame((double) 0, (double) 0, (double) 0, (double) 0); }
    static void call13(java.awt.geom.RectangularShape receiver)  { receiver.setFrame((java.awt.geom.Point2D) null, (java.awt.geom.Dimension2D) null); }
    static void call14(java.awt.geom.RectangularShape receiver)  { receiver.setFrame((java.awt.geom.Rectangle2D) null); }
    static void call15(java.awt.geom.RectangularShape receiver)  { receiver.setFrameFromDiagonal((double) 0, (double) 0, (double) 0, (double) 0); }
    static void call16(java.awt.geom.RectangularShape receiver)  { receiver.setFrameFromDiagonal((java.awt.geom.Point2D) null, (java.awt.geom.Point2D) null); }
    static void call17(java.awt.geom.RectangularShape receiver)  { receiver.setFrameFromCenter((double) 0, (double) 0, (double) 0, (double) 0); }
    static void call18(java.awt.geom.RectangularShape receiver)  { receiver.setFrameFromCenter((java.awt.geom.Point2D) null, (java.awt.geom.Point2D) null); }
    static void call19(java.awt.geom.RectangularShape receiver)  { boolean value = receiver.contains((java.awt.geom.Point2D) null); }
    static void call20(java.awt.geom.RectangularShape receiver)  { boolean value = receiver.intersects((java.awt.geom.Rectangle2D) null); }
    static void call21(java.awt.geom.RectangularShape receiver)  { boolean value = receiver.contains((java.awt.geom.Rectangle2D) null); }
    static void call22(java.awt.geom.RectangularShape receiver)  { java.awt.Rectangle value = receiver.getBounds(); }
    static void call23(java.awt.geom.RectangularShape receiver)  { java.awt.geom.PathIterator value = receiver.getPathIterator((java.awt.geom.AffineTransform) null, (double) 0); }
    static void call24(java.awt.geom.RectangularShape receiver)  { java.lang.Object value = receiver.clone(); }
}

abstract class Override_java_awt_geom_RectangularShape extends java.awt.geom.RectangularShape {
    Override_java_awt_geom_RectangularShape()  { super(); }
    @Override
    public double getX()  { throw new UnsupportedOperationException(); }
    @Override
    public double getY()  { throw new UnsupportedOperationException(); }
    @Override
    public double getWidth()  { throw new UnsupportedOperationException(); }
    @Override
    public double getHeight()  { throw new UnsupportedOperationException(); }
    @Override
    public double getMinX()  { throw new UnsupportedOperationException(); }
    @Override
    public double getMinY()  { throw new UnsupportedOperationException(); }
    @Override
    public double getMaxX()  { throw new UnsupportedOperationException(); }
    @Override
    public double getMaxY()  { throw new UnsupportedOperationException(); }
    @Override
    public double getCenterX()  { throw new UnsupportedOperationException(); }
    @Override
    public double getCenterY()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.Rectangle2D getFrame()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isEmpty()  { throw new UnsupportedOperationException(); }
    @Override
    public void setFrame(double arg0, double arg1, double arg2, double arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public void setFrame(java.awt.geom.Point2D arg0, java.awt.geom.Dimension2D arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void setFrame(java.awt.geom.Rectangle2D arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setFrameFromDiagonal(double arg0, double arg1, double arg2, double arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public void setFrameFromDiagonal(java.awt.geom.Point2D arg0, java.awt.geom.Point2D arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void setFrameFromCenter(double arg0, double arg1, double arg2, double arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public void setFrameFromCenter(java.awt.geom.Point2D arg0, java.awt.geom.Point2D arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean contains(java.awt.geom.Point2D arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean intersects(java.awt.geom.Rectangle2D arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean contains(java.awt.geom.Rectangle2D arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Rectangle getBounds()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.PathIterator getPathIterator(java.awt.geom.AffineTransform arg0, double arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Object clone()  { throw new UnsupportedOperationException(); }
}
