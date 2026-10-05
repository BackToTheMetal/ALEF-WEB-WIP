package com.webawt.portable;

import java.beans.PropertyChangeEvent;
import java.util.Locale;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRelation;
import javax.accessibility.AccessibleRelationSet;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;

public final class PortableAccessibilityScenarios {
    private PortableAccessibilityScenarios() {
    }

    public static void main(String[] arguments) {
        runAll();
    }

    public static void runAll() {
        maintainsUniqueAccessibleStates();
        mergesRelationTargetsDefensively();
        dispatchesAccessiblePropertyChanges();
    }

    public static void maintainsUniqueAccessibleStates() {
        AccessibleStateSet states = new AccessibleStateSet();
        assertTrue(states.add(AccessibleState.ENABLED));
        assertFalse(states.add(AccessibleState.ENABLED));
        assertTrue(states.add(AccessibleState.FOCUSABLE));
        assertEquals(2, states.toArray().length);
        assertTrue(states.contains(AccessibleState.ENABLED));
        assertTrue(states.remove(AccessibleState.ENABLED));
        assertFalse(states.contains(AccessibleState.ENABLED));
    }

    public static void mergesRelationTargetsDefensively() {
        Object first = new Object();
        Object second = new Object();
        AccessibleRelationSet relations = new AccessibleRelationSet();
        relations.add(new AccessibleRelation(AccessibleRelation.LABEL_FOR, first));
        relations.add(new AccessibleRelation(AccessibleRelation.LABEL_FOR, second));
        AccessibleRelation relation = relations.get(AccessibleRelation.LABEL_FOR);
        Object[] targets = relation.getTarget();
        assertEquals(2, targets.length);
        assertSame(first, targets[0]);
        assertSame(second, targets[1]);
        targets[0] = null;
        assertSame(first, relation.getTarget()[0]);
    }

    public static void dispatchesAccessiblePropertyChanges() {
        TestAccessible accessible = new TestAccessible();
        AccessibleContext context = accessible.getAccessibleContext();
        PropertyChangeEvent[] captured = new PropertyChangeEvent[1];
        context.addPropertyChangeListener(event -> captured[0] = event);
        context.setAccessibleName("Submit");
        assertEquals(AccessibleContext.ACCESSIBLE_NAME_PROPERTY, captured[0].getPropertyName());
        assertEquals("Submit", captured[0].getNewValue());
        assertSame(AccessibleRole.PUSH_BUTTON, context.getAccessibleRole());
    }

    private static void assertEquals(int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError("Expected " + expected + " but was " + actual);
        }
    }

    private static void assertEquals(Object expected, Object actual) {
        if (expected == null ? actual != null : !expected.equals(actual)) {
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

    private static final class TestAccessible implements Accessible {
        private final AccessibleContext context = new TestAccessibleContext();

        @Override
        public AccessibleContext getAccessibleContext() {
            return context;
        }
    }

    private static final class TestAccessibleContext extends AccessibleContext {
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.PUSH_BUTTON;
        }

        @Override
        public AccessibleStateSet getAccessibleStateSet() {
            return new AccessibleStateSet(new AccessibleState[] {AccessibleState.ENABLED});
        }

        @Override
        public int getAccessibleIndexInParent() {
            return -1;
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
            return Locale.ROOT;
        }
    }
}
