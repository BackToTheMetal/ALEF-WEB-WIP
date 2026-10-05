package java.awt.color;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_color_ICC_ColorSpace {
    static void call0()  { java.awt.color.ICC_ColorSpace value = new java.awt.color.ICC_ColorSpace((java.awt.color.ICC_Profile) null); }
    static void call1(java.awt.color.ICC_ColorSpace receiver)  { java.awt.color.ICC_Profile value = receiver.getProfile(); }
    static void call2(java.awt.color.ICC_ColorSpace receiver)  { float[] value = receiver.toRGB((float[]) null); }
    static void call3(java.awt.color.ICC_ColorSpace receiver)  { float[] value = receiver.fromRGB((float[]) null); }
    static void call4(java.awt.color.ICC_ColorSpace receiver)  { float[] value = receiver.toCIEXYZ((float[]) null); }
    static void call5(java.awt.color.ICC_ColorSpace receiver)  { float[] value = receiver.fromCIEXYZ((float[]) null); }
    static void call6(java.awt.color.ICC_ColorSpace receiver)  { float value = receiver.getMinValue((int) 0); }
    static void call7(java.awt.color.ICC_ColorSpace receiver)  { float value = receiver.getMaxValue((int) 0); }
}

abstract class Override_java_awt_color_ICC_ColorSpace extends java.awt.color.ICC_ColorSpace {
    Override_java_awt_color_ICC_ColorSpace()  { super((java.awt.color.ICC_Profile) null); }
    @Override
    public java.awt.color.ICC_Profile getProfile()  { throw new UnsupportedOperationException(); }
    @Override
    public float[] toRGB(float[] arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public float[] fromRGB(float[] arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public float[] toCIEXYZ(float[] arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public float[] fromCIEXYZ(float[] arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public float getMinValue(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public float getMaxValue(int arg0)  { throw new UnsupportedOperationException(); }
}
