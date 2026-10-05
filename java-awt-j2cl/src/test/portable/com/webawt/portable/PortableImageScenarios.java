package com.webawt.portable;

import java.awt.Point;
import java.awt.image.BandedSampleModel;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferDouble;
import java.awt.image.DataBufferFloat;
import java.awt.image.DataBufferUShort;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.WritableRenderedImage;

public final class PortableImageScenarios {
    private PortableImageScenarios() {
    }

    public static void main(String[] arguments) {
        runAll();
    }

    public static void runAll() {
        readsUnsignedAndOffsetBuffers();
        isolatesBanksAndOffsets();
        preservesFloatingPointSamples();
        readsAndWritesInterleavedPixels();
        readsAndWritesBandedPixels();
        packsSingleBitPixelsMostSignificantBitFirst();
        readsAndWritesMaskedPixels();
        sharesDataWithTranslatedRasterChildren();
        clipsRasterCopiesToDestinationBounds();
        convertsPackedAndIndexedColors();
        exposesRenderedImageContracts();
    }

    public static void readsUnsignedAndOffsetBuffers() {
        DataBufferByte bytes = new DataBufferByte(new byte[] {0, -1, 3, 4}, 2, 1);
        assertEquals(DataBuffer.TYPE_BYTE, bytes.getDataType());
        assertEquals(2, bytes.getSize());
        assertEquals(255, bytes.getElem(0));
        bytes.setElem(1, 200);
        assertEquals(200, bytes.getElem(1));
        DataBufferUShort shorts = new DataBufferUShort(new short[] {0, -1}, 1, 1);
        assertEquals(65535, shorts.getElem(0));
        assertEquals(32, DataBuffer.getDataTypeSize(DataBuffer.TYPE_INT));
    }

    public static void isolatesBanksAndOffsets() {
        byte[][] values = {{9, 10, 11}, {20, 21, 22, 23}};
        DataBufferByte buffer = new DataBufferByte(values, 2, new int[] {1, 2});
        assertEquals(10, buffer.getElem(0, 0));
        assertEquals(22, buffer.getElem(1, 0));
        buffer.setElem(1, 1, 99);
        assertEquals(99, values[1][3] & 255);
        int[] offsets = buffer.getOffsets();
        offsets[0] = 0;
        assertEquals(1, buffer.getOffsets()[0]);
    }

    public static void preservesFloatingPointSamples() {
        DataBufferFloat floats = new DataBufferFloat(2);
        floats.setElemFloat(0, 1.25f);
        assertClose(1.25, floats.getElemFloat(0));
        DataBufferDouble doubles = new DataBufferDouble(2);
        doubles.setElemDouble(1, Math.PI);
        assertClose(Math.PI, doubles.getElemDouble(1));
        assertEquals(3, doubles.getElem(1));
    }

    public static void readsAndWritesInterleavedPixels() {
        PixelInterleavedSampleModel model = new PixelInterleavedSampleModel(DataBuffer.TYPE_BYTE, 2, 2, 3, 6, new int[] {0, 1, 2});
        DataBuffer buffer = model.createDataBuffer();
        model.setPixel(1, 1, new int[] {17, 34, 51}, buffer);
        assertArrayEquals(new int[] {17, 34, 51}, model.getPixel(1, 1, (int[]) null, buffer));
        assertEquals(34, model.getSample(1, 1, 1, buffer));
        SampleModel subset = model.createSubsetSampleModel(new int[] {2, 0});
        assertArrayEquals(new int[] {51, 17}, subset.getPixel(1, 1, (int[]) null, buffer));
    }

    public static void readsAndWritesBandedPixels() {
        BandedSampleModel model = new BandedSampleModel(DataBuffer.TYPE_BYTE, 2, 2, 2);
        DataBuffer buffer = model.createDataBuffer();
        model.setPixel(0, 1, new int[] {91, 173}, buffer);
        assertArrayEquals(new int[] {91, 173}, model.getPixel(0, 1, (int[]) null, buffer));
        assertEquals(2, buffer.getNumBanks());
    }

    public static void packsSingleBitPixelsMostSignificantBitFirst() {
        MultiPixelPackedSampleModel model = new MultiPixelPackedSampleModel(DataBuffer.TYPE_BYTE, 8, 1, 1);
        DataBuffer buffer = model.createDataBuffer();
        model.setSample(3, 0, 0, 1, buffer);
        assertEquals(1, model.getSample(3, 0, 0, buffer));
        assertEquals(16, buffer.getElem(0));
        assertEquals(0, model.getSample(4, 0, 0, buffer));
    }

