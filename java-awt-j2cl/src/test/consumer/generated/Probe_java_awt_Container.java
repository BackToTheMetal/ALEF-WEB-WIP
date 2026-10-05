package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_Container {
    static void call0()  { java.awt.Container value = new java.awt.Container(); }
    static void call1(java.awt.Container receiver)  { int value = receiver.getComponentCount(); }
    static void call2(java.awt.Container receiver)  { int value = receiver.countComponents(); }
    static void call3(java.awt.Container receiver)  { java.awt.Component value = receiver.getComponent((int) 0); }
    static void call4(java.awt.Container receiver)  { java.awt.Component[] value = receiver.getComponents(); }
    static void call5(java.awt.Container receiver)  { java.awt.Insets value = receiver.getInsets(); }
    static void call6(java.awt.Container receiver)  { java.awt.Insets value = receiver.insets(); }
    static void call7(java.awt.Container receiver)  { java.awt.Component value = receiver.add((java.awt.Component) null); }
    static void call8(java.awt.Container receiver)  { java.awt.Component value = receiver.add((java.lang.String) null, (java.awt.Component) null); }
    static void call9(java.awt.Container receiver)  { java.awt.Component value = receiver.add((java.awt.Component) null, (int) 0); }
    static void call10(java.awt.Container receiver)  { receiver.setComponentZOrder((java.awt.Component) null, (int) 0); }
    static void call11(java.awt.Container receiver)  { int value = receiver.getComponentZOrder((java.awt.Component) null); }
    static void call12(java.awt.Container receiver)  { receiver.add((java.awt.Component) null, (java.lang.Object) null); }
    static void call13(java.awt.Container receiver)  { receiver.add((java.awt.Component) null, (java.lang.Object) null, (int) 0); }
    static void call14(java.awt.Container receiver)  { receiver.addImpl((java.awt.Component) null, (java.lang.Object) null, (int) 0); }
    static void call15(java.awt.Container receiver)  { receiver.remove((int) 0); }
    static void call16(java.awt.Container receiver)  { receiver.remove((java.awt.Component) null); }
    static void call17(java.awt.Container receiver)  { receiver.removeAll(); }
    static void call18(java.awt.Container receiver)  { java.awt.LayoutManager value = receiver.getLayout(); }
    static void call19(java.awt.Container receiver)  { receiver.setLayout((java.awt.LayoutManager) null); }
    static void call20(java.awt.Container receiver)  { receiver.doLayout(); }
    static void call21(java.awt.Container receiver)  { receiver.layout(); }
    static void call22(java.awt.Container receiver)  { boolean value = receiver.isValidateRoot(); }
    static void call23(java.awt.Container receiver)  { receiver.invalidate(); }
    static void call24(java.awt.Container receiver)  { receiver.validate(); }
    static void call25(java.awt.Container receiver)  { receiver.validateTree(); }
    static void call26(java.awt.Container receiver)  { receiver.setFont((java.awt.Font) null); }
    static void call27(java.awt.Container receiver)  { java.awt.Dimension value = receiver.getPreferredSize(); }
    static void call28(java.awt.Container receiver)  { java.awt.Dimension value = receiver.preferredSize(); }
    static void call29(java.awt.Container receiver)  { java.awt.Dimension value = receiver.getMinimumSize(); }
    static void call30(java.awt.Container receiver)  { java.awt.Dimension value = receiver.minimumSize(); }
    static void call31(java.awt.Container receiver)  { java.awt.Dimension value = receiver.getMaximumSize(); }
    static void call32(java.awt.Container receiver)  { float value = receiver.getAlignmentX(); }
    static void call33(java.awt.Container receiver)  { float value = receiver.getAlignmentY(); }
    static void call34(java.awt.Container receiver)  { receiver.paint((java.awt.Graphics) null); }
    static void call35(java.awt.Container receiver)  { receiver.update((java.awt.Graphics) null); }
    static void call36(java.awt.Container receiver)  { receiver.print((java.awt.Graphics) null); }
    static void call37(java.awt.Container receiver)  { receiver.paintComponents((java.awt.Graphics) null); }
    static void call38(java.awt.Container receiver)  { receiver.printComponents((java.awt.Graphics) null); }
    static void call39(java.awt.Container receiver)  { receiver.addContainerListener((java.awt.event.ContainerListener) null); }
    static void call40(java.awt.Container receiver)  { receiver.removeContainerListener((java.awt.event.ContainerListener) null); }
    static void call41(java.awt.Container receiver)  { java.awt.event.ContainerListener[] value = receiver.getContainerListeners(); }
    static void call42(java.awt.Container receiver)  { java.util.EventListener[] value = receiver.getListeners((java.lang.Class) null); }
    static void call43(java.awt.Container receiver)  { receiver.processEvent((java.awt.AWTEvent) null); }
    static void call44(java.awt.Container receiver)  { receiver.processContainerEvent((java.awt.event.ContainerEvent) null); }
    static void call45(java.awt.Container receiver)  { receiver.deliverEvent((java.awt.Event) null); }
    static void call46(java.awt.Container receiver)  { java.awt.Component value = receiver.getComponentAt((int) 0, (int) 0); }
    static void call47(java.awt.Container receiver)  { java.awt.Component value = receiver.locate((int) 0, (int) 0); }
    static void call48(java.awt.Container receiver)  { java.awt.Component value = receiver.getComponentAt((java.awt.Point) null); }
    static void call49(java.awt.Container receiver) throws java.awt.HeadlessException { try { java.awt.Point value = receiver.getMousePosition(false); } catch (java.awt.HeadlessException expected) { throw expected; } }
    static void call50(java.awt.Container receiver)  { java.awt.Component value = receiver.findComponentAt((int) 0, (int) 0); }
    static void call51(java.awt.Container receiver)  { java.awt.Component value = receiver.findComponentAt((java.awt.Point) null); }
    static void call52(java.awt.Container receiver)  { receiver.addNotify(); }
    static void call53(java.awt.Container receiver)  { receiver.removeNotify(); }
    static void call54(java.awt.Container receiver)  { boolean value = receiver.isAncestorOf((java.awt.Component) null); }
    static void call55(java.awt.Container receiver)  { java.lang.String value = receiver.paramString(); }
    static void call56(java.awt.Container receiver)  { receiver.list((java.io.PrintStream) null, (int) 0); }
    static void call57(java.awt.Container receiver)  { receiver.list((java.io.PrintWriter) null, (int) 0); }
    static void call58(java.awt.Container receiver)  { receiver.setFocusTraversalKeys((int) 0, (java.util.Set) null); }
    static void call59(java.awt.Container receiver)  { java.util.Set value = receiver.getFocusTraversalKeys((int) 0); }
    static void call60(java.awt.Container receiver)  { boolean value = receiver.areFocusTraversalKeysSet((int) 0); }
    static void call61(java.awt.Container receiver)  { boolean value = receiver.isFocusCycleRoot((java.awt.Container) null); }
    static void call62(java.awt.Container receiver)  { receiver.setFocusTraversalPolicy((java.awt.FocusTraversalPolicy) null); }
    static void call63(java.awt.Container receiver)  { java.awt.FocusTraversalPolicy value = receiver.getFocusTraversalPolicy(); }
    static void call64(java.awt.Container receiver)  { boolean value = receiver.isFocusTraversalPolicySet(); }
    static void call65(java.awt.Container receiver)  { receiver.setFocusCycleRoot(false); }
    static void call66(java.awt.Container receiver)  { boolean value = receiver.isFocusCycleRoot(); }
    static void call67(java.awt.Container receiver)  { receiver.setFocusTraversalPolicyProvider(false); }
    static void call68(java.awt.Container receiver)  { boolean value = receiver.isFocusTraversalPolicyProvider(); }
    static void call69(java.awt.Container receiver)  { receiver.transferFocusDownCycle(); }
    static void call70(java.awt.Container receiver)  { receiver.applyComponentOrientation((java.awt.ComponentOrientation) null); }
    static void call71(java.awt.Container receiver)  { receiver.addPropertyChangeListener((java.beans.PropertyChangeListener) null); }
    static void call72(java.awt.Container receiver)  { receiver.addPropertyChangeListener((java.lang.String) null, (java.beans.PropertyChangeListener) null); }
}

