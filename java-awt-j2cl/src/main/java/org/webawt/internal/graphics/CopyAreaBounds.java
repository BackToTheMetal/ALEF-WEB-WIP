package org.webawt.internal.graphics;

import java.awt.geom.AffineTransform;

/** Maps axis-aligned copy requests to surface pixels, using the JDK pixel-center convention. */
public final class CopyAreaBounds {
    private CopyAreaBounds() {
    }

    public static int[] map(AffineTransform transform, int x, int y, int width, int height, int dx, int dy) {
        if (width <= 0 || height <= 0) {
            return new int[6];
        }
        if ((transform.getType() & (AffineTransform.TYPE_FLIP | AffineTransform.TYPE_MASK_ROTATION | AffineTransform.TYPE_GENERAL_TRANSFORM)) != 0) {
            throw new UnsupportedOperationException("copyArea supports only translation and positive axis-aligned scaling");
        }
        if (transform.getScaleX() == 1 && transform.getScaleY() == 1) {
            return new int[] {x + (int) Math.floor(transform.getTranslateX() + 0.5), y + (int) Math.floor(transform.getTranslateY() + 0.5), width, height, dx, dy};
        }
        double[] coordinates = {x, y, (double) x + width, (double) y + height, (double) x + dx, (double) y + dy};
        transform.transform(coordinates, 0, coordinates, 0, 3);
        int x1 = pixel(coordinates[0]);
        int y1 = pixel(coordinates[1]);
        int x2 = pixel(coordinates[2]);
        int y2 = pixel(coordinates[3]);
        return new int[] {Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1), Math.abs(y2 - y1), pixel(coordinates[4]) - x1, pixel(coordinates[5]) - y1};
    }

    private static int pixel(double coordinate) {
        return (int) Math.ceil(coordinate - 0.5);
    }
}
