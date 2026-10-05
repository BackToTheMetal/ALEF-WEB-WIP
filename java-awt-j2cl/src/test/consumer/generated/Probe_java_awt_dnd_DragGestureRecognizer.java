package java.awt.dnd;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_dnd_DragGestureRecognizer {
    static void call0(java.awt.dnd.DragGestureRecognizer receiver) { java.awt.dnd.DragSource value = receiver.dragSource; receiver.dragSource = value; }
    static void call1(java.awt.dnd.DragGestureRecognizer receiver) { java.awt.Component value = receiver.component; receiver.component = value; }
    static void call2(java.awt.dnd.DragGestureRecognizer receiver) { java.awt.dnd.DragGestureListener value = receiver.dragGestureListener; receiver.dragGestureListener = value; }
    static void call3(java.awt.dnd.DragGestureRecognizer receiver) { int value = receiver.sourceActions; receiver.sourceActions = value; }
    static void call4(java.awt.dnd.DragGestureRecognizer receiver) { java.util.ArrayList value = receiver.events; receiver.events = value; }
    static void call5(java.awt.dnd.DragGestureRecognizer receiver)  { receiver.registerListeners(); }
    static void call6(java.awt.dnd.DragGestureRecognizer receiver)  { receiver.unregisterListeners(); }
    static void call7(java.awt.dnd.DragGestureRecognizer receiver)  { java.awt.dnd.DragSource value = receiver.getDragSource(); }
    static void call8(java.awt.dnd.DragGestureRecognizer receiver)  { java.awt.Component value = receiver.getComponent(); }
    static void call9(java.awt.dnd.DragGestureRecognizer receiver)  { receiver.setComponent((java.awt.Component) null); }
    static void call10(java.awt.dnd.DragGestureRecognizer receiver)  { int value = receiver.getSourceActions(); }
    static void call11(java.awt.dnd.DragGestureRecognizer receiver)  { receiver.setSourceActions((int) 0); }
    static void call12(java.awt.dnd.DragGestureRecognizer receiver)  { java.awt.event.InputEvent value = receiver.getTriggerEvent(); }
    static void call13(java.awt.dnd.DragGestureRecognizer receiver)  { receiver.resetRecognizer(); }
    static void call14(java.awt.dnd.DragGestureRecognizer receiver) throws java.util.TooManyListenersException { try { receiver.addDragGestureListener((java.awt.dnd.DragGestureListener) null); } catch (java.util.TooManyListenersException expected) { throw expected; } }
    static void call15(java.awt.dnd.DragGestureRecognizer receiver)  { receiver.removeDragGestureListener((java.awt.dnd.DragGestureListener) null); }
    static void call16(java.awt.dnd.DragGestureRecognizer receiver)  { receiver.fireDragGestureRecognized((int) 0, (java.awt.Point) null); }
    static void call17(java.awt.dnd.DragGestureRecognizer receiver)  { receiver.appendEvent((java.awt.event.InputEvent) null); }
}

abstract class Override_java_awt_dnd_DragGestureRecognizer extends java.awt.dnd.DragGestureRecognizer {
    Override_java_awt_dnd_DragGestureRecognizer()  { super((java.awt.dnd.DragSource) null, (java.awt.Component) null, (int) 0, (java.awt.dnd.DragGestureListener) null); }
    @Override
    protected void registerListeners()  { throw new UnsupportedOperationException(); }
    @Override
    protected void unregisterListeners()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.dnd.DragSource getDragSource()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Component getComponent()  { throw new UnsupportedOperationException(); }
    @Override
    public void setComponent(java.awt.Component arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int getSourceActions()  { throw new UnsupportedOperationException(); }
    @Override
    public void setSourceActions(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.event.InputEvent getTriggerEvent()  { throw new UnsupportedOperationException(); }
    @Override
    public void resetRecognizer()  { throw new UnsupportedOperationException(); }
    @Override
    public void addDragGestureListener(java.awt.dnd.DragGestureListener arg0) throws java.util.TooManyListenersException { throw new UnsupportedOperationException(); }
    @Override
    public void removeDragGestureListener(java.awt.dnd.DragGestureListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    protected void fireDragGestureRecognized(int arg0, java.awt.Point arg1)  { throw new UnsupportedOperationException(); }
    @Override
    protected void appendEvent(java.awt.event.InputEvent arg0)  { throw new UnsupportedOperationException(); }
}
