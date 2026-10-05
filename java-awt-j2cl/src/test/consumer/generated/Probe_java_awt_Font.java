package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_Font {
    static void call0() { java.lang.String value = java.awt.Font.DIALOG; }
    static void call1() { java.lang.String value = java.awt.Font.DIALOG_INPUT; }
    static void call2() { java.lang.String value = java.awt.Font.SANS_SERIF; }
    static void call3() { java.lang.String value = java.awt.Font.SERIF; }
    static void call4() { java.lang.String value = java.awt.Font.MONOSPACED; }
    static void call5() { int value = java.awt.Font.PLAIN; }
    static void call6() { int value = java.awt.Font.BOLD; }
    static void call7() { int value = java.awt.Font.ITALIC; }
    static void call8() { int value = java.awt.Font.ROMAN_BASELINE; }
    static void call9() { int value = java.awt.Font.CENTER_BASELINE; }
    static void call10() { int value = java.awt.Font.HANGING_BASELINE; }
    static void call11() { int value = java.awt.Font.TRUETYPE_FONT; }
    static void call12() { int value = java.awt.Font.TYPE1_FONT; }
    static void call13(java.awt.Font receiver) { java.lang.String value = receiver.name; receiver.name = value; }
    static void call14(java.awt.Font receiver) { int value = receiver.style; receiver.style = value; }
    static void call15(java.awt.Font receiver) { int value = receiver.size; receiver.size = value; }
    static void call16(java.awt.Font receiver) { float value = receiver.pointSize; receiver.pointSize = value; }
    static void call17() { int value = java.awt.Font.LAYOUT_LEFT_TO_RIGHT; }
    static void call18() { int value = java.awt.Font.LAYOUT_RIGHT_TO_LEFT; }
    static void call19() { int value = java.awt.Font.LAYOUT_NO_START_CONTEXT; }
    static void call20() { int value = java.awt.Font.LAYOUT_NO_LIMIT_CONTEXT; }
    static void call21()  { java.awt.Font value = new java.awt.Font((java.lang.String) null, (int) 0, (int) 0); }
    static void call22()  { java.awt.Font value = new java.awt.Font((java.util.Map) null); }
    static void call23()  { java.awt.Font value = new java.awt.Font((java.awt.Font) null); }
    static void call24()  { boolean value = java.awt.Font.textRequiresLayout((char[]) null, (int) 0, (int) 0); }
    static void call25()  { java.awt.Font value = java.awt.Font.getFont((java.util.Map) null); }
    static void call26(java.awt.Font receiver)  { java.awt.geom.AffineTransform value = receiver.getTransform(); }
    static void call27(java.awt.Font receiver)  { java.lang.String value = receiver.getFamily(); }
    static void call28(java.awt.Font receiver)  { java.lang.String value = receiver.getFamily((java.util.Locale) null); }
    static void call29(java.awt.Font receiver)  { java.lang.String value = receiver.getPSName(); }
    static void call30(java.awt.Font receiver)  { java.lang.String value = receiver.getName(); }
    static void call31(java.awt.Font receiver)  { java.lang.String value = receiver.getFontName(); }
    static void call32(java.awt.Font receiver)  { java.lang.String value = receiver.getFontName((java.util.Locale) null); }
    static void call33(java.awt.Font receiver)  { int value = receiver.getStyle(); }
    static void call34(java.awt.Font receiver)  { int value = receiver.getSize(); }
    static void call35(java.awt.Font receiver)  { float value = receiver.getSize2D(); }
    static void call36(java.awt.Font receiver)  { boolean value = receiver.isPlain(); }
    static void call37(java.awt.Font receiver)  { boolean value = receiver.isBold(); }
    static void call38(java.awt.Font receiver)  { boolean value = receiver.isItalic(); }
    static void call39(java.awt.Font receiver)  { boolean value = receiver.isTransformed(); }
    static void call40(java.awt.Font receiver)  { boolean value = receiver.hasLayoutAttributes(); }
    static void call41()  { java.awt.Font value = java.awt.Font.getFont((java.lang.String) null); }
    static void call42()  { java.awt.Font value = java.awt.Font.decode((java.lang.String) null); }
    static void call43()  { java.awt.Font value = java.awt.Font.getFont((java.lang.String) null, (java.awt.Font) null); }
    static void call44(java.awt.Font receiver)  { int value = receiver.hashCode(); }
    static void call45(java.awt.Font receiver)  { boolean value = receiver.equals((java.lang.Object) null); }
    static void call46(java.awt.Font receiver)  { java.lang.String value = receiver.toString(); }
    static void call47(java.awt.Font receiver)  { int value = receiver.getNumGlyphs(); }
    static void call48(java.awt.Font receiver)  { int value = receiver.getMissingGlyphCode(); }
    static void call49(java.awt.Font receiver)  { byte value = receiver.getBaselineFor((char) 0); }
    static void call50(java.awt.Font receiver)  { java.util.Map value = receiver.getAttributes(); }
    static void call51(java.awt.Font receiver)  { java.text.AttributedCharacterIterator.Attribute[] value = receiver.getAvailableAttributes(); }
    static void call52(java.awt.Font receiver)  { java.awt.Font value = receiver.deriveFont((int) 0, (float) 0); }
    static void call53(java.awt.Font receiver)  { java.awt.Font value = receiver.deriveFont((int) 0, (java.awt.geom.AffineTransform) null); }
    static void call54(java.awt.Font receiver)  { java.awt.Font value = receiver.deriveFont((float) 0); }
    static void call55(java.awt.Font receiver)  { java.awt.Font value = receiver.deriveFont((java.awt.geom.AffineTransform) null); }
    static void call56(java.awt.Font receiver)  { java.awt.Font value = receiver.deriveFont((int) 0); }
    static void call57(java.awt.Font receiver)  { java.awt.Font value = receiver.deriveFont((java.util.Map) null); }
    static void call58(java.awt.Font receiver)  { boolean value = receiver.canDisplay((char) 0); }
    static void call59(java.awt.Font receiver)  { boolean value = receiver.canDisplay((int) 0); }
    static void call60(java.awt.Font receiver)  { int value = receiver.canDisplayUpTo((java.lang.String) null); }
    static void call61(java.awt.Font receiver)  { int value = receiver.canDisplayUpTo((char[]) null, (int) 0, (int) 0); }
    static void call62(java.awt.Font receiver)  { int value = receiver.canDisplayUpTo((java.text.CharacterIterator) null, (int) 0, (int) 0); }
    static void call63(java.awt.Font receiver)  { float value = receiver.getItalicAngle(); }
    static void call64(java.awt.Font receiver)  { boolean value = receiver.hasUniformLineMetrics(); }
    static void call65(java.awt.Font receiver)  { java.awt.font.LineMetrics value = receiver.getLineMetrics((java.lang.String) null, (java.awt.font.FontRenderContext) null); }
    static void call66(java.awt.Font receiver)  { java.awt.font.LineMetrics value = receiver.getLineMetrics((java.lang.String) null, (int) 0, (int) 0, (java.awt.font.FontRenderContext) null); }
    static void call67(java.awt.Font receiver)  { java.awt.font.LineMetrics value = receiver.getLineMetrics((char[]) null, (int) 0, (int) 0, (java.awt.font.FontRenderContext) null); }
    static void call68(java.awt.Font receiver)  { java.awt.font.LineMetrics value = receiver.getLineMetrics((java.text.CharacterIterator) null, (int) 0, (int) 0, (java.awt.font.FontRenderContext) null); }
    static void call69(java.awt.Font receiver)  { java.awt.geom.Rectangle2D value = receiver.getStringBounds((java.lang.String) null, (java.awt.font.FontRenderContext) null); }
    static void call70(java.awt.Font receiver)  { java.awt.geom.Rectangle2D value = receiver.getStringBounds((java.lang.String) null, (int) 0, (int) 0, (java.awt.font.FontRenderContext) null); }
    static void call71(java.awt.Font receiver)  { java.awt.geom.Rectangle2D value = receiver.getStringBounds((char[]) null, (int) 0, (int) 0, (java.awt.font.FontRenderContext) null); }
    static void call72(java.awt.Font receiver)  { java.awt.geom.Rectangle2D value = receiver.getStringBounds((java.text.CharacterIterator) null, (int) 0, (int) 0, (java.awt.font.FontRenderContext) null); }
    static void call73(java.awt.Font receiver)  { java.awt.geom.Rectangle2D value = receiver.getMaxCharBounds((java.awt.font.FontRenderContext) null); }
    static void call74(java.awt.Font receiver)  { java.awt.font.GlyphVector value = receiver.createGlyphVector((java.awt.font.FontRenderContext) null, (java.lang.String) null); }
    static void call75(java.awt.Font receiver)  { java.awt.font.GlyphVector value = receiver.createGlyphVector((java.awt.font.FontRenderContext) null, (char[]) null); }
    static void call76(java.awt.Font receiver)  { java.awt.font.GlyphVector value = receiver.createGlyphVector((java.awt.font.FontRenderContext) null, (java.text.CharacterIterator) null); }
    static void call77(java.awt.Font receiver)  { java.awt.font.GlyphVector value = receiver.createGlyphVector((java.awt.font.FontRenderContext) null, (int[]) null); }
    static void call78(java.awt.Font receiver)  { java.awt.font.GlyphVector value = receiver.layoutGlyphVector((java.awt.font.FontRenderContext) null, (char[]) null, (int) 0, (int) 0, (int) 0); }
}

