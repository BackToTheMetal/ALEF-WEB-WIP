package java.awt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GridBagLayout implements LayoutManager2, Serializable {
    private static final long serialVersionUID = 8838754796412211005L;
    public int[] columnWidths;
    public int[] rowHeights;
    public double[] columnWeights;
    public double[] rowWeights;
    private final Map<Component, GridBagConstraints> constraintsByComponent = new HashMap<>();
    protected GridBagLayoutInfo layoutInfo;
    protected GridBagConstraints defaultConstraints = new GridBagConstraints();
    protected static final int MAXGRIDSIZE = 512;
    protected static final int MINSIZE = 1;
    protected static final int PREFERREDSIZE = 2;
    private boolean rightToLeft;

    public GridBagLayout() {
    }

    public void setConstraints(Component component, GridBagConstraints constraints) {
        if (component == null) {
            throw new NullPointerException("component");
        }
        if (constraints == null) {
            throw new NullPointerException("constraints");
        }
        constraintsByComponent.put(component, copy(constraints));
    }

    public GridBagConstraints getConstraints(Component component) {
        return copy(lookupConstraints(component));
    }

    public Point getLayoutOrigin() {
        return layoutInfo == null ? new Point() : new Point(layoutInfo.startx, layoutInfo.starty);
    }

    public int[][] getLayoutDimensions() {
        if (layoutInfo == null) {
            return new int[2][0];
        }
        return new int[][] {copy(layoutInfo.minWidth), copy(layoutInfo.minHeight)};
    }

    public double[][] getLayoutWeights() {
        if (layoutInfo == null) {
            return new double[2][0];
        }
        return new double[][] {copy(layoutInfo.weightX), copy(layoutInfo.weightY)};
    }

    public Point location(int x, int y) {
        if (layoutInfo == null) {
            return new Point();
        }
        int column = findColumn(x);
        int row = findCell(y, layoutInfo.starty, layoutInfo.minHeight);
        return new Point(column, row);
    }

    @Override
    public void addLayoutComponent(String name, Component component) {
    }

    @Override
    public void addLayoutComponent(Component component, Object constraints) {
        if (constraints == null) {
            setConstraints(component, new GridBagConstraints());
        } else if (constraints instanceof GridBagConstraints) {
            setConstraints(component, (GridBagConstraints) constraints);
        } else {
            throw new IllegalArgumentException("constraints must be a GridBagConstraint");
        }
    }

    @Override
    public void removeLayoutComponent(Component component) {
        constraintsByComponent.remove(component);
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        return layoutSize(parent, true);
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        return layoutSize(parent, false);
    }

    @Override
    public Dimension maximumLayoutSize(Container target) {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    @Override
    public float getLayoutAlignmentX(Container target) {
        return 0.5f;
    }

    @Override
    public float getLayoutAlignmentY(Container target) {
        return 0.5f;
    }

    @Override
    public void invalidateLayout(Container target) {
    }

    @Override
    public void layoutContainer(Container parent) { arrangeGrid(parent); }

    protected void arrangeGrid(Container parent) { ArrangeGrid(parent); }

    protected void ArrangeGrid(Container parent) {
        synchronized (parent.getTreeLock()) {
            rightToLeft = !parent.getComponentOrientation().isLeftToRight();
            LayoutGrid original = createGrid(parent, true);
            GridBagLayoutInfo info = getLayoutInfo(parent, PREFERREDSIZE);
            LayoutGrid grid = new LayoutGrid(info.width, info.height, original.placements);
            copyInitialValues(info.minWidth, grid.widths);
            copyInitialValues(info.minHeight, grid.heights);
            copyInitialValues(info.weightX, grid.weightX);
            copyInitialValues(info.weightY, grid.weightY);
            Insets parentInsets = parent.getInsets();
            int availableWidth = Math.max(0, parent.getWidth() - parentInsets.left - parentInsets.right);
            int availableHeight = Math.max(0, parent.getHeight() - parentInsets.top - parentInsets.bottom);
            int contentWidth = sum(grid.widths);
            int contentHeight = sum(grid.heights);
            int extraWidth = availableWidth - contentWidth;
            int extraHeight = availableHeight - contentHeight;
            int startX = parentInsets.left;
            int startY = parentInsets.top;
            if (extraWidth > 0 && sum(grid.weightX) > 0.0) {
                distributeExtra(grid.widths, grid.weightX, extraWidth);
            } else {
                startX += extraWidth / 2;
            }
            if (extraHeight > 0 && sum(grid.weightY) > 0.0) {
                distributeExtra(grid.heights, grid.weightY, extraHeight);
            } else {
                startY += extraHeight / 2;
            }
            layoutInfo = grid.toLayoutInfo(startX, startY);
            for (Placement placement : grid.placements) {
                setComponentBounds(placement, grid, startX, startY);
            }
        }
    }

    @Override
    public String toString() {
        return getClass().getName();
    }

    private Dimension layoutSize(Container parent, boolean preferred) {
        synchronized (parent.getTreeLock()) {
            return getMinSize(parent, getLayoutInfo(parent, preferred ? PREFERREDSIZE : MINSIZE));
        }
    }

    private LayoutGrid createGrid(Container parent, boolean preferred) {
        List<Placement> placements = resolvePlacements(parent);
        int columns = columnWidths == null ? 0 : columnWidths.length;
        int rows = rowHeights == null ? 0 : rowHeights.length;
        for (Placement placement : placements) {
            columns = Math.max(columns, placement.x + placement.initialWidth);
            rows = Math.max(rows, placement.y + placement.initialHeight);
        }
        for (Placement placement : placements) {
            placement.width = resolveSpan(placement.constraints.gridwidth, placement.x, columns);
            placement.height = resolveSpan(placement.constraints.gridheight, placement.y, rows);
            columns = Math.max(columns, placement.x + placement.width);
            rows = Math.max(rows, placement.y + placement.height);
        }
        LayoutGrid grid = new LayoutGrid(columns, rows, placements);
        copyInitialValues(columnWidths, grid.widths);
        copyInitialValues(rowHeights, grid.heights);
        copyInitialValues(columnWeights, grid.weightX);
        copyInitialValues(rowWeights, grid.weightY);
        applyComponentRequirements(grid, preferred);
        return grid;
    }

    private List<Placement> resolvePlacements(Container parent) {
        List<Placement> placements = new ArrayList<>();
        int cursorX = 0;
        int cursorY = 0;
        for (Component component : parent.getComponents()) {
            if (!component.isVisible()) {
                continue;
            }
            GridBagConstraints constraints = getConstraints(component);
            int x = constraints.gridx == GridBagConstraints.RELATIVE ? cursorX : constraints.gridx;
            int y = constraints.gridy == GridBagConstraints.RELATIVE ? cursorY : constraints.gridy;
            x = Math.max(0, x);
            y = Math.max(0, y);
            int width = constraints.gridwidth > 0 ? constraints.gridwidth : 1;
            int height = constraints.gridheight > 0 ? constraints.gridheight : 1;
            placements.add(new Placement(component, constraints, x, y, width, height));
            if (constraints.gridwidth == GridBagConstraints.REMAINDER) {
                cursorX = 0;
                cursorY = y + height;
            } else {
                cursorX = x + width;
                cursorY = y;
            }
        }
        return placements;
    }

    private void applyComponentRequirements(LayoutGrid grid, boolean preferred) {
        for (int span = 1; span <= Math.max(grid.widths.length, grid.heights.length); span++) {
            for (Placement placement : grid.placements) {
                Dimension size = preferred ? placement.component.getPreferredSize() : placement.component.getMinimumSize();
                placement.constraints.minWidth = size.width;
                placement.constraints.minHeight = size.height;
                GridBagConstraints stored = lookupConstraints(placement.component);
                stored.minWidth = size.width;
                stored.minHeight = size.height;
                Insets insets = effectiveInsets(placement.constraints.insets);
                int requiredWidth = Math.max(0, size.width + placement.constraints.ipadx + insets.left + insets.right);
                int requiredHeight = Math.max(0, size.height + placement.constraints.ipady + insets.top + insets.bottom);
                if (placement.width == span) {
                    ensureSize(grid.widths, placement.x, placement.width, requiredWidth);
                    ensureWeight(grid.weightX, placement.x, placement.width, placement.constraints.weightx);
                }
                if (placement.height == span) {
                    ensureSize(grid.heights, placement.y, placement.height, requiredHeight);
                    ensureWeight(grid.weightY, placement.y, placement.height, placement.constraints.weighty);
                }
            }
        }
    }

    private void setComponentBounds(Placement placement, LayoutGrid grid, int startX, int startY) {
        int cellX = rightToLeft ? startX + sum(grid.widths) - sum(grid.widths, 0, placement.x + placement.width) : startX + sum(grid.widths, 0, placement.x);
        int cellY = startY + sum(grid.heights, 0, placement.y);
        int cellWidth = sum(grid.widths, placement.x, placement.width);
        int cellHeight = sum(grid.heights, placement.y, placement.height);
        Rectangle bounds = new Rectangle(cellX, cellY, cellWidth, cellHeight);
        adjustForGravity(placement.constraints, bounds);
        placement.component.setBounds(bounds);
    }

    private int findColumn(int x) {
        if (!rightToLeft) {
            return findCell(x, layoutInfo.startx, layoutInfo.minWidth);
        }
        int position = layoutInfo.startx;
        for (int index = layoutInfo.width - 1; index >= 0; index--) {
            if (position > x) {
                return index + 1;
            }
            position += layoutInfo.minWidth[index];
        }
        return 0;
    }

    private static int findCell(int coordinate, int start, int[] sizes) {
        int position = start;
        for (int index = 0; index < sizes.length; index++) {
            position += sizes[index];
            if (position > coordinate) {
                return index;
            }
        }
        return sizes.length;
    }

    private static int resolveSpan(int configuredSpan, int position, int gridSize) {
        if (configuredSpan == GridBagConstraints.REMAINDER) {
            return Math.max(1, gridSize - position);
        }
        if (configuredSpan == GridBagConstraints.RELATIVE) {
            return Math.max(1, gridSize - position - 1);
        }
        return Math.max(1, configuredSpan);
    }

    private static void ensureSize(int[] sizes, int start, int length, int required) {
        int current = sum(sizes, start, length);
        if (required > current && length > 0) {
            sizes[start + length - 1] += required - current;
        }
    }

    private static void ensureWeight(double[] weights, int start, int length, double required) {
        double current = sum(weights, start, length);
        if (required > current && length > 0) {
            weights[start + length - 1] += required - current;
        }
    }

    private static void distributeExtra(int[] sizes, double[] weights, int extra) {
        double totalWeight = sum(weights);
        int distributed = 0;
        for (int index = 0; index < sizes.length; index++) {
            int addition = index == sizes.length - 1 ? extra - distributed : (int) (extra * weights[index] / totalWeight);
            sizes[index] += addition;
            distributed += addition;
        }
    }

    private static Point anchorOffset(int anchor, int extraWidth, int extraHeight, boolean rightToLeft) {
        int horizontal = 0;
        int vertical = 0;
        int resolvedAnchor = resolveRelativeAnchor(anchor, rightToLeft);
        if (resolvedAnchor == GridBagConstraints.CENTER || resolvedAnchor == GridBagConstraints.NORTH || resolvedAnchor == GridBagConstraints.SOUTH || isBaselineCenter(resolvedAnchor)) {
            horizontal = extraWidth / 2;
        } else if (resolvedAnchor == GridBagConstraints.NORTHEAST || resolvedAnchor == GridBagConstraints.EAST || resolvedAnchor == GridBagConstraints.SOUTHEAST || isBaselineTrailing(resolvedAnchor)) {
            horizontal = extraWidth;
        }
        if (resolvedAnchor == GridBagConstraints.CENTER || resolvedAnchor == GridBagConstraints.EAST || resolvedAnchor == GridBagConstraints.WEST || isBaselineAnchor(resolvedAnchor)) {
            vertical = extraHeight / 2;
        } else if (resolvedAnchor == GridBagConstraints.SOUTH || resolvedAnchor == GridBagConstraints.SOUTHEAST || resolvedAnchor == GridBagConstraints.SOUTHWEST || isBelowBaseline(resolvedAnchor)) {
            vertical = extraHeight;
        }
        return new Point(horizontal, vertical);
    }

    private static int resolveRelativeAnchor(int anchor, boolean rightToLeft) {
        switch (anchor) {
            case GridBagConstraints.PAGE_START:
                return GridBagConstraints.NORTH;
            case GridBagConstraints.PAGE_END:
                return GridBagConstraints.SOUTH;
            case GridBagConstraints.LINE_START:
                return rightToLeft ? GridBagConstraints.EAST : GridBagConstraints.WEST;
            case GridBagConstraints.LINE_END:
                return rightToLeft ? GridBagConstraints.WEST : GridBagConstraints.EAST;
            case GridBagConstraints.FIRST_LINE_START:
                return rightToLeft ? GridBagConstraints.NORTHEAST : GridBagConstraints.NORTHWEST;
            case GridBagConstraints.FIRST_LINE_END:
                return rightToLeft ? GridBagConstraints.NORTHWEST : GridBagConstraints.NORTHEAST;
            case GridBagConstraints.LAST_LINE_START:
                return rightToLeft ? GridBagConstraints.SOUTHEAST : GridBagConstraints.SOUTHWEST;
            case GridBagConstraints.LAST_LINE_END:
                return rightToLeft ? GridBagConstraints.SOUTHWEST : GridBagConstraints.SOUTHEAST;
            default:
                return anchor;
        }
    }

    private static boolean isBaselineAnchor(int anchor) {
        return anchor >= GridBagConstraints.BASELINE && anchor <= GridBagConstraints.BASELINE_TRAILING;
    }

    private static boolean isBaselineCenter(int anchor) {
        return anchor == GridBagConstraints.BASELINE || anchor == GridBagConstraints.ABOVE_BASELINE || anchor == GridBagConstraints.BELOW_BASELINE;
    }

    private static boolean isBaselineTrailing(int anchor) {
        return anchor == GridBagConstraints.BASELINE_TRAILING || anchor == GridBagConstraints.ABOVE_BASELINE_TRAILING || anchor == GridBagConstraints.BELOW_BASELINE_TRAILING;
    }

    private static boolean isBelowBaseline(int anchor) {
        return anchor >= GridBagConstraints.BELOW_BASELINE && anchor <= GridBagConstraints.BELOW_BASELINE_TRAILING;
    }

    private static Insets effectiveInsets(Insets insets) {
        return insets == null ? new Insets(0, 0, 0, 0) : insets;
    }

    private static GridBagConstraints copy(GridBagConstraints constraints) {
        return (GridBagConstraints) constraints.clone();
    }

    private static int[] copy(int[] values) {
        int[] copy = new int[values.length];
        System.arraycopy(values, 0, copy, 0, values.length);
        return copy;
    }

    private static double[] copy(double[] values) {
        double[] copy = new double[values.length];
        System.arraycopy(values, 0, copy, 0, values.length);
        return copy;
    }

    private static void copyInitialValues(int[] source, int[] destination) {
        if (source != null) {
            System.arraycopy(source, 0, destination, 0, Math.min(source.length, destination.length));
        }
    }

    private static void copyInitialValues(double[] source, double[] destination) {
        if (source != null) {
            System.arraycopy(source, 0, destination, 0, Math.min(source.length, destination.length));
        }
    }

    private static int sum(int[] values) {
        return sum(values, 0, values.length);
    }

    private static int sum(int[] values, int start, int length) {
        int result = 0;
        for (int index = start; index < start + length; index++) {
            result += values[index];
        }
        return result;
    }

    private static double sum(double[] values) {
        return sum(values, 0, values.length);
    }

    private static double sum(double[] values, int start, int length) {
        double result = 0.0;
        for (int index = start; index < start + length; index++) {
            result += values[index];
        }
        return result;
    }

    private static final class Placement {
        private final Component component;
        private final GridBagConstraints constraints;
        private final int initialHeight;
        private final int initialWidth;
        private final int x;
        private final int y;
        private int height;
        private int width;

        private Placement(Component component, GridBagConstraints constraints, int x, int y, int width, int height) {
            this.component = component;
            this.constraints = constraints;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            initialWidth = width;
            initialHeight = height;
        }
    }

    private static final class LayoutGrid {
        private final List<Placement> placements;
        private final double[] weightX;
        private final double[] weightY;
        private final int[] heights;
        private final int[] widths;

        private LayoutGrid(int columns, int rows, List<Placement> placements) {
            widths = new int[columns];
            heights = new int[rows];
            weightX = new double[columns];
            weightY = new double[rows];
            this.placements = placements;
        }

        private GridBagLayoutInfo toLayoutInfo(int startX, int startY) {
            GridBagLayoutInfo info = new GridBagLayoutInfo(widths.length, heights.length);
            info.startx = startX;
            info.starty = startY;
            info.minWidth = copy(widths);
            info.minHeight = copy(heights);
            info.weightX = copy(weightX);
            info.weightY = copy(weightY);
            return info;
        }
    }
    protected GridBagConstraints lookupConstraints(Component component) {
        GridBagConstraints constraints = constraintsByComponent.get(component);
        if (constraints == null) { constraints = copy(defaultConstraints); constraintsByComponent.put(component, constraints); }
        return constraints;
    }
    protected GridBagLayoutInfo getLayoutInfo(Container parent, int sizeFlag) { return GetLayoutInfo(parent, sizeFlag); }
    protected GridBagLayoutInfo GetLayoutInfo(Container parent, int sizeFlag) { return createGrid(parent, sizeFlag == PREFERREDSIZE).toLayoutInfo(0, 0); }
    protected Dimension getMinSize(Container parent, GridBagLayoutInfo info) { return GetMinSize(parent, info); }
    protected Dimension GetMinSize(Container parent, GridBagLayoutInfo info) {
        Insets insets = parent.getInsets();
        return new Dimension(sum(info.minWidth) + insets.left + insets.right, sum(info.minHeight) + insets.top + insets.bottom);
    }
    protected void adjustForGravity(GridBagConstraints constraints, Rectangle bounds) { AdjustForGravity(constraints, bounds); }
    protected void AdjustForGravity(GridBagConstraints constraints, Rectangle bounds) {
        Insets insets = effectiveInsets(constraints.insets);
        int areaWidth = Math.max(0, bounds.width - insets.left - insets.right);
        int areaHeight = Math.max(0, bounds.height - insets.top - insets.bottom);
        int width = Math.min(areaWidth, Math.max(0, constraints.minWidth + constraints.ipadx));
        int height = Math.min(areaHeight, Math.max(0, constraints.minHeight + constraints.ipady));
        if (constraints.fill == GridBagConstraints.BOTH || constraints.fill == GridBagConstraints.HORIZONTAL) { width = areaWidth; }
        if (constraints.fill == GridBagConstraints.BOTH || constraints.fill == GridBagConstraints.VERTICAL) { height = areaHeight; }
        Point offset = anchorOffset(constraints.anchor, areaWidth - width, areaHeight - height, rightToLeft);
        bounds.setBounds(bounds.x + insets.left + offset.x, bounds.y + insets.top + offset.y, width, height);
    }
}
