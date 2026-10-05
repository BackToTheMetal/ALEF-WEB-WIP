package java.awt.dnd;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_dnd_DropTargetAdapter {
    static void call0(java.awt.dnd.DropTargetAdapter receiver)  { receiver.dragEnter((java.awt.dnd.DropTargetDragEvent) null); }
    static void call1(java.awt.dnd.DropTargetAdapter receiver)  { receiver.dragOver((java.awt.dnd.DropTargetDragEvent) null); }
    static void call2(java.awt.dnd.DropTargetAdapter receiver)  { receiver.dropActionChanged((java.awt.dnd.DropTargetDragEvent) null); }
    static void call3(java.awt.dnd.DropTargetAdapter receiver)  { receiver.dragExit((java.awt.dnd.DropTargetEvent) null); }
}

abstract class Override_java_awt_dnd_DropTargetAdapter extends java.awt.dnd.DropTargetAdapter {
    Override_java_awt_dnd_DropTargetAdapter()  { super(); }
    @Override
    public void dragEnter(java.awt.dnd.DropTargetDragEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void dragOver(java.awt.dnd.DropTargetDragEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void dropActionChanged(java.awt.dnd.DropTargetDragEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void dragExit(java.awt.dnd.DropTargetEvent arg0)  { throw new UnsupportedOperationException(); }
}
