package com.webawt.portable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import org.junit.Test;

public final class ColorConvertOpJ2clTest {
    @Test
    public void preservesProfilesHintsBoundsAndPoints() {
        ICC_Profile[] profiles = {ICC_Profile.getInstance(ColorSpace.CS_sRGB), ICC_Profile.getInstance(ColorSpace.CS_GRAY)};
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        ColorConvertOp operation = new ColorConvertOp(profiles, hints);
        ICC_Profile[] returned = operation.getICC_Profiles();
        assertNotSame(profiles, returned);
        assertSame(profiles[0], returned[0]);
        assertSame(hints, operation.getRenderingHints());
        BufferedImage source = new BufferedImage(3, 2, BufferedImage.TYPE_INT_ARGB);
        assertEquals(source.getRaster().getBounds(), operation.getBounds2D(source));
        Point2D sourcePoint = new Point2D.Double(4.5, 7.25);
        Point2D destinationPoint = operation.getPoint2D(sourcePoint, null);
        assertEquals(sourcePoint, destinationPoint);
    }

    @Test
    public void convertsBufferedImagesToTheRequestedDestinationSpace() {
        BufferedImage source = new BufferedImage(2, 1, BufferedImage.TYPE_INT_ARGB);
        source.setRGB(0, 0, 0x80ff0000);
        source.setRGB(1, 0, 0xffffffff);
        ColorConvertOp operation = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
        BufferedImage destination = operation.filter(source, null);
        assertEquals(ColorSpace.TYPE_GRAY, destination.getColorModel().getColorSpace().getType());
        assertEquals(0x80, destination.getRGB(0, 0) >>> 24);
        assertEquals(0xff, destination.getRGB(1, 0) >>> 24);
        assertChannelBalance(destination.getRGB(0, 0));
        assertChannelBalance(destination.getRGB(1, 0));
    }

    @Test
    public void convertsRasterBandsBetweenExplicitColorSpaces() {
        WritableRaster source = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, 2, 1, 3, null);
        source.setPixel(0, 0, new int[] {0, 0, 0});
        source.setPixel(1, 0, new int[] {255, 255, 255});
        ColorConvertOp operation = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_sRGB), ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
        WritableRaster destination = operation.filter(source, null);
        assertEquals(1, destination.getNumBands());
        assertTrue(destination.getSample(0, 0, 0) <= 2);
        assertTrue(destination.getSample(1, 0, 0) >= 253);
    }

    private static void assertChannelBalance(int rgb) {
        int red = rgb >>> 16 & 0xff;
        int green = rgb >>> 8 & 0xff;
        int blue = rgb & 0xff;
        assertTrue(Math.abs(red - green) <= 2);
        assertTrue(Math.abs(green - blue) <= 2);
    }
}
