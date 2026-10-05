package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_Window {
    static void call0()  { java.awt.Window value = new java.awt.Window((java.awt.Frame) null); }
    static void call1()  { java.awt.Window value = new java.awt.Window((java.awt.Window) null); }
    static void call2()  { java.awt.Window value = new java.awt.Window((java.awt.Window) null, (java.awt.GraphicsConfiguration) null); }
    static void call3(java.awt.Window receiver)  { java.util.List value = receiver.getIconImages(); }
    static void call4(java.awt.Window receiver)  { receiver.setIconImages((java.util.List) null); }
    static void call5(java.awt.Window receiver)  { receiver.setIconImage((java.awt.Image) null); }
    static void call6(java.awt.Window receiver)  { receiver.addNotify(); }
    static void call7(java.awt.Window receiver)  { receiver.removeNotify(); }
    static void call8(java.awt.Window receiver)  { receiver.pack(); }
    static void call9(java.awt.Window receiver)  { receiver.setMinimumSize((java.awt.Dimension) null); }
    static void call10(java.awt.Window receiver)  { receiver.setSize((java.awt.Dimension) null); }
    static void call11(java.awt.Window receiver)  { receiver.setSize((int) 0, (int) 0); }
    static void call12(java.awt.Window receiver)  { receiver.setLocation((int) 0, (int) 0); }
    static void call13(java.awt.Window receiver)  { receiver.setLocation((java.awt.Point) null); }
    static void call14(java.awt.Window receiver)  { receiver.reshape((int) 0, (int) 0, (int) 0, (int) 0); }
    static void call15(java.awt.Window receiver)  { receiver.setVisible(false); }
    static void call16(java.awt.Window receiver)  { receiver.show(); }
    static void call17(java.awt.Window receiver)  { receiver.hide(); }
    static void call18(java.awt.Window receiver)  { receiver.dispose(); }
    static void call19(java.awt.Window receiver)  { receiver.toFront(); }
    static void call20(java.awt.Window receiver)  { receiver.toBack(); }
    static void call21(java.awt.Window receiver)  { java.awt.Toolkit value = receiver.getToolkit(); }
    static void call22(java.awt.Window receiver)  { java.lang.String value = receiver.getWarningString(); }
    static void call23(java.awt.Window receiver)  { java.util.Locale value = receiver.getLocale(); }
    static void call24(java.awt.Window receiver)  { java.awt.im.InputContext value = receiver.getInputContext(); }
    static void call25(java.awt.Window receiver)  { receiver.setCursor((java.awt.Cursor) null); }
    static void call26(java.awt.Window receiver)  { java.awt.Window value = receiver.getOwner(); }
    static void call27(java.awt.Window receiver)  { java.awt.Window[] value = receiver.getOwnedWindows(); }
    static void call28()  { java.awt.Window[] value = java.awt.Window.getWindows(); }
    static void call29()  { java.awt.Window[] value = java.awt.Window.getOwnerlessWindows(); }
    static void call30(java.awt.Window receiver)  { receiver.setModalExclusionType((java.awt.Dialog.ModalExclusionType) null); }
    static void call31(java.awt.Window receiver)  { java.awt.Dialog.ModalExclusionType value = receiver.getModalExclusionType(); }
    static void call32(java.awt.Window receiver)  { receiver.addWindowListener((java.awt.event.WindowListener) null); }
    static void call33(java.awt.Window receiver)  { receiver.addWindowStateListener((java.awt.event.WindowStateListener) null); }
    static void call34(java.awt.Window receiver)  { receiver.addWindowFocusListener((java.awt.event.WindowFocusListener) null); }
    static void call35(java.awt.Window receiver)  { receiver.removeWindowListener((java.awt.event.WindowListener) null); }
    static void call36(java.awt.Window receiver)  { receiver.removeWindowStateListener((java.awt.event.WindowStateListener) null); }
    static void call37(java.awt.Window receiver)  { receiver.removeWindowFocusListener((java.awt.event.WindowFocusListener) null); }
    static void call38(java.awt.Window receiver)  { java.awt.event.WindowListener[] value = receiver.getWindowListeners(); }
    static void call39(java.awt.Window receiver)  { java.awt.event.WindowFocusListener[] value = receiver.getWindowFocusListeners(); }
    static void call40(java.awt.Window receiver)  { java.awt.event.WindowStateListener[] value = receiver.getWindowStateListeners(); }
    static void call41(java.awt.Window receiver)  { java.util.EventListener[] value = receiver.getListeners((java.lang.Class) null); }
    static void call42(java.awt.Window receiver)  { receiver.processEvent((java.awt.AWTEvent) null); }
    static void call43(java.awt.Window receiver)  { receiver.processWindowEvent((java.awt.event.WindowEvent) null); }
    static void call44(java.awt.Window receiver)  { receiver.processWindowFocusEvent((java.awt.event.WindowEvent) null); }
    static void call45(java.awt.Window receiver)  { receiver.processWindowStateEvent((java.awt.event.WindowEvent) null); }
    static void call46(java.awt.Window receiver) throws java.lang.SecurityException { try { receiver.setAlwaysOnTop(false); } catch (java.lang.SecurityException expected) { throw expected; } }
    static void call47(java.awt.Window receiver)  { boolean value = receiver.isAlwaysOnTopSupported(); }
    static void call48(java.awt.Window receiver)  { boolean value = receiver.isAlwaysOnTop(); }
    static void call49(java.awt.Window receiver)  { java.awt.Component value = receiver.getFocusOwner(); }
    static void call50(java.awt.Window receiver)  { java.awt.Component value = receiver.getMostRecentFocusOwner(); }
    static void call51(java.awt.Window receiver)  { boolean value = receiver.isActive(); }
    static void call52(java.awt.Window receiver)  { boolean value = receiver.isFocused(); }
    static void call53(java.awt.Window receiver)  { java.util.Set value = receiver.getFocusTraversalKeys((int) 0); }
    static void call54(java.awt.Window receiver)  { receiver.setFocusCycleRoot(false); }
    static void call55(java.awt.Window receiver)  { boolean value = receiver.isFocusCycleRoot(); }
    static void call56(java.awt.Window receiver)  { java.awt.Container value = receiver.getFocusCycleRootAncestor(); }
    static void call57(java.awt.Window receiver)  { boolean value = receiver.isFocusableWindow(); }
    static void call58(java.awt.Window receiver)  { boolean value = receiver.getFocusableWindowState(); }
    static void call59(java.awt.Window receiver)  { receiver.setFocusableWindowState(false); }
    static void call60(java.awt.Window receiver)  { receiver.setAutoRequestFocus(false); }
    static void call61(java.awt.Window receiver)  { boolean value = receiver.isAutoRequestFocus(); }
    static void call62(java.awt.Window receiver)  { receiver.addPropertyChangeListener((java.beans.PropertyChangeListener) null); }
    static void call63(java.awt.Window receiver)  { receiver.addPropertyChangeListener((java.lang.String) null, (java.beans.PropertyChangeListener) null); }
    static void call64(java.awt.Window receiver)  { boolean value = receiver.isValidateRoot(); }
    static void call65(java.awt.Window receiver)  { boolean value = receiver.postEvent((java.awt.Event) null); }
    static void call66(java.awt.Window receiver)  { boolean value = receiver.isShowing(); }
    static void call67(java.awt.Window receiver)  { receiver.applyResourceBundle((java.lang.String) null); }
    static void call68(java.awt.Window receiver)  { receiver.setType((java.awt.Window.Type) null); }
    static void call69(java.awt.Window receiver)  { java.awt.Window.Type value = receiver.getType(); }
    static void call70(java.awt.Window receiver)  { javax.accessibility.AccessibleContext value = receiver.getAccessibleContext(); }
    static void call71(java.awt.Window receiver)  { receiver.setLocationRelativeTo((java.awt.Component) null); }
    static void call72(java.awt.Window receiver)  { receiver.createBufferStrategy((int) 0); }
    static void call73(java.awt.Window receiver) throws java.awt.AWTException { try { receiver.createBufferStrategy((int) 0, (java.awt.BufferCapabilities) null); } catch (java.awt.AWTException expected) { throw expected; } }
    static void call74(java.awt.Window receiver)  { java.awt.image.BufferStrategy value = receiver.getBufferStrategy(); }
    static void call75(java.awt.Window receiver)  { receiver.setLocationByPlatform(false); }
    static void call76(java.awt.Window receiver)  { boolean value = receiver.isLocationByPlatform(); }
    static void call77(java.awt.Window receiver)  { receiver.setBounds((int) 0, (int) 0, (int) 0, (int) 0); }
    static void call78(java.awt.Window receiver)  { receiver.setBounds((java.awt.Rectangle) null); }
    static void call79(java.awt.Window receiver)  { float value = receiver.getOpacity(); }
    static void call80(java.awt.Window receiver)  { receiver.setOpacity((float) 0); }
    static void call81(java.awt.Window receiver)  { java.awt.Shape value = receiver.getShape(); }
    static void call82(java.awt.Window receiver)  { receiver.setShape((java.awt.Shape) null); }
    static void call83(java.awt.Window receiver)  { java.awt.Color value = receiver.getBackground(); }
    static void call84(java.awt.Window receiver)  { receiver.setBackground((java.awt.Color) null); }
    static void call85(java.awt.Window receiver)  { boolean value = receiver.isOpaque(); }
    static void call86(java.awt.Window receiver)  { receiver.paint((java.awt.Graphics) null); }
}

