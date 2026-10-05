package java.awt;

import java.awt.event.AdjustmentEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Locale;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;

public class ScrollPane extends Container implements Accessible {
    private static final long serialVersionUID = 7956609840827222915L;
    public static final int SCROLLBARS_AS_NEEDED = 0;
    public static final int SCROLLBARS_ALWAYS = 1;
    public static final int SCROLLBARS_NEVER = 2;
    private final ScrollPaneAdjustable horizontalAdjustable;
    private final int scrollbarDisplayPolicy;
    private final ScrollPaneAdjustable verticalAdjustable;
    private boolean wheelScrollingEnabled = true;

    public ScrollPane() throws HeadlessException {
        this(SCROLLBARS_AS_NEEDED);
    }

    public ScrollPane(int scrollbarDisplayPolicy) throws HeadlessException {
        super("div");
        validateScrollbarDisplayPolicy(scrollbarDisplayPolicy);
        this.scrollbarDisplayPolicy = scrollbarDisplayPolicy;
        verticalAdjustable = new ScrollPaneAdjustable(this, this::adjustmentValueChanged, Adjustable.VERTICAL);
        horizontalAdjustable = new ScrollPaneAdjustable(this, this::adjustmentValueChanged, Adjustable.HORIZONTAL);
        setSize(100, 100);
    }

    @Override
    protected final void addImpl(Component component, Object constraints, int index) {
        synchronized (getTreeLock()) {
            if (getComponentCount() > 0) {
                remove(0);
            }
            if (index > 0) {
                throw new IllegalArgumentException("position greater than 0");
            }
            super.addImpl(component, constraints, index);
        }
    }

    public int getScrollbarDisplayPolicy() {
        return scrollbarDisplayPolicy;
    }

    public Dimension getViewportSize() {
        Insets insets = getInsets();
        return new Dimension(getWidth() - insets.left - insets.right, getHeight() - insets.top - insets.bottom);
    }

    public int getHScrollbarHeight() {
        return 0;
    }

    public int getVScrollbarWidth() {
        return 0;
    }

    public Adjustable getVAdjustable() {
        return verticalAdjustable;
    }

    public Adjustable getHAdjustable() {
        return horizontalAdjustable;
    }

    public void setScrollPosition(int x, int y) {
        if (getComponentCount() == 0) {
            throw new NullPointerException("child is null");
        }
        horizontalAdjustable.setValue(x);
        verticalAdjustable.setValue(y);
        synchronizeScrollPeer();
    }

    public void setScrollPosition(Point point) {
        if (point == null) {
            throw new NullPointerException("point");
        }
        setScrollPosition(point.x, point.y);
    }

    public Point getScrollPosition() {
        if (getComponentCount() == 0) {
            throw new NullPointerException("child is null");
        }
        return new Point(horizontalAdjustable.getValue(), verticalAdjustable.getValue());
    }

    @Override
    public final void setLayout(LayoutManager layout) {
        throw new AWTError("ScrollPane controls layout");
    }

    @Override
    public void doLayout() {
        layout();
    }

    @Override
    @Deprecated
    public void layout() {
        if (getComponentCount() == 0) {
            return;
        }
        Component child = getComponent(0);
        Dimension viewportSize = getViewportSize();
        Dimension preferredSize = child.getPreferredSize();
        int childWidth = Math.max(viewportSize.width, preferredSize.width);
        int childHeight = Math.max(viewportSize.height, preferredSize.height);
        child.setBounds(-horizontalAdjustable.getValue(), -verticalAdjustable.getValue(), childWidth, childHeight);
        horizontalAdjustable.setSpan(0, childWidth, viewportSize.width);
        verticalAdjustable.setSpan(0, childHeight, viewportSize.height);
        synchronizeScrollPeer();
    }

    @Override
    public void printComponents(Graphics graphics) {
        if (getComponentCount() == 0) {
            return;
        }
        Component child = getComponent(0);
        Point location = child.getLocation();
        Dimension viewportSize = getViewportSize();
        Insets insets = getInsets();
        Graphics childGraphics = graphics.create();
        try {
            childGraphics.clipRect(insets.left, insets.top, viewportSize.width, viewportSize.height);
            childGraphics.translate(location.x, location.y);
            child.printAll(childGraphics);
        } finally {
            childGraphics.dispose();
        }
    }

