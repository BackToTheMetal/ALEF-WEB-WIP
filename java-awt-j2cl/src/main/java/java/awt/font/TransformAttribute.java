package java.awt.font;

import java.awt.geom.AffineTransform;
import java.io.Serializable;

public final class TransformAttribute implements Serializable {
    public static final TransformAttribute IDENTITY = new TransformAttribute(new AffineTransform());
    private static final long serialVersionUID = 3356247357827709530L;
    private final AffineTransform transform;

    public TransformAttribute(AffineTransform transform) {
        this.transform = transform == null || transform.isIdentity() ? null : new AffineTransform(transform);
    }

    public AffineTransform getTransform() {
        return transform == null ? new AffineTransform() : new AffineTransform(transform);
    }

    public boolean isIdentity() {
        return transform == null;
    }

    @Override
    public int hashCode() {
        return transform == null ? 0 : transform.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof TransformAttribute)) {
            return false;
        }
        TransformAttribute attribute = (TransformAttribute) object;
        return transform == null ? attribute.transform == null : transform.equals(attribute.transform);
    }
}
