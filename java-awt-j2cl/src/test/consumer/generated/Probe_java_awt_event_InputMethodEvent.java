package java.awt.event;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_event_InputMethodEvent {
    static void call0() { int value = java.awt.event.InputMethodEvent.INPUT_METHOD_FIRST; }
    static void call1() { int value = java.awt.event.InputMethodEvent.INPUT_METHOD_TEXT_CHANGED; }
    static void call2() { int value = java.awt.event.InputMethodEvent.CARET_POSITION_CHANGED; }
    static void call3() { int value = java.awt.event.InputMethodEvent.INPUT_METHOD_LAST; }
    static void call4()  { java.awt.event.InputMethodEvent value = new java.awt.event.InputMethodEvent((java.awt.Component) null, (int) 0, (long) 0, (java.text.AttributedCharacterIterator) null, (int) 0, (java.awt.font.TextHitInfo) null, (java.awt.font.TextHitInfo) null); }
    static void call5()  { java.awt.event.InputMethodEvent value = new java.awt.event.InputMethodEvent((java.awt.Component) null, (int) 0, (java.text.AttributedCharacterIterator) null, (int) 0, (java.awt.font.TextHitInfo) null, (java.awt.font.TextHitInfo) null); }
    static void call6()  { java.awt.event.InputMethodEvent value = new java.awt.event.InputMethodEvent((java.awt.Component) null, (int) 0, (java.awt.font.TextHitInfo) null, (java.awt.font.TextHitInfo) null); }
    static void call7(java.awt.event.InputMethodEvent receiver)  { java.text.AttributedCharacterIterator value = receiver.getText(); }
    static void call8(java.awt.event.InputMethodEvent receiver)  { int value = receiver.getCommittedCharacterCount(); }
    static void call9(java.awt.event.InputMethodEvent receiver)  { java.awt.font.TextHitInfo value = receiver.getCaret(); }
    static void call10(java.awt.event.InputMethodEvent receiver)  { java.awt.font.TextHitInfo value = receiver.getVisiblePosition(); }
    static void call11(java.awt.event.InputMethodEvent receiver)  { receiver.consume(); }
    static void call12(java.awt.event.InputMethodEvent receiver)  { boolean value = receiver.isConsumed(); }
    static void call13(java.awt.event.InputMethodEvent receiver)  { long value = receiver.getWhen(); }
    static void call14(java.awt.event.InputMethodEvent receiver)  { java.lang.String value = receiver.paramString(); }
}

abstract class Override_java_awt_event_InputMethodEvent extends java.awt.event.InputMethodEvent {
    Override_java_awt_event_InputMethodEvent()  { super((java.awt.Component) null, (int) 0, (long) 0, (java.text.AttributedCharacterIterator) null, (int) 0, (java.awt.font.TextHitInfo) null, (java.awt.font.TextHitInfo) null); }
    @Override
    public java.text.AttributedCharacterIterator getText()  { throw new UnsupportedOperationException(); }
    @Override
    public int getCommittedCharacterCount()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.font.TextHitInfo getCaret()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.font.TextHitInfo getVisiblePosition()  { throw new UnsupportedOperationException(); }
    @Override
    public void consume()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isConsumed()  { throw new UnsupportedOperationException(); }
    @Override
    public long getWhen()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String paramString()  { throw new UnsupportedOperationException(); }
}
