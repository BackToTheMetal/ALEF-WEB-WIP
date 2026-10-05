package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_MenuShortcut {
    static void call0()  { java.awt.MenuShortcut value = new java.awt.MenuShortcut((int) 0); }
    static void call1()  { java.awt.MenuShortcut value = new java.awt.MenuShortcut((int) 0, false); }
    static void call2(java.awt.MenuShortcut receiver)  { int value = receiver.getKey(); }
    static void call3(java.awt.MenuShortcut receiver)  { boolean value = receiver.usesShiftModifier(); }
    static void call4(java.awt.MenuShortcut receiver)  { boolean value = receiver.equals((java.awt.MenuShortcut) null); }
    static void call5(java.awt.MenuShortcut receiver)  { boolean value = receiver.equals((java.lang.Object) null); }
    static void call6(java.awt.MenuShortcut receiver)  { int value = receiver.hashCode(); }
    static void call7(java.awt.MenuShortcut receiver)  { java.lang.String value = receiver.toString(); }
    static void call8(java.awt.MenuShortcut receiver)  { java.lang.String value = receiver.paramString(); }
}

abstract class Override_java_awt_MenuShortcut extends java.awt.MenuShortcut {
    Override_java_awt_MenuShortcut()  { super((int) 0); }
    @Override
    public int getKey()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean usesShiftModifier()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean equals(java.awt.MenuShortcut arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean equals(java.lang.Object arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int hashCode()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String toString()  { throw new UnsupportedOperationException(); }
    @Override
    protected java.lang.String paramString()  { throw new UnsupportedOperationException(); }
}
