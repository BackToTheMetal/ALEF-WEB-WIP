package org.webawt.samples.point;

import java.awt.Point;
import jsinterop.annotations.JsMethod;
import org.webawt.internal.browser.BrowserDocument;
import org.webawt.internal.browser.Elemental2BrowserDocument;

public final class PointSample {
    private static final BrowserDocument BROWSER_DOCUMENT = new Elemental2BrowserDocument();

    private PointSample() {
    }

    @JsMethod
    public static String translate(int x, int y, int deltaX, int deltaY) {
        Point point = new Point(x, y);
        point.translate(deltaX, deltaY);
        return point.x + "," + point.y;
    }

    @JsMethod
    public static void mount(String elementId, int x, int y, int deltaX, int deltaY) {
        BROWSER_DOCUMENT.setTextContent(elementId, translate(x, y, deltaX, deltaY));
    }
}
