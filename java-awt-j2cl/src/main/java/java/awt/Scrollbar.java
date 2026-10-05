package java.awt;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.Locale;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleValue;

public class Scrollbar extends Component implements Adjustable, Accessible {
    private static final long serialVersionUID = 8451667562882310543L;
    public static final int HORIZONTAL = Adjustable.HORIZONTAL;
    public static final int VERTICAL = Adjustable.VERTICAL;
    private final List<AdjustmentListener> adjustmentListeners = new ArrayList<>();
    private int blockIncrement = 10;
    private int maximum;
    private int minimum;
    private int orientation;
    private int unitIncrement = 1;
    private int value;
    private int visibleAmount;
    private boolean adjusting;

    public Scrollbar() throws HeadlessException {
        this(VERTICAL, 0, 10, 0, 100);
    }

    public Scrollbar(int orientation) throws HeadlessException {
        this(orientation, 0, 10, 0, 100);
    }

    public Scrollbar(int orientation, int value, int visible, int minimum, int maximum) throws HeadlessException {
        super("input");
        validateOrientation(orientation);
        this.orientation = orientation;
        setValues(value, visible, minimum, maximum);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        updatePeerInputType("range");
        synchronizePeer();
        updatePeerTextChangeHandler(this::readValueFromPeer);
    }

    @Override
    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        validateOrientation(orientation);
        if (this.orientation == orientation) {
            return;
        }
        this.orientation = orientation;
        if (isDisplayable()) {
            removeNotify();
            addNotify();
            invalidate();
        }
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public void setValue(int value) {
        setValues(value, visibleAmount, minimum, maximum);
    }

    @Override
    public int getMinimum() {
        return minimum;
    }

    @Override
    public void setMinimum(int minimum) {
        setValues(value, visibleAmount, minimum, maximum);
    }

    @Override
    public int getMaximum() {
        return maximum;
    }

    @Override
    public void setMaximum(int maximum) {
        int boundedMaximum = maximum == Integer.MIN_VALUE ? Integer.MIN_VALUE + 1 : maximum;
        int boundedMinimum = minimum >= boundedMaximum ? boundedMaximum - 1 : minimum;
        setValues(value, visibleAmount, boundedMinimum, boundedMaximum);
    }

    @Override
    public int getVisibleAmount() {
        return getVisible();
    }

    @Deprecated
    public int getVisible() {
        return visibleAmount;
    }

    @Override
    public void setVisibleAmount(int visibleAmount) {
        setValues(value, visibleAmount, minimum, maximum);
    }

    @Override
    public void setUnitIncrement(int increment) {
        setLineIncrement(increment);
    }

    @Deprecated
    public synchronized void setLineIncrement(int increment) {
        unitIncrement = Math.max(1, increment);
        synchronizePeer();
    }

    @Override
    public int getUnitIncrement() {
        return getLineIncrement();
    }

    @Deprecated
    public int getLineIncrement() {
        return unitIncrement;
    }

    @Override
    public void setBlockIncrement(int increment) {
        setPageIncrement(increment);
    }

    @Deprecated
    public synchronized void setPageIncrement(int increment) {
        blockIncrement = Math.max(1, increment);
    }

    @Override
    public int getBlockIncrement() {
        return getPageIncrement();
    }

    @Deprecated
    public int getPageIncrement() {
        return blockIncrement;
    }

    public void setValues(int value, int visible, int minimum, int maximum) {
        int boundedMinimum = minimum == Integer.MAX_VALUE ? Integer.MAX_VALUE - 1 : minimum;
        int boundedMaximum = maximum <= boundedMinimum ? boundedMinimum + 1 : maximum;
        long range = (long) boundedMaximum - boundedMinimum;
        if (range > Integer.MAX_VALUE) {
            range = Integer.MAX_VALUE;
            boundedMaximum = boundedMinimum + (int) range;
        }
        int boundedVisible = Math.max(1, Math.min(visible, (int) range));
        int boundedValue = Math.max(boundedMinimum, Math.min(value, boundedMaximum - boundedVisible));
        int previousValue = this.value;
        this.value = boundedValue;
        visibleAmount = boundedVisible;
        this.minimum = boundedMinimum;
        this.maximum = boundedMaximum;
        synchronizePeer();
        if (previousValue != boundedValue && accessibleContext != null) {
            accessibleContext.firePropertyChange(AccessibleContext.ACCESSIBLE_VALUE_PROPERTY, Integer.valueOf(previousValue), Integer.valueOf(boundedValue));
        }
    }

    public boolean getValueIsAdjusting() {
        return adjusting;
    }

