package java.awt.image;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_image_BufferedImageFilter {
    static void call0()  { java.awt.image.BufferedImageFilter value = new java.awt.image.BufferedImageFilter((java.awt.image.BufferedImageOp) null); }
    static void call1(java.awt.image.BufferedImageFilter receiver)  { java.awt.image.BufferedImageOp value = receiver.getBufferedImageOp(); }
    static void call2(java.awt.image.BufferedImageFilter receiver)  { receiver.setDimensions((int) 0, (int) 0); }
    static void call3(java.awt.image.BufferedImageFilter receiver)  { receiver.setColorModel((java.awt.image.ColorModel) null); }
    static void call4(java.awt.image.BufferedImageFilter receiver)  { receiver.setPixels((int) 0, (int) 0, (int) 0, (int) 0, (java.awt.image.ColorModel) null, (byte[]) null, (int) 0, (int) 0); }
    static void call5(java.awt.image.BufferedImageFilter receiver)  { receiver.setPixels((int) 0, (int) 0, (int) 0, (int) 0, (java.awt.image.ColorModel) null, (int[]) null, (int) 0, (int) 0); }
    static void call6(java.awt.image.BufferedImageFilter receiver)  { receiver.imageComplete((int) 0); }
}

abstract class Override_java_awt_image_BufferedImageFilter extends java.awt.image.BufferedImageFilter {
    Override_java_awt_image_BufferedImageFilter()  { super((java.awt.image.BufferedImageOp) null); }
    @Override
    public java.awt.image.BufferedImageOp getBufferedImageOp()  { throw new UnsupportedOperationException(); }
    @Override
    public void setDimensions(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void setColorModel(java.awt.image.ColorModel arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setPixels(int arg0, int arg1, int arg2, int arg3, java.awt.image.ColorModel arg4, byte[] arg5, int arg6, int arg7)  { throw new UnsupportedOperationException(); }
    @Override
    public void setPixels(int arg0, int arg1, int arg2, int arg3, java.awt.image.ColorModel arg4, int[] arg5, int arg6, int arg7)  { throw new UnsupportedOperationException(); }
    @Override
    public void imageComplete(int arg0)  { throw new UnsupportedOperationException(); }
}
