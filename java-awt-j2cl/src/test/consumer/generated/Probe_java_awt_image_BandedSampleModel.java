package java.awt.image;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_image_BandedSampleModel {
    static void call0()  { java.awt.image.BandedSampleModel value = new java.awt.image.BandedSampleModel((int) 0, (int) 0, (int) 0, (int) 0); }
    static void call1()  { java.awt.image.BandedSampleModel value = new java.awt.image.BandedSampleModel((int) 0, (int) 0, (int) 0, (int) 0, (int[]) null, (int[]) null); }
    static void call2(java.awt.image.BandedSampleModel receiver)  { java.awt.image.SampleModel value = receiver.createCompatibleSampleModel((int) 0, (int) 0); }
    static void call3(java.awt.image.BandedSampleModel receiver)  { java.awt.image.SampleModel value = receiver.createSubsetSampleModel((int[]) null); }
    static void call4(java.awt.image.BandedSampleModel receiver)  { java.awt.image.DataBuffer value = receiver.createDataBuffer(); }
    static void call5(java.awt.image.BandedSampleModel receiver)  { java.lang.Object value = receiver.getDataElements((int) 0, (int) 0, (java.lang.Object) null, (java.awt.image.DataBuffer) null); }
    static void call6(java.awt.image.BandedSampleModel receiver)  { int[] value = receiver.getPixel((int) 0, (int) 0, (int[]) null, (java.awt.image.DataBuffer) null); }
    static void call7(java.awt.image.BandedSampleModel receiver)  { int[] value = receiver.getPixels((int) 0, (int) 0, (int) 0, (int) 0, (int[]) null, (java.awt.image.DataBuffer) null); }
    static void call8(java.awt.image.BandedSampleModel receiver)  { int value = receiver.getSample((int) 0, (int) 0, (int) 0, (java.awt.image.DataBuffer) null); }
    static void call9(java.awt.image.BandedSampleModel receiver)  { float value = receiver.getSampleFloat((int) 0, (int) 0, (int) 0, (java.awt.image.DataBuffer) null); }
    static void call10(java.awt.image.BandedSampleModel receiver)  { double value = receiver.getSampleDouble((int) 0, (int) 0, (int) 0, (java.awt.image.DataBuffer) null); }
    static void call11(java.awt.image.BandedSampleModel receiver)  { int[] value = receiver.getSamples((int) 0, (int) 0, (int) 0, (int) 0, (int) 0, (int[]) null, (java.awt.image.DataBuffer) null); }
    static void call12(java.awt.image.BandedSampleModel receiver)  { receiver.setDataElements((int) 0, (int) 0, (java.lang.Object) null, (java.awt.image.DataBuffer) null); }
    static void call13(java.awt.image.BandedSampleModel receiver)  { receiver.setPixel((int) 0, (int) 0, (int[]) null, (java.awt.image.DataBuffer) null); }
    static void call14(java.awt.image.BandedSampleModel receiver)  { receiver.setPixels((int) 0, (int) 0, (int) 0, (int) 0, (int[]) null, (java.awt.image.DataBuffer) null); }
    static void call15(java.awt.image.BandedSampleModel receiver)  { receiver.setSample((int) 0, (int) 0, (int) 0, (int) 0, (java.awt.image.DataBuffer) null); }
    static void call16(java.awt.image.BandedSampleModel receiver)  { receiver.setSample((int) 0, (int) 0, (int) 0, (float) 0, (java.awt.image.DataBuffer) null); }
    static void call17(java.awt.image.BandedSampleModel receiver)  { receiver.setSample((int) 0, (int) 0, (int) 0, (double) 0, (java.awt.image.DataBuffer) null); }
    static void call18(java.awt.image.BandedSampleModel receiver)  { receiver.setSamples((int) 0, (int) 0, (int) 0, (int) 0, (int) 0, (int[]) null, (java.awt.image.DataBuffer) null); }
    static void call19(java.awt.image.BandedSampleModel receiver)  { int value = receiver.hashCode(); }
}
