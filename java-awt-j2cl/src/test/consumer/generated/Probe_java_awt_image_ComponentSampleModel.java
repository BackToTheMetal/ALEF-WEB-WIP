package java.awt.image;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_image_ComponentSampleModel {
    static void call0(java.awt.image.ComponentSampleModel receiver) { int[] value = receiver.bandOffsets; receiver.bandOffsets = value; }
    static void call1(java.awt.image.ComponentSampleModel receiver) { int[] value = receiver.bankIndices; receiver.bankIndices = value; }
    static void call2(java.awt.image.ComponentSampleModel receiver) { int value = receiver.numBands; receiver.numBands = value; }
    static void call3(java.awt.image.ComponentSampleModel receiver) { int value = receiver.numBanks; receiver.numBanks = value; }
    static void call4(java.awt.image.ComponentSampleModel receiver) { int value = receiver.scanlineStride; receiver.scanlineStride = value; }
    static void call5(java.awt.image.ComponentSampleModel receiver) { int value = receiver.pixelStride; receiver.pixelStride = value; }
    static void call6()  { java.awt.image.ComponentSampleModel value = new java.awt.image.ComponentSampleModel((int) 0, (int) 0, (int) 0, (int) 0, (int) 0, (int[]) null); }
    static void call7()  { java.awt.image.ComponentSampleModel value = new java.awt.image.ComponentSampleModel((int) 0, (int) 0, (int) 0, (int) 0, (int) 0, (int[]) null, (int[]) null); }
    static void call8(java.awt.image.ComponentSampleModel receiver)  { java.awt.image.SampleModel value = receiver.createCompatibleSampleModel((int) 0, (int) 0); }
    static void call9(java.awt.image.ComponentSampleModel receiver)  { java.awt.image.SampleModel value = receiver.createSubsetSampleModel((int[]) null); }
    static void call10(java.awt.image.ComponentSampleModel receiver)  { java.awt.image.DataBuffer value = receiver.createDataBuffer(); }
    static void call11(java.awt.image.ComponentSampleModel receiver)  { int value = receiver.getOffset((int) 0, (int) 0); }
    static void call12(java.awt.image.ComponentSampleModel receiver)  { int value = receiver.getOffset((int) 0, (int) 0, (int) 0); }
    static void call13(java.awt.image.ComponentSampleModel receiver)  { int[] value = receiver.getSampleSize(); }
    static void call14(java.awt.image.ComponentSampleModel receiver)  { int value = receiver.getSampleSize((int) 0); }
    static void call15(java.awt.image.ComponentSampleModel receiver)  { int[] value = receiver.getBankIndices(); }
    static void call16(java.awt.image.ComponentSampleModel receiver)  { int[] value = receiver.getBandOffsets(); }
    static void call17(java.awt.image.ComponentSampleModel receiver)  { int value = receiver.getScanlineStride(); }
    static void call18(java.awt.image.ComponentSampleModel receiver)  { int value = receiver.getPixelStride(); }
    static void call19(java.awt.image.ComponentSampleModel receiver)  { int value = receiver.getNumDataElements(); }
    static void call20(java.awt.image.ComponentSampleModel receiver)  { java.lang.Object value = receiver.getDataElements((int) 0, (int) 0, (java.lang.Object) null, (java.awt.image.DataBuffer) null); }
    static void call21(java.awt.image.ComponentSampleModel receiver)  { int[] value = receiver.getPixel((int) 0, (int) 0, (int[]) null, (java.awt.image.DataBuffer) null); }
    static void call22(java.awt.image.ComponentSampleModel receiver)  { int[] value = receiver.getPixels((int) 0, (int) 0, (int) 0, (int) 0, (int[]) null, (java.awt.image.DataBuffer) null); }
    static void call23(java.awt.image.ComponentSampleModel receiver)  { int value = receiver.getSample((int) 0, (int) 0, (int) 0, (java.awt.image.DataBuffer) null); }
    static void call24(java.awt.image.ComponentSampleModel receiver)  { float value = receiver.getSampleFloat((int) 0, (int) 0, (int) 0, (java.awt.image.DataBuffer) null); }
    static void call25(java.awt.image.ComponentSampleModel receiver)  { double value = receiver.getSampleDouble((int) 0, (int) 0, (int) 0, (java.awt.image.DataBuffer) null); }
    static void call26(java.awt.image.ComponentSampleModel receiver)  { int[] value = receiver.getSamples((int) 0, (int) 0, (int) 0, (int) 0, (int) 0, (int[]) null, (java.awt.image.DataBuffer) null); }
    static void call27(java.awt.image.ComponentSampleModel receiver)  { receiver.setDataElements((int) 0, (int) 0, (java.lang.Object) null, (java.awt.image.DataBuffer) null); }
    static void call28(java.awt.image.ComponentSampleModel receiver)  { receiver.setPixel((int) 0, (int) 0, (int[]) null, (java.awt.image.DataBuffer) null); }
    static void call29(java.awt.image.ComponentSampleModel receiver)  { receiver.setPixels((int) 0, (int) 0, (int) 0, (int) 0, (int[]) null, (java.awt.image.DataBuffer) null); }
    static void call30(java.awt.image.ComponentSampleModel receiver)  { receiver.setSample((int) 0, (int) 0, (int) 0, (int) 0, (java.awt.image.DataBuffer) null); }
    static void call31(java.awt.image.ComponentSampleModel receiver)  { receiver.setSample((int) 0, (int) 0, (int) 0, (float) 0, (java.awt.image.DataBuffer) null); }
    static void call32(java.awt.image.ComponentSampleModel receiver)  { receiver.setSample((int) 0, (int) 0, (int) 0, (double) 0, (java.awt.image.DataBuffer) null); }
    static void call33(java.awt.image.ComponentSampleModel receiver)  { receiver.setSamples((int) 0, (int) 0, (int) 0, (int) 0, (int) 0, (int[]) null, (java.awt.image.DataBuffer) null); }
    static void call34(java.awt.image.ComponentSampleModel receiver)  { boolean value = receiver.equals((java.lang.Object) null); }
    static void call35(java.awt.image.ComponentSampleModel receiver)  { int value = receiver.hashCode(); }
}

