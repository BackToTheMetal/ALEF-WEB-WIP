package java.awt.event;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_event_MouseEvent {
    static void call0() { int value = java.awt.event.MouseEvent.MOUSE_FIRST; }
    static void call1() { int value = java.awt.event.MouseEvent.MOUSE_LAST; }
    static void call2() { int value = java.awt.event.MouseEvent.MOUSE_CLICKED; }
    static void call3() { int value = java.awt.event.MouseEvent.MOUSE_PRESSED; }
    static void call4() { int value = java.awt.event.MouseEvent.MOUSE_RELEASED; }
    static void call5() { int value = java.awt.event.MouseEvent.MOUSE_MOVED; }
    static void call6() { int value = java.awt.event.MouseEvent.MOUSE_ENTERED; }
    static void call7() { int value = java.awt.event.MouseEvent.MOUSE_EXITED; }
    static void call8() { int value = java.awt.event.MouseEvent.MOUSE_DRAGGED; }
    static void call9() { int value = java.awt.event.MouseEvent.MOUSE_WHEEL; }
    static void call10() { int value = java.awt.event.MouseEvent.NOBUTTON; }
    static void call11() { int value = java.awt.event.MouseEvent.BUTTON1; }
    static void call12() { int value = java.awt.event.MouseEvent.BUTTON2; }
    static void call13() { int value = java.awt.event.MouseEvent.BUTTON3; }
    static void call14(java.awt.event.MouseEvent receiver)  { java.awt.Point value = receiver.getLocationOnScreen(); }
    static void call15(java.awt.event.MouseEvent receiver)  { int value = receiver.getXOnScreen(); }
    static void call16(java.awt.event.MouseEvent receiver)  { int value = receiver.getYOnScreen(); }
    static void call17()  { java.awt.event.MouseEvent value = new java.awt.event.MouseEvent((java.awt.Component) null, (int) 0, (long) 0, (int) 0, (int) 0, (int) 0, (int) 0, false, (int) 0); }
    static void call18()  { java.awt.event.MouseEvent value = new java.awt.event.MouseEvent((java.awt.Component) null, (int) 0, (long) 0, (int) 0, (int) 0, (int) 0, (int) 0, false); }
    static void call19(java.awt.event.MouseEvent receiver)  { int value = receiver.getModifiersEx(); }
    static void call20()  { java.awt.event.MouseEvent value = new java.awt.event.MouseEvent((java.awt.Component) null, (int) 0, (long) 0, (int) 0, (int) 0, (int) 0, (int) 0, (int) 0, (int) 0, false, (int) 0); }
    static void call21(java.awt.event.MouseEvent receiver)  { int value = receiver.getX(); }
    static void call22(java.awt.event.MouseEvent receiver)  { int value = receiver.getY(); }
    static void call23(java.awt.event.MouseEvent receiver)  { java.awt.Point value = receiver.getPoint(); }
    static void call24(java.awt.event.MouseEvent receiver)  { receiver.translatePoint((int) 0, (int) 0); }
    static void call25(java.awt.event.MouseEvent receiver)  { int value = receiver.getClickCount(); }
    static void call26(java.awt.event.MouseEvent receiver)  { int value = receiver.getButton(); }
    static void call27(java.awt.event.MouseEvent receiver)  { boolean value = receiver.isPopupTrigger(); }
    static void call28()  { java.lang.String value = java.awt.event.MouseEvent.getMouseModifiersText((int) 0); }
    static void call29(java.awt.event.MouseEvent receiver)  { java.lang.String value = receiver.paramString(); }
}

abstract class Override_java_awt_event_MouseEvent extends java.awt.event.MouseEvent {
    Override_java_awt_event_MouseEvent()  { super((java.awt.Component) null, (int) 0, (long) 0, (int) 0, (int) 0, (int) 0, (int) 0, false, (int) 0); }
    @Override
    public java.awt.Point getLocationOnScreen()  { throw new UnsupportedOperationException(); }
    @Override
    public int getXOnScreen()  { throw new UnsupportedOperationException(); }
    @Override
    public int getYOnScreen()  { throw new UnsupportedOperationException(); }
    @Override
    public int getModifiersEx()  { throw new UnsupportedOperationException(); }
    @Override
    public int getX()  { throw new UnsupportedOperationException(); }
    @Override
    public int getY()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Point getPoint()  { throw new UnsupportedOperationException(); }
    @Override
    public void translatePoint(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public int getClickCount()  { throw new UnsupportedOperationException(); }
    @Override
    public int getButton()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isPopupTrigger()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String paramString()  { throw new UnsupportedOperationException(); }
}
