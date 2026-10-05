package java.awt.image;

import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class ConvolveOp implements BufferedImageOp, RasterOp {
    public static final int EDGE_ZERO_FILL = 0;
    public static final int EDGE_NO_OP = 1;
    private final int edgeCondition;
    private final RenderingHints hints;
    private final Kernel kernel;

    public ConvolveOp(Kernel kernel, int edgeCondition, RenderingHints hints) {
        this.kernel = kernel;
        this.edgeCondition = edgeCondition;
        this.hints = hints;
    }

    public ConvolveOp(Kernel kernel) {
        this(kernel, EDGE_ZERO_FILL, null);
    }

    public int getEdgeCondition() {
        return edgeCondition;
    }

    public final Kernel getKernel() {
        return (Kernel) kernel.clone();
    }

    @Override
    public final BufferedImage filter(BufferedImage source, BufferedImage destination) {
        if (source == null) {
            throw new NullPointerException("src image is null");
        }
        if (source == destination) {
            throw new IllegalArgumentException("src image cannot be the same as the dst image");
        }
        BufferedImage result = destination == null ? createCompatibleDestImage(source, null) : destination;
        validateDimensions(source.getWidth(), source.getHeight(), result.getWidth(), result.getHeight());
        convolve(source.getRaster(), result.getRaster());
        return result;
    }

    @Override
    public final WritableRaster filter(Raster source, WritableRaster destination) {
        if (source == destination) {
            throw new IllegalArgumentException("src image cannot be the same as the dst image");
        }
        WritableRaster result = destination == null ? createCompatibleDestRaster(source) : destination;
        validateDimensions(source.getWidth(), source.getHeight(), result.getWidth(), result.getHeight());
        if (source.getNumBands() != result.getNumBands()) {
            throw new ImagingOpException("Different number of bands in src and dst Rasters");
        }
        convolve(source, result);
        return result;
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
    public final Rectangle2D getBounds2D(BufferedImage source) {
        return getBounds2D(source.getRaster());
    }

    @Override
    public final Rectangle2D getBounds2D(Raster source) {
        return source.getBounds();
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

    private void convolve(Raster source, WritableRaster destination) {
        int kernelWidth = kernel.getWidth();
        int kernelHeight = kernel.getHeight();
        float[] kernelData = kernel.getKernelData(null);
        int[] sourcePixel = new int[source.getNumBands()];
        int[] destinationPixel = new int[source.getNumBands()];
        for (int row = 0; row < source.getHeight(); row++) {
            int sourceY = source.getMinY() + row;
            int destinationY = destination.getMinY() + row;
            for (int column = 0; column < source.getWidth(); column++) {
                int sourceX = source.getMinX() + column;
                int destinationX = destination.getMinX() + column;
                if (!kernelFits(source, sourceX, sourceY, kernelWidth, kernelHeight)) {
                    writeEdgePixel(source, destination, sourceX, sourceY, destinationX, destinationY, sourcePixel, destinationPixel);
                    continue;
                }
                for (int band = 0; band < destinationPixel.length; band++) {
                    float value = 0.0f;
                    for (int kernelY = 0; kernelY < kernelHeight; kernelY++) {
                        for (int kernelX = 0; kernelX < kernelWidth; kernelX++) {
                            source.getPixel(sourceX + kernelX - kernel.getXOrigin(), sourceY + kernelY - kernel.getYOrigin(), sourcePixel);
                            value += kernelData[(kernelHeight - kernelY - 1) * kernelWidth + kernelWidth - kernelX - 1] * sourcePixel[band];
                        }
                    }
                    destinationPixel[band] = clamp((int) value, destination.getSampleModel().getSampleSize(band));
                }
                destination.setPixel(destinationX, destinationY, destinationPixel);
            }
        }
    }

    private boolean kernelFits(Raster source, int x, int y, int kernelWidth, int kernelHeight) {
        int firstX = x - kernel.getXOrigin();
        int firstY = y - kernel.getYOrigin();
        return firstX >= source.getMinX() && firstY >= source.getMinY() && firstX + kernelWidth <= (long) source.getMinX() + source.getWidth() && firstY + kernelHeight <= (long) source.getMinY() + source.getHeight();
    }

    private void writeEdgePixel(Raster source, WritableRaster destination, int sourceX, int sourceY, int destinationX, int destinationY, int[] sourcePixel, int[] destinationPixel) {
        if (edgeCondition == EDGE_NO_OP) {
            source.getPixel(sourceX, sourceY, sourcePixel);
            destination.setPixel(destinationX, destinationY, sourcePixel);
        } else {
            for (int band = 0; band < destinationPixel.length; band++) {
                destinationPixel[band] = 0;
            }
            destination.setPixel(destinationX, destinationY, destinationPixel);
        }
    }

    private static int clamp(int value, int bits) {
        int maximum = bits >= 31 ? Integer.MAX_VALUE : (1 << bits) - 1;
        return Math.max(0, Math.min(value, maximum));
    }

    private static void validateDimensions(int sourceWidth, int sourceHeight, int destinationWidth, int destinationHeight) {
        if (sourceWidth != destinationWidth || sourceHeight != destinationHeight) {
            throw new ImagingOpException("Source and destination dimensions do not match");
        }
    }
}
