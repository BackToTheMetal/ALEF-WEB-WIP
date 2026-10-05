package com.webawt.portable;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsConfigTemplate;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.ImageCapabilities;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.Transparency;
import java.awt.Window;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.util.Locale;
import org.junit.Test;
import org.webawt.internal.peer.DisplayMetricsProvider;
import org.webawt.internal.peer.WebAwtRuntime;

public final class GraphicsEnvironmentJ2clTest {
    @Test
    public void exposesSingleBrowserScreenUsingRuntimeMetrics() {
        installMetrics(800, 600, 2.0);
        GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = environment.getDefaultScreenDevice();
        GraphicsConfiguration configuration = device.getDefaultConfiguration();
        assertFalse(GraphicsEnvironment.isHeadless());
        assertFalse(environment.isHeadlessInstance());
        assertEquals(1, environment.getScreenDevices().length);
        assertSame(device, environment.getScreenDevices()[0]);
        assertEquals(GraphicsDevice.TYPE_RASTER_SCREEN, device.getType());
        assertEquals("webawt-screen-0", device.getIDstring());
        assertEquals(new Rectangle(0, 0, 800, 600), configuration.getBounds());
        AffineTransform transform = configuration.getDefaultTransform();
        assertEquals(2.0, transform.getScaleX());
        assertEquals(2.0, transform.getScaleY());
        assertTrue(configuration.getNormalizingTransform().isIdentity());
        assertEquals(new Point(400, 300), environment.getCenterPoint());
        assertEquals(new Rectangle(0, 0, 800, 600), environment.getMaximumWindowBounds());
        resetMetrics();
    }

    @Test
    public void createsCompatibleRasterAndVolatileImages() throws AWTException {
        GraphicsConfiguration configuration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        BufferedImage opaque = configuration.createCompatibleImage(4, 3);
        BufferedImage translucent = configuration.createCompatibleImage(4, 3, Transparency.TRANSLUCENT);
        assertEquals(Transparency.OPAQUE, opaque.getTransparency());
        assertEquals(Transparency.TRANSLUCENT, translucent.getTransparency());
        assertEquals(Transparency.OPAQUE, configuration.getColorModel(Transparency.OPAQUE).getTransparency());
        assertEquals(Transparency.TRANSLUCENT, configuration.getColorModel(Transparency.TRANSLUCENT).getTransparency());
        assertSame(null, configuration.getColorModel(99));
        assertTrue(configuration.isTranslucencyCapable());
        VolatileImage image = configuration.createCompatibleVolatileImage(5, 4, Transparency.TRANSLUCENT);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(Color.RED);
        graphics.fillRect(0, 0, 2, 2);
        graphics.dispose();
        assertEquals(5, image.getWidth());
        assertEquals(4, image.getHeight());
        assertEquals(0xffff0000, image.getSnapshot().getRGB(0, 0));
        assertEquals(VolatileImage.IMAGE_OK, image.validate(configuration));
        assertFalse(image.contentsLost());
        assertFalse(image.getCapabilities().isAccelerated());
        assertEquals(Transparency.TRANSLUCENT, image.getTransparency());
        assertThrowsAwt(() -> configuration.createCompatibleVolatileImage(2, 2, new TrueVolatileCapabilities()));
        assertThrowsIllegalArgument(() -> configuration.createCompatibleImage(1, 1, 99));
    }

