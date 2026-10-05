package java.awt.image;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_image_AffineTransformOp {
    static void call0() { int value = java.awt.image.AffineTransformOp.TYPE_NEAREST_NEIGHBOR; }
    static void call1() { int value = java.awt.image.AffineTransformOp.TYPE_BILINEAR; }
    static void call2() { int value = java.awt.image.AffineTransformOp.TYPE_BICUBIC; }
    static void call3()  { java.awt.image.AffineTransformOp value = new java.awt.image.AffineTransformOp((java.awt.geom.AffineTransform) null, (java.awt.RenderingHints) null); }
    static void call4()  { java.awt.image.AffineTransformOp value = new java.awt.image.AffineTransformOp((java.awt.geom.AffineTransform) null, (int) 0); }
    static void call5(java.awt.image.AffineTransformOp receiver)  { int value = receiver.getInterpolationType(); }
    static void call6(java.awt.image.AffineTransformOp receiver)  { java.awt.image.BufferedImage value = receiver.filter((java.awt.image.BufferedImage) null, (java.awt.image.BufferedImage) null); }
    static void call7(java.awt.image.AffineTransformOp receiver)  { java.awt.image.WritableRaster value = receiver.filter((java.awt.image.Raster) null, (java.awt.image.WritableRaster) null); }
    static void call8(java.awt.image.AffineTransformOp receiver)  { java.awt.geom.Rectangle2D value = receiver.getBounds2D((java.awt.image.BufferedImage) null); }
    static void call9(java.awt.image.AffineTransformOp receiver)  { java.awt.geom.Rectangle2D value = receiver.getBounds2D((java.awt.image.Raster) null); }
    static void call10(java.awt.image.AffineTransformOp receiver)  { java.awt.image.BufferedImage value = receiver.createCompatibleDestImage((java.awt.image.BufferedImage) null, (java.awt.image.ColorModel) null); }
    static void call11(java.awt.image.AffineTransformOp receiver)  { java.awt.image.WritableRaster value = receiver.createCompatibleDestRaster((java.awt.image.Raster) null); }
    static void call12(java.awt.image.AffineTransformOp receiver)  { java.awt.geom.Point2D value = receiver.getPoint2D((java.awt.geom.Point2D) null, (java.awt.geom.Point2D) null); }
    static void call13(java.awt.image.AffineTransformOp receiver)  { java.awt.geom.AffineTransform value = receiver.getTransform(); }
    static void call14(java.awt.image.AffineTransformOp receiver)  { java.awt.RenderingHints value = receiver.getRenderingHints(); }
}

abstract class Override_java_awt_image_AffineTransformOp extends java.awt.image.AffineTransformOp {
    Override_java_awt_image_AffineTransformOp()  { super((java.awt.geom.AffineTransform) null, (java.awt.RenderingHints) null); }
    @Override
    public java.awt.image.BufferedImage createCompatibleDestImage(java.awt.image.BufferedImage arg0, java.awt.image.ColorModel arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.WritableRaster createCompatibleDestRaster(java.awt.image.Raster arg0)  { throw new UnsupportedOperationException(); }
}
