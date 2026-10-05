package java.awt.font;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator;
import java.util.Map;

public final class TextLayout implements Cloneable {
    public static class CaretPolicy {
        public TextHitInfo getStrongCaret(TextHitInfo hit1, TextHitInfo hit2, TextLayout layout) {
            if (hit1 == null || hit2 == null || layout == null) {
                throw new NullPointerException("hits and layout must not be null");
            }
            byte firstLevel = layout.levelForHit(hit1);
            byte secondLevel = layout.levelForHit(hit2);
            if (firstLevel != secondLevel) {
                return firstLevel < secondLevel ? hit1 : hit2;
            }
            return hit1.isLeadingEdge() ? hit1 : hit2;
        }
    }

    public static final CaretPolicy DEFAULT_CARET_POLICY = new CaretPolicy();
    private final float advance;
    private final byte baseline;
    private final float[] baselineOffsets;
    private final Font font;
    private final FontRenderContext context;
    private final boolean leftToRight;
    private final float[] positions;
    private final String text;
    private final GlyphVector vector;
    private final TextRunLayout runs;

    public TextLayout(String string, Font font, FontRenderContext context) {
        this(plainRuns(string, font), requireContext(context));
    }

    public TextLayout(String string, Map<? extends AttributedCharacterIterator.Attribute, ?> attributes, FontRenderContext context) {
        this(new TextRunLayout(new MapIterator(string, attributes)), requireContext(context));
    }

    public TextLayout(AttributedCharacterIterator iterator, FontRenderContext context) {
        this(new TextRunLayout(iterator), requireContext(context));
    }

    TextLayout(TextRunLayout runs, FontRenderContext context) {
        if (runs.text.isEmpty()) {
            throw new IllegalArgumentException("string must not be empty");
        }
        this.runs = runs;
        text = runs.text;
        font = runs.firstFont();
        this.context = context;
        leftToRight = !runs.rightToLeft();
        vector = font.layoutGlyphVector(context, text.toCharArray(), 0, text.length(), leftToRight ? Font.LAYOUT_LEFT_TO_RIGHT : Font.LAYOUT_RIGHT_TO_LEFT);
        positions = runs.positions(context);
        advance = positions[positions.length - 1];
        LineMetrics metrics = font.getLineMetrics(text, context);
        baseline = (byte) metrics.getBaselineIndex();
        baselineOffsets = metrics.getBaselineOffsets();
    }

    private static TextRunLayout plainRuns(String text, Font font) {
        if (text == null || font == null) {
            throw new IllegalArgumentException("string and font must not be null");
        }
        return new TextRunLayout(text, font);
    }

    public TextLayout getJustifiedLayout(float justificationWidth) {
        if (justificationWidth < 0.0f) {
            throw new IllegalArgumentException("justificationWidth must be non-negative");
        }
        if (advance == 0.0f || Float.compare(advance, justificationWidth) == 0) {
            return this;
        }
        return new TextLayout(runs.scaled(justificationWidth / advance), context);
    }

    protected Object clone() { return new TextLayout(runs, context); }

    public byte getBaseline() {
        return baseline;
    }

    public float[] getBaselineOffsets() {
        float[] result = new float[baselineOffsets.length];
        System.arraycopy(baselineOffsets, 0, result, 0, result.length);
        return result;
    }

    public float getAdvance() {
        return advance;
    }

    public float getVisibleAdvance() {
        int visibleLength = text.length();
        while (visibleLength > 0 && Character.isWhitespace(text.charAt(visibleLength - 1))) {
            visibleLength--;
        }
        return positions[visibleLength];
    }

    public float getAscent() {
        return runs.metric(context, 0);
    }

    public float getDescent() {
        return runs.metric(context, 1);
    }

    public float getLeading() {
        return runs.metric(context, 2);
    }

    public Rectangle2D getBounds() {
        return vector.getVisualBounds();
    }

