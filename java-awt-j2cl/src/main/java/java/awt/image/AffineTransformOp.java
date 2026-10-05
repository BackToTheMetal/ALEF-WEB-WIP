package java.awt.image;

import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class AffineTransformOp implements BufferedImageOp, RasterOp {
    public static final int TYPE_NEAREST_NEIGHBOR = 1;
    public static final int TYPE_BILINEAR = 2;
    public static final int TYPE_BICUBIC = 3;
    private RenderingHints hints;
    private final int interpolationType;
    private final AffineTransform transform;

    public AffineTransformOp(AffineTransform transform, RenderingHints hints) {
        validateTransform(transform);
        this.transform = (AffineTransform) transform.clone();
        this.hints = hints;
        interpolationType = interpolationType(hints);
    }

    public AffineTransformOp(AffineTransform transform, int interpolationType) {
        validateTransform(transform);
        if (interpolationType != TYPE_NEAREST_NEIGHBOR && interpolationType != TYPE_BILINEAR && interpolationType != TYPE_BICUBIC) {
            throw new IllegalArgumentException("Unknown interpolation type: " + interpolationType);
        }
        this.transform = (AffineTransform) transform.clone();
        this.interpolationType = interpolationType;
    }

    public final int getInterpolationType() {
        return interpolationType;
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
        transform(source.getRaster(), result.getRaster());
        return result;
    }

    @Override
    public final WritableRaster filter(Raster source, WritableRaster destination) {
        if (source == null) {
            throw new NullPointerException("src image is null");
        }
        WritableRaster result = destination == null ? createCompatibleDestRaster(source) : destination;
        if (source == result) {
            throw new IllegalArgumentException("src image cannot be the same as the dst image");
        }
        if (source.getNumBands() != result.getNumBands()) {
            throw new IllegalArgumentException("Source and destination band counts do not match");
        }
        transform(source, result);
        return result;
    }

    @Override
    public final Rectangle2D getBounds2D(BufferedImage source) {
        return getBounds2D(source.getRaster());
    }

    @Override
    public final Rectangle2D getBounds2D(Raster source) {
        float[] points = {0.0f, 0.0f, source.getWidth(), 0.0f, source.getWidth(), source.getHeight(), 0.0f, source.getHeight()};
        transform.transform(points, 0, points, 0, 4);
        float minimumX = points[0];
        float maximumX = points[0];
        float minimumY = points[1];
        float maximumY = points[1];
        for (int index = 2; index < points.length; index += 2) {
            minimumX = Math.min(minimumX, points[index]);
            maximumX = Math.max(maximumX, points[index]);
            minimumY = Math.min(minimumY, points[index + 1]);
            maximumY = Math.max(maximumY, points[index + 1]);
        }
        return new Rectangle2D.Float(minimumX, minimumY, maximumX - minimumX, maximumY - minimumY);
    }

    @Override
    public BufferedImage createCompatibleDestImage(BufferedImage source, ColorModel destinationColorModel) {
        java.awt.Rectangle bounds = getBounds2D(source).getBounds();
        int width = bounds.x + bounds.width;
        int height = bounds.y + bounds.height;
        if (width <= 0) {
            throw new RasterFormatException("Transformed width is less than or equal to 0");
        }
        if (height <= 0) {
            throw new RasterFormatException("Transformed height is less than or equal to 0");
        }
        ColorModel colorModel = destinationColorModel == null ? source.getColorModel() : destinationColorModel;
        if (destinationColorModel == null && interpolationType != TYPE_NEAREST_NEIGHBOR && (colorModel instanceof IndexColorModel || colorModel.getTransparency() == Transparency.OPAQUE)) {
            return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        }
        return new BufferedImage(colorModel, colorModel.createCompatibleWritableRaster(width, height), BufferedImage.TYPE_CUSTOM);
    }

    @Override
    public WritableRaster createCompatibleDestRaster(Raster source) {
        Rectangle2D bounds = getBounds2D(source);
        return source.createCompatibleWritableRaster((int) bounds.getX(), (int) bounds.getY(), (int) bounds.getWidth(), (int) bounds.getHeight());
    }

    @Override
    public final Point2D getPoint2D(Point2D sourcePoint, Point2D destinationPoint) {
        return transform.transform(sourcePoint, destinationPoint);
    }

    public final AffineTransform getTransform() {
        return (AffineTransform) transform.clone();
    }

    @Override
    public final RenderingHints getRenderingHints() {
        if (hints == null) {
            Object value = interpolationType == TYPE_NEAREST_NEIGHBOR ? RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR : interpolationType == TYPE_BILINEAR ? RenderingHints.VALUE_INTERPOLATION_BILINEAR : RenderingHints.VALUE_INTERPOLATION_BICUBIC;
            hints = new RenderingHints(RenderingHints.KEY_INTERPOLATION, value);
        }
        return hints;
    }

    private void transform(Raster source, WritableRaster destination) {
        AffineTransform inverse;
        try {
            inverse = transform.createInverse();
        } catch (NoninvertibleTransformException exception) {
            throw new ImagingOpException("Unable to invert transform");
        }
        Point2D sourcePoint = new Point2D.Double();
        Point2D destinationPoint = new Point2D.Double();
        for (int y = destination.getMinY(); y < (long) destination.getMinY() + destination.getHeight(); y++) {
            for (int x = destination.getMinX(); x < (long) destination.getMinX() + destination.getWidth(); x++) {
                destinationPoint.setLocation(x, y);
                inverse.transform(destinationPoint, sourcePoint);
                if (interpolationType == TYPE_NEAREST_NEIGHBOR) {
                    sampleNearest(source, destination, sourcePoint.getX(), sourcePoint.getY(), x, y);
                } else {
                    sampleBilinear(source, destination, sourcePoint.getX(), sourcePoint.getY(), x, y);
                }
            }
        }
    }

    private static void sampleNearest(Raster source, WritableRaster destination, double sourceX, double sourceY, int destinationX, int destinationY) {
        int x = (int) Math.floor(sourceX);
        int y = (int) Math.floor(sourceY);
        if (contains(source, x, y)) {
            destination.setPixel(destinationX, destinationY, source.getPixel(x, y, (int[]) null));
        }
    }

    private static void sampleBilinear(Raster source, WritableRaster destination, double sourceX, double sourceY, int destinationX, int destinationY) {
        int x0 = (int) Math.floor(sourceX);
        int y0 = (int) Math.floor(sourceY);
        if (!contains(source, x0, y0)) {
            return;
        }
        int x1 = Math.min(x0 + 1, source.getMinX() + source.getWidth() - 1);
        int y1 = Math.min(y0 + 1, source.getMinY() + source.getHeight() - 1);
        double fractionX = sourceX - x0;
        double fractionY = sourceY - y0;
        int[] topLeft = source.getPixel(x0, y0, (int[]) null);
        int[] topRight = source.getPixel(x1, y0, (int[]) null);
        int[] bottomLeft = source.getPixel(x0, y1, (int[]) null);
        int[] bottomRight = source.getPixel(x1, y1, (int[]) null);
        int[] result = new int[source.getNumBands()];
        for (int band = 0; band < result.length; band++) {
            double top = topLeft[band] + (topRight[band] - topLeft[band]) * fractionX;
            double bottom = bottomLeft[band] + (bottomRight[band] - bottomLeft[band]) * fractionX;
            result[band] = (int) (top + (bottom - top) * fractionY);
        }
        destination.setPixel(destinationX, destinationY, result);
    }

    private static boolean contains(Raster raster, int x, int y) {
        return x >= raster.getMinX() && y >= raster.getMinY() && x < (long) raster.getMinX() + raster.getWidth() && y < (long) raster.getMinY() + raster.getHeight();
    }

    private static int interpolationType(RenderingHints hints) {
        if (hints == null) {
            return TYPE_NEAREST_NEIGHBOR;
        }
        Object interpolation = hints.get(RenderingHints.KEY_INTERPOLATION);
        if (interpolation == RenderingHints.VALUE_INTERPOLATION_BILINEAR) {
            return TYPE_BILINEAR;
        }
        if (interpolation == RenderingHints.VALUE_INTERPOLATION_BICUBIC) {
            return TYPE_BICUBIC;
        }
        if (interpolation == RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR) {
            return TYPE_NEAREST_NEIGHBOR;
        }
        return hints.get(RenderingHints.KEY_RENDERING) == RenderingHints.VALUE_RENDER_QUALITY ? TYPE_BILINEAR : TYPE_NEAREST_NEIGHBOR;
    }

    private static void validateTransform(AffineTransform transform) {
        if (Math.abs(transform.getDeterminant()) <= Double.MIN_VALUE) {
            throw new ImagingOpException("Unable to invert transform " + transform);
        }
    }
}
