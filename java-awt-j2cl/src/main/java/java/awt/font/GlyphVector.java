package java.awt.font;

import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public abstract class GlyphVector implements Cloneable {
    public static final int FLAG_HAS_TRANSFORMS = 1;
    public static final int FLAG_HAS_POSITION_ADJUSTMENTS = 2;
    public static final int FLAG_RUN_RTL = 4;
    public static final int FLAG_COMPLEX_GLYPHS = 8;
    public static final int FLAG_MASK = FLAG_HAS_TRANSFORMS | FLAG_HAS_POSITION_ADJUSTMENTS | FLAG_RUN_RTL | FLAG_COMPLEX_GLYPHS;

    public abstract Font getFont();

    public abstract FontRenderContext getFontRenderContext();

    public abstract void performDefaultLayout();

    public abstract int getNumGlyphs();

    public abstract int getGlyphCode(int glyphIndex);

    public abstract int[] getGlyphCodes(int beginGlyphIndex, int numEntries, int[] codeReturn);

    public abstract Rectangle2D getLogicalBounds();

    public abstract Rectangle2D getVisualBounds();

    public Rectangle getPixelBounds(FontRenderContext renderContext, float x, float y) {
        return translatedBounds(getVisualBounds(), x, y);
    }

    public abstract Shape getOutline();

    public abstract Shape getOutline(float x, float y);

    public abstract Shape getGlyphOutline(int glyphIndex);

    public Shape getGlyphOutline(int glyphIndex, float x, float y) {
        return AffineTransform.getTranslateInstance(x, y).createTransformedShape(getGlyphOutline(glyphIndex));
    }

    public abstract Point2D getGlyphPosition(int glyphIndex);

    public abstract void setGlyphPosition(int glyphIndex, Point2D newPosition);

    public abstract AffineTransform getGlyphTransform(int glyphIndex);

    public abstract void setGlyphTransform(int glyphIndex, AffineTransform newTransform);

    public int getLayoutFlags() {
        return 0;
    }

    public int getGlyphCharIndex(int glyphIndex) {
        checkGlyphIndex(glyphIndex);
        return glyphIndex;
    }

    public int[] getGlyphCharIndices(int beginGlyphIndex, int numEntries, int[] codeReturn) {
        checkGlyphRange(beginGlyphIndex, numEntries);
        int[] result = codeReturn == null ? new int[numEntries] : codeReturn;
        if (result.length < numEntries) {
            throw new IllegalArgumentException("codeReturn is too small");
        }
        for (int index = 0; index < numEntries; index++) {
            result[index] = getGlyphCharIndex(beginGlyphIndex + index);
        }
        return result;
    }

    public abstract float[] getGlyphPositions(int beginGlyphIndex, int numEntries, float[] positionReturn);

    public abstract Shape getGlyphLogicalBounds(int glyphIndex);

    public abstract Shape getGlyphVisualBounds(int glyphIndex);

    public Rectangle getGlyphPixelBounds(int glyphIndex, FontRenderContext renderContext, float x, float y) {
        return translatedBounds(getGlyphVisualBounds(glyphIndex).getBounds2D(), x, y);
    }

    public abstract GlyphMetrics getGlyphMetrics(int glyphIndex);

    public abstract GlyphJustificationInfo getGlyphJustificationInfo(int glyphIndex);

    public abstract boolean equals(GlyphVector glyphVector);

    protected final void checkGlyphIndex(int glyphIndex) {
        if (glyphIndex < 0 || glyphIndex >= getNumGlyphs()) {
            throw new IndexOutOfBoundsException("Invalid glyph index: " + glyphIndex);
        }
    }

    protected final void checkGlyphRange(int beginGlyphIndex, int numEntries) {
        if (beginGlyphIndex < 0 || numEntries < 0 || beginGlyphIndex > getNumGlyphs() - numEntries) {
            throw new IndexOutOfBoundsException("Invalid glyph range: " + beginGlyphIndex + " + " + numEntries);
        }
    }

    private static Rectangle translatedBounds(Rectangle2D bounds, float x, float y) {
        double minimumX = Math.floor(bounds.getMinX() + x);
        double minimumY = Math.floor(bounds.getMinY() + y);
        double maximumX = Math.ceil(bounds.getMaxX() + x);
        double maximumY = Math.ceil(bounds.getMaxY() + y);
        return new Rectangle((int) minimumX, (int) minimumY, (int) (maximumX - minimumX), (int) (maximumY - minimumY));
    }
}
