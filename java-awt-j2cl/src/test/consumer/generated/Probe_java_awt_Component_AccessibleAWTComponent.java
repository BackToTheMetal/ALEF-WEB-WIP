package java.awt;

// Generated from retained OpenJDK signatures. Compile only; never execute.
final class Probe_java_awt_Component_AccessibleAWTComponent {
    static void call0(java.awt.Component.AccessibleAWTComponent receiver) { java.awt.event.ComponentListener value = receiver.accessibleAWTComponentHandler; receiver.accessibleAWTComponentHandler = value; }
    static void call1(java.awt.Component.AccessibleAWTComponent receiver) { java.awt.event.FocusListener value = receiver.accessibleAWTFocusHandler; receiver.accessibleAWTFocusHandler = value; }
    static void call2(java.awt.Component.AccessibleAWTComponent receiver)  { receiver.addPropertyChangeListener((java.beans.PropertyChangeListener) null); }
    static void call3(java.awt.Component.AccessibleAWTComponent receiver)  { receiver.removePropertyChangeListener((java.beans.PropertyChangeListener) null); }
    static void call4(java.awt.Component.AccessibleAWTComponent receiver)  { java.lang.String value = receiver.getAccessibleName(); }
    static void call5(java.awt.Component.AccessibleAWTComponent receiver)  { java.lang.String value = receiver.getAccessibleDescription(); }
    static void call6(java.awt.Component.AccessibleAWTComponent receiver)  { javax.accessibility.AccessibleRole value = receiver.getAccessibleRole(); }
    static void call7(java.awt.Component.AccessibleAWTComponent receiver)  { javax.accessibility.AccessibleStateSet value = receiver.getAccessibleStateSet(); }
    static void call8(java.awt.Component.AccessibleAWTComponent receiver)  { javax.accessibility.Accessible value = receiver.getAccessibleParent(); }
    static void call9(java.awt.Component.AccessibleAWTComponent receiver)  { int value = receiver.getAccessibleIndexInParent(); }
    static void call10(java.awt.Component.AccessibleAWTComponent receiver)  { int value = receiver.getAccessibleChildrenCount(); }
    static void call11(java.awt.Component.AccessibleAWTComponent receiver)  { javax.accessibility.Accessible value = receiver.getAccessibleChild((int) 0); }
    static void call12(java.awt.Component.AccessibleAWTComponent receiver)  { java.util.Locale value = receiver.getLocale(); }
    static void call13(java.awt.Component.AccessibleAWTComponent receiver)  { javax.accessibility.AccessibleComponent value = receiver.getAccessibleComponent(); }
    static void call14(java.awt.Component.AccessibleAWTComponent receiver)  { java.awt.Color value = receiver.getBackground(); }
    static void call15(java.awt.Component.AccessibleAWTComponent receiver)  { receiver.setBackground((java.awt.Color) null); }
    static void call16(java.awt.Component.AccessibleAWTComponent receiver)  { java.awt.Color value = receiver.getForeground(); }
    static void call17(java.awt.Component.AccessibleAWTComponent receiver)  { receiver.setForeground((java.awt.Color) null); }
    static void call18(java.awt.Component.AccessibleAWTComponent receiver)  { java.awt.Cursor value = receiver.getCursor(); }
    static void call19(java.awt.Component.AccessibleAWTComponent receiver)  { receiver.setCursor((java.awt.Cursor) null); }
    static void call20(java.awt.Component.AccessibleAWTComponent receiver)  { java.awt.Font value = receiver.getFont(); }
    static void call21(java.awt.Component.AccessibleAWTComponent receiver)  { receiver.setFont((java.awt.Font) null); }
    static void call22(java.awt.Component.AccessibleAWTComponent receiver)  { java.awt.FontMetrics value = receiver.getFontMetrics((java.awt.Font) null); }
    static void call23(java.awt.Component.AccessibleAWTComponent receiver)  { boolean value = receiver.isEnabled(); }
    static void call24(java.awt.Component.AccessibleAWTComponent receiver)  { receiver.setEnabled(false); }
    static void call25(java.awt.Component.AccessibleAWTComponent receiver)  { boolean value = receiver.isVisible(); }
    static void call26(java.awt.Component.AccessibleAWTComponent receiver)  { receiver.setVisible(false); }
    static void call27(java.awt.Component.AccessibleAWTComponent receiver)  { boolean value = receiver.isShowing(); }
    static void call28(java.awt.Component.AccessibleAWTComponent receiver)  { boolean value = receiver.contains((java.awt.Point) null); }
    static void call29(java.awt.Component.AccessibleAWTComponent receiver)  { java.awt.Point value = receiver.getLocationOnScreen(); }
    static void call30(java.awt.Component.AccessibleAWTComponent receiver)  { java.awt.Point value = receiver.getLocation(); }
    static void call31(java.awt.Component.AccessibleAWTComponent receiver)  { receiver.setLocation((java.awt.Point) null); }
    static void call32(java.awt.Component.AccessibleAWTComponent receiver)  { java.awt.Rectangle value = receiver.getBounds(); }
    static void call33(java.awt.Component.AccessibleAWTComponent receiver)  { receiver.setBounds((java.awt.Rectangle) null); }
    static void call34(java.awt.Component.AccessibleAWTComponent receiver)  { java.awt.Dimension value = receiver.getSize(); }
    static void call35(java.awt.Component.AccessibleAWTComponent receiver)  { receiver.setSize((java.awt.Dimension) null); }
    static void call36(java.awt.Component.AccessibleAWTComponent receiver)  { javax.accessibility.Accessible value = receiver.getAccessibleAt((java.awt.Point) null); }
    static void call37(java.awt.Component.AccessibleAWTComponent receiver)  { boolean value = receiver.isFocusTraversable(); }
    static void call38(java.awt.Component.AccessibleAWTComponent receiver)  { receiver.requestFocus(); }
    static void call39(java.awt.Component.AccessibleAWTComponent receiver)  { receiver.addFocusListener((java.awt.event.FocusListener) null); }
    static void call40(java.awt.Component.AccessibleAWTComponent receiver)  { receiver.removeFocusListener((java.awt.event.FocusListener) null); }
}

