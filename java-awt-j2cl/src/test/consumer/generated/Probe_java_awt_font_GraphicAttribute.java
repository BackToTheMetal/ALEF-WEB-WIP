package java.awt.font;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_font_GraphicAttribute {
    static void call0() { int value = java.awt.font.GraphicAttribute.TOP_ALIGNMENT; }
    static void call1() { int value = java.awt.font.GraphicAttribute.BOTTOM_ALIGNMENT; }
    static void call2() { int value = java.awt.font.GraphicAttribute.ROMAN_BASELINE; }
    static void call3() { int value = java.awt.font.GraphicAttribute.CENTER_BASELINE; }
    static void call4() { int value = java.awt.font.GraphicAttribute.HANGING_BASELINE; }
    static void call5(java.awt.font.GraphicAttribute receiver)  { float value = receiver.getAscent(); }
    static void call6(java.awt.font.GraphicAttribute receiver)  { float value = receiver.getDescent(); }
    static void call7(java.awt.font.GraphicAttribute receiver)  { float value = receiver.getAdvance(); }
    static void call8(java.awt.font.GraphicAttribute receiver)  { java.awt.geom.Rectangle2D value = receiver.getBounds(); }
    static void call9(java.awt.font.GraphicAttribute receiver)  { java.awt.Shape value = receiver.getOutline((java.awt.geom.AffineTransform) null); }
    static void call10(java.awt.font.GraphicAttribute receiver)  { receiver.draw((java.awt.Graphics2D) null, (float) 0, (float) 0); }
    static void call11(java.awt.font.GraphicAttribute receiver)  { int value = receiver.getAlignment(); }
    static void call12(java.awt.font.GraphicAttribute receiver)  { java.awt.font.GlyphJustificationInfo value = receiver.getJustificationInfo(); }
}

abstract class Override_java_awt_font_GraphicAttribute extends java.awt.font.GraphicAttribute {
    Override_java_awt_font_GraphicAttribute()  { super((int) 0); }
    @Override
    public float getAscent()  { throw new UnsupportedOperationException(); }
    @Override
    public float getDescent()  { throw new UnsupportedOperationException(); }
    @Override
    public float getAdvance()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.Rectangle2D getBounds()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Shape getOutline(java.awt.geom.AffineTransform arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void draw(java.awt.Graphics2D arg0, float arg1, float arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.font.GlyphJustificationInfo getJustificationInfo()  { throw new UnsupportedOperationException(); }
}
