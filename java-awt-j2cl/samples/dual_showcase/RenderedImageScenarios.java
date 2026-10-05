package org.webawt.samples.dualshowcase;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.RenderableImage;
import java.awt.image.renderable.RenderContext;
import java.util.Vector;

final class RenderedImageScenarios {
    private RenderedImageScenarios() {
    }

    static String paint(Graphics2D graphics, int offset) {
        BufferedImage source = tile();
        RenderedImage rendered = new OffsetImage(source, 2, 3);
        AffineTransform placement = AffineTransform.getTranslateInstance(offset + 20, 20);
        placement.scale(30, 30);
        placement.translate(-2, -3);
        graphics.drawRenderedImage(rendered, placement);
        int[] requests = new int[1];
        AffineTransform original = graphics.getTransform();
        AffineTransform expected = new AffineTransform(original);
        AffineTransform imageTransform = AffineTransform.getTranslateInstance(offset + 140, 20);
        imageTransform.scale(30, 30);
        expected.concatenate(imageTransform);
        graphics.drawRenderableImage(new RenderableImage() {
            public Vector<RenderableImage> getSources() { return null; }
            public Object getProperty(String name) { return java.awt.Image.UndefinedProperty; }
            public String[] getPropertyNames() { return null; }
            public boolean isDynamic() { return true; }
            public float getWidth() { return 2; }
            public float getHeight() { return 2; }
            public float getMinX() { return 0; }
            public float getMinY() { return 0; }
            public RenderedImage createScaledRendering(int width, int height, RenderingHints hints) { return source; }
            public RenderedImage createDefaultRendering() { return source; }
            public RenderedImage createRendering(RenderContext context) {
                requests[0]++;
                if (!context.getTransform().equals(expected)) { throw new AssertionError("Renderable request must include the graphics transform"); }
                Rectangle bounds = expected.createTransformedShape(new Rectangle(0, 0, 2, 2)).getBounds();
                BufferedImage result = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D painter = result.createGraphics();
                try { painter.drawImage(source, 0, 0, bounds.width, bounds.height, null); } finally { painter.dispose(); }
                return new OffsetImage(result, bounds.x, bounds.y);
            }
        }, imageTransform);
        if (requests[0] != 1 || !graphics.getTransform().equals(original)) { throw new AssertionError("Renderable request count / restored graphics transform"); }
        return "RenderedImage with nonzero origin; RenderableImage sees the combined transform exactly once";
    }

    static int[][] pixels(int offset) {
        return new int[][] {{offset + 30, 30, Color.BLUE.getRGB()}, {offset + 70, 30, Color.GREEN.getRGB()}, {offset + 150, 30, Color.BLUE.getRGB()}, {offset + 190, 30, Color.GREEN.getRGB()}, {offset + 150, 70, Color.YELLOW.getRGB()}};
    }

    private static BufferedImage tile() {
        BufferedImage tile = new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB);
        tile.setRGB(0, 0, Color.BLUE.getRGB());
        tile.setRGB(1, 0, Color.GREEN.getRGB());
        tile.setRGB(0, 1, Color.YELLOW.getRGB());
        tile.setRGB(1, 1, Color.WHITE.getRGB());
        return tile;
    }

    private static final class OffsetImage implements RenderedImage {
        private final BufferedImage image;
        private final int x;
        private final int y;
        OffsetImage(BufferedImage image, int x, int y) { this.image = image; this.x = x; this.y = y; }
        public Vector<RenderedImage> getSources() { return null; }
        public Object getProperty(String name) { return image.getProperty(name); }
        public String[] getPropertyNames() { return image.getPropertyNames(); }
        public ColorModel getColorModel() { return image.getColorModel(); }
        public SampleModel getSampleModel() { return image.getSampleModel(); }
        public int getWidth() { return image.getWidth(); }
        public int getHeight() { return image.getHeight(); }
        public int getMinX() { return x; }
        public int getMinY() { return y; }
        public int getNumXTiles() { return 1; }
        public int getNumYTiles() { return 1; }
        public int getMinTileX() { return 0; }
        public int getMinTileY() { return 0; }
        public int getTileWidth() { return image.getWidth(); }
        public int getTileHeight() { return image.getHeight(); }
        public int getTileGridXOffset() { return x; }
        public int getTileGridYOffset() { return y; }
        public Raster getTile(int tileX, int tileY) { return getData(); }
        public Raster getData() { return image.getRaster().createTranslatedChild(x, y); }
        public Raster getData(Rectangle bounds) { return getData().createChild(bounds.x, bounds.y, bounds.width, bounds.height, bounds.x, bounds.y, null); }
        public WritableRaster copyData(WritableRaster target) {
            if (target == null) { target = image.getRaster().createCompatibleWritableRaster(x, y, getWidth(), getHeight()); }
            target.setRect(getData());
            return target;
        }
    }
}
