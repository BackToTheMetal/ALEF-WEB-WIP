package java.awt;

import java.io.Serializable;
import java.util.Locale;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleStateSet;

public abstract class MenuComponent implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Object TREE_LOCK = new Object();
    private Font font;
    private String name;
    private MenuContainer parent;

    public MenuComponent() throws HeadlessException {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MenuContainer getParent() {
        return parent;
    }

    public Font getFont() {
        if (font != null) {
            return font;
        }
        return parent == null ? null : parent.getFont();
    }

    public void setFont(Font font) {
        this.font = font;
        refreshMenuPeer();
    }

    public void removeNotify() {
    }

    public boolean postEvent(Event event) {
        if (parent != null) {
            parent.postEvent(event);
        }
        return false;
    }

    public final void dispatchEvent(AWTEvent event) {
        if (event == null) {
            throw new NullPointerException("event");
        }
        Toolkit.notifyAWTEventListeners(event);
        if (eventEnabled(event)) {
            processEvent(event);
        } else if (event instanceof java.awt.event.ActionEvent && parent instanceof MenuComponent) {
            event.setSource(parent);
            ((MenuComponent) parent).dispatchEvent(event);
        }
    }

    protected boolean eventEnabled(AWTEvent event) {
        return false;
    }

    protected void processEvent(AWTEvent event) {
    }

    protected String paramString() {
        return name == null ? "" : name;
    }

    @Override
    public String toString() {
        return getClass().getName() + "[" + paramString() + "]";
    }

    public AccessibleContext getAccessibleContext() {
        return null;
    }

    void setParent(MenuContainer parent) {
        this.parent = parent;
    }

    int getAccessibleChildIndex(MenuComponent child) {
        return -1;
    }

    protected final Object getTreeLock() {
        return TREE_LOCK;
    }

    final void refreshMenuPeer() {
        MenuComponent root = this;
        while (root.getParent() instanceof MenuComponent) {
            root = (MenuComponent) root.getParent();
        }
        if (root instanceof MenuBar) {
            ((MenuBar) root).synchronizePeer();
        } else if (root instanceof PopupMenu) {
            ((PopupMenu) root).synchronizePeer();
        }
    }

    final String getMenuCssFont() {
        return Component.toCssFont(getFont());
    }

    protected abstract class AccessibleAWTMenuComponent extends AccessibleContext implements java.io.Serializable, javax.accessibility.AccessibleComponent, javax.accessibility.AccessibleSelection {
        public javax.accessibility.AccessibleComponent getAccessibleComponent() { return this; }
        public javax.accessibility.AccessibleSelection getAccessibleSelection() { return this; }
        public int getAccessibleSelectionCount() { return 0; }
        public javax.accessibility.Accessible getAccessibleSelection(int index) { return null; }
        public boolean isAccessibleChildSelected(int index) { return false; }
        public void addAccessibleSelection(int index) {}
        public void removeAccessibleSelection(int index) {}
        public void clearAccessibleSelection() {}
        public void selectAllAccessibleSelection() {}
        public Color getBackground() { return null; }
        public void setBackground(Color color) {  }
        public Color getForeground() { return null; }
        public void setForeground(Color color) {  }
        public Cursor getCursor() { return null; }
        public void setCursor(Cursor cursor) {  }
        public Font getFont() { return MenuComponent.this.getFont(); }
        public void setFont(Font font) { MenuComponent.this.setFont(font); }
        public FontMetrics getFontMetrics(Font font) { return null; }
        public boolean isEnabled() { return true; }
        public void setEnabled(boolean enabled) {  }
        public boolean isVisible() { return true; }
        public void setVisible(boolean visible) {  }
        public boolean isShowing() { return true; }
        public boolean contains(Point point) { return false; }
        public Point getLocationOnScreen() { return null; }
        public Point getLocation() { return null; }
        public void setLocation(Point point) {  }
        public Rectangle getBounds() { return null; }
        public void setBounds(Rectangle bounds) {  }
        public Dimension getSize() { return null; }
        public void setSize(Dimension size) {  }
        public javax.accessibility.Accessible getAccessibleAt(Point point) { return null; }
        public boolean isFocusTraversable() { return true; }
        public void requestFocus() {  }
        public void addFocusListener(java.awt.event.FocusListener listener) {  }
        public void removeFocusListener(java.awt.event.FocusListener listener) {  }

        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.AWT_COMPONENT;
        }

        @Override
        public AccessibleStateSet getAccessibleStateSet() {
            return new AccessibleStateSet();
        }

        @Override
        public Accessible getAccessibleParent() {
            if (accessibleParent != null) {
                return accessibleParent;
            }
            return parent instanceof Accessible ? (Accessible) parent : null;
        }

        @Override
        public int getAccessibleIndexInParent() {
            return parent instanceof MenuComponent ? ((MenuComponent) parent).getAccessibleChildIndex(MenuComponent.this) : -1;
        }

        @Override
        public int getAccessibleChildrenCount() {
            return 0;
        }

        @Override
        public Accessible getAccessibleChild(int index) {
            return null;
        }

        @Override
        public Locale getLocale() {
            return parent instanceof Component ? ((Component) parent).getLocale() : Locale.getDefault();
        }
    }
}
