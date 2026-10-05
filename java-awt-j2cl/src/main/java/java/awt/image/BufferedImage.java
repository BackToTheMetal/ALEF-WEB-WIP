package java.awt.image;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.util.Vector;

public class BufferedImage extends Image implements WritableRenderedImage, Transparency {
    public static final int TYPE_CUSTOM = 0;
    public static final int TYPE_INT_RGB = 1;
    public static final int TYPE_INT_ARGB = 2;
    public static final int TYPE_INT_ARGB_PRE = 3;
    public static final int TYPE_INT_BGR = 4;
    public static final int TYPE_3BYTE_BGR = 5;
    public static final int TYPE_4BYTE_ABGR = 6;
    public static final int TYPE_4BYTE_ABGR_PRE = 7;
    public static final int TYPE_USHORT_565_RGB = 8;
    public static final int TYPE_USHORT_555_RGB = 9;
    public static final int TYPE_BYTE_GRAY = 10;
    public static final int TYPE_USHORT_GRAY = 11;
    public static final int TYPE_BYTE_BINARY = 12;
    public static final int TYPE_BYTE_INDEXED = 13;
    private ColorModel colorModel;
    private final WritableRaster raster;
    private final int imageType;

    public BufferedImage(int width, int height, int imageType) {
        validateDimensions(width, height);
        ImageLayout layout = createLayout(width, height, imageType, null);
        colorModel = layout.colorModel;
        raster = layout.raster;
        this.imageType = imageType;
    }

    public BufferedImage(int width, int height, int imageType, IndexColorModel colorModel) {
        validateDimensions(width, height);
        if (colorModel == null) {
            throw new NullPointerException("Color model cannot be null");
        }
        if (imageType != TYPE_BYTE_BINARY && imageType != TYPE_BYTE_INDEXED) {
            throw new IllegalArgumentException("Indexed color models require a byte binary or byte indexed image");
        }
        ImageLayout layout = createLayout(width, height, imageType, colorModel);
        this.colorModel = layout.colorModel;
        raster = layout.raster;
        this.imageType = imageType;
    }

    BufferedImage(ColorModel colorModel, WritableRaster raster, int imageType) {
        this.colorModel = colorModel;
        this.raster = raster;
        this.imageType = imageType;
    }

    public int getType() {
        return imageType;
    }

    public ColorModel getColorModel() {
        return colorModel;
    }

    public WritableRaster getRaster() {
        return raster;
    }

    public WritableRaster getAlphaRaster() {
        return colorModel.getAlphaRaster(raster);
    }

    public int getRGB(int x, int y) {
        return colorModel.getRGB(raster.getDataElements(x, y, null));
    }

    public int[] getRGB(int startX, int startY, int width, int height, int[] rgbArray, int offset, int scansize) {
        int[] output = rgbArray == null ? new int[offset + height * scansize] : rgbArray;
        for (int row = 0; row < height; row++) {
            int outputIndex = offset + row * scansize;
            for (int column = 0; column < width; column++) {
                output[outputIndex + column] = getRGB(startX + column, startY + row);
            }
        }
        return output;
    }

    public void setRGB(int x, int y, int rgb) {
        raster.setDataElements(x, y, colorModel.getDataElements(rgb, null));
    }

    public void setRGB(int startX, int startY, int width, int height, int[] rgbArray, int offset, int scansize) {
        if (rgbArray == null) {
            throw new NullPointerException("RGB array cannot be null");
        }
        for (int row = 0; row < height; row++) {
            int inputIndex = offset + row * scansize;
            for (int column = 0; column < width; column++) {
                setRGB(startX + column, startY + row, rgbArray[inputIndex + column]);
            }
        }
    }

    public int getWidth() {
        return raster.getWidth();
    }

    public int getHeight() {
        return raster.getHeight();
    }

    @Override
    public int getWidth(ImageObserver observer) {
        return getWidth();
    }

    @Override
    public int getHeight(ImageObserver observer) {
        return getHeight();
    }

    @Override
    public ImageProducer getSource() {
        int width = getWidth();
        int height = getHeight();
        return new MemoryImageSource(width, height, getRGB(0, 0, width, height, null, 0, width), 0, width);
    }

    @Override
    public Object getProperty(String name, ImageObserver observer) {
        return getProperty(name);
    }

    public Object getProperty(String name) {
        if (name == null) {
            throw new NullPointerException("Property name cannot be null");
        }
        return UndefinedProperty;
    }

