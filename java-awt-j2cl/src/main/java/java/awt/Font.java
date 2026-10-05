package java.awt;

import java.awt.font.FontRenderContext;
import java.awt.font.GlyphJustificationInfo;
import java.awt.font.GlyphMetrics;
import java.awt.font.GlyphVector;
import java.awt.font.LineMetrics;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.text.AttributedCharacterIterator;
import java.text.CharacterIterator;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.webawt.internal.peer.FontCss;
import org.webawt.internal.peer.WebAwtRuntime;

public class Font implements Serializable {
    public static final String DIALOG = "Dialog";
    public static final String DIALOG_INPUT = "DialogInput";
    public static final String SANS_SERIF = "SansSerif";
    public static final String SERIF = "Serif";
    public static final String MONOSPACED = "Monospaced";
    public static final int PLAIN = 0;
    public static final int BOLD = 1;
    public static final int ITALIC = 2;
    public static final int ROMAN_BASELINE = 0;
    public static final int CENTER_BASELINE = 1;
    public static final int HANGING_BASELINE = 2;
    public static final int TRUETYPE_FONT = 0;
    public static final int TYPE1_FONT = 1;
    public static final int LAYOUT_LEFT_TO_RIGHT = 0;
    public static final int LAYOUT_RIGHT_TO_LEFT = 1;
    public static final int LAYOUT_NO_START_CONTEXT = 2;
    public static final int LAYOUT_NO_LIMIT_CONTEXT = 4;
    private static final long serialVersionUID = -4206021311591459213L;
    protected String name;
    protected int style;
    protected int size;
    protected float pointSize;
    private AffineTransform transform;
    private Map<AttributedCharacterIterator.Attribute, Object> attributes;

    public Font(String name, int style, int size) {
        this.name = name == null ? "Default" : name;
        this.style = validStyle(style);
        this.size = size;
        pointSize = size;
        transform = new AffineTransform();
        attributes = Collections.emptyMap();
    }

    public Font(Map<? extends AttributedCharacterIterator.Attribute, ?> attributes) {
        this(fontState(attributes));
    }

    protected Font(Font font) {
        if (font == null) {
            throw new NullPointerException("font");
        }
        name = font.name;
        style = font.style;
        size = font.size;
        pointSize = font.pointSize;
        transform = new AffineTransform(font.transform);
        attributes = font.attributes;
    }

    private Font(FontState state) {
        name = state.name;
        style = state.style;
        pointSize = state.size;
        size = roundedSize(state.size);
        transform = state.transform;
        attributes = state.attributes;
    }

    private Font(String name, int style, float size, AffineTransform transform, Map<AttributedCharacterIterator.Attribute, Object> attributes) {
        this.name = name;
        this.style = validStyle(style);
        pointSize = size;
        this.size = roundedSize(size);
        this.transform = transform == null ? new AffineTransform() : new AffineTransform(transform);
        this.attributes = attributes == null || attributes.isEmpty() ? Collections.emptyMap() : Collections.unmodifiableMap(new HashMap<>(attributes));
    }

    public static boolean textRequiresLayout(char[] characters, int start, int end) {
        checkRange(characters.length, start, end);
        for (int index = start; index < end; index++) {
            int character = characters[index];
            if (character >= 0x0300 && character <= 0x036f || character >= 0x0590 && character <= 0x08ff || character >= 0x0900 && character <= 0x0dff || character >= 0x1100 && character <= 0x11ff || character >= 0x200c && character <= 0x200f || character >= 0x202a && character <= 0x202e || character >= 0xd800 && character <= 0xdfff) {
                return true;
            }
        }
        return false;
    }

    public static Font getFont(Map<? extends AttributedCharacterIterator.Attribute, ?> attributes) {
        if (attributes == null) {
            throw new NullPointerException("attributes");
        }
        for (Map.Entry<? extends AttributedCharacterIterator.Attribute, ?> entry : attributes.entrySet()) {
            if (attributeName(entry.getKey()).equals("font") && entry.getValue() instanceof Font) {
                return (Font) entry.getValue();
            }
        }
        return new Font(attributes);
    }

    public AffineTransform getTransform() {
        return new AffineTransform(transform);
    }

    public String getFamily() {
        return logicalFamily(name);
    }

    public String getFamily(Locale locale) {
        if (locale == null) {
            throw new NullPointerException("locale");
        }
        return getFamily();
    }

    public String getPSName() {
        return styledName();
    }

    public String getName() {
        return name;
    }

    public String getFontName() {
        return styledName();
    }

    public String getFontName(Locale locale) {
        if (locale == null) {
            throw new NullPointerException("locale");
        }
        return getFontName();
    }

    public int getStyle() {
        return style;
    }

    public int getSize() {
        return size;
    }

    public float getSize2D() {
        return pointSize;
    }

    public boolean isPlain() {
        return style == PLAIN;
    }

    public boolean isBold() {
        return (style & BOLD) != 0;
    }

    public boolean isItalic() {
        return (style & ITALIC) != 0;
    }

