package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_Color {
    static void call0() { java.awt.Color value = java.awt.Color.white; }
    static void call1() { java.awt.Color value = java.awt.Color.WHITE; }
    static void call2() { java.awt.Color value = java.awt.Color.lightGray; }
    static void call3() { java.awt.Color value = java.awt.Color.LIGHT_GRAY; }
    static void call4() { java.awt.Color value = java.awt.Color.gray; }
    static void call5() { java.awt.Color value = java.awt.Color.GRAY; }
    static void call6() { java.awt.Color value = java.awt.Color.darkGray; }
    static void call7() { java.awt.Color value = java.awt.Color.DARK_GRAY; }
    static void call8() { java.awt.Color value = java.awt.Color.black; }
    static void call9() { java.awt.Color value = java.awt.Color.BLACK; }
    static void call10() { java.awt.Color value = java.awt.Color.red; }
    static void call11() { java.awt.Color value = java.awt.Color.RED; }
    static void call12() { java.awt.Color value = java.awt.Color.pink; }
    static void call13() { java.awt.Color value = java.awt.Color.PINK; }
    static void call14() { java.awt.Color value = java.awt.Color.orange; }
    static void call15() { java.awt.Color value = java.awt.Color.ORANGE; }
    static void call16() { java.awt.Color value = java.awt.Color.yellow; }
    static void call17() { java.awt.Color value = java.awt.Color.YELLOW; }
    static void call18() { java.awt.Color value = java.awt.Color.green; }
    static void call19() { java.awt.Color value = java.awt.Color.GREEN; }
    static void call20() { java.awt.Color value = java.awt.Color.magenta; }
    static void call21() { java.awt.Color value = java.awt.Color.MAGENTA; }
    static void call22() { java.awt.Color value = java.awt.Color.cyan; }
    static void call23() { java.awt.Color value = java.awt.Color.CYAN; }
    static void call24() { java.awt.Color value = java.awt.Color.blue; }
    static void call25() { java.awt.Color value = java.awt.Color.BLUE; }
    static void call26()  { java.awt.Color value = new java.awt.Color((int) 0, (int) 0, (int) 0); }
    static void call27()  { java.awt.Color value = new java.awt.Color((int) 0, (int) 0, (int) 0, (int) 0); }
    static void call28()  { java.awt.Color value = new java.awt.Color((int) 0); }
    static void call29()  { java.awt.Color value = new java.awt.Color((int) 0, false); }
    static void call30()  { java.awt.Color value = new java.awt.Color((float) 0, (float) 0, (float) 0); }
    static void call31()  { java.awt.Color value = new java.awt.Color((float) 0, (float) 0, (float) 0, (float) 0); }
    static void call32()  { java.awt.Color value = new java.awt.Color((java.awt.color.ColorSpace) null, (float[]) null, (float) 0); }
    static void call33(java.awt.Color receiver)  { int value = receiver.getRed(); }
    static void call34(java.awt.Color receiver)  { int value = receiver.getGreen(); }
    static void call35(java.awt.Color receiver)  { int value = receiver.getBlue(); }
    static void call36(java.awt.Color receiver)  { int value = receiver.getAlpha(); }
    static void call37(java.awt.Color receiver)  { int value = receiver.getRGB(); }
    static void call38(java.awt.Color receiver)  { java.awt.Color value = receiver.brighter(); }
    static void call39(java.awt.Color receiver)  { java.awt.Color value = receiver.darker(); }
    static void call40(java.awt.Color receiver)  { int value = receiver.hashCode(); }
    static void call41(java.awt.Color receiver)  { boolean value = receiver.equals((java.lang.Object) null); }
    static void call42(java.awt.Color receiver)  { java.lang.String value = receiver.toString(); }
    static void call43() throws java.lang.NumberFormatException { try { java.awt.Color value = java.awt.Color.decode((java.lang.String) null); } catch (java.lang.NumberFormatException expected) { throw expected; } }
    static void call44()  { java.awt.Color value = java.awt.Color.getColor((java.lang.String) null); }
    static void call45()  { java.awt.Color value = java.awt.Color.getColor((java.lang.String) null, (java.awt.Color) null); }
    static void call46()  { java.awt.Color value = java.awt.Color.getColor((java.lang.String) null, (int) 0); }
    static void call47()  { int value = java.awt.Color.HSBtoRGB((float) 0, (float) 0, (float) 0); }
    static void call48()  { float[] value = java.awt.Color.RGBtoHSB((int) 0, (int) 0, (int) 0, (float[]) null); }
    static void call49()  { java.awt.Color value = java.awt.Color.getHSBColor((float) 0, (float) 0, (float) 0); }
    static void call50(java.awt.Color receiver)  { float[] value = receiver.getRGBComponents((float[]) null); }
    static void call51(java.awt.Color receiver)  { float[] value = receiver.getRGBColorComponents((float[]) null); }
    static void call52(java.awt.Color receiver)  { float[] value = receiver.getComponents((float[]) null); }
    static void call53(java.awt.Color receiver)  { float[] value = receiver.getColorComponents((float[]) null); }
    static void call54(java.awt.Color receiver)  { float[] value = receiver.getComponents((java.awt.color.ColorSpace) null, (float[]) null); }
    static void call55(java.awt.Color receiver)  { float[] value = receiver.getColorComponents((java.awt.color.ColorSpace) null, (float[]) null); }
    static void call56(java.awt.Color receiver)  { java.awt.color.ColorSpace value = receiver.getColorSpace(); }
    static void call57(java.awt.Color receiver)  { java.awt.PaintContext value = receiver.createContext((java.awt.image.ColorModel) null, (java.awt.Rectangle) null, (java.awt.geom.Rectangle2D) null, (java.awt.geom.AffineTransform) null, (java.awt.RenderingHints) null); }
    static void call58(java.awt.Color receiver)  { int value = receiver.getTransparency(); }
}

abstract class Override_java_awt_Color extends java.awt.Color {
    Override_java_awt_Color()  { super((int) 0, (int) 0, (int) 0); }
    @Override
    public int getRed()  { throw new UnsupportedOperationException(); }
    @Override
    public int getGreen()  { throw new UnsupportedOperationException(); }
    @Override
    public int getBlue()  { throw new UnsupportedOperationException(); }
    @Override
    public int getAlpha()  { throw new UnsupportedOperationException(); }
    @Override
    public int getRGB()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Color brighter()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Color darker()  { throw new UnsupportedOperationException(); }
    @Override
    public int hashCode()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean equals(java.lang.Object arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String toString()  { throw new UnsupportedOperationException(); }
    @Override
    public float[] getRGBComponents(float[] arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public float[] getRGBColorComponents(float[] arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public float[] getComponents(float[] arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public float[] getColorComponents(float[] arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public float[] getComponents(java.awt.color.ColorSpace arg0, float[] arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public float[] getColorComponents(java.awt.color.ColorSpace arg0, float[] arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.color.ColorSpace getColorSpace()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.PaintContext createContext(java.awt.image.ColorModel arg0, java.awt.Rectangle arg1, java.awt.geom.Rectangle2D arg2, java.awt.geom.AffineTransform arg3, java.awt.RenderingHints arg4)  { throw new UnsupportedOperationException(); }
    @Override
    public int getTransparency()  { throw new UnsupportedOperationException(); }
}
