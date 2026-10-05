package java.awt.font;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public abstract class GraphicAttribute {
    public static final int TOP_ALIGNMENT = -1;
    public static final int BOTTOM_ALIGNMENT = -2;
    public static final int ROMAN_BASELINE = 0;
    public static final int CENTER_BASELINE = 1;
    public static final int HANGING_BASELINE = 2;
    private final int alignment;

    protected GraphicAttribute(int alignment) {
        if (alignment < BOTTOM_ALIGNMENT || alignment > HANGING_BASELINE) {
            throw new IllegalArgumentException("alignment must be a defined GraphicAttribute alignment");
        }
        this.alignment = alignment;
    }

    public abstract float getAscent();
    public abstract float getDescent();
    public abstract float getAdvance();

    public Rectangle2D getBounds() {
        return new Rectangle2D.Float(0.0f, -getAscent(), getAdvance(), getAscent() + getDescent());
    }

    public Shape getOutline(AffineTransform transform) {
        Shape bounds = getBounds();
        return transform == null ? bounds : transform.createTransformedShape(bounds);
    }

    public abstract void draw(Graphics2D graphics, float x, float y);

    public final int getAlignment() {
        return alignment;
    }

    public GlyphJustificationInfo getJustificationInfo() {
        float advance = getAdvance();
        return new GlyphJustificationInfo(advance, false, GlyphJustificationInfo.PRIORITY_INTERCHAR, advance / 3.0f, advance / 3.0f, false, GlyphJustificationInfo.PRIORITY_WHITESPACE, 0.0f, 0.0f);
    }
}
