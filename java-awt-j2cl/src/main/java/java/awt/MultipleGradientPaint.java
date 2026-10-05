package java.awt;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Arrays;

public abstract class MultipleGradientPaint implements Paint {
    public enum CycleMethod {
        NO_CYCLE,
        REFLECT,
        REPEAT
    }

    public enum ColorSpaceType {
        SRGB,
        LINEAR_RGB
    }

    private final float[] fractions;
    private final Color[] colors;
    private final CycleMethod cycleMethod;
    private final ColorSpaceType colorSpace;
    private final AffineTransform gradientTransform;
    private final int transparency;

    MultipleGradientPaint(float[] fractions, Color[] colors, CycleMethod cycleMethod, ColorSpaceType colorSpace, AffineTransform gradientTransform) {
        if (fractions == null || colors == null) {
            throw new NullPointerException("Fractions and colors must be non-null");
        }
        if (cycleMethod == null) {
            throw new NullPointerException("Cycle method cannot be null");
        }
        if (colorSpace == null) {
            throw new NullPointerException("Color space cannot be null");
        }
        if (gradientTransform == null) {
            throw new NullPointerException("Gradient transform cannot be null");
        }
        if (colors.length < 2) {
            throw new IllegalArgumentException("User must specify at least 2 colors");
        }
        if (fractions.length != colors.length) {
            throw new IllegalArgumentException("Colors and fractions must have equal size");
        }
        validateFractions(fractions);
        for (Color color : colors) {
            if (color == null) {
                throw new NullPointerException("Colors must be non-null");
            }
        }
        boolean prepend = fractions[0] != 0.0f;
        boolean append = fractions[fractions.length - 1] != 1.0f;
        int extra = (prepend ? 1 : 0) + (append ? 1 : 0);
        this.fractions = new float[fractions.length + extra];
        this.colors = new Color[colors.length + extra];
        int offset = prepend ? 1 : 0;
        System.arraycopy(fractions, 0, this.fractions, offset, fractions.length);
        System.arraycopy(colors, 0, this.colors, offset, colors.length);
        if (prepend) {
            this.fractions[0] = 0.0f;
            this.colors[0] = colors[0];
        }
        if (append) {
            this.fractions[this.fractions.length - 1] = 1.0f;
            this.colors[this.colors.length - 1] = colors[colors.length - 1];
        }
        this.cycleMethod = cycleMethod;
        this.colorSpace = colorSpace;
        this.gradientTransform = new AffineTransform(gradientTransform);
        transparency = determineTransparency(this.colors);
    }

    public final float[] getFractions() {
        return Arrays.copyOf(fractions, fractions.length);
    }

    public final Color[] getColors() {
        return Arrays.copyOf(colors, colors.length);
    }

    public final CycleMethod getCycleMethod() {
        return cycleMethod;
    }

    public final ColorSpaceType getColorSpace() {
        return colorSpace;
    }

    public final AffineTransform getTransform() {
        return new AffineTransform(gradientTransform);
    }

    @Override
    public final int getTransparency() {
        return transparency;
    }

    final GradientRasterContext createLinearContext(AffineTransform deviceTransform, double startX, double startY, double endX, double endY) {
        return new GradientRasterContext(fractions, colors, cycleMethod, colorSpace, combinedTransform(deviceTransform), GradientGeometry.linear(startX, startY, endX, endY));
    }

    final GradientRasterContext createRadialContext(AffineTransform deviceTransform, double centerX, double centerY, double focusX, double focusY, double radius) {
        return new GradientRasterContext(fractions, colors, cycleMethod, colorSpace, combinedTransform(deviceTransform), GradientGeometry.radial(centerX, centerY, focusX, focusY, radius));
    }

    static PaintContext createSimpleLinearContext(Color first, Color second, boolean cyclic, AffineTransform transform, Point2D start, Point2D end) {
        float[] fractions = {0.0f, 1.0f};
        Color[] colors = {first, second};
        CycleMethod cycleMethod = cyclic ? CycleMethod.REFLECT : CycleMethod.NO_CYCLE;
        return new GradientRasterContext(fractions, colors, cycleMethod, ColorSpaceType.SRGB, transform, GradientGeometry.linear(start.getX(), start.getY(), end.getX(), end.getY()));
    }

    private AffineTransform combinedTransform(AffineTransform deviceTransform) {
        if (deviceTransform == null) {
            throw new NullPointerException("Transform cannot be null");
        }
        AffineTransform combined = new AffineTransform(deviceTransform);
        combined.concatenate(gradientTransform);
        return combined;
    }

