package com.webawt.portable;

import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeSupport;

public final class PortableBeansScenarios {
    private PortableBeansScenarios() {
    }

    public static void main(String[] arguments) throws PropertyVetoException {
        runAll();
    }

    public static void runAll() throws PropertyVetoException {
        dispatchesPropertyChanges();
        describesIndexedChanges();
        rollsBackVetoNotifications();
    }

    public static void dispatchesPropertyChanges() {
        PropertyChangeSupport support = new PropertyChangeSupport("source");
        StringBuilder calls = new StringBuilder();
        support.addPropertyChangeListener(event -> calls.append("all:").append(event.getPropertyName()).append(';'));
        support.addPropertyChangeListener("name", event -> calls.append("named:").append(event.getNewValue()).append(';'));
        support.firePropertyChange("name", "old", "new");
        support.firePropertyChange("name", "same", "same");
        assertEquals("all:name;named:new;", calls.toString());
        assertTrue(support.hasListeners("name"));
        assertEquals(2, support.getPropertyChangeListeners().length);
    }

    public static void describesIndexedChanges() {
        PropertyChangeEvent event = new IndexedPropertyChangeEvent("source", "items", "old", "new", 3);
        assertEquals("items", event.getPropertyName());
        assertEquals("old", event.getOldValue());
        assertEquals("new", event.getNewValue());
        assertEquals(3, ((IndexedPropertyChangeEvent) event).getIndex());
        event.setPropagationId("propagation");
        assertEquals("propagation", event.getPropagationId());
    }

    public static void rollsBackVetoNotifications() throws PropertyVetoException {
        VetoableChangeSupport support = new VetoableChangeSupport("source");
        int[] calls = new int[3];
        support.addVetoableChangeListener(event -> calls[event.getNewValue().equals("new") ? 0 : 1]++);
        support.addVetoableChangeListener(event -> {
            if (event.getNewValue().equals("new")) {
                calls[2]++;
                throw new PropertyVetoException("blocked", event);
            }
        });
        try {
            support.fireVetoableChange("name", "old", "new");
            throw new AssertionError("Expected veto");
        } catch (PropertyVetoException expected) {
            assertEquals("blocked", expected.getMessage());
        }
        assertEquals(1, calls[0]);
        assertEquals(1, calls[1]);
        assertEquals(1, calls[2]);
    }

    private static void assertEquals(int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError("Expected " + expected + " but was " + actual);
        }
    }

    private static void assertEquals(Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected " + expected + " but was " + actual);
        }
    }

    private static void assertTrue(boolean value) {
        if (!value) {
            throw new AssertionError("Expected true");
        }
    }
}
