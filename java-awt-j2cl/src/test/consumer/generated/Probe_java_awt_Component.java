package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_Component {
    static void call0() { float value = java.awt.Component.TOP_ALIGNMENT; }
    static void call1() { float value = java.awt.Component.CENTER_ALIGNMENT; }
    static void call2() { float value = java.awt.Component.BOTTOM_ALIGNMENT; }
    static void call3() { float value = java.awt.Component.LEFT_ALIGNMENT; }
    static void call4() { float value = java.awt.Component.RIGHT_ALIGNMENT; }
    static void call5(java.awt.Component receiver) { javax.accessibility.AccessibleContext value = receiver.accessibleContext; receiver.accessibleContext = value; }
    static void call6(java.awt.Component receiver)  { java.lang.String value = receiver.getName(); }
    static void call7(java.awt.Component receiver)  { receiver.setName((java.lang.String) null); }
    static void call8(java.awt.Component receiver)  { java.awt.Container value = receiver.getParent(); }
    static void call9(java.awt.Component receiver)  { receiver.setDropTarget((java.awt.dnd.DropTarget) null); }
    static void call10(java.awt.Component receiver)  { java.awt.dnd.DropTarget value = receiver.getDropTarget(); }
    static void call11(java.awt.Component receiver)  { java.awt.GraphicsConfiguration value = receiver.getGraphicsConfiguration(); }
    static void call12(java.awt.Component receiver)  { java.lang.Object value = receiver.getTreeLock(); }
    static void call13(java.awt.Component receiver)  { java.awt.Toolkit value = receiver.getToolkit(); }
    static void call14(java.awt.Component receiver)  { boolean value = receiver.isValid(); }
    static void call15(java.awt.Component receiver)  { boolean value = receiver.isDisplayable(); }
    static void call16(java.awt.Component receiver)  { boolean value = receiver.isVisible(); }
    static void call17(java.awt.Component receiver) throws java.awt.HeadlessException { try { java.awt.Point value = receiver.getMousePosition(); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call18(java.awt.Component receiver)  { boolean value = receiver.isShowing(); }
    static void call19(java.awt.Component receiver)  { boolean value = receiver.isEnabled(); }
    static void call20(java.awt.Component receiver)  { receiver.setEnabled(false); }
    static void call21(java.awt.Component receiver)  { receiver.enable(); }
    static void call22(java.awt.Component receiver)  { receiver.enable(false); }
    static void call23(java.awt.Component receiver)  { receiver.disable(); }
    static void call24(java.awt.Component receiver)  { boolean value = receiver.isDoubleBuffered(); }
    static void call25(java.awt.Component receiver)  { receiver.enableInputMethods(false); }
    static void call26(java.awt.Component receiver)  { receiver.setVisible(false); }
    static void call27(java.awt.Component receiver)  { receiver.show(); }
    static void call28(java.awt.Component receiver)  { receiver.show(false); }
    static void call29(java.awt.Component receiver)  { receiver.hide(); }
    static void call30(java.awt.Component receiver)  { java.awt.Color value = receiver.getForeground(); }
    static void call31(java.awt.Component receiver)  { receiver.setForeground((java.awt.Color) null); }
    static void call32(java.awt.Component receiver)  { boolean value = receiver.isForegroundSet(); }
    static void call33(java.awt.Component receiver)  { java.awt.Color value = receiver.getBackground(); }
    static void call34(java.awt.Component receiver)  { receiver.setBackground((java.awt.Color) null); }
    static void call35(java.awt.Component receiver)  { boolean value = receiver.isBackgroundSet(); }
    static void call36(java.awt.Component receiver)  { java.awt.Font value = receiver.getFont(); }
    static void call37(java.awt.Component receiver)  { receiver.setFont((java.awt.Font) null); }
    static void call38(java.awt.Component receiver)  { boolean value = receiver.isFontSet(); }
    static void call39(java.awt.Component receiver)  { java.util.Locale value = receiver.getLocale(); }
    static void call40(java.awt.Component receiver)  { receiver.setLocale((java.util.Locale) null); }
    static void call41(java.awt.Component receiver)  { java.awt.image.ColorModel value = receiver.getColorModel(); }
    static void call42(java.awt.Component receiver)  { java.awt.Point value = receiver.getLocation(); }
    static void call43(java.awt.Component receiver)  { java.awt.Point value = receiver.getLocationOnScreen(); }
    static void call44(java.awt.Component receiver)  { java.awt.Point value = receiver.location(); }
    static void call45(java.awt.Component receiver)  { receiver.setLocation((int) 0, (int) 0); }
    static void call46(java.awt.Component receiver)  { receiver.move((int) 0, (int) 0); }
    static void call47(java.awt.Component receiver)  { receiver.setLocation((java.awt.Point) null); }
    static void call48(java.awt.Component receiver)  { java.awt.Dimension value = receiver.getSize(); }
    static void call49(java.awt.Component receiver)  { java.awt.Dimension value = receiver.size(); }
    static void call50(java.awt.Component receiver)  { receiver.setSize((int) 0, (int) 0); }
    static void call51(java.awt.Component receiver)  { receiver.resize((int) 0, (int) 0); }
    static void call52(java.awt.Component receiver)  { receiver.setSize((java.awt.Dimension) null); }
    static void call53(java.awt.Component receiver)  { receiver.resize((java.awt.Dimension) null); }
    static void call54(java.awt.Component receiver)  { java.awt.Rectangle value = receiver.getBounds(); }
    static void call55(java.awt.Component receiver)  { java.awt.Rectangle value = receiver.bounds(); }
    static void call56(java.awt.Component receiver)  { receiver.setBounds((int) 0, (int) 0, (int) 0, (int) 0); }
    static void call57(java.awt.Component receiver)  { receiver.reshape((int) 0, (int) 0, (int) 0, (int) 0); }
    static void call58(java.awt.Component receiver)  { receiver.setBounds((java.awt.Rectangle) null); }
    static void call59(java.awt.Component receiver)  { int value = receiver.getX(); }
    static void call60(java.awt.Component receiver)  { int value = receiver.getY(); }
    static void call61(java.awt.Component receiver)  { int value = receiver.getWidth(); }
    static void call62(java.awt.Component receiver)  { int value = receiver.getHeight(); }
    static void call63(java.awt.Component receiver)  { java.awt.Rectangle value = receiver.getBounds((java.awt.Rectangle) null); }
    static void call64(java.awt.Component receiver)  { java.awt.Dimension value = receiver.getSize((java.awt.Dimension) null); }
    static void call65(java.awt.Component receiver)  { java.awt.Point value = receiver.getLocation((java.awt.Point) null); }
    static void call66(java.awt.Component receiver)  { boolean value = receiver.isOpaque(); }
    static void call67(java.awt.Component receiver)  { boolean value = receiver.isLightweight(); }
    static void call68(java.awt.Component receiver)  { receiver.setPreferredSize((java.awt.Dimension) null); }
    static void call69(java.awt.Component receiver)  { boolean value = receiver.isPreferredSizeSet(); }
    static void call70(java.awt.Component receiver)  { java.awt.Dimension value = receiver.getPreferredSize(); }
    static void call71(java.awt.Component receiver)  { java.awt.Dimension value = receiver.preferredSize(); }
    static void call72(java.awt.Component receiver)  { receiver.setMinimumSize((java.awt.Dimension) null); }
    static void call73(java.awt.Component receiver)  { boolean value = receiver.isMinimumSizeSet(); }
    static void call74(java.awt.Component receiver)  { java.awt.Dimension value = receiver.getMinimumSize(); }
    static void call75(java.awt.Component receiver)  { java.awt.Dimension value = receiver.minimumSize(); }
    static void call76(java.awt.Component receiver)  { receiver.setMaximumSize((java.awt.Dimension) null); }
    static void call77(java.awt.Component receiver)  { boolean value = receiver.isMaximumSizeSet(); }
    static void call78(java.awt.Component receiver)  { java.awt.Dimension value = receiver.getMaximumSize(); }
    static void call79(java.awt.Component receiver)  { float value = receiver.getAlignmentX(); }
    static void call80(java.awt.Component receiver)  { float value = receiver.getAlignmentY(); }
    static void call81(java.awt.Component receiver)  { int value = receiver.getBaseline((int) 0, (int) 0); }
    static void call82(java.awt.Component receiver)  { java.awt.Component.BaselineResizeBehavior value = receiver.getBaselineResizeBehavior(); }
    static void call83(java.awt.Component receiver)  { receiver.doLayout(); }
    static void call84(java.awt.Component receiver)  { receiver.layout(); }
    static void call85(java.awt.Component receiver)  { receiver.validate(); }
    static void call86(java.awt.Component receiver)  { receiver.invalidate(); }
    static void call87(java.awt.Component receiver)  { receiver.revalidate(); }
    static void call88(java.awt.Component receiver)  { java.awt.Graphics value = receiver.getGraphics(); }
    static void call89(java.awt.Component receiver)  { java.awt.FontMetrics value = receiver.getFontMetrics((java.awt.Font) null); }
    static void call90(java.awt.Component receiver)  { receiver.setCursor((java.awt.Cursor) null); }
    static void call91(java.awt.Component receiver)  { java.awt.Cursor value = receiver.getCursor(); }
    static void call92(java.awt.Component receiver)  { boolean value = receiver.isCursorSet(); }
    static void call93(java.awt.Component receiver)  { receiver.paint((java.awt.Graphics) null); }
    static void call94(java.awt.Component receiver)  { receiver.update((java.awt.Graphics) null); }
    static void call95(java.awt.Component receiver)  { receiver.paintAll((java.awt.Graphics) null); }
    static void call96(java.awt.Component receiver)  { receiver.repaint(); }
    static void call97(java.awt.Component receiver)  { receiver.repaint((long) 0); }
    static void call98(java.awt.Component receiver)  { receiver.repaint((int) 0, (int) 0, (int) 0, (int) 0); }
    static void call99(java.awt.Component receiver)  { receiver.repaint((long) 0, (int) 0, (int) 0, (int) 0, (int) 0); }
    static void call100(java.awt.Component receiver)  { receiver.print((java.awt.Graphics) null); }
    static void call101(java.awt.Component receiver)  { receiver.printAll((java.awt.Graphics) null); }
    static void call102(java.awt.Component receiver)  { boolean value = receiver.imageUpdate((java.awt.Image) null, (int) 0, (int) 0, (int) 0, (int) 0, (int) 0); }
    static void call103(java.awt.Component receiver)  { java.awt.Image value = receiver.createImage((java.awt.image.ImageProducer) null); }
    static void call104(java.awt.Component receiver)  { java.awt.Image value = receiver.createImage((int) 0, (int) 0); }
    static void call105(java.awt.Component receiver)  { java.awt.image.VolatileImage value = receiver.createVolatileImage((int) 0, (int) 0); }
    static void call106(java.awt.Component receiver) throws java.awt.AWTException { try { java.awt.image.VolatileImage value = receiver.createVolatileImage((int) 0, (int) 0, (java.awt.ImageCapabilities) null); } catch (java.awt.AWTException expected) { throw expected; } }
    static void call107(java.awt.Component receiver)  { boolean value = receiver.prepareImage((java.awt.Image) null, (java.awt.image.ImageObserver) null); }
    static void call108(java.awt.Component receiver)  { boolean value = receiver.prepareImage((java.awt.Image) null, (int) 0, (int) 0, (java.awt.image.ImageObserver) null); }
    static void call109(java.awt.Component receiver)  { int value = receiver.checkImage((java.awt.Image) null, (java.awt.image.ImageObserver) null); }
    static void call110(java.awt.Component receiver)  { int value = receiver.checkImage((java.awt.Image) null, (int) 0, (int) 0, (java.awt.image.ImageObserver) null); }
    static void call111(java.awt.Component receiver)  { receiver.setIgnoreRepaint(false); }
    static void call112(java.awt.Component receiver)  { boolean value = receiver.getIgnoreRepaint(); }
    static void call113(java.awt.Component receiver)  { boolean value = receiver.contains((int) 0, (int) 0); }
    static void call114(java.awt.Component receiver)  { boolean value = receiver.inside((int) 0, (int) 0); }
    static void call115(java.awt.Component receiver)  { boolean value = receiver.contains((java.awt.Point) null); }
    static void call116(java.awt.Component receiver)  { java.awt.Component value = receiver.getComponentAt((int) 0, (int) 0); }
    static void call117(java.awt.Component receiver)  { java.awt.Component value = receiver.locate((int) 0, (int) 0); }
    static void call118(java.awt.Component receiver)  { java.awt.Component value = receiver.getComponentAt((java.awt.Point) null); }
    static void call119(java.awt.Component receiver)  { receiver.deliverEvent((java.awt.Event) null); }
    static void call120(java.awt.Component receiver)  { receiver.dispatchEvent((java.awt.AWTEvent) null); }
    static void call121(java.awt.Component receiver)  { boolean value = receiver.postEvent((java.awt.Event) null); }
    static void call122(java.awt.Component receiver)  { receiver.addComponentListener((java.awt.event.ComponentListener) null); }
    static void call123(java.awt.Component receiver)  { receiver.removeComponentListener((java.awt.event.ComponentListener) null); }
    static void call124(java.awt.Component receiver)  { java.awt.event.ComponentListener[] value = receiver.getComponentListeners(); }
    static void call125(java.awt.Component receiver)  { receiver.addFocusListener((java.awt.event.FocusListener) null); }
    static void call126(java.awt.Component receiver)  { receiver.removeFocusListener((java.awt.event.FocusListener) null); }
    static void call127(java.awt.Component receiver)  { java.awt.event.FocusListener[] value = receiver.getFocusListeners(); }
    static void call128(java.awt.Component receiver)  { receiver.addHierarchyListener((java.awt.event.HierarchyListener) null); }
    static void call129(java.awt.Component receiver)  { receiver.removeHierarchyListener((java.awt.event.HierarchyListener) null); }
    static void call130(java.awt.Component receiver)  { java.awt.event.HierarchyListener[] value = receiver.getHierarchyListeners(); }
    static void call131(java.awt.Component receiver)  { receiver.addHierarchyBoundsListener((java.awt.event.HierarchyBoundsListener) null); }
    static void call132(java.awt.Component receiver)  { receiver.removeHierarchyBoundsListener((java.awt.event.HierarchyBoundsListener) null); }
    static void call133(java.awt.Component receiver)  { java.awt.event.HierarchyBoundsListener[] value = receiver.getHierarchyBoundsListeners(); }
    static void call134(java.awt.Component receiver)  { receiver.addKeyListener((java.awt.event.KeyListener) null); }
    static void call135(java.awt.Component receiver)  { receiver.removeKeyListener((java.awt.event.KeyListener) null); }
    static void call136(java.awt.Component receiver)  { java.awt.event.KeyListener[] value = receiver.getKeyListeners(); }
    static void call137(java.awt.Component receiver)  { receiver.addMouseListener((java.awt.event.MouseListener) null); }
    static void call138(java.awt.Component receiver)  { receiver.removeMouseListener((java.awt.event.MouseListener) null); }
    static void call139(java.awt.Component receiver)  { java.awt.event.MouseListener[] value = receiver.getMouseListeners(); }
    static void call140(java.awt.Component receiver)  { receiver.addMouseMotionListener((java.awt.event.MouseMotionListener) null); }
    static void call141(java.awt.Component receiver)  { receiver.removeMouseMotionListener((java.awt.event.MouseMotionListener) null); }
    static void call142(java.awt.Component receiver)  { java.awt.event.MouseMotionListener[] value = receiver.getMouseMotionListeners(); }
    static void call143(java.awt.Component receiver)  { receiver.addMouseWheelListener((java.awt.event.MouseWheelListener) null); }
    static void call144(java.awt.Component receiver)  { receiver.removeMouseWheelListener((java.awt.event.MouseWheelListener) null); }
    static void call145(java.awt.Component receiver)  { java.awt.event.MouseWheelListener[] value = receiver.getMouseWheelListeners(); }
    static void call146(java.awt.Component receiver)  { receiver.addInputMethodListener((java.awt.event.InputMethodListener) null); }
    static void call147(java.awt.Component receiver)  { receiver.removeInputMethodListener((java.awt.event.InputMethodListener) null); }
    static void call148(java.awt.Component receiver)  { java.awt.event.InputMethodListener[] value = receiver.getInputMethodListeners(); }
    static void call149(java.awt.Component receiver)  { java.util.EventListener[] value = receiver.getListeners((java.lang.Class) null); }
    static void call150(java.awt.Component receiver)  { java.awt.im.InputMethodRequests value = receiver.getInputMethodRequests(); }
    static void call151(java.awt.Component receiver)  { java.awt.im.InputContext value = receiver.getInputContext(); }
    static void call152(java.awt.Component receiver)  { receiver.enableEvents((long) 0); }
    static void call153(java.awt.Component receiver)  { receiver.disableEvents((long) 0); }
    static void call154(java.awt.Component receiver)  { java.awt.AWTEvent value = receiver.coalesceEvents((java.awt.AWTEvent) null, (java.awt.AWTEvent) null); }
    static void call155(java.awt.Component receiver)  { receiver.processEvent((java.awt.AWTEvent) null); }
    static void call156(java.awt.Component receiver)  { receiver.processComponentEvent((java.awt.event.ComponentEvent) null); }
    static void call157(java.awt.Component receiver)  { receiver.processFocusEvent((java.awt.event.FocusEvent) null); }
    static void call158(java.awt.Component receiver)  { receiver.processKeyEvent((java.awt.event.KeyEvent) null); }
    static void call159(java.awt.Component receiver)  { receiver.processMouseEvent((java.awt.event.MouseEvent) null); }
    static void call160(java.awt.Component receiver)  { receiver.processMouseMotionEvent((java.awt.event.MouseEvent) null); }
    static void call161(java.awt.Component receiver)  { receiver.processMouseWheelEvent((java.awt.event.MouseWheelEvent) null); }
    static void call162(java.awt.Component receiver)  { receiver.processInputMethodEvent((java.awt.event.InputMethodEvent) null); }
    static void call163(java.awt.Component receiver)  { receiver.processHierarchyEvent((java.awt.event.HierarchyEvent) null); }
    static void call164(java.awt.Component receiver)  { receiver.processHierarchyBoundsEvent((java.awt.event.HierarchyEvent) null); }
    static void call165(java.awt.Component receiver)  { boolean value = receiver.handleEvent((java.awt.Event) null); }
    static void call166(java.awt.Component receiver)  { boolean value = receiver.mouseDown((java.awt.Event) null, (int) 0, (int) 0); }
    static void call167(java.awt.Component receiver)  { boolean value = receiver.mouseDrag((java.awt.Event) null, (int) 0, (int) 0); }
    static void call168(java.awt.Component receiver)  { boolean value = receiver.mouseUp((java.awt.Event) null, (int) 0, (int) 0); }
    static void call169(java.awt.Component receiver)  { boolean value = receiver.mouseMove((java.awt.Event) null, (int) 0, (int) 0); }
    static void call170(java.awt.Component receiver)  { boolean value = receiver.mouseEnter((java.awt.Event) null, (int) 0, (int) 0); }
    static void call171(java.awt.Component receiver)  { boolean value = receiver.mouseExit((java.awt.Event) null, (int) 0, (int) 0); }
    static void call172(java.awt.Component receiver)  { boolean value = receiver.keyDown((java.awt.Event) null, (int) 0); }
    static void call173(java.awt.Component receiver)  { boolean value = receiver.keyUp((java.awt.Event) null, (int) 0); }
    static void call174(java.awt.Component receiver)  { boolean value = receiver.action((java.awt.Event) null, (java.lang.Object) null); }
    static void call175(java.awt.Component receiver)  { receiver.addNotify(); }
    static void call176(java.awt.Component receiver)  { receiver.removeNotify(); }
    static void call177(java.awt.Component receiver)  { boolean value = receiver.gotFocus((java.awt.Event) null, (java.lang.Object) null); }
    static void call178(java.awt.Component receiver)  { boolean value = receiver.lostFocus((java.awt.Event) null, (java.lang.Object) null); }
    static void call179(java.awt.Component receiver)  { boolean value = receiver.isFocusTraversable(); }
    static void call180(java.awt.Component receiver)  { boolean value = receiver.isFocusable(); }
    static void call181(java.awt.Component receiver)  { receiver.setFocusable(false); }
    static void call182(java.awt.Component receiver)  { receiver.setFocusTraversalKeys((int) 0, (java.util.Set) null); }
    static void call183(java.awt.Component receiver)  { java.util.Set value = receiver.getFocusTraversalKeys((int) 0); }
    static void call184(java.awt.Component receiver)  { boolean value = receiver.areFocusTraversalKeysSet((int) 0); }
    static void call185(java.awt.Component receiver)  { receiver.setFocusTraversalKeysEnabled(false); }
    static void call186(java.awt.Component receiver)  { boolean value = receiver.getFocusTraversalKeysEnabled(); }
    static void call187(java.awt.Component receiver)  { receiver.requestFocus(); }
    static void call188(java.awt.Component receiver)  { receiver.requestFocus((java.awt.event.FocusEvent.Cause) null); }
    static void call189(java.awt.Component receiver)  { boolean value = receiver.requestFocus(false); }
    static void call190(java.awt.Component receiver)  { boolean value = receiver.requestFocus(false, (java.awt.event.FocusEvent.Cause) null); }
    static void call191(java.awt.Component receiver)  { boolean value = receiver.requestFocusInWindow(); }
    static void call192(java.awt.Component receiver)  { boolean value = receiver.requestFocusInWindow((java.awt.event.FocusEvent.Cause) null); }
    static void call193(java.awt.Component receiver)  { boolean value = receiver.requestFocusInWindow(false); }
    static void call194(java.awt.Component receiver)  { java.awt.Container value = receiver.getFocusCycleRootAncestor(); }
    static void call195(java.awt.Component receiver)  { boolean value = receiver.isFocusCycleRoot((java.awt.Container) null); }
    static void call196(java.awt.Component receiver)  { receiver.transferFocus(); }
    static void call197(java.awt.Component receiver)  { receiver.nextFocus(); }
    static void call198(java.awt.Component receiver)  { receiver.transferFocusBackward(); }
    static void call199(java.awt.Component receiver)  { receiver.transferFocusUpCycle(); }
    static void call200(java.awt.Component receiver)  { boolean value = receiver.hasFocus(); }
    static void call201(java.awt.Component receiver)  { boolean value = receiver.isFocusOwner(); }
    static void call202(java.awt.Component receiver)  { receiver.add((java.awt.PopupMenu) null); }
    static void call203(java.awt.Component receiver)  { receiver.remove((java.awt.MenuComponent) null); }
    static void call204(java.awt.Component receiver)  { java.lang.String value = receiver.paramString(); }
    static void call205(java.awt.Component receiver)  { java.lang.String value = receiver.toString(); }
    static void call206(java.awt.Component receiver)  { receiver.list(); }
    static void call207(java.awt.Component receiver)  { receiver.list((java.io.PrintStream) null); }
    static void call208(java.awt.Component receiver)  { receiver.list((java.io.PrintStream) null, (int) 0); }
    static void call209(java.awt.Component receiver)  { receiver.list((java.io.PrintWriter) null); }
    static void call210(java.awt.Component receiver)  { receiver.list((java.io.PrintWriter) null, (int) 0); }
    static void call211(java.awt.Component receiver)  { receiver.addPropertyChangeListener((java.beans.PropertyChangeListener) null); }
    static void call212(java.awt.Component receiver)  { receiver.removePropertyChangeListener((java.beans.PropertyChangeListener) null); }
    static void call213(java.awt.Component receiver)  { java.beans.PropertyChangeListener[] value = receiver.getPropertyChangeListeners(); }
    static void call214(java.awt.Component receiver)  { receiver.addPropertyChangeListener((java.lang.String) null, (java.beans.PropertyChangeListener) null); }
    static void call215(java.awt.Component receiver)  { receiver.removePropertyChangeListener((java.lang.String) null, (java.beans.PropertyChangeListener) null); }
    static void call216(java.awt.Component receiver)  { java.beans.PropertyChangeListener[] value = receiver.getPropertyChangeListeners((java.lang.String) null); }
    static void call217(java.awt.Component receiver)  { receiver.firePropertyChange((java.lang.String) null, (java.lang.Object) null, (java.lang.Object) null); }
    static void call218(java.awt.Component receiver)  { receiver.firePropertyChange((java.lang.String) null, false, false); }
    static void call219(java.awt.Component receiver)  { receiver.firePropertyChange((java.lang.String) null, (int) 0, (int) 0); }
    static void call220(java.awt.Component receiver)  { receiver.firePropertyChange((java.lang.String) null, (byte) 0, (byte) 0); }
    static void call221(java.awt.Component receiver)  { receiver.firePropertyChange((java.lang.String) null, (char) 0, (char) 0); }
    static void call222(java.awt.Component receiver)  { receiver.firePropertyChange((java.lang.String) null, (short) 0, (short) 0); }
    static void call223(java.awt.Component receiver)  { receiver.firePropertyChange((java.lang.String) null, (long) 0, (long) 0); }
    static void call224(java.awt.Component receiver)  { receiver.firePropertyChange((java.lang.String) null, (float) 0, (float) 0); }
    static void call225(java.awt.Component receiver)  { receiver.firePropertyChange((java.lang.String) null, (double) 0, (double) 0); }
    static void call226(java.awt.Component receiver)  { receiver.setComponentOrientation((java.awt.ComponentOrientation) null); }
    static void call227(java.awt.Component receiver)  { java.awt.ComponentOrientation value = receiver.getComponentOrientation(); }
    static void call228(java.awt.Component receiver)  { receiver.applyComponentOrientation((java.awt.ComponentOrientation) null); }
    static void call229(java.awt.Component receiver)  { javax.accessibility.AccessibleContext value = receiver.getAccessibleContext(); }
    static void call230(java.awt.Component receiver)  { receiver.setMixingCutoutShape((java.awt.Shape) null); }
}

abstract class Override_java_awt_Component extends java.awt.Component {
    Override_java_awt_Component()  { super(); }
    @Override
    public java.lang.String getName()  { throw new UnsupportedOperationException(); }
    @Override
    public void setName(java.lang.String arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Container getParent()  { throw new UnsupportedOperationException(); }
    @Override
    public void setDropTarget(java.awt.dnd.DropTarget arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.dnd.DropTarget getDropTarget()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.GraphicsConfiguration getGraphicsConfiguration()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Toolkit getToolkit()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isValid()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isDisplayable()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isVisible()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Point getMousePosition() throws java.awt.HeadlessException { throw new UnsupportedOperationException(); }
    @Override
    public boolean isShowing()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isEnabled()  { throw new UnsupportedOperationException(); }
    @Override
    public void setEnabled(boolean arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void enable()  { throw new UnsupportedOperationException(); }
    @Override
    public void enable(boolean arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void disable()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isDoubleBuffered()  { throw new UnsupportedOperationException(); }
    @Override
    public void enableInputMethods(boolean arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setVisible(boolean arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void show()  { throw new UnsupportedOperationException(); }
    @Override
    public void show(boolean arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void hide()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Color getForeground()  { throw new UnsupportedOperationException(); }
    @Override
    public void setForeground(java.awt.Color arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isForegroundSet()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Color getBackground()  { throw new UnsupportedOperationException(); }
    @Override
    public void setBackground(java.awt.Color arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isBackgroundSet()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Font getFont()  { throw new UnsupportedOperationException(); }
    @Override
    public void setFont(java.awt.Font arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isFontSet()  { throw new UnsupportedOperationException(); }
    @Override
    public java.util.Locale getLocale()  { throw new UnsupportedOperationException(); }
    @Override
    public void setLocale(java.util.Locale arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.ColorModel getColorModel()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Point getLocation()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Point getLocationOnScreen()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Point location()  { throw new UnsupportedOperationException(); }
    @Override
    public void setLocation(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void move(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void setLocation(java.awt.Point arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Dimension getSize()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Dimension size()  { throw new UnsupportedOperationException(); }
    @Override
    public void setSize(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void resize(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void setSize(java.awt.Dimension arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void resize(java.awt.Dimension arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Rectangle getBounds()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Rectangle bounds()  { throw new UnsupportedOperationException(); }
    @Override
    public void setBounds(int arg0, int arg1, int arg2, int arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public void reshape(int arg0, int arg1, int arg2, int arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public void setBounds(java.awt.Rectangle arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int getX()  { throw new UnsupportedOperationException(); }
    @Override
    public int getY()  { throw new UnsupportedOperationException(); }
    @Override
    public int getWidth()  { throw new UnsupportedOperationException(); }
    @Override
    public int getHeight()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Rectangle getBounds(java.awt.Rectangle arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Dimension getSize(java.awt.Dimension arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Point getLocation(java.awt.Point arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isOpaque()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isLightweight()  { throw new UnsupportedOperationException(); }
    @Override
    public void setPreferredSize(java.awt.Dimension arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isPreferredSizeSet()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Dimension getPreferredSize()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Dimension preferredSize()  { throw new UnsupportedOperationException(); }
    @Override
    public void setMinimumSize(java.awt.Dimension arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isMinimumSizeSet()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Dimension getMinimumSize()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Dimension minimumSize()  { throw new UnsupportedOperationException(); }
    @Override
    public void setMaximumSize(java.awt.Dimension arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isMaximumSizeSet()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Dimension getMaximumSize()  { throw new UnsupportedOperationException(); }
    @Override
    public float getAlignmentX()  { throw new UnsupportedOperationException(); }
    @Override
    public float getAlignmentY()  { throw new UnsupportedOperationException(); }
    @Override
    public int getBaseline(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Component.BaselineResizeBehavior getBaselineResizeBehavior()  { throw new UnsupportedOperationException(); }
    @Override
    public void doLayout()  { throw new UnsupportedOperationException(); }
    @Override
    public void layout()  { throw new UnsupportedOperationException(); }
    @Override
    public void validate()  { throw new UnsupportedOperationException(); }
    @Override
    public void invalidate()  { throw new UnsupportedOperationException(); }
    @Override
    public void revalidate()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Graphics getGraphics()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.FontMetrics getFontMetrics(java.awt.Font arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setCursor(java.awt.Cursor arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Cursor getCursor()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isCursorSet()  { throw new UnsupportedOperationException(); }
    @Override
    public void paint(java.awt.Graphics arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void update(java.awt.Graphics arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void paintAll(java.awt.Graphics arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void repaint()  { throw new UnsupportedOperationException(); }
    @Override
    public void repaint(long arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void repaint(int arg0, int arg1, int arg2, int arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public void repaint(long arg0, int arg1, int arg2, int arg3, int arg4)  { throw new UnsupportedOperationException(); }
    @Override
    public void print(java.awt.Graphics arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void printAll(java.awt.Graphics arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean imageUpdate(java.awt.Image arg0, int arg1, int arg2, int arg3, int arg4, int arg5)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Image createImage(java.awt.image.ImageProducer arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Image createImage(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.VolatileImage createVolatileImage(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.image.VolatileImage createVolatileImage(int arg0, int arg1, java.awt.ImageCapabilities arg2) throws java.awt.AWTException { throw new UnsupportedOperationException(); }
    @Override
    public boolean prepareImage(java.awt.Image arg0, java.awt.image.ImageObserver arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean prepareImage(java.awt.Image arg0, int arg1, int arg2, java.awt.image.ImageObserver arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public int checkImage(java.awt.Image arg0, java.awt.image.ImageObserver arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public int checkImage(java.awt.Image arg0, int arg1, int arg2, java.awt.image.ImageObserver arg3)  { throw new UnsupportedOperationException(); }
    @Override
    public void setIgnoreRepaint(boolean arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean getIgnoreRepaint()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean contains(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean inside(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean contains(java.awt.Point arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Component getComponentAt(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Component locate(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Component getComponentAt(java.awt.Point arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void deliverEvent(java.awt.Event arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean postEvent(java.awt.Event arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void addComponentListener(java.awt.event.ComponentListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void removeComponentListener(java.awt.event.ComponentListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.event.ComponentListener[] getComponentListeners()  { throw new UnsupportedOperationException(); }
    @Override
    public void addFocusListener(java.awt.event.FocusListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void removeFocusListener(java.awt.event.FocusListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.event.FocusListener[] getFocusListeners()  { throw new UnsupportedOperationException(); }
    @Override
    public void addHierarchyListener(java.awt.event.HierarchyListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void removeHierarchyListener(java.awt.event.HierarchyListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.event.HierarchyListener[] getHierarchyListeners()  { throw new UnsupportedOperationException(); }
    @Override
    public void addHierarchyBoundsListener(java.awt.event.HierarchyBoundsListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void removeHierarchyBoundsListener(java.awt.event.HierarchyBoundsListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.event.HierarchyBoundsListener[] getHierarchyBoundsListeners()  { throw new UnsupportedOperationException(); }
    @Override
    public void addKeyListener(java.awt.event.KeyListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void removeKeyListener(java.awt.event.KeyListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.event.KeyListener[] getKeyListeners()  { throw new UnsupportedOperationException(); }
    @Override
    public void addMouseListener(java.awt.event.MouseListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void removeMouseListener(java.awt.event.MouseListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.event.MouseListener[] getMouseListeners()  { throw new UnsupportedOperationException(); }
    @Override
    public void addMouseMotionListener(java.awt.event.MouseMotionListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void removeMouseMotionListener(java.awt.event.MouseMotionListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.event.MouseMotionListener[] getMouseMotionListeners()  { throw new UnsupportedOperationException(); }
    @Override
    public void addMouseWheelListener(java.awt.event.MouseWheelListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void removeMouseWheelListener(java.awt.event.MouseWheelListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.event.MouseWheelListener[] getMouseWheelListeners()  { throw new UnsupportedOperationException(); }
    @Override
    public void addInputMethodListener(java.awt.event.InputMethodListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void removeInputMethodListener(java.awt.event.InputMethodListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.event.InputMethodListener[] getInputMethodListeners()  { throw new UnsupportedOperationException(); }
    @Override
    public <T extends java.util.EventListener> T[] getListeners(java.lang.Class<T> arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.im.InputMethodRequests getInputMethodRequests()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.im.InputContext getInputContext()  { throw new UnsupportedOperationException(); }
    @Override
    protected java.awt.AWTEvent coalesceEvents(java.awt.AWTEvent arg0, java.awt.AWTEvent arg1)  { throw new UnsupportedOperationException(); }
    @Override
    protected void processEvent(java.awt.AWTEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    protected void processComponentEvent(java.awt.event.ComponentEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    protected void processFocusEvent(java.awt.event.FocusEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    protected void processKeyEvent(java.awt.event.KeyEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    protected void processMouseEvent(java.awt.event.MouseEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    protected void processMouseMotionEvent(java.awt.event.MouseEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    protected void processMouseWheelEvent(java.awt.event.MouseWheelEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    protected void processInputMethodEvent(java.awt.event.InputMethodEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    protected void processHierarchyEvent(java.awt.event.HierarchyEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    protected void processHierarchyBoundsEvent(java.awt.event.HierarchyEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean handleEvent(java.awt.Event arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean mouseDown(java.awt.Event arg0, int arg1, int arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean mouseDrag(java.awt.Event arg0, int arg1, int arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean mouseUp(java.awt.Event arg0, int arg1, int arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean mouseMove(java.awt.Event arg0, int arg1, int arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean mouseEnter(java.awt.Event arg0, int arg1, int arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean mouseExit(java.awt.Event arg0, int arg1, int arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean keyDown(java.awt.Event arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean keyUp(java.awt.Event arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean action(java.awt.Event arg0, java.lang.Object arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void addNotify()  { throw new UnsupportedOperationException(); }
    @Override
    public void removeNotify()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean gotFocus(java.awt.Event arg0, java.lang.Object arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean lostFocus(java.awt.Event arg0, java.lang.Object arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isFocusTraversable()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isFocusable()  { throw new UnsupportedOperationException(); }
    @Override
    public void setFocusable(boolean arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setFocusTraversalKeys(int arg0, java.util.Set<? extends java.awt.AWTKeyStroke> arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.util.Set<java.awt.AWTKeyStroke> getFocusTraversalKeys(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean areFocusTraversalKeysSet(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setFocusTraversalKeysEnabled(boolean arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean getFocusTraversalKeysEnabled()  { throw new UnsupportedOperationException(); }
    @Override
    public void requestFocus()  { throw new UnsupportedOperationException(); }
    @Override
    public void requestFocus(java.awt.event.FocusEvent.Cause arg0)  { throw new UnsupportedOperationException(); }
    @Override
    protected boolean requestFocus(boolean arg0)  { throw new UnsupportedOperationException(); }
    @Override
    protected boolean requestFocus(boolean arg0, java.awt.event.FocusEvent.Cause arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean requestFocusInWindow()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean requestFocusInWindow(java.awt.event.FocusEvent.Cause arg0)  { throw new UnsupportedOperationException(); }
    @Override
    protected boolean requestFocusInWindow(boolean arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Container getFocusCycleRootAncestor()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isFocusCycleRoot(java.awt.Container arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void transferFocus()  { throw new UnsupportedOperationException(); }
    @Override
    public void nextFocus()  { throw new UnsupportedOperationException(); }
    @Override
    public void transferFocusBackward()  { throw new UnsupportedOperationException(); }
    @Override
    public void transferFocusUpCycle()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean hasFocus()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isFocusOwner()  { throw new UnsupportedOperationException(); }
    @Override
    public void add(java.awt.PopupMenu arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void remove(java.awt.MenuComponent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    protected java.lang.String paramString()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String toString()  { throw new UnsupportedOperationException(); }
    @Override
    public void list()  { throw new UnsupportedOperationException(); }
    @Override
    public void list(java.io.PrintStream arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void list(java.io.PrintStream arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void list(java.io.PrintWriter arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void list(java.io.PrintWriter arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void addPropertyChangeListener(java.beans.PropertyChangeListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void removePropertyChangeListener(java.beans.PropertyChangeListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.beans.PropertyChangeListener[] getPropertyChangeListeners()  { throw new UnsupportedOperationException(); }
    @Override
    public void addPropertyChangeListener(java.lang.String arg0, java.beans.PropertyChangeListener arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void removePropertyChangeListener(java.lang.String arg0, java.beans.PropertyChangeListener arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.beans.PropertyChangeListener[] getPropertyChangeListeners(java.lang.String arg0)  { throw new UnsupportedOperationException(); }
    @Override
    protected void firePropertyChange(java.lang.String arg0, java.lang.Object arg1, java.lang.Object arg2)  { throw new UnsupportedOperationException(); }
    @Override
    protected void firePropertyChange(java.lang.String arg0, boolean arg1, boolean arg2)  { throw new UnsupportedOperationException(); }
    @Override
    protected void firePropertyChange(java.lang.String arg0, int arg1, int arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public void firePropertyChange(java.lang.String arg0, byte arg1, byte arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public void firePropertyChange(java.lang.String arg0, char arg1, char arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public void firePropertyChange(java.lang.String arg0, short arg1, short arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public void firePropertyChange(java.lang.String arg0, long arg1, long arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public void firePropertyChange(java.lang.String arg0, float arg1, float arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public void firePropertyChange(java.lang.String arg0, double arg1, double arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public void setComponentOrientation(java.awt.ComponentOrientation arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.ComponentOrientation getComponentOrientation()  { throw new UnsupportedOperationException(); }
    @Override
    public void applyComponentOrientation(java.awt.ComponentOrientation arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.AccessibleContext getAccessibleContext()  { throw new UnsupportedOperationException(); }
    @Override
    public void setMixingCutoutShape(java.awt.Shape arg0)  { throw new UnsupportedOperationException(); }
}
