package java.awt.geom;

import java.awt.Rectangle;
import java.awt.Shape;
import java.io.Serializable;
import java.util.NoSuchElementException;

public abstract class QuadCurve2D implements Shape, Cloneable {
    protected QuadCurve2D() {
    }

    public abstract double getX1();

    public abstract double getY1();

    public abstract Point2D getP1();

    public abstract double getCtrlX();

    public abstract double getCtrlY();

    public abstract Point2D getCtrlPt();

    public abstract double getX2();

    public abstract double getY2();

    public abstract Point2D getP2();

    public abstract void setCurve(double x1, double y1, double controlX, double controlY, double x2, double y2);

    public void setCurve(double[] coordinates, int offset) {
        setCurve(coordinates[offset], coordinates[offset + 1], coordinates[offset + 2], coordinates[offset + 3], coordinates[offset + 4], coordinates[offset + 5]);
    }

    public void setCurve(Point2D first, Point2D control, Point2D second) {
        setCurve(first.getX(), first.getY(), control.getX(), control.getY(), second.getX(), second.getY());
    }

    public void setCurve(Point2D[] points, int offset) {
        setCurve(points[offset], points[offset + 1], points[offset + 2]);
    }

    public void setCurve(QuadCurve2D curve) {
        setCurve(curve.getX1(), curve.getY1(), curve.getCtrlX(), curve.getCtrlY(), curve.getX2(), curve.getY2());
    }

    public static double getFlatnessSq(double x1, double y1, double controlX, double controlY, double x2, double y2) {
        return Line2D.ptSegDistSq(x1, y1, x2, y2, controlX, controlY);
    }

    public static double getFlatness(double x1, double y1, double controlX, double controlY, double x2, double y2) {
        return Math.sqrt(getFlatnessSq(x1, y1, controlX, controlY, x2, y2));
    }

    public static double getFlatnessSq(double[] coordinates, int offset) {
        return getFlatnessSq(coordinates[offset], coordinates[offset + 1], coordinates[offset + 2], coordinates[offset + 3], coordinates[offset + 4], coordinates[offset + 5]);
    }

    public static double getFlatness(double[] coordinates, int offset) {
        return Math.sqrt(getFlatnessSq(coordinates, offset));
    }

    public double getFlatnessSq() {
        return getFlatnessSq(getX1(), getY1(), getCtrlX(), getCtrlY(), getX2(), getY2());
    }

    public double getFlatness() {
        return Math.sqrt(getFlatnessSq());
    }

    public void subdivide(QuadCurve2D left, QuadCurve2D right) {
        subdivide(this, left, right);
    }

    public static void subdivide(QuadCurve2D source, QuadCurve2D left, QuadCurve2D right) {
        double x1 = source.getX1();
        double y1 = source.getY1();
        double controlX = source.getCtrlX();
        double controlY = source.getCtrlY();
        double x2 = source.getX2();
        double y2 = source.getY2();
        double leftControlX = (x1 + controlX) / 2.0;
        double leftControlY = (y1 + controlY) / 2.0;
        double rightControlX = (x2 + controlX) / 2.0;
        double rightControlY = (y2 + controlY) / 2.0;
        double centerX = (leftControlX + rightControlX) / 2.0;
        double centerY = (leftControlY + rightControlY) / 2.0;
        if (left != null) {
            left.setCurve(x1, y1, leftControlX, leftControlY, centerX, centerY);
        }
        if (right != null) {
            right.setCurve(centerX, centerY, rightControlX, rightControlY, x2, y2);
        }
    }

