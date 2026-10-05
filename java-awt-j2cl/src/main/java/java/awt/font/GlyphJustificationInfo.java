package java.awt.font;

public final class GlyphJustificationInfo {
    public static final int PRIORITY_KASHIDA = 0;
    public static final int PRIORITY_WHITESPACE = 1;
    public static final int PRIORITY_INTERCHAR = 2;
    public static final int PRIORITY_NONE = 3;
    public final float weight;
    public final int growPriority;
    public final boolean growAbsorb;
    public final float growLeftLimit;
    public final float growRightLimit;
    public final int shrinkPriority;
    public final boolean shrinkAbsorb;
    public final float shrinkLeftLimit;
    public final float shrinkRightLimit;

    public GlyphJustificationInfo(float weight, boolean growAbsorb, int growPriority, float growLeftLimit, float growRightLimit, boolean shrinkAbsorb, int shrinkPriority, float shrinkLeftLimit, float shrinkRightLimit) {
        if (weight < 0.0f) {
            throw new IllegalArgumentException("weight must be non-negative");
        }
        checkPriority(growPriority);
        checkLimit(growLeftLimit);
        checkLimit(growRightLimit);
        checkPriority(shrinkPriority);
        checkLimit(shrinkLeftLimit);
        checkLimit(shrinkRightLimit);
        this.weight = weight;
        this.growAbsorb = growAbsorb;
        this.growPriority = growPriority;
        this.growLeftLimit = growLeftLimit;
        this.growRightLimit = growRightLimit;
        this.shrinkAbsorb = shrinkAbsorb;
        this.shrinkPriority = shrinkPriority;
        this.shrinkLeftLimit = shrinkLeftLimit;
        this.shrinkRightLimit = shrinkRightLimit;
    }

    private static void checkPriority(int priority) {
        if (priority < PRIORITY_KASHIDA || priority > PRIORITY_NONE) {
            throw new IllegalArgumentException("priority must be between PRIORITY_KASHIDA and PRIORITY_NONE");
        }
    }

    private static void checkLimit(float limit) {
        if (limit < 0.0f) {
            throw new IllegalArgumentException("limit must be non-negative");
        }
    }
}
