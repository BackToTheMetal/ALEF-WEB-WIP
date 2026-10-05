package java.awt;

import java.awt.geom.AffineTransform;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;

public abstract class Graphics2D extends Graphics {
    protected Graphics2D() {
    }

    public abstract void draw(Shape shape);

    @Override
    public abstract void drawString(String text, int x, int y);

    public abstract void drawString(String text, float x, float y);

    @Override
    public abstract void drawString(AttributedCharacterIterator iterator, int x, int y);

    public abstract void drawString(AttributedCharacterIterator iterator, float x, float y);

    public abstract void drawGlyphVector(GlyphVector glyphVector, float x, float y);

    public abstract void fill(Shape shape);

    public abstract boolean drawImage(Image image, AffineTransform transform, ImageObserver observer);

    public abstract void drawImage(BufferedImage image, BufferedImageOp operation, int x, int y);

    public abstract void drawRenderedImage(RenderedImage image, AffineTransform transform);

    public abstract void drawRenderableImage(RenderableImage image, AffineTransform transform);

    public abstract boolean hit(Rectangle rectangle, Shape shape, boolean onStroke);

    public abstract void setComposite(Composite composite);

    public abstract void setPaint(Paint paint);

    public abstract void setStroke(Stroke stroke);

    public abstract void setRenderingHint(RenderingHints.Key key, Object value);

    public abstract Object getRenderingHint(RenderingHints.Key key);

    public abstract void setRenderingHints(Map<?, ?> hints);

    public abstract void addRenderingHints(Map<?, ?> hints);

    public abstract RenderingHints getRenderingHints();

    public GraphicsConfiguration getDeviceConfiguration() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
    }

    @Override
    public abstract void translate(int x, int y);

    public abstract void translate(double x, double y);

    public abstract void rotate(double angle);

    public abstract void rotate(double angle, double x, double y);

    public abstract void scale(double x, double y);

    public abstract void shear(double x, double y);

    public abstract void transform(AffineTransform transform);

    public abstract void setTransform(AffineTransform transform);

    public abstract AffineTransform getTransform();

    public abstract Paint getPaint();

    public abstract Composite getComposite();

    public abstract void setBackground(Color color);

    public abstract Color getBackground();

    public abstract Stroke getStroke();

    public abstract void clip(Shape shape);

    public abstract FontRenderContext getFontRenderContext();

}
