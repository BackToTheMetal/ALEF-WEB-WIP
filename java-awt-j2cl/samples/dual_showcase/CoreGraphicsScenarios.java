package org.webawt.samples.dualshowcase;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public final class CoreGraphicsScenarios {
    private static final Color FACE = new Color(80, 120, 180);
    private CoreGraphicsScenarios() {
    }

    public static String paint(Graphics2D graphics, int scenario, int offset) {
        graphics.setColor(Color.BLUE);
        if (scenario == 0) {
            graphics.fillOval(offset + 10, 20, 50, 50);
            graphics.fillRoundRect(offset + 80, 20, 50, 50, 20, 20);
            graphics.fillArc(offset + 150, 20, 50, 50, 0, 180);
            graphics.fillPolygon(new int[] {offset + 20, offset + 60, offset + 40}, new int[] {140, 140, 90}, 3);
            graphics.setColor(Color.GREEN);
            graphics.fillPolygon(new Polygon(new int[] {offset + 100, offset + 140, offset + 120}, new int[] {140, 140, 90}, 3));
            return "Filled oval / rounded rectangle / arc; polygon arrays (blue) and Polygon object (green)";
        }
        if (scenario == 1) {
            graphics.setColor(FACE);
            graphics.draw3DRect(offset + 20, 20, 80, 50, true);
            graphics.fill3DRect(offset + 130, 20, 80, 50, false);
            expect(graphics.getColor().equals(FACE), "3D primitives preserve color");
            graphics.fillRect(offset + 20, 100, 80, 30);
            return "Raised outline, sunken fill and restored face color: " + graphics.getColor();
        }
        if (scenario == 2) {
            Map<RenderingHints.Key, Object> hints = new HashMap<>();
            hints.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            graphics.setRenderingHints(hints);
            hints.clear();
            expect(graphics.getRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS) == RenderingHints.VALUE_FRACTIONALMETRICS_ON, "hint map copied");
            hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            graphics.addRenderingHints(hints);
            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            RenderingHints returned = graphics.getRenderingHints();
            returned.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
            expect(graphics.getRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS) == RenderingHints.VALUE_FRACTIONALMETRICS_ON, "returned hints are defensive");
            expect(graphics.getRenderingHint(RenderingHints.KEY_INTERPOLATION) == RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR, "add retains interpolation");
            expect(graphics.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING) == RenderingHints.VALUE_TEXT_ANTIALIAS_ON, "text hint retained");
            AffineTransform original = graphics.getTransform();
            AffineTransform copy = graphics.getTransform();
            copy.translate(1000, 1000);
            expect(graphics.getTransform().equals(original), "returned transform is defensive");
            graphics.translate(offset + 20, 20);
            graphics.rotate(Math.PI / 2, 10, 10);
            graphics.shear(0.5, 0);
            graphics.transform(AffineTransform.getScaleInstance(2, 1));
            graphics.fillRect(0, 0, 20, 20);
            graphics.setTransform(original);
            graphics.setColor(Color.GREEN);
            graphics.fillRect(offset + 80, 100, 40, 30);
            return "Anchored rotation + shear + concatenation; transform restored; copied/merged/queryable hints";
        }
        if (scenario == 3) {
            graphics.setClip(offset + 20, 20, 100, 100);
            expect(graphics.getClipRect().equals(new Rectangle(offset + 20, 20, 100, 100)), "legacy clip query");
            expect(graphics.hitClip(offset + 30, 30, 10, 10), "inside clip hit");
            expect(!graphics.hitClip(offset + 140, 30, 10, 10), "outside clip miss");
            Rectangle shape = new Rectangle(offset + 30, 30, 40, 40);
            AffineTransform device = graphics.getTransform();
            Rectangle target = device.createTransformedShape(new Rectangle(offset + 40, 40, 5, 5)).getBounds();
            expect(graphics.hit(target, shape, false), "device-space fill hit");
            Rectangle miss = device.createTransformedShape(new Rectangle(offset + 180, 100, 5, 5)).getBounds();
            expect(!graphics.hit(miss, shape, true), "stroked shape miss");
            expect(graphics.getDeviceConfiguration() != null && graphics.getDeviceConfiguration().getColorModel() != null, "device/color model available");
            graphics.fill(shape);
            graphics.setColor(Color.GREEN);
            graphics.fillRect(offset + 80, 80, 60, 60);
            return "Hit/clip queries and device configuration verified; green corner clipped to 100x100";
        }
        if (scenario == 4) {
            return RenderedImageScenarios.paint(graphics, offset);
        }
        graphics.setColor(Color.BLUE);
        graphics.fillRect(offset + 20, 20, 80, 50);
        String xor = "available";
        try {
            graphics.setXORMode(Color.WHITE);
            graphics.fillRect(offset + 20, 20, 80, 50);
            graphics.fillRect(offset + 20, 20, 80, 50);
        } catch (UnsupportedOperationException expected) {
            xor = "explicitly unavailable on Canvas";
        }
        graphics.setPaintMode();
        graphics.setColor(Color.GREEN);
        graphics.fillRect(offset + 130, 20, 70, 50);
        String description = graphics.toString();
        expect(description != null && description.contains("font=") && description.contains("color="), "graphics diagnostic includes state");
        Graphics2D child = (Graphics2D) graphics.create();
        child.setColor(Color.YELLOW);
        child.fillRect(offset + 20, 100, 80, 30);
        child.finalize();
        child.dispose();
        expect(graphics.getColor().equals(Color.GREEN), "explicit finalize/dispose preserves parent");
        return "XOR: " + xor + "; paint mode restored; diagnostic state and child finalize/dispose checked";
    }

    public static int[][] pixels(int scenario, int offset) {
        int blue = Color.BLUE.getRGB();
        int green = Color.GREEN.getRGB();
        int red = Color.RED.getRGB();
        if (scenario == 0) {
            return new int[][] {{offset + 35, 45, blue}, {offset + 105, 45, blue}, {offset + 175, 30, blue}, {offset + 175, 60, red}, {offset + 40, 125, blue}, {offset + 120, 125, green}, {offset + 15, 90, red}};
        }
        if (scenario == 1) {
            return new int[][] {{offset + 60, 20, FACE.brighter().getRGB()}, {offset + 60, 70, FACE.darker().getRGB()}, {offset + 170, 40, FACE.darker().getRGB()}, {offset + 60, 115, FACE.getRGB()}};
        }
        if (scenario == 2) {
            return new int[][] {{offset + 30, 40, blue}, {offset + 50, 40, red}, {offset + 100, 115, green}};
        }
        if (scenario == 3) {
            return new int[][] {{offset + 45, 45, blue}, {offset + 100, 100, green}, {offset + 130, 100, red}};
        }
        if (scenario == 4) { return RenderedImageScenarios.pixels(offset); }
        return new int[][] {{offset + 60, 45, blue}, {offset + 150, 45, green}, {offset + 60, 115, Color.YELLOW.getRGB()}};
    }

    private static void expect(boolean condition, String message) {
        if (!condition) { throw new AssertionError(message); }
    }
}
