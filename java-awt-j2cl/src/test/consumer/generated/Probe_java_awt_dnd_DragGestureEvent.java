package java.awt.dnd;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_dnd_DragGestureEvent {
    static void call0()  { java.awt.dnd.DragGestureEvent value = new java.awt.dnd.DragGestureEvent((java.awt.dnd.DragGestureRecognizer) null, (int) 0, (java.awt.Point) null, (java.util.List) null); }
    static void call1(java.awt.dnd.DragGestureEvent receiver)  { java.awt.dnd.DragGestureRecognizer value = receiver.getSourceAsDragGestureRecognizer(); }
    static void call2(java.awt.dnd.DragGestureEvent receiver)  { java.awt.Component value = receiver.getComponent(); }
    static void call3(java.awt.dnd.DragGestureEvent receiver)  { java.awt.dnd.DragSource value = receiver.getDragSource(); }
    static void call4(java.awt.dnd.DragGestureEvent receiver)  { java.awt.Point value = receiver.getDragOrigin(); }
    static void call5(java.awt.dnd.DragGestureEvent receiver)  { java.util.Iterator value = receiver.iterator(); }
    static void call6(java.awt.dnd.DragGestureEvent receiver)  { java.lang.Object[] value = receiver.toArray(); }
    static void call7(java.awt.dnd.DragGestureEvent receiver)  { java.lang.Object[] value = receiver.toArray((java.lang.Object[]) null); }
    static void call8(java.awt.dnd.DragGestureEvent receiver)  { int value = receiver.getDragAction(); }
    static void call9(java.awt.dnd.DragGestureEvent receiver)  { java.awt.event.InputEvent value = receiver.getTriggerEvent(); }
    static void call10(java.awt.dnd.DragGestureEvent receiver) throws java.awt.dnd.InvalidDnDOperationException { try { receiver.startDrag((java.awt.Cursor) null, (java.awt.datatransfer.Transferable) null); } catch (java.awt.dnd.InvalidDnDOperationException expected) { throw expected; } }
    static void call11(java.awt.dnd.DragGestureEvent receiver) throws java.awt.dnd.InvalidDnDOperationException { try { receiver.startDrag((java.awt.Cursor) null, (java.awt.datatransfer.Transferable) null, (java.awt.dnd.DragSourceListener) null); } catch (java.awt.dnd.InvalidDnDOperationException expected) { throw expected; } }
    static void call12(java.awt.dnd.DragGestureEvent receiver) throws java.awt.dnd.InvalidDnDOperationException { try { receiver.startDrag((java.awt.Cursor) null, (java.awt.Image) null, (java.awt.Point) null, (java.awt.datatransfer.Transferable) null, (java.awt.dnd.DragSourceListener) null); } catch (java.awt.dnd.InvalidDnDOperationException expected) { throw expected; } }
}

abstract class Override_java_awt_dnd_DragGestureEvent extends java.awt.dnd.DragGestureEvent {
    Override_java_awt_dnd_DragGestureEvent()  { super((java.awt.dnd.DragGestureRecognizer) null, (int) 0, (java.awt.Point) null, (java.util.List) null); }
    @Override
    public java.awt.dnd.DragGestureRecognizer getSourceAsDragGestureRecognizer()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Component getComponent()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.dnd.DragSource getDragSource()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Point getDragOrigin()  { throw new UnsupportedOperationException(); }
    @Override
    public java.util.Iterator<java.awt.event.InputEvent> iterator()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Object[] toArray()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Object[] toArray(java.lang.Object[] arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int getDragAction()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.event.InputEvent getTriggerEvent()  { throw new UnsupportedOperationException(); }
    @Override
    public void startDrag(java.awt.Cursor arg0, java.awt.datatransfer.Transferable arg1) throws java.awt.dnd.InvalidDnDOperationException { throw new UnsupportedOperationException(); }
    @Override
    public void startDrag(java.awt.Cursor arg0, java.awt.datatransfer.Transferable arg1, java.awt.dnd.DragSourceListener arg2) throws java.awt.dnd.InvalidDnDOperationException { throw new UnsupportedOperationException(); }
    @Override
    public void startDrag(java.awt.Cursor arg0, java.awt.Image arg1, java.awt.Point arg2, java.awt.datatransfer.Transferable arg3, java.awt.dnd.DragSourceListener arg4) throws java.awt.dnd.InvalidDnDOperationException { throw new UnsupportedOperationException(); }
}
