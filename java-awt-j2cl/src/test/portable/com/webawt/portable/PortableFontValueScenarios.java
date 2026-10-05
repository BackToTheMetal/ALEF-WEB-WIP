package com.webawt.portable;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphJustificationInfo;
import java.awt.font.GlyphMetrics;
import java.awt.font.GlyphVector;
import java.awt.font.GraphicAttribute;
import java.awt.font.ImageGraphicAttribute;
import java.awt.font.LineMetrics;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.NumericShaper;
import java.awt.font.OpenType;
import java.awt.font.ShapeGraphicAttribute;
import java.awt.font.TextAttribute;
import java.awt.font.TextHitInfo;
import java.awt.font.TextLayout;
import java.awt.font.TextMeasurer;
import java.awt.font.TransformAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

public final class PortableFontValueScenarios {
    private PortableFontValueScenarios() {
    }

    public static void storesAndValidatesGlyphJustification() {
        GlyphJustificationInfo info = new GlyphJustificationInfo(4.0f, true, GlyphJustificationInfo.PRIORITY_KASHIDA, 1.0f, 2.0f, false, GlyphJustificationInfo.PRIORITY_WHITESPACE, 3.0f, 4.0f);
        assertEquals(4.0f, info.weight);
        assertTrue(info.growAbsorb);
        assertEquals(GlyphJustificationInfo.PRIORITY_KASHIDA, info.growPriority);
        assertEquals(1.0f, info.growLeftLimit);
        assertEquals(2.0f, info.growRightLimit);
        assertFalse(info.shrinkAbsorb);
        assertEquals(GlyphJustificationInfo.PRIORITY_WHITESPACE, info.shrinkPriority);
        assertEquals(3.0f, info.shrinkLeftLimit);
        assertEquals(4.0f, info.shrinkRightLimit);
        assertThrowsIllegalArgument(() -> new GlyphJustificationInfo(-1.0f, false, 0, 0.0f, 0.0f, false, 0, 0.0f, 0.0f));
        assertThrowsIllegalArgument(() -> new GlyphJustificationInfo(1.0f, false, 4, 0.0f, 0.0f, false, 0, 0.0f, 0.0f));
        assertThrowsIllegalArgument(() -> new GlyphJustificationInfo(1.0f, false, 0, -1.0f, 0.0f, false, 0, 0.0f, 0.0f));
    }

    public static void reportsHorizontalAndVerticalGlyphMetrics() {
        Rectangle2D.Float sourceBounds = new Rectangle2D.Float(1.0f, -2.0f, 4.0f, 5.0f);
        GlyphMetrics horizontal = new GlyphMetrics(7.0f, sourceBounds, (byte) (GlyphMetrics.LIGATURE | GlyphMetrics.WHITESPACE));
        sourceBounds.setRect(9.0, 9.0, 9.0, 9.0);
        assertEquals(7.0f, horizontal.getAdvance());
        assertEquals(7.0f, horizontal.getAdvanceX());
        assertEquals(0.0f, horizontal.getAdvanceY());
        assertEquals(1.0f, horizontal.getLSB());
        assertEquals(2.0f, horizontal.getRSB());
        assertEquals(GlyphMetrics.LIGATURE | GlyphMetrics.WHITESPACE, horizontal.getType());
        assertTrue(horizontal.isLigature());
        assertTrue(horizontal.isWhitespace());
        assertFalse(horizontal.isStandard());
        GlyphMetrics vertical = new GlyphMetrics(false, 2.0f, 9.0f, new Rectangle2D.Float(-1.0f, 2.0f, 3.0f, 4.0f), GlyphMetrics.COMBINING);
        assertEquals(9.0f, vertical.getAdvance());
        assertEquals(2.0f, vertical.getLSB());
        assertEquals(3.0f, vertical.getRSB());
        assertTrue(vertical.isCombining());
        assertFalse(vertical.isComponent());
    }