abstract class Override_java_awt_Window extends java.awt.Window {
    Override_java_awt_Window()  { super((java.awt.Frame) null); }
    @Override
    public java.util.List<java.awt.Image> getIconImages()  { throw new UnsupportedOperationException(); }
    @Override
    public void setIconImages(java.util.List<? extends java.awt.Image> arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setIconImage(java.awt.Image arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void addNotify()  { throw new UnsupportedOperationException(); }
    @Override
    public void removeNotify()  { throw new UnsupportedOperationException(); }
    @Override
    public void pack()  { throw new UnsupportedOperationException(); }
    @Override
    public void setMinimumSize(java.awt.Dimension arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setSize(java.awt.Dimension arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setSize(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void setLocation(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void setLocation(java.awt.Point arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void reshape(int arg0, int arg1, int arg2, int arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public void setVisible(boolean arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void show()  { throw new UnsupportedOperationException(); }
    @Override
    public void hide()  { throw new UnsupportedOperationException(); }
    @Override
    public void dispose()  { throw new UnsupportedOperationException(); }
    @Override
    public void toFront()  { throw new UnsupportedOperationException(); }
    @Override
    public void toBack()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Toolkit getToolkit()  { throw new UnsupportedOperationException(); }
    @Override
    public java.util.Locale getLocale()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.im.InputContext getInputContext()  { throw new UnsupportedOperationException(); }
    @Override
    public void setCursor(java.awt.Cursor arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Window getOwner()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Window[] getOwnedWindows()  { throw new UnsupportedOperationException(); }
    @Override
    public void setModalExclusionType(java.awt.Dialog.ModalExclusionType arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Dialog.ModalExclusionType getModalExclusionType()  { throw new UnsupportedOperationException(); }
    @Override
    public void addWindowListener(java.awt.event.WindowListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void addWindowStateListener(java.awt.event.WindowStateListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void addWindowFocusListener(java.awt.event.WindowFocusListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void removeWindowListener(java.awt.event.WindowListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void removeWindowStateListener(java.awt.event.WindowStateListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void removeWindowFocusListener(java.awt.event.WindowFocusListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.event.WindowListener[] getWindowListeners()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.event.WindowFocusListener[] getWindowFocusListeners()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.event.WindowStateListener[] getWindowStateListeners()  { throw new UnsupportedOperationException(); }
    @Override
    public <T extends java.util.EventListener> T[] getListeners(java.lang.Class<T> arg0)  { throw new UnsupportedOperationException(); }
    @Override
    protected void processEvent(java.awt.AWTEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    protected void processWindowEvent(java.awt.event.WindowEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    protected void processWindowFocusEvent(java.awt.event.WindowEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    protected void processWindowStateEvent(java.awt.event.WindowEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isAlwaysOnTopSupported()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Component getFocusOwner()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Component getMostRecentFocusOwner()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isActive()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isFocused()  { throw new UnsupportedOperationException(); }
    @Override
    public java.util.Set<java.awt.AWTKeyStroke> getFocusTraversalKeys(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean getFocusableWindowState()  { throw new UnsupportedOperationException(); }
    @Override
    public void setFocusableWindowState(boolean arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setAutoRequestFocus(boolean arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isAutoRequestFocus()  { throw new UnsupportedOperationException(); }
    @Override
    public void addPropertyChangeListener(java.beans.PropertyChangeListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void addPropertyChangeListener(java.lang.String arg0, java.beans.PropertyChangeListener arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isValidateRoot()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean postEvent(java.awt.Event arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isShowing()  { throw new UnsupportedOperationException(); }
    @Override
    public void applyResourceBundle(java.lang.String arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setType(java.awt.Window.Type arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Window.Type getType()  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.AccessibleContext getAccessibleContext()  { throw new UnsupportedOperationException(); }
    @Override
    public void setLocationRelativeTo(java.awt.Component arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void createBufferStrategy(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void createBufferStrategy(int arg0, java.awt.BufferCapabilities arg1) throws java.awt.AWTException { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.BufferStrategy getBufferStrategy()  { throw new UnsupportedOperationException(); }
    @Override
    public void setLocationByPlatform(boolean arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isLocationByPlatform()  { throw new UnsupportedOperationException(); }
    @Override
    public void setBounds(int arg0, int arg1, int arg2, int arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public void setBounds(java.awt.Rectangle arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public float getOpacity()  { throw new UnsupportedOperationException(); }
    @Override
    public void setOpacity(float arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Shape getShape()  { throw new UnsupportedOperationException(); }
    @Override
    public void setShape(java.awt.Shape arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Color getBackground()  { throw new UnsupportedOperationException(); }
    @Override
    public void setBackground(java.awt.Color arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isOpaque()  { throw new UnsupportedOperationException(); }
    @Override
    public void paint(java.awt.Graphics arg0)  { throw new UnsupportedOperationException(); }
}
