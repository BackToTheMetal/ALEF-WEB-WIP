package java.awt.event;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_event_ActionEvent {
    static void call0() { int value = java.awt.event.ActionEvent.SHIFT_MASK; }
    static void call1() { int value = java.awt.event.ActionEvent.CTRL_MASK; }
    static void call2() { int value = java.awt.event.ActionEvent.META_MASK; }
    static void call3() { int value = java.awt.event.ActionEvent.ALT_MASK; }
    static void call4() { int value = java.awt.event.ActionEvent.ACTION_FIRST; }
    static void call5() { int value = java.awt.event.ActionEvent.ACTION_LAST; }
    static void call6() { int value = java.awt.event.ActionEvent.ACTION_PERFORMED; }
    static void call7()  { java.awt.event.ActionEvent value = new java.awt.event.ActionEvent((java.lang.Object) null, (int) 0, (java.lang.String) null); }
    static void call8()  { java.awt.event.ActionEvent value = new java.awt.event.ActionEvent((java.lang.Object) null, (int) 0, (java.lang.String) null, (int) 0); }
    static void call9()  { java.awt.event.ActionEvent value = new java.awt.event.ActionEvent((java.lang.Object) null, (int) 0, (java.lang.String) null, (long) 0, (int) 0); }
    static void call10(java.awt.event.ActionEvent receiver)  { java.lang.String value = receiver.getActionCommand(); }
    static void call11(java.awt.event.ActionEvent receiver)  { long value = receiver.getWhen(); }
    static void call12(java.awt.event.ActionEvent receiver)  { int value = receiver.getModifiers(); }
    static void call13(java.awt.event.ActionEvent receiver)  { java.lang.String value = receiver.paramString(); }
}

abstract class Override_java_awt_event_ActionEvent extends java.awt.event.ActionEvent {
    Override_java_awt_event_ActionEvent()  { super((java.lang.Object) null, (int) 0, (java.lang.String) null); }
    @Override
    public java.lang.String getActionCommand()  { throw new UnsupportedOperationException(); }
    @Override
    public long getWhen()  { throw new UnsupportedOperationException(); }
    @Override
    public int getModifiers()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String paramString()  { throw new UnsupportedOperationException(); }
}
