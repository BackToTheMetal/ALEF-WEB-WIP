package java.awt.image;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_image_AbstractMultiResolutionImage {
    static void call0(java.awt.image.AbstractMultiResolutionImage receiver)  { int value = receiver.getWidth((java.awt.image.ImageObserver) null); }
    static void call1(java.awt.image.AbstractMultiResolutionImage receiver)  { int value = receiver.getHeight((java.awt.image.ImageObserver) null); }
    static void call2(java.awt.image.AbstractMultiResolutionImage receiver)  { java.awt.image.ImageProducer value = receiver.getSource(); }
    static void call3(java.awt.image.AbstractMultiResolutionImage receiver)  { java.awt.Graphics value = receiver.getGraphics(); }
    static void call4(java.awt.image.AbstractMultiResolutionImage receiver)  { java.lang.Object value = receiver.getProperty((java.lang.String) null, (java.awt.image.ImageObserver) null); }
    static void call5(java.awt.image.AbstractMultiResolutionImage receiver)  { java.awt.Image value = receiver.getBaseImage(); }
}

abstract class Override_java_awt_image_AbstractMultiResolutionImage extends java.awt.image.AbstractMultiResolutionImage {
    Override_java_awt_image_AbstractMultiResolutionImage()  { super(); }
    @Override
    public int getWidth(java.awt.image.ImageObserver arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int getHeight(java.awt.image.ImageObserver arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.ImageProducer getSource()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Graphics getGraphics()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Object getProperty(java.lang.String arg0, java.awt.image.ImageObserver arg1)  { throw new UnsupportedOperationException(); }
    @Override
    protected java.awt.Image getBaseImage()  { throw new UnsupportedOperationException(); }
}
