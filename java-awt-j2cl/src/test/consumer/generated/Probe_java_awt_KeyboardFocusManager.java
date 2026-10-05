package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_KeyboardFocusManager {
    static void call0() { int value = java.awt.KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS; }
    static void call1() { int value = java.awt.KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS; }
    static void call2() { int value = java.awt.KeyboardFocusManager.UP_CYCLE_TRAVERSAL_KEYS; }
    static void call3() { int value = java.awt.KeyboardFocusManager.DOWN_CYCLE_TRAVERSAL_KEYS; }
    static void call4()  { java.awt.KeyboardFocusManager value = java.awt.KeyboardFocusManager.getCurrentKeyboardFocusManager(); }
    static void call5() throws java.lang.SecurityException { try { java.awt.KeyboardFocusManager.setCurrentKeyboardFocusManager((java.awt.KeyboardFocusManager) null); } catch (java.lang.SecurityException expected) { throw expected; } }
    static void call6(java.awt.KeyboardFocusManager receiver)  { java.awt.Component value = receiver.getFocusOwner(); }
    static void call7(java.awt.KeyboardFocusManager receiver) throws java.lang.SecurityException { try { java.awt.Component value = receiver.getGlobalFocusOwner(); } catch (java.lang.SecurityException expected) { throw expected; } }
    static void call8(java.awt.KeyboardFocusManager receiver) throws java.lang.SecurityException { try { receiver.setGlobalFocusOwner((java.awt.Component) null); } catch (java.lang.SecurityException expected) { throw expected; } }
    static void call9(java.awt.KeyboardFocusManager receiver)  { receiver.clearFocusOwner(); }
    static void call10(java.awt.KeyboardFocusManager receiver) throws java.lang.SecurityException { try { receiver.clearGlobalFocusOwner(); } catch (java.lang.SecurityException expected) { throw expected; } }
    static void call11(java.awt.KeyboardFocusManager receiver)  { java.awt.Component value = receiver.getPermanentFocusOwner(); }
    static void call12(java.awt.KeyboardFocusManager receiver) throws java.lang.SecurityException { try { java.awt.Component value = receiver.getGlobalPermanentFocusOwner(); } catch (java.lang.SecurityException expected) { throw expected; } }
    static void call13(java.awt.KeyboardFocusManager receiver) throws java.lang.SecurityException { try { receiver.setGlobalPermanentFocusOwner((java.awt.Component) null); } catch (java.lang.SecurityException expected) { throw expected; } }
    static void call14(java.awt.KeyboardFocusManager receiver)  { java.awt.Window value = receiver.getFocusedWindow(); }
    static void call15(java.awt.KeyboardFocusManager receiver) throws java.lang.SecurityException { try { java.awt.Window value = receiver.getGlobalFocusedWindow(); } catch (java.lang.SecurityException expected) { throw expected; } }
    static void call16(java.awt.KeyboardFocusManager receiver) throws java.lang.SecurityException { try { receiver.setGlobalFocusedWindow((java.awt.Window) null); } catch (java.lang.SecurityException expected) { throw expected; } }
    static void call17(java.awt.KeyboardFocusManager receiver)  { java.awt.Window value = receiver.getActiveWindow(); }
    static void call18(java.awt.KeyboardFocusManager receiver) throws java.lang.SecurityException { try { java.awt.Window value = receiver.getGlobalActiveWindow(); } catch (java.lang.SecurityException expected) { throw expected; } }
    static void call19(java.awt.KeyboardFocusManager receiver) throws java.lang.SecurityException { try { receiver.setGlobalActiveWindow((java.awt.Window) null); } catch (java.lang.SecurityException expected) { throw expected; } }
    static void call20(java.awt.KeyboardFocusManager receiver)  { java.awt.FocusTraversalPolicy value = receiver.getDefaultFocusTraversalPolicy(); }
    static void call21(java.awt.KeyboardFocusManager receiver)  { receiver.setDefaultFocusTraversalPolicy((java.awt.FocusTraversalPolicy) null); }
    static void call22(java.awt.KeyboardFocusManager receiver)  { receiver.setDefaultFocusTraversalKeys((int) 0, (java.util.Set) null); }
    static void call23(java.awt.KeyboardFocusManager receiver)  { java.util.Set value = receiver.getDefaultFocusTraversalKeys((int) 0); }
    static void call24(java.awt.KeyboardFocusManager receiver)  { java.awt.Container value = receiver.getCurrentFocusCycleRoot(); }
    static void call25(java.awt.KeyboardFocusManager receiver) throws java.lang.SecurityException { try { java.awt.Container value = receiver.getGlobalCurrentFocusCycleRoot(); } catch (java.lang.SecurityException expected) { throw expected; } }
    static void call26(java.awt.KeyboardFocusManager receiver) throws java.lang.SecurityException { try { receiver.setGlobalCurrentFocusCycleRoot((java.awt.Container) null); } catch (java.lang.SecurityException expected) { throw expected; } }
    static void call27(java.awt.KeyboardFocusManager receiver)  { receiver.addPropertyChangeListener((java.beans.PropertyChangeListener) null); }
    static void call28(java.awt.KeyboardFocusManager receiver)  { receiver.removePropertyChangeListener((java.beans.PropertyChangeListener) null); }
    static void call29(java.awt.KeyboardFocusManager receiver)  { java.beans.PropertyChangeListener[] value = receiver.getPropertyChangeListeners(); }
    static void call30(java.awt.KeyboardFocusManager receiver)  { receiver.addPropertyChangeListener((java.lang.String) null, (java.beans.PropertyChangeListener) null); }
    static void call31(java.awt.KeyboardFocusManager receiver)  { receiver.removePropertyChangeListener((java.lang.String) null, (java.beans.PropertyChangeListener) null); }
    static void call32(java.awt.KeyboardFocusManager receiver)  { java.beans.PropertyChangeListener[] value = receiver.getPropertyChangeListeners((java.lang.String) null); }
    static void call33(java.awt.KeyboardFocusManager receiver)  { receiver.firePropertyChange((java.lang.String) null, (java.lang.Object) null, (java.lang.Object) null); }
    static void call34(java.awt.KeyboardFocusManager receiver)  { receiver.addVetoableChangeListener((java.beans.VetoableChangeListener) null); }
    static void call35(java.awt.KeyboardFocusManager receiver)  { receiver.removeVetoableChangeListener((java.beans.VetoableChangeListener) null); }
    static void call36(java.awt.KeyboardFocusManager receiver)  { java.beans.VetoableChangeListener[] value = receiver.getVetoableChangeListeners(); }
    static void call37(java.awt.KeyboardFocusManager receiver)  { receiver.addVetoableChangeListener((java.lang.String) null, (java.beans.VetoableChangeListener) null); }
    static void call38(java.awt.KeyboardFocusManager receiver)  { receiver.removeVetoableChangeListener((java.lang.String) null, (java.beans.VetoableChangeListener) null); }
    static void call39(java.awt.KeyboardFocusManager receiver)  { java.beans.VetoableChangeListener[] value = receiver.getVetoableChangeListeners((java.lang.String) null); }
    static void call40(java.awt.KeyboardFocusManager receiver) throws java.beans.PropertyVetoException { try { receiver.fireVetoableChange((java.lang.String) null, (java.lang.Object) null, (java.lang.Object) null); } catch (java.beans.PropertyVetoException expected) { throw expected; } }
    static void call41(java.awt.KeyboardFocusManager receiver)  { receiver.addKeyEventDispatcher((java.awt.KeyEventDispatcher) null); }
    static void call42(java.awt.KeyboardFocusManager receiver)  { receiver.removeKeyEventDispatcher((java.awt.KeyEventDispatcher) null); }
    static void call43(java.awt.KeyboardFocusManager receiver)  { java.util.List value = receiver.getKeyEventDispatchers(); }
    static void call44(java.awt.KeyboardFocusManager receiver)  { receiver.addKeyEventPostProcessor((java.awt.KeyEventPostProcessor) null); }
    static void call45(java.awt.KeyboardFocusManager receiver)  { receiver.removeKeyEventPostProcessor((java.awt.KeyEventPostProcessor) null); }
    static void call46(java.awt.KeyboardFocusManager receiver)  { java.util.List value = receiver.getKeyEventPostProcessors(); }
    static void call47(java.awt.KeyboardFocusManager receiver)  { boolean value = receiver.dispatchEvent((java.awt.AWTEvent) null); }
    static void call48(java.awt.KeyboardFocusManager receiver)  { receiver.redispatchEvent((java.awt.Component) null, (java.awt.AWTEvent) null); }
    static void call49(java.awt.KeyboardFocusManager receiver)  { boolean value = receiver.dispatchKeyEvent((java.awt.event.KeyEvent) null); }
    static void call50(java.awt.KeyboardFocusManager receiver)  { boolean value = receiver.postProcessKeyEvent((java.awt.event.KeyEvent) null); }
    static void call51(java.awt.KeyboardFocusManager receiver)  { receiver.processKeyEvent((java.awt.Component) null, (java.awt.event.KeyEvent) null); }
    static void call52(java.awt.KeyboardFocusManager receiver)  { receiver.enqueueKeyEvents((long) 0, (java.awt.Component) null); }
    static void call53(java.awt.KeyboardFocusManager receiver)  { receiver.dequeueKeyEvents((long) 0, (java.awt.Component) null); }
    static void call54(java.awt.KeyboardFocusManager receiver)  { receiver.discardKeyEvents((java.awt.Component) null); }
    static void call55(java.awt.KeyboardFocusManager receiver)  { receiver.focusNextComponent((java.awt.Component) null); }
    static void call56(java.awt.KeyboardFocusManager receiver)  { receiver.focusPreviousComponent((java.awt.Component) null); }
    static void call57(java.awt.KeyboardFocusManager receiver)  { receiver.upFocusCycle((java.awt.Component) null); }
    static void call58(java.awt.KeyboardFocusManager receiver)  { receiver.downFocusCycle((java.awt.Container) null); }
    static void call59(java.awt.KeyboardFocusManager receiver)  { receiver.focusNextComponent(); }
    static void call60(java.awt.KeyboardFocusManager receiver)  { receiver.focusPreviousComponent(); }
    static void call61(java.awt.KeyboardFocusManager receiver)  { receiver.upFocusCycle(); }
    static void call62(java.awt.KeyboardFocusManager receiver)  { receiver.downFocusCycle(); }
}

