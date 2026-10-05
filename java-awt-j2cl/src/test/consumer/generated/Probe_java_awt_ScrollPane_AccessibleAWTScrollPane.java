package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_ScrollPane_AccessibleAWTScrollPane {
    static void call0(java.awt.ScrollPane outer)  { java.awt.ScrollPane.AccessibleAWTScrollPane value = outer.new AccessibleAWTScrollPane(); }
    static void call1(java.awt.ScrollPane.AccessibleAWTScrollPane receiver)  { javax.accessibility.AccessibleRole value = receiver.getAccessibleRole(); }
}

abstract class Override_java_awt_ScrollPane_AccessibleAWTScrollPane extends java.awt.ScrollPane.AccessibleAWTScrollPane {
    Override_java_awt_ScrollPane_AccessibleAWTScrollPane(java.awt.ScrollPane outer)  { outer.super(); }
    @Override
    public javax.accessibility.AccessibleRole getAccessibleRole()  { throw new UnsupportedOperationException(); }
}
