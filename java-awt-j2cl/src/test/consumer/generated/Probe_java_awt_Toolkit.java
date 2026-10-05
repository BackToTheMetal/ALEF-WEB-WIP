package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_Toolkit {
    static void call0(java.awt.Toolkit receiver) { java.util.Map value = receiver.desktopProperties; }
    static void call1(java.awt.Toolkit receiver) { java.beans.PropertyChangeSupport value = receiver.desktopPropsSupport; }
    static void call2(java.awt.Toolkit receiver) throws java.awt.HeadlessException { try { receiver.loadSystemColors((int[]) null); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call3(java.awt.Toolkit receiver) throws java.awt.HeadlessException { try { receiver.setDynamicLayout(false); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call4(java.awt.Toolkit receiver) throws java.awt.HeadlessException { try { boolean value = receiver.isDynamicLayoutSet(); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call5(java.awt.Toolkit receiver) throws java.awt.HeadlessException { try { boolean value = receiver.isDynamicLayoutActive(); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call6(java.awt.Toolkit receiver) throws java.awt.HeadlessException { try { java.awt.Dimension value = receiver.getScreenSize(); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call7(java.awt.Toolkit receiver) throws java.awt.HeadlessException { try { int value = receiver.getScreenResolution(); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call8(java.awt.Toolkit receiver) throws java.awt.HeadlessException { try { java.awt.Insets value = receiver.getScreenInsets((java.awt.GraphicsConfiguration) null); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call9(java.awt.Toolkit receiver) throws java.awt.HeadlessException { try { java.awt.image.ColorModel value = receiver.getColorModel(); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call10(java.awt.Toolkit receiver)  { java.lang.String[] value = receiver.getFontList(); }
    static void call11(java.awt.Toolkit receiver)  { java.awt.FontMetrics value = receiver.getFontMetrics((java.awt.Font) null); }
    static void call12(java.awt.Toolkit receiver)  { receiver.sync(); }
    static void call13()  { java.awt.Toolkit value = java.awt.Toolkit.getDefaultToolkit(); }
    static void call14(java.awt.Toolkit receiver)  { java.awt.Image value = receiver.getImage((java.lang.String) null); }
    static void call15(java.awt.Toolkit receiver)  { java.awt.Image value = receiver.getImage((java.net.URL) null); }
    static void call16(java.awt.Toolkit receiver)  { java.awt.Image value = receiver.createImage((java.lang.String) null); }
    static void call17(java.awt.Toolkit receiver)  { java.awt.Image value = receiver.createImage((java.net.URL) null); }
    static void call18(java.awt.Toolkit receiver)  { boolean value = receiver.prepareImage((java.awt.Image) null, (int) 0, (int) 0, (java.awt.image.ImageObserver) null); }
    static void call19(java.awt.Toolkit receiver)  { int value = receiver.checkImage((java.awt.Image) null, (int) 0, (int) 0, (java.awt.image.ImageObserver) null); }
    static void call20(java.awt.Toolkit receiver)  { java.awt.Image value = receiver.createImage((java.awt.image.ImageProducer) null); }
    static void call21(java.awt.Toolkit receiver)  { java.awt.Image value = receiver.createImage((byte[]) null); }
    static void call22(java.awt.Toolkit receiver)  { java.awt.Image value = receiver.createImage((byte[]) null, (int) 0, (int) 0); }
    static void call23(java.awt.Toolkit receiver)  { receiver.beep(); }
    static void call24(java.awt.Toolkit receiver) throws java.awt.HeadlessException { try { java.awt.datatransfer.Clipboard value = receiver.getSystemClipboard(); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call25(java.awt.Toolkit receiver) throws java.awt.HeadlessException { try { java.awt.datatransfer.Clipboard value = receiver.getSystemSelection(); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call26(java.awt.Toolkit receiver) throws java.awt.HeadlessException { try { int value = receiver.getMenuShortcutKeyMask(); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call27(java.awt.Toolkit receiver) throws java.awt.HeadlessException { try { int value = receiver.getMenuShortcutKeyMaskEx(); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call28(java.awt.Toolkit receiver) throws java.lang.UnsupportedOperationException { try { boolean value = receiver.getLockingKeyState((int) 0); } catch (java.lang.UnsupportedOperationException expected) { throw expected; } }
    static void call29(java.awt.Toolkit receiver) throws java.lang.UnsupportedOperationException { try { receiver.setLockingKeyState((int) 0, false); } catch (java.lang.UnsupportedOperationException expected) { throw expected; } }
    static void call30(java.awt.Toolkit receiver) throws java.lang.IndexOutOfBoundsException, java.awt.HeadlessException { try { try { java.awt.Cursor value = receiver.createCustomCursor((java.awt.Image) null, (java.awt.Point) null, (java.lang.String) null); } catch (java.lang.IndexOutOfBoundsException expected) { throw expected; } } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call31(java.awt.Toolkit receiver) throws java.awt.HeadlessException { try { java.awt.Dimension value = receiver.getBestCursorSize((int) 0, (int) 0); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call32(java.awt.Toolkit receiver) throws java.awt.HeadlessException { try { int value = receiver.getMaximumCursorColors(); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call33(java.awt.Toolkit receiver) throws java.awt.HeadlessException { try { boolean value = receiver.isFrameStateSupported((int) 0); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call34()  { java.lang.String value = java.awt.Toolkit.getProperty((java.lang.String) null, (java.lang.String) null); }
    static void call35(java.awt.Toolkit receiver)  { java.awt.EventQueue value = receiver.getSystemEventQueue(); }
    static void call36(java.awt.Toolkit receiver)  { java.awt.EventQueue value = receiver.getSystemEventQueueImpl(); }
    static void call37(java.awt.Toolkit receiver)  { java.awt.dnd.DragGestureRecognizer value = receiver.createDragGestureRecognizer((java.lang.Class) null, (java.awt.dnd.DragSource) null, (java.awt.Component) null, (int) 0, (java.awt.dnd.DragGestureListener) null); }
    static void call38(java.awt.Toolkit receiver)  { java.lang.Object value = receiver.getDesktopProperty((java.lang.String) null); }
    static void call39(java.awt.Toolkit receiver)  { receiver.setDesktopProperty((java.lang.String) null, (java.lang.Object) null); }
    static void call40(java.awt.Toolkit receiver)  { java.lang.Object value = receiver.lazilyLoadDesktopProperty((java.lang.String) null); }
    static void call41(java.awt.Toolkit receiver)  { receiver.initializeDesktopProperties(); }
    static void call42(java.awt.Toolkit receiver)  { receiver.addPropertyChangeListener((java.lang.String) null, (java.beans.PropertyChangeListener) null); }
    static void call43(java.awt.Toolkit receiver)  { receiver.removePropertyChangeListener((java.lang.String) null, (java.beans.PropertyChangeListener) null); }
    static void call44(java.awt.Toolkit receiver)  { java.beans.PropertyChangeListener[] value = receiver.getPropertyChangeListeners(); }
    static void call45(java.awt.Toolkit receiver)  { java.beans.PropertyChangeListener[] value = receiver.getPropertyChangeListeners((java.lang.String) null); }
    static void call46(java.awt.Toolkit receiver)  { boolean value = receiver.isAlwaysOnTopSupported(); }
    static void call47(java.awt.Toolkit receiver)  { boolean value = receiver.isModalityTypeSupported((java.awt.Dialog.ModalityType) null); }
    static void call48(java.awt.Toolkit receiver)  { boolean value = receiver.isModalExclusionTypeSupported((java.awt.Dialog.ModalExclusionType) null); }
    static void call49(java.awt.Toolkit receiver)  { receiver.addAWTEventListener((java.awt.event.AWTEventListener) null, (long) 0); }
    static void call50(java.awt.Toolkit receiver)  { receiver.removeAWTEventListener((java.awt.event.AWTEventListener) null); }
    static void call51(java.awt.Toolkit receiver)  { java.awt.event.AWTEventListener[] value = receiver.getAWTEventListeners(); }
    static void call52(java.awt.Toolkit receiver)  { java.awt.event.AWTEventListener[] value = receiver.getAWTEventListeners((long) 0); }
    static void call53(java.awt.Toolkit receiver) throws java.awt.HeadlessException { try { java.util.Map value = receiver.mapInputMethodHighlight((java.awt.im.InputMethodHighlight) null); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call54(java.awt.Toolkit receiver) throws java.awt.HeadlessException { try { boolean value = receiver.areExtraMouseButtonsEnabled(); } catch (java.awt.HeadlessException expected) { throw expected; } }
}

abstract class Override_java_awt_Toolkit extends java.awt.Toolkit {
    Override_java_awt_Toolkit()  { super(); }
    @Override
    protected void loadSystemColors(int[] arg0) throws java.awt.HeadlessException { throw new UnsupportedOperationException(); }
    @Override
    public void setDynamicLayout(boolean arg0) throws java.awt.HeadlessException { throw new UnsupportedOperationException(); }
    @Override
    protected boolean isDynamicLayoutSet() throws java.awt.HeadlessException { throw new UnsupportedOperationException(); }
    @Override
    public boolean isDynamicLayoutActive() throws java.awt.HeadlessException { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Dimension getScreenSize() throws java.awt.HeadlessException { throw new UnsupportedOperationException(); }
    @Override
    public int getScreenResolution() throws java.awt.HeadlessException { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Insets getScreenInsets(java.awt.GraphicsConfiguration arg0) throws java.awt.HeadlessException { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.ColorModel getColorModel() throws java.awt.HeadlessException { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String[] getFontList()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.FontMetrics getFontMetrics(java.awt.Font arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void sync()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Image getImage(java.lang.String arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Image getImage(java.net.URL arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Image createImage(java.lang.String arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Image createImage(java.net.URL arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean prepareImage(java.awt.Image arg0, int arg1, int arg2, java.awt.image.ImageObserver arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public int checkImage(java.awt.Image arg0, int arg1, int arg2, java.awt.image.ImageObserver arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Image createImage(java.awt.image.ImageProducer arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Image createImage(byte[] arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Image createImage(byte[] arg0, int arg1, int arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public void beep()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.datatransfer.Clipboard getSystemClipboard() throws java.awt.HeadlessException { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.datatransfer.Clipboard getSystemSelection() throws java.awt.HeadlessException { throw new UnsupportedOperationException(); }
    @Override
    public int getMenuShortcutKeyMask() throws java.awt.HeadlessException { throw new UnsupportedOperationException(); }
    @Override
    public int getMenuShortcutKeyMaskEx() throws java.awt.HeadlessException { throw new UnsupportedOperationException(); }
    @Override
    public boolean getLockingKeyState(int arg0) throws java.lang.UnsupportedOperationException { throw new UnsupportedOperationException(); }
    @Override
    public void setLockingKeyState(int arg0, boolean arg1) throws java.lang.UnsupportedOperationException { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Cursor createCustomCursor(java.awt.Image arg0, java.awt.Point arg1, java.lang.String arg2) throws java.lang.IndexOutOfBoundsException, java.awt.HeadlessException { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Dimension getBestCursorSize(int arg0, int arg1) throws java.awt.HeadlessException { throw new UnsupportedOperationException(); }
    @Override
    public int getMaximumCursorColors() throws java.awt.HeadlessException { throw new UnsupportedOperationException(); }
    @Override
    public boolean isFrameStateSupported(int arg0) throws java.awt.HeadlessException { throw new UnsupportedOperationException(); }
    @Override
    protected java.awt.EventQueue getSystemEventQueueImpl()  { throw new UnsupportedOperationException(); }
    @Override
    public <T extends java.awt.dnd.DragGestureRecognizer> T createDragGestureRecognizer(java.lang.Class<T> arg0, java.awt.dnd.DragSource arg1, java.awt.Component arg2, int arg3, java.awt.dnd.DragGestureListener arg4)  { throw new UnsupportedOperationException(); }
    @Override
    protected java.lang.Object lazilyLoadDesktopProperty(java.lang.String arg0)  { throw new UnsupportedOperationException(); }
    @Override
    protected void initializeDesktopProperties()  { throw new UnsupportedOperationException(); }
    @Override
    public void addPropertyChangeListener(java.lang.String arg0, java.beans.PropertyChangeListener arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void removePropertyChangeListener(java.lang.String arg0, java.beans.PropertyChangeListener arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.beans.PropertyChangeListener[] getPropertyChangeListeners()  { throw new UnsupportedOperationException(); }
    @Override
    public java.beans.PropertyChangeListener[] getPropertyChangeListeners(java.lang.String arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isAlwaysOnTopSupported()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isModalityTypeSupported(java.awt.Dialog.ModalityType arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isModalExclusionTypeSupported(java.awt.Dialog.ModalExclusionType arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void addAWTEventListener(java.awt.event.AWTEventListener arg0, long arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void removeAWTEventListener(java.awt.event.AWTEventListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.event.AWTEventListener[] getAWTEventListeners()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.event.AWTEventListener[] getAWTEventListeners(long arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.util.Map<java.awt.font.TextAttribute, ?> mapInputMethodHighlight(java.awt.im.InputMethodHighlight arg0) throws java.awt.HeadlessException { throw new UnsupportedOperationException(); }
    @Override
    public boolean areExtraMouseButtonsEnabled() throws java.awt.HeadlessException { throw new UnsupportedOperationException(); }
}
