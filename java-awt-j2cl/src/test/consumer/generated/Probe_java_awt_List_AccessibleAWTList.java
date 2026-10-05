package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_List_AccessibleAWTList {
    static void call0(java.awt.List outer)  { java.awt.List.AccessibleAWTList value = outer.new AccessibleAWTList(); }
    static void call1(java.awt.List.AccessibleAWTList receiver)  { receiver.actionPerformed((java.awt.event.ActionEvent) null); }
    static void call2(java.awt.List.AccessibleAWTList receiver)  { receiver.itemStateChanged((java.awt.event.ItemEvent) null); }
    static void call3(java.awt.List.AccessibleAWTList receiver)  { javax.accessibility.AccessibleStateSet value = receiver.getAccessibleStateSet(); }
    static void call4(java.awt.List.AccessibleAWTList receiver)  { javax.accessibility.AccessibleRole value = receiver.getAccessibleRole(); }
    static void call5(java.awt.List.AccessibleAWTList receiver)  { javax.accessibility.Accessible value = receiver.getAccessibleAt((java.awt.Point) null); }
    static void call6(java.awt.List.AccessibleAWTList receiver)  { int value = receiver.getAccessibleChildrenCount(); }
    static void call7(java.awt.List.AccessibleAWTList receiver)  { javax.accessibility.Accessible value = receiver.getAccessibleChild((int) 0); }
    static void call8(java.awt.List.AccessibleAWTList receiver)  { javax.accessibility.AccessibleSelection value = receiver.getAccessibleSelection(); }
    static void call9(java.awt.List.AccessibleAWTList receiver)  { int value = receiver.getAccessibleSelectionCount(); }
    static void call10(java.awt.List.AccessibleAWTList receiver)  { javax.accessibility.Accessible value = receiver.getAccessibleSelection((int) 0); }
    static void call11(java.awt.List.AccessibleAWTList receiver)  { boolean value = receiver.isAccessibleChildSelected((int) 0); }
    static void call12(java.awt.List.AccessibleAWTList receiver)  { receiver.addAccessibleSelection((int) 0); }
    static void call13(java.awt.List.AccessibleAWTList receiver)  { receiver.removeAccessibleSelection((int) 0); }
    static void call14(java.awt.List.AccessibleAWTList receiver)  { receiver.clearAccessibleSelection(); }
    static void call15(java.awt.List.AccessibleAWTList receiver)  { receiver.selectAllAccessibleSelection(); }
}

abstract class Override_java_awt_List_AccessibleAWTList extends java.awt.List.AccessibleAWTList {
    Override_java_awt_List_AccessibleAWTList(java.awt.List outer)  { outer.super(); }
    @Override
    public void actionPerformed(java.awt.event.ActionEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void itemStateChanged(java.awt.event.ItemEvent arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.AccessibleStateSet getAccessibleStateSet()  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.AccessibleRole getAccessibleRole()  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.Accessible getAccessibleAt(java.awt.Point arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int getAccessibleChildrenCount()  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.Accessible getAccessibleChild(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.AccessibleSelection getAccessibleSelection()  { throw new UnsupportedOperationException(); }
    @Override
    public int getAccessibleSelectionCount()  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.Accessible getAccessibleSelection(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isAccessibleChildSelected(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void addAccessibleSelection(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void removeAccessibleSelection(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void clearAccessibleSelection()  { throw new UnsupportedOperationException(); }
    @Override
    public void selectAllAccessibleSelection()  { throw new UnsupportedOperationException(); }
}
