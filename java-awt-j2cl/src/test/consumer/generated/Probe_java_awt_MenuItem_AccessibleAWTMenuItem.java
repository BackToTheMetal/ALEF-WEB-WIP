package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_MenuItem_AccessibleAWTMenuItem {
    static void call0(java.awt.MenuItem outer)  { java.awt.MenuItem.AccessibleAWTMenuItem value = outer.new AccessibleAWTMenuItem(); }
    static void call1(java.awt.MenuItem.AccessibleAWTMenuItem receiver)  { java.lang.String value = receiver.getAccessibleName(); }
    static void call2(java.awt.MenuItem.AccessibleAWTMenuItem receiver)  { javax.accessibility.AccessibleRole value = receiver.getAccessibleRole(); }
    static void call3(java.awt.MenuItem.AccessibleAWTMenuItem receiver)  { javax.accessibility.AccessibleAction value = receiver.getAccessibleAction(); }
    static void call4(java.awt.MenuItem.AccessibleAWTMenuItem receiver)  { javax.accessibility.AccessibleValue value = receiver.getAccessibleValue(); }
    static void call5(java.awt.MenuItem.AccessibleAWTMenuItem receiver)  { int value = receiver.getAccessibleActionCount(); }
    static void call6(java.awt.MenuItem.AccessibleAWTMenuItem receiver)  { java.lang.String value = receiver.getAccessibleActionDescription((int) 0); }
    static void call7(java.awt.MenuItem.AccessibleAWTMenuItem receiver)  { boolean value = receiver.doAccessibleAction((int) 0); }
    static void call8(java.awt.MenuItem.AccessibleAWTMenuItem receiver)  { java.lang.Number value = receiver.getCurrentAccessibleValue(); }
    static void call9(java.awt.MenuItem.AccessibleAWTMenuItem receiver)  { boolean value = receiver.setCurrentAccessibleValue((java.lang.Number) null); }
    static void call10(java.awt.MenuItem.AccessibleAWTMenuItem receiver)  { java.lang.Number value = receiver.getMinimumAccessibleValue(); }
    static void call11(java.awt.MenuItem.AccessibleAWTMenuItem receiver)  { java.lang.Number value = receiver.getMaximumAccessibleValue(); }
}

abstract class Override_java_awt_MenuItem_AccessibleAWTMenuItem extends java.awt.MenuItem.AccessibleAWTMenuItem {
    Override_java_awt_MenuItem_AccessibleAWTMenuItem(java.awt.MenuItem outer)  { outer.super(); }
    @Override
    public java.lang.String getAccessibleName()  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.AccessibleRole getAccessibleRole()  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.AccessibleAction getAccessibleAction()  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.AccessibleValue getAccessibleValue()  { throw new UnsupportedOperationException(); }
    @Override
    public int getAccessibleActionCount()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String getAccessibleActionDescription(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean doAccessibleAction(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Number getCurrentAccessibleValue()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean setCurrentAccessibleValue(java.lang.Number arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Number getMinimumAccessibleValue()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.Number getMaximumAccessibleValue()  { throw new UnsupportedOperationException(); }
}