    public void setValueIsAdjusting(boolean adjusting) {
        boolean previous = this.adjusting;
        this.adjusting = adjusting;
        if (previous != adjusting && accessibleContext != null) {
            accessibleContext.firePropertyChange(AccessibleContext.ACCESSIBLE_STATE_PROPERTY, previous ? AccessibleState.BUSY : null, adjusting ? AccessibleState.BUSY : null);
        }
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
    @SuppressWarnings("unchecked")
    public <T extends EventListener> T[] getListeners(Class<T> listenerType) {
        return listenerType == AdjustmentListener.class ? (T[]) getAdjustmentListeners() : super.getListeners(listenerType);
    }

    @Override
    public Dimension getMinimumSize() {
        if (isMinimumSizeSet()) {
            return super.getMinimumSize();
        }
        return orientation == VERTICAL ? new Dimension(16, 48) : new Dimension(48, 16);
    }

    @Override
    public Dimension getPreferredSize() {
        return isPreferredSizeSet() ? super.getPreferredSize() : getMinimumSize();
    }

    @Override
    protected boolean eventEnabled(AWTEvent event) {
        return (event.getID() == AdjustmentEvent.ADJUSTMENT_VALUE_CHANGED && !adjustmentListeners.isEmpty()) || super.eventEnabled(event);
    }

    @Override
    protected void processEvent(AWTEvent event) {
        if (event instanceof AdjustmentEvent) {
            processAdjustmentEvent((AdjustmentEvent) event);
        } else {
            super.processEvent(event);
        }
    }

    protected void processAdjustmentEvent(AdjustmentEvent event) {
        for (AdjustmentListener listener : getAdjustmentListeners()) {
            listener.adjustmentValueChanged(event);
        }
    }

    @Override
    protected String paramString() {
        return super.paramString() + ",val=" + value + ",vis=" + visibleAmount + ",min=" + minimum + ",max=" + maximum + (orientation == VERTICAL ? ",vert" : ",horz") + ",isAdjusting=" + adjusting;
    }

    @Override
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleAWTScrollBar();
        }
        return accessibleContext;
    }

    private void synchronizePeer() {
        updatePeerRange(minimum, maximum - visibleAmount, unitIncrement, orientation == VERTICAL);
        updatePeerValue(Integer.toString(value));
    }

    private void readValueFromPeer() {
        String peerValue = getPeerValue();
        if (peerValue == null) {
            return;
        }
        int nextValue;
        try {
            nextValue = Integer.parseInt(peerValue);
        } catch (NumberFormatException exception) {
            return;
        }
        nextValue = Math.max(minimum, Math.min(nextValue, maximum - visibleAmount));
        if (nextValue != value) {
            value = nextValue;
            dispatchEvent(new AdjustmentEvent(this, AdjustmentEvent.ADJUSTMENT_VALUE_CHANGED, AdjustmentEvent.TRACK, value, adjusting));
        }
    }

    private static void validateOrientation(int orientation) {
        if (orientation != HORIZONTAL && orientation != VERTICAL) {
            throw new IllegalArgumentException("illegal scrollbar orientation");
        }
    }

    protected class AccessibleAWTScrollBar extends AccessibleAWTComponent implements AccessibleValue {
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.SCROLL_BAR;
        }

        @Override
        public AccessibleStateSet getAccessibleStateSet() {
            AccessibleStateSet states = new AccessibleStateSet();
            if (isEnabled()) {
                states.add(AccessibleState.ENABLED);
            }
            if (isVisible()) {
                states.add(AccessibleState.VISIBLE);
            }
            if (adjusting) {
                states.add(AccessibleState.BUSY);
            }
            states.add(orientation == VERTICAL ? AccessibleState.VERTICAL : AccessibleState.HORIZONTAL);
            return states;
        }

        @Override
        public int getAccessibleIndexInParent() {
            return getParent() == null ? -1 : getParent().getComponentZOrder(Scrollbar.this);
        }

        @Override
        public int getAccessibleChildrenCount() {
            return 0;
        }

        @Override
        public Accessible getAccessibleChild(int index) {
            return null;
        }

        @Override
        public Locale getLocale() {
            return Scrollbar.this.getLocale();
        }

        @Override
        public AccessibleValue getAccessibleValue() {
            return this;
        }

        @Override
        public Number getCurrentAccessibleValue() {
            return Integer.valueOf(value);
        }

        @Override
        public boolean setCurrentAccessibleValue(Number value) {
            if (value == null) {
                return false;
            }
            setValue(value.intValue());
            return true;
        }

        @Override
        public Number getMinimumAccessibleValue() {
            return Integer.valueOf(minimum);
        }

        @Override
        public Number getMaximumAccessibleValue() {
            return Integer.valueOf(maximum);
        }
    }
}
