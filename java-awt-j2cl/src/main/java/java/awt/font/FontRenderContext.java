package java.awt.font;

import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;

public class FontRenderContext {
    private final AffineTransform transform;
    private final Object antiAliasingHint;
    private final Object fractionalMetricsHint;

    protected FontRenderContext() {
        transform = new AffineTransform();
        antiAliasingHint = RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT;
        fractionalMetricsHint = RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT;
    }

    public FontRenderContext(AffineTransform transform, boolean antiAliased, boolean useFractionalMetrics) {
        this.transform = transform == null ? new AffineTransform() : new AffineTransform(transform);
        antiAliasingHint = antiAliased ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF;
        fractionalMetricsHint = useFractionalMetrics ? RenderingHints.VALUE_FRACTIONALMETRICS_ON : RenderingHints.VALUE_FRACTIONALMETRICS_OFF;
    }

    public FontRenderContext(AffineTransform transform, Object antiAliasingHint, Object fractionalMetricsHint) {
        if (!RenderingHints.KEY_TEXT_ANTIALIASING.isCompatibleValue(antiAliasingHint)) {
            throw new IllegalArgumentException("Invalid text antialiasing hint");
        }
        if (!RenderingHints.KEY_FRACTIONALMETRICS.isCompatibleValue(fractionalMetricsHint)) {
            throw new IllegalArgumentException("Invalid fractional metrics hint");
        }
        this.transform = transform == null ? new AffineTransform() : new AffineTransform(transform);
        this.antiAliasingHint = antiAliasingHint;
        this.fractionalMetricsHint = fractionalMetricsHint;
    }

    public boolean isTransformed() {
        return !transform.isIdentity();
    }

    public int getTransformType() {
        return transform.getType();
    }

    public AffineTransform getTransform() {
        return new AffineTransform(transform);
    }

    public boolean isAntiAliased() {
        return antiAliasingHint != RenderingHints.VALUE_TEXT_ANTIALIAS_OFF && antiAliasingHint != RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT;
    }

    public boolean usesFractionalMetrics() {
        return fractionalMetricsHint == RenderingHints.VALUE_FRACTIONALMETRICS_ON;
    }

    public Object getAntiAliasingHint() {
        return antiAliasingHint;
    }

    public Object getFractionalMetricsHint() {
        return fractionalMetricsHint;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof FontRenderContext && equals((FontRenderContext) object);
    }

    public boolean equals(FontRenderContext context) {
        return context != null && transform.equals(context.transform) && antiAliasingHint.equals(context.antiAliasingHint) && fractionalMetricsHint.equals(context.fractionalMetricsHint);
    }

    @Override
    public int hashCode() {
        int result = transform.hashCode();
        result = 31 * result + antiAliasingHint.hashCode();
        return 31 * result + fractionalMetricsHint.hashCode();
    }
}
