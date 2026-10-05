package java.awt;

import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ContainerEvent;
import java.awt.event.FocusEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.InputEvent;
import java.awt.event.InputMethodEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.PaintEvent;
import java.awt.event.TextEvent;
import java.awt.event.WindowEvent;
import java.awt.datatransfer.Clipboard;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.font.TextAttribute;
import java.awt.im.InputMethodHighlight;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.webawt.internal.peer.WebAwtRuntime;

public abstract class Toolkit {
    private static final Toolkit DEFAULT_TOOLKIT = new BrowserToolkit();
    static {
        WebAwtRuntime.addShutdownHook(() -> {
            DEFAULT_TOOLKIT.eventQueue = null;
            DEFAULT_TOOLKIT.systemClipboard = null;
            DEFAULT_TOOLKIT.eventListeners.clear();
            for (PropertyChangeListener listener : DEFAULT_TOOLKIT.desktopPropsSupport.getPropertyChangeListeners()) {
                DEFAULT_TOOLKIT.desktopPropsSupport.removePropertyChangeListener(listener);
            }
        });
    }
    private EventQueue eventQueue;
    private Clipboard systemClipboard;
    protected final Map<String, Object> desktopProperties = new HashMap<>();
    protected final PropertyChangeSupport desktopPropsSupport = new PropertyChangeSupport(this);
    private final List<EventListenerRegistration> eventListeners = new ArrayList<>();
    private boolean dynamicLayout;
    private boolean desktopPropertiesInitialized;

    protected Toolkit() {
        desktopProperties.put("awt.dynamicLayoutSupported", Boolean.TRUE);
        desktopProperties.put("awt.mouse.numButtons", Integer.valueOf(3));
        desktopProperties.put("awt.multiClickInterval", Integer.valueOf(500));
    }

    public void setDynamicLayout(boolean dynamic) throws HeadlessException {
        dynamicLayout = dynamic;
    }

    public boolean isDynamicLayoutActive() throws HeadlessException {
        return dynamicLayout;
    }

    public abstract Dimension getScreenSize() throws HeadlessException;

    public abstract int getScreenResolution() throws HeadlessException;

    public Insets getScreenInsets(GraphicsConfiguration configuration) throws HeadlessException {
        if (configuration == null) {
            throw new NullPointerException("configuration");
        }
        return new Insets(0, 0, 0, 0);
    }

    public abstract ColorModel getColorModel() throws HeadlessException;

    public abstract String[] getFontList();

    public abstract FontMetrics getFontMetrics(Font font);

    public abstract void sync();

    public static synchronized Toolkit getDefaultToolkit() {
        return DEFAULT_TOOLKIT;
    }

    public abstract Image getImage(String source);

    public abstract Image getImage(URL source);

    public abstract Image createImage(String source);

    public abstract Image createImage(URL source);

    public abstract boolean prepareImage(Image image, int width, int height, ImageObserver observer);

    public abstract int checkImage(Image image, int width, int height, ImageObserver observer);

    public abstract Image createImage(ImageProducer producer);

    public Image createImage(byte[] imageData) {
        return createImage(imageData, 0, imageData.length);
    }

    public abstract Image createImage(byte[] imageData, int offset, int length);

    public abstract void beep();

    public int getMenuShortcutKeyMask() throws HeadlessException {
        return getMenuShortcutKeyMaskEx() == InputEvent.META_DOWN_MASK ? InputEvent.META_MASK : InputEvent.CTRL_MASK;
    }

    public int getMenuShortcutKeyMaskEx() throws HeadlessException {
        return WebAwtRuntime.getMenuShortcutKeyMaskEx();
    }

    public boolean getLockingKeyState(int keyCode) throws UnsupportedOperationException {
        validateLockingKey(keyCode);
        throw new UnsupportedOperationException("Unavailable in browser");
    }

    public void setLockingKeyState(int keyCode, boolean enabled) throws UnsupportedOperationException {
        validateLockingKey(keyCode);
        throw new UnsupportedOperationException("Unavailable in browser");
    }

    public Cursor createCustomCursor(Image cursor, Point hotSpot, String name) throws IndexOutOfBoundsException, HeadlessException {
        if (cursor == null || hotSpot == null || name == null) {
            throw new NullPointerException("cursor, hotSpot, or name");
        }
        int width = cursor.getWidth(null);
        int height = cursor.getHeight(null);
        if (hotSpot.x < 0 || hotSpot.y < 0 || hotSpot.x >= width || hotSpot.y >= height) {
            throw new IndexOutOfBoundsException("hotSpot outside image");
        }
        return new Cursor(name);
    }

