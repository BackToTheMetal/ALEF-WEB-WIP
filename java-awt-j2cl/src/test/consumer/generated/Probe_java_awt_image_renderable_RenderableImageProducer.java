package java.awt.image.renderable;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_image_renderable_RenderableImageProducer {
    static void call0()  { java.awt.image.renderable.RenderableImageProducer value = new java.awt.image.renderable.RenderableImageProducer((java.awt.image.renderable.RenderableImage) null, (java.awt.image.renderable.RenderContext) null); }
    static void call1(java.awt.image.renderable.RenderableImageProducer receiver)  { receiver.setRenderContext((java.awt.image.renderable.RenderContext) null); }
    static void call2(java.awt.image.renderable.RenderableImageProducer receiver)  { receiver.addConsumer((java.awt.image.ImageConsumer) null); }
    static void call3(java.awt.image.renderable.RenderableImageProducer receiver)  { boolean value = receiver.isConsumer((java.awt.image.ImageConsumer) null); }
    static void call4(java.awt.image.renderable.RenderableImageProducer receiver)  { receiver.removeConsumer((java.awt.image.ImageConsumer) null); }
    static void call5(java.awt.image.renderable.RenderableImageProducer receiver)  { receiver.startProduction((java.awt.image.ImageConsumer) null); }
    static void call6(java.awt.image.renderable.RenderableImageProducer receiver)  { receiver.requestTopDownLeftRightResend((java.awt.image.ImageConsumer) null); }
    static void call7(java.awt.image.renderable.RenderableImageProducer receiver)  { receiver.run(); }
}

abstract class Override_java_awt_image_renderable_RenderableImageProducer extends java.awt.image.renderable.RenderableImageProducer {
    Override_java_awt_image_renderable_RenderableImageProducer()  { super((java.awt.image.renderable.RenderableImage) null, (java.awt.image.renderable.RenderContext) null); }
    @Override
    public void setRenderContext(java.awt.image.renderable.RenderContext arg0)  { throw new UnsupportedOperationException(); }
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
    @Override
    public void run()  { throw new UnsupportedOperationException(); }
}
