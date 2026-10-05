package java.awt.event;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_event_ContainerAdapter {
    static void call0(java.awt.event.ContainerAdapter receiver)  { receiver.componentAdded((java.awt.event.ContainerEvent) null); }
    static void call1(java.awt.event.ContainerAdapter receiver)  { receiver.componentRemoved((java.awt.event.ContainerEvent) null); }
}

abstract class Override_java_awt_event_ContainerAdapter extends java.awt.event.ContainerAdapter {
    Override_java_awt_event_ContainerAdapter()  { super(); }
    @Override
    public void componentAdded(java.awt.event.ContainerEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void componentRemoved(java.awt.event.ContainerEvent arg0)  { throw new UnsupportedOperationException(); }
}
