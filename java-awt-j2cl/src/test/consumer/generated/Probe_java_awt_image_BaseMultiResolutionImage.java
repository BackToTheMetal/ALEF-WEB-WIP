package java.awt.image;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_image_BaseMultiResolutionImage {
    static void call0()  { java.awt.image.BaseMultiResolutionImage value = new java.awt.image.BaseMultiResolutionImage((java.awt.Image[]) null); }
    static void call1()  { java.awt.image.BaseMultiResolutionImage value = new java.awt.image.BaseMultiResolutionImage((int) 0, (java.awt.Image[]) null); }
    static void call2(java.awt.image.BaseMultiResolutionImage receiver)  { java.awt.Image value = receiver.getResolutionVariant((double) 0, (double) 0); }
    static void call3(java.awt.image.BaseMultiResolutionImage receiver)  { java.util.List value = receiver.getResolutionVariants(); }
    static void call4(java.awt.image.BaseMultiResolutionImage receiver)  { java.awt.Image value = receiver.getBaseImage(); }
}

abstract class Override_java_awt_image_BaseMultiResolutionImage extends java.awt.image.BaseMultiResolutionImage {
    Override_java_awt_image_BaseMultiResolutionImage()  { super((java.awt.Image[]) null); }
    @Override
    public java.awt.Image getResolutionVariant(double arg0, double arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.util.List<java.awt.Image> getResolutionVariants()  { throw new UnsupportedOperationException(); }
    @Override
    protected java.awt.Image getBaseImage()  { throw new UnsupportedOperationException(); }
}