    public static void subdivide(double[] source, int sourceOffset, double[] left, int leftOffset, double[] right, int rightOffset) {
        double x1 = source[sourceOffset];
        double y1 = source[sourceOffset + 1];
        double controlX = source[sourceOffset + 2];
        double controlY = source[sourceOffset + 3];
        double x2 = source[sourceOffset + 4];
        double y2 = source[sourceOffset + 5];
        double leftControlX = (x1 + controlX) / 2.0;
        double leftControlY = (y1 + controlY) / 2.0;
        double rightControlX = (x2 + controlX) / 2.0;
        double rightControlY = (y2 + controlY) / 2.0;
        double centerX = (leftControlX + rightControlX) / 2.0;
        double centerY = (leftControlY + rightControlY) / 2.0;
        if (left != null) {
            left[leftOffset] = x1;
            left[leftOffset + 1] = y1;
            left[leftOffset + 2] = leftControlX;
            left[leftOffset + 3] = leftControlY;
            left[leftOffset + 4] = centerX;
            left[leftOffset + 5] = centerY;
        }
        if (right != null) {
            right[rightOffset] = centerX;
            right[rightOffset + 1] = centerY;
            right[rightOffset + 2] = rightControlX;
            right[rightOffset + 3] = rightControlY;
            right[rightOffset + 4] = x2;
            right[rightOffset + 5] = y2;
        }
    }

    public static int solveQuadratic(double[] equation) {
        return solveQuadratic(equation, equation);
    }

    public static int solveQuadratic(double[] equation, double[] results) {
        double constant = equation[0];
        double linear = equation[1];
        double quadratic = equation[2];
        if (quadratic == 0.0) {
            if (linear == 0.0) {
                return -1;
            }
            results[0] = -constant / linear;
            return 1;
        }
        double discriminant = linear * linear - 4.0 * quadratic * constant;
        if (discriminant < 0.0) {
            return 0;
        }
        double root = Math.sqrt(discriminant);
        if (linear < 0.0) {
            root = -root;
        }
        double stable = (linear + root) / -2.0;
        int count = 0;
        results[count++] = stable / quadratic;
        if (stable != 0.0) {
            results[count++] = constant / stable;
        }
        return count;
    }

    @Override
    public boolean contains(double x, double y) {
        return asPath().contains(x, y);
    }

    @Override
    public boolean contains(Point2D point) {
        return contains(point.getX(), point.getY());
    }

    @Override
    public boolean intersects(double x, double y, double width, double height) {
        return asPath().intersects(x, y, width, height);
    }

