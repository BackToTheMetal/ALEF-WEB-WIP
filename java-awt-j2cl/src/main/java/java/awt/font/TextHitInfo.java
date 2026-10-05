package java.awt.font;

public final class TextHitInfo {
    private final int charIndex;
    private final boolean leadingEdge;

    private TextHitInfo(int charIndex, boolean leadingEdge) {
        this.charIndex = charIndex;
        this.leadingEdge = leadingEdge;
    }

    public int getCharIndex() {
        return charIndex;
    }

    public boolean isLeadingEdge() {
        return leadingEdge;
    }

    public int getInsertionIndex() {
        return leadingEdge ? charIndex : charIndex + 1;
    }

    @Override
    public int hashCode() {
        return charIndex;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof TextHitInfo && equals((TextHitInfo) object);
    }

    public boolean equals(TextHitInfo hitInfo) {
        return hitInfo != null && charIndex == hitInfo.charIndex && leadingEdge == hitInfo.leadingEdge;
    }

    @Override
    public String toString() {
        return "TextHitInfo[" + charIndex + (leadingEdge ? "L" : "T") + "]";
    }

    public static TextHitInfo leading(int charIndex) {
        return new TextHitInfo(charIndex, true);
    }

    public static TextHitInfo trailing(int charIndex) {
        return new TextHitInfo(charIndex, false);
    }

    public static TextHitInfo beforeOffset(int offset) {
        return trailing(offset - 1);
    }

    public static TextHitInfo afterOffset(int offset) {
        return leading(offset);
    }

    public TextHitInfo getOtherHit() {
        return leadingEdge ? trailing(charIndex - 1) : leading(charIndex + 1);
    }

    public TextHitInfo getOffsetHit(int delta) {
        return new TextHitInfo(charIndex + delta, leadingEdge);
    }
}
