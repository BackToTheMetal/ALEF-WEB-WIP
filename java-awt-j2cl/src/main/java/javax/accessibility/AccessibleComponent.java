package javax.accessibility;

public interface AccessibleComponent {
    java.awt.Color getBackground();
    void setBackground(java.awt.Color color);
    java.awt.Color getForeground();
    void setForeground(java.awt.Color color);
    java.awt.Cursor getCursor();
    void setCursor(java.awt.Cursor cursor);
    java.awt.Font getFont();
    void setFont(java.awt.Font font);
    java.awt.FontMetrics getFontMetrics(java.awt.Font font);
    boolean isEnabled();
    void setEnabled(boolean enabled);
    boolean isVisible();
    void setVisible(boolean visible);
    boolean isShowing();
    boolean contains(java.awt.Point point);
    java.awt.Point getLocationOnScreen();
    java.awt.Point getLocation();
    void setLocation(java.awt.Point point);
    java.awt.Rectangle getBounds();
    void setBounds(java.awt.Rectangle bounds);
    java.awt.Dimension getSize();
    void setSize(java.awt.Dimension size);
    javax.accessibility.Accessible getAccessibleAt(java.awt.Point point);
    boolean isFocusTraversable();
    void requestFocus();
    void addFocusListener(java.awt.event.FocusListener listener);
    void removeFocusListener(java.awt.event.FocusListener listener);
}