    public Dimension getBestCursorSize(int preferredWidth, int preferredHeight) throws HeadlessException {
        if (preferredWidth < 0 || preferredHeight < 0) {
            throw new IllegalArgumentException("negative cursor size");
        }
        return new Dimension(preferredWidth, preferredHeight);
    }

    public int getMaximumCursorColors() throws HeadlessException {
        return 1 << 24;
    }

    public boolean isFrameStateSupported(int state) throws HeadlessException {
        return (state & ~(Frame.ICONIFIED | Frame.MAXIMIZED_BOTH)) == 0;
    }

    public static String getProperty(String key, String defaultValue) {
        if (key == null) {
            throw new NullPointerException("key");
        }
        return defaultValue;
    }

    public final EventQueue getSystemEventQueue() { return getSystemEventQueueImpl(); }

    protected abstract EventQueue getSystemEventQueueImpl();

    final EventQueue applicationEventQueue() {
        if (eventQueue == null) { eventQueue = new EventQueue(); }
        return eventQueue;
    }

    protected boolean isDynamicLayoutSet() throws HeadlessException { return dynamicLayout; }
    protected void loadSystemColors(int[] colors) throws HeadlessException {
        for (int index = 0; index < colors.length; index++) { colors[index] = WebAwtRuntime.getSystemColor(index, colors[index]); }
    }
    protected Object lazilyLoadDesktopProperty(String name) { return null; }
    protected void initializeDesktopProperties() {}


    public abstract Clipboard getSystemClipboard() throws HeadlessException;

    public Clipboard getSystemSelection() throws HeadlessException {
        return null;
    }

    public <T extends DragGestureRecognizer> T createDragGestureRecognizer(Class<T> recognizerClass, DragSource dragSource, Component component, int actions, DragGestureListener listener) {
        if (dragSource == null) {
            throw new NullPointerException("dragSource");
        }
        return dragSource.createDragGestureRecognizer(recognizerClass, component, actions, listener);
    }

    final Clipboard applicationClipboard() {
        if (systemClipboard == null) {
            systemClipboard = new Clipboard("WebAWT");
        }
        return systemClipboard;
    }

    public final synchronized Object getDesktopProperty(String propertyName) {
        if (propertyName == null) {
            throw new NullPointerException("propertyName");
        }
        if (!desktopPropertiesInitialized) {
            desktopPropertiesInitialized = true;
            initializeDesktopProperties();
        }
        if (!desktopProperties.containsKey(propertyName)) {
            Object value = lazilyLoadDesktopProperty(propertyName);
            if (value != null) { desktopProperties.put(propertyName, value); }
        }
        return desktopProperties.get(propertyName);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        desktopPropsSupport.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        desktopPropsSupport.removePropertyChangeListener(propertyName, listener);
    }

    public PropertyChangeListener[] getPropertyChangeListeners() {
        return desktopPropsSupport.getPropertyChangeListeners();
    }

