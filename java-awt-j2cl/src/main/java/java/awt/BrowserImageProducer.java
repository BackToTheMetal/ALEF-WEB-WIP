package java.awt;

import java.awt.image.ColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;
import java.util.ArrayList;
import java.util.List;
import org.webawt.internal.peer.ImageDecodeCallback;
import org.webawt.internal.peer.WebAwtRuntime;

final class BrowserImageProducer implements ImageProducer, ImageDecodeCallback {
    private static final int HINTS = ImageConsumer.TOPDOWNLEFTRIGHT | ImageConsumer.COMPLETESCANLINES | ImageConsumer.SINGLEPASS | ImageConsumer.SINGLEFRAME;
    private final List<ImageConsumer> consumers = new ArrayList<ImageConsumer>();
    private final String source;
    private int completionStatus;
    private int height;
    private int[] pixels;
    private boolean started;
    private int width;

    BrowserImageProducer(String source) {
        if (source == null) {
            throw new NullPointerException("source");
        }
        this.source = source;
    }

    @Override
    public void addConsumer(ImageConsumer consumer) {
        if (consumer == null) {
            throw new NullPointerException("consumer");
        }
        if (!consumers.contains(consumer)) {
            consumers.add(consumer);
        }
        if (completionStatus != 0) {
            deliver(consumer);
        }
    }

    @Override
    public boolean isConsumer(ImageConsumer consumer) {
        return consumers.contains(consumer);
    }

    @Override
    public void removeConsumer(ImageConsumer consumer) {
        consumers.remove(consumer);
    }

    @Override
    public void startProduction(ImageConsumer consumer) {
        addConsumer(consumer);
        if (!started) {
            started = true;
            WebAwtRuntime.decodeImage(source, this);
        }
    }

    @Override
    public void requestTopDownLeftRightResend(ImageConsumer consumer) {
        if (completionStatus != 0 && isConsumer(consumer)) {
            deliver(consumer);
        }
    }

    @Override
    public void onDecoded(int width, int height, int[] argbPixels) {
        if (width <= 0 || height <= 0 || argbPixels == null || argbPixels.length < width * height) {
            onError();
            return;
        }
        this.width = width;
        this.height = height;
        pixels = argbPixels;
        completionStatus = ImageConsumer.STATICIMAGEDONE;
        deliverToAll();
    }

    @Override
    public void onError() {
        completionStatus = ImageConsumer.IMAGEERROR;
        deliverToAll();
    }

    private void deliverToAll() {
        for (ImageConsumer consumer : new ArrayList<ImageConsumer>(consumers)) {
            deliver(consumer);
        }
    }

    private void deliver(ImageConsumer consumer) {
        if (completionStatus == ImageConsumer.STATICIMAGEDONE) {
            consumer.setDimensions(width, height);
            consumer.setColorModel(ColorModel.getRGBdefault());
            consumer.setHints(HINTS);
            consumer.setPixels(0, 0, width, height, ColorModel.getRGBdefault(), pixels, 0, width);
        }
        consumer.imageComplete(completionStatus);
    }
}
