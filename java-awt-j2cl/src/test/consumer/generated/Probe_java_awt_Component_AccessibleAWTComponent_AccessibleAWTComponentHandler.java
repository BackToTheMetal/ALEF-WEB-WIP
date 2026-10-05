package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_Component_AccessibleAWTComponent_AccessibleAWTComponentHandler {
    static void call0(java.awt.Component.AccessibleAWTComponent outer)  { java.awt.Component.AccessibleAWTComponent.AccessibleAWTComponentHandler value = outer.new AccessibleAWTComponentHandler(); }
    static void call1(java.awt.Component.AccessibleAWTComponent.AccessibleAWTComponentHandler receiver)  { receiver.componentHidden((java.awt.event.ComponentEvent) null); }
    static void call2(java.awt.Component.AccessibleAWTComponent.AccessibleAWTComponentHandler receiver)  { receiver.componentShown((java.awt.event.ComponentEvent) null); }
    static void call3(java.awt.Component.AccessibleAWTComponent.AccessibleAWTComponentHandler receiver)  { receiver.componentMoved((java.awt.event.ComponentEvent) null); }
    static void call4(java.awt.Component.AccessibleAWTComponent.AccessibleAWTComponentHandler receiver)  { receiver.componentResized((java.awt.event.ComponentEvent) null); }
}

abstract class Override_java_awt_Component_AccessibleAWTComponent_AccessibleAWTComponentHandler extends java.awt.Component.AccessibleAWTComponent.AccessibleAWTComponentHandler {
    Override_java_awt_Component_AccessibleAWTComponent_AccessibleAWTComponentHandler(java.awt.Component.AccessibleAWTComponent outer)  { outer.super(); }
    @Override
    public void componentHidden(java.awt.event.ComponentEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void componentShown(java.awt.event.ComponentEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void componentMoved(java.awt.event.ComponentEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void componentResized(java.awt.event.ComponentEvent arg0)  { throw new UnsupportedOperationException(); }
}
