package org.webawt.internal.peer;

import java.util.ArrayList;
import java.util.List;

public final class WebAwtRuntime {
    private static final List<PendingTask> pendingTasks = new ArrayList<>();
    private static final List<Runnable> shutdownHooks = new ArrayList<>();
    private static DomPeerFactory peerFactory = WebAwtRuntime::createModelPeer;
    private static MenuPeerFactory menuPeerFactory = menuBar -> new ModelMenuPeer();
    private static BrowserScheduler scheduler = Runnable::run;
    private static DisplayMetricsProvider displayMetricsProvider = new ModelDisplayMetricsProvider();
    private static ToolkitProvider toolkitProvider = () -> {
    };
    private static SystemColorProvider systemColorProvider = (index, fallbackRgb) -> fallbackRgb;
    private static ImageDecoder imageDecoder = (source, callback) -> callback.onError();
    private static DesktopPeer desktopPeer = new ModelDesktopPeer();
    private static String rootElementId = "webawt-root";
    private static FontMeasurementProvider fontMeasurementProvider = (font, text) -> null;
    private static FontRasterizer fontRasterizer;
    private static String[] dragMimeTypes = new String[0];
    private static String[] dragData = new String[0];
    private static int dragSourceActions;
    private static PeerDragSourceListener dragSourceListener;

    private WebAwtRuntime() {
    }

    public static void installFontMeasurementProvider(FontMeasurementProvider provider) {
        if (provider == null) {
            throw new NullPointerException("provider");
        }
        fontMeasurementProvider = provider;
    }

    public static double[] measureText(String cssFont, String text) {
        return fontMeasurementProvider.measure(cssFont, text);
    }

    public static void installFontRasterizer(FontRasterizer rasterizer) {
        fontRasterizer = rasterizer;
    }

    public static FontRasterizer getFontRasterizer() {
        if (fontRasterizer == null) {
            throw new UnsupportedOperationException("BufferedImage text painting requires an installed font rasterizer (WebAwt.install)");
        }
        return fontRasterizer;
    }

    public static DomPeer createPeer(String elementName) {
        if (elementName == null || elementName.length() == 0) {
            throw new IllegalArgumentException("element name must not be empty");
        }
        return peerFactory.create(elementName);
    }

    public static MenuPeer createMenuPeer(boolean menuBar) {
        return menuPeerFactory.create(menuBar);
    }

    public static void installPeerFactory(DomPeerFactory factory) {
        if (factory == null) {
            throw new NullPointerException("factory");
        }
        peerFactory = factory;
    }

    public static void installMenuPeerFactory(MenuPeerFactory factory) {
        if (factory == null) {
            throw new NullPointerException("factory");
        }
        menuPeerFactory = factory;
    }

    public static void installScheduler(BrowserScheduler browserScheduler) {
        if (browserScheduler == null) {
            throw new NullPointerException("browserScheduler");
        }
        scheduler = browserScheduler;
    }

    public static void installDisplayMetricsProvider(DisplayMetricsProvider provider) {
        if (provider == null) {
            throw new NullPointerException("provider");
        }
        displayMetricsProvider = provider;
    }

    public static void installToolkitProvider(ToolkitProvider provider) {
        if (provider == null) {
            throw new NullPointerException("provider");
        }
        toolkitProvider = provider;
    }

    public static void installSystemColorProvider(SystemColorProvider provider) {
        if (provider == null) {
            throw new NullPointerException("provider");
        }
        systemColorProvider = provider;
    }

    public static void installImageDecoder(ImageDecoder decoder) {
        if (decoder == null) {
            throw new NullPointerException("decoder");
        }
        imageDecoder = decoder;
    }

    public static void installDesktopPeer(DesktopPeer peer) {
        if (peer == null) {
            throw new NullPointerException("peer");
        }
        desktopPeer = peer;
    }

    public static DesktopPeer getDesktopPeer() {
        return desktopPeer;
    }

    public static int getViewportWidth() {
        return Math.max(0, displayMetricsProvider.getViewportWidth());
    }

    public static int getViewportHeight() {
        return Math.max(0, displayMetricsProvider.getViewportHeight());
    }

    public static double getDeviceScaleFactor() {
        return Math.max(1.0, displayMetricsProvider.getDeviceScaleFactor());
    }

    public static void beep() {
        toolkitProvider.beep();
    }

    public static int getMenuShortcutKeyMaskEx() {
        return toolkitProvider.getMenuShortcutKeyMaskEx();
    }

