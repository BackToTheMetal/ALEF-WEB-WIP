package java.awt;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;

public abstract class Image {
    public static final Object UndefinedProperty = new Object();
    public static final int SCALE_DEFAULT = 1;
    public static final int SCALE_FAST = 2;
    public static final int SCALE_SMOOTH = 4;
    public static final int SCALE_REPLICATE = 8;
    public static final int SCALE_AREA_AVERAGING = 16;
    protected float accelerationPriority = 0.5f;

    protected Image() {
    }

    public abstract int getWidth(ImageObserver observer);

    public abstract int getHeight(ImageObserver observer);

    public abstract ImageProducer getSource();

    public abstract Graphics getGraphics();

    public abstract Object getProperty(String name, ImageObserver observer);

    public Image getScaledInstance(int width, int height, int hints) {
        if (width == 0 || height == 0) {
            throw new IllegalArgumentException("Width and height must be non-zero");
        }
        int sourceWidth = getWidth(null);
        int sourceHeight = getHeight(null);
        int targetWidth = width < 0 ? height < 0 ? sourceWidth : Math.max(1, sourceWidth * height / sourceHeight) : width;
        int targetHeight = height < 0 ? width < 0 ? sourceHeight : Math.max(1, sourceHeight * width / sourceWidth) : height;
        if (!(this instanceof BufferedImage)) {
            throw new UnsupportedOperationException("Scaling requires a raster-backed image");
        }
        BufferedImage source = (BufferedImage) this;
        BufferedImage scaled = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        boolean smooth = (hints & (SCALE_SMOOTH | SCALE_AREA_AVERAGING)) != 0;
        for (int y = 0; y < targetHeight; y++) {
            for (int x = 0; x < targetWidth; x++) {
                scaled.setRGB(x, y, smooth ? bilinear(source, x, y, targetWidth, targetHeight) : nearest(source, x, y, targetWidth, targetHeight));
            }
        }
        return scaled;
    }

    public void flush() {
        // Buffered image storage is managed by the JavaScript garbage collector.
    }

    public void setAccelerationPriority(float priority) {
        if (priority < 0.0f || priority > 1.0f) {
            throw new IllegalArgumentException("Priority must be between 0 and 1");
        }
        accelerationPriority = priority;
    }

    public float getAccelerationPriority() {
        return accelerationPriority;
    }

    public ImageCapabilities getCapabilities(GraphicsConfiguration configuration) {
        return new ImageCapabilities(false);
    }

    private static int nearest(BufferedImage source, int x, int y, int targetWidth, int targetHeight) {
        int sourceX = Math.min(source.getWidth() - 1, x * source.getWidth() / targetWidth);
        int sourceY = Math.min(source.getHeight() - 1, y * source.getHeight() / targetHeight);
        return source.getRGB(sourceX, sourceY);
    }

    private static int bilinear(BufferedImage source, int x, int y, int targetWidth, int targetHeight) {
        double sourceX = (x + 0.5) * source.getWidth() / targetWidth - 0.5;
        double sourceY = (y + 0.5) * source.getHeight() / targetHeight - 0.5;
        int left = Math.max(0, (int) Math.floor(sourceX));
        int top = Math.max(0, (int) Math.floor(sourceY));
        int right = Math.min(source.getWidth() - 1, left + 1);
        int bottom = Math.min(source.getHeight() - 1, top + 1);
        double horizontal = Math.max(0.0, sourceX - left);
        double vertical = Math.max(0.0, sourceY - top);
        int upper = interpolateArgb(source.getRGB(left, top), source.getRGB(right, top), horizontal);
        int lower = interpolateArgb(source.getRGB(left, bottom), source.getRGB(right, bottom), horizontal);
        return interpolateArgb(upper, lower, vertical);
    }

    private static int interpolateArgb(int first, int second, double amount) {
        int alpha = interpolateChannel(first >>> 24, second >>> 24, amount);
        int red = interpolateChannel(first >>> 16 & 0xff, second >>> 16 & 0xff, amount);
        int green = interpolateChannel(first >>> 8 & 0xff, second >>> 8 & 0xff, amount);
        int blue = interpolateChannel(first & 0xff, second & 0xff, amount);
        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    private static int interpolateChannel(int first, int second, double amount) {
        return (int) (first * (1.0 - amount) + second * amount + 0.5);
    }
}
