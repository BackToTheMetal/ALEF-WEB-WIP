package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_AWTEvent {
    static void call0(java.awt.AWTEvent receiver) { int value = receiver.id; receiver.id = value; }
    static void call1(java.awt.AWTEvent receiver) { boolean value = receiver.consumed; receiver.consumed = value; }
    static void call2() { long value = java.awt.AWTEvent.COMPONENT_EVENT_MASK; }
    static void call3() { long value = java.awt.AWTEvent.CONTAINER_EVENT_MASK; }
    static void call4() { long value = java.awt.AWTEvent.FOCUS_EVENT_MASK; }
    static void call5() { long value = java.awt.AWTEvent.KEY_EVENT_MASK; }
    static void call6() { long value = java.awt.AWTEvent.MOUSE_EVENT_MASK; }
    static void call7() { long value = java.awt.AWTEvent.MOUSE_MOTION_EVENT_MASK; }
    static void call8() { long value = java.awt.AWTEvent.WINDOW_EVENT_MASK; }
    static void call9() { long value = java.awt.AWTEvent.ACTION_EVENT_MASK; }
    static void call10() { long value = java.awt.AWTEvent.ADJUSTMENT_EVENT_MASK; }
    static void call11() { long value = java.awt.AWTEvent.ITEM_EVENT_MASK; }
    static void call12() { long value = java.awt.AWTEvent.TEXT_EVENT_MASK; }
    static void call13() { long value = java.awt.AWTEvent.INPUT_METHOD_EVENT_MASK; }
    static void call14() { long value = java.awt.AWTEvent.PAINT_EVENT_MASK; }
    static void call15() { long value = java.awt.AWTEvent.INVOCATION_EVENT_MASK; }
    static void call16() { long value = java.awt.AWTEvent.HIERARCHY_EVENT_MASK; }
    static void call17() { long value = java.awt.AWTEvent.HIERARCHY_BOUNDS_EVENT_MASK; }
    static void call18() { long value = java.awt.AWTEvent.MOUSE_WHEEL_EVENT_MASK; }
    static void call19() { long value = java.awt.AWTEvent.WINDOW_STATE_EVENT_MASK; }
    static void call20() { long value = java.awt.AWTEvent.WINDOW_FOCUS_EVENT_MASK; }
    static void call21() { int value = java.awt.AWTEvent.RESERVED_ID_MAX; }
    static void call22(java.awt.AWTEvent receiver)  { receiver.setSource((java.lang.Object) null); }
    static void call23(java.awt.AWTEvent receiver)  { int value = receiver.getID(); }
    static void call24(java.awt.AWTEvent receiver)  { java.lang.String value = receiver.toString(); }
    static void call25(java.awt.AWTEvent receiver)  { java.lang.String value = receiver.paramString(); }
    static void call26(java.awt.AWTEvent receiver)  { receiver.consume(); }
    static void call27(java.awt.AWTEvent receiver)  { boolean value = receiver.isConsumed(); }
}

abstract class Override_java_awt_AWTEvent extends java.awt.AWTEvent {
    Override_java_awt_AWTEvent()  { super((java.awt.Event) null); }
    @Override
    public void setSource(java.lang.Object arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int getID()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String toString()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String paramString()  { throw new UnsupportedOperationException(); }
    @Override
    protected void consume()  { throw new UnsupportedOperationException(); }
    @Override
    protected boolean isConsumed()  { throw new UnsupportedOperationException(); }
}
