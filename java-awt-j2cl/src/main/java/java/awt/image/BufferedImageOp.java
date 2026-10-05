package java.awt.image;

import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public interface BufferedImageOp {
    BufferedImage filter(BufferedImage source, BufferedImage destination);
    Rectangle2D getBounds2D(BufferedImage source);
    BufferedImage createCompatibleDestImage(BufferedImage source, ColorModel destinationColorModel);
    Point2D getPoint2D(Point2D sourcePoint, Point2D destinationPoint);
    RenderingHints getRenderingHints();
}
