package java.awt;

import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public abstract class KeyboardFocusManager implements KeyEventDispatcher, KeyEventPostProcessor {
    public static final int FORWARD_TRAVERSAL_KEYS = 0;
    public static final int BACKWARD_TRAVERSAL_KEYS = 1;
    public static final int UP_CYCLE_TRAVERSAL_KEYS = 2;
    public static final int DOWN_CYCLE_TRAVERSAL_KEYS = 3;
    private static KeyboardFocusManager currentKeyboardFocusManager = new DefaultKeyboardFocusManager();
    private final PropertyChangeSupport propertyChanges = new PropertyChangeSupport(this);
    private final VetoableChangeSupport vetoableChanges = new VetoableChangeSupport(this);
    private final List<KeyEventDispatcher> keyEventDispatchers = new ArrayList<>();
    private final List<KeyEventPostProcessor> keyEventPostProcessors = new ArrayList<>();
    private final List<Set<AWTKeyStroke>> defaultFocusTraversalKeys = new ArrayList<>();
    private FocusTraversalPolicy defaultFocusTraversalPolicy = new DefaultFocusTraversalPolicy();
    private Component focusOwner;
    private Component permanentFocusOwner;
    private Window focusedWindow;
    private Window activeWindow;
    private Container currentFocusCycleRoot;

    public KeyboardFocusManager() {
        defaultFocusTraversalKeys.add(strokeSet(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_TAB, 0), AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_TAB, java.awt.event.InputEvent.CTRL_DOWN_MASK)));
        defaultFocusTraversalKeys.add(strokeSet(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_TAB, java.awt.event.InputEvent.SHIFT_DOWN_MASK), AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_TAB, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK)));
        defaultFocusTraversalKeys.add(Collections.emptySet());
        defaultFocusTraversalKeys.add(Collections.emptySet());
    }

    public static KeyboardFocusManager getCurrentKeyboardFocusManager() {
        return currentKeyboardFocusManager;
    }

    public static void setCurrentKeyboardFocusManager(KeyboardFocusManager manager) throws SecurityException {
        currentKeyboardFocusManager = manager == null ? new DefaultKeyboardFocusManager() : manager;
    }

    public Component getFocusOwner() {
        return focusOwner;
    }

    protected Component getGlobalFocusOwner() throws SecurityException {
        return focusOwner;
    }

    protected void setGlobalFocusOwner(Component component) throws SecurityException {
        if (acceptVeto("focusOwner", focusOwner, component)) {
            Component previous = focusOwner;
            focusOwner = component;
            firePropertyChange("focusOwner", previous, component);
        }
    }

    public void clearFocusOwner() {
        clearGlobalFocusOwner();
    }

    public void clearGlobalFocusOwner() throws SecurityException {
        setGlobalFocusOwner(null);
    }

    public Component getPermanentFocusOwner() {
        return permanentFocusOwner;
    }

    protected Component getGlobalPermanentFocusOwner() throws SecurityException {
        return permanentFocusOwner;
    }

    protected void setGlobalPermanentFocusOwner(Component component) throws SecurityException {
        if (acceptVeto("permanentFocusOwner", permanentFocusOwner, component)) {
            Component previous = permanentFocusOwner;
            permanentFocusOwner = component;
            firePropertyChange("permanentFocusOwner", previous, component);
        }
    }

    public Window getFocusedWindow() {
        return focusedWindow;
    }

    protected Window getGlobalFocusedWindow() throws SecurityException {
        return focusedWindow;
    }

    protected void setGlobalFocusedWindow(Window window) throws SecurityException {
        if (acceptVeto("focusedWindow", focusedWindow, window)) {
            Window previous = focusedWindow;
            focusedWindow = window;
            firePropertyChange("focusedWindow", previous, window);
        }
    }

    public Window getActiveWindow() {
        return activeWindow;
    }

    protected Window getGlobalActiveWindow() throws SecurityException {
        return activeWindow;
    }

    protected void setGlobalActiveWindow(Window window) throws SecurityException {
        if (acceptVeto("activeWindow", activeWindow, window)) {
            Window previous = activeWindow;
            activeWindow = window;
            firePropertyChange("activeWindow", previous, window);
        }
    }

    public synchronized FocusTraversalPolicy getDefaultFocusTraversalPolicy() {
        return defaultFocusTraversalPolicy;
    }

    public void setDefaultFocusTraversalPolicy(FocusTraversalPolicy policy) {
        if (policy == null) {
            throw new IllegalArgumentException("default focus traversal policy cannot be null");
        }
        FocusTraversalPolicy previous = defaultFocusTraversalPolicy;
        defaultFocusTraversalPolicy = policy;
        firePropertyChange("defaultFocusTraversalPolicy", previous, policy);
    }

    public void setDefaultFocusTraversalKeys(int identifier, Set<? extends AWTKeyStroke> keystrokes) {
        validateTraversalIdentifier(identifier);
        Set<AWTKeyStroke> copy = checkedTraversalKeys(identifier, keystrokes);
        Set<AWTKeyStroke> previous = defaultFocusTraversalKeys.set(identifier, copy);
        firePropertyChange(traversalPropertyName(identifier), previous, copy);
    }

    public Set<AWTKeyStroke> getDefaultFocusTraversalKeys(int identifier) {
        validateTraversalIdentifier(identifier);
        return defaultFocusTraversalKeys.get(identifier);
    }

    public Container getCurrentFocusCycleRoot() {
        return currentFocusCycleRoot;
    }

    protected Container getGlobalCurrentFocusCycleRoot() throws SecurityException {
        return currentFocusCycleRoot;
    }

    public void setGlobalCurrentFocusCycleRoot(Container container) throws SecurityException {
        Container previous = currentFocusCycleRoot;
        currentFocusCycleRoot = container;
        firePropertyChange("currentFocusCycleRoot", previous, container);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChanges.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChanges.removePropertyChangeListener(listener);
    }

    public synchronized PropertyChangeListener[] getPropertyChangeListeners() {
        return propertyChanges.getPropertyChangeListeners();
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertyChanges.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertyChanges.removePropertyChangeListener(propertyName, listener);
    }

    public synchronized PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
        return propertyChanges.getPropertyChangeListeners(propertyName);
    }

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        propertyChanges.firePropertyChange(propertyName, oldValue, newValue);
    }

    public void addVetoableChangeListener(VetoableChangeListener listener) {
        vetoableChanges.addVetoableChangeListener(listener);
    }

    public void removeVetoableChangeListener(VetoableChangeListener listener) {
        vetoableChanges.removeVetoableChangeListener(listener);
    }

    public synchronized VetoableChangeListener[] getVetoableChangeListeners() {
        return vetoableChanges.getVetoableChangeListeners();
    }

    public void addVetoableChangeListener(String propertyName, VetoableChangeListener listener) {
        vetoableChanges.addVetoableChangeListener(propertyName, listener);
    }

    public void removeVetoableChangeListener(String propertyName, VetoableChangeListener listener) {
        vetoableChanges.removeVetoableChangeListener(propertyName, listener);
    }

    public synchronized VetoableChangeListener[] getVetoableChangeListeners(String propertyName) {
        return vetoableChanges.getVetoableChangeListeners(propertyName);
    }

    protected void fireVetoableChange(String propertyName, Object oldValue, Object newValue) throws PropertyVetoException {
        vetoableChanges.fireVetoableChange(propertyName, oldValue, newValue);
    }

    public void addKeyEventDispatcher(KeyEventDispatcher dispatcher) {
        if (dispatcher != null) {
            keyEventDispatchers.add(dispatcher);
        }
    }

    public void removeKeyEventDispatcher(KeyEventDispatcher dispatcher) {
        keyEventDispatchers.remove(dispatcher);
    }

    protected synchronized List<KeyEventDispatcher> getKeyEventDispatchers() {
        return keyEventDispatchers.isEmpty() ? null : new ArrayList<>(keyEventDispatchers);
    }

    public void addKeyEventPostProcessor(KeyEventPostProcessor processor) {
        if (processor != null) {
            keyEventPostProcessors.add(processor);
        }
    }

    public void removeKeyEventPostProcessor(KeyEventPostProcessor processor) {
        keyEventPostProcessors.remove(processor);
    }

    protected List<KeyEventPostProcessor> getKeyEventPostProcessors() {
        return keyEventPostProcessors.isEmpty() ? null : new ArrayList<>(keyEventPostProcessors);
    }

    public abstract boolean dispatchEvent(AWTEvent event);

    public final void redispatchEvent(Component target, AWTEvent event) {
        target.dispatchEvent(event);
    }

    @Override
    public abstract boolean dispatchKeyEvent(KeyEvent event);

    @Override
    public abstract boolean postProcessKeyEvent(KeyEvent event);

    public abstract void processKeyEvent(Component focusedComponent, KeyEvent event);

    protected abstract void enqueueKeyEvents(long after, Component untilFocused);

    protected abstract void dequeueKeyEvents(long after, Component untilFocused);

    protected abstract void discardKeyEvents(Component component);

    public abstract void focusNextComponent(Component component);

    public abstract void focusPreviousComponent(Component component);

    public abstract void upFocusCycle(Component component);

    public abstract void downFocusCycle(Container container);

    public final void focusNextComponent() {
        focusNextComponent(getFocusOwner());
    }

    public final void focusPreviousComponent() {
        focusPreviousComponent(getFocusOwner());
    }

    public final void upFocusCycle() {
        upFocusCycle(getFocusOwner());
    }

    public final void downFocusCycle() {
        Component owner = getFocusOwner();
        if (owner instanceof Container) {
            downFocusCycle((Container) owner);
        }
    }

    final void focusGained(Component component, boolean temporary) {
        Window previousFocusedWindow = focusedWindow;
        setGlobalFocusOwner(component);
        if (!temporary) {
            setGlobalPermanentFocusOwner(component);
        }
        Window window = containingWindow(component);
        if (window != null) {
            window.setMostRecentFocusOwner(component);
        }
        setGlobalFocusedWindow(window);
        setGlobalActiveWindow(window);
        if (previousFocusedWindow != window) {
            if (previousFocusedWindow != null) {
                previousFocusedWindow.dispatchEvent(new java.awt.event.WindowEvent(previousFocusedWindow, java.awt.event.WindowEvent.WINDOW_LOST_FOCUS, window));
                previousFocusedWindow.dispatchEvent(new java.awt.event.WindowEvent(previousFocusedWindow, java.awt.event.WindowEvent.WINDOW_DEACTIVATED, window));
            }
            if (window != null) {
                window.dispatchEvent(new java.awt.event.WindowEvent(window, java.awt.event.WindowEvent.WINDOW_ACTIVATED, previousFocusedWindow));
                window.dispatchEvent(new java.awt.event.WindowEvent(window, java.awt.event.WindowEvent.WINDOW_GAINED_FOCUS, previousFocusedWindow));
            }
        }
        Container root = component instanceof Container && ((Container) component).isFocusCycleRoot() ? (Container) component : component.getFocusCycleRootAncestor();
        setGlobalCurrentFocusCycleRoot(root);
    }

    final void focusLost(Component component, boolean temporary) {
        if (focusOwner == component) {
            setGlobalFocusOwner(null);
        }
        if (!temporary && permanentFocusOwner == component) {
            setGlobalPermanentFocusOwner(null);
        }
    }

    private boolean acceptVeto(String propertyName, Object oldValue, Object newValue) {
        try {
            fireVetoableChange(propertyName, oldValue, newValue);
            return true;
        } catch (PropertyVetoException veto) {
            return false;
        }
    }

    private Set<AWTKeyStroke> checkedTraversalKeys(int identifier, Set<? extends AWTKeyStroke> keystrokes) {
        if (keystrokes == null) {
            throw new IllegalArgumentException("keystrokes cannot be null");
        }
        LinkedHashSet<AWTKeyStroke> copy = new LinkedHashSet<>();
        for (AWTKeyStroke keystroke : keystrokes) {
            if (keystroke == null || keystroke.getKeyEventType() == KeyEvent.KEY_TYPED) {
                throw new IllegalArgumentException("focus traversal keys must identify pressed or released keys");
            }
            for (int index = 0; index < defaultFocusTraversalKeys.size(); index++) {
                if (index != identifier && defaultFocusTraversalKeys.get(index).contains(keystroke)) {
                    throw new IllegalArgumentException("focus traversal keys must be unique");
                }
            }
            copy.add(keystroke);
        }
        return Collections.unmodifiableSet(copy);
    }

    private static Set<AWTKeyStroke> strokeSet(AWTKeyStroke first, AWTKeyStroke second) {
        LinkedHashSet<AWTKeyStroke> strokes = new LinkedHashSet<>();
        strokes.add(first);
        strokes.add(second);
        return Collections.unmodifiableSet(strokes);
    }

    private static void validateTraversalIdentifier(int identifier) {
        if (identifier < FORWARD_TRAVERSAL_KEYS || identifier > DOWN_CYCLE_TRAVERSAL_KEYS) {
            throw new IllegalArgumentException("invalid focus traversal key identifier");
        }
    }

    private static String traversalPropertyName(int identifier) {
        switch (identifier) {
            case FORWARD_TRAVERSAL_KEYS:
                return "forwardDefaultFocusTraversalKeys";
            case BACKWARD_TRAVERSAL_KEYS:
                return "backwardDefaultFocusTraversalKeys";
            case UP_CYCLE_TRAVERSAL_KEYS:
                return "upCycleDefaultFocusTraversalKeys";
            default:
                return "downCycleDefaultFocusTraversalKeys";
        }
    }

    private static Window containingWindow(Component component) {
        for (Component current = component; current != null; current = current.getParent()) {
            if (current instanceof Window) {
                return (Window) current;
            }
        }
        return null;
    }
}
