package java.awt.image;

import java.util.HashMap;
import java.util.Map;

public class FilteredImageSource implements ImageProducer {
    private final ImageProducer source;
    private final ImageFilter filter;
    private final Map<ImageConsumer, ImageFilter> consumers = new HashMap<ImageConsumer, ImageFilter>();

    public FilteredImageSource(ImageProducer source, ImageFilter filter) {
        if (source == null || filter == null) {
            throw new NullPointerException("Source and filter must not be null");
        }
        this.source = source;
        this.filter = filter;
    }

    @Override
    public void addConsumer(ImageConsumer consumer) {
        if (consumers.containsKey(consumer)) {
            return;
        }
        ImageFilter instance = filter.getFilterInstance(consumer);
        consumers.put(consumer, instance);
        source.addConsumer(instance);
    }

    @Override
    public boolean isConsumer(ImageConsumer consumer) {
        return consumers.containsKey(consumer);
    }

    @Override
    public void removeConsumer(ImageConsumer consumer) {
        ImageFilter instance = consumers.remove(consumer);
        if (instance != null) {
            source.removeConsumer(instance);
        }
    }

    @Override
    public void startProduction(ImageConsumer consumer) {
        ImageFilter instance = consumers.get(consumer);
        if (instance == null) {
            instance = filter.getFilterInstance(consumer);
            consumers.put(consumer, instance);
        }
        source.startProduction(instance);
    }

    @Override
    public void requestTopDownLeftRightResend(ImageConsumer consumer) {
        ImageFilter instance = consumers.get(consumer);
        if (instance != null) {
            instance.resendTopDownLeftRight(source);
        }
    }
}
