package java.awt.color;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_color_ColorSpace {
    static void call0() { int value = java.awt.color.ColorSpace.TYPE_XYZ; }
    static void call1() { int value = java.awt.color.ColorSpace.TYPE_Lab; }
    static void call2() { int value = java.awt.color.ColorSpace.TYPE_Luv; }
    static void call3() { int value = java.awt.color.ColorSpace.TYPE_YCbCr; }
    static void call4() { int value = java.awt.color.ColorSpace.TYPE_Yxy; }
    static void call5() { int value = java.awt.color.ColorSpace.TYPE_RGB; }
    static void call6() { int value = java.awt.color.ColorSpace.TYPE_GRAY; }
    static void call7() { int value = java.awt.color.ColorSpace.TYPE_HSV; }
    static void call8() { int value = java.awt.color.ColorSpace.TYPE_HLS; }
    static void call9() { int value = java.awt.color.ColorSpace.TYPE_CMYK; }
    static void call10() { int value = java.awt.color.ColorSpace.TYPE_CMY; }
    static void call11() { int value = java.awt.color.ColorSpace.TYPE_2CLR; }
    static void call12() { int value = java.awt.color.ColorSpace.TYPE_3CLR; }
    static void call13() { int value = java.awt.color.ColorSpace.TYPE_4CLR; }
    static void call14() { int value = java.awt.color.ColorSpace.TYPE_5CLR; }
    static void call15() { int value = java.awt.color.ColorSpace.TYPE_6CLR; }
    static void call16() { int value = java.awt.color.ColorSpace.TYPE_7CLR; }
    static void call17() { int value = java.awt.color.ColorSpace.TYPE_8CLR; }
    static void call18() { int value = java.awt.color.ColorSpace.TYPE_9CLR; }
    static void call19() { int value = java.awt.color.ColorSpace.TYPE_ACLR; }
    static void call20() { int value = java.awt.color.ColorSpace.TYPE_BCLR; }
    static void call21() { int value = java.awt.color.ColorSpace.TYPE_CCLR; }
    static void call22() { int value = java.awt.color.ColorSpace.TYPE_DCLR; }
    static void call23() { int value = java.awt.color.ColorSpace.TYPE_ECLR; }
    static void call24() { int value = java.awt.color.ColorSpace.TYPE_FCLR; }
    static void call25() { int value = java.awt.color.ColorSpace.CS_sRGB; }
    static void call26() { int value = java.awt.color.ColorSpace.CS_LINEAR_RGB; }
    static void call27() { int value = java.awt.color.ColorSpace.CS_CIEXYZ; }
    static void call28() { int value = java.awt.color.ColorSpace.CS_PYCC; }
    static void call29() { int value = java.awt.color.ColorSpace.CS_GRAY; }
    static void call30()  { java.awt.color.ColorSpace value = java.awt.color.ColorSpace.getInstance((int) 0); }
    static void call31(java.awt.color.ColorSpace receiver)  { boolean value = receiver.isCS_sRGB(); }
    static void call32(java.awt.color.ColorSpace receiver)  { float[] value = receiver.toRGB((float[]) null); }
    static void call33(java.awt.color.ColorSpace receiver)  { float[] value = receiver.fromRGB((float[]) null); }
    static void call34(java.awt.color.ColorSpace receiver)  { float[] value = receiver.toCIEXYZ((float[]) null); }
    static void call35(java.awt.color.ColorSpace receiver)  { float[] value = receiver.fromCIEXYZ((float[]) null); }
    static void call36(java.awt.color.ColorSpace receiver)  { int value = receiver.getType(); }
    static void call37(java.awt.color.ColorSpace receiver)  { int value = receiver.getNumComponents(); }
    static void call38(java.awt.color.ColorSpace receiver)  { java.lang.String value = receiver.getName((int) 0); }
    static void call39(java.awt.color.ColorSpace receiver)  { float value = receiver.getMinValue((int) 0); }
    static void call40(java.awt.color.ColorSpace receiver)  { float value = receiver.getMaxValue((int) 0); }
}

abstract class Override_java_awt_color_ColorSpace extends java.awt.color.ColorSpace {
    Override_java_awt_color_ColorSpace()  { super((int) 0, (int) 0); }
    @Override
    public boolean isCS_sRGB()  { throw new UnsupportedOperationException(); }
    @Override
    public float[] toRGB(float[] arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public float[] fromRGB(float[] arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public float[] toCIEXYZ(float[] arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public float[] fromCIEXYZ(float[] arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int getType()  { throw new UnsupportedOperationException(); }
    @Override
    public int getNumComponents()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String getName(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public float getMinValue(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public float getMaxValue(int arg0)  { throw new UnsupportedOperationException(); }
}
