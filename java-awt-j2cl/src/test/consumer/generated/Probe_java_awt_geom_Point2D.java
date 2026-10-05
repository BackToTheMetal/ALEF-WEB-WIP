package java.awt.geom;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_geom_Point2D {
    static void call0(java.awt.geom.Point2D receiver)  { double value = receiver.getX(); }
    static void call1(java.awt.geom.Point2D receiver)  { double value = receiver.getY(); }
    static void call2(java.awt.geom.Point2D receiver)  { receiver.setLocation((double) 0, (double) 0); }
    static void call3(java.awt.geom.Point2D receiver)  { receiver.setLocation((java.awt.geom.Point2D) null); }
    static void call4()  { double value = java.awt.geom.Point2D.distanceSq((double) 0, (double) 0, (double) 0, (double) 0); }
    static void call5()  { double value = java.awt.geom.Point2D.distance((double) 0, (double) 0, (double) 0, (double) 0); }
    static void call6(java.awt.geom.Point2D receiver)  { double value = receiver.distanceSq((double) 0, (double) 0); }
    static void call7(java.awt.geom.Point2D receiver)  { double value = receiver.distanceSq((java.awt.geom.Point2D) null); }
    static void call8(java.awt.geom.Point2D receiver)  { double value = receiver.distance((double) 0, (double) 0); }
    static void call9(java.awt.geom.Point2D receiver)  { double value = receiver.distance((java.awt.geom.Point2D) null); }
    static void call10(java.awt.geom.Point2D receiver)  { java.lang.Object value = receiver.clone(); }
    static void call11(java.awt.geom.Point2D receiver)  { int value = receiver.hashCode(); }
    static void call12(java.awt.geom.Point2D receiver)  { boolean value = receiver.equals((java.lang.Object) null); }
}

abstract class Override_java_awt_geom_Point2D extends java.awt.geom.Point2D {
    Override_java_awt_geom_Point2D()  { super(); }
    @Override
    public double getX()  { throw new UnsupportedOperationException(); }
    @Override
    public double getY()  { throw new UnsupportedOperationException(); }
    @Override
    public void setLocation(double arg0, double arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void setLocation(java.awt.geom.Point2D arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public double distanceSq(double arg0, double arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public double distanceSq(java.awt.geom.Point2D arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public double distance(double arg0, double arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public double distance(java.awt.geom.Point2D arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Object clone()  { throw new UnsupportedOperationException(); }
    @Override
    public int hashCode()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean equals(java.lang.Object arg0)  { throw new UnsupportedOperationException(); }
}
