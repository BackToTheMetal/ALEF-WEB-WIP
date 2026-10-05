package java.awt.image;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_image_Raster {
    static void call0(java.awt.image.Raster receiver) { java.awt.image.SampleModel value = receiver.sampleModel; receiver.sampleModel = value; }
    static void call1(java.awt.image.Raster receiver) { java.awt.image.DataBuffer value = receiver.dataBuffer; receiver.dataBuffer = value; }
    static void call2(java.awt.image.Raster receiver) { int value = receiver.minX; receiver.minX = value; }
    static void call3(java.awt.image.Raster receiver) { int value = receiver.minY; receiver.minY = value; }
    static void call4(java.awt.image.Raster receiver) { int value = receiver.width; receiver.width = value; }
    static void call5(java.awt.image.Raster receiver) { int value = receiver.height; receiver.height = value; }
    static void call6(java.awt.image.Raster receiver) { int value = receiver.sampleModelTranslateX; receiver.sampleModelTranslateX = value; }
    static void call7(java.awt.image.Raster receiver) { int value = receiver.sampleModelTranslateY; receiver.sampleModelTranslateY = value; }
    static void call8(java.awt.image.Raster receiver) { int value = receiver.numBands; receiver.numBands = value; }
    static void call9(java.awt.image.Raster receiver) { int value = receiver.numDataElements; receiver.numDataElements = value; }
    static void call10(java.awt.image.Raster receiver) { java.awt.image.Raster value = receiver.parent; receiver.parent = value; }
    static void call11()  { java.awt.image.WritableRaster value = java.awt.image.Raster.createInterleavedRaster((int) 0, (int) 0, (int) 0, (int) 0, (java.awt.Point) null); }
    static void call12()  { java.awt.image.WritableRaster value = java.awt.image.Raster.createInterleavedRaster((int) 0, (int) 0, (int) 0, (int) 0, (int) 0, (int[]) null, (java.awt.Point) null); }
    static void call13()  { java.awt.image.WritableRaster value = java.awt.image.Raster.createBandedRaster((int) 0, (int) 0, (int) 0, (int) 0, (java.awt.Point) null); }
    static void call14()  { java.awt.image.WritableRaster value = java.awt.image.Raster.createBandedRaster((int) 0, (int) 0, (int) 0, (int) 0, (int[]) null, (int[]) null, (java.awt.Point) null); }
    static void call15()  { java.awt.image.WritableRaster value = java.awt.image.Raster.createPackedRaster((int) 0, (int) 0, (int) 0, (int[]) null, (java.awt.Point) null); }
    static void call16()  { java.awt.image.WritableRaster value = java.awt.image.Raster.createPackedRaster((int) 0, (int) 0, (int) 0, (int) 0, (int) 0, (java.awt.Point) null); }
    static void call17()  { java.awt.image.WritableRaster value = java.awt.image.Raster.createInterleavedRaster((java.awt.image.DataBuffer) null, (int) 0, (int) 0, (int) 0, (int) 0, (int[]) null, (java.awt.Point) null); }
    static void call18()  { java.awt.image.WritableRaster value = java.awt.image.Raster.createBandedRaster((java.awt.image.DataBuffer) null, (int) 0, (int) 0, (int) 0, (int[]) null, (int[]) null, (java.awt.Point) null); }
    static void call19()  { java.awt.image.WritableRaster value = java.awt.image.Raster.createPackedRaster((java.awt.image.DataBuffer) null, (int) 0, (int) 0, (int) 0, (int[]) null, (java.awt.Point) null); }
    static void call20()  { java.awt.image.WritableRaster value = java.awt.image.Raster.createPackedRaster((java.awt.image.DataBuffer) null, (int) 0, (int) 0, (int) 0, (java.awt.Point) null); }
    static void call21()  { java.awt.image.Raster value = java.awt.image.Raster.createRaster((java.awt.image.SampleModel) null, (java.awt.image.DataBuffer) null, (java.awt.Point) null); }
    static void call22()  { java.awt.image.WritableRaster value = java.awt.image.Raster.createWritableRaster((java.awt.image.SampleModel) null, (java.awt.Point) null); }
    static void call23()  { java.awt.image.WritableRaster value = java.awt.image.Raster.createWritableRaster((java.awt.image.SampleModel) null, (java.awt.image.DataBuffer) null, (java.awt.Point) null); }
    static void call24()  { java.awt.image.Raster value = new java.awt.image.Raster((java.awt.image.SampleModel) null, (java.awt.Point) null); }
    static void call25()  { java.awt.image.Raster value = new java.awt.image.Raster((java.awt.image.SampleModel) null, (java.awt.image.DataBuffer) null, (java.awt.Point) null); }
    static void call26()  { java.awt.image.Raster value = new java.awt.image.Raster((java.awt.image.SampleModel) null, (java.awt.image.DataBuffer) null, (java.awt.Rectangle) null, (java.awt.Point) null, (java.awt.image.Raster) null); }
    static void call27(java.awt.image.Raster receiver)  { java.awt.image.Raster value = receiver.getParent(); }
    static void call28(java.awt.image.Raster receiver)  { int value = receiver.getSampleModelTranslateX(); }
    static void call29(java.awt.image.Raster receiver)  { int value = receiver.getSampleModelTranslateY(); }
    static void call30(java.awt.image.Raster receiver)  { java.awt.image.WritableRaster value = receiver.createCompatibleWritableRaster(); }
    static void call31(java.awt.image.Raster receiver)  { java.awt.image.WritableRaster value = receiver.createCompatibleWritableRaster((int) 0, (int) 0); }
    static void call32(java.awt.image.Raster receiver)  { java.awt.image.WritableRaster value = receiver.createCompatibleWritableRaster((java.awt.Rectangle) null); }
    static void call33(java.awt.image.Raster receiver)  { java.awt.image.WritableRaster value = receiver.createCompatibleWritableRaster((int) 0, (int) 0, (int) 0, (int) 0); }
    static void call34(java.awt.image.Raster receiver)  { java.awt.image.Raster value = receiver.createTranslatedChild((int) 0, (int) 0); }
    static void call35(java.awt.image.Raster receiver)  { java.awt.image.Raster value = receiver.createChild((int) 0, (int) 0, (int) 0, (int) 0, (int) 0, (int) 0, (int[]) null); }
    static void call36(java.awt.image.Raster receiver)  { java.awt.Rectangle value = receiver.getBounds(); }
    static void call37(java.awt.image.Raster receiver)  { int value = receiver.getMinX(); }
    static void call38(java.awt.image.Raster receiver)  { int value = receiver.getMinY(); }
    static void call39(java.awt.image.Raster receiver)  { int value = receiver.getWidth(); }
    static void call40(java.awt.image.Raster receiver)  { int value = receiver.getHeight(); }
    static void call41(java.awt.image.Raster receiver)  { int value = receiver.getNumBands(); }
    static void call42(java.awt.image.Raster receiver)  { int value = receiver.getNumDataElements(); }
    static void call43(java.awt.image.Raster receiver)  { int value = receiver.getTransferType(); }
    static void call44(java.awt.image.Raster receiver)  { java.awt.image.DataBuffer value = receiver.getDataBuffer(); }
    static void call45(java.awt.image.Raster receiver)  { java.awt.image.SampleModel value = receiver.getSampleModel(); }
    static void call46(java.awt.image.Raster receiver)  { java.lang.Object value = receiver.getDataElements((int) 0, (int) 0, (java.lang.Object) null); }
    static void call47(java.awt.image.Raster receiver)  { java.lang.Object value = receiver.getDataElements((int) 0, (int) 0, (int) 0, (int) 0, (java.lang.Object) null); }
    static void call48(java.awt.image.Raster receiver)  { int[] value = receiver.getPixel((int) 0, (int) 0, (int[]) null); }
    static void call49(java.awt.image.Raster receiver)  { float[] value = receiver.getPixel((int) 0, (int) 0, (float[]) null); }
    static void call50(java.awt.image.Raster receiver)  { double[] value = receiver.getPixel((int) 0, (int) 0, (double[]) null); }
    static void call51(java.awt.image.Raster receiver)  { int[] value = receiver.getPixels((int) 0, (int) 0, (int) 0, (int) 0, (int[]) null); }
    static void call52(java.awt.image.Raster receiver)  { float[] value = receiver.getPixels((int) 0, (int) 0, (int) 0, (int) 0, (float[]) null); }
    static void call53(java.awt.image.Raster receiver)  { double[] value = receiver.getPixels((int) 0, (int) 0, (int) 0, (int) 0, (double[]) null); }
    static void call54(java.awt.image.Raster receiver)  { int value = receiver.getSample((int) 0, (int) 0, (int) 0); }
    static void call55(java.awt.image.Raster receiver)  { float value = receiver.getSampleFloat((int) 0, (int) 0, (int) 0); }
    static void call56(java.awt.image.Raster receiver)  { double value = receiver.getSampleDouble((int) 0, (int) 0, (int) 0); }
    static void call57(java.awt.image.Raster receiver)  { int[] value = receiver.getSamples((int) 0, (int) 0, (int) 0, (int) 0, (int) 0, (int[]) null); }
    static void call58(java.awt.image.Raster receiver)  { float[] value = receiver.getSamples((int) 0, (int) 0, (int) 0, (int) 0, (int) 0, (float[]) null); }
    static void call59(java.awt.image.Raster receiver)  { double[] value = receiver.getSamples((int) 0, (int) 0, (int) 0, (int) 0, (int) 0, (double[]) null); }
}

