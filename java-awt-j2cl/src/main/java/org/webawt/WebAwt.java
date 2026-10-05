package org.webawt;

import elemental2.dom.DomGlobal;
import elemental2.dom.FontFace;
import elemental2.dom.URL;
import elemental2.promise.Promise;
import java.awt.Window;
import java.awt.KeyboardFocusManager;
import java.util.HashMap;
import java.util.Map;
import org.webawt.internal.browser.Elemental2BrowserScheduler;
import org.webawt.internal.browser.Elemental2DisplayMetricsProvider;
import org.webawt.internal.browser.Elemental2DesktopPeer;
import org.webawt.internal.browser.Elemental2ImageDecoder;
import org.webawt.internal.browser.Elemental2FontMeasurementProvider;
import org.webawt.internal.browser.Elemental2FontRasterizer;
import org.webawt.internal.browser.Elemental2SystemColorProvider;
import org.webawt.internal.browser.Elemental2DomPeerFactory;
import org.webawt.internal.browser.Elemental2MenuPeerFactory;
import org.webawt.internal.browser.Elemental2ToolkitProvider;
import org.webawt.internal.peer.WebAwtRuntime;

public final class WebAwt {
    private static boolean installed;
    private static final Map<String, Promise<Void>> fontLoads = new HashMap<>();
    private WebAwt() {
    }

    public static void install(String rootElementId) {
        if (rootElementId == null || rootElementId.isEmpty()) {
            throw new IllegalArgumentException("root element id must not be empty");
        }
        uninstall();
        installed = true;
        WebAwtRuntime.setRootElementId(rootElementId);
        WebAwtRuntime.installPeerFactory(new Elemental2DomPeerFactory());
        WebAwtRuntime.installMenuPeerFactory(new Elemental2MenuPeerFactory());
        WebAwtRuntime.installScheduler(new Elemental2BrowserScheduler());
        WebAwtRuntime.installDisplayMetricsProvider(new Elemental2DisplayMetricsProvider());
        Elemental2DesktopPeer.getInstance().attach();
        WebAwtRuntime.installDesktopPeer(Elemental2DesktopPeer.getInstance());
        WebAwtRuntime.installImageDecoder(new Elemental2ImageDecoder());
        WebAwtRuntime.installSystemColorProvider(new Elemental2SystemColorProvider());
        WebAwtRuntime.installToolkitProvider(new Elemental2ToolkitProvider());
        WebAwtRuntime.installFontMeasurementProvider(new Elemental2FontMeasurementProvider());
        WebAwtRuntime.installFontRasterizer(new Elemental2FontRasterizer());
    }

    /** Disposes the single installed application. Successfully loaded fonts remain document-scoped. */
    public static void uninstall() {
        if (!installed) {
            return;
        }
        installed = false;
        try {
            for (Window window : Window.getWindows()) {
                window.dispose();
            }
        } finally {
            Elemental2DesktopPeer.getInstance().detach();
            KeyboardFocusManager.setCurrentKeyboardFocusManager(null);
            WebAwtRuntime.shutdown();
        }
    }

    public static Promise<Void> loadFont(String family, String url) {
        if (family == null || family.isEmpty() || url == null || url.isEmpty()) {
            throw new IllegalArgumentException("font family and URL must not be empty");
        }
        url = new URL(url, DomGlobal.document.baseURI).href;
        String source = "url(\"" + url.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "").replace("\r", "") + "\")";
        String key = family + "\n" + url;
        Promise<Void> existing = fontLoads.get(key);
        if (existing != null) {
            return existing;
        }
        FontFace face = new FontFace(family, source);
        Promise<Void> loading = face.load().then(loaded -> {
            DomGlobal.document.fonts.add(loaded);
            return null;
        }, error -> {
            fontLoads.remove(key);
            return Promise.reject(error);
        });
        fontLoads.put(key, loading);
        return loading;
    }

    public static boolean isClipboardAvailable() {
        return DomGlobal.navigator.clipboard != null;
    }

    public static Promise<String> readClipboardText() {
        if (!isClipboardAvailable()) {
            return Promise.reject("Clipboard API is unavailable");
        }
        return DomGlobal.navigator.clipboard.readText();
    }

    public static Promise<Void> writeClipboardText(String text) {
        if (!isClipboardAvailable()) {
            return Promise.reject("Clipboard API is unavailable");
        }
        return DomGlobal.navigator.clipboard.writeText(text);
    }

    public static boolean openBrowserWindow(String url) {
        if (url == null || url.length() == 0) {
            throw new IllegalArgumentException("url must not be empty");
        }
        return DomGlobal.window.open(url, "_blank", "noopener") != null;
    }
}
