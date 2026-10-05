package java.awt;

import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.awt.image.BufferStrategy;
import java.awt.im.InputContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;

public class Window extends Container implements Accessible {
    public enum Type {
        NORMAL,
        UTILITY,
        POPUP
    }

    private static final List<Window> WINDOWS = new ArrayList<>();
    private final List<Window> ownedWindows = new ArrayList<>();
    private final List<WindowFocusListener> windowFocusListeners = new ArrayList<>();
    private final List<WindowListener> windowListeners = new ArrayList<>();
    private final List<WindowStateListener> windowStateListeners = new ArrayList<>();
    private final List<Image> iconImages = new ArrayList<>();
    private final Window owner;
    private Component mostRecentFocusOwner;
    private Shape shape;
    private Type type = Type.NORMAL;
    private Dialog.ModalExclusionType modalExclusionType = Dialog.ModalExclusionType.NO_EXCLUDE;
    private boolean alwaysOnTop;
    private boolean autoRequestFocus = true;
    private boolean focusableWindowState = true;
    private boolean locationByPlatform;
    private float opacity = 1.0f;
    private boolean opened;
    private boolean disposed;
    private WebBufferStrategy bufferStrategy;

    public Window(Frame owner) {
        this("div", owner, null);
    }

    public Window(Window owner) {
        this("div", owner, null);
    }

    public Window(Window owner, GraphicsConfiguration configuration) {
        this("div", owner, configuration);
    }

    Window(String elementName, Window owner) {
        this(elementName, owner, null);
    }

    Window(String elementName, Window owner, GraphicsConfiguration configuration) {
        super(elementName);
        this.owner = owner;
        GraphicsConfiguration selectedConfiguration = configuration == null && owner != null ? owner.getGraphicsConfiguration() : configuration;
        if (selectedConfiguration != null && (selectedConfiguration.getDevice() == null || selectedConfiguration.getDevice().getType() != GraphicsDevice.TYPE_RASTER_SCREEN)) {
            throw new IllegalArgumentException("graphics configuration must belong to a raster screen device");
        }
        setGraphicsConfiguration(selectedConfiguration);
        if (owner != null) {
            owner.ownedWindows.add(this);
        }
        WINDOWS.add(this);
        setLayout(new BorderLayout());
    }

    public void pack() {
        setSize(getPreferredSize());
        validate();
    }

    public void applyResourceBundle(String resourceBundleName) {
        if (resourceBundleName == null) {
            throw new NullPointerException("resourceBundleName");
        }
        applyComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
    }

