package java.awt.image.renderable;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_image_renderable_RenderContext {
    static void call0()  { java.awt.image.renderable.RenderContext value = new java.awt.image.renderable.RenderContext((java.awt.geom.AffineTransform) null, (java.awt.Shape) null, (java.awt.RenderingHints) null); }
    static void call1()  { java.awt.image.renderable.RenderContext value = new java.awt.image.renderable.RenderContext((java.awt.geom.AffineTransform) null); }
    static void call2()  { java.awt.image.renderable.RenderContext value = new java.awt.image.renderable.RenderContext((java.awt.geom.AffineTransform) null, (java.awt.RenderingHints) null); }
    static void call3()  { java.awt.image.renderable.RenderContext value = new java.awt.image.renderable.RenderContext((java.awt.geom.AffineTransform) null, (java.awt.Shape) null); }
    static void call4(java.awt.image.renderable.RenderContext receiver)  { java.awt.RenderingHints value = receiver.getRenderingHints(); }
    static void call5(java.awt.image.renderable.RenderContext receiver)  { receiver.setRenderingHints((java.awt.RenderingHints) null); }
    static void call6(java.awt.image.renderable.RenderContext receiver)  { receiver.setTransform((java.awt.geom.AffineTransform) null); }
    static void call7(java.awt.image.renderable.RenderContext receiver)  { receiver.preConcatenateTransform((java.awt.geom.AffineTransform) null); }
    static void call8(java.awt.image.renderable.RenderContext receiver)  { receiver.preConcetenateTransform((java.awt.geom.AffineTransform) null); }
    static void call9(java.awt.image.renderable.RenderContext receiver)  { receiver.concatenateTransform((java.awt.geom.AffineTransform) null); }
    static void call10(java.awt.image.renderable.RenderContext receiver)  { receiver.concetenateTransform((java.awt.geom.AffineTransform) null); }
    static void call11(java.awt.image.renderable.RenderContext receiver)  { java.awt.geom.AffineTransform value = receiver.getTransform(); }
    static void call12(java.awt.image.renderable.RenderContext receiver)  { receiver.setAreaOfInterest((java.awt.Shape) null); }
    static void call13(java.awt.image.renderable.RenderContext receiver)  { java.awt.Shape value = receiver.getAreaOfInterest(); }
    static void call14(java.awt.image.renderable.RenderContext receiver)  { java.lang.Object value = receiver.clone(); }
}

abstract class Override_java_awt_image_renderable_RenderContext extends java.awt.image.renderable.RenderContext {
    Override_java_awt_image_renderable_RenderContext()  { super((java.awt.geom.AffineTransform) null, (java.awt.Shape) null, (java.awt.RenderingHints) null); }
    @Override
    public java.awt.RenderingHints getRenderingHints()  { throw new UnsupportedOperationException(); }
    @Override
    public void setRenderingHints(java.awt.RenderingHints arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setTransform(java.awt.geom.AffineTransform arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void preConcatenateTransform(java.awt.geom.AffineTransform arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void preConcetenateTransform(java.awt.geom.AffineTransform arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void concatenateTransform(java.awt.geom.AffineTransform arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void concetenateTransform(java.awt.geom.AffineTransform arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.AffineTransform getTransform()  { throw new UnsupportedOperationException(); }
    @Override
    public void setAreaOfInterest(java.awt.Shape arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Shape getAreaOfInterest()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Object clone()  { throw new UnsupportedOperationException(); }
}
