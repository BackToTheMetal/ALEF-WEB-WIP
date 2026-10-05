package java.awt.geom;

import java.util.Arrays;
import java.util.NoSuchElementException;

public class FlatteningPathIterator implements PathIterator {
    private static final int DEFAULT_LIMIT = 10;
    private final double flatness;
    private final int recursionLimit;
    private final int windingRule;
    private byte[] types = new byte[16];
    private double[] coordinates = new double[32];
    private int count;
    private int index;

    public FlatteningPathIterator(PathIterator source, double flatness) {
        this(source, flatness, DEFAULT_LIMIT);
    }

    public FlatteningPathIterator(PathIterator source, double flatness, int limit) {
        if (flatness < 0.0) {
            throw new IllegalArgumentException("flatness must be non-negative");
        }
        if (limit < 0) {
            throw new IllegalArgumentException("limit must be non-negative");
        }
        this.flatness = flatness;
        recursionLimit = limit;
        windingRule = source.getWindingRule();
        flatten(source);
    }

    public double getFlatness() {
        return flatness;
    }

    public int getRecursionLimit() {
        return recursionLimit;
    }

    @Override
    public int getWindingRule() {
        return windingRule;
    }

    @Override
    public boolean isDone() {
        return index >= count;
    }

    @Override
    public void next() {
        if (!isDone()) {
            index++;
        }
    }

    @Override
    public int currentSegment(float[] output) {
        requireCurrent();
        if (types[index] != SEG_CLOSE) {
            output[0] = (float) coordinates[index * 2];
            output[1] = (float) coordinates[index * 2 + 1];
        }
        return types[index];
    }

    @Override
    public int currentSegment(double[] output) {
        requireCurrent();
        if (types[index] != SEG_CLOSE) {
            output[0] = coordinates[index * 2];
            output[1] = coordinates[index * 2 + 1];
        }
        return types[index];
    }

    private void flatten(PathIterator source) {
        double[] values = new double[6];
        double currentX = 0.0;
        double currentY = 0.0;
        while (!source.isDone()) {
            int type = source.currentSegment(values);
            if (type == SEG_MOVETO) {
                add(SEG_MOVETO, values[0], values[1]);
                currentX = values[0];
                currentY = values[1];
            } else if (type == SEG_LINETO) {
                add(SEG_LINETO, values[0], values[1]);
                currentX = values[0];
                currentY = values[1];
            } else if (type == SEG_QUADTO) {
                flattenQuad(currentX, currentY, values[0], values[1], values[2], values[3], 0);
                currentX = values[2];
                currentY = values[3];
            } else if (type == SEG_CUBICTO) {
                flattenCubic(currentX, currentY, values[0], values[1], values[2], values[3], values[4], values[5], 0);
                currentX = values[4];
                currentY = values[5];
            } else {
                add(SEG_CLOSE, 0.0, 0.0);
            }
            source.next();
        }
    }

    private void flattenQuad(double x1, double y1, double controlX, double controlY, double x2, double y2, int level) {
        if (level >= recursionLimit || QuadCurve2D.getFlatnessSq(x1, y1, controlX, controlY, x2, y2) < flatness * flatness) {
            add(SEG_LINETO, x2, y2);
            return;
        }
        double x1Control = (x1 + controlX) / 2.0;
        double y1Control = (y1 + controlY) / 2.0;
        double controlX2 = (controlX + x2) / 2.0;
        double controlY2 = (controlY + y2) / 2.0;
        double centerX = (x1Control + controlX2) / 2.0;
        double centerY = (y1Control + controlY2) / 2.0;
        flattenQuad(x1, y1, x1Control, y1Control, centerX, centerY, level + 1);
        flattenQuad(centerX, centerY, controlX2, controlY2, x2, y2, level + 1);
    }

    private void flattenCubic(double x1, double y1, double controlX1, double controlY1, double controlX2, double controlY2, double x2, double y2, int level) {
        if (level >= recursionLimit || CubicCurve2D.getFlatnessSq(x1, y1, controlX1, controlY1, controlX2, controlY2, x2, y2) < flatness * flatness) {
            add(SEG_LINETO, x2, y2);
            return;
        }
        double firstX = (x1 + controlX1) / 2.0;
        double firstY = (y1 + controlY1) / 2.0;
        double middleX = (controlX1 + controlX2) / 2.0;
        double middleY = (controlY1 + controlY2) / 2.0;
        double lastX = (controlX2 + x2) / 2.0;
        double lastY = (controlY2 + y2) / 2.0;
        double leftControlX = (firstX + middleX) / 2.0;
        double leftControlY = (firstY + middleY) / 2.0;
        double rightControlX = (middleX + lastX) / 2.0;
        double rightControlY = (middleY + lastY) / 2.0;
        double centerX = (leftControlX + rightControlX) / 2.0;
        double centerY = (leftControlY + rightControlY) / 2.0;
        flattenCubic(x1, y1, firstX, firstY, leftControlX, leftControlY, centerX, centerY, level + 1);
        flattenCubic(centerX, centerY, rightControlX, rightControlY, lastX, lastY, x2, y2, level + 1);
    }

    private void add(int type, double x, double y) {
        if (count == types.length) {
            types = Arrays.copyOf(types, count * 2);
            coordinates = Arrays.copyOf(coordinates, count * 4);
        }
        types[count] = (byte) type;
        coordinates[count * 2] = x;
        coordinates[count * 2 + 1] = y;
        count++;
    }

    private void requireCurrent() {
        if (isDone()) {
            throw new NoSuchElementException("flattening iterator out of bounds");
        }
    }
}