abstract class Override_java_awt_Container extends java.awt.Container {
    Override_java_awt_Container()  { super(); }
    @Override
    public int getComponentCount()  { throw new UnsupportedOperationException(); }
    @Override
    public int countComponents()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Component getComponent(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Component[] getComponents()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Insets getInsets()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Insets insets()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Component add(java.awt.Component arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Component add(java.lang.String arg0, java.awt.Component arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Component add(java.awt.Component arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void setComponentZOrder(java.awt.Component arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public int getComponentZOrder(java.awt.Component arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void add(java.awt.Component arg0, java.lang.Object arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void add(java.awt.Component arg0, java.lang.Object arg1, int arg2)  { throw new UnsupportedOperationException(); }
    @Override
    protected void addImpl(java.awt.Component arg0, java.lang.Object arg1, int arg2)  { throw new UnsupportedOperationException(); }
    @Override
    public void remove(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void remove(java.awt.Component arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void removeAll()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.LayoutManager getLayout()  { throw new UnsupportedOperationException(); }
    @Override
    public void setLayout(java.awt.LayoutManager arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void doLayout()  { throw new UnsupportedOperationException(); }
    @Override
    public void layout()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isValidateRoot()  { throw new UnsupportedOperationException(); }
    @Override
    public void invalidate()  { throw new UnsupportedOperationException(); }
    @Override
    public void validate()  { throw new UnsupportedOperationException(); }
    @Override
    protected void validateTree()  { throw new UnsupportedOperationException(); }
    @Override
    public void setFont(java.awt.Font arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Dimension getPreferredSize()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Dimension preferredSize()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Dimension getMinimumSize()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Dimension minimumSize()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Dimension getMaximumSize()  { throw new UnsupportedOperationException(); }
    @Override
    public float getAlignmentX()  { throw new UnsupportedOperationException(); }
    @Override
    public float getAlignmentY()  { throw new UnsupportedOperationException(); }
    @Override
    public void paint(java.awt.Graphics arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void update(java.awt.Graphics arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void print(java.awt.Graphics arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void paintComponents(java.awt.Graphics arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void printComponents(java.awt.Graphics arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void addContainerListener(java.awt.event.ContainerListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void removeContainerListener(java.awt.event.ContainerListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.event.ContainerListener[] getContainerListeners()  { throw new UnsupportedOperationException(); }
    @Override
    public <T extends java.util.EventListener> T[] getListeners(java.lang.Class<T> arg0)  { throw new UnsupportedOperationException(); }
    @Override
    protected void processEvent(java.awt.AWTEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    protected void processContainerEvent(java.awt.event.ContainerEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void deliverEvent(java.awt.Event arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Component getComponentAt(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Component locate(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Component getComponentAt(java.awt.Point arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Point getMousePosition(boolean arg0) throws java.awt.HeadlessException { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Component findComponentAt(int arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Component findComponentAt(java.awt.Point arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void addNotify()  { throw new UnsupportedOperationException(); }
    @Override
    public void removeNotify()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isAncestorOf(java.awt.Component arg0)  { throw new UnsupportedOperationException(); }
    @Override
    protected java.lang.String paramString()  { throw new UnsupportedOperationException(); }
    @Override
    public void list(java.io.PrintStream arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void list(java.io.PrintWriter arg0, int arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void setFocusTraversalKeys(int arg0, java.util.Set<? extends java.awt.AWTKeyStroke> arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.util.Set<java.awt.AWTKeyStroke> getFocusTraversalKeys(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean areFocusTraversalKeysSet(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isFocusCycleRoot(java.awt.Container arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setFocusTraversalPolicy(java.awt.FocusTraversalPolicy arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.FocusTraversalPolicy getFocusTraversalPolicy()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isFocusTraversalPolicySet()  { throw new UnsupportedOperationException(); }
    @Override
    public void setFocusCycleRoot(boolean arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isFocusCycleRoot()  { throw new UnsupportedOperationException(); }
    @Override
    public void transferFocusDownCycle()  { throw new UnsupportedOperationException(); }
    @Override
    public void applyComponentOrientation(java.awt.ComponentOrientation arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void addPropertyChangeListener(java.beans.PropertyChangeListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void addPropertyChangeListener(java.lang.String arg0, java.beans.PropertyChangeListener arg1)  { throw new UnsupportedOperationException(); }
}
