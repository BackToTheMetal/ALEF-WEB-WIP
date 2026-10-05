package java.awt.geom;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_geom_Ellipse2D_Double {
    static void call0(java.awt.geom.Ellipse2D.Double receiver) { double value = receiver.x; receiver.x = value; }
    static void call1(java.awt.geom.Ellipse2D.Double receiver) { double value = receiver.y; receiver.y = value; }
    static void call2(java.awt.geom.Ellipse2D.Double receiver) { double value = receiver.width; receiver.width = value; }
    static void call3(java.awt.geom.Ellipse2D.Double receiver) { double value = receiver.height; receiver.height = value; }
    static void call4()  { java.awt.geom.Ellipse2D.Double value = new java.awt.geom.Ellipse2D.Double(); }
    static void call5()  { java.awt.geom.Ellipse2D.Double value = new java.awt.geom.Ellipse2D.Double((double) 0, (double) 0, (double) 0, (double) 0); }
    static void call6(java.awt.geom.Ellipse2D.Double receiver)  { double value = receiver.getX(); }
    static void call7(java.awt.geom.Ellipse2D.Double receiver)  { double value = receiver.getY(); }
    static void call8(java.awt.geom.Ellipse2D.Double receiver)  { double value = receiver.getWidth(); }
    static void call9(java.awt.geom.Ellipse2D.Double receiver)  { double value = receiver.getHeight(); }
    static void call10(java.awt.geom.Ellipse2D.Double receiver)  { boolean value = receiver.isEmpty(); }
    static void call11(java.awt.geom.Ellipse2D.Double receiver)  { receiver.setFrame((double) 0, (double) 0, (double) 0, (double) 0); }
    static void call12(java.awt.geom.Ellipse2D.Double receiver)  { java.awt.geom.Rectangle2D value = receiver.getBounds2D(); }
}

abstract class Override_java_awt_geom_Ellipse2D_Double extends java.awt.geom.Ellipse2D.Double {
    Override_java_awt_geom_Ellipse2D_Double()  { super(); }
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
    public void setFrame(double arg0, double arg1, double arg2, double arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.Rectangle2D getBounds2D()  { throw new UnsupportedOperationException(); }
}
