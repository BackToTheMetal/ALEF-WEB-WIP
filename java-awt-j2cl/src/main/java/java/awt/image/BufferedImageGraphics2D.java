package java.awt.image;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
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
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.renderable.RenderableImage;
import java.awt.image.renderable.RenderContext;
import java.text.AttributedCharacterIterator;
import java.util.Map;
import org.webawt.internal.graphics.CopyAreaBounds;
import org.webawt.internal.graphics.PreparedShape;
import org.webawt.internal.peer.FontCss;
import org.webawt.internal.peer.FontRasterizer;
import org.webawt.internal.peer.WebAwtRuntime;

final class BufferedImageGraphics2D extends Graphics2D {
    private static final Stroke DEFAULT_STROKE = new BasicStroke();
    private final BufferedImage image;
    private AffineTransform transform;
    private Shape deviceClip;
    private PreparedShape preparedClip;
    private Color color;
    private Paint paint;
    private Composite composite;
    private Font font;
    private Stroke stroke;
    private Color background;
    private Color xorColor;
    private RenderingHints hints;

    BufferedImageGraphics2D(BufferedImage image) {
        this.image = image;
        transform = new AffineTransform();
        color = Color.white;
        paint = color;
        composite = AlphaComposite.SrcOver;
        font = new Font(Font.DIALOG, Font.PLAIN, 12);
        stroke = DEFAULT_STROKE;
        background = Color.black;
        hints = new RenderingHints((Map<RenderingHints.Key, ?>) null);
    }

    private BufferedImageGraphics2D(BufferedImageGraphics2D source) {
        image = source.image;
        transform = new AffineTransform(source.transform);
        deviceClip = source.deviceClip == null ? null : new Area(source.deviceClip);
        color = source.color;
        paint = source.paint;
        composite = source.composite;
        font = source.font;
        stroke = source.stroke;
        background = source.background;
        xorColor = source.xorColor;
        hints = new RenderingHints((Map<RenderingHints.Key, ?>) null);
        hints.putAll(source.hints);
    }

    @Override
    public Graphics create() {
        return new BufferedImageGraphics2D(this);
    }

    @Override
    public void translate(int x, int y) {
        transform.translate(x, y);
    }

    @Override
    public void translate(double x, double y) {
        transform.translate(x, y);
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
        xorColor = null;
    }

    @Override
    public void setXORMode(Color color) {
        if (color == null) {
            throw new IllegalArgumentException("XOR color cannot be null");
        }
        xorColor = color;
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
        Object antialiasing = hints.get(RenderingHints.KEY_TEXT_ANTIALIASING);
        Object fractional = hints.get(RenderingHints.KEY_FRACTIONALMETRICS);
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
        deviceClip = clip == null ? null : transform.createTransformedShape(clip);
        preparedClip = null;
    }

