package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_Menu_AccessibleAWTMenu {
    static void call0(java.awt.Menu outer)  { java.awt.Menu.AccessibleAWTMenu value = outer.new AccessibleAWTMenu(); }
    static void call1(java.awt.Menu.AccessibleAWTMenu receiver)  { javax.accessibility.AccessibleRole value = receiver.getAccessibleRole(); }
}

abstract class Override_java_awt_Menu_AccessibleAWTMenu extends java.awt.Menu.AccessibleAWTMenu {
    Override_java_awt_Menu_AccessibleAWTMenu(java.awt.Menu outer)  { outer.super(); }
    @Override
    public javax.accessibility.AccessibleRole getAccessibleRole()  { throw new UnsupportedOperationException(); }
}
