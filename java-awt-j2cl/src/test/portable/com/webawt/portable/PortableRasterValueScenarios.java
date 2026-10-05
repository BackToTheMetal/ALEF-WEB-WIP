package com.webawt.portable;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.MediaTracker;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.AreaAveragingScaleFilter;
import java.awt.image.BandCombineOp;
import java.awt.image.BaseMultiResolutionImage;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageFilter;
import java.awt.image.ByteLookupTable;
import java.awt.image.ConvolveOp;
import java.awt.image.CropImageFilter;
import java.awt.image.DataBuffer;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageConsumer;
import java.awt.image.ImagingOpException;
import java.awt.image.IndexColorModel;
import java.awt.image.Kernel;
import java.awt.image.LookupOp;
import java.awt.image.ImageProducer;
import java.awt.image.PixelGrabber;
import java.awt.image.Raster;
import java.awt.image.RescaleOp;
import java.awt.image.ReplicateScaleFilter;
import java.awt.image.RenderedImage;
import java.awt.image.ShortLookupTable;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ContextualRenderedImageFactory;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderableImage;
import java.awt.image.renderable.RenderableImageOp;
import java.awt.image.renderable.RenderableImageProducer;
import java.awt.image.renderable.RenderContext;
import java.util.Vector;

public final class PortableRasterValueScenarios {
    private PortableRasterValueScenarios() {
    }

    public static void main(String[] arguments) {
        copiesAndReportsKernelData();
        appliesByteLookupTables();
        appliesShortLookupTables();
        rejectsInvalidLookupDefinitionsAndIndexes();
        preservesImagingExceptionMessages();
        filtersRasterSamplesWithLookupAndRescaleOperations();
        filtersBufferedImageColorsWithoutChangingAlpha();
        combinesRasterBandsWithLinearMatrices();
        convolvesRasterInteriorsAndAppliesEdgePolicies();
        transformsRasterBoundsAndPixels();
        selectsAndDelegatesMultiResolutionImages();
        cropsAndReplicatesProducerPixels();
        averagesScaledProducerPixels();
        filtersProducerFramesWithBufferedImageOperations();
        grabsImagePixelsAndReportsCompletion();
        managesRenderableParametersAndContexts();
        evaluatesRenderableGraphsAndProducesPixels();
        tilesTexturePaintAcrossDeviceCoordinates();
        tracksCompletedImagesByIdentifier();
        expandsIndexedRastersToDiscreteImages();
        drawsImagesAcrossGraphicsOverloads();
    }

    public static void copiesAndReportsKernelData() {
        float[] source = {1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f};
        Kernel kernel = new Kernel(3, 2, source);
        source[0] = 99.0f;
        assertEquals(3, kernel.getWidth());
        assertEquals(2, kernel.getHeight());
        assertEquals(1, kernel.getXOrigin());
        assertEquals(0, kernel.getYOrigin());
        float[] destination = {-1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 42.0f};
        assertSame(destination, kernel.getKernelData(destination));
        assertEquals(1.0f, destination[0]);
        assertEquals(6.0f, destination[5]);
        assertEquals(42.0f, destination[6]);
        Kernel clone = (Kernel) kernel.clone();
        assertNotSame(kernel, clone);
        assertEquals(4.0f, clone.getKernelData(null)[3]);
        assertThrowsIllegalArgument(() -> new Kernel(3, 2, new float[5]));
        assertThrowsIllegalArgument(() -> kernel.getKernelData(new float[5]));
    }

    public static void appliesByteLookupTables() {
        byte[] shared = {10, 20, (byte) 255};
        ByteLookupTable single = new ByteLookupTable(1, shared);
        assertEquals(1, single.getOffset());
        assertEquals(1, single.getNumComponents());
        assertArrayEquals(new int[] {10, 20, -1}, single.lookupPixel(new int[] {1, 2, 3}, null));
        shared[1] = 30;
        assertArrayEquals(new byte[] {30}, single.lookupPixel(new byte[] {2}, null));
        byte[][] bands = {{1, 2}, {3, 4}, {5, 6}};
        ByteLookupTable multiple = new ByteLookupTable(0, bands);
        assertEquals(3, multiple.getNumComponents());
        assertNotSame(bands, multiple.getTable());
        assertSame(bands[1], multiple.getTable()[1]);
        int[] inPlace = {1, 0, 1};
        assertSame(inPlace, multiple.lookupPixel(inPlace, inPlace));
        assertArrayEquals(new int[] {2, 3, 6}, inPlace);
        byte[] unsignedTable = new byte[256];
        unsignedTable[255] = 77;
        assertArrayEquals(new byte[] {77}, new ByteLookupTable(0, unsignedTable).lookupPixel(new byte[] {(byte) 255}, null));
    }

    public static void appliesShortLookupTables() {
        short[] shared = {100, 200, (short) 65535};
        ShortLookupTable single = new ShortLookupTable(2, shared);
        assertEquals(2, single.getOffset());
        assertEquals(1, single.getNumComponents());
        assertArrayEquals(new int[] {100, 200, -1}, single.lookupPixel(new int[] {2, 3, 4}, null));
        shared[0] = 150;
        assertArrayEquals(new short[] {150}, single.lookupPixel(new short[] {2}, null));
        short[][] bands = {{7, 8}, {9, 10}};
        ShortLookupTable multiple = new ShortLookupTable(0, bands);
        assertNotSame(bands, multiple.getTable());
        assertSame(bands[0], multiple.getTable()[0]);
        assertArrayEquals(new int[] {8, 9}, multiple.lookupPixel(new int[] {1, 0}, null));
    }

