package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_Choice_AccessibleAWTChoice {
    static void call0(java.awt.Choice outer)  { java.awt.Choice.AccessibleAWTChoice value = outer.new AccessibleAWTChoice(); }
    static void call1(java.awt.Choice.AccessibleAWTChoice receiver)  { javax.accessibility.AccessibleAction value = receiver.getAccessibleAction(); }
    static void call2(java.awt.Choice.AccessibleAWTChoice receiver)  { javax.accessibility.AccessibleRole value = receiver.getAccessibleRole(); }
    static void call3(java.awt.Choice.AccessibleAWTChoice receiver)  { int value = receiver.getAccessibleActionCount(); }
    static void call4(java.awt.Choice.AccessibleAWTChoice receiver)  { java.lang.String value = receiver.getAccessibleActionDescription((int) 0); }
    static void call5(java.awt.Choice.AccessibleAWTChoice receiver)  { boolean value = receiver.doAccessibleAction((int) 0); }
}

abstract class Override_java_awt_Choice_AccessibleAWTChoice extends java.awt.Choice.AccessibleAWTChoice {
    Override_java_awt_Choice_AccessibleAWTChoice(java.awt.Choice outer)  { outer.super(); }
    @Override
    public javax.accessibility.AccessibleAction getAccessibleAction()  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.AccessibleRole getAccessibleRole()  { throw new UnsupportedOperationException(); }
    @Override
    public int getAccessibleActionCount()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String getAccessibleActionDescription(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean doAccessibleAction(int arg0)  { throw new UnsupportedOperationException(); }
}
