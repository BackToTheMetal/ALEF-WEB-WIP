package java.awt.dnd;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_dnd_DragSourceEvent {
    static void call0()  { java.awt.dnd.DragSourceEvent value = new java.awt.dnd.DragSourceEvent((java.awt.dnd.DragSourceContext) null); }
    static void call1()  { java.awt.dnd.DragSourceEvent value = new java.awt.dnd.DragSourceEvent((java.awt.dnd.DragSourceContext) null, (int) 0, (int) 0); }
    static void call2(java.awt.dnd.DragSourceEvent receiver)  { java.awt.dnd.DragSourceContext value = receiver.getDragSourceContext(); }
    static void call3(java.awt.dnd.DragSourceEvent receiver)  { java.awt.Point value = receiver.getLocation(); }
    static void call4(java.awt.dnd.DragSourceEvent receiver)  { int value = receiver.getX(); }
    static void call5(java.awt.dnd.DragSourceEvent receiver)  { int value = receiver.getY(); }
}

abstract class Override_java_awt_dnd_DragSourceEvent extends java.awt.dnd.DragSourceEvent {
    Override_java_awt_dnd_DragSourceEvent()  { super((java.awt.dnd.DragSourceContext) null); }
    @Override
    public java.awt.dnd.DragSourceContext getDragSourceContext()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Point getLocation()  { throw new UnsupportedOperationException(); }
    @Override
    public int getX()  { throw new UnsupportedOperationException(); }
    @Override
    public int getY()  { throw new UnsupportedOperationException(); }
}
