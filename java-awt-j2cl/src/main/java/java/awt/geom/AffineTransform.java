package java.awt.geom;

import java.awt.Shape;
import java.io.Serializable;

public class AffineTransform implements Cloneable, Serializable {
    private static final long serialVersionUID = 1330973210523860834L;
    public static final int TYPE_IDENTITY = 0;
    public static final int TYPE_TRANSLATION = 1;
    public static final int TYPE_UNIFORM_SCALE = 2;
    public static final int TYPE_GENERAL_SCALE = 4;
    public static final int TYPE_MASK_SCALE = TYPE_UNIFORM_SCALE | TYPE_GENERAL_SCALE;
    public static final int TYPE_FLIP = 64;
    public static final int TYPE_QUADRANT_ROTATION = 8;
    public static final int TYPE_GENERAL_ROTATION = 16;
    public static final int TYPE_MASK_ROTATION = TYPE_QUADRANT_ROTATION | TYPE_GENERAL_ROTATION;
    public static final int TYPE_GENERAL_TRANSFORM = 32;
    private double m00;
    private double m10;
    private double m01;
    private double m11;
    private double m02;
    private double m12;

    public AffineTransform() {
        setToIdentity();
    }

    public AffineTransform(AffineTransform transform) {
        setTransform(transform);
    }

    public AffineTransform(float m00, float m10, float m01, float m11, float m02, float m12) {
        setTransform(m00, m10, m01, m11, m02, m12);
    }

    public AffineTransform(float[] matrix) {
        setTransform(matrix[0], matrix[1], matrix[2], matrix[3], matrix.length > 5 ? matrix[4] : 0.0, matrix.length > 5 ? matrix[5] : 0.0);
    }

    public AffineTransform(double m00, double m10, double m01, double m11, double m02, double m12) {
        setTransform(m00, m10, m01, m11, m02, m12);
    }

    public AffineTransform(double[] matrix) {
        setTransform(matrix[0], matrix[1], matrix[2], matrix[3], matrix.length > 5 ? matrix[4] : 0.0, matrix.length > 5 ? matrix[5] : 0.0);
    }

    public static AffineTransform getTranslateInstance(double translateX, double translateY) {
        AffineTransform transform = new AffineTransform();
        transform.setToTranslation(translateX, translateY);
        return transform;
    }

    public static AffineTransform getRotateInstance(double angle) {
        AffineTransform transform = new AffineTransform();
        transform.setToRotation(angle);
        return transform;
    }

    public static AffineTransform getRotateInstance(double angle, double anchorX, double anchorY) {
        AffineTransform transform = new AffineTransform();
        transform.setToRotation(angle, anchorX, anchorY);
        return transform;
    }

    public static AffineTransform getRotateInstance(double vectorX, double vectorY) {
        AffineTransform transform = new AffineTransform();
        transform.setToRotation(vectorX, vectorY);
        return transform;
    }

    public static AffineTransform getRotateInstance(double vectorX, double vectorY, double anchorX, double anchorY) {
        AffineTransform transform = new AffineTransform();
        transform.setToRotation(vectorX, vectorY, anchorX, anchorY);
        return transform;
    }

    public static AffineTransform getQuadrantRotateInstance(int quadrants) {
        AffineTransform transform = new AffineTransform();
        transform.setToQuadrantRotation(quadrants);
        return transform;
    }

    public static AffineTransform getQuadrantRotateInstance(int quadrants, double anchorX, double anchorY) {
        AffineTransform transform = new AffineTransform();
        transform.setToQuadrantRotation(quadrants, anchorX, anchorY);
        return transform;
    }

    public static AffineTransform getScaleInstance(double scaleX, double scaleY) {
        AffineTransform transform = new AffineTransform();
        transform.setToScale(scaleX, scaleY);
        return transform;
    }

    public static AffineTransform getShearInstance(double shearX, double shearY) {
        AffineTransform transform = new AffineTransform();
        transform.setToShear(shearX, shearY);
        return transform;
    }

