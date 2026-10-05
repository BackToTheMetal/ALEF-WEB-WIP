package java.awt;

import java.awt.image.ColorModel;
import java.awt.image.Raster;

public interface PaintContext {
    void dispose();

    ColorModel getColorModel();

    Raster getRaster(int x, int y, int width, int height);
}
