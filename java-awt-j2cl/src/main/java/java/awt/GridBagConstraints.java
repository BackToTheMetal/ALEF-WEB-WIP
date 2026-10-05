package java.awt;

import java.io.Serializable;

public class GridBagConstraints implements Cloneable, Serializable {
    private static final long serialVersionUID = -1000070633030801713L;
    public static final int RELATIVE = -1;
    public static final int REMAINDER = 0;
    public static final int NONE = 0;
    public static final int BOTH = 1;
    public static final int HORIZONTAL = 2;
    public static final int VERTICAL = 3;
    public static final int CENTER = 10;
    public static final int NORTH = 11;
    public static final int NORTHEAST = 12;
    public static final int EAST = 13;
    public static final int SOUTHEAST = 14;
    public static final int SOUTH = 15;
    public static final int SOUTHWEST = 16;
    public static final int WEST = 17;
    public static final int NORTHWEST = 18;
    public static final int PAGE_START = 19;
    public static final int PAGE_END = 20;
    public static final int LINE_START = 21;
    public static final int LINE_END = 22;
    public static final int FIRST_LINE_START = 23;
    public static final int FIRST_LINE_END = 24;
    public static final int LAST_LINE_START = 25;
    public static final int LAST_LINE_END = 26;
    public static final int BASELINE = 256;
    public static final int BASELINE_LEADING = 512;
    public static final int BASELINE_TRAILING = 768;
    public static final int ABOVE_BASELINE = 1024;
    public static final int ABOVE_BASELINE_LEADING = 1280;
    public static final int ABOVE_BASELINE_TRAILING = 1536;
    public static final int BELOW_BASELINE = 1792;
    public static final int BELOW_BASELINE_LEADING = 2048;
    public static final int BELOW_BASELINE_TRAILING = 2304;
    int minWidth;
    int minHeight;
    public int gridx;
    public int gridy;
    public int gridwidth;
    public int gridheight;
    public double weightx;
    public double weighty;
    public int anchor;
    public int fill;
    public Insets insets;
    public int ipadx;
    public int ipady;

    public GridBagConstraints() {
        this(RELATIVE, RELATIVE, 1, 1, 0.0, 0.0, CENTER, NONE, new Insets(0, 0, 0, 0), 0, 0);
    }

    public GridBagConstraints(int gridx, int gridy, int gridwidth, int gridheight, double weightx, double weighty, int anchor, int fill, Insets insets, int ipadx, int ipady) {
        this.gridx = gridx;
        this.gridy = gridy;
        this.gridwidth = gridwidth;
        this.gridheight = gridheight;
        this.weightx = weightx;
        this.weighty = weighty;
        this.anchor = anchor;
        this.fill = fill;
        this.insets = insets;
        this.ipadx = ipadx;
        this.ipady = ipady;
    }

    public Object clone() {
        GridBagConstraints copy = new GridBagConstraints(gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets == null ? null : (Insets) insets.clone(), ipadx, ipady);
        return copy;
    }
}
