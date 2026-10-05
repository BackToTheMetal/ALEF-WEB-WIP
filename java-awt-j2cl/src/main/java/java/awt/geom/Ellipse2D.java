package java.awt.geom;

import java.io.Serializable;
import java.util.NoSuchElementException;

public abstract class Ellipse2D extends RectangularShape {
    private static final double CONTROL_LOW = 0.22385762508460333;
    private static final double CONTROL_HIGH = 0.7761423749153966;

    protected Ellipse2D() {
    }

    @Override
    public boolean contains(double x, double y) {
        double width = getWidth();
        double height = getHeight();
        if (width <= 0.0 || height <= 0.0) {
            return false;
        }
        double normalizedX = (x - getX()) / width - 0.5;
        double normalizedY = (y - getY()) / height - 0.5;
        return normalizedX * normalizedX + normalizedY * normalizedY < 0.25;
    }

    @Override
    public boolean intersects(double x, double y, double width, double height) {
        if (width <= 0.0 || height <= 0.0 || isEmpty()) {
            return false;
        }
        double centerX = getCenterX();
        double centerY = getCenterY();
        double nearestX = Math.max(x, Math.min(centerX, x + width));
        double nearestY = Math.max(y, Math.min(centerY, y + height));
        double normalizedX = (nearestX - centerX) / (getWidth() / 2.0);
        double normalizedY = (nearestY - centerY) / (getHeight() / 2.0);
        return normalizedX * normalizedX + normalizedY * normalizedY < 1.0;
    }

    @Override
    public boolean contains(double x, double y, double width, double height) {
        return contains(x, y) && contains(x + width, y) && contains(x, y + height) && contains(x + width, y + height);
    }

    @Override
    public PathIterator getPathIterator(AffineTransform transform) {
        return new EllipseIterator(this, transform);
    }

    @Override
    public int hashCode() {
        long bits = java.lang.Double.doubleToLongBits(getX());
        bits += java.lang.Double.doubleToLongBits(getY()) * 37;
        bits += java.lang.Double.doubleToLongBits(getWidth()) * 43;
        bits += java.lang.Double.doubleToLongBits(getHeight()) * 47;
        return (int) bits ^ (int) (bits >> 32);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Ellipse2D)) {
            return false;
        }
        Ellipse2D ellipse = (Ellipse2D) object;
        return getX() == ellipse.getX() && getY() == ellipse.getY() && getWidth() == ellipse.getWidth() && getHeight() == ellipse.getHeight();
    }

    public static class Float extends Ellipse2D implements Serializable {
        private static final long serialVersionUID = -6631491953081842455L;
        public float x;
        public float y;
        public float width;
        public float height;

        public Float() {
        }

        public Float(float x, float y, float width, float height) {
            setFrame(x, y, width, height);
        }

        @Override
        public double getX() {
            return x;
        }

        @Override
        public double getY() {
            return y;
        }

        @Override
        public double getWidth() {
            return width;
        }

        @Override
        public double getHeight() {
            return height;
        }

        @Override
        public boolean isEmpty() {
            return width <= 0.0f || height <= 0.0f;
        }

        public void setFrame(float x, float y, float width, float height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        @Override
        public void setFrame(double x, double y, double width, double height) {
            setFrame((float) x, (float) y, (float) width, (float) height);
        }

        @Override
        public Rectangle2D getBounds2D() {
            return new Rectangle2D.Float(x, y, width, height);
        }

        @Override
        public Object clone() {
            return new Float(x, y, width, height);
        }
    }

    public static class Double extends Ellipse2D implements Serializable {
        private static final long serialVersionUID = 5555464816372320683L;
        public double x;
        public double y;
        public double width;
        public double height;

        public Double() {
        }

        public Double(double x, double y, double width, double height) {
            setFrame(x, y, width, height);
        }

        @Override
        public double getX() {
            return x;
        }

        @Override
        public double getY() {
            return y;
        }

        @Override
        public double getWidth() {
            return width;
        }

        @Override
        public double getHeight() {
            return height;
        }

        @Override
        public boolean isEmpty() {
            return width <= 0.0 || height <= 0.0;
        }

        @Override
        public void setFrame(double x, double y, double width, double height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        @Override
        public Rectangle2D getBounds2D() {
            return new Rectangle2D.Double(x, y, width, height);
        }

        @Override
        public Object clone() {
            return new Double(x, y, width, height);
        }
    }

    private static final class EllipseIterator implements PathIterator {
        private final Ellipse2D ellipse;
        private final AffineTransform transform;
        private int index;

        private EllipseIterator(Ellipse2D ellipse, AffineTransform transform) {
            this.ellipse = ellipse;
            this.transform = transform;
            if (ellipse.getWidth() < 0.0 || ellipse.getHeight() < 0.0) {
                index = 6;
            }
        }

        @Override
        public int getWindingRule() {
            return WIND_NON_ZERO;
        }

        @Override
        public boolean isDone() {
            return index > 5;
        }

        @Override
        public void next() {
            index++;
        }

        @Override
        public int currentSegment(float[] coordinates) {
            double[] values = new double[6];
            int type = currentSegment(values);
            int count = type == SEG_CUBICTO ? 6 : type == SEG_CLOSE ? 0 : 2;
            for (int valueIndex = 0; valueIndex < count; valueIndex++) {
                coordinates[valueIndex] = (float) values[valueIndex];
            }
            return type;
        }

        @Override
        public int currentSegment(double[] coordinates) {
            if (isDone()) {
                throw new NoSuchElementException("ellipse iterator out of bounds");
            }
            if (index == 5) {
                return SEG_CLOSE;
            }
            double x = ellipse.getX();
            double y = ellipse.getY();
            double width = ellipse.getWidth();
            double height = ellipse.getHeight();
            if (index == 0) {
                coordinates[0] = x + width;
                coordinates[1] = y + height / 2.0;
                transform(coordinates, 1);
                return SEG_MOVETO;
            }
            setCurve(coordinates, x, y, width, height, index - 1);
            transform(coordinates, 3);
            return SEG_CUBICTO;
        }

        private void transform(double[] coordinates, int pointCount) {
            if (transform != null) {
                transform.transform(coordinates, 0, coordinates, 0, pointCount);
            }
        }

        private static void setCurve(double[] output, double x, double y, double width, double height, int quarter) {
            if (quarter == 0) {
                set(output, x + width, y + height * CONTROL_HIGH, x + width * CONTROL_HIGH, y + height, x + width * 0.5, y + height);
            } else if (quarter == 1) {
                set(output, x + width * CONTROL_LOW, y + height, x, y + height * CONTROL_HIGH, x, y + height * 0.5);
            } else if (quarter == 2) {
                set(output, x, y + height * CONTROL_LOW, x + width * CONTROL_LOW, y, x + width * 0.5, y);
            } else {
                set(output, x + width * CONTROL_HIGH, y, x + width, y + height * CONTROL_LOW, x + width, y + height * 0.5);
            }
        }

        private static void set(double[] output, double x1, double y1, double x2, double y2, double x3, double y3) {
            output[0] = x1;
            output[1] = y1;
            output[2] = x2;
            output[3] = y2;
            output[4] = x3;
            output[5] = y3;
        }
    }
}
