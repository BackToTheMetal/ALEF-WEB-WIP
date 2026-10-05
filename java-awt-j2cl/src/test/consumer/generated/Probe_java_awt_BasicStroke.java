package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_BasicStroke {
    static void call0() { int value = java.awt.BasicStroke.JOIN_MITER; }
    static void call1() { int value = java.awt.BasicStroke.JOIN_ROUND; }
    static void call2() { int value = java.awt.BasicStroke.JOIN_BEVEL; }
    static void call3() { int value = java.awt.BasicStroke.CAP_BUTT; }
    static void call4() { int value = java.awt.BasicStroke.CAP_ROUND; }
    static void call5() { int value = java.awt.BasicStroke.CAP_SQUARE; }
    static void call6()  { java.awt.BasicStroke value = new java.awt.BasicStroke((float) 0, (int) 0, (int) 0, (float) 0, (float[]) null, (float) 0); }
    static void call7()  { java.awt.BasicStroke value = new java.awt.BasicStroke((float) 0, (int) 0, (int) 0, (float) 0); }
    static void call8()  { java.awt.BasicStroke value = new java.awt.BasicStroke((float) 0, (int) 0, (int) 0); }
    static void call9()  { java.awt.BasicStroke value = new java.awt.BasicStroke((float) 0); }
    static void call10()  { java.awt.BasicStroke value = new java.awt.BasicStroke(); }
    static void call11(java.awt.BasicStroke receiver)  { java.awt.Shape value = receiver.createStrokedShape((java.awt.Shape) null); }
    static void call12(java.awt.BasicStroke receiver)  { float value = receiver.getLineWidth(); }
    static void call13(java.awt.BasicStroke receiver)  { int value = receiver.getEndCap(); }
    static void call14(java.awt.BasicStroke receiver)  { int value = receiver.getLineJoin(); }
    static void call15(java.awt.BasicStroke receiver)  { float value = receiver.getMiterLimit(); }
    static void call16(java.awt.BasicStroke receiver)  { float[] value = receiver.getDashArray(); }
    static void call17(java.awt.BasicStroke receiver)  { float value = receiver.getDashPhase(); }
    static void call18(java.awt.BasicStroke receiver)  { int value = receiver.hashCode(); }
    static void call19(java.awt.BasicStroke receiver)  { boolean value = receiver.equals((java.lang.Object) null); }
}

abstract class Override_java_awt_BasicStroke extends java.awt.BasicStroke {
    Override_java_awt_BasicStroke()  { super((float) 0, (int) 0, (int) 0, (float) 0, (float[]) null, (float) 0); }
    @Override
    public java.awt.Shape createStrokedShape(java.awt.Shape arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public float getLineWidth()  { throw new UnsupportedOperationException(); }
    @Override
    public int getEndCap()  { throw new UnsupportedOperationException(); }
    @Override
    public int getLineJoin()  { throw new UnsupportedOperationException(); }
    @Override
    public float getMiterLimit()  { throw new UnsupportedOperationException(); }
    @Override
    public float[] getDashArray()  { throw new UnsupportedOperationException(); }
    @Override
    public float getDashPhase()  { throw new UnsupportedOperationException(); }
    @Override
    public int hashCode()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean equals(java.lang.Object arg0)  { throw new UnsupportedOperationException(); }
}
