package org.webawt.samples.dualshowcase;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextHitInfo;
import java.awt.font.TextLayout;
import java.awt.font.TextMeasurer;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AttributedScenarios {
    private AttributedScenarios() {
    }

    public static StyledText sample(boolean decorated) {
        List<Map<AttributedCharacterIterator.Attribute, Object>> runs = new ArrayList<>();
        Map<AttributedCharacterIterator.Attribute, Object> first = new HashMap<>();
        first.put(TextAttribute.FONT, FontScenarios.referenceFont().deriveFont(24f));
        first.put(TextAttribute.FOREGROUND, Color.BLUE);
        if (decorated) {
            first.put(TextAttribute.BACKGROUND, Color.YELLOW);
            first.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        }
        runs.add(first);
        Map<AttributedCharacterIterator.Attribute, Object> second = new HashMap<>();
        second.put(TextAttribute.FONT, FontScenarios.referenceFont().deriveFont(16f));
        second.put(TextAttribute.FOREGROUND, Color.GREEN);
        if (decorated) { second.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON); }
        runs.add(second);
        return new StyledText("Big small", 5, new int[] {4, 9}, runs);
    }

    public static double[] diagnostics() {
        java.awt.font.FontRenderContext context = new java.awt.font.FontRenderContext(null, true, true);
        TextMeasurer measurer = new TextMeasurer(sample(false), context);
        TextLayout layout = measurer.getLayout(5, 14);
        TextLayout cluster = new TextLayout("A\u0301\ud83d\ude42Z", FontScenarios.referenceFont(), context);
        return new double[] {layout.getAdvance(), layout.getCaretInfo(TextHitInfo.afterOffset(4))[0], layout.getLogicalHighlightShape(0, 4).getBounds2D().getWidth(), measurer.getAdvanceBetween(5, 9), new LineBreakMeasurer(sample(false), context).nextOffset(65), cluster.getNextRightHit(0).getInsertionIndex(), cluster.getNextRightHit(2).getInsertionIndex()};
    }

    public static String paint(Graphics2D graphics, int scenario, int offset) {
        Font font = FontScenarios.referenceFont();
        graphics.setFont(font);
        graphics.setColor(Color.BLUE);
        if (scenario < 2) {
            StyledText text = sample(scenario == 1);
            text.setIndex(7);
            Paint paint = graphics.getPaint();
            AffineTransform transform = graphics.getTransform();
            if (scenario == 0) {
                graphics.drawString(text, offset + 20, 50);
                graphics.drawString(text, offset + 20.5f, 115.25f);
            } else {
                graphics.translate(offset + 20, 50);
                graphics.rotate(Math.PI / 18);
                graphics.drawString(text, 0, 0);
                graphics.drawString(text, 0.5f, 60.25f);
            }
            expect(text.getIndex() >= 5 && text.getIndex() <= 14, "iterator remains within its range");
            expect(graphics.getFont().equals(font) && graphics.getPaint().equals(paint), "attributed painting preserves font/paint");
            if (scenario == 0) { expect(graphics.getTransform().equals(transform), "transform preserved"); }
            return scenario == 0 ? "Both iterator overloads: blue 24px and green 16px; iterator starts at 5, current index=" + text.getIndex() : "Rotated attribute runs: yellow background, blue underline, green strike; parent state preserved";
        }
        if (scenario == 2) {
            StyledText text = sample(false);
            TextMeasurer measurer = new TextMeasurer(text, graphics.getFontRenderContext());
            TextLayout layout = measurer.getLayout(5, 14);
            expect(Math.abs(layout.getAdvance() - measurer.getAdvanceBetween(5, 14)) < 0.01, "layout and measurement share runs");
            graphics.setColor(Color.YELLOW);
            graphics.translate(offset + 20, 50);
            graphics.fill(layout.getLogicalHighlightShape(0, 4));
            layout.draw(graphics, 0, 0);
            graphics.setColor(Color.BLUE);
            Shape caret = layout.getCaretShapes(4)[0];
            graphics.draw(caret);
            LineBreakMeasurer breaks = new LineBreakMeasurer(sample(false), graphics.getFontRenderContext());
            float y = 65;
            int lines = 0;
            while (breaks.getPosition() < 14) {
                TextLayout line = breaks.nextLayout(65);
                expect(line != null && line.getCharacterCount() > 0, "wrapping makes progress");
                line.draw(graphics, 0, y);
                y += 35;
                lines++;
            }
            expect(lines == 2, "mixed-size text wraps into two styled lines");
            return "Run-preserving TextLayout/TextMeasurer; highlight and caret; two wrapped lines";
        }
        GlyphVector vector = font.createGlyphVector(graphics.getFontRenderContext(), "Hi");
        vector.setGlyphPosition(1, new Point2D.Float(40, 10));
        vector.setGlyphTransform(1, AffineTransform.getRotateInstance(Math.PI / 8));
        graphics.drawGlyphVector(vector, offset + 20, 60);
        Graphics2D child = (Graphics2D) graphics.create();
        try {
            child.translate(offset + 20, 120);
            child.scale(1.3, 1);
            child.drawGlyphVector(vector, 0, 0);
        } finally {
            child.dispose();
        }
        TextLayout cluster = new TextLayout("A\u0301\ud83d\ude42Z", font, graphics.getFontRenderContext());
        TextHitInfo next = cluster.getNextRightHit(0);
        expect(next != null && next.getInsertionIndex() == 2, "caret skips combining cluster");
        next = cluster.getNextRightHit(2);
        expect(next != null && next.getInsertionIndex() == 4, "caret skips surrogate pair");
        return "Glyph positions/rotation on both backends; caret skips combining marks and surrogate pairs";
    }

    public static void verifyInk(BufferedImage image, int scenario, int offset) {
        int blue = 0;
        int green = 0;
        for (int y = 15; y < 175; y++) {
            for (int x = offset + 10; x < 280; x++) {
                int value = image.getRGB(x, y);
                if ((value & 255) > 100 && (value >>> 16 & 255) < 100) { blue++; }
                if ((value >>> 8 & 255) > 100 && (value >>> 16 & 255) < 100) { green++; }
            }
        }
        expect(blue > 40, "visible blue glyphs");
        if (scenario < 3) { expect(green > 35, "visible green attribute run"); }
    }

    private static void expect(boolean condition, String message) {
        if (!condition) { throw new AssertionError(message); }
    }
}
