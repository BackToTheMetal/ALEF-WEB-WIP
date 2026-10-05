package java.awt.geom;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_geom_Arc2D_Double {
    static void call0(java.awt.geom.Arc2D.Double receiver) { double value = receiver.x; receiver.x = value; }
    static void call1(java.awt.geom.Arc2D.Double receiver) { double value = receiver.y; receiver.y = value; }
    static void call2(java.awt.geom.Arc2D.Double receiver) { double value = receiver.width; receiver.width = value; }
    static void call3(java.awt.geom.Arc2D.Double receiver) { double value = receiver.height; receiver.height = value; }
    static void call4(java.awt.geom.Arc2D.Double receiver) { double value = receiver.start; receiver.start = value; }
    static void call5(java.awt.geom.Arc2D.Double receiver) { double value = receiver.extent; receiver.extent = value; }
    static void call6()  { java.awt.geom.Arc2D.Double value = new java.awt.geom.Arc2D.Double(); }
    static void call7()  { java.awt.geom.Arc2D.Double value = new java.awt.geom.Arc2D.Double((int) 0); }
    static void call8()  { java.awt.geom.Arc2D.Double value = new java.awt.geom.Arc2D.Double((double) 0, (double) 0, (double) 0, (double) 0, (double) 0, (double) 0, (int) 0); }
    static void call9()  { java.awt.geom.Arc2D.Double value = new java.awt.geom.Arc2D.Double((java.awt.geom.Rectangle2D) null, (double) 0, (double) 0, (int) 0); }
    static void call10(java.awt.geom.Arc2D.Double receiver)  { double value = receiver.getX(); }
    static void call11(java.awt.geom.Arc2D.Double receiver)  { double value = receiver.getY(); }
    static void call12(java.awt.geom.Arc2D.Double receiver)  { double value = receiver.getWidth(); }
    static void call13(java.awt.geom.Arc2D.Double receiver)  { double value = receiver.getHeight(); }
    static void call14(java.awt.geom.Arc2D.Double receiver)  { double value = receiver.getAngleStart(); }
    static void call15(java.awt.geom.Arc2D.Double receiver)  { double value = receiver.getAngleExtent(); }
    static void call16(java.awt.geom.Arc2D.Double receiver)  { boolean value = receiver.isEmpty(); }
    static void call17(java.awt.geom.Arc2D.Double receiver)  { receiver.setArc((double) 0, (double) 0, (double) 0, (double) 0, (double) 0, (double) 0, (int) 0); }
    static void call18(java.awt.geom.Arc2D.Double receiver)  { receiver.setAngleStart((double) 0); }
    static void call19(java.awt.geom.Arc2D.Double receiver)  { receiver.setAngleExtent((double) 0); }
    static void call20(java.awt.geom.Arc2D.Double receiver)  { java.awt.geom.Rectangle2D value = receiver.makeBounds((double) 0, (double) 0, (double) 0, (double) 0); }
}

abstract class Override_java_awt_geom_Arc2D_Double extends java.awt.geom.Arc2D.Double {
    Override_java_awt_geom_Arc2D_Double()  { super(); }
    @Override
    public double getX()  { throw new UnsupportedOperationException(); }
    @Override
    public double getY()  { throw new UnsupportedOperationException(); }
    @Override
    public double getWidth()  { throw new UnsupportedOperationException(); }
    @Override
    public double getHeight()  { throw new UnsupportedOperationException(); }
    @Override
    public double getAngleStart()  { throw new UnsupportedOperationException(); }
    @Override
    public double getAngleExtent()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isEmpty()  { throw new UnsupportedOperationException(); }
    @Override
    public void setArc(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5, int arg6)  { throw new UnsupportedOperationException(); }
    @Override
    public void setAngleStart(double arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setAngleExtent(double arg0)  { throw new UnsupportedOperationException(); }
    @Override
    protected java.awt.geom.Rectangle2D makeBounds(double arg0, double arg1, double arg2, double arg3)  { throw new UnsupportedOperationException(); }
}