    public static int getSystemColor(int index, int fallbackRgb) {
        return systemColorProvider.getSystemColor(index, fallbackRgb);
    }

    public static void decodeImage(String source, ImageDecodeCallback callback) {
        if (source == null || callback == null) {
            throw new NullPointerException("Image source and callback must not be null");
        }
        imageDecoder.decode(source, callback);
    }

    public static void schedule(Runnable task) {
        if (task == null) {
            throw new NullPointerException("task");
        }
        schedule(task, () -> {});
    }

    public static void schedule(Runnable task, Runnable cancelled) {
        if (task == null || cancelled == null) {
            throw new NullPointerException("task and cancellation handler");
        }
        PendingTask pending = new PendingTask(task, cancelled);
        pendingTasks.add(pending);
        scheduler.schedule(() -> {
            try {
                if (pending.task != null) {
                    pending.task.run();
                }
            } finally {
                pendingTasks.remove(pending);
                pending.task = null;
                pending.cancelled = null;
            }
        });
    }

    public static int pendingTaskCount() {
        return pendingTasks.size();
    }

    // Hooks belong to document-lifetime singleton services, never to component instances.
    public static void addShutdownHook(Runnable hook) {
        shutdownHooks.add(hook);
    }

    public static void shutdown() {
        scheduler.cancelPending();
        imageDecoder.dispose();
        List<PendingTask> cancelled = new ArrayList<>(pendingTasks);
        pendingTasks.clear();
        for (PendingTask pending : cancelled) {
            Runnable callback = pending.cancelled;
            pending.task = null;
            pending.cancelled = null;
            callback.run();
        }
        cancelDrag();
        for (Runnable hook : shutdownHooks) {
            hook.run();
        }
        peerFactory = WebAwtRuntime::createModelPeer;
        menuPeerFactory = menuBar -> new ModelMenuPeer();
        scheduler = Runnable::run;
        desktopPeer = new ModelDesktopPeer();
        imageDecoder = (source, callback) -> callback.onError();
        fontRasterizer = null;
        fontMeasurementProvider = (font, text) -> null;
    }

    private static final class PendingTask {
        private Runnable task;
        private Runnable cancelled;

        private PendingTask(Runnable task, Runnable cancelled) {
            this.task = task;
            this.cancelled = cancelled;
        }
    }

    public static void setRootElementId(String elementId) {
        if (elementId == null || elementId.length() == 0) {
            throw new IllegalArgumentException("root element id must not be empty");
        }
        rootElementId = elementId;
    }

    public static void attach(DomPeer peer, DomPeer parent) {
        if (parent == null) {
            peer.attachToElement(rootElementId);
            return;
        }
        peer.attachTo(parent);
    }

    public static void beginDrag(String[] mimeTypes, String[] data, int sourceActions, PeerDragSourceListener listener) {
        dragMimeTypes = copy(mimeTypes);
        dragData = copy(data);
        dragSourceActions = sourceActions;
        dragSourceListener = listener;
    }

    public static void populateDragStart(PeerDragEvent event) {
        if (dragSourceListener != null) {
            event.setSource(dragSourceActions, dragMimeTypes, dragData);
        }
    }

    public static boolean isLocalDragActive() {
        return dragSourceListener != null;
    }

    public static void notifyDragSource(int type, int action, int targetActions, int modifiers, int x, int y, boolean success) {
        PeerDragSourceListener listener = dragSourceListener;
        if (type == PeerDragEvent.DRAG_END) {
            cancelDrag();
        }
        if (listener != null) {
            listener.handle(type, action, targetActions, modifiers, x, y, success);
        }
    }

    private static void cancelDrag() {
        dragMimeTypes = new String[0];
        dragData = new String[0];
        dragSourceActions = 0;
        dragSourceListener = null;
    }

    private static DomPeer createModelPeer(String elementName) {
        return "canvas".equals(elementName) ? new ModelCanvasPeer() : new ModelDomPeer(elementName);
    }

    private static String[] copy(String[] values) {
        String[] result = new String[values.length];
        System.arraycopy(values, 0, result, 0, values.length);
        return result;
    }

    private static final class ModelDisplayMetricsProvider implements DisplayMetricsProvider {
        @Override
        public int getViewportWidth() {
            return 1024;
        }

        @Override
        public int getViewportHeight() {
            return 768;
        }

        @Override
        public double getDeviceScaleFactor() {
            return 1.0;
        }
    }
}
