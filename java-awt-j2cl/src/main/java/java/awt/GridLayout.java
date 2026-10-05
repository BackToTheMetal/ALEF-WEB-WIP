package java.awt;

import java.io.Serializable;

public class GridLayout implements LayoutManager, Serializable {
    private static final long serialVersionUID = 1L;
    private int rows;
    private int columns;
    private int horizontalGap;
    private int verticalGap;

    public GridLayout() {
        this(1, 0, 0, 0);
    }

    public GridLayout(int rows, int columns) {
        this(rows, columns, 0, 0);
    }

    public GridLayout(int rows, int columns, int horizontalGap, int verticalGap) {
        if (rows == 0 && columns == 0) {
            throw new IllegalArgumentException("rows and cols cannot both be zero");
        }
        this.rows = rows;
        this.columns = columns;
        this.horizontalGap = horizontalGap;
        this.verticalGap = verticalGap;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if (rows == 0 && columns == 0) {
            throw new IllegalArgumentException("rows and cols cannot both be zero");
        }
        this.rows = rows;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        if (columns == 0 && rows == 0) {
            throw new IllegalArgumentException("rows and cols cannot both be zero");
        }
        this.columns = columns;
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

    @Override
    public void addLayoutComponent(String name, Component component) {
        // Grid layout derives all placement from container order.
    }

    @Override
    public void removeLayoutComponent(Component component) {
        // Grid layout retains no component-specific state.
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        synchronized (parent.getTreeLock()) {
            return calculateLayoutSize(parent, true);
        }
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        synchronized (parent.getTreeLock()) {
            return calculateLayoutSize(parent, false);
        }
    }

    @Override
    public void layoutContainer(Container parent) {
        synchronized (parent.getTreeLock()) {
            int componentCount = parent.getComponentCount();
            if (componentCount == 0) {
                return;
            }
            int actualRows = rows;
            int actualColumns = columns;
            if (actualRows > 0) {
                actualColumns = divideRoundingUp(componentCount, actualRows);
            } else {
                actualRows = divideRoundingUp(componentCount, actualColumns);
            }
            Insets insets = parent.getInsets();
            int availableWidth = parent.getWidth() - insets.left - insets.right;
            int availableHeight = parent.getHeight() - insets.top - insets.bottom;
            int totalHorizontalGap = (actualColumns - 1) * horizontalGap;
            int totalVerticalGap = (actualRows - 1) * verticalGap;
            int componentWidth = (availableWidth - totalHorizontalGap) / actualColumns;
            int componentHeight = (availableHeight - totalVerticalGap) / actualRows;
            int horizontalRemainder = (availableWidth - componentWidth * actualColumns - totalHorizontalGap) / 2;
            int verticalRemainder = (availableHeight - componentHeight * actualRows - totalVerticalGap) / 2;
            boolean leftToRight = parent.getComponentOrientation().isLeftToRight();
            int initialX = leftToRight ? insets.left + horizontalRemainder : parent.getWidth() - insets.right - componentWidth - horizontalRemainder;
            int xStep = leftToRight ? componentWidth + horizontalGap : -componentWidth - horizontalGap;
            for (int column = 0, x = initialX; column < actualColumns; column++, x += xStep) {
                for (int row = 0, y = insets.top + verticalRemainder; row < actualRows; row++, y += componentHeight + verticalGap) {
                    int componentIndex = row * actualColumns + column;
                    if (componentIndex < componentCount) {
                        parent.getComponent(componentIndex).setBounds(x, y, componentWidth, componentHeight);
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        return getClass().getName() + "[hgap=" + horizontalGap + ",vgap=" + verticalGap + ",rows=" + rows + ",cols=" + columns + "]";
    }

    private Dimension calculateLayoutSize(Container parent, boolean preferred) {
        Insets insets = parent.getInsets();
        int componentCount = parent.getComponentCount();
        int actualRows = rows;
        int actualColumns = columns;
        if (actualRows > 0) {
            actualColumns = divideRoundingUp(componentCount, actualRows);
        } else {
            actualRows = divideRoundingUp(componentCount, actualColumns);
        }
        int componentWidth = 0;
        int componentHeight = 0;
        for (int index = 0; index < componentCount; index++) {
            Dimension size = preferred ? parent.getComponent(index).getPreferredSize() : parent.getComponent(index).getMinimumSize();
            componentWidth = Math.max(componentWidth, size.width);
            componentHeight = Math.max(componentHeight, size.height);
        }
        int width = insets.left + insets.right + actualColumns * componentWidth + (actualColumns - 1) * horizontalGap;
        int height = insets.top + insets.bottom + actualRows * componentHeight + (actualRows - 1) * verticalGap;
        return new Dimension(width, height);
    }

    private static int divideRoundingUp(int dividend, int divisor) {
        return (dividend + divisor - 1) / divisor;
    }
}