    public Rectangle getPixelBounds(FontRenderContext renderContext, float x, float y) {
        return vector.getPixelBounds(renderContext == null ? context : renderContext, x, y);
    }

    public boolean isLeftToRight() {
        return leftToRight;
    }

    public boolean isVertical() {
        return false;
    }

    public int getCharacterCount() {
        return text.length();
    }

    public float[] getCaretInfo(TextHitInfo hit, Rectangle2D bounds) {
        checkHit(hit);
        if (bounds == null) {
            throw new IllegalArgumentException("bounds must not be null");
        }
        float x = caretX(hit.getInsertionIndex());
        return new float[] {x, 0.0f, x, -getAscent(), x, getDescent()};
    }

    public float[] getCaretInfo(TextHitInfo hit) {
        return getCaretInfo(hit, naturalBounds());
    }

    public TextHitInfo getNextRightHit(TextHitInfo hit) {
        checkHit(hit);
        return getNextRightHit(hit.getInsertionIndex());
    }

    public TextHitInfo getNextRightHit(int caret, CaretPolicy policy) {
        if (policy == null) {
            throw new NullPointerException("policy");
        }
        return getNextRightHit(caret);
    }

    public TextHitInfo getNextRightHit(int caret) {
        checkCaret(caret);
        int next = leftToRight ? caret + 1 : caret - 1;
        while (next >= 0 && next <= text.length() && !caretBoundary(next)) {
            next += leftToRight ? 1 : -1;
        }
        return next < 0 || next > text.length() ? null : TextHitInfo.afterOffset(next);
    }

    public TextHitInfo getNextLeftHit(TextHitInfo hit) {
        checkHit(hit);
        return getNextLeftHit(hit.getInsertionIndex());
    }

    public TextHitInfo getNextLeftHit(int caret, CaretPolicy policy) {
        if (policy == null) {
            throw new NullPointerException("policy");
        }
        return getNextLeftHit(caret);
    }

    public TextHitInfo getNextLeftHit(int caret) {
        checkCaret(caret);
        int next = leftToRight ? caret - 1 : caret + 1;
        while (next >= 0 && next <= text.length() && !caretBoundary(next)) {
            next += leftToRight ? -1 : 1;
        }
        return next < 0 || next > text.length() ? null : TextHitInfo.afterOffset(next);
    }

    public TextHitInfo getVisualOtherHit(TextHitInfo hit) {
        checkHit(hit);
        return hit.getOtherHit();
    }

    public Shape getCaretShape(TextHitInfo hit, Rectangle2D bounds) {
        checkHit(hit);
        if (bounds == null) {
            throw new IllegalArgumentException("bounds must not be null");
        }
        float x = caretX(hit.getInsertionIndex());
        return new Line2D.Float(x, (float) bounds.getMinY(), x, (float) bounds.getMaxY());
    }

    public Shape getCaretShape(TextHitInfo hit) {
        return getCaretShape(hit, naturalBounds());
    }

    public byte getCharacterLevel(int index) {
        if (index < -1 || index > text.length()) {
            throw new IllegalArgumentException("Invalid character index: " + index);
        }
        int adjusted = index == text.length() ? index - 1 : Math.max(0, index);
        return adjusted < text.length() && isRtlCharacter(text.charAt(adjusted)) ? (byte) 1 : (byte) 0;
    }

    public Shape[] getCaretShapes(int offset, Rectangle2D bounds, CaretPolicy policy) {
        checkCaret(offset);
        if (bounds == null || policy == null) {
            throw new IllegalArgumentException("bounds and policy must not be null");
        }
        return new Shape[] {getCaretShape(TextHitInfo.afterOffset(offset), bounds), null};
    }

    public Shape[] getCaretShapes(int offset, Rectangle2D bounds) {
        return getCaretShapes(offset, bounds, DEFAULT_CARET_POLICY);
    }

    public Shape[] getCaretShapes(int offset) {
        return getCaretShapes(offset, naturalBounds(), DEFAULT_CARET_POLICY);
    }

