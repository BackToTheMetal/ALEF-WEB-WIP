package java.awt.font;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_font_GlyphVector {
    static void call0() { int value = java.awt.font.GlyphVector.FLAG_HAS_TRANSFORMS; }
    static void call1() { int value = java.awt.font.GlyphVector.FLAG_HAS_POSITION_ADJUSTMENTS; }
    static void call2() { int value = java.awt.font.GlyphVector.FLAG_RUN_RTL; }
    static void call3() { int value = java.awt.font.GlyphVector.FLAG_COMPLEX_GLYPHS; }
    static void call4() { int value = java.awt.font.GlyphVector.FLAG_MASK; }
    static void call5(java.awt.font.GlyphVector receiver)  { java.awt.Font value = receiver.getFont(); }
    static void call6(java.awt.font.GlyphVector receiver)  { java.awt.font.FontRenderContext value = receiver.getFontRenderContext(); }
    static void call7(java.awt.font.GlyphVector receiver)  { receiver.performDefaultLayout(); }
    static void call8(java.awt.font.GlyphVector receiver)  { int value = receiver.getNumGlyphs(); }
    static void call9(java.awt.font.GlyphVector receiver)  { int value = receiver.getGlyphCode((int) 0); }
    static void call10(java.awt.font.GlyphVector receiver)  { int[] value = receiver.getGlyphCodes((int) 0, (int) 0, (int[]) null); }
    static void call11(java.awt.font.GlyphVector receiver)  { int value = receiver.getGlyphCharIndex((int) 0); }
    static void call12(java.awt.font.GlyphVector receiver)  { int[] value = receiver.getGlyphCharIndices((int) 0, (int) 0, (int[]) null); }
    static void call13(java.awt.font.GlyphVector receiver)  { java.awt.geom.Rectangle2D value = receiver.getLogicalBounds(); }
    static void call14(java.awt.font.GlyphVector receiver)  { java.awt.geom.Rectangle2D value = receiver.getVisualBounds(); }
    static void call15(java.awt.font.GlyphVector receiver)  { java.awt.Rectangle value = receiver.getPixelBounds((java.awt.font.FontRenderContext) null, (float) 0, (float) 0); }
    static void call16(java.awt.font.GlyphVector receiver)  { java.awt.Shape value = receiver.getOutline(); }
    static void call17(java.awt.font.GlyphVector receiver)  { java.awt.Shape value = receiver.getOutline((float) 0, (float) 0); }
    static void call18(java.awt.font.GlyphVector receiver)  { java.awt.Shape value = receiver.getGlyphOutline((int) 0); }
    static void call19(java.awt.font.GlyphVector receiver)  { java.awt.Shape value = receiver.getGlyphOutline((int) 0, (float) 0, (float) 0); }
    static void call20(java.awt.font.GlyphVector receiver)  { java.awt.geom.Point2D value = receiver.getGlyphPosition((int) 0); }
    static void call21(java.awt.font.GlyphVector receiver)  { receiver.setGlyphPosition((int) 0, (java.awt.geom.Point2D) null); }
    static void call22(java.awt.font.GlyphVector receiver)  { java.awt.geom.AffineTransform value = receiver.getGlyphTransform((int) 0); }
    static void call23(java.awt.font.GlyphVector receiver)  { receiver.setGlyphTransform((int) 0, (java.awt.geom.AffineTransform) null); }
    static void call24(java.awt.font.GlyphVector receiver)  { int value = receiver.getLayoutFlags(); }
    static void call25(java.awt.font.GlyphVector receiver)  { float[] value = receiver.getGlyphPositions((int) 0, (int) 0, (float[]) null); }
    static void call26(java.awt.font.GlyphVector receiver)  { java.awt.Shape value = receiver.getGlyphLogicalBounds((int) 0); }
    static void call27(java.awt.font.GlyphVector receiver)  { java.awt.Shape value = receiver.getGlyphVisualBounds((int) 0); }
    static void call28(java.awt.font.GlyphVector receiver)  { java.awt.Rectangle value = receiver.getGlyphPixelBounds((int) 0, (java.awt.font.FontRenderContext) null, (float) 0, (float) 0); }
    static void call29(java.awt.font.GlyphVector receiver)  { java.awt.font.GlyphMetrics value = receiver.getGlyphMetrics((int) 0); }
    static void call30(java.awt.font.GlyphVector receiver)  { java.awt.font.GlyphJustificationInfo value = receiver.getGlyphJustificationInfo((int) 0); }
    static void call31(java.awt.font.GlyphVector receiver)  { boolean value = receiver.equals((java.awt.font.GlyphVector) null); }
}

abstract class Override_java_awt_font_GlyphVector extends java.awt.font.GlyphVector {
    Override_java_awt_font_GlyphVector()  { super(); }
    @Override
    public java.awt.Font getFont()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.font.FontRenderContext getFontRenderContext()  { throw new UnsupportedOperationException(); }
    @Override
    public void performDefaultLayout()  { throw new UnsupportedOperationException(); }
    @Override
    public int getNumGlyphs()  { throw new UnsupportedOperationException(); }
    @Override
    public int getGlyphCode(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int[] getGlyphCodes(int arg0, int arg1, int[] arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public int getGlyphCharIndex(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int[] getGlyphCharIndices(int arg0, int arg1, int[] arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.Rectangle2D getLogicalBounds()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.Rectangle2D getVisualBounds()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Rectangle getPixelBounds(java.awt.font.FontRenderContext arg0, float arg1, float arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Shape getOutline()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Shape getOutline(float arg0, float arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Shape getGlyphOutline(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Shape getGlyphOutline(int arg0, float arg1, float arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.Point2D getGlyphPosition(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setGlyphPosition(int arg0, java.awt.geom.Point2D arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.AffineTransform getGlyphTransform(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setGlyphTransform(int arg0, java.awt.geom.AffineTransform arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public int getLayoutFlags()  { throw new UnsupportedOperationException(); }
    @Override
    public float[] getGlyphPositions(int arg0, int arg1, float[] arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Shape getGlyphLogicalBounds(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Shape getGlyphVisualBounds(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Rectangle getGlyphPixelBounds(int arg0, java.awt.font.FontRenderContext arg1, float arg2, float arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.font.GlyphMetrics getGlyphMetrics(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.font.GlyphJustificationInfo getGlyphJustificationInfo(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean equals(java.awt.font.GlyphVector arg0)  { throw new UnsupportedOperationException(); }
}
