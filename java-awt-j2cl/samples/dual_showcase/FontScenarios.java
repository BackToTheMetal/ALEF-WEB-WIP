package org.webawt.samples.dualshowcase;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public final class FontScenarios {
    public static final String FAMILY = "WebAWT Reference";
    public static final String[] TEXT = {"iiii", "WWWW", "WebAWT 123", "Cafe\u0301"};
    private static Font reference = new Font(FAMILY, Font.PLAIN, 24);

    private FontScenarios() {
    }

    public static void setReference(Font font) {
        reference = font.deriveFont(24.0f);
    }

    public static Font referenceFont() {
        return reference;
    }

    public static double[] measurements() {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        try {
            graphics.setFont(reference);
            graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            double[] values = new double[TEXT.length * 2];
            for (int index = 0; index < TEXT.length; index++) {
                values[index * 2] = reference.getStringBounds(TEXT[index], graphics.getFontRenderContext()).getWidth();
                values[index * 2 + 1] = graphics.getFontMetrics().stringWidth(TEXT[index]);
            }
            return values;
        } finally {
            graphics.dispose();
            image.flush();
        }
    }

    static String paint(Graphics2D graphics, int scenario, int offset) {
        Font font = reference;
        graphics.setFont(font);
        graphics.setFont(null);
        expect(graphics.getFont() == font, "null font preserves selected font");
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        if (scenario == 0) {
            FontMetrics metrics = graphics.getFontMetrics();
            Font smaller = font.deriveFont(12.0f);
            FontMetrics explicit = graphics.getFontMetrics(smaller);
            expect(metrics.getFont().equals(font) && explicit.getFont().equals(smaller), "both metrics overloads retain their requested font");
            expect(graphics.getFont() == font, "querying another font does not select it");
            StringBuilder report = new StringBuilder("Font metrics widths:");
            for (int index = 0; index < TEXT.length; index++) {
                String text = TEXT[index];
                int width = metrics.stringWidth(text);
                expect(width > 0 && explicit.stringWidth(text) < width, "font size changes measured width");
                Rectangle2D bounds = metrics.getStringBounds(text, graphics);
                expect(Math.abs(bounds.getWidth() - width) <= text.length(), "integer metrics agree with fractional bounds within rounding");
                graphics.setColor(Color.GREEN);
                graphics.fillRect(offset + 20, 20 + index * 32, width, 10);
                graphics.setColor(Color.BLUE);
                graphics.fillRect(offset + 20, 32 + index * 32, explicit.stringWidth(text), 5);
                report.append(' ').append(width);
            }
            expect(metrics.stringWidth("iiii") < metrics.stringWidth("WWWW"), "proportional font distinguishes narrow and wide glyphs");
            expect(metrics.getAscent() > 0 && metrics.getHeight() >= metrics.getAscent() + metrics.getDescent(), "line metrics have consistent extents");
            Graphics2D child = (Graphics2D) graphics.create();
            child.setFont(smaller);
            child.dispose();
            expect(graphics.getFont() == font, "child font selection leaves parent unchanged");
            return report + "; green 24px / blue 12px; height=" + metrics.getHeight();
        }
        AffineTransform original = graphics.getTransform();
        graphics.translate(offset, 10);
        graphics.scale(2, 3);
        graphics.shear(0.25, 0);
        FontRenderContext context = graphics.getFontRenderContext();
        AffineTransform expected = new AffineTransform(2 * original.getScaleX(), 2 * original.getShearY(), 0.5 * original.getScaleX() + 3 * original.getShearX(), 0.5 * original.getShearY() + 3 * original.getScaleY(), 0, 0);
        expect(context.getTransform().equals(expected), "font context retains the linear transform and excludes translation");
        expect(context.isAntiAliased() && context.usesFractionalMetrics(), "font context retains explicit text hints");
        FontMetrics metrics = graphics.getFontMetrics();
        expect(metrics.getFontRenderContext().equals(context), "metrics capture their graphics font context");
        AffineTransform detached = context.getTransform();
        detached.setToIdentity();
        expect(context.getTransform().equals(expected), "font-context transform is defensive");
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
        graphics.setTransform(original);
        expect(context.isAntiAliased() && context.usesFractionalMetrics() && metrics.getFontRenderContext().equals(context), "existing contexts remain snapshots");
        expect(!graphics.getFontRenderContext().isAntiAliased() && !graphics.getFontRenderContext().usesFractionalMetrics(), "new context reflects changed hints");
        graphics.setColor(Color.GREEN);
        graphics.fillRect(offset + 20, 20, 100, 50);
        graphics.setColor(Color.BLUE);
        graphics.fillRect(offset + 20, 90, 50, 30);
        return "FRC scale 2x3, shear 0.5; translation excluded; AA/fractional snapshots isolated";
    }

    static int[][] pixels(int scenario, int offset) {
        if (scenario == 0) {
            return new int[][] {{offset + 24, 24, Color.GREEN.getRGB()}, {offset + 24, 34, Color.BLUE.getRGB()}, {offset + 24, 56, Color.GREEN.getRGB()}, {offset + 24, 66, Color.BLUE.getRGB()}, {offset + 24, 88, Color.GREEN.getRGB()}, {offset + 24, 120, Color.GREEN.getRGB()}, {offset + 275, 24, Color.RED.getRGB()}};
        }
        return new int[][] {{offset + 30, 30, Color.GREEN.getRGB()}, {offset + 30, 100, Color.BLUE.getRGB()}, {offset + 140, 30, Color.RED.getRGB()}};
    }

    private static void expect(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}
