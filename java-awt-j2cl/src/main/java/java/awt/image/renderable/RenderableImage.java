package java.awt.image.renderable;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.util.Vector;

public interface RenderableImage {
    String HINTS_OBSERVED = "HINTS_OBSERVED";

    Vector<RenderableImage> getSources();

    Object getProperty(String name);

    String[] getPropertyNames();

    boolean isDynamic();

    float getWidth();

    float getHeight();

    float getMinX();

    float getMinY();

    RenderedImage createScaledRendering(int width, int height, RenderingHints hints);

    RenderedImage createDefaultRendering();

    RenderedImage createRendering(RenderContext renderContext);
}
