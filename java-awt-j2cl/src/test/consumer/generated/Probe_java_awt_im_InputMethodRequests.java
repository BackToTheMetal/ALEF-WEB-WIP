package java.awt.im;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_im_InputMethodRequests {
    static void call0(java.awt.im.InputMethodRequests receiver)  { java.awt.Rectangle value = receiver.getTextLocation((java.awt.font.TextHitInfo) null); }
    static void call1(java.awt.im.InputMethodRequests receiver)  { java.awt.font.TextHitInfo value = receiver.getLocationOffset((int) 0, (int) 0); }
    static void call2(java.awt.im.InputMethodRequests receiver)  { int value = receiver.getInsertPositionOffset(); }
    static void call3(java.awt.im.InputMethodRequests receiver)  { java.text.AttributedCharacterIterator value = receiver.getCommittedText((int) 0, (int) 0, (java.text.AttributedCharacterIterator.Attribute[]) null); }
    static void call4(java.awt.im.InputMethodRequests receiver)  { int value = receiver.getCommittedTextLength(); }
    static void call5(java.awt.im.InputMethodRequests receiver)  { java.text.AttributedCharacterIterator value = receiver.cancelLatestCommittedText((java.text.AttributedCharacterIterator.Attribute[]) null); }
    static void call6(java.awt.im.InputMethodRequests receiver)  { java.text.AttributedCharacterIterator value = receiver.getSelectedText((java.text.AttributedCharacterIterator.Attribute[]) null); }
}
