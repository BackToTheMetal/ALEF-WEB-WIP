package org.webawt.internal.graphics;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.Image;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.RadialGradientPaint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.awt.image.renderable.RenderContext;
import java.text.AttributedCharacterIterator;
import java.util.Map;
import org.webawt.internal.peer.CanvasPeer;

public final class CanvasGraphics2D extends Graphics2D {
    private final CanvasPeer peer;
    private AffineTransform transform;
    private Color background = new Color(0, 0, 0, 0);
    private Color color = Color.BLACK;
    private Composite composite = AlphaComposite.SrcOver;
    private Font font = new Font(Font.DIALOG, Font.PLAIN, 12);
    private Paint paint = Color.BLACK;
    private RenderingHints renderingHints = new RenderingHints(null);
    private Shape deviceClip;
    private Stroke stroke = new BasicStroke();
    private boolean disposed;

    public CanvasGraphics2D(CanvasPeer peer) {
        this.peer = peer;
        transform = new AffineTransform();
    }

    private CanvasGraphics2D(CanvasGraphics2D source) {
        peer = source.peer;
        transform = new AffineTransform(source.transform);
        background = source.background;
        color = source.color;
        composite = source.composite;
        font = source.font;
        paint = source.paint;
        renderingHints = (RenderingHints) source.renderingHints.clone();
        deviceClip = source.deviceClip == null ? null : new Area(source.deviceClip);
        stroke = source.stroke;
    }

    @Override
    public Graphics create() {
        ensureUsable();
        return new CanvasGraphics2D(this);
    }

    @Override
    public void draw(Shape shape) {
        if (stroke instanceof java.awt.BasicStroke && ((java.awt.BasicStroke) stroke).getLineWidth() == 0) {
            fill(org.webawt.internal.graphics.PreparedShape.hairline(shape, transform, (java.awt.BasicStroke) stroke));
            return;
        }
        ensureUsable();
        if (shape == null) {
            throw new NullPointerException("shape");
        }
        if (!(stroke instanceof BasicStroke) || !(paint instanceof Color) || boundsComposite()) {
            fill(stroke.createStrokedShape(shape));
            return;
        }
        if (!prepareOperation(false)) {
            return;
        }
        try {
            if (((BasicStroke) stroke).getLineWidth() == 0) {
                // Keep the existing Canvas hairline in surface coordinates.
                emitPath(shape);
            } else {
                peer.transform(transform.getScaleX(), transform.getShearY(), transform.getShearX(), transform.getScaleY(), transform.getTranslateX(), transform.getTranslateY());
                emitPath(shape, null);
            }
            peer.stroke();
        } finally {
            peer.restore();
        }
    }

    @Override
    public void fill(Shape shape) {
        ensureUsable();
        if (shape == null) {
            throw new NullPointerException("shape");
        }
        if (!(paint instanceof Color) && ((AlphaComposite) composite).getRule() != AlphaComposite.CLEAR) {
            fillPaint(shape);
            return;
        }
        if (!prepareOperation(true)) {
            return;
        }
        try {
            if (boundsComposite()) {
                clipOperation(shape);
            }
            PathIterator iterator = emitPath(shape);
            peer.fill(iterator.getWindingRule() == PathIterator.WIND_EVEN_ODD);
        } finally {
            peer.restore();
        }
    }

