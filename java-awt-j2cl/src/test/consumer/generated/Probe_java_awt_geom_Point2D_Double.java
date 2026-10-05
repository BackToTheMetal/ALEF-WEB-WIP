package java.awt.geom;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_geom_Point2D_Double {
    static void call0(java.awt.geom.Point2D.Double receiver) { double value = receiver.x; receiver.x = value; }
    static void call1(java.awt.geom.Point2D.Double receiver) { double value = receiver.y; receiver.y = value; }
    static void call2()  { java.awt.geom.Point2D.Double value = new java.awt.geom.Point2D.Double(); }
    static void call3()  { java.awt.geom.Point2D.Double value = new java.awt.geom.Point2D.Double((double) 0, (double) 0); }
    static void call4(java.awt.geom.Point2D.Double receiver)  { double value = receiver.getX(); }
    static void call5(java.awt.geom.Point2D.Double receiver)  { double value = receiver.getY(); }
    static void call6(java.awt.geom.Point2D.Double receiver)  { receiver.setLocation((double) 0, (double) 0); }
    static void call7(java.awt.geom.Point2D.Double receiver)  { java.lang.String value = receiver.toString(); }
}

abstract class Override_java_awt_geom_Point2D_Double extends java.awt.geom.Point2D.Double {
    Override_java_awt_geom_Point2D_Double()  { super(); }
    @Override
    public double getX()  { throw new UnsupportedOperationException(); }
    @Override
    public double getY()  { throw new UnsupportedOperationException(); }
    @Override
    public void setLocation(double arg0, double arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String toString()  { throw new UnsupportedOperationException(); }
}
