package java.awt.geom;

import java.awt.Rectangle;
import java.awt.Shape;
import java.io.Serializable;
import java.util.Arrays;
import java.util.NoSuchElementException;

public abstract class Path2D implements Shape, Cloneable {
    public static final int WIND_EVEN_ODD = PathIterator.WIND_EVEN_ODD;
    public static final int WIND_NON_ZERO = PathIterator.WIND_NON_ZERO;
    private static final int DEFAULT_CAPACITY = 20;
    private byte[] pointTypes;
    private double[] coordinates;
    private int typeCount;
    private int coordinateCount;
    private int windingRule;

    Path2D(int rule, int initialCapacity) {
        if (initialCapacity < 0) {
            throw new NegativeArraySizeException("Initial capacity must be non-negative");
        }
        setWindingRule(rule);
        pointTypes = new byte[initialCapacity];
        coordinates = new double[initialCapacity * 2];
    }

    public abstract void moveTo(double x, double y);

    public abstract void lineTo(double x, double y);

    public abstract void quadTo(double controlX, double controlY, double x, double y);

    public abstract void curveTo(double controlX1, double controlY1, double controlX2, double controlY2, double x, double y);

    public final void closePath() {
        if (typeCount != 0 && pointTypes[typeCount - 1] != PathIterator.SEG_CLOSE) {
            ensureTypeCapacity(1);
            pointTypes[typeCount++] = PathIterator.SEG_CLOSE;
        }
    }

    public final void append(Shape shape, boolean connect) {
        append(shape.getPathIterator(null), connect);
    }

    public abstract void append(PathIterator iterator, boolean connect);

    public final int getWindingRule() {
        return windingRule;
    }

    public final void setWindingRule(int rule) {
        if (rule != WIND_EVEN_ODD && rule != WIND_NON_ZERO) {
            throw new IllegalArgumentException("winding rule must be WIND_EVEN_ODD or WIND_NON_ZERO");
        }
        windingRule = rule;
    }

    public final Point2D getCurrentPoint() {
        if (typeCount == 0) {
            return null;
        }
        int index = coordinateCount;
        if (pointTypes[typeCount - 1] == PathIterator.SEG_CLOSE) {
            for (int typeIndex = typeCount - 2; typeIndex >= 0; typeIndex--) {
                index -= coordinateCount(pointTypes[typeIndex]);
                if (pointTypes[typeIndex] == PathIterator.SEG_MOVETO) {
                    return createPoint(coordinates[index], coordinates[index + 1]);
                }
            }
        }
        return createPoint(coordinates[index - 2], coordinates[index - 1]);
    }

    public final void reset() {
        typeCount = 0;
        coordinateCount = 0;
    }

    public abstract void transform(AffineTransform transform);

    public final Shape createTransformedShape(AffineTransform transform) {
        Path2D copy = (Path2D) clone();
        if (transform != null) {
            copy.transform(transform);
        }
        return copy;
    }

    @Override
    public final Rectangle getBounds() {
        return getBounds2D().getBounds();
    }

    public static boolean contains(PathIterator iterator, double x, double y) {
        return PathSupport.contains(iterator, x, y);
    }

    public static boolean contains(PathIterator iterator, Point2D point) {
        return contains(iterator, point.getX(), point.getY());
    }

    @Override
    public final boolean contains(double x, double y) {
        return contains(getPathIterator(null), x, y);
    }

    @Override
    public final boolean contains(Point2D point) {
        return contains(point.getX(), point.getY());
    }

    public static boolean contains(PathIterator iterator, double x, double y, double width, double height) {
        return PathSupport.contains(iterator, x, y, width, height);
    }