abstract class Override_java_awt_Font extends java.awt.Font {
    Override_java_awt_Font()  { super((java.lang.String) null, (int) 0, (int) 0); }
    @Override
    public java.awt.geom.AffineTransform getTransform()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String getFamily()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String getFamily(java.util.Locale arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String getPSName()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String getName()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String getFontName()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String getFontName(java.util.Locale arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int getStyle()  { throw new UnsupportedOperationException(); }
    @Override
    public int getSize()  { throw new UnsupportedOperationException(); }
    @Override
    public float getSize2D()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isPlain()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isBold()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isItalic()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isTransformed()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean hasLayoutAttributes()  { throw new UnsupportedOperationException(); }
    @Override
    public int hashCode()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean equals(java.lang.Object arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String toString()  { throw new UnsupportedOperationException(); }
    @Override
    public int getNumGlyphs()  { throw new UnsupportedOperationException(); }
    @Override
    public int getMissingGlyphCode()  { throw new UnsupportedOperationException(); }
    @Override
    public byte getBaselineFor(char arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.util.Map<java.awt.font.TextAttribute, ?> getAttributes()  { throw new UnsupportedOperationException(); }
    @Override
    public java.text.AttributedCharacterIterator.Attribute[] getAvailableAttributes()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Font deriveFont(int arg0, float arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Font deriveFont(int arg0, java.awt.geom.AffineTransform arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Font deriveFont(float arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Font deriveFont(java.awt.geom.AffineTransform arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Font deriveFont(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Font deriveFont(java.util.Map<? extends java.text.AttributedCharacterIterator.Attribute, ?> arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean canDisplay(char arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean canDisplay(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int canDisplayUpTo(java.lang.String arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int canDisplayUpTo(char[] arg0, int arg1, int arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public int canDisplayUpTo(java.text.CharacterIterator arg0, int arg1, int arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public float getItalicAngle()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean hasUniformLineMetrics()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.font.LineMetrics getLineMetrics(java.lang.String arg0, java.awt.font.FontRenderContext arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.font.LineMetrics getLineMetrics(java.lang.String arg0, int arg1, int arg2, java.awt.font.FontRenderContext arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.font.LineMetrics getLineMetrics(char[] arg0, int arg1, int arg2, java.awt.font.FontRenderContext arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.font.LineMetrics getLineMetrics(java.text.CharacterIterator arg0, int arg1, int arg2, java.awt.font.FontRenderContext arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.Rectangle2D getStringBounds(java.lang.String arg0, java.awt.font.FontRenderContext arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.Rectangle2D getStringBounds(java.lang.String arg0, int arg1, int arg2, java.awt.font.FontRenderContext arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.Rectangle2D getStringBounds(char[] arg0, int arg1, int arg2, java.awt.font.FontRenderContext arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.Rectangle2D getStringBounds(java.text.CharacterIterator arg0, int arg1, int arg2, java.awt.font.FontRenderContext arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.Rectangle2D getMaxCharBounds(java.awt.font.FontRenderContext arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.font.GlyphVector createGlyphVector(java.awt.font.FontRenderContext arg0, java.lang.String arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.font.GlyphVector createGlyphVector(java.awt.font.FontRenderContext arg0, char[] arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.font.GlyphVector createGlyphVector(java.awt.font.FontRenderContext arg0, java.text.CharacterIterator arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.font.GlyphVector createGlyphVector(java.awt.font.FontRenderContext arg0, int[] arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.font.GlyphVector layoutGlyphVector(java.awt.font.FontRenderContext arg0, char[] arg1, int arg2, int arg3, int arg4)  { throw new UnsupportedOperationException(); }
}
