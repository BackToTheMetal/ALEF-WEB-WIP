package java.awt.image;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_image_WritableRaster {
    static void call0()  { java.awt.image.WritableRaster value = new java.awt.image.WritableRaster((java.awt.image.SampleModel) null, (java.awt.Point) null); }
    static void call1()  { java.awt.image.WritableRaster value = new java.awt.image.WritableRaster((java.awt.image.SampleModel) null, (java.awt.image.DataBuffer) null, (java.awt.Point) null); }
    static void call2()  { java.awt.image.WritableRaster value = new java.awt.image.WritableRaster((java.awt.image.SampleModel) null, (java.awt.image.DataBuffer) null, (java.awt.Rectangle) null, (java.awt.Point) null, (java.awt.image.WritableRaster) null); }
    static void call3(java.awt.image.WritableRaster receiver)  { java.awt.image.WritableRaster value = receiver.getWritableParent(); }
    static void call4(java.awt.image.WritableRaster receiver)  { java.awt.image.WritableRaster value = receiver.createWritableTranslatedChild((int) 0, (int) 0); }
    static void call5(java.awt.image.WritableRaster receiver)  { java.awt.image.WritableRaster value = receiver.createWritableChild((int) 0, (int) 0, (int) 0, (int) 0, (int) 0, (int) 0, (int[]) null); }
    static void call6(java.awt.image.WritableRaster receiver)  { receiver.setDataElements((int) 0, (int) 0, (java.lang.Object) null); }
    static void call7(java.awt.image.WritableRaster receiver)  { receiver.setDataElements((int) 0, (int) 0, (java.awt.image.Raster) null); }
    static void call8(java.awt.image.WritableRaster receiver)  { receiver.setDataElements((int) 0, (int) 0, (int) 0, (int) 0, (java.lang.Object) null); }
    static void call9(java.awt.image.WritableRaster receiver)  { receiver.setRect((java.awt.image.Raster) null); }
    static void call10(java.awt.image.WritableRaster receiver)  { receiver.setRect((int) 0, (int) 0, (java.awt.image.Raster) null); }
    static void call11(java.awt.image.WritableRaster receiver)  { receiver.setPixel((int) 0, (int) 0, (int[]) null); }
    static void call12(java.awt.image.WritableRaster receiver)  { receiver.setPixel((int) 0, (int) 0, (float[]) null); }
    static void call13(java.awt.image.WritableRaster receiver)  { receiver.setPixel((int) 0, (int) 0, (double[]) null); }
    static void call14(java.awt.image.WritableRaster receiver)  { receiver.setPixels((int) 0, (int) 0, (int) 0, (int) 0, (int[]) null); }
    static void call15(java.awt.image.WritableRaster receiver)  { receiver.setPixels((int) 0, (int) 0, (int) 0, (int) 0, (float[]) null); }
    static void call16(java.awt.image.WritableRaster receiver)  { receiver.setPixels((int) 0, (int) 0, (int) 0, (int) 0, (double[]) null); }
    static void call17(java.awt.image.WritableRaster receiver)  { receiver.setSample((int) 0, (int) 0, (int) 0, (int) 0); }
    static void call18(java.awt.image.WritableRaster receiver)  { receiver.setSample((int) 0, (int) 0, (int) 0, (float) 0); }
    static void call19(java.awt.image.WritableRaster receiver)  { receiver.setSample((int) 0, (int) 0, (int) 0, (double) 0); }
    static void call20(java.awt.image.WritableRaster receiver)  { receiver.setSamples((int) 0, (int) 0, (int) 0, (int) 0, (int) 0, (int[]) null); }
    static void call21(java.awt.image.WritableRaster receiver)  { receiver.setSamples((int) 0, (int) 0, (int) 0, (int) 0, (int) 0, (float[]) null); }
    static void call22(java.awt.image.WritableRaster receiver)  { receiver.setSamples((int) 0, (int) 0, (int) 0, (int) 0, (int) 0, (double[]) null); }
}

abstract class Override_java_awt_image_WritableRaster extends java.awt.image.WritableRaster {
    Override_java_awt_image_WritableRaster()  { super((java.awt.image.SampleModel) null, (java.awt.Point) null); }
    @Override
    public java.awt.image.WritableRaster getWritableParent()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.WritableRaster createWritableTranslatedChild(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.WritableRaster createWritableChild(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5, int[] arg6)  { throw new UnsupportedOperationException(); }
    @Override
    public void setDataElements(int arg0, int arg1, java.lang.Object arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public void setDataElements(int arg0, int arg1, java.awt.image.Raster arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public void setDataElements(int arg0, int arg1, int arg2, int arg3, java.lang.Object arg4)  { throw new UnsupportedOperationException(); }
    @Override
    public void setRect(java.awt.image.Raster arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setRect(int arg0, int arg1, java.awt.image.Raster arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public void setPixel(int arg0, int arg1, int[] arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public void setPixel(int arg0, int arg1, float[] arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public void setPixel(int arg0, int arg1, double[] arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public void setPixels(int arg0, int arg1, int arg2, int arg3, int[] arg4)  { throw new UnsupportedOperationException(); }
    @Override
    public void setPixels(int arg0, int arg1, int arg2, int arg3, float[] arg4)  { throw new UnsupportedOperationException(); }
    @Override
    public void setPixels(int arg0, int arg1, int arg2, int arg3, double[] arg4)  { throw new UnsupportedOperationException(); }
    @Override
    public void setSample(int arg0, int arg1, int arg2, int arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public void setSample(int arg0, int arg1, int arg2, float arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public void setSample(int arg0, int arg1, int arg2, double arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public void setSamples(int arg0, int arg1, int arg2, int arg3, int arg4, int[] arg5)  { throw new UnsupportedOperationException(); }
    @Override
    public void setSamples(int arg0, int arg1, int arg2, int arg3, int arg4, float[] arg5)  { throw new UnsupportedOperationException(); }
    @Override
    public void setSamples(int arg0, int arg1, int arg2, int arg3, int arg4, double[] arg5)  { throw new UnsupportedOperationException(); }
}
