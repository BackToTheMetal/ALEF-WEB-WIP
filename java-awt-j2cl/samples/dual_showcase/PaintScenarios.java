package org.webawt.samples.dualshowcase;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Polygon;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;

final class PaintScenarios {
    private PaintScenarios() {
    }

    static String paint(Graphics2D graphics, int scenario, int offset) {
        if (scenario == 0) {
            BufferedImage tile = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
            for (int y = 0; y < 16; y++) {
                for (int x = 0; x < 16; x++) {
                    tile.setRGB(x, y, (y < 8 ? x < 8 ? Color.BLUE : Color.GREEN : x < 8 ? Color.YELLOW : Color.CYAN).getRGB());
                }
            }
            Paint texture = new TexturePaint(tile, new Rectangle(0, 0, 32, 32));
            graphics.setClip(offset + 30, 30, 180, 100);
            graphics.translate(offset + 20, 20);
            graphics.scale(2, 1);
            graphics.setPaint(texture);
            graphics.setPaint(null);
            expect(graphics.getPaint() == texture && graphics.getColor().equals(Color.RED), "paint and color stay independent; null paint is ignored");
            graphics.setComposite(AlphaComposite.Src);
            graphics.fill(new Rectangle(0, 0, 120, 120));
            tile.flush();
            return "Texture anchor 32x32, 2x horizontal scale, fixed clip; paint retained, color red";
        }
        graphics.translate(offset, 0);
        if (scenario == 1) {
            int[] lifecycle = new int[3];
            Paint custom = customPaint(lifecycle, false);
            graphics.setPaint(custom);
            graphics.fill(new Rectangle(20, 20, 100, 70));
            expect(lifecycle[0] > 0 && lifecycle[0] == lifecycle[2] && lifecycle[1] > 0, "paint contexts are disposed after use");
            Paint failed = customPaint(lifecycle, true);
            graphics.setPaint(failed);
            AffineTransform transform = graphics.getTransform();
            Composite composite = graphics.getComposite();
            boolean caught = false;
            try {
                graphics.fill(new Rectangle(20, 100, 100, 40));
            } catch (IllegalStateException expected) {
                caught = true;
            }
            expect(caught, "paint failure reaches the caller");
            expect(graphics.getPaint() == failed && graphics.getTransform().equals(transform) && graphics.getComposite() == composite, "failed paint preserves graphics state");
            graphics.setColor(Color.BLUE);
            expect(graphics.getPaint().equals(Color.BLUE), "setColor replaces paint");
            graphics.fill(new Rectangle(140, 20, 60, 70));
            graphics.setPaint(custom);
            graphics.setStroke(new BasicStroke(6));
            graphics.drawLine(20, 145, 100, 145);
            String contexts = "Contexts created/disposed: " + lifecycle[0] + "/" + lifecycle[2];
            String textPaint = "returned";
            try {
                graphics.drawString("Custom paint text", 140, 150);
            } catch (UnsupportedOperationException expected) {
                textPaint = "rejected";
                expect(graphics.getPaint() == custom, "unsupported text paint preserves state");
            }
            graphics.setColor(Color.BLUE);
            graphics.fill(new Rectangle(220, 100, 40, 20));
            return contexts + "; paint failure caught; text paint " + textPaint + "; blue recovery";
        }
        if (scenario == 2) {
            graphics.setPaint(new GradientPaint(0, 0, Color.BLUE, 40, 0, Color.GREEN, true));
            graphics.fill(new Rectangle(0, 20, 240, 30));
            graphics.setPaint(new LinearGradientPaint(new Point2D.Double(0, 0), new Point2D.Double(40, 0), new float[] {0, 1}, new Color[] {Color.BLUE, Color.GREEN}, MultipleGradientPaint.CycleMethod.REPEAT, MultipleGradientPaint.ColorSpaceType.LINEAR_RGB, AffineTransform.getScaleInstance(2, 1)));
            graphics.fill(new Rectangle(0, 65, 240, 30));
            graphics.setPaint(new RadialGradientPaint(new Point2D.Double(60, 125), 20, new Point2D.Double(60, 125), new float[] {0, 1}, new Color[] {Color.BLUE, Color.GREEN}, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, AffineTransform.getScaleInstance(2, 1)));
            graphics.fill(new Rectangle(70, 100, 100, 50));
            return "Reflect gradient; repeat in linear RGB; radial paint transformed into an ellipse";
        }
        if (scenario == 3) {
            for (int rule = 1; rule <= 12; rule++) {
                Graphics2D cell = (Graphics2D) graphics.create(8 + (rule - 1) % 4 * 65, 8 + (rule - 1) / 4 * 55, 56, 45);
                try {
                    cell.setColor(Color.BLUE);
                    cell.fillRect(0, 0, 40, 35);
                    cell.setComposite(AlphaComposite.getInstance(rule));
                    cell.setColor(rule == AlphaComposite.CLEAR ? new Color(0, 255, 0, 0) : Color.GREEN);
                    cell.fill(new Polygon(new int[] {15, 32, 50}, new int[] {35, 5, 35}, 3));
                    expect(((AlphaComposite) cell.getComposite()).getRule() == rule, "composite rule is retained");
                } finally {
                    cell.dispose();
                }
            }
            return "12 AlphaComposite rules in numeric order; green triangle affects only its shape";
        }
        if (scenario == 4) {
            BufferedImage source = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
            for (int y = 0; y < 40; y++) {
                for (int x = 0; x < 20; x++) {
                    source.setRGB(x, y, Color.BLUE.getRGB());
                }
            }
            int[] unused = new int[3];
            Paint ignored = customPaint(unused, true);
            graphics.setPaint(ignored);
            graphics.setComposite(AlphaComposite.Src);
            graphics.drawImage(source, 20, 20, null);
            graphics.setComposite(AlphaComposite.Clear);
            graphics.drawImage(source, 100, 20, null);
            graphics.setComposite(AlphaComposite.Src);
            graphics.copyArea(20, 20, 40, 40, 140, 0);
            graphics.setComposite(AlphaComposite.Clear);
            graphics.copyArea(20, 20, 40, 40, 220, 0);
            graphics.setComposite(AlphaComposite.Src);
            graphics.drawImage(source, 20, 100, 100, 140, -20, 0, 60, 40, null);
            expect(unused[0] == 0 && graphics.getPaint() == ignored, "image and copy operations ignore current paint");
            source.flush();
            return "Image/copy SRC and CLEAR: transparent source, bounded output, source clipping, paint ignored";
        }
        Composite previous = graphics.getComposite();
        Composite custom = (source, destination, hints) -> AlphaComposite.SrcOver.createContext(source, destination, hints);
        boolean accepted = false;
        try {
            graphics.setComposite(custom);
            accepted = graphics.getComposite() == custom;
        } catch (UnsupportedOperationException expected) {
            expect(graphics.getComposite() == previous, "rejected composite leaves state unchanged");
        }
        Composite retained = graphics.getComposite();
        boolean invalid = false;
        try {
            graphics.setComposite(null);
        } catch (IllegalArgumentException expected) {
            invalid = true;
        }
        expect(invalid && graphics.getComposite() == retained, "null composite is rejected without changing state");
        graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 128 / 255.0f));
        graphics.setPaint(Color.GREEN);
        graphics.fill(new Rectangle(20, 20, 80, 60));
        graphics.setComposite(AlphaComposite.Dst);
        graphics.setPaint(Color.BLUE);
        graphics.fill(new Rectangle(120, 20, 60, 60));
        graphics.setComposite(previous);
        graphics.fill(new Rectangle(200, 20, 50, 60));
        return "Custom composite " + (accepted ? "accepted (JDK)" : "rejected (WebAWT)") + "; alpha 128/255; DST unchanged";
    }

    private static Paint customPaint(int[] lifecycle, boolean fail) {
        return new Paint() {
            @Override
            public int getTransparency() {
                return OPAQUE;
            }
            @Override
            public PaintContext createContext(ColorModel model, Rectangle device, Rectangle2D user, AffineTransform transform, RenderingHints hints) {
                lifecycle[0]++;
                PaintContext delegate = Color.CYAN.createContext(model, device, user, transform, hints);
                return new PaintContext() {
                    @Override
                    public ColorModel getColorModel() {
                        return delegate.getColorModel();
                    }
                    @Override
                    public Raster getRaster(int x, int y, int width, int height) {
                        lifecycle[1]++;
                        if (fail) {
                            throw new IllegalStateException("paint raster failure");
                        }
                        return delegate.getRaster(x, y, width, height);
                    }
                    @Override
                    public void dispose() {
                        lifecycle[2]++;
                        delegate.dispose();
                    }
                };
            }
        };
    }

    // Vectors contain x, y, ARGB, optional channel tolerance, and optional native-window tolerance.
    static int[][] pixels(int scenario, int offset) {
        int blue = Color.BLUE.getRGB();
        int green = Color.GREEN.getRGB();
        int red = Color.RED.getRGB();
        int[][] points;
        if (scenario == 0) {
            points = new int[][] {{40, 32, blue}, {64, 32, green}, {40, 44, Color.YELLOW.getRGB()}, {64, 44, Color.CYAN.getRGB()}, {104, 32, blue}, {168, 32, blue}, {24, 32, red}, {40, 24, red}, {220, 40, red}};
        } else if (scenario == 1) {
            points = new int[][] {{30, 30, Color.CYAN.getRGB()}, {110, 80, Color.CYAN.getRGB()}, {150, 30, blue}, {30, 110, red}, {210, 30, red}, {30, 145, Color.CYAN.getRGB()}, {230, 110, blue}};
        } else if (scenario == 2) {
            points = new int[][] {{10, 35, 0xff0040bf, 3, 4}, {50, 35, 0xff00bf40, 3, 4}, {90, 35, 0xff0040bf, 3, 4}, {20, 80, 0xff0089e1, 3}, {100, 80, 0xff0089e1, 3}, {60, 80, 0xff00e189, 3}, {120, 125, blue, 6, 8}, {140, 125, 0xff008080, 6}, {120, 135, 0xff008080, 6}, {165, 125, green, 3}};
        } else if (scenario == 3) {
            int[] overlap = {0, green, green, blue, green, blue, 0, 0, blue, green, blue, 0};
            points = new int[48][3];
            for (int rule = 1; rule <= 12; rule++) {
                int x = 8 + (rule - 1) % 4 * 65;
                int y = 8 + (rule - 1) / 4 * 55;
                points[(rule - 1) * 4] = new int[] {x + 25, y + 20, overlap[rule - 1]};
                points[(rule - 1) * 4 + 1] = new int[] {x + 5, y + 20, blue};
                points[(rule - 1) * 4 + 2] = new int[] {x + 16, y + 6, blue};
                points[(rule - 1) * 4 + 3] = new int[] {x + 53, y + 20, red};
            }
        } else if (scenario == 4) {
            points = new int[][] {{30, 30, blue}, {50, 30, 0}, {110, 30, 0}, {130, 30, 0}, {170, 30, blue}, {190, 30, 0}, {250, 30, 0}, {270, 30, 0}, {10, 30, red}, {90, 30, red}, {30, 110, red}, {50, 110, blue}, {70, 110, 0}, {90, 110, red}};
        } else {
            points = new int[][] {{30, 30, 0xff7f8000, 1}, {130, 30, red}, {210, 30, blue}, {10, 30, red}};
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
