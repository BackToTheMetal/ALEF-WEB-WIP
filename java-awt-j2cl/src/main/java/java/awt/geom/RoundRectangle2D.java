package java.awt.geom;

import java.io.Serializable;
import java.util.NoSuchElementException;

public abstract class RoundRectangle2D extends RectangularShape {
    private static final double CONTROL_VALUE = 0.5522847498307933;

    protected RoundRectangle2D() {
    }

    public abstract double getArcWidth();

    public abstract double getArcHeight();

    public abstract void setRoundRect(double x, double y, double width, double height, double arcWidth, double arcHeight);

    public void setRoundRect(RoundRectangle2D rectangle) {
        setRoundRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight(), rectangle.getArcWidth(), rectangle.getArcHeight());
    }

    @Override
    public void setFrame(double x, double y, double width, double height) {
        setRoundRect(x, y, width, height, getArcWidth(), getArcHeight());
    }

    @Override
    public boolean contains(double x, double y) {
        double left = getX();
        double top = getY();
        double width = getWidth();
        double height = getHeight();
        if (width <= 0.0 || height <= 0.0 || x < left || y < top || x >= left + width || y >= top + height) {
            return false;
        }
        double radiusX = Math.min(width, Math.abs(getArcWidth())) / 2.0;
        double radiusY = Math.min(height, Math.abs(getArcHeight())) / 2.0;
        if (x >= left + radiusX && x < left + width - radiusX) {
            return true;
        }
        if (y >= top + radiusY && y < top + height - radiusY) {
            return true;
        }
        double cornerCenterX = x < left + radiusX ? left + radiusX : left + width - radiusX;
        double cornerCenterY = y < top + radiusY ? top + radiusY : top + height - radiusY;
        double normalizedX = radiusX == 0.0 ? 0.0 : (x - cornerCenterX) / radiusX;
        double normalizedY = radiusY == 0.0 ? 0.0 : (y - cornerCenterY) / radiusY;
        return normalizedX * normalizedX + normalizedY * normalizedY <= 1.0;
    }

    @Override
    public boolean intersects(double x, double y, double width, double height) {
        double ownWidth = getWidth();
        double ownHeight = getHeight();
        if (ownWidth <= 0.0 || ownHeight <= 0.0 || width <= 0.0 || height <= 0.0) {
            return false;
        }
        double left = getX();
        double top = getY();
        double right = left + ownWidth;
        double bottom = top + ownHeight;
        if (x + width <= left || x >= right || y + height <= top || y >= bottom) {
            return false;
        }
        double radiusX = Math.min(ownWidth, Math.abs(getArcWidth())) / 2.0;
        double radiusY = Math.min(ownHeight, Math.abs(getArcHeight())) / 2.0;
        int leftClass = classify(x, left + radiusX, right - radiusX);
        int rightClass = classify(x + width, left + radiusX, right - radiusX);
        int topClass = classify(y, top + radiusY, bottom - radiusY);
        int bottomClass = classify(y + height, top + radiusY, bottom - radiusY);
        if (leftClass == 1 || rightClass == 1 || topClass == 1 || bottomClass == 1) {
            return true;
        }
        double nearestX = leftClass == 0 ? x + width - (left + radiusX) : x - (right - radiusX);
        double nearestY = topClass == 0 ? y + height - (top + radiusY) : y - (bottom - radiusY);
        double normalizedX = radiusX == 0.0 ? 0.0 : nearestX / radiusX;
        double normalizedY = radiusY == 0.0 ? 0.0 : nearestY / radiusY;
        return normalizedX * normalizedX + normalizedY * normalizedY <= 1.0;
    }

    @Override
    public boolean contains(double x, double y, double width, double height) {
        return width > 0.0 && height > 0.0 && contains(x, y) && contains(x + width, y) && contains(x, y + height) && contains(x + width, y + height);
    }

    @Override
    public PathIterator getPathIterator(AffineTransform transform) {
        return new RoundRectangleIterator(this, transform);
    }

    @Override
    public int hashCode() {
        long bits = java.lang.Double.doubleToLongBits(getX());
        bits += java.lang.Double.doubleToLongBits(getY()) * 37;
        bits += java.lang.Double.doubleToLongBits(getWidth()) * 43;
        bits += java.lang.Double.doubleToLongBits(getHeight()) * 47;
        bits += java.lang.Double.doubleToLongBits(getArcWidth()) * 53;
        bits += java.lang.Double.doubleToLongBits(getArcHeight()) * 59;
        return (int) bits ^ (int) (bits >> 32);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof RoundRectangle2D)) {
            return false;
        }
        RoundRectangle2D rectangle = (RoundRectangle2D) object;
        return getX() == rectangle.getX() && getY() == rectangle.getY() && getWidth() == rectangle.getWidth() && getHeight() == rectangle.getHeight() && getArcWidth() == rectangle.getArcWidth() && getArcHeight() == rectangle.getArcHeight();
    }

    private static int classify(double coordinate, double minimum, double maximum) {
        if (coordinate < minimum) {
            return 0;
        }
        return coordinate < maximum ? 1 : 2;
    }

    public static class Float extends RoundRectangle2D implements Serializable {
        private static final long serialVersionUID = -3423150618393866922L;
        public float x;
        public float y;
        public float width;
        public float height;
        public float arcwidth;
        public float archeight;

        public Float() {
        }

        public Float(float x, float y, float width, float height, float arcWidth, float arcHeight) {
            setRoundRect(x, y, width, height, arcWidth, arcHeight);
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
        public double getArcWidth() {
            return arcwidth;
        }

        @Override
        public double getArcHeight() {
            return archeight;
        }

        @Override
        public boolean isEmpty() {
            return width <= 0.0f || height <= 0.0f;
        }

        public void setRoundRect(float x, float y, float width, float height, float arcWidth, float arcHeight) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            arcwidth = arcWidth;
            archeight = arcHeight;
        }

        @Override
        public void setRoundRect(double x, double y, double width, double height, double arcWidth, double arcHeight) {
            setRoundRect((float) x, (float) y, (float) width, (float) height, (float) arcWidth, (float) arcHeight);
        }

        @Override
        public void setRoundRect(RoundRectangle2D rectangle) {
            setRoundRect((float) rectangle.getX(), (float) rectangle.getY(), (float) rectangle.getWidth(), (float) rectangle.getHeight(), (float) rectangle.getArcWidth(), (float) rectangle.getArcHeight());
        }

        @Override
        public Rectangle2D getBounds2D() {
            return new Rectangle2D.Float(x, y, width, height);
        }

        @Override
        public Object clone() {
            return new Float(x, y, width, height, arcwidth, archeight);
        }
    }

    public static class Double extends RoundRectangle2D implements Serializable {
        private static final long serialVersionUID = 1048939333485206117L;
        public double x;
        public double y;
        public double width;
        public double height;
        public double arcwidth;
        public double archeight;

        public Double() {
        }

        public Double(double x, double y, double width, double height, double arcWidth, double arcHeight) {
            setRoundRect(x, y, width, height, arcWidth, arcHeight);
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
        public double getArcWidth() {
            return arcwidth;
        }

        @Override
        public double getArcHeight() {
            return archeight;
        }

        @Override
        public boolean isEmpty() {
            return width <= 0.0 || height <= 0.0;
        }

        @Override
        public void setRoundRect(double x, double y, double width, double height, double arcWidth, double arcHeight) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            arcwidth = arcWidth;
            archeight = arcHeight;
        }

        @Override
        public void setRoundRect(RoundRectangle2D rectangle) {
            setRoundRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight(), rectangle.getArcWidth(), rectangle.getArcHeight());
        }

        @Override
        public Rectangle2D getBounds2D() {
            return new Rectangle2D.Double(x, y, width, height);
        }

        @Override
        public Object clone() {
            return new Double(x, y, width, height, arcwidth, archeight);
        }
    }

    private static final class RoundRectangleIterator implements PathIterator {
        private final RoundRectangle2D rectangle;
        private final AffineTransform transform;
        private final double radiusX;
        private final double radiusY;
        private int index;

        private RoundRectangleIterator(RoundRectangle2D rectangle, AffineTransform transform) {
            this.rectangle = rectangle;
            this.transform = transform;
            radiusX = Math.min(rectangle.getWidth(), Math.abs(rectangle.getArcWidth())) / 2.0;
            radiusY = Math.min(rectangle.getHeight(), Math.abs(rectangle.getArcHeight())) / 2.0;
            if (rectangle.getWidth() < 0.0 || rectangle.getHeight() < 0.0) {
                index = 10;
            }
        }

        @Override
        public int getWindingRule() {
            return WIND_NON_ZERO;
        }

        @Override
        public boolean isDone() {
            return index > 9;
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
                throw new NoSuchElementException("round rectangle iterator out of bounds");
            }
            if (index == 9) {
                return SEG_CLOSE;
            }
            double x = rectangle.getX();
            double y = rectangle.getY();
            double right = x + rectangle.getWidth();
            double bottom = y + rectangle.getHeight();
            if (index == 0) {
                setPoint(coordinates, x, y + radiusY);
                transform(coordinates, 1);
                return SEG_MOVETO;
            }
            if ((index & 1) == 1) {
                setLine(coordinates, x, y, right, bottom, index);
                transform(coordinates, 1);
                return SEG_LINETO;
            }
            setCurve(coordinates, x, y, right, bottom, index);
            transform(coordinates, 3);
            return SEG_CUBICTO;
        }

        private void setLine(double[] output, double x, double y, double right, double bottom, int segment) {
            if (segment == 1) {
                setPoint(output, x, bottom - radiusY);
            } else if (segment == 3) {
                setPoint(output, right - radiusX, bottom);
            } else if (segment == 5) {
                setPoint(output, right, y + radiusY);
            } else {
                setPoint(output, x + radiusX, y);
            }
        }

        private void setCurve(double[] output, double x, double y, double right, double bottom, int segment) {
            double controlX = radiusX * CONTROL_VALUE;
            double controlY = radiusY * CONTROL_VALUE;
            if (segment == 2) {
                set(output, x, bottom - radiusY + controlY, x + radiusX - controlX, bottom, x + radiusX, bottom);
            } else if (segment == 4) {
                set(output, right - radiusX + controlX, bottom, right, bottom - radiusY + controlY, right, bottom - radiusY);
            } else if (segment == 6) {
                set(output, right, y + radiusY - controlY, right - radiusX + controlX, y, right - radiusX, y);
            } else {
                set(output, x + radiusX - controlX, y, x, y + radiusY - controlY, x, y + radiusY);
            }
        }

        private void transform(double[] coordinates, int pointCount) {
            if (transform != null) {
                transform.transform(coordinates, 0, coordinates, 0, pointCount);
            }
        }

        private static void setPoint(double[] output, double x, double y) {
            output[0] = x;
            output[1] = y;
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
