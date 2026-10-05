package java.awt.image;

import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class BandCombineOp implements RasterOp {
    private final RenderingHints hints;
    private final float[][] matrix;
    private final int columns;
    private final int rows;

    public BandCombineOp(float[][] matrix, RenderingHints hints) {
        if (matrix.length == 0) {
            throw new ArrayIndexOutOfBoundsException(0);
        }
        rows = matrix.length;
        columns = matrix[0].length;
        this.matrix = new float[rows][columns];
        for (int row = 0; row < rows; row++) {
            if (matrix[row].length < columns) {
                throw new IndexOutOfBoundsException("row " + row + " too short");
            }
            System.arraycopy(matrix[row], 0, this.matrix[row], 0, columns);
        }
        this.hints = hints;
    }

    public final float[][] getMatrix() {
        float[][] result = new float[rows][columns];
        for (int row = 0; row < rows; row++) {
            System.arraycopy(matrix[row], 0, result[row], 0, columns);
        }
        return result;
    }

    @Override
    public WritableRaster filter(Raster source, WritableRaster destination) {
        validateSourceBands(source);
        WritableRaster result = destination == null ? createCompatibleDestRaster(source) : destination;
        if (rows != result.getNumBands()) {
            throw new IllegalArgumentException("Number of rows in the matrix must equal the number of destination bands");
        }
        int[] sourcePixel = new int[source.getNumBands()];
        int[] destinationPixel = new int[rows];
        for (int row = 0; row < source.getHeight(); row++) {
            int sourceY = source.getMinY() + row;
            int destinationY = result.getMinY() + row;
            for (int column = 0; column < source.getWidth(); column++) {
                int sourceX = source.getMinX() + column;
                int destinationX = result.getMinX() + column;
                source.getPixel(sourceX, sourceY, sourcePixel);
                combine(sourcePixel, destinationPixel);
                result.setPixel(destinationX, destinationY, destinationPixel);
            }
        }
        return result;
    }

    @Override
    public final Rectangle2D getBounds2D(Raster source) {
        return source.getBounds();
    }

    @Override
    public WritableRaster createCompatibleDestRaster(Raster source) {
        validateSourceBands(source);
        if (source.getNumBands() != rows) {
            throw new IllegalArgumentException("Cannot create a compatible Raster with " + rows + " bands");
        }
        return source.createCompatibleWritableRaster();
    }

    @Override
    public final Point2D getPoint2D(Point2D sourcePoint, Point2D destinationPoint) {
        Point2D result = destinationPoint == null ? new Point2D.Float() : destinationPoint;
        result.setLocation(sourcePoint.getX(), sourcePoint.getY());
        return result;
    }

    @Override
    public final RenderingHints getRenderingHints() {
        return hints;
    }

    private void combine(int[] source, int[] destination) {
        int sourceBands = source.length;
        for (int row = 0; row < rows; row++) {
            float value = columns == sourceBands + 1 ? matrix[row][sourceBands] : 0.0f;
            for (int column = 0; column < sourceBands; column++) {
                value += matrix[row][column] * source[column];
            }
            destination[row] = (int) value;
        }
    }

    private void validateSourceBands(Raster source) {
        int sourceBands = source.getNumBands();
        if (columns != sourceBands && columns != sourceBands + 1) {
            throw new IllegalArgumentException("Number of matrix columns must equal the number of source bands, optionally plus one");
        }
    }
}
