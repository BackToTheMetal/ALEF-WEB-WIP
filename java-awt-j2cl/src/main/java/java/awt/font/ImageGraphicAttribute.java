package java.awt.font;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public final class ImageGraphicAttribute extends GraphicAttribute {
    private final Image image;
    private final float originX;
    private final float originY;
    private final int imageWidth;
    private final int imageHeight;

    public ImageGraphicAttribute(Image image, int alignment) {
        this(image, alignment, 0.0f, 0.0f);
    }

    public ImageGraphicAttribute(Image image, int alignment, float originX, float originY) {
        super(alignment);
        if (image == null) {
            throw new NullPointerException("image");
        }
        this.image = image;
        this.originX = originX;
        this.originY = originY;
        imageWidth = image.getWidth(null);
        imageHeight = image.getHeight(null);
    }

    @Override
    public float getAscent() {
        return Math.max(0.0f, originY);
    }

    @Override
    public float getDescent() {
        return Math.max(0.0f, imageHeight - originY);
    }

    @Override
    public float getAdvance() {
        return Math.max(0.0f, imageWidth - originX);
    }

    @Override
    public Rectangle2D getBounds() {
        return new Rectangle2D.Float(-originX, -originY, imageWidth, imageHeight);
    }

    @Override
    public void draw(Graphics2D graphics, float x, float y) {
        graphics.drawImage(image, AffineTransform.getTranslateInstance(x - originX, y - originY), null);
    }

    @Override
    public int hashCode() {
        return image.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof ImageGraphicAttribute && equals((ImageGraphicAttribute) object);
    }

    public boolean equals(ImageGraphicAttribute attribute) {
        return attribute != null && getAlignment() == attribute.getAlignment() && originX == attribute.originX && originY == attribute.originY && image.equals(attribute.image);
    }
}
