package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_Component_AccessibleAWTComponent_AccessibleAWTFocusHandler {
    static void call0(java.awt.Component.AccessibleAWTComponent outer)  { java.awt.Component.AccessibleAWTComponent.AccessibleAWTFocusHandler value = outer.new AccessibleAWTFocusHandler(); }
    static void call1(java.awt.Component.AccessibleAWTComponent.AccessibleAWTFocusHandler receiver)  { receiver.focusGained((java.awt.event.FocusEvent) null); }
    static void call2(java.awt.Component.AccessibleAWTComponent.AccessibleAWTFocusHandler receiver)  { receiver.focusLost((java.awt.event.FocusEvent) null); }
}

abstract class Override_java_awt_Component_AccessibleAWTComponent_AccessibleAWTFocusHandler extends java.awt.Component.AccessibleAWTComponent.AccessibleAWTFocusHandler {
    Override_java_awt_Component_AccessibleAWTComponent_AccessibleAWTFocusHandler(java.awt.Component.AccessibleAWTComponent outer)  { outer.super(); }
    @Override
    public void focusGained(java.awt.event.FocusEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void focusLost(java.awt.event.FocusEvent arg0)  { throw new UnsupportedOperationException(); }
}
