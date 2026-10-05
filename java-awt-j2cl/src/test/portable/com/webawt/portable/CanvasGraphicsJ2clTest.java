package com.webawt.portable;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.image.BufferedImage;
import org.junit.Test;
import org.webawt.internal.graphics.CanvasGraphics2D;
import org.webawt.internal.peer.ModelCanvasPeer;

public final class CanvasGraphicsJ2clTest {
    @Test
    public void transformsTextGlyphsAndRejectsUnboundedComposites() {
        ModelCanvasPeer peer = new ModelCanvasPeer();
        CanvasGraphics2D graphics = new CanvasGraphics2D(peer);
        graphics.setClip(2, 3, 60, 40);
        graphics.translate(10, 20);
        graphics.scale(2, 3);
        graphics.setFont(new java.awt.Font("Dialog", 0, 24).deriveFont(new java.awt.geom.AffineTransform(1, 0.5, 0, 1, 4, 5)));
        graphics.drawString("AV", 7.5f, 9.25f);
        assertContains(peer.getCommands(), "transform(2,1.5,0,3,33,62.75)");
        assertContains(peer.getCommands(), "fillText(AV,0,0)");
        org.junit.Assert.assertEquals("restore()", peer.getCommands()[peer.getCommands().length - 1]);
        graphics.setComposite(AlphaComposite.Src);
        String[] before = peer.getCommands();
        try {
            graphics.drawString("SRC", 0, 0);
            org.junit.Assert.fail("Expected explicit unsupported composite");
        } catch (UnsupportedOperationException expected) {
            org.junit.Assert.assertArrayEquals(before, peer.getCommands());
        }
        graphics.setComposite(AlphaComposite.SrcOver);
        graphics.drawBytes(new byte[] {0, 65, (byte) 233, 0}, 1, 2, 0, 0);
        assertContains(peer.getCommands(), "fillText(A\u00e9,0,0)");
        try {
            graphics.drawBytes(new byte[0], 1, 0, 0, 0);
            org.junit.Assert.fail("Invalid empty range must fail");
        } catch (ArrayIndexOutOfBoundsException expected) {
            // The range is checked even for zero length.
        }
    }

