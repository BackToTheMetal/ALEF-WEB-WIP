package java.awt.image;

public interface ImageProducer {
    void addConsumer(ImageConsumer consumer);

    boolean isConsumer(ImageConsumer consumer);

    void removeConsumer(ImageConsumer consumer);

    void startProduction(ImageConsumer consumer);

    void requestTopDownLeftRightResend(ImageConsumer consumer);
}
