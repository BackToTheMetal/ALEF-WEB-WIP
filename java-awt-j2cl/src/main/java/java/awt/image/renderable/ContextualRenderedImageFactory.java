package java.awt.image.renderable;

import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;

public interface ContextualRenderedImageFactory extends RenderedImageFactory {
    RenderContext mapRenderContext(int sourceIndex, RenderContext renderContext, ParameterBlock parameterBlock, RenderableImage image);

    RenderedImage create(RenderContext renderContext, ParameterBlock parameterBlock);

    Rectangle2D getBounds2D(ParameterBlock parameterBlock);

    Object getProperty(ParameterBlock parameterBlock, String name);

    String[] getPropertyNames();

    boolean isDynamic();
}
