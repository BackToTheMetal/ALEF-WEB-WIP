package com.webawt.portable;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.ContainerOrderFocusTraversalPolicy;
import java.awt.DefaultFocusTraversalPolicy;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.FocusTraversalPolicy;
import java.awt.GridLayout;
import java.awt.MenuContainer;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentListener;
import java.awt.event.HierarchyBoundsAdapter;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.image.ImageObserver;
import java.io.Serializable;

public final class PortableComponentScenarios {
    private PortableComponentScenarios() {
    }

    public static void main(String[] arguments) {
        runAll();
    }

    public static void runAll() {
        maintainsComponentHierarchyAndBounds();
        laysOutFlowRows();
        laysOutBorderRegions();
        laysOutUniformGridCells();
        switchesNamedCards();
        traversesFocusInContainerOrder();
        dispatchesHierarchyAndInputMethodEvents();
        exposesLegacyComponentContracts();
        dispatchesLegacyEventsAndPrimitiveProperties();
    }

    public static void maintainsComponentHierarchyAndBounds() {
        Panel panel = new Panel(null);
        panel.setSize(100, 100);
        TestComponent child = new TestComponent(10, 20);
        panel.add(child);
        child.setBounds(3, 4, 10, 20);
        assertSame(panel, child.getParent());
        assertEquals(new Rectangle(3, 4, 10, 20), child.getBounds());
        assertSame(child, panel.findComponentAt(4, 5));
        panel.remove(child);
        assertSame(null, child.getParent());
    }

    public static void laysOutFlowRows() {
        Panel panel = new Panel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        panel.setSize(100, 50);
        TestComponent first = new TestComponent(10, 10);
        TestComponent second = new TestComponent(20, 10);
        panel.add(first);
        panel.add(second);
        panel.doLayout();
        assertEquals(new Rectangle(5, 5, 10, 10), first.getBounds());
        assertEquals(new Rectangle(20, 5, 20, 10), second.getBounds());
    }

    public static void laysOutBorderRegions() {
        Panel panel = new Panel(new BorderLayout(2, 3));
        panel.setSize(100, 60);
        TestComponent north = new TestComponent(10, 10);
        TestComponent west = new TestComponent(20, 5);
        TestComponent center = new TestComponent(1, 1);
        panel.add(north, BorderLayout.NORTH);
        panel.add(west, BorderLayout.WEST);
        panel.add(center, BorderLayout.CENTER);
        panel.doLayout();
        assertEquals(new Rectangle(0, 0, 100, 10), north.getBounds());
        assertEquals(new Rectangle(0, 13, 20, 47), west.getBounds());
        assertEquals(new Rectangle(22, 13, 78, 47), center.getBounds());
    }

    public static void laysOutUniformGridCells() {
        Panel panel = new Panel(new GridLayout(1, 2, 2, 0));
        panel.setSize(100, 20);
        TestComponent first = new TestComponent(1, 1);
        TestComponent second = new TestComponent(1, 1);
        panel.add(first);
        panel.add(second);
        panel.doLayout();
        assertEquals(new Rectangle(0, 0, 49, 20), first.getBounds());
        assertEquals(new Rectangle(51, 0, 49, 20), second.getBounds());
    }

    public static void switchesNamedCards() {
        CardLayout layout = new CardLayout();
        Panel panel = new Panel(layout);
        panel.setSize(30, 40);
        TestComponent first = new TestComponent(1, 1);
        TestComponent second = new TestComponent(1, 1);
        panel.add(first, "first");
        panel.add(second, "second");
        panel.doLayout();
        assertTrue(first.isVisible());
        assertFalse(second.isVisible());
        layout.show(panel, "second");
        assertFalse(first.isVisible());
        assertTrue(second.isVisible());
        assertEquals(new Rectangle(0, 0, 30, 40), second.getBounds());
    }