    public int getType() {
        int type = m02 == 0.0 && m12 == 0.0 ? TYPE_IDENTITY : TYPE_TRANSLATION;
        if (m01 == 0.0 && m10 == 0.0) {
            if (m00 == 1.0 && m11 == 1.0) {
                return type;
            }
            if (m00 * m11 < 0.0) {
                type |= TYPE_FLIP;
            }
            double absoluteX = Math.abs(m00);
            double absoluteY = Math.abs(m11);
            type |= absoluteX == absoluteY ? TYPE_UNIFORM_SCALE : TYPE_GENERAL_SCALE;
            return type;
        }
        double columnDotProduct = m00 * m01 + m10 * m11;
        if (columnDotProduct != 0.0) {
            return type | TYPE_GENERAL_TRANSFORM;
        }
        if (getDeterminant() < 0.0) {
            type |= TYPE_FLIP;
        }
        double firstLength = m00 * m00 + m10 * m10;
        double secondLength = m01 * m01 + m11 * m11;
        if (firstLength != 1.0 || secondLength != 1.0) {
            type |= firstLength == secondLength ? TYPE_UNIFORM_SCALE : TYPE_GENERAL_SCALE;
        }
        type |= isQuadrantMatrix() ? TYPE_QUADRANT_ROTATION : TYPE_GENERAL_ROTATION;
        return type;
    }

    public double getDeterminant() {
        return m00 * m11 - m01 * m10;
    }

    public void getMatrix(double[] matrix) {
        matrix[0] = m00;
        matrix[1] = m10;
        matrix[2] = m01;
        matrix[3] = m11;
        if (matrix.length > 5) {
            matrix[4] = m02;
            matrix[5] = m12;
        }
    }

    public double getScaleX() {
        return m00;
    }

    public double getScaleY() {
        return m11;
    }

    public double getShearX() {
        return m01;
    }

    public double getShearY() {
        return m10;
    }

    public double getTranslateX() {
        return m02;
    }

    public double getTranslateY() {
        return m12;
    }

    public void translate(double translateX, double translateY) {
        concatenate(getTranslateInstance(translateX, translateY));
    }

    public void rotate(double angle) {
        concatenate(getRotateInstance(angle));
    }

    public void rotate(double angle, double anchorX, double anchorY) {
        concatenate(getRotateInstance(angle, anchorX, anchorY));
    }

    public void rotate(double vectorX, double vectorY) {
        concatenate(getRotateInstance(vectorX, vectorY));
    }

    public void rotate(double vectorX, double vectorY, double anchorX, double anchorY) {
        concatenate(getRotateInstance(vectorX, vectorY, anchorX, anchorY));
    }

    public void quadrantRotate(int quadrants) {
        concatenate(getQuadrantRotateInstance(quadrants));
    }

    public void quadrantRotate(int quadrants, double anchorX, double anchorY) {
        concatenate(getQuadrantRotateInstance(quadrants, anchorX, anchorY));
    }

    public void scale(double scaleX, double scaleY) {
        concatenate(getScaleInstance(scaleX, scaleY));
    }

    public void shear(double shearX, double shearY) {
        concatenate(getShearInstance(shearX, shearY));
    }

    public void setToIdentity() {
        setTransform(1.0, 0.0, 0.0, 1.0, 0.0, 0.0);
    }

    public void setToTranslation(double translateX, double translateY) {
        setTransform(1.0, 0.0, 0.0, 1.0, translateX, translateY);
    }

    public void setToRotation(double angle) {
        double sine = Math.sin(angle);
        if (sine == 1.0) {
            setTransform(0.0, 1.0, -1.0, 0.0, 0.0, 0.0);
            return;
        }
        if (sine == -1.0) {
            setTransform(0.0, -1.0, 1.0, 0.0, 0.0, 0.0);
            return;
        }
        double cosine = Math.cos(angle);
        if (cosine == -1.0) {
            setTransform(cosine, sine, -sine, cosine, 0.0, 0.0);
            return;
        }
        if (cosine == 1.0) {
            setTransform(cosine, sine, -sine, cosine, 0.0, 0.0);
            return;
        }
        setTransform(cosine, sine, -sine, cosine, 0.0, 0.0);
    }

