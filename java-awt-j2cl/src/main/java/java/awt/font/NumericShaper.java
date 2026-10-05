package java.awt.font;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class NumericShaper implements Serializable {
    public enum Range {
        EUROPEAN(0x0030, 0x0000, 0x024f, 1 << 0, true),
        ARABIC(0x0660, 0x0600, 0x06ff, 1 << 1, true),
        EASTERN_ARABIC(0x06f0, 0x0600, 0x06ff, 1 << 2, true),
        DEVANAGARI(0x0966, 0x0900, 0x097f, 1 << 3, true),
        BENGALI(0x09e6, 0x0980, 0x09ff, 1 << 4, true),
        GURMUKHI(0x0a66, 0x0a00, 0x0a7f, 1 << 5, true),
        GUJARATI(0x0ae6, 0x0a80, 0x0aff, 1 << 6, true),
        ORIYA(0x0b66, 0x0b00, 0x0b7f, 1 << 7, true),
        TAMIL(0x0be6, 0x0b80, 0x0bff, 1 << 8, true),
        TELUGU(0x0c66, 0x0c00, 0x0c7f, 1 << 9, true),
        KANNADA(0x0ce6, 0x0c80, 0x0cff, 1 << 10, true),
        MALAYALAM(0x0d66, 0x0d00, 0x0d7f, 1 << 11, true),
        THAI(0x0e50, 0x0e00, 0x0e7f, 1 << 12, true),
        LAO(0x0ed0, 0x0e80, 0x0eff, 1 << 13, true),
        TIBETAN(0x0f20, 0x0f00, 0x0fff, 1 << 14, true),
        MYANMAR(0x1040, 0x1000, 0x109f, 1 << 15, true),
        ETHIOPIC(0x1368, 0x1200, 0x137f, 1 << 16, false),
        KHMER(0x17e0, 0x1780, 0x17ff, 1 << 17, true),
        MONGOLIAN(0x1810, 0x1800, 0x18af, 1 << 18, true),
        NKO(0x07c0, 0x07c0, 0x07ff, 0, true),
        MYANMAR_SHAN(0x1090, 0x1000, 0x109f, 0, true),
        LIMBU(0x1946, 0x1900, 0x194f, 0, true),
        NEW_TAI_LUE(0x19d0, 0x1980, 0x19df, 0, true),
        BALINESE(0x1b50, 0x1b00, 0x1b7f, 0, true),
        SUNDANESE(0x1bb0, 0x1b80, 0x1bbf, 0, true),
        LEPCHA(0x1c40, 0x1c00, 0x1c4f, 0, true),
        OL_CHIKI(0x1c50, 0x1c50, 0x1c7f, 0, true),
        VAI(0xa620, 0xa500, 0xa63f, 0, true),
        SAURASHTRA(0xa8d0, 0xa880, 0xa8df, 0, true),
        KAYAH_LI(0xa900, 0xa900, 0xa92f, 0, true),
        CHAM(0xaa50, 0xaa00, 0xaa5f, 0, true),
        TAI_THAM_HORA(0x1a80, 0x1a20, 0x1aaf, 0, true),
        TAI_THAM_THAM(0x1a90, 0x1a20, 0x1aaf, 0, true),
        JAVANESE(0xa9d0, 0xa980, 0xa9df, 0, true),
        MEETEI_MAYEK(0xabf0, 0xabc0, 0xabff, 0, true),
        SINHALA(0x0de6, 0x0d80, 0x0dff, 0, true),
        MYANMAR_TAI_LAING(0xa9f0, 0xa9e0, 0xa9ff, 0, true);

        private final int digitBase;
        private final int legacyMask;
        private final int maximum;
        private final int minimum;
        private final boolean zeroBased;

        Range(int digitBase, int minimum, int maximum, int legacyMask, boolean zeroBased) {
            this.digitBase = digitBase;
            this.minimum = minimum;
            this.maximum = maximum;
            this.legacyMask = legacyMask;
            this.zeroBased = zeroBased;
        }
    }

    public static final int EUROPEAN = 1 << 0;
    public static final int ARABIC = 1 << 1;
    public static final int EASTERN_ARABIC = 1 << 2;
    public static final int DEVANAGARI = 1 << 3;
    public static final int BENGALI = 1 << 4;
    public static final int GURMUKHI = 1 << 5;
    public static final int GUJARATI = 1 << 6;
    public static final int ORIYA = 1 << 7;
    public static final int TAMIL = 1 << 8;
    public static final int TELUGU = 1 << 9;
    public static final int KANNADA = 1 << 10;
    public static final int MALAYALAM = 1 << 11;
    public static final int THAI = 1 << 12;
    public static final int LAO = 1 << 13;
    public static final int TIBETAN = 1 << 14;
    public static final int MYANMAR = 1 << 15;
    public static final int ETHIOPIC = 1 << 16;
    public static final int KHMER = 1 << 17;
    public static final int MONGOLIAN = 1 << 18;
    public static final int ALL_RANGES = (1 << 19) - 1;
    private static final long serialVersionUID = -8022764705923730308L;
    private final boolean contextual;
    private final Range defaultContext;
    private final Set<Range> ranges;

    private NumericShaper(Set<Range> ranges, boolean contextual, Range defaultContext) {
        this.ranges = Collections.unmodifiableSet(new HashSet<>(ranges));
        this.contextual = contextual;
        this.defaultContext = defaultContext;
    }

    public static NumericShaper getShaper(int singleRange) {
        return getShaper(singleLegacyRange(singleRange));
    }

    public static NumericShaper getShaper(Range singleRange) {
        if (singleRange == null) {
            throw new NullPointerException("singleRange");
        }
        Set<Range> ranges = new HashSet<>();
        ranges.add(singleRange);
        return new NumericShaper(ranges, false, singleRange);
    }

    public static NumericShaper getContextualShaper(int ranges) {
        return new NumericShaper(legacyRanges(ranges), true, Range.EUROPEAN);
    }

    public static NumericShaper getContextualShaper(Set<Range> ranges) {
        return getContextualShaper(ranges, Range.EUROPEAN);
    }

    public static NumericShaper getContextualShaper(int ranges, int defaultContext) {
        return new NumericShaper(legacyRanges(ranges), true, singleLegacyRange(defaultContext));
    }

    public static NumericShaper getContextualShaper(Set<Range> ranges, Range defaultContext) {
        if (ranges == null || defaultContext == null) {
            throw new NullPointerException("ranges and defaultContext must not be null");
        }
        if (ranges.isEmpty()) {
            throw new IllegalArgumentException("ranges must not be empty");
        }
        return new NumericShaper(ranges, true, defaultContext);
    }

    public void shape(char[] text, int start, int count) {
        shape(text, start, count, defaultContext);
    }

    public void shape(char[] text, int start, int count, int context) {
        shape(text, start, count, singleLegacyRange(context));
    }

    public void shape(char[] text, int start, int count, Range context) {
        if (text == null || context == null) {
            throw new NullPointerException("text and context must not be null");
        }
        checkRange(text.length, start, count);
        Range active = contextual ? context : defaultContext;
        int limit = start + count;
        for (int index = start; index < limit; index++) {
            char character = text[index];
            if (contextual && !isEuropeanDigit(character)) {
                Range detected = contextFor(character);
                if (detected != null) {
                    active = detected;
                }
            }
            if (isEuropeanDigit(character) && ranges.contains(active)) {
                text[index] = shapeDigit(character, active);
            }
        }
    }

    public boolean isContextual() {
        return contextual;
    }

    public int getRanges() {
        int result = 0;
        for (Range range : ranges) {
            result |= range.legacyMask;
        }
        return result;
    }

    public Set<Range> getRangeSet() {
        return ranges;
    }

    @Override
    public int hashCode() {
        int result = ranges.hashCode();
        result = 31 * result + (contextual ? 1 : 0);
        return 31 * result + defaultContext.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof NumericShaper)) {
            return false;
        }
        NumericShaper shaper = (NumericShaper) object;
        return contextual == shaper.contextual && defaultContext == shaper.defaultContext && ranges.equals(shaper.ranges);
    }

    @Override
    public String toString() {
        return getClass().getName() + "[contextual:" + contextual + ", context:" + defaultContext + ", range(s):" + ranges + "]";
    }

    private static Range singleLegacyRange(int rangeMask) {
        if (rangeMask == 0 || (rangeMask & ~ALL_RANGES) != 0 || (rangeMask & rangeMask - 1) != 0) {
            throw new IllegalArgumentException("Exactly one legacy range is required: " + rangeMask);
        }
        for (Range range : Range.values()) {
            if (range.legacyMask == rangeMask) {
                return range;
            }
        }
        throw new IllegalArgumentException("Unknown legacy range: " + rangeMask);
    }

    private static Set<Range> legacyRanges(int rangeMask) {
        if (rangeMask == 0 || (rangeMask & ~ALL_RANGES) != 0) {
            throw new IllegalArgumentException("Invalid legacy ranges: " + rangeMask);
        }
        Set<Range> result = new HashSet<>();
        for (Range range : Range.values()) {
            if ((range.legacyMask & rangeMask) != 0) {
                result.add(range);
            }
        }
        return result;
    }

    private static Range contextFor(char character) {
        for (Range range : Range.values()) {
            if (character >= range.minimum && character <= range.maximum) {
                return range;
            }
        }
        return character >= 0x0041 && character <= 0x024f ? Range.EUROPEAN : null;
    }

    private static char shapeDigit(char digit, Range range) {
        int value = digit - '0';
        if (!range.zeroBased && value == 0) {
            return digit;
        }
        return (char) (range.digitBase + value);
    }

    private static boolean isEuropeanDigit(char character) {
        return character >= '0' && character <= '9';
    }

    private static void checkRange(int length, int start, int count) {
        if (start < 0 || count < 0 || start > length - count) {
            throw new IndexOutOfBoundsException("Invalid text range: " + start + " + " + count);
        }
    }
}
