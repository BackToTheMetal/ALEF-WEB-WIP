package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_AWTKeyStroke {
    static void call0()  { java.awt.AWTKeyStroke value = new java.awt.AWTKeyStroke(); }
    static void call1()  { java.awt.AWTKeyStroke value = new java.awt.AWTKeyStroke((char) 0, (int) 0, (int) 0, false); }
    static void call2()  { java.awt.AWTKeyStroke.registerSubclass((java.lang.Class) null); }
    static void call3()  { java.awt.AWTKeyStroke value = java.awt.AWTKeyStroke.getAWTKeyStroke((char) 0); }
    static void call4()  { java.awt.AWTKeyStroke value = java.awt.AWTKeyStroke.getAWTKeyStroke((java.lang.Character) null, (int) 0); }
    static void call5()  { java.awt.AWTKeyStroke value = java.awt.AWTKeyStroke.getAWTKeyStroke((int) 0, (int) 0, false); }
    static void call6()  { java.awt.AWTKeyStroke value = java.awt.AWTKeyStroke.getAWTKeyStroke((int) 0, (int) 0); }
    static void call7()  { java.awt.AWTKeyStroke value = java.awt.AWTKeyStroke.getAWTKeyStrokeForEvent((java.awt.event.KeyEvent) null); }
    static void call8()  { java.awt.AWTKeyStroke value = java.awt.AWTKeyStroke.getAWTKeyStroke((java.lang.String) null); }
    static void call9(java.awt.AWTKeyStroke receiver)  { char value = receiver.getKeyChar(); }
    static void call10(java.awt.AWTKeyStroke receiver)  { int value = receiver.getKeyCode(); }
    static void call11(java.awt.AWTKeyStroke receiver)  { int value = receiver.getModifiers(); }
    static void call12(java.awt.AWTKeyStroke receiver)  { boolean value = receiver.isOnKeyRelease(); }
    static void call13(java.awt.AWTKeyStroke receiver)  { int value = receiver.getKeyEventType(); }
    static void call14(java.awt.AWTKeyStroke receiver)  { int value = receiver.hashCode(); }
    static void call15(java.awt.AWTKeyStroke receiver)  { boolean value = receiver.equals((java.lang.Object) null); }
    static void call16(java.awt.AWTKeyStroke receiver)  { java.lang.String value = receiver.toString(); }
}

abstract class Override_java_awt_AWTKeyStroke extends java.awt.AWTKeyStroke {
    Override_java_awt_AWTKeyStroke()  { super(); }
    @Override
    public int hashCode()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String toString()  { throw new UnsupportedOperationException(); }
}
