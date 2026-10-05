package org.webawt.internal.browser;

import elemental2.dom.CanvasRenderingContext2D;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLCanvasElement;
import elemental2.dom.TextMetrics;
import java.util.HashMap;
import java.util.Map;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import org.webawt.internal.peer.FontMeasurementProvider;

public final class Elemental2FontMeasurementProvider implements FontMeasurementProvider {
    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    private interface SizedFontSet {
        @JsProperty double getSize();
    }
    private final CanvasRenderingContext2D context;
    private final Map<String, double[]> measurements = new HashMap<>();
    private boolean clearScheduled;

    public Elemental2FontMeasurementProvider() {
        HTMLCanvasElement canvas = (HTMLCanvasElement) DomGlobal.document.createElement("canvas");
        context = (CanvasRenderingContext2D) (Object) canvas.getContext("2d");
    }

    @Override
    public double[] measure(String cssFont, String text) {
        // Rebuilding an editor repeatedly asks for the same glyphs and line
        // metrics. Share those measurements within one browser task. Custom
        // FontFace sets bypass the cache, including synchronous add/delete.
        boolean cacheable = ((SizedFontSet) (Object) DomGlobal.document.fonts).getSize() == 0;
        String key = cssFont + '\u0000' + text;
        double[] cached = cacheable ? measurements.get(key) : null;
        if (cached != null) return new double[] {cached[0], cached[1], cached[2]};
        context.font = "10px sans-serif";
        context.font = cssFont;
        TextMetrics metrics = context.measureText(text);
        double ascent = metrics.fontBoundingBoxAscent;
        double descent = metrics.fontBoundingBoxDescent;
        if (!Double.isFinite(ascent) || !Double.isFinite(descent)) {
            TextMetrics sample = context.measureText("Hg");
            ascent = sample.actualBoundingBoxAscent;
            descent = sample.actualBoundingBoxDescent;
        }
        double[] result = new double[] {metrics.width, ascent, descent};
        if (cacheable) {
            if (measurements.size() >= 2048) measurements.clear();
            measurements.put(key, new double[] {result[0], result[1], result[2]});
            if (!clearScheduled) {
                clearScheduled = true;
                DomGlobal.setTimeout(ignored -> {
                    measurements.clear();
                    clearScheduled = false;
                }, 0);
            }
        } else {
            measurements.clear();
        }
        return result;
    }
}
