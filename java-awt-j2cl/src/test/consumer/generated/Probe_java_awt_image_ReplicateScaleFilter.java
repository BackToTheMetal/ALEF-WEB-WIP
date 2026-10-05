package java.awt.image;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_image_ReplicateScaleFilter {
    static void call0(java.awt.image.ReplicateScaleFilter receiver) { int value = receiver.srcWidth; receiver.srcWidth = value; }
    static void call1(java.awt.image.ReplicateScaleFilter receiver) { int value = receiver.srcHeight; receiver.srcHeight = value; }
    static void call2(java.awt.image.ReplicateScaleFilter receiver) { int value = receiver.destWidth; receiver.destWidth = value; }
    static void call3(java.awt.image.ReplicateScaleFilter receiver) { int value = receiver.destHeight; receiver.destHeight = value; }
    static void call4(java.awt.image.ReplicateScaleFilter receiver) { int[] value = receiver.srcrows; receiver.srcrows = value; }
    static void call5(java.awt.image.ReplicateScaleFilter receiver) { int[] value = receiver.srccols; receiver.srccols = value; }
    static void call6(java.awt.image.ReplicateScaleFilter receiver) { java.lang.Object value = receiver.outpixbuf; receiver.outpixbuf = value; }
    static void call7()  { java.awt.image.ReplicateScaleFilter value = new java.awt.image.ReplicateScaleFilter((int) 0, (int) 0); }
    static void call8(java.awt.image.ReplicateScaleFilter receiver)  { receiver.setDimensions((int) 0, (int) 0); }
    static void call9(java.awt.image.ReplicateScaleFilter receiver)  { receiver.setPixels((int) 0, (int) 0, (int) 0, (int) 0, (java.awt.image.ColorModel) null, (byte[]) null, (int) 0, (int) 0); }
    static void call10(java.awt.image.ReplicateScaleFilter receiver)  { receiver.setPixels((int) 0, (int) 0, (int) 0, (int) 0, (java.awt.image.ColorModel) null, (int[]) null, (int) 0, (int) 0); }
}

abstract class Override_java_awt_image_ReplicateScaleFilter extends java.awt.image.ReplicateScaleFilter {
    Override_java_awt_image_ReplicateScaleFilter()  { super((int) 0, (int) 0); }
    @Override
    public void setDimensions(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void setPixels(int arg0, int arg1, int arg2, int arg3, java.awt.image.ColorModel arg4, byte[] arg5, int arg6, int arg7)  { throw new UnsupportedOperationException(); }
    @Override
    public void setPixels(int arg0, int arg1, int arg2, int arg3, java.awt.image.ColorModel arg4, int[] arg5, int arg6, int arg7)  { throw new UnsupportedOperationException(); }
}
