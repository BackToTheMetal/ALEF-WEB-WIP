package java.awt.dnd;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_dnd_DragSourceDragEvent {
    static void call0()  { java.awt.dnd.DragSourceDragEvent value = new java.awt.dnd.DragSourceDragEvent((java.awt.dnd.DragSourceContext) null, (int) 0, (int) 0, (int) 0); }
    static void call1()  { java.awt.dnd.DragSourceDragEvent value = new java.awt.dnd.DragSourceDragEvent((java.awt.dnd.DragSourceContext) null, (int) 0, (int) 0, (int) 0, (int) 0, (int) 0); }
    static void call2(java.awt.dnd.DragSourceDragEvent receiver)  { int value = receiver.getTargetActions(); }
    static void call3(java.awt.dnd.DragSourceDragEvent receiver)  { int value = receiver.getGestureModifiers(); }
    static void call4(java.awt.dnd.DragSourceDragEvent receiver)  { int value = receiver.getGestureModifiersEx(); }
    static void call5(java.awt.dnd.DragSourceDragEvent receiver)  { int value = receiver.getUserAction(); }
    static void call6(java.awt.dnd.DragSourceDragEvent receiver)  { int value = receiver.getDropAction(); }
}

abstract class Override_java_awt_dnd_DragSourceDragEvent extends java.awt.dnd.DragSourceDragEvent {
    Override_java_awt_dnd_DragSourceDragEvent()  { super((java.awt.dnd.DragSourceContext) null, (int) 0, (int) 0, (int) 0); }
    @Override
    public int getTargetActions()  { throw new UnsupportedOperationException(); }
    @Override
    public int getGestureModifiers()  { throw new UnsupportedOperationException(); }
    @Override
    public int getGestureModifiersEx()  { throw new UnsupportedOperationException(); }
    @Override
    public int getUserAction()  { throw new UnsupportedOperationException(); }
    @Override
    public int getDropAction()  { throw new UnsupportedOperationException(); }
}