    private static void validateFractions(float[] fractions) {
        float previous = -1.0f;
        for (float fraction : fractions) {
            if (fraction < 0.0f || fraction > 1.0f) {
                throw new IllegalArgumentException("Fraction values must be in the range 0 to 1: " + fraction);
            }
            if (fraction <= previous) {
                throw new IllegalArgumentException("Keyframe fractions must be increasing: " + fraction);
            }
            previous = fraction;
        }
    }

    private static int determineTransparency(Color[] colors) {
        for (Color color : colors) {
            if (color.getAlpha() != 255) {
                return TRANSLUCENT;
            }
        }
        return OPAQUE;
    }
}

final class GradientRasterContext implements PaintContext {
    private static final double FOCUS_SCALE = 0.99f;
    private final float[] fractions;
    private final int[] colors;
    private final MultipleGradientPaint.CycleMethod cycleMethod;
    private final MultipleGradientPaint.ColorSpaceType colorSpace;
    private final DeviceToGradientMapper mapper;
    private final boolean radial;
    private final double firstX;
    private final double firstY;
    private final double secondX;
    private final double secondY;
    private final double radius;
    private final double linearDenominator;
    private final ColorModel colorModel = ColorModel.getRGBdefault();

    GradientRasterContext(float[] fractions, Color[] colors, MultipleGradientPaint.CycleMethod cycleMethod, MultipleGradientPaint.ColorSpaceType colorSpace, AffineTransform transform, GradientGeometry geometry) {
        this.fractions = Arrays.copyOf(fractions, fractions.length);
        this.colors = new int[colors.length];
        for (int index = 0; index < colors.length; index++) {
            this.colors[index] = colors[index].getRGB();
        }
        this.cycleMethod = cycleMethod;
        this.colorSpace = colorSpace;
        mapper = new DeviceToGradientMapper(transform);
        radial = geometry.radial;
        firstX = geometry.firstX;
        firstY = geometry.firstY;
        if (radial) {
            double focusX = geometry.secondX;
            double focusY = geometry.secondY;
            double offsetX = focusX - firstX;
            double offsetY = focusY - firstY;
            double distance = Math.sqrt(offsetX * offsetX + offsetY * offsetY);
            double maximum = geometry.radius * FOCUS_SCALE;
            if (distance > maximum) {
                focusX = firstX + offsetX * maximum / distance;
                focusY = firstY + offsetY * maximum / distance;
            }
            this.secondX = focusX;
            this.secondY = focusY;
        } else {
            secondX = geometry.secondX;
            secondY = geometry.secondY;
        }
        radius = geometry.radius;
        double deltaX = secondX - firstX;
        double deltaY = secondY - firstY;
        linearDenominator = deltaX * deltaX + deltaY * deltaY;
    }

    @Override
    public void dispose() {
        // No resources are retained by this context.
    }

    @Override
    public ColorModel getColorModel() {
        return colorModel;
    }

