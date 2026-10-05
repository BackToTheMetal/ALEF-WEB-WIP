package javax.accessibility;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_javax_accessibility_AccessibleContext {
    static void call0() { java.lang.String value = javax.accessibility.AccessibleContext.ACCESSIBLE_NAME_PROPERTY; }
    static void call1() { java.lang.String value = javax.accessibility.AccessibleContext.ACCESSIBLE_DESCRIPTION_PROPERTY; }
    static void call2() { java.lang.String value = javax.accessibility.AccessibleContext.ACCESSIBLE_STATE_PROPERTY; }
    static void call3() { java.lang.String value = javax.accessibility.AccessibleContext.ACCESSIBLE_VALUE_PROPERTY; }
    static void call4() { java.lang.String value = javax.accessibility.AccessibleContext.ACCESSIBLE_SELECTION_PROPERTY; }
    static void call5() { java.lang.String value = javax.accessibility.AccessibleContext.ACCESSIBLE_CARET_PROPERTY; }
    static void call6() { java.lang.String value = javax.accessibility.AccessibleContext.ACCESSIBLE_VISIBLE_DATA_PROPERTY; }
    static void call7() { java.lang.String value = javax.accessibility.AccessibleContext.ACCESSIBLE_CHILD_PROPERTY; }
    static void call8() { java.lang.String value = javax.accessibility.AccessibleContext.ACCESSIBLE_ACTIVE_DESCENDANT_PROPERTY; }
    static void call9() { java.lang.String value = javax.accessibility.AccessibleContext.ACCESSIBLE_TABLE_CAPTION_CHANGED; }
    static void call10() { java.lang.String value = javax.accessibility.AccessibleContext.ACCESSIBLE_TABLE_SUMMARY_CHANGED; }
    static void call11() { java.lang.String value = javax.accessibility.AccessibleContext.ACCESSIBLE_TABLE_MODEL_CHANGED; }
    static void call12() { java.lang.String value = javax.accessibility.AccessibleContext.ACCESSIBLE_TABLE_ROW_HEADER_CHANGED; }
    static void call13() { java.lang.String value = javax.accessibility.AccessibleContext.ACCESSIBLE_TABLE_ROW_DESCRIPTION_CHANGED; }
    static void call14() { java.lang.String value = javax.accessibility.AccessibleContext.ACCESSIBLE_TABLE_COLUMN_HEADER_CHANGED; }
    static void call15() { java.lang.String value = javax.accessibility.AccessibleContext.ACCESSIBLE_TABLE_COLUMN_DESCRIPTION_CHANGED; }
    static void call16() { java.lang.String value = javax.accessibility.AccessibleContext.ACCESSIBLE_ACTION_PROPERTY; }
    static void call17() { java.lang.String value = javax.accessibility.AccessibleContext.ACCESSIBLE_HYPERTEXT_OFFSET; }
    static void call18() { java.lang.String value = javax.accessibility.AccessibleContext.ACCESSIBLE_TEXT_PROPERTY; }
    static void call19() { java.lang.String value = javax.accessibility.AccessibleContext.ACCESSIBLE_INVALIDATE_CHILDREN; }
    static void call20() { java.lang.String value = javax.accessibility.AccessibleContext.ACCESSIBLE_TEXT_ATTRIBUTES_CHANGED; }
    static void call21() { java.lang.String value = javax.accessibility.AccessibleContext.ACCESSIBLE_COMPONENT_BOUNDS_CHANGED; }
    static void call22(javax.accessibility.AccessibleContext receiver) { javax.accessibility.Accessible value = receiver.accessibleParent; receiver.accessibleParent = value; }
    static void call23(javax.accessibility.AccessibleContext receiver) { java.lang.String value = receiver.accessibleName; receiver.accessibleName = value; }
    static void call24(javax.accessibility.AccessibleContext receiver) { java.lang.String value = receiver.accessibleDescription; receiver.accessibleDescription = value; }
    static void call25(javax.accessibility.AccessibleContext receiver)  { java.lang.String value = receiver.getAccessibleName(); }
    static void call26(javax.accessibility.AccessibleContext receiver)  { receiver.setAccessibleName((java.lang.String) null); }
    static void call27(javax.accessibility.AccessibleContext receiver)  { java.lang.String value = receiver.getAccessibleDescription(); }
    static void call28(javax.accessibility.AccessibleContext receiver)  { receiver.setAccessibleDescription((java.lang.String) null); }
    static void call29(javax.accessibility.AccessibleContext receiver)  { javax.accessibility.AccessibleRole value = receiver.getAccessibleRole(); }
    static void call30(javax.accessibility.AccessibleContext receiver)  { javax.accessibility.AccessibleStateSet value = receiver.getAccessibleStateSet(); }
    static void call31(javax.accessibility.AccessibleContext receiver)  { javax.accessibility.Accessible value = receiver.getAccessibleParent(); }
    static void call32(javax.accessibility.AccessibleContext receiver)  { receiver.setAccessibleParent((javax.accessibility.Accessible) null); }
    static void call33(javax.accessibility.AccessibleContext receiver)  { int value = receiver.getAccessibleIndexInParent(); }
    static void call34(javax.accessibility.AccessibleContext receiver)  { int value = receiver.getAccessibleChildrenCount(); }
    static void call35(javax.accessibility.AccessibleContext receiver)  { javax.accessibility.Accessible value = receiver.getAccessibleChild((int) 0); }
    static void call36(javax.accessibility.AccessibleContext receiver) throws java.awt.IllegalComponentStateException { try { java.util.Locale value = receiver.getLocale(); } catch (java.awt.IllegalComponentStateException expected) { throw expected; } }
    static void call37(javax.accessibility.AccessibleContext receiver)  { receiver.addPropertyChangeListener((java.beans.PropertyChangeListener) null); }
    static void call38(javax.accessibility.AccessibleContext receiver)  { receiver.removePropertyChangeListener((java.beans.PropertyChangeListener) null); }
    static void call39(javax.accessibility.AccessibleContext receiver)  { javax.accessibility.AccessibleAction value = receiver.getAccessibleAction(); }
    static void call40(javax.accessibility.AccessibleContext receiver)  { javax.accessibility.AccessibleComponent value = receiver.getAccessibleComponent(); }
    static void call41(javax.accessibility.AccessibleContext receiver)  { javax.accessibility.AccessibleSelection value = receiver.getAccessibleSelection(); }
    static void call42(javax.accessibility.AccessibleContext receiver)  { javax.accessibility.AccessibleValue value = receiver.getAccessibleValue(); }
    static void call43(javax.accessibility.AccessibleContext receiver)  { javax.accessibility.AccessibleIcon[] value = receiver.getAccessibleIcon(); }
    static void call44(javax.accessibility.AccessibleContext receiver)  { javax.accessibility.AccessibleRelationSet value = receiver.getAccessibleRelationSet(); }
    static void call45(javax.accessibility.AccessibleContext receiver)  { javax.accessibility.AccessibleTable value = receiver.getAccessibleTable(); }
    static void call46(javax.accessibility.AccessibleContext receiver)  { receiver.firePropertyChange((java.lang.String) null, (java.lang.Object) null, (java.lang.Object) null); }
}