    private void fillPaint(Shape shape) {
        if (((AlphaComposite) composite).getRule() == AlphaComposite.DST) {
            return;
        }
        Rectangle bounds = transform.createTransformedShape(shape).getBounds();
        if (deviceClip != null) {
            bounds = bounds.intersection(deviceClip.getBounds());
        }
        int[] size = peer.getSurfaceSize();
        if (size != null) {
            bounds = bounds.intersection(new Rectangle(0, 0, size[0], size[1]));
        }
        if (bounds.isEmpty()) {
            return;
        }
        PaintContext context = paint.createContext(ColorModel.getRGBdefault(), bounds, shape.getBounds2D(), new AffineTransform(transform), renderingHints);
        try {
            peer.save();
            try {
                applyComposite();
                applyClip();
                clipOperation(shape);
                ColorModel model = context.getColorModel();
                // Bound temporary storage and each composite operation to one tile.
                for (int y = bounds.y; y < bounds.y + bounds.height; y += 64) {
                    for (int x = bounds.x; x < bounds.x + bounds.width; x += 64) {
                        int width = Math.min(64, bounds.x + bounds.width - x);
                        int height = Math.min(64, bounds.y + bounds.height - y);
                        Raster raster = context.getRaster(x, y, width, height);
                        int[] pixels = new int[width * height];
                        for (int row = 0; row < height; row++) {
                            for (int column = 0; column < width; column++) {
                                pixels[row * width + column] = model.getRGB(raster.getDataElements(raster.getMinX() + column, raster.getMinY() + row, null));
                            }
                        }
                        peer.save();
                        try {
                            PathIterator tile = emitPath(new Rectangle(x, y, width, height), null);
                            peer.clip(tile.getWindingRule() == PathIterator.WIND_EVEN_ODD);
                            peer.drawImage(pixels, width, height, 0, 0, width, height, x, y, x + width, y + height);
                        } finally {
                            peer.restore();
                        }
                    }
                }
            } finally {
                peer.restore();
            }
        } finally {
            context.dispose();
        }
    }

    private void clipOperation(Shape shape) {
        PathIterator iterator = emitPath(shape);
        peer.clip(iterator.getWindingRule() == PathIterator.WIND_EVEN_ODD);
    }

    private boolean boundsComposite() {
        int rule = ((AlphaComposite) composite).getRule();
        return rule == AlphaComposite.SRC || rule == AlphaComposite.SRC_IN || rule == AlphaComposite.SRC_OUT || rule == AlphaComposite.DST_IN || rule == AlphaComposite.DST_ATOP;
    }

    @Override
    public boolean hit(Rectangle rectangle, Shape shape, boolean onStroke) {
        Shape tested = onStroke ? stroke.createStrokedShape(shape) : shape;
        return transform.createTransformedShape(tested).intersects(rectangle);
    }

    @Override
    public void setComposite(Composite composite) {
        if (composite == null) {
            throw new IllegalArgumentException("composite must not be null");
        }
        if (!(composite instanceof AlphaComposite)) {
            throw new UnsupportedOperationException("Only AlphaComposite is supported");
        }
        this.composite = composite;
    }

    @Override
    public void setPaint(Paint paint) {
        if (paint != null) {
            this.paint = paint;
            if (paint instanceof Color) {
                color = (Color) paint;
            }
        }
    }

    @Override
    public void setStroke(Stroke stroke) {
        if (stroke == null) {
            throw new IllegalArgumentException("stroke must not be null");
        }
        this.stroke = stroke;
    }

    @Override
    public void setRenderingHint(RenderingHints.Key key, Object value) {
        renderingHints.put(key, value);
    }

    @Override
    public Object getRenderingHint(RenderingHints.Key key) {
        return renderingHints.get(key);
    }

    @Override
    public void setRenderingHints(Map<?, ?> hints) {
        RenderingHints replacement = new RenderingHints(null);
        replacement.putAll(hints);
        renderingHints = replacement;
    }

    @Override
    public void addRenderingHints(Map<?, ?> hints) {
        renderingHints.putAll(hints);
    }

    @Override
    public RenderingHints getRenderingHints() {
        return (RenderingHints) renderingHints.clone();
    }

    @Override
    public void translate(int x, int y) {
        translate((double) x, y);
    }

    @Override
    public void translate(double x, double y) {
        transform.translate(x, y);
    }

    @Override
    public void rotate(double angle) {
        transform.rotate(angle);
    }

    @Override
    public void rotate(double angle, double x, double y) {
        transform.rotate(angle, x, y);
    }

    @Override
    public void scale(double x, double y) {
        transform.scale(x, y);
    }

    @Override
    public void shear(double x, double y) {
        transform.shear(x, y);
    }

    @Override
    public void transform(AffineTransform transform) {
        this.transform.concatenate(transform);
    }

    @Override
    public void setTransform(AffineTransform transform) {
        this.transform = new AffineTransform(transform);
    }

    @Override
    public AffineTransform getTransform() {
        return new AffineTransform(transform);
    }