    @Override
    public Graphics getGraphics() {
        return createGraphics();
    }

    public Graphics2D createGraphics() {
        return new BufferedImageGraphics2D(this);
    }

    public BufferedImage getSubimage(int x, int y, int width, int height) {
        WritableRaster child = raster.createWritableChild(x, y, width, height, 0, 0, null);
        return new BufferedImage(colorModel, child, imageType);
    }

    public boolean isAlphaPremultiplied() {
        return colorModel.isAlphaPremultiplied();
    }

    public void coerceData(boolean isAlphaPremultiplied) {
        colorModel = colorModel.coerceData(raster, isAlphaPremultiplied);
    }

    @Override
    public String toString() {
        return "BufferedImage@" + Integer.toHexString(hashCode()) + ": type = " + imageType + " " + colorModel + " " + raster;
    }

    @Override
    public Vector<RenderedImage> getSources() {
        return null;
    }

    public String[] getPropertyNames() {
        return null;
    }

    public int getMinX() {
        return 0;
    }

    public int getMinY() {
        return 0;
    }

    public SampleModel getSampleModel() {
        return raster.getSampleModel();
    }

    public int getNumXTiles() {
        return 1;
    }

    public int getNumYTiles() {
        return 1;
    }

    public int getMinTileX() {
        return 0;
    }

    public int getMinTileY() {
        return 0;
    }

    public int getTileWidth() {
        return raster.getWidth();
    }

    public int getTileHeight() {
        return raster.getHeight();
    }

    public int getTileGridXOffset() {
        return 0;
    }

    public int getTileGridYOffset() {
        return 0;
    }

    public Raster getTile(int tileX, int tileY) {
        checkTile(tileX, tileY);
        return raster;
    }

    public Raster getData() {
        return copyData(null);
    }

    public Raster getData(Rectangle rectangle) {
        if (rectangle == null) {
            throw new NullPointerException("Rectangle cannot be null");
        }
        WritableRaster output = raster.createCompatibleWritableRaster(rectangle);
        output.setRect(raster);
        return output;
    }

    public WritableRaster copyData(WritableRaster output) {
        WritableRaster result = output == null ? raster.createCompatibleWritableRaster() : output;
        result.setRect(raster);
        return result;
    }

    public void setData(Raster source) {
        if (source == null) {
            throw new NullPointerException("Source raster cannot be null");
        }
        raster.setRect(source);
    }

    @Override
    public void addTileObserver(TileObserver observer) {
    }

    @Override
    public void removeTileObserver(TileObserver observer) {
    }

    public boolean isTileWritable(int tileX, int tileY) {
        checkTile(tileX, tileY);
        return true;
    }

    public Point[] getWritableTileIndices() {
        return new Point[] {new Point(0, 0)};
    }

    public boolean hasTileWriters() {
        return true;
    }

    public WritableRaster getWritableTile(int tileX, int tileY) {
        checkTile(tileX, tileY);
        return raster;
    }

    public void releaseWritableTile(int tileX, int tileY) {
        checkTile(tileX, tileY);
    }

    @Override
    public int getTransparency() {
        return colorModel.getTransparency();
    }

