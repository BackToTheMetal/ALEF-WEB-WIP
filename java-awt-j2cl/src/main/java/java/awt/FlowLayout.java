package java.awt;

import java.io.Serializable;

public class FlowLayout implements LayoutManager, Serializable {
    public static final int LEFT = 0;
    public static final int CENTER = 1;
    public static final int RIGHT = 2;
    public static final int LEADING = 3;
    public static final int TRAILING = 4;
    private static final long serialVersionUID = 1L;
    private int alignment;
    private int legacyAlignment;
    private int horizontalGap;
    private int verticalGap;
    private boolean alignOnBaseline;

    public FlowLayout() {
        this(CENTER, 5, 5);
    }

    public FlowLayout(int alignment) {
        this(alignment, 5, 5);
    }

    public FlowLayout(int alignment, int horizontalGap, int verticalGap) {
        this.horizontalGap = horizontalGap;
        this.verticalGap = verticalGap;
        setAlignment(alignment);
    }

    public int getAlignment() {
        return alignment;
    }

    public void setAlignment(int alignment) {
        this.alignment = alignment;
        if (alignment == LEADING) {
            legacyAlignment = LEFT;
        } else if (alignment == TRAILING) {
            legacyAlignment = RIGHT;
        } else {
            legacyAlignment = alignment;
        }
    }

    public int getHgap() {
        return horizontalGap;
    }

    public void setHgap(int horizontalGap) {
        this.horizontalGap = horizontalGap;
    }

    public int getVgap() {
        return verticalGap;
    }

    public void setVgap(int verticalGap) {
        this.verticalGap = verticalGap;
    }

    public void setAlignOnBaseline(boolean alignOnBaseline) {
        this.alignOnBaseline = alignOnBaseline;
    }

    public boolean getAlignOnBaseline() {
        return alignOnBaseline;
    }

    @Override
    public void addLayoutComponent(String name, Component component) {
        // Flow layout derives all placement from container order.
    }

    @Override
    public void removeLayoutComponent(Component component) {
        // Flow layout retains no component-specific state.
    }

    @Override
    public Dimension preferredLayoutSize(Container target) {
        synchronized (target.getTreeLock()) {
            return calculateLayoutSize(target, true);
        }
    }

    @Override
    public Dimension minimumLayoutSize(Container target) {
        synchronized (target.getTreeLock()) {
            return calculateLayoutSize(target, false);
        }
    }

    @Override
    public void layoutContainer(Container target) {
        synchronized (target.getTreeLock()) {
            Insets insets = target.getInsets();
            int availableWidth = target.getWidth() - insets.left - insets.right - horizontalGap * 2;
            int componentCount = target.getComponentCount();
            int x = 0;
            int y = insets.top + verticalGap;
            int rowHeight = 0;
            int rowStart = 0;
            boolean leftToRight = target.getComponentOrientation().isLeftToRight();
            int[] ascents = alignOnBaseline ? new int[componentCount] : null;
            int[] descents = alignOnBaseline ? new int[componentCount] : null;
            for (int index = 0; index < componentCount; index++) {
                Component component = target.getComponent(index);
                if (!component.isVisible()) {
                    continue;
                }
                Dimension size = component.getPreferredSize();
                component.setSize(size.width, size.height);
                if (alignOnBaseline) {
                    int baseline = component.getBaseline(size.width, size.height);
                    ascents[index] = baseline;
                    if (baseline >= 0) {
                        descents[index] = size.height - baseline;
                    }
                }
                if (x == 0 || x + size.width <= availableWidth) {
                    if (x > 0) {
                        x += horizontalGap;
                    }
                    x += size.width;
                    rowHeight = Math.max(rowHeight, size.height);
                } else {
                    rowHeight = positionRow(target, insets.left + horizontalGap, y, availableWidth - x, rowHeight, rowStart, index, leftToRight, ascents, descents);
                    x = size.width;
                    y += verticalGap + rowHeight;
                    rowHeight = size.height;
                    rowStart = index;
                }
            }
            positionRow(target, insets.left + horizontalGap, y, availableWidth - x, rowHeight, rowStart, componentCount, leftToRight, ascents, descents);
        }
    }

