package java.awt;

import java.awt.image.ImageObserver;
import java.text.AttributedCharacterIterator;

public abstract class Graphics {
    protected Graphics() {
    }

    public abstract Graphics create();

    public Graphics create(int x, int y, int width, int height) {
        Graphics copy = create();
        copy.translate(x, y);
        copy.clipRect(0, 0, width, height);
        return copy;
    }

    public abstract void translate(int x, int y);

    public abstract Color getColor();

    public abstract void setColor(Color color);

    public abstract void setPaintMode();

    public abstract void setXORMode(Color color);

    public abstract Font getFont();

    public abstract void setFont(Font font);

    public abstract FontMetrics getFontMetrics(Font font);

    public FontMetrics getFontMetrics() {
        return getFontMetrics(getFont());
    }

    public abstract Rectangle getClipBounds();

    public abstract void clipRect(int x, int y, int width, int height);

    public abstract void setClip(int x, int y, int width, int height);

    public abstract Shape getClip();

    public abstract void setClip(Shape clip);

    public abstract void copyArea(int x, int y, int width, int height, int deltaX, int deltaY);

    public abstract void drawLine(int x1, int y1, int x2, int y2);

    public abstract void fillRect(int x, int y, int width, int height);

    public void draw3DRect(int x, int y, int width, int height, boolean raised) {
        if (width < 0 || height < 0) { return; }
        Color color = getColor();
        Color brighter = color.brighter();
        Color darker = color.darker();
        setColor(raised ? brighter : darker);
        fillRect(x, y, 1, height + 1);
        fillRect(x + 1, y, Math.max(0, width - 1), 1);
        setColor(raised ? darker : brighter);
        fillRect(x + 1, y + height, width, 1);
        fillRect(x + width, y, 1, height);
        setColor(color);
    }

    public void fill3DRect(int x, int y, int width, int height, boolean raised) {
        Color color = getColor();
        Color brighter = color.brighter();
        Color darker = color.darker();
        if (!raised) {
            setColor(darker);
        }
        fillRect(x + 1, y + 1, width - 2, height - 2);
        setColor(raised ? brighter : darker);
        fillRect(x, y, 1, height);
        fillRect(x + 1, y, width - 2, 1);
        setColor(raised ? darker : brighter);
        fillRect(x + 1, y + height - 1, width - 1, 1);
        fillRect(x + width - 1, y, 1, height - 1);
        setColor(color);
    }

    public void drawRect(int x, int y, int width, int height) {
        if (width < 0 || height < 0) {
            return;
        }
        drawLine(x, y, x + width, y);
        drawLine(x + width, y, x + width, y + height);
        drawLine(x + width, y + height, x, y + height);
        drawLine(x, y + height, x, y);
    }

    public abstract void clearRect(int x, int y, int width, int height);

    public abstract void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight);

    public abstract void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight);

    public abstract void drawOval(int x, int y, int width, int height);

    public abstract void fillOval(int x, int y, int width, int height);

    public abstract void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle);

    public abstract void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle);

    public abstract void drawPolyline(int[] xPoints, int[] yPoints, int pointCount);

    public abstract void drawPolygon(int[] xPoints, int[] yPoints, int pointCount);

    public void drawPolygon(Polygon polygon) {
        drawPolygon(polygon.xpoints, polygon.ypoints, polygon.npoints);
    }

    public abstract void fillPolygon(int[] xPoints, int[] yPoints, int pointCount);

    public void fillPolygon(Polygon polygon) {
        fillPolygon(polygon.xpoints, polygon.ypoints, polygon.npoints);
    }

    public abstract void drawString(String text, int x, int y);

    public abstract void drawString(AttributedCharacterIterator iterator, int x, int y);

    public void drawChars(char[] data, int offset, int length, int x, int y) {
        drawString(new String(data, offset, length), x, y);
    }

    public void drawBytes(byte[] data, int offset, int length, int x, int y) {
        if (data == null) {
            throw new NullPointerException("data");
        }
        if (offset < 0 || length < 0 || offset > data.length - length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        char[] characters = new char[length];
        for (int index = 0; index < length; index++) {
            characters[index] = (char) (data[offset + index] & 255);
        }
        drawString(new String(characters), x, y);
    }

    public boolean drawImage(Image image, int x, int y, ImageObserver observer) {
        if (image == null) {
            return true;
        }
        int width = image.getWidth(observer);
        int height = image.getHeight(observer);
        return width < 0 || height < 0 ? false : drawImageRegion(image, x, y, x + width, y + height, 0, 0, width, height, null, observer);
    }

    public boolean drawImage(Image image, int x, int y, int width, int height, ImageObserver observer) {
        if (image == null) {
            return true;
        }
        int imageWidth = image.getWidth(observer);
        int imageHeight = image.getHeight(observer);
        return imageWidth < 0 || imageHeight < 0 ? false : drawImageRegion(image, x, y, x + width, y + height, 0, 0, imageWidth, imageHeight, null, observer);
    }

    public boolean drawImage(Image image, int x, int y, Color background, ImageObserver observer) {
        if (image == null) {
            return true;
        }
        int width = image.getWidth(observer);
        int height = image.getHeight(observer);
        return width < 0 || height < 0 ? false : drawImageRegion(image, x, y, x + width, y + height, 0, 0, width, height, background, observer);
    }

    public boolean drawImage(Image image, int x, int y, int width, int height, Color background, ImageObserver observer) {
        if (image == null) {
            return true;
        }
        int imageWidth = image.getWidth(observer);
        int imageHeight = image.getHeight(observer);
        return imageWidth < 0 || imageHeight < 0 ? false : drawImageRegion(image, x, y, x + width, y + height, 0, 0, imageWidth, imageHeight, background, observer);
    }

    public boolean drawImage(Image image, int destinationX1, int destinationY1, int destinationX2, int destinationY2, int sourceX1, int sourceY1, int sourceX2, int sourceY2, ImageObserver observer) {
        return image == null || drawImageRegion(image, destinationX1, destinationY1, destinationX2, destinationY2, sourceX1, sourceY1, sourceX2, sourceY2, null, observer);
    }

    public boolean drawImage(Image image, int destinationX1, int destinationY1, int destinationX2, int destinationY2, int sourceX1, int sourceY1, int sourceX2, int sourceY2, Color background, ImageObserver observer) {
        return image == null || drawImageRegion(image, destinationX1, destinationY1, destinationX2, destinationY2, sourceX1, sourceY1, sourceX2, sourceY2, background, observer);
    }

    public abstract void dispose();

    public void finalize() {
        dispose();
    }

    public Rectangle getClipRect() {
        return getClipBounds();
    }

    public boolean hitClip(int x, int y, int width, int height) {
        Rectangle clipBounds = getClipBounds();
        return clipBounds == null || clipBounds.intersects(x, y, width, height);
    }

    public Rectangle getClipBounds(Rectangle target) {
        Rectangle bounds = getClipBounds();
        if (bounds == null) {
            return null;
        }
        Rectangle result = target == null ? new Rectangle() : target;
        result.setBounds(bounds);
        return result;
    }

    @Override
    public String toString() {
        return getClass().getName() + "[font=" + getFont() + ",color=" + getColor() + "]";
    }

    protected abstract boolean drawImageRegion(Image image, int destinationX1, int destinationY1, int destinationX2, int destinationY2, int sourceX1, int sourceY1, int sourceX2, int sourceY2, Color background, ImageObserver observer);
}
