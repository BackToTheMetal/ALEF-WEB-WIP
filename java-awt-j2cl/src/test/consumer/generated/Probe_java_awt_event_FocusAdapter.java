package java.awt.event;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_event_FocusAdapter {
    static void call0(java.awt.event.FocusAdapter receiver)  { receiver.focusGained((java.awt.event.FocusEvent) null); }
    static void call1(java.awt.event.FocusAdapter receiver)  { receiver.focusLost((java.awt.event.FocusEvent) null); }
}

abstract class Override_java_awt_event_FocusAdapter extends java.awt.event.FocusAdapter {
    Override_java_awt_event_FocusAdapter()  { super(); }
    @Override
    public void focusGained(java.awt.event.FocusEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void focusLost(java.awt.event.FocusEvent arg0)  { throw new UnsupportedOperationException(); }
}
