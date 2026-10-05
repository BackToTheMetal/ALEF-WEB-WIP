package java.awt.event;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_event_PaintEvent {
    static void call0() { int value = java.awt.event.PaintEvent.PAINT_FIRST; }
    static void call1() { int value = java.awt.event.PaintEvent.PAINT_LAST; }
    static void call2() { int value = java.awt.event.PaintEvent.PAINT; }
    static void call3() { int value = java.awt.event.PaintEvent.UPDATE; }
    static void call4()  { java.awt.event.PaintEvent value = new java.awt.event.PaintEvent((java.awt.Component) null, (int) 0, (java.awt.Rectangle) null); }
    static void call5(java.awt.event.PaintEvent receiver)  { java.awt.Rectangle value = receiver.getUpdateRect(); }
    static void call6(java.awt.event.PaintEvent receiver)  { receiver.setUpdateRect((java.awt.Rectangle) null); }
    static void call7(java.awt.event.PaintEvent receiver)  { java.lang.String value = receiver.paramString(); }
}

abstract class Override_java_awt_event_PaintEvent extends java.awt.event.PaintEvent {
    Override_java_awt_event_PaintEvent()  { super((java.awt.Component) null, (int) 0, (java.awt.Rectangle) null); }
    @Override
    public java.awt.Rectangle getUpdateRect()  { throw new UnsupportedOperationException(); }
    @Override
    public void setUpdateRect(java.awt.Rectangle arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String paramString()  { throw new UnsupportedOperationException(); }
}
