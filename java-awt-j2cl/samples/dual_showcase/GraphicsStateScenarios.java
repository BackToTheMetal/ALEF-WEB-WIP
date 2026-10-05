package org.webawt.samples.dualshowcase;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

/** Shared, deterministic Graphics2D consumer scenarios for both rendering backends. */
public final class GraphicsStateScenarios {
    public static final String[] NAMES = {"Clip before translation", "Intersect transformed clips", "Clear with background", "Child graphics isolation", "Scaled clipped copy", "Overlapping copy", "Copy composite and limits", "Image overloads", "Affine and filtered images", "Transformed strokes", "Dash phase and joins", "Caps and joins", "Custom stroke primitives", "Dashed primitives", "Texture paint", "Custom paint lifecycle", "Gradient cycles and transforms", "Composite rules", "Image and copy composites", "Custom composite policy", "Font state and metrics", "Font render context", "Plain text overloads", "Transformed text", "Clipped translucent text", "Text clearing and limits", "Attributed text overloads", "Decorated transformed text", "Styled layout and wrapping", "Positioned glyphs and carets", "Filled primitive overloads", "Raised and sunken rectangles", "Transform and hint queries", "Hit clip and device queries", "Rendered and renderable images", "Paint modes and diagnostics", "Curves and mutable image reuse"};
    public static final int WIDTH = 320;
    public static final int HEIGHT = 180;

    private GraphicsStateScenarios() {
    }

    public static String paint(Graphics2D graphics, int scenario, int offset, boolean transparent) {
        graphics.setColor(Color.RED);
        graphics.fillRect(0, 0, WIDTH, HEIGHT);
        if (scenario == 36) {
            return PerformanceScenarios.paint(graphics, offset);
        }
        if (scenario >= 30) {
            return CoreGraphicsScenarios.paint(graphics, scenario - 30, offset);
        }
        if (scenario >= 26) {
            return AttributedScenarios.paint(graphics, scenario - 26, offset);
        }
        if (scenario >= 22) {
            return TextScenarios.paint(graphics, scenario - 22, offset);
        }
        if (scenario >= 20) {
            return FontScenarios.paint(graphics, scenario - 20, offset);
        }
        if (scenario >= 14) {
            return PaintScenarios.paint(graphics, scenario - 14, offset);
        }
        if (scenario >= 9) {
            return StrokeScenarios.paint(graphics, scenario - 9, offset);
        }
        if (scenario >= 4) {
            return paintTransfer(graphics, scenario, offset);
        }
        if (scenario == 0) {
            graphics.setClip(20, 20, 80, 60);
            graphics.translate(offset, 0);
            expect(new Rectangle(20 - offset, 20, 80, 60).equals(graphics.getClipBounds()), "clip stays fixed on the surface");
            Rectangle destination = new Rectangle();
            expect(graphics.getClipBounds(destination) == destination, "clip bounds reuse the supplied rectangle");
            expect(destination.equals(graphics.getClip().getBounds()), "shape and rectangle queries agree");
            graphics.setColor(Color.BLUE);
            graphics.fillRect(-offset, 0, 160, 140);
            return "Clip x=" + destination.x + ", width=" + destination.width + "; surface clip stays at x=20";
        }
        if (scenario == 1) {
            Rectangle original = new Rectangle(20, 20, 100, 80);
            graphics.setClip(original);
            original.setBounds(0, 0, 0, 0);
            graphics.translate((double) offset, 10.0);
            graphics.clip(new Rectangle(0, 20, 80, 40));
            graphics.clipRect(10, 0, 60, 100);
            Shape copy = graphics.getClip();
            expect(new Rectangle(10, 20, 60, 40).equals(copy.getBounds()), "clips intersect in surface coordinates");
            graphics.setColor(Color.BLUE);
            graphics.fillRect(0, 0, 160, 140);
            return "Intersection: x=10, y=20, width=60, height=40";
        }
        if (scenario == 2) {
            graphics.setClip(20, 20, 130, 100);
            graphics.translate(offset, 20);
            graphics.rotate(Math.PI / 2);
            graphics.scale(2, 1);
            graphics.setBackground(transparent ? new Color(0, 0, 0, 0) : Color.CYAN);
            graphics.setComposite(AlphaComposite.Dst);
            Paint paint = graphics.getPaint();
            AffineTransform transform = graphics.getTransform();
            Rectangle clip = graphics.getClipBounds();
            graphics.clearRect(0, -60, 60, 60);
            expect(paint.equals(graphics.getPaint()) && Color.RED.equals(graphics.getColor()), "clear preserves paint and color");
            expect(AlphaComposite.Dst.equals(graphics.getComposite()), "clear preserves composite");
            expect(transform.equals(graphics.getTransform()) && clip.equals(graphics.getClipBounds()), "clear preserves transform and clip");
            expect(graphics.getBackground().equals(transparent ? new Color(0, 0, 0, 0) : Color.CYAN), "background is preserved");
            graphics.clearRect(0, 0, -10, 20);
            graphics.clearRect(0, 0, 20, 0);
            return "90-degree rotation, 2x scale; clear ignores DST; background alpha=" + graphics.getBackground().getAlpha();
        }
        graphics.setClip(20, 20, 180, 100);
        AffineTransform parentTransform = graphics.getTransform();
        Rectangle parentClip = graphics.getClipBounds();
        Graphics2D child = (Graphics2D) graphics.create(offset, 10, 80, 80);
        try {
            child.setBackground(Color.GREEN);
            child.clearRect(0, 0, 80, 80);
            child.clipRect(10, 20, 20, 30);
            child.translate(10.0, 0.0);
            child.setColor(Color.BLUE);
            child.fillRect(0, 0, 80, 80);
        } finally {
            child.dispose();
        }
        expect(parentTransform.equals(graphics.getTransform()) && parentClip.equals(graphics.getClipBounds()), "child state is isolated");
        Graphics2D sibling = (Graphics2D) graphics.create();
        try {
            sibling.setClip((Shape) null);
            expect(sibling.getClip() == null, "null removes the user clip");
            AffineTransform copy = sibling.getTransform();
            copy.translate(999, 999);
            expect(parentTransform.equals(sibling.getTransform()), "transform query returns a copy");
            sibling.setTransform(parentTransform);
            sibling.setColor(Color.YELLOW);
            sibling.fillRect(150, 30, 20, 20);
        } finally {
            sibling.dispose();
        }
        return "Child clip/transform/background isolated; parent remains usable after dispose";
    }