    public void setToRotation(double angle, double anchorX, double anchorY) {
        setToRotation(angle);
        m02 = anchorX - m00 * anchorX - m01 * anchorY;
        m12 = anchorY - m10 * anchorX - m11 * anchorY;
    }

    public void setToRotation(double vectorX, double vectorY) {
        if (vectorY == 0.0) {
            setTransform(vectorX < 0.0 ? -1.0 : 1.0, 0.0, 0.0, vectorX < 0.0 ? -1.0 : 1.0, 0.0, 0.0);
            return;
        }
        if (vectorX == 0.0) {
            setTransform(0.0, vectorY > 0.0 ? 1.0 : -1.0, vectorY > 0.0 ? -1.0 : 1.0, 0.0, 0.0, 0.0);
            return;
        }
        double length = Math.sqrt(vectorX * vectorX + vectorY * vectorY);
        double cosine = vectorX / length;
        double sine = vectorY / length;
        setTransform(cosine, sine, -sine, cosine, 0.0, 0.0);
    }

    public void setToRotation(double vectorX, double vectorY, double anchorX, double anchorY) {
        setToRotation(vectorX, vectorY);
        m02 = anchorX * (1.0 - m00) + anchorY * m10;
        m12 = anchorY * (1.0 - m00) - anchorX * m10;
    }

    public void setToQuadrantRotation(int quadrants) {
        switch (quadrants & 3) {
            case 0:
                setToIdentity();
                break;
            case 1:
                setTransform(0.0, 1.0, -1.0, 0.0, 0.0, 0.0);
                break;
            case 2:
                setTransform(-1.0, 0.0, 0.0, -1.0, 0.0, 0.0);
                break;
            default:
                setTransform(0.0, -1.0, 1.0, 0.0, 0.0, 0.0);
                break;
        }
    }

    public void setToQuadrantRotation(int quadrants, double anchorX, double anchorY) {
        setToQuadrantRotation(quadrants);
        m02 = anchorX - m00 * anchorX - m01 * anchorY;
        m12 = anchorY - m10 * anchorX - m11 * anchorY;
    }

    public void setToScale(double scaleX, double scaleY) {
        setTransform(scaleX, 0.0, 0.0, scaleY, 0.0, 0.0);
    }

    public void setToShear(double shearX, double shearY) {
        setTransform(1.0, shearY, shearX, 1.0, 0.0, 0.0);
    }

    public void setTransform(AffineTransform transform) {
        setTransform(transform.m00, transform.m10, transform.m01, transform.m11, transform.m02, transform.m12);
    }

    public void setTransform(double m00, double m10, double m01, double m11, double m02, double m12) {
        this.m00 = m00;
        this.m10 = m10;
        this.m01 = m01;
        this.m11 = m11;
        this.m02 = m02;
        this.m12 = m12;
    }

    public void concatenate(AffineTransform transform) {
        double newM00 = m00 * transform.m00 + m01 * transform.m10;
        double newM01 = m00 * transform.m01 + m01 * transform.m11;
        double newM02 = m00 * transform.m02 + m01 * transform.m12 + m02;
        double newM10 = m10 * transform.m00 + m11 * transform.m10;
        double newM11 = m10 * transform.m01 + m11 * transform.m11;
        double newM12 = m10 * transform.m02 + m11 * transform.m12 + m12;
        setTransform(newM00, newM10, newM01, newM11, newM02, newM12);
    }

    public void preConcatenate(AffineTransform transform) {
        double newM00 = transform.m00 * m00 + transform.m01 * m10;
        double newM01 = transform.m00 * m01 + transform.m01 * m11;
        double newM02 = transform.m00 * m02 + transform.m01 * m12 + transform.m02;
        double newM10 = transform.m10 * m00 + transform.m11 * m10;
        double newM11 = transform.m10 * m01 + transform.m11 * m11;
        double newM12 = transform.m10 * m02 + transform.m11 * m12 + transform.m12;
        setTransform(newM00, newM10, newM01, newM11, newM02, newM12);
    }

    public AffineTransform createInverse() throws NoninvertibleTransformException {
        AffineTransform inverse = new AffineTransform(this);
        inverse.invert();
        return inverse;
    }

