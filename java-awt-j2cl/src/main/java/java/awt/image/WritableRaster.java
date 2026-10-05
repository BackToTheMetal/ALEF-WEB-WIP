package java.awt.image;

import java.awt.Point;
import java.awt.Rectangle;

public class WritableRaster extends Raster {
    protected WritableRaster(SampleModel model, Point origin) {
        super(model, origin);
    }

    protected WritableRaster(SampleModel model, DataBuffer buffer, Point origin) {
        super(model, buffer, origin);
    }

    protected WritableRaster(SampleModel model, DataBuffer buffer, Rectangle region, Point sampleModelTranslate, WritableRaster parent) {
        super(model, buffer, region, sampleModelTranslate, parent);
    }

    public WritableRaster getWritableParent() {
        return (WritableRaster) parent;
    }

    public WritableRaster createWritableTranslatedChild(int childMinX, int childMinY) {
        return createWritableChild(minX, minY, width, height, childMinX, childMinY, null);
    }

    public WritableRaster createWritableChild(int parentX, int parentY, int width, int height, int childMinX, int childMinY, int[] bands) {
        checkRegion(parentX, parentY, width, height);
        SampleModel childModel = bands == null ? sampleModel : sampleModel.createSubsetSampleModel(bands);
        int deltaX = childMinX - parentX;
        int deltaY = childMinY - parentY;
        Point translate = new Point(sampleModelTranslateX + deltaX, sampleModelTranslateY + deltaY);
        return new WritableRaster(childModel, dataBuffer, new Rectangle(childMinX, childMinY, width, height), translate, this);
    }

    public void setDataElements(int x, int y, Object values) {
        checkPoint(x, y);
        sampleModel.setDataElements(modelX(x), modelY(y), values, dataBuffer);
    }

    public void setDataElements(int x, int y, Raster source) {
        int destinationX = x + source.getMinX();
        int destinationY = y + source.getMinY();
        checkRegion(destinationX, destinationY, source.getWidth(), source.getHeight());
        Object values = source.getDataElements(source.getMinX(), source.getMinY(), source.getWidth(), source.getHeight(), null);
        sampleModel.setDataElements(modelX(destinationX), modelY(destinationY), source.getWidth(), source.getHeight(), values, dataBuffer);
    }

    public void setDataElements(int x, int y, int width, int height, Object values) {
        checkRegion(x, y, width, height);
        sampleModel.setDataElements(modelX(x), modelY(y), width, height, values, dataBuffer);
    }

    public void setRect(Raster source) {
        setRect(0, 0, source);
    }

    public void setRect(int deltaX, int deltaY, Raster source) {
        int sourceX = source.getMinX();
        int sourceY = source.getMinY();
        int destinationX = sourceX + deltaX;
        int destinationY = sourceY + deltaY;
        int copyWidth = source.getWidth();
        int copyHeight = source.getHeight();
        if (destinationX < minX) {
            int difference = minX - destinationX;
            sourceX += difference;
            copyWidth -= difference;
            destinationX = minX;
        }
        if (destinationY < minY) {
            int difference = minY - destinationY;
            sourceY += difference;
            copyHeight -= difference;
            destinationY = minY;
        }
        copyWidth = (int) Math.min(copyWidth, (long) minX + width - destinationX);
        copyHeight = (int) Math.min(copyHeight, (long) minY + height - destinationY);
        if (copyWidth <= 0 || copyHeight <= 0) {
            return;
        }
        int[] pixels = source.getPixels(sourceX, sourceY, copyWidth, copyHeight, (int[]) null);
        setPixels(destinationX, destinationY, copyWidth, copyHeight, pixels);
    }

    public void setPixel(int x, int y, int[] values) {
        checkPoint(x, y);
        sampleModel.setPixel(modelX(x), modelY(y), values, dataBuffer);
    }

    public void setPixel(int x, int y, float[] values) {
        checkPoint(x, y);
        sampleModel.setPixel(modelX(x), modelY(y), values, dataBuffer);
    }

    public void setPixel(int x, int y, double[] values) {
        checkPoint(x, y);
        sampleModel.setPixel(modelX(x), modelY(y), values, dataBuffer);
    }

    public void setPixels(int x, int y, int width, int height, int[] values) {
        checkRegion(x, y, width, height);
        sampleModel.setPixels(modelX(x), modelY(y), width, height, values, dataBuffer);
    }

    public void setPixels(int x, int y, int width, int height, float[] values) {
        checkRegion(x, y, width, height);
        sampleModel.setPixels(modelX(x), modelY(y), width, height, values, dataBuffer);
    }

    public void setPixels(int x, int y, int width, int height, double[] values) {
        checkRegion(x, y, width, height);
        sampleModel.setPixels(modelX(x), modelY(y), width, height, values, dataBuffer);
    }

    public void setSample(int x, int y, int band, int value) {
        checkPoint(x, y);
        sampleModel.setSample(modelX(x), modelY(y), band, value, dataBuffer);
    }

    public void setSample(int x, int y, int band, float value) {
        checkPoint(x, y);
        sampleModel.setSample(modelX(x), modelY(y), band, value, dataBuffer);
    }

    public void setSample(int x, int y, int band, double value) {
        checkPoint(x, y);
        sampleModel.setSample(modelX(x), modelY(y), band, value, dataBuffer);
    }

    public void setSamples(int x, int y, int width, int height, int band, int[] values) {
        checkRegion(x, y, width, height);
        sampleModel.setSamples(modelX(x), modelY(y), width, height, band, values, dataBuffer);
    }

    public void setSamples(int x, int y, int width, int height, int band, float[] values) {
        checkRegion(x, y, width, height);
        sampleModel.setSamples(modelX(x), modelY(y), width, height, band, values, dataBuffer);
    }

    public void setSamples(int x, int y, int width, int height, int band, double[] values) {
        checkRegion(x, y, width, height);
        sampleModel.setSamples(modelX(x), modelY(y), width, height, band, values, dataBuffer);
    }
}