    public boolean isTransformed() {
        return !transform.isIdentity();
    }

    public boolean hasLayoutAttributes() {
        return isTransformed() || !attributes.isEmpty();
    }

    public static Font getFont(String propertyName) {
        return getFont(propertyName, null);
    }

    public static Font decode(String description) {
        if (description == null || description.isEmpty()) {
            return new Font(DIALOG, PLAIN, 12);
        }
        String value = description.trim();
        if (value.isEmpty()) {
            return new Font(DIALOG, PLAIN, 12);
        }
        int parsedSize = 12;
        int parsedStyle = PLAIN;
        int lastSeparator = separatorBeforeLastToken(value);
        if (lastSeparator >= 0) {
            String sizeToken = value.substring(lastSeparator + 1);
            try {
                int candidate = Integer.parseInt(sizeToken);
                parsedSize = candidate > 0 ? candidate : 12;
                value = trimSeparators(value.substring(0, lastSeparator));
            } catch (NumberFormatException exception) {
                parsedSize = 12;
            }
        }
        int styleSeparator = separatorBeforeLastToken(value);
        String styleToken = styleSeparator >= 0 ? value.substring(styleSeparator + 1) : value;
        int candidateStyle = parseStyle(styleToken);
        if (candidateStyle >= 0) {
            parsedStyle = candidateStyle;
            value = styleSeparator >= 0 ? trimSeparators(value.substring(0, styleSeparator)) : DIALOG;
        }
        return new Font(value.isEmpty() ? DIALOG : value, parsedStyle, parsedSize);
    }

