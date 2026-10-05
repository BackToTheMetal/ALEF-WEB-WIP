package java.awt.image;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_image_RescaleOp {
    static void call0()  { java.awt.image.RescaleOp value = new java.awt.image.RescaleOp((float[]) null, (float[]) null, (java.awt.RenderingHints) null); }
    static void call1()  { java.awt.image.RescaleOp value = new java.awt.image.RescaleOp((float) 0, (float) 0, (java.awt.RenderingHints) null); }
    static void call2(java.awt.image.RescaleOp receiver)  { float[] value = receiver.getScaleFactors((float[]) null); }
    static void call3(java.awt.image.RescaleOp receiver)  { float[] value = receiver.getOffsets((float[]) null); }
    static void call4(java.awt.image.RescaleOp receiver)  { int value = receiver.getNumFactors(); }
    static void call5(java.awt.image.RescaleOp receiver)  { java.awt.image.BufferedImage value = receiver.filter((java.awt.image.BufferedImage) null, (java.awt.image.BufferedImage) null); }
    static void call6(java.awt.image.RescaleOp receiver)  { java.awt.image.WritableRaster value = receiver.filter((java.awt.image.Raster) null, (java.awt.image.WritableRaster) null); }
    static void call7(java.awt.image.RescaleOp receiver)  { java.awt.geom.Rectangle2D value = receiver.getBounds2D((java.awt.image.BufferedImage) null); }
    static void call8(java.awt.image.RescaleOp receiver)  { java.awt.geom.Rectangle2D value = receiver.getBounds2D((java.awt.image.Raster) null); }
    static void call9(java.awt.image.RescaleOp receiver)  { java.awt.image.BufferedImage value = receiver.createCompatibleDestImage((java.awt.image.BufferedImage) null, (java.awt.image.ColorModel) null); }
    static void call10(java.awt.image.RescaleOp receiver)  { java.awt.image.WritableRaster value = receiver.createCompatibleDestRaster((java.awt.image.Raster) null); }
    static void call11(java.awt.image.RescaleOp receiver)  { java.awt.geom.Point2D value = receiver.getPoint2D((java.awt.geom.Point2D) null, (java.awt.geom.Point2D) null); }
    static void call12(java.awt.image.RescaleOp receiver)  { java.awt.RenderingHints value = receiver.getRenderingHints(); }
}

abstract class Override_java_awt_image_RescaleOp extends java.awt.image.RescaleOp {
    Override_java_awt_image_RescaleOp()  { super((float[]) null, (float[]) null, (java.awt.RenderingHints) null); }
    @Override
    public java.awt.image.BufferedImage createCompatibleDestImage(java.awt.image.BufferedImage arg0, java.awt.image.ColorModel arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.WritableRaster createCompatibleDestRaster(java.awt.image.Raster arg0)  { throw new UnsupportedOperationException(); }
}