    public int[] getLogicalRangesForVisualSelection(TextHitInfo firstEndpoint, TextHitInfo secondEndpoint) {
        checkHit(firstEndpoint);
        checkHit(secondEndpoint);
        int first = firstEndpoint.getInsertionIndex();
        int second = secondEndpoint.getInsertionIndex();
        return new int[] {Math.min(first, second), Math.max(first, second)};
    }

    public Shape getVisualHighlightShape(TextHitInfo firstEndpoint, TextHitInfo secondEndpoint, Rectangle2D bounds) {
        int[] ranges = getLogicalRangesForVisualSelection(firstEndpoint, secondEndpoint);
        return getLogicalHighlightShape(ranges[0], ranges[1], bounds);
    }

    public Shape getVisualHighlightShape(TextHitInfo firstEndpoint, TextHitInfo secondEndpoint) {
        return getVisualHighlightShape(firstEndpoint, secondEndpoint, naturalBounds());
    }

    public Shape getLogicalHighlightShape(int firstEndpoint, int secondEndpoint, Rectangle2D bounds) {
        checkCaret(firstEndpoint);
        checkCaret(secondEndpoint);
        if (bounds == null) {
            throw new IllegalArgumentException("bounds must not be null");
        }
        float first = caretX(firstEndpoint);
        float second = caretX(secondEndpoint);
        return new Rectangle2D.Float(Math.min(first, second), (float) bounds.getMinY(), Math.abs(first - second), (float) bounds.getHeight());
    }

    public Shape getLogicalHighlightShape(int firstEndpoint, int secondEndpoint) {
        return getLogicalHighlightShape(firstEndpoint, secondEndpoint, naturalBounds());
    }

    public Shape getBlackBoxBounds(int firstEndpoint, int secondEndpoint) {
        checkCaret(firstEndpoint);
        checkCaret(secondEndpoint);
        int start = Math.min(firstEndpoint, secondEndpoint);
        int limit = Math.max(firstEndpoint, secondEndpoint);
        if (start == limit) {
            return new Rectangle2D.Float();
        }
        float first = caretX(start);
        float second = caretX(limit);
        return new Rectangle2D.Float(Math.min(first, second), -getAscent(), Math.abs(first - second), getAscent() + getDescent());
    }

    public TextHitInfo hitTestChar(float x, float y, Rectangle2D bounds) {
        if (bounds == null) {
            throw new IllegalArgumentException("bounds must not be null");
        }
        int closest = 0;
        float distance = Math.abs(x - caretX(0));
        for (int index = 1; index <= text.length(); index++) {
            if (!caretBoundary(index)) {
                continue;
            }
            float candidate = Math.abs(x - caretX(index));
            if (candidate < distance) {
                closest = index;
                distance = candidate;
            }
        }
        return TextHitInfo.afterOffset(closest);
    }

    public TextHitInfo hitTestChar(float x, float y) {
        return hitTestChar(x, y, naturalBounds());
    }

    public boolean equals(TextLayout layout) {
        return layout != null && text.equals(layout.text) && runs.sameRuns(layout.runs) && context.equals(layout.context) && leftToRight == layout.leftToRight;
    }

    @Override
    public String toString() {
        return getClass().getName() + "[text=" + text + ", advance=" + advance + ", leftToRight=" + leftToRight + "]";
    }

    public void draw(Graphics2D graphics, float x, float y) {
        if (graphics == null) {
            throw new IllegalArgumentException("graphics must not be null");
        }
        runs.draw(graphics, x, y, context);
    }

    public Shape getOutline(AffineTransform transform) {
        Shape outline = vector.getOutline();
        return transform == null ? outline : transform.createTransformedShape(outline);
    }

    public LayoutPath getLayoutPath() {
        return null;
    }

