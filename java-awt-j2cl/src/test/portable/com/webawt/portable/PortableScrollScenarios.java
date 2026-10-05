package com.webawt.portable;

import java.awt.AWTError;
import java.awt.Adjustable;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.ScrollPane;
import java.awt.ScrollPaneAdjustable;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import javax.accessibility.AccessibleRole;

public final class PortableScrollScenarios {
    private PortableScrollScenarios() {
    }

    public static void main(String[] arguments) {
        normalizesScrollbarRanges();
        laysOutAndClampsScrollPaneContent();
        reportsAdjustmentChanges();
    }

    public static void normalizesScrollbarRanges() {
        Scrollbar scrollbar = new Scrollbar(Scrollbar.VERTICAL, 50, 20, 0, 100);
        assertEquals(Integer.valueOf(50), Integer.valueOf(scrollbar.getValue()));
        scrollbar.setValue(90);
        assertEquals(Integer.valueOf(80), Integer.valueOf(scrollbar.getValue()));
        scrollbar.setMinimum(85);
        assertEquals(Integer.valueOf(85), Integer.valueOf(scrollbar.getMinimum()));
        assertEquals(Integer.valueOf(15), Integer.valueOf(scrollbar.getVisibleAmount()));
        assertEquals(Integer.valueOf(85), Integer.valueOf(scrollbar.getValue()));
        scrollbar.setMaximum(80);
        assertEquals(Integer.valueOf(79), Integer.valueOf(scrollbar.getMinimum()));
        assertEquals(Integer.valueOf(80), Integer.valueOf(scrollbar.getMaximum()));
        assertEquals(Integer.valueOf(1), Integer.valueOf(scrollbar.getVisible()));
        assertEquals(Integer.valueOf(79), Integer.valueOf(scrollbar.getValue()));
        scrollbar.setUnitIncrement(0);
        scrollbar.setBlockIncrement(-5);
        assertEquals(Integer.valueOf(1), Integer.valueOf(scrollbar.getLineIncrement()));
        assertEquals(Integer.valueOf(1), Integer.valueOf(scrollbar.getPageIncrement()));
        scrollbar.setOrientation(Scrollbar.HORIZONTAL);
        assertEquals(Integer.valueOf(Scrollbar.HORIZONTAL), Integer.valueOf(scrollbar.getOrientation()));
        assertTrue(scrollbar.toString().contains(",horz,isAdjusting=false"));
        assertSame(AccessibleRole.SCROLL_BAR, scrollbar.getAccessibleContext().getAccessibleRole());
        assertThrowsIllegalArgument(() -> scrollbar.setOrientation(99));
    }

    public static void laysOutAndClampsScrollPaneContent() {
        ScrollPane scrollPane = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
        assertEquals(new Dimension(100, 100), scrollPane.getViewportSize());
        assertEquals(Integer.valueOf(0), Integer.valueOf(scrollPane.getHScrollbarHeight()));
        assertEquals(Integer.valueOf(0), Integer.valueOf(scrollPane.getVScrollbarWidth()));
        assertThrowsNullPointer(scrollPane::getScrollPosition);
        scrollPane.add(new TestComponent(300, 200));
        scrollPane.doLayout();
        assertEquals(new Dimension(300, 200), scrollPane.getComponent(0).getSize());
        scrollPane.setScrollPosition(250, 150);
        assertEquals(new Point(200, 100), scrollPane.getScrollPosition());
        assertEquals(new Point(-200, -100), scrollPane.getComponent(0).getLocation());
        Adjustable horizontal = scrollPane.getHAdjustable();
        Adjustable vertical = scrollPane.getVAdjustable();
        assertEquals(Integer.valueOf(300), Integer.valueOf(horizontal.getMaximum()));
        assertEquals(Integer.valueOf(200), Integer.valueOf(vertical.getMaximum()));
        assertEquals(Integer.valueOf(100), Integer.valueOf(horizontal.getVisibleAmount()));
        assertEquals(Integer.valueOf(100), Integer.valueOf(vertical.getVisibleAmount()));
        assertThrowsAwtError(() -> horizontal.setMinimum(1));
        assertThrowsAwtError(() -> vertical.setVisibleAmount(10));
        scrollPane.add(new TestComponent(50, 40));
        assertEquals(Integer.valueOf(1), Integer.valueOf(scrollPane.getComponentCount()));
        scrollPane.doLayout();
        assertEquals(new Dimension(100, 100), scrollPane.getComponent(0).getSize());
        assertSame(AccessibleRole.SCROLL_PANE, scrollPane.getAccessibleContext().getAccessibleRole());
        assertThrowsAwtError(() -> scrollPane.setLayout(null));
    }

    public static void reportsAdjustmentChanges() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.add(new TestComponent(200, 200));
        scrollPane.doLayout();
        ScrollPaneAdjustable vertical = (ScrollPaneAdjustable) scrollPane.getVAdjustable();
        int[] events = new int[1];
        vertical.addAdjustmentListener(event -> {
            assertSame(vertical, event.getAdjustable());
            assertEquals(Integer.valueOf(AdjustmentEvent.TRACK), Integer.valueOf(event.getAdjustmentType()));
            events[0]++;
        });
        vertical.setValue(20);
        vertical.setValue(20);
        vertical.setValueIsAdjusting(true);
        assertEquals(Integer.valueOf(2), Integer.valueOf(events[0]));
        assertTrue(vertical.getValueIsAdjusting());
        assertEquals(Integer.valueOf(2), Integer.valueOf(vertical.getAdjustmentListeners().length));
        assertTrue(vertical.paramString().startsWith("vertical,[0..200],val=20,vis=100"));
    }

    public static void dispatchesScrollbarAdjustmentEvents() {
        Scrollbar scrollbar = new Scrollbar(Scrollbar.HORIZONTAL);
        int[] events = new int[1];
        scrollbar.addAdjustmentListener(event -> events[0]++);
        scrollbar.dispatchEvent(new AdjustmentEvent(scrollbar, AdjustmentEvent.ADJUSTMENT_VALUE_CHANGED, AdjustmentEvent.TRACK, 10));
        assertEquals(Integer.valueOf(1), Integer.valueOf(events[0]));
        assertEquals(Integer.valueOf(1), Integer.valueOf(scrollbar.getAdjustmentListeners().length));
    }

    private static void assertThrowsAwtError(Runnable runnable) {
        try {
            runnable.run();
        } catch (AWTError expected) {
            return;
        }
        throw new AssertionError("Expected AWTError");
    }

    private static void assertThrowsIllegalArgument(Runnable runnable) {
        try {
            runnable.run();
        } catch (IllegalArgumentException expected) {
            return;
        }
        throw new AssertionError("Expected IllegalArgumentException");
    }

    private static void assertThrowsNullPointer(Runnable runnable) {
        try {
            runnable.run();
        } catch (NullPointerException expected) {
            return;
        }
        throw new AssertionError("Expected NullPointerException");
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

    private static final class TestComponent extends Component {
        private TestComponent(int width, int height) {
            setPreferredSize(new Dimension(width, height));
        }
    }
}