    @Override
    public String toString() {
        String alignmentText = "";
        if (legacyAlignment == LEFT) {
            alignmentText = ",align=left";
        } else if (legacyAlignment == CENTER) {
            alignmentText = ",align=center";
        } else if (legacyAlignment == RIGHT) {
            alignmentText = ",align=right";
        } else if (legacyAlignment == LEADING) {
            alignmentText = ",align=leading";
        } else if (legacyAlignment == TRAILING) {
            alignmentText = ",align=trailing";
        }
        return getClass().getName() + "[hgap=" + horizontalGap + ",vgap=" + verticalGap + alignmentText + "]";
    }

    private Dimension calculateLayoutSize(Container target, boolean preferred) {
        int width = 0;
        int height = 0;
        int maximumAscent = 0;
        int maximumDescent = 0;
        boolean firstVisible = true;
        for (int index = 0; index < target.getComponentCount(); index++) {
            Component component = target.getComponent(index);
            if (!component.isVisible()) {
                continue;
            }
            Dimension size = preferred ? component.getPreferredSize() : component.getMinimumSize();
            if (!firstVisible) {
                width += horizontalGap;
            }
            firstVisible = false;
            width += size.width;
            height = Math.max(height, size.height);
            if (alignOnBaseline) {
                int baseline = component.getBaseline(size.width, size.height);
                if (baseline >= 0) {
                    maximumAscent = Math.max(maximumAscent, baseline);
                    maximumDescent = Math.max(maximumDescent, size.height - baseline);
                }
            }
        }
        if (alignOnBaseline) {
            height = Math.max(height, maximumAscent + maximumDescent);
        }
        Insets insets = target.getInsets();
        return new Dimension(width + insets.left + insets.right + horizontalGap * 2, height + insets.top + insets.bottom + verticalGap * 2);
    }

    private int positionRow(Container target, int x, int y, int remainingWidth, int rowHeight, int rowStart, int rowEnd, boolean leftToRight, int[] ascents, int[] descents) {
        if (alignment == LEFT) {
            x += leftToRight ? 0 : remainingWidth;
        } else if (alignment == CENTER) {
            x += remainingWidth / 2;
        } else if (alignment == RIGHT) {
            x += leftToRight ? remainingWidth : 0;
        } else if (alignment == TRAILING) {
            x += remainingWidth;
        }
        int maximumAscent = 0;
        int maximumDescent = 0;
        int nonBaselineHeight = 0;
        int baselineOffset = 0;
        if (alignOnBaseline) {
            for (int index = rowStart; index < rowEnd; index++) {
                Component component = target.getComponent(index);
                if (!component.isVisible()) {
                    continue;
                }
                if (ascents[index] >= 0) {
                    maximumAscent = Math.max(maximumAscent, ascents[index]);
                    maximumDescent = Math.max(maximumDescent, descents[index]);
                } else {
                    nonBaselineHeight = Math.max(nonBaselineHeight, component.getHeight());
                }
            }
            rowHeight = Math.max(maximumAscent + maximumDescent, nonBaselineHeight);
            baselineOffset = (rowHeight - maximumAscent - maximumDescent) / 2;
        }
        for (int index = rowStart; index < rowEnd; index++) {
            Component component = target.getComponent(index);
            if (!component.isVisible()) {
                continue;
            }
            int componentY = alignOnBaseline && ascents[index] >= 0 ? y + baselineOffset + maximumAscent - ascents[index] : y + (rowHeight - component.getHeight()) / 2;
            int componentX = leftToRight ? x : target.getWidth() - x - component.getWidth();
            component.setLocation(componentX, componentY);
            x += component.getWidth() + horizontalGap;
        }
        return rowHeight;
    }
}
