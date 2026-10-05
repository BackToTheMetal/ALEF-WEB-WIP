package java.awt.image;

public interface ImageConsumer {
    int RANDOMPIXELORDER = 1;
    int TOPDOWNLEFTRIGHT = 2;
    int COMPLETESCANLINES = 4;
    int SINGLEPASS = 8;
    int SINGLEFRAME = 16;
    int IMAGEERROR = 1;
    int SINGLEFRAMEDONE = 2;
    int STATICIMAGEDONE = 3;
    int IMAGEABORTED = 4;

    void setDimensions(int width, int height);

    void setColorModel(ColorModel model);

    void setHints(int hints);

    void setPixels(int x, int y, int width, int height, ColorModel model, byte[] pixels, int offset, int scanlineStride);

    void setPixels(int x, int y, int width, int height, ColorModel model, int[] pixels, int offset, int scanlineStride);

    void imageComplete(int status);
}
