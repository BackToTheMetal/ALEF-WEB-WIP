package java.awt.image;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_image_FilteredImageSource {
    static void call0()  { java.awt.image.FilteredImageSource value = new java.awt.image.FilteredImageSource((java.awt.image.ImageProducer) null, (java.awt.image.ImageFilter) null); }
    static void call1(java.awt.image.FilteredImageSource receiver)  { receiver.addConsumer((java.awt.image.ImageConsumer) null); }
    static void call2(java.awt.image.FilteredImageSource receiver)  { boolean value = receiver.isConsumer((java.awt.image.ImageConsumer) null); }
    static void call3(java.awt.image.FilteredImageSource receiver)  { receiver.removeConsumer((java.awt.image.ImageConsumer) null); }
    static void call4(java.awt.image.FilteredImageSource receiver)  { receiver.startProduction((java.awt.image.ImageConsumer) null); }
    static void call5(java.awt.image.FilteredImageSource receiver)  { receiver.requestTopDownLeftRightResend((java.awt.image.ImageConsumer) null); }
}

abstract class Override_java_awt_image_FilteredImageSource extends java.awt.image.FilteredImageSource {
    Override_java_awt_image_FilteredImageSource()  { super((java.awt.image.ImageProducer) null, (java.awt.image.ImageFilter) null); }
    @Override
    public void addConsumer(java.awt.image.ImageConsumer arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isConsumer(java.awt.image.ImageConsumer arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void removeConsumer(java.awt.image.ImageConsumer arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void startProduction(java.awt.image.ImageConsumer arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void requestTopDownLeftRightResend(java.awt.image.ImageConsumer arg0)  { throw new UnsupportedOperationException(); }
}