    @Override
    public Raster getRaster(int x, int y, int width, int height) {
        WritableRaster raster = colorModel.createCompatibleWritableRaster(width, height);
        int[] pixels = new int[width * height];
        int index = 0;
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                double gradientX = mapper.x(x + column, y + row);
                double gradientY = mapper.y(x + column, y + row);
                double position = radial ? radialPosition(gradientX, gradientY) : linearPosition(gradientX, gradientY);
                pixels[index++] = colorAt(cycle(position));
            }
        }
        raster.setDataElements(0, 0, width, height, pixels);
        return raster;
    }

    private double linearPosition(double x, double y) {
        return ((x - firstX) * (secondX - firstX) + (y - firstY) * (secondY - firstY)) / linearDenominator;
    }

    private double radialPosition(double x, double y) {
        double directionX = x - secondX;
        double directionY = y - secondY;
        double directionLength = directionX * directionX + directionY * directionY;
        if (directionLength == 0.0) {
            return 0.0;
        }
        double focusOffsetX = secondX - firstX;
        double focusOffsetY = secondY - firstY;
        double projection = focusOffsetX * directionX + focusOffsetY * directionY;
        double discriminant = projection * projection - directionLength * (focusOffsetX * focusOffsetX + focusOffsetY * focusOffsetY - radius * radius);
        double intersection = (-projection + Math.sqrt(Math.max(0.0, discriminant))) / directionLength;
        return intersection <= 0.0 ? 0.0 : 1.0 / intersection;
    }

    private double cycle(double position) {
        if (java.lang.Double.isNaN(position)) {
            return 0.0;
        }
        if (cycleMethod == MultipleGradientPaint.CycleMethod.NO_CYCLE) {
            return Math.max(0.0, Math.min(1.0, position));
        }
        if (cycleMethod == MultipleGradientPaint.CycleMethod.REPEAT) {
            return position - Math.floor(position);
        }
        double reflected = position - Math.floor(position / 2.0) * 2.0;
        return reflected <= 1.0 ? reflected : 2.0 - reflected;
    }

    private int colorAt(double position) {
        int segment = fractions.length - 2;
        for (int index = 0; index < fractions.length - 1; index++) {
            if (position <= fractions[index + 1]) {
                segment = index;
                break;
            }
        }
        double interval = fractions[segment + 1] - fractions[segment];
        double amount = interval == 0.0 ? 0.0 : (position - fractions[segment]) / interval;
        return interpolate(colors[segment], colors[segment + 1], Math.max(0.0, Math.min(1.0, amount)));
    }

    private int interpolate(int first, int second, double amount) {
        int alpha = interpolateByte(first >>> 24, second >>> 24, amount);
        int red = interpolateColorByte(first >> 16 & 0xff, second >> 16 & 0xff, amount);
        int green = interpolateColorByte(first >> 8 & 0xff, second >> 8 & 0xff, amount);
        int blue = interpolateColorByte(first & 0xff, second & 0xff, amount);
        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    private int interpolateColorByte(int first, int second, double amount) {
        if (colorSpace == MultipleGradientPaint.ColorSpaceType.SRGB) {
            return interpolateByte(first, second, amount);
        }
        double linear = toLinear(first / 255.0) * (1.0 - amount) + toLinear(second / 255.0) * amount;
        return byteValue(toSrgb(linear));
    }

    private static int interpolateByte(int first, int second, double amount) {
        return Math.max(0, Math.min(255, (int) (first * (1.0 - amount) + second * amount + 0.5)));
    }

    private static double toLinear(double value) {
        return value <= 0.04045 ? value / 12.92 : Math.pow((value + 0.055) / 1.055, 2.4);
    }

    private static double toSrgb(double value) {
        return value <= 0.0031308 ? value * 12.92 : 1.055 * Math.pow(value, 1.0 / 2.4) - 0.055;
    }

    private static int byteValue(double value) {
        return Math.max(0, Math.min(255, (int) (value * 255.0 + 0.5)));
    }
}

final class GradientGeometry {
    final boolean radial;
    final double firstX;
    final double firstY;
    final double secondX;
    final double secondY;
    final double radius;

    private GradientGeometry(boolean radial, double firstX, double firstY, double secondX, double secondY, double radius) {
        this.radial = radial;
        this.firstX = firstX;
        this.firstY = firstY;
        this.secondX = secondX;
        this.secondY = secondY;
        this.radius = radius;
    }

    static GradientGeometry linear(double startX, double startY, double endX, double endY) {
        return new GradientGeometry(false, startX, startY, endX, endY, 0.0);
    }

    static GradientGeometry radial(double centerX, double centerY, double focusX, double focusY, double radius) {
        return new GradientGeometry(true, centerX, centerY, focusX, focusY, radius);
    }
}

final class DeviceToGradientMapper {
    private final double m00;
    private final double m01;
    private final double m02;
    private final double m10;
    private final double m11;
    private final double m12;

    DeviceToGradientMapper(AffineTransform transform) {
        double[] matrix = new double[6];
        transform.getMatrix(matrix);
        double determinant = matrix[0] * matrix[3] - matrix[2] * matrix[1];
        if (Math.abs(determinant) <= java.lang.Double.MIN_VALUE) {
            m00 = 0.0;
            m01 = 0.0;
            m02 = 0.0;
            m10 = 0.0;
            m11 = 0.0;
            m12 = 0.0;
            return;
        }
        m00 = matrix[3] / determinant;
        m01 = -matrix[2] / determinant;
        m02 = (matrix[2] * matrix[5] - matrix[3] * matrix[4]) / determinant;
        m10 = -matrix[1] / determinant;
        m11 = matrix[0] / determinant;
        m12 = (matrix[1] * matrix[4] - matrix[0] * matrix[5]) / determinant;
    }

    double x(double x, double y) {
        return m00 * x + m01 * y + m02;
    }

    double y(double x, double y) {
        return m10 * x + m11 * y + m12;
    }
}
