package java.awt;

import java.awt.image.ColorModel;

public interface Composite {
    CompositeContext createContext(ColorModel sourceColorModel, ColorModel destinationColorModel, RenderingHints hints);
}
