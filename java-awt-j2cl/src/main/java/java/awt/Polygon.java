package java.awt;

import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Arrays;

public class Polygon implements Shape, Serializable {
    private static final long serialVersionUID = -6460061437900069969L;
    private static final int MINIMUM_CAPACITY = 4;
    public int npoints;
    public int[] xpoints;
    public int[] ypoints;
    protected Rectangle bounds;

    public Polygon() {
        xpoints = new int[MINIMUM_CAPACITY];
        ypoints = new int[MINIMUM_CAPACITY];
    }

    public Polygon(int[] xpoints, int[] ypoints, int npoints) {
        if (npoints > xpoints.length || npoints > ypoints.length) {
            throw new IndexOutOfBoundsException("npoints exceeds coordinate array length");
        }
        if (npoints < 0) {
            throw new NegativeArraySizeException("npoints must be non-negative");
        }
        this.npoints = npoints;
        this.xpoints = Arrays.copyOf(xpoints, npoints);
        this.ypoints = Arrays.copyOf(ypoints, npoints);
    }

    public void reset() {
        npoints = 0;
        bounds = null;
    }

    public void invalidate() {
        bounds = null;
    }

    public void translate(int deltaX, int deltaY) {
        for (int index = 0; index < npoints; index++) {
            xpoints[index] += deltaX;
            ypoints[index] += deltaY;
        }
        if (bounds != null) {
            bounds.translate(deltaX, deltaY);
        }
    }

    public void addPoint(int x, int y) {
        ensureCapacity(npoints + 1);
        xpoints[npoints] = x;
        ypoints[npoints] = y;
        npoints++;
        if (bounds != null) {
            bounds.add(x, y);
        }
    }

    @Override
    public Rectangle getBounds() {
        if (bounds == null) {
            bounds = calculateBounds();
        }
        return new Rectangle(bounds);
    }

    public Rectangle getBoundingBox() {
        return getBounds();
    }

    public boolean contains(Point point) {
        return contains(point.x, point.y);
    }

    public boolean contains(int x, int y) {
        return contains((double) x, (double) y);
    }

    public boolean inside(int x, int y) {
        return contains(x, y);
    }

    @Override
    public Rectangle2D getBounds2D() {
        return getBounds();
    }

    @Override
    public boolean contains(double x, double y) {
        return npoints > 2 && asPath().contains(x, y);
    }

    @Override
    public boolean contains(Point2D point) {
        return contains(point.getX(), point.getY());
    }

    @Override
    public boolean intersects(double x, double y, double width, double height) {
        return npoints > 0 && asPath().intersects(x, y, width, height);
    }

    @Override
    public boolean intersects(Rectangle2D rectangle) {
        return intersects(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
    }

    @Override
    public boolean contains(double x, double y, double width, double height) {
        return npoints > 0 && asPath().contains(x, y, width, height);
    }

    @Override
    public boolean contains(Rectangle2D rectangle) {
        return contains(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
    }

    @Override
    public PathIterator getPathIterator(AffineTransform transform) {
        return asPath().getPathIterator(transform);
    }

    @Override
    public PathIterator getPathIterator(AffineTransform transform, double flatness) {
        return getPathIterator(transform);
    }

    private void ensureCapacity(int requiredCapacity) {
        if (requiredCapacity <= xpoints.length) {
            return;
        }
        int capacity = Math.max(requiredCapacity, Math.max(MINIMUM_CAPACITY, xpoints.length * 2));
        xpoints = Arrays.copyOf(xpoints, capacity);
        ypoints = Arrays.copyOf(ypoints, capacity);
    }

    private Rectangle calculateBounds() {
        if (npoints == 0) {
            return new Rectangle();
        }
        int minX = xpoints[0];
        int minY = ypoints[0];
        int maxX = minX;
        int maxY = minY;
        for (int index = 1; index < npoints; index++) {
            minX = Math.min(minX, xpoints[index]);
            minY = Math.min(minY, ypoints[index]);
            maxX = Math.max(maxX, xpoints[index]);
            maxY = Math.max(maxY, ypoints[index]);
        }
        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }

    private Path2D asPath() {
        Path2D.Double path = new Path2D.Double(Path2D.WIND_EVEN_ODD, npoints);
        if (npoints == 0) {
            return path;
        }
        path.moveTo(xpoints[0], ypoints[0]);
        for (int index = 1; index < npoints; index++) {
            path.lineTo(xpoints[index], ypoints[index]);
        }
        path.closePath();
        return path;
    }
}