    public static void copiesAndComparesTransformAttributes() {
        assertTrue(TransformAttribute.IDENTITY.isIdentity());
        AffineTransform source = AffineTransform.getTranslateInstance(3.0, 4.0);
        TransformAttribute attribute = new TransformAttribute(source);
        source.translate(8.0, 9.0);
        assertFalse(attribute.isIdentity());
        assertEquals(3.0f, (float) attribute.getTransform().getTranslateX());
        AffineTransform returned = attribute.getTransform();
        returned.translate(2.0, 2.0);
        assertEquals(3.0f, (float) attribute.getTransform().getTranslateX());
        TransformAttribute equal = new TransformAttribute(AffineTransform.getTranslateInstance(3.0, 4.0));
        assertTrue(attribute.equals(equal));
        assertEquals(attribute.hashCode(), equal.hashCode());
        assertFalse(attribute.equals(TransformAttribute.IDENTITY));
        assertTrue(new TransformAttribute(null).isIdentity());
    }

    public static void exposesCanonicalTextAttributesAndOpenTypeTags() {
        assertEquals(0.5f, TextAttribute.WEIGHT_EXTRA_LIGHT.floatValue());
        assertEquals(2.75f, TextAttribute.WEIGHT_ULTRABOLD.floatValue());
        assertEquals(0.2f, TextAttribute.POSTURE_OBLIQUE.floatValue());
        assertEquals(-0.04f, TextAttribute.TRACKING_TIGHT.floatValue());
        assertEquals(0.04f, TextAttribute.TRACKING_LOOSE.floatValue());
        assertSame(Boolean.FALSE, TextAttribute.RUN_DIRECTION_LTR);
        assertSame(Boolean.TRUE, TextAttribute.RUN_DIRECTION_RTL);
        assertTrue(TextAttribute.FAMILY.toString().contains("family"));
        assertEquals('c' << 24 | 'm' << 16 | 'a' << 8 | 'p', OpenType.TAG_CMAP);
        assertEquals('G' << 24 | 'P' << 16 | 'O' << 8 | 'S', OpenType.TAG_GPOS);
        assertEquals('t' << 24 | 'r' << 16 | 'a' << 8 | 'k', OpenType.TAG_TRAK);
    }

    public static void measuresAndDrawsGraphicAttributes() {
        TestGraphicAttribute base = new TestGraphicAttribute();
        assertEquals(GraphicAttribute.CENTER_BASELINE, base.getAlignment());
        assertRectangle(base.getBounds(), 0.0, -2.0, 4.0, 5.0);
        GlyphJustificationInfo justification = base.getJustificationInfo();
        assertEquals(4.0f, justification.weight);
        assertEquals(GlyphJustificationInfo.PRIORITY_INTERCHAR, justification.growPriority);
        Shape shape = new Rectangle2D.Float(1.0f, -2.0f, 4.0f, 5.0f);
        ShapeGraphicAttribute fill = new ShapeGraphicAttribute(shape, GraphicAttribute.ROMAN_BASELINE, ShapeGraphicAttribute.FILL);
        assertEquals(2.0f, fill.getAscent());
        assertEquals(3.0f, fill.getDescent());
        assertEquals(5.0f, fill.getAdvance());
        assertRectangle(fill.getBounds(), 1.0, -2.0, 4.0, 5.0);
        assertTrue(fill.equals(new ShapeGraphicAttribute(shape, GraphicAttribute.ROMAN_BASELINE, ShapeGraphicAttribute.FILL)));
        ShapeGraphicAttribute stroke = new ShapeGraphicAttribute(shape, GraphicAttribute.ROMAN_BASELINE, ShapeGraphicAttribute.STROKE);
        assertRectangle(stroke.getBounds(), 1.0, -2.0, 5.0, 6.0);
        BufferedImage shapeImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = shapeImage.createGraphics();
        graphics.setColor(Color.RED);
        fill.draw(graphics, 2.0f, 3.0f);
        assertEquals(0xffff0000, shapeImage.getRGB(3, 1));
        BufferedImage image = new BufferedImage(3, 4, BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 0, 0xff123456);
        ImageGraphicAttribute imageAttribute = new ImageGraphicAttribute(image, GraphicAttribute.ROMAN_BASELINE, 1.0f, 2.0f);
        assertEquals(2.0f, imageAttribute.getAscent());
        assertEquals(2.0f, imageAttribute.getDescent());
        assertEquals(2.0f, imageAttribute.getAdvance());
        assertRectangle(imageAttribute.getBounds(), -1.0, -2.0, 3.0, 4.0);
        imageAttribute.draw(graphics, 6.0f, 6.0f);
        assertEquals(0xff123456, shapeImage.getRGB(5, 4));
        assertTrue(imageAttribute.equals(new ImageGraphicAttribute(image, GraphicAttribute.ROMAN_BASELINE, 1.0f, 2.0f)));
        graphics.dispose();
        assertThrowsIllegalArgument(() -> new TestGraphicAttribute(3));
    }

