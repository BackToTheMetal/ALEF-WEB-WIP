package java.awt.image.renderable;

import java.awt.image.ColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.util.Vector;

public class RenderableImageProducer implements ImageProducer, Runnable {
    private final Vector<ImageConsumer> consumers = new Vector<ImageConsumer>();
    private final RenderableImage image;
    private RenderContext renderContext;

    public RenderableImageProducer(RenderableImage image, RenderContext renderContext) {
        this.image = image;
        this.renderContext = renderContext;
    }

    public synchronized void setRenderContext(RenderContext renderContext) {
        this.renderContext = renderContext;
    }

    @Override
    public synchronized void addConsumer(ImageConsumer consumer) {
        if (!consumers.contains(consumer)) {
            consumers.addElement(consumer);
        }
    }

    @Override
    public synchronized boolean isConsumer(ImageConsumer consumer) {
        return consumers.contains(consumer);
    }

    @Override
    public synchronized void removeConsumer(ImageConsumer consumer) {
        consumers.removeElement(consumer);
    }

    @Override
    public synchronized void startProduction(ImageConsumer consumer) {
        addConsumer(consumer);
        run();
    }

    @Override
    public void requestTopDownLeftRightResend(ImageConsumer consumer) {
    }

    @Override
    public void run() {
        RenderedImage renderedImage = renderContext == null ? image.createDefaultRendering() : image.createRendering(renderContext);
        if (renderedImage == null) {
            complete(ImageConsumer.IMAGEERROR);
            return;
        }
        ColorModel colorModel = renderedImage.getColorModel();
        if (colorModel == null) {
            colorModel = ColorModel.getRGBdefault();
        }
        Raster raster = renderedImage.getData();
        int width = raster.getWidth();
        int height = raster.getHeight();
        Vector<ImageConsumer> snapshot = snapshotConsumers();
        int hints = ImageConsumer.TOPDOWNLEFTRIGHT | ImageConsumer.COMPLETESCANLINES | ImageConsumer.SINGLEPASS | ImageConsumer.SINGLEFRAME;
        for (ImageConsumer consumer : snapshot) {
            consumer.setDimensions(width, height);
            consumer.setHints(hints);
        }
        int[] scanline = new int[width];
        for (int row = 0; row < height; row++) {
            int y = raster.getMinY() + row;
            for (int column = 0; column < width; column++) {
                scanline[column] = colorModel.getRGB(raster.getDataElements(raster.getMinX() + column, y, null));
            }
            for (ImageConsumer consumer : snapshot) {
                consumer.setPixels(0, row, width, 1, ColorModel.getRGBdefault(), scanline, 0, width);
            }
        }
        for (ImageConsumer consumer : snapshot) {
            consumer.imageComplete(ImageConsumer.STATICIMAGEDONE);
        }
    }

    private synchronized Vector<ImageConsumer> snapshotConsumers() {
        return new Vector<ImageConsumer>(consumers);
    }

    private void complete(int status) {
        for (ImageConsumer consumer : snapshotConsumers()) {
            consumer.imageComplete(status);
        }
    }
}
