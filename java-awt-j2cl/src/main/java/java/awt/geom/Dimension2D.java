package java.awt.geom;

public abstract class Dimension2D implements Cloneable {
    protected Dimension2D() {
    }

    public abstract double getWidth();

    public abstract double getHeight();

    public abstract void setSize(double width, double height);

    public void setSize(Dimension2D dimension) {
        setSize(dimension.getWidth(), dimension.getHeight());
    }

    public Object clone() {
        return new ImmutableDimension2D(getWidth(), getHeight());
    }

    private static final class ImmutableDimension2D extends Dimension2D {
        private double width;
        private double height;

        private ImmutableDimension2D(double width, double height) {
            this.width = width;
            this.height = height;
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
        public void setSize(double width, double height) {
            this.width = width;
            this.height = height;
        }
    }
}