    public PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
        return desktopPropsSupport.getPropertyChangeListeners(propertyName);
    }

    public boolean isAlwaysOnTopSupported() {
        return true;
    }

    public abstract boolean isModalityTypeSupported(Dialog.ModalityType modalityType);

    public abstract boolean isModalExclusionTypeSupported(Dialog.ModalExclusionType modalExclusionType);

    public abstract Map<TextAttribute, ?> mapInputMethodHighlight(InputMethodHighlight highlight) throws HeadlessException;

    public void addAWTEventListener(AWTEventListener listener, long eventMask) {
        if (listener == null) {
            return;
        }
        eventListeners.add(new EventListenerRegistration(listener, eventMask));
    }

    public void removeAWTEventListener(AWTEventListener listener) {
        for (int index = eventListeners.size() - 1; index >= 0; index--) {
            if (eventListeners.get(index).listener == listener) {
                eventListeners.remove(index);
            }
        }
    }

    public AWTEventListener[] getAWTEventListeners() {
        AWTEventListener[] listeners = new AWTEventListener[eventListeners.size()];
        for (int index = 0; index < listeners.length; index++) {
            listeners[index] = eventListeners.get(index).listener;
        }
        return listeners;
    }

    public AWTEventListener[] getAWTEventListeners(long eventMask) {
        List<AWTEventListener> matching = new ArrayList<>();
        for (EventListenerRegistration registration : eventListeners) {
            if ((registration.eventMask & eventMask) == eventMask) {
                matching.add(registration.listener);
            }
        }
        return matching.toArray(new AWTEventListener[0]);
    }

    public boolean areExtraMouseButtonsEnabled() throws HeadlessException {
        return true;
    }

    protected final void setDesktopProperty(String propertyName, Object value) {
        Object previous;
        synchronized (this) {
            previous = desktopProperties.put(propertyName, value);
        }
        desktopPropsSupport.firePropertyChange(propertyName, previous, value);
    }

    static void notifyAWTEventListeners(AWTEvent event) {
        DEFAULT_TOOLKIT.notifyEventListeners(event);
    }

    private void notifyEventListeners(AWTEvent event) {
        long mask = eventMask(event);
        List<EventListenerRegistration> snapshot = new ArrayList<>(eventListeners);
        for (EventListenerRegistration registration : snapshot) {
            if ((registration.eventMask & mask) != 0) {
                registration.listener.eventDispatched(event);
            }
        }
    }

    private static long eventMask(AWTEvent event) {
        if (event instanceof WindowEvent) {
            int id = event.getID();
            if (id == WindowEvent.WINDOW_STATE_CHANGED) {
                return AWTEvent.WINDOW_STATE_EVENT_MASK;
            }
            if (id == WindowEvent.WINDOW_GAINED_FOCUS || id == WindowEvent.WINDOW_LOST_FOCUS) {
                return AWTEvent.WINDOW_FOCUS_EVENT_MASK;
            }
            return AWTEvent.WINDOW_EVENT_MASK;
        }
        if (event instanceof MouseWheelEvent) {
            return AWTEvent.MOUSE_WHEEL_EVENT_MASK;
        }
        if (event instanceof MouseEvent) {
            int id = event.getID();
            return id == MouseEvent.MOUSE_MOVED || id == MouseEvent.MOUSE_DRAGGED ? AWTEvent.MOUSE_MOTION_EVENT_MASK : AWTEvent.MOUSE_EVENT_MASK;
        }
        if (event instanceof HierarchyEvent) {
            int id = event.getID();
            return id == HierarchyEvent.ANCESTOR_MOVED || id == HierarchyEvent.ANCESTOR_RESIZED ? AWTEvent.HIERARCHY_BOUNDS_EVENT_MASK : AWTEvent.HIERARCHY_EVENT_MASK;
        }
        if (event instanceof FocusEvent) {
            return AWTEvent.FOCUS_EVENT_MASK;
        }
        if (event instanceof KeyEvent) {
            return AWTEvent.KEY_EVENT_MASK;
        }
        if (event instanceof ActionEvent) {
            return AWTEvent.ACTION_EVENT_MASK;
        }
        if (event instanceof AdjustmentEvent) {
            return AWTEvent.ADJUSTMENT_EVENT_MASK;
        }
        if (event instanceof ItemEvent) {
            return AWTEvent.ITEM_EVENT_MASK;
        }
        if (event instanceof TextEvent) {
            return AWTEvent.TEXT_EVENT_MASK;
        }
        if (event instanceof InputMethodEvent) {
            return AWTEvent.INPUT_METHOD_EVENT_MASK;
        }
        if (event instanceof PaintEvent) {
            return AWTEvent.PAINT_EVENT_MASK;
        }
        if (event instanceof ComponentEvent) {
            return event instanceof ContainerEvent ? AWTEvent.CONTAINER_EVENT_MASK : AWTEvent.COMPONENT_EVENT_MASK;
        }
        return 0L;
    }

    private static void validateLockingKey(int keyCode) {
        if (keyCode != KeyEvent.VK_CAPS_LOCK && keyCode != KeyEvent.VK_NUM_LOCK && keyCode != KeyEvent.VK_SCROLL_LOCK && keyCode != KeyEvent.VK_KANA_LOCK) {
            throw new IllegalArgumentException("invalid locking key");
        }
    }

    private static final class EventListenerRegistration {
        private final AWTEventListener listener;
        private final long eventMask;

        private EventListenerRegistration(AWTEventListener listener, long eventMask) {
            this.listener = listener;
            this.eventMask = eventMask;
        }
    }
}

final class BrowserToolkit extends Toolkit {
    protected EventQueue getSystemEventQueueImpl() { return applicationEventQueue(); }
    private final Map<String, Image> imageCache = new HashMap<String, Image>();
    BrowserToolkit() {
        WebAwtRuntime.addShutdownHook(() -> {
            for (Image image : imageCache.values()) { if (image instanceof ProducerImage) { ((ProducerImage) image).release(); } }
            imageCache.clear();
        });
    }

    @Override
    public Clipboard getSystemClipboard() {
        return applicationClipboard();
    }

    @Override
    public Dimension getScreenSize() {
        return new Dimension(WebAwtRuntime.getViewportWidth(), WebAwtRuntime.getViewportHeight());
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
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
    }

