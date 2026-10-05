package java.awt.geom;

import java.io.Serializable;
import java.util.NoSuchElementException;

public abstract class Arc2D extends RectangularShape {
    public static final int OPEN = 0;
    public static final int CHORD = 1;
    public static final int PIE = 2;
    private int type;

    protected Arc2D() {
        this(OPEN);
    }

    protected Arc2D(int type) {
        setArcType(type);
    }

    public abstract double getAngleStart();

    public abstract double getAngleExtent();

    public int getArcType() {
        return type;
    }

    public Point2D getStartPoint() {
        double angle = Math.toRadians(getAngleStart());
        return new Point2D.Double(getX() + (Math.cos(angle) * 0.5 + 0.5) * getWidth(), getY() + (0.5 - Math.sin(angle) * 0.5) * getHeight());
    }

    public Point2D getEndPoint() {
        double angle = Math.toRadians(getAngleStart() + getAngleExtent());
        return new Point2D.Double(getX() + (Math.cos(angle) * 0.5 + 0.5) * getWidth(), getY() + (0.5 - Math.sin(angle) * 0.5) * getHeight());
    }

    public abstract void setArc(double x, double y, double width, double height, double start, double extent, int type);

    public void setArc(Point2D location, Dimension2D size, double start, double extent, int type) {
        setArc(location.getX(), location.getY(), size.getWidth(), size.getHeight(), start, extent, type);
    }

    public void setArc(Rectangle2D rectangle, double start, double extent, int type) {
        setArc(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight(), start, extent, type);
    }

    public void setArc(Arc2D arc) {
        setArc(arc.getX(), arc.getY(), arc.getWidth(), arc.getHeight(), arc.getAngleStart(), arc.getAngleExtent(), arc.getArcType());
    }

    public void setArcByCenter(double centerX, double centerY, double radius, double start, double extent, int type) {
        setArc(centerX - radius, centerY - radius, radius * 2.0, radius * 2.0, start, extent, type);
    }

    public void setArcByTangent(Point2D first, Point2D vertex, Point2D second, double radius) {
        double firstAngle = Math.atan2(first.getY() - vertex.getY(), first.getX() - vertex.getX());
        double secondAngle = Math.atan2(second.getY() - vertex.getY(), second.getX() - vertex.getX());
        double difference = normalizeRadians(secondAngle - firstAngle);
        double bisector = firstAngle + difference / 2.0;
        double distance = radius / Math.sin(Math.abs(difference) / 2.0);
        double centerX = vertex.getX() + Math.cos(bisector) * distance;
        double centerY = vertex.getY() + Math.sin(bisector) * distance;
        double tangentOffset = difference > 0.0 ? -Math.PI / 2.0 : Math.PI / 2.0;
        double start = -Math.toDegrees(firstAngle + tangentOffset);
        double end = -Math.toDegrees(secondAngle - tangentOffset);
        double extent = end - start;
        if (difference > 0.0 && extent < 0.0) {
            extent += 360.0;
        } else if (difference < 0.0 && extent > 0.0) {
            extent -= 360.0;
        }
        setArcByCenter(centerX, centerY, radius, start, extent, type);
    }

    protected abstract Rectangle2D makeBounds(double x, double y, double width, double height);

    public abstract void setAngleStart(double angle);

    public abstract void setAngleExtent(double angle);

    public void setAngleStart(Point2D point) {
        double x = (point.getX() - getCenterX()) * getHeight();
        double y = (point.getY() - getCenterY()) * getWidth();
        setAngleStart(-Math.toDegrees(Math.atan2(y, x)));
    }

    public void setAngles(double x1, double y1, double x2, double y2) {
        double width = getWidth();
        double height = getHeight();
        double start = Math.atan2((getCenterY() - y1) * width, (x1 - getCenterX()) * height);
        double end = Math.atan2((getCenterY() - y2) * width, (x2 - getCenterX()) * height);
        double extent = Math.toDegrees(end - start);
        if (extent <= 0.0) {
            extent += 360.0;
        }
        setAngleStart(Math.toDegrees(start));
        setAngleExtent(extent);
    }

    public void setAngles(Point2D first, Point2D second) {
        setAngles(first.getX(), first.getY(), second.getX(), second.getY());
    }

    public void setArcType(int type) {
        if (type < OPEN || type > PIE) {
            throw new IllegalArgumentException("invalid type for Arc: " + type);
        }
        this.type = type;
    }

