package java.awt.event;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_event_FocusEvent {
    static void call0() { int value = java.awt.event.FocusEvent.FOCUS_FIRST; }
    static void call1() { int value = java.awt.event.FocusEvent.FOCUS_LAST; }
    static void call2() { int value = java.awt.event.FocusEvent.FOCUS_GAINED; }
    static void call3() { int value = java.awt.event.FocusEvent.FOCUS_LOST; }
    static void call4()  { java.awt.event.FocusEvent value = new java.awt.event.FocusEvent((java.awt.Component) null, (int) 0, false, (java.awt.Component) null); }
    static void call5()  { java.awt.event.FocusEvent value = new java.awt.event.FocusEvent((java.awt.Component) null, (int) 0, false, (java.awt.Component) null, (java.awt.event.FocusEvent.Cause) null); }
    static void call6()  { java.awt.event.FocusEvent value = new java.awt.event.FocusEvent((java.awt.Component) null, (int) 0, false); }
    static void call7()  { java.awt.event.FocusEvent value = new java.awt.event.FocusEvent((java.awt.Component) null, (int) 0); }
    static void call8(java.awt.event.FocusEvent receiver)  { boolean value = receiver.isTemporary(); }
    static void call9(java.awt.event.FocusEvent receiver)  { java.awt.Component value = receiver.getOppositeComponent(); }
    static void call10(java.awt.event.FocusEvent receiver)  { java.lang.String value = receiver.paramString(); }
    static void call11(java.awt.event.FocusEvent receiver)  { java.awt.event.FocusEvent.Cause value = receiver.getCause(); }
}

abstract class Override_java_awt_event_FocusEvent extends java.awt.event.FocusEvent {
    Override_java_awt_event_FocusEvent()  { super((java.awt.Component) null, (int) 0, false, (java.awt.Component) null); }
    @Override
    public boolean isTemporary()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Component getOppositeComponent()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String paramString()  { throw new UnsupportedOperationException(); }
}
