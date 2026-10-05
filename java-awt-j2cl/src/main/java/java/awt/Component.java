package java.awt;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.event.ActionEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.TextEvent;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.im.InputContext;
import java.awt.im.InputMethodRequests;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EventListener;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import javax.accessibility.AccessibleContext;
import org.webawt.internal.peer.DomPeer;
import org.webawt.internal.peer.PeerInputEvent;
import org.webawt.internal.peer.PeerCompositionEventHandler;
import org.webawt.internal.peer.PeerDragEvent;
import org.webawt.internal.peer.PeerDragGestureStarter;
import org.webawt.internal.peer.WebAwtRuntime;

public abstract class Component implements ImageObserver, MenuContainer, Serializable {
    private static final long serialVersionUID = 1L;
    public enum BaselineResizeBehavior {
        CONSTANT_ASCENT,
        CONSTANT_DESCENT,
        CENTER_OFFSET,
        OTHER
    }

    public static final float TOP_ALIGNMENT = 0.0f;
    public static final float CENTER_ALIGNMENT = 0.5f;
    public static final float BOTTOM_ALIGNMENT = 1.0f;
    public static final float LEFT_ALIGNMENT = 0.0f;
    public static final float RIGHT_ALIGNMENT = 1.0f;
    private static final Object TREE_LOCK = new Object();
    private final PropertyChangeSupport propertyChanges = new PropertyChangeSupport(this);
    private final java.util.List<ComponentListener> componentListeners = new ArrayList<>();
    private final java.util.List<FocusListener> focusListeners = new ArrayList<>();
    private final java.util.List<HierarchyBoundsListener> hierarchyBoundsListeners = new ArrayList<>();
    private final java.util.List<HierarchyListener> hierarchyListeners = new ArrayList<>();
    private final java.util.List<InputMethodListener> inputMethodListeners = new ArrayList<>();
    private final java.util.List<KeyListener> keyListeners = new ArrayList<>();
    private final java.util.List<MouseListener> mouseListeners = new ArrayList<>();
    private final java.util.List<MouseMotionListener> mouseMotionListeners = new ArrayList<>();
    private final java.util.List<MouseWheelListener> mouseWheelListeners = new ArrayList<>();
    private final java.util.List<PopupMenu> popupMenus = new ArrayList<>();
    private final java.util.List<Set<AWTKeyStroke>> focusTraversalKeys = createFocusTraversalKeyList();
    private final String peerElementName;
    private ComponentOrientation componentOrientation = ComponentOrientation.UNKNOWN;
    private Color background;
    private Color foreground;
    private Cursor cursor;
    private Font font;
    private GraphicsConfiguration graphicsConfiguration;
    private InputContext inputContext;
    private Dimension maximumSize;
    private Dimension minimumSize;
    private Dimension preferredSize;
    private DropTarget dropTarget;
    private DomPeer peer;
    private Component mouseEventTarget;
    private Locale locale;
    private String name;
    private Container parent;
    private boolean enabled = true;
    private boolean focusable = true;
    private boolean focusTraversalKeysEnabled = true;
    private boolean ignoreRepaint;
    private boolean inputMethodsEnabled = true;
    private boolean valid;
    private boolean visible = true;
    private int height;
    private int lastMouseX;
    private int lastMouseY;
    private int width;
    private int x;
    private int y;
    long eventMask;

    protected Component() {
        this("div");
    }

    Component(String peerElementName) {
        this.peerElementName = peerElementName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        String previous = this.name;
        this.name = name;
        propertyChanges.firePropertyChange("name", previous, name);
    }

    public Container getParent() {
        return parent;
    }

    public synchronized void setDropTarget(DropTarget dropTarget) {
        if (this.dropTarget == dropTarget) {
            return;
        }
        DropTarget previous = this.dropTarget;
        this.dropTarget = dropTarget;
        if (previous != null && previous.getComponent() == this) {
            previous.setComponent(null);
        }
        if (dropTarget != null && dropTarget.getComponent() != this) {
            dropTarget.setComponent(this);
        }
    }

    public synchronized DropTarget getDropTarget() {
        return dropTarget;
    }

    public final Object getTreeLock() {
        return TREE_LOCK;
    }

    public boolean isValid() {
        return valid;
    }

