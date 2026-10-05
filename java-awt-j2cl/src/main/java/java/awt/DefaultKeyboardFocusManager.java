package java.awt;

import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class DefaultKeyboardFocusManager extends KeyboardFocusManager {
    private final List<TypeAheadMarker> typeAheadMarkers = new ArrayList<>();
    private final List<KeyEvent> enqueuedKeyEvents = new ArrayList<>();

    public DefaultKeyboardFocusManager() {
    }

    @Override
    public boolean dispatchEvent(AWTEvent event) {
        if (event instanceof KeyEvent) {
            return dispatchKeyEvent((KeyEvent) event);
        }
        if (!(event.getSource() instanceof Component)) {
            return false;
        }
        Component component = (Component) event.getSource();
        if (event instanceof FocusEvent) {
            FocusEvent focusEvent = (FocusEvent) event;
            if (focusEvent.getID() == FocusEvent.FOCUS_GAINED) {
                focusGained(component, focusEvent.isTemporary());
                dequeueKeyEvents(-1L, component);
            } else if (focusEvent.getID() == FocusEvent.FOCUS_LOST) {
                focusLost(component, focusEvent.isTemporary());
            }
        }
        redispatchEvent(component, event);
        return event.isConsumed();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (shouldEnqueue(event)) {
            enqueuedKeyEvents.add(event);
            return true;
        }
        List<KeyEventDispatcher> dispatchers = getKeyEventDispatchers();
        if (dispatchers != null) {
            for (KeyEventDispatcher dispatcher : dispatchers) {
                if (dispatcher.dispatchKeyEvent(event)) {
                    return true;
                }
            }
        }
        if (event.getSource() instanceof Component) {
            processKeyEvent((Component) event.getSource(), event);
        }
        List<KeyEventPostProcessor> processors = getKeyEventPostProcessors();
        if (processors != null) {
            for (KeyEventPostProcessor processor : processors) {
                if (processor.postProcessKeyEvent(event)) {
                    return true;
                }
            }
        }
        return postProcessKeyEvent(event);
    }

    @Override
    public boolean postProcessKeyEvent(KeyEvent event) {
        if (!event.isConsumed() && event.getSource() instanceof Component) {
            Component source = (Component) event.getSource();
            for (Component ancestor = source; ancestor != null; ancestor = ancestor.getParent()) {
                if (ancestor instanceof Frame) {
                    MenuBar menuBar = ((Frame) ancestor).getMenuBar();
                    if (menuBar != null && menuBar.handleShortcut(event)) {
                        event.consume();
                    }
                    break;
                }
            }
        }
        return event.isConsumed();
    }

    @Override
    public void processKeyEvent(Component focusedComponent, KeyEvent event) {
        if (focusedComponent.getFocusTraversalKeysEnabled()) {
            AWTKeyStroke stroke = AWTKeyStroke.getAWTKeyStrokeForEvent(event);
            if (isTraversalKey(focusedComponent, FORWARD_TRAVERSAL_KEYS, stroke)) {
                if (moveFocus(focusedComponent, true)) {
                    event.consume();
                    return;
                }
            }
            if (isTraversalKey(focusedComponent, BACKWARD_TRAVERSAL_KEYS, stroke)) {
                if (moveFocus(focusedComponent, false)) {
                    event.consume();
                    return;
                }
            }
            if (isTraversalKey(focusedComponent, UP_CYCLE_TRAVERSAL_KEYS, stroke)) {
                if (moveUpFocusCycle(focusedComponent)) {
                    event.consume();
                    return;
                }
            }
            if (focusedComponent instanceof Container && isTraversalKey(focusedComponent, DOWN_CYCLE_TRAVERSAL_KEYS, stroke)) {
                if (moveDownFocusCycle((Container) focusedComponent)) {
                    event.consume();
                    return;
                }
            }
        }
        redispatchEvent(focusedComponent, event);
    }

    @Override
    protected synchronized void enqueueKeyEvents(long after, Component untilFocused) {
        if (untilFocused == null) {
            throw new IllegalArgumentException("untilFocused cannot be null");
        }
        typeAheadMarkers.add(new TypeAheadMarker(after, untilFocused));
    }

    @Override
    protected synchronized void dequeueKeyEvents(long after, Component untilFocused) {
        for (Iterator<TypeAheadMarker> iterator = typeAheadMarkers.iterator(); iterator.hasNext();) {
            TypeAheadMarker marker = iterator.next();
            if (marker.untilFocused == untilFocused && (after < 0L || marker.after == after)) {
                iterator.remove();
                break;
            }
        }
        flushReadyKeyEvents();
    }

    @Override
    protected synchronized void discardKeyEvents(Component component) {
        boolean removed = false;
        for (Iterator<TypeAheadMarker> iterator = typeAheadMarkers.iterator(); iterator.hasNext();) {
            TypeAheadMarker marker = iterator.next();
            if (contains(component, marker.untilFocused)) {
                iterator.remove();
                removed = true;
            }
        }
        if (removed) {
            enqueuedKeyEvents.clear();
        }
    }

    @Override
    public void focusPreviousComponent(Component component) {
        moveFocus(component, false);
    }

    @Override
    public void focusNextComponent(Component component) {
        moveFocus(component, true);
    }

    @Override
    public void upFocusCycle(Component component) {
        moveUpFocusCycle(component);
    }

    @Override
    public void downFocusCycle(Container container) {
        moveDownFocusCycle(container);
    }

    private boolean moveUpFocusCycle(Component component) {
        if (component == null) {
            return false;
        }
        Container root = component.getFocusCycleRootAncestor();
        if (root == null) {
            return false;
        }
        Container parentRoot = root.getFocusCycleRootAncestor();
        setGlobalCurrentFocusCycleRoot(parentRoot);
        root.requestFocus();
        return true;
    }

    private boolean moveDownFocusCycle(Container container) {
        if (container == null || !container.isFocusCycleRoot()) {
            return false;
        }
        FocusTraversalPolicy policy = container.getFocusTraversalPolicy();
        Component target = policy == null ? null : policy.getDefaultComponent(container);
        if (target != null) {
            setGlobalCurrentFocusCycleRoot(container);
            target.requestFocus();
            return true;
        }
        return false;
    }

    private static boolean isTraversalKey(Component component, int identifier, AWTKeyStroke stroke) {
        Set<AWTKeyStroke> strokes = component.getFocusTraversalKeys(identifier);
        return strokes.contains(stroke);
    }

    private boolean shouldEnqueue(KeyEvent event) {
        for (TypeAheadMarker marker : typeAheadMarkers) {
            if (event.getWhen() > marker.after && getFocusOwner() != marker.untilFocused) {
                return true;
            }
        }
        return false;
    }

    private void flushReadyKeyEvents() {
        List<KeyEvent> ready = new ArrayList<>();
        for (Iterator<KeyEvent> iterator = enqueuedKeyEvents.iterator(); iterator.hasNext();) {
            KeyEvent event = iterator.next();
            if (!shouldEnqueue(event)) {
                ready.add(event);
                iterator.remove();
            }
        }
        for (KeyEvent event : ready) {
            dispatchKeyEvent(event);
        }
    }

    private static boolean contains(Component ancestor, Component component) {
        return ancestor == component || ancestor instanceof Container && ((Container) ancestor).isAncestorOf(component);
    }

    private static boolean moveFocus(Component component, boolean forward) {
        if (component == null) {
            return false;
        }
        Container root = component instanceof Container && ((Container) component).isFocusCycleRoot() ? (Container) component : component.getFocusCycleRootAncestor();
        if (root == null) {
            return false;
        }
        FocusTraversalPolicy policy = root.getFocusTraversalPolicy();
        Component target = forward ? policy.getComponentAfter(root, component) : policy.getComponentBefore(root, component);
        if (target != null) {
            target.requestFocus();
            return true;
        }
        return false;
    }

    private static final class TypeAheadMarker {
        private final long after;
        private final Component untilFocused;

        private TypeAheadMarker(long after, Component untilFocused) {
            this.after = after;
            this.untilFocused = untilFocused;
        }
    }
}
