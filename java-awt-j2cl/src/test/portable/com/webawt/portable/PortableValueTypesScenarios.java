package com.webawt.portable;

import java.awt.BufferCapabilities;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Event;
import java.awt.GridBagConstraints;
import java.awt.GraphicsConfigTemplate;
import java.awt.GraphicsConfiguration;
import java.awt.ImageCapabilities;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.SystemColor;
import java.util.Locale;

public final class PortableValueTypesScenarios {
    private PortableValueTypesScenarios() {
    }

    public static void main(String[] arguments) {
        runAll();
    }

    public static void runAll() {
        copiesDimensionsWithoutSharingState();
        roundsFloatingPointDimensionsUpward();
        convertsSpecialFloatingPointDimensionsLikeOpenJdk();
        matchesDimensionValueContract();
        updatesAndClonesInsets();
        matchesInsetsValueContract();
        translatesAndMeasuresPoints();
        roundsPointLocationsLikeOpenJdk();
        matchesPointValueContract();
        describesBrowserDisplayAndCursorValues();
        exposesPortableOrientationAndImageCapabilities();
        describesBufferCapabilities();
        translatesLegacyEvents();
        managesRenderingHints();
        copiesGridBagConstraints();
        convertsAndTransformsColors();
        selectsGraphicsConfigurationsAndDescribesSystemColors();
    }

    private static void copiesDimensionsWithoutSharingState() {
        Dimension original = new Dimension(12, 34);
        Dimension copy = new Dimension(original);
        Dimension size = original.getSize();
        assertEquals(original, copy);
        assertEquals(original, size);
        assertNotSame(original, copy);
        assertNotSame(original, size);
    }

    private static void roundsFloatingPointDimensionsUpward() {
        Dimension dimension = new Dimension();
        dimension.setSize(1.1, -1.1);
        assertEquals(2, dimension.width);
        assertEquals(-1, dimension.height);
    }

    private static void convertsSpecialFloatingPointDimensionsLikeOpenJdk() {
        Dimension dimension = new Dimension();
        dimension.setSize(Double.NaN, Double.POSITIVE_INFINITY);
        assertEquals(0, dimension.width);
        assertEquals(Integer.MAX_VALUE, dimension.height);
    }

    private static void matchesDimensionValueContract() {
        Dimension dimension = new Dimension(2, 3);
        assertEquals(17, dimension.hashCode());
        assertEquals("java.awt.Dimension[width=2,height=3]", dimension.toString());
        assertEquals(dimension, dimension.clone());
        assertNotSame(dimension, dimension.clone());
    }

    private static void updatesAndClonesInsets() {
        Insets insets = new Insets(1, 2, 3, 4);
        Insets clone = (Insets) insets.clone();
        insets.set(5, 6, 7, 8);
        assertEquals(new Insets(1, 2, 3, 4), clone);
        assertEquals(new Insets(5, 6, 7, 8), insets);
        assertNotSame(insets, clone);
    }

    private static void matchesInsetsValueContract() {
        Insets insets = new Insets(1, 2, 3, 4);
        assertEquals(577, insets.hashCode());
        assertEquals("java.awt.Insets[top=1,left=2,bottom=3,right=4]", insets.toString());
    }

    private static void translatesAndMeasuresPoints() {
        Point point = new Point(3, 4);
        point.translate(5, -2);
        assertEquals(8, point.x);
        assertEquals(2, point.y);
        assertEquals(2.0, point.distance(8, 0));
    }

    private static void roundsPointLocationsLikeOpenJdk() {
        Point point = new Point();
        point.setLocation(1.5, -1.5);
        assertEquals(2, point.x);
        assertEquals(-1, point.y);
    }

    private static void matchesPointValueContract() {
        Point point = new Point(2, 3);
        Point location = point.getLocation();
        Point clone = (Point) point.clone();
        assertEquals(point, location);
        assertEquals(point, clone);
        assertNotSame(point, location);
        assertNotSame(point, clone);
        assertEquals("java.awt.Point[x=2,y=3]", point.toString());
    }

