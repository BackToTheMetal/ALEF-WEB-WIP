package java.awt.image;

import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class RescaleOp implements BufferedImageOp, RasterOp {
    private final RenderingHints hints;
    private final int length;
    private final float[] offsets;
    private final float[] scaleFactors;

    public RescaleOp(float[] scaleFactors, float[] offsets, RenderingHints hints) {
        length = Math.min(scaleFactors.length, offsets.length);
        this.scaleFactors = new float[length];
        this.offsets = new float[length];
        System.arraycopy(scaleFactors, 0, this.scaleFactors, 0, length);
        System.arraycopy(offsets, 0, this.offsets, 0, length);
        this.hints = hints;
    }

    public RescaleOp(float scaleFactor, float offset, RenderingHints hints) {
        length = 1;
        scaleFactors = new float[] {scaleFactor};
        offsets = new float[] {offset};
        this.hints = hints;
    }

    public final float[] getScaleFactors(float[] output) {
        float[] result = output == null ? new float[length] : output;
        System.arraycopy(scaleFactors, 0, result, 0, Math.min(length, result.length));
        return result;
    }

    public final float[] getOffsets(float[] output) {
        float[] result = output == null ? new float[length] : output;
        System.arraycopy(offsets, 0, result, 0, Math.min(length, result.length));
        return result;
    }

    public final int getNumFactors() {
        return length;
    }

    @Override
    public final BufferedImage filter(BufferedImage source, BufferedImage destination) {
        ColorModel sourceColorModel = source.getColorModel();
        if (sourceColorModel instanceof IndexColorModel) {
            throw new IllegalArgumentException("Rescaling cannot be performed on an indexed image");
        }
        int colorComponents = sourceColorModel.getNumColorComponents();
        int components = sourceColorModel.getNumComponents();
        if (length != 1 && length != colorComponents && length != components) {
            throw new IllegalArgumentException("Number of scaling constants does not equal the number of color or color/alpha components");
        }
        BufferedImage result = destination == null ? createCompatibleDestImage(source, null) : destination;
        validateDimensions(source.getWidth(), source.getHeight(), result.getWidth(), result.getHeight());
        scalePixels(source.getRaster(), result.getRaster(), length == components ? components : colorComponents);
        return result;
    }

    @Override
    public final WritableRaster filter(Raster source, WritableRaster destination) {
        WritableRaster result = destination == null ? createCompatibleDestRaster(source) : destination;
        validateRasterCompatibility(source, result);
        if (length != 1 && length != source.getNumBands()) {
            throw new IllegalArgumentException("Number of scaling constants does not equal the number of bands in the source Raster");
        }
        scalePixels(source, result, source.getNumBands());
        return result;
    }

    @Override
    public final Rectangle2D getBounds2D(BufferedImage source) {
        return getBounds2D(source.getRaster());
    }

    @Override
    public final Rectangle2D getBounds2D(Raster source) {
        return source.getBounds();
    }

    @Override
    public BufferedImage createCompatibleDestImage(BufferedImage source, ColorModel destinationColorModel) {
        ColorModel colorModel = destinationColorModel == null ? source.getColorModel() : destinationColorModel;
        WritableRaster raster = colorModel.createCompatibleWritableRaster(source.getWidth(), source.getHeight());
        return new BufferedImage(colorModel, raster, BufferedImage.TYPE_CUSTOM);
    }

    @Override
    public WritableRaster createCompatibleDestRaster(Raster source) {
        return source.createCompatibleWritableRaster(source.getWidth(), source.getHeight());
    }

    @Override
    public final Point2D getPoint2D(Point2D sourcePoint, Point2D destinationPoint) {
        Point2D result = destinationPoint == null ? new Point2D.Float() : destinationPoint;
        result.setLocation(sourcePoint.getX(), sourcePoint.getY());
        return result;
    }

    @Override
    public final RenderingHints getRenderingHints() {
        return hints;
    }

    private void scalePixels(Raster source, WritableRaster destination, int scaledBands) {
        int[] pixel = new int[source.getNumBands()];
        for (int row = 0; row < source.getHeight(); row++) {
            int sourceY = source.getMinY() + row;
            int destinationY = destination.getMinY() + row;
            for (int column = 0; column < source.getWidth(); column++) {
                int sourceX = source.getMinX() + column;
                int destinationX = destination.getMinX() + column;
                source.getPixel(sourceX, sourceY, pixel);
                for (int band = 0; band < scaledBands; band++) {
                    int factorIndex = length == 1 ? 0 : band;
                    int value = (int) (pixel[band] * scaleFactors[factorIndex] + offsets[factorIndex]);
                    pixel[band] = clamp(value, destination.getSampleModel().getSampleSize(band));
                }
                destination.setPixel(destinationX, destinationY, pixel);
            }
        }
    }

    private static int clamp(int value, int bits) {
        int maximum = bits >= 31 ? Integer.MAX_VALUE : (1 << bits) - 1;
        return Math.max(0, Math.min(value, maximum));
    }

    private static void validateDimensions(int sourceWidth, int sourceHeight, int destinationWidth, int destinationHeight) {
        if (sourceWidth != destinationWidth || sourceHeight != destinationHeight) {
            throw new IllegalArgumentException("Source and destination dimensions do not match");
        }
    }

    private static void validateRasterCompatibility(Raster source, Raster destination) {
        validateDimensions(source.getWidth(), source.getHeight(), destination.getWidth(), destination.getHeight());
        if (source.getNumBands() != destination.getNumBands()) {
            throw new IllegalArgumentException("Source and destination band counts do not match");
        }
    }
}