    @Override
    public void setFrame(double x, double y, double width, double height) {
        setArc(x, y, width, height, getAngleStart(), getAngleExtent(), type);
    }

    @Override
    public Rectangle2D getBounds2D() {
        if (isEmpty()) {
            return makeBounds(getX(), getY(), getWidth(), getHeight());
        }
        double minimumX = type == PIE ? 0.0 : 1.0;
        double minimumY = type == PIE ? 0.0 : 1.0;
        double maximumX = type == PIE ? 0.0 : -1.0;
        double maximumY = type == PIE ? 0.0 : -1.0;
        double startRadians = Math.toRadians(getAngleStart());
        double endRadians = Math.toRadians(getAngleStart() + getAngleExtent());
        minimumX = Math.min(minimumX, Math.min(Math.cos(startRadians), Math.cos(endRadians)));
        minimumY = Math.min(minimumY, Math.min(-Math.sin(startRadians), -Math.sin(endRadians)));
        maximumX = Math.max(maximumX, Math.max(Math.cos(startRadians), Math.cos(endRadians)));
        maximumY = Math.max(maximumY, Math.max(-Math.sin(startRadians), -Math.sin(endRadians)));
        if (containsAngle(0.0)) {
            maximumX = 1.0;
        }
        if (containsAngle(90.0)) {
            minimumY = -1.0;
        }
        if (containsAngle(180.0)) {
            minimumX = -1.0;
        }
        if (containsAngle(270.0)) {
            maximumY = 1.0;
        }
        double x = getX() + (minimumX + 1.0) * getWidth() / 2.0;
        double y = getY() + (minimumY + 1.0) * getHeight() / 2.0;
        return makeBounds(x, y, (maximumX - minimumX) * getWidth() / 2.0, (maximumY - minimumY) * getHeight() / 2.0);
    }

    public boolean containsAngle(double angle) {
        double extent = getAngleExtent();
        boolean backwards = extent < 0.0;
        double relative = normalizeDegrees(angle) - normalizeDegrees(getAngleStart());
        if (relative < 0.0) {
            relative += 360.0;
        }
        if (backwards && relative != 0.0) {
            relative = 360.0 - relative;
        }
        return relative >= 0.0 && relative < Math.abs(extent);
    }

    @Override
    public boolean contains(double x, double y) {
        double width = getWidth();
        double height = getHeight();
        if (width <= 0.0 || height <= 0.0) {
            return false;
        }
        double normalizedX = (x - getX()) / width - 0.5;
        double normalizedY = (y - getY()) / height - 0.5;
        if (normalizedX * normalizedX + normalizedY * normalizedY >= 0.25) {
            return false;
        }
        double angle = -Math.toDegrees(Math.atan2(normalizedY, normalizedX));
        boolean withinArc = containsAngle(angle);
        if (type == PIE) {
            return withinArc;
        }
        double extent = Math.abs(getAngleExtent());
        if (withinArc && extent >= 180.0) {
            return true;
        }
        if (!withinArc && extent <= 180.0) {
            return false;
        }
        double start = Math.toRadians(-getAngleStart());
        double end = Math.toRadians(-getAngleStart() - getAngleExtent());
        int pointSide = Line2D.relativeCCW(Math.cos(start), Math.sin(start), Math.cos(end), Math.sin(end), normalizedX * 2.0, normalizedY * 2.0);
        int centerSide = Line2D.relativeCCW(Math.cos(start), Math.sin(start), Math.cos(end), Math.sin(end), 0.0, 0.0);
        boolean sameSideAsCenter = pointSide * centerSide >= 0;
        return withinArc ? !sameSideAsCenter : sameSideAsCenter;
    }

    @Override
    public boolean intersects(double x, double y, double width, double height) {
        if (getAngleExtent() == 0.0 || width <= 0.0 || height <= 0.0 || isEmpty()) {
            return false;
        }
        if (contains(x, y) || contains(x + width, y) || contains(x, y + height) || contains(x + width, y + height)) {
            return true;
        }
        return PathSupport.intersects(getPathIterator(null), x, y, width, height);
    }

    @Override
    public boolean contains(double x, double y, double width, double height) {
        return PathSupport.contains(getPathIterator(null), x, y, width, height);
    }