abstract class Override_javax_accessibility_AccessibleContext extends javax.accessibility.AccessibleContext {
    Override_javax_accessibility_AccessibleContext()  { super(); }
    @Override
    public java.lang.String getAccessibleName()  { throw new UnsupportedOperationException(); }
    @Override
    public void setAccessibleName(java.lang.String arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String getAccessibleDescription()  { throw new UnsupportedOperationException(); }
    @Override
    public void setAccessibleDescription(java.lang.String arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.AccessibleRole getAccessibleRole()  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.AccessibleStateSet getAccessibleStateSet()  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.Accessible getAccessibleParent()  { throw new UnsupportedOperationException(); }
    @Override
    public void setAccessibleParent(javax.accessibility.Accessible arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public int getAccessibleIndexInParent()  { throw new UnsupportedOperationException(); }
    @Override
    public int getAccessibleChildrenCount()  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.Accessible getAccessibleChild(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.util.Locale getLocale() throws java.awt.IllegalComponentStateException { throw new UnsupportedOperationException(); }
    @Override
    public void addPropertyChangeListener(java.beans.PropertyChangeListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void removePropertyChangeListener(java.beans.PropertyChangeListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.AccessibleAction getAccessibleAction()  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.AccessibleComponent getAccessibleComponent()  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.AccessibleSelection getAccessibleSelection()  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.AccessibleValue getAccessibleValue()  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.AccessibleIcon[] getAccessibleIcon()  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.AccessibleRelationSet getAccessibleRelationSet()  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.AccessibleTable getAccessibleTable()  { throw new UnsupportedOperationException(); }
    @Override
    public void firePropertyChange(java.lang.String arg0, java.lang.Object arg1, java.lang.Object arg2)  { throw new UnsupportedOperationException(); }
}
