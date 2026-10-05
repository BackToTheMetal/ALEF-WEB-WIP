package java.awt.geom;

import java.awt.Shape;
import java.io.Serializable;

public final class GeneralPath extends Path2D.Float implements Serializable {
    private static final long serialVersionUID = -8327096662768731142L;

    public GeneralPath() {
        super();
    }

    public GeneralPath(int rule) {
        super(rule);
    }

    public GeneralPath(int rule, int initialCapacity) {
        super(rule, initialCapacity);
    }

    public GeneralPath(Shape shape) {
        super(shape);
    }
}