    public static Font getFont(String propertyName, Font defaultFont) {
        return defaultFont;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + style;
        result = 31 * result + Float.floatToIntBits(pointSize);
        result = 31 * result + transform.hashCode();
        return 31 * result + attributes.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Font)) {
            return false;
        }
        Font font = (Font) object;
        return style == font.style && Float.floatToIntBits(pointSize) == Float.floatToIntBits(font.pointSize) && name.equals(font.name) && transform.equals(font.transform) && attributes.equals(font.attributes);
    }

    @Override
    public String toString() {
        return getClass().getName() + "[family=" + getFamily() + ",name=" + name + ",style=" + styleName() + ",size=" + size + "]";
    }

    public int getNumGlyphs() {
        return 0x110000;
    }

    public int getMissingGlyphCode() {
        return 0;
    }

    public byte getBaselineFor(char character) {
        return ROMAN_BASELINE;
    }

    public AttributedCharacterIterator.Attribute[] getAvailableAttributes() {
        return attributes.keySet().toArray(new AttributedCharacterIterator.Attribute[attributes.size()]);
    }

    public Map<TextAttribute, ?> getAttributes() {
        Map<TextAttribute, Object> result = new HashMap<>();
        result.put(TextAttribute.FAMILY, getFamily());
        result.put(TextAttribute.SIZE, Float.valueOf(pointSize));
        result.put(TextAttribute.WEIGHT, isBold() ? TextAttribute.WEIGHT_BOLD : TextAttribute.WEIGHT_REGULAR);
        result.put(TextAttribute.POSTURE, isItalic() ? TextAttribute.POSTURE_OBLIQUE : TextAttribute.POSTURE_REGULAR);
        result.put(TextAttribute.WIDTH, TextAttribute.WIDTH_REGULAR);
        result.put(TextAttribute.SUPERSCRIPT, Integer.valueOf(0));
        result.put(TextAttribute.TRACKING, Float.valueOf(0.0f));
        if (isTransformed()) {
            result.put(TextAttribute.TRANSFORM, new java.awt.font.TransformAttribute(transform));
        }
        for (Map.Entry<AttributedCharacterIterator.Attribute, Object> entry : attributes.entrySet()) {
            if (entry.getKey() instanceof TextAttribute) {
                result.put((TextAttribute) entry.getKey(), entry.getValue());
            }
        }
        return Collections.unmodifiableMap(result);
    }

    public Font deriveFont(int style, float size) {
        return new Font(name, style, size, transform, attributes);
    }

    public Font deriveFont(int style, AffineTransform transform) {
        requireTransform(transform);
        return new Font(name, style, pointSize, transform, attributes);
    }

    public Font deriveFont(float size) {
        return new Font(name, style, size, transform, attributes);
    }

    public Font deriveFont(AffineTransform transform) {
        requireTransform(transform);
        return new Font(name, style, pointSize, transform, attributes);
    }

    public Font deriveFont(int style) {
        return new Font(name, style, pointSize, transform, attributes);
    }

    public Font deriveFont(Map<? extends AttributedCharacterIterator.Attribute, ?> newAttributes) {
        if (newAttributes == null) {
            throw new NullPointerException("attributes");
        }
        Map<AttributedCharacterIterator.Attribute, Object> merged = new HashMap<>(attributes);
        merged.putAll(newAttributes);
        FontState state = fontState(merged);
        if (!containsPrimaryAttribute(newAttributes)) {
            state.name = name;
            state.style = style;
            state.size = pointSize;
            state.transform = new AffineTransform(transform);
        }
        return new Font(state);
    }

    public boolean canDisplay(char character) {
        return !isSurrogate(character) && isDisplayableScalar(character);
    }

    public boolean canDisplay(int codePoint) {
        if (codePoint < 0 || codePoint > 0x10ffff) {
            throw new IllegalArgumentException("Invalid Unicode code point: " + codePoint);
        }
        return (codePoint < 0xd800 || codePoint > 0xdfff) && isDisplayableScalar(codePoint);
    }

    public int canDisplayUpTo(String text) {
        if (text == null) {
            throw new NullPointerException("text");
        }
        return canDisplayUpTo(text.toCharArray(), 0, text.length());
    }

    public int canDisplayUpTo(char[] text, int start, int limit) {
        checkRange(text.length, start, limit);
        for (int index = start; index < limit; index++) {
            int character = text[index];
            if (isHighSurrogate(character) && index + 1 < limit && isLowSurrogate(text[index + 1])) {
                int codePoint = 0x10000 + ((character - 0xd800) << 10) + (text[index + 1] - 0xdc00);
                if (!canDisplay(codePoint)) {
                    return index;
                }
                index++;
            } else if (!canDisplay((char) character)) {
                return index;
            }
        }
        return -1;
    }

    public int canDisplayUpTo(CharacterIterator iterator, int start, int limit) {
        if (iterator == null) {
            throw new NullPointerException("iterator");
        }
        if (start < iterator.getBeginIndex() || limit < start || limit > iterator.getEndIndex()) {
            throw new IndexOutOfBoundsException("Invalid character iterator range");
        }
        int original = iterator.getIndex();
        char[] text = new char[limit - start];
        for (int index = start; index < limit; index++) {
            text[index - start] = iterator.setIndex(index);
        }
        iterator.setIndex(original);
        int result = canDisplayUpTo(text, 0, text.length);
        return result < 0 ? -1 : start + result;
    }

    public float getItalicAngle() {
        return isItalic() ? -0.2f : 0.0f;
    }

    public boolean hasUniformLineMetrics() {
        return true;
    }

    public LineMetrics getLineMetrics(String text, FontRenderContext context) {
        if (text == null) {
            throw new NullPointerException("text");
        }
        return getLineMetrics(text, 0, text.length(), context);
    }

    public LineMetrics getLineMetrics(String text, int start, int end, FontRenderContext context) {
        if (text == null) {
            throw new NullPointerException("text");
        }
        checkRange(text.length(), start, end);
        return new SimpleLineMetrics(end - start);
    }

    public LineMetrics getLineMetrics(char[] text, int start, int end, FontRenderContext context) {
        if (text == null) {
            throw new NullPointerException("text");
        }
        checkRange(text.length, start, end);
        return new SimpleLineMetrics(end - start);
    }

    public LineMetrics getLineMetrics(CharacterIterator iterator, int start, int limit, FontRenderContext context) {
        if (iterator == null) {
            throw new NullPointerException("iterator");
        }
        checkIteratorRange(iterator, start, limit);
        return new SimpleLineMetrics(limit - start);
    }

    public GlyphVector createGlyphVector(FontRenderContext context, String text) {
        if (text == null) {
            throw new NullPointerException("text");
        }
        return new SimpleGlyphVector(context, text.toCharArray(), 0, text.length(), LAYOUT_LEFT_TO_RIGHT);
    }

    public GlyphVector createGlyphVector(FontRenderContext context, char[] text) {
        if (text == null) {
            throw new NullPointerException("text");
        }
        return new SimpleGlyphVector(context, text, 0, text.length, LAYOUT_LEFT_TO_RIGHT);
    }

    public GlyphVector createGlyphVector(FontRenderContext context, CharacterIterator iterator) {
        if (iterator == null) {
            throw new NullPointerException("iterator");
        }
        int original = iterator.getIndex();
        char[] text = new char[iterator.getEndIndex() - iterator.getBeginIndex()];
        for (int index = iterator.getBeginIndex(); index < iterator.getEndIndex(); index++) {
            text[index - iterator.getBeginIndex()] = iterator.setIndex(index);
        }
        iterator.setIndex(original);
        return createGlyphVector(context, text);
    }

    public GlyphVector createGlyphVector(FontRenderContext context, int[] glyphCodes) {
        if (glyphCodes == null) {
            throw new NullPointerException("glyphCodes");
        }
        return new SimpleGlyphVector(context, glyphCodes);
    }

    public GlyphVector layoutGlyphVector(FontRenderContext context, char[] text, int start, int limit, int flags) {
        if (text == null) {
            throw new NullPointerException("text");
        }
        checkRange(text.length, start, limit);
        if ((flags & ~(LAYOUT_RIGHT_TO_LEFT | LAYOUT_NO_START_CONTEXT | LAYOUT_NO_LIMIT_CONTEXT)) != 0) {
            throw new IllegalArgumentException("Invalid layout flags: " + flags);
        }
        return new SimpleGlyphVector(context, text, start, limit, flags);
    }

    public Rectangle2D getStringBounds(String text, FontRenderContext context) {
        if (text == null) {
            throw new NullPointerException("text");
        }
        return getStringBounds(text, 0, text.length(), context);
    }

    public Rectangle2D getStringBounds(String text, int start, int end, FontRenderContext context) {
        if (text == null) {
            throw new NullPointerException("text");
        }
        checkRange(text.length(), start, end);
        return bounds(advance(text, start, end), context);
    }

    public Rectangle2D getStringBounds(char[] text, int start, int end, FontRenderContext context) {
        if (text == null) {
            throw new NullPointerException("text");
        }
        checkRange(text.length, start, end);
        return bounds(advance(text, start, end), context);
    }

    public Rectangle2D getStringBounds(CharacterIterator iterator, int start, int limit, FontRenderContext context) {
        if (iterator == null) {
            throw new NullPointerException("iterator");
        }
        if (start < iterator.getBeginIndex() || limit < start || limit > iterator.getEndIndex()) {
            throw new IndexOutOfBoundsException("Invalid character iterator range");
        }
        int original = iterator.getIndex();
        StringBuilder text = new StringBuilder();
        try {
            for (int index = start; index < limit; index++) {
                text.append(iterator.setIndex(index));
            }
        } finally {
            iterator.setIndex(original);
        }
        return bounds(advance(text.toString(), 0, text.length()), context);
    }

    public Rectangle2D getMaxCharBounds(FontRenderContext context) {
        double horizontalScale = horizontalScale();
        double verticalScale = verticalScale();
        double ascent = pointSize * 0.8 * verticalScale;
        double height = pointSize * 1.1 * verticalScale;
        return new Rectangle2D.Double(0.0, -ascent, pointSize * horizontalScale, height);
    }

    double measuredAdvance(String text) {
        double[] metrics = browserMetrics(text);
        return metrics == null ? Double.NaN : metrics[0] * horizontalScale();
    }

    private double[] browserMetrics(String text) {
        if (pointSize <= 0 || !Float.isFinite(pointSize)) {
            return null;
        }
        double[] metrics = WebAwtRuntime.measureText(FontCss.font(getFamily(), isBold(), isItalic(), pointSize), text);
        return metrics != null && metrics.length >= 3 && Double.isFinite(metrics[0]) && Double.isFinite(metrics[1]) && Double.isFinite(metrics[2]) ? metrics : null;
    }

    double approximateAdvance(int codePoint) {
        if (Character.isValidCodePoint(codePoint)) {
            double measured = measuredAdvance(new String(Character.toChars(codePoint)));
            if (!Double.isNaN(measured)) {
                return measured;
            }
        }
        double em;
        if (codePoint == 0x200b || codePoint >= 0x300 && codePoint <= 0x36f) {
            em = 0.0;
        } else if (codePoint == '\t') {
            em = 1.32;
        } else if (codePoint == ' ') {
            em = 0.33;
        } else if (getFamily().equals(MONOSPACED) || getFamily().equals(DIALOG_INPUT)) {
            em = 0.6;
        } else if (codePoint >= 0x2e80 || codePoint > 0xffff) {
            em = 1.0;
        } else if (isNarrow(codePoint)) {
            em = 0.3;
        } else if (isWide(codePoint)) {
            em = 0.8;
        } else if (codePoint >= 'A' && codePoint <= 'Z') {
            em = 0.65;
        } else if (codePoint >= '0' && codePoint <= '9') {
            em = 0.56;
        } else {
            em = 0.55;
        }
        return em * pointSize * (isBold() ? 1.04 : 1.0) * horizontalScale();
    }

    double approximateAscent() {
        double[] metrics = browserMetrics("");
        return (metrics == null ? pointSize * 0.8 : metrics[1]) * verticalScale();
    }

    double approximateDescent() {
        double[] metrics = browserMetrics("");
        return (metrics == null ? pointSize * 0.2 : metrics[2]) * verticalScale();
    }

    double approximateLeading() {
        return browserMetrics("") == null ? pointSize * 0.1 * verticalScale() : 0.0;
    }

    private Rectangle2D bounds(double width, FontRenderContext context) {
        double ascent = approximateAscent();
        double height = approximateAscent() + approximateDescent() + approximateLeading();
        double adjustedWidth = context != null && !context.usesFractionalMetrics() ? Math.round(width) : width;
        return new Rectangle2D.Double(0.0, -ascent, adjustedWidth, height);
    }

    private double advance(String text, int start, int end) {
        double measured = measuredAdvance(text.substring(start, end));
        if (!Double.isNaN(measured)) {
            return measured;
        }
        double width = 0.0;
        for (int index = start; index < end; index++) {
            int character = text.charAt(index);
            if (isHighSurrogate(character) && index + 1 < end && isLowSurrogate(text.charAt(index + 1))) {
                character = 0x10000 + ((character - 0xd800) << 10) + (text.charAt(++index) - 0xdc00);
            }
            width += approximateAdvance(character);
        }
        return width;
    }

    private double advance(char[] text, int start, int end) {
        double measured = measuredAdvance(new String(text, start, end - start));
        if (!Double.isNaN(measured)) {
            return measured;
        }
        double width = 0.0;
        for (int index = start; index < end; index++) {
            int character = text[index];
            if (isHighSurrogate(character) && index + 1 < end && isLowSurrogate(text[index + 1])) {
                character = 0x10000 + ((character - 0xd800) << 10) + (text[++index] - 0xdc00);
            }
            width += approximateAdvance(character);
        }
        return width;
    }

    private double horizontalScale() {
        return Math.sqrt(transform.getScaleX() * transform.getScaleX() + transform.getShearY() * transform.getShearY());
    }

    private double verticalScale() {
        return Math.sqrt(transform.getShearX() * transform.getShearX() + transform.getScaleY() * transform.getScaleY());
    }

    private String styledName() {
        return name + "." + styleName();
    }

    private String styleName() {
        switch (style) {
            case BOLD:
                return "bold";
            case ITALIC:
                return "italic";
            case BOLD | ITALIC:
                return "bolditalic";
            default:
                return "plain";
        }
    }

    private static FontState fontState(Map<? extends AttributedCharacterIterator.Attribute, ?> source) {
        if (source == null) {
            throw new NullPointerException("attributes");
        }
        FontState state = new FontState();
        Map<AttributedCharacterIterator.Attribute, Object> copied = new HashMap<>();
        for (Map.Entry<? extends AttributedCharacterIterator.Attribute, ?> entry : source.entrySet()) {
            AttributedCharacterIterator.Attribute key = entry.getKey();
            Object value = entry.getValue();
            if (key == null) {
                continue;
            }
            copied.put(key, value);
            applyAttribute(state, attributeName(key), value);
        }
        state.attributes = copied.isEmpty() ? Collections.emptyMap() : Collections.unmodifiableMap(copied);
        return state;
    }

    private static void applyAttribute(FontState state, String key, Object value) {
        if (key.equals("font") && value instanceof Font) {
            Font font = (Font) value;
            state.name = font.name;
            state.style = font.style;
            state.size = font.pointSize;
            state.transform = new AffineTransform(font.transform);
        } else if (key.equals("family") && value instanceof String) {
            state.name = (String) value;
        } else if (key.equals("size") && value instanceof Number) {
            state.size = ((Number) value).floatValue();
        } else if (key.equals("weight") && value instanceof Number && ((Number) value).floatValue() >= 1.5f) {
            state.style |= BOLD;
        } else if (key.equals("posture") && value instanceof Number && ((Number) value).floatValue() > 0.0f) {
            state.style |= ITALIC;
        } else if (key.equals("transform") && value instanceof AffineTransform) {
            state.transform = new AffineTransform((AffineTransform) value);
        }
    }

    private static boolean containsPrimaryAttribute(Map<? extends AttributedCharacterIterator.Attribute, ?> source) {
        for (AttributedCharacterIterator.Attribute attribute : source.keySet()) {
            String name = attributeName(attribute);
            if (name.equals("font") || name.equals("family") || name.equals("size") || name.equals("weight") || name.equals("posture") || name.equals("transform")) {
                return true;
            }
        }
        return false;
    }

    private static String attributeName(AttributedCharacterIterator.Attribute attribute) {
        if (attribute == null) {
            return "";
        }
        String text = attribute.toString();
        int open = text.lastIndexOf('(');
        return open >= 0 && text.endsWith(")") ? text.substring(open + 1, text.length() - 1).toLowerCase(Locale.ROOT) : text.toLowerCase(Locale.ROOT);
    }

    private static int separatorBeforeLastToken(String value) {
        int hyphen = value.lastIndexOf('-');
        int space = value.lastIndexOf(' ');
        return Math.max(hyphen, space);
    }

    private static String trimSeparators(String value) {
        int end = value.length();
        while (end > 0 && (value.charAt(end - 1) == '-' || value.charAt(end - 1) == ' ')) {
            end--;
        }
        return value.substring(0, end);
    }

    private static int parseStyle(String value) {
        if (value.equalsIgnoreCase("plain")) {
            return PLAIN;
        }
        if (value.equalsIgnoreCase("bold")) {
            return BOLD;
        }
        if (value.equalsIgnoreCase("italic")) {
            return ITALIC;
        }
        return value.equalsIgnoreCase("bolditalic") ? BOLD | ITALIC : -1;
    }

    private static String logicalFamily(String candidate) {
        if (candidate.equalsIgnoreCase(DIALOG) || candidate.equalsIgnoreCase("Default")) {
            return DIALOG;
        }
        if (candidate.equalsIgnoreCase(DIALOG_INPUT)) {
            return DIALOG_INPUT;
        }
        if (candidate.equalsIgnoreCase(SANS_SERIF)) {
            return SANS_SERIF;
        }
        if (candidate.equalsIgnoreCase(SERIF)) {
            return SERIF;
        }
        return candidate.equalsIgnoreCase(MONOSPACED) ? MONOSPACED : candidate;
    }

    private static void checkRange(int length, int start, int end) {
        if (start < 0 || end < start || end > length) {
            throw new IndexOutOfBoundsException("Invalid text range: " + start + ".." + end);
        }
    }

    private static void checkIteratorRange(CharacterIterator iterator, int start, int limit) {
        if (start < iterator.getBeginIndex() || limit < start || limit > iterator.getEndIndex()) {
            throw new IndexOutOfBoundsException("Invalid character iterator range");
        }
    }

    private static void requireTransform(AffineTransform transform) {
        if (transform == null) {
            throw new IllegalArgumentException("transform must not be null");
        }
    }

    private static int validStyle(int style) {
        return style < PLAIN || style > (BOLD | ITALIC) ? PLAIN : style;
    }

    private static int roundedSize(float size) {
        return (int) (size + 0.5f);
    }

    private static boolean isSurrogate(int value) {
        return value >= 0xd800 && value <= 0xdfff;
    }

    private static boolean isHighSurrogate(int value) {
        return value >= 0xd800 && value <= 0xdbff;
    }

    private static boolean isLowSurrogate(int value) {
        return value >= 0xdc00 && value <= 0xdfff;
    }

    private static boolean isDisplayableScalar(int value) {
        return value >= 0x20 && !(value >= 0x7f && value <= 0x9f) && value != 0xffff && value != 0xfffe;
    }

    private static boolean isNarrow(int value) {
        return value == 'i' || value == 'l' || value == 'I' || value == '.' || value == ',' || value == '\'' || value == '`' || value == '!' || value == '|' || value == ':' || value == ';' || value == '[' || value == ']';
    }

    private static boolean isWide(int value) {
        return value == 'M' || value == 'W' || value == '@' || value == '#' || value == '%' || value == '&' || value == 'Q' || value == 'O';
    }

    private final class SimpleLineMetrics extends LineMetrics {
        private final int characterCount;

        private SimpleLineMetrics(int characterCount) {
            this.characterCount = characterCount;
        }

        @Override
        public int getNumChars() {
            return characterCount;
        }

        @Override
        public float getAscent() {
            return (float) approximateAscent();
        }

        @Override
        public float getDescent() {
            return (float) approximateDescent();
        }

        @Override
        public float getLeading() {
            return (float) approximateLeading();
        }

        @Override
        public float getHeight() {
            return getAscent() + getDescent() + getLeading();
        }

        @Override
        public int getBaselineIndex() {
            return ROMAN_BASELINE;
        }

        @Override
        public float[] getBaselineOffsets() {
            return new float[] {0.0f, (getDescent() - getAscent()) / 2.0f, -getAscent()};
        }

        @Override
        public float getStrikethroughOffset() {
            return -getAscent() * 0.3f;
        }

        @Override
        public float getStrikethroughThickness() {
            return Math.max(1.0f, pointSize / 12.0f);
        }

        @Override
        public float getUnderlineOffset() {
            return getDescent() * 0.5f;
        }

        @Override
        public float getUnderlineThickness() {
            return Math.max(1.0f, pointSize / 12.0f);
        }
    }

    private final class SimpleGlyphVector extends GlyphVector implements org.webawt.internal.peer.GlyphTextSource {
        private final int[] characterIndices;
        private final FontRenderContext context;
        private final int[] glyphCodes;
        private final int layoutFlags;
        private final float[] positions;
        private final AffineTransform[] transforms;

        private SimpleGlyphVector(FontRenderContext context, char[] text, int start, int limit, int flags) {
            this.context = context == null ? new FontRenderContext(null, false, false) : context;
            int glyphCount = countGlyphs(text, start, limit);
            glyphCodes = new int[glyphCount];
            characterIndices = new int[glyphCount];
            boolean rightToLeft = (flags & LAYOUT_RIGHT_TO_LEFT) != 0;
            int glyphIndex = rightToLeft ? glyphCount - 1 : 0;
            for (int characterIndex = start; characterIndex < limit; characterIndex++) {
                int codePoint = text[characterIndex];
                if (isHighSurrogate(codePoint) && characterIndex + 1 < limit && isLowSurrogate(text[characterIndex + 1])) {
                    codePoint = 0x10000 + ((codePoint - 0xd800) << 10) + (text[characterIndex + 1] - 0xdc00);
                    characterIndex++;
                }
                glyphCodes[glyphIndex] = canDisplay(codePoint) ? codePoint : getMissingGlyphCode();
                characterIndices[glyphIndex] = characterIndex - start - (codePoint > 0xffff ? 1 : 0);
                glyphIndex += rightToLeft ? -1 : 1;
            }
            layoutFlags = rightToLeft ? FLAG_RUN_RTL : 0;
            positions = new float[(glyphCount + 1) * 2];
            transforms = new AffineTransform[glyphCount];
            performDefaultLayout();
        }

        private SimpleGlyphVector(FontRenderContext context, int[] glyphCodes) {
            this.context = context == null ? new FontRenderContext(null, false, false) : context;
            this.glyphCodes = new int[glyphCodes.length];
            System.arraycopy(glyphCodes, 0, this.glyphCodes, 0, glyphCodes.length);
            characterIndices = new int[glyphCodes.length];
            for (int index = 0; index < characterIndices.length; index++) {
                characterIndices[index] = index;
            }
            layoutFlags = 0;
            positions = new float[(glyphCodes.length + 1) * 2];
            transforms = new AffineTransform[glyphCodes.length];
            performDefaultLayout();
        }

        @Override
        public Font getFont() {
            return Font.this;
        }

        @Override
        public FontRenderContext getFontRenderContext() {
            return context;
        }

        @Override
        public void performDefaultLayout() {
            float advance = 0.0f;
            for (int index = 0; index < glyphCodes.length; index++) {
                positions[index * 2] = advance;
                positions[index * 2 + 1] = 0.0f;
                advance += (float) approximateAdvance(glyphCodes[index]);
            }
            positions[glyphCodes.length * 2] = advance;
            positions[glyphCodes.length * 2 + 1] = 0.0f;
        }

        @Override
        public int getNumGlyphs() {
            return glyphCodes.length;
        }

        @Override
        public int getGlyphCode(int glyphIndex) {
            checkGlyphIndex(glyphIndex);
            return glyphCodes[glyphIndex];
        }

        @Override
        public String getGlyphText(int glyphIndex) {
            int codePoint = getGlyphCode(glyphIndex);
            return new String(Character.toChars(codePoint > 0 && codePoint <= 0x10ffff ? codePoint : 0xfffd));
        }

        @Override
        public int[] getGlyphCodes(int beginGlyphIndex, int numEntries, int[] codeReturn) {
            checkGlyphRange(beginGlyphIndex, numEntries);
            int[] result = codeReturn == null ? new int[numEntries] : codeReturn;
            if (result.length < numEntries) {
                throw new IllegalArgumentException("codeReturn is too small");
            }
            System.arraycopy(glyphCodes, beginGlyphIndex, result, 0, numEntries);
            return result;
        }

        @Override
        public int getGlyphCharIndex(int glyphIndex) {
            checkGlyphIndex(glyphIndex);
            return characterIndices[glyphIndex];
        }

        @Override
        public Rectangle2D getLogicalBounds() {
            return vectorBounds();
        }

        @Override
        public Rectangle2D getVisualBounds() {
            return getOutline().getBounds2D();
        }

        @Override
        public Shape getOutline() {
            return getOutline(0.0f, 0.0f);
        }

        @Override
        public Shape getOutline(float x, float y) {
            Path2D.Double outline = new Path2D.Double();
            for (int index = 0; index < glyphCodes.length; index++) {
                outline.append(getGlyphOutline(index, x, y), false);
            }
            return outline;
        }

        @Override
        public Shape getGlyphOutline(int glyphIndex) {
            checkGlyphIndex(glyphIndex);
            Shape outline = glyphBounds(glyphIndex);
            AffineTransform glyphTransform = transforms[glyphIndex];
            return glyphTransform == null ? outline : glyphTransform.createTransformedShape(outline);
        }

        @Override
        public Point2D getGlyphPosition(int glyphIndex) {
            checkPositionIndex(glyphIndex);
            return new Point2D.Float(positions[glyphIndex * 2], positions[glyphIndex * 2 + 1]);
        }

        @Override
        public void setGlyphPosition(int glyphIndex, Point2D newPosition) {
            checkPositionIndex(glyphIndex);
            if (newPosition == null) {
                throw new NullPointerException("newPosition");
            }
            positions[glyphIndex * 2] = (float) newPosition.getX();
            positions[glyphIndex * 2 + 1] = (float) newPosition.getY();
        }

        @Override
        public AffineTransform getGlyphTransform(int glyphIndex) {
            checkGlyphIndex(glyphIndex);
            return transforms[glyphIndex] == null ? null : new AffineTransform(transforms[glyphIndex]);
        }

        @Override
        public void setGlyphTransform(int glyphIndex, AffineTransform newTransform) {
            checkGlyphIndex(glyphIndex);
            transforms[glyphIndex] = newTransform == null || newTransform.isIdentity() ? null : new AffineTransform(newTransform);
        }

        @Override
        public int getLayoutFlags() {
            int flags = layoutFlags;
            for (AffineTransform glyphTransform : transforms) {
                if (glyphTransform != null) {
                    flags |= FLAG_HAS_TRANSFORMS;
                    break;
                }
            }
            if (hasPositionAdjustments()) {
                flags |= FLAG_HAS_POSITION_ADJUSTMENTS;
            }
            return flags;
        }

        @Override
        public float[] getGlyphPositions(int beginGlyphIndex, int numEntries, float[] positionReturn) {
            if (beginGlyphIndex < 0 || numEntries < 0 || beginGlyphIndex > glyphCodes.length + 1 - numEntries) {
                throw new IndexOutOfBoundsException("Invalid glyph position range");
            }
            int length = numEntries * 2;
            float[] result = positionReturn == null ? new float[length] : positionReturn;
            if (result.length < length) {
                throw new IllegalArgumentException("positionReturn is too small");
            }
            System.arraycopy(positions, beginGlyphIndex * 2, result, 0, length);
            return result;
        }

        @Override
        public Shape getGlyphLogicalBounds(int glyphIndex) {
            checkGlyphIndex(glyphIndex);
            return glyphBounds(glyphIndex);
        }

        @Override
        public Shape getGlyphVisualBounds(int glyphIndex) {
            return getGlyphOutline(glyphIndex);
        }

        @Override
        public GlyphMetrics getGlyphMetrics(int glyphIndex) {
            checkGlyphIndex(glyphIndex);
            float advance = (float) approximateAdvance(glyphCodes[glyphIndex]);
            byte type = glyphCodes[glyphIndex] == ' ' || glyphCodes[glyphIndex] == '\t' ? GlyphMetrics.WHITESPACE : GlyphMetrics.STANDARD;
            return new GlyphMetrics(advance, new Rectangle2D.Float(0.0f, -(float) approximateAscent(), advance, (float) (approximateAscent() + approximateDescent())), type);
        }

        @Override
        public GlyphJustificationInfo getGlyphJustificationInfo(int glyphIndex) {
            GlyphMetrics metrics = getGlyphMetrics(glyphIndex);
            int priority = metrics.isWhitespace() ? GlyphJustificationInfo.PRIORITY_WHITESPACE : GlyphJustificationInfo.PRIORITY_INTERCHAR;
            float weight = metrics.getAdvance();
            return new GlyphJustificationInfo(weight, false, priority, weight / 2.0f, weight / 2.0f, false, priority, weight / 3.0f, weight / 3.0f);
        }

        @Override
        public boolean equals(GlyphVector glyphVector) {
            if (!(glyphVector instanceof Font.SimpleGlyphVector)) {
                return false;
            }
            SimpleGlyphVector other = (SimpleGlyphVector) glyphVector;
            if (!Font.this.equals(other.getFont()) || glyphCodes.length != other.glyphCodes.length || getLayoutFlags() != other.getLayoutFlags()) {
                return false;
            }
            for (int index = 0; index < glyphCodes.length; index++) {
                if (glyphCodes[index] != other.glyphCodes[index] || characterIndices[index] != other.characterIndices[index] || !sameTransform(transforms[index], other.transforms[index])) {
                    return false;
                }
            }
            for (int index = 0; index < positions.length; index++) {
                if (Float.floatToIntBits(positions[index]) != Float.floatToIntBits(other.positions[index])) {
                    return false;
                }
            }
            return true;
        }

        private Rectangle2D vectorBounds() {
            if (glyphCodes.length == 0) {
                return new Rectangle2D.Float();
            }
            Rectangle2D result = getGlyphLogicalBounds(0).getBounds2D();
            for (int index = 1; index < glyphCodes.length; index++) {
                result.add(getGlyphLogicalBounds(index).getBounds2D());
            }
            return result;
        }

        private Rectangle2D glyphBounds(int glyphIndex) {
            float x = positions[glyphIndex * 2];
            float y = positions[glyphIndex * 2 + 1] - (float) approximateAscent();
            float width = (float) approximateAdvance(glyphCodes[glyphIndex]);
            float height = (float) (approximateAscent() + approximateDescent());
            return new Rectangle2D.Float(x, y, width, height);
        }

        private boolean hasPositionAdjustments() {
            float advance = 0.0f;
            for (int index = 0; index < glyphCodes.length; index++) {
                if (Float.compare(positions[index * 2], advance) != 0 || Float.compare(positions[index * 2 + 1], 0.0f) != 0) {
                    return true;
                }
                advance += (float) approximateAdvance(glyphCodes[index]);
            }
            return Float.compare(positions[glyphCodes.length * 2], advance) != 0 || Float.compare(positions[glyphCodes.length * 2 + 1], 0.0f) != 0;
        }

        private void checkPositionIndex(int glyphIndex) {
            if (glyphIndex < 0 || glyphIndex > glyphCodes.length) {
                throw new IndexOutOfBoundsException("Invalid glyph position index: " + glyphIndex);
            }
        }
    }

    private static int countGlyphs(char[] text, int start, int limit) {
        int count = 0;
        for (int index = start; index < limit; index++) {
            if (isHighSurrogate(text[index]) && index + 1 < limit && isLowSurrogate(text[index + 1])) {
                index++;
            }
            count++;
        }
        return count;
    }

    private static boolean sameTransform(AffineTransform first, AffineTransform second) {
        return first == null ? second == null : first.equals(second);
    }

    private static final class FontState {
        private String name = DIALOG;
        private int style = PLAIN;
        private float size = 12.0f;
        private AffineTransform transform = new AffineTransform();
        private Map<AttributedCharacterIterator.Attribute, Object> attributes = Collections.emptyMap();
    }
}
