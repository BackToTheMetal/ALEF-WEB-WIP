package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_GraphicsConfigTemplate {
    static void call0() { int value = java.awt.GraphicsConfigTemplate.REQUIRED; }
    static void call1() { int value = java.awt.GraphicsConfigTemplate.PREFERRED; }
    static void call2() { int value = java.awt.GraphicsConfigTemplate.UNNECESSARY; }
    static void call3(java.awt.GraphicsConfigTemplate receiver)  { java.awt.GraphicsConfiguration value = receiver.getBestConfiguration((java.awt.GraphicsConfiguration[]) null); }
    static void call4(java.awt.GraphicsConfigTemplate receiver)  { boolean value = receiver.isGraphicsConfigSupported((java.awt.GraphicsConfiguration) null); }
}

abstract class Override_java_awt_GraphicsConfigTemplate extends java.awt.GraphicsConfigTemplate {
    Override_java_awt_GraphicsConfigTemplate()  { super(); }
    @Override
    public java.awt.GraphicsConfiguration getBestConfiguration(java.awt.GraphicsConfiguration[] arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isGraphicsConfigSupported(java.awt.GraphicsConfiguration arg0)  { throw new UnsupportedOperationException(); }
}
