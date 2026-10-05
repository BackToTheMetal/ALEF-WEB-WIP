package java.awt.image;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_image_RenderedImage {
    static void call0(java.awt.image.RenderedImage receiver)  { java.util.Vector value = receiver.getSources(); }
    static void call1(java.awt.image.RenderedImage receiver)  { java.lang.Object value = receiver.getProperty((java.lang.String) null); }
    static void call2(java.awt.image.RenderedImage receiver)  { java.lang.String[] value = receiver.getPropertyNames(); }
    static void call3(java.awt.image.RenderedImage receiver)  { java.awt.image.ColorModel value = receiver.getColorModel(); }
    static void call4(java.awt.image.RenderedImage receiver)  { java.awt.image.SampleModel value = receiver.getSampleModel(); }
    static void call5(java.awt.image.RenderedImage receiver)  { int value = receiver.getWidth(); }
    static void call6(java.awt.image.RenderedImage receiver)  { int value = receiver.getHeight(); }
    static void call7(java.awt.image.RenderedImage receiver)  { int value = receiver.getMinX(); }
    static void call8(java.awt.image.RenderedImage receiver)  { int value = receiver.getMinY(); }
    static void call9(java.awt.image.RenderedImage receiver)  { int value = receiver.getNumXTiles(); }
    static void call10(java.awt.image.RenderedImage receiver)  { int value = receiver.getNumYTiles(); }
    static void call11(java.awt.image.RenderedImage receiver)  { int value = receiver.getMinTileX(); }
    static void call12(java.awt.image.RenderedImage receiver)  { int value = receiver.getMinTileY(); }
    static void call13(java.awt.image.RenderedImage receiver)  { int value = receiver.getTileWidth(); }
    static void call14(java.awt.image.RenderedImage receiver)  { int value = receiver.getTileHeight(); }
    static void call15(java.awt.image.RenderedImage receiver)  { int value = receiver.getTileGridXOffset(); }
    static void call16(java.awt.image.RenderedImage receiver)  { int value = receiver.getTileGridYOffset(); }
    static void call17(java.awt.image.RenderedImage receiver)  { java.awt.image.Raster value = receiver.getTile((int) 0, (int) 0); }
    static void call18(java.awt.image.RenderedImage receiver)  { java.awt.image.Raster value = receiver.getData(); }
    static void call19(java.awt.image.RenderedImage receiver)  { java.awt.image.Raster value = receiver.getData((java.awt.Rectangle) null); }
    static void call20(java.awt.image.RenderedImage receiver)  { java.awt.image.WritableRaster value = receiver.copyData((java.awt.image.WritableRaster) null); }
}
