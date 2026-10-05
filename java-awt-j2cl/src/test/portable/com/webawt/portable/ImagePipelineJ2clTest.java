package com.webawt.portable;

import java.awt.image.ColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.MemoryImageSource;
import org.junit.Test;

public final class ImagePipelineJ2clTest {
    @Test
    public void deliversMemoryPixelsSynchronously() {
        RecordingConsumer consumer = new RecordingConsumer();
        int[] pixels = {0xff102030, 0xff405060};
        MemoryImageSource source = new MemoryImageSource(2, 1, pixels, 0, 2);
        source.startProduction(consumer);
        assertEquals(2, consumer.width);
        assertEquals(1, consumer.height);
        assertEquals(0xff102030, consumer.firstPixel);
        assertEquals(ImageConsumer.STATICIMAGEDONE, consumer.status);
    }

    private static void assertEquals(int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError("Expected " + expected + " but was " + actual);
        }
    }

    private static final class RecordingConsumer implements ImageConsumer {
        private int firstPixel;
        private int height;
        private int status;
        private int width;

        @Override
        public void setDimensions(int width, int height) {
            this.width = width;
            this.height = height;
        }

        @Override
        public void setColorModel(ColorModel model) {
        }

        @Override
        public void setHints(int hints) {
        }

        @Override
        public void setPixels(int x, int y, int width, int height, ColorModel model, byte[] pixels, int offset, int scanlineStride) {
            firstPixel = model.getRGB(pixels[offset] & 255);
        }

        @Override
        public void setPixels(int x, int y, int width, int height, ColorModel model, int[] pixels, int offset, int scanlineStride) {
            firstPixel = model.getRGB(pixels[offset]);
        }

        @Override
        public void imageComplete(int status) {
            this.status = status;
        }
    }
}
