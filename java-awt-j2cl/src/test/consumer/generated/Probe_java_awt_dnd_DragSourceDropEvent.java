package java.awt.dnd;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_dnd_DragSourceDropEvent {
    static void call0()  { java.awt.dnd.DragSourceDropEvent value = new java.awt.dnd.DragSourceDropEvent((java.awt.dnd.DragSourceContext) null, (int) 0, false); }
    static void call1()  { java.awt.dnd.DragSourceDropEvent value = new java.awt.dnd.DragSourceDropEvent((java.awt.dnd.DragSourceContext) null, (int) 0, false, (int) 0, (int) 0); }
    static void call2()  { java.awt.dnd.DragSourceDropEvent value = new java.awt.dnd.DragSourceDropEvent((java.awt.dnd.DragSourceContext) null); }
    static void call3(java.awt.dnd.DragSourceDropEvent receiver)  { boolean value = receiver.getDropSuccess(); }
    static void call4(java.awt.dnd.DragSourceDropEvent receiver)  { int value = receiver.getDropAction(); }
}

abstract class Override_java_awt_dnd_DragSourceDropEvent extends java.awt.dnd.DragSourceDropEvent {
    Override_java_awt_dnd_DragSourceDropEvent()  { super((java.awt.dnd.DragSourceContext) null, (int) 0, false); }
    @Override
    public boolean getDropSuccess()  { throw new UnsupportedOperationException(); }
    @Override
    public int getDropAction()  { throw new UnsupportedOperationException(); }
}
