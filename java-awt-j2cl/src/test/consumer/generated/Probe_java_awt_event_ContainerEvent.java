package java.awt.event;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_event_ContainerEvent {
    static void call0() { int value = java.awt.event.ContainerEvent.CONTAINER_FIRST; }
    static void call1() { int value = java.awt.event.ContainerEvent.CONTAINER_LAST; }
    static void call2() { int value = java.awt.event.ContainerEvent.COMPONENT_ADDED; }
    static void call3() { int value = java.awt.event.ContainerEvent.COMPONENT_REMOVED; }
    static void call4()  { java.awt.event.ContainerEvent value = new java.awt.event.ContainerEvent((java.awt.Component) null, (int) 0, (java.awt.Component) null); }
    static void call5(java.awt.event.ContainerEvent receiver)  { java.awt.Container value = receiver.getContainer(); }
    static void call6(java.awt.event.ContainerEvent receiver)  { java.awt.Component value = receiver.getChild(); }
    static void call7(java.awt.event.ContainerEvent receiver)  { java.lang.String value = receiver.paramString(); }
}

abstract class Override_java_awt_event_ContainerEvent extends java.awt.event.ContainerEvent {
    Override_java_awt_event_ContainerEvent()  { super((java.awt.Component) null, (int) 0, (java.awt.Component) null); }
    @Override
    public java.awt.Container getContainer()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Component getChild()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String paramString()  { throw new UnsupportedOperationException(); }
}