    public static void laysOutMeasuresAndDrawsGlyphVectors() {
        Font font = new Font(Font.SANS_SERIF, Font.BOLD, 18);
        FontRenderContext context = new FontRenderContext(null, false, true);
        Map<TextAttribute, ?> attributes = font.getAttributes();
        assertEquals(18.0f, ((Number) attributes.get(TextAttribute.SIZE)).floatValue());
        assertTrue(attributes.containsKey(TextAttribute.FAMILY));
        assertTrue(attributes.containsKey(TextAttribute.WEIGHT));
        LineMetrics lineMetrics = font.getLineMetrics("Web", context);
        assertEquals(3, lineMetrics.getNumChars());
        assertTrue(lineMetrics.getAscent() > 0.0f);
        assertTrue(lineMetrics.getHeight() >= lineMetrics.getAscent() + lineMetrics.getDescent());
        float[] offsets = lineMetrics.getBaselineOffsets();
        offsets[0] = 99.0f;
        assertEquals(0.0f, lineMetrics.getBaselineOffsets()[0]);
        GlyphVector vector = font.createGlyphVector(context, "A B");
        assertEquals(3, vector.getNumGlyphs());
        assertEquals(4, vector.getGlyphPositions(0, 4, null).length / 2);
        assertTrue(vector.getGlyphPosition(3).getX() > vector.getGlyphPosition(0).getX());
        assertTrue(vector.getLogicalBounds().getWidth() > 0.0);
        assertTrue(vector.getGlyphMetrics(0).getAdvance() > 0.0f);
        int[] codes = vector.getGlyphCodes(0, 3, null);
        assertEquals(codes[1], font.createGlyphVector(context, codes).getGlyphCode(1));
        assertEquals(1, vector.getGlyphCharIndex(1));
        vector.setGlyphPosition(1, new Point2D.Float(30.0f, 2.0f));
        assertTrue((vector.getLayoutFlags() & GlyphVector.FLAG_HAS_POSITION_ADJUSTMENTS) != 0);
        vector.setGlyphTransform(0, AffineTransform.getScaleInstance(1.2, 1.0));
        assertTrue((vector.getLayoutFlags() & GlyphVector.FLAG_HAS_TRANSFORMS) != 0);
        assertTrue(vector.getGlyphPixelBounds(0, context, 2.0f, 3.0f).width > 0);
        char[] rightToLeftText = {'a', 'b', 'c'};
        GlyphVector rightToLeft = font.layoutGlyphVector(context, rightToLeftText, 0, rightToLeftText.length, Font.LAYOUT_RIGHT_TO_LEFT);
        assertTrue((rightToLeft.getLayoutFlags() & GlyphVector.FLAG_RUN_RTL) != 0);
        assertEquals(2, rightToLeft.getGlyphCharIndex(0));
        TestFontMetrics fontMetrics = new TestFontMetrics(font);
        assertEquals(2, fontMetrics.getLineMetrics("AW", null).getNumChars());
        assertTrue(fontMetrics.getStringBounds("AW", null).getWidth() > 0.0);
        assertTrue(fontMetrics.getMaxCharBounds(null).getHeight() > 0.0);
        BufferedImage image = new BufferedImage(40, 30, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(Color.BLUE);
        graphics.drawGlyphVector(font.createGlyphVector(context, "A"), 5.0f, 22.0f);
        graphics.dispose();
        assertTrue(hasOpaquePixel(image));
    }

    public static void shapesEuropeanDigitsAcrossUnicodeRanges() {
        char[] devanagari = "a0129".toCharArray();
        NumericShaper shaper = NumericShaper.getShaper(NumericShaper.DEVANAGARI);
        shaper.shape(devanagari, 1, 4);
        assertEquals('a', devanagari[0]);
        assertEquals('\u0966', devanagari[1]);
        assertEquals('\u0967', devanagari[2]);
        assertEquals('\u0968', devanagari[3]);
        assertEquals('\u096f', devanagari[4]);
        assertFalse(shaper.isContextual());
        assertEquals(NumericShaper.DEVANAGARI, shaper.getRanges());
        assertTrue(shaper.getRangeSet().contains(NumericShaper.Range.DEVANAGARI));
        assertTrue(shaper.equals(NumericShaper.getShaper(NumericShaper.Range.DEVANAGARI)));
        assertEquals(shaper.hashCode(), NumericShaper.getShaper(NumericShaper.DEVANAGARI).hashCode());
        Set<NumericShaper.Range> ranges = new HashSet<>();
        ranges.add(NumericShaper.Range.EUROPEAN);
        ranges.add(NumericShaper.Range.DEVANAGARI);
        NumericShaper contextual = NumericShaper.getContextualShaper(ranges, NumericShaper.Range.EUROPEAN);
        char[] mixed = "A12\u091534".toCharArray();
        contextual.shape(mixed, 0, mixed.length);
        assertEquals('1', mixed[1]);
        assertEquals('2', mixed[2]);
        assertEquals('\u0969', mixed[4]);
        assertEquals('\u096a', mixed[5]);
        assertTrue(contextual.isContextual());
        assertThrowsIllegalArgument(() -> NumericShaper.getShaper(NumericShaper.ARABIC | NumericShaper.THAI));
    }

    public static void laysOutCaretsHighlightsAndWrappedLines() {
        Font font = new Font(Font.SERIF, Font.PLAIN, 16);
        FontRenderContext context = new FontRenderContext(null, false, true);
        TextLayout layout = new TextLayout("A B ", font, context);
        assertEquals(4, layout.getCharacterCount());
        assertTrue(layout.getAdvance() > 0.0f);
        assertTrue(layout.getVisibleAdvance() < layout.getAdvance());
        assertTrue(layout.getAscent() > 0.0f);
        assertTrue(layout.getDescent() >= 0.0f);
        assertTrue(layout.isLeftToRight());
        assertFalse(layout.isVertical());
        assertEquals(1, layout.getNextRightHit(0).getInsertionIndex());
        assertEquals(0, layout.getNextLeftHit(1).getInsertionIndex());
        assertEquals(6, layout.getCaretInfo(TextHitInfo.afterOffset(1)).length);
        assertEquals(2, layout.getCaretShapes(1).length);
        assertTrue(layout.getLogicalHighlightShape(0, 2).getBounds2D().getWidth() > 0.0);
        assertTrue(layout.getVisualHighlightShape(TextHitInfo.afterOffset(0), TextHitInfo.afterOffset(2)).getBounds2D().getWidth() > 0.0);
        assertTrue(layout.getBlackBoxBounds(0, 2).getBounds2D().getWidth() > 0.0);
        assertEquals(0, layout.hitTestChar(0.0f, 0.0f).getInsertionIndex());
        Point2D.Float caretPoint = new Point2D.Float();
        layout.hitToPoint(TextHitInfo.afterOffset(2), caretPoint);
        assertTrue(caretPoint.x > 0.0f);
        assertTrue(layout.getOutline(null).getBounds2D().getWidth() > 0.0);
        TextLayout justified = layout.getJustifiedLayout(layout.getAdvance() + 12.0f);
        assertTrue(justified.getAdvance() > layout.getAdvance());
        assertTrue(layout.equals(layout));
        assertFalse(new TextLayout("\u05d0\u05d1", font, context).isLeftToRight());
        BufferedImage image = new BufferedImage(80, 30, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(Color.BLACK);
        layout.draw(graphics, 2.0f, 20.0f);
        graphics.dispose();
        assertTrue(hasOpaquePixel(image));
        PortableAttributedIterator iterator = new PortableAttributedIterator("alpha beta gamma", font);
        TextMeasurer measurer = new TextMeasurer(iterator, context);
        int breakIndex = measurer.getLineBreakIndex(0, measurer.getAdvanceBetween(0, 6));
        assertTrue(breakIndex >= 5 && breakIndex <= 7);
        assertEquals(5, measurer.getLayout(0, 5).getCharacterCount());
        LineBreakMeasurer breaks = new LineBreakMeasurer(new PortableAttributedIterator("alpha beta gamma", font), context);
        TextLayout firstLine = breaks.nextLayout(measurer.getAdvanceBetween(0, 7));
        assertTrue(firstLine != null);
        assertTrue(breaks.getPosition() > 0);
        breaks.setPosition(0);
        assertEquals(0, breaks.getPosition());
        measurer.insertChar(new PortableAttributedIterator("xalpha beta gamma", font), 0);
        assertEquals(1, measurer.getLayout(0, 1).getCharacterCount());
        measurer.deleteChar(new PortableAttributedIterator("alpha beta gamma", font), 0);
        assertEquals(5, measurer.getLayout(0, 5).getCharacterCount());
    }

    private static boolean hasOpaquePixel(BufferedImage image) {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if ((image.getRGB(x, y) >>> 24) != 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void assertRectangle(Rectangle2D rectangle, double x, double y, double width, double height) {
        if (rectangle.getX() != x || rectangle.getY() != y || rectangle.getWidth() != width || rectangle.getHeight() != height) {
            throw new AssertionError("Unexpected rectangle: " + rectangle);
        }
    }

    private static void assertThrowsIllegalArgument(Runnable runnable) {
        try {
            runnable.run();
        } catch (IllegalArgumentException expected) {
            return;
        }
        throw new AssertionError("Expected IllegalArgumentException");
    }

    private static void assertEquals(float expected, float actual) {
        if (Float.compare(expected, actual) != 0) {
            throw new AssertionError("Expected " + expected + " but was " + actual);
        }
    }

    private static void assertEquals(int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError("Expected " + expected + " but was " + actual);
        }
    }

    private static void assertEquals(char expected, char actual) {
        if (expected != actual) {
            throw new AssertionError("Expected " + expected + " but was " + actual);
        }
    }

    private static void assertSame(Object expected, Object actual) {
        if (expected != actual) {
            throw new AssertionError("Expected identical objects");
        }
    }

    private static void assertTrue(boolean value) {
        if (!value) {
            throw new AssertionError("Expected true");
        }
    }

    private static void assertFalse(boolean value) {
        if (value) {
            throw new AssertionError("Expected false");
        }
    }

    private static final class TestGraphicAttribute extends GraphicAttribute {
        private TestGraphicAttribute() {
            this(CENTER_BASELINE);
        }

        private TestGraphicAttribute(int alignment) {
            super(alignment);
        }

        @Override
        public float getAscent() {
            return 2.0f;
        }

        @Override
        public float getDescent() {
            return 3.0f;
        }

        @Override
        public float getAdvance() {
            return 4.0f;
        }

        @Override
        public void draw(Graphics2D graphics, float x, float y) {
        }
    }

    private static final class TestFontMetrics extends FontMetrics {
        private TestFontMetrics(Font font) {
            super(font);
        }
    }
}
