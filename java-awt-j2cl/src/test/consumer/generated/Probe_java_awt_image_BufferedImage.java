package java.awt.image;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_image_BufferedImage {
    static void call0() { int value = java.awt.image.BufferedImage.TYPE_CUSTOM; }
    static void call1() { int value = java.awt.image.BufferedImage.TYPE_INT_RGB; }
    static void call2() { int value = java.awt.image.BufferedImage.TYPE_INT_ARGB; }
    static void call3() { int value = java.awt.image.BufferedImage.TYPE_INT_ARGB_PRE; }
    static void call4() { int value = java.awt.image.BufferedImage.TYPE_INT_BGR; }
    static void call5() { int value = java.awt.image.BufferedImage.TYPE_3BYTE_BGR; }
    static void call6() { int value = java.awt.image.BufferedImage.TYPE_4BYTE_ABGR; }
    static void call7() { int value = java.awt.image.BufferedImage.TYPE_4BYTE_ABGR_PRE; }
    static void call8() { int value = java.awt.image.BufferedImage.TYPE_USHORT_565_RGB; }
    static void call9() { int value = java.awt.image.BufferedImage.TYPE_USHORT_555_RGB; }
    static void call10() { int value = java.awt.image.BufferedImage.TYPE_BYTE_GRAY; }
    static void call11() { int value = java.awt.image.BufferedImage.TYPE_USHORT_GRAY; }
    static void call12() { int value = java.awt.image.BufferedImage.TYPE_BYTE_BINARY; }
    static void call13() { int value = java.awt.image.BufferedImage.TYPE_BYTE_INDEXED; }
    static void call14()  { java.awt.image.BufferedImage value = new java.awt.image.BufferedImage((int) 0, (int) 0, (int) 0); }
    static void call15()  { java.awt.image.BufferedImage value = new java.awt.image.BufferedImage((int) 0, (int) 0, (int) 0, (java.awt.image.IndexColorModel) null); }
    static void call16(java.awt.image.BufferedImage receiver)  { int value = receiver.getType(); }
    static void call17(java.awt.image.BufferedImage receiver)  { java.awt.image.ColorModel value = receiver.getColorModel(); }
    static void call18(java.awt.image.BufferedImage receiver)  { java.awt.image.WritableRaster value = receiver.getRaster(); }
    static void call19(java.awt.image.BufferedImage receiver)  { java.awt.image.WritableRaster value = receiver.getAlphaRaster(); }
    static void call20(java.awt.image.BufferedImage receiver)  { int value = receiver.getRGB((int) 0, (int) 0); }
    static void call21(java.awt.image.BufferedImage receiver)  { int[] value = receiver.getRGB((int) 0, (int) 0, (int) 0, (int) 0, (int[]) null, (int) 0, (int) 0); }
    static void call22(java.awt.image.BufferedImage receiver)  { receiver.setRGB((int) 0, (int) 0, (int) 0); }
    static void call23(java.awt.image.BufferedImage receiver)  { receiver.setRGB((int) 0, (int) 0, (int) 0, (int) 0, (int[]) null, (int) 0, (int) 0); }
    static void call24(java.awt.image.BufferedImage receiver)  { int value = receiver.getWidth(); }
    static void call25(java.awt.image.BufferedImage receiver)  { int value = receiver.getHeight(); }
    static void call26(java.awt.image.BufferedImage receiver)  { int value = receiver.getWidth((java.awt.image.ImageObserver) null); }
    static void call27(java.awt.image.BufferedImage receiver)  { int value = receiver.getHeight((java.awt.image.ImageObserver) null); }
    static void call28(java.awt.image.BufferedImage receiver)  { java.awt.image.ImageProducer value = receiver.getSource(); }
    static void call29(java.awt.image.BufferedImage receiver)  { java.lang.Object value = receiver.getProperty((java.lang.String) null, (java.awt.image.ImageObserver) null); }
    static void call30(java.awt.image.BufferedImage receiver)  { java.lang.Object value = receiver.getProperty((java.lang.String) null); }
    static void call31(java.awt.image.BufferedImage receiver)  { java.awt.Graphics value = receiver.getGraphics(); }
    static void call32(java.awt.image.BufferedImage receiver)  { java.awt.Graphics2D value = receiver.createGraphics(); }
    static void call33(java.awt.image.BufferedImage receiver)  { java.awt.image.BufferedImage value = receiver.getSubimage((int) 0, (int) 0, (int) 0, (int) 0); }
    static void call34(java.awt.image.BufferedImage receiver)  { boolean value = receiver.isAlphaPremultiplied(); }
    static void call35(java.awt.image.BufferedImage receiver)  { receiver.coerceData(false); }
    static void call36(java.awt.image.BufferedImage receiver)  { java.lang.String value = receiver.toString(); }
    static void call37(java.awt.image.BufferedImage receiver)  { java.util.Vector value = receiver.getSources(); }
    static void call38(java.awt.image.BufferedImage receiver)  { java.lang.String[] value = receiver.getPropertyNames(); }
    static void call39(java.awt.image.BufferedImage receiver)  { int value = receiver.getMinX(); }
    static void call40(java.awt.image.BufferedImage receiver)  { int value = receiver.getMinY(); }
    static void call41(java.awt.image.BufferedImage receiver)  { java.awt.image.SampleModel value = receiver.getSampleModel(); }
    static void call42(java.awt.image.BufferedImage receiver)  { int value = receiver.getNumXTiles(); }
    static void call43(java.awt.image.BufferedImage receiver)  { int value = receiver.getNumYTiles(); }
    static void call44(java.awt.image.BufferedImage receiver)  { int value = receiver.getMinTileX(); }
    static void call45(java.awt.image.BufferedImage receiver)  { int value = receiver.getMinTileY(); }
    static void call46(java.awt.image.BufferedImage receiver)  { int value = receiver.getTileWidth(); }
    static void call47(java.awt.image.BufferedImage receiver)  { int value = receiver.getTileHeight(); }
    static void call48(java.awt.image.BufferedImage receiver)  { int value = receiver.getTileGridXOffset(); }
    static void call49(java.awt.image.BufferedImage receiver)  { int value = receiver.getTileGridYOffset(); }
    static void call50(java.awt.image.BufferedImage receiver)  { java.awt.image.Raster value = receiver.getTile((int) 0, (int) 0); }
    static void call51(java.awt.image.BufferedImage receiver)  { java.awt.image.Raster value = receiver.getData(); }
    static void call52(java.awt.image.BufferedImage receiver)  { java.awt.image.Raster value = receiver.getData((java.awt.Rectangle) null); }
    static void call53(java.awt.image.BufferedImage receiver)  { java.awt.image.WritableRaster value = receiver.copyData((java.awt.image.WritableRaster) null); }
    static void call54(java.awt.image.BufferedImage receiver)  { receiver.setData((java.awt.image.Raster) null); }
    static void call55(java.awt.image.BufferedImage receiver)  { receiver.addTileObserver((java.awt.image.TileObserver) null); }
    static void call56(java.awt.image.BufferedImage receiver)  { receiver.removeTileObserver((java.awt.image.TileObserver) null); }
    static void call57(java.awt.image.BufferedImage receiver)  { boolean value = receiver.isTileWritable((int) 0, (int) 0); }
    static void call58(java.awt.image.BufferedImage receiver)  { java.awt.Point[] value = receiver.getWritableTileIndices(); }
    static void call59(java.awt.image.BufferedImage receiver)  { boolean value = receiver.hasTileWriters(); }
    static void call60(java.awt.image.BufferedImage receiver)  { java.awt.image.WritableRaster value = receiver.getWritableTile((int) 0, (int) 0); }
    static void call61(java.awt.image.BufferedImage receiver)  { receiver.releaseWritableTile((int) 0, (int) 0); }
    static void call62(java.awt.image.BufferedImage receiver)  { int value = receiver.getTransparency(); }
}

