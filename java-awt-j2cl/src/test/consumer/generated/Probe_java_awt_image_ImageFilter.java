package java.awt.image;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_image_ImageFilter {
    static void call0(java.awt.image.ImageFilter receiver) { java.awt.image.ImageConsumer value = receiver.consumer; receiver.consumer = value; }
    static void call1()  { java.awt.image.ImageFilter value = new java.awt.image.ImageFilter(); }
    static void call2(java.awt.image.ImageFilter receiver)  { java.awt.image.ImageFilter value = receiver.getFilterInstance((java.awt.image.ImageConsumer) null); }
    static void call3(java.awt.image.ImageFilter receiver)  { receiver.setDimensions((int) 0, (int) 0); }
    static void call4(java.awt.image.ImageFilter receiver)  { receiver.setColorModel((java.awt.image.ColorModel) null); }
    static void call5(java.awt.image.ImageFilter receiver)  { receiver.setHints((int) 0); }
    static void call6(java.awt.image.ImageFilter receiver)  { receiver.setPixels((int) 0, (int) 0, (int) 0, (int) 0, (java.awt.image.ColorModel) null, (byte[]) null, (int) 0, (int) 0); }
    static void call7(java.awt.image.ImageFilter receiver)  { receiver.setPixels((int) 0, (int) 0, (int) 0, (int) 0, (java.awt.image.ColorModel) null, (int[]) null, (int) 0, (int) 0); }
    static void call8(java.awt.image.ImageFilter receiver)  { receiver.imageComplete((int) 0); }
    static void call9(java.awt.image.ImageFilter receiver)  { receiver.resendTopDownLeftRight((java.awt.image.ImageProducer) null); }
    static void call10(java.awt.image.ImageFilter receiver)  { java.lang.Object value = receiver.clone(); }
}

abstract class Override_java_awt_image_ImageFilter extends java.awt.image.ImageFilter {
    Override_java_awt_image_ImageFilter()  { super(); }
    @Override
    public java.awt.image.ImageFilter getFilterInstance(java.awt.image.ImageConsumer arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setDimensions(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void setColorModel(java.awt.image.ColorModel arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setHints(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setPixels(int arg0, int arg1, int arg2, int arg3, java.awt.image.ColorModel arg4, byte[] arg5, int arg6, int arg7)  { throw new UnsupportedOperationException(); }
    @Override
    public void setPixels(int arg0, int arg1, int arg2, int arg3, java.awt.image.ColorModel arg4, int[] arg5, int arg6, int arg7)  { throw new UnsupportedOperationException(); }
    @Override
    public void imageComplete(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void resendTopDownLeftRight(java.awt.image.ImageProducer arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Object clone()  { throw new UnsupportedOperationException(); }
}
