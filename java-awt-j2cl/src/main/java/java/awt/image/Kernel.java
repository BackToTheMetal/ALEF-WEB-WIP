package java.awt.image;

public class Kernel implements Cloneable {
    private final float[] data;
    private final int height;
    private final int width;
    private final int xOrigin;
    private final int yOrigin;

    public Kernel(int width, int height, float[] data) {
        this.width = width;
        this.height = height;
        xOrigin = width - 1 >> 1;
        yOrigin = height - 1 >> 1;
        int length = width * height;
        if (data.length < length) {
            throw new IllegalArgumentException("Data array too small (is " + data.length + " and should be " + length);
        }
        this.data = new float[length];
        System.arraycopy(data, 0, this.data, 0, length);
    }

    public final int getXOrigin() {
        return xOrigin;
    }

    public final int getYOrigin() {
        return yOrigin;
    }

    public final int getWidth() {
        return width;
    }

    public final int getHeight() {
        return height;
    }

    public final float[] getKernelData(float[] output) {
        float[] result = output == null ? new float[data.length] : output;
        if (result.length < data.length) {
            throw new IllegalArgumentException("Data array too small (should be " + data.length + " but is " + result.length + " )");
        }
        System.arraycopy(data, 0, result, 0, data.length);
        return result;
    }

    public Object clone() {
        return new Kernel(width, height, data);
    }
}