    public static boolean contains(PathIterator iterator, Rectangle2D rectangle) {
        return contains(iterator, rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
    }

    @Override
    public final boolean contains(double x, double y, double width, double height) {
        return contains(getPathIterator(null), x, y, width, height);
    }

    @Override
    public final boolean contains(Rectangle2D rectangle) {
        return contains(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
    }

    public static boolean intersects(PathIterator iterator, double x, double y, double width, double height) {
        return PathSupport.intersects(iterator, x, y, width, height);
    }

    public static boolean intersects(PathIterator iterator, Rectangle2D rectangle) {
        return intersects(iterator, rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
    }

    @Override
    public final boolean intersects(double x, double y, double width, double height) {
        return intersects(getPathIterator(null), x, y, width, height);
    }

    @Override
    public final boolean intersects(Rectangle2D rectangle) {
        return intersects(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
    }

    @Override
    public final PathIterator getPathIterator(AffineTransform transform, double flatness) {
        return new FlatteningPathIterator(getPathIterator(transform), flatness);
    }

    public abstract Object clone();

    public abstract void trimToSize();

    final void addMove(double x, double y, boolean floatPrecision) {
        double storedX = store(x, floatPrecision);
        double storedY = store(y, floatPrecision);
        if (typeCount > 0 && pointTypes[typeCount - 1] == PathIterator.SEG_MOVETO) {
            coordinates[coordinateCount - 2] = storedX;
            coordinates[coordinateCount - 1] = storedY;
            return;
        }
        addType(PathIterator.SEG_MOVETO, 2);
        coordinates[coordinateCount++] = storedX;
        coordinates[coordinateCount++] = storedY;
    }

    final void addLine(double x, double y, boolean floatPrecision) {
        requireCurrentPoint();
        addType(PathIterator.SEG_LINETO, 2);
        coordinates[coordinateCount++] = store(x, floatPrecision);
        coordinates[coordinateCount++] = store(y, floatPrecision);
    }

    final void addQuad(double controlX, double controlY, double x, double y, boolean floatPrecision) {
        requireCurrentPoint();
        addType(PathIterator.SEG_QUADTO, 4);
        coordinates[coordinateCount++] = store(controlX, floatPrecision);
        coordinates[coordinateCount++] = store(controlY, floatPrecision);
        coordinates[coordinateCount++] = store(x, floatPrecision);
        coordinates[coordinateCount++] = store(y, floatPrecision);
    }

    final void addCubic(double controlX1, double controlY1, double controlX2, double controlY2, double x, double y, boolean floatPrecision) {
        requireCurrentPoint();
        addType(PathIterator.SEG_CUBICTO, 6);
        coordinates[coordinateCount++] = store(controlX1, floatPrecision);
        coordinates[coordinateCount++] = store(controlY1, floatPrecision);
        coordinates[coordinateCount++] = store(controlX2, floatPrecision);
        coordinates[coordinateCount++] = store(controlY2, floatPrecision);
        coordinates[coordinateCount++] = store(x, floatPrecision);
        coordinates[coordinateCount++] = store(y, floatPrecision);
    }

    final void appendIterator(PathIterator iterator, boolean connect, boolean floatPrecision) {
        double[] values = new double[6];
        boolean first = true;
        while (!iterator.isDone()) {
            int type = iterator.currentSegment(values);
            if (type == PathIterator.SEG_MOVETO) {
                Point2D current = getCurrentPoint();
                if (!first || !connect || current == null) {
                    addMove(values[0], values[1], floatPrecision);
                } else if (pointTypes[typeCount - 1] != PathIterator.SEG_CLOSE && current.getX() == values[0] && current.getY() == values[1]) {
                    // The connected subpath already starts at the current point.
                } else {
                    addLine(values[0], values[1], floatPrecision);
                }
            } else if (type == PathIterator.SEG_LINETO) {
                addLine(values[0], values[1], floatPrecision);
            } else if (type == PathIterator.SEG_QUADTO) {
                addQuad(values[0], values[1], values[2], values[3], floatPrecision);
            } else if (type == PathIterator.SEG_CUBICTO) {
                addCubic(values[0], values[1], values[2], values[3], values[4], values[5], floatPrecision);
            } else {
                closePath();
            }
            first = false;
            iterator.next();
        }
    }

    final void transformCoordinates(AffineTransform transform, boolean floatPrecision) {
        transform.transform(coordinates, 0, coordinates, 0, coordinateCount / 2);
        if (floatPrecision) {
            for (int index = 0; index < coordinateCount; index++) {
                coordinates[index] = (float) coordinates[index];
            }
        }
    }

    final Rectangle2D bounds(boolean floatPrecision) {
        if (coordinateCount == 0) {
            return floatPrecision ? new Rectangle2D.Float() : new Rectangle2D.Double();
        }
        double minX = coordinates[0];
        double minY = coordinates[1];
        double maxX = minX;
        double maxY = minY;
        for (int index = 2; index < coordinateCount; index += 2) {
            minX = Math.min(minX, coordinates[index]);
            minY = Math.min(minY, coordinates[index + 1]);
            maxX = Math.max(maxX, coordinates[index]);
            maxY = Math.max(maxY, coordinates[index + 1]);
        }
        return floatPrecision ? new Rectangle2D.Float((float) minX, (float) minY, (float) (maxX - minX), (float) (maxY - minY)) : new Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY);
    }

    final PathIterator iterator(AffineTransform transform) {
        return new StoredPathIterator(windingRule, Arrays.copyOf(pointTypes, typeCount), Arrays.copyOf(coordinates, coordinateCount), transform);
    }

    final void trimStorage() {
        pointTypes = Arrays.copyOf(pointTypes, typeCount);
        coordinates = Arrays.copyOf(coordinates, coordinateCount);
    }

    final void copyStorage(Path2D source) {
        windingRule = source.windingRule;
        pointTypes = Arrays.copyOf(source.pointTypes, source.typeCount);
        coordinates = Arrays.copyOf(source.coordinates, source.coordinateCount);
        typeCount = source.typeCount;
        coordinateCount = source.coordinateCount;
    }

    private void addType(int type, int addedCoordinates) {
        ensureTypeCapacity(1);
        ensureCoordinateCapacity(addedCoordinates);
        pointTypes[typeCount++] = (byte) type;
    }

    private void requireCurrentPoint() {
        if (typeCount == 0) {
            throw new IllegalPathStateException("missing initial moveto in path definition");
        }
    }

    private void ensureTypeCapacity(int additional) {
        if (typeCount + additional > pointTypes.length) {
            int capacity = Math.max(typeCount + additional, Math.max(DEFAULT_CAPACITY, pointTypes.length * 2));
            pointTypes = Arrays.copyOf(pointTypes, capacity);
        }
    }

    private void ensureCoordinateCapacity(int additional) {
        if (coordinateCount + additional > coordinates.length) {
            int capacity = Math.max(coordinateCount + additional, Math.max(DEFAULT_CAPACITY * 2, coordinates.length * 2));
            coordinates = Arrays.copyOf(coordinates, capacity);
        }
    }

    private static double store(double value, boolean floatPrecision) {
        return floatPrecision ? (float) value : value;
    }

    private Point2D createPoint(double x, double y) {
        return this instanceof Float ? new Point2D.Float((float) x, (float) y) : new Point2D.Double(x, y);
    }

    private static int coordinateCount(int type) {
        if (type == PathIterator.SEG_CUBICTO) {
            return 6;
        }
        if (type == PathIterator.SEG_QUADTO) {
            return 4;
        }
        return type == PathIterator.SEG_CLOSE ? 0 : 2;
    }

    public static class Float extends Path2D implements Serializable {
        private static final long serialVersionUID = 6990832515060788886L;

        public Float() {
            this(WIND_NON_ZERO, DEFAULT_CAPACITY);
        }

        public Float(int rule) {
            this(rule, DEFAULT_CAPACITY);
        }

        public Float(int rule, int initialCapacity) {
            super(rule, initialCapacity);
        }

        public Float(Shape shape) {
            this(shape, null);
        }

        public Float(Shape shape, AffineTransform transform) {
            this(shape.getPathIterator(transform));
        }

        private Float(PathIterator iterator) {
            this(iterator.getWindingRule(), DEFAULT_CAPACITY);
            append(iterator, false);
        }

        @Override
        public final void trimToSize() {
            trimStorage();
        }

        @Override
        public final void moveTo(double x, double y) {
            addMove(x, y, true);
        }

        public final void moveTo(float x, float y) {
            addMove(x, y, true);
        }

        @Override
        public final void lineTo(double x, double y) {
            addLine(x, y, true);
        }

        public final void lineTo(float x, float y) {
            addLine(x, y, true);
        }

        @Override
        public final void quadTo(double controlX, double controlY, double x, double y) {
            addQuad(controlX, controlY, x, y, true);
        }

        public final void quadTo(float controlX, float controlY, float x, float y) {
            addQuad(controlX, controlY, x, y, true);
        }

        @Override
        public final void curveTo(double controlX1, double controlY1, double controlX2, double controlY2, double x, double y) {
            addCubic(controlX1, controlY1, controlX2, controlY2, x, y, true);
        }

        public final void curveTo(float controlX1, float controlY1, float controlX2, float controlY2, float x, float y) {
            addCubic(controlX1, controlY1, controlX2, controlY2, x, y, true);
        }

        @Override
        public final void append(PathIterator iterator, boolean connect) {
            appendIterator(iterator, connect, true);
        }

        @Override
        public final void transform(AffineTransform transform) {
            transformCoordinates(transform, true);
        }

        @Override
        public final Rectangle2D getBounds2D() {
            return bounds(true);
        }

        @Override
        public final PathIterator getPathIterator(AffineTransform transform) {
            return iterator(transform);
        }

        @Override
        public final Object clone() {
            Float copy = this instanceof GeneralPath ? new GeneralPath(getWindingRule(), 0) : new Float(getWindingRule(), 0);
            copy.copyStorage(this);
            return copy;
        }
    }

    public static class Double extends Path2D implements Serializable {
        private static final long serialVersionUID = 1826762518450014216L;

        public Double() {
            this(WIND_NON_ZERO, DEFAULT_CAPACITY);
        }

        public Double(int rule) {
            this(rule, DEFAULT_CAPACITY);
        }

        public Double(int rule, int initialCapacity) {
            super(rule, initialCapacity);
        }

        public Double(Shape shape) {
            this(shape, null);
        }

        public Double(Shape shape, AffineTransform transform) {
            this(shape.getPathIterator(transform));
        }

        private Double(PathIterator iterator) {
            this(iterator.getWindingRule(), DEFAULT_CAPACITY);
            append(iterator, false);
        }

        @Override
        public final void trimToSize() {
            trimStorage();
        }

        @Override
        public final void moveTo(double x, double y) {
            addMove(x, y, false);
        }

        @Override
        public final void lineTo(double x, double y) {
            addLine(x, y, false);
        }

        @Override
        public final void quadTo(double controlX, double controlY, double x, double y) {
            addQuad(controlX, controlY, x, y, false);
        }

        @Override
        public final void curveTo(double controlX1, double controlY1, double controlX2, double controlY2, double x, double y) {
            addCubic(controlX1, controlY1, controlX2, controlY2, x, y, false);
        }

        @Override
        public final void append(PathIterator iterator, boolean connect) {
            appendIterator(iterator, connect, false);
        }

        @Override
        public final void transform(AffineTransform transform) {
            transformCoordinates(transform, false);
        }

        @Override
        public final Rectangle2D getBounds2D() {
            return bounds(false);
        }

        @Override
        public final PathIterator getPathIterator(AffineTransform transform) {
            return iterator(transform);
        }

        @Override
        public final Object clone() {
            Double copy = new Double(getWindingRule(), 0);
            copy.copyStorage(this);
            return copy;
        }
    }

    private static final class StoredPathIterator implements PathIterator {
        private final int windingRule;
        private final byte[] types;
        private final double[] coordinates;
        private final AffineTransform transform;
        private int typeIndex;
        private int coordinateIndex;

        private StoredPathIterator(int windingRule, byte[] types, double[] coordinates, AffineTransform transform) {
            this.windingRule = windingRule;
            this.types = types;
            this.coordinates = coordinates;
            this.transform = transform;
        }

        @Override
        public int getWindingRule() {
            return windingRule;
        }

        @Override
        public boolean isDone() {
            return typeIndex >= types.length;
        }

        @Override
        public void next() {
            if (!isDone()) {
                coordinateIndex += coordinateCount(types[typeIndex++]);
            }
        }

        @Override
        public int currentSegment(float[] output) {
            double[] values = new double[6];
            int type = currentSegment(values);
            int count = coordinateCount(type);
            for (int index = 0; index < count; index++) {
                output[index] = (float) values[index];
            }
            return type;
        }

        @Override
        public int currentSegment(double[] output) {
            if (isDone()) {
                throw new NoSuchElementException("path iterator out of bounds");
            }
            int type = types[typeIndex];
            int count = coordinateCount(type);
            for (int index = 0; index < count; index++) {
                output[index] = coordinates[coordinateIndex + index];
            }
            if (transform != null && count != 0) {
                transform.transform(output, 0, output, 0, count / 2);
            }
            return type;
        }
    }
}