abstract class Override_java_awt_KeyboardFocusManager extends java.awt.KeyboardFocusManager {
    Override_java_awt_KeyboardFocusManager()  { super(); }
    @Override
    public java.awt.Component getFocusOwner()  { throw new UnsupportedOperationException(); }
    @Override
    protected java.awt.Component getGlobalFocusOwner() throws java.lang.SecurityException { throw new UnsupportedOperationException(); }
    @Override
    protected void setGlobalFocusOwner(java.awt.Component arg0) throws java.lang.SecurityException { throw new UnsupportedOperationException(); }
    @Override
    public void clearFocusOwner()  { throw new UnsupportedOperationException(); }
    @Override
    public void clearGlobalFocusOwner() throws java.lang.SecurityException { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Component getPermanentFocusOwner()  { throw new UnsupportedOperationException(); }
    @Override
    protected java.awt.Component getGlobalPermanentFocusOwner() throws java.lang.SecurityException { throw new UnsupportedOperationException(); }
    @Override
    protected void setGlobalPermanentFocusOwner(java.awt.Component arg0) throws java.lang.SecurityException { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Window getFocusedWindow()  { throw new UnsupportedOperationException(); }
    @Override
    protected java.awt.Window getGlobalFocusedWindow() throws java.lang.SecurityException { throw new UnsupportedOperationException(); }
    @Override
    protected void setGlobalFocusedWindow(java.awt.Window arg0) throws java.lang.SecurityException { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Window getActiveWindow()  { throw new UnsupportedOperationException(); }
    @Override
    protected java.awt.Window getGlobalActiveWindow() throws java.lang.SecurityException { throw new UnsupportedOperationException(); }
    @Override
    protected void setGlobalActiveWindow(java.awt.Window arg0) throws java.lang.SecurityException { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.FocusTraversalPolicy getDefaultFocusTraversalPolicy()  { throw new UnsupportedOperationException(); }
    @Override
    public void setDefaultFocusTraversalPolicy(java.awt.FocusTraversalPolicy arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void setDefaultFocusTraversalKeys(int arg0, java.util.Set<? extends java.awt.AWTKeyStroke> arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.util.Set<java.awt.AWTKeyStroke> getDefaultFocusTraversalKeys(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Container getCurrentFocusCycleRoot()  { throw new UnsupportedOperationException(); }
    @Override
    protected java.awt.Container getGlobalCurrentFocusCycleRoot() throws java.lang.SecurityException { throw new UnsupportedOperationException(); }
    @Override
    public void setGlobalCurrentFocusCycleRoot(java.awt.Container arg0) throws java.lang.SecurityException { throw new UnsupportedOperationException(); }
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
    public void addVetoableChangeListener(java.beans.VetoableChangeListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void removeVetoableChangeListener(java.beans.VetoableChangeListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.beans.VetoableChangeListener[] getVetoableChangeListeners()  { throw new UnsupportedOperationException(); }
    @Override
    public void addVetoableChangeListener(java.lang.String arg0, java.beans.VetoableChangeListener arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public void removeVetoableChangeListener(java.lang.String arg0, java.beans.VetoableChangeListener arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.beans.VetoableChangeListener[] getVetoableChangeListeners(java.lang.String arg0)  { throw new UnsupportedOperationException(); }
    @Override
    protected void fireVetoableChange(java.lang.String arg0, java.lang.Object arg1, java.lang.Object arg2) throws java.beans.PropertyVetoException { throw new UnsupportedOperationException(); }
    @Override
    public void addKeyEventDispatcher(java.awt.KeyEventDispatcher arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void removeKeyEventDispatcher(java.awt.KeyEventDispatcher arg0)  { throw new UnsupportedOperationException(); }
    @Override
    protected java.util.List<java.awt.KeyEventDispatcher> getKeyEventDispatchers()  { throw new UnsupportedOperationException(); }
    @Override
    public void addKeyEventPostProcessor(java.awt.KeyEventPostProcessor arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void removeKeyEventPostProcessor(java.awt.KeyEventPostProcessor arg0)  { throw new UnsupportedOperationException(); }
    @Override
    protected java.util.List<java.awt.KeyEventPostProcessor> getKeyEventPostProcessors()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean dispatchEvent(java.awt.AWTEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean dispatchKeyEvent(java.awt.event.KeyEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean postProcessKeyEvent(java.awt.event.KeyEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void processKeyEvent(java.awt.Component arg0, java.awt.event.KeyEvent arg1)  { throw new UnsupportedOperationException(); }
    @Override
    protected void enqueueKeyEvents(long arg0, java.awt.Component arg1)  { throw new UnsupportedOperationException(); }
    @Override
    protected void dequeueKeyEvents(long arg0, java.awt.Component arg1)  { throw new UnsupportedOperationException(); }
    @Override
    protected void discardKeyEvents(java.awt.Component arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void focusNextComponent(java.awt.Component arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void focusPreviousComponent(java.awt.Component arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void upFocusCycle(java.awt.Component arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void downFocusCycle(java.awt.Container arg0)  { throw new UnsupportedOperationException(); }
}
