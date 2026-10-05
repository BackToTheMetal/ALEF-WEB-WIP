package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_Container_AccessibleAWTContainer {
    static void call0(java.awt.Container.AccessibleAWTContainer receiver) { java.awt.event.ContainerListener value = receiver.accessibleContainerHandler; receiver.accessibleContainerHandler = value; }
    static void call1(java.awt.Container outer)  { java.awt.Container.AccessibleAWTContainer value = outer.new AccessibleAWTContainer(); }
    static void call2(java.awt.Container.AccessibleAWTContainer receiver)  { int value = receiver.getAccessibleChildrenCount(); }
    static void call3(java.awt.Container.AccessibleAWTContainer receiver)  { javax.accessibility.Accessible value = receiver.getAccessibleChild((int) 0); }
    static void call4(java.awt.Container.AccessibleAWTContainer receiver)  { javax.accessibility.Accessible value = receiver.getAccessibleAt((java.awt.Point) null); }
    static void call5(java.awt.Container.AccessibleAWTContainer receiver)  { receiver.addPropertyChangeListener((java.beans.PropertyChangeListener) null); }
    static void call6(java.awt.Container.AccessibleAWTContainer receiver)  { receiver.removePropertyChangeListener((java.beans.PropertyChangeListener) null); }
}

abstract class Override_java_awt_Container_AccessibleAWTContainer extends java.awt.Container.AccessibleAWTContainer {
    Override_java_awt_Container_AccessibleAWTContainer(java.awt.Container outer)  { outer.super(); }
    @Override
    public int getAccessibleChildrenCount()  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.Accessible getAccessibleChild(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.Accessible getAccessibleAt(java.awt.Point arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void addPropertyChangeListener(java.beans.PropertyChangeListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void removePropertyChangeListener(java.beans.PropertyChangeListener arg0)  { throw new UnsupportedOperationException(); }
}
