package com.webawt.portable;

import java.awt.AWTKeyStroke;
import java.awt.Component;
import java.awt.Container;
import java.awt.DefaultKeyboardFocusManager;
import java.awt.KeyboardFocusManager;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.util.LinkedHashSet;
import java.util.Set;
import org.junit.Test;

public final class KeyboardFocusManagerJ2clTest {
    @Test
    public void dispatchesOwningFrameShortcutsAndRespectsDisabledMenusAndConsumption() {
        java.awt.Frame frame = new java.awt.Frame();
        java.awt.MenuBar bar = new java.awt.MenuBar();
        java.awt.Menu menu = new java.awt.Menu("Actions");
        java.awt.MenuItem item = new java.awt.MenuItem("Test", new java.awt.MenuShortcut(KeyEvent.VK_M, true));
        int[] invoked = new int[1];
        item.addActionListener(event -> invoked[0]++);
        menu.add(item);
        bar.add(menu);
        frame.setMenuBar(bar);
        TestComponent component = new TestComponent();
        frame.add(component);
        DefaultKeyboardFocusManager manager = new DefaultKeyboardFocusManager();
        int modifiers = java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx() | KeyEvent.SHIFT_DOWN_MASK;
        KeyEvent pressed = new KeyEvent(component, KeyEvent.KEY_PRESSED, 1L, modifiers, KeyEvent.VK_M, 'M');
        assertTrue(manager.dispatchKeyEvent(pressed));
        assertEquals(1, invoked[0]);
        assertTrue(manager.dispatchKeyEvent(new KeyEvent(component, KeyEvent.KEY_RELEASED, 2L, modifiers, KeyEvent.VK_M, 'M')));
        assertEquals(1, invoked[0]);
        menu.setEnabled(false);
        assertFalse(manager.dispatchKeyEvent(new KeyEvent(component, KeyEvent.KEY_PRESSED, 3L, modifiers, KeyEvent.VK_M, 'M')));
        assertEquals(1, invoked[0]);
        menu.setEnabled(true);
        KeyEvent consumed = new KeyEvent(component, KeyEvent.KEY_PRESSED, 4L, modifiers, KeyEvent.VK_M, 'M');
        consumed.consume();
        manager.dispatchKeyEvent(consumed);
        assertEquals(1, invoked[0]);
        java.awt.Frame other = new java.awt.Frame();
        TestComponent unrelated = new TestComponent();
        other.add(unrelated);
        assertFalse(manager.dispatchKeyEvent(new KeyEvent(unrelated, KeyEvent.KEY_PRESSED, 5L, modifiers, KeyEvent.VK_M, 'M')));
        assertEquals(1, invoked[0]);
        frame.dispose();
        other.dispose();
    }

    @Test
    public void managesFocusPropertiesAndVetoes() {
        TestKeyboardFocusManager manager = new TestKeyboardFocusManager();
        KeyboardFocusManager previous = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        KeyboardFocusManager.setCurrentKeyboardFocusManager(manager);
        TestComponent first = new TestComponent();
        TestComponent second = new TestComponent();
        int[] propertyChanges = new int[1];
        manager.addPropertyChangeListener("focusOwner", event -> propertyChanges[0]++);
        manager.claimFocus(first);
        assertSame(first, manager.getFocusOwner());
        assertTrue(first.hasFocus());
        assertEquals(1, propertyChanges[0]);
        manager.addVetoableChangeListener("focusOwner", event -> vetoSecond(event, second));
        manager.claimFocus(second);
        assertSame(first, manager.getFocusOwner());
        assertEquals(1, propertyChanges[0]);
        manager.clearGlobalFocusOwner();
        assertSame(null, manager.getFocusOwner());
        assertFalse(first.isFocusOwner());
        KeyboardFocusManager.setCurrentKeyboardFocusManager(previous);
    }

