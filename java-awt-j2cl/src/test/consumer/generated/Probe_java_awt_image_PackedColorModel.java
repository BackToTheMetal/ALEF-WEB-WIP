package java.awt.image;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_image_PackedColorModel {
    static void call0(java.awt.image.PackedColorModel receiver)  { int value = receiver.getMask((int) 0); }
    static void call1(java.awt.image.PackedColorModel receiver)  { int[] value = receiver.getMasks(); }
    static void call2(java.awt.image.PackedColorModel receiver)  { java.awt.image.SampleModel value = receiver.createCompatibleSampleModel((int) 0, (int) 0); }
    static void call3(java.awt.image.PackedColorModel receiver)  { boolean value = receiver.isCompatibleSampleModel((java.awt.image.SampleModel) null); }
    static void call4(java.awt.image.PackedColorModel receiver)  { java.awt.image.WritableRaster value = receiver.getAlphaRaster((java.awt.image.WritableRaster) null); }
    static void call5(java.awt.image.PackedColorModel receiver)  { boolean value = receiver.equals((java.lang.Object) null); }
    static void call6(java.awt.image.PackedColorModel receiver)  { int value = receiver.hashCode(); }
}

abstract class Override_java_awt_image_PackedColorModel extends java.awt.image.PackedColorModel {
    Override_java_awt_image_PackedColorModel()  { super((java.awt.color.ColorSpace) null, (int) 0, (int[]) null, (int) 0, false, (int) 0, (int) 0); }
    @Override
    public java.awt.image.SampleModel createCompatibleSampleModel(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isCompatibleSampleModel(java.awt.image.SampleModel arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.WritableRaster getAlphaRaster(java.awt.image.WritableRaster arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean equals(java.lang.Object arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int hashCode()  { throw new UnsupportedOperationException(); }
}
