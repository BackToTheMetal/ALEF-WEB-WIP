package java.awt.event;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_event_ItemEvent {
    static void call0() { int value = java.awt.event.ItemEvent.ITEM_FIRST; }
    static void call1() { int value = java.awt.event.ItemEvent.ITEM_LAST; }
    static void call2() { int value = java.awt.event.ItemEvent.ITEM_STATE_CHANGED; }
    static void call3() { int value = java.awt.event.ItemEvent.SELECTED; }
    static void call4() { int value = java.awt.event.ItemEvent.DESELECTED; }
    static void call5()  { java.awt.event.ItemEvent value = new java.awt.event.ItemEvent((java.awt.ItemSelectable) null, (int) 0, (java.lang.Object) null, (int) 0); }
    static void call6(java.awt.event.ItemEvent receiver)  { java.awt.ItemSelectable value = receiver.getItemSelectable(); }
    static void call7(java.awt.event.ItemEvent receiver)  { java.lang.Object value = receiver.getItem(); }
    static void call8(java.awt.event.ItemEvent receiver)  { int value = receiver.getStateChange(); }
    static void call9(java.awt.event.ItemEvent receiver)  { java.lang.String value = receiver.paramString(); }
}

abstract class Override_java_awt_event_ItemEvent extends java.awt.event.ItemEvent {
    Override_java_awt_event_ItemEvent()  { super((java.awt.ItemSelectable) null, (int) 0, (java.lang.Object) null, (int) 0); }
    @Override
    public java.awt.ItemSelectable getItemSelectable()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Object getItem()  { throw new UnsupportedOperationException(); }
    @Override
    public int getStateChange()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String paramString()  { throw new UnsupportedOperationException(); }
}
