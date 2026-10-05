package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_FontMetrics {
    static void call0(java.awt.FontMetrics receiver) { java.awt.Font value = receiver.font; receiver.font = value; }
    static void call1(java.awt.FontMetrics receiver)  { java.awt.Font value = receiver.getFont(); }
    static void call2(java.awt.FontMetrics receiver)  { java.awt.font.FontRenderContext value = receiver.getFontRenderContext(); }
    static void call3(java.awt.FontMetrics receiver)  { int value = receiver.getLeading(); }
    static void call4(java.awt.FontMetrics receiver)  { int value = receiver.getAscent(); }
    static void call5(java.awt.FontMetrics receiver)  { int value = receiver.getDescent(); }
    static void call6(java.awt.FontMetrics receiver)  { int value = receiver.getHeight(); }
    static void call7(java.awt.FontMetrics receiver)  { int value = receiver.getMaxAscent(); }
    static void call8(java.awt.FontMetrics receiver)  { int value = receiver.getMaxDescent(); }
    static void call9(java.awt.FontMetrics receiver)  { int value = receiver.getMaxDecent(); }
    static void call10(java.awt.FontMetrics receiver)  { int value = receiver.getMaxAdvance(); }
    static void call11(java.awt.FontMetrics receiver)  { int value = receiver.charWidth((int) 0); }
    static void call12(java.awt.FontMetrics receiver)  { int value = receiver.charWidth((char) 0); }
    static void call13(java.awt.FontMetrics receiver)  { int value = receiver.stringWidth((java.lang.String) null); }
    static void call14(java.awt.FontMetrics receiver)  { int value = receiver.charsWidth((char[]) null, (int) 0, (int) 0); }
    static void call15(java.awt.FontMetrics receiver)  { int value = receiver.bytesWidth((byte[]) null, (int) 0, (int) 0); }
    static void call16(java.awt.FontMetrics receiver)  { int[] value = receiver.getWidths(); }
    static void call17(java.awt.FontMetrics receiver)  { boolean value = receiver.hasUniformLineMetrics(); }
    static void call18(java.awt.FontMetrics receiver)  { java.awt.font.LineMetrics value = receiver.getLineMetrics((java.lang.String) null, (java.awt.Graphics) null); }
    static void call19(java.awt.FontMetrics receiver)  { java.awt.font.LineMetrics value = receiver.getLineMetrics((java.lang.String) null, (int) 0, (int) 0, (java.awt.Graphics) null); }
    static void call20(java.awt.FontMetrics receiver)  { java.awt.font.LineMetrics value = receiver.getLineMetrics((char[]) null, (int) 0, (int) 0, (java.awt.Graphics) null); }
    static void call21(java.awt.FontMetrics receiver)  { java.awt.font.LineMetrics value = receiver.getLineMetrics((java.text.CharacterIterator) null, (int) 0, (int) 0, (java.awt.Graphics) null); }
    static void call22(java.awt.FontMetrics receiver)  { java.awt.geom.Rectangle2D value = receiver.getStringBounds((java.lang.String) null, (java.awt.Graphics) null); }
    static void call23(java.awt.FontMetrics receiver)  { java.awt.geom.Rectangle2D value = receiver.getStringBounds((java.lang.String) null, (int) 0, (int) 0, (java.awt.Graphics) null); }
    static void call24(java.awt.FontMetrics receiver)  { java.awt.geom.Rectangle2D value = receiver.getStringBounds((char[]) null, (int) 0, (int) 0, (java.awt.Graphics) null); }
    static void call25(java.awt.FontMetrics receiver)  { java.awt.geom.Rectangle2D value = receiver.getStringBounds((java.text.CharacterIterator) null, (int) 0, (int) 0, (java.awt.Graphics) null); }
    static void call26(java.awt.FontMetrics receiver)  { java.awt.geom.Rectangle2D value = receiver.getMaxCharBounds((java.awt.Graphics) null); }
    static void call27(java.awt.FontMetrics receiver)  { java.lang.String value = receiver.toString(); }
}

abstract class Override_java_awt_FontMetrics extends java.awt.FontMetrics {
    Override_java_awt_FontMetrics()  { super((java.awt.Font) null); }
    @Override
    public java.awt.Font getFont()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.font.FontRenderContext getFontRenderContext()  { throw new UnsupportedOperationException(); }
    @Override
    public int getLeading()  { throw new UnsupportedOperationException(); }
    @Override
    public int getAscent()  { throw new UnsupportedOperationException(); }
    @Override
    public int getDescent()  { throw new UnsupportedOperationException(); }
    @Override
    public int getHeight()  { throw new UnsupportedOperationException(); }
    @Override
    public int getMaxAscent()  { throw new UnsupportedOperationException(); }
    @Override
    public int getMaxDescent()  { throw new UnsupportedOperationException(); }
    @Override
    public int getMaxDecent()  { throw new UnsupportedOperationException(); }
    @Override
    public int getMaxAdvance()  { throw new UnsupportedOperationException(); }
    @Override
    public int charWidth(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int charWidth(char arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int stringWidth(java.lang.String arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int charsWidth(char[] arg0, int arg1, int arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public int bytesWidth(byte[] arg0, int arg1, int arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public int[] getWidths()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean hasUniformLineMetrics()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.font.LineMetrics getLineMetrics(java.lang.String arg0, java.awt.Graphics arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.font.LineMetrics getLineMetrics(java.lang.String arg0, int arg1, int arg2, java.awt.Graphics arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.font.LineMetrics getLineMetrics(char[] arg0, int arg1, int arg2, java.awt.Graphics arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.font.LineMetrics getLineMetrics(java.text.CharacterIterator arg0, int arg1, int arg2, java.awt.Graphics arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.Rectangle2D getStringBounds(java.lang.String arg0, java.awt.Graphics arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.Rectangle2D getStringBounds(java.lang.String arg0, int arg1, int arg2, java.awt.Graphics arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.Rectangle2D getStringBounds(char[] arg0, int arg1, int arg2, java.awt.Graphics arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.Rectangle2D getStringBounds(java.text.CharacterIterator arg0, int arg1, int arg2, java.awt.Graphics arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.geom.Rectangle2D getMaxCharBounds(java.awt.Graphics arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String toString()  { throw new UnsupportedOperationException(); }
}
