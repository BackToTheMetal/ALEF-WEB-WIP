package java.awt.image.renderable;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_image_renderable_RenderableImageOp {
    static void call0()  { java.awt.image.renderable.RenderableImageOp value = new java.awt.image.renderable.RenderableImageOp((java.awt.image.renderable.ContextualRenderedImageFactory) null, (java.awt.image.renderable.ParameterBlock) null); }
    static void call1(java.awt.image.renderable.RenderableImageOp receiver)  { java.util.Vector value = receiver.getSources(); }
    static void call2(java.awt.image.renderable.RenderableImageOp receiver)  { java.lang.Object value = receiver.getProperty((java.lang.String) null); }
    static void call3(java.awt.image.renderable.RenderableImageOp receiver)  { java.lang.String[] value = receiver.getPropertyNames(); }
    static void call4(java.awt.image.renderable.RenderableImageOp receiver)  { boolean value = receiver.isDynamic(); }
    static void call5(java.awt.image.renderable.RenderableImageOp receiver)  { float value = receiver.getWidth(); }
    static void call6(java.awt.image.renderable.RenderableImageOp receiver)  { float value = receiver.getHeight(); }
    static void call7(java.awt.image.renderable.RenderableImageOp receiver)  { float value = receiver.getMinX(); }
    static void call8(java.awt.image.renderable.RenderableImageOp receiver)  { float value = receiver.getMinY(); }
    static void call9(java.awt.image.renderable.RenderableImageOp receiver)  { java.awt.image.renderable.ParameterBlock value = receiver.setParameterBlock((java.awt.image.renderable.ParameterBlock) null); }
    static void call10(java.awt.image.renderable.RenderableImageOp receiver)  { java.awt.image.renderable.ParameterBlock value = receiver.getParameterBlock(); }
    static void call11(java.awt.image.renderable.RenderableImageOp receiver)  { java.awt.image.RenderedImage value = receiver.createScaledRendering((int) 0, (int) 0, (java.awt.RenderingHints) null); }
    static void call12(java.awt.image.renderable.RenderableImageOp receiver)  { java.awt.image.RenderedImage value = receiver.createDefaultRendering(); }
    static void call13(java.awt.image.renderable.RenderableImageOp receiver)  { java.awt.image.RenderedImage value = receiver.createRendering((java.awt.image.renderable.RenderContext) null); }
}

abstract class Override_java_awt_image_renderable_RenderableImageOp extends java.awt.image.renderable.RenderableImageOp {
    Override_java_awt_image_renderable_RenderableImageOp()  { super((java.awt.image.renderable.ContextualRenderedImageFactory) null, (java.awt.image.renderable.ParameterBlock) null); }
    @Override
    public java.util.Vector<java.awt.image.renderable.RenderableImage> getSources()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Object getProperty(java.lang.String arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String[] getPropertyNames()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isDynamic()  { throw new UnsupportedOperationException(); }
    @Override
    public float getWidth()  { throw new UnsupportedOperationException(); }
    @Override
    public float getHeight()  { throw new UnsupportedOperationException(); }
    @Override
    public float getMinX()  { throw new UnsupportedOperationException(); }
    @Override
    public float getMinY()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.renderable.ParameterBlock setParameterBlock(java.awt.image.renderable.ParameterBlock arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.renderable.ParameterBlock getParameterBlock()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.RenderedImage createScaledRendering(int arg0, int arg1, java.awt.RenderingHints arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.RenderedImage createDefaultRendering()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.RenderedImage createRendering(java.awt.image.renderable.RenderContext arg0)  { throw new UnsupportedOperationException(); }
}
