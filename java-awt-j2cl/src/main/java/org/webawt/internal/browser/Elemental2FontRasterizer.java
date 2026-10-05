package org.webawt.internal.browser;

import elemental2.dom.CanvasRenderingContext2D;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLCanvasElement;
import elemental2.dom.ImageData;
import elemental2.dom.TextMetrics;
import org.webawt.internal.peer.FontRasterizer;

public final class Elemental2FontRasterizer implements FontRasterizer {
    private final HTMLCanvasElement canvas = (HTMLCanvasElement) DomGlobal.document.createElement("canvas");
    private final CanvasRenderingContext2D context = (CanvasRenderingContext2D) (Object) canvas.getContext("2d");

    @Override
    public double[] bounds(String cssFont, String text) {
        context.font = "10px sans-serif";
        context.font = cssFont;
        context.textAlign = "left";
        context.textBaseline = "alphabetic";
        TextMetrics metrics = context.measureText(text);
        double[] bounds = {-metrics.actualBoundingBoxLeft, -metrics.actualBoundingBoxAscent, metrics.actualBoundingBoxLeft + metrics.actualBoundingBoxRight, metrics.actualBoundingBoxAscent + metrics.actualBoundingBoxDescent};
        for (double value : bounds) {
            if (!Double.isFinite(value)) {
                throw new UnsupportedOperationException("Native text ink bounds are unavailable in this browser");
            }
        }
        return bounds;
    }

    @Override
    public int[] rasterize(String cssFont, String text, double[] transform, int x, int y, int width, int height) {
        // Resizing resets all native state and bounds retained storage to one tile.
        canvas.width = width;
        canvas.height = height;
        context.font = cssFont;
        context.textAlign = "left";
        context.textBaseline = "alphabetic";
        context.setTransform(transform[0], transform[1], transform[2], transform[3], transform[4] - x, transform[5] - y);
        context.fillText(text, 0, 0);
        ImageData data = context.getImageData(0, 0, width, height);
        int[] coverage = new int[width * height];
        for (int index = 0; index < coverage.length; index++) {
            coverage[index] = (int) (double) data.data.getAt(index * 4 + 3);
        }
        return coverage;
    }
}