    private static String paintTransfer(Graphics2D graphics, int scenario, int offset) {
        if (scenario == 4) {
            graphics.setColor(Color.BLUE);
            graphics.fillRect(offset + 20, 20, 40, 40);
            graphics.setColor(Color.GREEN);
            graphics.fillRect(offset + 60, 20, 40, 40);
            graphics.setClip(offset + 70, 70, 60, 50);
            graphics.translate(offset, 0);
            graphics.scale(2, 1);
            graphics.copyArea(10, 20, 40, 40, 20, 60);
            return "2x copy; destination clipped; source remains readable outside the clip";
        }
        if (scenario == 5) {
            graphics.translate(offset, 0);
            graphics.setColor(Color.BLUE);
            graphics.fillRect(80, 20, 20, 40);
            graphics.setColor(Color.GREEN);
            graphics.fillRect(100, 20, 20, 40);
            graphics.copyArea(80, 20, 40, 40, -10, 0);
            return "Copy overlaps its source; original pixels are snapshotted";
        }
        if (scenario == 6) {
            graphics.translate(offset, 0);
            graphics.setColor(Color.BLUE);
            graphics.fillRect(20, 20, 40, 40);
            graphics.setComposite(AlphaComposite.Src);
            graphics.copyArea(20, 20, 40, 40, 80, 0);
            graphics.setComposite(AlphaComposite.Dst);
            graphics.copyArea(20, 20, 40, 40, 160, 0);
            expect(AlphaComposite.Dst.equals(graphics.getComposite()), "copy preserves composite");
            AffineTransform saved = graphics.getTransform();
            for (int variant = 0; variant < 3; variant++) {
                if (variant == 0) {
                    graphics.rotate(Math.PI / 4);
                } else if (variant == 1) {
                    graphics.scale(-1, 1);
                } else {
                    graphics.shear(0.5, 0);
                }
                graphics.copyArea(0, 0, 0, 10, 20, 0);
                boolean rejected = false;
                try {
                    graphics.copyArea(0, 0, 10, 10, 20, 0);
                } catch (UnsupportedOperationException expected) {
                    rejected = true;
                } catch (Error expected) {
                    // InternalError is absent from the stock J2CL runtime.
                    rejected = "java.lang.InternalError".equals(expected.getClass().getName());
                }
                expect(rejected, "copy transform has the same explicit limit as the JDK");
                graphics.setTransform(saved);
            }
            return "SRC bounded; DST unchanged; rotation, reflection and shear rejected";
        }
        BufferedImage source = transferImage();
        try {
            if (scenario == 7) {
                for (int variant = 0; variant < 6; variant++) {
                    int x = offset + 10 + variant % 3 * 90;
                    int y = 20 + variant / 3 * 70;
                    boolean complete;
                    if (variant == 0) {
                        complete = graphics.drawImage(source, x, y, null);
                    } else if (variant == 1) {
                        complete = graphics.drawImage(source, x, y, 60, 60, null);
                    } else if (variant == 2) {
                        complete = graphics.drawImage(source, x, y, Color.CYAN, null);
                    } else if (variant == 3) {
                        complete = graphics.drawImage(source, x, y, 60, 60, Color.CYAN, null);
                    } else if (variant == 4) {
                        complete = graphics.drawImage(source, x + 60, y, x, y + 60, 0, 0, 40, 40, null);
                    } else {
                        complete = graphics.drawImage(source, x, y, x + 60, y + 60, 40, 0, 0, 40, Color.CYAN, null);
                    }
                    expect(complete, "loaded image overload returns true");
                }
                return "Six overloads: natural / scaled / background; destination and source reversal";
            }
            graphics.setClip(offset + 20, 20, 240, 120);
            graphics.translate(offset, 0);
            AffineTransform saved = graphics.getTransform();
            Rectangle clip = graphics.getClipBounds();
            AffineTransform placement = AffineTransform.getTranslateInstance(60, 20);
            placement.rotate(Math.PI / 2);
            placement.scale(1.5, 1.5);
            expect(graphics.drawImage(source, placement, null), "affine image returns true");
            graphics.drawImage(source, null, 100, 20);
            graphics.drawImage(source, new RescaleOp(0.0f, 255.0f, null), 170, 20);
            expect(saved.equals(graphics.getTransform()) && clip.equals(graphics.getClipBounds()), "image placement preserves graphics state");
            return "Rotated/scaled image and fixed clip; null operation and white rescale filter";
        } finally {
            source.flush();
        }
    }

