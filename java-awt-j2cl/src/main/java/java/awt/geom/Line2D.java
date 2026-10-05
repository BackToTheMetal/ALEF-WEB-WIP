package java.awt.geom;

import java.awt.Rectangle;
import java.awt.Shape;
import java.io.Serializable;
import java.util.NoSuchElementException;

public abstract class Line2D implements Shape, Cloneable {
    protected Line2D() {
    }

    public abstract double getX1();

    public abstract double getY1();

    public abstract Point2D getP1();

    public abstract double getX2();

    public abstract double getY2();

    public abstract Point2D getP2();

    public abstract void setLine(double x1, double y1, double x2, double y2);

    public void setLine(Point2D first, Point2D second) {
        setLine(first.getX(), first.getY(), second.getX(), second.getY());
    }

    public void setLine(Line2D line) {
        setLine(line.getX1(), line.getY1(), line.getX2(), line.getY2());
    }

    public static int relativeCCW(double x1, double y1, double x2, double y2, double pointX, double pointY) {
        x2 -= x1;
        y2 -= y1;
        pointX -= x1;
        pointY -= y1;
        double comparison = pointX * y2 - pointY * x2;
        if (comparison == 0.0) {
            comparison = pointX * x2 + pointY * y2;
            if (comparison > 0.0) {
                pointX -= x2;
                pointY -= y2;
                comparison = pointX * x2 + pointY * y2;
                if (comparison < 0.0) {
                    comparison = 0.0;
                }
            }
        }
        return comparison < 0.0 ? -1 : comparison > 0.0 ? 1 : 0;
    }

    public int relativeCCW(double x, double y) {
        return relativeCCW(getX1(), getY1(), getX2(), getY2(), x, y);
    }

    public int relativeCCW(Point2D point) {
        return relativeCCW(point.getX(), point.getY());
    }

    public static boolean linesIntersect(double firstX1, double firstY1, double firstX2, double firstY2, double secondX1, double secondY1, double secondX2, double secondY2) {
        return relativeCCW(firstX1, firstY1, firstX2, firstY2, secondX1, secondY1) * relativeCCW(firstX1, firstY1, firstX2, firstY2, secondX2, secondY2) <= 0 && relativeCCW(secondX1, secondY1, secondX2, secondY2, firstX1, firstY1) * relativeCCW(secondX1, secondY1, secondX2, secondY2, firstX2, firstY2) <= 0;
    }

    public boolean intersectsLine(double x1, double y1, double x2, double y2) {
        return linesIntersect(x1, y1, x2, y2, getX1(), getY1(), getX2(), getY2());
    }

    public boolean intersectsLine(Line2D line) {
        return linesIntersect(line.getX1(), line.getY1(), line.getX2(), line.getY2(), getX1(), getY1(), getX2(), getY2());
    }

    public static double ptSegDistSq(double x1, double y1, double x2, double y2, double pointX, double pointY) {
        double lineX = x2 - x1;
        double lineY = y2 - y1;
        double relativeX = pointX - x1;
        double relativeY = pointY - y1;
        double projection = relativeX * lineX + relativeY * lineY;
        if (projection <= 0.0) {
            return relativeX * relativeX + relativeY * relativeY;
        }
        double lengthSquared = lineX * lineX + lineY * lineY;
        if (projection >= lengthSquared) {
            relativeX = pointX - x2;
            relativeY = pointY - y2;
            return relativeX * relativeX + relativeY * relativeY;
        }
        double cross = relativeX * lineY - relativeY * lineX;
        return cross * cross / lengthSquared;
    }

    public static double ptSegDist(double x1, double y1, double x2, double y2, double pointX, double pointY) {
        return Math.sqrt(ptSegDistSq(x1, y1, x2, y2, pointX, pointY));
    }

    public double ptSegDistSq(double x, double y) {
        return ptSegDistSq(getX1(), getY1(), getX2(), getY2(), x, y);
    }

    public double ptSegDistSq(Point2D point) {
        return ptSegDistSq(point.getX(), point.getY());
    }

    public double ptSegDist(double x, double y) {
        return Math.sqrt(ptSegDistSq(x, y));
    }

    public double ptSegDist(Point2D point) {
        return ptSegDist(point.getX(), point.getY());
    }

    public static double ptLineDistSq(double x1, double y1, double x2, double y2, double pointX, double pointY) {
        double lineX = x2 - x1;
        double lineY = y2 - y1;
        double relativeX = pointX - x1;
        double relativeY = pointY - y1;
        double cross = relativeX * lineY - relativeY * lineX;
        return cross * cross / (lineX * lineX + lineY * lineY);
    }

    public static double ptLineDist(double x1, double y1, double x2, double y2, double pointX, double pointY) {
        return Math.sqrt(ptLineDistSq(x1, y1, x2, y2, pointX, pointY));
    }

    public double ptLineDistSq(double x, double y) {
        return ptLineDistSq(getX1(), getY1(), getX2(), getY2(), x, y);
    }

