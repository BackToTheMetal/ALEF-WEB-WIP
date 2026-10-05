package java.awt.geom;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_geom_Point2D_Float {
    static void call0(java.awt.geom.Point2D.Float receiver) { float value = receiver.x; receiver.x = value; }
    static void call1(java.awt.geom.Point2D.Float receiver) { float value = receiver.y; receiver.y = value; }
    static void call2()  { java.awt.geom.Point2D.Float value = new java.awt.geom.Point2D.Float(); }
    static void call3()  { java.awt.geom.Point2D.Float value = new java.awt.geom.Point2D.Float((float) 0, (float) 0); }
    static void call4(java.awt.geom.Point2D.Float receiver)  { double value = receiver.getX(); }
    static void call5(java.awt.geom.Point2D.Float receiver)  { double value = receiver.getY(); }
    static void call6(java.awt.geom.Point2D.Float receiver)  { receiver.setLocation((double) 0, (double) 0); }
    static void call7(java.awt.geom.Point2D.Float receiver)  { receiver.setLocation((float) 0, (float) 0); }
    static void call8(java.awt.geom.Point2D.Float receiver)  { java.lang.String value = receiver.toString(); }
}

abstract class Override_java_awt_geom_Point2D_Float extends java.awt.geom.Point2D.Float {
    Override_java_awt_geom_Point2D_Float()  { super(); }
    @Override
    public double getX()  { throw new UnsupportedOperationException(); }
    @Override
    public double getY()  { throw new UnsupportedOperationException(); }
    @Override
    public void setLocation(double arg0, double arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void setLocation(float arg0, float arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String toString()  { throw new UnsupportedOperationException(); }
}
