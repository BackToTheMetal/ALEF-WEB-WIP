package org.webawt.samples.dualshowcase;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/** Plain-text painting shared by the JDK and browser consumer. */
public final class TextScenarios {
    private TextScenarios() {
    }

    public static String paint(Graphics2D graphics, int scenario, int offset) {
        Font font = FontScenarios.referenceFont();
        graphics.setFont(font);
        graphics.setColor(Color.BLUE);
        if (scenario == 0) {
            graphics.drawString("WebAWT canvas", offset + 20, 35);
            graphics.drawString("Cafe\u0301", offset + 20.5f, 75.25f);
            graphics.drawChars("<AV\u00e9>".toCharArray(), 1, 3, offset + 20, 115);
            graphics.drawBytes(new byte[] {0, 65, 86, (byte) 233, 0}, 1, 3, offset + 20, 155);
            return "String int / String float / char slice / unsigned byte slice";
        }
        if (scenario == 1) {
            Graphics2D child = (Graphics2D) graphics.create();
            try {
                child.translate(offset + 25, 65);
                child.rotate(-Math.PI / 6);
                child.drawString("Hi", 0, 0);
            } finally {
                child.dispose();
            }
            child = (Graphics2D) graphics.create();
            try {
                child.translate(offset + 130, 60);
                child.scale(1.5, 0.8);
                child.shear(0.3, 0);
                child.drawString("AV", 0.5f, 0.25f);
            } finally {
                child.dispose();
            }
            AffineTransform fontTransform = new AffineTransform(1.2, 0.2, -0.2, 1.1, 3, -2);
            graphics.setFont(font.deriveFont(fontTransform));
            graphics.drawString("Tilt", offset + 30, 130);
            graphics.setFont(font);
            return "Rotated baseline; scaled/sheared graphics; transformed font; child isolation";
        }
        if (scenario == 2) {
            graphics.setClip(offset + 35, 25, 100, 100);
            graphics.translate(offset + 20, 45);
            graphics.rotate(Math.PI / 12);
            graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            graphics.drawString("Clipped text", 0, 0);
            graphics.drawString("Second row", 0.5f, 45.25f);
            return "Fixed surface clip, rotation and SRC_OVER 50%; no pixels escape the clip";
        }
        graphics.setColor(Color.GREEN);
        graphics.fillRect(20, 20, 240, 120);
        graphics.setComposite(AlphaComposite.Clear);
        graphics.drawString("Clear", offset + 20, 60);
        graphics.setComposite(AlphaComposite.Src);
        String policy = "accepted (JDK)";
        try {
            graphics.drawString("Off-surface SRC probe", 10000, 10000);
        } catch (UnsupportedOperationException expected) {
            policy = "rejected (Canvas profile)";
            if (!graphics.getComposite().equals(AlphaComposite.Src)) {
                throw new AssertionError("Rejected text preserves composite state");
            }
        }
        graphics.setComposite(AlphaComposite.SrcOver);
        graphics.setColor(Color.BLUE);
        graphics.drawString("After", offset + 20, 120);
        return "CLEAR glyph coverage and recovery; SRC text " + policy;
    }

    public static int[][] pixels(int scenario, int offset) {
        return new int[][] {{5, 5, Color.RED.getRGB()}, {300, 170, Color.RED.getRGB()}, {10, 100, Color.RED.getRGB()}};
    }

    /** Generous ink regions; exact outlines vary with the runtime rasterizer. */
    public static int[][] regions(int scenario, int offset) {
        if (scenario == 0) {
            return new int[][] {{offset + 15, 10, 240, 30}, {offset + 15, 45, 150, 35}, {offset + 15, 85, 150, 35}, {offset + 15, 125, 150, 35}};
        }
        if (scenario == 1) {
            return new int[][] {{offset + 10, 20, 90, 60}, {offset + 120, 25, 100, 50}, {offset + 20, 95, 120, 55}};
        }
        if (scenario == 2) {
            return new int[][] {{offset + 35, 25, 100, 45}, {offset + 35, 70, 100, 55}};
        }
        return new int[][] {{offset + 15, 30, 110, 35}, {offset + 15, 90, 110, 35}};
    }

    public static void verifyInk(BufferedImage image, int scenario, int offset) {
        int regionIndex = 0;
        for (int[] region : regions(scenario, offset)) {
            int count = 0;
            for (int y = region[1]; y < region[1] + region[3]; y++) {
                for (int x = region[0]; x < region[0] + region[2]; x++) {
                    int pixel = image.getRGB(x, y);
                    boolean ink = scenario == 3 && regionIndex == 0 ? (pixel >>> 24) < 240 : (pixel & 255) > 40;
                    if (ink) {
                        count++;
                    }
                }
            }
            if (count < 35) {
                throw new AssertionError("Missing text ink in scenario " + scenario + " region " + regionIndex + ": " + count);
            }
            regionIndex++;
        }
        if (scenario == 2) {
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    if ((x < offset + 35 || x >= offset + 135 || y < 25 || y >= 125) && image.getRGB(x, y) != Color.RED.getRGB()) {
                        throw new AssertionError("Text escaped the surface clip");
                    }
                }
            }
        }
    }
}
