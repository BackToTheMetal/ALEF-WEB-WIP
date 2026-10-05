package com.webawt.portable;

import java.awt.AWTEvent;
import java.awt.Button;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.MouseDragGestureRecognizer;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.util.Locale;
import java.net.URL;
import org.junit.Test;
import org.webawt.internal.peer.DisplayMetricsProvider;
import org.webawt.internal.peer.ImageDecodeCallback;
import org.webawt.internal.peer.WebAwtRuntime;

public final class ToolkitJ2clTest {
    @Test
    public void dispatchesProtectedToolkitQueueAndPropertyHooks() {
        MutableToolkit toolkit = new MutableToolkit();
        assertSame(toolkit.queue, toolkit.getSystemEventQueue());
        assertEquals(Boolean.TRUE, toolkit.getDesktopProperty("initialized"));
        assertEquals("loaded:custom", toolkit.getDesktopProperty("custom"));
        assertEquals("loaded:custom", toolkit.getDesktopProperty("custom"));
        assertEquals(1, toolkit.propertyLoads);
    }

    @Test
    public void exposesBrowserScreenFontsAndCapabilities() {
        installMetrics(900, 700, 1.5);
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        assertSame(toolkit, Toolkit.getDefaultToolkit());
        assertEquals(new Dimension(900, 700), toolkit.getScreenSize());
        assertEquals(96, toolkit.getScreenResolution());
        assertEquals(ColorModel.getRGBdefault(), toolkit.getColorModel());
        assertTrue(toolkit.getFontList().length >= 5);
        Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
        FontMetrics metrics = toolkit.getFontMetrics(font);
        assertSame(font, metrics.getFont());
        assertTrue(metrics.stringWidth("WebAWT") > 0);
        GraphicsConfiguration configuration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        assertEquals(new Insets(0, 0, 0, 0), toolkit.getScreenInsets(configuration));
        assertTrue(toolkit.isAlwaysOnTopSupported());
        assertTrue(toolkit.areExtraMouseButtonsEnabled());
        assertTrue(toolkit.isFrameStateSupported(Frame.MAXIMIZED_BOTH | Frame.ICONIFIED));
        assertFalse(toolkit.isFrameStateSupported(16));
        assertTrue(toolkit.isModalityTypeSupported(Dialog.ModalityType.APPLICATION_MODAL));
        assertFalse(toolkit.isModalityTypeSupported(Dialog.ModalityType.TOOLKIT_MODAL));
        assertTrue(toolkit.isModalExclusionTypeSupported(Dialog.ModalExclusionType.APPLICATION_EXCLUDE));
        assertFalse(toolkit.isModalExclusionTypeSupported(Dialog.ModalExclusionType.TOOLKIT_EXCLUDE));
        resetMetrics();
    }

