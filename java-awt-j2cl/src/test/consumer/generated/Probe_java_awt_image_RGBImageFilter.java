package java.awt.image;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_image_RGBImageFilter {
    static void call0(java.awt.image.RGBImageFilter receiver) { java.awt.image.ColorModel value = receiver.origmodel; receiver.origmodel = value; }
    static void call1(java.awt.image.RGBImageFilter receiver) { java.awt.image.ColorModel value = receiver.newmodel; receiver.newmodel = value; }
    static void call2(java.awt.image.RGBImageFilter receiver) { boolean value = receiver.canFilterIndexColorModel; receiver.canFilterIndexColorModel = value; }
    static void call3(java.awt.image.RGBImageFilter receiver)  { receiver.setColorModel((java.awt.image.ColorModel) null); }
    static void call4(java.awt.image.RGBImageFilter receiver)  { receiver.substituteColorModel((java.awt.image.ColorModel) null, (java.awt.image.ColorModel) null); }
    static void call5(java.awt.image.RGBImageFilter receiver)  { java.awt.image.IndexColorModel value = receiver.filterIndexColorModel((java.awt.image.IndexColorModel) null); }
    static void call6(java.awt.image.RGBImageFilter receiver)  { receiver.filterRGBPixels((int) 0, (int) 0, (int) 0, (int) 0, (int[]) null, (int) 0, (int) 0); }
    static void call7(java.awt.image.RGBImageFilter receiver)  { receiver.setPixels((int) 0, (int) 0, (int) 0, (int) 0, (java.awt.image.ColorModel) null, (byte[]) null, (int) 0, (int) 0); }
    static void call8(java.awt.image.RGBImageFilter receiver)  { receiver.setPixels((int) 0, (int) 0, (int) 0, (int) 0, (java.awt.image.ColorModel) null, (int[]) null, (int) 0, (int) 0); }
    static void call9(java.awt.image.RGBImageFilter receiver)  { int value = receiver.filterRGB((int) 0, (int) 0, (int) 0); }
}

abstract class Override_java_awt_image_RGBImageFilter extends java.awt.image.RGBImageFilter {
    Override_java_awt_image_RGBImageFilter()  { super(); }
    @Override
    public void setColorModel(java.awt.image.ColorModel arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void substituteColorModel(java.awt.image.ColorModel arg0, java.awt.image.ColorModel arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.IndexColorModel filterIndexColorModel(java.awt.image.IndexColorModel arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void filterRGBPixels(int arg0, int arg1, int arg2, int arg3, int[] arg4, int arg5, int arg6)  { throw new UnsupportedOperationException(); }
    @Override
    public void setPixels(int arg0, int arg1, int arg2, int arg3, java.awt.image.ColorModel arg4, byte[] arg5, int arg6, int arg7)  { throw new UnsupportedOperationException(); }
    @Override
    public void setPixels(int arg0, int arg1, int arg2, int arg3, java.awt.image.ColorModel arg4, int[] arg5, int arg6, int arg7)  { throw new UnsupportedOperationException(); }
    @Override
    public int filterRGB(int arg0, int arg1, int arg2)  { throw new UnsupportedOperationException(); }
}