abstract class Override_java_awt_image_BufferedImage extends java.awt.image.BufferedImage {
    Override_java_awt_image_BufferedImage()  { super((int) 0, (int) 0, (int) 0); }
    @Override
    public int getType()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.ColorModel getColorModel()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.WritableRaster getRaster()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.WritableRaster getAlphaRaster()  { throw new UnsupportedOperationException(); }
    @Override
    public int getRGB(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public int[] getRGB(int arg0, int arg1, int arg2, int arg3, int[] arg4, int arg5, int arg6)  { throw new UnsupportedOperationException(); }
    @Override
    public void setRGB(int arg0, int arg1, int arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public void setRGB(int arg0, int arg1, int arg2, int arg3, int[] arg4, int arg5, int arg6)  { throw new UnsupportedOperationException(); }
    @Override
    public int getWidth()  { throw new UnsupportedOperationException(); }
    @Override
    public int getHeight()  { throw new UnsupportedOperationException(); }
    @Override
    public int getWidth(java.awt.image.ImageObserver arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int getHeight(java.awt.image.ImageObserver arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.ImageProducer getSource()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Object getProperty(java.lang.String arg0, java.awt.image.ImageObserver arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Object getProperty(java.lang.String arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Graphics getGraphics()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Graphics2D createGraphics()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.BufferedImage getSubimage(int arg0, int arg1, int arg2, int arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isAlphaPremultiplied()  { throw new UnsupportedOperationException(); }
    @Override
    public void coerceData(boolean arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String toString()  { throw new UnsupportedOperationException(); }
    @Override
    public java.util.Vector<java.awt.image.RenderedImage> getSources()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String[] getPropertyNames()  { throw new UnsupportedOperationException(); }
    @Override
    public int getMinX()  { throw new UnsupportedOperationException(); }
    @Override
    public int getMinY()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.SampleModel getSampleModel()  { throw new UnsupportedOperationException(); }
    @Override
    public int getNumXTiles()  { throw new UnsupportedOperationException(); }
    @Override
    public int getNumYTiles()  { throw new UnsupportedOperationException(); }
    @Override
    public int getMinTileX()  { throw new UnsupportedOperationException(); }
    @Override
    public int getMinTileY()  { throw new UnsupportedOperationException(); }
    @Override
    public int getTileWidth()  { throw new UnsupportedOperationException(); }
    @Override
    public int getTileHeight()  { throw new UnsupportedOperationException(); }
    @Override
    public int getTileGridXOffset()  { throw new UnsupportedOperationException(); }
    @Override
    public int getTileGridYOffset()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.Raster getTile(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.Raster getData()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.Raster getData(java.awt.Rectangle arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.WritableRaster copyData(java.awt.image.WritableRaster arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setData(java.awt.image.Raster arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void addTileObserver(java.awt.image.TileObserver arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void removeTileObserver(java.awt.image.TileObserver arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isTileWritable(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Point[] getWritableTileIndices()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean hasTileWriters()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.WritableRaster getWritableTile(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void releaseWritableTile(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public int getTransparency()  { throw new UnsupportedOperationException(); }
}
