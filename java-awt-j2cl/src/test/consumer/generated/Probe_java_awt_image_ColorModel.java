package java.awt.image;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_image_ColorModel {
    static void call0(java.awt.image.ColorModel receiver) { int value = receiver.pixel_bits; receiver.pixel_bits = value; }
    static void call1(java.awt.image.ColorModel receiver) { int value = receiver.transferType; receiver.transferType = value; }
    static void call2()  { java.awt.image.ColorModel value = java.awt.image.ColorModel.getRGBdefault(); }
    static void call3(java.awt.image.ColorModel receiver)  { boolean value = receiver.hasAlpha(); }
    static void call4(java.awt.image.ColorModel receiver)  { boolean value = receiver.isAlphaPremultiplied(); }
    static void call5(java.awt.image.ColorModel receiver)  { int value = receiver.getTransferType(); }
    static void call6(java.awt.image.ColorModel receiver)  { int value = receiver.getPixelSize(); }
    static void call7(java.awt.image.ColorModel receiver)  { int value = receiver.getComponentSize((int) 0); }
    static void call8(java.awt.image.ColorModel receiver)  { int[] value = receiver.getComponentSize(); }
    static void call9(java.awt.image.ColorModel receiver)  { int value = receiver.getTransparency(); }
    static void call10(java.awt.image.ColorModel receiver)  { int value = receiver.getNumComponents(); }
    static void call11(java.awt.image.ColorModel receiver)  { int value = receiver.getNumColorComponents(); }
    static void call12(java.awt.image.ColorModel receiver)  { int value = receiver.getRed((int) 0); }
    static void call13(java.awt.image.ColorModel receiver)  { int value = receiver.getGreen((int) 0); }
    static void call14(java.awt.image.ColorModel receiver)  { int value = receiver.getBlue((int) 0); }
    static void call15(java.awt.image.ColorModel receiver)  { int value = receiver.getAlpha((int) 0); }
    static void call16(java.awt.image.ColorModel receiver)  { int value = receiver.getRGB((int) 0); }
    static void call17(java.awt.image.ColorModel receiver)  { int value = receiver.getRed((java.lang.Object) null); }
    static void call18(java.awt.image.ColorModel receiver)  { int value = receiver.getGreen((java.lang.Object) null); }
    static void call19(java.awt.image.ColorModel receiver)  { int value = receiver.getBlue((java.lang.Object) null); }
    static void call20(java.awt.image.ColorModel receiver)  { int value = receiver.getAlpha((java.lang.Object) null); }
    static void call21(java.awt.image.ColorModel receiver)  { int value = receiver.getRGB((java.lang.Object) null); }
    static void call22(java.awt.image.ColorModel receiver)  { java.lang.Object value = receiver.getDataElements((int) 0, (java.lang.Object) null); }
    static void call23(java.awt.image.ColorModel receiver)  { int[] value = receiver.getComponents((int) 0, (int[]) null, (int) 0); }
    static void call24(java.awt.image.ColorModel receiver)  { int[] value = receiver.getComponents((java.lang.Object) null, (int[]) null, (int) 0); }
    static void call25(java.awt.image.ColorModel receiver)  { int[] value = receiver.getUnnormalizedComponents((float[]) null, (int) 0, (int[]) null, (int) 0); }
    static void call26(java.awt.image.ColorModel receiver)  { float[] value = receiver.getNormalizedComponents((int[]) null, (int) 0, (float[]) null, (int) 0); }
    static void call27(java.awt.image.ColorModel receiver)  { int value = receiver.getDataElement((int[]) null, (int) 0); }
    static void call28(java.awt.image.ColorModel receiver)  { java.lang.Object value = receiver.getDataElements((int[]) null, (int) 0, (java.lang.Object) null); }
    static void call29(java.awt.image.ColorModel receiver)  { int value = receiver.getDataElement((float[]) null, (int) 0); }
    static void call30(java.awt.image.ColorModel receiver)  { java.lang.Object value = receiver.getDataElements((float[]) null, (int) 0, (java.lang.Object) null); }
    static void call31(java.awt.image.ColorModel receiver)  { float[] value = receiver.getNormalizedComponents((java.lang.Object) null, (float[]) null, (int) 0); }
    static void call32(java.awt.image.ColorModel receiver)  { boolean value = receiver.equals((java.lang.Object) null); }
    static void call33(java.awt.image.ColorModel receiver)  { int value = receiver.hashCode(); }
    static void call34(java.awt.image.ColorModel receiver)  { java.awt.color.ColorSpace value = receiver.getColorSpace(); }
    static void call35(java.awt.image.ColorModel receiver)  { java.awt.image.ColorModel value = receiver.coerceData((java.awt.image.WritableRaster) null, false); }
    static void call36(java.awt.image.ColorModel receiver)  { boolean value = receiver.isCompatibleRaster((java.awt.image.Raster) null); }
    static void call37(java.awt.image.ColorModel receiver)  { java.awt.image.WritableRaster value = receiver.createCompatibleWritableRaster((int) 0, (int) 0); }
    static void call38(java.awt.image.ColorModel receiver)  { java.awt.image.SampleModel value = receiver.createCompatibleSampleModel((int) 0, (int) 0); }
    static void call39(java.awt.image.ColorModel receiver)  { boolean value = receiver.isCompatibleSampleModel((java.awt.image.SampleModel) null); }
    static void call40(java.awt.image.ColorModel receiver)  { receiver.finalize(); }
    static void call41(java.awt.image.ColorModel receiver)  { java.awt.image.WritableRaster value = receiver.getAlphaRaster((java.awt.image.WritableRaster) null); }
    static void call42(java.awt.image.ColorModel receiver)  { java.lang.String value = receiver.toString(); }
}

