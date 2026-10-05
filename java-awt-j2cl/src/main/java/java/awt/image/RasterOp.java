package java.awt.image;

import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public interface RasterOp {
    WritableRaster filter(Raster source, WritableRaster destination);
    Rectangle2D getBounds2D(Raster source);
    WritableRaster createCompatibleDestRaster(Raster source);
    Point2D getPoint2D(Point2D sourcePoint, Point2D destinationPoint);
    RenderingHints getRenderingHints();
}
