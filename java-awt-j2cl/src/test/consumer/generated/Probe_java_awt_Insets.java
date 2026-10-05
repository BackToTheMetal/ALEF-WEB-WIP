package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_Insets {
    static void call0(java.awt.Insets receiver) { int value = receiver.top; receiver.top = value; }
    static void call1(java.awt.Insets receiver) { int value = receiver.left; receiver.left = value; }
    static void call2(java.awt.Insets receiver) { int value = receiver.bottom; receiver.bottom = value; }
    static void call3(java.awt.Insets receiver) { int value = receiver.right; receiver.right = value; }
    static void call4()  { java.awt.Insets value = new java.awt.Insets((int) 0, (int) 0, (int) 0, (int) 0); }
    static void call5(java.awt.Insets receiver)  { receiver.set((int) 0, (int) 0, (int) 0, (int) 0); }
    static void call6(java.awt.Insets receiver)  { boolean value = receiver.equals((java.lang.Object) null); }
    static void call7(java.awt.Insets receiver)  { int value = receiver.hashCode(); }
    static void call8(java.awt.Insets receiver)  { java.lang.String value = receiver.toString(); }
    static void call9(java.awt.Insets receiver)  { java.lang.Object value = receiver.clone(); }
}

abstract class Override_java_awt_Insets extends java.awt.Insets {
    Override_java_awt_Insets()  { super((int) 0, (int) 0, (int) 0, (int) 0); }
    @Override
    public void set(int arg0, int arg1, int arg2, int arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean equals(java.lang.Object arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int hashCode()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String toString()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Object clone()  { throw new UnsupportedOperationException(); }
}
