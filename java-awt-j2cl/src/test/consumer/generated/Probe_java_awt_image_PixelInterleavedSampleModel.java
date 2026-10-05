package java.awt.image;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_image_PixelInterleavedSampleModel {
    static void call0()  { java.awt.image.PixelInterleavedSampleModel value = new java.awt.image.PixelInterleavedSampleModel((int) 0, (int) 0, (int) 0, (int) 0, (int) 0, (int[]) null); }
    static void call1(java.awt.image.PixelInterleavedSampleModel receiver)  { java.awt.image.SampleModel value = receiver.createCompatibleSampleModel((int) 0, (int) 0); }
    static void call2(java.awt.image.PixelInterleavedSampleModel receiver)  { java.awt.image.SampleModel value = receiver.createSubsetSampleModel((int[]) null); }
    static void call3(java.awt.image.PixelInterleavedSampleModel receiver)  { int value = receiver.hashCode(); }
}

abstract class Override_java_awt_image_PixelInterleavedSampleModel extends java.awt.image.PixelInterleavedSampleModel {
    Override_java_awt_image_PixelInterleavedSampleModel()  { super((int) 0, (int) 0, (int) 0, (int) 0, (int) 0, (int[]) null); }
    @Override
    public java.awt.image.SampleModel createCompatibleSampleModel(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.SampleModel createSubsetSampleModel(int[] arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int hashCode()  { throw new UnsupportedOperationException(); }
}