    private static BufferedImage transferImage() {
        BufferedImage source = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < 40; y++) {
            for (int x = 0; x < 40; x++) {
                source.setRGB(x, y, y < 20 ? (x < 20 ? Color.BLUE : Color.GREEN).getRGB() : x < 20 ? 0 : Color.YELLOW.getRGB());
            }
        }
        return source;
    }

    public static BufferedImage image(int scenario, int offset, boolean transparent) {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        try {
            paint(graphics, scenario, offset, transparent);
        } finally {
            graphics.dispose();
        }
        return image;
    }

    /** Interior sample points avoid platform-specific antialiasing at path edges. */
    public static int[][] pixels(int scenario, int offset, boolean transparent) {
        if (scenario == 36) {
            return PerformanceScenarios.pixels(offset);
        }
        if (scenario >= 30) {
            return CoreGraphicsScenarios.pixels(scenario - 30, offset);
        }
        if (scenario >= 26) {
            return new int[][] {{5, 5, Color.RED.getRGB()}, {300, 175, Color.RED.getRGB()}};
        }
        if (scenario >= 22) {
            return TextScenarios.pixels(scenario - 22, offset);
        }
        if (scenario >= 20) {
            return FontScenarios.pixels(scenario - 20, offset);
        }
        if (scenario >= 14) {
            return PaintScenarios.pixels(scenario - 14, offset);
        }
        if (scenario >= 9) {
            return StrokeScenarios.pixels(scenario - 9, offset);
        }
        if (scenario >= 4) {
            return transferPixels(scenario, offset);
        }
        if (scenario == 0) {
            return new int[][] {{30, 30, Color.BLUE.getRGB()}, {90, 70, Color.BLUE.getRGB()}, {110, 30, Color.RED.getRGB()}, {10, 30, Color.RED.getRGB()}};
        }
        if (scenario == 1) {
            return new int[][] {{offset + 20, 40, Color.BLUE.getRGB()}, {offset + 5, 40, Color.RED.getRGB()}, {offset + 75, 40, Color.RED.getRGB()}, {offset + 20, 85, Color.RED.getRGB()}};
        }
        if (scenario == 2) {
            int cleared = transparent ? 0 : Color.CYAN.getRGB();
            return new int[][] {{offset + 10, 30, cleared}, {offset + 50, 110, cleared}, {offset + 70, 40, Color.RED.getRGB()}, {offset + 10, 130, Color.RED.getRGB()}, {10, 30, Color.RED.getRGB()}, {250, 150, Color.RED.getRGB()}};
        }
        return new int[][] {{offset + 15, 40, Color.BLUE.getRGB()}, {offset + 50, 40, Color.GREEN.getRGB()}, {155, 40, Color.YELLOW.getRGB()}, {210, 40, Color.RED.getRGB()}};
    }

    private static int[][] transferPixels(int scenario, int offset) {
        if (scenario == 4) {
            return new int[][] {{offset + 75, 90, Color.BLUE.getRGB()}, {offset + 115, 90, Color.GREEN.getRGB()}, {offset + 65, 90, Color.RED.getRGB()}, {offset + 135, 90, Color.RED.getRGB()}, {offset + 25, 30, Color.BLUE.getRGB()}, {offset + 65, 30, Color.GREEN.getRGB()}};
        }
        if (scenario == 5) {
            return new int[][] {{offset + 75, 30, Color.BLUE.getRGB()}, {offset + 95, 30, Color.GREEN.getRGB()}, {offset + 115, 30, Color.GREEN.getRGB()}, {offset + 65, 30, Color.RED.getRGB()}};
        }
        if (scenario == 6) {
            return new int[][] {{offset + 30, 30, Color.BLUE.getRGB()}, {offset + 110, 30, Color.BLUE.getRGB()}, {offset + 190, 30, Color.RED.getRGB()}, {offset + 210, 30, Color.RED.getRGB()}, {offset + 80, 30, Color.RED.getRGB()}, {offset + 110, 80, Color.RED.getRGB()}};
        }
        if (scenario == 7) {
            int[][] result = new int[24][3];
            for (int variant = 0; variant < 6; variant++) {
                int size = variant == 0 || variant == 2 ? 40 : 60;
                for (int quadrant = 0; quadrant < 4; quadrant++) {
                    int sourceQuadrant = variant >= 4 ? quadrant ^ 1 : quadrant;
                    int color = sourceQuadrant == 0 ? Color.BLUE.getRGB() : sourceQuadrant == 1 ? Color.GREEN.getRGB() : sourceQuadrant == 3 ? Color.YELLOW.getRGB() : (variant == 2 || variant == 3 || variant == 5 ? Color.CYAN : Color.RED).getRGB();
                    result[variant * 4 + quadrant] = new int[] {offset + 10 + variant % 3 * 90 + size / 4 + quadrant % 2 * size / 2, 20 + variant / 3 * 70 + size / 4 + quadrant / 2 * size / 2, color};
                }
            }
            return result;
        }
        return new int[][] {{offset + 45, 35, Color.BLUE.getRGB()}, {offset + 45, 65, Color.GREEN.getRGB()}, {offset + 25, 65, Color.YELLOW.getRGB()}, {offset + 25, 35, Color.RED.getRGB()}, {offset + 10, 65, Color.RED.getRGB()}, {offset + 110, 30, Color.BLUE.getRGB()}, {offset + 130, 30, Color.GREEN.getRGB()}, {offset + 110, 50, Color.RED.getRGB()}, {offset + 130, 50, Color.YELLOW.getRGB()}, {offset + 180, 30, Color.WHITE.getRGB()}, {offset + 180, 50, Color.RED.getRGB()}, {offset + 200, 50, Color.WHITE.getRGB()}};
    }

    public static boolean requiresAlphaSurface(int scenario) {
        return scenario == 17 || scenario == 18 || scenario == 25;
    }

    public static boolean matchesPixel(int expected, int actual, int tolerance) {
        for (int shift = 0; shift <= 24; shift += 8) {
            if (Math.abs((expected >>> shift & 255) - (actual >>> shift & 255)) > tolerance) {
                return false;
            }
        }
        return true;
    }

    private static void expect(boolean value, String description) {
        if (!value) {
            throw new IllegalStateException("Graphics state check failed: " + description);
        }
    }
}
