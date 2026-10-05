package java.awt.image;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_image_MemoryImageSource {
    static void call0()  { java.awt.image.MemoryImageSource value = new java.awt.image.MemoryImageSource((int) 0, (int) 0, (java.awt.image.ColorModel) null, (byte[]) null, (int) 0, (int) 0); }
    static void call1()  { java.awt.image.MemoryImageSource value = new java.awt.image.MemoryImageSource((int) 0, (int) 0, (java.awt.image.ColorModel) null, (int[]) null, (int) 0, (int) 0); }
    static void call2()  { java.awt.image.MemoryImageSource value = new java.awt.image.MemoryImageSource((int) 0, (int) 0, (int[]) null, (int) 0, (int) 0); }
    static void call3(java.awt.image.MemoryImageSource receiver)  { receiver.addConsumer((java.awt.image.ImageConsumer) null); }
    static void call4(java.awt.image.MemoryImageSource receiver)  { boolean value = receiver.isConsumer((java.awt.image.ImageConsumer) null); }
    static void call5(java.awt.image.MemoryImageSource receiver)  { receiver.removeConsumer((java.awt.image.ImageConsumer) null); }
    static void call6(java.awt.image.MemoryImageSource receiver)  { receiver.startProduction((java.awt.image.ImageConsumer) null); }
    static void call7(java.awt.image.MemoryImageSource receiver)  { receiver.requestTopDownLeftRightResend((java.awt.image.ImageConsumer) null); }
    static void call8(java.awt.image.MemoryImageSource receiver)  { receiver.setAnimated(false); }
    static void call9(java.awt.image.MemoryImageSource receiver)  { receiver.setFullBufferUpdates(false); }
    static void call10(java.awt.image.MemoryImageSource receiver)  { receiver.newPixels(); }
    static void call11(java.awt.image.MemoryImageSource receiver)  { receiver.newPixels((int) 0, (int) 0, (int) 0, (int) 0); }
    static void call12(java.awt.image.MemoryImageSource receiver)  { receiver.newPixels((int) 0, (int) 0, (int) 0, (int) 0, false); }
    static void call13(java.awt.image.MemoryImageSource receiver)  { receiver.newPixels((byte[]) null, (java.awt.image.ColorModel) null, (int) 0, (int) 0); }
    static void call14(java.awt.image.MemoryImageSource receiver)  { receiver.newPixels((int[]) null, (java.awt.image.ColorModel) null, (int) 0, (int) 0); }
}

abstract class Override_java_awt_image_MemoryImageSource extends java.awt.image.MemoryImageSource {
    Override_java_awt_image_MemoryImageSource()  { super((int) 0, (int) 0, (java.awt.image.ColorModel) null, (byte[]) null, (int) 0, (int) 0); }
    @Override
    public void addConsumer(java.awt.image.ImageConsumer arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isConsumer(java.awt.image.ImageConsumer arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void removeConsumer(java.awt.image.ImageConsumer arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void startProduction(java.awt.image.ImageConsumer arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void requestTopDownLeftRightResend(java.awt.image.ImageConsumer arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setAnimated(boolean arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setFullBufferUpdates(boolean arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void newPixels()  { throw new UnsupportedOperationException(); }
    @Override
    public void newPixels(int arg0, int arg1, int arg2, int arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public void newPixels(int arg0, int arg1, int arg2, int arg3, boolean arg4)  { throw new UnsupportedOperationException(); }
    @Override
    public void newPixels(byte[] arg0, java.awt.image.ColorModel arg1, int arg2, int arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public void newPixels(int[] arg0, java.awt.image.ColorModel arg1, int arg2, int arg3)  { throw new UnsupportedOperationException(); }
}
