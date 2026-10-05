package java.awt.image;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_image_IndexColorModel {
    static void call0()  { java.awt.image.IndexColorModel value = new java.awt.image.IndexColorModel((int) 0, (int) 0, (byte[]) null, (byte[]) null, (byte[]) null); }
    static void call1()  { java.awt.image.IndexColorModel value = new java.awt.image.IndexColorModel((int) 0, (int) 0, (byte[]) null, (byte[]) null, (byte[]) null, (int) 0); }
    static void call2()  { java.awt.image.IndexColorModel value = new java.awt.image.IndexColorModel((int) 0, (int) 0, (byte[]) null, (byte[]) null, (byte[]) null, (byte[]) null); }
    static void call3()  { java.awt.image.IndexColorModel value = new java.awt.image.IndexColorModel((int) 0, (int) 0, (byte[]) null, (int) 0, false); }
    static void call4()  { java.awt.image.IndexColorModel value = new java.awt.image.IndexColorModel((int) 0, (int) 0, (byte[]) null, (int) 0, false, (int) 0); }
    static void call5()  { java.awt.image.IndexColorModel value = new java.awt.image.IndexColorModel((int) 0, (int) 0, (int[]) null, (int) 0, false, (int) 0, (int) 0); }
    static void call6()  { java.awt.image.IndexColorModel value = new java.awt.image.IndexColorModel((int) 0, (int) 0, (int[]) null, (int) 0, (int) 0, (java.math.BigInteger) null); }
    static void call7(java.awt.image.IndexColorModel receiver)  { int value = receiver.getTransparency(); }
    static void call8(java.awt.image.IndexColorModel receiver)  { int[] value = receiver.getComponentSize(); }
    static void call9(java.awt.image.IndexColorModel receiver)  { int value = receiver.getMapSize(); }
    static void call10(java.awt.image.IndexColorModel receiver)  { int value = receiver.getTransparentPixel(); }
    static void call11(java.awt.image.IndexColorModel receiver)  { receiver.getReds((byte[]) null); }
    static void call12(java.awt.image.IndexColorModel receiver)  { receiver.getGreens((byte[]) null); }
    static void call13(java.awt.image.IndexColorModel receiver)  { receiver.getBlues((byte[]) null); }
    static void call14(java.awt.image.IndexColorModel receiver)  { receiver.getAlphas((byte[]) null); }
    static void call15(java.awt.image.IndexColorModel receiver)  { receiver.getRGBs((int[]) null); }
    static void call16(java.awt.image.IndexColorModel receiver)  { int value = receiver.getRed((int) 0); }
    static void call17(java.awt.image.IndexColorModel receiver)  { int value = receiver.getGreen((int) 0); }
    static void call18(java.awt.image.IndexColorModel receiver)  { int value = receiver.getBlue((int) 0); }
    static void call19(java.awt.image.IndexColorModel receiver)  { int value = receiver.getAlpha((int) 0); }
    static void call20(java.awt.image.IndexColorModel receiver)  { int value = receiver.getRGB((int) 0); }
    static void call21(java.awt.image.IndexColorModel receiver)  { java.lang.Object value = receiver.getDataElements((int) 0, (java.lang.Object) null); }
    static void call22(java.awt.image.IndexColorModel receiver)  { int[] value = receiver.getComponents((int) 0, (int[]) null, (int) 0); }
    static void call23(java.awt.image.IndexColorModel receiver)  { int[] value = receiver.getComponents((java.lang.Object) null, (int[]) null, (int) 0); }
    static void call24(java.awt.image.IndexColorModel receiver)  { int value = receiver.getDataElement((int[]) null, (int) 0); }
    static void call25(java.awt.image.IndexColorModel receiver)  { java.lang.Object value = receiver.getDataElements((int[]) null, (int) 0, (java.lang.Object) null); }
    static void call26(java.awt.image.IndexColorModel receiver)  { java.awt.image.WritableRaster value = receiver.createCompatibleWritableRaster((int) 0, (int) 0); }
    static void call27(java.awt.image.IndexColorModel receiver)  { boolean value = receiver.isCompatibleRaster((java.awt.image.Raster) null); }
    static void call28(java.awt.image.IndexColorModel receiver)  { java.awt.image.SampleModel value = receiver.createCompatibleSampleModel((int) 0, (int) 0); }
    static void call29(java.awt.image.IndexColorModel receiver)  { boolean value = receiver.isCompatibleSampleModel((java.awt.image.SampleModel) null); }
    static void call30(java.awt.image.IndexColorModel receiver)  { java.awt.image.BufferedImage value = receiver.convertToIntDiscrete((java.awt.image.Raster) null, false); }
    static void call31(java.awt.image.IndexColorModel receiver)  { boolean value = receiver.isValid((int) 0); }
    static void call32(java.awt.image.IndexColorModel receiver)  { boolean value = receiver.isValid(); }
    static void call33(java.awt.image.IndexColorModel receiver)  { java.math.BigInteger value = receiver.getValidPixels(); }
    static void call34(java.awt.image.IndexColorModel receiver)  { receiver.finalize(); }
    static void call35(java.awt.image.IndexColorModel receiver)  { java.lang.String value = receiver.toString(); }
    static void call36(java.awt.image.IndexColorModel receiver)  { boolean value = receiver.equals((java.lang.Object) null); }
    static void call37(java.awt.image.IndexColorModel receiver)  { int value = receiver.hashCode(); }
}

abstract class Override_java_awt_image_IndexColorModel extends java.awt.image.IndexColorModel {
    Override_java_awt_image_IndexColorModel()  { super((int) 0, (int) 0, (byte[]) null, (byte[]) null, (byte[]) null); }
    @Override
    public int getTransparency()  { throw new UnsupportedOperationException(); }
    @Override
    public int[] getComponentSize()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Object getDataElements(int arg0, java.lang.Object arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public int[] getComponents(int arg0, int[] arg1, int arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public int[] getComponents(java.lang.Object arg0, int[] arg1, int arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public int getDataElement(int[] arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Object getDataElements(int[] arg0, int arg1, java.lang.Object arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.WritableRaster createCompatibleWritableRaster(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isCompatibleRaster(java.awt.image.Raster arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.SampleModel createCompatibleSampleModel(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isCompatibleSampleModel(java.awt.image.SampleModel arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.BufferedImage convertToIntDiscrete(java.awt.image.Raster arg0, boolean arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isValid(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isValid()  { throw new UnsupportedOperationException(); }
    @Override
    public java.math.BigInteger getValidPixels()  { throw new UnsupportedOperationException(); }
    @Override
    public void finalize()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String toString()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean equals(java.lang.Object arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int hashCode()  { throw new UnsupportedOperationException(); }
}
