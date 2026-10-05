package org.webawt.samples.dualshowcase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;

final class GraphicsStateAssertions {
    private GraphicsStateAssertions() {
    }

    static void verify() {
        verify(true);
    }

    static void verify(boolean textPixels) {
        verifySourceBounds();
        BufferedImage scaled = new BufferedImage(640, 360, BufferedImage.TYPE_INT_ARGB);
        Graphics2D scaledGraphics = scaled.createGraphics();
        try {
            scaledGraphics.scale(2, 2);
            GraphicsStateScenarios.paint(scaledGraphics, 21, 20, false);
            assertEquals(Color.GREEN.getRGB(), scaled.getRGB(100, 60));
        } finally {
            scaledGraphics.dispose();
            scaled.flush();
        }
        for (int scenario = 0; scenario < GraphicsStateScenarios.NAMES.length; scenario++) {
            if (!textPixels && scenario >= 22 && scenario < 30) { continue; }
            for (int offset : new int[] {20, 40}) {
                for (boolean transparent : new boolean[] {false, true}) {
                    BufferedImage image = GraphicsStateScenarios.image(scenario, offset, transparent);
                    for (int[] pixel : GraphicsStateScenarios.pixels(scenario, offset, transparent)) {
                        int actual = image.getRGB(pixel[0], pixel[1]);
                        assertTrue(GraphicsStateScenarios.NAMES[scenario] + " at " + pixel[0] + "," + pixel[1] + ": expected=" + Integer.toHexString(pixel[2]) + ", actual=" + Integer.toHexString(actual), GraphicsStateScenarios.matchesPixel(pixel[2], actual, pixel.length > 3 ? pixel[3] : 0));
                    }
                    if (scenario == 36) { PerformanceScenarios.verifyHairline(image, offset); }
                    if (scenario >= 26 && scenario < 30) {
                        AttributedScenarios.verifyInk(image, scenario - 26, offset);
                    } else if (scenario >= 22 && scenario < 26) {
                        TextScenarios.verifyInk(image, scenario - 22, offset);
                    }
                    image.flush();
                }
            }
        }
    }

    private static void verifySourceBounds() {
        BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        try {
            graphics.setColor(Color.RED);
            graphics.fillRect(0, 0, 16, 16);
            graphics.setColor(Color.BLUE);
            graphics.fillRect(0, 0, 8, 8);
            graphics.setComposite(AlphaComposite.Src);
            graphics.copyArea(-4, 0, 8, 8, 8, 8);
            graphics.copyArea(-100, 0, 8, 8, 100, 8);
            for (int x = 0; x < 16; x++) {
                assertEquals("Only accessible source pixels are copied: " + x, (x >= 8 && x < 12 ? Color.BLUE : Color.RED).getRGB(), image.getRGB(x, 12));
            }
        } finally {
            graphics.dispose();
            image.flush();
        }
    }
}
