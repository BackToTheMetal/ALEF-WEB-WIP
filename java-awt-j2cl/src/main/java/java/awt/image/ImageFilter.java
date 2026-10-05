package java.awt.image;

public class ImageFilter implements ImageConsumer, Cloneable {
    protected ImageConsumer consumer;

    public ImageFilter() {
    }

    public ImageFilter getFilterInstance(ImageConsumer consumer) {
        return new BoundImageFilter(this, consumer);
    }

    @Override
    public void setDimensions(int width, int height) {
        consumer.setDimensions(width, height);
    }

    @Override
    public void setColorModel(ColorModel model) {
        consumer.setColorModel(model);
    }

    @Override
    public void setHints(int hints) {
        consumer.setHints(hints);
    }

    @Override
    public void setPixels(int x, int y, int width, int height, ColorModel model, byte[] pixels, int offset, int scanlineStride) {
        consumer.setPixels(x, y, width, height, model, pixels, offset, scanlineStride);
    }

    @Override
    public void setPixels(int x, int y, int width, int height, ColorModel model, int[] pixels, int offset, int scanlineStride) {
        consumer.setPixels(x, y, width, height, model, pixels, offset, scanlineStride);
    }

    @Override
    public void imageComplete(int status) {
        consumer.imageComplete(status);
    }

    public void resendTopDownLeftRight(ImageProducer producer) {
        producer.requestTopDownLeftRightResend(this);
    }

    public Object clone() {
        return new ImageFilter();
    }

    private static final class BoundImageFilter extends ImageFilter {
        private final ImageFilter delegate;

        private BoundImageFilter(ImageFilter delegate, ImageConsumer consumer) {
            this.delegate = delegate;
            this.consumer = consumer;
        }

        @Override
        public void setDimensions(int width, int height) {
            ImageConsumer previous = delegate.consumer;
            delegate.consumer = consumer;
            try {
                delegate.setDimensions(width, height);
            } finally {
                delegate.consumer = previous;
            }
        }

        @Override
        public void setColorModel(ColorModel model) {
            ImageConsumer previous = delegate.consumer;
            delegate.consumer = consumer;
            try {
                delegate.setColorModel(model);
            } finally {
                delegate.consumer = previous;
            }
        }

        @Override
        public void setHints(int hints) {
            ImageConsumer previous = delegate.consumer;
            delegate.consumer = consumer;
            try {
                delegate.setHints(hints);
            } finally {
                delegate.consumer = previous;
            }
        }

        @Override
        public void setPixels(int x, int y, int width, int height, ColorModel model, byte[] pixels, int offset, int scanlineStride) {
            ImageConsumer previous = delegate.consumer;
            delegate.consumer = consumer;
            try {
                delegate.setPixels(x, y, width, height, model, pixels, offset, scanlineStride);
            } finally {
                delegate.consumer = previous;
            }
        }

        @Override
        public void setPixels(int x, int y, int width, int height, ColorModel model, int[] pixels, int offset, int scanlineStride) {
            ImageConsumer previous = delegate.consumer;
            delegate.consumer = consumer;
            try {
                delegate.setPixels(x, y, width, height, model, pixels, offset, scanlineStride);
            } finally {
                delegate.consumer = previous;
            }
        }

        @Override
        public void imageComplete(int status) {
            ImageConsumer previous = delegate.consumer;
            delegate.consumer = consumer;
            try {
                delegate.imageComplete(status);
            } finally {
                delegate.consumer = previous;
            }
        }

        @Override
        public void resendTopDownLeftRight(ImageProducer producer) {
            producer.requestTopDownLeftRightResend(this);
        }
    }
}