    @Override
    public void copyArea(int x, int y, int width, int height, int deltaX, int deltaY) {
        int[] bounds = CopyAreaBounds.map(transform, x, y, width, height, deltaX, deltaY);
        int sourceX = Math.max(0, bounds[0]);
        int sourceY = Math.max(0, bounds[1]);
        width = (int) Math.min(image.getWidth(), (long) bounds[0] + bounds[2]) - sourceX;
        height = (int) Math.min(image.getHeight(), (long) bounds[1] + bounds[3]) - sourceY;
        if (width <= 0 || height <= 0) {
            return;
        }
        int[] pixels = new int[width * height];
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                int pixelX = sourceX + column;
                int pixelY = sourceY + row;
                pixels[row * width + column] = image.getRGB(pixelX, pixelY);
            }
        }
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                long targetX = (long) sourceX + bounds[4] + column;
                long targetY = (long) sourceY + bounds[5] + row;
                if (targetX >= 0 && targetY >= 0 && targetX < image.getWidth() && targetY < image.getHeight() && insideClip((int) targetX, (int) targetY)) {
                    compositePixel((int) targetX, (int) targetY, pixels[row * width + column]);
                }
            }
        }
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2) {
        if (!usesPixelLines()) {
            draw(new Line2D.Double(x1, y1, x2, y2));
            return;
        }
        Point2D first = transform.transform(new Point2D.Double(x1, y1), null);
        Point2D second = transform.transform(new Point2D.Double(x2, y2), null);
        rasterLine(rounded(first.getX()), rounded(first.getY()), rounded(second.getX()), rounded(second.getY()));
    }

    private boolean usesPixelLines() {
        return DEFAULT_STROKE.equals(stroke) && (transform.getType() & ~AffineTransform.TYPE_TRANSLATION) == 0;
    }

    @Override
    public void drawRect(int x, int y, int width, int height) {
        if (usesPixelLines()) {
            super.drawRect(x, y, width, height);
        } else if (width >= 0 && height >= 0) {
            draw(new Rectangle(x, y, width, height));
        }
    }

    @Override
    public void fillRect(int x, int y, int width, int height) {
        fill(new Rectangle2D.Double(x, y, width, height));
    }

    @Override
    public void clearRect(int x, int y, int width, int height) {
        Paint savedPaint = paint;
        Composite savedComposite = composite;
        Color savedXor = xorColor;
        paint = background;
        composite = AlphaComposite.Src;
        xorColor = null;
        fillRect(x, y, width, height);
        paint = savedPaint;
        composite = savedComposite;
        xorColor = savedXor;
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
        checkPoints(xPoints, yPoints, pointCount);
        if (!usesPixelLines() && pointCount > 0) {
            Path2D path = new Path2D.Double();
            path.moveTo(xPoints[0], yPoints[0]);
            for (int index = 1; index < pointCount; index++) {
                path.lineTo(xPoints[index], yPoints[index]);
            }
            draw(path);
            return;
        }
        for (int index = 1; index < pointCount; index++) {
            drawLine(xPoints[index - 1], yPoints[index - 1], xPoints[index], yPoints[index]);
        }
    }

    @Override
    public void drawPolygon(int[] xPoints, int[] yPoints, int pointCount) {
        checkPoints(xPoints, yPoints, pointCount);
        if (!usesPixelLines()) {
            draw(new Polygon(xPoints, yPoints, pointCount));
            return;
        }
        drawPolyline(xPoints, yPoints, pointCount);
        if (pointCount > 1) {
            drawLine(xPoints[pointCount - 1], yPoints[pointCount - 1], xPoints[0], yPoints[0]);
        }
    }

    @Override
    public void fillPolygon(int[] xPoints, int[] yPoints, int pointCount) {
        checkPoints(xPoints, yPoints, pointCount);
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
        if (text.isEmpty() || font.getSize2D() <= 0 || ((AlphaComposite) composite).getRule() == AlphaComposite.DST) {
            return;
        }
        AffineTransform placement = new AffineTransform(transform);
        placement.translate(x, y);
        placement.concatenate(font.getTransform());
        double[] matrix = new double[6];
        placement.getMatrix(matrix);
        for (double value : matrix) {
            if (!Double.isFinite(value)) {
                return;
            }
        }
        if (placement.getDeterminant() == 0) {
            return;
        }
        FontRasterizer rasterizer = WebAwtRuntime.getFontRasterizer();
        String css = FontCss.font(font.getFamily(), font.isBold(), font.isItalic(), font.getSize2D());
        double[] ink = rasterizer.bounds(css, text);
        if (ink[2] <= 0 || ink[3] <= 0) {
            return;
        }
        Rectangle2D bounds = placement.createTransformedShape(new Rectangle2D.Double(ink[0], ink[1], ink[2], ink[3])).getBounds2D();
        int left = Math.max(0, (int) Math.floor(bounds.getMinX() - 2));
        int top = Math.max(0, (int) Math.floor(bounds.getMinY() - 2));
        int right = Math.min(image.getWidth(), (int) Math.ceil(bounds.getMaxX() + 2));
        int bottom = Math.min(image.getHeight(), (int) Math.ceil(bounds.getMaxY() + 2));
        if (deviceClip != null) {
            Rectangle clip = deviceClip.getBounds();
            left = Math.max(left, clip.x);
            top = Math.max(top, clip.y);
            right = Math.min(right, clip.x + clip.width);
            bottom = Math.min(bottom, clip.y + clip.height);
        }
        if (left >= right || top >= bottom) {
            return;
        }
        Shape userInk = font.getTransform().createTransformedShape(new Rectangle2D.Double(ink[0], ink[1], ink[2], ink[3]));
        userInk = AffineTransform.getTranslateInstance(x, y).createTransformedShape(userInk);
        PaintContext context = paint.createContext(image.getColorModel(), new Rectangle(left, top, right - left, bottom - top), userInk.getBounds2D(), transform, hints);
        try {
            for (int tileY = top; tileY < bottom; tileY += 128) {
                for (int tileX = left; tileX < right; tileX += 128) {
                    int width = Math.min(128, right - tileX);
                    int height = Math.min(128, bottom - tileY);
                    int[] coverage = rasterizer.rasterize(css, text, matrix, tileX, tileY, width, height);
                    Raster source = context.getRaster(tileX, tileY, width, height);
                    ColorModel model = context.getColorModel();
                    for (int row = 0; row < height; row++) {
                        for (int column = 0; column < width; column++) {
                            int alpha = coverage[row * width + column];
                            int pixelX = tileX + column;
                            int pixelY = tileY + row;
                            if (alpha > 0 && insideClip(pixelX, pixelY)) {
                                int value = model.getRGB(source.getDataElements(source.getMinX() + column, source.getMinY() + row, null));
                                int previous = image.getRGB(pixelX, pixelY);
                                compositePixel(pixelX, pixelY, value);
                                if (alpha < 255) {
                                    image.setRGB(pixelX, pixelY, coveredPixel(image.getRGB(pixelX, pixelY), previous, alpha));
                                }
                            }
                        }
                    }
                }
            }
        } finally {
            context.dispose();
        }
    }

    private static int coveredPixel(int result, int previous, int coverage) {
        double amount = coverage / 255.0;
        double resultAlpha = (result >>> 24) * amount;
        double previousAlpha = (previous >>> 24) * (1 - amount);
        double alpha = resultAlpha + previousAlpha;
        if (alpha == 0) {
            return 0;
        }
        int value = (int) Math.round(alpha) << 24;
        for (int shift = 0; shift <= 16; shift += 8) {
            value |= (int) Math.round(((result >>> shift & 255) * resultAlpha + (previous >>> shift & 255) * previousAlpha) / alpha) << shift;
        }
        return value;
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
        // This graphics object retains no external resources.
    }

    @Override
    protected boolean drawImageRegion(Image source, int destinationX1, int destinationY1, int destinationX2, int destinationY2, int sourceX1, int sourceY1, int sourceX2, int sourceY2, Color background, ImageObserver observer) {
        ImagePixels sourcePixels = readPixels(source, observer);
        if (sourcePixels == null) {
            return false;
        }
        if (destinationX1 == destinationX2 || destinationY1 == destinationY2 || sourceX1 == sourceX2 || sourceY1 == sourceY2) {
            return true;
        }
        AffineTransform inverse;
        try {
            inverse = transform.createInverse();
        } catch (NoninvertibleTransformException exception) {
            return true;
        }
        Rectangle2D destination = new Rectangle2D.Double(Math.min(destinationX1, destinationX2), Math.min(destinationY1, destinationY2), Math.abs(destinationX2 - destinationX1), Math.abs(destinationY2 - destinationY1));
        Rectangle2D bounds = transform.createTransformedShape(destination).getBounds2D();
        int minimumX = Math.max(0, (int) Math.floor(bounds.getMinX()));
        int minimumY = Math.max(0, (int) Math.floor(bounds.getMinY()));
        int maximumX = Math.min(image.getWidth(), (int) Math.ceil(bounds.getMaxX()));
        int maximumY = Math.min(image.getHeight(), (int) Math.ceil(bounds.getMaxY()));
        for (int y = minimumY; y < maximumY; y++) {
            for (int x = minimumX; x < maximumX; x++) {
                Point2D point = inverse.transform(new Point2D.Double(x + 0.5, y + 0.5), null);
                double horizontal = (point.getX() - destinationX1) / (destinationX2 - destinationX1);
                double vertical = (point.getY() - destinationY1) / (destinationY2 - destinationY1);
                if (horizontal >= 0.0 && horizontal < 1.0 && vertical >= 0.0 && vertical < 1.0 && insideClip(x, y)) {
                    drawSourcePixel(sourcePixels, sourceX1 + horizontal * (sourceX2 - sourceX1), sourceY1 + vertical * (sourceY2 - sourceY1), x, y, background);
                }
            }
        }
        return true;
    }

    @Override
    public boolean drawImage(Image source, AffineTransform imageTransform, ImageObserver observer) {
        if (imageTransform == null) {
            throw new NullPointerException("imageTransform");
        }
        AffineTransform previous = transform;
        transform = new AffineTransform(transform);
        transform.concatenate(imageTransform);
        try {
            return drawImage(source, 0, 0, observer);
        } finally {
            transform = previous;
        }
    }

    @Override
    public void drawImage(BufferedImage source, BufferedImageOp operation, int x, int y) {
        drawImage(operation == null ? source : operation.filter(source, null), x, y, null);
    }

    @Override
    public void drawRenderedImage(RenderedImage source, AffineTransform imageTransform) {
        if (source == null || imageTransform == null) {
            throw new NullPointerException("source and imageTransform must not be null");
        }
        AffineTransform placement = new AffineTransform(imageTransform);
        placement.translate(source.getMinX(), source.getMinY());
        drawImage(toBufferedImage(source), placement, null);
    }

    @Override
    public void drawRenderableImage(RenderableImage source, AffineTransform imageTransform) {
        if (source == null || imageTransform == null) {
            throw new NullPointerException("source and imageTransform must not be null");
        }
        AffineTransform requested = new AffineTransform(transform);
        requested.concatenate(imageTransform);
        RenderedImage rendered = source.createRendering(new RenderContext(requested, hints));
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

    @Override
    public void draw(Shape shape) {
        if (stroke instanceof java.awt.BasicStroke && ((java.awt.BasicStroke) stroke).getLineWidth() == 0) {
            fill(org.webawt.internal.graphics.PreparedShape.hairline(shape, transform, (java.awt.BasicStroke) stroke));
            return;
        }
        if (shape == null) {
            throw new NullPointerException("Shape cannot be null");
        }
        fill(stroke.createStrokedShape(shape));
    }

    @Override
    public void fill(Shape shape) {
        if (shape == null) {
            throw new NullPointerException("Shape cannot be null");
        }
        Shape deviceShape = transform.createTransformedShape(shape);
        PreparedShape prepared = new PreparedShape(deviceShape);
        Rectangle2D bounds = deviceShape.getBounds2D();
        int minimumX = Math.max(0, (int) Math.floor(bounds.getMinX()));
        int minimumY = Math.max(0, (int) Math.floor(bounds.getMinY()));
        int maximumX = Math.min(image.getWidth(), (int) Math.ceil(bounds.getMaxX()));
        int maximumY = Math.min(image.getHeight(), (int) Math.ceil(bounds.getMaxY()));
        int width = maximumX - minimumX;
        int height = maximumY - minimumY;
        if (width <= 0 || height <= 0) {
            return;
        }
        PaintContext context = paint.createContext(image.getColorModel(), new Rectangle(minimumX, minimumY, width, height), shape.getBounds2D(), transform, hints);
        try {
            ColorModel sourceModel = context.getColorModel();
            for (int tileY = minimumY; tileY < maximumY; tileY += 64) {
                for (int tileX = minimumX; tileX < maximumX; tileX += 64) {
                    int tileWidth = Math.min(64, maximumX - tileX);
                    int tileHeight = Math.min(64, maximumY - tileY);
                    Raster source = context.getRaster(tileX, tileY, tileWidth, tileHeight);
                    for (int row = 0; row < tileHeight; row++) {
                        for (int column = 0; column < tileWidth; column++) {
                            int pixelX = tileX + column;
                            int pixelY = tileY + row;
                            if (prepared.contains(pixelX, pixelY) && insideClip(pixelX, pixelY)) {
                                compositePixel(pixelX, pixelY, sourceModel.getRGB(source.getDataElements(source.getMinX() + column, source.getMinY() + row, null)));
                            }
                        }
                    }
                }
            }
        } finally {
            context.dispose();
        }
    }

    @Override
    public boolean hit(Rectangle rectangle, Shape shape, boolean onStroke) {
        if (rectangle == null || shape == null) {
            throw new NullPointerException("Hit arguments cannot be null");
        }
        Shape tested = onStroke ? stroke.createStrokedShape(shape) : shape;
        return transform.createTransformedShape(tested).intersects(rectangle);
    }

    @Override
    public void setComposite(Composite composite) {
        if (composite == null) {
            throw new IllegalArgumentException("Composite cannot be null");
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
            throw new IllegalArgumentException("Stroke cannot be null");
        }
        this.stroke = stroke;
    }

    @Override
    public void setRenderingHint(RenderingHints.Key key, Object value) {
        hints.put(key, value);
    }

    @Override
    public Object getRenderingHint(RenderingHints.Key key) {
        return hints.get(key);
    }

    @Override
    public void setRenderingHints(Map<?, ?> hints) {
        this.hints.clear();
        this.hints.putAll(hints);
    }

    @Override
    public void addRenderingHints(Map<?, ?> hints) {
        this.hints.putAll(hints);
    }

    @Override
    public RenderingHints getRenderingHints() {
        RenderingHints copy = new RenderingHints((Map<RenderingHints.Key, ?>) null);
        copy.putAll(hints);
        return copy;
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
        if (transform == null) {
            throw new NullPointerException("Transform cannot be null");
        }
        this.transform.concatenate(transform);
    }

    @Override
    public void setTransform(AffineTransform transform) {
        if (transform == null) {
            throw new NullPointerException("Transform cannot be null");
        }
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
    public void setBackground(Color color) {
        if (color == null) {
            throw new IllegalArgumentException("Background cannot be null");
        }
        background = color;
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
            deviceClip = transformed;
            preparedClip = null;
            return;
        }
        Area intersection = new Area(deviceClip);
        intersection.intersect(new Area(transformed));
        deviceClip = intersection;
        preparedClip = null;
    }

    private void rasterLine(int firstX, int firstY, int secondX, int secondY) {
        int deltaX = Math.abs(secondX - firstX);
        int directionX = firstX < secondX ? 1 : -1;
        int deltaY = -Math.abs(secondY - firstY);
        int directionY = firstY < secondY ? 1 : -1;
        int error = deltaX + deltaY;
        int x = firstX;
        int y = firstY;
        while (true) {
            if (insideImage(x, y) && insideClip(x, y)) {
                compositePixel(x, y, paintAt(x, y));
            }
            if (x == secondX && y == secondY) {
                return;
            }
            int doubled = error * 2;
            if (doubled >= deltaY) {
                error += deltaY;
                x += directionX;
            }
            if (doubled <= deltaX) {
                error += deltaX;
                y += directionY;
            }
        }
    }

    private int paintAt(int x, int y) {
        if (paint instanceof Color) {
            return ((Color) paint).getRGB();
        }
        PaintContext context = paint.createContext(image.getColorModel(), new Rectangle(x, y, 1, 1), new Rectangle2D.Double(x, y, 1, 1), transform, hints);
        Raster raster = context.getRaster(x, y, 1, 1);
        int rgb = context.getColorModel().getRGB(raster.getDataElements(0, 0, null));
        context.dispose();
        return rgb;
    }

    private void compositePixel(int x, int y, int source) {
        int destination = image.getRGB(x, y);
        if (xorColor != null) {
            image.setRGB(x, y, 0xff000000 | (source ^ xorColor.getRGB() ^ destination) & 0x00ffffff);
            return;
        }
        if (!(composite instanceof AlphaComposite)) {
            throw new UnsupportedOperationException("Only AlphaComposite is supported");
        }
        AlphaComposite alphaComposite = (AlphaComposite) composite;
        image.setRGB(x, y, blend(source, destination, alphaComposite.getRule(), alphaComposite.getAlpha()));
    }

    private void drawSourcePixel(ImagePixels source, double sourceX, double sourceY, int destinationX, int destinationY, Color background) {
        int x = (int) Math.floor(sourceX);
        int y = (int) Math.floor(sourceY);
        if (x < 0 || y < 0 || x >= source.width || y >= source.height) {
            return;
        }
        int pixel = source.pixels[y * source.width + x];
        compositePixel(destinationX, destinationY, background == null ? pixel : blend(pixel, background.getRGB(), AlphaComposite.SRC_OVER, 1.0f));
    }

    private static ImagePixels readPixels(Image source, ImageObserver observer) {
        if (source == null) {
            return new ImagePixels(0, 0, new int[0]);
        }
        int width = source.getWidth(observer);
        int height = source.getHeight(observer);
        if (width < 0 || height < 0) {
            return null;
        }
        if (source instanceof BufferedImage) {
            BufferedImage buffered = (BufferedImage) source;
            return new ImagePixels(width, height, buffered.getRGB(0, 0, width, height, null, 0, width));
        }
        int[] pixels = new int[width * height];
        PixelGrabber grabber = new PixelGrabber(source, 0, 0, width, height, pixels, 0, width);
        try {
            return grabber.grabPixels() ? new ImagePixels(width, height, pixels) : null;
        } catch (InterruptedException exception) {
            return null;
        }
    }

    private static BufferedImage toBufferedImage(RenderedImage source) {
        if (source instanceof BufferedImage) {
            return (BufferedImage) source;
        }
        BufferedImage result = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Raster raster = source.getData();
        ColorModel model = source.getColorModel();
        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                result.setRGB(x, y, model.getRGB(raster.getDataElements(source.getMinX() + x, source.getMinY() + y, null)));
            }
        }
        return result;
    }

    private static int blend(int source, int destination, int rule, float globalAlpha) {
        double sourceAlpha = (source >>> 24) / 255.0 * globalAlpha;
        double destinationAlpha = (destination >>> 24) / 255.0;
        double sourceFactor = sourceFactor(rule, destinationAlpha);
        double destinationFactor = destinationFactor(rule, sourceAlpha);
        double outputAlpha = sourceAlpha * sourceFactor + destinationAlpha * destinationFactor;
        if (outputAlpha <= 0.0) {
            return 0;
        }
        int red = blendChannel(source, destination, 16, sourceAlpha, destinationAlpha, sourceFactor, destinationFactor, outputAlpha);
        int green = blendChannel(source, destination, 8, sourceAlpha, destinationAlpha, sourceFactor, destinationFactor, outputAlpha);
        int blue = blendChannel(source, destination, 0, sourceAlpha, destinationAlpha, sourceFactor, destinationFactor, outputAlpha);
        return byteValue(outputAlpha) << 24 | red << 16 | green << 8 | blue;
    }

    private static int blendChannel(int source, int destination, int shift, double sourceAlpha, double destinationAlpha, double sourceFactor, double destinationFactor, double outputAlpha) {
        double sourceValue = (source >>> shift & 0xff) / 255.0;
        double destinationValue = (destination >>> shift & 0xff) / 255.0;
        double premultiplied = sourceValue * sourceAlpha * sourceFactor + destinationValue * destinationAlpha * destinationFactor;
        return byteValue(premultiplied / outputAlpha);
    }

    private static double sourceFactor(int rule, double destinationAlpha) {
        switch (rule) {
            case AlphaComposite.SRC:
            case AlphaComposite.SRC_OVER:
                return 1.0;
            case AlphaComposite.SRC_OUT:
            case AlphaComposite.XOR:
            case AlphaComposite.DST_OVER:
            case AlphaComposite.DST_ATOP:
                return 1.0 - destinationAlpha;
            case AlphaComposite.SRC_IN:
            case AlphaComposite.SRC_ATOP:
                return destinationAlpha;
            default:
                return 0.0;
        }
    }

    private static double destinationFactor(int rule, double sourceAlpha) {
        switch (rule) {
            case AlphaComposite.DST:
            case AlphaComposite.DST_OVER:
                return 1.0;
            case AlphaComposite.DST_OUT:
            case AlphaComposite.XOR:
            case AlphaComposite.SRC_OVER:
            case AlphaComposite.SRC_ATOP:
                return 1.0 - sourceAlpha;
            case AlphaComposite.DST_IN:
            case AlphaComposite.DST_ATOP:
                return sourceAlpha;
            default:
                return 0.0;
        }
    }

    private boolean insideClip(int x, int y) {
        if (deviceClip == null) { return true; }
        if (preparedClip == null) { preparedClip = new PreparedShape(deviceClip); }
        return preparedClip.contains(x, y);
    }

    private boolean insideImage(int x, int y) {
        return x >= 0 && y >= 0 && x < image.getWidth() && y < image.getHeight();
    }

    private static int byteValue(double value) {
        return Math.max(0, Math.min(255, (int) (value * 255.0 + 0.5)));
    }

    private static int rounded(double value) {
        return (int) Math.floor(value + 0.5);
    }

    private static void checkPoints(int[] xPoints, int[] yPoints, int pointCount) {
        if (xPoints == null || yPoints == null) {
            throw new NullPointerException("Point arrays cannot be null");
        }
        if (pointCount < 0 || pointCount > xPoints.length || pointCount > yPoints.length) {
            throw new IndexOutOfBoundsException("Point count exceeds the array bounds");
        }
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
    }
}