    @Test
    public void appliesTextCoverageAfterCompositingAndKeepsUntouchedPixels() {
        org.webawt.internal.peer.FontRasterizer rasterizer = new org.webawt.internal.peer.FontRasterizer() {
            @Override
            public double[] bounds(String css, String text) {
                return new double[] {0, 0, 3, 1};
            }
            @Override
            public int[] rasterize(String css, String text, double[] matrix, int x, int y, int width, int height) {
                org.junit.Assert.assertEquals(2, matrix[4], 0);
                org.junit.Assert.assertEquals(2, matrix[5], 0);
                int[] coverage = new int[width * height];
                if (y <= 2 && y + height > 2) {
                    coverage[(2 - y) * width + 2 - x] = 255;
                    coverage[(2 - y) * width + 3 - x] = 128;
                }
                return coverage;
            }
        };
        org.webawt.internal.peer.WebAwtRuntime.installFontRasterizer(rasterizer);
        try {
            for (AlphaComposite composite : new AlphaComposite[] {AlphaComposite.Src, AlphaComposite.Clear, AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f)}) {
                BufferedImage image = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
                Graphics2D graphics = image.createGraphics();
                graphics.setColor(Color.RED);
                graphics.fillRect(0, 0, 8, 8);
                graphics.setColor(Color.BLUE);
                graphics.setComposite(composite);
                graphics.drawString("ink", 2, 2);
                int rule = composite.getRule();
                int solid = rule == AlphaComposite.CLEAR ? 0 : rule == AlphaComposite.SRC ? 0xff0000ff : 0xff800080;
                int edge = rule == AlphaComposite.CLEAR ? 0x7fff0000 : rule == AlphaComposite.SRC ? 0xff7f0080 : 0xffbf0040;
                org.junit.Assert.assertEquals(solid, image.getRGB(2, 2));
                org.junit.Assert.assertTrue(org.webawt.samples.dualshowcase.GraphicsStateScenarios.matchesPixel(edge, image.getRGB(3, 2), 1));
                org.junit.Assert.assertEquals(Color.RED.getRGB(), image.getRGB(4, 2));
                org.junit.Assert.assertEquals(Color.RED.getRGB(), image.getRGB(7, 7));
                graphics.dispose();
            }
        } finally {
            org.webawt.internal.peer.WebAwtRuntime.installFontRasterizer(null);
        }
        try {
            new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB).createGraphics().drawString("ink", 2, 2);
            org.junit.Assert.fail("Uninstalled rasterization must fail explicitly");
        } catch (UnsupportedOperationException expected) {
            // Model execution cannot manufacture actual glyph pixels.
        }
    }

    @Test
    public void boundsTextTilesAndDisposesPaintContextAfterRasterizerFailure() {
        int[] calls = new int[2];
        org.webawt.internal.peer.WebAwtRuntime.installFontRasterizer(new org.webawt.internal.peer.FontRasterizer() {
            @Override
            public double[] bounds(String css, String text) {
                return new double[] {0, 0, 300, 4};
            }
            @Override
            public int[] rasterize(String css, String text, double[] matrix, int x, int y, int width, int height) {
                org.junit.Assert.assertTrue(width <= 128 && height <= 128);
                if (++calls[0] == 2) {
                    throw new IllegalStateException("rasterization failed");
                }
                int[] coverage = new int[width * height];
                java.util.Arrays.fill(coverage, 255);
                return coverage;
            }
        });
        try {
            BufferedImage image = new BufferedImage(400, 16, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = image.createGraphics();
            graphics.setPaint(new Color(0, 0, 255) {
                @Override
                public java.awt.PaintContext createContext(java.awt.image.ColorModel model, java.awt.Rectangle device, java.awt.geom.Rectangle2D user, java.awt.geom.AffineTransform transform, java.awt.RenderingHints hints) {
                    java.awt.PaintContext delegate = Color.BLUE.createContext(model, device, user, transform, hints);
                    return new java.awt.PaintContext() {
                        @Override
                        public java.awt.image.ColorModel getColorModel() {
                            return delegate.getColorModel();
                        }
                        @Override
                        public java.awt.image.Raster getRaster(int x, int y, int width, int height) {
                            return delegate.getRaster(x, y, width, height);
                        }
                        @Override
                        public void dispose() {
                            calls[1]++;
                            delegate.dispose();
                        }
                    };
                }
            });
            java.awt.Paint paint = graphics.getPaint();
            try {
                graphics.drawString("wide", 0, 5);
                org.junit.Assert.fail("Expected second tile failure");
            } catch (IllegalStateException expected) {
                org.junit.Assert.assertEquals(2, calls[0]);
                org.junit.Assert.assertEquals(1, calls[1]);
                org.junit.Assert.assertSame(paint, graphics.getPaint());
            }
            graphics.setColor(Color.GREEN);
            graphics.fillRect(350, 0, 5, 5);
            org.junit.Assert.assertEquals(Color.GREEN.getRGB(), image.getRGB(351, 1));
            graphics.dispose();
        } finally {
            org.webawt.internal.peer.WebAwtRuntime.installFontRasterizer(null);
        }
    }

    @Test
    public void measuresCompleteTextAndRefreshesAfterFontProviderChanges() {
        org.webawt.internal.peer.WebAwtRuntime.installFontMeasurementProvider((css, text) -> new double[] {text.equals("AV") ? 7.25 : text.length() * 4.0, 14, 4});
        try {
            java.awt.Font font = new java.awt.Font(java.awt.Font.SANS_SERIF, java.awt.Font.PLAIN, 24);
            java.awt.font.FontRenderContext context = new java.awt.font.FontRenderContext(null, true, true);
            java.awt.FontMetrics metrics = new java.awt.FontMetrics(font) {
            };
            org.junit.Assert.assertEquals(7.25, font.getStringBounds("AV", context).getWidth(), 0.001);
            org.junit.Assert.assertEquals(7, metrics.stringWidth("AV"));
            org.junit.Assert.assertEquals(7, metrics.charsWidth("<AV>".toCharArray(), 1, 2));
            org.junit.Assert.assertEquals(7, metrics.bytesWidth(new byte[] {0, 65, 86, 0}, 1, 2));
            org.junit.Assert.assertEquals(18, metrics.getHeight());
            java.text.StringCharacterIterator iterator = new java.text.StringCharacterIterator("<AV>");
            iterator.setIndex(3);
            org.junit.Assert.assertEquals(7.25, font.getStringBounds(iterator, 1, 3, context).getWidth(), 0.001);
            org.junit.Assert.assertEquals(3, iterator.getIndex());
            org.junit.Assert.assertEquals(14.5, font.deriveFont(java.awt.geom.AffineTransform.getScaleInstance(2, 1)).getStringBounds("AV", context).getWidth(), 0.001);
            org.webawt.internal.peer.WebAwtRuntime.installFontMeasurementProvider((css, text) -> new double[] {12.5, 14, 4});
            org.junit.Assert.assertEquals(13, metrics.stringWidth("AV"));
        } finally {
            org.webawt.internal.peer.WebAwtRuntime.installFontMeasurementProvider((css, text) -> null);
        }
    }

    @Test
    public void runsSharedShowcaseStateScenariosOnCanvas() {
        for (int scenario = 0; scenario < org.webawt.samples.dualshowcase.GraphicsStateScenarios.NAMES.length; scenario++) {
            ModelCanvasPeer peer = new ModelCanvasPeer();
            CanvasGraphics2D graphics = new CanvasGraphics2D(peer);
            if (scenario >= 28 && scenario < 30) {
                continue;
            }
            String report = org.webawt.samples.dualshowcase.GraphicsStateScenarios.paint(graphics, scenario, 20, false);
            if (scenario == 15 && !report.contains("Contexts created/disposed: 3/3")) {
                throw new AssertionError(report);
            }
            if (scenario == 15 && !report.contains("text paint rejected")) {
                throw new AssertionError(report);
            }
            if (scenario == 19 && !report.contains("Custom composite rejected (WebAWT)")) {
                throw new AssertionError(report);
            }
            if (scenario == 2) {
                assertContains(peer.getCommands(), "globalAlpha(1)");
                assertContains(peer.getCommands(), "composite(source-over)");
                assertContains(peer.getCommands(), "fillStyle(rgba(0,255,255,1))");
            } else if (scenario == 4) {
                assertContains(peer.getCommands(), "copyArea(40,20,80,40,40,60)");
                assertContains(peer.getCommands(), "clip(nonzero)");
            } else if (scenario == 5) {
                assertContains(peer.getCommands(), "copyArea(100,20,40,40,-10,0)");
            } else if (scenario == 6) {
                assertContains(peer.getCommands(), "composite(copy)");
            } else if (scenario == 9) {
                assertContains(peer.getCommands(), "transform(2,0,0,3,20,0)");
                assertContains(peer.getCommands(), "lineWidth(6)");
            } else if (scenario == 10) {
                assertContains(peer.getCommands(), "lineDash([20, 10],5)");
                assertContains(peer.getCommands(), "lineDash([10, 10],0)");
            }
            graphics.dispose();
        }
    }

    @Test
    public void disposesFailedBufferedImagePaintContext() {
        BufferedImage image = new BufferedImage(320, 180, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        try {
            String report = org.webawt.samples.dualshowcase.GraphicsStateScenarios.paint(graphics, 15, 20, false);
            if (!report.contains("Contexts created/disposed: 3/3")) {
                throw new AssertionError(report);
            }
            report = org.webawt.samples.dualshowcase.GraphicsStateScenarios.paint(graphics, 19, 20, false);
            if (!report.contains("Custom composite rejected (WebAWT)")) {
                throw new AssertionError(report);
            }
        } finally {
            graphics.dispose();
        }
    }

    @Test
    public void paintsUnicodeGlyphsUsingNativeTextWithPositionsAndTransforms() {
        ModelCanvasPeer peer = new ModelCanvasPeer();
        CanvasGraphics2D graphics = new CanvasGraphics2D(peer);
        java.awt.Font font = new java.awt.Font(java.awt.Font.SERIF, java.awt.Font.PLAIN, 24);
        java.awt.font.GlyphVector glyphs = font.createGlyphVector(graphics.getFontRenderContext(), "A\ud83d\ude42");
        glyphs.setGlyphPosition(0, new java.awt.geom.Point2D.Double(2, 3));
        glyphs.setGlyphTransform(0, java.awt.geom.AffineTransform.getScaleInstance(2, 1));
        graphics.translate(10, 20);
        graphics.drawGlyphVector(glyphs, 5, 7);
        assertContains(peer.getCommands(), "fillText(A,0,0)");
        assertContains(peer.getCommands(), "fillText(\ud83d\ude42,0,0)");
        assertContains(peer.getCommands(), "transform(2,0,0,1,17,30)");
        graphics.dispose();
    }

    @Test
    public void recordsTransformedCanvasPathCommands() {
        ModelCanvasPeer peer = new ModelCanvasPeer();
        CanvasGraphics2D graphics = new CanvasGraphics2D(peer);
        graphics.setColor(new Color(255, 0, 0, 128));
        graphics.translate(2, 3);
        graphics.drawLine(0, 0, 4, 5);
        String[] commands = peer.getCommands();
        assertStartsWith(commands, "strokeStyle(rgba(255,0,0,");
        assertContains(commands, "transform(1,0,0,1,2,3)");
        assertStartsWith(commands, "moveTo(0,");
        assertStartsWith(commands, "lineTo(4,");
        assertContains(commands, "stroke()");
    }

    @Test
    public void copiesGraphicsStateIndependently() {
        ModelCanvasPeer peer = new ModelCanvasPeer();
        CanvasGraphics2D original = new CanvasGraphics2D(peer);
        original.translate(5, 0);
        Graphics2D copy = (Graphics2D) original.create();
        copy.translate(2, 0);
        copy.fillRect(0, 0, 3, 4);
        original.fillRect(0, 0, 3, 4);
        String[] commands = peer.getCommands();
        assertStartsWith(commands, "moveTo(7,");
        assertStartsWith(commands, "moveTo(5,");
        copy.dispose();
        original.dispose();
    }

    @Test
    public void samplesGradientPaintThroughItsContext() {
        ModelCanvasPeer peer = new ModelCanvasPeer();
        CanvasGraphics2D graphics = new CanvasGraphics2D(peer);
        graphics.setPaint(new GradientPaint(0, 0, Color.RED, 20, 0, Color.BLUE));
        graphics.fillRect(0, 0, 20, 5);
        assertContains(peer.getCommands(), "drawImage(20,5,0,0,20,5,0,0,20,5)");
    }

    @Test
    public void mapsClearCompositeToDestinationOutAtFullAlpha() {
        ModelCanvasPeer peer = new ModelCanvasPeer();
        CanvasGraphics2D graphics = new CanvasGraphics2D(peer);
        graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.25f));
        graphics.fillRect(0, 0, 5, 5);
        assertContains(peer.getCommands(), "globalAlpha(1)");
        assertContains(peer.getCommands(), "composite(destination-out)");
    }

    @Test
    public void skipsDestinationOnlyDrawing() {
        ModelCanvasPeer peer = new ModelCanvasPeer();
        CanvasGraphics2D graphics = new CanvasGraphics2D(peer);
        graphics.setComposite(AlphaComposite.Dst);
        graphics.fillRect(0, 0, 5, 5);
        if (peer.getCommands().length != 0) {
            throw new AssertionError("Destination-only drawing must not emit canvas commands");
        }
    }

    @Test
    public void recordsImageSourceDestinationAndTransformCommands() {
        ModelCanvasPeer peer = new ModelCanvasPeer();
        CanvasGraphics2D graphics = new CanvasGraphics2D(peer);
        assertTrue(graphics.getDeviceConfiguration() != null);
        BufferedImage image = new BufferedImage(2, 1, BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 0, 0xffff0000);
        image.setRGB(1, 0, 0xff0000ff);
        graphics.translate(3, 4);
        assertTrue(graphics.drawImage(image, 8, 0, 4, 2, 0, 0, 2, 1, null));
        assertContains(peer.getCommands(), "transform(1,0,0,1,3,4)");
        assertContains(peer.getCommands(), "drawImage(2,1,0,0,2,1,8,0,4,2)");
    }

    private static void assertContains(String[] values, String expected) {
        for (String value : values) {
            if (expected.equals(value)) {
                return;
            }
        }
        throw new AssertionError("Missing command: " + expected + " in " + String.join(";", values));
    }

    private static void assertStartsWith(String[] values, String expectedPrefix) {
        for (String value : values) {
            if (value.startsWith(expectedPrefix)) {
                return;
            }
        }
        throw new AssertionError("Missing command prefix: " + expectedPrefix);
    }

    private static void assertTrue(boolean value) {
        if (!value) {
            throw new AssertionError("Expected true");
        }
    }
}
