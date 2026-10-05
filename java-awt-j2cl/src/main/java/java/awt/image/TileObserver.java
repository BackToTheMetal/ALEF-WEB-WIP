package java.awt.image;

public interface TileObserver {
    void tileUpdate(WritableRenderedImage source, int tileX, int tileY, boolean willBeWritable);
}
