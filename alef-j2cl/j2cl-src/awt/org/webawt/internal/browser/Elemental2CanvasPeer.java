package org.webawt.internal.browser;

import elemental2.dom.BaseRenderingContext2D;
import elemental2.dom.CanvasRenderingContext2D;
import elemental2.dom.CanvasGradient;
import elemental2.dom.HTMLCanvasElement;
import org.webawt.internal.peer.CanvasPeer;

public final class Elemental2CanvasPeer extends Elemental2DomPeer implements CanvasPeer {
    private final HTMLCanvasElement canvas;
    private final CanvasRenderingContext2D context;
    private double pixelRatio = 1;
    private static final int MAX_CACHE_PIXELS = 262144;
    private static final int MAX_CACHE_ENTRIES = 4;
    private static int uploads;
    private static int hits;
    private static int retainedPixels;
    private static int retainedEntries;
    private final java.util.List<CachedImage> imageCache = new java.util.ArrayList<>();
    private int cachePixels;

    public static int[] imageCacheStatistics() {
        return new int[] {uploads, hits, retainedEntries, retainedPixels};
    }

    @Override
    public void detach() {
        clearImageCache();
        canvas.width = 0;
        canvas.height = 0;
        super.detach();
    }

    private void clearImageCache() {
        for (CachedImage entry : imageCache) {
            entry.canvas.width = 0;
            entry.canvas.height = 0;
        }
        retainedPixels -= cachePixels;
        retainedEntries -= imageCache.size();
        cachePixels = 0;
        imageCache.clear();
    }

    private HTMLCanvasElement imageSource(int[] pixels, int width, int height) {
        int count = Math.min(pixels.length, width * height);
        int hash = 1;
        for (int index = 0; index < count; index++) { hash = 31 * hash + pixels[index]; }
        for (int index = 0; index < imageCache.size(); index++) {
            CachedImage entry = imageCache.get(index);
            if (entry.width == width && entry.height == height && entry.hash == hash && entry.pixels.length == count) {
                boolean same = true;
                for (int pixel = 0; pixel < count; pixel++) {
                    if (entry.pixels[pixel] != pixels[pixel]) { same = false; break; }
                }
                if (same) {
                    imageCache.remove(index);
                    imageCache.add(0, entry);
                    hits++;
                    return entry.canvas;
                }
            }
        }
        HTMLCanvasElement source = (HTMLCanvasElement) elemental2.dom.DomGlobal.document.createElement("canvas");
        source.width = width;
        source.height = height;
        CanvasRenderingContext2D sourceContext = (CanvasRenderingContext2D) (Object) source.getContext("2d");
        elemental2.dom.ImageData imageData = sourceContext.createImageData(width, height);
        for (int index = 0; index < count; index++) {
            int pixel = pixels[index];
            int dataIndex = index * 4;
            imageData.data.setAt(dataIndex, (double) (pixel >>> 16 & 0xff));
            imageData.data.setAt(dataIndex + 1, (double) (pixel >>> 8 & 0xff));
            imageData.data.setAt(dataIndex + 2, (double) (pixel & 0xff));
            imageData.data.setAt(dataIndex + 3, (double) (pixel >>> 24));
        }
        sourceContext.putImageData(imageData, 0, 0);
        uploads++;
        if (width * height <= MAX_CACHE_PIXELS) {
            while (!imageCache.isEmpty() && (imageCache.size() >= MAX_CACHE_ENTRIES || cachePixels + width * height > MAX_CACHE_PIXELS)) {
                CachedImage removed = imageCache.remove(imageCache.size() - 1);
                cachePixels -= removed.width * removed.height;
                retainedPixels -= removed.width * removed.height;
                retainedEntries--;
                removed.canvas.width = 0;
                removed.canvas.height = 0;
            }
            imageCache.add(0, new CachedImage(source, width, height, hash, java.util.Arrays.copyOf(pixels, count)));
            cachePixels += width * height;
            retainedPixels += width * height;
            retainedEntries++;
        }
        return source;
    }

    private static final class CachedImage {
        final HTMLCanvasElement canvas;
        final int width;
        final int height;
        final int hash;
        final int[] pixels;
        CachedImage(HTMLCanvasElement canvas, int width, int height, int hash, int[] pixels) {
            this.canvas = canvas;
            this.width = width;
            this.height = height;
            this.hash = hash;
            this.pixels = pixels;
        }
    }

