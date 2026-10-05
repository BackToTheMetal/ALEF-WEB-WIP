package java.awt.image;

import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class ColorConvertOp implements BufferedImageOp, RasterOp {
    private final RenderingHints hints;
    private final ICC_Profile[] profiles;
    private final ColorSpace[] spaces;

    public ColorConvertOp(RenderingHints hints) {
        this.hints = hints;
        profiles = null;
        spaces = new ColorSpace[0];
    }

    public ColorConvertOp(ColorSpace colorSpace, RenderingHints hints) {
        if (colorSpace == null) {
            throw new NullPointerException("colorSpace");
        }
        this.hints = hints;
        profiles = null;
        spaces = new ColorSpace[] {colorSpace};
    }

    public ColorConvertOp(ColorSpace sourceColorSpace, ColorSpace destinationColorSpace, RenderingHints hints) {
        if (sourceColorSpace == null || destinationColorSpace == null) {
            throw new NullPointerException("sourceColorSpace and destinationColorSpace");
        }
        this.hints = hints;
        profiles = null;
        spaces = new ColorSpace[] {sourceColorSpace, destinationColorSpace};
    }

    public ColorConvertOp(ICC_Profile[] profiles, RenderingHints hints) {
        if (profiles == null) {
            throw new NullPointerException("profiles");
        }
        if (profiles.length == 0) {
            throw new IllegalArgumentException("profiles must not be empty");
        }
        this.hints = hints;
        this.profiles = copyProfiles(profiles);
        spaces = new ColorSpace[profiles.length];
        for (int index = 0; index < profiles.length; index++) {
            spaces[index] = new ICC_ColorSpace(requireProfile(profiles[index]));
        }
    }

    public final ICC_Profile[] getICC_Profiles() {
        return profiles == null ? null : copyProfiles(profiles);
    }

    @Override
    public final BufferedImage filter(BufferedImage source, BufferedImage destination) {
        if (source == null) {
            throw new NullPointerException("source");
        }
        if (destination == null && spaces.length == 0) {
            throw new IllegalArgumentException("Destination color space is undefined");
        }
        BufferedImage result = destination == null ? createCompatibleDestImage(source, null) : destination;
        if (source.getWidth() != result.getWidth() || source.getHeight() != result.getHeight()) {
            throw new IllegalArgumentException("Source and destination dimensions do not match");
        }
        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                int argb = source.getRGB(x, y);
                float[] rgb = {(argb >>> 16 & 0xff) / 255.0f, (argb >>> 8 & 0xff) / 255.0f, (argb & 0xff) / 255.0f};
                float[] converted = convertFromRgb(rgb);
                int output = argb & 0xff000000 | toByte(converted[0]) << 16 | toByte(converted[1]) << 8 | toByte(converted[2]);
                result.setRGB(x, y, output);
            }
        }
        return result;
    }

    @Override
    public final WritableRaster filter(Raster source, WritableRaster destination) {
        requireRasterSpaces();
        if (source == null) {
            throw new NullPointerException("source");
        }
        ColorSpace sourceSpace = spaces[0];
        ColorSpace destinationSpace = spaces[spaces.length - 1];
        if (source.getNumBands() != sourceSpace.getNumComponents()) {
            throw new IllegalArgumentException("Source band count does not match the source color space");
        }
        WritableRaster result = destination == null ? createCompatibleDestRaster(source) : destination;
        if (result.getNumBands() != destinationSpace.getNumComponents()) {
            throw new IllegalArgumentException("Destination band count does not match the destination color space");
        }
        if (source.getWidth() != result.getWidth() || source.getHeight() != result.getHeight()) {
            throw new IllegalArgumentException("Source and destination dimensions do not match");
        }
        convertRaster(source, result);
        return result;
    }

    @Override
    public final Rectangle2D getBounds2D(BufferedImage source) {
        return source.getRaster().getBounds();
    }

    @Override
    public final Rectangle2D getBounds2D(Raster source) {
        return source.getBounds();
    }

    @Override
    public BufferedImage createCompatibleDestImage(BufferedImage source, ColorModel destinationColorModel) {
        if (source == null) {
            throw new NullPointerException("source");
        }
        ColorModel colorModel = destinationColorModel;
        if (colorModel == null) {
            if (spaces.length == 0) {
                throw new IllegalArgumentException("Destination color space is undefined");
            }
            ColorSpace destinationSpace = spaces[spaces.length - 1];
            boolean alpha = source.getColorModel().hasAlpha();
            int transparency = alpha ? Transparency.TRANSLUCENT : Transparency.OPAQUE;
            colorModel = new ComponentColorModel(destinationSpace, alpha, false, transparency, DataBuffer.TYPE_BYTE);
        }
        WritableRaster raster = colorModel.createCompatibleWritableRaster(source.getWidth(), source.getHeight());
        return new BufferedImage(colorModel, raster, BufferedImage.TYPE_CUSTOM);
    }

    @Override
    public WritableRaster createCompatibleDestRaster(Raster source) {
        requireRasterSpaces();
        if (source == null) {
            throw new NullPointerException("source");
        }
        int bands = spaces[spaces.length - 1].getNumComponents();
        int[] offsets = new int[bands];
        for (int index = 0; index < bands; index++) {
            offsets[index] = index;
        }
        SampleModel model = new PixelInterleavedSampleModel(source.getTransferType(), source.getWidth(), source.getHeight(), bands, source.getWidth() * bands, offsets);
        return Raster.createWritableRaster(model, new Point(source.getMinX(), source.getMinY()));
    }

    @Override
    public final Point2D getPoint2D(Point2D sourcePoint, Point2D destinationPoint) {
        if (sourcePoint == null) {
            throw new NullPointerException("sourcePoint");
        }
        Point2D result = destinationPoint == null ? new Point2D.Float() : destinationPoint;
        result.setLocation(sourcePoint.getX(), sourcePoint.getY());
        return result;
    }

    @Override
    public final RenderingHints getRenderingHints() {
        return hints;
    }

    private void convertRaster(Raster source, WritableRaster destination) {
        ColorSpace sourceSpace = spaces[0];
        ColorSpace destinationSpace = spaces[spaces.length - 1];
        float[] sourcePixel = new float[source.getNumBands()];
        float[] destinationPixel = new float[destination.getNumBands()];
        for (int row = 0; row < source.getHeight(); row++) {
            int sourceY = source.getMinY() + row;
            int destinationY = destination.getMinY() + row;
            for (int column = 0; column < source.getWidth(); column++) {
                int sourceX = source.getMinX() + column;
                int destinationX = destination.getMinX() + column;
                source.getPixel(sourceX, sourceY, sourcePixel);
                normalize(sourcePixel, source.getSampleModel(), sourceSpace, source.getTransferType());
                float[] converted = convert(sourcePixel);
                System.arraycopy(converted, 0, destinationPixel, 0, destinationPixel.length);
                denormalize(destinationPixel, destination.getSampleModel(), destinationSpace, destination.getTransferType());
                destination.setPixel(destinationX, destinationY, destinationPixel);
            }
        }
    }

    private float[] convertFromRgb(float[] rgb) {
        if (spaces.length == 0) {
            return rgb;
        }
        float[] value = spaces[0].fromRGB(rgb);
        for (int index = 1; index < spaces.length; index++) {
            value = spaces[index].fromCIEXYZ(spaces[index - 1].toCIEXYZ(value));
        }
        return spaces[spaces.length - 1].toRGB(value);
    }

    private float[] convert(float[] value) {
        float[] converted = value;
        for (int index = 1; index < spaces.length; index++) {
            converted = spaces[index].fromCIEXYZ(spaces[index - 1].toCIEXYZ(converted));
        }
        return converted;
    }

    private void requireRasterSpaces() {
        if (spaces.length < 2) {
            throw new IllegalArgumentException("Raster conversion requires source and destination color spaces");
        }
    }

    private static void normalize(float[] pixel, SampleModel model, ColorSpace space, int transferType) {
        if (transferType == DataBuffer.TYPE_FLOAT || transferType == DataBuffer.TYPE_DOUBLE) {
            return;
        }
        for (int component = 0; component < pixel.length; component++) {
            float unit = pixel[component] / maximumSample(model.getSampleSize(component));
            pixel[component] = space.getMinValue(component) + unit * (space.getMaxValue(component) - space.getMinValue(component));
        }
    }

    private static void denormalize(float[] pixel, SampleModel model, ColorSpace space, int transferType) {
        if (transferType == DataBuffer.TYPE_FLOAT || transferType == DataBuffer.TYPE_DOUBLE) {
            return;
        }
        for (int component = 0; component < pixel.length; component++) {
            float minimum = space.getMinValue(component);
            float range = space.getMaxValue(component) - minimum;
            float unit = range == 0.0f ? 0.0f : (pixel[component] - minimum) / range;
            pixel[component] = clamp(unit) * maximumSample(model.getSampleSize(component));
        }
    }

    private static float maximumSample(int bits) {
        return bits >= 31 ? Integer.MAX_VALUE : (1 << bits) - 1;
    }

    private static int toByte(float value) {
        return (int) (clamp(value) * 255.0f + 0.5f);
    }

    private static float clamp(float value) {
        return Math.max(0.0f, Math.min(1.0f, value));
    }

    private static ICC_Profile requireProfile(ICC_Profile profile) {
        if (profile == null) {
            throw new NullPointerException("profiles contains null");
        }
        return profile;
    }

    private static ICC_Profile[] copyProfiles(ICC_Profile[] source) {
        ICC_Profile[] result = new ICC_Profile[source.length];
        System.arraycopy(source, 0, result, 0, source.length);
        return result;
    }
}