    @Test
    public void dispatchesKeysInPipelineOrder() {
        DefaultKeyboardFocusManager manager = new DefaultKeyboardFocusManager();
        TestComponent component = new TestComponent();
        int[] calls = new int[4];
        manager.addKeyEventDispatcher(event -> {
            calls[0]++;
            return false;
        });
        manager.addKeyEventDispatcher(event -> {
            calls[1]++;
            return true;
        });
        component.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent event) {
                calls[2]++;
            }
        });
        KeyEvent intercepted = new KeyEvent(component, KeyEvent.KEY_PRESSED, 1L, 0, KeyEvent.VK_A, 'a');
        assertTrue(manager.dispatchKeyEvent(intercepted));
        assertEquals(1, calls[0]);
        assertEquals(1, calls[1]);
        assertEquals(0, calls[2]);
        DefaultKeyboardFocusManager deliveredManager = new DefaultKeyboardFocusManager();
        deliveredManager.addKeyEventPostProcessor(event -> {
            calls[3]++;
            return true;
        });
        KeyEvent delivered = new KeyEvent(component, KeyEvent.KEY_PRESSED, 2L, 0, KeyEvent.VK_A, 'a');
        assertTrue(deliveredManager.dispatchKeyEvent(delivered));
        assertEquals(1, calls[2]);
        assertEquals(1, calls[3]);
    }

    @Test
    public void managesTraversalKeySets() {
        DefaultKeyboardFocusManager manager = new DefaultKeyboardFocusManager();
        Set<AWTKeyStroke> forward = manager.getDefaultFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
        assertTrue(forward.contains(AWTKeyStroke.getAWTKeyStroke("pressed TAB")));
        assertTrue(forward.contains(AWTKeyStroke.getAWTKeyStroke("ctrl pressed TAB")));
        LinkedHashSet<AWTKeyStroke> up = new LinkedHashSet<>();
        up.add(AWTKeyStroke.getAWTKeyStroke("pressed F6"));
        manager.setDefaultFocusTraversalKeys(KeyboardFocusManager.UP_CYCLE_TRAVERSAL_KEYS, up);
        assertTrue(manager.getDefaultFocusTraversalKeys(KeyboardFocusManager.UP_CYCLE_TRAVERSAL_KEYS).contains(AWTKeyStroke.getAWTKeyStroke("pressed F6")));
        TestComponent component = new TestComponent();
        component.setFocusTraversalKeys(KeyboardFocusManager.UP_CYCLE_TRAVERSAL_KEYS, up);
        assertTrue(component.areFocusTraversalKeysSet(KeyboardFocusManager.UP_CYCLE_TRAVERSAL_KEYS));
        assertTrue(component.getFocusTraversalKeys(KeyboardFocusManager.UP_CYCLE_TRAVERSAL_KEYS).contains(AWTKeyStroke.getAWTKeyStroke("pressed F6")));
        component.setFocusTraversalKeysEnabled(false);
        assertFalse(component.getFocusTraversalKeysEnabled());
        assertIllegalArgument(() -> manager.setDefaultFocusTraversalKeys(KeyboardFocusManager.UP_CYCLE_TRAVERSAL_KEYS, typedKeySet()));
        assertIllegalArgument(() -> component.getFocusTraversalKeys(KeyboardFocusManager.DOWN_CYCLE_TRAVERSAL_KEYS));
    }

    @Test
    public void containersSupportDownCycleKeysAndReceiveOtherKeys() {
        Container container = new Container();
        LinkedHashSet<AWTKeyStroke> down = new LinkedHashSet<>();
        down.add(AWTKeyStroke.getAWTKeyStroke("pressed F7"));
        container.setFocusTraversalKeys(KeyboardFocusManager.DOWN_CYCLE_TRAVERSAL_KEYS, down);
        assertTrue(container.areFocusTraversalKeysSet(KeyboardFocusManager.DOWN_CYCLE_TRAVERSAL_KEYS));
        assertTrue(container.getFocusTraversalKeys(KeyboardFocusManager.DOWN_CYCLE_TRAVERSAL_KEYS)
                .contains(AWTKeyStroke.getAWTKeyStroke("pressed F7")));
        int[] delivered = new int[1];
        container.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent event) {
                delivered[0]++;
            }
        });
        new DefaultKeyboardFocusManager().dispatchKeyEvent(
                new KeyEvent(container, KeyEvent.KEY_PRESSED, 1L, 0, KeyEvent.VK_A, 'a'));
        assertEquals(1, delivered[0]);
        assertIllegalArgument(() -> container.setFocusTraversalKeys(
                KeyboardFocusManager.DOWN_CYCLE_TRAVERSAL_KEYS, typedKeySet()));
    }

    @Test
    public void updatesFocusFromFocusEvents() {
        DefaultKeyboardFocusManager manager = new DefaultKeyboardFocusManager();
        KeyboardFocusManager previous = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        KeyboardFocusManager.setCurrentKeyboardFocusManager(manager);
        TestComponent component = new TestComponent();
        int[] gained = new int[1];
        component.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(FocusEvent event) {
                gained[0]++;
            }
        });
        assertFalse(manager.dispatchEvent(new FocusEvent(component, FocusEvent.FOCUS_GAINED)));
        assertSame(component, manager.getFocusOwner());
        assertSame(component, manager.getPermanentFocusOwner());
        assertTrue(component.isFocusOwner());
        assertEquals(1, gained[0]);
        manager.dispatchEvent(new FocusEvent(component, FocusEvent.FOCUS_LOST));
        assertSame(null, manager.getFocusOwner());
        KeyboardFocusManager.setCurrentKeyboardFocusManager(previous);
    }

    @Test
    public void queuesKeysUntilFocusArrives() {
        TestKeyboardFocusManager manager = new TestKeyboardFocusManager();
        TestComponent component = new TestComponent();
        int[] delivered = new int[1];
        component.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent event) {
                delivered[0]++;
            }
        });
        manager.holdKeysAfter(10L, component);
        assertTrue(manager.dispatchKeyEvent(new KeyEvent(component, KeyEvent.KEY_PRESSED, 11L, 0, KeyEvent.VK_A, 'a')));
        assertEquals(0, delivered[0]);
        manager.releaseKeys(component);
        assertEquals(1, delivered[0]);
    }

    private static Set<AWTKeyStroke> typedKeySet() {
        LinkedHashSet<AWTKeyStroke> keys = new LinkedHashSet<>();
        keys.add(AWTKeyStroke.getAWTKeyStroke('x'));
        return keys;
    }

    private static void vetoSecond(PropertyChangeEvent event, Component second) throws PropertyVetoException {
        if (event.getNewValue() == second) {
            throw new PropertyVetoException("rejected", event);
        }
    }

    private static void assertIllegalArgument(Runnable action) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            return;
        }
        throw new AssertionError("Expected IllegalArgumentException");
    }

    private static void assertEquals(int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError("Expected " + expected + " but was " + actual);
        }
    }

    private static void assertSame(Object expected, Object actual) {
        if (expected != actual) {
            throw new AssertionError("Expected identical objects");
        }
    }

    private static void assertTrue(boolean value) {
        if (!value) {
            throw new AssertionError("Expected true");
        }
    }

    private static void assertFalse(boolean value) {
        if (value) {
            throw new AssertionError("Expected false");
        }
    }

    private static final class TestComponent extends Component {
    }

    private static final class TestKeyboardFocusManager extends DefaultKeyboardFocusManager {
        private void claimFocus(Component component) {
            setGlobalFocusOwner(component);
        }

        private void holdKeysAfter(long when, Component component) {
            enqueueKeyEvents(when, component);
        }

        private void releaseKeys(Component component) {
            dequeueKeyEvents(-1L, component);
        }
    }
}
