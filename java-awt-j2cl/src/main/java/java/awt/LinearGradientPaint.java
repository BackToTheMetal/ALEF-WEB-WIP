package java.awt;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;

public final class LinearGradientPaint extends MultipleGradientPaint {
    private final Point2D.Double start;
    private final Point2D.Double end;

    public LinearGradientPaint(float startX, float startY, float endX, float endY, float[] fractions, Color[] colors) {
        this(new Point2D.Float(startX, startY), new Point2D.Float(endX, endY), fractions, colors, CycleMethod.NO_CYCLE);
    }

    public LinearGradientPaint(float startX, float startY, float endX, float endY, float[] fractions, Color[] colors, CycleMethod cycleMethod) {
        this(new Point2D.Float(startX, startY), new Point2D.Float(endX, endY), fractions, colors, cycleMethod);
    }

    public LinearGradientPaint(Point2D start, Point2D end, float[] fractions, Color[] colors) {
        this(start, end, fractions, colors, CycleMethod.NO_CYCLE);
    }

    public LinearGradientPaint(Point2D start, Point2D end, float[] fractions, Color[] colors, CycleMethod cycleMethod) {
        this(start, end, fractions, colors, cycleMethod, ColorSpaceType.SRGB, new AffineTransform());
    }

    public LinearGradientPaint(Point2D start, Point2D end, float[] fractions, Color[] colors, CycleMethod cycleMethod, ColorSpaceType colorSpace, AffineTransform gradientTransform) {
        super(fractions, colors, cycleMethod, colorSpace, gradientTransform);
        if (start == null || end == null) {
            throw new NullPointerException("Start and end points must be non-null");
        }
        if (start.equals(end)) {
            throw new IllegalArgumentException("Start point cannot equal endpoint");
        }
        this.start = new Point2D.Double(start.getX(), start.getY());
        this.end = new Point2D.Double(end.getX(), end.getY());
    }

    @Override
    public PaintContext createContext(ColorModel colorModel, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform transform, RenderingHints hints) {
        if (hints == null) {
            throw new NullPointerException("RenderingHints cannot be null");
        }
        return createLinearContext(transform, start.x, start.y, end.x, end.y);
    }

    public Point2D getStartPoint() {
        return new Point2D.Double(start.x, start.y);
    }

    public Point2D getEndPoint() {
        return new Point2D.Double(end.x, end.y);
    }
}
