package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_ContainerOrderFocusTraversalPolicy {
    static void call0()  { java.awt.ContainerOrderFocusTraversalPolicy value = new java.awt.ContainerOrderFocusTraversalPolicy(); }
    static void call1(java.awt.ContainerOrderFocusTraversalPolicy receiver)  { java.awt.Component value = receiver.getComponentAfter((java.awt.Container) null, (java.awt.Component) null); }
    static void call2(java.awt.ContainerOrderFocusTraversalPolicy receiver)  { java.awt.Component value = receiver.getComponentBefore((java.awt.Container) null, (java.awt.Component) null); }
    static void call3(java.awt.ContainerOrderFocusTraversalPolicy receiver)  { java.awt.Component value = receiver.getFirstComponent((java.awt.Container) null); }
    static void call4(java.awt.ContainerOrderFocusTraversalPolicy receiver)  { java.awt.Component value = receiver.getLastComponent((java.awt.Container) null); }
    static void call5(java.awt.ContainerOrderFocusTraversalPolicy receiver)  { java.awt.Component value = receiver.getDefaultComponent((java.awt.Container) null); }
    static void call6(java.awt.ContainerOrderFocusTraversalPolicy receiver)  { receiver.setImplicitDownCycleTraversal(false); }
    static void call7(java.awt.ContainerOrderFocusTraversalPolicy receiver)  { boolean value = receiver.getImplicitDownCycleTraversal(); }
    static void call8(java.awt.ContainerOrderFocusTraversalPolicy receiver)  { boolean value = receiver.accept((java.awt.Component) null); }
}

abstract class Override_java_awt_ContainerOrderFocusTraversalPolicy extends java.awt.ContainerOrderFocusTraversalPolicy {
    Override_java_awt_ContainerOrderFocusTraversalPolicy()  { super(); }
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
    public void setImplicitDownCycleTraversal(boolean arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean getImplicitDownCycleTraversal()  { throw new UnsupportedOperationException(); }
    @Override
    protected boolean accept(java.awt.Component arg0)  { throw new UnsupportedOperationException(); }
}
