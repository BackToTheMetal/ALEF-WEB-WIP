package java.awt;

import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public final class AlphaComposite implements Composite {
    public static final int CLEAR = 1;
    public static final int SRC = 2;
    public static final int DST = 9;
    public static final int SRC_OVER = 3;
    public static final int DST_OVER = 4;
    public static final int SRC_IN = 5;
    public static final int DST_IN = 6;
    public static final int SRC_OUT = 7;
    public static final int DST_OUT = 8;
    public static final int SRC_ATOP = 10;
    public static final int DST_ATOP = 11;
    public static final int XOR = 12;
    public static final AlphaComposite Clear = new AlphaComposite(CLEAR, 1.0f);
    public static final AlphaComposite Src = new AlphaComposite(SRC, 1.0f);
    public static final AlphaComposite Dst = new AlphaComposite(DST, 1.0f);
    public static final AlphaComposite SrcOver = new AlphaComposite(SRC_OVER, 1.0f);
    public static final AlphaComposite DstOver = new AlphaComposite(DST_OVER, 1.0f);
    public static final AlphaComposite SrcIn = new AlphaComposite(SRC_IN, 1.0f);
    public static final AlphaComposite DstIn = new AlphaComposite(DST_IN, 1.0f);
    public static final AlphaComposite SrcOut = new AlphaComposite(SRC_OUT, 1.0f);
    public static final AlphaComposite DstOut = new AlphaComposite(DST_OUT, 1.0f);
    public static final AlphaComposite SrcAtop = new AlphaComposite(SRC_ATOP, 1.0f);
    public static final AlphaComposite DstAtop = new AlphaComposite(DST_ATOP, 1.0f);
    public static final AlphaComposite Xor = new AlphaComposite(XOR, 1.0f);
    private final int rule;
    private final float alpha;

    private AlphaComposite(int rule, float alpha) {
        this.rule = rule;
        this.alpha = alpha;
    }

    public static AlphaComposite getInstance(int rule) {
        validateRule(rule);
        return singleton(rule);
    }

    public static AlphaComposite getInstance(int rule, float alpha) {
        validateRule(rule);
        validateAlpha(alpha);
        return alpha == 1.0f ? singleton(rule) : new AlphaComposite(rule, alpha);
    }

    @Override
    public CompositeContext createContext(ColorModel sourceColorModel, ColorModel destinationColorModel, RenderingHints hints) {
        if (sourceColorModel == null || destinationColorModel == null) {
            throw new NullPointerException("Color models must not be null");
        }
        return new AlphaCompositeContext(rule, alpha, sourceColorModel, destinationColorModel);
    }

    public float getAlpha() {
        return alpha;
    }

    public int getRule() {
        return rule;
    }

    public AlphaComposite derive(int rule) {
        return this.rule == rule ? this : getInstance(rule, alpha);
    }

    public AlphaComposite derive(float alpha) {
        return this.alpha == alpha ? this : getInstance(rule, alpha);
    }

    @Override
    public int hashCode() {
        return Float.floatToIntBits(alpha) * 31 + rule;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof AlphaComposite)) {
            return false;
        }
        AlphaComposite composite = (AlphaComposite) object;
        return rule == composite.rule && alpha == composite.alpha;
    }

    private static AlphaComposite singleton(int rule) {
        switch (rule) {
            case CLEAR:
                return Clear;
            case SRC:
                return Src;
            case DST:
                return Dst;
            case SRC_OVER:
                return SrcOver;
            case DST_OVER:
                return DstOver;
            case SRC_IN:
                return SrcIn;
            case DST_IN:
                return DstIn;
            case SRC_OUT:
                return SrcOut;
            case DST_OUT:
                return DstOut;
            case SRC_ATOP:
                return SrcAtop;
            case DST_ATOP:
                return DstAtop;
            case XOR:
                return Xor;
            default:
                throw new IllegalArgumentException("unknown composite rule");
        }
    }

    private static void validateRule(int rule) {
        if (rule < CLEAR || rule > XOR) {
            throw new IllegalArgumentException("unknown composite rule");
        }
    }

    private static void validateAlpha(float alpha) {
        if (!(alpha >= 0.0f && alpha <= 1.0f)) {
            throw new IllegalArgumentException("alpha value out of range");
        }
    }

    private static final class AlphaCompositeContext implements CompositeContext {
        private final int rule;
        private final double globalAlpha;
        private final ColorModel sourceColorModel;
        private final ColorModel destinationColorModel;

        private AlphaCompositeContext(int rule, float alpha, ColorModel sourceColorModel, ColorModel destinationColorModel) {
            this.rule = rule;
            globalAlpha = alpha;
            this.sourceColorModel = sourceColorModel;
            this.destinationColorModel = destinationColorModel;
        }

        @Override
        public void dispose() {
            // No resources are retained by this context.
        }

        @Override
        public void compose(Raster source, Raster destinationInput, WritableRaster destinationOutput) {
            if (source == null || destinationInput == null || destinationOutput == null) {
                throw new NullPointerException("Rasters must not be null");
            }
            int width = Math.min(source.getWidth(), Math.min(destinationInput.getWidth(), destinationOutput.getWidth()));
            int height = Math.min(source.getHeight(), Math.min(destinationInput.getHeight(), destinationOutput.getHeight()));
            if (rule == DST) {
                Object data = destinationInput.getDataElements(destinationInput.getMinX(), destinationInput.getMinY(), width, height, null);
                destinationOutput.setDataElements(destinationOutput.getMinX(), destinationOutput.getMinY(), width, height, data);
                return;
            }
            int[] output = new int[width * height];
            int index = 0;
            for (int row = 0; row < height; row++) {
                for (int column = 0; column < width; column++) {
                    int sourceArgb = sourceColorModel.getRGB(source.getDataElements(source.getMinX() + column, source.getMinY() + row, null));
                    int destinationArgb = destinationColorModel.getRGB(destinationInput.getDataElements(destinationInput.getMinX() + column, destinationInput.getMinY() + row, null));
                    output[index++] = composePixel(sourceArgb, destinationArgb);
                }
            }
            index = 0;
            for (int row = 0; row < height; row++) {
                for (int column = 0; column < width; column++) {
                    Object data = destinationColorModel.getDataElements(output[index++], null);
                    destinationOutput.setDataElements(destinationOutput.getMinX() + column, destinationOutput.getMinY() + row, data);
                }
            }
        }

        private int composePixel(int source, int destination) {
            double sourceAlpha = (source >>> 24) / 255.0 * globalAlpha;
            double destinationAlpha = (destination >>> 24) / 255.0;
            double sourceFactor = sourceFactor(destinationAlpha);
            double destinationFactor = destinationFactor(sourceAlpha);
            double outputAlpha = sourceAlpha * sourceFactor + destinationAlpha * destinationFactor;
            if (outputAlpha <= 0.0) {
                return 0;
            }
            int red = component(source, destination, 16, sourceAlpha, destinationAlpha, sourceFactor, destinationFactor, outputAlpha);
            int green = component(source, destination, 8, sourceAlpha, destinationAlpha, sourceFactor, destinationFactor, outputAlpha);
            int blue = component(source, destination, 0, sourceAlpha, destinationAlpha, sourceFactor, destinationFactor, outputAlpha);
            int alpha = byteValue(outputAlpha);
            return alpha << 24 | red << 16 | green << 8 | blue;
        }

        private int component(int source, int destination, int shift, double sourceAlpha, double destinationAlpha, double sourceFactor, double destinationFactor, double outputAlpha) {
            double sourceComponent = (source >>> shift & 0xff) / 255.0;
            double destinationComponent = (destination >>> shift & 0xff) / 255.0;
            double premultiplied = sourceComponent * sourceAlpha * sourceFactor + destinationComponent * destinationAlpha * destinationFactor;
            return byteValue(premultiplied / outputAlpha);
        }

        private double sourceFactor(double destinationAlpha) {
            switch (rule) {
                case SRC:
                case SRC_OVER:
                case SRC_OUT:
                case XOR:
                    return rule == SRC_OUT || rule == XOR ? 1.0 - destinationAlpha : 1.0;
                case DST_OVER:
                    return 1.0 - destinationAlpha;
                case SRC_IN:
                case SRC_ATOP:
                    return destinationAlpha;
                case DST_ATOP:
                    return 1.0 - destinationAlpha;
                default:
                    return 0.0;
            }
        }

        private double destinationFactor(double sourceAlpha) {
            switch (rule) {
                case DST:
                case DST_OVER:
                case DST_OUT:
                case XOR:
                    return rule == DST_OUT || rule == XOR ? 1.0 - sourceAlpha : 1.0;
                case SRC_OVER:
                case SRC_ATOP:
                    return 1.0 - sourceAlpha;
                case DST_IN:
                case DST_ATOP:
                    return sourceAlpha;
                default:
                    return 0.0;
            }
        }

        private static int byteValue(double value) {
            return Math.max(0, Math.min(255, (int) (value * 255.0 + 0.5)));
        }
    }
}