    public double ptLineDistSq(Point2D point) {
        return ptLineDistSq(point.getX(), point.getY());
    }

    public double ptLineDist(double x, double y) {
        return Math.sqrt(ptLineDistSq(x, y));
    }

    public double ptLineDist(Point2D point) {
        return ptLineDist(point.getX(), point.getY());
    }

    @Override
    public boolean contains(double x, double y) {
        return false;
    }

    @Override
    public boolean contains(Point2D point) {
        return false;
    }

    @Override
    public boolean intersects(double x, double y, double width, double height) {
        return new Rectangle2D.Double(x, y, width, height).intersectsLine(this);
    }

    @Override
    public boolean intersects(Rectangle2D rectangle) {
        return rectangle.intersectsLine(this);
    }

    @Override
    public boolean contains(double x, double y, double width, double height) {
        return false;
    }

    @Override
    public boolean contains(Rectangle2D rectangle) {
        return false;
    }

    @Override
    public Rectangle getBounds() {
        return getBounds2D().getBounds();
    }

    @Override
    public Rectangle2D getBounds2D() {
        double x = Math.min(getX1(), getX2());
        double y = Math.min(getY1(), getY2());
        return new Rectangle2D.Double(x, y, Math.max(getX1(), getX2()) - x, Math.max(getY1(), getY2()) - y);
    }

    @Override
    public PathIterator getPathIterator(AffineTransform transform) {
        return new LineIterator(this, transform);
    }

    @Override
    public PathIterator getPathIterator(AffineTransform transform, double flatness) {
        return new LineIterator(this, transform);
    }

    public Object clone() {
        return new Double(getX1(), getY1(), getX2(), getY2());
    }

    public static class Float extends Line2D implements Serializable {
        private static final long serialVersionUID = 6161772511649436349L;
        public float x1;
        public float y1;
        public float x2;
        public float y2;

        public Float() {
        }

        public Float(float x1, float y1, float x2, float y2) {
            setLine(x1, y1, x2, y2);
        }

        public Float(Point2D first, Point2D second) {
            setLine(first, second);
        }

        @Override
        public double getX1() {
            return x1;
        }

        @Override
        public double getY1() {
            return y1;
        }

        @Override
        public Point2D getP1() {
            return new Point2D.Float(x1, y1);
        }

        @Override
        public double getX2() {
            return x2;
        }

        @Override
        public double getY2() {
            return y2;
        }

        @Override
        public Point2D getP2() {
            return new Point2D.Float(x2, y2);
        }

        @Override
        public void setLine(double x1, double y1, double x2, double y2) {
            this.x1 = (float) x1;
            this.y1 = (float) y1;
            this.x2 = (float) x2;
            this.y2 = (float) y2;
        }

        public void setLine(float x1, float y1, float x2, float y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }

        @Override
        public Object clone() {
            return new Float(x1, y1, x2, y2);
        }
    }

    public static class Double extends Line2D implements Serializable {
        private static final long serialVersionUID = 7979627399746467499L;
        public double x1;
        public double y1;
        public double x2;
        public double y2;

        public Double() {
        }

        public Double(double x1, double y1, double x2, double y2) {
            setLine(x1, y1, x2, y2);
        }

        public Double(Point2D first, Point2D second) {
            setLine(first, second);
        }

        @Override
        public double getX1() {
            return x1;
        }

        @Override
        public double getY1() {
            return y1;
        }

        @Override
        public Point2D getP1() {
            return new Point2D.Double(x1, y1);
        }

        @Override
        public double getX2() {
            return x2;
        }

        @Override
        public double getY2() {
            return y2;
        }

        @Override
        public Point2D getP2() {
            return new Point2D.Double(x2, y2);
        }

        @Override
        public void setLine(double x1, double y1, double x2, double y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }

        @Override
        public Object clone() {
            return new Double(x1, y1, x2, y2);
        }
    }

    private static final class LineIterator implements PathIterator {
        private final Line2D line;
        private final AffineTransform transform;
        private int index;

        private LineIterator(Line2D line, AffineTransform transform) {
            this.line = line;
            this.transform = transform;
        }

        @Override
        public int getWindingRule() {
            return WIND_NON_ZERO;
        }

        @Override
        public boolean isDone() {
            return index > 1;
        }

        @Override
        public void next() {
            index++;
        }

        @Override
        public int currentSegment(float[] coordinates) {
            double[] values = new double[2];
            int segment = currentSegment(values);
            coordinates[0] = (float) values[0];
            coordinates[1] = (float) values[1];
            return segment;
        }

        @Override
        public int currentSegment(double[] coordinates) {
            if (isDone()) {
                throw new NoSuchElementException("line iterator out of bounds");
            }
            coordinates[0] = index == 0 ? line.getX1() : line.getX2();
            coordinates[1] = index == 0 ? line.getY1() : line.getY2();
            if (transform != null) {
                transform.transform(coordinates, 0, coordinates, 0, 1);
            }
            return index == 0 ? SEG_MOVETO : SEG_LINETO;
        }
    }
}
