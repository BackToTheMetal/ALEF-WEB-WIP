package java.awt.image;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_image_PixelGrabber {
    static void call0()  { java.awt.image.PixelGrabber value = new java.awt.image.PixelGrabber((java.awt.Image) null, (int) 0, (int) 0, (int) 0, (int) 0, (int[]) null, (int) 0, (int) 0); }
    static void call1()  { java.awt.image.PixelGrabber value = new java.awt.image.PixelGrabber((java.awt.image.ImageProducer) null, (int) 0, (int) 0, (int) 0, (int) 0, (int[]) null, (int) 0, (int) 0); }
    static void call2()  { java.awt.image.PixelGrabber value = new java.awt.image.PixelGrabber((java.awt.Image) null, (int) 0, (int) 0, (int) 0, (int) 0, false); }
    static void call3(java.awt.image.PixelGrabber receiver)  { receiver.startGrabbing(); }
    static void call4(java.awt.image.PixelGrabber receiver)  { receiver.abortGrabbing(); }
    static void call5(java.awt.image.PixelGrabber receiver) throws java.lang.InterruptedException { try { boolean value = receiver.grabPixels(); } catch (java.lang.InterruptedException expected) { throw expected; } }
    static void call6(java.awt.image.PixelGrabber receiver) throws java.lang.InterruptedException { try { boolean value = receiver.grabPixels((long) 0); } catch (java.lang.InterruptedException expected) { throw expected; } }
    static void call7(java.awt.image.PixelGrabber receiver)  { int value = receiver.getStatus(); }
    static void call8(java.awt.image.PixelGrabber receiver)  { int value = receiver.getWidth(); }
    static void call9(java.awt.image.PixelGrabber receiver)  { int value = receiver.getHeight(); }
    static void call10(java.awt.image.PixelGrabber receiver)  { java.lang.Object value = receiver.getPixels(); }
    static void call11(java.awt.image.PixelGrabber receiver)  { java.awt.image.ColorModel value = receiver.getColorModel(); }
    static void call12(java.awt.image.PixelGrabber receiver)  { receiver.setDimensions((int) 0, (int) 0); }
    static void call13(java.awt.image.PixelGrabber receiver)  { receiver.setHints((int) 0); }
    static void call14(java.awt.image.PixelGrabber receiver)  { receiver.setColorModel((java.awt.image.ColorModel) null); }
    static void call15(java.awt.image.PixelGrabber receiver)  { receiver.setPixels((int) 0, (int) 0, (int) 0, (int) 0, (java.awt.image.ColorModel) null, (byte[]) null, (int) 0, (int) 0); }
    static void call16(java.awt.image.PixelGrabber receiver)  { receiver.setPixels((int) 0, (int) 0, (int) 0, (int) 0, (java.awt.image.ColorModel) null, (int[]) null, (int) 0, (int) 0); }
    static void call17(java.awt.image.PixelGrabber receiver)  { receiver.imageComplete((int) 0); }
    static void call18(java.awt.image.PixelGrabber receiver)  { int value = receiver.status(); }
}

abstract class Override_java_awt_image_PixelGrabber extends java.awt.image.PixelGrabber {
    Override_java_awt_image_PixelGrabber()  { super((java.awt.Image) null, (int) 0, (int) 0, (int) 0, (int) 0, (int[]) null, (int) 0, (int) 0); }
    @Override
    public void startGrabbing()  { throw new UnsupportedOperationException(); }
    @Override
    public void abortGrabbing()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean grabPixels() throws java.lang.InterruptedException { throw new UnsupportedOperationException(); }
    @Override
    public boolean grabPixels(long arg0) throws java.lang.InterruptedException { throw new UnsupportedOperationException(); }
    @Override
    public int getStatus()  { throw new UnsupportedOperationException(); }
    @Override
    public int getWidth()  { throw new UnsupportedOperationException(); }
    @Override
    public int getHeight()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Object getPixels()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.ColorModel getColorModel()  { throw new UnsupportedOperationException(); }
    @Override
    public void setDimensions(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void setHints(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setColorModel(java.awt.image.ColorModel arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setPixels(int arg0, int arg1, int arg2, int arg3, java.awt.image.ColorModel arg4, byte[] arg5, int arg6, int arg7)  { throw new UnsupportedOperationException(); }
    @Override
    public void setPixels(int arg0, int arg1, int arg2, int arg3, java.awt.image.ColorModel arg4, int[] arg5, int arg6, int arg7)  { throw new UnsupportedOperationException(); }
    @Override
    public void imageComplete(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int status()  { throw new UnsupportedOperationException(); }
}