    @Override
    public boolean intersects(Rectangle2D rectangle) {
        return intersects(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
    }

    @Override
    public boolean contains(double x, double y, double width, double height) {
        return asPath().contains(x, y, width, height);
    }

    @Override
    public boolean contains(Rectangle2D rectangle) {
        return contains(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
    }

    @Override
    public Rectangle getBounds() {
        return getBounds2D().getBounds();
    }

    @Override
    public PathIterator getPathIterator(AffineTransform transform) {
        return new QuadIterator(this, transform);
    }

    @Override
    public PathIterator getPathIterator(AffineTransform transform, double flatness) {
        return new FlatteningPathIterator(getPathIterator(transform), flatness);
    }

    public Object clone() {
        if (this instanceof Float) {
            return new Float((float) getX1(), (float) getY1(), (float) getCtrlX(), (float) getCtrlY(), (float) getX2(), (float) getY2());
        }
        return new Double(getX1(), getY1(), getCtrlX(), getCtrlY(), getX2(), getY2());
    }

    private Path2D asPath() {
        Path2D.Double path = new Path2D.Double();
        path.moveTo(getX1(), getY1());
        path.quadTo(getCtrlX(), getCtrlY(), getX2(), getY2());
        path.closePath();
        return path;
    }

    public static class Float extends QuadCurve2D implements Serializable {
        private static final long serialVersionUID = -8511188402130719609L;
        public float x1;
        public float y1;
        public float ctrlx;
        public float ctrly;
        public float x2;
        public float y2;

        public Float() {
        }

        public Float(float x1, float y1, float controlX, float controlY, float x2, float y2) {
            setCurve(x1, y1, controlX, controlY, x2, y2);
        }

        @Override
        public double getX1() {
            return x1;
        }

        @Override
        public double getY1() {
            return y1;
        }

        @Override
        public Point2D getP1() {
            return new Point2D.Float(x1, y1);
        }

        @Override
        public double getCtrlX() {
            return ctrlx;
        }

        @Override
        public double getCtrlY() {
            return ctrly;
        }

        @Override
        public Point2D getCtrlPt() {
            return new Point2D.Float(ctrlx, ctrly);
        }

        @Override
        public double getX2() {
            return x2;
        }

        @Override
        public double getY2() {
            return y2;
        }

        @Override
        public Point2D getP2() {
            return new Point2D.Float(x2, y2);
        }

        @Override
        public void setCurve(double x1, double y1, double controlX, double controlY, double x2, double y2) {
            setCurve((float) x1, (float) y1, (float) controlX, (float) controlY, (float) x2, (float) y2);
        }

        public void setCurve(float x1, float y1, float controlX, float controlY, float x2, float y2) {
            this.x1 = x1;
            this.y1 = y1;
            ctrlx = controlX;
            ctrly = controlY;
            this.x2 = x2;
            this.y2 = y2;
        }

        @Override
        public Rectangle2D getBounds2D() {
            float x = Math.min(Math.min(x1, x2), ctrlx);
            float y = Math.min(Math.min(y1, y2), ctrly);
            return new Rectangle2D.Float(x, y, Math.max(Math.max(x1, x2), ctrlx) - x, Math.max(Math.max(y1, y2), ctrly) - y);
        }
    }

    public static class Double extends QuadCurve2D implements Serializable {
        private static final long serialVersionUID = 4217149928428559721L;
        public double x1;
        public double y1;
        public double ctrlx;
        public double ctrly;
        public double x2;
        public double y2;

        public Double() {
        }

        public Double(double x1, double y1, double controlX, double controlY, double x2, double y2) {
            setCurve(x1, y1, controlX, controlY, x2, y2);
        }

        @Override
        public double getX1() {
            return x1;
        }

        @Override
        public double getY1() {
            return y1;
        }

        @Override
        public Point2D getP1() {
            return new Point2D.Double(x1, y1);
        }

        @Override
        public double getCtrlX() {
            return ctrlx;
        }

        @Override
        public double getCtrlY() {
            return ctrly;
        }

        @Override
        public Point2D getCtrlPt() {
            return new Point2D.Double(ctrlx, ctrly);
        }

        @Override
        public double getX2() {
            return x2;
        }

        @Override
        public double getY2() {
            return y2;
        }

        @Override
        public Point2D getP2() {
            return new Point2D.Double(x2, y2);
        }

        @Override
        public void setCurve(double x1, double y1, double controlX, double controlY, double x2, double y2) {
            this.x1 = x1;
            this.y1 = y1;
            ctrlx = controlX;
            ctrly = controlY;
            this.x2 = x2;
            this.y2 = y2;
        }

        @Override
        public Rectangle2D getBounds2D() {
            double x = Math.min(Math.min(x1, x2), ctrlx);
            double y = Math.min(Math.min(y1, y2), ctrly);
            return new Rectangle2D.Double(x, y, Math.max(Math.max(x1, x2), ctrlx) - x, Math.max(Math.max(y1, y2), ctrly) - y);
        }
    }

    private static final class QuadIterator implements PathIterator {
        private final QuadCurve2D curve;
        private final AffineTransform transform;
        private int index;

        private QuadIterator(QuadCurve2D curve, AffineTransform transform) {
            this.curve = curve;
            this.transform = transform;
        }

        @Override
        public int getWindingRule() {
            return WIND_NON_ZERO;
        }

        @Override
        public boolean isDone() {
            return index > 1;
        }

        @Override
        public void next() {
            index++;
        }

        @Override
        public int currentSegment(float[] output) {
            double[] values = new double[4];
            int type = currentSegment(values);
            int count = type == SEG_MOVETO ? 2 : 4;
            for (int valueIndex = 0; valueIndex < count; valueIndex++) {
                output[valueIndex] = (float) values[valueIndex];
            }
            return type;
        }

        @Override
        public int currentSegment(double[] output) {
            if (isDone()) {
                throw new NoSuchElementException("quadratic iterator out of bounds");
            }
            int count;
            int type;
            if (index == 0) {
                output[0] = curve.getX1();
                output[1] = curve.getY1();
                count = 1;
                type = SEG_MOVETO;
            } else {
                output[0] = curve.getCtrlX();
                output[1] = curve.getCtrlY();
                output[2] = curve.getX2();
                output[3] = curve.getY2();
                count = 2;
                type = SEG_QUADTO;
            }
            if (transform != null) {
                transform.transform(output, 0, output, 0, count);
            }
            return type;
        }
    }
}
