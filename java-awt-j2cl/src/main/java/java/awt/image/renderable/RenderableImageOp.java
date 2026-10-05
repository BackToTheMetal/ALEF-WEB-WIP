package java.awt.image.renderable;

import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.util.Vector;

public class RenderableImageOp implements RenderableImage {
    private final ContextualRenderedImageFactory factory;
    private Rectangle2D bounds;
    private ParameterBlock parameterBlock;

    public RenderableImageOp(ContextualRenderedImageFactory factory, ParameterBlock parameterBlock) {
        this.factory = factory;
        this.parameterBlock = (ParameterBlock) parameterBlock.clone();
    }

    @Override
    public Vector<RenderableImage> getSources() {
        if (parameterBlock.getNumSources() == 0) {
            return null;
        }
        Vector<RenderableImage> result = new Vector<RenderableImage>();
        for (int index = 0; index < parameterBlock.getNumSources(); index++) {
            Object source = parameterBlock.getSource(index);
            if (!(source instanceof RenderableImage)) {
                break;
            }
            result.addElement((RenderableImage) source);
        }
        return result;
    }

    @Override
    public Object getProperty(String name) {
        return factory.getProperty(parameterBlock, name);
    }

    @Override
    public String[] getPropertyNames() {
        return factory.getPropertyNames();
    }

    @Override
    public boolean isDynamic() {
        return factory.isDynamic();
    }

    @Override
    public float getWidth() {
        return (float) getBounds().getWidth();
    }

    @Override
    public float getHeight() {
        return (float) getBounds().getHeight();
    }

    @Override
    public float getMinX() {
        return (float) getBounds().getMinX();
    }

    @Override
    public float getMinY() {
        return (float) getBounds().getMinY();
    }

    public ParameterBlock setParameterBlock(ParameterBlock parameterBlock) {
        ParameterBlock previous = this.parameterBlock;
        this.parameterBlock = (ParameterBlock) parameterBlock.clone();
        bounds = null;
        return previous;
    }

    public ParameterBlock getParameterBlock() {
        return parameterBlock;
    }

    @Override
    public RenderedImage createScaledRendering(int width, int height, RenderingHints hints) {
        double scaleX = width / (double) getWidth();
        double scaleY = height / (double) getHeight();
        if (Math.abs(scaleX / scaleY - 1.0) < 0.01) {
            scaleX = scaleY;
        }
        return createRendering(new RenderContext(AffineTransform.getScaleInstance(scaleX, scaleY), hints));
    }

    @Override
    public RenderedImage createDefaultRendering() {
        return createRendering(new RenderContext(new AffineTransform()));
    }

    @Override
    public RenderedImage createRendering(RenderContext renderContext) {
        ParameterBlock renderedParameters = (ParameterBlock) parameterBlock.clone();
        Vector<RenderableImage> sources = getSources();
        if (sources != null) {
            Vector<Object> renderedSources = new Vector<Object>();
            for (int index = 0; index < sources.size(); index++) {
                RenderContext sourceContext = factory.mapRenderContext(index, renderContext, parameterBlock, this);
                RenderedImage renderedSource = sources.elementAt(index).createRendering(sourceContext);
                if (renderedSource == null) {
                    return null;
                }
                renderedSources.addElement(renderedSource);
            }
            if (!renderedSources.isEmpty()) {
                renderedParameters.setSources(renderedSources);
            }
        }
        return factory.create(renderContext, renderedParameters);
    }

    private Rectangle2D getBounds() {
        if (bounds == null) {
            bounds = factory.getBounds2D(parameterBlock);
        }
        return bounds;
    }
}
