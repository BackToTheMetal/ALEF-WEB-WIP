package java.awt.image;

import java.awt.Image;
import java.util.List;

public interface MultiResolutionImage {
    Image getResolutionVariant(double destinationImageWidth, double destinationImageHeight);
    List<Image> getResolutionVariants();
}