abstract class Override_java_awt_image_ColorModel extends java.awt.image.ColorModel {
    Override_java_awt_image_ColorModel()  { super((int) 0); }
    @Override
    public int getPixelSize()  { throw new UnsupportedOperationException(); }
    @Override
    public int getComponentSize(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int[] getComponentSize()  { throw new UnsupportedOperationException(); }
    @Override
    public int getTransparency()  { throw new UnsupportedOperationException(); }
    @Override
    public int getNumComponents()  { throw new UnsupportedOperationException(); }
    @Override
    public int getNumColorComponents()  { throw new UnsupportedOperationException(); }
    @Override
    public int getRed(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int getGreen(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int getBlue(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int getAlpha(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int getRGB(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int getRed(java.lang.Object arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int getGreen(java.lang.Object arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int getBlue(java.lang.Object arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int getAlpha(java.lang.Object arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int getRGB(java.lang.Object arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Object getDataElements(int arg0, java.lang.Object arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public int[] getComponents(int arg0, int[] arg1, int arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public int[] getComponents(java.lang.Object arg0, int[] arg1, int arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public int[] getUnnormalizedComponents(float[] arg0, int arg1, int[] arg2, int arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public float[] getNormalizedComponents(int[] arg0, int arg1, float[] arg2, int arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public int getDataElement(int[] arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Object getDataElements(int[] arg0, int arg1, java.lang.Object arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public int getDataElement(float[] arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Object getDataElements(float[] arg0, int arg1, java.lang.Object arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public float[] getNormalizedComponents(java.lang.Object arg0, float[] arg1, int arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean equals(java.lang.Object arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int hashCode()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.ColorModel coerceData(java.awt.image.WritableRaster arg0, boolean arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isCompatibleRaster(java.awt.image.Raster arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.WritableRaster createCompatibleWritableRaster(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.SampleModel createCompatibleSampleModel(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isCompatibleSampleModel(java.awt.image.SampleModel arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void finalize()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.WritableRaster getAlphaRaster(java.awt.image.WritableRaster arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String toString()  { throw new UnsupportedOperationException(); }
}
