package java.awt.event;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_event_InputEvent {
    static void call0() { int value = java.awt.event.InputEvent.SHIFT_MASK; }
    static void call1() { int value = java.awt.event.InputEvent.CTRL_MASK; }
    static void call2() { int value = java.awt.event.InputEvent.META_MASK; }
    static void call3() { int value = java.awt.event.InputEvent.ALT_MASK; }
    static void call4() { int value = java.awt.event.InputEvent.ALT_GRAPH_MASK; }
    static void call5() { int value = java.awt.event.InputEvent.BUTTON1_MASK; }
    static void call6() { int value = java.awt.event.InputEvent.BUTTON2_MASK; }
    static void call7() { int value = java.awt.event.InputEvent.BUTTON3_MASK; }
    static void call8() { int value = java.awt.event.InputEvent.SHIFT_DOWN_MASK; }
    static void call9() { int value = java.awt.event.InputEvent.CTRL_DOWN_MASK; }
    static void call10() { int value = java.awt.event.InputEvent.META_DOWN_MASK; }
    static void call11() { int value = java.awt.event.InputEvent.ALT_DOWN_MASK; }
    static void call12() { int value = java.awt.event.InputEvent.BUTTON1_DOWN_MASK; }
    static void call13() { int value = java.awt.event.InputEvent.BUTTON2_DOWN_MASK; }
    static void call14() { int value = java.awt.event.InputEvent.BUTTON3_DOWN_MASK; }
    static void call15() { int value = java.awt.event.InputEvent.ALT_GRAPH_DOWN_MASK; }
    static void call16()  { int value = java.awt.event.InputEvent.getMaskForButton((int) 0); }
    static void call17(java.awt.event.InputEvent receiver)  { boolean value = receiver.isShiftDown(); }
    static void call18(java.awt.event.InputEvent receiver)  { boolean value = receiver.isControlDown(); }
    static void call19(java.awt.event.InputEvent receiver)  { boolean value = receiver.isMetaDown(); }
    static void call20(java.awt.event.InputEvent receiver)  { boolean value = receiver.isAltDown(); }
    static void call21(java.awt.event.InputEvent receiver)  { boolean value = receiver.isAltGraphDown(); }
    static void call22(java.awt.event.InputEvent receiver)  { long value = receiver.getWhen(); }
    static void call23(java.awt.event.InputEvent receiver)  { int value = receiver.getModifiers(); }
    static void call24(java.awt.event.InputEvent receiver)  { int value = receiver.getModifiersEx(); }
    static void call25(java.awt.event.InputEvent receiver)  { receiver.consume(); }
    static void call26(java.awt.event.InputEvent receiver)  { boolean value = receiver.isConsumed(); }
    static void call27()  { java.lang.String value = java.awt.event.InputEvent.getModifiersExText((int) 0); }
}
