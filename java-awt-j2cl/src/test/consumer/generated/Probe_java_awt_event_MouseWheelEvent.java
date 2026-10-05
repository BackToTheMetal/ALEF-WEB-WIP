package java.awt.event;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_event_MouseWheelEvent {
    static void call0() { int value = java.awt.event.MouseWheelEvent.WHEEL_UNIT_SCROLL; }
    static void call1() { int value = java.awt.event.MouseWheelEvent.WHEEL_BLOCK_SCROLL; }
    static void call2()  { java.awt.event.MouseWheelEvent value = new java.awt.event.MouseWheelEvent((java.awt.Component) null, (int) 0, (long) 0, (int) 0, (int) 0, (int) 0, (int) 0, false, (int) 0, (int) 0, (int) 0); }
    static void call3()  { java.awt.event.MouseWheelEvent value = new java.awt.event.MouseWheelEvent((java.awt.Component) null, (int) 0, (long) 0, (int) 0, (int) 0, (int) 0, (int) 0, (int) 0, (int) 0, false, (int) 0, (int) 0, (int) 0); }
    static void call4()  { java.awt.event.MouseWheelEvent value = new java.awt.event.MouseWheelEvent((java.awt.Component) null, (int) 0, (long) 0, (int) 0, (int) 0, (int) 0, (int) 0, (int) 0, (int) 0, false, (int) 0, (int) 0, (int) 0, (double) 0); }
    static void call5(java.awt.event.MouseWheelEvent receiver)  { int value = receiver.getScrollType(); }
    static void call6(java.awt.event.MouseWheelEvent receiver)  { int value = receiver.getScrollAmount(); }
    static void call7(java.awt.event.MouseWheelEvent receiver)  { int value = receiver.getWheelRotation(); }
    static void call8(java.awt.event.MouseWheelEvent receiver)  { double value = receiver.getPreciseWheelRotation(); }
    static void call9(java.awt.event.MouseWheelEvent receiver)  { int value = receiver.getUnitsToScroll(); }
    static void call10(java.awt.event.MouseWheelEvent receiver)  { java.lang.String value = receiver.paramString(); }
}

abstract class Override_java_awt_event_MouseWheelEvent extends java.awt.event.MouseWheelEvent {
    Override_java_awt_event_MouseWheelEvent()  { super((java.awt.Component) null, (int) 0, (long) 0, (int) 0, (int) 0, (int) 0, (int) 0, false, (int) 0, (int) 0, (int) 0); }
    @Override
    public int getScrollType()  { throw new UnsupportedOperationException(); }
    @Override
    public int getScrollAmount()  { throw new UnsupportedOperationException(); }
    @Override
    public int getWheelRotation()  { throw new UnsupportedOperationException(); }
    @Override
    public double getPreciseWheelRotation()  { throw new UnsupportedOperationException(); }
    @Override
    public int getUnitsToScroll()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String paramString()  { throw new UnsupportedOperationException(); }
}
