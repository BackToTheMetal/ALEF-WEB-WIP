package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_FocusTraversalPolicy {
    static void call0(java.awt.FocusTraversalPolicy receiver)  { java.awt.Component value = receiver.getComponentAfter((java.awt.Container) null, (java.awt.Component) null); }
    static void call1(java.awt.FocusTraversalPolicy receiver)  { java.awt.Component value = receiver.getComponentBefore((java.awt.Container) null, (java.awt.Component) null); }
    static void call2(java.awt.FocusTraversalPolicy receiver)  { java.awt.Component value = receiver.getFirstComponent((java.awt.Container) null); }
    static void call3(java.awt.FocusTraversalPolicy receiver)  { java.awt.Component value = receiver.getLastComponent((java.awt.Container) null); }
    static void call4(java.awt.FocusTraversalPolicy receiver)  { java.awt.Component value = receiver.getDefaultComponent((java.awt.Container) null); }
    static void call5(java.awt.FocusTraversalPolicy receiver)  { java.awt.Component value = receiver.getInitialComponent((java.awt.Window) null); }
}

abstract class Override_java_awt_FocusTraversalPolicy extends java.awt.FocusTraversalPolicy {
    Override_java_awt_FocusTraversalPolicy()  { super(); }
    @Override
    public java.awt.Component getComponentAfter(java.awt.Container arg0, java.awt.Component arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Component getComponentBefore(java.awt.Container arg0, java.awt.Component arg1)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Component getFirstComponent(java.awt.Container arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Component getLastComponent(java.awt.Container arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Component getDefaultComponent(java.awt.Container arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Component getInitialComponent(java.awt.Window arg0)  { throw new UnsupportedOperationException(); }
}