    @Test
    public void managesDynamicLayoutDesktopPropertiesAndBeepProvider() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        toolkit.setDynamicLayout(true);
        assertTrue(toolkit.isDynamicLayoutActive());
        assertEquals(Boolean.TRUE, toolkit.getDesktopProperty("awt.dynamicLayoutSupported"));
        assertEquals(Integer.valueOf(3), toolkit.getDesktopProperty("awt.mouse.numButtons"));
        assertEquals("fallback", Toolkit.getProperty("missing", "fallback"));
        MutableToolkit mutable = new MutableToolkit();
        int[] changes = new int[1];
        mutable.addPropertyChangeListener("theme", event -> changes[0]++);
        mutable.updateProperty("theme", "dark");
        assertEquals("dark", mutable.getDesktopProperty("theme"));
        assertEquals(1, changes[0]);
        assertEquals(1, mutable.getPropertyChangeListeners("theme").length);
        int[] beeps = new int[1];
        WebAwtRuntime.installToolkitProvider(() -> beeps[0]++);
        toolkit.beep();
        assertEquals(1, beeps[0]);
        WebAwtRuntime.installToolkitProvider(() -> {
        });
    }

    @Test
    public void exposesApplicationClipboardAndDefaultDragRecognizer() throws Exception {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Clipboard clipboard = toolkit.getSystemClipboard();
        StringSelection selection = new StringSelection("WebAWT clipboard");
        clipboard.setContents(selection, selection);
        assertSame(clipboard, toolkit.getSystemClipboard());
        assertEquals("WebAWT clipboard", clipboard.getData(DataFlavor.stringFlavor));
        assertSame(null, toolkit.getSystemSelection());
        DragGestureRecognizer recognizer = toolkit.createDragGestureRecognizer(MouseDragGestureRecognizer.class, DragSource.getDefaultDragSource(), new Button("Drag"), DnDConstants.ACTION_COPY, event -> {
        });
        assertTrue(recognizer instanceof MouseDragGestureRecognizer);
        assertTrue(toolkit.createImage(new URL("https://example.invalid/image.png")) != null);
        Image cached = toolkit.getImage(new URL("https://example.invalid/image.png"));
        assertSame(cached, toolkit.getImage(new URL("https://example.invalid/image.png")));
    }

    @Test
    public void createsProducerImagesAndReportsObserverState() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        int[] pixels = {0xffff0000, 0xff00ff00, 0xff0000ff, 0xffffffff};
        Image image = toolkit.createImage(new MemoryImageSource(2, 2, pixels, 0, 2));
        assertEquals(2, image.getWidth(null));
        assertEquals(2, image.getHeight(null));
        assertTrue(toolkit.prepareImage(image, 2, 2, null));
        assertEquals(ImageObserver.ALLBITS, toolkit.checkImage(image, 2, 2, null));
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.BLACK);
        graphics.fillRect(0, 0, 1, 1);
        graphics.dispose();
        BufferedImage cursorImage = new BufferedImage(4, 4, BufferedImage.TYPE_INT_ARGB);
        Cursor cursor = toolkit.createCustomCursor(cursorImage, new Point(1, 2), "test cursor");
        assertEquals(Cursor.CUSTOM_CURSOR, cursor.getType());
        assertEquals("test cursor", cursor.getName());
        assertEquals(new Dimension(24, 36), toolkit.getBestCursorSize(24, 36));
        assertEquals(1 << 24, toolkit.getMaximumCursorColors());
    }

    @Test
    public void decodesStringAndByteImagesThroughBrowserProvider() throws Exception {
        String[] decodedSource = new String[1];
        WebAwtRuntime.installImageDecoder((source, callback) -> {
            decodedSource[0] = source;
            callback.onDecoded(2, 1, new int[] {0xff123456, 0xffabcdef});
        });
        try {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Image created = toolkit.createImage("memory:created");
            assertEquals(2, created.getWidth(null));
            assertEquals(1, created.getHeight(null));
            assertEquals(ImageObserver.ALLBITS, toolkit.checkImage(created, -1, -1, null));
            PixelGrabber grabber = new PixelGrabber(created, 0, 0, 2, 1, true);
            assertTrue(grabber.grabPixels());
            int[] pixels = (int[]) grabber.getPixels();
            assertEquals(0xff123456, pixels[0]);
            assertEquals(0xffabcdef, pixels[1]);
            Image cached = toolkit.getImage("memory:cached");
            assertSame(cached, toolkit.getImage("memory:cached"));
            assertTrue(cached != toolkit.createImage("memory:cached"));
            byte[] png = {0, (byte) 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a, 0};
            Image bytes = toolkit.createImage(png, 1, 8);
            assertTrue(decodedSource[0].startsWith("data:image/png;base64,"));
            assertEquals(2, bytes.getWidth(null));
            WebAwtRuntime.installImageDecoder((source, callback) -> callback.onError());
            Image failed = toolkit.createImage("memory:failed");
            assertEquals(ImageObserver.ERROR, toolkit.checkImage(failed, -1, -1, null));
        } finally {
            WebAwtRuntime.installImageDecoder((source, callback) -> callback.onError());
        }
    }

    @Test
    public void notifiesObserversBeforeADeferredImageCanBeDrawn() {
        ImageDecodeCallback[] pendingDecode = new ImageDecodeCallback[1];
        WebAwtRuntime.installImageDecoder((source, callback) -> pendingDecode[0] = callback);
        try {
            Image pending = Toolkit.getDefaultToolkit().createImage("memory:deferred");
            BufferedImage destination = new BufferedImage(2, 1, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = destination.createGraphics();
            int[] updates = new int[1];
            ImageObserver observer = (image, flags, x, y, width, height) -> {
                updates[0]++;
                return true;
            };
            assertFalse(graphics.drawImage(pending, 0, 0, observer));
            pendingDecode[0].onDecoded(1, 1, new int[] {0xff2468ac});
            assertTrue(updates[0] >= 2);
            assertTrue(graphics.drawImage(pending, 0, 0, observer));
            assertEquals(0xff2468ac, destination.getRGB(0, 0));
            graphics.dispose();
        } finally {
            WebAwtRuntime.installImageDecoder((source, callback) -> callback.onError());
        }
    }

    @Test
    public void prepareImageRetainsObserversForDeferredCompletionAndFailure() {
        ImageDecodeCallback[] pendingDecode = new ImageDecodeCallback[1];
        WebAwtRuntime.installImageDecoder((source, callback) -> pendingDecode[0] = callback);
        try {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            int[] flagsSeen = new int[1];
            ImageObserver observer = (image, flags, x, y, width, height) -> {
                flagsSeen[0] |= flags;
                return (flags & (ImageObserver.ALLBITS | ImageObserver.ERROR)) == 0;
            };
            Image image = toolkit.createImage("memory:prepare-success");
            assertFalse(toolkit.prepareImage(image, -1, -1, observer));
            assertEquals(0, flagsSeen[0]);
            pendingDecode[0].onDecoded(1, 1, new int[] {0xff123456});
            assertTrue((flagsSeen[0] & ImageObserver.ALLBITS) != 0);
            flagsSeen[0] = 0;
            Image failed = toolkit.createImage("memory:prepare-failure");
            assertFalse(toolkit.prepareImage(failed, -1, -1, observer));
            pendingDecode[0].onError();
            assertTrue((flagsSeen[0] & ImageObserver.ERROR) != 0);
        } finally {
            WebAwtRuntime.installImageDecoder((source, callback) -> callback.onError());
        }
    }

    @Test
    public void dispatchesGlobalAwtListenersByMask() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Button button = new Button("Run");
        int[] calls = new int[2];
        AWTEventListener actionListener = event -> calls[0]++;
        AWTEventListener keyListener = event -> calls[1]++;
        toolkit.addAWTEventListener(actionListener, AWTEvent.ACTION_EVENT_MASK);
        toolkit.addAWTEventListener(keyListener, AWTEvent.KEY_EVENT_MASK);
        try {
            button.dispatchEvent(new ActionEvent(button, ActionEvent.ACTION_PERFORMED, "run"));
            button.dispatchEvent(new KeyEvent(button, KeyEvent.KEY_PRESSED, 1L, 0, KeyEvent.VK_A, 'a'));
            assertEquals(1, calls[0]);
            assertEquals(1, calls[1]);
            assertEquals(2, toolkit.getAWTEventListeners().length);
            assertEquals(1, toolkit.getAWTEventListeners(AWTEvent.KEY_EVENT_MASK).length);
            assertTrue(toolkit.getSystemEventQueue() == toolkit.getSystemEventQueue());
        } finally {
            toolkit.removeAWTEventListener(actionListener);
            toolkit.removeAWTEventListener(keyListener);
        }
    }

    @Test
    public void connectsComponentsImagesAndGraphicsToEnvironment() throws Exception {
        Button button = new Button("Environment");
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        assertSame(toolkit, button.getToolkit());
        assertSame(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration(), button.getGraphicsConfiguration());
        assertEquals(3, button.createVolatileImage(3, 2).getWidth());
        Image image = button.createImage(4, 3);
        assertEquals(4, image.getWidth(null));
        assertEquals(3, image.getHeight(null));
        assertEquals(Transparency.TRANSLUCENT, ((BufferedImage) image).getTransparency());
        assertTrue(button.prepareImage(image, null));
        assertEquals(ImageObserver.ALLBITS, button.checkImage(image, null));
    }

    @Test
    public void paintsAndPrintsVisibleChildHierarchy() {
        ShowingPanel panel = new ShowingPanel();
        PaintComponent child = new PaintComponent();
        panel.setSize(20, 20);
        child.setBounds(2, 3, 5, 4);
        panel.add(child);
        BufferedImage image = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        panel.paintComponents(graphics);
        assertEquals(1, child.paintCalls);
        assertEquals(0xffff0000, image.getRGB(2, 3));
        panel.print(graphics);
        assertEquals(2, child.paintCalls);
        graphics.dispose();
    }

    private static void installMetrics(int width, int height, double scale) {
        WebAwtRuntime.installDisplayMetricsProvider(new FixedDisplayMetricsProvider(width, height, scale));
    }

    private static void resetMetrics() {
        installMetrics(1024, 768, 1.0);
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

    private static final class MutableToolkit extends Toolkit {
        private final java.awt.EventQueue queue = new java.awt.EventQueue();
        private int propertyLoads;
        protected java.awt.EventQueue getSystemEventQueueImpl() { return queue; }
        protected void initializeDesktopProperties() { desktopProperties.put("initialized", Boolean.TRUE); }
        protected Object lazilyLoadDesktopProperty(String name) { propertyLoads++; return "loaded:" + name; }

        private final Clipboard clipboard = new Clipboard("test");

        @Override
        public Clipboard getSystemClipboard() {
            return clipboard;
        }

        @Override
        public Dimension getScreenSize() {
            return new Dimension(1, 1);
        }

        @Override
        public int getScreenResolution() {
            return 96;
        }

        @Override
        public ColorModel getColorModel() {
            return ColorModel.getRGBdefault();
        }

        @Override
        public String[] getFontList() {
            return new String[] {Font.DIALOG};
        }

        @Override
        public FontMetrics getFontMetrics(Font font) {
            return new FontMetrics(font) {
            };
        }

        @Override
        public void sync() {
        }

        @Override
        public boolean prepareImage(Image image, int width, int height, ImageObserver observer) {
            return false;
        }

        @Override
        public int checkImage(Image image, int width, int height, ImageObserver observer) {
            return 0;
        }

        @Override
        public Image getImage(String source) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Image getImage(URL source) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Image createImage(String source) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Image createImage(URL source) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Image createImage(ImageProducer producer) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Image createImage(byte[] imageData, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void beep() {
        }

        @Override
        public boolean isModalityTypeSupported(Dialog.ModalityType modalityType) {
            return true;
        }

        @Override
        public boolean isModalExclusionTypeSupported(Dialog.ModalExclusionType modalExclusionType) {
            return true;
        }

        @Override
        public java.util.Map<java.awt.font.TextAttribute, ?> mapInputMethodHighlight(java.awt.im.InputMethodHighlight highlight) {
            return java.util.Collections.emptyMap();
        }

        private void updateProperty(String propertyName, Object value) {
            setDesktopProperty(propertyName, value);
        }
    }

    private static final class ShowingPanel extends Panel {
        private ShowingPanel() {
            super(null);
        }

        @Override
        public boolean isShowing() {
            return true;
        }
    }

    private static final class PaintComponent extends java.awt.Component {
        private int paintCalls;

        @Override
        public boolean isShowing() {
            return true;
        }

        @Override
        public void paint(java.awt.Graphics graphics) {
            paintCalls++;
            graphics.setColor(Color.RED);
            graphics.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}
