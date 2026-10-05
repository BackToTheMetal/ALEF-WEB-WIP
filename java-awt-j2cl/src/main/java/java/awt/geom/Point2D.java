package java.awt.geom;

import java.io.Serializable;

public abstract class Point2D implements Cloneable {
    protected Point2D() {
    }

    public abstract double getX();

    public abstract double getY();

    public abstract void setLocation(double x, double y);

    public void setLocation(Point2D point) {
        setLocation(point.getX(), point.getY());
    }

    public static double distanceSq(double x1, double y1, double x2, double y2) {
        double deltaX = x1 - x2;
        double deltaY = y1 - y2;
        return deltaX * deltaX + deltaY * deltaY;
    }

    public static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(distanceSq(x1, y1, x2, y2));
    }

    public double distanceSq(double x, double y) {
        return distanceSq(getX(), getY(), x, y);
    }

    public double distanceSq(Point2D point) {
        return distanceSq(getX(), getY(), point.getX(), point.getY());
    }

    public double distance(double x, double y) {
        return Math.sqrt(distanceSq(x, y));
    }

    public double distance(Point2D point) {
        return Math.sqrt(distanceSq(point));
    }

    public Object clone() {
        return new Point2D.Double(getX(), getY());
    }

    @Override
    public int hashCode() {
        long bits = java.lang.Double.doubleToLongBits(getX());
        bits ^= java.lang.Double.doubleToLongBits(getY()) * 31;
        return (int) bits ^ (int) (bits >> 32);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Point2D)) {
            return false;
        }
        Point2D point = (Point2D) object;
        return getX() == point.getX() && getY() == point.getY();
    }

    public static class Float extends Point2D implements Serializable {
        private static final long serialVersionUID = -2870572449815403710L;
        public float x;
        public float y;

        public Float() {
        }

        public Float(float x, float y) {
            this.x = x;
            this.y = y;
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
        public void setLocation(double x, double y) {
            this.x = (float) x;
            this.y = (float) y;
        }

        public void setLocation(float x, float y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public Object clone() {
            return new Float(x, y);
        }

        @Override
        public String toString() {
            return "Point2D.Float[" + GeometryStrings.format(x) + ", " + GeometryStrings.format(y) + "]";
        }
    }

    public static class Double extends Point2D implements Serializable {
        private static final long serialVersionUID = 6150783262733311327L;
        public double x;
        public double y;

        public Double() {
        }

        public Double(double x, double y) {
            this.x = x;
            this.y = y;
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
        public void setLocation(double x, double y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public Object clone() {
            return new Double(x, y);
        }

        @Override
        public String toString() {
            return "Point2D.Double[" + GeometryStrings.format(x) + ", " + GeometryStrings.format(y) + "]";
        }
    }
}
