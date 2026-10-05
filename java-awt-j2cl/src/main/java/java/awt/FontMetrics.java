package java.awt;

import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.text.CharacterIterator;

public abstract class FontMetrics implements Serializable {
    private static final long serialVersionUID = 1681126225205050147L;
    private static final FontRenderContext DEFAULT_CONTEXT = new FontRenderContext(null, false, false);
    protected Font font;

    protected FontMetrics(Font font) {
        if (font == null) {
            throw new NullPointerException("font");
        }
        this.font = font;
    }

    public Font getFont() {
        return font;
    }

    public FontRenderContext getFontRenderContext() {
        return DEFAULT_CONTEXT;
    }

    public int getLeading() {
        return (int) Math.ceil(font.approximateLeading());
    }

    public int getAscent() {
        return (int) Math.ceil(font.approximateAscent());
    }

    public int getDescent() {
        return (int) Math.ceil(font.approximateDescent());
    }

    public int getHeight() {
        return getLeading() + getAscent() + getDescent();
    }

    public int getMaxAscent() {
        return getAscent();
    }

    public int getMaxDescent() {
        return getDescent();
    }

    public int getMaxDecent() {
        return getMaxDescent();
    }

    public int getMaxAdvance() {
        return (int) Math.ceil(font.getSize2D());
    }

    public int charWidth(int codePoint) {
        return (int) Math.round(font.approximateAdvance(codePoint));
    }

    public int charWidth(char character) {
        return charWidth((int) character);
    }

    public int stringWidth(String text) {
        if (text == null) {
            throw new NullPointerException("text");
        }
        double measured = font.measuredAdvance(text);
        if (!Double.isNaN(measured)) {
            return (int) Math.round(measured);
        }
        int width = 0;
        for (int index = 0; index < text.length(); index++) {
            int character = text.charAt(index);
            if (character >= 0xd800 && character <= 0xdbff && index + 1 < text.length()) {
                int low = text.charAt(index + 1);
                if (low >= 0xdc00 && low <= 0xdfff) {
                    character = 0x10000 + ((character - 0xd800) << 10) + (low - 0xdc00);
                    index++;
                }
            }
            width += charWidth(character);
        }
        return width;
    }

    public int charsWidth(char[] characters, int offset, int length) {
        if (characters == null) {
            throw new NullPointerException("characters");
        }
        checkRange(characters.length, offset, length);
        double measured = font.measuredAdvance(new String(characters, offset, length));
        if (!Double.isNaN(measured)) {
            return (int) Math.round(measured);
        }
        int width = 0;
        int limit = offset + length;
        for (int index = offset; index < limit; index++) {
            int character = characters[index];
            if (character >= 0xd800 && character <= 0xdbff && index + 1 < limit) {
                int low = characters[index + 1];
                if (low >= 0xdc00 && low <= 0xdfff) {
                    character = 0x10000 + ((character - 0xd800) << 10) + (low - 0xdc00);
                    index++;
                }
            }
            width += charWidth(character);
        }
        return width;
    }

    public int bytesWidth(byte[] bytes, int offset, int length) {
        if (bytes == null) {
            throw new NullPointerException("bytes");
        }
        checkRange(bytes.length, offset, length);
        char[] text = new char[length];
        for (int index = 0; index < length; index++) {
            text[index] = (char) (bytes[offset + index] & 0xff);
        }
        return charsWidth(text, 0, length);
    }

    public int[] getWidths() {
        int[] widths = new int[256];
        for (int character = 0; character < widths.length; character++) {
            widths[character] = charWidth(character);
        }
        return widths;
    }

    public boolean hasUniformLineMetrics() {
        return font.hasUniformLineMetrics();
    }

    public LineMetrics getLineMetrics(String text, Graphics graphics) {
        return font.getLineMetrics(text, context(graphics));
    }

    public LineMetrics getLineMetrics(String text, int start, int end, Graphics graphics) {
        return font.getLineMetrics(text, start, end, context(graphics));
    }

    public LineMetrics getLineMetrics(char[] text, int start, int end, Graphics graphics) {
        return font.getLineMetrics(text, start, end, context(graphics));
    }

    public LineMetrics getLineMetrics(CharacterIterator iterator, int start, int limit, Graphics graphics) {
        return font.getLineMetrics(iterator, start, limit, context(graphics));
    }

    public Rectangle2D getStringBounds(String text, Graphics graphics) {
        return font.getStringBounds(text, context(graphics));
    }

    public Rectangle2D getStringBounds(String text, int start, int end, Graphics graphics) {
        return font.getStringBounds(text, start, end, context(graphics));
    }

    public Rectangle2D getStringBounds(char[] text, int start, int end, Graphics graphics) {
        return font.getStringBounds(text, start, end, context(graphics));
    }

    public Rectangle2D getStringBounds(CharacterIterator iterator, int start, int limit, Graphics graphics) {
        return font.getStringBounds(iterator, start, limit, context(graphics));
    }

    public Rectangle2D getMaxCharBounds(Graphics graphics) {
        return font.getMaxCharBounds(context(graphics));
    }

    @Override
    public String toString() {
        return getClass().getName() + "[font=" + font + "ascent=" + getAscent() + ", descent=" + getDescent() + ", height=" + getHeight() + "]";
    }

    private static void checkRange(int arrayLength, int offset, int length) {
        if (offset < 0 || length < 0 || offset > arrayLength - length) {
            throw new IndexOutOfBoundsException("Invalid array range: " + offset + " + " + length);
        }
    }

    private FontRenderContext context(Graphics graphics) {
        return graphics instanceof Graphics2D ? ((Graphics2D) graphics).getFontRenderContext() : getFontRenderContext();
    }
}
