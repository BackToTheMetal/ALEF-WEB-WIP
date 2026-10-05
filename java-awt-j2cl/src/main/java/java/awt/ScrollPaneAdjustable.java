package java.awt;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ScrollPaneAdjustable implements Adjustable, Serializable {
    private static final long serialVersionUID = -3359745691033257079L;
    private static final String SCROLLPANE_ONLY = "Can be set by scrollpane only";
    private final List<AdjustmentListener> adjustmentListeners = new ArrayList<>();
    private final int orientation;
    private final ScrollPane scrollPane;
    private int blockIncrement = 1;
    private int maximum;
    private int minimum;
    private int unitIncrement = 1;
    private int value;
    private int visibleAmount;
    private boolean adjusting;

    ScrollPaneAdjustable(ScrollPane scrollPane, AdjustmentListener listener, int orientation) {
        this.scrollPane = scrollPane;
        this.orientation = orientation;
        addAdjustmentListener(listener);
    }

    void setSpan(int minimum, int maximum, int visibleAmount) {
        this.minimum = minimum;
        this.maximum = Math.max(maximum, minimum + 1);
        this.visibleAmount = Math.max(1, Math.min(visibleAmount, this.maximum - minimum));
        blockIncrement = Math.max((int) (visibleAmount * 0.9), 1);
        setValue(value);
    }

    @Override
    public int getOrientation() {
        return orientation;
    }

    @Override
    public void setMinimum(int minimum) {
        throw new AWTError(SCROLLPANE_ONLY);
    }

    @Override
    public int getMinimum() {
        return 0;
    }

    @Override
    public void setMaximum(int maximum) {
        throw new AWTError(SCROLLPANE_ONLY);
    }

    @Override
    public int getMaximum() {
        return maximum;
    }

    @Override
    public synchronized void setUnitIncrement(int unitIncrement) {
        if (this.unitIncrement != unitIncrement) {
            this.unitIncrement = unitIncrement;
            scrollPane.synchronizeScrollPeer();
        }
    }

    @Override
    public int getUnitIncrement() {
        return unitIncrement;
    }

    @Override
    public synchronized void setBlockIncrement(int blockIncrement) {
        this.blockIncrement = blockIncrement;
    }

    @Override
    public int getBlockIncrement() {
        return blockIncrement;
    }

    @Override
    public void setVisibleAmount(int visibleAmount) {
        throw new AWTError(SCROLLPANE_ONLY);
    }

    @Override
    public int getVisibleAmount() {
        return visibleAmount;
    }

    public void setValueIsAdjusting(boolean adjusting) {
        if (this.adjusting != adjusting) {
            this.adjusting = adjusting;
            fireAdjustmentEvent(AdjustmentEvent.TRACK);
        }
    }

    public boolean getValueIsAdjusting() {
        return adjusting;
    }

    @Override
    public void setValue(int value) {
        setTypedValue(value, AdjustmentEvent.TRACK);
    }

    void setTypedValue(int value, int type) {
        int boundedValue = Math.max(minimum, Math.min(value, maximum - visibleAmount));
        if (this.value != boundedValue) {
            this.value = boundedValue;
            fireAdjustmentEvent(type);
        }
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public synchronized void addAdjustmentListener(AdjustmentListener listener) {
        if (listener != null) {
            adjustmentListeners.add(listener);
        }
    }

    @Override
    public synchronized void removeAdjustmentListener(AdjustmentListener listener) {
        adjustmentListeners.remove(listener);
    }

    public synchronized AdjustmentListener[] getAdjustmentListeners() {
        return adjustmentListeners.toArray(new AdjustmentListener[0]);
    }

    @Override
    public String toString() {
        return getClass().getName() + "[" + paramString() + "]";
    }

    public String paramString() {
        String direction = orientation == Adjustable.VERTICAL ? "vertical," : "horizontal,";
        return direction + "[0.." + maximum + "]" + ",val=" + value + ",vis=" + visibleAmount + ",unit=" + unitIncrement + ",block=" + blockIncrement + ",isAdjusting=" + adjusting;
    }

    private void fireAdjustmentEvent(int type) {
        AdjustmentEvent event = new AdjustmentEvent(this, AdjustmentEvent.ADJUSTMENT_VALUE_CHANGED, type, value, adjusting);
        for (AdjustmentListener listener : getAdjustmentListeners()) {
            listener.adjustmentValueChanged(event);
        }
    }
}