    public void hitToPoint(TextHitInfo hit, Point2D point) {
        checkHit(hit);
        if (point == null) {
            throw new IllegalArgumentException("point must not be null");
        }
        point.setLocation(caretX(hit.getInsertionIndex()), 0.0);
    }

    private boolean caretBoundary(int index) {
        if (index == 0 || index == text.length()) {
            return true;
        }
        return !(Character.isHighSurrogate(text.charAt(index - 1)) && Character.isLowSurrogate(text.charAt(index))) && !TextMeasurer.isCombining(text.codePointAt(index));
    }

    private float caretX(int insertionIndex) {
        return leftToRight ? positions[insertionIndex] : advance - positions[insertionIndex];
    }

    private Rectangle2D naturalBounds() {
        return new Rectangle2D.Float(0.0f, -getAscent(), advance, getAscent() + getDescent() + getLeading());
    }

    private void checkHit(TextHitInfo hit) {
        if (hit == null) {
            throw new IllegalArgumentException("hit must not be null");
        }
        checkCaret(hit.getInsertionIndex());
    }

    private void checkCaret(int caret) {
        if (runs.mixedDirections()) {
            throw new UnsupportedOperationException("Mixed-direction caret/highlight geometry requires a bidi layout engine");
        }
        if (caret < 0 || caret > text.length()) {
            throw new IllegalArgumentException("Invalid caret: " + caret);
        }
    }

    private byte levelForHit(TextHitInfo hit) {
        int index = Math.max(0, Math.min(text.length() - 1, hit.getCharIndex()));
        return text.isEmpty() ? 0 : getCharacterLevel(index);
    }

    private static FontRenderContext requireContext(FontRenderContext context) {
        if (context == null) {
            throw new IllegalArgumentException("context must not be null");
        }
        return context;
    }

    private static final class MapIterator implements AttributedCharacterIterator {
        private final String value;
        private final Map<Attribute, Object> attributes;
        private int index;
        MapIterator(String value, Map<? extends Attribute, ?> attributes) {
            if (value == null || attributes == null) {
                throw new IllegalArgumentException("text and attributes must not be null");
            }
            this.value = value;
            this.attributes = new java.util.HashMap<>(attributes);
        }
        public char first() { return setIndex(0); }
        public char last() { return setIndex(Math.max(0, value.length() - 1)); }
        public char current() { return index == value.length() ? DONE : value.charAt(index); }
        public char next() { return setIndex(Math.min(value.length(), index + 1)); }
        public char previous() { return index == 0 ? DONE : setIndex(index - 1); }
        public char setIndex(int position) {
            if (position < 0 || position > value.length()) { throw new IllegalArgumentException("position"); }
            index = position;
            return current();
        }
        public int getBeginIndex() { return 0; }
        public int getEndIndex() { return value.length(); }
        public int getIndex() { return index; }
        public int getRunStart() { return 0; }
        public int getRunStart(Attribute attribute) { return 0; }
        public int getRunStart(java.util.Set<? extends Attribute> keys) { return 0; }
        public int getRunLimit() { return value.length(); }
        public int getRunLimit(Attribute attribute) { return value.length(); }
        public int getRunLimit(java.util.Set<? extends Attribute> keys) { return value.length(); }
        public Map<Attribute, Object> getAttributes() { return attributes; }
        public Object getAttribute(Attribute attribute) { return attributes.get(attribute); }
        public java.util.Set<Attribute> getAllAttributeKeys() { return attributes.keySet(); }
        public Object clone() { MapIterator copy = new MapIterator(value, attributes); copy.index = index; return copy; }
    }

    private static boolean isRightToLeft(String text) {
        for (int index = 0; index < text.length(); index++) {
            char character = text.charAt(index);
            if (isRtlCharacter(character)) {
                return true;
            }
            if (Character.isLetter(character)) {
                return false;
            }
        }
        return false;
    }

    private static boolean isRtlCharacter(char character) {
        return character >= 0x0590 && character <= 0x08ff;
    }
}
