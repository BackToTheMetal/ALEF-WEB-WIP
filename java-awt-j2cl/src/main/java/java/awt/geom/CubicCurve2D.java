package java.awt.geom;

import java.awt.Rectangle;
import java.awt.Shape;
import java.io.Serializable;
import java.util.NoSuchElementException;

public abstract class CubicCurve2D implements Shape, Cloneable {
    protected CubicCurve2D() {
    }

    public abstract double getX1();

    public abstract double getY1();

    public abstract Point2D getP1();

    public abstract double getCtrlX1();

    public abstract double getCtrlY1();

    public abstract Point2D getCtrlP1();

    public abstract double getCtrlX2();

    public abstract double getCtrlY2();

    public abstract Point2D getCtrlP2();

    public abstract double getX2();

    public abstract double getY2();

    public abstract Point2D getP2();

    public abstract void setCurve(double x1, double y1, double controlX1, double controlY1, double controlX2, double controlY2, double x2, double y2);

    public void setCurve(double[] coordinates, int offset) {
        setCurve(coordinates[offset], coordinates[offset + 1], coordinates[offset + 2], coordinates[offset + 3], coordinates[offset + 4], coordinates[offset + 5], coordinates[offset + 6], coordinates[offset + 7]);
    }

    public void setCurve(Point2D first, Point2D firstControl, Point2D secondControl, Point2D second) {
        setCurve(first.getX(), first.getY(), firstControl.getX(), firstControl.getY(), secondControl.getX(), secondControl.getY(), second.getX(), second.getY());
    }

    public void setCurve(Point2D[] points, int offset) {
        setCurve(points[offset], points[offset + 1], points[offset + 2], points[offset + 3]);
    }

    public void setCurve(CubicCurve2D curve) {
        setCurve(curve.getX1(), curve.getY1(), curve.getCtrlX1(), curve.getCtrlY1(), curve.getCtrlX2(), curve.getCtrlY2(), curve.getX2(), curve.getY2());
    }

    public static double getFlatnessSq(double x1, double y1, double controlX1, double controlY1, double controlX2, double controlY2, double x2, double y2) {
        return Math.max(Line2D.ptSegDistSq(x1, y1, x2, y2, controlX1, controlY1), Line2D.ptSegDistSq(x1, y1, x2, y2, controlX2, controlY2));
    }

    public static double getFlatness(double x1, double y1, double controlX1, double controlY1, double controlX2, double controlY2, double x2, double y2) {
        return Math.sqrt(getFlatnessSq(x1, y1, controlX1, controlY1, controlX2, controlY2, x2, y2));
    }

    public static double getFlatnessSq(double[] coordinates, int offset) {
        return getFlatnessSq(coordinates[offset], coordinates[offset + 1], coordinates[offset + 2], coordinates[offset + 3], coordinates[offset + 4], coordinates[offset + 5], coordinates[offset + 6], coordinates[offset + 7]);
    }

    public static double getFlatness(double[] coordinates, int offset) {
        return Math.sqrt(getFlatnessSq(coordinates, offset));
    }

    public double getFlatnessSq() {
        return getFlatnessSq(getX1(), getY1(), getCtrlX1(), getCtrlY1(), getCtrlX2(), getCtrlY2(), getX2(), getY2());
    }

    public double getFlatness() {
        return Math.sqrt(getFlatnessSq());
    }

    public void subdivide(CubicCurve2D left, CubicCurve2D right) {
        subdivide(this, left, right);
    }

    public static void subdivide(CubicCurve2D source, CubicCurve2D left, CubicCurve2D right) {
        double[] values = {source.getX1(), source.getY1(), source.getCtrlX1(), source.getCtrlY1(), source.getCtrlX2(), source.getCtrlY2(), source.getX2(), source.getY2()};
        double[] leftValues = left == null ? null : new double[8];
        double[] rightValues = right == null ? null : new double[8];
        subdivide(values, 0, leftValues, 0, rightValues, 0);
        if (left != null) {
            left.setCurve(leftValues, 0);
        }
        if (right != null) {
            right.setCurve(rightValues, 0);
        }
    }

