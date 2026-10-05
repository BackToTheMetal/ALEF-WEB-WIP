package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_Cursor {
    static void call0() { int value = java.awt.Cursor.DEFAULT_CURSOR; }
    static void call1() { int value = java.awt.Cursor.CROSSHAIR_CURSOR; }
    static void call2() { int value = java.awt.Cursor.TEXT_CURSOR; }
    static void call3() { int value = java.awt.Cursor.WAIT_CURSOR; }
    static void call4() { int value = java.awt.Cursor.SW_RESIZE_CURSOR; }
    static void call5() { int value = java.awt.Cursor.SE_RESIZE_CURSOR; }
    static void call6() { int value = java.awt.Cursor.NW_RESIZE_CURSOR; }
    static void call7() { int value = java.awt.Cursor.NE_RESIZE_CURSOR; }
    static void call8() { int value = java.awt.Cursor.N_RESIZE_CURSOR; }
    static void call9() { int value = java.awt.Cursor.S_RESIZE_CURSOR; }
    static void call10() { int value = java.awt.Cursor.W_RESIZE_CURSOR; }
    static void call11() { int value = java.awt.Cursor.E_RESIZE_CURSOR; }
    static void call12() { int value = java.awt.Cursor.HAND_CURSOR; }
    static void call13() { int value = java.awt.Cursor.MOVE_CURSOR; }
    static void call14() { java.awt.Cursor[] value = java.awt.Cursor.predefined; java.awt.Cursor.predefined = value; }
    static void call15() { int value = java.awt.Cursor.CUSTOM_CURSOR; }
    static void call16(java.awt.Cursor receiver) { java.lang.String value = receiver.name; receiver.name = value; }
    static void call17()  { java.awt.Cursor value = java.awt.Cursor.getPredefinedCursor((int) 0); }
    static void call18() throws java.awt.AWTException, java.awt.HeadlessException { try { try { java.awt.Cursor value = java.awt.Cursor.getSystemCustomCursor((java.lang.String) null); } catch (java.awt.AWTException expected) { throw expected; } } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call19()  { java.awt.Cursor value = java.awt.Cursor.getDefaultCursor(); }
    static void call20()  { java.awt.Cursor value = new java.awt.Cursor((int) 0); }
    static void call21()  { java.awt.Cursor value = new java.awt.Cursor((java.lang.String) null); }
    static void call22(java.awt.Cursor receiver)  { int value = receiver.getType(); }
    static void call23(java.awt.Cursor receiver)  { java.lang.String value = receiver.getName(); }
    static void call24(java.awt.Cursor receiver)  { java.lang.String value = receiver.toString(); }
}

abstract class Override_java_awt_Cursor extends java.awt.Cursor {
    Override_java_awt_Cursor()  { super((int) 0); }
    @Override
    public int getType()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String getName()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String toString()  { throw new UnsupportedOperationException(); }
}
