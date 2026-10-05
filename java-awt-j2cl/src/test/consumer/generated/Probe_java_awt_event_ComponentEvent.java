package java.awt.event;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_event_ComponentEvent {
    static void call0() { int value = java.awt.event.ComponentEvent.COMPONENT_FIRST; }
    static void call1() { int value = java.awt.event.ComponentEvent.COMPONENT_LAST; }
    static void call2() { int value = java.awt.event.ComponentEvent.COMPONENT_MOVED; }
    static void call3() { int value = java.awt.event.ComponentEvent.COMPONENT_RESIZED; }
    static void call4() { int value = java.awt.event.ComponentEvent.COMPONENT_SHOWN; }
    static void call5() { int value = java.awt.event.ComponentEvent.COMPONENT_HIDDEN; }
    static void call6()  { java.awt.event.ComponentEvent value = new java.awt.event.ComponentEvent((java.awt.Component) null, (int) 0); }
    static void call7(java.awt.event.ComponentEvent receiver)  { java.awt.Component value = receiver.getComponent(); }
    static void call8(java.awt.event.ComponentEvent receiver)  { java.lang.String value = receiver.paramString(); }
}

abstract class Override_java_awt_event_ComponentEvent extends java.awt.event.ComponentEvent {
    Override_java_awt_event_ComponentEvent()  { super((java.awt.Component) null, (int) 0); }
    @Override
    public java.awt.Component getComponent()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String paramString()  { throw new UnsupportedOperationException(); }
}
