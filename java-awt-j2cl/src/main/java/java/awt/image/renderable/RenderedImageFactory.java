package java.awt.image.renderable;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;

public interface RenderedImageFactory {
    RenderedImage create(ParameterBlock parameterBlock, RenderingHints hints);
}
