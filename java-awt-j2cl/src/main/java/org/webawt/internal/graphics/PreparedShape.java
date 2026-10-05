package org.webawt.internal.graphics;

import java.awt.Shape;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.PathIterator;
import java.util.Arrays;

/** Device-space fill geometry, flattened once and swept once per raster row. */
public final class PreparedShape {
    public static final double FLATNESS = 0.125;
    private double[] edges = new double[64];
    private int count;
    private final int winding;
    private double cachedY = Double.NaN;
    private double[] crossings;
    private int[] directions;
    private double[] starts;
    private double[] ends;
    private int spans;

    public PreparedShape(Shape shape) {
        PathIterator path = shape.getPathIterator(null);
        winding = path.getWindingRule();
        FlatteningPathIterator iterator = new FlatteningPathIterator(path, FLATNESS, 12);
        double[] point = new double[2];
        double firstX = 0;
        double firstY = 0;
        double x = 0;
        double y = 0;
        boolean open = false;
        while (!iterator.isDone()) {
            int type = iterator.currentSegment(point);
            if (type == PathIterator.SEG_MOVETO) {
                if (open) { add(x, y, firstX, firstY); }
                x = firstX = point[0];
                y = firstY = point[1];
                open = true;
            } else if (type == PathIterator.SEG_LINETO) {
                add(x, y, point[0], point[1]);
                x = point[0];
                y = point[1];
            } else if (type == PathIterator.SEG_CLOSE && open) {
                add(x, y, firstX, firstY);
                x = firstX;
                y = firstY;
            }
            iterator.next();
        }
        if (open) { add(x, y, firstX, firstY); }
        int length = count / 4;
        crossings = new double[length];
        directions = new int[length];
        starts = new double[length];
        ends = new double[length];
    }

    public static java.awt.Shape hairline(java.awt.Shape shape, java.awt.geom.AffineTransform transform, java.awt.BasicStroke stroke) {
        if (stroke.getDashArray() != null) {
            throw new UnsupportedOperationException("Dashed zero-width strokes are outside the rendering profile");
        }
        java.awt.Shape device = new java.awt.BasicStroke(1, stroke.getEndCap(), stroke.getLineJoin(), stroke.getMiterLimit()).createStrokedShape(transform.createTransformedShape(shape));
        try {
            return transform.createInverse().createTransformedShape(device);
        } catch (java.awt.geom.NoninvertibleTransformException error) {
            throw new UnsupportedOperationException("Hairlines require an invertible transform");
        }
    }

    public int edgeCount() {
        return count / 4;
    }

    public boolean contains(double x, double y) {
        if (!Double.isFinite(x) || !Double.isFinite(y)) { return false; }
        prepareRow(y);
        int low = 0;
        int high = spans - 1;
        while (low <= high) {
            int middle = (low + high) >>> 1;
            if (x < starts[middle]) { high = middle - 1; }
            else if (x >= ends[middle]) { low = middle + 1; }
            else { return true; }
        }
        return false;
    }

    private void add(double x1, double y1, double x2, double y2) {
        if (y1 == y2) { return; }
        if (count + 4 > edges.length) { edges = Arrays.copyOf(edges, edges.length * 2); }
        edges[count++] = x1;
        edges[count++] = y1;
        edges[count++] = x2;
        edges[count++] = y2;
    }

    private void prepareRow(double y) {
        if (y == cachedY) { return; }
        cachedY = y;
        int size = 0;
        for (int index = 0; index < count; index += 4) {
            double y1 = edges[index + 1];
            double y2 = edges[index + 3];
            if (y >= Math.min(y1, y2) && y < Math.max(y1, y2)) {
                crossings[size] = edges[index] + (y - y1) * (edges[index + 2] - edges[index]) / (y2 - y1);
                directions[size++] = y2 > y1 ? 1 : -1;
            }
        }
        sort(0, size - 1);
        spans = 0;
        int level = 0;
        double start = 0;
        for (int index = 0; index < size;) {
            double x = crossings[index];
            boolean before = winding == PathIterator.WIND_EVEN_ODD ? (level & 1) != 0 : level != 0;
            do { level += directions[index++]; } while (index < size && crossings[index] == x);
            boolean after = winding == PathIterator.WIND_EVEN_ODD ? (level & 1) != 0 : level != 0;
            if (!before && after) { start = x; }
            if (before && !after) { starts[spans] = start; ends[spans++] = x; }
        }
    }

    private void sort(int low, int high) {
        if (low >= high) { return; }
        int left = low;
        int right = high;
        double pivot = crossings[(low + high) >>> 1];
        while (left <= right) {
            while (crossings[left] < pivot) { left++; }
            while (crossings[right] > pivot) { right--; }
            if (left <= right) {
                double x = crossings[left]; crossings[left] = crossings[right]; crossings[right] = x;
                int direction = directions[left]; directions[left] = directions[right]; directions[right] = direction;
                left++;
                right--;
            }
        }
        if (low < right) { sort(low, right); }
        if (left < high) { sort(left, high); }
    }
}