    public boolean isDisplayable() {
        return peer != null;
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean isShowing() {
        return visible && peer != null && (parent == null || parent.isShowing());
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        boolean previous = this.enabled;
        this.enabled = enabled;
        if (peer != null) {
            peer.setEnabled(enabled);
        }
        propertyChanges.firePropertyChange("enabled", previous, enabled);
    }

    public void enable() {
        setEnabled(true);
    }

    public void enable(boolean enabled) {
        setEnabled(enabled);
    }

    public void disable() {
        setEnabled(false);
    }

    public void setVisible(boolean visible) {
        boolean previous = this.visible;
        this.visible = visible;
        if (peer != null) {
            peer.setVisible(visible);
        }
        propertyChanges.firePropertyChange("visible", previous, visible);
        if (previous != visible) {
            dispatchEvent(new ComponentEvent(this, visible ? ComponentEvent.COMPONENT_SHOWN : ComponentEvent.COMPONENT_HIDDEN));
            dispatchHierarchyEventToSubtree(HierarchyEvent.HIERARCHY_CHANGED, this, parent, HierarchyEvent.SHOWING_CHANGED);
        }
    }

    public void show() {
        setVisible(true);
    }

    public void show(boolean visible) {
        setVisible(visible);
    }

    public void hide() {
        setVisible(false);
    }

    public Color getForeground() {
        if (foreground != null) {
            return foreground;
        }
        return parent == null ? null : parent.getForeground();
    }

    public void setForeground(Color foreground) {
        Color previous = this.foreground;
        this.foreground = foreground;
        if (peer != null) {
            peer.setForeground(toCssColor(foreground));
        }
        propertyChanges.firePropertyChange("foreground", previous, foreground);
    }

    public boolean isForegroundSet() {
        return foreground != null;
    }

    public Color getBackground() {
        if (background != null) {
            return background;
        }
        return parent == null ? null : parent.getBackground();
    }

    public void setBackground(Color background) {
        Color previous = this.background;
        this.background = background;
        if (peer != null) {
            peer.setBackground(toCssColor(background));
        }
        propertyChanges.firePropertyChange("background", previous, background);
    }

    public boolean isBackgroundSet() {
        return background != null;
    }

    public Font getFont() {
        if (font != null) {
            return font;
        }
        return parent == null ? null : parent.getFont();
    }

    public void setFont(Font font) {
        Font previous = this.font;
        this.font = font;
        refreshPeerFont();
        propertyChanges.firePropertyChange("font", previous, font);
    }

    void refreshPeerFont() {
        if (peer != null) {
            peer.setFont(toCssFont(getFont()));
        }
        invalidate();
        if (this instanceof Container) {
            for (Component child : ((Container) this).getComponents()) {
                if (!child.isFontSet()) {
                    child.refreshPeerFont();
                }
            }
        }
    }

    static Font effectiveFont(Font font) {
        return font == null ? new Font(Font.DIALOG, Font.PLAIN, 12) : font;
    }

    public boolean isFontSet() {
        return font != null;
    }

    public FontMetrics getFontMetrics(Font font) {
        if (font == null) {
            throw new NullPointerException("font");
        }
        return new FontMetrics(font) {
        };
    }

    public GraphicsConfiguration getGraphicsConfiguration() {
        return graphicsConfiguration == null ? GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration() : graphicsConfiguration;
    }

    public Toolkit getToolkit() {
        return Toolkit.getDefaultToolkit();
    }

    public ColorModel getColorModel() {
        return getGraphicsConfiguration().getColorModel();
    }

    public Image createImage(ImageProducer producer) {
        return getToolkit().createImage(producer);
    }

    public Image createImage(int width, int height) {
        return getGraphicsConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
    }

    public boolean prepareImage(Image image, ImageObserver observer) {
        return prepareImage(image, -1, -1, observer);
    }

    public boolean prepareImage(Image image, int width, int height, ImageObserver observer) {
        return getToolkit().prepareImage(image, width, height, observer);
    }

    public int checkImage(Image image, ImageObserver observer) {
        return checkImage(image, -1, -1, observer);
    }

    public int checkImage(Image image, int width, int height, ImageObserver observer) {
        return getToolkit().checkImage(image, width, height, observer);
    }

    public java.awt.image.VolatileImage createVolatileImage(int width, int height) {
        return getGraphicsConfiguration().createCompatibleVolatileImage(width, height);
    }

    public java.awt.image.VolatileImage createVolatileImage(int width, int height, ImageCapabilities capabilities) throws AWTException {
        return getGraphicsConfiguration().createCompatibleVolatileImage(width, height, capabilities);
    }

    @Override
    public boolean imageUpdate(Image image, int flags, int x, int y, int width, int height) {
        return (flags & (ImageObserver.ALLBITS | ImageObserver.ABORT)) == 0;
    }

    @Override
    public void remove(MenuComponent component) {
        int index = popupMenus.indexOf(component);
        if (index >= 0) {
            PopupMenu popupMenu = popupMenus.remove(index);
            popupMenu.removeNotify();
            popupMenu.setParent(null);
        }
    }

    public void add(PopupMenu popupMenu) {
        if (popupMenu == null) {
            throw new NullPointerException("popupMenu");
        }
        synchronized (getTreeLock()) {
            MenuContainer previousParent = popupMenu.getParent();
            if (previousParent != null) {
                previousParent.remove(popupMenu);
            }
            popupMenus.add(popupMenu);
            popupMenu.setParent(this);
            if (peer != null) {
                popupMenu.addNotify();
            }
        }
    }

    @Override
    public boolean postEvent(Event event) {
        if (handleEvent(event)) {
            return true;
        }
        if (parent == null) {
            return false;
        }
        int eventX = event.x;
        int eventY = event.y;
        event.translate(x, y);
        boolean handled = parent.postEvent(event);
        if (!handled) {
            event.x = eventX;
            event.y = eventY;
        }
        return handled;
    }

    public void deliverEvent(Event event) {
        postEvent(event);
    }

    public Locale getLocale() {
        if (locale != null) {
            return locale;
        }
        if (parent != null) {
            return parent.getLocale();
        }
        return Locale.getDefault();
    }

    public void setLocale(Locale locale) {
        Locale previous = this.locale;
        this.locale = locale;
        propertyChanges.firePropertyChange("locale", previous, locale);
        invalidate();
    }

    public Point getLocation() {
        return new Point(x, y);
    }

    public Point getLocationOnScreen() {
        if (!isShowing()) {
            throw new IllegalComponentStateException("component must be showing on the screen");
        }
        int[] peerLocation = peer == null ? null : peer.getLocationOnScreen();
        if (peerLocation != null) {
            return new Point(peerLocation[0], peerLocation[1]);
        }
        int screenX = x;
        int screenY = y;
        Container ancestor = parent;
        while (ancestor != null) {
            screenX += ancestor.getX();
            screenY += ancestor.getY();
            ancestor = ancestor.getParent();
        }
        return new Point(screenX, screenY);
    }

    public Point getMousePosition() throws HeadlessException {
        return getRecordedMousePosition(false);
    }

    public Point location() {
        return getLocation();
    }

    public void setLocation(int x, int y) {
        setBounds(x, y, width, height);
    }

    public void move(int x, int y) {
        setLocation(x, y);
    }

    public void setLocation(Point location) {
        setLocation(location.x, location.y);
    }

    public Dimension getSize() {
        return new Dimension(width, height);
    }

    public Dimension size() {
        return getSize();
    }

    public void setSize(int width, int height) {
        setBounds(x, y, width, height);
    }

    public void resize(int width, int height) {
        setSize(width, height);
    }

    public void setSize(Dimension size) {
        setSize(size.width, size.height);
    }

    public void resize(Dimension size) {
        setSize(size);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public Rectangle bounds() {
        return getBounds();
    }

    public void setBounds(int x, int y, int width, int height) {
        Rectangle previous = getBounds();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        if (peer != null) {
            peer.setBounds(x, y, width, height);
        }
        propertyChanges.firePropertyChange("bounds", previous, getBounds());
        invalidate();
        if (previous.x != x || previous.y != y) {
            dispatchEvent(new ComponentEvent(this, ComponentEvent.COMPONENT_MOVED));
            dispatchAncestorBoundsEvent(HierarchyEvent.ANCESTOR_MOVED);
        }
        if (previous.width != width || previous.height != height) {
            dispatchEvent(new ComponentEvent(this, ComponentEvent.COMPONENT_RESIZED));
            dispatchAncestorBoundsEvent(HierarchyEvent.ANCESTOR_RESIZED);
        }
    }

    public void reshape(int x, int y, int width, int height) {
        setBounds(x, y, width, height);
    }

    public void setBounds(Rectangle bounds) {
        setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Rectangle getBounds(Rectangle target) {
        Rectangle result = target == null ? new Rectangle() : target;
        result.setBounds(x, y, width, height);
        return result;
    }

    public Dimension getSize(Dimension target) {
        Dimension result = target == null ? new Dimension() : target;
        result.setSize(width, height);
        return result;
    }

    public Point getLocation(Point target) {
        Point result = target == null ? new Point() : target;
        result.setLocation(x, y);
        return result;
    }

    public boolean isOpaque() {
        return false;
    }

    public boolean isDoubleBuffered() {
        return false;
    }

    public boolean isLightweight() {
        return true;
    }

    public void setPreferredSize(Dimension preferredSize) {
        Dimension previous = this.preferredSize;
        this.preferredSize = copy(preferredSize);
        propertyChanges.firePropertyChange("preferredSize", previous, preferredSize);
        invalidate();
    }

    public boolean isPreferredSizeSet() {
        return preferredSize != null;
    }

    public Dimension getPreferredSize() {
        return preferredSize == null ? minimumSize() : copy(preferredSize);
    }

    public Dimension preferredSize() {
        return getPreferredSize();
    }

    public void setMinimumSize(Dimension minimumSize) {
        Dimension previous = this.minimumSize;
        this.minimumSize = copy(minimumSize);
        propertyChanges.firePropertyChange("minimumSize", previous, minimumSize);
        invalidate();
    }

    public boolean isMinimumSizeSet() {
        return minimumSize != null;
    }

    public Dimension getMinimumSize() {
        return minimumSize == null ? new Dimension() : copy(minimumSize);
    }

    public Dimension minimumSize() {
        return getMinimumSize();
    }

    public void setMaximumSize(Dimension maximumSize) {
        Dimension previous = this.maximumSize;
        this.maximumSize = copy(maximumSize);
        propertyChanges.firePropertyChange("maximumSize", previous, maximumSize);
        invalidate();
    }

    public boolean isMaximumSizeSet() {
        return maximumSize != null;
    }

    public Dimension getMaximumSize() {
        return maximumSize == null ? new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE) : copy(maximumSize);
    }

    public float getAlignmentX() {
        return CENTER_ALIGNMENT;
    }

    public float getAlignmentY() {
        return CENTER_ALIGNMENT;
    }

    public int getBaseline(int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("width and height must be non-negative");
        }
        return -1;
    }

    public BaselineResizeBehavior getBaselineResizeBehavior() {
        return BaselineResizeBehavior.OTHER;
    }

    public void doLayout() {
        layout();
    }

    public void layout() {
    }

    public void validate() {
        valid = true;
    }

    public void invalidate() {
        valid = false;
        if (parent != null && parent.isValid()) {
            parent.invalidate();
        }
    }

    public void revalidate() {
        invalidate();
        if (parent != null) {
            parent.validate();
        }
    }

    public void setCursor(Cursor cursor) {
        Cursor previous = this.cursor;
        this.cursor = cursor;
        propertyChanges.firePropertyChange("cursor", previous, cursor);
    }

    public Cursor getCursor() {
        if (cursor != null) {
            return cursor;
        }
        return parent == null ? Cursor.getDefaultCursor() : parent.getCursor();
    }

    public boolean isCursorSet() {
        return cursor != null;
    }

    public void repaint() {
        WebAwtRuntime.schedule(() -> {
            Graphics graphics = getGraphics();
            if (graphics != null) {
                try {
                    update(graphics);
                } finally {
                    graphics.dispose();
                }
            }
        });
    }

    public void repaint(long delay) {
        repaint();
    }

    public void repaint(int x, int y, int width, int height) {
        repaint();
    }

    public void repaint(long delay, int x, int y, int width, int height) {
        repaint();
    }

    public Graphics getGraphics() {
        return null;
    }

    public void paint(Graphics graphics) {
    }

    public void update(Graphics graphics) {
        paint(graphics);
    }

    public void paintAll(Graphics graphics) {
        if (isShowing()) {
            paint(graphics);
        }
    }

    public void print(Graphics graphics) {
        paint(graphics);
    }

    public void printAll(Graphics graphics) {
        if (isShowing()) {
            print(graphics);
        }
    }

    public void setIgnoreRepaint(boolean ignoreRepaint) {
        this.ignoreRepaint = ignoreRepaint;
    }

    public boolean getIgnoreRepaint() {
        return ignoreRepaint;
    }

    public boolean contains(int x, int y) {
        return x >= 0 && y >= 0 && x < width && y < height;
    }

    public boolean inside(int x, int y) {
        return contains(x, y);
    }

    public boolean contains(Point point) {
        return contains(point.x, point.y);
    }

    public Component getComponentAt(int x, int y) {
        return contains(x, y) ? this : null;
    }

    public Component locate(int x, int y) {
        return getComponentAt(x, y);
    }

    public Component getComponentAt(Point point) {
        return getComponentAt(point.x, point.y);
    }

    public void addNotify() {
        if (peer != null) {
            return;
        }
        peer = WebAwtRuntime.createPeer(peerElementName);
        synchronizePeer();
        WebAwtRuntime.attach(peer, parent == null ? null : parent.getPeer());
        for (PopupMenu popupMenu : new ArrayList<>(popupMenus)) {
            popupMenu.addNotify();
        }
        dispatchHierarchyEventToSubtree(HierarchyEvent.HIERARCHY_CHANGED, this, parent, HierarchyEvent.DISPLAYABILITY_CHANGED | HierarchyEvent.SHOWING_CHANGED);
    }

    public void removeNotify() {
        if (peer == null) {
            return;
        }
        for (PopupMenu popupMenu : new ArrayList<>(popupMenus)) {
            popupMenu.removeNotify();
        }
        peer.detach();
        peer = null;
        clearRecordedMousePosition(this);
        dispatchHierarchyEventToSubtree(HierarchyEvent.HIERARCHY_CHANGED, this, parent, HierarchyEvent.DISPLAYABILITY_CHANGED | HierarchyEvent.SHOWING_CHANGED);
    }

    public boolean isFocusTraversable() {
        return focusable;
    }

    public boolean isFocusable() {
        return focusable;
    }

    public void setFocusable(boolean focusable) {
        this.focusable = focusable;
        if (peer != null) {
            peer.setFocusable(focusable);
        }
    }

    public Container getFocusCycleRootAncestor() {
        for (Container ancestor = parent; ancestor != null; ancestor = ancestor.getParent()) {
            if (ancestor.isFocusCycleRoot()) {
                return ancestor;
            }
        }
        return null;
    }

    public boolean isFocusCycleRoot(Container container) {
        return container != null && container == getFocusCycleRootAncestor();
    }

    public void setFocusTraversalKeys(int identifier, Set<? extends AWTKeyStroke> keystrokes) {
        validateComponentTraversalIdentifier(identifier);
        if (keystrokes == null) {
            focusTraversalKeys.set(identifier, null);
            return;
        }
        LinkedHashSet<AWTKeyStroke> copy = new LinkedHashSet<>();
        for (AWTKeyStroke keystroke : keystrokes) {
            if (keystroke == null || keystroke.getKeyEventType() == KeyEvent.KEY_TYPED) {
                throw new IllegalArgumentException("focus traversal keys must identify pressed or released keys");
            }
            copy.add(keystroke);
        }
        focusTraversalKeys.set(identifier, Collections.unmodifiableSet(copy));
    }

    public Set<AWTKeyStroke> getFocusTraversalKeys(int identifier) {
        validateComponentTraversalIdentifier(identifier);
        Set<AWTKeyStroke> strokes = focusTraversalKeys.get(identifier);
        return strokes == null ? KeyboardFocusManager.getCurrentKeyboardFocusManager().getDefaultFocusTraversalKeys(identifier) : strokes;
    }

    public boolean areFocusTraversalKeysSet(int identifier) {
        validateComponentTraversalIdentifier(identifier);
        return focusTraversalKeys.get(identifier) != null;
    }

    public void setFocusTraversalKeysEnabled(boolean focusTraversalKeysEnabled) {
        this.focusTraversalKeysEnabled = focusTraversalKeysEnabled;
    }

    public boolean getFocusTraversalKeysEnabled() {
        return focusTraversalKeysEnabled;
    }

    public boolean hasFocus() {
        return isFocusOwner();
    }

    public boolean isFocusOwner() {
        return KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner() == this;
    }

    public void enableInputMethods(boolean enable) {
        inputMethodsEnabled = enable;
    }

    public InputMethodRequests getInputMethodRequests() {
        return null;
    }

    public InputContext getInputContext() {
        if (!inputMethodsEnabled) {
            return null;
        }
        if (parent != null) {
            InputContext parentContext = parent.getInputContext();
            if (parentContext != null) {
                return parentContext;
            }
        }
        if (inputContext == null) {
            inputContext = InputContext.getInstance();
        }
        return inputContext;
    }

    protected AWTEvent coalesceEvents(AWTEvent existingEvent, AWTEvent newEvent) { return null; }

    protected boolean requestFocus(boolean temporary) { return requestFocusInWindow(); }
    protected boolean requestFocus(boolean temporary, FocusEvent.Cause cause) { requireFocusCause(cause); return requestFocus(temporary); }
    protected boolean requestFocusInWindow(boolean temporary) { return requestFocusInWindow(); }
    protected void firePropertyChange(String name, boolean before, boolean after) { propertyChanges.firePropertyChange(name, before, after); }
    protected void firePropertyChange(String name, int before, int after) { propertyChanges.firePropertyChange(name, before, after); }

    public void requestFocus() {
        if (peer != null && focusable && enabled && visible) {
            peer.requestFocus();
        }
    }

    public boolean requestFocusInWindow() {
        requestFocus();
        return peer != null && focusable && enabled && visible;
    }

    public void requestFocus(FocusEvent.Cause cause) {
        requireFocusCause(cause);
        requestFocus();
    }

    public boolean requestFocusInWindow(FocusEvent.Cause cause) {
        requireFocusCause(cause);
        return requestFocusInWindow();
    }

    public void transferFocus() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent(this);
    }

