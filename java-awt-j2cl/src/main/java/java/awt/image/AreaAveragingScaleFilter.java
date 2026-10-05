package java.awt.image;

public class AreaAveragingScaleFilter extends ReplicateScaleFilter {
    private static final int NEEDED_HINTS = TOPDOWNLEFTRIGHT | COMPLETESCANLINES;
    private static final ColorModel RGB_MODEL = ColorModel.getRGBdefault();
    private float[] alphas;
    private float[] blues;
    private float[] greens;
    private boolean passthrough;
    private float[] reds;
    private final int requestedHeight;
    private final int requestedWidth;
    private int savedDestinationRow;
    private int savedDestinationRemainder;

    public AreaAveragingScaleFilter(int width, int height) {
        super(width, height);
        requestedWidth = width;
        requestedHeight = height;
    }

    @Override
    public ImageFilter getFilterInstance(ImageConsumer consumer) {
        AreaAveragingScaleFilter filter = new AreaAveragingScaleFilter(requestedWidth, requestedHeight);
        filter.consumer = consumer;
        return filter;
    }

    @Override
    public void setHints(int hints) {
        passthrough = (hints & NEEDED_HINTS) != NEEDED_HINTS;
        super.setHints(hints);
    }

    @Override
    public void setPixels(int x, int y, int width, int height, ColorModel model, byte[] pixels, int offset, int scanlineStride) {
        if (passthrough) {
            super.setPixels(x, y, width, height, model, pixels, offset, scanlineStride);
        } else {
            accumulatePixels(y, width, height, model, pixels, offset, scanlineStride);
        }
    }

    @Override
    public void setPixels(int x, int y, int width, int height, ColorModel model, int[] pixels, int offset, int scanlineStride) {
        if (passthrough) {
            super.setPixels(x, y, width, height, model, pixels, offset, scanlineStride);
        } else {
            accumulatePixels(y, width, height, model, pixels, offset, scanlineStride);
        }
    }

    private void accumulatePixels(int y, int width, int height, ColorModel model, Object pixels, int offset, int scanlineStride) {
        ensureAccumulationBuffers();
        int sourceY = y;
        int sourceYRemaining = destHeight;
        int destinationY = sourceY == 0 ? 0 : savedDestinationRow;
        int destinationYRemaining = sourceY == 0 ? 0 : savedDestinationRemainder;
        while (sourceY < y + height) {
            if (destinationYRemaining == 0) {
                clearAccumulationBuffers();
                destinationYRemaining = srcHeight;
            }
            int amountY = Math.min(sourceYRemaining, destinationYRemaining);
            accumulateRow(width, model, pixels, offset, amountY);
            destinationYRemaining -= amountY;
            if (destinationYRemaining == 0) {
                int[] output = calculateRow();
                do {
                    consumer.setPixels(0, destinationY, destWidth, 1, RGB_MODEL, output, 0, destWidth);
                    destinationY++;
                } while ((sourceYRemaining -= amountY) >= amountY && amountY == srcHeight);
            } else {
                sourceYRemaining -= amountY;
            }
            if (sourceYRemaining == 0) {
                sourceYRemaining = destHeight;
                sourceY++;
                offset += scanlineStride;
            }
        }
        savedDestinationRemainder = destinationYRemaining;
        savedDestinationRow = destinationY;
    }

    private void accumulateRow(int width, ColorModel model, Object pixels, int offset, int amountY) {
        int sourceX = 0;
        int destinationX = 0;
        int sourceXRemaining = 0;
        int destinationXRemaining = srcWidth;
        float alpha = 0.0f;
        float red = 0.0f;
        float green = 0.0f;
        float blue = 0.0f;
        while (sourceX < width) {
            if (sourceXRemaining == 0) {
                sourceXRemaining = destWidth;
                int pixel = pixels instanceof byte[] ? ((byte[]) pixels)[offset + sourceX] & 0xff : ((int[]) pixels)[offset + sourceX];
                int rgb = model.getRGB(pixel);
                alpha = rgb >>> 24;
                red = rgb >> 16 & 0xff;
                green = rgb >> 8 & 0xff;
                blue = rgb & 0xff;
                if (alpha != 255.0f) {
                    float alphaScale = alpha / 255.0f;
                    red *= alphaScale;
                    green *= alphaScale;
                    blue *= alphaScale;
                }
            }
            int amountX = Math.min(sourceXRemaining, destinationXRemaining);
            float multiplier = (float) amountX * amountY;
            alphas[destinationX] += multiplier * alpha;
            reds[destinationX] += multiplier * red;
            greens[destinationX] += multiplier * green;
            blues[destinationX] += multiplier * blue;
            sourceXRemaining -= amountX;
            destinationXRemaining -= amountX;
            if (sourceXRemaining == 0) {
                sourceX++;
            }
            if (destinationXRemaining == 0) {
                destinationX++;
                destinationXRemaining = srcWidth;
            }
        }
    }

    private int[] calculateRow() {
        float originalMultiplier = (float) srcWidth * srcHeight;
        int[] output = outpixbuf instanceof int[] ? (int[]) outpixbuf : new int[destWidth];
        outpixbuf = output;
        for (int x = 0; x < destWidth; x++) {
            float multiplier = originalMultiplier;
            int alpha = clamp(Math.round(alphas[x] / multiplier));
            if (alpha > 0 && alpha < 255) {
                multiplier = alphas[x] / 255.0f;
            }
            int red = clamp(Math.round(reds[x] / multiplier));
            int green = clamp(Math.round(greens[x] / multiplier));
            int blue = clamp(Math.round(blues[x] / multiplier));
            output[x] = alpha << 24 | red << 16 | green << 8 | blue;
        }
        return output;
    }

    private void ensureAccumulationBuffers() {
        if (reds == null) {
            reds = new float[destWidth];
            greens = new float[destWidth];
            blues = new float[destWidth];
            alphas = new float[destWidth];
        }
    }

    private void clearAccumulationBuffers() {
        for (int index = 0; index < destWidth; index++) {
            reds[index] = 0.0f;
            greens[index] = 0.0f;
            blues[index] = 0.0f;
            alphas[index] = 0.0f;
        }
    }

    private static int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }
}
