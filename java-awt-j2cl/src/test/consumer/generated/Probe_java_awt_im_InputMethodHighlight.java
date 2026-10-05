package java.awt.im;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_im_InputMethodHighlight {
    static void call0() { int value = java.awt.im.InputMethodHighlight.RAW_TEXT; }
    static void call1() { int value = java.awt.im.InputMethodHighlight.CONVERTED_TEXT; }
    static void call2() { java.awt.im.InputMethodHighlight value = java.awt.im.InputMethodHighlight.UNSELECTED_RAW_TEXT_HIGHLIGHT; }
    static void call3() { java.awt.im.InputMethodHighlight value = java.awt.im.InputMethodHighlight.SELECTED_RAW_TEXT_HIGHLIGHT; }
    static void call4() { java.awt.im.InputMethodHighlight value = java.awt.im.InputMethodHighlight.UNSELECTED_CONVERTED_TEXT_HIGHLIGHT; }
    static void call5() { java.awt.im.InputMethodHighlight value = java.awt.im.InputMethodHighlight.SELECTED_CONVERTED_TEXT_HIGHLIGHT; }
    static void call6()  { java.awt.im.InputMethodHighlight value = new java.awt.im.InputMethodHighlight(false, (int) 0); }
    static void call7()  { java.awt.im.InputMethodHighlight value = new java.awt.im.InputMethodHighlight(false, (int) 0, (int) 0); }
    static void call8()  { java.awt.im.InputMethodHighlight value = new java.awt.im.InputMethodHighlight(false, (int) 0, (int) 0, (java.util.Map) null); }
    static void call9(java.awt.im.InputMethodHighlight receiver)  { boolean value = receiver.isSelected(); }
    static void call10(java.awt.im.InputMethodHighlight receiver)  { int value = receiver.getState(); }
    static void call11(java.awt.im.InputMethodHighlight receiver)  { int value = receiver.getVariation(); }
    static void call12(java.awt.im.InputMethodHighlight receiver)  { java.util.Map value = receiver.getStyle(); }
}

abstract class Override_java_awt_im_InputMethodHighlight extends java.awt.im.InputMethodHighlight {
    Override_java_awt_im_InputMethodHighlight()  { super(false, (int) 0); }
    @Override
    public boolean isSelected()  { throw new UnsupportedOperationException(); }
    @Override
    public int getState()  { throw new UnsupportedOperationException(); }
    @Override
    public int getVariation()  { throw new UnsupportedOperationException(); }
    @Override
    public java.util.Map<java.awt.font.TextAttribute, ?> getStyle()  { throw new UnsupportedOperationException(); }
}
