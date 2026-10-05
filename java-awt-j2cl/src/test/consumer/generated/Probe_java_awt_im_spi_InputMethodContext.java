package java.awt.im.spi;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_im_spi_InputMethodContext {
    static void call0(java.awt.im.spi.InputMethodContext receiver)  { receiver.dispatchInputMethodEvent((int) 0, (java.text.AttributedCharacterIterator) null, (int) 0, (java.awt.font.TextHitInfo) null, (java.awt.font.TextHitInfo) null); }
    static void call1(java.awt.im.spi.InputMethodContext receiver)  { java.awt.Window value = receiver.createInputMethodWindow((java.lang.String) null, false); }
    static void call2(java.awt.im.spi.InputMethodContext receiver)  { receiver.enableClientWindowNotification((java.awt.im.spi.InputMethod) null, false); }
}
