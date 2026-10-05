package java.awt.dnd;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_dnd_DragSourceContext {
    static void call0() { int value = java.awt.dnd.DragSourceContext.DEFAULT; }
    static void call1() { int value = java.awt.dnd.DragSourceContext.ENTER; }
    static void call2() { int value = java.awt.dnd.DragSourceContext.OVER; }
    static void call3() { int value = java.awt.dnd.DragSourceContext.CHANGED; }
    static void call4()  { java.awt.dnd.DragSourceContext value = new java.awt.dnd.DragSourceContext((java.awt.dnd.DragGestureEvent) null, (java.awt.Cursor) null, (java.awt.Image) null, (java.awt.Point) null, (java.awt.datatransfer.Transferable) null, (java.awt.dnd.DragSourceListener) null); }
    static void call5(java.awt.dnd.DragSourceContext receiver)  { java.awt.dnd.DragSource value = receiver.getDragSource(); }
    static void call6(java.awt.dnd.DragSourceContext receiver)  { java.awt.Component value = receiver.getComponent(); }
    static void call7(java.awt.dnd.DragSourceContext receiver)  { java.awt.dnd.DragGestureEvent value = receiver.getTrigger(); }
    static void call8(java.awt.dnd.DragSourceContext receiver)  { int value = receiver.getSourceActions(); }
    static void call9(java.awt.dnd.DragSourceContext receiver)  { receiver.setCursor((java.awt.Cursor) null); }
    static void call10(java.awt.dnd.DragSourceContext receiver)  { java.awt.Cursor value = receiver.getCursor(); }
    static void call11(java.awt.dnd.DragSourceContext receiver) throws java.util.TooManyListenersException { try { receiver.addDragSourceListener((java.awt.dnd.DragSourceListener) null); } catch (java.util.TooManyListenersException expected) { throw expected; } }
    static void call12(java.awt.dnd.DragSourceContext receiver)  { receiver.removeDragSourceListener((java.awt.dnd.DragSourceListener) null); }
    static void call13(java.awt.dnd.DragSourceContext receiver)  { receiver.transferablesFlavorsChanged(); }
    static void call14(java.awt.dnd.DragSourceContext receiver)  { receiver.dragEnter((java.awt.dnd.DragSourceDragEvent) null); }
    static void call15(java.awt.dnd.DragSourceContext receiver)  { receiver.dragOver((java.awt.dnd.DragSourceDragEvent) null); }
    static void call16(java.awt.dnd.DragSourceContext receiver)  { receiver.dragExit((java.awt.dnd.DragSourceEvent) null); }
    static void call17(java.awt.dnd.DragSourceContext receiver)  { receiver.dropActionChanged((java.awt.dnd.DragSourceDragEvent) null); }
    static void call18(java.awt.dnd.DragSourceContext receiver)  { receiver.dragDropEnd((java.awt.dnd.DragSourceDropEvent) null); }
    static void call19(java.awt.dnd.DragSourceContext receiver)  { receiver.dragMouseMoved((java.awt.dnd.DragSourceDragEvent) null); }
    static void call20(java.awt.dnd.DragSourceContext receiver)  { java.awt.datatransfer.Transferable value = receiver.getTransferable(); }
}

abstract class Override_java_awt_dnd_DragSourceContext extends java.awt.dnd.DragSourceContext {
    Override_java_awt_dnd_DragSourceContext()  { super((java.awt.dnd.DragGestureEvent) null, (java.awt.Cursor) null, (java.awt.Image) null, (java.awt.Point) null, (java.awt.datatransfer.Transferable) null, (java.awt.dnd.DragSourceListener) null); }
    @Override
    public java.awt.dnd.DragSource getDragSource()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Component getComponent()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.dnd.DragGestureEvent getTrigger()  { throw new UnsupportedOperationException(); }
    @Override
    public int getSourceActions()  { throw new UnsupportedOperationException(); }
    @Override
    public void setCursor(java.awt.Cursor arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Cursor getCursor()  { throw new UnsupportedOperationException(); }
    @Override
    public void addDragSourceListener(java.awt.dnd.DragSourceListener arg0) throws java.util.TooManyListenersException { throw new UnsupportedOperationException(); }
    @Override
    public void removeDragSourceListener(java.awt.dnd.DragSourceListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void transferablesFlavorsChanged()  { throw new UnsupportedOperationException(); }
    @Override
    public void dragEnter(java.awt.dnd.DragSourceDragEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void dragOver(java.awt.dnd.DragSourceDragEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void dragExit(java.awt.dnd.DragSourceEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void dropActionChanged(java.awt.dnd.DragSourceDragEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void dragDropEnd(java.awt.dnd.DragSourceDropEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void dragMouseMoved(java.awt.dnd.DragSourceDragEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.datatransfer.Transferable getTransferable()  { throw new UnsupportedOperationException(); }
}
