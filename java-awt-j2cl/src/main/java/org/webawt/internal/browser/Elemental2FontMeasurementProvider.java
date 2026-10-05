package org.webawt.internal.browser;

import elemental2.dom.CanvasRenderingContext2D;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLCanvasElement;
import elemental2.dom.TextMetrics;
import org.webawt.internal.peer.FontMeasurementProvider;

public final class Elemental2FontMeasurementProvider implements FontMeasurementProvider {
    private final CanvasRenderingContext2D context;

    public Elemental2FontMeasurementProvider() {
        HTMLCanvasElement canvas = (HTMLCanvasElement) DomGlobal.document.createElement("canvas");
        context = (CanvasRenderingContext2D) (Object) canvas.getContext("2d");
    }

    @Override
    public double[] measure(String cssFont, String text) {
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
        return new double[] {metrics.width, ascent, descent};
    }
}
