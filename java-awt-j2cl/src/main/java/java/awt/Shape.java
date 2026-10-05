package java.awt;

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public interface Shape {
    Rectangle getBounds();

    Rectangle2D getBounds2D();

    boolean contains(double x, double y);

    boolean contains(Point2D point);

    boolean intersects(double x, double y, double width, double height);

    boolean intersects(Rectangle2D rectangle);

    boolean contains(double x, double y, double width, double height);

    boolean contains(Rectangle2D rectangle);

    PathIterator getPathIterator(AffineTransform transform);

    PathIterator getPathIterator(AffineTransform transform, double flatness);
}
