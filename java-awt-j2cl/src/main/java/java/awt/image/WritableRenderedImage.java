package java.awt.image;

import java.awt.Point;

public interface WritableRenderedImage extends RenderedImage {
    void addTileObserver(TileObserver observer);

    void removeTileObserver(TileObserver observer);

    WritableRaster getWritableTile(int tileX, int tileY);

    void releaseWritableTile(int tileX, int tileY);

    boolean isTileWritable(int tileX, int tileY);

    Point[] getWritableTileIndices();

    boolean hasTileWriters();

    void setData(Raster source);
}