abstract class Override_java_awt_Component_AccessibleAWTComponent extends java.awt.Component.AccessibleAWTComponent {
    Override_java_awt_Component_AccessibleAWTComponent(java.awt.Component outer)  { outer.super(); }
    @Override
    public void addPropertyChangeListener(java.beans.PropertyChangeListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void removePropertyChangeListener(java.beans.PropertyChangeListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String getAccessibleName()  { throw new UnsupportedOperationException(); }
    @Override
    public java.lang.String getAccessibleDescription()  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.AccessibleRole getAccessibleRole()  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.AccessibleStateSet getAccessibleStateSet()  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.Accessible getAccessibleParent()  { throw new UnsupportedOperationException(); }
    @Override
    public int getAccessibleIndexInParent()  { throw new UnsupportedOperationException(); }
    @Override
    public int getAccessibleChildrenCount()  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.Accessible getAccessibleChild(int arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.util.Locale getLocale()  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.AccessibleComponent getAccessibleComponent()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Color getBackground()  { throw new UnsupportedOperationException(); }
    @Override
    public void setBackground(java.awt.Color arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Color getForeground()  { throw new UnsupportedOperationException(); }
    @Override
    public void setForeground(java.awt.Color arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Cursor getCursor()  { throw new UnsupportedOperationException(); }
    @Override
    public void setCursor(java.awt.Cursor arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Font getFont()  { throw new UnsupportedOperationException(); }
    @Override
    public void setFont(java.awt.Font arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.FontMetrics getFontMetrics(java.awt.Font arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isEnabled()  { throw new UnsupportedOperationException(); }
    @Override
    public void setEnabled(boolean arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isVisible()  { throw new UnsupportedOperationException(); }
    @Override
    public void setVisible(boolean arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isShowing()  { throw new UnsupportedOperationException(); }
    @Override
    public boolean contains(java.awt.Point arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Point getLocationOnScreen()  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Point getLocation()  { throw new UnsupportedOperationException(); }
    @Override
    public void setLocation(java.awt.Point arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Rectangle getBounds()  { throw new UnsupportedOperationException(); }
    @Override
    public void setBounds(java.awt.Rectangle arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public java.awt.Dimension getSize()  { throw new UnsupportedOperationException(); }
    @Override
    public void setSize(java.awt.Dimension arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public javax.accessibility.Accessible getAccessibleAt(java.awt.Point arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public boolean isFocusTraversable()  { throw new UnsupportedOperationException(); }
    @Override
    public void requestFocus()  { throw new UnsupportedOperationException(); }
    @Override
    public void addFocusListener(java.awt.event.FocusListener arg0)  { throw new UnsupportedOperationException(); }
    @Override
    public void removeFocusListener(java.awt.event.FocusListener arg0)  { throw new UnsupportedOperationException(); }
}
