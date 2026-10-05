package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_Dimension {
    static void call0(java.awt.Dimension receiver) { int value = receiver.width; receiver.width = value; }
    static void call1(java.awt.Dimension receiver) { int value = receiver.height; receiver.height = value; }
    static void call2()  { java.awt.Dimension value = new java.awt.Dimension(); }
    static void call3()  { java.awt.Dimension value = new java.awt.Dimension((java.awt.Dimension) null); }
    static void call4()  { java.awt.Dimension value = new java.awt.Dimension((int) 0, (int) 0); }
    static void call5(java.awt.Dimension receiver)  { double value = receiver.getWidth(); }
    static void call6(java.awt.Dimension receiver)  { double value = receiver.getHeight(); }
    static void call7(java.awt.Dimension receiver)  { receiver.setSize((double) 0, (double) 0); }
    static void call8(java.awt.Dimension receiver)  { java.awt.Dimension value = receiver.getSize(); }
    static void call9(java.awt.Dimension receiver)  { receiver.setSize((java.awt.Dimension) null); }
    static void call10(java.awt.Dimension receiver)  { receiver.setSize((int) 0, (int) 0); }
    static void call11(java.awt.Dimension receiver)  { boolean value = receiver.equals((java.lang.Object) null); }
    static void call12(java.awt.Dimension receiver)  { int value = receiver.hashCode(); }
    static void call13(java.awt.Dimension receiver)  { java.lang.String value = receiver.toString(); }
}

abstract class Override_java_awt_Dimension extends java.awt.Dimension {
    Override_java_awt_Dimension()  { super(); }
    @Override
    public double getWidth()  { throw new UnsupportedOperationException(); }
    @Override
    public double getHeight()  { throw new UnsupportedOperationException(); }
    @Override
    public void setSize(double arg0, double arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Dimension getSize()  { throw new UnsupportedOperationException(); }
    @Override
    public void setSize(java.awt.Dimension arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setSize(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean equals(java.lang.Object arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int hashCode()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String toString()  { throw new UnsupportedOperationException(); }
}