    public static void rejectsInvalidLookupDefinitionsAndIndexes() {
        assertThrowsIllegalArgument(() -> new ByteLookupTable(-1, new byte[] {1}));
        assertThrowsIllegalArgument(() -> new ByteLookupTable(0, new byte[0]));
        ByteLookupTable table = new ByteLookupTable(2, new byte[] {1, 2});
        assertThrowsArrayBounds(() -> table.lookupPixel(new int[] {1}, null));
        assertThrowsArrayBounds(() -> table.lookupPixel(new int[] {2, 3}, new int[1]));
        ShortLookupTable multiBand = new ShortLookupTable(0, new short[][] {{1}, {2}});
        assertThrowsArrayBounds(() -> multiBand.lookupPixel(new int[] {0, 0, 0}, null));
    }

    public static void preservesImagingExceptionMessages() {
        ImagingOpException exception = new ImagingOpException("cannot filter");
        assertEquals("cannot filter", exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }

    public static void filtersRasterSamplesWithLookupAndRescaleOperations() {
        WritableRaster source = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, 2, 1, 3, new Point(4, 5));
        source.setPixels(4, 5, 2, 1, new int[] {10, 20, 30, 250, 1, 100});
        byte[] inversion = new byte[256];
        for (int index = 0; index < inversion.length; index++) {
            inversion[index] = (byte) (255 - index);
        }
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
        LookupOp lookup = new LookupOp(new ByteLookupTable(0, inversion), hints);
        WritableRaster lookedUp = lookup.filter(source, null);
        assertArrayEquals(new int[] {245, 235, 225, 5, 254, 155}, lookedUp.getPixels(0, 0, 2, 1, (int[]) null));
        Rectangle2D bounds = lookup.getBounds2D(source);
        assertEquals(4, (int) bounds.getX());
        assertEquals(5, (int) bounds.getY());
        assertSame(hints, lookup.getRenderingHints());
        Point destinationPoint = new Point();
        assertSame(destinationPoint, lookup.getPoint2D(new Point(7, 8), destinationPoint));
        assertEquals(7, destinationPoint.x);
        assertEquals(8, destinationPoint.y);
        RescaleOp rescale = new RescaleOp(new float[] {2.0f, 1.0f, 0.5f}, new float[] {1.0f, -30.0f, 10.0f}, hints);
        WritableRaster rescaled = rescale.filter(source, null);
        assertArrayEquals(new int[] {21, 0, 25, 255, 0, 60}, rescaled.getPixels(0, 0, 2, 1, (int[]) null));
        assertEquals(3, rescale.getNumFactors());
        float[] factors = {-1.0f, -1.0f, -1.0f, 42.0f};
        assertSame(factors, rescale.getScaleFactors(factors));
        assertEquals(42.0f, factors[3]);
        assertThrowsIllegalArgument(() -> lookup.filter(source, Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, 1, 1, 3, null)));
    }

    public static void filtersBufferedImageColorsWithoutChangingAlpha() {
        BufferedImage source = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        source.setRGB(0, 0, 0x80402010);
        byte[] inversion = new byte[256];
        for (int index = 0; index < inversion.length; index++) {
            inversion[index] = (byte) (255 - index);
        }
        BufferedImage lookedUp = new LookupOp(new ByteLookupTable(0, inversion), null).filter(source, null);
        assertEquals(0x80bfdfef, lookedUp.getRGB(0, 0));
        BufferedImage rescaled = new RescaleOp(2.0f, 1.0f, null).filter(source, null);
        assertEquals(0x80814121, rescaled.getRGB(0, 0));
        assertEquals(source.getWidth(), rescaled.getWidth());
        assertEquals(source.getHeight(), rescaled.getHeight());
    }

    public static void combinesRasterBandsWithLinearMatrices() {
        WritableRaster source = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, 2, 1, 3, null);
        source.setPixels(0, 0, 2, 1, new int[] {10, 20, 30, 1, 2, 3});
        float[][] matrix = {{1.0f, 0.0f, 0.0f, 5.0f}, {0.0f, -1.0f, 0.0f, 255.0f}, {0.0f, 0.0f, 0.5f, 0.0f}};
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
        BandCombineOp operation = new BandCombineOp(matrix, hints);
        matrix[0][0] = 99.0f;
        WritableRaster result = operation.filter(source, null);
        assertArrayEquals(new int[] {15, 235, 15, 6, 253, 1}, result.getPixels(0, 0, 2, 1, (int[]) null));
        float[][] copy = operation.getMatrix();
        copy[0][0] = 77.0f;
        assertEquals(1.0f, operation.getMatrix()[0][0]);
        assertSame(hints, operation.getRenderingHints());
        Rectangle2D bounds = operation.getBounds2D(source);
        assertEquals(2, (int) bounds.getWidth());
        assertEquals(1, (int) bounds.getHeight());
        Point2D point = operation.getPoint2D(new Point(3, 4), null);
        assertEquals(3, (int) point.getX());
        assertEquals(4, (int) point.getY());
        assertThrowsIllegalArgument(() -> new BandCombineOp(new float[][] {{1.0f, 0.0f}}, null).filter(source, null));
        assertThrowsIndexBounds(() -> new BandCombineOp(new float[][] {{1.0f, 2.0f}, {3.0f}}, null));
    }

    public static void convolvesRasterInteriorsAndAppliesEdgePolicies() {
        WritableRaster source = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, 4, 1, 1, null);
        source.setPixels(0, 0, 4, 1, new int[] {10, 20, 30, 40});
        Kernel kernel = new Kernel(3, 1, new float[] {1.0f, 2.0f, 3.0f});
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        ConvolveOp zeroEdges = new ConvolveOp(kernel, ConvolveOp.EDGE_ZERO_FILL, hints);
        assertArrayEquals(new int[] {0, 100, 160, 0}, zeroEdges.filter(source, null).getPixels(0, 0, 4, 1, (int[]) null));
        ConvolveOp copiedEdges = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        assertArrayEquals(new int[] {10, 100, 160, 40}, copiedEdges.filter(source, null).getPixels(0, 0, 4, 1, (int[]) null));
        assertEquals(ConvolveOp.EDGE_ZERO_FILL, zeroEdges.getEdgeCondition());
        assertSame(hints, zeroEdges.getRenderingHints());
        assertNotSame(kernel, zeroEdges.getKernel());
        assertEquals(2.0f, zeroEdges.getKernel().getKernelData(null)[1]);
        assertThrowsIllegalArgument(() -> zeroEdges.filter(source, source));
        assertThrowsImagingOperation(() -> zeroEdges.filter(source, Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, 4, 1, 2, null)));
    }

    public static void transformsRasterBoundsAndPixels() {
        WritableRaster source = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, 2, 1, 1, null);
        source.setPixels(0, 0, 2, 1, new int[] {10, 20});
        AffineTransform transform = AffineTransform.getTranslateInstance(2.0, 3.0);
        AffineTransformOp operation = new AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        transform.translate(100.0, 100.0);
        Rectangle2D bounds = operation.getBounds2D(source);
        assertEquals(2, (int) bounds.getX());
        assertEquals(3, (int) bounds.getY());
        assertEquals(2, (int) bounds.getWidth());
        WritableRaster translated = operation.createCompatibleDestRaster(source);
        assertEquals(2, translated.getMinX());
        assertEquals(3, translated.getMinY());
        WritableRaster result = new AffineTransformOp(new AffineTransform(), AffineTransformOp.TYPE_NEAREST_NEIGHBOR).filter(source, null);
        assertArrayEquals(new int[] {10, 20}, result.getPixels(0, 0, 2, 1, (int[]) null));
        assertEquals(AffineTransformOp.TYPE_NEAREST_NEIGHBOR, operation.getInterpolationType());
        assertEquals(2, (int) operation.getPoint2D(new Point(0, 0), null).getX());
        assertNotSame(operation.getTransform(), operation.getTransform());
        assertSame(RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR, operation.getRenderingHints().get(RenderingHints.KEY_INTERPOLATION));
        RenderingHints quality = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        assertEquals(AffineTransformOp.TYPE_BILINEAR, new AffineTransformOp(new AffineTransform(), quality).getInterpolationType());
        assertThrowsIllegalArgument(() -> new AffineTransformOp(new AffineTransform(), 99));
        assertThrowsImagingOperation(() -> new AffineTransformOp(AffineTransform.getScaleInstance(0.0, 1.0), null));
    }

    public static void selectsAndDelegatesMultiResolutionImages() {
        BufferedImage small = new BufferedImage(16, 12, BufferedImage.TYPE_INT_ARGB);
        BufferedImage medium = new BufferedImage(32, 24, BufferedImage.TYPE_INT_ARGB);
        BufferedImage large = new BufferedImage(64, 48, BufferedImage.TYPE_INT_ARGB);
        BaseMultiResolutionImage image = new BaseMultiResolutionImage(1, small, medium, large);
        assertEquals(32, image.getWidth(null));
        assertEquals(24, image.getHeight(null));
        assertSame(small, image.getResolutionVariant(10.0, 10.0));
        assertSame(medium, image.getResolutionVariant(20.0, 20.0));
        assertSame(large, image.getResolutionVariant(100.0, 100.0));
        assertEquals(3, image.getResolutionVariants().size());
        assertTrue(image.getSource() != null);
        assertSame(java.awt.Image.UndefinedProperty, image.getProperty("missing", null));
        assertThrowsUnsupported(() -> image.getResolutionVariants().add(small));
        assertThrowsUnsupported(image::getGraphics);
        assertThrowsIllegalArgument(() -> image.getResolutionVariant(Double.NaN, 1.0));
        assertThrowsIllegalArgument(() -> image.getResolutionVariant(1.0, Double.POSITIVE_INFINITY));
        assertThrowsIllegalArgument(() -> image.getResolutionVariant(0.0, 1.0));
        assertThrowsIllegalArgument(() -> new BaseMultiResolutionImage(new java.awt.Image[0]));
        assertThrowsNullPointer(() -> new BaseMultiResolutionImage(small, null));
        assertThrowsIndexBounds(() -> new BaseMultiResolutionImage(2, small));
    }

    public static void cropsAndReplicatesProducerPixels() {
        int[] pixels = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        FilteredImageSource croppedSource = new FilteredImageSource(new TestImageProducer(4, 3, pixels), new CropImageFilter(1, 1, 2, 2));
        RecordingConsumer cropped = new RecordingConsumer();
        croppedSource.startProduction(cropped);
        assertEquals(2, cropped.width);
        assertEquals(2, cropped.height);
        assertArrayEquals(new int[] {5, 6, 9, 10}, cropped.pixels);
        assertEquals(ImageConsumer.STATICIMAGEDONE, cropped.completionStatus);
        FilteredImageSource scaledSource = new FilteredImageSource(new TestImageProducer(2, 2, new int[] {1, 2, 3, 4}), new ReplicateScaleFilter(4, 4));
        RecordingConsumer scaled = new RecordingConsumer();
        scaledSource.startProduction(scaled);
        assertEquals(4, scaled.width);
        assertEquals(4, scaled.height);
        assertArrayEquals(new int[] {1, 1, 2, 2, 1, 1, 2, 2, 3, 3, 4, 4, 3, 3, 4, 4}, scaled.pixels);
        RecordingConsumer secondConsumer = new RecordingConsumer();
        scaledSource.startProduction(secondConsumer);
        assertArrayEquals(scaled.pixels, secondConsumer.pixels);
        FilteredImageSource proportionalSource = new FilteredImageSource(new TestImageProducer(4, 2, new int[8]), new ReplicateScaleFilter(-1, 2));
        RecordingConsumer proportional = new RecordingConsumer();
        proportionalSource.startProduction(proportional);
        assertEquals(4, proportional.width);
        assertEquals(2, proportional.height);
        assertThrowsIllegalArgument(() -> new ReplicateScaleFilter(0, 1));
    }

    public static void averagesScaledProducerPixels() {
        int[] pixels = {0xff000000, 0xffff0000, 0xff00ff00, 0xff0000ff};
        FilteredImageSource source = new FilteredImageSource(new TestImageProducer(2, 2, pixels), new AreaAveragingScaleFilter(1, 1));
        RecordingConsumer result = new RecordingConsumer();
        source.startProduction(result);
        assertEquals(1, result.width);
        assertEquals(1, result.height);
        assertEquals(0xff404040, result.pixels[0]);
    }

    public static void filtersProducerFramesWithBufferedImageOperations() {
        int[] pixels = {0xff102030, 0xff405060, 0xff708090, 0xffa0b0c0};
        BufferedImageFilter prototype = new BufferedImageFilter(new RescaleOp(1.0f, 16.0f, null));
        assertTrue(prototype.getBufferedImageOp() instanceof RescaleOp);
        FilteredImageSource source = new FilteredImageSource(new TestImageProducer(2, 2, pixels), prototype);
        RecordingConsumer first = new RecordingConsumer();
        source.startProduction(first);
        assertEquals(2, first.width);
        assertEquals(2, first.height);
        assertArrayEquals(new int[] {0xff203040, 0xff506070, 0xff8090a0, 0xffb0c0d0}, first.pixels);
        assertEquals(ImageConsumer.STATICIMAGEDONE, first.completionStatus);
        RecordingConsumer second = new RecordingConsumer();
        source.startProduction(second);
        assertArrayEquals(first.pixels, second.pixels);
        assertThrowsNullPointer(() -> new BufferedImageFilter(null));
    }

    public static void grabsImagePixelsAndReportsCompletion() {
        int[] source = {0xff010203, 0xff112233, 0xff445566, 0xff778899, 0xffaabbcc, 0xffddeeff};
        int[] destination = {7, 7, 7, 7, 7, 7};
        PixelGrabber subset = new PixelGrabber(new TestImageProducer(3, 2, source), 1, 0, 2, 2, destination, 1, 2);
        assertTrue(grab(subset));
        assertSame(destination, subset.getPixels());
        assertArrayEquals(new int[] {7, 0xff112233, 0xff445566, 0xffaabbcc, 0xffddeeff, 7}, destination);
        assertEquals(2, subset.getWidth());
        assertEquals(2, subset.getHeight());
        assertSame(java.awt.image.ColorModel.getRGBdefault(), subset.getColorModel());
        assertEquals(subset.getStatus(), subset.status());
        assertTrue((subset.getStatus() & java.awt.image.ImageObserver.ALLBITS) != 0);
        BufferedImage image = new BufferedImage(2, 1, BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 0, 0xff123456);
        image.setRGB(1, 0, 0xffabcdef);
        PixelGrabber allocated = new PixelGrabber(image, 0, 0, -1, -1, true);
        assertTrue(grab(allocated));
        assertEquals(2, allocated.getWidth());
        assertEquals(1, allocated.getHeight());
        assertArrayEquals(new int[] {0xff123456, 0xffabcdef}, (int[]) allocated.getPixels());
        PixelGrabber aborted = new PixelGrabber(new TestImageProducer(1, 1, new int[] {1}), 0, 0, 1, 1, new int[1], 0, 1);
        aborted.abortGrabbing();
        assertTrue((aborted.getStatus() & java.awt.image.ImageObserver.ABORT) != 0);
    }

    private static boolean grab(PixelGrabber grabber) {
        try {
            return grabber.grabPixels();
        } catch (InterruptedException exception) {
            throw new AssertionError(exception);
        }
    }

    public static void managesRenderableParametersAndContexts() {
        BufferedImage image = new BufferedImage(2, 1, BufferedImage.TYPE_INT_ARGB);
        ParameterBlock block = new ParameterBlock();
        assertSame(block, block.addSource(image));
        assertSame(image, block.getSource(0));
        assertSame(image, block.getRenderedSource(0));
        block.setSource("third", 2);
        assertEquals(3, block.getNumSources());
        assertSame(null, block.getSource(1));
        block.add((byte) 1).add('a').add((short) 2).add(3).add(4L).add(5.0f).add(6.0).add("value");
        assertEquals(8, block.getNumParameters());
        assertEquals(1, block.getByteParameter(0));
        assertEquals('a', block.getCharParameter(1));
        assertEquals(2, block.getShortParameter(2));
        assertEquals(3, block.getIntParameter(3));
        assertTrue(block.getLongParameter(4) == 4L);
        assertEquals(5.0f, block.getFloatParameter(5));
        assertTrue(block.getDoubleParameter(6) == 6.0);
        assertSame(String.class, block.getParamClasses()[7]);
        assertSame(byte.class, block.getParamClasses()[0]);
        ParameterBlock shallow = (ParameterBlock) block.shallowClone();
        shallow.add("shared");
        assertEquals(9, block.getNumParameters());
        ParameterBlock clone = (ParameterBlock) block.clone();
        clone.add("independent");
        assertEquals(9, block.getNumParameters());
        assertEquals(10, clone.getNumParameters());
        clone.removeParameters();
        clone.removeSources();
        assertEquals(0, clone.getNumParameters());
        assertEquals(0, clone.getNumSources());
        Rectangle2D area = new Rectangle2D.Double(1.0, 2.0, 3.0, 4.0);
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        AffineTransform original = AffineTransform.getTranslateInstance(2.0, 3.0);
        RenderContext context = new RenderContext(original, area, hints);
        original.translate(9.0, 9.0);
        assertEquals(2, (int) context.getTransform().getTranslateX());
        AffineTransform returned = context.getTransform();
        returned.translate(5.0, 5.0);
        assertEquals(2, (int) context.getTransform().getTranslateX());
        assertSame(area, context.getAreaOfInterest());
        assertSame(hints, context.getRenderingHints());
        context.concatenateTransform(AffineTransform.getScaleInstance(2.0, 2.0));
        context.preConcatenateTransform(AffineTransform.getTranslateInstance(1.0, 1.0));
        RenderContext contextClone = (RenderContext) context.clone();
        assertNotSame(context.getTransform(), contextClone.getTransform());
        assertSame(area, contextClone.getAreaOfInterest());
        assertSame(hints, contextClone.getRenderingHints());
    }

    public static void evaluatesRenderableGraphsAndProducesPixels() {
        BufferedImage image = new BufferedImage(2, 1, BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 0, 0xff123456);
        image.setRGB(1, 0, 0xffabcdef);
        StaticRenderableImage source = new StaticRenderableImage(image);
        RecordingFactory factory = new RecordingFactory();
        ParameterBlock parameters = new ParameterBlock().addSource(source).add(7);
        RenderableImageOp operation = new RenderableImageOp(factory, parameters);
        parameters.removeSources();
        assertEquals(1, operation.getSources().size());
        assertSame(source, operation.getSources().elementAt(0));
        assertEquals(10.0f, operation.getWidth());
        assertEquals(5.0f, operation.getHeight());
        assertEquals(2.0f, operation.getMinX());
        assertEquals(3.0f, operation.getMinY());
        assertEquals("property-value", (String) operation.getProperty("property"));
        assertEquals("property", operation.getPropertyNames()[0]);
        assertTrue(operation.isDynamic());
        assertSame(image, operation.createDefaultRendering());
        assertSame(image, operation.createScaledRendering(20, 10, null));
        assertSame(image, operation.createRendering(new RenderContext(new AffineTransform())));
        assertTrue(factory.mapCount >= 3);
        ParameterBlock replacement = new ParameterBlock().addSource(image);
        ParameterBlock previous = operation.setParameterBlock(replacement);
        replacement.removeSources();
        assertEquals(1, previous.getNumSources());
        assertEquals(1, operation.getParameterBlock().getNumSources());
        assertEquals(0, operation.getSources().size());
        RenderableImageProducer producer = new RenderableImageProducer(source, null);
        RecordingConsumer consumer = new RecordingConsumer();
        producer.addConsumer(consumer);
        assertTrue(producer.isConsumer(consumer));
        producer.run();
        assertEquals(2, consumer.width);
        assertEquals(1, consumer.height);
        assertArrayEquals(new int[] {0xff123456, 0xffabcdef}, consumer.pixels);
        assertEquals(ImageConsumer.STATICIMAGEDONE, consumer.completionStatus);
        producer.requestTopDownLeftRightResend(consumer);
        producer.removeConsumer(consumer);
        assertTrue(!producer.isConsumer(consumer));
    }

    public static void tilesTexturePaintAcrossDeviceCoordinates() {
        BufferedImage texture = new BufferedImage(2, 1, BufferedImage.TYPE_INT_ARGB);
        texture.setRGB(0, 0, 0xffff0000);
        texture.setRGB(1, 0, 0xff0000ff);
        TexturePaint paint = new TexturePaint(texture, new Rectangle2D.Double(10.0, 5.0, 4.0, 2.0));
        assertSame(texture, paint.getImage());
        assertEquals(10, (int) paint.getAnchorRect().getX());
        assertEquals(4, (int) paint.getAnchorRect().getWidth());
        assertEquals(java.awt.Transparency.TRANSLUCENT, paint.getTransparency());
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        java.awt.PaintContext context = paint.createContext(null, new java.awt.Rectangle(0, 0, 8, 2), null, new AffineTransform(), hints);
        Raster raster = context.getRaster(9, 5, 6, 1);
        int[] actual = new int[6];
        for (int index = 0; index < actual.length; index++) {
            actual[index] = context.getColorModel().getRGB(raster.getDataElements(index, 0, null));
        }
        assertArrayEquals(new int[] {0xff0000ff, 0xff0000ff, 0xffff0000, 0xffff0000, 0xff0000ff, 0xff0000ff}, actual);
        context.dispose();
    }

    public static void tracksCompletedImagesByIdentifier() {
        BufferedImage first = new BufferedImage(2, 1, BufferedImage.TYPE_INT_ARGB);
        BufferedImage second = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        MediaTracker tracker = new MediaTracker(new java.awt.Canvas());
        tracker.addImage(first, 1);
        tracker.addImage(second, 2, 3, 4);
        assertTrue(tracker.checkAll());
        assertTrue(tracker.checkAll(true));
        assertTrue(tracker.checkID(1));
        assertTrue(tracker.checkID(99));
        assertEquals(MediaTracker.COMPLETE, tracker.statusAll(false));
        assertEquals(MediaTracker.COMPLETE, tracker.statusID(2, false));
        assertEquals(0, tracker.statusID(99, false));
        assertTrue(!tracker.isErrorAny());
        assertTrue(!tracker.isErrorID(1));
        assertSame(null, tracker.getErrorsAny());
        assertSame(null, tracker.getErrorsID(2));
        assertTrue(waitForAll(tracker));
        assertTrue(waitForId(tracker, 1));
        tracker.removeImage(second, 2, 3, 4);
        assertEquals(MediaTracker.COMPLETE, tracker.statusAll(false));
        tracker.removeImage(first, 1);
        assertEquals(0, tracker.statusAll(false));
        tracker.addImage(first, 3);
        tracker.removeImage(first);
        assertEquals(0, tracker.statusAll(false));
    }

    private static boolean waitForAll(MediaTracker tracker) {
        try {
            return tracker.waitForAll(1);
        } catch (InterruptedException exception) {
            throw new AssertionError(exception);
        }
    }

    private static boolean waitForId(MediaTracker tracker, int identifier) {
        try {
            return tracker.waitForID(identifier, 1);
        } catch (InterruptedException exception) {
            throw new AssertionError(exception);
        }
    }

    public static void expandsIndexedRastersToDiscreteImages() {
        byte[] red = {0, (byte) 255};
        byte[] green = {0, 0};
        byte[] blue = {(byte) 255, 0};
        IndexColorModel model = new IndexColorModel(1, 2, red, green, blue);
        WritableRaster raster = model.createCompatibleWritableRaster(2, 1);
        raster.setSamples(0, 0, 2, 1, 0, new int[] {0, 1});
        BufferedImage rgb = model.convertToIntDiscrete(raster, false);
        assertArrayEquals(new int[] {0xff0000ff, 0xffff0000}, rgb.getRGB(0, 0, 2, 1, null, 0, 2));
        BufferedImage argb = model.convertToIntDiscrete(raster, true);
        assertArrayEquals(new int[] {0xff0000ff, 0xffff0000}, argb.getRGB(0, 0, 2, 1, null, 0, 2));
        assertThrowsIllegalArgument(() -> model.convertToIntDiscrete(Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, 2, 1, 2, null), false));
    }

    public static void drawsImagesAcrossGraphicsOverloads() {
        BufferedImage source = new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB);
        source.setRGB(0, 0, 0xffff0000);
        source.setRGB(1, 0, 0xff00ff00);
        source.setRGB(0, 1, 0xff0000ff);
        source.setRGB(1, 1, 0x00000000);
        BufferedImage direct = new BufferedImage(4, 4, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = direct.createGraphics();
        assertTrue(graphics.drawImage(source, 1, 1, null));
        assertEquals(0xffff0000, direct.getRGB(1, 1));
        assertEquals(0xff00ff00, direct.getRGB(2, 1));
        assertEquals(0xff0000ff, direct.getRGB(1, 2));
        assertTrue(source.getCapabilities(null) != null);
        assertSame(graphics.getFont(), graphics.getFontMetrics().getFont());
        assertTrue(graphics.toString().contains("font="));
        Color savedColor = graphics.getColor();
        graphics.draw3DRect(0, 0, 2, 2, true);
        assertSame(savedColor, graphics.getColor());
        graphics.dispose();
        BufferedImage scaled = new BufferedImage(4, 4, BufferedImage.TYPE_INT_ARGB);
        graphics = scaled.createGraphics();
        assertTrue(graphics.drawImage(source, 0, 0, 4, 4, null));
        assertEquals(0xffff0000, scaled.getRGB(0, 0));
        assertEquals(0xff00ff00, scaled.getRGB(3, 0));
        assertEquals(0xff0000ff, scaled.getRGB(0, 3));
        BufferedImage cropped = new BufferedImage(4, 2, BufferedImage.TYPE_INT_ARGB);
        Graphics2D croppedGraphics = cropped.createGraphics();
        assertTrue(croppedGraphics.drawImage(source, 0, 0, 2, 2, 1, 0, 2, 2, Color.YELLOW, null));
        assertEquals(0xff00ff00, cropped.getRGB(0, 0));
        assertEquals(0xffffff00, cropped.getRGB(0, 1));
        assertTrue(croppedGraphics.drawImage(source, 4, 0, 2, 2, 0, 0, 2, 2, null));
        assertEquals(0xff00ff00, cropped.getRGB(2, 0));
        assertEquals(0xffff0000, cropped.getRGB(3, 0));
        croppedGraphics.dispose();
        BufferedImage transformed = new BufferedImage(5, 4, BufferedImage.TYPE_INT_ARGB);
        Graphics2D transformedGraphics = transformed.createGraphics();
        assertTrue(transformedGraphics.drawImage(source, AffineTransform.getTranslateInstance(2.0, 1.0), null));
        assertEquals(0xffff0000, transformed.getRGB(2, 1));
        transformedGraphics.drawImage(source, new RescaleOp(1.0f, 0.0f, null), 0, 0);
        assertEquals(0xff00ff00, transformed.getRGB(1, 0));
        transformedGraphics.dispose();
        BufferedImage rendered = new BufferedImage(5, 4, BufferedImage.TYPE_INT_ARGB);
        Graphics2D renderedGraphics = rendered.createGraphics();
        renderedGraphics.drawRenderedImage(source, AffineTransform.getTranslateInstance(0.0, 2.0));
        assertEquals(0xffff0000, rendered.getRGB(0, 2));
        renderedGraphics.dispose();
        BufferedImage renderable = new BufferedImage(5, 4, BufferedImage.TYPE_INT_ARGB);
        Graphics2D renderableGraphics = renderable.createGraphics();
        renderableGraphics.drawRenderableImage(new StaticRenderableImage(source), AffineTransform.getTranslateInstance(2.0, 2.0));
        assertEquals(0xffff0000, renderable.getRGB(0, 0));
        assertEquals(0x00000000, renderable.getRGB(2, 2));
        renderableGraphics.dispose();
        graphics.dispose();
    }

    private static void assertArrayEquals(byte[] expected, byte[] actual) {
        assertEquals(expected.length, actual.length);
        for (int index = 0; index < expected.length; index++) {
            assertEquals(expected[index], actual[index]);
        }
    }

    private static void assertArrayEquals(short[] expected, short[] actual) {
        assertEquals(expected.length, actual.length);
        for (int index = 0; index < expected.length; index++) {
            assertEquals(expected[index], actual[index]);
        }
    }

    private static void assertArrayEquals(int[] expected, int[] actual) {
        assertEquals(expected.length, actual.length);
        for (int index = 0; index < expected.length; index++) {
            assertEquals(expected[index], actual[index]);
        }
    }

    private static void assertThrowsArrayBounds(Runnable runnable) {
        try {
            runnable.run();
        } catch (ArrayIndexOutOfBoundsException expected) {
            return;
        }
        throw new AssertionError("Expected ArrayIndexOutOfBoundsException");
    }

    private static void assertThrowsIllegalArgument(Runnable runnable) {
        try {
            runnable.run();
        } catch (IllegalArgumentException expected) {
            return;
        }
        throw new AssertionError("Expected IllegalArgumentException");
    }

    private static void assertThrowsIndexBounds(Runnable runnable) {
        try {
            runnable.run();
        } catch (IndexOutOfBoundsException expected) {
            return;
        }
        throw new AssertionError("Expected IndexOutOfBoundsException");
    }

    private static void assertThrowsImagingOperation(Runnable runnable) {
        try {
            runnable.run();
        } catch (ImagingOpException expected) {
            return;
        }
        throw new AssertionError("Expected ImagingOpException");
    }

    private static void assertThrowsNullPointer(Runnable runnable) {
        try {
            runnable.run();
        } catch (NullPointerException expected) {
            return;
        }
        throw new AssertionError("Expected NullPointerException");
    }

    private static void assertThrowsUnsupported(Runnable runnable) {
        try {
            runnable.run();
        } catch (UnsupportedOperationException expected) {
            return;
        }
        throw new AssertionError("Expected UnsupportedOperationException");
    }

    private static void assertEquals(float expected, float actual) {
        if (Float.compare(expected, actual) != 0) {
            throw new AssertionError("Expected " + expected + " but was " + actual);
        }
    }

    private static void assertEquals(int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError("Expected " + expected + " but was " + actual);
        }
    }

    private static void assertEquals(String expected, String actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected " + expected + " but was " + actual);
        }
    }

    private static void assertNotSame(Object expected, Object actual) {
        if (expected == actual) {
            throw new AssertionError("Expected different objects");
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

    private static final class RecordingFactory implements ContextualRenderedImageFactory {
        private int mapCount;

        @Override
        public RenderContext mapRenderContext(int sourceIndex, RenderContext renderContext, ParameterBlock parameterBlock, RenderableImage image) {
            mapCount++;
            return (RenderContext) renderContext.clone();
        }

        @Override
        public RenderedImage create(RenderContext renderContext, ParameterBlock parameterBlock) {
            return parameterBlock.getRenderedSource(0);
        }

        @Override
        public RenderedImage create(ParameterBlock parameterBlock, RenderingHints hints) {
            return parameterBlock.getRenderedSource(0);
        }

        @Override
        public Rectangle2D getBounds2D(ParameterBlock parameterBlock) {
            return new Rectangle2D.Double(2.0, 3.0, 10.0, 5.0);
        }

        @Override
        public Object getProperty(ParameterBlock parameterBlock, String name) {
            return "property".equals(name) ? "property-value" : java.awt.Image.UndefinedProperty;
        }

        @Override
        public String[] getPropertyNames() {
            return new String[] {"property"};
        }

        @Override
        public boolean isDynamic() {
            return true;
        }
    }

    private static final class StaticRenderableImage implements RenderableImage {
        private final BufferedImage image;

        private StaticRenderableImage(BufferedImage image) {
            this.image = image;
        }

        @Override
        public Vector<RenderableImage> getSources() {
            return null;
        }

        @Override
        public Object getProperty(String name) {
            return java.awt.Image.UndefinedProperty;
        }

        @Override
        public String[] getPropertyNames() {
            return null;
        }

        @Override
        public boolean isDynamic() {
            return false;
        }

        @Override
        public float getWidth() {
            return image.getWidth();
        }

        @Override
        public float getHeight() {
            return image.getHeight();
        }

        @Override
        public float getMinX() {
            return 0.0f;
        }

        @Override
        public float getMinY() {
            return 0.0f;
        }

        @Override
        public RenderedImage createScaledRendering(int width, int height, RenderingHints hints) {
            return image;
        }

        @Override
        public RenderedImage createDefaultRendering() {
            return image;
        }

        @Override
        public RenderedImage createRendering(RenderContext renderContext) {
            return image;
        }
    }

    private static final class RecordingConsumer extends java.awt.image.ImageFilter {
        private int completionStatus;
        private int height;
        private int[] pixels;
        private int width;

        @Override
        public void setDimensions(int width, int height) {
            this.width = width;
            this.height = height;
            pixels = new int[width * height];
        }

        @Override
        public void setColorModel(java.awt.image.ColorModel model) {
        }

        @Override
        public void setHints(int hints) {
        }

        @Override
        public void setPixels(int x, int y, int width, int height, java.awt.image.ColorModel model, byte[] pixels, int offset, int scanlineStride) {
            for (int row = 0; row < height; row++) {
                for (int column = 0; column < width; column++) {
                    this.pixels[(y + row) * this.width + x + column] = pixels[offset + row * scanlineStride + column] & 0xff;
                }
            }
        }

        @Override
        public void setPixels(int x, int y, int width, int height, java.awt.image.ColorModel model, int[] pixels, int offset, int scanlineStride) {
            for (int row = 0; row < height; row++) {
                for (int column = 0; column < width; column++) {
                    this.pixels[(y + row) * this.width + x + column] = pixels[offset + row * scanlineStride + column];
                }
            }
        }

        @Override
        public void imageComplete(int status) {
            completionStatus = status;
        }
    }

    private static final class TestImageProducer implements ImageProducer {
        private final int height;
        private final int[] pixels;
        private final int width;
        private ImageConsumer consumer;

        private TestImageProducer(int width, int height, int[] pixels) {
            this.width = width;
            this.height = height;
            this.pixels = pixels;
        }

        @Override
        public void addConsumer(ImageConsumer consumer) {
            this.consumer = consumer;
        }

        @Override
        public boolean isConsumer(ImageConsumer consumer) {
            return this.consumer == consumer;
        }

        @Override
        public void removeConsumer(ImageConsumer consumer) {
            if (this.consumer == consumer) {
                this.consumer = null;
            }
        }

        @Override
        public void startProduction(ImageConsumer consumer) {
            addConsumer(consumer);
            consumer.setDimensions(width, height);
            consumer.setColorModel(java.awt.image.ColorModel.getRGBdefault());
            consumer.setHints(ImageConsumer.TOPDOWNLEFTRIGHT | ImageConsumer.COMPLETESCANLINES | ImageConsumer.SINGLEPASS | ImageConsumer.SINGLEFRAME);
            consumer.setPixels(0, 0, width, height, java.awt.image.ColorModel.getRGBdefault(), pixels, 0, width);
            consumer.imageComplete(ImageConsumer.STATICIMAGEDONE);
            removeConsumer(consumer);
        }

        @Override
        public void requestTopDownLeftRightResend(ImageConsumer consumer) {
            startProduction(consumer);
        }
    }
}