    @Override
    public boolean contains(Rectangle2D rectangle) {
        return contains(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
    }

    @Override
    public PathIterator getPathIterator(AffineTransform transform) {
        return new ArcIterator(this, transform);
    }

    @Override
    public int hashCode() {
        long bits = java.lang.Double.doubleToLongBits(getX());
        bits += java.lang.Double.doubleToLongBits(getY()) * 37;
        bits += java.lang.Double.doubleToLongBits(getWidth()) * 43;
        bits += java.lang.Double.doubleToLongBits(getHeight()) * 47;
        bits += java.lang.Double.doubleToLongBits(getAngleStart()) * 53;
        bits += java.lang.Double.doubleToLongBits(getAngleExtent()) * 59;
        bits += getArcType() * 61L;
        return (int) bits ^ (int) (bits >> 32);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Arc2D)) {
            return false;
        }
        Arc2D arc = (Arc2D) object;
        return getX() == arc.getX() && getY() == arc.getY() && getWidth() == arc.getWidth() && getHeight() == arc.getHeight() && getAngleStart() == arc.getAngleStart() && getAngleExtent() == arc.getAngleExtent() && getArcType() == arc.getArcType();
    }

    private static double normalizeDegrees(double angle) {
        double normalized = angle % 360.0;
        return normalized < 0.0 ? normalized + 360.0 : normalized;
    }

    private static double normalizeRadians(double angle) {
        double normalized = angle % (Math.PI * 2.0);
        if (normalized <= -Math.PI) {
            normalized += Math.PI * 2.0;
        } else if (normalized > Math.PI) {
            normalized -= Math.PI * 2.0;
        }
        return normalized;
    }

    public static class Float extends Arc2D implements Serializable {
        protected Rectangle2D makeBounds(double x, double y, double width, double height) { return new Rectangle2D.Float((float) x, (float) y, (float) width, (float) height); }
        private static final long serialVersionUID = 9130893014586380278L;
        public float x;
        public float y;
        public float width;
        public float height;
        public float start;
        public float extent;

        public Float() {
        }

        public Float(int type) {
            super(type);
        }

        public Float(float x, float y, float width, float height, float start, float extent, int type) {
            super(type);
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.start = start;
            this.extent = extent;
        }

        public Float(Rectangle2D rectangle, float start, float extent, int type) {
            this((float) rectangle.getX(), (float) rectangle.getY(), (float) rectangle.getWidth(), (float) rectangle.getHeight(), start, extent, type);
        }

        @Override
        public double getX() {
            return x;
        }

        @Override
        public double getY() {
            return y;
        }

        @Override
        public double getWidth() {
            return width;
        }

        @Override
        public double getHeight() {
            return height;
        }

        @Override
        public double getAngleStart() {
            return start;
        }

        @Override
        public double getAngleExtent() {
            return extent;
        }

        @Override
        public boolean isEmpty() {
            return width <= 0.0f || height <= 0.0f;
        }

        @Override
        public void setArc(double x, double y, double width, double height, double start, double extent, int type) {
            setArcType(type);
            this.x = (float) x;
            this.y = (float) y;
            this.width = (float) width;
            this.height = (float) height;
            this.start = (float) start;
            this.extent = (float) extent;
        }

        @Override
        public void setAngleStart(double angle) {
            start = (float) angle;
        }

        @Override
        public void setAngleExtent(double angle) {
            extent = (float) angle;
        }

        @Override
        public Object clone() {
            return new Float(x, y, width, height, start, extent, getArcType());
        }
    }

    public static class Double extends Arc2D implements Serializable {
        protected Rectangle2D makeBounds(double x, double y, double width, double height) { return new Rectangle2D.Double(x, y, width, height); }
        private static final long serialVersionUID = 728264085846882001L;
        public double x;
        public double y;
        public double width;
        public double height;
        public double start;
        public double extent;

        public Double() {
        }

        public Double(int type) {
            super(type);
        }

        public Double(double x, double y, double width, double height, double start, double extent, int type) {
            super(type);
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.start = start;
            this.extent = extent;
        }

        public Double(Rectangle2D rectangle, double start, double extent, int type) {
            this(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight(), start, extent, type);
        }

        @Override
        public double getX() {
            return x;
        }

        @Override
        public double getY() {
            return y;
        }

        @Override
        public double getWidth() {
            return width;
        }

        @Override
        public double getHeight() {
            return height;
        }

        @Override
        public double getAngleStart() {
            return start;
        }

        @Override
        public double getAngleExtent() {
            return extent;
        }

        @Override
        public boolean isEmpty() {
            return width <= 0.0 || height <= 0.0;
        }

        @Override
        public void setArc(double x, double y, double width, double height, double start, double extent, int type) {
            setArcType(type);
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.start = start;
            this.extent = extent;
        }

        @Override
        public void setAngleStart(double angle) {
            start = angle;
        }

        @Override
        public void setAngleExtent(double angle) {
            extent = angle;
        }

        @Override
        public Object clone() {
            return new Double(x, y, width, height, start, extent, getArcType());
        }
    }

    private static final class ArcIterator implements PathIterator {
        private final Arc2D arc;
        private final AffineTransform transform;
        private final int curveCount;
        private final double startRadians;
        private final double increment;
        private int index;

        private ArcIterator(Arc2D arc, AffineTransform transform) {
            this.arc = arc;
            this.transform = transform;
            double extent = Math.max(-360.0, Math.min(360.0, arc.getAngleExtent()));
            curveCount = extent == 0.0 ? 0 : (int) Math.ceil(Math.abs(extent) / 90.0);
            startRadians = Math.toRadians(arc.getAngleStart());
            increment = curveCount == 0 ? 0.0 : Math.toRadians(extent / curveCount);
            if (arc.getWidth() < 0.0 || arc.getHeight() < 0.0) {
                index = segmentCount();
            }
        }

        @Override
        public int getWindingRule() {
            return WIND_NON_ZERO;
        }

        @Override
        public boolean isDone() {
            return index >= segmentCount();
        }

        @Override
        public void next() {
            index++;
        }

        @Override
        public int currentSegment(float[] coordinates) {
            double[] values = new double[6];
            int type = currentSegment(values);
            int count = type == SEG_CUBICTO ? 6 : type == SEG_CLOSE ? 0 : 2;
            for (int valueIndex = 0; valueIndex < count; valueIndex++) {
                coordinates[valueIndex] = (float) values[valueIndex];
            }
            return type;
        }

        @Override
        public int currentSegment(double[] coordinates) {
            if (isDone()) {
                throw new NoSuchElementException("arc iterator out of bounds");
            }
            if (index == 0) {
                setEllipsePoint(coordinates, startRadians);
                transform(coordinates, 1);
                return SEG_MOVETO;
            }
            if (index <= curveCount) {
                setCurve(coordinates, startRadians + increment * (index - 1), increment);
                transform(coordinates, 3);
                return SEG_CUBICTO;
            }
            if (arc.getArcType() == PIE && index == curveCount + 1) {
                coordinates[0] = arc.getCenterX();
                coordinates[1] = arc.getCenterY();
                transform(coordinates, 1);
                return SEG_LINETO;
            }
            return SEG_CLOSE;
        }

        private int segmentCount() {
            if (arc.getArcType() == OPEN) {
                return curveCount + 1;
            }
            return arc.getArcType() == CHORD ? curveCount + 2 : curveCount + 3;
        }

        private void setCurve(double[] output, double angle, double delta) {
            double radiusX = arc.getWidth() / 2.0;
            double radiusY = arc.getHeight() / 2.0;
            double centerX = arc.getCenterX();
            double centerY = arc.getCenterY();
            double end = angle + delta;
            double control = 4.0 / 3.0 * Math.tan(delta / 4.0);
            output[0] = centerX + radiusX * (Math.cos(angle) - control * Math.sin(angle));
            output[1] = centerY - radiusY * (Math.sin(angle) + control * Math.cos(angle));
            output[2] = centerX + radiusX * (Math.cos(end) + control * Math.sin(end));
            output[3] = centerY - radiusY * (Math.sin(end) - control * Math.cos(end));
            output[4] = centerX + radiusX * Math.cos(end);
            output[5] = centerY - radiusY * Math.sin(end);
        }

        private void setEllipsePoint(double[] output, double angle) {
            output[0] = arc.getCenterX() + arc.getWidth() / 2.0 * Math.cos(angle);
            output[1] = arc.getCenterY() - arc.getHeight() / 2.0 * Math.sin(angle);
        }

        private void transform(double[] coordinates, int pointCount) {
            if (transform != null) {
                transform.transform(coordinates, 0, coordinates, 0, pointCount);
            }
        }
    }
}
