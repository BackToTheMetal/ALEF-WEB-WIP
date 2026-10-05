package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_Point {
    static void call0(java.awt.Point receiver) { int value = receiver.x; receiver.x = value; }
    static void call1(java.awt.Point receiver) { int value = receiver.y; receiver.y = value; }
    static void call2()  { java.awt.Point value = new java.awt.Point(); }
    static void call3()  { java.awt.Point value = new java.awt.Point((java.awt.Point) null); }
    static void call4()  { java.awt.Point value = new java.awt.Point((int) 0, (int) 0); }
    static void call5(java.awt.Point receiver)  { double value = receiver.getX(); }
    static void call6(java.awt.Point receiver)  { double value = receiver.getY(); }
    static void call7(java.awt.Point receiver)  { java.awt.Point value = receiver.getLocation(); }
    static void call8(java.awt.Point receiver)  { receiver.setLocation((java.awt.Point) null); }
    static void call9(java.awt.Point receiver)  { receiver.setLocation((int) 0, (int) 0); }
    static void call10(java.awt.Point receiver)  { receiver.setLocation((double) 0, (double) 0); }
    static void call11(java.awt.Point receiver)  { receiver.move((int) 0, (int) 0); }
    static void call12(java.awt.Point receiver)  { receiver.translate((int) 0, (int) 0); }
    static void call13(java.awt.Point receiver)  { boolean value = receiver.equals((java.lang.Object) null); }
    static void call14(java.awt.Point receiver)  { java.lang.String value = receiver.toString(); }
}

abstract class Override_java_awt_Point extends java.awt.Point {
    Override_java_awt_Point()  { super(); }
    @Override
    public double getX()  { throw new UnsupportedOperationException(); }
    @Override
    public double getY()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Point getLocation()  { throw new UnsupportedOperationException(); }
    @Override
    public void setLocation(java.awt.Point arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setLocation(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void setLocation(double arg0, double arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void move(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void translate(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean equals(java.lang.Object arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String toString()  { throw new UnsupportedOperationException(); }
}