    @Test
    public void modelsDisplayCapabilitiesAndSimulatedFullScreen() {
        installMetrics(640, 480, 1.0);
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        DisplayMode mode = device.getDisplayMode();
        assertEquals(640, mode.getWidth());
        assertEquals(480, mode.getHeight());
        assertEquals(1, device.getDisplayModes().length);
        assertFalse(device.isDisplayChangeSupported());
        assertFalse(device.isFullScreenSupported());
        assertEquals(-1, device.getAvailableAcceleratedMemory());
        assertFalse(device.isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.PERPIXEL_TRANSPARENT));
        assertTrue(device.isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.TRANSLUCENT));
        assertTrue(device.isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.PERPIXEL_TRANSLUCENT));
        Window window = new Window((Window) null);
        window.setBounds(10, 20, 200, 100);
        device.setFullScreenWindow(window);
        assertSame(window, device.getFullScreenWindow());
        assertEquals(new Rectangle(0, 0, 640, 480), window.getBounds());
        device.setFullScreenWindow(null);
        assertSame(null, device.getFullScreenWindow());
        assertEquals(new Rectangle(10, 20, 200, 100), window.getBounds());
        window.dispose();
        assertThrowsUnsupported(() -> device.setDisplayMode(mode));
        resetMetrics();
    }

    @Test
    public void delegatesConfigurationSelectionAndManagesFonts() {
        GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = environment.getDefaultScreenDevice();
        GraphicsConfiguration expected = device.getDefaultConfiguration();
        GraphicsConfigTemplate template = new GraphicsConfigTemplate() {
            @Override
            public GraphicsConfiguration getBestConfiguration(GraphicsConfiguration[] configurations) {
                return configurations.length == 0 ? null : configurations[0];
            }
            @Override
            public boolean isGraphicsConfigSupported(GraphicsConfiguration configuration) {
                return configuration == expected;
            }
        };
        assertSame(expected, device.getBestConfiguration(template));
        assertTrue(template.isGraphicsConfigSupported(expected));
        assertTrue(environment.getAvailableFontFamilyNames().length >= 5);
        assertTrue(environment.getAvailableFontFamilyNames(Locale.ENGLISH).length >= 5);
        assertEquals(environment.getAvailableFontFamilyNames().length, environment.getAllFonts().length);
        Font custom = new Font("WebAwt Test Family", Font.PLAIN, 12);
        assertTrue(environment.registerFont(custom));
        assertFalse(environment.registerFont(custom));
        assertFalse(environment.registerFont(new Font(Font.DIALOG, Font.PLAIN, 12)));
        environment.preferLocaleFonts();
        environment.preferProportionalFonts();
        assertEquals(Font.SANS_SERIF, environment.getAvailableFontFamilyNames()[0]);
        BufferedImage image = new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = environment.createGraphics(image);
        graphics.setColor(Color.BLUE);
        graphics.fillRect(0, 0, 1, 1);
        graphics.dispose();
        assertEquals(0xff0000ff, image.getRGB(0, 0));
    }

    @Test
    public void exposesStableSystemColors() {
        assertEquals(26, SystemColor.NUM_COLORS);
        assertEquals(0xff000080, SystemColor.activeCaption.getRGB());
        assertEquals("java.awt.SystemColor[i=1]", SystemColor.activeCaption.toString());
        assertSame(SystemColor.control, SystemColor.control);
    }

    private static void installMetrics(int width, int height, double scale) {
        WebAwtRuntime.installDisplayMetricsProvider(new FixedDisplayMetricsProvider(width, height, scale));
    }

    private static void resetMetrics() {
        installMetrics(1024, 768, 1.0);
    }

    private static void assertThrowsAwt(AwtAction action) {
        try {
            action.run();
        } catch (AWTException expected) {
            return;
        }
        throw new AssertionError("Expected AWTException");
    }

    private static void assertThrowsIllegalArgument(Runnable action) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            return;
        }
        throw new AssertionError("Expected IllegalArgumentException");
    }

    private static void assertThrowsUnsupported(Runnable action) {
        try {
            action.run();
        } catch (UnsupportedOperationException expected) {
            return;
        }
        throw new AssertionError("Expected UnsupportedOperationException");
    }

    private static void assertEquals(Object expected, Object actual) {
        if (expected == null ? actual != null : !expected.equals(actual)) {
            throw new AssertionError("Expected " + expected + " but was " + actual);
        }
    }

    private static void assertEquals(int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError("Expected " + expected + " but was " + actual);
        }
    }

    private static void assertEquals(double expected, double actual) {
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

    private interface AwtAction {
        void run() throws AWTException;
    }

    private static final class FixedDisplayMetricsProvider implements DisplayMetricsProvider {
        private final int width;
        private final int height;
        private final double scale;

        private FixedDisplayMetricsProvider(int width, int height, double scale) {
            this.width = width;
            this.height = height;
            this.scale = scale;
        }

        @Override
        public int getViewportWidth() {
            return width;
        }

        @Override
        public int getViewportHeight() {
            return height;
        }

        @Override
        public double getDeviceScaleFactor() {
            return scale;
        }
    }

    private static final class TrueVolatileCapabilities extends ImageCapabilities {
        private TrueVolatileCapabilities() {
            super(true);
        }

        @Override
        public boolean isTrueVolatile() {
            return true;
        }
    }
}
