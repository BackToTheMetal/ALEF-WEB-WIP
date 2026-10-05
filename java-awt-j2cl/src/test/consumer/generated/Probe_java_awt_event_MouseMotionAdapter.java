package java.awt.event;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_event_MouseMotionAdapter {
    static void call0(java.awt.event.MouseMotionAdapter receiver)  { receiver.mouseDragged((java.awt.event.MouseEvent) null); }
    static void call1(java.awt.event.MouseMotionAdapter receiver)  { receiver.mouseMoved((java.awt.event.MouseEvent) null); }
}

abstract class Override_java_awt_event_MouseMotionAdapter extends java.awt.event.MouseMotionAdapter {
    Override_java_awt_event_MouseMotionAdapter()  { super(); }
    @Override
    public void mouseDragged(java.awt.event.MouseEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void mouseMoved(java.awt.event.MouseEvent arg0)  { throw new UnsupportedOperationException(); }
}
