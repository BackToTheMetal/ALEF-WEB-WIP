package java.awt.geom;

import java.io.Serializable;
import java.util.NoSuchElementException;

public abstract class Rectangle2D extends RectangularShape {
    public static final int OUT_LEFT = 1;
    public static final int OUT_TOP = 2;
    public static final int OUT_RIGHT = 4;
    public static final int OUT_BOTTOM = 8;

    protected Rectangle2D() {
    }

    public abstract void setRect(double x, double y, double width, double height);

    public void setRect(Rectangle2D rectangle) {
        setRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
    }

    public boolean intersectsLine(double x1, double y1, double x2, double y2) {
        int secondOutcode = outcode(x2, y2);
        if (secondOutcode == 0) {
            return true;
        }
        int firstOutcode;
        while ((firstOutcode = outcode(x1, y1)) != 0) {
            if ((firstOutcode & secondOutcode) != 0) {
                return false;
            }
            if ((firstOutcode & (OUT_LEFT | OUT_RIGHT)) != 0) {
                double x = (firstOutcode & OUT_RIGHT) != 0 ? getX() + getWidth() : getX();
                y1 += (x - x1) * (y2 - y1) / (x2 - x1);
                x1 = x;
            } else {
                double y = (firstOutcode & OUT_BOTTOM) != 0 ? getY() + getHeight() : getY();
                x1 += (y - y1) * (x2 - x1) / (y2 - y1);
                y1 = y;
            }
        }
        return true;
    }

    public boolean intersectsLine(Line2D line) {
        return intersectsLine(line.getX1(), line.getY1(), line.getX2(), line.getY2());
    }

    public abstract int outcode(double x, double y);

    public int outcode(Point2D point) {
        return outcode(point.getX(), point.getY());
    }

    @Override
    public void setFrame(double x, double y, double width, double height) {
        setRect(x, y, width, height);
    }

    @Override
    public Rectangle2D getBounds2D() {
        return new Double(getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public boolean contains(double x, double y) {
        double width = getWidth();
        double height = getHeight();
        return width > 0.0 && height > 0.0 && x >= getX() && y >= getY() && x < getX() + width && y < getY() + height;
    }

    @Override
    public boolean intersects(double x, double y, double width, double height) {
        double ownWidth = getWidth();
        double ownHeight = getHeight();
        return ownWidth > 0.0 && ownHeight > 0.0 && width > 0.0 && height > 0.0 && x + width > getX() && y + height > getY() && x < getX() + ownWidth && y < getY() + ownHeight;
    }

    @Override
    public boolean contains(double x, double y, double width, double height) {
        double ownWidth = getWidth();
        double ownHeight = getHeight();
        return ownWidth > 0.0 && ownHeight > 0.0 && width >= 0.0 && height >= 0.0 && x >= getX() && y >= getY() && x + width <= getX() + ownWidth && y + height <= getY() + ownHeight;
    }

    public abstract Rectangle2D createIntersection(Rectangle2D rectangle);

    public static void intersect(Rectangle2D first, Rectangle2D second, Rectangle2D destination) {
        double x = Math.max(first.getMinX(), second.getMinX());
        double y = Math.max(first.getMinY(), second.getMinY());
        destination.setFrame(x, y, Math.min(first.getMaxX(), second.getMaxX()) - x, Math.min(first.getMaxY(), second.getMaxY()) - y);
    }

    public abstract Rectangle2D createUnion(Rectangle2D rectangle);

    public static void union(Rectangle2D first, Rectangle2D second, Rectangle2D destination) {
        double x = Math.min(first.getMinX(), second.getMinX());
        double y = Math.min(first.getMinY(), second.getMinY());
        destination.setFrameFromDiagonal(x, y, Math.max(first.getMaxX(), second.getMaxX()), Math.max(first.getMaxY(), second.getMaxY()));
    }

    public void add(double x, double y) {
        double minX = Math.min(getMinX(), x);
        double minY = Math.min(getMinY(), y);
        setRect(minX, minY, Math.max(getMaxX(), x) - minX, Math.max(getMaxY(), y) - minY);
    }

    public void add(Point2D point) {
        add(point.getX(), point.getY());
    }

    public void add(Rectangle2D rectangle) {
        double minX = Math.min(getMinX(), rectangle.getMinX());
        double minY = Math.min(getMinY(), rectangle.getMinY());
        setRect(minX, minY, Math.max(getMaxX(), rectangle.getMaxX()) - minX, Math.max(getMaxY(), rectangle.getMaxY()) - minY);
    }

    @Override
    public PathIterator getPathIterator(AffineTransform transform) {
        return new RectangleIterator(this, transform);
    }

    @Override
    public PathIterator getPathIterator(AffineTransform transform, double flatness) {
        return new RectangleIterator(this, transform);
    }

    @Override
    public int hashCode() {
        long bits = java.lang.Double.doubleToLongBits(getX());
        bits += java.lang.Double.doubleToLongBits(getY()) * 37;
        bits += java.lang.Double.doubleToLongBits(getWidth()) * 43;
        bits += java.lang.Double.doubleToLongBits(getHeight()) * 47;
        return (int) bits ^ (int) (bits >> 32);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Rectangle2D)) {
            return false;
        }
        Rectangle2D rectangle = (Rectangle2D) object;
        return getX() == rectangle.getX() && getY() == rectangle.getY() && getWidth() == rectangle.getWidth() && getHeight() == rectangle.getHeight();
    }

