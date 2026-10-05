package org.webawt.samples.dualshowcase;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

/** Shared consumer vectors for stroke geometry, path continuity and custom strokes. */
final class StrokeScenarios {
    private StrokeScenarios() {
    }

    static String paint(Graphics2D graphics, int scenario, int offset) {
        if (scenario == 0) {
            graphics.setClip(offset + 30, 20, 160, 120);
            graphics.translate(offset, 0);
            graphics.scale(2, 3);
            BasicStroke wide = new BasicStroke(6, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
            graphics.setStroke(wide);
            expect(graphics.getStroke() == wide, "stroke query retains the supplied stroke");
            graphics.setColor(Color.BLUE);
            graphics.drawLine(10, 15, 100, 15);
            graphics.setColor(Color.GREEN);
            graphics.draw(new Line2D.Double(70, 25, 70, 40));
            graphics.setStroke(new BasicStroke());
            graphics.setColor(Color.BLUE);
            graphics.drawLine(20, 35, 50, 35);
            return "Scale 2x / 3x changes stroke thickness; fixed clip; default width also scales";
        }
        graphics.translate(offset, 0);
        graphics.setColor(Color.BLUE);
        if (scenario == 1) {
            BasicStroke dashed = new BasicStroke(10, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, new float[] {20, 10}, 5);
            graphics.setStroke(dashed);
            graphics.drawPolyline(new int[] {20, 90, 90}, new int[] {35, 35, 115}, 3);
            graphics.drawRect(140, 35, 60, 60);
            Graphics2D child = (Graphics2D) graphics.create();
            try {
                child.setStroke(new BasicStroke(10, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, new float[] {10, 10}, 0));
                child.drawLine(20, 145, 100, 145);
            } finally {
                child.dispose();
            }
            expect(graphics.getStroke() == dashed, "child stroke is isolated");
            graphics.drawLine(140, 145, 240, 145);
            return "Dash 20 / 10, phase 5 continues around corners; child dash leaves parent unchanged";
        }
        if (scenario == 2) {
            for (int cap = 0; cap < 3; cap++) {
                graphics.setStroke(new BasicStroke(14, cap, BasicStroke.JOIN_MITER));
                graphics.drawLine(20 + cap * 80, 30, 50 + cap * 80, 30);
            }
            for (int variant = 0; variant < 4; variant++) {
                int x = 30 + variant * 65;
                graphics.setStroke(new BasicStroke(14, BasicStroke.CAP_BUTT, variant == 3 ? BasicStroke.JOIN_MITER : variant, variant == 3 ? 1 : 10));
                graphics.drawPolyline(new int[] {x - 20, x, x}, new int[] {110, 110, 150}, 3);
            }
            return "Caps: butt / round / square. Joins: miter / round / bevel / limited miter";
        }
        int[] calls = {0};
        Stroke selected = scenario == 3 ? shape -> {
            calls[0]++;
            Rectangle2D bounds = shape.getBounds2D();
            return new Rectangle2D.Double(bounds.getX() - 4, bounds.getY() - 4, bounds.getWidth() + 8, bounds.getHeight() + 8);
        } : new BasicStroke(8, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, new float[] {12, 8}, 3);
        graphics.setStroke(selected);
        for (int variant = 0; variant < 8; variant++) {
            primitive(graphics, variant, 10 + variant % 4 * 65, 35 + variant / 4 * 75);
        }
        expect(graphics.getStroke() == selected, "drawing preserves stroke");
        if (scenario == 3) {
            expect(calls[0] == 8, "custom stroke receives each complete primitive once");
        }
        return scenario == 3 ? "Custom stroke fills expanded bounds: 8 complete shapes, 8 calls" : "Eight primitives share width 8, dash 12 / 8, phase 3, continuous paths";
    }

    private static void primitive(Graphics2D graphics, int variant, int x, int y) {
        if (variant == 0) {
            graphics.drawLine(x, y, x + 40, y);
        } else if (variant == 1) {
            graphics.drawRect(x, y, 40, 30);
        } else if (variant == 2) {
            graphics.drawRoundRect(x, y, 40, 30, 16, 16);
        } else if (variant == 3) {
            graphics.drawOval(x, y, 40, 30);
        } else if (variant == 4) {
            graphics.drawArc(x, y, 40, 30, 0, 360);
        } else if (variant == 5) {
            graphics.drawPolyline(new int[] {x, x + 40, x + 40}, new int[] {y, y, y + 30}, 3);
        } else if (variant == 6) {
            graphics.drawPolygon(new int[] {x, x + 40, x + 20}, new int[] {y, y, y + 30}, 3);
        } else {
            graphics.drawPolygon(new Polygon(new int[] {x, x + 40, x + 40, x}, new int[] {y, y, y + 30, y + 30}, 4));
        }
    }

    static int[][] pixels(int scenario, int offset) {
        int blue = Color.BLUE.getRGB();
        int red = Color.RED.getRGB();
        int[][] points;
        if (scenario == 0) {
            points = new int[][] {{40, 40, blue}, {40, 32, red}, {25, 45, red}, {195, 45, red}, {145, 90, Color.GREEN.getRGB()}, {148, 90, red}, {50, 104, blue}};
        } else if (scenario == 1) {
            points = new int[][] {{25, 35, blue}, {40, 35, red}, {50, 35, blue}, {90, 37, blue}, {90, 45, red}, {90, 55, blue}, {90, 75, red}, {200, 40, blue}, {200, 55, red}, {25, 145, blue}, {35, 145, red}, {150, 145, blue}, {160, 145, red}};
        } else if (scenario == 2) {
            points = new int[][] {{16, 30, red}, {96, 30, blue}, {94, 24, red}, {176, 30, blue}, {174, 24, blue}, {36, 104, blue}, {32, 108, blue}, {101, 104, red}, {99, 106, blue}, {164, 106, red}, {162, 108, blue}, {231, 104, red}, {227, 108, blue}};
        } else {
            points = new int[32][3];
            for (int variant = 0; variant < 8; variant++) {
                int x = 10 + variant % 4 * 65;
                int y = 35 + variant / 4 * 75;
                points[variant * 4] = new int[] {x + (scenario == 4 && variant >= 2 && variant <= 4 ? 20 : 5), y, blue};
                points[variant * 4 + 1] = new int[] {x + 20, y + (variant == 0 ? 0 : 15), scenario == 3 ? blue : variant == 0 ? blue : red};
                points[variant * 4 + 2] = new int[] {x + 47, y + 15, red};
                points[variant * 4 + 3] = new int[] {x + (variant == 2 ? 25 : variant == 3 ? 0 : variant == 4 ? 40 : 15), y + (variant == 3 || variant == 4 ? 15 : 0), scenario == 3 ? blue : red};
            }
        }
        for (int[] point : points) {
            point[0] += offset;
        }
        return points;
    }

    private static void expect(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}