    private static ImageLayout createLayout(int width, int height, int imageType, IndexColorModel indexedModel) {
        ColorSpace standardRgb = ColorSpace.getInstance(ColorSpace.CS_sRGB);
        switch (imageType) {
            case TYPE_INT_RGB:
                return packed(new DirectColorModel(24, 0x00ff0000, 0x0000ff00, 0x000000ff), width, height);
            case TYPE_INT_ARGB:
                return packed(ColorModel.getRGBdefault(), width, height);
            case TYPE_INT_ARGB_PRE:
                return packed(new DirectColorModel(standardRgb, 32, 0x00ff0000, 0x0000ff00, 0x000000ff, 0xff000000, true, DataBuffer.TYPE_INT), width, height);
            case TYPE_INT_BGR:
                return packed(new DirectColorModel(24, 0x000000ff, 0x0000ff00, 0x00ff0000), width, height);
            case TYPE_3BYTE_BGR:
                return interleaved(new ComponentColorModel(standardRgb, new int[] {8, 8, 8}, false, false, OPAQUE, DataBuffer.TYPE_BYTE), width, height, new int[] {2, 1, 0});
            case TYPE_4BYTE_ABGR:
                return interleaved(new ComponentColorModel(standardRgb, new int[] {8, 8, 8, 8}, true, false, TRANSLUCENT, DataBuffer.TYPE_BYTE), width, height, new int[] {3, 2, 1, 0});
            case TYPE_4BYTE_ABGR_PRE:
                return interleaved(new ComponentColorModel(standardRgb, new int[] {8, 8, 8, 8}, true, true, TRANSLUCENT, DataBuffer.TYPE_BYTE), width, height, new int[] {3, 2, 1, 0});
            case TYPE_USHORT_565_RGB:
                return packed(new DirectColorModel(16, 0xf800, 0x07e0, 0x001f), width, height);
            case TYPE_USHORT_555_RGB:
                return packed(new DirectColorModel(15, 0x7c00, 0x03e0, 0x001f), width, height);
            case TYPE_BYTE_GRAY:
                return interleaved(new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_GRAY), new int[] {8}, false, false, OPAQUE, DataBuffer.TYPE_BYTE), width, height, new int[] {0});
            case TYPE_USHORT_GRAY:
                return interleaved(new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_GRAY), new int[] {16}, false, false, OPAQUE, DataBuffer.TYPE_USHORT), width, height, new int[] {0});
            case TYPE_BYTE_BINARY:
                return indexed(indexedModel == null ? binaryColorModel() : indexedModel, width, height, true);
            case TYPE_BYTE_INDEXED:
                return indexed(indexedModel == null ? indexedColorModel() : indexedModel, width, height, false);
            default:
                throw new IllegalArgumentException("Unknown image type: " + imageType);
        }
    }

    private static ImageLayout packed(ColorModel model, int width, int height) {
        return new ImageLayout(model, model.createCompatibleWritableRaster(width, height));
    }

    private static ImageLayout interleaved(ColorModel model, int width, int height, int[] bandOffsets) {
        int bands = bandOffsets.length;
        SampleModel sampleModel = new PixelInterleavedSampleModel(model.getTransferType(), width, height, bands, width * bands, bandOffsets);
        return new ImageLayout(model, Raster.createWritableRaster(sampleModel, null));
    }

    private static ImageLayout indexed(IndexColorModel model, int width, int height, boolean packed) {
        if (model.getTransferType() != DataBuffer.TYPE_BYTE) {
            throw new IllegalArgumentException("Byte images require a byte color model");
        }
        SampleModel sampleModel;
        if (packed) {
            int bits = model.getMapSize() <= 2 ? 1 : model.getMapSize() <= 4 ? 2 : model.getMapSize() <= 16 ? 4 : 0;
            if (bits == 0) {
                throw new IllegalArgumentException("Binary images support at most 16 colors");
            }
            sampleModel = new MultiPixelPackedSampleModel(DataBuffer.TYPE_BYTE, width, height, bits);
        } else {
            sampleModel = new PixelInterleavedSampleModel(DataBuffer.TYPE_BYTE, width, height, 1, width, new int[] {0});
        }
        return new ImageLayout(model, Raster.createWritableRaster(sampleModel, null));
    }

    private static IndexColorModel binaryColorModel() {
        return new IndexColorModel(1, 2, new byte[] {0, (byte) 255}, new byte[] {0, (byte) 255}, new byte[] {0, (byte) 255});
    }

    private static IndexColorModel indexedColorModel() {
        int[] colors = new int[256];
        int index = 0;
        for (int red = 0; red < 6; red++) {
            for (int green = 0; green < 6; green++) {
                for (int blue = 0; blue < 6; blue++) {
                    colors[index++] = 0xff000000 | red * 51 << 16 | green * 51 << 8 | blue * 51;
                }
            }
        }
        while (index < colors.length) {
            int gray = 18 + (index - 216) * 6;
            colors[index++] = 0xff000000 | gray << 16 | gray << 8 | gray;
        }
        return new IndexColorModel(8, colors.length, colors, 0, false, -1, DataBuffer.TYPE_BYTE);
    }

    private static void validateDimensions(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Width and height must be greater than zero");
        }
    }

    private static void checkTile(int tileX, int tileY) {
        if (tileX != 0 || tileY != 0) {
            throw new ArrayIndexOutOfBoundsException("BufferedImages only have one tile with index 0,0");
        }
    }

    private static final class ImageLayout {
        private final ColorModel colorModel;
        private final WritableRaster raster;

        private ImageLayout(ColorModel colorModel, WritableRaster raster) {
            this.colorModel = colorModel;
            this.raster = raster;
        }
    }
}
