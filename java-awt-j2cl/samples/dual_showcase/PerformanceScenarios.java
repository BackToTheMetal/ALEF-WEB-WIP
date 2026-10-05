package org.webawt.samples.dualshowcase;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public final class PerformanceScenarios {
    private PerformanceScenarios() {
    }

    public static String paint(Graphics2D graphics, int offset) {
        graphics.setColor(Color.BLUE);
        graphics.fillOval(offset + 10, 10, 100, 100);
        Path2D path = new Path2D.Double(Path2D.WIND_EVEN_ODD);
        path.moveTo(offset + 130, 10);
        path.curveTo(offset + 260, 0, offset + 270, 120, offset + 130, 110);
        path.closePath();
        path.moveTo(offset + 160, 35);
        path.lineTo(offset + 160, 85);
        path.lineTo(offset + 195, 60);
        path.closePath();
        graphics.setColor(Color.GREEN);
        graphics.fill(path);
        Graphics2D hairline = (Graphics2D) graphics.create();
        try {
            hairline.translate(offset + 10, 120);
            hairline.scale(3, 3);
            hairline.setColor(Color.BLUE);
            hairline.setStroke(new java.awt.BasicStroke(0));
            hairline.drawLine(0, 0, 30, 0);
        } finally { hairline.dispose(); }
        BufferedImage tile = new BufferedImage(12, 12, BufferedImage.TYPE_INT_ARGB);
        int[] storage = ((DataBufferInt) tile.getRaster().getDataBuffer()).getData();
        java.util.Arrays.fill(storage, Color.BLUE.getRGB());
        for (int index = 0; index < 8; index++) {
            graphics.drawImage(tile, offset + 10 + index * 15, 140, null);
        }
        java.util.Arrays.fill(storage, Color.GREEN.getRGB());
        graphics.drawImage(tile, offset + 150, 140, null);
        BufferedImage shared = tile.getSubimage(0, 0, 12, 12);
        for (int y = 0; y < 12; y++) {
            for (int x = 0; x < 12; x++) { shared.setRGB(x, y, Color.YELLOW.getRGB()); }
        }
        graphics.drawImage(tile, offset + 175, 140, null);
        return "Prepared curves, holes and a transformed one-pixel hairline; repeated uploads reuse pixels; array and subimage changes remain visible";
    }

    public static int[][] pixels(int offset) {
        return new int[][] {{offset + 60, 60, Color.BLUE.getRGB()}, {offset + 150, 60, Color.GREEN.getRGB()}, {offset + 170, 60, Color.RED.getRGB()}, {offset + 15, 145, Color.BLUE.getRGB()}, {offset + 155, 145, Color.GREEN.getRGB()}, {offset + 180, 145, Color.YELLOW.getRGB()}};
    }

    public static void verifyHairline(BufferedImage image, int offset) {
        int ink = 0;
        for (int y = 118; y <= 122; y++) {
            for (int x = offset + 20; x < offset + 90; x++) {
                if ((image.getRGB(x, y) & 255) > 100) { ink++; }
            }
        }
        if (ink < 60 || ink > 150) { throw new AssertionError("Transformed hairline coverage: " + ink); }
    }

    public static void curveWorkload(int repetitions) {
        BufferedImage image = new BufferedImage(256, 128, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        try {
            graphics.setColor(Color.BLUE);
            for (int index = 0; index < repetitions; index++) {
                graphics.fillOval(0, 0, 240, 120);
                graphics.fillRoundRect(20, 20, 180, 80, 32, 32);
            }
            if (image.getRGB(120, 60) != Color.BLUE.getRGB()) { throw new AssertionError("curve workload pixels"); }
        } finally { graphics.dispose(); }
    }

    public static void scalingWorkload() {
        BufferedImage source = new BufferedImage(512, 256, BufferedImage.TYPE_INT_ARGB);
        java.util.Arrays.fill(((DataBufferInt) source.getRaster().getDataBuffer()).getData(), Color.BLUE.getRGB());
        BufferedImage destination = new BufferedImage(256, 128, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = destination.createGraphics();
        try {
            for (int index = 0; index < 10; index++) { graphics.drawImage(source, 0, 0, 256, 128, null); }
            if (destination.getRGB(128, 64) != Color.BLUE.getRGB()) { throw new AssertionError("scaled image workload pixels"); }
        } finally { graphics.dispose(); }
    }

    public static void layoutWorkload() {
        java.awt.Panel panel = new java.awt.Panel(new java.awt.GridLayout(10, 10));
        for (int index = 0; index < 100; index++) { panel.add(new java.awt.Panel()); }
        for (int index = 0; index < 20; index++) {
            panel.setSize(600 + index * 10, 400 + index * 10);
            panel.doLayout();
        }
        if (panel.getComponent(99).getWidth() != 79 || panel.getComponent(99).getHeight() != 59) { throw new AssertionError("component layout workload bounds"); }
    }

}
