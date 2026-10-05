package java.awt.font;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_font_TextLayout {
    static void call0() { java.awt.font.TextLayout.CaretPolicy value = java.awt.font.TextLayout.DEFAULT_CARET_POLICY; }
    static void call1()  { java.awt.font.TextLayout value = new java.awt.font.TextLayout((java.lang.String) null, (java.awt.Font) null, (java.awt.font.FontRenderContext) null); }
    static void call2()  { java.awt.font.TextLayout value = new java.awt.font.TextLayout((java.lang.String) null, (java.util.Map) null, (java.awt.font.FontRenderContext) null); }
    static void call3()  { java.awt.font.TextLayout value = new java.awt.font.TextLayout((java.text.AttributedCharacterIterator) null, (java.awt.font.FontRenderContext) null); }
    static void call4(java.awt.font.TextLayout receiver)  { java.lang.Object value = receiver.clone(); }
    static void call5(java.awt.font.TextLayout receiver)  { java.awt.font.TextLayout value = receiver.getJustifiedLayout((float) 0); }
    static void call6(java.awt.font.TextLayout receiver)  { byte value = receiver.getBaseline(); }
    static void call7(java.awt.font.TextLayout receiver)  { float[] value = receiver.getBaselineOffsets(); }
    static void call8(java.awt.font.TextLayout receiver)  { float value = receiver.getAdvance(); }
    static void call9(java.awt.font.TextLayout receiver)  { float value = receiver.getVisibleAdvance(); }
    static void call10(java.awt.font.TextLayout receiver)  { float value = receiver.getAscent(); }
    static void call11(java.awt.font.TextLayout receiver)  { float value = receiver.getDescent(); }
    static void call12(java.awt.font.TextLayout receiver)  { float value = receiver.getLeading(); }
    static void call13(java.awt.font.TextLayout receiver)  { java.awt.geom.Rectangle2D value = receiver.getBounds(); }
    static void call14(java.awt.font.TextLayout receiver)  { java.awt.Rectangle value = receiver.getPixelBounds((java.awt.font.FontRenderContext) null, (float) 0, (float) 0); }
    static void call15(java.awt.font.TextLayout receiver)  { boolean value = receiver.isLeftToRight(); }
    static void call16(java.awt.font.TextLayout receiver)  { boolean value = receiver.isVertical(); }
    static void call17(java.awt.font.TextLayout receiver)  { int value = receiver.getCharacterCount(); }
    static void call18(java.awt.font.TextLayout receiver)  { float[] value = receiver.getCaretInfo((java.awt.font.TextHitInfo) null, (java.awt.geom.Rectangle2D) null); }
    static void call19(java.awt.font.TextLayout receiver)  { float[] value = receiver.getCaretInfo((java.awt.font.TextHitInfo) null); }
    static void call20(java.awt.font.TextLayout receiver)  { java.awt.font.TextHitInfo value = receiver.getNextRightHit((java.awt.font.TextHitInfo) null); }
    static void call21(java.awt.font.TextLayout receiver)  { java.awt.font.TextHitInfo value = receiver.getNextRightHit((int) 0, (java.awt.font.TextLayout.CaretPolicy) null); }
    static void call22(java.awt.font.TextLayout receiver)  { java.awt.font.TextHitInfo value = receiver.getNextRightHit((int) 0); }
    static void call23(java.awt.font.TextLayout receiver)  { java.awt.font.TextHitInfo value = receiver.getNextLeftHit((java.awt.font.TextHitInfo) null); }
    static void call24(java.awt.font.TextLayout receiver)  { java.awt.font.TextHitInfo value = receiver.getNextLeftHit((int) 0, (java.awt.font.TextLayout.CaretPolicy) null); }
    static void call25(java.awt.font.TextLayout receiver)  { java.awt.font.TextHitInfo value = receiver.getNextLeftHit((int) 0); }
    static void call26(java.awt.font.TextLayout receiver)  { java.awt.font.TextHitInfo value = receiver.getVisualOtherHit((java.awt.font.TextHitInfo) null); }
    static void call27(java.awt.font.TextLayout receiver)  { java.awt.Shape value = receiver.getCaretShape((java.awt.font.TextHitInfo) null, (java.awt.geom.Rectangle2D) null); }
    static void call28(java.awt.font.TextLayout receiver)  { java.awt.Shape value = receiver.getCaretShape((java.awt.font.TextHitInfo) null); }
    static void call29(java.awt.font.TextLayout receiver)  { byte value = receiver.getCharacterLevel((int) 0); }
    static void call30(java.awt.font.TextLayout receiver)  { java.awt.Shape[] value = receiver.getCaretShapes((int) 0, (java.awt.geom.Rectangle2D) null, (java.awt.font.TextLayout.CaretPolicy) null); }
    static void call31(java.awt.font.TextLayout receiver)  { java.awt.Shape[] value = receiver.getCaretShapes((int) 0, (java.awt.geom.Rectangle2D) null); }
    static void call32(java.awt.font.TextLayout receiver)  { java.awt.Shape[] value = receiver.getCaretShapes((int) 0); }
    static void call33(java.awt.font.TextLayout receiver)  { int[] value = receiver.getLogicalRangesForVisualSelection((java.awt.font.TextHitInfo) null, (java.awt.font.TextHitInfo) null); }
    static void call34(java.awt.font.TextLayout receiver)  { java.awt.Shape value = receiver.getVisualHighlightShape((java.awt.font.TextHitInfo) null, (java.awt.font.TextHitInfo) null, (java.awt.geom.Rectangle2D) null); }
    static void call35(java.awt.font.TextLayout receiver)  { java.awt.Shape value = receiver.getVisualHighlightShape((java.awt.font.TextHitInfo) null, (java.awt.font.TextHitInfo) null); }
    static void call36(java.awt.font.TextLayout receiver)  { java.awt.Shape value = receiver.getLogicalHighlightShape((int) 0, (int) 0, (java.awt.geom.Rectangle2D) null); }
    static void call37(java.awt.font.TextLayout receiver)  { java.awt.Shape value = receiver.getLogicalHighlightShape((int) 0, (int) 0); }
    static void call38(java.awt.font.TextLayout receiver)  { java.awt.Shape value = receiver.getBlackBoxBounds((int) 0, (int) 0); }
    static void call39(java.awt.font.TextLayout receiver)  { java.awt.font.TextHitInfo value = receiver.hitTestChar((float) 0, (float) 0, (java.awt.geom.Rectangle2D) null); }
    static void call40(java.awt.font.TextLayout receiver)  { java.awt.font.TextHitInfo value = receiver.hitTestChar((float) 0, (float) 0); }
    static void call41(java.awt.font.TextLayout receiver)  { boolean value = receiver.equals((java.awt.font.TextLayout) null); }
    static void call42(java.awt.font.TextLayout receiver)  { java.lang.String value = receiver.toString(); }
    static void call43(java.awt.font.TextLayout receiver)  { receiver.draw((java.awt.Graphics2D) null, (float) 0, (float) 0); }
    static void call44(java.awt.font.TextLayout receiver)  { java.awt.Shape value = receiver.getOutline((java.awt.geom.AffineTransform) null); }
    static void call45(java.awt.font.TextLayout receiver)  { java.awt.font.LayoutPath value = receiver.getLayoutPath(); }
    static void call46(java.awt.font.TextLayout receiver)  { receiver.hitToPoint((java.awt.font.TextHitInfo) null, (java.awt.geom.Point2D) null); }
}
