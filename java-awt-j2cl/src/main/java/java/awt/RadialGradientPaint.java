package java.awt;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;

public final class RadialGradientPaint extends MultipleGradientPaint {
    private final Point2D.Double center;
    private final Point2D.Double focus;
    private final float radius;

    public RadialGradientPaint(float centerX, float centerY, float radius, float[] fractions, Color[] colors) {
        this(new Point2D.Float(centerX, centerY), radius, new Point2D.Float(centerX, centerY), fractions, colors, CycleMethod.NO_CYCLE);
    }

    public RadialGradientPaint(Point2D center, float radius, float[] fractions, Color[] colors) {
        this(center, radius, center, fractions, colors, CycleMethod.NO_CYCLE);
    }

    public RadialGradientPaint(float centerX, float centerY, float radius, float[] fractions, Color[] colors, CycleMethod cycleMethod) {
        this(new Point2D.Float(centerX, centerY), radius, new Point2D.Float(centerX, centerY), fractions, colors, cycleMethod);
    }

    public RadialGradientPaint(Point2D center, float radius, float[] fractions, Color[] colors, CycleMethod cycleMethod) {
        this(center, radius, center, fractions, colors, cycleMethod);
    }

    public RadialGradientPaint(float centerX, float centerY, float radius, float focusX, float focusY, float[] fractions, Color[] colors, CycleMethod cycleMethod) {
        this(new Point2D.Float(centerX, centerY), radius, new Point2D.Float(focusX, focusY), fractions, colors, cycleMethod);
    }

    public RadialGradientPaint(Point2D center, float radius, Point2D focus, float[] fractions, Color[] colors, CycleMethod cycleMethod) {
        this(center, radius, focus, fractions, colors, cycleMethod, ColorSpaceType.SRGB, new AffineTransform());
    }

    public RadialGradientPaint(Point2D center, float radius, Point2D focus, float[] fractions, Color[] colors, CycleMethod cycleMethod, ColorSpaceType colorSpace, AffineTransform gradientTransform) {
        super(fractions, colors, cycleMethod, colorSpace, gradientTransform);
        if (center == null || focus == null) {
            throw new NullPointerException("Center and focus points must be non-null");
        }
        if (radius <= 0.0f) {
            throw new IllegalArgumentException("Radius must be greater than zero");
        }
        this.center = new Point2D.Double(center.getX(), center.getY());
        this.focus = new Point2D.Double(focus.getX(), focus.getY());
        this.radius = radius;
    }

    public RadialGradientPaint(Rectangle2D gradientBounds, float[] fractions, Color[] colors, CycleMethod cycleMethod) {
        this(rectangleCenter(gradientBounds), 1.0f, rectangleCenter(gradientBounds), fractions, colors, cycleMethod, ColorSpaceType.SRGB, rectangleTransform(gradientBounds));
    }

    @Override
    public PaintContext createContext(ColorModel colorModel, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform transform, RenderingHints hints) {
        if (hints == null) {
            throw new NullPointerException("RenderingHints cannot be null");
        }
        return createRadialContext(transform, center.x, center.y, focus.x, focus.y, radius);
    }

    public Point2D getCenterPoint() {
        return new Point2D.Double(center.x, center.y);
    }

    public Point2D getFocusPoint() {
        return new Point2D.Double(focus.x, focus.y);
    }

    public float getRadius() {
        return radius;
    }

    private static Point2D rectangleCenter(Rectangle2D bounds) {
        validateBounds(bounds);
        return new Point2D.Double(bounds.getCenterX(), bounds.getCenterY());
    }

    private static AffineTransform rectangleTransform(Rectangle2D bounds) {
        validateBounds(bounds);
        double centerX = bounds.getCenterX();
        double centerY = bounds.getCenterY();
        AffineTransform transform = AffineTransform.getTranslateInstance(centerX, centerY);
        transform.scale(bounds.getWidth() / 2.0, bounds.getHeight() / 2.0);
        transform.translate(-centerX, -centerY);
        return transform;
    }

    private static void validateBounds(Rectangle2D bounds) {
        if (bounds == null) {
            throw new NullPointerException("Gradient bounds cannot be null");
        }
        if (bounds.isEmpty()) {
            throw new IllegalArgumentException("Gradient bounds must be non-empty");
        }
    }
}
