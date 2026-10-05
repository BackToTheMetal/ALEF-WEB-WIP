package java.awt.event;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_event_AdjustmentEvent {
    static void call0() { int value = java.awt.event.AdjustmentEvent.ADJUSTMENT_FIRST; }
    static void call1() { int value = java.awt.event.AdjustmentEvent.ADJUSTMENT_LAST; }
    static void call2() { int value = java.awt.event.AdjustmentEvent.ADJUSTMENT_VALUE_CHANGED; }
    static void call3() { int value = java.awt.event.AdjustmentEvent.UNIT_INCREMENT; }
    static void call4() { int value = java.awt.event.AdjustmentEvent.UNIT_DECREMENT; }
    static void call5() { int value = java.awt.event.AdjustmentEvent.BLOCK_DECREMENT; }
    static void call6() { int value = java.awt.event.AdjustmentEvent.BLOCK_INCREMENT; }
    static void call7() { int value = java.awt.event.AdjustmentEvent.TRACK; }
    static void call8()  { java.awt.event.AdjustmentEvent value = new java.awt.event.AdjustmentEvent((java.awt.Adjustable) null, (int) 0, (int) 0, (int) 0); }
    static void call9()  { java.awt.event.AdjustmentEvent value = new java.awt.event.AdjustmentEvent((java.awt.Adjustable) null, (int) 0, (int) 0, (int) 0, false); }
    static void call10(java.awt.event.AdjustmentEvent receiver)  { java.awt.Adjustable value = receiver.getAdjustable(); }
    static void call11(java.awt.event.AdjustmentEvent receiver)  { int value = receiver.getValue(); }
    static void call12(java.awt.event.AdjustmentEvent receiver)  { int value = receiver.getAdjustmentType(); }
    static void call13(java.awt.event.AdjustmentEvent receiver)  { boolean value = receiver.getValueIsAdjusting(); }
    static void call14(java.awt.event.AdjustmentEvent receiver)  { java.lang.String value = receiver.paramString(); }
}

abstract class Override_java_awt_event_AdjustmentEvent extends java.awt.event.AdjustmentEvent {
    Override_java_awt_event_AdjustmentEvent()  { super((java.awt.Adjustable) null, (int) 0, (int) 0, (int) 0); }
    @Override
    public java.awt.Adjustable getAdjustable()  { throw new UnsupportedOperationException(); }
    @Override
    public int getValue()  { throw new UnsupportedOperationException(); }
    @Override
    public int getAdjustmentType()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean getValueIsAdjusting()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String paramString()  { throw new UnsupportedOperationException(); }
}