    @Override
    public Paint getPaint() {
        return paint;
    }

    @Override
    public Composite getComposite() {
        return composite;
    }

    @Override
    public void setBackground(Color background) {
        if (background == null) {
            throw new NullPointerException("background");
        }
        this.background = background;
    }

    @Override
    public Color getBackground() {
        return background;
    }

    @Override
    public Stroke getStroke() {
        return stroke;
    }

    @Override
    public void clip(Shape shape) {
        if (shape == null) {
            return;
        }
        Shape transformed = transform.createTransformedShape(shape);
        if (deviceClip == null) {
            deviceClip = new Area(transformed);
        } else {
            Area area = new Area(deviceClip);
            area.intersect(new Area(transformed));
            deviceClip = area;
        }
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public void setColor(Color color) {
        if (color != null) {
            this.color = color;
            paint = color;
        }
    }

    @Override
    public void setPaintMode() {
        composite = AlphaComposite.SrcOver;
    }

    @Override
    public void setXORMode(Color color) {
        throw new UnsupportedOperationException("XOR paint mode has no reliable Canvas 2D equivalent");
    }

    @Override
    public Font getFont() {
        return font;
    }

    @Override
    public void setFont(Font font) {
        if (font != null) {
            this.font = font;
        }
    }

    @Override
    public FontMetrics getFontMetrics(Font font) {
        Font measuredFont = font == null ? this.font : font;
        FontRenderContext context = getFontRenderContext();
        return new FontMetrics(measuredFont) {
            @Override
            public FontRenderContext getFontRenderContext() {
                return context;
            }
        };
    }

    @Override
    public FontRenderContext getFontRenderContext() {
        Object antialiasing = renderingHints.get(RenderingHints.KEY_TEXT_ANTIALIASING);
        Object fractional = renderingHints.get(RenderingHints.KEY_FRACTIONALMETRICS);
        AffineTransform linear = new AffineTransform(transform.getScaleX(), transform.getShearY(), transform.getShearX(), transform.getScaleY(), 0, 0);
        return new FontRenderContext(linear, antialiasing == null ? RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT : antialiasing, fractional == null ? RenderingHints.VALUE_FRACTIONALMETRICS_OFF : fractional);
    }

    @Override
    public Rectangle getClipBounds() {
        Shape clip = getClip();
        return clip == null ? null : clip.getBounds();
    }

    @Override
    public void clipRect(int x, int y, int width, int height) {
        clip(new Rectangle(x, y, width, height));
    }

    @Override
    public void setClip(int x, int y, int width, int height) {
        setClip(new Rectangle(x, y, width, height));
    }

    @Override
    public Shape getClip() {
        if (deviceClip == null) {
            return null;
        }
        try {
            return transform.createInverse().createTransformedShape(deviceClip);
        } catch (NoninvertibleTransformException exception) {
            return null;
        }
    }

    @Override
    public void setClip(Shape clip) {
        deviceClip = clip == null ? null : new Area(transform.createTransformedShape(clip));
    }

    @Override
    public void copyArea(int x, int y, int width, int height, int deltaX, int deltaY) {
        ensureUsable();
        int[] bounds = CopyAreaBounds.map(transform, x, y, width, height, deltaX, deltaY);
        if (bounds[2] <= 0 || bounds[3] <= 0 || !prepareOperation(true, false)) {
            return;
        }
        try {
            if (((AlphaComposite) composite).getRule() == AlphaComposite.CLEAR) {
                Rectangle source = new Rectangle(bounds[0], bounds[1], bounds[2], bounds[3]);
                int[] size = peer.getSurfaceSize();
                if (size != null) {
                    source = source.intersection(new Rectangle(0, 0, size[0], size[1]));
                }
                if (!source.isEmpty()) {
                    source.translate(bounds[4], bounds[5]);
                    peer.setFillStyle("#000000");
                    PathIterator cleared = emitPath(source, null);
                    peer.fill(cleared.getWindingRule() == PathIterator.WIND_EVEN_ODD);
                }
            } else {
                peer.copyArea(bounds[0], bounds[1], bounds[2], bounds[3], bounds[4], bounds[5]);
            }
        } finally {
            peer.restore();
        }
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2) {
        draw(new Line2D.Double(x1, y1, x2, y2));
    }

    @Override
    public void drawRect(int x, int y, int width, int height) {
        if (width >= 0 && height >= 0) {
            draw(new Rectangle(x, y, width, height));
        }
    }

    @Override
    public void fillRect(int x, int y, int width, int height) {
        fill(new Rectangle(x, y, width, height));
    }

    @Override
    public void clearRect(int x, int y, int width, int height) {
        ensureUsable();
        if (width <= 0 || height <= 0) {
            return;
        }
        peer.save();
        try {
            applyClip();
            Shape cleared = transform.createTransformedShape(new Rectangle(x, y, width, height));
            PathIterator iterator = emitPath(cleared, null);
            peer.clip(iterator.getWindingRule() == PathIterator.WIND_EVEN_ODD);
            Rectangle2D bounds = cleared.getBounds2D();
            peer.clearRect(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
            peer.setGlobalAlpha(1.0);
            peer.setCompositeOperation("source-over");
            peer.setFillStyle(toCssColor(background));
            peer.fillRect(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
        } finally {
            peer.restore();
        }
    }

    @Override
    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        draw(new RoundRectangle2D.Double(x, y, width, height, arcWidth, arcHeight));
    }

    @Override
    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        fill(new RoundRectangle2D.Double(x, y, width, height, arcWidth, arcHeight));
    }

    @Override
    public void drawOval(int x, int y, int width, int height) {
        draw(new Ellipse2D.Double(x, y, width, height));
    }

    @Override
    public void fillOval(int x, int y, int width, int height) {
        fill(new Ellipse2D.Double(x, y, width, height));
    }

    @Override
    public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        draw(new Arc2D.Double(x, y, width, height, startAngle, arcAngle, Arc2D.OPEN));
    }

