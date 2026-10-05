package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_MenuBar_AccessibleAWTMenuBar {
    static void call0(java.awt.MenuBar outer)  { java.awt.MenuBar.AccessibleAWTMenuBar value = outer.new AccessibleAWTMenuBar(); }
    static void call1(java.awt.MenuBar.AccessibleAWTMenuBar receiver)  { javax.accessibility.AccessibleRole value = receiver.getAccessibleRole(); }
}

abstract class Override_java_awt_MenuBar_AccessibleAWTMenuBar extends java.awt.MenuBar.AccessibleAWTMenuBar {
    Override_java_awt_MenuBar_AccessibleAWTMenuBar(java.awt.MenuBar outer)  { outer.super(); }
    @Override
    public javax.accessibility.AccessibleRole getAccessibleRole()  { throw new UnsupportedOperationException(); }
}
