package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_Frame {
    static void call0() { int value = java.awt.Frame.DEFAULT_CURSOR; }
    static void call1() { int value = java.awt.Frame.CROSSHAIR_CURSOR; }
    static void call2() { int value = java.awt.Frame.TEXT_CURSOR; }
    static void call3() { int value = java.awt.Frame.WAIT_CURSOR; }
    static void call4() { int value = java.awt.Frame.SW_RESIZE_CURSOR; }
    static void call5() { int value = java.awt.Frame.SE_RESIZE_CURSOR; }
    static void call6() { int value = java.awt.Frame.NW_RESIZE_CURSOR; }
    static void call7() { int value = java.awt.Frame.NE_RESIZE_CURSOR; }
    static void call8() { int value = java.awt.Frame.N_RESIZE_CURSOR; }
    static void call9() { int value = java.awt.Frame.S_RESIZE_CURSOR; }
    static void call10() { int value = java.awt.Frame.W_RESIZE_CURSOR; }
    static void call11() { int value = java.awt.Frame.E_RESIZE_CURSOR; }
    static void call12() { int value = java.awt.Frame.HAND_CURSOR; }
    static void call13() { int value = java.awt.Frame.MOVE_CURSOR; }
    static void call14() { int value = java.awt.Frame.NORMAL; }
    static void call15() { int value = java.awt.Frame.ICONIFIED; }
    static void call16() { int value = java.awt.Frame.MAXIMIZED_HORIZ; }
    static void call17() { int value = java.awt.Frame.MAXIMIZED_VERT; }
    static void call18() { int value = java.awt.Frame.MAXIMIZED_BOTH; }
    static void call19() throws java.awt.HeadlessException { try { java.awt.Frame value = new java.awt.Frame(); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call20()  { java.awt.Frame value = new java.awt.Frame((java.awt.GraphicsConfiguration) null); }
    static void call21() throws java.awt.HeadlessException { try { java.awt.Frame value = new java.awt.Frame((java.lang.String) null); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call22()  { java.awt.Frame value = new java.awt.Frame((java.lang.String) null, (java.awt.GraphicsConfiguration) null); }
    static void call23(java.awt.Frame receiver)  { receiver.addNotify(); }
    static void call24(java.awt.Frame receiver)  { java.lang.String value = receiver.getTitle(); }
    static void call25(java.awt.Frame receiver)  { receiver.setTitle((java.lang.String) null); }
    static void call26(java.awt.Frame receiver)  { java.awt.Image value = receiver.getIconImage(); }
    static void call27(java.awt.Frame receiver)  { receiver.setIconImage((java.awt.Image) null); }
    static void call28(java.awt.Frame receiver)  { java.awt.MenuBar value = receiver.getMenuBar(); }
    static void call29(java.awt.Frame receiver)  { receiver.setMenuBar((java.awt.MenuBar) null); }
    static void call30(java.awt.Frame receiver)  { boolean value = receiver.isResizable(); }
    static void call31(java.awt.Frame receiver)  { receiver.setResizable(false); }
    static void call32(java.awt.Frame receiver)  { receiver.setState((int) 0); }
    static void call33(java.awt.Frame receiver)  { receiver.setExtendedState((int) 0); }
    static void call34(java.awt.Frame receiver)  { int value = receiver.getState(); }
    static void call35(java.awt.Frame receiver)  { int value = receiver.getExtendedState(); }
    static void call36(java.awt.Frame receiver)  { receiver.setMaximizedBounds((java.awt.Rectangle) null); }
    static void call37(java.awt.Frame receiver)  { java.awt.Rectangle value = receiver.getMaximizedBounds(); }
    static void call38(java.awt.Frame receiver)  { receiver.setUndecorated(false); }
    static void call39(java.awt.Frame receiver)  { boolean value = receiver.isUndecorated(); }
    static void call40(java.awt.Frame receiver)  { receiver.setOpacity((float) 0); }
    static void call41(java.awt.Frame receiver)  { receiver.setShape((java.awt.Shape) null); }
    static void call42(java.awt.Frame receiver)  { receiver.setBackground((java.awt.Color) null); }
    static void call43(java.awt.Frame receiver)  { receiver.remove((java.awt.MenuComponent) null); }
    static void call44(java.awt.Frame receiver)  { receiver.removeNotify(); }
    static void call45(java.awt.Frame receiver)  { java.lang.String value = receiver.paramString(); }
    static void call46(java.awt.Frame receiver)  { receiver.setCursor((int) 0); }
    static void call47(java.awt.Frame receiver)  { int value = receiver.getCursorType(); }
    static void call48()  { java.awt.Frame[] value = java.awt.Frame.getFrames(); }
    static void call49(java.awt.Frame receiver)  { javax.accessibility.AccessibleContext value = receiver.getAccessibleContext(); }
}

abstract class Override_java_awt_Frame extends java.awt.Frame {
    Override_java_awt_Frame() throws java.awt.HeadlessException { super(); }
    @Override
    public void addNotify()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String getTitle()  { throw new UnsupportedOperationException(); }
    @Override
    public void setTitle(java.lang.String arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Image getIconImage()  { throw new UnsupportedOperationException(); }
    @Override
    public void setIconImage(java.awt.Image arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.MenuBar getMenuBar()  { throw new UnsupportedOperationException(); }
    @Override
    public void setMenuBar(java.awt.MenuBar arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isResizable()  { throw new UnsupportedOperationException(); }
    @Override
    public void setResizable(boolean arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setState(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setExtendedState(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int getState()  { throw new UnsupportedOperationException(); }
    @Override
    public int getExtendedState()  { throw new UnsupportedOperationException(); }
    @Override
    public void setMaximizedBounds(java.awt.Rectangle arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Rectangle getMaximizedBounds()  { throw new UnsupportedOperationException(); }
    @Override
    public void setUndecorated(boolean arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isUndecorated()  { throw new UnsupportedOperationException(); }
    @Override
    public void setOpacity(float arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setShape(java.awt.Shape arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setBackground(java.awt.Color arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void remove(java.awt.MenuComponent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void removeNotify()  { throw new UnsupportedOperationException(); }
    @Override
    protected java.lang.String paramString()  { throw new UnsupportedOperationException(); }
    @Override
    public void setCursor(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int getCursorType()  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.AccessibleContext getAccessibleContext()  { throw new UnsupportedOperationException(); }
}
