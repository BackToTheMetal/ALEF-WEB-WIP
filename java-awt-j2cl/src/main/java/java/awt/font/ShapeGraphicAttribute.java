package java.awt.font;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public final class ShapeGraphicAttribute extends GraphicAttribute {
    public static final boolean STROKE = true;
    public static final boolean FILL = false;
    private final Shape shape;
    private final boolean stroke;
    private final Rectangle2D bounds;

    public ShapeGraphicAttribute(Shape shape, int alignment, boolean stroke) {
        super(alignment);
        if (shape == null) {
            throw new NullPointerException("shape");
        }
        this.shape = shape;
        this.stroke = stroke;
        bounds = shape.getBounds2D();
        if (stroke) {
            bounds.setRect(bounds.getX(), bounds.getY(), bounds.getWidth() + 1.0, bounds.getHeight() + 1.0);
        }
    }

    @Override
    public float getAscent() {
        return (float) Math.max(0.0, -bounds.getMinY());
    }

    @Override
    public float getDescent() {
        return (float) Math.max(0.0, bounds.getMaxY());
    }

    @Override
    public float getAdvance() {
        return (float) Math.max(0.0, bounds.getMaxX());
    }

    @Override
    public void draw(Graphics2D graphics, float x, float y) {
        graphics.translate(x, y);
        try {
            if (stroke) {
                graphics.draw(shape);
            } else {
                graphics.fill(shape);
            }
        } finally {
            graphics.translate(-x, -y);
        }
    }

    @Override
    public Rectangle2D getBounds() {
        return (Rectangle2D) bounds.clone();
    }

    @Override
    public Shape getOutline(AffineTransform transform) {
        return transform == null ? shape : transform.createTransformedShape(shape);
    }

    @Override
    public int hashCode() {
        return shape.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof ShapeGraphicAttribute && equals((ShapeGraphicAttribute) object);
    }

    public boolean equals(ShapeGraphicAttribute attribute) {
        return attribute != null && getAlignment() == attribute.getAlignment() && stroke == attribute.stroke && shape.equals(attribute.shape);
    }
}