    @Override
    public FontMetrics getFontMetrics(Font font) {
        return new FontMetrics(font) {
        };
    }

    @Override
    public void sync() {
        // Browser Canvas and DOM mutations are committed synchronously, so there is no deferred rendering queue to flush.
    }

    @Override
    public Image getImage(String source) {
        if (source == null) {
            throw new NullPointerException("source");
        }
        Image image = imageCache.get(source);
        if (image == null) {
            image = createImage(source);
            imageCache.put(source, image);
        }
        return image;
    }

    @Override
    public Image getImage(URL source) {
        if (source == null) {
            throw new NullPointerException("source");
        }
        return getImage(source.toExternalForm());
    }

    @Override
    public Image createImage(String source) {
        return new ProducerImage(new BrowserImageProducer(source));
    }

    @Override
    public Image createImage(URL source) {
        if (source == null) {
            throw new NullPointerException("source");
        }
        return createImage(source.toExternalForm());
    }

    @Override
    public boolean prepareImage(Image image, int width, int height, ImageObserver observer) {
        return (checkImage(image, width, height, observer) & ImageObserver.ALLBITS) != 0;
    }

    @Override
    public int checkImage(Image image, int width, int height, ImageObserver observer) {
        if (image == null) {
            return ImageObserver.ERROR;
        }
        image.getWidth(observer);
        int status = image instanceof ProducerImage ? ((ProducerImage) image).getCompletionStatus() : ImageObserver.ALLBITS;
        if (observer != null && status != 0) {
            observer.imageUpdate(image, status, 0, 0, image.getWidth(null), image.getHeight(null));
        }
        return status;
    }

    @Override
    public Image createImage(ImageProducer producer) {
        return new ProducerImage(producer);
    }

    @Override
    public Image createImage(byte[] imageData, int offset, int length) {
        if (imageData == null) {
            throw new NullPointerException("imageData");
        }
        if (offset < 0 || length < 0 || offset > imageData.length - length) {
            throw new ArrayIndexOutOfBoundsException("Invalid image-data range");
        }
        byte[] selected = new byte[length];
        System.arraycopy(imageData, offset, selected, 0, length);
        String source = "data:" + imageMimeType(selected) + ";base64," + Base64.getEncoder().encodeToString(selected);
        return createImage(source);
    }

    private static String imageMimeType(byte[] data) {
        if (data.length >= 8 && (data[0] & 0xff) == 0x89 && data[1] == 0x50 && data[2] == 0x4e && data[3] == 0x47) {
            return "image/png";
        }
        if (data.length >= 3 && (data[0] & 0xff) == 0xff && (data[1] & 0xff) == 0xd8 && (data[2] & 0xff) == 0xff) {
            return "image/jpeg";
        }
        if (data.length >= 3 && data[0] == 'G' && data[1] == 'I' && data[2] == 'F') {
            return "image/gif";
        }
        return "application/octet-stream";
    }

    @Override
    public void beep() {
        WebAwtRuntime.beep();
    }

    @Override
    public boolean isModalityTypeSupported(Dialog.ModalityType modalityType) {
        if (modalityType == null) {
            throw new NullPointerException("modalityType");
        }
        return modalityType != Dialog.ModalityType.TOOLKIT_MODAL;
    }

    @Override
    public boolean isModalExclusionTypeSupported(Dialog.ModalExclusionType modalExclusionType) {
        if (modalExclusionType == null) {
            throw new NullPointerException("modalExclusionType");
        }
        return modalExclusionType != Dialog.ModalExclusionType.TOOLKIT_EXCLUDE;
    }

    @Override
    public Map<TextAttribute, ?> mapInputMethodHighlight(InputMethodHighlight highlight) throws HeadlessException {
        if (highlight == null) {
            throw new NullPointerException("highlight");
        }
        if (highlight.getStyle() != null) {
            return highlight.getStyle();
        }
        Object underline = highlight.getState() == InputMethodHighlight.RAW_TEXT ? TextAttribute.UNDERLINE_LOW_DOTTED : TextAttribute.UNDERLINE_LOW_ONE_PIXEL;
        if (highlight.isSelected()) {
            Map<TextAttribute, Object> style = new HashMap<>();
            style.put(TextAttribute.INPUT_METHOD_UNDERLINE, underline);
            style.put(TextAttribute.SWAP_COLORS, TextAttribute.SWAP_COLORS_ON);
            return Collections.unmodifiableMap(style);
        }
        return Collections.singletonMap(TextAttribute.INPUT_METHOD_UNDERLINE, underline);
    }
}
