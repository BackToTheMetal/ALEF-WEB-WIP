package java.awt.event;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_event_TextEvent {
    static void call0() { int value = java.awt.event.TextEvent.TEXT_FIRST; }
    static void call1() { int value = java.awt.event.TextEvent.TEXT_LAST; }
    static void call2() { int value = java.awt.event.TextEvent.TEXT_VALUE_CHANGED; }
    static void call3()  { java.awt.event.TextEvent value = new java.awt.event.TextEvent((java.lang.Object) null, (int) 0); }
    static void call4(java.awt.event.TextEvent receiver)  { java.lang.String value = receiver.paramString(); }
}

abstract class Override_java_awt_event_TextEvent extends java.awt.event.TextEvent {
    Override_java_awt_event_TextEvent()  { super((java.lang.Object) null, (int) 0); }
    @Override
    public java.lang.String paramString()  { throw new UnsupportedOperationException(); }
}
