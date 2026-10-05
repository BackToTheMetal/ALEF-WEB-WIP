package java.awt;

import java.awt.event.AdjustmentListener;

public interface Adjustable {
    int HORIZONTAL = 0;
    int VERTICAL = 1;
    int NO_ORIENTATION = 2;
    int getOrientation();
    void setMinimum(int minimum);
    int getMinimum();
    void setMaximum(int maximum);
    int getMaximum();
    void setUnitIncrement(int increment);
    int getUnitIncrement();
    void setBlockIncrement(int increment);
    int getBlockIncrement();
    void setVisibleAmount(int amount);
    int getVisibleAmount();
    void setValue(int value);
    int getValue();
    void addAdjustmentListener(AdjustmentListener listener);
    void removeAdjustmentListener(AdjustmentListener listener);
}