    @Override
    public int[] getSurfaceSize() {
        return new int[] {(int) Math.ceil(canvas.width / pixelRatio), (int) Math.ceil(canvas.height / pixelRatio)};
    }

    public Elemental2CanvasPeer() {
        super("canvas");
        canvas = (HTMLCanvasElement) element;
        context = (CanvasRenderingContext2D) (Object) canvas.getContext("2d");
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        pixelRatio = Math.max(1.0, elemental2.dom.DomGlobal.window.devicePixelRatio);
        int pixelWidth = (int) Math.round(Math.max(0, width) * pixelRatio);
        int pixelHeight = (int) Math.round(Math.max(0, height) * pixelRatio);
        // Assigning either dimension clears the surface, even for the same value.
        // A layout or getGraphics() call must not erase an unchanged canvas.
        if (canvas.width != pixelWidth) canvas.width = pixelWidth;
        if (canvas.height != pixelHeight) canvas.height = pixelHeight;
        context.setTransform(pixelRatio, 0, 0, pixelRatio, 0, 0);
    }

    @Override
    public void setFillStyle(String cssColor) {
        context.fillStyle = BaseRenderingContext2D.FillStyleUnionType.of(cssColor);
    }

    @Override
    public void setStrokeStyle(String cssColor) {
        context.strokeStyle = BaseRenderingContext2D.StrokeStyleUnionType.of(cssColor);
    }

    @Override
    public void setLinearGradient(boolean filling, double x1, double y1, double x2, double y2, String[] cssColors, double[] fractions) {
        CanvasGradient gradient = context.createLinearGradient(x1, y1, x2, y2);
        addColorStops(gradient, cssColors, fractions);
        if (filling) {
            context.fillStyle = BaseRenderingContext2D.FillStyleUnionType.of(gradient);
        } else {
            context.strokeStyle = BaseRenderingContext2D.StrokeStyleUnionType.of(gradient);
        }
    }

    @Override
    public void setRadialGradient(boolean filling, double centerX, double centerY, double radius, double focusX, double focusY, String[] cssColors, double[] fractions) {
        CanvasGradient gradient = context.createRadialGradient(focusX, focusY, 0, centerX, centerY, radius);
        addColorStops(gradient, cssColors, fractions);
        if (filling) {
            context.fillStyle = BaseRenderingContext2D.FillStyleUnionType.of(gradient);
        } else {
            context.strokeStyle = BaseRenderingContext2D.StrokeStyleUnionType.of(gradient);
        }
    }

    @Override
    public void setLineWidth(double width) {
        context.lineWidth = width;
    }

    @Override
    public void setLineCap(String cap) {
        context.lineCap = cap;
    }

    @Override
    public void setLineJoin(String join) {
        context.lineJoin = join;
    }

    @Override
    public void setMiterLimit(double limit) {
        context.miterLimit = limit;
    }

    @Override
    public void setLineDash(double[] dash, double phase) {
        context.setLineDash(dash);
        context.lineDashOffset = phase;
    }

    @Override
    public void setCompositeOperation(String operation) {
        context.globalCompositeOperation = operation;
    }

    @Override
    public void setFont(String cssFont) {
        context.font = "10px sans-serif";
        context.font = cssFont;
        context.textAlign = "left";
        context.textBaseline = "alphabetic";
    }

    @Override
    public void setGlobalAlpha(double alpha) {
        context.globalAlpha = alpha;
    }

    @Override
    public void clearRect(double x, double y, double width, double height) {
        context.clearRect(x, y, width, height);
    }

    @Override
    public void copyArea(double x, double y, double width, double height, double deltaX, double deltaY) {
        double right = Math.min(canvas.width / pixelRatio, x + width);
        double bottom = Math.min(canvas.height / pixelRatio, y + height);
        x = Math.max(0, x);
        y = Math.max(0, y);
        width = right - x;
        height = bottom - y;
        if (width <= 0 || height <= 0) {
            return;
        }
        context.save();
        try {
            // Bound operators such as SRC to the accessible destination, including source clipping.
            context.beginPath();
            context.rect(x + deltaX, y + deltaY, width, height);
            context.clip();
            context.drawImage(canvas, x * pixelRatio, y * pixelRatio, width * pixelRatio, height * pixelRatio, x + deltaX, y + deltaY, width, height);
        } finally {
            context.restore();
        }
    }

