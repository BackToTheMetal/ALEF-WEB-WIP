package java.awt.image;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_image_ColorConvertOp {
    static void call0()  { java.awt.image.ColorConvertOp value = new java.awt.image.ColorConvertOp((java.awt.RenderingHints) null); }
    static void call1()  { java.awt.image.ColorConvertOp value = new java.awt.image.ColorConvertOp((java.awt.color.ColorSpace) null, (java.awt.RenderingHints) null); }
    static void call2()  { java.awt.image.ColorConvertOp value = new java.awt.image.ColorConvertOp((java.awt.color.ColorSpace) null, (java.awt.color.ColorSpace) null, (java.awt.RenderingHints) null); }
    static void call3()  { java.awt.image.ColorConvertOp value = new java.awt.image.ColorConvertOp((java.awt.color.ICC_Profile[]) null, (java.awt.RenderingHints) null); }
    static void call4(java.awt.image.ColorConvertOp receiver)  { java.awt.color.ICC_Profile[] value = receiver.getICC_Profiles(); }
    static void call5(java.awt.image.ColorConvertOp receiver)  { java.awt.image.BufferedImage value = receiver.filter((java.awt.image.BufferedImage) null, (java.awt.image.BufferedImage) null); }
    static void call6(java.awt.image.ColorConvertOp receiver)  { java.awt.image.WritableRaster value = receiver.filter((java.awt.image.Raster) null, (java.awt.image.WritableRaster) null); }
    static void call7(java.awt.image.ColorConvertOp receiver)  { java.awt.geom.Rectangle2D value = receiver.getBounds2D((java.awt.image.BufferedImage) null); }
    static void call8(java.awt.image.ColorConvertOp receiver)  { java.awt.geom.Rectangle2D value = receiver.getBounds2D((java.awt.image.Raster) null); }
    static void call9(java.awt.image.ColorConvertOp receiver)  { java.awt.image.BufferedImage value = receiver.createCompatibleDestImage((java.awt.image.BufferedImage) null, (java.awt.image.ColorModel) null); }
    static void call10(java.awt.image.ColorConvertOp receiver)  { java.awt.image.WritableRaster value = receiver.createCompatibleDestRaster((java.awt.image.Raster) null); }
    static void call11(java.awt.image.ColorConvertOp receiver)  { java.awt.geom.Point2D value = receiver.getPoint2D((java.awt.geom.Point2D) null, (java.awt.geom.Point2D) null); }
    static void call12(java.awt.image.ColorConvertOp receiver)  { java.awt.RenderingHints value = receiver.getRenderingHints(); }
}

abstract class Override_java_awt_image_ColorConvertOp extends java.awt.image.ColorConvertOp {
    Override_java_awt_image_ColorConvertOp()  { super((java.awt.RenderingHints) null); }
    @Override
    public java.awt.image.BufferedImage createCompatibleDestImage(java.awt.image.BufferedImage arg0, java.awt.image.ColorModel arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.WritableRaster createCompatibleDestRaster(java.awt.image.Raster arg0)  { throw new UnsupportedOperationException(); }
}
