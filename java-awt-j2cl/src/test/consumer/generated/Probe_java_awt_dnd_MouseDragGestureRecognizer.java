package java.awt.dnd;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_dnd_MouseDragGestureRecognizer {
    static void call0(java.awt.dnd.MouseDragGestureRecognizer receiver)  { receiver.registerListeners(); }
    static void call1(java.awt.dnd.MouseDragGestureRecognizer receiver)  { receiver.unregisterListeners(); }
    static void call2(java.awt.dnd.MouseDragGestureRecognizer receiver)  { receiver.mouseClicked((java.awt.event.MouseEvent) null); }
    static void call3(java.awt.dnd.MouseDragGestureRecognizer receiver)  { receiver.mousePressed((java.awt.event.MouseEvent) null); }
    static void call4(java.awt.dnd.MouseDragGestureRecognizer receiver)  { receiver.mouseReleased((java.awt.event.MouseEvent) null); }
    static void call5(java.awt.dnd.MouseDragGestureRecognizer receiver)  { receiver.mouseEntered((java.awt.event.MouseEvent) null); }
    static void call6(java.awt.dnd.MouseDragGestureRecognizer receiver)  { receiver.mouseExited((java.awt.event.MouseEvent) null); }
    static void call7(java.awt.dnd.MouseDragGestureRecognizer receiver)  { receiver.mouseDragged((java.awt.event.MouseEvent) null); }
    static void call8(java.awt.dnd.MouseDragGestureRecognizer receiver)  { receiver.mouseMoved((java.awt.event.MouseEvent) null); }
}

abstract class Override_java_awt_dnd_MouseDragGestureRecognizer extends java.awt.dnd.MouseDragGestureRecognizer {
    Override_java_awt_dnd_MouseDragGestureRecognizer()  { super((java.awt.dnd.DragSource) null, (java.awt.Component) null, (int) 0, (java.awt.dnd.DragGestureListener) null); }
    @Override
    protected void registerListeners()  { throw new UnsupportedOperationException(); }
    @Override
    protected void unregisterListeners()  { throw new UnsupportedOperationException(); }
    @Override
    public void mouseClicked(java.awt.event.MouseEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void mousePressed(java.awt.event.MouseEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void mouseReleased(java.awt.event.MouseEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void mouseEntered(java.awt.event.MouseEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void mouseExited(java.awt.event.MouseEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void mouseDragged(java.awt.event.MouseEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void mouseMoved(java.awt.event.MouseEvent arg0)  { throw new UnsupportedOperationException(); }
}