    public static void traversesFocusInContainerOrder() {
        TestPanel root = new TestPanel();
        root.setFocusCycleRoot(true);
        TestComponent first = new TestComponent(1, 1);
        TestPanel nested = new TestPanel();
        TestComponent second = new TestComponent(1, 1);
        TestComponent third = new TestComponent(1, 1);
        root.add(first);
        root.add(nested);
        nested.add(second);
        root.add(third);
        root.enableDisplayable();
        nested.enableDisplayable();
        TestFocusTraversalPolicy policy = new TestFocusTraversalPolicy();
        root.setFocusTraversalPolicy(policy);
        assertTrue(root.isFocusCycleRoot());
        assertTrue(root.isFocusTraversalPolicySet());
        assertSame(policy, root.getFocusTraversalPolicy());
        assertSame(root, second.getFocusCycleRootAncestor());
        assertTrue(second.isFocusCycleRoot(root));
        assertSame(first, policy.getFirstComponent(root));
        assertSame(third, policy.getLastComponent(root));
        assertSame(first, policy.getDefaultComponent(root));
        assertSame(second, policy.getComponentAfter(root, first));
        assertSame(third, policy.getComponentAfter(root, second));
        assertSame(first, policy.getComponentAfter(root, third));
        assertSame(third, policy.getComponentBefore(root, first));
        second.setEnabled(false);
        assertSame(third, policy.getComponentAfter(root, first));
        policy.setImplicitDownCycleTraversal(false);
        assertFalse(policy.getImplicitDownCycleTraversal());
        root.setFocusTraversalPolicyProvider(true);
        assertTrue(root.isFocusTraversalPolicyProvider());
        assertSame(null, new DefaultFocusTraversalPolicy().getFirstComponent(root));
        FocusTraversalPolicy abstractPolicy = policy;
        assertSame(first, abstractPolicy.getDefaultComponent(root));
    }

    public static void dispatchesHierarchyAndInputMethodEvents() {
        Panel root = new Panel(null);
        TestComponent child = new TestComponent(1, 1);
        int[] hierarchyChanges = new int[1];
        HierarchyListener hierarchyListener = event -> {
            assertSame(child, event.getChanged());
            assertSame(root, event.getChangedParent());
            assertTrue((event.getChangeFlags() & HierarchyEvent.PARENT_CHANGED) != 0);
            hierarchyChanges[0]++;
        };
        child.addHierarchyListener(hierarchyListener);
        root.add(child);
        assertEquals(Integer.valueOf(1), Integer.valueOf(hierarchyChanges[0]));
        assertSame(hierarchyListener, child.getHierarchyListeners()[0]);
        assertSame(hierarchyListener, child.getListeners(HierarchyListener.class)[0]);
        int[] ancestorMoves = new int[1];
        HierarchyBoundsListener boundsListener = new HierarchyBoundsAdapter() {
            @Override
            public void ancestorMoved(HierarchyEvent event) {
                ancestorMoves[0]++;
            }
        };
        child.addHierarchyBoundsListener(boundsListener);
        root.setLocation(10, 20);
        assertEquals(Integer.valueOf(1), Integer.valueOf(ancestorMoves[0]));
        assertSame(boundsListener, child.getHierarchyBoundsListeners()[0]);
        int[] inputChanges = new int[1];
        InputMethodListener inputListener = new InputMethodListener() {
            @Override
            public void inputMethodTextChanged(InputMethodEvent event) {
                inputChanges[0]++;
            }
            @Override
            public void caretPositionChanged(InputMethodEvent event) {
                inputChanges[0]++;
            }
        };
        child.addInputMethodListener(inputListener);
        child.dispatchEvent(new InputMethodEvent(child, InputMethodEvent.CARET_POSITION_CHANGED, null, null));
        assertEquals(Integer.valueOf(1), Integer.valueOf(inputChanges[0]));
        child.enableInputMethods(false);
        child.dispatchEvent(new InputMethodEvent(child, InputMethodEvent.CARET_POSITION_CHANGED, null, null));
        assertEquals(Integer.valueOf(2), Integer.valueOf(inputChanges[0]));
        child.removeInputMethodListener(inputListener);
        assertEquals(Integer.valueOf(0), Integer.valueOf(child.getInputMethodListeners().length));
    }