    public static void readsAndWritesMaskedPixels() {
        int[] masks = {0x00ff0000, 0x0000ff00, 0x000000ff};
        SinglePixelPackedSampleModel model = new SinglePixelPackedSampleModel(DataBuffer.TYPE_INT, 2, 1, masks);
        DataBuffer buffer = model.createDataBuffer();
        model.setPixel(1, 0, new int[] {18, 52, 86}, buffer);
        assertArrayEquals(new int[] {18, 52, 86}, model.getPixel(1, 0, (int[]) null, buffer));
        assertEquals(0x00123456, buffer.getElem(1));
    }

    public static void sharesDataWithTranslatedRasterChildren() {
        WritableRaster raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, 3, 2, 3, null);
        raster.setPixel(1, 1, new int[] {7, 11, 13});
        WritableRaster child = raster.createWritableChild(1, 1, 1, 1, 10, 20, new int[] {2, 0});
        assertArrayEquals(new int[] {13, 7}, child.getPixel(10, 20, (int[]) null));
        child.setPixel(10, 20, new int[] {23, 29});
        assertArrayEquals(new int[] {29, 11, 23}, raster.getPixel(1, 1, (int[]) null));
        assertSame(raster, child.getParent());
    }

    public static void clipsRasterCopiesToDestinationBounds() {
        WritableRaster source = Raster.createBandedRaster(DataBuffer.TYPE_BYTE, 3, 2, 1, null);
        source.setPixels(0, 0, 3, 2, new int[] {1, 2, 3, 4, 5, 6});
        WritableRaster target = Raster.createBandedRaster(DataBuffer.TYPE_BYTE, 2, 2, 1, null);
        target.setRect(-1, 0, source);
        assertArrayEquals(new int[] {2, 3, 5, 6}, target.getPixels(0, 0, 2, 2, (int[]) null));
    }

    public static void convertsPackedAndIndexedColors() {
        DirectColorModel rgb565 = new DirectColorModel(16, 0xf800, 0x07e0, 0x001f);
        assertEquals(0xffff0000, rgb565.getRGB(0xf800));
        assertEquals(0xff00ff00, rgb565.getRGB(0x07e0));
        byte[] red = {0, -1};
        byte[] green = {0, 0};
        byte[] blue = {-1, 0};
        IndexColorModel indexed = new IndexColorModel(1, 2, red, green, blue);
        assertEquals(0xff0000ff, indexed.getRGB(0));
        assertEquals(0xffff0000, indexed.getRGB(1));
        WritableRaster raster = indexed.createCompatibleWritableRaster(2, 1);
        raster.setSample(1, 0, 0, 1);
        assertEquals(1, raster.getSample(1, 0, 0));
    }

    public static void exposesRenderedImageContracts() {
        BufferedImage image = new BufferedImage(3, 2, BufferedImage.TYPE_INT_ARGB);
        assertTrue(image instanceof RenderedImage);
        assertTrue(image instanceof WritableRenderedImage);
        assertEquals(1, image.getNumXTiles());
        assertEquals(1, image.getNumYTiles());
        assertSame(null, image.getSources());
        Point tileIndex = image.getWritableTileIndices()[0];
        assertEquals(0, tileIndex.x);
        assertEquals(0, tileIndex.y);
        image.releaseWritableTile(0, 0);
    }

    private static void assertEquals(int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError("Expected " + expected + " but was " + actual);
        }
    }

    private static void assertClose(double expected, double actual) {
        if (Math.abs(expected - actual) > 0.000000001) {
            throw new AssertionError("Expected " + expected + " but was " + actual);
        }
    }

    private static void assertArrayEquals(int[] expected, int[] actual) {
        if (expected.length != actual.length) {
            throw new AssertionError("Expected length " + expected.length + " but was " + actual.length);
        }
        for (int index = 0; index < expected.length; index++) {
            assertEquals(expected[index], actual[index]);
        }
    }

    private static void assertSame(Object expected, Object actual) {
        if (expected != actual) {
            throw new AssertionError("Expected identical objects");
        }
    }

    private static void assertTrue(boolean value) {
        if (!value) {
            throw new AssertionError("Expected true");
        }
    }
}
