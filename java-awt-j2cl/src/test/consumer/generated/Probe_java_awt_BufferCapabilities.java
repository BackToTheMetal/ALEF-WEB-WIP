package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_BufferCapabilities {
    static void call0()  { java.awt.BufferCapabilities value = new java.awt.BufferCapabilities((java.awt.ImageCapabilities) null, (java.awt.ImageCapabilities) null, (java.awt.BufferCapabilities.FlipContents) null); }
    static void call1(java.awt.BufferCapabilities receiver)  { java.awt.ImageCapabilities value = receiver.getFrontBufferCapabilities(); }
    static void call2(java.awt.BufferCapabilities receiver)  { java.awt.ImageCapabilities value = receiver.getBackBufferCapabilities(); }
    static void call3(java.awt.BufferCapabilities receiver)  { boolean value = receiver.isPageFlipping(); }
    static void call4(java.awt.BufferCapabilities receiver)  { java.awt.BufferCapabilities.FlipContents value = receiver.getFlipContents(); }
    static void call5(java.awt.BufferCapabilities receiver)  { boolean value = receiver.isFullScreenRequired(); }
    static void call6(java.awt.BufferCapabilities receiver)  { boolean value = receiver.isMultiBufferAvailable(); }
    static void call7(java.awt.BufferCapabilities receiver)  { java.lang.Object value = receiver.clone(); }
}

abstract class Override_java_awt_BufferCapabilities extends java.awt.BufferCapabilities {
    Override_java_awt_BufferCapabilities()  { super((java.awt.ImageCapabilities) null, (java.awt.ImageCapabilities) null, (java.awt.BufferCapabilities.FlipContents) null); }
    @Override
    public java.awt.ImageCapabilities getFrontBufferCapabilities()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.ImageCapabilities getBackBufferCapabilities()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isPageFlipping()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.BufferCapabilities.FlipContents getFlipContents()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isFullScreenRequired()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isMultiBufferAvailable()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Object clone()  { throw new UnsupportedOperationException(); }
}
