package java.awt.image;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_image_Kernel {
    static void call0()  { java.awt.image.Kernel value = new java.awt.image.Kernel((int) 0, (int) 0, (float[]) null); }
    static void call1(java.awt.image.Kernel receiver)  { int value = receiver.getXOrigin(); }
    static void call2(java.awt.image.Kernel receiver)  { int value = receiver.getYOrigin(); }
    static void call3(java.awt.image.Kernel receiver)  { int value = receiver.getWidth(); }
    static void call4(java.awt.image.Kernel receiver)  { int value = receiver.getHeight(); }
    static void call5(java.awt.image.Kernel receiver)  { float[] value = receiver.getKernelData((float[]) null); }
    static void call6(java.awt.image.Kernel receiver)  { java.lang.Object value = receiver.clone(); }
}

abstract class Override_java_awt_image_Kernel extends java.awt.image.Kernel {
    Override_java_awt_image_Kernel()  { super((int) 0, (int) 0, (float[]) null); }
    @Override
    public java.lang.Object clone()  { throw new UnsupportedOperationException(); }
}
