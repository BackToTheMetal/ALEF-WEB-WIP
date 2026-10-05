package java.awt;

import java.awt.event.WindowEvent;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;

public class Frame extends Window {
    public static final int DEFAULT_CURSOR = Cursor.DEFAULT_CURSOR;
    public static final int CROSSHAIR_CURSOR = Cursor.CROSSHAIR_CURSOR;
    public static final int TEXT_CURSOR = Cursor.TEXT_CURSOR;
    public static final int WAIT_CURSOR = Cursor.WAIT_CURSOR;
    public static final int SW_RESIZE_CURSOR = Cursor.SW_RESIZE_CURSOR;
    public static final int SE_RESIZE_CURSOR = Cursor.SE_RESIZE_CURSOR;
    public static final int NW_RESIZE_CURSOR = Cursor.NW_RESIZE_CURSOR;
    public static final int NE_RESIZE_CURSOR = Cursor.NE_RESIZE_CURSOR;
    public static final int N_RESIZE_CURSOR = Cursor.N_RESIZE_CURSOR;
    public static final int S_RESIZE_CURSOR = Cursor.S_RESIZE_CURSOR;
    public static final int W_RESIZE_CURSOR = Cursor.W_RESIZE_CURSOR;
    public static final int E_RESIZE_CURSOR = Cursor.E_RESIZE_CURSOR;
    public static final int HAND_CURSOR = Cursor.HAND_CURSOR;
    public static final int MOVE_CURSOR = Cursor.MOVE_CURSOR;
    public static final int NORMAL = 0;
    public static final int ICONIFIED = 1;
    public static final int MAXIMIZED_HORIZ = 2;
    public static final int MAXIMIZED_VERT = 4;
    public static final int MAXIMIZED_BOTH = MAXIMIZED_HORIZ | MAXIMIZED_VERT;
    private Rectangle maximizedBounds;
    private MenuBar menuBar;
    private String title;
    private boolean resizable = true;
    private boolean undecorated;
    private int state;

    public Frame() throws HeadlessException {
        this("");
    }

    public Frame(String title) throws HeadlessException {
        this(title, null);
    }

    public Frame(GraphicsConfiguration configuration) {
        this("", configuration);
    }

    public Frame(String title, GraphicsConfiguration configuration) {
        super("section", null, configuration);
        this.title = title == null ? "" : title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? "" : title;
        setName(this.title);
    }

    public boolean isResizable() {
        return resizable;
    }

    public void setResizable(boolean resizable) {
        this.resizable = resizable;
    }

    public synchronized void setState(int state) {
        int previous = this.state;
        this.state = state;
        if (previous != state) {
            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_STATE_CHANGED, previous, state));
            if ((previous & ICONIFIED) == 0 && (state & ICONIFIED) != 0) {
                dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_ICONIFIED));
            } else if ((previous & ICONIFIED) != 0 && (state & ICONIFIED) == 0) {
                dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_DEICONIFIED));
            }
        }
    }

    public void setExtendedState(int state) {
        setState(state);
    }

    public synchronized int getState() {
        return state;
    }

    public int getExtendedState() {
        return getState();
    }

    public void setMaximizedBounds(Rectangle bounds) {
        maximizedBounds = bounds == null ? null : new Rectangle(bounds);
    }

    public Rectangle getMaximizedBounds() {
        return maximizedBounds == null ? null : new Rectangle(maximizedBounds);
    }

    public void setUndecorated(boolean undecorated) {
        if (isDisplayable()) {
            throw new IllegalComponentStateException("frame is displayable");
        }
        this.undecorated = undecorated;
    }

    public boolean isUndecorated() {
        return undecorated;
    }

    public void setCursor(int cursorType) {
        setCursor(Cursor.getPredefinedCursor(cursorType));
    }

    public int getCursorType() {
        return getCursor().getType();
    }

    public Image getIconImage() {
        java.util.List<Image> icons = getIconImages();
        return icons.isEmpty() ? null : icons.get(0);
    }

    public MenuBar getMenuBar() {
        return menuBar;
    }

    @Override
    public Insets getInsets() {
        Insets insets = super.getInsets();
        if (menuBar != null) {
            insets.top += menuBar.getBarHeight();
        }
        return insets;
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        menuBarChanged();
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            menuBarChanged();
        }
    }

    @Override
    void refreshPeerFont() {
        super.refreshPeerFont();
        if (menuBar != null) {
            menuBar.synchronizePeer();
        }
    }

    void menuBarChanged() {
        invalidate();
        if (isDisplayable()) {
            validate();
        }
    }

    public void setMenuBar(MenuBar menuBar) {
        synchronized (getTreeLock()) {
            if (this.menuBar == menuBar) {
                return;
            }
            if (menuBar != null && menuBar.getParent() != null) {
                menuBar.getParent().remove(menuBar);
            }
            if (this.menuBar != null) {
                remove(this.menuBar);
            }
            this.menuBar = menuBar;
            if (menuBar != null) {
                menuBar.setParent(this);
                if (isDisplayable()) {
                    menuBar.addNotify();
                }
            }
            menuBarChanged();
        }
    }

    @Override
    public void remove(MenuComponent component) {
        if (component == menuBar) {
            menuBar.removeNotify();
            menuBar.setParent(null);
            menuBar = null;
            menuBarChanged();
            return;
        }
        super.remove(component);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        if (menuBar != null) {
            menuBar.addNotify();
        }
    }

    @Override
    public void removeNotify() {
        if (menuBar != null) {
            menuBar.removeNotify();
        }
        super.removeNotify();
    }

    @Override
    public void setIconImage(Image image) {
        super.setIconImage(image);
    }

    public static Frame[] getFrames() {
        Window[] windows = getWindows();
        int count = 0;
        for (Window window : windows) {
            if (window instanceof Frame) {
                count++;
            }
        }
        Frame[] frames = new Frame[count];
        int index = 0;
        for (Window window : windows) {
            if (window instanceof Frame) {
                frames[index++] = (Frame) window;
            }
        }
        return frames;
    }

    @Override
    public AccessibleContext getAccessibleContext() {
        return new AccessibleAWTFrame();
    }

    protected class AccessibleAWTFrame extends AccessibleAWTWindow {
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.FRAME;
        }
    }
}
