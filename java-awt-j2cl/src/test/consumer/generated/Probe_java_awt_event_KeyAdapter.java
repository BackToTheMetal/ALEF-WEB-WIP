package java.awt.event;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_event_KeyAdapter {
    static void call0(java.awt.event.KeyAdapter receiver)  { receiver.keyTyped((java.awt.event.KeyEvent) null); }
    static void call1(java.awt.event.KeyAdapter receiver)  { receiver.keyPressed((java.awt.event.KeyEvent) null); }
    static void call2(java.awt.event.KeyAdapter receiver)  { receiver.keyReleased((java.awt.event.KeyEvent) null); }
}

abstract class Override_java_awt_event_KeyAdapter extends java.awt.event.KeyAdapter {
    Override_java_awt_event_KeyAdapter()  { super(); }
    @Override
    public void keyTyped(java.awt.event.KeyEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void keyPressed(java.awt.event.KeyEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void keyReleased(java.awt.event.KeyEvent arg0)  { throw new UnsupportedOperationException(); }
}
