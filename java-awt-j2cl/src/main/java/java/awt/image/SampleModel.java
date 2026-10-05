package java.awt.image;

public abstract class SampleModel {
    protected int width;
    protected int height;
    protected int numBands;
    protected int dataType;

    public SampleModel(int dataType, int width, int height, int numBands) {
        DataBuffer.getDataTypeSize(dataType);
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Width and height must be greater than 0");
        }
        if (numBands <= 0) {
            throw new IllegalArgumentException("Number of bands must be greater than 0");
        }
        this.dataType = dataType;
        this.width = width;
        this.height = height;
        this.numBands = numBands;
    }

    public final int getWidth() {
        return width;
    }

    public final int getHeight() {
        return height;
    }

    public final int getNumBands() {
        return numBands;
    }

    public abstract int getNumDataElements();

    public final int getDataType() {
        return dataType;
    }

    public int getTransferType() {
        return dataType;
    }

    public int[] getPixel(int x, int y, int[] output, DataBuffer buffer) {
        int[] values = output == null ? new int[numBands] : output;
        for (int band = 0; band < numBands; band++) {
            values[band] = getSample(x, y, band, buffer);
        }
        return values;
    }

    public abstract Object getDataElements(int x, int y, Object output, DataBuffer buffer);

    public Object getDataElements(int x, int y, int width, int height, Object output, DataBuffer buffer) {
        int elements = getNumDataElements();
        Object values = output == null ? SampleModelSupport.createArray(getTransferType(), width * height * elements) : output;
        int outputIndex = 0;
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                Object pixel = getDataElements(x + column, y + row, null, buffer);
                for (int element = 0; element < elements; element++) {
                    SampleModelSupport.set(values, getTransferType(), outputIndex++, SampleModelSupport.get(pixel, getTransferType(), element));
                }
            }
        }
        return values;
    }

    public abstract void setDataElements(int x, int y, Object values, DataBuffer buffer);

    public void setDataElements(int x, int y, int width, int height, Object values, DataBuffer buffer) {
        int elements = getNumDataElements();
        Object pixel = SampleModelSupport.createArray(getTransferType(), elements);
        int inputIndex = 0;
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                for (int element = 0; element < elements; element++) {
                    SampleModelSupport.set(pixel, getTransferType(), element, SampleModelSupport.get(values, getTransferType(), inputIndex++));
                }
                setDataElements(x + column, y + row, pixel, buffer);
            }
        }
    }

    public float[] getPixel(int x, int y, float[] output, DataBuffer buffer) {
        float[] values = output == null ? new float[numBands] : output;
        for (int band = 0; band < numBands; band++) {
            values[band] = getSampleFloat(x, y, band, buffer);
        }
        return values;
    }

    public double[] getPixel(int x, int y, double[] output, DataBuffer buffer) {
        double[] values = output == null ? new double[numBands] : output;
        for (int band = 0; band < numBands; band++) {
            values[band] = getSampleDouble(x, y, band, buffer);
        }
        return values;
    }

    public int[] getPixels(int x, int y, int width, int height, int[] output, DataBuffer buffer) {
        int[] values = output == null ? new int[width * height * numBands] : output;
        int index = 0;
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                for (int band = 0; band < numBands; band++) {
                    values[index++] = getSample(x + column, y + row, band, buffer);
                }
            }
        }
        return values;
    }

    public float[] getPixels(int x, int y, int width, int height, float[] output, DataBuffer buffer) {
        float[] values = output == null ? new float[width * height * numBands] : output;
        int index = 0;
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                for (int band = 0; band < numBands; band++) {
                    values[index++] = getSampleFloat(x + column, y + row, band, buffer);
                }
            }
        }
        return values;
    }

    public double[] getPixels(int x, int y, int width, int height, double[] output, DataBuffer buffer) {
        double[] values = output == null ? new double[width * height * numBands] : output;
        int index = 0;
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                for (int band = 0; band < numBands; band++) {
                    values[index++] = getSampleDouble(x + column, y + row, band, buffer);
                }
            }
        }
        return values;
    }

    public abstract int getSample(int x, int y, int band, DataBuffer buffer);

    public float getSampleFloat(int x, int y, int band, DataBuffer buffer) {
        return getSample(x, y, band, buffer);
    }

    public double getSampleDouble(int x, int y, int band, DataBuffer buffer) {
        return getSample(x, y, band, buffer);
    }

    public int[] getSamples(int x, int y, int width, int height, int band, int[] output, DataBuffer buffer) {
        int[] values = output == null ? new int[width * height] : output;
        int index = 0;
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                values[index++] = getSample(x + column, y + row, band, buffer);
            }
        }
        return values;
    }

    public float[] getSamples(int x, int y, int width, int height, int band, float[] output, DataBuffer buffer) {
        float[] values = output == null ? new float[width * height] : output;
        int index = 0;
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                values[index++] = getSampleFloat(x + column, y + row, band, buffer);
            }
        }
        return values;
    }

    public double[] getSamples(int x, int y, int width, int height, int band, double[] output, DataBuffer buffer) {
        double[] values = output == null ? new double[width * height] : output;
        int index = 0;
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                values[index++] = getSampleDouble(x + column, y + row, band, buffer);
            }
        }
        return values;
    }

    public void setPixel(int x, int y, int[] values, DataBuffer buffer) {
        for (int band = 0; band < numBands; band++) {
            setSample(x, y, band, values[band], buffer);
        }
    }

    public void setPixel(int x, int y, float[] values, DataBuffer buffer) {
        for (int band = 0; band < numBands; band++) {
            setSample(x, y, band, values[band], buffer);
        }
    }

    public void setPixel(int x, int y, double[] values, DataBuffer buffer) {
        for (int band = 0; band < numBands; band++) {
            setSample(x, y, band, values[band], buffer);
        }
    }

    public void setPixels(int x, int y, int width, int height, int[] values, DataBuffer buffer) {
        int index = 0;
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                for (int band = 0; band < numBands; band++) {
                    setSample(x + column, y + row, band, values[index++], buffer);
                }
            }
        }
    }

    public void setPixels(int x, int y, int width, int height, float[] values, DataBuffer buffer) {
        int index = 0;
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                for (int band = 0; band < numBands; band++) {
                    setSample(x + column, y + row, band, values[index++], buffer);
                }
            }
        }
    }

    public void setPixels(int x, int y, int width, int height, double[] values, DataBuffer buffer) {
        int index = 0;
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                for (int band = 0; band < numBands; band++) {
                    setSample(x + column, y + row, band, values[index++], buffer);
                }
            }
        }
    }

    public abstract void setSample(int x, int y, int band, int value, DataBuffer buffer);

    public void setSample(int x, int y, int band, float value, DataBuffer buffer) {
        setSample(x, y, band, (int) value, buffer);
    }

    public void setSample(int x, int y, int band, double value, DataBuffer buffer) {
        setSample(x, y, band, (int) value, buffer);
    }

    public void setSamples(int x, int y, int width, int height, int band, int[] values, DataBuffer buffer) {
        int index = 0;
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                setSample(x + column, y + row, band, values[index++], buffer);
            }
        }
    }

    public void setSamples(int x, int y, int width, int height, int band, float[] values, DataBuffer buffer) {
        int index = 0;
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                setSample(x + column, y + row, band, values[index++], buffer);
            }
        }
    }

    public void setSamples(int x, int y, int width, int height, int band, double[] values, DataBuffer buffer) {
        int index = 0;
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                setSample(x + column, y + row, band, values[index++], buffer);
            }
        }
    }

    public abstract SampleModel createCompatibleSampleModel(int width, int height);

    public abstract SampleModel createSubsetSampleModel(int[] bands);

    public abstract DataBuffer createDataBuffer();

    public abstract int[] getSampleSize();

    public abstract int getSampleSize(int band);

    final void checkPixel(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds");
        }
    }

    final void checkBand(int band) {
        if (band < 0 || band >= numBands) {
            throw new ArrayIndexOutOfBoundsException("Band out of bounds");
        }
    }
}