    public static void exposesLegacyComponentContracts() {
        TestComponent component = new TestComponent(1, 1);
        assertTrue(component instanceof ImageObserver);
        assertTrue(component instanceof MenuContainer);
        assertTrue(component instanceof Serializable);
        assertTrue(component.imageUpdate(null, ImageObserver.SOMEBITS, 0, 0, 1, 1));
        assertFalse(component.imageUpdate(null, ImageObserver.ALLBITS, 0, 0, 1, 1));
        assertFalse(component.imageUpdate(null, ImageObserver.ABORT, 0, 0, 1, 1));
        ComponentListener listener = new ComponentAdapter() {
        };
        component.addComponentListener(listener);
        assertSame(listener, component.getListeners(ComponentListener.class)[0]);
    }

    public static void dispatchesLegacyEventsAndPrimitiveProperties() {
        LegacyPanel panel = new LegacyPanel();
        LegacyComponent child = new LegacyComponent();
        child.setBounds(4, 6, 20, 20);
        panel.setSize(40, 40);
        panel.add(child);
        Event direct = new Event(child, 1L, Event.MOUSE_DOWN, 2, 3, 0, 0);
        assertTrue(child.handleEvent(direct));
        assertEquals(Integer.valueOf(2), Integer.valueOf(child.lastX));
        Event bubbled = new Event(child, 1L, Event.MOUSE_MOVE, 2, 3, 0, 0);
        assertTrue(child.postEvent(bubbled));
        assertEquals(Integer.valueOf(6), Integer.valueOf(panel.lastX));
        assertEquals(Integer.valueOf(9), Integer.valueOf(panel.lastY));
        Event delivered = new Event(panel, 1L, Event.MOUSE_DOWN, 7, 10, 0, 0);
        panel.deliverEvent(delivered);
        assertEquals(Integer.valueOf(3), Integer.valueOf(child.lastX));
        assertEquals(Integer.valueOf(4), Integer.valueOf(child.lastY));
        int[] changes = new int[1];
        child.addPropertyChangeListener(event -> changes[0]++);
        child.firePropertyChange("byte", (byte) 1, (byte) 2);
        child.firePropertyChange("char", 'a', 'b');
        child.firePropertyChange("short", (short) 1, (short) 2);
        child.firePropertyChange("long", 1L, 2L);
        child.firePropertyChange("float", 1.0f, 2.0f);
        child.firePropertyChange("double", 1.0, 2.0);
        child.firePropertyChange("same", 1L, 1L);
        assertEquals(Integer.valueOf(6), Integer.valueOf(changes[0]));
        assertFalse(child.isDoubleBuffered());
        child.setName("legacy");
        assertTrue(child.toString().contains("legacy,4,6,20x20,invalid"));
    }

    private static void assertEquals(Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected " + expected + " but was " + actual);
        }
    }

    private static void assertSame(Object expected, Object actual) {
        if (expected != actual) {
            throw new AssertionError("Expected identical objects: expected=" + expected + ", actual=" + actual);
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
        TestComponent(int width, int height) {
            setPreferredSize(new Dimension(width, height));
            setMinimumSize(new Dimension(width, height));
        }
    }

    private static final class TestPanel extends Panel {
        private boolean displayable;

        private TestPanel() {
            super(null);
            setFocusable(false);
        }

        @Override
        public boolean isDisplayable() {
            return displayable;
        }

        private void enableDisplayable() {
            displayable = true;
        }
    }

    private static final class TestFocusTraversalPolicy extends ContainerOrderFocusTraversalPolicy {
        @Override
        protected boolean accept(Component component) {
            return component instanceof TestComponent && component.isVisible() && component.isEnabled() && component.isFocusable();
        }
    }

    private static final class LegacyComponent extends Component {
        private int lastX;
        private int lastY;

        @Override
        public boolean mouseDown(Event event, int x, int y) {
            lastX = x;
            lastY = y;
            return true;
        }
    }

    private static final class LegacyPanel extends Panel {
        private int lastX;
        private int lastY;

        private LegacyPanel() {
            super(null);
        }

        @Override
        public boolean mouseMove(Event event, int x, int y) {
            lastX = x;
            lastY = y;
            return true;
        }
    }
}