    @Override
    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        fill(new Arc2D.Double(x, y, width, height, startAngle, arcAngle, Arc2D.PIE));
    }

    @Override
    public void drawPolyline(int[] xPoints, int[] yPoints, int pointCount) {
        if (xPoints == null || yPoints == null) {
            throw new NullPointerException("points");
        }
        if (pointCount > xPoints.length || pointCount > yPoints.length) {
            throw new ArrayIndexOutOfBoundsException("pointCount");
        }
        if (pointCount <= 0) {
            return;
        }
        Path2D path = new Path2D.Double();
        path.moveTo(xPoints[0], yPoints[0]);
        for (int index = 1; index < pointCount; index++) {
            path.lineTo(xPoints[index], yPoints[index]);
        }
        draw(path);
    }

    @Override
    public void drawPolygon(int[] xPoints, int[] yPoints, int pointCount) {
        draw(new Polygon(xPoints, yPoints, pointCount));
    }

    @Override
    public void fillPolygon(int[] xPoints, int[] yPoints, int pointCount) {
        fill(new Polygon(xPoints, yPoints, pointCount));
    }

    @Override
    public void drawString(String text, int x, int y) {
        drawString(text, (float) x, y);
    }

    @Override
    public void drawString(String text, float x, float y) {
        if (text == null) {
            throw new NullPointerException("text");
        }
        ensureUsable();
        if (text.isEmpty() || font.getSize2D() <= 0) {
            return;
        }
        if (boundsComposite()) {
            throw new UnsupportedOperationException("Canvas text does not support SRC, SRC_IN, SRC_OUT, DST_IN or DST_ATOP composites");
        }
        if (!prepareOperation(true)) {
            return;
        }
        try {
            AffineTransform placement = new AffineTransform(transform);
            placement.translate(x, y);
            placement.concatenate(font.getTransform());
            peer.transform(placement.getScaleX(), placement.getShearY(), placement.getShearX(), placement.getScaleY(), placement.getTranslateX(), placement.getTranslateY());
            peer.fillText(text, 0, 0);
        } finally {
            peer.restore();
        }
    }

    @Override
    public void drawString(AttributedCharacterIterator iterator, int x, int y) {
        drawString(iterator, (float) x, y);
    }

    @Override
    public void drawString(AttributedCharacterIterator iterator, float x, float y) {
        if (iterator == null) {
            throw new NullPointerException("iterator");
        }
        if (iterator.getBeginIndex() == iterator.getEndIndex()) {
            return;
        }
        new java.awt.font.TextLayout(iterator, getFontRenderContext()).draw(this, x, y);
    }

    @Override
    public void drawGlyphVector(GlyphVector glyphVector, float x, float y) {
        if (glyphVector == null) {
            throw new NullPointerException("glyphVector");
        }
        if (!(glyphVector instanceof org.webawt.internal.peer.GlyphTextSource)) {
            fill(glyphVector.getOutline(x, y));
            return;
        }
        org.webawt.internal.peer.GlyphTextSource source = (org.webawt.internal.peer.GlyphTextSource) glyphVector;
        for (int index = 0; index < glyphVector.getNumGlyphs(); index++) {
            Graphics2D child = (Graphics2D) create();
            try {
                Point2D position = glyphVector.getGlyphPosition(index);
                child.translate(x + position.getX(), y + position.getY());
                AffineTransform placement = glyphVector.getGlyphTransform(index);
                if (placement != null) {
                    child.transform(placement);
                }
                child.setFont(glyphVector.getFont());
                child.drawString(source.getGlyphText(index), 0, 0);
            } finally {
                child.dispose();
            }
        }
    }

    @Override
    public void dispose() {
        disposed = true;
    }

    @Override
    protected boolean drawImageRegion(Image image, int destinationX1, int destinationY1, int destinationX2, int destinationY2, int sourceX1, int sourceY1, int sourceX2, int sourceY2, Color background, ImageObserver observer) {
        ImagePixels source = readPixels(image, observer);
        if (source == null) {
            return false;
        }
        if (background != null) {
            source = source.withBackground(background);
        }
        ensureUsable();
        if (sourceX1 == sourceX2 || sourceY1 == sourceY2 || destinationX1 == destinationX2 || destinationY1 == destinationY2 || source.width <= 0 || source.height <= 0) {
            return true;
        }
        double[] horizontal = imageInterval(source.width, sourceX1, sourceX2, destinationX1, destinationX2);
        double[] vertical = imageInterval(source.height, sourceY1, sourceY2, destinationY1, destinationY2);
        if (horizontal == null || vertical == null || !prepareOperation(true, false)) {
            return true;
        }
        try {
            Rectangle2D region = new Rectangle2D.Double(horizontal[0], vertical[0], horizontal[1] - horizontal[0], vertical[1] - vertical[0]);
            if (((AlphaComposite) composite).getRule() == AlphaComposite.CLEAR) {
                peer.setFillStyle("#000000");
                PathIterator cleared = emitPath(region);
                peer.fill(cleared.getWindingRule() == PathIterator.WIND_EVEN_ODD);
            } else {
                if (boundsComposite()) {
                    clipOperation(region);
                }
                peer.transform(transform.getScaleX(), transform.getShearY(), transform.getShearX(), transform.getScaleY(), transform.getTranslateX(), transform.getTranslateY());
                peer.drawImage(source.pixels, source.width, source.height, sourceX1, sourceY1, sourceX2, sourceY2, destinationX1, destinationY1, destinationX2, destinationY2);
            }
        } finally {
            peer.restore();
        }
        return true;
    }

    private static double[] imageInterval(int size, int source1, int source2, int destination1, int destination2) {
        double first = -((double) source1) / ((double) source2 - source1);
        double second = (size - (double) source1) / ((double) source2 - source1);
        double start = Math.max(0, Math.min(first, second));
        double end = Math.min(1, Math.max(first, second));
        if (start >= end) {
            return null;
        }
        double low = destination1 + start * ((double) destination2 - destination1);
        double high = destination1 + end * ((double) destination2 - destination1);
        return new double[] {Math.min(low, high), Math.max(low, high)};
    }

    @Override
    public boolean drawImage(Image image, AffineTransform imageTransform, ImageObserver observer) {
        if (imageTransform == null) {
            throw new NullPointerException("imageTransform");
        }
        AffineTransform previous = transform;
        transform = new AffineTransform(transform);
        transform.concatenate(imageTransform);
        try {
            return drawImage(image, 0, 0, observer);
        } finally {
            transform = previous;
        }
    }

    @Override
    public void drawImage(BufferedImage image, BufferedImageOp operation, int x, int y) {
        drawImage(operation == null ? image : operation.filter(image, null), x, y, null);
    }

    @Override
    public void drawRenderedImage(RenderedImage image, AffineTransform imageTransform) {
        if (image == null || imageTransform == null) {
            throw new NullPointerException("image and imageTransform must not be null");
        }
        AffineTransform placement = new AffineTransform(imageTransform);
        placement.translate(image.getMinX(), image.getMinY());
        drawImage(toBufferedImage(image), placement, null);
    }

    @Override
    public void drawRenderableImage(RenderableImage image, AffineTransform imageTransform) {
        if (image == null || imageTransform == null) {
            throw new NullPointerException("image and imageTransform must not be null");
        }
        AffineTransform requested = new AffineTransform(transform);
        requested.concatenate(imageTransform);
        RenderedImage rendered = image.createRendering(new RenderContext(requested, renderingHints));
        if (rendered != null) {
            AffineTransform previous = transform;
            transform = new AffineTransform();
            try {
                drawRenderedImage(rendered, new AffineTransform());
            } finally {
                transform = previous;
            }
        }
    }

    private boolean prepareOperation(boolean filling) {
        return prepareOperation(filling, true);
    }

    private boolean prepareOperation(boolean filling, boolean usesPaint) {
        if (composite instanceof AlphaComposite && ((AlphaComposite) composite).getRule() == AlphaComposite.DST) {
            return false;
        }
        peer.save();
        try {
            applyComposite();
            if (!filling) {
                applyStroke();
            }
            peer.setFont(toCssFont(font));
            if (usesPaint) {
                applyPaint(filling);
            }
            applyClip();
            return true;
        } catch (RuntimeException | Error failure) {
            peer.restore();
            throw failure;
        }
    }

    private void applyClip() {
        if (deviceClip != null) {
            PathIterator iterator = emitPath(deviceClip, null);
            peer.clip(iterator.getWindingRule() == PathIterator.WIND_EVEN_ODD);
        }
    }

    private PathIterator emitPath(Shape shape) {
        return emitPath(shape, transform);
    }

    private PathIterator emitPath(Shape shape, AffineTransform placement) {
        PathIterator iterator = shape.getPathIterator(placement);
        double[] coordinates = new double[6];
        peer.beginPath();
        while (!iterator.isDone()) {
            int type = iterator.currentSegment(coordinates);
            if (type == PathIterator.SEG_MOVETO) {
                peer.moveTo(coordinates[0], coordinates[1]);
            } else if (type == PathIterator.SEG_LINETO) {
                peer.lineTo(coordinates[0], coordinates[1]);
            } else if (type == PathIterator.SEG_QUADTO) {
                peer.quadraticCurveTo(coordinates[0], coordinates[1], coordinates[2], coordinates[3]);
            } else if (type == PathIterator.SEG_CUBICTO) {
                peer.bezierCurveTo(coordinates[0], coordinates[1], coordinates[2], coordinates[3], coordinates[4], coordinates[5]);
            } else if (type == PathIterator.SEG_CLOSE) {
                peer.closePath();
            }
            iterator.next();
        }
        return iterator;
    }

    private void applyStroke() {
        if (!(stroke instanceof BasicStroke)) {
            return;
        }
        BasicStroke basicStroke = (BasicStroke) stroke;
        peer.setLineWidth(basicStroke.getLineWidth() == 0 ? 1 : basicStroke.getLineWidth());
        peer.setLineCap(lineCap(basicStroke.getEndCap()));
        peer.setLineJoin(lineJoin(basicStroke.getLineJoin()));
        peer.setMiterLimit(basicStroke.getMiterLimit());
        float[] dash = basicStroke.getDashArray();
        peer.setLineDash(dash == null ? new double[0] : toDoubles(dash), basicStroke.getDashPhase());
    }

    private void applyComposite() {
        AlphaComposite alphaComposite = (AlphaComposite) composite;
        peer.setGlobalAlpha(alphaComposite.getRule() == AlphaComposite.CLEAR ? 1.0 : alphaComposite.getAlpha());
        peer.setCompositeOperation(compositeOperation(alphaComposite.getRule()));
    }

    private void applyPaint(boolean filling) {
        if (((AlphaComposite) composite).getRule() == AlphaComposite.CLEAR) {
            if (filling) {
                peer.setFillStyle("#000000");
            } else {
                peer.setStrokeStyle("#000000");
            }
            return;
        }
        if (paint instanceof GradientPaint) {
            GradientPaint gradient = (GradientPaint) paint;
            setLinearGradient(filling, gradient.getPoint1(), gradient.getPoint2(), new Color[] {gradient.getColor1(), gradient.getColor2()}, new float[] {0.0f, 1.0f}, null);
            return;
        }
        if (paint instanceof LinearGradientPaint) {
            LinearGradientPaint gradient = (LinearGradientPaint) paint;
            setLinearGradient(filling, gradient.getStartPoint(), gradient.getEndPoint(), gradient.getColors(), gradient.getFractions(), gradient.getTransform());
            return;
        }
        if (paint instanceof RadialGradientPaint) {
            setRadialGradient(filling, (RadialGradientPaint) paint);
            return;
        }
        if (!(paint instanceof Color)) {
            throw new UnsupportedOperationException("This Canvas operation requires a color or native gradient; arbitrary paints are supported for shapes");
        }
        String cssColor = toCssColor((Color) paint);
        if (filling) {
            peer.setFillStyle(cssColor);
        } else {
            peer.setStrokeStyle(cssColor);
        }
    }

    private void setLinearGradient(boolean filling, Point2D start, Point2D end, Color[] colors, float[] fractions, AffineTransform paintTransform) {
        double[] startPoint = transformPaintPoint(start, paintTransform);
        double[] endPoint = transformPaintPoint(end, paintTransform);
        peer.setLinearGradient(filling, startPoint[0], startPoint[1], endPoint[0], endPoint[1], toCssColors(colors), toDoubles(fractions));
    }

    private void setRadialGradient(boolean filling, RadialGradientPaint gradient) {
        AffineTransform paintTransform = gradient.getTransform();
        double[] center = transformPaintPoint(gradient.getCenterPoint(), paintTransform);
        double[] focus = transformPaintPoint(gradient.getFocusPoint(), paintTransform);
        Point2D radiusPoint = new Point2D.Double(gradient.getCenterPoint().getX() + gradient.getRadius(), gradient.getCenterPoint().getY());
        double[] edge = transformPaintPoint(radiusPoint, paintTransform);
        double radius = Point2D.distance(center[0], center[1], edge[0], edge[1]);
        peer.setRadialGradient(filling, center[0], center[1], radius, focus[0], focus[1], toCssColors(gradient.getColors()), toDoubles(gradient.getFractions()));
    }

    private double[] transformPaintPoint(Point2D point, AffineTransform paintTransform) {
        Point2D transformed = paintTransform == null ? point : paintTransform.transform(point, null);
        return transformPoint(transformed.getX(), transformed.getY());
    }

    private double[] transformPoint(double x, double y) {
        double[] coordinates = {x, y};
        transform.transform(coordinates, 0, coordinates, 0, 1);
        return coordinates;
    }

    private void ensureUsable() {
        if (disposed) {
            throw new IllegalStateException("graphics has been disposed");
        }
    }

    private static ImagePixels readPixels(Image image, ImageObserver observer) {
        if (image == null) {
            return new ImagePixels(0, 0, new int[0]);
        }
        int width = image.getWidth(observer);
        int height = image.getHeight(observer);
        if (width < 0 || height < 0) {
            return null;
        }
        if (image instanceof BufferedImage) {
            BufferedImage buffered = (BufferedImage) image;
            return new ImagePixels(width, height, buffered.getRGB(0, 0, width, height, null, 0, width));
        }
        int[] pixels = new int[width * height];
        PixelGrabber grabber = new PixelGrabber(image, 0, 0, width, height, pixels, 0, width);
        try {
            return grabber.grabPixels() ? new ImagePixels(width, height, pixels) : null;
        } catch (InterruptedException exception) {
            return null;
        }
    }

    private static BufferedImage toBufferedImage(RenderedImage image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Raster raster = image.getData();
        ColorModel model = image.getColorModel();
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                result.setRGB(x, y, model.getRGB(raster.getDataElements(image.getMinX() + x, image.getMinY() + y, null)));
            }
        }
        return result;
    }

    private static int blendBackground(int source, int background) {
        double sourceAlpha = (source >>> 24) / 255.0;
        double backgroundAlpha = (background >>> 24) / 255.0;
        double outputAlpha = sourceAlpha + backgroundAlpha * (1.0 - sourceAlpha);
        if (outputAlpha == 0.0) {
            return 0;
        }
        int red = blendBackgroundChannel(source, background, 16, sourceAlpha, backgroundAlpha, outputAlpha);
        int green = blendBackgroundChannel(source, background, 8, sourceAlpha, backgroundAlpha, outputAlpha);
        int blue = blendBackgroundChannel(source, background, 0, sourceAlpha, backgroundAlpha, outputAlpha);
        return byteValue(outputAlpha) << 24 | red << 16 | green << 8 | blue;
    }

    private static int blendBackgroundChannel(int source, int background, int shift, double sourceAlpha, double backgroundAlpha, double outputAlpha) {
        double sourceValue = (source >>> shift & 0xff) / 255.0;
        double backgroundValue = (background >>> shift & 0xff) / 255.0;
        return byteValue((sourceValue * sourceAlpha + backgroundValue * backgroundAlpha * (1.0 - sourceAlpha)) / outputAlpha);
    }

    private static int byteValue(double value) {
        return Math.max(0, Math.min(255, (int) Math.round(value * 255.0)));
    }

    private static String toCssColor(Color color) {
        return "rgba(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + "," + color.getAlpha() / 255.0 + ")";
    }

    private static String toCssFont(Font font) {
        return org.webawt.internal.peer.FontCss.font(font.getFamily(), font.isBold(), font.isItalic(), font.getSize2D());
    }

    private static String lineCap(int cap) {
        if (cap == BasicStroke.CAP_ROUND) {
            return "round";
        }
        return cap == BasicStroke.CAP_SQUARE ? "square" : "butt";
    }

    private static String lineJoin(int join) {
        if (join == BasicStroke.JOIN_ROUND) {
            return "round";
        }
        return join == BasicStroke.JOIN_BEVEL ? "bevel" : "miter";
    }

    private static String compositeOperation(int rule) {
        switch (rule) {
            case AlphaComposite.CLEAR:
                return "destination-out";
            case AlphaComposite.SRC:
                return "copy";
            case AlphaComposite.DST:
                return "source-over";
            case AlphaComposite.SRC_IN:
                return "source-in";
            case AlphaComposite.DST_IN:
                return "destination-in";
            case AlphaComposite.SRC_OUT:
                return "source-out";
            case AlphaComposite.DST_OUT:
                return "destination-out";
            case AlphaComposite.DST_OVER:
                return "destination-over";
            case AlphaComposite.SRC_ATOP:
                return "source-atop";
            case AlphaComposite.DST_ATOP:
                return "destination-atop";
            case AlphaComposite.XOR:
                return "xor";
            default:
                return "source-over";
        }
    }

    private static String[] toCssColors(Color[] colors) {
        String[] cssColors = new String[colors.length];
        for (int index = 0; index < colors.length; index++) {
            cssColors[index] = toCssColor(colors[index]);
        }
        return cssColors;
    }

    private static double[] toDoubles(float[] values) {
        double[] doubles = new double[values.length];
        for (int index = 0; index < values.length; index++) {
            doubles[index] = values[index];
        }
        return doubles;
    }

    private static final class ImagePixels {
        private final int height;
        private final int[] pixels;
        private final int width;

        private ImagePixels(int width, int height, int[] pixels) {
            this.width = width;
            this.height = height;
            this.pixels = pixels;
        }

        private ImagePixels withBackground(Color background) {
            int[] composed = new int[pixels.length];
            for (int index = 0; index < pixels.length; index++) {
                composed[index] = blendBackground(pixels[index], background.getRGB());
            }
            return new ImagePixels(width, height, composed);
        }
    }
}
