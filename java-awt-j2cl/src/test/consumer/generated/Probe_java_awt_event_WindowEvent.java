package java.awt.event;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_event_WindowEvent {
    static void call0() { int value = java.awt.event.WindowEvent.WINDOW_FIRST; }
    static void call1() { int value = java.awt.event.WindowEvent.WINDOW_OPENED; }
    static void call2() { int value = java.awt.event.WindowEvent.WINDOW_CLOSING; }
    static void call3() { int value = java.awt.event.WindowEvent.WINDOW_CLOSED; }
    static void call4() { int value = java.awt.event.WindowEvent.WINDOW_ICONIFIED; }
    static void call5() { int value = java.awt.event.WindowEvent.WINDOW_DEICONIFIED; }
    static void call6() { int value = java.awt.event.WindowEvent.WINDOW_ACTIVATED; }
    static void call7() { int value = java.awt.event.WindowEvent.WINDOW_DEACTIVATED; }
    static void call8() { int value = java.awt.event.WindowEvent.WINDOW_GAINED_FOCUS; }
    static void call9() { int value = java.awt.event.WindowEvent.WINDOW_LOST_FOCUS; }
    static void call10() { int value = java.awt.event.WindowEvent.WINDOW_STATE_CHANGED; }
    static void call11() { int value = java.awt.event.WindowEvent.WINDOW_LAST; }
    static void call12()  { java.awt.event.WindowEvent value = new java.awt.event.WindowEvent((java.awt.Window) null, (int) 0, (java.awt.Window) null, (int) 0, (int) 0); }
    static void call13()  { java.awt.event.WindowEvent value = new java.awt.event.WindowEvent((java.awt.Window) null, (int) 0, (java.awt.Window) null); }
    static void call14()  { java.awt.event.WindowEvent value = new java.awt.event.WindowEvent((java.awt.Window) null, (int) 0, (int) 0, (int) 0); }
    static void call15()  { java.awt.event.WindowEvent value = new java.awt.event.WindowEvent((java.awt.Window) null, (int) 0); }
    static void call16(java.awt.event.WindowEvent receiver)  { java.awt.Window value = receiver.getWindow(); }
    static void call17(java.awt.event.WindowEvent receiver)  { java.awt.Window value = receiver.getOppositeWindow(); }
    static void call18(java.awt.event.WindowEvent receiver)  { int value = receiver.getOldState(); }
    static void call19(java.awt.event.WindowEvent receiver)  { int value = receiver.getNewState(); }
    static void call20(java.awt.event.WindowEvent receiver)  { java.lang.String value = receiver.paramString(); }
}

abstract class Override_java_awt_event_WindowEvent extends java.awt.event.WindowEvent {
    Override_java_awt_event_WindowEvent()  { super((java.awt.Window) null, (int) 0, (java.awt.Window) null, (int) 0, (int) 0); }
    @Override
    public java.awt.Window getWindow()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Window getOppositeWindow()  { throw new UnsupportedOperationException(); }
    @Override
    public int getOldState()  { throw new UnsupportedOperationException(); }
    @Override
    public int getNewState()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String paramString()  { throw new UnsupportedOperationException(); }
}
