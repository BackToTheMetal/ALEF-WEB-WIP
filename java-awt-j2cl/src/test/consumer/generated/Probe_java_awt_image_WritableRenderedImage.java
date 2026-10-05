package java.awt.image;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_image_WritableRenderedImage {
    static void call0(java.awt.image.WritableRenderedImage receiver)  { receiver.addTileObserver((java.awt.image.TileObserver) null); }
    static void call1(java.awt.image.WritableRenderedImage receiver)  { receiver.removeTileObserver((java.awt.image.TileObserver) null); }
    static void call2(java.awt.image.WritableRenderedImage receiver)  { java.awt.image.WritableRaster value = receiver.getWritableTile((int) 0, (int) 0); }
    static void call3(java.awt.image.WritableRenderedImage receiver)  { receiver.releaseWritableTile((int) 0, (int) 0); }
    static void call4(java.awt.image.WritableRenderedImage receiver)  { boolean value = receiver.isTileWritable((int) 0, (int) 0); }
    static void call5(java.awt.image.WritableRenderedImage receiver)  { java.awt.Point[] value = receiver.getWritableTileIndices(); }
    static void call6(java.awt.image.WritableRenderedImage receiver)  { boolean value = receiver.hasTileWriters(); }
    static void call7(java.awt.image.WritableRenderedImage receiver)  { receiver.setData((java.awt.image.Raster) null); }
}
