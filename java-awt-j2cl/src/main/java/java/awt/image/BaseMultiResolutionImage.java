package java.awt.image;

import java.awt.Image;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BaseMultiResolutionImage extends AbstractMultiResolutionImage {
    private final int baseImageIndex;
    private final Image[] resolutionVariants;

    public BaseMultiResolutionImage(Image... resolutionVariants) {
        this(0, resolutionVariants);
    }

    public BaseMultiResolutionImage(int baseImageIndex, Image... resolutionVariants) {
        if (resolutionVariants == null || resolutionVariants.length == 0) {
            throw new IllegalArgumentException("Null or zero-length array is passed");
        }
        if (baseImageIndex < 0 || baseImageIndex >= resolutionVariants.length) {
            throw new IndexOutOfBoundsException("Invalid base image index: " + baseImageIndex);
        }
        this.baseImageIndex = baseImageIndex;
        this.resolutionVariants = new Image[resolutionVariants.length];
        System.arraycopy(resolutionVariants, 0, this.resolutionVariants, 0, resolutionVariants.length);
        for (Image variant : this.resolutionVariants) {
            if (variant == null) {
                throw new NullPointerException("Resolution variant can't be null");
            }
        }
    }

    @Override
    public Image getResolutionVariant(double destinationImageWidth, double destinationImageHeight) {
        validateSize(destinationImageWidth, destinationImageHeight);
        for (Image variant : resolutionVariants) {
            if (destinationImageWidth <= variant.getWidth(null) && destinationImageHeight <= variant.getHeight(null)) {
                return variant;
            }
        }
        return resolutionVariants[resolutionVariants.length - 1];
    }

    @Override
    public List<Image> getResolutionVariants() {
        return Collections.unmodifiableList(Arrays.asList(resolutionVariants));
    }

    @Override
    protected Image getBaseImage() {
        return resolutionVariants[baseImageIndex];
    }

    private static void validateSize(double width, double height) {
        if (width <= 0.0 || height <= 0.0) {
            throw new IllegalArgumentException("Width or height cannot be <= 0");
        }
        if (Double.isNaN(width) || Double.isNaN(height) || Double.isInfinite(width) || Double.isInfinite(height)) {
            throw new IllegalArgumentException("Width or height is not finite");
        }
    }
}