    public static void subdivide(double[] source, int sourceOffset, double[] left, int leftOffset, double[] right, int rightOffset) {
        double x1 = source[sourceOffset];
        double y1 = source[sourceOffset + 1];
        double controlX1 = source[sourceOffset + 2];
        double controlY1 = source[sourceOffset + 3];
        double controlX2 = source[sourceOffset + 4];
        double controlY2 = source[sourceOffset + 5];
        double x2 = source[sourceOffset + 6];
        double y2 = source[sourceOffset + 7];
        double firstX = (x1 + controlX1) / 2.0;
        double firstY = (y1 + controlY1) / 2.0;
        double middleX = (controlX1 + controlX2) / 2.0;
        double middleY = (controlY1 + controlY2) / 2.0;
        double lastX = (controlX2 + x2) / 2.0;
        double lastY = (controlY2 + y2) / 2.0;
        double leftControlX = (firstX + middleX) / 2.0;
        double leftControlY = (firstY + middleY) / 2.0;
        double rightControlX = (middleX + lastX) / 2.0;
        double rightControlY = (middleY + lastY) / 2.0;
        double centerX = (leftControlX + rightControlX) / 2.0;
        double centerY = (leftControlY + rightControlY) / 2.0;
        if (left != null) {
            left[leftOffset] = x1;
            left[leftOffset + 1] = y1;
            left[leftOffset + 2] = firstX;
            left[leftOffset + 3] = firstY;
            left[leftOffset + 4] = leftControlX;
            left[leftOffset + 5] = leftControlY;
            left[leftOffset + 6] = centerX;
            left[leftOffset + 7] = centerY;
        }
        if (right != null) {
            right[rightOffset] = centerX;
            right[rightOffset + 1] = centerY;
            right[rightOffset + 2] = rightControlX;
            right[rightOffset + 3] = rightControlY;
            right[rightOffset + 4] = lastX;
            right[rightOffset + 5] = lastY;
            right[rightOffset + 6] = x2;
            right[rightOffset + 7] = y2;
        }
    }

    public static int solveCubic(double[] equation) {
        return solveCubic(equation, equation);
    }

    public static int solveCubic(double[] equation, double[] results) {
        double cubic = equation[3];
        if (cubic == 0.0) {
            return QuadCurve2D.solveQuadratic(equation, results);
        }
        double quadratic = equation[2] / cubic;
        double linear = equation[1] / cubic;
        double constant = equation[0] / cubic;
        double offset = quadratic / 3.0;
        double p = linear - quadratic * quadratic / 3.0;
        double q = 2.0 * quadratic * quadratic * quadratic / 27.0 - quadratic * linear / 3.0 + constant;
        double discriminant = q * q / 4.0 + p * p * p / 27.0;
        if (discriminant > 0.0) {
            double squareRoot = Math.sqrt(discriminant);
            results[0] = Math.cbrt(-q / 2.0 + squareRoot) + Math.cbrt(-q / 2.0 - squareRoot) - offset;
            return 1;
        }
        if (discriminant == 0.0) {
            double root = Math.cbrt(-q / 2.0);
            results[0] = 2.0 * root - offset;
            if (root == 0.0) {
                return 1;
            }
            results[1] = -root - offset;
            return 2;
        }
        double radius = 2.0 * Math.sqrt(-p / 3.0);
        double angle = Math.acos(3.0 * q / (2.0 * p) * Math.sqrt(-3.0 / p)) / 3.0;
        results[0] = radius * Math.cos(angle) - offset;
        results[1] = radius * Math.cos(angle - 2.0 * Math.PI / 3.0) - offset;
        results[2] = radius * Math.cos(angle - 4.0 * Math.PI / 3.0) - offset;
        return 3;
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
        return new CubicIterator(this, transform);
    }

    @Override
    public PathIterator getPathIterator(AffineTransform transform, double flatness) {
        return new FlatteningPathIterator(getPathIterator(transform), flatness);
    }

    public Object clone() {
        if (this instanceof Float) {
            return new Float((float) getX1(), (float) getY1(), (float) getCtrlX1(), (float) getCtrlY1(), (float) getCtrlX2(), (float) getCtrlY2(), (float) getX2(), (float) getY2());
        }
        return new Double(getX1(), getY1(), getCtrlX1(), getCtrlY1(), getCtrlX2(), getCtrlY2(), getX2(), getY2());
    }

    private Path2D asPath() {
        Path2D.Double path = new Path2D.Double();
        path.moveTo(getX1(), getY1());
        path.curveTo(getCtrlX1(), getCtrlY1(), getCtrlX2(), getCtrlY2(), getX2(), getY2());
        path.closePath();
        return path;
    }

    public static class Float extends CubicCurve2D implements Serializable {
        private static final long serialVersionUID = -1272015596714244385L;
        public float x1;
        public float y1;
        public float ctrlx1;
        public float ctrly1;
        public float ctrlx2;
        public float ctrly2;
        public float x2;
        public float y2;

        public Float() {
        }

