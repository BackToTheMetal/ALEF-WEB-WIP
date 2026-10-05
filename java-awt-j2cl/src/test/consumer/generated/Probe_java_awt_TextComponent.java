package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_TextComponent {
    static void call0(java.awt.TextComponent receiver) { java.awt.event.TextListener value = receiver.textListener; receiver.textListener = value; }
    static void call1(java.awt.TextComponent receiver)  { receiver.enableInputMethods(false); }
    static void call2(java.awt.TextComponent receiver)  { java.awt.im.InputMethodRequests value = receiver.getInputMethodRequests(); }
    static void call3(java.awt.TextComponent receiver)  { receiver.addNotify(); }
    static void call4(java.awt.TextComponent receiver)  { receiver.removeNotify(); }
    static void call5(java.awt.TextComponent receiver)  { receiver.setText((java.lang.String) null); }
    static void call6(java.awt.TextComponent receiver)  { java.lang.String value = receiver.getText(); }
    static void call7(java.awt.TextComponent receiver)  { java.lang.String value = receiver.getSelectedText(); }
    static void call8(java.awt.TextComponent receiver)  { boolean value = receiver.isEditable(); }
    static void call9(java.awt.TextComponent receiver)  { receiver.setEditable(false); }
    static void call10(java.awt.TextComponent receiver)  { java.awt.Color value = receiver.getBackground(); }
    static void call11(java.awt.TextComponent receiver)  { receiver.setBackground((java.awt.Color) null); }
    static void call12(java.awt.TextComponent receiver)  { int value = receiver.getSelectionStart(); }
    static void call13(java.awt.TextComponent receiver)  { receiver.setSelectionStart((int) 0); }
    static void call14(java.awt.TextComponent receiver)  { int value = receiver.getSelectionEnd(); }
    static void call15(java.awt.TextComponent receiver)  { receiver.setSelectionEnd((int) 0); }
    static void call16(java.awt.TextComponent receiver)  { receiver.select((int) 0, (int) 0); }
    static void call17(java.awt.TextComponent receiver)  { receiver.selectAll(); }
    static void call18(java.awt.TextComponent receiver)  { receiver.setCaretPosition((int) 0); }
    static void call19(java.awt.TextComponent receiver)  { int value = receiver.getCaretPosition(); }
    static void call20(java.awt.TextComponent receiver)  { receiver.addTextListener((java.awt.event.TextListener) null); }
    static void call21(java.awt.TextComponent receiver)  { receiver.removeTextListener((java.awt.event.TextListener) null); }
    static void call22(java.awt.TextComponent receiver)  { java.awt.event.TextListener[] value = receiver.getTextListeners(); }
    static void call23(java.awt.TextComponent receiver)  { java.util.EventListener[] value = receiver.getListeners((java.lang.Class) null); }
    static void call24(java.awt.TextComponent receiver)  { receiver.processEvent((java.awt.AWTEvent) null); }
    static void call25(java.awt.TextComponent receiver)  { receiver.processTextEvent((java.awt.event.TextEvent) null); }
    static void call26(java.awt.TextComponent receiver)  { java.lang.String value = receiver.paramString(); }
    static void call27(java.awt.TextComponent receiver)  { javax.accessibility.AccessibleContext value = receiver.getAccessibleContext(); }
}