    @Override
    public InputContext getInputContext() {
        return super.getInputContext();
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible && !isDisplayable()) {
            addNotify();
        }
        super.setVisible(visible);
        if (visible && !opened) {
            opened = true;
            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_OPENED));
        }
    }

    @Override
    public void show() {
        setVisible(true);
    }

    @Override
    public void hide() {
        setVisible(false);
    }

    public void dispose() {
        if (disposed) {
            return;
        }
        for (Window child : new ArrayList<Window>(ownedWindows)) {
            child.dispose();
        }
        if (bufferStrategy != null) {
            bufferStrategy.dispose();
        }
        removeNotify();
        if (owner != null) {
            owner.ownedWindows.remove(this);
        }
        WINDOWS.remove(this);
        disposed = true;
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSED));
    }

    public void createBufferStrategy(int bufferCount) {
        createBufferStrategyInternal(bufferCount, new BufferCapabilities(new ImageCapabilities(false), new ImageCapabilities(false), null));
    }

    public void createBufferStrategy(int bufferCount, BufferCapabilities capabilities) throws AWTException {
        if (capabilities == null) {
            throw new IllegalArgumentException("Buffer capabilities cannot be null");
        }
        if (capabilities.isPageFlipping() || capabilities.getFrontBufferCapabilities().isAccelerated() || capabilities.getBackBufferCapabilities().isAccelerated()) {
            throw new AWTException("The browser does not support the requested buffer capabilities");
        }
        createBufferStrategyInternal(bufferCount, capabilities);
    }

    public BufferStrategy getBufferStrategy() {
        return bufferStrategy;
    }

    void clearBufferStrategy(WebBufferStrategy strategy) {
        if (bufferStrategy == strategy) {
            bufferStrategy = null;
        }
    }

    private void createBufferStrategyInternal(int bufferCount, BufferCapabilities capabilities) {
        if (bufferCount < 1) {
            throw new IllegalArgumentException("Buffer count must be at least one");
        }
        if (!isDisplayable()) {
            throw new IllegalStateException("Component must be displayable");
        }
        if (bufferStrategy != null) {
            bufferStrategy.dispose();
        }
        bufferStrategy = new WebBufferStrategy(this, capabilities);
    }

    public void toFront() {
    }

    public void toBack() {
    }

    public final String getWarningString() {
        return null;
    }

    public Window getOwner() {
        return owner;
    }

    public Window[] getOwnedWindows() {
        return ownedWindows.toArray(new Window[0]);
    }

    public List<Image> getIconImages() {
        return new ArrayList<>(iconImages);
    }

    public synchronized void setIconImages(List<? extends Image> images) {
        iconImages.clear();
        if (images != null) {
            iconImages.addAll(images);
        }
        firePropertyChange("iconImage", null, null);
    }

    public void setIconImage(Image image) {
        List<Image> images = new ArrayList<>();
        if (image != null) {
            images.add(image);
        }
        setIconImages(images);
    }

    public static Window[] getWindows() {
        return WINDOWS.toArray(new Window[0]);
    }

    public static Window[] getOwnerlessWindows() {
        List<Window> ownerless = new ArrayList<>();
        for (Window window : WINDOWS) {
            if (window.owner == null) {
                ownerless.add(window);
            }
        }
        return ownerless.toArray(new Window[0]);
    }

    public final void setAlwaysOnTop(boolean alwaysOnTop) throws SecurityException {
        this.alwaysOnTop = alwaysOnTop;
    }

    public boolean isAlwaysOnTopSupported() {
        return true;
    }

    public final boolean isAlwaysOnTop() {
        return alwaysOnTop;
    }

    public final boolean isFocusCycleRoot() {
        return true;
    }

    @Override
    public final void setFocusCycleRoot(boolean focusCycleRoot) {
    }

    public synchronized void addWindowListener(WindowListener listener) {
        if (listener != null) {
            windowListeners.add(listener);
        }
    }

    public synchronized void addWindowStateListener(WindowStateListener listener) {
        if (listener != null) {
            windowStateListeners.add(listener);
        }
    }

    public synchronized void addWindowFocusListener(WindowFocusListener listener) {
        if (listener != null) {
            windowFocusListeners.add(listener);
        }
    }

    public synchronized void removeWindowListener(WindowListener listener) {
        windowListeners.remove(listener);
    }

    public synchronized void removeWindowStateListener(WindowStateListener listener) {
        windowStateListeners.remove(listener);
    }

    public synchronized void removeWindowFocusListener(WindowFocusListener listener) {
        windowFocusListeners.remove(listener);
    }

    public synchronized WindowListener[] getWindowListeners() {
        return windowListeners.toArray(new WindowListener[0]);
    }

    public synchronized WindowFocusListener[] getWindowFocusListeners() {
        return windowFocusListeners.toArray(new WindowFocusListener[0]);
    }

    public synchronized WindowStateListener[] getWindowStateListeners() {
        return windowStateListeners.toArray(new WindowStateListener[0]);
    }

    public Component getFocusOwner() {
        Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        return isFocused() && (focusOwner == this || isAncestorOf(focusOwner)) ? focusOwner : null;
    }

    public Component getMostRecentFocusOwner() {
        Component focusOwner = getFocusOwner();
        return focusOwner == null ? mostRecentFocusOwner : focusOwner;
    }

    public boolean isFocused() {
        return KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow() == this;
    }

    public boolean isActive() {
        return KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow() == this;
    }

    public void setModalExclusionType(Dialog.ModalExclusionType exclusionType) {
        Dialog.ModalExclusionType supportedType = exclusionType == null ? Dialog.ModalExclusionType.NO_EXCLUDE : exclusionType;
        if (!Toolkit.getDefaultToolkit().isModalExclusionTypeSupported(supportedType)) {
            supportedType = Dialog.ModalExclusionType.NO_EXCLUDE;
        }
        modalExclusionType = supportedType;
    }

    public Dialog.ModalExclusionType getModalExclusionType() {
        return modalExclusionType;
    }

    public final Container getFocusCycleRootAncestor() {
        return null;
    }

    public final boolean isFocusableWindow() {
        return focusableWindowState;
    }

    public boolean getFocusableWindowState() {
        return focusableWindowState;
    }

    public void setFocusableWindowState(boolean focusableWindowState) {
        this.focusableWindowState = focusableWindowState;
    }

    public void setAutoRequestFocus(boolean autoRequestFocus) {
        this.autoRequestFocus = autoRequestFocus;
    }

    public boolean isAutoRequestFocus() {
        return autoRequestFocus;
    }

    @Override
    protected boolean eventEnabled(AWTEvent event) {
        switch (event.getID()) {
            case WindowEvent.WINDOW_OPENED:
            case WindowEvent.WINDOW_CLOSING:
            case WindowEvent.WINDOW_CLOSED:
            case WindowEvent.WINDOW_ICONIFIED:
            case WindowEvent.WINDOW_DEICONIFIED:
            case WindowEvent.WINDOW_ACTIVATED:
            case WindowEvent.WINDOW_DEACTIVATED:
                return (eventMask & AWTEvent.WINDOW_EVENT_MASK) != 0 || !windowListeners.isEmpty();
            case WindowEvent.WINDOW_GAINED_FOCUS:
            case WindowEvent.WINDOW_LOST_FOCUS:
                return (eventMask & AWTEvent.WINDOW_FOCUS_EVENT_MASK) != 0 || !windowFocusListeners.isEmpty();
            case WindowEvent.WINDOW_STATE_CHANGED:
                return (eventMask & AWTEvent.WINDOW_STATE_EVENT_MASK) != 0 || !windowStateListeners.isEmpty();
            default:
                return super.eventEnabled(event);
        }
    }

    @Override
    protected void processEvent(AWTEvent event) {
        if (event instanceof WindowEvent) {
            WindowEvent windowEvent = (WindowEvent) event;
            if (windowEvent.getID() == WindowEvent.WINDOW_GAINED_FOCUS || windowEvent.getID() == WindowEvent.WINDOW_LOST_FOCUS) {
                processWindowFocusEvent(windowEvent);
            } else if (windowEvent.getID() == WindowEvent.WINDOW_STATE_CHANGED) {
                processWindowStateEvent(windowEvent);
            } else {
                processWindowEvent(windowEvent);
            }
        } else {
            super.processEvent(event);
        }
    }

    protected void processWindowEvent(WindowEvent event) {
        for (WindowListener listener : getWindowListeners()) {
            dispatchWindowEvent(listener, event);
        }
    }

    protected void processWindowFocusEvent(WindowEvent event) {
        for (WindowFocusListener listener : getWindowFocusListeners()) {
            if (event.getID() == WindowEvent.WINDOW_GAINED_FOCUS) {
                listener.windowGainedFocus(event);
            } else if (event.getID() == WindowEvent.WINDOW_LOST_FOCUS) {
                listener.windowLostFocus(event);
            }
        }
    }

    protected void processWindowStateEvent(WindowEvent event) {
        for (WindowStateListener listener : getWindowStateListeners()) {
            listener.windowStateChanged(event);
        }
    }

    final void setMostRecentFocusOwner(Component component) {
        mostRecentFocusOwner = component;
    }

    @Override
    public boolean isValidateRoot() {
        return true;
    }

    @Override
    public boolean isShowing() {
        return isVisible() && isDisplayable();
    }

    public void setType(Type type) {
        if (type == null) {
            throw new IllegalArgumentException("type must not be null");
        }
        if (isDisplayable()) {
            throw new IllegalComponentStateException("window is displayable");
        }
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public void setLocationRelativeTo(Component component) {
        if (component == null) {
            setLocation(0, 0);
            return;
        }
        Point location = component.isShowing() ? component.getLocationOnScreen() : component.getLocation();
        setLocation(location.x + (component.getWidth() - getWidth()) / 2, location.y + (component.getHeight() - getHeight()) / 2);
    }

    public void setLocationByPlatform(boolean locationByPlatform) {
        if (locationByPlatform && isShowing()) {
            throw new IllegalComponentStateException("window is showing");
        }
        this.locationByPlatform = locationByPlatform;
    }

    public boolean isLocationByPlatform() {
        return locationByPlatform;
    }

    public float getOpacity() {
        return opacity;
    }

    public void setOpacity(float opacity) {
        if (opacity < 0.0f || opacity > 1.0f) {
            throw new IllegalArgumentException("opacity must be in the range 0 to 1");
        }
        this.opacity = opacity;
    }

    public Shape getShape() {
        return shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    @Override
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleAWTWindow();
        }
        return accessibleContext;
    }

    protected class AccessibleAWTWindow extends AccessibleAWTContainer {
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.WINDOW;
        }

        @Override
        public AccessibleStateSet getAccessibleStateSet() {
            AccessibleStateSet states = new AccessibleStateSet();
            if (isEnabled()) {
                states.add(AccessibleState.ENABLED);
            }
            if (isVisible()) {
                states.add(AccessibleState.VISIBLE);
            }
            return states;
        }

        @Override
        public int getAccessibleIndexInParent() {
            return -1;
        }

        @Override
        public int getAccessibleChildrenCount() {
            return getComponentCount();
        }

        @Override
        public Accessible getAccessibleChild(int index) {
            Component child = getComponent(index);
            return child instanceof Accessible ? (Accessible) child : null;
        }

        @Override
        public Locale getLocale() {
            return Window.this.getLocale();
        }
    }

    private static void dispatchWindowEvent(WindowListener listener, WindowEvent event) {
        switch (event.getID()) {
            case WindowEvent.WINDOW_OPENED:
                listener.windowOpened(event);
                break;
            case WindowEvent.WINDOW_CLOSING:
                listener.windowClosing(event);
                break;
            case WindowEvent.WINDOW_CLOSED:
                listener.windowClosed(event);
                break;
            case WindowEvent.WINDOW_ICONIFIED:
                listener.windowIconified(event);
                break;
            case WindowEvent.WINDOW_DEICONIFIED:
                listener.windowDeiconified(event);
                break;
            case WindowEvent.WINDOW_ACTIVATED:
                listener.windowActivated(event);
                break;
            case WindowEvent.WINDOW_DEACTIVATED:
                listener.windowDeactivated(event);
                break;
            default:
                break;
        }
    }
}
