package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_Panel_AccessibleAWTPanel {
    static void call0(java.awt.Panel outer)  { java.awt.Panel.AccessibleAWTPanel value = outer.new AccessibleAWTPanel(); }
    static void call1(java.awt.Panel.AccessibleAWTPanel receiver)  { javax.accessibility.AccessibleRole value = receiver.getAccessibleRole(); }
}

abstract class Override_java_awt_Panel_AccessibleAWTPanel extends java.awt.Panel.AccessibleAWTPanel {
    Override_java_awt_Panel_AccessibleAWTPanel(java.awt.Panel outer)  { outer.super(); }
    @Override
    public javax.accessibility.AccessibleRole getAccessibleRole()  { throw new UnsupportedOperationException(); }
}