abstract class Override_java_awt_image_ComponentSampleModel extends java.awt.image.ComponentSampleModel {
    Override_java_awt_image_ComponentSampleModel()  { super((int) 0, (int) 0, (int) 0, (int) 0, (int) 0, (int[]) null); }
    @Override
    public java.awt.image.SampleModel createCompatibleSampleModel(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.SampleModel createSubsetSampleModel(int[] arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.DataBuffer createDataBuffer()  { throw new UnsupportedOperationException(); }
    @Override
    public int getOffset(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public int getOffset(int arg0, int arg1, int arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Object getDataElements(int arg0, int arg1, java.lang.Object arg2, java.awt.image.DataBuffer arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public int[] getPixel(int arg0, int arg1, int[] arg2, java.awt.image.DataBuffer arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public int[] getPixels(int arg0, int arg1, int arg2, int arg3, int[] arg4, java.awt.image.DataBuffer arg5)  { throw new UnsupportedOperationException(); }
    @Override
    public int getSample(int arg0, int arg1, int arg2, java.awt.image.DataBuffer arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public float getSampleFloat(int arg0, int arg1, int arg2, java.awt.image.DataBuffer arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public double getSampleDouble(int arg0, int arg1, int arg2, java.awt.image.DataBuffer arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public int[] getSamples(int arg0, int arg1, int arg2, int arg3, int arg4, int[] arg5, java.awt.image.DataBuffer arg6)  { throw new UnsupportedOperationException(); }
    @Override
    public void setDataElements(int arg0, int arg1, java.lang.Object arg2, java.awt.image.DataBuffer arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public void setPixel(int arg0, int arg1, int[] arg2, java.awt.image.DataBuffer arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public void setPixels(int arg0, int arg1, int arg2, int arg3, int[] arg4, java.awt.image.DataBuffer arg5)  { throw new UnsupportedOperationException(); }
    @Override
    public void setSample(int arg0, int arg1, int arg2, int arg3, java.awt.image.DataBuffer arg4)  { throw new UnsupportedOperationException(); }
    @Override
    public void setSample(int arg0, int arg1, int arg2, float arg3, java.awt.image.DataBuffer arg4)  { throw new UnsupportedOperationException(); }
    @Override
    public void setSample(int arg0, int arg1, int arg2, double arg3, java.awt.image.DataBuffer arg4)  { throw new UnsupportedOperationException(); }
    @Override
    public void setSamples(int arg0, int arg1, int arg2, int arg3, int arg4, int[] arg5, java.awt.image.DataBuffer arg6)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean equals(java.lang.Object arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int hashCode()  { throw new UnsupportedOperationException(); }
}
