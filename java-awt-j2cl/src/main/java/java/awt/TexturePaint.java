package java.awt;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;

public class TexturePaint implements Paint {
    private final BufferedImage image;
    private final double scaleX;
    private final double scaleY;
    private final double translateX;
    private final double translateY;

    public TexturePaint(BufferedImage image, Rectangle2D anchor) {
        this.image = image;
        translateX = anchor.getX();
        translateY = anchor.getY();
        scaleX = anchor.getWidth() / image.getWidth();
        scaleY = anchor.getHeight() / image.getHeight();
    }

    public BufferedImage getImage() {
        return image;
    }

    public Rectangle2D getAnchorRect() {
        return new Rectangle2D.Double(translateX, translateY, scaleX * image.getWidth(), scaleY * image.getHeight());
    }

    @Override
    public PaintContext createContext(ColorModel colorModel, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform transform, RenderingHints hints) {
        AffineTransform textureToDevice = transform == null ? new AffineTransform() : new AffineTransform(transform);
        textureToDevice.translate(translateX, translateY);
        textureToDevice.scale(scaleX, scaleY);
        return new TextureContext(image, invert(textureToDevice));
    }

    @Override
    public int getTransparency() {
        return image.getColorModel().getTransparency();
    }

    private static AffineTransform invert(AffineTransform transform) {
        try {
            return transform.createInverse();
        } catch (NoninvertibleTransformException exception) {
            return new AffineTransform();
        }
    }

    private static final class TextureContext implements PaintContext {
        private final BufferedImage image;
        private final AffineTransform deviceToTexture;

        private TextureContext(BufferedImage image, AffineTransform deviceToTexture) {
            this.image = image;
            this.deviceToTexture = deviceToTexture;
        }

        @Override
        public void dispose() {
        }

        @Override
        public ColorModel getColorModel() {
            return ColorModel.getRGBdefault();
        }

        @Override
        public Raster getRaster(int x, int y, int width, int height) {
            BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Point2D point = new Point2D.Double();
            for (int row = 0; row < height; row++) {
                for (int column = 0; column < width; column++) {
                    deviceToTexture.transform(new Point2D.Double(x + column, y + row), point);
                    int textureX = modulo((int) Math.floor(point.getX() - 0.5), image.getWidth());
                    int textureY = modulo((int) Math.floor(point.getY() - 0.5), image.getHeight());
                    output.setRGB(column, row, image.getRGB(textureX, textureY));
                }
            }
            return output.getRaster();
        }

        private static int modulo(int value, int modulus) {
            int result = value % modulus;
            return result < 0 ? result + modulus : result;
        }
    }
}