    private static void describesBrowserDisplayAndCursorValues() {
        DisplayMode mode = new DisplayMode(800, 600, 32, 60);
        assertEquals(800, mode.getWidth());
        assertEquals(600, mode.getHeight());
        assertEquals(2404, mode.hashCode());
        assertEquals("800x600x32bpp@60Hz", mode.toString());
        assertEquals(mode, new DisplayMode(800, 600, 32, 60));
        Cursor cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
        assertSame(cursor, Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        assertEquals("Hand Cursor", cursor.getName());
        assertEquals("java.awt.Cursor[Hand Cursor]", cursor.toString());
    }

    private static void exposesPortableOrientationAndImageCapabilities() {
        assertSame(ComponentOrientation.LEFT_TO_RIGHT, ComponentOrientation.getOrientation(Locale.US));
        assertTrue(ComponentOrientation.LEFT_TO_RIGHT.isHorizontal());
        assertTrue(ComponentOrientation.LEFT_TO_RIGHT.isLeftToRight());
        assertFalse(ComponentOrientation.RIGHT_TO_LEFT.isLeftToRight());
        ImageCapabilities capabilities = new ImageCapabilities(true);
        assertTrue(capabilities.isAccelerated());
        assertFalse(capabilities.isTrueVolatile());
        assertNotSame(capabilities, capabilities.clone());
    }

    private static void describesBufferCapabilities() {
        ImageCapabilities front = new ImageCapabilities(true);
        ImageCapabilities back = new ImageCapabilities(false);
        BufferCapabilities capabilities = new BufferCapabilities(front, back, BufferCapabilities.FlipContents.COPIED);
        assertSame(front, capabilities.getFrontBufferCapabilities());
        assertSame(back, capabilities.getBackBufferCapabilities());
        assertTrue(capabilities.isPageFlipping());
        assertFalse(capabilities.isFullScreenRequired());
        assertFalse(capabilities.isMultiBufferAvailable());
        assertEquals("copied", capabilities.getFlipContents().toString());
        assertEquals(3, capabilities.getFlipContents().hashCode());
        assertNotSame(capabilities, capabilities.clone());
    }

    private static void translatesLegacyEvents() {
        Event event = new Event("target", 123L, Event.MOUSE_DOWN, 4, 5, 65, Event.SHIFT_MASK | Event.CTRL_MASK, "arg");
        event.translate(2, -3);
        assertEquals(6, event.x);
        assertEquals(2, event.y);
        assertTrue(event.shiftDown());
        assertTrue(event.controlDown());
        assertFalse(event.metaDown());
        assertEquals("java.awt.Event[id=501,x=6,y=2,key=65,shift,control,target=target,arg=arg]", event.toString());
    }

    private static void managesRenderingHints() {
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        assertEquals(2, hints.size());
        assertSame(RenderingHints.VALUE_ANTIALIAS_ON, hints.get(RenderingHints.KEY_ANTIALIASING));
        RenderingHints clone = (RenderingHints) hints.clone();
        clone.remove(RenderingHints.KEY_INTERPOLATION);
        assertEquals(2, hints.size());
        assertEquals(1, clone.size());
        try {
            hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_RENDER_SPEED);
            throw new AssertionError("Expected incompatible rendering hint rejection");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("incompatible"));
        }
    }

    private static void copiesGridBagConstraints() {
        GridBagConstraints constraints = new GridBagConstraints();
        assertEquals(GridBagConstraints.RELATIVE, constraints.gridx);
        assertEquals(1, constraints.gridwidth);
        assertEquals(GridBagConstraints.CENTER, constraints.anchor);
        GridBagConstraints copy = (GridBagConstraints) constraints.clone();
        assertNotSame(constraints, copy);
        assertNotSame(constraints.insets, copy.insets);
        copy.insets.left = 4;
        assertEquals(0, constraints.insets.left);
    }

    private static void convertsAndTransformsColors() {
        Color color = new Color(0x11223344, true);
        assertEquals(0x22, color.getRed());
        assertEquals(0x33, color.getGreen());
        assertEquals(0x44, color.getBlue());
        assertEquals(0x11, color.getAlpha());
        assertEquals(0xffff0000, Color.HSBtoRGB(0.0f, 1.0f, 1.0f));
        float[] hsb = Color.RGBtoHSB(255, 0, 0, null);
        assertEquals(0.0, hsb[0]);
        assertEquals(1.0, hsb[1]);
        assertEquals(1.0, hsb[2]);
        assertEquals(new Color(255, 255, 255), Color.WHITE.brighter());
        assertEquals(new Color(178, 0, 0), Color.RED.darker());
    }

    private static void selectsGraphicsConfigurationsAndDescribesSystemColors() {
        GraphicsConfigTemplate template = new GraphicsConfigTemplate() {
            @Override
            public GraphicsConfiguration getBestConfiguration(GraphicsConfiguration[] configurations) {
                return configurations[0];
            }
            @Override
            public boolean isGraphicsConfigSupported(GraphicsConfiguration configuration) {
                return configuration == null;
            }
        };
        assertEquals(1, GraphicsConfigTemplate.REQUIRED);
        assertEquals(2, GraphicsConfigTemplate.PREFERRED);
        assertEquals(3, GraphicsConfigTemplate.UNNECESSARY);
        assertSame(null, template.getBestConfiguration(new GraphicsConfiguration[] {null}));
        assertTrue(template.isGraphicsConfigSupported(null));
        assertEquals(26, SystemColor.NUM_COLORS);
        assertEquals("java.awt.SystemColor[i=1]", SystemColor.activeCaption.toString());
    }

    private static void assertEquals(int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError("Expected " + expected + " but was " + actual);
        }
    }

    private static void assertEquals(double expected, double actual) {
        if (Double.compare(expected, actual) != 0) {
            throw new AssertionError("Expected " + expected + " but was " + actual);
        }
    }

    private static void assertEquals(Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected " + expected + " but was " + actual);
        }
    }

    private static void assertNotSame(Object expected, Object actual) {
        if (expected == actual) {
            throw new AssertionError("Expected distinct objects");
        }
    }

    private static void assertSame(Object expected, Object actual) {
        if (expected != actual) {
            throw new AssertionError("Expected same object");
        }
    }

    private static void assertTrue(boolean condition) {
        if (!condition) {
            throw new AssertionError("Expected true");
        }
    }

    private static void assertFalse(boolean condition) {
        if (condition) {
            throw new AssertionError("Expected false");
        }
    }
}