abstract class Override_java_awt_image_Raster extends java.awt.image.Raster {
    Override_java_awt_image_Raster()  { super((java.awt.image.SampleModel) null, (java.awt.Point) null); }
    @Override
    public java.awt.image.Raster getParent()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.WritableRaster createCompatibleWritableRaster()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.WritableRaster createCompatibleWritableRaster(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.WritableRaster createCompatibleWritableRaster(java.awt.Rectangle arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.WritableRaster createCompatibleWritableRaster(int arg0, int arg1, int arg2, int arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.Raster createTranslatedChild(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.Raster createChild(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5, int[] arg6)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Rectangle getBounds()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.DataBuffer getDataBuffer()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.SampleModel getSampleModel()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Object getDataElements(int arg0, int arg1, java.lang.Object arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Object getDataElements(int arg0, int arg1, int arg2, int arg3, java.lang.Object arg4)  { throw new UnsupportedOperationException(); }
    @Override
    public int[] getPixel(int arg0, int arg1, int[] arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public float[] getPixel(int arg0, int arg1, float[] arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public double[] getPixel(int arg0, int arg1, double[] arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public int[] getPixels(int arg0, int arg1, int arg2, int arg3, int[] arg4)  { throw new UnsupportedOperationException(); }
    @Override
    public float[] getPixels(int arg0, int arg1, int arg2, int arg3, float[] arg4)  { throw new UnsupportedOperationException(); }
    @Override
    public double[] getPixels(int arg0, int arg1, int arg2, int arg3, double[] arg4)  { throw new UnsupportedOperationException(); }
    @Override
    public int getSample(int arg0, int arg1, int arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public float getSampleFloat(int arg0, int arg1, int arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public double getSampleDouble(int arg0, int arg1, int arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public int[] getSamples(int arg0, int arg1, int arg2, int arg3, int arg4, int[] arg5)  { throw new UnsupportedOperationException(); }
    @Override
    public float[] getSamples(int arg0, int arg1, int arg2, int arg3, int arg4, float[] arg5)  { throw new UnsupportedOperationException(); }
    @Override
    public double[] getSamples(int arg0, int arg1, int arg2, int arg3, int arg4, double[] arg5)  { throw new UnsupportedOperationException(); }
}
