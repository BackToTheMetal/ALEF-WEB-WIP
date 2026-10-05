package java.awt.image;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_image_VolatileImage {
    static void call0() { int value = java.awt.image.VolatileImage.IMAGE_OK; }
    static void call1() { int value = java.awt.image.VolatileImage.IMAGE_RESTORED; }
    static void call2() { int value = java.awt.image.VolatileImage.IMAGE_INCOMPATIBLE; }
    static void call3(java.awt.image.VolatileImage receiver) { int value = receiver.transparency; receiver.transparency = value; }
    static void call4(java.awt.image.VolatileImage receiver)  { java.awt.image.BufferedImage value = receiver.getSnapshot(); }
    static void call5(java.awt.image.VolatileImage receiver)  { int value = receiver.getWidth(); }
    static void call6(java.awt.image.VolatileImage receiver)  { int value = receiver.getHeight(); }
    static void call7(java.awt.image.VolatileImage receiver)  { java.awt.image.ImageProducer value = receiver.getSource(); }
    static void call8(java.awt.image.VolatileImage receiver)  { java.awt.Graphics value = receiver.getGraphics(); }
    static void call9(java.awt.image.VolatileImage receiver)  { java.awt.Graphics2D value = receiver.createGraphics(); }
    static void call10(java.awt.image.VolatileImage receiver)  { int value = receiver.validate((java.awt.GraphicsConfiguration) null); }
    static void call11(java.awt.image.VolatileImage receiver)  { boolean value = receiver.contentsLost(); }
    static void call12(java.awt.image.VolatileImage receiver)  { java.awt.ImageCapabilities value = receiver.getCapabilities(); }
    static void call13(java.awt.image.VolatileImage receiver)  { int value = receiver.getTransparency(); }
}

abstract class Override_java_awt_image_VolatileImage extends java.awt.image.VolatileImage {
    Override_java_awt_image_VolatileImage()  { super(); }
    @Override
    public java.awt.image.BufferedImage getSnapshot()  { throw new UnsupportedOperationException(); }
    @Override
    public int getWidth()  { throw new UnsupportedOperationException(); }
    @Override
    public int getHeight()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.ImageProducer getSource()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Graphics getGraphics()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Graphics2D createGraphics()  { throw new UnsupportedOperationException(); }
    @Override
    public int validate(java.awt.GraphicsConfiguration arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean contentsLost()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.ImageCapabilities getCapabilities()  { throw new UnsupportedOperationException(); }
    @Override
    public int getTransparency()  { throw new UnsupportedOperationException(); }
}
