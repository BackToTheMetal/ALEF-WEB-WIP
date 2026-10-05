package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_ImageCapabilities {
    static void call0()  { java.awt.ImageCapabilities value = new java.awt.ImageCapabilities(false); }
    static void call1(java.awt.ImageCapabilities receiver)  { boolean value = receiver.isAccelerated(); }
    static void call2(java.awt.ImageCapabilities receiver)  { boolean value = receiver.isTrueVolatile(); }
    static void call3(java.awt.ImageCapabilities receiver)  { java.lang.Object value = receiver.clone(); }
}

abstract class Override_java_awt_ImageCapabilities extends java.awt.ImageCapabilities {
    Override_java_awt_ImageCapabilities()  { super(false); }
    @Override
    public boolean isAccelerated()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isTrueVolatile()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Object clone()  { throw new UnsupportedOperationException(); }
}
