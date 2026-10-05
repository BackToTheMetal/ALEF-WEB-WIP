package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_GraphicsConfiguration {
    static void call0(java.awt.GraphicsConfiguration receiver)  { java.awt.GraphicsDevice value = receiver.getDevice(); }
    static void call1(java.awt.GraphicsConfiguration receiver)  { java.awt.image.BufferedImage value = receiver.createCompatibleImage((int) 0, (int) 0); }
    static void call2(java.awt.GraphicsConfiguration receiver)  { java.awt.image.BufferedImage value = receiver.createCompatibleImage((int) 0, (int) 0, (int) 0); }
    static void call3(java.awt.GraphicsConfiguration receiver)  { java.awt.image.VolatileImage value = receiver.createCompatibleVolatileImage((int) 0, (int) 0); }
    static void call4(java.awt.GraphicsConfiguration receiver)  { java.awt.image.VolatileImage value = receiver.createCompatibleVolatileImage((int) 0, (int) 0, (int) 0); }
    static void call5(java.awt.GraphicsConfiguration receiver) throws java.awt.AWTException { try { java.awt.image.VolatileImage value = receiver.createCompatibleVolatileImage((int) 0, (int) 0, (java.awt.ImageCapabilities) null); } catch (java.awt.AWTException expected) { throw expected; } }
    static void call6(java.awt.GraphicsConfiguration receiver) throws java.awt.AWTException { try { java.awt.image.VolatileImage value = receiver.createCompatibleVolatileImage((int) 0, (int) 0, (java.awt.ImageCapabilities) null, (int) 0); } catch (java.awt.AWTException expected) { throw expected; } }
    static void call7(java.awt.GraphicsConfiguration receiver)  { java.awt.image.ColorModel value = receiver.getColorModel(); }
    static void call8(java.awt.GraphicsConfiguration receiver)  { java.awt.image.ColorModel value = receiver.getColorModel((int) 0); }
    static void call9(java.awt.GraphicsConfiguration receiver)  { java.awt.geom.AffineTransform value = receiver.getDefaultTransform(); }
    static void call10(java.awt.GraphicsConfiguration receiver)  { java.awt.geom.AffineTransform value = receiver.getNormalizingTransform(); }
    static void call11(java.awt.GraphicsConfiguration receiver)  { java.awt.Rectangle value = receiver.getBounds(); }
    static void call12(java.awt.GraphicsConfiguration receiver)  { java.awt.BufferCapabilities value = receiver.getBufferCapabilities(); }
    static void call13(java.awt.GraphicsConfiguration receiver)  { java.awt.ImageCapabilities value = receiver.getImageCapabilities(); }
    static void call14(java.awt.GraphicsConfiguration receiver)  { boolean value = receiver.isTranslucencyCapable(); }
}

abstract class Override_java_awt_GraphicsConfiguration extends java.awt.GraphicsConfiguration {
    Override_java_awt_GraphicsConfiguration()  { super(); }
    @Override
    public java.awt.GraphicsDevice getDevice()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.BufferedImage createCompatibleImage(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.BufferedImage createCompatibleImage(int arg0, int arg1, int arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.VolatileImage createCompatibleVolatileImage(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.VolatileImage createCompatibleVolatileImage(int arg0, int arg1, int arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.VolatileImage createCompatibleVolatileImage(int arg0, int arg1, java.awt.ImageCapabilities arg2) throws java.awt.AWTException { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.VolatileImage createCompatibleVolatileImage(int arg0, int arg1, java.awt.ImageCapabilities arg2, int arg3) throws java.awt.AWTException { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.ColorModel getColorModel()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.ColorModel getColorModel(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.AffineTransform getDefaultTransform()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.AffineTransform getNormalizingTransform()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Rectangle getBounds()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.BufferCapabilities getBufferCapabilities()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.ImageCapabilities getImageCapabilities()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isTranslucencyCapable()  { throw new UnsupportedOperationException(); }
}