    public void invert() throws NoninvertibleTransformException {
        double determinant = getDeterminant();
        if (Math.abs(determinant) <= java.lang.Double.MIN_VALUE) {
            throw new NoninvertibleTransformException("Determinant is " + determinant);
        }
        double newM00 = m11 / determinant;
        double newM01 = -m01 / determinant;
        double newM02 = (m01 * m12 - m11 * m02) / determinant;
        double newM10 = -m10 / determinant;
        double newM11 = m00 / determinant;
        double newM12 = (m10 * m02 - m00 * m12) / determinant;
        setTransform(newM00, newM10, newM01, newM11, newM02, newM12);
    }

    public Point2D transform(Point2D source, Point2D destination) {
        Point2D result = destination;
        if (result == null) {
            result = source instanceof Point2D.Double ? new Point2D.Double() : new Point2D.Float();
        }
        double x = source.getX();
        double y = source.getY();
        result.setLocation(m00 * x + m01 * y + m02, m10 * x + m11 * y + m12);
        return result;
    }

    public void transform(Point2D[] sources, int sourceOffset, Point2D[] destinations, int destinationOffset, int pointCount) {
        if (sources == destinations && destinationOffset > sourceOffset && destinationOffset < sourceOffset + pointCount) {
            for (int index = pointCount - 1; index >= 0; index--) {
                destinations[destinationOffset + index] = transform(sources[sourceOffset + index], destinations[destinationOffset + index]);
            }
            return;
        }
        for (int index = 0; index < pointCount; index++) {
            destinations[destinationOffset + index] = transform(sources[sourceOffset + index], destinations[destinationOffset + index]);
        }
    }

    public void transform(float[] sources, int sourceOffset, float[] destinations, int destinationOffset, int pointCount) {
        double[] values = copyPoints(sources, sourceOffset, pointCount);
        for (int index = 0; index < pointCount; index++) {
            double x = values[index * 2];
            double y = values[index * 2 + 1];
            destinations[destinationOffset + index * 2] = (float) (m00 * x + m01 * y + m02);
            destinations[destinationOffset + index * 2 + 1] = (float) (m10 * x + m11 * y + m12);
        }
    }

    public void transform(double[] sources, int sourceOffset, double[] destinations, int destinationOffset, int pointCount) {
        double[] values = copyPoints(sources, sourceOffset, pointCount);
        for (int index = 0; index < pointCount; index++) {
            double x = values[index * 2];
            double y = values[index * 2 + 1];
            destinations[destinationOffset + index * 2] = m00 * x + m01 * y + m02;
            destinations[destinationOffset + index * 2 + 1] = m10 * x + m11 * y + m12;
        }
    }

    public void transform(float[] sources, int sourceOffset, double[] destinations, int destinationOffset, int pointCount) {
        for (int index = 0; index < pointCount; index++) {
            double x = sources[sourceOffset + index * 2];
            double y = sources[sourceOffset + index * 2 + 1];
            destinations[destinationOffset + index * 2] = m00 * x + m01 * y + m02;
            destinations[destinationOffset + index * 2 + 1] = m10 * x + m11 * y + m12;
        }
    }

    public void transform(double[] sources, int sourceOffset, float[] destinations, int destinationOffset, int pointCount) {
        for (int index = 0; index < pointCount; index++) {
            double x = sources[sourceOffset + index * 2];
            double y = sources[sourceOffset + index * 2 + 1];
            destinations[destinationOffset + index * 2] = (float) (m00 * x + m01 * y + m02);
            destinations[destinationOffset + index * 2 + 1] = (float) (m10 * x + m11 * y + m12);
        }
    }

    public Point2D inverseTransform(Point2D source, Point2D destination) throws NoninvertibleTransformException {
        Point2D result = destination;
        if (result == null) {
            result = source instanceof Point2D.Double ? new Point2D.Double() : new Point2D.Float();
        }
        double determinant = getDeterminant();
        if (Math.abs(determinant) <= java.lang.Double.MIN_VALUE) {
            throw new NoninvertibleTransformException("Determinant is " + determinant);
        }
        double x = source.getX() - m02;
        double y = source.getY() - m12;
        result.setLocation((x * m11 - y * m01) / determinant, (y * m00 - x * m10) / determinant);
        return result;
    }

