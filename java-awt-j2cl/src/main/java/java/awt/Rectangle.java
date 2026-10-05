package java.awt;

import java.awt.geom.Rectangle2D;
import java.io.Serializable;

public class Rectangle extends Rectangle2D implements Shape, Serializable {
    private static final long serialVersionUID = -4345857070255674764L;
    public int x;
    public int y;
    public int width;
    public int height;

    public Rectangle() {
        this(0, 0, 0, 0);
    }

    public Rectangle(Rectangle rectangle) {
        this(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }

    public Rectangle(int x, int y, int width, int height) {
        reshape(x, y, width, height);
    }

    public Rectangle(int width, int height) {
        this(0, 0, width, height);
    }

    public Rectangle(Point location, Dimension size) {
        this(location.x, location.y, size.width, size.height);
    }

    public Rectangle(Point location) {
        this(location.x, location.y, 0, 0);
    }

    public Rectangle(Dimension size) {
        this(0, 0, size.width, size.height);
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
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    @Override
    public Rectangle2D getBounds2D() {
        return new Rectangle(x, y, width, height);
    }

    public void setBounds(Rectangle rectangle) {
        setBounds(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }

    public void setBounds(int x, int y, int width, int height) {
        reshape(x, y, width, height);
    }

    @Override
    public void setRect(double x, double y, double width, double height) {
        int newX = clipFloor(x);
        int newY = clipFloor(y);
        int newWidth = width < 0.0 ? clipFloor(width) : subtractClipped(clipCeil(x + width), newX);
        int newHeight = height < 0.0 ? clipFloor(height) : subtractClipped(clipCeil(y + height), newY);
        reshape(newX, newY, newWidth, newHeight);
    }

    @Deprecated
    public void reshape(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Point getLocation() {
        return new Point(x, y);
    }

    public void setLocation(Point location) {
        setLocation(location.x, location.y);
    }

    public void setLocation(int x, int y) {
        move(x, y);
    }

    @Deprecated
    public void move(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void translate(int deltaX, int deltaY) {
        Translation horizontal = translateAxis(x, width, deltaX);
        Translation vertical = translateAxis(y, height, deltaY);
        x = horizontal.position;
        width = horizontal.size;
        y = vertical.position;
        height = vertical.size;
    }

    public Dimension getSize() {
        return new Dimension(width, height);
    }

    public void setSize(Dimension size) {
        setSize(size.width, size.height);
    }

    public void setSize(int width, int height) {
        resize(width, height);
    }

    @Deprecated
    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public boolean contains(Point point) {
        return contains(point.x, point.y);
    }

    public boolean contains(int x, int y) {
        return inside(x, y);
    }

    public boolean contains(Rectangle rectangle) {
        return contains(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }

    public boolean contains(int x, int y, int width, int height) {
        if (this.width < 0 || this.height < 0 || width < 0 || height < 0) {
            return false;
        }
        long ownMaxX = (long) this.x + this.width;
        long ownMaxY = (long) this.y + this.height;
        return x >= this.x && y >= this.y && x < ownMaxX && y < ownMaxY && (long) x + width <= ownMaxX && (long) y + height <= ownMaxY;
    }

    @Deprecated
    public boolean inside(int x, int y) {
        if (width < 0 || height < 0) {
            return false;
        }
        return x >= this.x && y >= this.y && (long) x < (long) this.x + width && (long) y < (long) this.y + height;
    }

    public boolean intersects(Rectangle rectangle) {
        if (width <= 0 || height <= 0 || rectangle.width <= 0 || rectangle.height <= 0) {
            return false;
        }
        return (long) rectangle.x + rectangle.width > x && (long) rectangle.y + rectangle.height > y && rectangle.x < (long) x + width && rectangle.y < (long) y + height;
    }

    public Rectangle intersection(Rectangle rectangle) {
        long minX = Math.max(x, rectangle.x);
        long minY = Math.max(y, rectangle.y);
        long maxX = Math.min((long) x + width, (long) rectangle.x + rectangle.width);
        long maxY = Math.min((long) y + height, (long) rectangle.y + rectangle.height);
        return new Rectangle((int) minX, (int) minY, clipLong(maxX - minX), clipLong(maxY - minY));
    }

    public Rectangle union(Rectangle rectangle) {
        if (width < 0 || height < 0) {
            return new Rectangle(rectangle);
        }
        if (rectangle.width < 0 || rectangle.height < 0) {
            return new Rectangle(this);
        }
        long minX = Math.min(x, rectangle.x);
        long minY = Math.min(y, rectangle.y);
        long maxX = Math.max((long) x + width, (long) rectangle.x + rectangle.width);
        long maxY = Math.max((long) y + height, (long) rectangle.y + rectangle.height);
        return new Rectangle((int) minX, (int) minY, clipLong(maxX - minX), clipLong(maxY - minY));
    }

    public void add(int newX, int newY) {
        if (width < 0 || height < 0) {
            reshape(newX, newY, 0, 0);
            return;
        }
        long minX = Math.min(x, newX);
        long minY = Math.min(y, newY);
        long maxX = Math.max((long) x + width, newX);
        long maxY = Math.max((long) y + height, newY);
        reshape((int) minX, (int) minY, clipLong(maxX - minX), clipLong(maxY - minY));
    }

    public void add(Point point) {
        add(point.x, point.y);
    }

    public void add(Rectangle rectangle) {
        Rectangle result = union(rectangle);
        reshape(result.x, result.y, result.width, result.height);
    }

    public void grow(int horizontal, int vertical) {
        long minX = (long) x - horizontal;
        long minY = (long) y - vertical;
        long maxX = (long) x + width + horizontal;
        long maxY = (long) y + height + vertical;
        x = clipLong(minX);
        y = clipLong(minY);
        width = clipLong(maxX - x);
        height = clipLong(maxY - y);
    }

    @Override
    public boolean isEmpty() {
        return width <= 0 || height <= 0;
    }

    @Override
    public int outcode(double pointX, double pointY) {
        int code = 0;
        if (width <= 0) {
            code |= OUT_LEFT | OUT_RIGHT;
        } else if (pointX < x) {
            code |= OUT_LEFT;
        } else if (pointX > (double) x + width) {
            code |= OUT_RIGHT;
        }
        if (height <= 0) {
            code |= OUT_TOP | OUT_BOTTOM;
        } else if (pointY < y) {
            code |= OUT_TOP;
        } else if (pointY > (double) y + height) {
            code |= OUT_BOTTOM;
        }
        return code;
    }

    @Override
    public Rectangle2D createIntersection(Rectangle2D rectangle) {
        if (rectangle instanceof Rectangle) {
            return intersection((Rectangle) rectangle);
        }
        Rectangle2D result = new Rectangle2D.Double();
        Rectangle2D.intersect(this, rectangle, result);
        return result;
    }

    @Override
    public Rectangle2D createUnion(Rectangle2D rectangle) {
        if (rectangle instanceof Rectangle) {
            return union((Rectangle) rectangle);
        }
        Rectangle2D result = new Rectangle2D.Double();
        Rectangle2D.union(this, rectangle, result);
        return result;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Rectangle)) {
            return false;
        }
        Rectangle rectangle = (Rectangle) object;
        return x == rectangle.x && y == rectangle.y && width == rectangle.width && height == rectangle.height;
    }

    @Override
    public Object clone() {
        return new Rectangle(x, y, width, height);
    }

    @Override
    public String toString() {
        return getClass().getName() + "[x=" + x + ",y=" + y + ",width=" + width + ",height=" + height + "]";
    }

    private static int clipFloor(double value) {
        if (value <= Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }
        if (value >= Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int) Math.floor(value);
    }

    private static int clipCeil(double value) {
        if (value <= Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }
        if (value >= Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int) Math.ceil(value);
    }

    private static int subtractClipped(int maximum, int minimum) {
        return clipLong((long) maximum - minimum);
    }

    private static int clipLong(long value) {
        if (value < Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }
        if (value > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int) value;
    }

    private static Translation translateAxis(int position, int size, int delta) {
        int translatedPosition = position + delta;
        if (delta < 0 && translatedPosition > position) {
            int translatedSize = size < 0 ? size : size + translatedPosition - Integer.MIN_VALUE;
            return new Translation(Integer.MIN_VALUE, translatedSize);
        }
        if (delta > 0 && translatedPosition < position) {
            int translatedSize = size < 0 ? size : size + translatedPosition - Integer.MAX_VALUE;
            return new Translation(Integer.MAX_VALUE, translatedSize < 0 ? Integer.MAX_VALUE : translatedSize);
        }
        return new Translation(translatedPosition, size);
    }

    private static final class Translation {
        private final int position;
        private final int size;

        private Translation(int position, int size) {
            this.position = position;
            this.size = size;
        }
    }
}
