package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_Container_AccessibleAWTContainer_AccessibleContainerHandler {
    static void call0(java.awt.Container.AccessibleAWTContainer outer)  { java.awt.Container.AccessibleAWTContainer.AccessibleContainerHandler value = outer.new AccessibleContainerHandler(); }
    static void call1(java.awt.Container.AccessibleAWTContainer.AccessibleContainerHandler receiver)  { receiver.componentAdded((java.awt.event.ContainerEvent) null); }
    static void call2(java.awt.Container.AccessibleAWTContainer.AccessibleContainerHandler receiver)  { receiver.componentRemoved((java.awt.event.ContainerEvent) null); }
}

abstract class Override_java_awt_Container_AccessibleAWTContainer_AccessibleContainerHandler extends java.awt.Container.AccessibleAWTContainer.AccessibleContainerHandler {
    Override_java_awt_Container_AccessibleAWTContainer_AccessibleContainerHandler(java.awt.Container.AccessibleAWTContainer outer)  { outer.super(); }
    @Override
    public void componentAdded(java.awt.event.ContainerEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void componentRemoved(java.awt.event.ContainerEvent arg0)  { throw new UnsupportedOperationException(); }
}
