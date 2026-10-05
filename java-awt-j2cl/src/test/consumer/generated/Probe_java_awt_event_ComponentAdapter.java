package java.awt.event;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_event_ComponentAdapter {
    static void call0(java.awt.event.ComponentAdapter receiver)  { receiver.componentResized((java.awt.event.ComponentEvent) null); }
    static void call1(java.awt.event.ComponentAdapter receiver)  { receiver.componentMoved((java.awt.event.ComponentEvent) null); }
    static void call2(java.awt.event.ComponentAdapter receiver)  { receiver.componentShown((java.awt.event.ComponentEvent) null); }
    static void call3(java.awt.event.ComponentAdapter receiver)  { receiver.componentHidden((java.awt.event.ComponentEvent) null); }
}

abstract class Override_java_awt_event_ComponentAdapter extends java.awt.event.ComponentAdapter {
    Override_java_awt_event_ComponentAdapter()  { super(); }
    @Override
    public void componentResized(java.awt.event.ComponentEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void componentMoved(java.awt.event.ComponentEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void componentShown(java.awt.event.ComponentEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void componentHidden(java.awt.event.ComponentEvent arg0)  { throw new UnsupportedOperationException(); }
}