    public void nextFocus() {
        transferFocus();
    }

    public void transferFocusBackward() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().focusPreviousComponent(this);
    }

    public void transferFocusUpCycle() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().upFocusCycle(this);
    }

    public synchronized void addComponentListener(ComponentListener listener) {
        if (listener != null) {
            componentListeners.add(listener);
        }
    }

    public synchronized void removeComponentListener(ComponentListener listener) {
        componentListeners.remove(listener);
    }

    public synchronized ComponentListener[] getComponentListeners() {
        return componentListeners.toArray(new ComponentListener[0]);
    }

    public synchronized void addFocusListener(FocusListener listener) {
        if (listener != null) {
            focusListeners.add(listener);
        }
    }

    public synchronized void removeFocusListener(FocusListener listener) {
        focusListeners.remove(listener);
    }

    public synchronized FocusListener[] getFocusListeners() {
        return focusListeners.toArray(new FocusListener[0]);
    }

    public void addHierarchyListener(HierarchyListener listener) {
        if (listener != null) {
            hierarchyListeners.add(listener);
        }
    }

    public void removeHierarchyListener(HierarchyListener listener) {
        hierarchyListeners.remove(listener);
    }

    public synchronized HierarchyListener[] getHierarchyListeners() {
        return hierarchyListeners.toArray(new HierarchyListener[0]);
    }

    public void addHierarchyBoundsListener(HierarchyBoundsListener listener) {
        if (listener != null) {
            hierarchyBoundsListeners.add(listener);
        }
    }

    public void removeHierarchyBoundsListener(HierarchyBoundsListener listener) {
        hierarchyBoundsListeners.remove(listener);
    }

    public synchronized HierarchyBoundsListener[] getHierarchyBoundsListeners() {
        return hierarchyBoundsListeners.toArray(new HierarchyBoundsListener[0]);
    }

    public synchronized void addInputMethodListener(InputMethodListener listener) {
        if (listener != null) {
            inputMethodListeners.add(listener);
        }
    }

    public synchronized void removeInputMethodListener(InputMethodListener listener) {
        inputMethodListeners.remove(listener);
    }

    public synchronized InputMethodListener[] getInputMethodListeners() {
        return inputMethodListeners.toArray(new InputMethodListener[0]);
    }

    public synchronized void addKeyListener(KeyListener listener) {
        if (listener != null) {
            keyListeners.add(listener);
        }
    }

    public synchronized void removeKeyListener(KeyListener listener) {
        keyListeners.remove(listener);
    }

    public synchronized KeyListener[] getKeyListeners() {
        return keyListeners.toArray(new KeyListener[0]);
    }

    public synchronized void addMouseListener(MouseListener listener) {
        if (listener != null) {
            mouseListeners.add(listener);
        }
    }

    public synchronized void removeMouseListener(MouseListener listener) {
        mouseListeners.remove(listener);
    }

    public synchronized MouseListener[] getMouseListeners() {
        return mouseListeners.toArray(new MouseListener[0]);
    }

    public synchronized void addMouseMotionListener(MouseMotionListener listener) {
        if (listener != null) {
            mouseMotionListeners.add(listener);
            updatePeerDragSourceEnabled();
        }
    }

    public synchronized void removeMouseMotionListener(MouseMotionListener listener) {
        mouseMotionListeners.remove(listener);
        updatePeerDragSourceEnabled();
    }

    public synchronized MouseMotionListener[] getMouseMotionListeners() {
        return mouseMotionListeners.toArray(new MouseMotionListener[0]);
    }

    public synchronized void addMouseWheelListener(MouseWheelListener listener) {
        if (listener != null) {
            mouseWheelListeners.add(listener);
        }
    }

    public synchronized void removeMouseWheelListener(MouseWheelListener listener) {
        mouseWheelListeners.remove(listener);
    }

    public synchronized MouseWheelListener[] getMouseWheelListeners() {
        return mouseWheelListeners.toArray(new MouseWheelListener[0]);
    }

    @SuppressWarnings("unchecked")
    public <T extends EventListener> T[] getListeners(Class<T> listenerType) {
        if (listenerType == null) {
            throw new NullPointerException("listenerType");
        }
        if (listenerType == ComponentListener.class) {
            return (T[]) getComponentListeners();
        }
        if (listenerType == FocusListener.class) {
            return (T[]) getFocusListeners();
        }
        if (listenerType == HierarchyListener.class) {
            return (T[]) getHierarchyListeners();
        }
        if (listenerType == HierarchyBoundsListener.class) {
            return (T[]) getHierarchyBoundsListeners();
        }
        if (listenerType == InputMethodListener.class) {
            return (T[]) getInputMethodListeners();
        }
        if (listenerType == KeyListener.class) {
            return (T[]) getKeyListeners();
        }
        if (listenerType == MouseListener.class) {
            return (T[]) getMouseListeners();
        }
        if (listenerType == MouseMotionListener.class) {
            return (T[]) getMouseMotionListeners();
        }
        if (listenerType == MouseWheelListener.class) {
            return (T[]) getMouseWheelListeners();
        }
        return (T[]) new EventListener[0];
    }

    public final void dispatchEvent(AWTEvent event) {
        if (event == null) {
            throw new NullPointerException("event");
        }
        Toolkit.notifyAWTEventListeners(event);
        if (eventEnabled(event)) {
            processEvent(event);
        }
    }

    protected boolean eventEnabled(AWTEvent event) {
        switch (event.getID()) {
            case ComponentEvent.COMPONENT_MOVED:
            case ComponentEvent.COMPONENT_RESIZED:
            case ComponentEvent.COMPONENT_SHOWN:
            case ComponentEvent.COMPONENT_HIDDEN:
                return (eventMask & AWTEvent.COMPONENT_EVENT_MASK) != 0 || !componentListeners.isEmpty();
            case FocusEvent.FOCUS_GAINED:
            case FocusEvent.FOCUS_LOST:
                return (eventMask & AWTEvent.FOCUS_EVENT_MASK) != 0 || !focusListeners.isEmpty();
            case KeyEvent.KEY_PRESSED:
            case KeyEvent.KEY_RELEASED:
            case KeyEvent.KEY_TYPED:
                return (eventMask & AWTEvent.KEY_EVENT_MASK) != 0 || !keyListeners.isEmpty();
            case MouseEvent.MOUSE_PRESSED:
            case MouseEvent.MOUSE_RELEASED:
            case MouseEvent.MOUSE_CLICKED:
            case MouseEvent.MOUSE_ENTERED:
            case MouseEvent.MOUSE_EXITED:
                return (eventMask & AWTEvent.MOUSE_EVENT_MASK) != 0 || !mouseListeners.isEmpty();
            case MouseEvent.MOUSE_MOVED:
            case MouseEvent.MOUSE_DRAGGED:
                return (eventMask & AWTEvent.MOUSE_MOTION_EVENT_MASK) != 0 || !mouseMotionListeners.isEmpty();
            case MouseEvent.MOUSE_WHEEL:
                return (eventMask & AWTEvent.MOUSE_WHEEL_EVENT_MASK) != 0 || !mouseWheelListeners.isEmpty();
            case InputMethodEvent.INPUT_METHOD_TEXT_CHANGED:
            case InputMethodEvent.CARET_POSITION_CHANGED:
                return (eventMask & AWTEvent.INPUT_METHOD_EVENT_MASK) != 0 || !inputMethodListeners.isEmpty();
            case HierarchyEvent.HIERARCHY_CHANGED:
                return (eventMask & AWTEvent.HIERARCHY_EVENT_MASK) != 0 || !hierarchyListeners.isEmpty();
            case HierarchyEvent.ANCESTOR_MOVED:
            case HierarchyEvent.ANCESTOR_RESIZED:
                return (eventMask & AWTEvent.HIERARCHY_BOUNDS_EVENT_MASK) != 0 || !hierarchyBoundsListeners.isEmpty();
            case ActionEvent.ACTION_PERFORMED:
                return (eventMask & AWTEvent.ACTION_EVENT_MASK) != 0;
            case AdjustmentEvent.ADJUSTMENT_VALUE_CHANGED:
                return (eventMask & AWTEvent.ADJUSTMENT_EVENT_MASK) != 0;
            case ItemEvent.ITEM_STATE_CHANGED:
                return (eventMask & AWTEvent.ITEM_EVENT_MASK) != 0;
            case TextEvent.TEXT_VALUE_CHANGED:
                return (eventMask & AWTEvent.TEXT_EVENT_MASK) != 0;
            default:
                return event.getID() > AWTEvent.RESERVED_ID_MAX;
        }
    }

    protected final void enableEvents(long eventsToEnable) {
        eventMask |= eventsToEnable;
    }

    protected final void disableEvents(long eventsToDisable) {
        eventMask &= ~eventsToDisable;
    }

    protected void processEvent(AWTEvent event) {
        if (event instanceof HierarchyEvent) {
            processHierarchyEventById((HierarchyEvent) event);
        } else if (event instanceof InputMethodEvent) {
            processInputMethodEvent((InputMethodEvent) event);
        } else if (event instanceof FocusEvent) {
            processFocusEvent((FocusEvent) event);
        } else if (event instanceof KeyEvent) {
            processKeyEvent((KeyEvent) event);
        } else if (event instanceof MouseWheelEvent) {
            processMouseWheelEvent((MouseWheelEvent) event);
        } else if (event instanceof MouseEvent) {
            processMouseEventById((MouseEvent) event);
        } else if (event instanceof ComponentEvent) {
            processComponentEvent((ComponentEvent) event);
        }
    }

    protected void processComponentEvent(ComponentEvent event) {
        for (ComponentListener listener : getComponentListeners()) {
            if (event.getID() == ComponentEvent.COMPONENT_MOVED) {
                listener.componentMoved(event);
            } else if (event.getID() == ComponentEvent.COMPONENT_RESIZED) {
                listener.componentResized(event);
            } else if (event.getID() == ComponentEvent.COMPONENT_SHOWN) {
                listener.componentShown(event);
            } else if (event.getID() == ComponentEvent.COMPONENT_HIDDEN) {
                listener.componentHidden(event);
            }
        }
    }

    protected void processFocusEvent(FocusEvent event) {
        for (FocusListener listener : getFocusListeners()) {
            if (event.getID() == FocusEvent.FOCUS_GAINED) {
                listener.focusGained(event);
            } else if (event.getID() == FocusEvent.FOCUS_LOST) {
                listener.focusLost(event);
            }
        }
    }

    protected void processHierarchyEvent(HierarchyEvent event) {
        for (HierarchyListener listener : getHierarchyListeners()) {
            listener.hierarchyChanged(event);
        }
    }

    protected void processHierarchyBoundsEvent(HierarchyEvent event) {
        for (HierarchyBoundsListener listener : getHierarchyBoundsListeners()) {
            if (event.getID() == HierarchyEvent.ANCESTOR_MOVED) {
                listener.ancestorMoved(event);
            } else if (event.getID() == HierarchyEvent.ANCESTOR_RESIZED) {
                listener.ancestorResized(event);
            }
        }
    }

    protected void processInputMethodEvent(InputMethodEvent event) {
        for (InputMethodListener listener : getInputMethodListeners()) {
            if (event.getID() == InputMethodEvent.INPUT_METHOD_TEXT_CHANGED) {
                listener.inputMethodTextChanged(event);
            } else if (event.getID() == InputMethodEvent.CARET_POSITION_CHANGED) {
                listener.caretPositionChanged(event);
            }
        }
    }

    public boolean handleEvent(Event event) {
        switch (event.id) {
            case Event.MOUSE_ENTER:
                return mouseEnter(event, event.x, event.y);
            case Event.MOUSE_EXIT:
                return mouseExit(event, event.x, event.y);
            case Event.MOUSE_MOVE:
                return mouseMove(event, event.x, event.y);
            case Event.MOUSE_DOWN:
                return mouseDown(event, event.x, event.y);
            case Event.MOUSE_DRAG:
                return mouseDrag(event, event.x, event.y);
            case Event.MOUSE_UP:
                return mouseUp(event, event.x, event.y);
            case Event.KEY_PRESS:
            case Event.KEY_ACTION:
                return keyDown(event, event.key);
            case Event.KEY_RELEASE:
            case Event.KEY_ACTION_RELEASE:
                return keyUp(event, event.key);
            case Event.ACTION_EVENT:
                return action(event, event.arg);
            case Event.GOT_FOCUS:
                return gotFocus(event, event.arg);
            case Event.LOST_FOCUS:
                return lostFocus(event, event.arg);
            default:
                return false;
        }
    }

    public boolean mouseDown(Event event, int x, int y) {
        return false;
    }

    public boolean mouseDrag(Event event, int x, int y) {
        return false;
    }

    public boolean mouseUp(Event event, int x, int y) {
        return false;
    }

    public boolean mouseMove(Event event, int x, int y) {
        return false;
    }

    public boolean mouseEnter(Event event, int x, int y) {
        return false;
    }

    public boolean mouseExit(Event event, int x, int y) {
        return false;
    }

    public boolean keyDown(Event event, int key) {
        return false;
    }

    public boolean keyUp(Event event, int key) {
        return false;
    }

    public boolean action(Event event, Object what) {
        return false;
    }

    public boolean gotFocus(Event event, Object what) {
        return false;
    }

    public boolean lostFocus(Event event, Object what) {
        return false;
    }

    protected void processKeyEvent(KeyEvent event) {
        for (KeyListener listener : getKeyListeners()) {
            if (event.getID() == KeyEvent.KEY_TYPED) {
                listener.keyTyped(event);
            } else if (event.getID() == KeyEvent.KEY_PRESSED) {
                listener.keyPressed(event);
            } else if (event.getID() == KeyEvent.KEY_RELEASED) {
                listener.keyReleased(event);
            }
        }
    }

    protected void processMouseEvent(MouseEvent event) {
        for (MouseListener listener : getMouseListeners()) {
            if (event.getID() == MouseEvent.MOUSE_CLICKED) {
                listener.mouseClicked(event);
            } else if (event.getID() == MouseEvent.MOUSE_PRESSED) {
                listener.mousePressed(event);
            } else if (event.getID() == MouseEvent.MOUSE_RELEASED) {
                listener.mouseReleased(event);
            } else if (event.getID() == MouseEvent.MOUSE_ENTERED) {
                listener.mouseEntered(event);
            } else if (event.getID() == MouseEvent.MOUSE_EXITED) {
                listener.mouseExited(event);
            }
        }
    }

    protected void processMouseMotionEvent(MouseEvent event) {
        for (MouseMotionListener listener : getMouseMotionListeners()) {
            if (event.getID() == MouseEvent.MOUSE_DRAGGED) {
                listener.mouseDragged(event);
            } else if (event.getID() == MouseEvent.MOUSE_MOVED) {
                listener.mouseMoved(event);
            }
        }
    }

    protected void processMouseWheelEvent(MouseWheelEvent event) {
        for (MouseWheelListener listener : getMouseWheelListeners()) {
            listener.mouseWheelMoved(event);
        }
    }

    private void processMouseEventById(MouseEvent event) {
        if (event.getID() == MouseEvent.MOUSE_MOVED || event.getID() == MouseEvent.MOUSE_DRAGGED) {
            processMouseMotionEvent(event);
        } else {
            processMouseEvent(event);
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChanges.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChanges.removePropertyChangeListener(listener);
    }

    public PropertyChangeListener[] getPropertyChangeListeners() {
        return propertyChanges.getPropertyChangeListeners();
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertyChanges.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertyChanges.removePropertyChangeListener(propertyName, listener);
    }

    public PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
        return propertyChanges.getPropertyChangeListeners(propertyName);
    }

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        propertyChanges.firePropertyChange(propertyName, oldValue, newValue);
    }

    public void firePropertyChange(String propertyName, byte oldValue, byte newValue) {
        if (oldValue != newValue) {
            propertyChanges.firePropertyChange(propertyName, Byte.valueOf(oldValue), Byte.valueOf(newValue));
        }
    }

    public void firePropertyChange(String propertyName, char oldValue, char newValue) {
        if (oldValue != newValue) {
            propertyChanges.firePropertyChange(propertyName, Character.valueOf(oldValue), Character.valueOf(newValue));
        }
    }

    public void firePropertyChange(String propertyName, short oldValue, short newValue) {
        if (oldValue != newValue) {
            propertyChanges.firePropertyChange(propertyName, Short.valueOf(oldValue), Short.valueOf(newValue));
        }
    }

    public void firePropertyChange(String propertyName, long oldValue, long newValue) {
        if (oldValue != newValue) {
            propertyChanges.firePropertyChange(propertyName, Long.valueOf(oldValue), Long.valueOf(newValue));
        }
    }

    public void firePropertyChange(String propertyName, float oldValue, float newValue) {
        if (oldValue != newValue) {
            propertyChanges.firePropertyChange(propertyName, Float.valueOf(oldValue), Float.valueOf(newValue));
        }
    }

    public void firePropertyChange(String propertyName, double oldValue, double newValue) {
        if (oldValue != newValue) {
            propertyChanges.firePropertyChange(propertyName, Double.valueOf(oldValue), Double.valueOf(newValue));
        }
    }

    public void setComponentOrientation(ComponentOrientation orientation) {
        if (orientation == null) {
            throw new NullPointerException("orientation");
        }
        ComponentOrientation previous = componentOrientation;
        componentOrientation = orientation;
        propertyChanges.firePropertyChange("componentOrientation", previous, orientation);
        invalidate();
    }

    public ComponentOrientation getComponentOrientation() {
        return componentOrientation;
    }

    public void applyComponentOrientation(ComponentOrientation orientation) {
        setComponentOrientation(orientation);
    }

    public void setMixingCutoutShape(Shape shape) {
        // Every WebAWT component is lightweight, so there is no heavyweight mixing boundary to cut out.
    }

    protected String paramString() {
        String componentName = name == null ? "" : name;
        String invalid = valid ? "" : ",invalid";
        String hidden = visible ? "" : ",hidden";
        String disabled = enabled ? "" : ",disabled";
        return componentName + ',' + x + ',' + y + ',' + width + 'x' + height + invalid + hidden + disabled;
    }

    @Override
    public String toString() {
        return getClass().getName() + '[' + paramString() + ']';
    }

    public void list() {
        list(System.out, 0);
    }

    public void list(PrintStream output) {
        list(output, 0);
    }

    public void list(PrintStream output, int indent) {
        for (int index = 0; index < indent; index++) {
            output.print(' ');
        }
        output.println(this);
    }

    public void list(PrintWriter output) {
        list(output, 0);
    }

    public void list(PrintWriter output, int indent) {
        for (int index = 0; index < indent; index++) {
            output.print(' ');
        }
        output.println(this);
    }

    protected AccessibleContext accessibleContext;

    public AccessibleContext getAccessibleContext() {
        return null;
    }

    protected final void updatePeerText(String text) {
        if (peer != null) {
            peer.setText(text);
        }
    }

    protected final void updatePeerActionHandler(Runnable handler) {
        if (peer != null) {
            peer.setActionHandler(handler);
        }
    }

    protected final String getPeerValue() {
        return peer == null ? null : peer.getValue();
    }

    protected final void updatePeerValue(String value) {
        if (peer != null) {
            peer.setValue(value);
        }
    }

    protected final void updatePeerEditable(boolean editable) {
        if (peer != null) {
            peer.setEditable(editable);
        }
    }

    protected final void updatePeerPassword(boolean password) {
        if (peer != null) {
            peer.setPassword(password);
        }
    }

    protected final void updatePeerInputType(String type) {
        if (peer != null) {
            peer.setInputType(type);
        }
    }

    protected final void updatePeerRange(int minimum, int maximum, int step, boolean vertical) {
        if (peer != null) {
            peer.setRange(minimum, maximum, step, vertical);
        }
    }

    protected final void updatePeerScrollPolicy(int policy) {
        if (peer != null) {
            peer.setScrollPolicy(policy);
        }
    }

    protected final void updatePeerScrollPosition(int x, int y) {
        if (peer != null) {
            peer.setScrollPosition(x, y);
        }
    }

    protected final Point getPeerScrollPosition() {
        return peer == null ? new Point() : new Point(peer.getScrollX(), peer.getScrollY());
    }

    protected final void updatePeerScrollHandler(Runnable handler) {
        if (peer != null) {
            peer.setScrollHandler(handler);
        }
    }

    protected final void updatePeerTextChangeHandler(Runnable handler) {
        if (peer != null) {
            peer.setTextChangeHandler(handler);
        }
    }

    protected final void updatePeerCompositionEventHandler(PeerCompositionEventHandler handler) {
        if (peer != null) {
            peer.setCompositionEventHandler(handler);
        }
    }

    protected final boolean isPeerSelected() {
        return peer != null && peer.isSelected();
    }

    protected final void updatePeerSelected(boolean selected) {
        if (peer != null) {
            peer.setSelected(selected);
        }
    }

    protected final void updatePeerSelectionItems(String[] items, int[] selectedIndexes, boolean multiple, int visibleRows) {
        if (peer != null) {
            peer.setSelectionItems(items, selectedIndexes, multiple, visibleRows);
        }
    }

    protected final int[] getPeerSelectedIndexes() {
        return peer == null ? new int[0] : peer.getSelectedIndexes();
    }

    protected final void updatePeerItemChangeHandler(Runnable handler) {
        if (peer != null) {
            peer.setItemChangeHandler(handler);
        }
    }

    final DomPeer getPeer() {
        return peer;
    }

    final void setPeerBounds(int x, int y, int width, int height) {
        if (peer != null) {
            peer.setBounds(x, y, width, height);
        }
    }

    final void setParent(Container parent) {
        this.parent = parent;
    }

    void setGraphicsConfiguration(GraphicsConfiguration configuration) {
        graphicsConfiguration = configuration;
    }

    final Point getRecordedMousePosition(boolean includeDescendants) {
        if (!isShowing() || mouseEventTarget == null || (!includeDescendants && mouseEventTarget != this)) {
            return null;
        }
        return new Point(lastMouseX, lastMouseY);
    }

    final void dispatchHierarchyEventToSubtree(int id, Component changed, Container changedParent, long changeFlags) {
        dispatchEvent(new HierarchyEvent(this, id, changed, changedParent, changeFlags));
        if (this instanceof Container) {
            for (Component child : ((Container) this).getComponents()) {
                child.dispatchHierarchyEventToSubtree(id, changed, changedParent, changeFlags);
            }
        }
    }

    private void synchronizePeer() {
        peer.setBounds(x, y, width, height);
        peer.setVisible(visible);
        peer.setEnabled(enabled);
        peer.setFocusable(focusable);
        peer.setForeground(toCssColor(foreground));
        peer.setBackground(toCssColor(background));
        peer.setFont(toCssFont(getFont()));
        peer.setActionHandler(null);
        peer.setTextChangeHandler(null);
        peer.setCompositionEventHandler(null);
        peer.setDragEventHandler(this::dispatchPeerDragEvent);
        updatePeerDragSourceEnabled();
        peer.setItemChangeHandler(null);
        peer.setInputEventHandler(this::dispatchPeerInputEvent);
    }

    private static Dimension copy(Dimension value) {
        return value == null ? null : new Dimension(value);
    }

    private void dispatchPeerDragEvent(PeerDragEvent event) {
        if (event.getType() == PeerDragEvent.DRAG_START) {
            for (MouseMotionListener listener : mouseMotionListeners) {
                if (listener instanceof PeerDragGestureStarter) {
                    ((PeerDragGestureStarter) listener).startPeerDrag(event.getX(), event.getY(), event.getModifiers());
                }
            }
            WebAwtRuntime.populateDragStart(event);
        } else if (event.getType() == PeerDragEvent.DRAG_END) {
            WebAwtRuntime.notifyDragSource(PeerDragEvent.DRAG_END, event.getDropAction(), event.getSourceActions(), event.getModifiers(), event.getX(), event.getY(), event.getDropAction() != java.awt.dnd.DnDConstants.ACTION_NONE);
        } else if (dropTarget != null) {
            dropTarget.handlePeerDragEvent(event);
        }
    }

    private void updatePeerDragSourceEnabled() {
        if (peer == null) {
            return;
        }
        boolean enabled = false;
        for (MouseMotionListener listener : mouseMotionListeners) {
            enabled |= listener instanceof DragGestureRecognizer;
        }
        peer.setDragSourceEnabled(enabled);
    }

    private static java.util.List<Set<AWTKeyStroke>> createFocusTraversalKeyList() {
        java.util.List<Set<AWTKeyStroke>> keys = new ArrayList<>();
        keys.add(null);
        keys.add(null);
        keys.add(null);
        return keys;
    }

    private static void validateComponentTraversalIdentifier(int identifier) {
        if (identifier < KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS || identifier > KeyboardFocusManager.UP_CYCLE_TRAVERSAL_KEYS) {
            throw new IllegalArgumentException("invalid focus traversal key identifier");
        }
    }

    private static void requireFocusCause(FocusEvent.Cause cause) {
        if (cause == null) {
            throw new IllegalArgumentException("cause cannot be null");
        }
    }

    private void dispatchAncestorBoundsEvent(int id) {
        if (!(this instanceof Container)) {
            return;
        }
        for (Component child : ((Container) this).getComponents()) {
            child.dispatchHierarchyEventToSubtree(id, this, parent, 0L);
        }
    }

    private void processHierarchyEventById(HierarchyEvent event) {
        if (event.getID() == HierarchyEvent.HIERARCHY_CHANGED) {
            processHierarchyEvent(event);
        } else {
            processHierarchyBoundsEvent(event);
        }
    }

    private boolean dispatchPeerInputEvent(PeerInputEvent peerEvent) {
        if (peerEvent.type == PeerInputEvent.FOCUS_GAINED || peerEvent.type == PeerInputEvent.FOCUS_LOST) {
            int id = peerEvent.type == PeerInputEvent.FOCUS_GAINED ? FocusEvent.FOCUS_GAINED : FocusEvent.FOCUS_LOST;
            KeyboardFocusManager.getCurrentKeyboardFocusManager().dispatchEvent(new FocusEvent(this, id));
            return false;
        }
        if (peerEvent.type == PeerInputEvent.KEY_TYPED || peerEvent.type == PeerInputEvent.KEY_PRESSED || peerEvent.type == PeerInputEvent.KEY_RELEASED) {
            KeyEvent event = new KeyEvent(this, keyEventId(peerEvent.type), peerEvent.when, peerEvent.modifiers, peerEvent.keyCode, peerEvent.keyChar, peerEvent.keyLocation);
            return KeyboardFocusManager.getCurrentKeyboardFocusManager().dispatchKeyEvent(event);
        }
        updateRecordedMousePosition(peerEvent);
        MouseEvent event = createMouseEvent(peerEvent);
        dispatchEvent(event);
        return event.isConsumed();
    }

    private void updateRecordedMousePosition(PeerInputEvent event) {
        if (event.type == PeerInputEvent.MOUSE_EXITED) {
            clearRecordedMousePosition(this);
            return;
        }
        Component target = this;
        Component current = this;
        int currentX = event.x;
        int currentY = event.y;
        while (current != null) {
            current.mouseEventTarget = target;
            current.lastMouseX = currentX;
            current.lastMouseY = currentY;
            currentX += current.x;
            currentY += current.y;
            current = current.parent;
        }
    }

    private void clearRecordedMousePosition(Component target) {
        Component current = this;
        while (current != null) {
            if (current.mouseEventTarget == target) {
                current.mouseEventTarget = null;
            }
            current = current.parent;
        }
    }

    private MouseEvent createMouseEvent(PeerInputEvent peerEvent) {
        if (peerEvent.type == PeerInputEvent.MOUSE_WHEEL) {
            int wheelRotation = (int) Math.round(peerEvent.wheelRotation);
            return new MouseWheelEvent(this, MouseEvent.MOUSE_WHEEL, peerEvent.when, peerEvent.modifiers, peerEvent.x, peerEvent.y, peerEvent.screenX, peerEvent.screenY, peerEvent.clickCount, false, MouseWheelEvent.WHEEL_UNIT_SCROLL, 3, wheelRotation, peerEvent.wheelRotation);
        }
        int id = mouseEventId(peerEvent.type);
        boolean popupTrigger = peerEvent.button == MouseEvent.BUTTON3 && (id == MouseEvent.MOUSE_PRESSED || id == MouseEvent.MOUSE_RELEASED);
        return new MouseEvent(this, id, peerEvent.when, peerEvent.modifiers, peerEvent.x, peerEvent.y, peerEvent.screenX, peerEvent.screenY, peerEvent.clickCount, popupTrigger, peerEvent.button);
    }

    private static int keyEventId(int type) {
        if (type == PeerInputEvent.KEY_TYPED) {
            return KeyEvent.KEY_TYPED;
        }
        return type == PeerInputEvent.KEY_PRESSED ? KeyEvent.KEY_PRESSED : KeyEvent.KEY_RELEASED;
    }

    private static int mouseEventId(int type) {
        if (type == PeerInputEvent.MOUSE_CLICKED) {
            return MouseEvent.MOUSE_CLICKED;
        }
        if (type == PeerInputEvent.MOUSE_PRESSED) {
            return MouseEvent.MOUSE_PRESSED;
        }
        if (type == PeerInputEvent.MOUSE_RELEASED) {
            return MouseEvent.MOUSE_RELEASED;
        }
        if (type == PeerInputEvent.MOUSE_ENTERED) {
            return MouseEvent.MOUSE_ENTERED;
        }
        if (type == PeerInputEvent.MOUSE_EXITED) {
            return MouseEvent.MOUSE_EXITED;
        }
        return type == PeerInputEvent.MOUSE_DRAGGED ? MouseEvent.MOUSE_DRAGGED : MouseEvent.MOUSE_MOVED;
    }

    private static String toCssColor(Color color) {
        if (color == null) {
            return null;
        }
        return "rgba(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + "," + color.getAlpha() / 255.0 + ")";
    }

    static String toCssFont(Font font) {
        font = effectiveFont(font);
        String style = font.isItalic() ? "italic " : "";
        String weight = font.isBold() ? "bold " : "";
        String family = font.getFamily();
        if (Font.DIALOG.equalsIgnoreCase(family) || Font.SANS_SERIF.equalsIgnoreCase(family)) {
            family = "sans-serif";
        } else if (Font.DIALOG_INPUT.equalsIgnoreCase(family) || Font.MONOSPACED.equalsIgnoreCase(family)) {
            family = "monospace";
        } else if (Font.SERIF.equalsIgnoreCase(family)) {
            family = "serif";
        } else {
            family = "\"" + family.replace("\\", "\\\\").replace("\"", "\\\"") + "\", sans-serif";
        }
        return style + weight + font.getSize2D() + "px " + family;
    }

    protected abstract class AccessibleAWTComponent extends AccessibleContext implements java.io.Serializable, javax.accessibility.AccessibleComponent {
        protected java.awt.event.ComponentListener accessibleAWTComponentHandler;
        protected java.awt.event.FocusListener accessibleAWTFocusHandler;
        private final java.util.List<java.beans.PropertyChangeListener> accessibilityListeners = new java.util.ArrayList<>();

        protected AccessibleAWTComponent() {
        }

        public void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
            if (listener == null) { return; }
            if (accessibilityListeners.isEmpty()) {
                accessibleAWTComponentHandler = new AccessibleAWTComponentHandler();
                accessibleAWTFocusHandler = new AccessibleAWTFocusHandler();
                Component.this.addComponentListener(accessibleAWTComponentHandler);
                Component.this.addFocusListener(accessibleAWTFocusHandler);
            }
            accessibilityListeners.add(listener);
            super.addPropertyChangeListener(listener);
        }

        public void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
            accessibilityListeners.remove(listener);
            super.removePropertyChangeListener(listener);
            if (accessibilityListeners.isEmpty()) {
                Component.this.removeComponentListener(accessibleAWTComponentHandler);
                Component.this.removeFocusListener(accessibleAWTFocusHandler);
                accessibleAWTComponentHandler = null;
                accessibleAWTFocusHandler = null;
            }
        }

        public String getAccessibleName() { return accessibleName == null ? Component.this.getName() : accessibleName; }
        public String getAccessibleDescription() { return accessibleDescription; }
        public javax.accessibility.AccessibleRole getAccessibleRole() { return javax.accessibility.AccessibleRole.AWT_COMPONENT; }
        public javax.accessibility.AccessibleStateSet getAccessibleStateSet() {
            javax.accessibility.AccessibleStateSet states = new javax.accessibility.AccessibleStateSet();
            if (Component.this.isEnabled()) { states.add(javax.accessibility.AccessibleState.ENABLED); }
            if (Component.this.isVisible()) { states.add(javax.accessibility.AccessibleState.VISIBLE); }
            if (Component.this.isShowing()) { states.add(javax.accessibility.AccessibleState.SHOWING); }
            if (Component.this.isFocusable()) { states.add(javax.accessibility.AccessibleState.FOCUSABLE); }
            if (Component.this.isFocusOwner()) { states.add(javax.accessibility.AccessibleState.FOCUSED); }
            return states;
        }
        public javax.accessibility.Accessible getAccessibleParent() { return accessibleParent != null ? accessibleParent : Component.this.getParent() instanceof javax.accessibility.Accessible ? (javax.accessibility.Accessible) Component.this.getParent() : null; }
        public int getAccessibleIndexInParent() { return Component.this.getParent() == null ? -1 : Component.this.getParent().getComponentZOrder(Component.this); }
        public int getAccessibleChildrenCount() { return 0; }
        public javax.accessibility.Accessible getAccessibleChild(int index) { return null; }
        public java.util.Locale getLocale() { return Component.this.getLocale(); }
        public javax.accessibility.AccessibleComponent getAccessibleComponent() { return this; }
        public java.awt.Color getBackground() { return Component.this.getBackground(); }
        public void setBackground(java.awt.Color color) { Component.this.setBackground(color); }
        public java.awt.Color getForeground() { return Component.this.getForeground(); }
        public void setForeground(java.awt.Color color) { Component.this.setForeground(color); }
        public java.awt.Cursor getCursor() { return Component.this.getCursor(); }
        public void setCursor(java.awt.Cursor cursor) { Component.this.setCursor(cursor); }
        public java.awt.Font getFont() { return Component.this.getFont(); }
        public void setFont(java.awt.Font font) { Component.this.setFont(font); }
        public java.awt.FontMetrics getFontMetrics(java.awt.Font font) { return Component.this.getFontMetrics(font); }
        public boolean isEnabled() { return Component.this.isEnabled(); }
        public void setEnabled(boolean enabled) { Component.this.setEnabled(enabled); }
        public boolean isVisible() { return Component.this.isVisible(); }
        public void setVisible(boolean visible) { Component.this.setVisible(visible); }
        public boolean isShowing() { return Component.this.isShowing(); }
        public boolean contains(java.awt.Point point) { return Component.this.contains(point); }
        public java.awt.Point getLocationOnScreen() { return Component.this.getLocationOnScreen(); }
        public java.awt.Point getLocation() { return Component.this.getLocation(); }
        public void setLocation(java.awt.Point point) { Component.this.setLocation(point); }
        public java.awt.Rectangle getBounds() { return Component.this.getBounds(); }
        public void setBounds(java.awt.Rectangle bounds) { Component.this.setBounds(bounds); }
        public java.awt.Dimension getSize() { return Component.this.getSize(); }
        public void setSize(java.awt.Dimension size) { Component.this.setSize(size); }
        public javax.accessibility.Accessible getAccessibleAt(java.awt.Point point) { return null; }
        public boolean isFocusTraversable() { return Component.this.isFocusTraversable(); }
        public void requestFocus() { Component.this.requestFocus(); }
        public void addFocusListener(java.awt.event.FocusListener listener) { Component.this.addFocusListener(listener); }
        public void removeFocusListener(java.awt.event.FocusListener listener) { Component.this.removeFocusListener(listener); }

        protected class AccessibleAWTComponentHandler implements java.awt.event.ComponentListener, java.io.Serializable {
            protected AccessibleAWTComponentHandler() {}
            public void componentHidden(java.awt.event.ComponentEvent event) { firePropertyChange(ACCESSIBLE_STATE_PROPERTY, javax.accessibility.AccessibleState.VISIBLE, null); }
            public void componentShown(java.awt.event.ComponentEvent event) { firePropertyChange(ACCESSIBLE_STATE_PROPERTY, null, javax.accessibility.AccessibleState.VISIBLE); }
            public void componentMoved(java.awt.event.ComponentEvent event) {}
            public void componentResized(java.awt.event.ComponentEvent event) {}
        }
        protected class AccessibleAWTFocusHandler implements java.awt.event.FocusListener, java.io.Serializable {
            protected AccessibleAWTFocusHandler() {}
            public void focusGained(java.awt.event.FocusEvent event) { firePropertyChange(ACCESSIBLE_STATE_PROPERTY, null, javax.accessibility.AccessibleState.FOCUSED); }
            public void focusLost(java.awt.event.FocusEvent event) { firePropertyChange(ACCESSIBLE_STATE_PROPERTY, javax.accessibility.AccessibleState.FOCUSED, null); }
        }
    }
}
