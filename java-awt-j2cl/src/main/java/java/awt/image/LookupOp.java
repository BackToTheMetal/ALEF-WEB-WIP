package java.awt.image;

import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class LookupOp implements BufferedImageOp, RasterOp {
    private final RenderingHints hints;
    private final LookupTable table;

    public LookupOp(LookupTable lookup, RenderingHints hints) {
        table = lookup;
        table.getNumComponents();
        this.hints = hints;
    }

    public final LookupTable getTable() {
        return table;
    }

    @Override
    public final BufferedImage filter(BufferedImage source, BufferedImage destination) {
        ColorModel sourceColorModel = source.getColorModel();
        if (sourceColorModel instanceof IndexColorModel) {
            throw new IllegalArgumentException("LookupOp cannot be performed on an indexed image");
        }
        int tableComponents = table.getNumComponents();
        int colorComponents = sourceColorModel.getNumColorComponents();
        int components = sourceColorModel.getNumComponents();
        if (tableComponents != 1 && tableComponents != colorComponents && tableComponents != components) {
            throw new IllegalArgumentException("Number of arrays in the lookup table is not compatible with the source image");
        }
        BufferedImage result = destination == null ? createCompatibleDestImage(source, null) : destination;
        validateDimensions(source.getWidth(), source.getHeight(), result.getWidth(), result.getHeight());
        filterPixels(source.getRaster(), result.getRaster(), tableComponents == components ? components : colorComponents);
        return result;
    }

    @Override
    public final WritableRaster filter(Raster source, WritableRaster destination) {
        WritableRaster result = destination == null ? createCompatibleDestRaster(source) : destination;
        validateRasterCompatibility(source, result);
        int components = table.getNumComponents();
        if (components != 1 && components != source.getNumBands()) {
            throw new IllegalArgumentException("Number of arrays in the lookup table is not compatible with the source Raster");
        }
        filterPixels(source, result, source.getNumBands());
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
        return source.createCompatibleWritableRaster();
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

    private void filterPixels(Raster source, WritableRaster destination, int filteredBands) {
        int[] pixel = new int[source.getNumBands()];
        int[] filtered = new int[filteredBands];
        for (int row = 0; row < source.getHeight(); row++) {
            int sourceY = source.getMinY() + row;
            int destinationY = destination.getMinY() + row;
            for (int column = 0; column < source.getWidth(); column++) {
                int sourceX = source.getMinX() + column;
                int destinationX = destination.getMinX() + column;
                source.getPixel(sourceX, sourceY, pixel);
                System.arraycopy(pixel, 0, filtered, 0, filteredBands);
                table.lookupPixel(filtered, filtered);
                System.arraycopy(filtered, 0, pixel, 0, filteredBands);
                destination.setPixel(destinationX, destinationY, pixel);
            }
        }
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
