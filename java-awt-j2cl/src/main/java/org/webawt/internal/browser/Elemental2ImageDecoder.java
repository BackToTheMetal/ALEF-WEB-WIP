package org.webawt.internal.browser;

import elemental2.core.Uint8ClampedArray;
import elemental2.dom.CanvasRenderingContext2D;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLCanvasElement;
import elemental2.dom.HTMLImageElement;
import elemental2.dom.ImageData;
import org.webawt.internal.peer.ImageDecodeCallback;
import org.webawt.internal.peer.ImageDecoder;

public final class Elemental2ImageDecoder implements ImageDecoder {
    private final java.util.List<HTMLImageElement> pending = new java.util.ArrayList<>();

    @Override
    public void dispose() {
        for (HTMLImageElement image : pending) { clearHandlers(image); image.removeAttribute("src"); }
        pending.clear();
    }

    @Override
    public void decode(String source, ImageDecodeCallback callback) {
        HTMLImageElement image = (HTMLImageElement) DomGlobal.document.createElement("img");
        pending.add(image);
        image.onload = event -> {
            pending.remove(image);
            decodeLoadedImage(image, callback);
            return null;
        };
        image.onerror = event -> {
            pending.remove(image);
            clearHandlers(image);
            callback.onError();
            return null;
        };
        image.src = source;
    }

    private static void decodeLoadedImage(HTMLImageElement image, ImageDecodeCallback callback) {
        try {
            int width = image.naturalWidth;
            int height = image.naturalHeight;
            if (width <= 0 || height <= 0) {
                callback.onError();
                return;
            }
            HTMLCanvasElement canvas = (HTMLCanvasElement) DomGlobal.document.createElement("canvas");
            canvas.width = width;
            canvas.height = height;
            CanvasRenderingContext2D context = (CanvasRenderingContext2D) (Object) canvas.getContext("2d");
            context.drawImage(image, 0, 0, width, height);
            ImageData imageData = context.getImageData(0, 0, width, height);
            callback.onDecoded(width, height, toArgb(imageData.data, width * height));
        } catch (RuntimeException exception) {
            callback.onError();
        } finally {
            clearHandlers(image);
        }
    }

    private static int[] toArgb(Uint8ClampedArray data, int pixelCount) {
        int[] pixels = new int[pixelCount];
        for (int index = 0; index < pixelCount; index++) {
            int dataIndex = index * 4;
            int red = (int) data.at(dataIndex);
            int green = (int) data.at(dataIndex + 1);
            int blue = (int) data.at(dataIndex + 2);
            int alpha = (int) data.at(dataIndex + 3);
            pixels[index] = alpha << 24 | red << 16 | green << 8 | blue;
        }
        return pixels;
    }

    private static void clearHandlers(HTMLImageElement image) {
        image.onload = null;
        image.onerror = null;
    }
}
