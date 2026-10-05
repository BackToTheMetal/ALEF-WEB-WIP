package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_Canvas_AccessibleAWTCanvas {
    static void call0(java.awt.Canvas outer)  { java.awt.Canvas.AccessibleAWTCanvas value = outer.new AccessibleAWTCanvas(); }
    static void call1(java.awt.Canvas.AccessibleAWTCanvas receiver)  { javax.accessibility.AccessibleRole value = receiver.getAccessibleRole(); }
}

abstract class Override_java_awt_Canvas_AccessibleAWTCanvas extends java.awt.Canvas.AccessibleAWTCanvas {
    Override_java_awt_Canvas_AccessibleAWTCanvas(java.awt.Canvas outer)  { outer.super(); }
    @Override
    public javax.accessibility.AccessibleRole getAccessibleRole()  { throw new UnsupportedOperationException(); }
}