    @Override
    public void addNotify() {
        super.addNotify();
        updatePeerScrollPolicy(scrollbarDisplayPolicy);
        updatePeerScrollHandler(this::readScrollPositionFromPeer);
        layout();
    }

    @Override
    public String paramString() {
        String policy = scrollbarDisplayPolicy == SCROLLBARS_AS_NEEDED ? "as-needed" : scrollbarDisplayPolicy == SCROLLBARS_ALWAYS ? "always" : "never";
        Point position = getComponentCount() == 0 ? new Point() : getScrollPosition();
        Insets insets = getInsets();
        return super.paramString() + ",ScrollPosition=(" + position.x + "," + position.y + ")" + ",Insets=(" + insets.top + "," + insets.left + "," + insets.bottom + "," + insets.right + ")" + ",ScrollbarDisplayPolicy=" + policy + ",wheelScrollingEnabled=" + wheelScrollingEnabled;
    }

    public void setWheelScrollingEnabled(boolean wheelScrollingEnabled) {
        this.wheelScrollingEnabled = wheelScrollingEnabled;
    }

    public boolean isWheelScrollingEnabled() {
        return wheelScrollingEnabled;
    }

    @Override
    protected void processMouseWheelEvent(MouseWheelEvent event) {
        if (wheelScrollingEnabled && getComponentCount() > 0) {
            ScrollPaneAdjustable adjustable = verticalAdjustable.getMaximum() > verticalAdjustable.getVisibleAmount() ? verticalAdjustable : horizontalAdjustable;
            int increment = event.getScrollType() == MouseWheelEvent.WHEEL_BLOCK_SCROLL ? adjustable.getBlockIncrement() : adjustable.getUnitIncrement() * event.getScrollAmount();
            adjustable.setTypedValue(adjustable.getValue() + event.getWheelRotation() * increment, event.getWheelRotation() < 0 ? AdjustmentEvent.UNIT_DECREMENT : AdjustmentEvent.UNIT_INCREMENT);
            event.consume();
        }
        super.processMouseWheelEvent(event);
    }

    @Override
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleAWTScrollPane();
        }
        return accessibleContext;
    }

    void synchronizeScrollPeer() {
        if (getComponentCount() > 0) {
            Component child = getComponent(0);
            child.setLocation(-horizontalAdjustable.getValue(), -verticalAdjustable.getValue());
            child.setPeerBounds(0, 0, child.getWidth(), child.getHeight());
        }
        updatePeerScrollPosition(horizontalAdjustable.getValue(), verticalAdjustable.getValue());
    }

    private void adjustmentValueChanged(AdjustmentEvent event) {
        synchronizeScrollPeer();
    }

    private void readScrollPositionFromPeer() {
        Point position = getPeerScrollPosition();
        horizontalAdjustable.setTypedValue(position.x, AdjustmentEvent.TRACK);
        verticalAdjustable.setTypedValue(position.y, AdjustmentEvent.TRACK);
        synchronizeScrollPeer();
    }

    private static void validateScrollbarDisplayPolicy(int policy) {
        if (policy != SCROLLBARS_AS_NEEDED && policy != SCROLLBARS_ALWAYS && policy != SCROLLBARS_NEVER) {
            throw new IllegalArgumentException("illegal scrollbar display policy");
        }
    }

    protected class AccessibleAWTScrollPane extends AccessibleAWTContainer {
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.SCROLL_PANE;
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
            return states;
        }

        @Override
        public int getAccessibleIndexInParent() {
            return getParent() == null ? -1 : getParent().getComponentZOrder(ScrollPane.this);
        }

        @Override
        public int getAccessibleChildrenCount() {
            return getComponentCount();
        }

        @Override
        public Accessible getAccessibleChild(int index) {
            Component child = getComponent(index);
            return child instanceof Accessible ? (Accessible) child : null;
        }

        @Override
        public Locale getLocale() {
            return ScrollPane.this.getLocale();
        }
    }
    protected boolean eventTypeEnabled(int type) { return type != java.awt.event.MouseEvent.MOUSE_WHEEL || isWheelScrollingEnabled(); }
}
