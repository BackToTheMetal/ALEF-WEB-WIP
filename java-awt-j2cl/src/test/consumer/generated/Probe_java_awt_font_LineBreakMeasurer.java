package java.awt.font;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_font_LineBreakMeasurer {
    static void call0()  { java.awt.font.LineBreakMeasurer value = new java.awt.font.LineBreakMeasurer((java.text.AttributedCharacterIterator) null, (java.awt.font.FontRenderContext) null); }
    static void call1()  { java.awt.font.LineBreakMeasurer value = new java.awt.font.LineBreakMeasurer((java.text.AttributedCharacterIterator) null, (java.text.BreakIterator) null, (java.awt.font.FontRenderContext) null); }
    static void call2(java.awt.font.LineBreakMeasurer receiver)  { int value = receiver.nextOffset((float) 0); }
    static void call3(java.awt.font.LineBreakMeasurer receiver)  { int value = receiver.nextOffset((float) 0, (int) 0, false); }
    static void call4(java.awt.font.LineBreakMeasurer receiver)  { java.awt.font.TextLayout value = receiver.nextLayout((float) 0); }
    static void call5(java.awt.font.LineBreakMeasurer receiver)  { java.awt.font.TextLayout value = receiver.nextLayout((float) 0, (int) 0, false); }
    static void call6(java.awt.font.LineBreakMeasurer receiver)  { int value = receiver.getPosition(); }
    static void call7(java.awt.font.LineBreakMeasurer receiver)  { receiver.setPosition((int) 0); }
    static void call8(java.awt.font.LineBreakMeasurer receiver)  { receiver.insertChar((java.text.AttributedCharacterIterator) null, (int) 0); }
    static void call9(java.awt.font.LineBreakMeasurer receiver)  { receiver.deleteChar((java.text.AttributedCharacterIterator) null, (int) 0); }
}
