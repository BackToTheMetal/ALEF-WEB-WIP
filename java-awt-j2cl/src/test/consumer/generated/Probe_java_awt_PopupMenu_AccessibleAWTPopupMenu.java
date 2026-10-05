package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_PopupMenu_AccessibleAWTPopupMenu {
    static void call0(java.awt.PopupMenu outer)  { java.awt.PopupMenu.AccessibleAWTPopupMenu value = outer.new AccessibleAWTPopupMenu(); }
    static void call1(java.awt.PopupMenu.AccessibleAWTPopupMenu receiver)  { javax.accessibility.AccessibleRole value = receiver.getAccessibleRole(); }
}

abstract class Override_java_awt_PopupMenu_AccessibleAWTPopupMenu extends java.awt.PopupMenu.AccessibleAWTPopupMenu {
    Override_java_awt_PopupMenu_AccessibleAWTPopupMenu(java.awt.PopupMenu outer)  { outer.super(); }
    @Override
    public javax.accessibility.AccessibleRole getAccessibleRole()  { throw new UnsupportedOperationException(); }
}