    public void inverseTransform(double[] sources, int sourceOffset, double[] destinations, int destinationOffset, int pointCount) throws NoninvertibleTransformException {
        AffineTransform inverse = createInverse();
        inverse.transform(sources, sourceOffset, destinations, destinationOffset, pointCount);
    }

    public Point2D deltaTransform(Point2D source, Point2D destination) {
        Point2D result = destination;
        if (result == null) {
            result = source instanceof Point2D.Double ? new Point2D.Double() : new Point2D.Float();
        }
        double x = source.getX();
        double y = source.getY();
        result.setLocation(m00 * x + m01 * y, m10 * x + m11 * y);
        return result;
    }

    public void deltaTransform(double[] sources, int sourceOffset, double[] destinations, int destinationOffset, int pointCount) {
        double[] values = copyPoints(sources, sourceOffset, pointCount);
        for (int index = 0; index < pointCount; index++) {
            double x = values[index * 2];
            double y = values[index * 2 + 1];
            destinations[destinationOffset + index * 2] = m00 * x + m01 * y;
            destinations[destinationOffset + index * 2 + 1] = m10 * x + m11 * y;
        }
    }

    public Shape createTransformedShape(Shape shape) {
        return shape == null ? null : new Path2D.Double(shape, this);
    }

    @Override
    public String toString() {
        return "AffineTransform[[" + stringValue(m00) + ", " + stringValue(m01) + ", " + stringValue(m02) + "], [" + stringValue(m10) + ", " + stringValue(m11) + ", " + stringValue(m12) + "]]";
    }

    public boolean isIdentity() {
        return m00 == 1.0 && m10 == 0.0 && m01 == 0.0 && m11 == 1.0 && m02 == 0.0 && m12 == 0.0;
    }

    public Object clone() {
        return new AffineTransform(this);
    }

    @Override
    public int hashCode() {
        long bits = java.lang.Double.doubleToLongBits(m00);
        bits = bits * 31 + java.lang.Double.doubleToLongBits(m01);
        bits = bits * 31 + java.lang.Double.doubleToLongBits(m02);
        bits = bits * 31 + java.lang.Double.doubleToLongBits(m10);
        bits = bits * 31 + java.lang.Double.doubleToLongBits(m11);
        bits = bits * 31 + java.lang.Double.doubleToLongBits(m12);
        return (int) bits ^ (int) (bits >> 32);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof AffineTransform)) {
            return false;
        }
        AffineTransform transform = (AffineTransform) object;
        return m00 == transform.m00 && m10 == transform.m10 && m01 == transform.m01 && m11 == transform.m11 && m02 == transform.m02 && m12 == transform.m12;
    }

    private boolean isQuadrantMatrix() {
        return (m00 == 0.0 && m11 == 0.0) || (m01 == 0.0 && m10 == 0.0);
    }

    private static double roundedForString(double value) {
        return Math.rint(value * 1000000000000000.0) / 1000000000000000.0;
    }

    private static String stringValue(double value) {
        double roundedValue = roundedForString(value);
        if (roundedValue == 0.0) {
            return 1.0 / roundedValue < 0.0 ? "-0.0" : "0.0";
        }
        String text = java.lang.Double.toString(roundedValue);
        if (!java.lang.Double.isInfinite(roundedValue) && !java.lang.Double.isNaN(roundedValue) && roundedValue == Math.rint(roundedValue) && text.indexOf('.') < 0 && text.indexOf('E') < 0) {
            return text + ".0";
        }
        return text;
    }

    private static double[] copyPoints(float[] source, int offset, int pointCount) {
        double[] result = new double[pointCount * 2];
        for (int index = 0; index < result.length; index++) {
            result[index] = source[offset + index];
        }
        return result;
    }

    private static double[] copyPoints(double[] source, int offset, int pointCount) {
        double[] result = new double[pointCount * 2];
        for (int index = 0; index < result.length; index++) {
            result[index] = source[offset + index];
        }
        return result;
    }

}