    @Override
    public void fillRect(double x, double y, double width, double height) {
        context.fillRect(x, y, width, height);
    }

    @Override
    public void strokeRect(double x, double y, double width, double height) {
        context.strokeRect(x, y, width, height);
    }

    @Override
    public void beginPath() {
        context.beginPath();
    }

    @Override
    public void moveTo(double x, double y) {
        context.moveTo(x, y);
    }

    @Override
    public void lineTo(double x, double y) {
        context.lineTo(x, y);
    }

    @Override
    public void quadraticCurveTo(double controlX, double controlY, double x, double y) {
        context.quadraticCurveTo(controlX, controlY, x, y);
    }

    @Override
    public void bezierCurveTo(double controlX1, double controlY1, double controlX2, double controlY2, double x, double y) {
        context.bezierCurveTo(controlX1, controlY1, controlX2, controlY2, x, y);
    }

    @Override
    public void closePath() {
        context.closePath();
    }

    @Override
    public void fill() {
        context.fill();
    }

    @Override
    public void fill(boolean evenOdd) {
        context.fill(evenOdd ? "evenodd" : "nonzero");
    }

    @Override
    public void stroke() {
        context.stroke();
    }

    @Override
    public void clip() {
        context.clip();
    }

    @Override
    public void clip(boolean evenOdd) {
        context.clip(evenOdd ? "evenodd" : "nonzero");
    }

    @Override
    public void save() {
        context.save();
    }

    @Override
    public void restore() {
        context.restore();
    }

    @Override
    public void translate(double x, double y) {
        context.translate(x, y);
    }

    @Override
    public void scale(double x, double y) {
        context.scale(x, y);
    }

    @Override
    public void rotate(double angle) {
        context.rotate(angle);
    }

    @Override
    public void transform(double scaleX, double shearY, double shearX, double scaleY, double translateX, double translateY) {
        context.transform(scaleX, shearY, shearX, scaleY, translateX, translateY);
    }

    @Override
    public void drawImage(int[] pixels, int imageWidth, int imageHeight, int sourceX1, int sourceY1, int sourceX2, int sourceY2, double destinationX1, double destinationY1, double destinationX2, double destinationY2) {
        if (imageWidth <= 0 || imageHeight <= 0 || sourceX1 == sourceX2 || sourceY1 == sourceY2 || destinationX1 == destinationX2 || destinationY1 == destinationY2) {
            return;
        }
        HTMLCanvasElement source = imageSource(pixels, imageWidth, imageHeight);
        double destinationWidth = Math.abs(destinationX2 - destinationX1);
        double destinationHeight = Math.abs(destinationY2 - destinationY1);
        boolean flipX = (sourceX2 < sourceX1) ^ (destinationX2 < destinationX1);
        boolean flipY = (sourceY2 < sourceY1) ^ (destinationY2 < destinationY1);
        context.save();
        try {
            context.translate(Math.min(destinationX1, destinationX2) + (flipX ? destinationWidth : 0), Math.min(destinationY1, destinationY2) + (flipY ? destinationHeight : 0));
            context.scale(flipX ? -1 : 1, flipY ? -1 : 1);
            context.drawImage(source, Math.min(sourceX1, sourceX2), Math.min(sourceY1, sourceY2), Math.abs(sourceX2 - sourceX1), Math.abs(sourceY2 - sourceY1), 0, 0, destinationWidth, destinationHeight);
        } finally {
            context.restore();
            if ((double) imageWidth * imageHeight > MAX_CACHE_PIXELS) { source.width = 0; source.height = 0; }
        }
    }

    @Override
    public void fillText(String text, double x, double y) {
        context.fillText(text, x, y);
    }

    private static void addColorStops(CanvasGradient gradient, String[] cssColors, double[] fractions) {
        for (int index = 0; index < cssColors.length; index++) {
            gradient.addColorStop(fractions[index], cssColors[index]);
        }
    }
}