    public static class Float extends Rectangle2D implements Serializable {
        private static final long serialVersionUID = 3798716824173675777L;
        public float x;
        public float y;
        public float width;
        public float height;

        public Float() {
        }

        public Float(float x, float y, float width, float height) {
            setRect(x, y, width, height);
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
        public boolean isEmpty() {
            return width <= 0.0f || height <= 0.0f;
        }

        @Override
        public void setRect(double x, double y, double width, double height) {
            this.x = (float) x;
            this.y = (float) y;
            this.width = (float) width;
            this.height = (float) height;
        }

        public void setRect(float x, float y, float width, float height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        @Override
        public int outcode(double x, double y) {
            return calculateOutcode(x, y, this.x, this.y, width, height);
        }

        @Override
        public Rectangle2D getBounds2D() {
            return new Float(x, y, width, height);
        }

        @Override
        public Rectangle2D createIntersection(Rectangle2D rectangle) {
            Rectangle2D result = new Float();
            intersect(this, rectangle, result);
            return result;
        }

        @Override
        public Rectangle2D createUnion(Rectangle2D rectangle) {
            Rectangle2D result = new Float();
            union(this, rectangle, result);
            return result;
        }

        @Override
        public Object clone() {
            return new Float(x, y, width, height);
        }

        @Override
        public String toString() {
            return getClass().getName() + "[x=" + GeometryStrings.format(x) + ",y=" + GeometryStrings.format(y) + ",w=" + GeometryStrings.format(width) + ",h=" + GeometryStrings.format(height) + "]";
        }
    }

    public static class Double extends Rectangle2D implements Serializable {
        private static final long serialVersionUID = 7771313791441850493L;
        public double x;
        public double y;
        public double width;
        public double height;

        public Double() {
        }

        public Double(double x, double y, double width, double height) {
            setRect(x, y, width, height);
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
        public boolean isEmpty() {
            return width <= 0.0 || height <= 0.0;
        }

        @Override
        public void setRect(double x, double y, double width, double height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        @Override
        public int outcode(double x, double y) {
            return calculateOutcode(x, y, this.x, this.y, width, height);
        }

        @Override
        public Rectangle2D getBounds2D() {
            return new Double(x, y, width, height);
        }

        @Override
        public Rectangle2D createIntersection(Rectangle2D rectangle) {
            Rectangle2D result = new Double();
            intersect(this, rectangle, result);
            return result;
        }

        @Override
        public Rectangle2D createUnion(Rectangle2D rectangle) {
            Rectangle2D result = new Double();
            union(this, rectangle, result);
            return result;
        }

        @Override
        public Object clone() {
            return new Double(x, y, width, height);
        }

        @Override
        public String toString() {
            return getClass().getName() + "[x=" + GeometryStrings.format(x) + ",y=" + GeometryStrings.format(y) + ",w=" + GeometryStrings.format(width) + ",h=" + GeometryStrings.format(height) + "]";
        }
    }

    private static int calculateOutcode(double pointX, double pointY, double x, double y, double width, double height) {
        int code = 0;
        if (width <= 0.0) {
            code |= OUT_LEFT | OUT_RIGHT;
        } else if (pointX < x) {
            code |= OUT_LEFT;
        } else if (pointX > x + width) {
            code |= OUT_RIGHT;
        }
        if (height <= 0.0) {
            code |= OUT_TOP | OUT_BOTTOM;
        } else if (pointY < y) {
            code |= OUT_TOP;
        } else if (pointY > y + height) {
            code |= OUT_BOTTOM;
        }
        return code;
    }

    private static final class RectangleIterator implements PathIterator {
        private final Rectangle2D rectangle;
        private final AffineTransform transform;
        private int index;

        private RectangleIterator(Rectangle2D rectangle, AffineTransform transform) {
            this.rectangle = rectangle;
            this.transform = transform;
            if (rectangle.getWidth() < 0.0 || rectangle.getHeight() < 0.0) {
                index = 6;
            }
        }

        @Override
        public int getWindingRule() {
            return WIND_NON_ZERO;
        }

        @Override
        public boolean isDone() {
            return index > 5;
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
                throw new NoSuchElementException("rectangle iterator out of bounds");
            }
            if (index == 5) {
                return SEG_CLOSE;
            }
            coordinates[0] = rectangle.getX();
            coordinates[1] = rectangle.getY();
            if (index == 1 || index == 2) {
                coordinates[0] += rectangle.getWidth();
            }
            if (index == 2 || index == 3) {
                coordinates[1] += rectangle.getHeight();
            }
            if (transform != null) {
                transform.transform(coordinates, 0, coordinates, 0, 1);
            }
            return index == 0 ? SEG_MOVETO : SEG_LINETO;
        }
    }
}
