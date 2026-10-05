package java.awt;

import java.io.Serializable;

public class BorderLayout implements LayoutManager2, Serializable {
    public static final String NORTH = "North";
    public static final String SOUTH = "South";
    public static final String EAST = "East";
    public static final String WEST = "West";
    public static final String CENTER = "Center";
    public static final String BEFORE_FIRST_LINE = "First";
    public static final String AFTER_LAST_LINE = "Last";
    public static final String BEFORE_LINE_BEGINS = "Before";
    public static final String AFTER_LINE_ENDS = "After";
    public static final String PAGE_START = BEFORE_FIRST_LINE;
    public static final String PAGE_END = AFTER_LAST_LINE;
    public static final String LINE_START = BEFORE_LINE_BEGINS;
    public static final String LINE_END = AFTER_LINE_ENDS;
    private static final long serialVersionUID = 1L;
    private int horizontalGap;
    private int verticalGap;
    private Component north;
    private Component west;
    private Component east;
    private Component south;
    private Component center;
    private Component firstLine;
    private Component lastLine;
    private Component firstItem;
    private Component lastItem;

    public BorderLayout() {
        this(0, 0);
    }

    public BorderLayout(int horizontalGap, int verticalGap) {
        this.horizontalGap = horizontalGap;
        this.verticalGap = verticalGap;
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
    public void addLayoutComponent(Component component, Object constraints) {
        synchronized (component.getTreeLock()) {
            if (constraints != null && !(constraints instanceof String)) {
                throw new IllegalArgumentException("cannot add to layout: constraint must be a string (or null)");
            }
            addLayoutComponent((String) constraints, component);
        }
    }

    @Deprecated
    @Override
    public void addLayoutComponent(String name, Component component) {
        synchronized (component.getTreeLock()) {
            String constraint = name == null ? CENTER : name;
            if (CENTER.equals(constraint)) {
                center = component;
            } else if (NORTH.equals(constraint)) {
                north = component;
            } else if (SOUTH.equals(constraint)) {
                south = component;
            } else if (EAST.equals(constraint)) {
                east = component;
            } else if (WEST.equals(constraint)) {
                west = component;
            } else if (BEFORE_FIRST_LINE.equals(constraint)) {
                firstLine = component;
            } else if (AFTER_LAST_LINE.equals(constraint)) {
                lastLine = component;
            } else if (BEFORE_LINE_BEGINS.equals(constraint)) {
                firstItem = component;
            } else if (AFTER_LINE_ENDS.equals(constraint)) {
                lastItem = component;
            } else {
                throw new IllegalArgumentException("cannot add to layout: unknown constraint: " + constraint);
            }
        }
    }

    @Override
    public void removeLayoutComponent(Component component) {
        synchronized (component.getTreeLock()) {
            if (component == center) {
                center = null;
            } else if (component == north) {
                north = null;
            } else if (component == south) {
                south = null;
            } else if (component == east) {
                east = null;
            } else if (component == west) {
                west = null;
            }
            if (component == firstLine) {
                firstLine = null;
            } else if (component == lastLine) {
                lastLine = null;
            } else if (component == firstItem) {
                firstItem = null;
            } else if (component == lastItem) {
                lastItem = null;
            }
        }
    }

    public Component getLayoutComponent(Object constraints) {
        if (CENTER.equals(constraints)) {
            return center;
        }
        if (NORTH.equals(constraints)) {
            return north;
        }
        if (SOUTH.equals(constraints)) {
            return south;
        }
        if (WEST.equals(constraints)) {
            return west;
        }
        if (EAST.equals(constraints)) {
            return east;
        }
        if (PAGE_START.equals(constraints)) {
            return firstLine;
        }
        if (PAGE_END.equals(constraints)) {
            return lastLine;
        }
        if (LINE_START.equals(constraints)) {
            return firstItem;
        }
        if (LINE_END.equals(constraints)) {
            return lastItem;
        }
        throw new IllegalArgumentException("cannot get component: unknown constraint: " + constraints);
    }

    public Component getLayoutComponent(Container target, Object constraints) {
        boolean leftToRight = target.getComponentOrientation().isLeftToRight();
        if (NORTH.equals(constraints)) {
            return firstLine == null ? north : firstLine;
        }
        if (SOUTH.equals(constraints)) {
            return lastLine == null ? south : lastLine;
        }
        if (WEST.equals(constraints)) {
            Component relative = leftToRight ? firstItem : lastItem;
            return relative == null ? west : relative;
        }
        if (EAST.equals(constraints)) {
            Component relative = leftToRight ? lastItem : firstItem;
            return relative == null ? east : relative;
        }
        if (CENTER.equals(constraints)) {
            return center;
        }
        throw new IllegalArgumentException("cannot get component: invalid constraint: " + constraints);
    }

    public Object getConstraints(Component component) {
        if (component == null) {
            return null;
        }
        if (component == center) {
            return CENTER;
        }
        if (component == north) {
            return NORTH;
        }
        if (component == south) {
            return SOUTH;
        }
        if (component == west) {
            return WEST;
        }
        if (component == east) {
            return EAST;
        }
        if (component == firstLine) {
            return PAGE_START;
        }
        if (component == lastLine) {
            return PAGE_END;
        }
        if (component == firstItem) {
            return LINE_START;
        }
        if (component == lastItem) {
            return LINE_END;
        }
        return null;
    }

    @Override
    public Dimension minimumLayoutSize(Container target) {
        synchronized (target.getTreeLock()) {
            return calculateLayoutSize(target, false);
        }
    }

    @Override
    public Dimension preferredLayoutSize(Container target) {
        synchronized (target.getTreeLock()) {
            return calculateLayoutSize(target, true);
        }
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
        // Border layout does not cache measurements.
    }

    @Override
    public void layoutContainer(Container target) {
        synchronized (target.getTreeLock()) {
            Insets insets = target.getInsets();
            int top = insets.top;
            int bottom = target.getHeight() - insets.bottom;
            int left = insets.left;
            int right = target.getWidth() - insets.right;
            boolean leftToRight = target.getComponentOrientation().isLeftToRight();
            Component component = getVisibleChild(NORTH, leftToRight);
            if (component != null) {
                component.setSize(right - left, component.getHeight());
                Dimension size = component.getPreferredSize();
                component.setBounds(left, top, right - left, size.height);
                top += size.height + verticalGap;
            }
            component = getVisibleChild(SOUTH, leftToRight);
            if (component != null) {
                component.setSize(right - left, component.getHeight());
                Dimension size = component.getPreferredSize();
                component.setBounds(left, bottom - size.height, right - left, size.height);
                bottom -= size.height + verticalGap;
            }
            component = getVisibleChild(EAST, leftToRight);
            if (component != null) {
                component.setSize(component.getWidth(), bottom - top);
                Dimension size = component.getPreferredSize();
                component.setBounds(right - size.width, top, size.width, bottom - top);
                right -= size.width + horizontalGap;
            }
            component = getVisibleChild(WEST, leftToRight);
            if (component != null) {
                component.setSize(component.getWidth(), bottom - top);
                Dimension size = component.getPreferredSize();
                component.setBounds(left, top, size.width, bottom - top);
                left += size.width + horizontalGap;
            }
            component = getVisibleChild(CENTER, leftToRight);
            if (component != null) {
                component.setBounds(left, top, right - left, bottom - top);
            }
        }
    }

    @Override
    public String toString() {
        return getClass().getName() + "[hgap=" + horizontalGap + ",vgap=" + verticalGap + "]";
    }

    private Dimension calculateLayoutSize(Container target, boolean preferred) {
        Dimension result = new Dimension();
        boolean leftToRight = target.getComponentOrientation().isLeftToRight();
        Component component = getVisibleChild(EAST, leftToRight);
        if (component != null) {
            Dimension size = preferred ? component.getPreferredSize() : component.getMinimumSize();
            result.width += size.width + horizontalGap;
            result.height = Math.max(result.height, size.height);
        }
        component = getVisibleChild(WEST, leftToRight);
        if (component != null) {
            Dimension size = preferred ? component.getPreferredSize() : component.getMinimumSize();
            result.width += size.width + horizontalGap;
            result.height = Math.max(result.height, size.height);
        }
        component = getVisibleChild(CENTER, leftToRight);
        if (component != null) {
            Dimension size = preferred ? component.getPreferredSize() : component.getMinimumSize();
            result.width += size.width;
            result.height = Math.max(result.height, size.height);
        }
        component = getVisibleChild(NORTH, leftToRight);
        if (component != null) {
            Dimension size = preferred ? component.getPreferredSize() : component.getMinimumSize();
            result.width = Math.max(result.width, size.width);
            result.height += size.height + verticalGap;
        }
        component = getVisibleChild(SOUTH, leftToRight);
        if (component != null) {
            Dimension size = preferred ? component.getPreferredSize() : component.getMinimumSize();
            result.width = Math.max(result.width, size.width);
            result.height += size.height + verticalGap;
        }
        Insets insets = target.getInsets();
        result.width += insets.left + insets.right;
        result.height += insets.top + insets.bottom;
        return result;
    }

    private Component getVisibleChild(String constraint, boolean leftToRight) {
        Component result = null;
        if (constraint == NORTH) {
            result = firstLine == null ? north : firstLine;
        } else if (constraint == SOUTH) {
            result = lastLine == null ? south : lastLine;
        } else if (constraint == WEST) {
            result = leftToRight ? firstItem : lastItem;
            if (result == null) {
                result = west;
            }
        } else if (constraint == EAST) {
            result = leftToRight ? lastItem : firstItem;
            if (result == null) {
                result = east;
            }
        } else if (constraint == CENTER) {
            result = center;
        }
        return result != null && result.isVisible() ? result : null;
    }
}
