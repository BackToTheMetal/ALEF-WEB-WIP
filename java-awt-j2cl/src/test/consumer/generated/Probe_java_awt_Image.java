package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_Image {
    static void call0(java.awt.Image receiver) { float value = receiver.accelerationPriority; receiver.accelerationPriority = value; }
    static void call1() { java.lang.Object value = java.awt.Image.UndefinedProperty; }
    static void call2() { int value = java.awt.Image.SCALE_DEFAULT; }
    static void call3() { int value = java.awt.Image.SCALE_FAST; }
    static void call4() { int value = java.awt.Image.SCALE_SMOOTH; }
    static void call5() { int value = java.awt.Image.SCALE_REPLICATE; }
    static void call6() { int value = java.awt.Image.SCALE_AREA_AVERAGING; }
    static void call7(java.awt.Image receiver)  { int value = receiver.getWidth((java.awt.image.ImageObserver) null); }
    static void call8(java.awt.Image receiver)  { int value = receiver.getHeight((java.awt.image.ImageObserver) null); }
    static void call9(java.awt.Image receiver)  { java.awt.image.ImageProducer value = receiver.getSource(); }
    static void call10(java.awt.Image receiver)  { java.awt.Graphics value = receiver.getGraphics(); }
    static void call11(java.awt.Image receiver)  { java.lang.Object value = receiver.getProperty((java.lang.String) null, (java.awt.image.ImageObserver) null); }
    static void call12(java.awt.Image receiver)  { java.awt.Image value = receiver.getScaledInstance((int) 0, (int) 0, (int) 0); }
    static void call13(java.awt.Image receiver)  { receiver.flush(); }
    static void call14(java.awt.Image receiver)  { java.awt.ImageCapabilities value = receiver.getCapabilities((java.awt.GraphicsConfiguration) null); }
    static void call15(java.awt.Image receiver)  { receiver.setAccelerationPriority((float) 0); }
    static void call16(java.awt.Image receiver)  { float value = receiver.getAccelerationPriority(); }
}

abstract class Override_java_awt_Image extends java.awt.Image {
    Override_java_awt_Image()  { super(); }
    @Override
    public int getWidth(java.awt.image.ImageObserver arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int getHeight(java.awt.image.ImageObserver arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.ImageProducer getSource()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Graphics getGraphics()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Object getProperty(java.lang.String arg0, java.awt.image.ImageObserver arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Image getScaledInstance(int arg0, int arg1, int arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public void flush()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.ImageCapabilities getCapabilities(java.awt.GraphicsConfiguration arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setAccelerationPriority(float arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public float getAccelerationPriority()  { throw new UnsupportedOperationException(); }
}
