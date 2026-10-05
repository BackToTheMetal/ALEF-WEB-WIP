package java.awt.geom;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_geom_Line2D_Float {
    static void call0(java.awt.geom.Line2D.Float receiver) { float value = receiver.x1; receiver.x1 = value; }
    static void call1(java.awt.geom.Line2D.Float receiver) { float value = receiver.y1; receiver.y1 = value; }
    static void call2(java.awt.geom.Line2D.Float receiver) { float value = receiver.x2; receiver.x2 = value; }
    static void call3(java.awt.geom.Line2D.Float receiver) { float value = receiver.y2; receiver.y2 = value; }
    static void call4()  { java.awt.geom.Line2D.Float value = new java.awt.geom.Line2D.Float(); }
    static void call5()  { java.awt.geom.Line2D.Float value = new java.awt.geom.Line2D.Float((float) 0, (float) 0, (float) 0, (float) 0); }
    static void call6()  { java.awt.geom.Line2D.Float value = new java.awt.geom.Line2D.Float((java.awt.geom.Point2D) null, (java.awt.geom.Point2D) null); }
    static void call7(java.awt.geom.Line2D.Float receiver)  { double value = receiver.getX1(); }
    static void call8(java.awt.geom.Line2D.Float receiver)  { double value = receiver.getY1(); }
    static void call9(java.awt.geom.Line2D.Float receiver)  { java.awt.geom.Point2D value = receiver.getP1(); }
    static void call10(java.awt.geom.Line2D.Float receiver)  { double value = receiver.getX2(); }
    static void call11(java.awt.geom.Line2D.Float receiver)  { double value = receiver.getY2(); }
    static void call12(java.awt.geom.Line2D.Float receiver)  { java.awt.geom.Point2D value = receiver.getP2(); }
    static void call13(java.awt.geom.Line2D.Float receiver)  { receiver.setLine((double) 0, (double) 0, (double) 0, (double) 0); }
    static void call14(java.awt.geom.Line2D.Float receiver)  { receiver.setLine((float) 0, (float) 0, (float) 0, (float) 0); }
    static void call15(java.awt.geom.Line2D.Float receiver)  { java.awt.geom.Rectangle2D value = receiver.getBounds2D(); }
}

abstract class Override_java_awt_geom_Line2D_Float extends java.awt.geom.Line2D.Float {
    Override_java_awt_geom_Line2D_Float()  { super(); }
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
    public void setLine(float arg0, float arg1, float arg2, float arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.Rectangle2D getBounds2D()  { throw new UnsupportedOperationException(); }
}
