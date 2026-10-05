package java.awt.image;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_image_BandCombineOp {
    static void call0()  { java.awt.image.BandCombineOp value = new java.awt.image.BandCombineOp((float[][]) null, (java.awt.RenderingHints) null); }
    static void call1(java.awt.image.BandCombineOp receiver)  { float[][] value = receiver.getMatrix(); }
    static void call2(java.awt.image.BandCombineOp receiver)  { java.awt.image.WritableRaster value = receiver.filter((java.awt.image.Raster) null, (java.awt.image.WritableRaster) null); }
    static void call3(java.awt.image.BandCombineOp receiver)  { java.awt.geom.Rectangle2D value = receiver.getBounds2D((java.awt.image.Raster) null); }
    static void call4(java.awt.image.BandCombineOp receiver)  { java.awt.image.WritableRaster value = receiver.createCompatibleDestRaster((java.awt.image.Raster) null); }
    static void call5(java.awt.image.BandCombineOp receiver)  { java.awt.geom.Point2D value = receiver.getPoint2D((java.awt.geom.Point2D) null, (java.awt.geom.Point2D) null); }
    static void call6(java.awt.image.BandCombineOp receiver)  { java.awt.RenderingHints value = receiver.getRenderingHints(); }
}

abstract class Override_java_awt_image_BandCombineOp extends java.awt.image.BandCombineOp {
    Override_java_awt_image_BandCombineOp()  { super((float[][]) null, (java.awt.RenderingHints) null); }
    @Override
    public java.awt.image.WritableRaster filter(java.awt.image.Raster arg0, java.awt.image.WritableRaster arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.WritableRaster createCompatibleDestRaster(java.awt.image.Raster arg0)  { throw new UnsupportedOperationException(); }
}
