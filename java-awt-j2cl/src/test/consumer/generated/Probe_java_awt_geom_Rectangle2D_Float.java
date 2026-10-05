package java.awt.geom;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_geom_Rectangle2D_Float {
    static void call0(java.awt.geom.Rectangle2D.Float receiver) { float value = receiver.x; receiver.x = value; }
    static void call1(java.awt.geom.Rectangle2D.Float receiver) { float value = receiver.y; receiver.y = value; }
    static void call2(java.awt.geom.Rectangle2D.Float receiver) { float value = receiver.width; receiver.width = value; }
    static void call3(java.awt.geom.Rectangle2D.Float receiver) { float value = receiver.height; receiver.height = value; }
    static void call4()  { java.awt.geom.Rectangle2D.Float value = new java.awt.geom.Rectangle2D.Float(); }
    static void call5()  { java.awt.geom.Rectangle2D.Float value = new java.awt.geom.Rectangle2D.Float((float) 0, (float) 0, (float) 0, (float) 0); }
    static void call6(java.awt.geom.Rectangle2D.Float receiver)  { double value = receiver.getX(); }
    static void call7(java.awt.geom.Rectangle2D.Float receiver)  { double value = receiver.getY(); }
    static void call8(java.awt.geom.Rectangle2D.Float receiver)  { double value = receiver.getWidth(); }
    static void call9(java.awt.geom.Rectangle2D.Float receiver)  { double value = receiver.getHeight(); }
    static void call10(java.awt.geom.Rectangle2D.Float receiver)  { boolean value = receiver.isEmpty(); }
    static void call11(java.awt.geom.Rectangle2D.Float receiver)  { receiver.setRect((float) 0, (float) 0, (float) 0, (float) 0); }
    static void call12(java.awt.geom.Rectangle2D.Float receiver)  { receiver.setRect((double) 0, (double) 0, (double) 0, (double) 0); }
    static void call13(java.awt.geom.Rectangle2D.Float receiver)  { receiver.setRect((java.awt.geom.Rectangle2D) null); }
    static void call14(java.awt.geom.Rectangle2D.Float receiver)  { int value = receiver.outcode((double) 0, (double) 0); }
    static void call15(java.awt.geom.Rectangle2D.Float receiver)  { java.awt.geom.Rectangle2D value = receiver.getBounds2D(); }
    static void call16(java.awt.geom.Rectangle2D.Float receiver)  { java.awt.geom.Rectangle2D value = receiver.createIntersection((java.awt.geom.Rectangle2D) null); }
    static void call17(java.awt.geom.Rectangle2D.Float receiver)  { java.awt.geom.Rectangle2D value = receiver.createUnion((java.awt.geom.Rectangle2D) null); }
    static void call18(java.awt.geom.Rectangle2D.Float receiver)  { java.lang.String value = receiver.toString(); }
}

abstract class Override_java_awt_geom_Rectangle2D_Float extends java.awt.geom.Rectangle2D.Float {
    Override_java_awt_geom_Rectangle2D_Float()  { super(); }
    @Override
    public double getX()  { throw new UnsupportedOperationException(); }
    @Override
    public double getY()  { throw new UnsupportedOperationException(); }
    @Override
    public double getWidth()  { throw new UnsupportedOperationException(); }
    @Override
    public double getHeight()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isEmpty()  { throw new UnsupportedOperationException(); }
    @Override
    public void setRect(float arg0, float arg1, float arg2, float arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public void setRect(double arg0, double arg1, double arg2, double arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public void setRect(java.awt.geom.Rectangle2D arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int outcode(double arg0, double arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.Rectangle2D getBounds2D()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.Rectangle2D createIntersection(java.awt.geom.Rectangle2D arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.Rectangle2D createUnion(java.awt.geom.Rectangle2D arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String toString()  { throw new UnsupportedOperationException(); }
}
