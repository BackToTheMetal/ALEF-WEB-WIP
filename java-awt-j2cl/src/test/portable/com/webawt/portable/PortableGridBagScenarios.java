package com.webawt.portable;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Rectangle;

public final class PortableGridBagScenarios {
    private PortableGridBagScenarios() {
    }

    public static void main(String[] arguments) {
        clonesConstraintsAndRejectsInvalidValues();
        distributesExtraSpaceByWeight();
        appliesInsetsFillAndAnchors();
        resolvesRemainderRowsAndReportsGridState();
        mirrorsColumnsForRightToLeftContainers();
    }

    public static void clonesConstraintsAndRejectsInvalidValues() {
        GridBagLayout layout = new GridBagLayout();
        TestComponent component = new TestComponent(10, 10);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 2;
        constraints.insets = new Insets(1, 2, 3, 4);
        layout.setConstraints(component, constraints);
        constraints.gridx = 9;
        constraints.insets.left = 20;
        GridBagConstraints firstCopy = layout.getConstraints(component);
        assertEquals(Integer.valueOf(2), Integer.valueOf(firstCopy.gridx));
        assertEquals(Integer.valueOf(2), Integer.valueOf(firstCopy.insets.left));
        firstCopy.gridx = 7;
        assertEquals(Integer.valueOf(2), Integer.valueOf(layout.getConstraints(component).gridx));
        assertEquals(Integer.valueOf(GridBagConstraints.RELATIVE), Integer.valueOf(layout.getConstraints(new TestComponent(1, 1)).gridx));
        assertThrowsIllegalArgument(() -> layout.addLayoutComponent(component, "invalid"));
    }

    public static void distributesExtraSpaceByWeight() {
        GridBagLayout layout = new GridBagLayout();
        Panel panel = new Panel(layout);
        TestComponent first = new TestComponent(10, 10);
        TestComponent second = new TestComponent(20, 10);
        panel.add(first, constraints(0, 0, 1.0, 1.0, GridBagConstraints.BOTH, GridBagConstraints.CENTER));
        panel.add(second, constraints(1, 0, 1.0, 1.0, GridBagConstraints.BOTH, GridBagConstraints.CENTER));
        assertEquals(new Dimension(30, 10), layout.preferredLayoutSize(panel));
        panel.setSize(100, 40);
        panel.doLayout();
        assertEquals(new Rectangle(0, 0, 45, 40), first.getBounds());
        assertEquals(new Rectangle(45, 0, 55, 40), second.getBounds());
        int[][] dimensions = layout.getLayoutDimensions();
        assertArrayEquals(new int[] {45, 55}, dimensions[0]);
        assertArrayEquals(new int[] {40}, dimensions[1]);
        double[][] weights = layout.getLayoutWeights();
        assertArrayEquals(new double[] {1.0, 1.0}, weights[0]);
        assertArrayEquals(new double[] {1.0}, weights[1]);
    }

    public static void appliesInsetsFillAndAnchors() {
        GridBagLayout layout = new GridBagLayout();
        Panel panel = new Panel(layout);
        TestComponent component = new TestComponent(20, 10);
        GridBagConstraints constraints = constraints(0, 0, 1.0, 1.0, GridBagConstraints.NONE, GridBagConstraints.EAST);
        constraints.insets = new Insets(2, 3, 4, 5);
        panel.add(component, constraints);
        panel.setSize(80, 40);
        panel.doLayout();
        assertEquals(new Rectangle(55, 14, 20, 10), component.getBounds());
        assertEquals(new Point(0, 0), layout.location(4, 3));
        assertEquals(new Point(1, 1), layout.location(100, 100));
    }

    public static void resolvesRemainderRowsAndReportsGridState() {
        GridBagLayout layout = new GridBagLayout();
        Panel panel = new Panel(layout);
        TestComponent first = new TestComponent(10, 10);
        TestComponent second = new TestComponent(10, 10);
        TestComponent third = new TestComponent(10, 10);
        panel.add(first);
        GridBagConstraints remainder = new GridBagConstraints();
        remainder.gridwidth = GridBagConstraints.REMAINDER;
        panel.add(second, remainder);
        panel.add(third);
        panel.setSize(20, 20);
        panel.doLayout();
        assertEquals(new Rectangle(0, 0, 10, 10), first.getBounds());
        assertEquals(new Rectangle(10, 0, 10, 10), second.getBounds());
        assertEquals(new Rectangle(0, 10, 10, 10), third.getBounds());
        assertEquals(new Point(0, 0), layout.getLayoutOrigin());
    }

    public static void mirrorsColumnsForRightToLeftContainers() {
        GridBagLayout layout = new GridBagLayout();
        layout.columnWidths = new int[] {10, 20};
        layout.rowHeights = new int[] {10};
        Panel panel = new Panel(layout);
        panel.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        TestComponent first = new TestComponent(1, 1);
        TestComponent second = new TestComponent(1, 1);
        panel.add(first, constraints(0, 0, 0.0, 0.0, GridBagConstraints.BOTH, GridBagConstraints.CENTER));
        panel.add(second, constraints(1, 0, 0.0, 0.0, GridBagConstraints.BOTH, GridBagConstraints.CENTER));
        panel.setSize(30, 10);
        panel.doLayout();
        assertEquals(new Rectangle(20, 0, 10, 10), first.getBounds());
        assertEquals(new Rectangle(0, 0, 20, 10), second.getBounds());
    }

    private static GridBagConstraints constraints(int x, int y, double weightX, double weightY, int fill, int anchor) {
        return new GridBagConstraints(x, y, 1, 1, weightX, weightY, anchor, fill, new Insets(0, 0, 0, 0), 0, 0);
    }

    private static void assertThrowsIllegalArgument(Runnable runnable) {
        try {
            runnable.run();
        } catch (IllegalArgumentException expected) {
            return;
        }
        throw new AssertionError("Expected IllegalArgumentException");
    }

    private static void assertArrayEquals(int[] expected, int[] actual) {
        if (expected.length != actual.length) {
            throw new AssertionError("Array lengths differ");
        }
        for (int index = 0; index < expected.length; index++) {
            assertEquals(Integer.valueOf(expected[index]), Integer.valueOf(actual[index]));
        }
    }

    private static void assertArrayEquals(double[] expected, double[] actual) {
        if (expected.length != actual.length) {
            throw new AssertionError("Array lengths differ");
        }
        for (int index = 0; index < expected.length; index++) {
            if (Math.abs(expected[index] - actual[index]) > 0.000001) {
                throw new AssertionError("Expected " + expected[index] + " but was " + actual[index]);
            }
        }
    }

    private static void assertEquals(Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected " + expected + " but was " + actual);
        }
    }

    private static final class TestComponent extends Component {
        private TestComponent(int width, int height) {
            setPreferredSize(new Dimension(width, height));
            setMinimumSize(new Dimension(width, height));
        }
    }
}