        public Float(float x1, float y1, float controlX1, float controlY1, float controlX2, float controlY2, float x2, float y2) {
            setCurve(x1, y1, controlX1, controlY1, controlX2, controlY2, x2, y2);
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
        public double getCtrlX1() {
            return ctrlx1;
        }

        @Override
        public double getCtrlY1() {
            return ctrly1;
        }

        @Override
        public Point2D getCtrlP1() {
            return new Point2D.Float(ctrlx1, ctrly1);
        }

        @Override
        public double getCtrlX2() {
            return ctrlx2;
        }

        @Override
        public double getCtrlY2() {
            return ctrly2;
        }

        @Override
        public Point2D getCtrlP2() {
            return new Point2D.Float(ctrlx2, ctrly2);
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
        public void setCurve(double x1, double y1, double controlX1, double controlY1, double controlX2, double controlY2, double x2, double y2) {
            setCurve((float) x1, (float) y1, (float) controlX1, (float) controlY1, (float) controlX2, (float) controlY2, (float) x2, (float) y2);
        }

        public void setCurve(float x1, float y1, float controlX1, float controlY1, float controlX2, float controlY2, float x2, float y2) {
            this.x1 = x1;
            this.y1 = y1;
            ctrlx1 = controlX1;
            ctrly1 = controlY1;
            ctrlx2 = controlX2;
            ctrly2 = controlY2;
            this.x2 = x2;
            this.y2 = y2;
        }

        @Override
        public Rectangle2D getBounds2D() {
            float x = Math.min(Math.min(x1, x2), Math.min(ctrlx1, ctrlx2));
            float y = Math.min(Math.min(y1, y2), Math.min(ctrly1, ctrly2));
            return new Rectangle2D.Float(x, y, Math.max(Math.max(x1, x2), Math.max(ctrlx1, ctrlx2)) - x, Math.max(Math.max(y1, y2), Math.max(ctrly1, ctrly2)) - y);
        }
    }

    public static class Double extends CubicCurve2D implements Serializable {
        private static final long serialVersionUID = 2206249238917811803L;
        public double x1;
        public double y1;
        public double ctrlx1;
        public double ctrly1;
        public double ctrlx2;
        public double ctrly2;
        public double x2;
        public double y2;

        public Double() {
        }

        public Double(double x1, double y1, double controlX1, double controlY1, double controlX2, double controlY2, double x2, double y2) {
            setCurve(x1, y1, controlX1, controlY1, controlX2, controlY2, x2, y2);
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
        public double getCtrlX1() {
            return ctrlx1;
        }

        @Override
        public double getCtrlY1() {
            return ctrly1;
        }

        @Override
        public Point2D getCtrlP1() {
            return new Point2D.Double(ctrlx1, ctrly1);
        }

        @Override
        public double getCtrlX2() {
            return ctrlx2;
        }

        @Override
        public double getCtrlY2() {
            return ctrly2;
        }

        @Override
        public Point2D getCtrlP2() {
            return new Point2D.Double(ctrlx2, ctrly2);
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
        public void setCurve(double x1, double y1, double controlX1, double controlY1, double controlX2, double controlY2, double x2, double y2) {
            this.x1 = x1;
            this.y1 = y1;
            ctrlx1 = controlX1;
            ctrly1 = controlY1;
            ctrlx2 = controlX2;
            ctrly2 = controlY2;
            this.x2 = x2;
            this.y2 = y2;
        }

        @Override
        public Rectangle2D getBounds2D() {
            double x = Math.min(Math.min(x1, x2), Math.min(ctrlx1, ctrlx2));
            double y = Math.min(Math.min(y1, y2), Math.min(ctrly1, ctrly2));
            return new Rectangle2D.Double(x, y, Math.max(Math.max(x1, x2), Math.max(ctrlx1, ctrlx2)) - x, Math.max(Math.max(y1, y2), Math.max(ctrly1, ctrly2)) - y);
        }
    }

    private static final class CubicIterator implements PathIterator {
        private final CubicCurve2D curve;
        private final AffineTransform transform;
        private int index;

        private CubicIterator(CubicCurve2D curve, AffineTransform transform) {
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
            double[] values = new double[6];
            int type = currentSegment(values);
            int count = type == SEG_MOVETO ? 2 : 6;
            for (int valueIndex = 0; valueIndex < count; valueIndex++) {
                output[valueIndex] = (float) values[valueIndex];
            }
            return type;
        }

        @Override
        public int currentSegment(double[] output) {
            if (isDone()) {
                throw new NoSuchElementException("cubic iterator out of bounds");
            }
            int count;
            int type;
            if (index == 0) {
                output[0] = curve.getX1();
                output[1] = curve.getY1();
                count = 1;
                type = SEG_MOVETO;
            } else {
                output[0] = curve.getCtrlX1();
                output[1] = curve.getCtrlY1();
                output[2] = curve.getCtrlX2();
                output[3] = curve.getCtrlY2();
                output[4] = curve.getX2();
                output[5] = curve.getY2();
                count = 3;
                type = SEG_CUBICTO;
            }
            if (transform != null) {
                transform.transform(output, 0, output, 0, count);
            }
            return type;
        }
    }
}
