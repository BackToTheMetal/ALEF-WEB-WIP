package java.awt.geom;

import java.awt.Rectangle;
import java.awt.Shape;

public abstract class RectangularShape implements Shape, Cloneable {
    protected RectangularShape() {
    }

    public abstract double getX();

    public abstract double getY();

    public abstract double getWidth();

    public abstract double getHeight();

    public double getMinX() {
        return getX();
    }

    public double getMinY() {
        return getY();
    }

    public double getMaxX() {
        return getX() + getWidth();
    }

    public double getMaxY() {
        return getY() + getHeight();
    }

    public double getCenterX() {
        return getX() + getWidth() / 2.0;
    }

    public double getCenterY() {
        return getY() + getHeight() / 2.0;
    }

    public Rectangle2D getFrame() {
        return new Rectangle2D.Double(getX(), getY(), getWidth(), getHeight());
    }

    public abstract boolean isEmpty();

    public abstract void setFrame(double x, double y, double width, double height);

    public void setFrame(Point2D location, Dimension2D size) {
        setFrame(location.getX(), location.getY(), size.getWidth(), size.getHeight());
    }

    public void setFrame(Rectangle2D rectangle) {
        setFrame(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
    }

    public void setFrameFromDiagonal(double x1, double y1, double x2, double y2) {
        double x = Math.min(x1, x2);
        double y = Math.min(y1, y2);
        setFrame(x, y, Math.max(x1, x2) - x, Math.max(y1, y2) - y);
    }

    public void setFrameFromDiagonal(Point2D first, Point2D second) {
        setFrameFromDiagonal(first.getX(), first.getY(), second.getX(), second.getY());
    }

    public void setFrameFromCenter(double centerX, double centerY, double cornerX, double cornerY) {
        double halfWidth = Math.abs(cornerX - centerX);
        double halfHeight = Math.abs(cornerY - centerY);
        setFrame(centerX - halfWidth, centerY - halfHeight, halfWidth * 2.0, halfHeight * 2.0);
    }

    public void setFrameFromCenter(Point2D center, Point2D corner) {
        setFrameFromCenter(center.getX(), center.getY(), corner.getX(), corner.getY());
    }

    @Override
    public boolean contains(Point2D point) {
        return contains(point.getX(), point.getY());
    }

    @Override
    public boolean intersects(Rectangle2D rectangle) {
        return intersects(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
    }

    @Override
    public boolean contains(Rectangle2D rectangle) {
        return contains(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
    }

    @Override
    public Rectangle getBounds() {
        if (isEmpty()) {
            return new Rectangle();
        }
        double minX = Math.floor(getMinX());
        double minY = Math.floor(getMinY());
        double maxX = Math.ceil(getMaxX());
        double maxY = Math.ceil(getMaxY());
        return new Rectangle((int) minX, (int) minY, (int) (maxX - minX), (int) (maxY - minY));
    }

    @Override
    public PathIterator getPathIterator(AffineTransform transform, double flatness) {
        return new FlatteningPathIterator(getPathIterator(transform), flatness);
    }

    public Object clone() {
        return new Rectangle2D.Double(getX(), getY(), getWidth(), getHeight());
    }
}
