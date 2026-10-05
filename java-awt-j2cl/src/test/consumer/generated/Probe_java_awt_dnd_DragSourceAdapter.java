package java.awt.dnd;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_dnd_DragSourceAdapter {
    static void call0(java.awt.dnd.DragSourceAdapter receiver)  { receiver.dragEnter((java.awt.dnd.DragSourceDragEvent) null); }
    static void call1(java.awt.dnd.DragSourceAdapter receiver)  { receiver.dragOver((java.awt.dnd.DragSourceDragEvent) null); }
    static void call2(java.awt.dnd.DragSourceAdapter receiver)  { receiver.dragMouseMoved((java.awt.dnd.DragSourceDragEvent) null); }
    static void call3(java.awt.dnd.DragSourceAdapter receiver)  { receiver.dropActionChanged((java.awt.dnd.DragSourceDragEvent) null); }
    static void call4(java.awt.dnd.DragSourceAdapter receiver)  { receiver.dragExit((java.awt.dnd.DragSourceEvent) null); }
    static void call5(java.awt.dnd.DragSourceAdapter receiver)  { receiver.dragDropEnd((java.awt.dnd.DragSourceDropEvent) null); }
}

abstract class Override_java_awt_dnd_DragSourceAdapter extends java.awt.dnd.DragSourceAdapter {
    Override_java_awt_dnd_DragSourceAdapter()  { super(); }
    @Override
    public void dragEnter(java.awt.dnd.DragSourceDragEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void dragOver(java.awt.dnd.DragSourceDragEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void dragMouseMoved(java.awt.dnd.DragSourceDragEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void dropActionChanged(java.awt.dnd.DragSourceDragEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void dragExit(java.awt.dnd.DragSourceEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void dragDropEnd(java.awt.dnd.DragSourceDropEvent arg0)  { throw new UnsupportedOperationException(); }
}
