package java.awt.image.renderable;

import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

public class RenderContext implements Cloneable {
    private Shape areaOfInterest;
    private RenderingHints hints;
    private AffineTransform transform;

    public RenderContext(AffineTransform transform, Shape areaOfInterest, RenderingHints hints) {
        this.transform = new AffineTransform(transform);
        this.areaOfInterest = areaOfInterest;
        this.hints = hints;
    }

    public RenderContext(AffineTransform transform) {
        this(transform, null, null);
    }

    public RenderContext(AffineTransform transform, RenderingHints hints) {
        this(transform, null, hints);
    }

    public RenderContext(AffineTransform transform, Shape areaOfInterest) {
        this(transform, areaOfInterest, null);
    }

    public RenderingHints getRenderingHints() {
        return hints;
    }

    public void setRenderingHints(RenderingHints hints) {
        this.hints = hints;
    }

    public void setTransform(AffineTransform transform) {
        this.transform = new AffineTransform(transform);
    }

    public void preConcatenateTransform(AffineTransform transform) {
        preConcetenateTransform(transform);
    }

    @Deprecated
    public void preConcetenateTransform(AffineTransform transform) {
        this.transform.preConcatenate(transform);
    }

    public void concatenateTransform(AffineTransform transform) {
        concetenateTransform(transform);
    }

    @Deprecated
    public void concetenateTransform(AffineTransform transform) {
        this.transform.concatenate(transform);
    }

    public AffineTransform getTransform() {
        return new AffineTransform(transform);
    }

    public void setAreaOfInterest(Shape areaOfInterest) {
        this.areaOfInterest = areaOfInterest;
    }

    public Shape getAreaOfInterest() {
        return areaOfInterest;
    }

    public Object clone() {
        return new RenderContext(transform, areaOfInterest, hints);
    }
}
