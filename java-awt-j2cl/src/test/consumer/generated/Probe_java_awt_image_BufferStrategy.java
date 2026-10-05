package java.awt.image;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_image_BufferStrategy {
    static void call0(java.awt.image.BufferStrategy receiver)  { java.awt.BufferCapabilities value = receiver.getCapabilities(); }
    static void call1(java.awt.image.BufferStrategy receiver)  { java.awt.Graphics value = receiver.getDrawGraphics(); }
    static void call2(java.awt.image.BufferStrategy receiver)  { boolean value = receiver.contentsLost(); }
    static void call3(java.awt.image.BufferStrategy receiver)  { boolean value = receiver.contentsRestored(); }
    static void call4(java.awt.image.BufferStrategy receiver)  { receiver.show(); }
    static void call5(java.awt.image.BufferStrategy receiver)  { receiver.dispose(); }
}

abstract class Override_java_awt_image_BufferStrategy extends java.awt.image.BufferStrategy {
    Override_java_awt_image_BufferStrategy()  { super(); }
    @Override
    public java.awt.BufferCapabilities getCapabilities()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Graphics getDrawGraphics()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean contentsLost()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean contentsRestored()  { throw new UnsupportedOperationException(); }
    @Override
    public void show()  { throw new UnsupportedOperationException(); }
    @Override
    public void dispose()  { throw new UnsupportedOperationException(); }
}
