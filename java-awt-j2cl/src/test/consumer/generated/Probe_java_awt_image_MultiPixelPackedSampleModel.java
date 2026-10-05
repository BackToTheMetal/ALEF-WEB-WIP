package java.awt.image;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_image_MultiPixelPackedSampleModel {
    static void call0()  { java.awt.image.MultiPixelPackedSampleModel value = new java.awt.image.MultiPixelPackedSampleModel((int) 0, (int) 0, (int) 0, (int) 0); }
    static void call1()  { java.awt.image.MultiPixelPackedSampleModel value = new java.awt.image.MultiPixelPackedSampleModel((int) 0, (int) 0, (int) 0, (int) 0, (int) 0, (int) 0); }
    static void call2(java.awt.image.MultiPixelPackedSampleModel receiver)  { java.awt.image.SampleModel value = receiver.createCompatibleSampleModel((int) 0, (int) 0); }
    static void call3(java.awt.image.MultiPixelPackedSampleModel receiver)  { java.awt.image.DataBuffer value = receiver.createDataBuffer(); }
    static void call4(java.awt.image.MultiPixelPackedSampleModel receiver)  { int value = receiver.getNumDataElements(); }
    static void call5(java.awt.image.MultiPixelPackedSampleModel receiver)  { int[] value = receiver.getSampleSize(); }
    static void call6(java.awt.image.MultiPixelPackedSampleModel receiver)  { int value = receiver.getSampleSize((int) 0); }
    static void call7(java.awt.image.MultiPixelPackedSampleModel receiver)  { int value = receiver.getOffset((int) 0, (int) 0); }
    static void call8(java.awt.image.MultiPixelPackedSampleModel receiver)  { int value = receiver.getBitOffset((int) 0); }
    static void call9(java.awt.image.MultiPixelPackedSampleModel receiver)  { int value = receiver.getScanlineStride(); }
    static void call10(java.awt.image.MultiPixelPackedSampleModel receiver)  { int value = receiver.getPixelBitStride(); }
    static void call11(java.awt.image.MultiPixelPackedSampleModel receiver)  { int value = receiver.getDataBitOffset(); }
    static void call12(java.awt.image.MultiPixelPackedSampleModel receiver)  { int value = receiver.getTransferType(); }
    static void call13(java.awt.image.MultiPixelPackedSampleModel receiver)  { java.awt.image.SampleModel value = receiver.createSubsetSampleModel((int[]) null); }
    static void call14(java.awt.image.MultiPixelPackedSampleModel receiver)  { int value = receiver.getSample((int) 0, (int) 0, (int) 0, (java.awt.image.DataBuffer) null); }
    static void call15(java.awt.image.MultiPixelPackedSampleModel receiver)  { receiver.setSample((int) 0, (int) 0, (int) 0, (int) 0, (java.awt.image.DataBuffer) null); }
    static void call16(java.awt.image.MultiPixelPackedSampleModel receiver)  { java.lang.Object value = receiver.getDataElements((int) 0, (int) 0, (java.lang.Object) null, (java.awt.image.DataBuffer) null); }
    static void call17(java.awt.image.MultiPixelPackedSampleModel receiver)  { int[] value = receiver.getPixel((int) 0, (int) 0, (int[]) null, (java.awt.image.DataBuffer) null); }
    static void call18(java.awt.image.MultiPixelPackedSampleModel receiver)  { receiver.setDataElements((int) 0, (int) 0, (java.lang.Object) null, (java.awt.image.DataBuffer) null); }
    static void call19(java.awt.image.MultiPixelPackedSampleModel receiver)  { receiver.setPixel((int) 0, (int) 0, (int[]) null, (java.awt.image.DataBuffer) null); }
    static void call20(java.awt.image.MultiPixelPackedSampleModel receiver)  { boolean value = receiver.equals((java.lang.Object) null); }
    static void call21(java.awt.image.MultiPixelPackedSampleModel receiver)  { int value = receiver.hashCode(); }
}

abstract class Override_java_awt_image_MultiPixelPackedSampleModel extends java.awt.image.MultiPixelPackedSampleModel {
    Override_java_awt_image_MultiPixelPackedSampleModel()  { super((int) 0, (int) 0, (int) 0, (int) 0); }
    @Override
    public java.awt.image.SampleModel createCompatibleSampleModel(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.DataBuffer createDataBuffer()  { throw new UnsupportedOperationException(); }
    @Override
    public int getNumDataElements()  { throw new UnsupportedOperationException(); }
    @Override
    public int[] getSampleSize()  { throw new UnsupportedOperationException(); }
    @Override
    public int getSampleSize(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int getOffset(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public int getBitOffset(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int getScanlineStride()  { throw new UnsupportedOperationException(); }
    @Override
    public int getPixelBitStride()  { throw new UnsupportedOperationException(); }
    @Override
    public int getDataBitOffset()  { throw new UnsupportedOperationException(); }
    @Override
    public int getTransferType()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.SampleModel createSubsetSampleModel(int[] arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int getSample(int arg0, int arg1, int arg2, java.awt.image.DataBuffer arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public void setSample(int arg0, int arg1, int arg2, int arg3, java.awt.image.DataBuffer arg4)  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Object getDataElements(int arg0, int arg1, java.lang.Object arg2, java.awt.image.DataBuffer arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public int[] getPixel(int arg0, int arg1, int[] arg2, java.awt.image.DataBuffer arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public void setDataElements(int arg0, int arg1, java.lang.Object arg2, java.awt.image.DataBuffer arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public void setPixel(int arg0, int arg1, int[] arg2, java.awt.image.DataBuffer arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean equals(java.lang.Object arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int hashCode()  { throw new UnsupportedOperationException(); }
}
