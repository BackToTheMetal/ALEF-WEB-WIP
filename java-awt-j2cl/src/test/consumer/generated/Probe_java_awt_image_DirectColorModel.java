package java.awt.image;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_image_DirectColorModel {
    static void call0()  { java.awt.image.DirectColorModel value = new java.awt.image.DirectColorModel((int) 0, (int) 0, (int) 0, (int) 0); }
    static void call1()  { java.awt.image.DirectColorModel value = new java.awt.image.DirectColorModel((int) 0, (int) 0, (int) 0, (int) 0, (int) 0); }
    static void call2()  { java.awt.image.DirectColorModel value = new java.awt.image.DirectColorModel((java.awt.color.ColorSpace) null, (int) 0, (int) 0, (int) 0, (int) 0, (int) 0, false, (int) 0); }
    static void call3(java.awt.image.DirectColorModel receiver)  { int value = receiver.getRedMask(); }
    static void call4(java.awt.image.DirectColorModel receiver)  { int value = receiver.getGreenMask(); }
    static void call5(java.awt.image.DirectColorModel receiver)  { int value = receiver.getBlueMask(); }
    static void call6(java.awt.image.DirectColorModel receiver)  { int value = receiver.getAlphaMask(); }
    static void call7(java.awt.image.DirectColorModel receiver)  { int value = receiver.getRed((int) 0); }
    static void call8(java.awt.image.DirectColorModel receiver)  { int value = receiver.getGreen((int) 0); }
    static void call9(java.awt.image.DirectColorModel receiver)  { int value = receiver.getBlue((int) 0); }
    static void call10(java.awt.image.DirectColorModel receiver)  { int value = receiver.getAlpha((int) 0); }
    static void call11(java.awt.image.DirectColorModel receiver)  { int value = receiver.getRGB((int) 0); }
    static void call12(java.awt.image.DirectColorModel receiver)  { int value = receiver.getRed((java.lang.Object) null); }
    static void call13(java.awt.image.DirectColorModel receiver)  { int value = receiver.getGreen((java.lang.Object) null); }
    static void call14(java.awt.image.DirectColorModel receiver)  { int value = receiver.getBlue((java.lang.Object) null); }
    static void call15(java.awt.image.DirectColorModel receiver)  { int value = receiver.getAlpha((java.lang.Object) null); }
    static void call16(java.awt.image.DirectColorModel receiver)  { int value = receiver.getRGB((java.lang.Object) null); }
    static void call17(java.awt.image.DirectColorModel receiver)  { java.lang.Object value = receiver.getDataElements((int) 0, (java.lang.Object) null); }
    static void call18(java.awt.image.DirectColorModel receiver)  { int[] value = receiver.getComponents((int) 0, (int[]) null, (int) 0); }
    static void call19(java.awt.image.DirectColorModel receiver)  { int[] value = receiver.getComponents((java.lang.Object) null, (int[]) null, (int) 0); }
    static void call20(java.awt.image.DirectColorModel receiver)  { java.awt.image.WritableRaster value = receiver.createCompatibleWritableRaster((int) 0, (int) 0); }
    static void call21(java.awt.image.DirectColorModel receiver)  { int value = receiver.getDataElement((int[]) null, (int) 0); }
    static void call22(java.awt.image.DirectColorModel receiver)  { java.lang.Object value = receiver.getDataElements((int[]) null, (int) 0, (java.lang.Object) null); }
    static void call23(java.awt.image.DirectColorModel receiver)  { java.awt.image.ColorModel value = receiver.coerceData((java.awt.image.WritableRaster) null, false); }
    static void call24(java.awt.image.DirectColorModel receiver)  { boolean value = receiver.isCompatibleRaster((java.awt.image.Raster) null); }
    static void call25(java.awt.image.DirectColorModel receiver)  { java.lang.String value = receiver.toString(); }
}

abstract class Override_java_awt_image_DirectColorModel extends java.awt.image.DirectColorModel {
    Override_java_awt_image_DirectColorModel()  { super((int) 0, (int) 0, (int) 0, (int) 0); }
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
    public int getDataElement(int[] arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Object getDataElements(int[] arg0, int arg1, java.lang.Object arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isCompatibleRaster(java.awt.image.Raster arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String toString()  { throw new UnsupportedOperationException(); }
}
