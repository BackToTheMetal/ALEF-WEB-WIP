package java.awt.dnd;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_dnd_DropTargetEvent {
    static void call0(java.awt.dnd.DropTargetEvent receiver) { java.awt.dnd.DropTargetContext value = receiver.context; receiver.context = value; }
    static void call1()  { java.awt.dnd.DropTargetEvent value = new java.awt.dnd.DropTargetEvent((java.awt.dnd.DropTargetContext) null); }
    static void call2(java.awt.dnd.DropTargetEvent receiver)  { java.awt.dnd.DropTargetContext value = receiver.getDropTargetContext(); }
}

abstract class Override_java_awt_dnd_DropTargetEvent extends java.awt.dnd.DropTargetEvent {
    Override_java_awt_dnd_DropTargetEvent()  { super((java.awt.dnd.DropTargetContext) null); }
    @Override
    public java.awt.dnd.DropTargetContext getDropTargetContext()  { throw new UnsupportedOperationException(); }
}
