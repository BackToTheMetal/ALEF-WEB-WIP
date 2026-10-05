package java.awt.dnd;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_dnd_DragSource {
    static void call0() { java.awt.Cursor value = java.awt.dnd.DragSource.DefaultCopyDrop; }
    static void call1() { java.awt.Cursor value = java.awt.dnd.DragSource.DefaultMoveDrop; }
    static void call2() { java.awt.Cursor value = java.awt.dnd.DragSource.DefaultLinkDrop; }
    static void call3() { java.awt.Cursor value = java.awt.dnd.DragSource.DefaultCopyNoDrop; }
    static void call4() { java.awt.Cursor value = java.awt.dnd.DragSource.DefaultMoveNoDrop; }
    static void call5() { java.awt.Cursor value = java.awt.dnd.DragSource.DefaultLinkNoDrop; }
    static void call6()  { java.awt.dnd.DragSource value = java.awt.dnd.DragSource.getDefaultDragSource(); }
    static void call7()  { boolean value = java.awt.dnd.DragSource.isDragImageSupported(); }
    static void call8() throws java.awt.HeadlessException { try { java.awt.dnd.DragSource value = new java.awt.dnd.DragSource(); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call9(java.awt.dnd.DragSource receiver) throws java.awt.dnd.InvalidDnDOperationException { try { receiver.startDrag((java.awt.dnd.DragGestureEvent) null, (java.awt.Cursor) null, (java.awt.Image) null, (java.awt.Point) null, (java.awt.datatransfer.Transferable) null, (java.awt.dnd.DragSourceListener) null, (java.awt.datatransfer.FlavorMap) null); } catch (java.awt.dnd.InvalidDnDOperationException expected) { throw expected; } }
    static void call10(java.awt.dnd.DragSource receiver) throws java.awt.dnd.InvalidDnDOperationException { try { receiver.startDrag((java.awt.dnd.DragGestureEvent) null, (java.awt.Cursor) null, (java.awt.datatransfer.Transferable) null, (java.awt.dnd.DragSourceListener) null, (java.awt.datatransfer.FlavorMap) null); } catch (java.awt.dnd.InvalidDnDOperationException expected) { throw expected; } }
    static void call11(java.awt.dnd.DragSource receiver) throws java.awt.dnd.InvalidDnDOperationException { try { receiver.startDrag((java.awt.dnd.DragGestureEvent) null, (java.awt.Cursor) null, (java.awt.Image) null, (java.awt.Point) null, (java.awt.datatransfer.Transferable) null, (java.awt.dnd.DragSourceListener) null); } catch (java.awt.dnd.InvalidDnDOperationException expected) { throw expected; } }
    static void call12(java.awt.dnd.DragSource receiver) throws java.awt.dnd.InvalidDnDOperationException { try { receiver.startDrag((java.awt.dnd.DragGestureEvent) null, (java.awt.Cursor) null, (java.awt.datatransfer.Transferable) null, (java.awt.dnd.DragSourceListener) null); } catch (java.awt.dnd.InvalidDnDOperationException expected) { throw expected; } }
    static void call13(java.awt.dnd.DragSource receiver)  { java.awt.dnd.DragSourceContext value = receiver.createDragSourceContext((java.awt.dnd.DragGestureEvent) null, (java.awt.Cursor) null, (java.awt.Image) null, (java.awt.Point) null, (java.awt.datatransfer.Transferable) null, (java.awt.dnd.DragSourceListener) null); }
    static void call14(java.awt.dnd.DragSource receiver)  { java.awt.datatransfer.FlavorMap value = receiver.getFlavorMap(); }
    static void call15(java.awt.dnd.DragSource receiver)  { java.awt.dnd.DragGestureRecognizer value = receiver.createDragGestureRecognizer((java.lang.Class) null, (java.awt.Component) null, (int) 0, (java.awt.dnd.DragGestureListener) null); }
    static void call16(java.awt.dnd.DragSource receiver)  { java.awt.dnd.DragGestureRecognizer value = receiver.createDefaultDragGestureRecognizer((java.awt.Component) null, (int) 0, (java.awt.dnd.DragGestureListener) null); }
    static void call17(java.awt.dnd.DragSource receiver)  { receiver.addDragSourceListener((java.awt.dnd.DragSourceListener) null); }
    static void call18(java.awt.dnd.DragSource receiver)  { receiver.removeDragSourceListener((java.awt.dnd.DragSourceListener) null); }
    static void call19(java.awt.dnd.DragSource receiver)  { java.awt.dnd.DragSourceListener[] value = receiver.getDragSourceListeners(); }
    static void call20(java.awt.dnd.DragSource receiver)  { receiver.addDragSourceMotionListener((java.awt.dnd.DragSourceMotionListener) null); }
    static void call21(java.awt.dnd.DragSource receiver)  { receiver.removeDragSourceMotionListener((java.awt.dnd.DragSourceMotionListener) null); }
    static void call22(java.awt.dnd.DragSource receiver)  { java.awt.dnd.DragSourceMotionListener[] value = receiver.getDragSourceMotionListeners(); }
    static void call23(java.awt.dnd.DragSource receiver)  { java.util.EventListener[] value = receiver.getListeners((java.lang.Class) null); }
    static void call24()  { int value = java.awt.dnd.DragSource.getDragThreshold(); }
}

abstract class Override_java_awt_dnd_DragSource extends java.awt.dnd.DragSource {
    Override_java_awt_dnd_DragSource() throws java.awt.HeadlessException { super(); }
    @Override
    public void startDrag(java.awt.dnd.DragGestureEvent arg0, java.awt.Cursor arg1, java.awt.Image arg2, java.awt.Point arg3, java.awt.datatransfer.Transferable arg4, java.awt.dnd.DragSourceListener arg5, java.awt.datatransfer.FlavorMap arg6) throws java.awt.dnd.InvalidDnDOperationException { throw new UnsupportedOperationException(); }
    @Override
    public void startDrag(java.awt.dnd.DragGestureEvent arg0, java.awt.Cursor arg1, java.awt.datatransfer.Transferable arg2, java.awt.dnd.DragSourceListener arg3, java.awt.datatransfer.FlavorMap arg4) throws java.awt.dnd.InvalidDnDOperationException { throw new UnsupportedOperationException(); }
    @Override
    public void startDrag(java.awt.dnd.DragGestureEvent arg0, java.awt.Cursor arg1, java.awt.Image arg2, java.awt.Point arg3, java.awt.datatransfer.Transferable arg4, java.awt.dnd.DragSourceListener arg5) throws java.awt.dnd.InvalidDnDOperationException { throw new UnsupportedOperationException(); }
    @Override
    public void startDrag(java.awt.dnd.DragGestureEvent arg0, java.awt.Cursor arg1, java.awt.datatransfer.Transferable arg2, java.awt.dnd.DragSourceListener arg3) throws java.awt.dnd.InvalidDnDOperationException { throw new UnsupportedOperationException(); }
    @Override
    protected java.awt.dnd.DragSourceContext createDragSourceContext(java.awt.dnd.DragGestureEvent arg0, java.awt.Cursor arg1, java.awt.Image arg2, java.awt.Point arg3, java.awt.datatransfer.Transferable arg4, java.awt.dnd.DragSourceListener arg5)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.datatransfer.FlavorMap getFlavorMap()  { throw new UnsupportedOperationException(); }
    @Override
    public <T extends java.awt.dnd.DragGestureRecognizer> T createDragGestureRecognizer(java.lang.Class<T> arg0, java.awt.Component arg1, int arg2, java.awt.dnd.DragGestureListener arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.dnd.DragGestureRecognizer createDefaultDragGestureRecognizer(java.awt.Component arg0, int arg1, java.awt.dnd.DragGestureListener arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public void addDragSourceListener(java.awt.dnd.DragSourceListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void removeDragSourceListener(java.awt.dnd.DragSourceListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.dnd.DragSourceListener[] getDragSourceListeners()  { throw new UnsupportedOperationException(); }
    @Override
    public void addDragSourceMotionListener(java.awt.dnd.DragSourceMotionListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void removeDragSourceMotionListener(java.awt.dnd.DragSourceMotionListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.dnd.DragSourceMotionListener[] getDragSourceMotionListeners()  { throw new UnsupportedOperationException(); }
    @Override
    public <T extends java.util.EventListener> T[] getListeners(java.lang.Class<T> arg0)  { throw new UnsupportedOperationException(); }
}
