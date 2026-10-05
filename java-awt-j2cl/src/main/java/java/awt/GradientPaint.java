package java.awt;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;

public class GradientPaint implements Paint {
    private final Point2D.Float point1;
    private final Point2D.Float point2;
    private final Color color1;
    private final Color color2;
    private final boolean cyclic;

    public GradientPaint(float x1, float y1, Color color1, float x2, float y2, Color color2) {
        this(x1, y1, color1, x2, y2, color2, false);
    }

    public GradientPaint(Point2D point1, Color color1, Point2D point2, Color color2) {
        this(point1, color1, point2, color2, false);
    }

    public GradientPaint(float x1, float y1, Color color1, float x2, float y2, Color color2, boolean cyclic) {
        if (color1 == null || color2 == null) {
            throw new NullPointerException("Colors cannot be null");
        }
        point1 = new Point2D.Float(x1, y1);
        point2 = new Point2D.Float(x2, y2);
        this.color1 = color1;
        this.color2 = color2;
        this.cyclic = cyclic;
    }

    public GradientPaint(Point2D point1, Color color1, Point2D point2, Color color2, boolean cyclic) {
        if (point1 == null || point2 == null) {
            throw new NullPointerException("Colors and points should be non-null");
        }
        if (color1 == null || color2 == null) {
            throw new NullPointerException("Colors cannot be null");
        }
        this.point1 = new Point2D.Float((float) point1.getX(), (float) point1.getY());
        this.point2 = new Point2D.Float((float) point2.getX(), (float) point2.getY());
        this.color1 = color1;
        this.color2 = color2;
        this.cyclic = cyclic;
    }

    public Point2D getPoint1() {
        return new Point2D.Float(point1.x, point1.y);
    }

    public Color getColor1() {
        return color1;
    }

    public Point2D getPoint2() {
        return new Point2D.Float(point2.x, point2.y);
    }

    public Color getColor2() {
        return color2;
    }

    public boolean isCyclic() {
        return cyclic;
    }

    @Override
    public PaintContext createContext(ColorModel colorModel, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform transform, RenderingHints hints) {
        if (transform == null) {
            throw new NullPointerException("Transform cannot be null");
        }
        return MultipleGradientPaint.createSimpleLinearContext(color1, color2, cyclic, new AffineTransform(transform), point1, point2);
    }

    @Override
    public int getTransparency() {
        return color1.getAlpha() == 255 && color2.getAlpha() == 255 ? OPAQUE : TRANSLUCENT;
    }
}
